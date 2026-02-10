package com.yourcompany.plugins.volumecontrol

import android.content.Context
import android.media.AudioManager
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlin.math.round

@CapacitorPlugin(name = "VolumeControl")
class VolumeControlPlugin : Plugin() {
    
    private lateinit var audioManager: AudioManager
    private var volumeObserver: ContentObserver? = null
    private var isWatching = false
    private var lastReported: Float? = null
    
    override fun load() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    private fun roundToTwoDecimals(value: Float): Float {
        return (round(value * 100f) / 100f).coerceIn(0f, 1f)
    }
    
    private fun streamTypeFromString(type: String?): Int {
        return when (type?.lowercase()) {
            "voice_call" -> AudioManager.STREAM_VOICE_CALL
            "system" -> AudioManager.STREAM_SYSTEM
            "ring" -> AudioManager.STREAM_RING
            "alarm" -> AudioManager.STREAM_ALARM
            "notification" -> AudioManager.STREAM_NOTIFICATION
            "dtmf" -> AudioManager.STREAM_DTMF
            "music" -> AudioManager.STREAM_MUSIC
            "default", null -> AudioManager.STREAM_MUSIC
            else -> AudioManager.STREAM_MUSIC
        }
    }

    private fun getCurrentVolume(streamType: Int): Float {
        val currentVolume = audioManager.getStreamVolume(streamType)
        val maxVolume = audioManager.getStreamMaxVolume(streamType)
        if (maxVolume <= 0) return 0f
        return roundToTwoDecimals(currentVolume.toFloat() / maxVolume.toFloat())
    }
    
    @PluginMethod
    fun getVolumeLevel(call: PluginCall) {
        try {
            val streamType = streamTypeFromString(call.getString("type"))
            val normalizedVolume = getCurrentVolume(streamType)
            
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

            val streamType = streamTypeFromString(call.getString("type"))
            val maxVolume = audioManager.getStreamMaxVolume(streamType)
            val roundedValue = roundToTwoDecimals(value)
            val targetVolumeLevel = (roundedValue * maxVolume).toInt()
            
            // Use 0 for flags to avoid showing the UI
            audioManager.setStreamVolume(streamType, targetVolumeLevel, 0)
            
            // Get the actual volume after setting it
            val actualVolume = getCurrentVolume(streamType)
            
            val ret = JSObject()
            ret.put("value", actualVolume)
            call.resolve(ret)

            // Emit an instant change event
            emitVolumeChanged(actualVolume, null, call.getString("type"))
        } catch (e: Exception) {
            call.reject("Failed to set volume level", e)
        }
    }

    @PluginMethod
    fun watchVolume(call: PluginCall) {
        if (isWatching) {
            call.reject("Volume is already being watched")
            return
        }

        val handler = Handler(Looper.getMainLooper())
        val streamType = streamTypeFromString(null)

        volumeObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                val current = getCurrentVolume(streamType)
                val prev = lastReported
                val direction = if (prev != null) {
                    if (current > prev) "up" else if (current < prev) "down" else null
                } else null
                emitVolumeChanged(current, direction, null)
            }
        }

        try {
            context.contentResolver.registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                volumeObserver!!
            )
        } catch (e: Exception) {
            volumeObserver = null
            call.reject("Failed to register volume observer", e)
            return
        }

        isWatching = true
        // Emit initial volume immediately
        val initial = getCurrentVolume(streamType)
        emitVolumeChanged(initial, null, null)
        call.resolve()
    }

    @PluginMethod
    fun clearWatch(call: PluginCall) {
        if (!isWatching) {
            call.reject("Volume is not being watched")
            return
        }
        try {
            if (volumeObserver != null) {
                context.contentResolver.unregisterContentObserver(volumeObserver!!)
            }
        } catch (_: Exception) {
        }
        volumeObserver = null
        isWatching = false
        call.resolve()
    }

    @PluginMethod
    fun isWatching(call: PluginCall) {
        val ret = JSObject()
        ret.put("value", isWatching)
        call.resolve(ret)
    }

    private fun emitVolumeChanged(value: Float, direction: String?, type: String?) {
        lastReported = value

        // New event: includes volume value
        val changed = JSObject()
        changed.put("value", value)
        if (direction != null) changed.put("direction", direction)
        if (type != null) changed.put("type", type)
        notifyListeners("volumeLevelChanged", changed)

        // Back-compat event: direction only (optionally includes value)
        if (direction != null) {
            val pressed = JSObject()
            pressed.put("direction", direction)
            pressed.put("value", value)
            notifyListeners("volumeButtonPressed", pressed)
        }
    }
}
