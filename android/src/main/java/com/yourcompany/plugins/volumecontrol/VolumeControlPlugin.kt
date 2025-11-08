package com.yourcompany.plugins.volumecontrol

import android.content.Context
import android.media.AudioManager
import androidx.media.VolumeProviderCompat
import androidx.media.session.MediaSessionCompat
import androidx.media.session.PlaybackStateCompat
import android.view.KeyEvent
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlin.math.round

@CapacitorPlugin(name = "VolumeControl")
class VolumeControlPlugin : Plugin() {
    
    private lateinit var audioManager: AudioManager
    private var mediaSession: MediaSessionCompat? = null
    private var volumeProvider: VolumeProviderCompat? = null
    private var isStarted = false
    private var suppressVolumeIndicator = false
    
    override fun load() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    override fun handleOnDestroy() {
        super.handleOnDestroy()
        cleanupMediaSession()
    }
    
    private fun cleanupMediaSession() {
        mediaSession?.let {
            it.isActive = false
            it.release()
        }
        mediaSession = null
        volumeProvider = null
    }
    
    private fun roundToTwoDecimals(value: Float): Float {
        return (round(value * 100f) / 100f).coerceIn(0f, 1f)
    }
    
    private fun getCurrentVolume(): Float {
        val streamType = AudioManager.STREAM_MUSIC
        val currentVolume = audioManager.getStreamVolume(streamType)
        val maxVolume = audioManager.getStreamMaxVolume(streamType)
        return roundToTwoDecimals(currentVolume.toFloat() / maxVolume.toFloat())
    }
    
    @PluginMethod
    fun getVolumeLevel(call: PluginCall) {
        try {
            val normalizedVolume = getCurrentVolume()
            
            val ret = JSObject()
            ret.put("value", normalizedVolume)
            call.resolve(ret)
        } catch (e: Exception) {
            call.reject("Failed to get volume level", e)
        }
    }
    
    @PluginMethod
    fun setVolumeLevel(call: PluginCall) {
        try {
            val value = call.getFloat("value") ?: run {
                call.reject("Missing required parameter: value")
                return
            }
            
            if (value < 0f || value > 1f) {
                call.reject("Volume value must be between 0 and 1")
                return
            }

            val streamType = AudioManager.STREAM_MUSIC
            val maxVolume = audioManager.getStreamMaxVolume(streamType)
            val roundedValue = roundToTwoDecimals(value)
            val targetVolumeLevel = (roundedValue * maxVolume).toInt()
            
            // Use 0 for flags to avoid showing the UI
            audioManager.setStreamVolume(streamType, targetVolumeLevel, 0)
            
            // Get the actual volume after setting it
            val actualVolume = getCurrentVolume()
            
            val ret = JSObject()
            ret.put("value", actualVolume)
            call.resolve(ret)
        } catch (e: Exception) {
            call.reject("Failed to set volume level", e)
        }
    }
    
    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun isWatching(call: PluginCall) {
        val ret = JSObject()
        ret.put("value", isStarted)
        call.resolve(ret)
    }
    
    @PluginMethod
    fun watchVolume(call: PluginCall) {
        if (isStarted) {
            call.reject("Volume buttons has already been watched")
            return
        }
        
        suppressVolumeIndicator = call.getBoolean("suppressVolumeIndicator") ?: false
        
        try {
            setupMediaSessionVolumeControl()
            isStarted = true
            call.resolve()
        } catch (e: Exception) {
            call.reject("Failed to start volume watching: ${e.message}", e)
        }
    }
    
    private fun setupMediaSessionVolumeControl() {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        
        // Create MediaSession
        mediaSession = MediaSessionCompat(context, "VolumeControlPlugin").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            
            // Set playback state to active (required for volume buttons to work)
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .build()
            )
            
            // Create VolumeProvider to intercept volume button presses
            volumeProvider = object : VolumeProviderCompat(
                VolumeProviderCompat.VOLUME_CONTROL_RELATIVE,
                maxVolume,
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            ) {
                override fun onAdjustVolume(direction: Int) {
                    // Get current live volume
                    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    
                    // Calculate new volume
                    val newVolume = (currentVolume + direction).coerceIn(0, maxVolume)
                    
                    // Apply volume change (with or without UI based on suppressVolumeIndicator)
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        newVolume,
                        if (suppressVolumeIndicator) 0 else AudioManager.FLAG_SHOW_UI
                    )
                    
                    // Update the provider's current volume to stay in sync
                    setCurrentVolume(newVolume)
                    
                    // Notify listeners about button press
                    val ret = JSObject()
                    ret.put("direction", if (direction > 0) "up" else "down")
                    notifyListeners("volumeButtonPressed", ret)
                }
                
                override fun onSetVolumeTo(volume: Int) {
                    // Apply the absolute volume change
                    val clampedVolume = volume.coerceIn(0, maxVolume)
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        clampedVolume,
                        if (suppressVolumeIndicator) 0 else AudioManager.FLAG_SHOW_UI
                    )
                    
                    // Update the provider's current volume to stay in sync
                    setCurrentVolume(clampedVolume)
                }
            }
            
            setPlaybackToRemote(volumeProvider)
            
            // Required: Set a minimal callback
            setCallback(object : MediaSessionCompat.Callback() {})
            
            isActive = true
        }
    }
    
    /**
     * LEGACY METHOD - For backward compatibility with MainActivity integration
     * This method can still be called from MainActivity's dispatchKeyEvent if needed
     * Returns true if the event was handled and should be consumed
     */
    fun handleVolumeKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        if (!isStarted) {
            return false
        }
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Only handle ACTION_DOWN to avoid duplicate events
            if (event.action == KeyEvent.ACTION_DOWN) {
                val ret = JSObject()
                ret.put("direction", if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) "up" else "down")
                notifyListeners("volumeButtonPressed", ret)
            }
            // Return true to suppress volume indicator if option is enabled
            return suppressVolumeIndicator
        }
        
        return false
    }
    
    @PluginMethod
    fun clearWatch(call: PluginCall) {
        if (!isStarted) {
            call.reject("Volume buttons has not been watched")
            return
        }
        
        cleanupMediaSession()
        isStarted = false
        suppressVolumeIndicator = false
        call.resolve()
    }
}