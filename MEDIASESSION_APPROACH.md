# MediaSession Approach for Volume Button Detection

## Overview

Version 2.0.0+ uses Android's **MediaSession + VolumeProvider** pattern to automatically intercept hardware volume button presses **without requiring MainActivity modification**.

## Why This Works Better

### Old Approach (MainActivity Required)
```
Hardware Button → Activity.dispatchKeyEvent() → Plugin.handleVolumeKeyEvent()
                    ↑ USER MUST ADD THIS CODE
```

### New Approach (Automatic)
```
Hardware Button → MediaSession.VolumeProvider.onAdjustVolume() → Plugin fires event
                    ↑ AUTOMATICALLY HANDLED BY THE PLUGIN
```

## Technical Implementation

The plugin creates a MediaSession with a custom VolumeProvider when `watchVolume()` is called:

```kotlin
// Create MediaSession
mediaSession = MediaSessionCompat(context, "VolumeControlPlugin").apply {
    setFlags(
        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
    )
    
    // Set playback state (required for volume buttons to work)
    setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
            .build()
    )
    
    // Create VolumeProvider to intercept volume button presses
    volumeProvider = object : VolumeProviderCompat(
        VolumeProviderCompat.VOLUME_CONTROL_RELATIVE,
        maxVolume,
        currentVolume
    ) {
        override fun onAdjustVolume(direction: Int) {
            // direction: -1 = volume down, 1 = volume up
            notifyListeners("volumeButtonPressed", data)
        }
    }
    
    setPlaybackToRemote(volumeProvider)
    isActive = true
}
```

## Benefits

✅ **No MainActivity modification** - Users just install and use  
✅ **Automatic lifecycle management** - Plugin handles cleanup  
✅ **Works with screen off** - MediaSession continues in background  
✅ **Official Android pattern** - Recommended by Google for media apps  
✅ **Better UX for plugin users** - No manual setup required  

## Compatibility

- **Android 5.0+ (API 21+)** - Full support
- **Earlier versions** - Falls back gracefully (no hardware button detection)

## When It Works

MediaSession volume interception works when:
- Plugin has called `watchVolume()`
- App is in foreground or background
- Screen is on or off (if app has foreground service)
- Device is not in Do Not Disturb mode (varies by Android version)

## Optional: MainActivity Integration

For advanced users who need more control or are experiencing issues, the legacy MainActivity integration approach is still available as a fallback. See [MAINACTIVITY_INTEGRATION.md](MAINACTIVITY_INTEGRATION.md).

## Limitations

- **Android only** - iOS uses a different approach with volume observation
- **Media apps recommended** - Works best in apps that play audio/video
- **Requires active session** - MediaSession must be active to intercept buttons

## Troubleshooting

### Build Error: "Unresolved reference 'VolumeProviderCompat'"

If you get this error in Android Studio, it means the androidx.media dependency wasn't resolved. **Solution:**

1. **Update the plugin to the latest version:**
   ```bash
   npm install @odion-cloud/capacitor-volume-control@latest
   npx cap sync android
   ```

2. **In Android Studio:**
   - Click **File → Sync Project with Gradle Files**
   - Or click **Build → Clean Project** then **Build → Rebuild Project**

The plugin uses `api` to expose `androidx.media:media:1.6.0` as a transitive dependency, so your app will automatically include it.

### Volume buttons not working?

1. **Check if watching started successfully:**
   ```javascript
   const result = await VolumeControl.isWatching();
   console.log('Watching:', result.value);
   ```

2. **Verify listener is attached:**
   ```javascript
   await VolumeControl.addListener('volumeButtonPressed', (event) => {
     console.log('Event received:', event);
   });
   ```

3. **Check Android version:**
   MediaSession requires Android 5.0+ (API 21+)

4. **Try the legacy approach:**
   If MediaSession doesn't work on your device, use the MainActivity integration method described in [MAINACTIVITY_INTEGRATION.md](MAINACTIVITY_INTEGRATION.md).

## How to Disable MediaSession (Use MainActivity Instead)

If you prefer the MainActivity integration approach or need more control:

1. Don't use the built-in MediaSession approach
2. Follow [MAINACTIVITY_INTEGRATION.md](MAINACTIVITY_INTEGRATION.md) instructions
3. The `handleVolumeKeyEvent()` method is still available for backward compatibility

## References

- [Android MediaSession Guide](https://developer.android.com/guide/topics/media-apps/audio-app/mediasession-callbacks)
- [VolumeProviderCompat Documentation](https://developer.android.com/reference/androidx/media/VolumeProviderCompat)
- [Media Apps Best Practices](https://developer.android.com/guide/topics/media-apps/audio-app/building-an-audio-app)
