package com.yourcompany.plugins.volumecontrol

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import android.view.View
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlin.math.round

@CapacitorPlugin(name = "VolumeControl")
class VolumeControlPlugin : Plugin() {
    
    private lateinit var audioManager: AudioManager
    private var savedCall: PluginCall? = null
    private var isStarted = false
    private var suppressVolumeIndicator = false
    
    override fun load() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
    
    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    fun watchVolume(call: PluginCall) {
        if (isStarted) {
            call.reject("Volume buttons has already been watched")
            return
        }
        
        suppressVolumeIndicator = call.getBoolean("suppressVolumeIndicator") ?: false
        
        call.setKeepAlive(true)
        savedCall = call
        
        bridge.webView.setOnKeyListener(
            object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        val isKeyUp = event?.action == KeyEvent.ACTION_UP
                        if (isKeyUp) {
                            // Longer delay to ensure volume change is fully registered
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                // Force a fresh volume reading after the button press
                                val freshVolume = getCurrentVolume()
                                val ret = JSObject()
                                ret.put("direction", if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) "up" else "down")
                                ret.put("volume", freshVolume)
                                call.resolve(ret)
                            }, 100) // Increased delay to 100ms for more reliable readings
                        }
                        return suppressVolumeIndicator
                    }
                    return false
                }
            }
        )
        
        isStarted = true
    }
    
    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun clearWatch(call: PluginCall) {
        if (!isStarted) {
            call.reject("Volume buttons has not been watched")
            return
        }
        
        bridge.webView.setOnKeyListener(null)
        
        if (savedCall != null) {
            bridge.releaseCall(savedCall!!)
            savedCall = null
        }
        
        isStarted = false
        call.resolve()
    }
}