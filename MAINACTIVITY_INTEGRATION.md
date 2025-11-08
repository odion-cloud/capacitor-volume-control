# MainActivity Integration Required for Volume Button Detection

## ⚠️ CRITICAL: This plugin requires MainActivity modification

Hardware volume buttons are handled at the **Activity level**, not the WebView level. You must add code to your MainActivity to forward key events to the plugin.

## Java Integration

Edit `android/app/src/main/java/[your-package]/MainActivity.java`:

```java
package your.package.name;

import android.os.Bundle;
import android.view.KeyEvent;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Plugin;
import com.yourcompany.plugins.volumecontrol.VolumeControlPlugin;

public class MainActivity extends BridgeActivity {
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Get the VolumeControl plugin instance
        Plugin plugin = this.bridge.getPlugin("VolumeControl").getInstance();
        
        if (plugin instanceof VolumeControlPlugin) {
            VolumeControlPlugin volumePlugin = (VolumeControlPlugin) plugin;
            
            // Let the plugin handle volume key events
            if (volumePlugin.handleVolumeKeyEvent(event.getKeyCode(), event)) {
                // Event was handled and should be consumed
                return true;
            }
        }
        
        // Let other keys pass through normally
        return super.dispatchKeyEvent(event);
    }
}
```

## Kotlin Integration

Edit `android/app/src/main/java/[your-package]/MainActivity.kt`:

```kotlin
package your.package.name

import android.os.Bundle
import android.view.KeyEvent
import com.getcapacitor.BridgeActivity
import com.yourcompany.plugins.volumecontrol.VolumeControlPlugin

class MainActivity : BridgeActivity() {
    
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Get the VolumeControl plugin instance
        val plugin = this.bridge.getPlugin("VolumeControl").getInstance()
        
        if (plugin is VolumeControlPlugin) {
            // Let the plugin handle volume key events
            if (plugin.handleVolumeKeyEvent(event.keyCode, event)) {
                // Event was handled and should be consumed
                return true
            }
        }
        
        // Let other keys pass through normally
        return super.dispatchKeyEvent(event)
    }
}
```

## How It Works

1. **dispatchKeyEvent** - Called by Android when ANY hardware key is pressed (including volume buttons)
2. **Plugin gets the event** - Forward volume key events to the plugin's `handleVolumeKeyEvent()` method
3. **Plugin decides** - Plugin returns:
   - `true` = Event handled, suppress default behavior (e.g., hide volume UI)
   - `false` = Not watching, let system handle normally

## Testing

After adding the code:

1. **Build and run**:
   ```bash
   npx cap sync android
   npx cap run android
   ```

2. **Press volume buttons** - You should see in logs:
   ```
   🔊 Volume button pressed: up
   🔊 Volume button pressed: down
   ```

3. **If suppressVolumeIndicator: true** - Volume UI should be hidden

## Troubleshooting

### No events firing?
- ✅ Check MainActivity has the dispatchKeyEvent override
- ✅ Verify package name matches your project
- ✅ Rebuild app: `npx cap sync android` then run

### Volume UI still showing?
- Set `suppressVolumeIndicator: true` in watchVolume options
- Make sure handleVolumeKeyEvent returns true (which happens when suppressVolumeIndicator is enabled)

### Build errors?
- Make sure import paths match your package structure
- Verify plugin package name is `com.yourcompany.plugins.volumecontrol`
