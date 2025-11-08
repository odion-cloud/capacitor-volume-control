package com.yourcompany.plugins.volumecontrol

import android.content.Context
import android.media.AudioManager
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
    private var isStarted = false
    private var suppressVolumeIndicator = false
    
    override fun load() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    /**
     * Handle key events for volume buttons at the activity level
     * This method is called by the MainActivity's dispatchKeyEvent
     */
    override fun handleOnPause() {
        super.handleOnPause()
    }
    
    override fun handleOnResume() {
        super.handleOnResume()
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
        isStarted = true
        call.resolve()
    }
    
    /**
     * This method must be called from MainActivity's dispatchKeyEvent
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
        
        isStarted = false
        suppressVolumeIndicator = false
        call.resolve()
    }
}