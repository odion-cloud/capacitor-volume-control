# Capacitor Volume Control Plugin

A Capacitor plugin for advanced volume control with native Android and iOS implementations. This plugin provides comprehensive volume management capabilities including volume level control, volume change monitoring, and platform-specific features.

## Features

- 🔊 **Volume Level Control**: Get and set volume levels for different audio streams
- 👂 **Volume Change Monitoring**: Watch for volume changes in real-time
- 📱 **Platform-Specific Features**: 
  - Android: Suppress volume indicator, control different volume types
  - iOS: Disable system volume handler, voice call volume control
- 🎯 **Type Safety**: Full TypeScript support with comprehensive type definitions
- 🔧 **Easy Integration**: Simple API that works seamlessly with Capacitor apps

## Installation

```bash
npm install @odion-cloud/capacitor-volume-control
npx cap sync
```

## Setup

1. Install dependencies:
```bash
npm install
```

2. Add the plugin:
```bash
npm install @odion-cloud/capacitor-volume-control
```

3. Sync with native platforms:
```bash
npx cap sync
```

## Usage Examples

### Basic Volume Control

```typescript
import { VolumeControl, VolumeType } from '@odion-cloud/capacitor-volume-control';

// Get current volume
const volume = await VolumeControl.getVolumeLevel();
console.log('Current volume:', volume.value);

// Set volume to 50%
await VolumeControl.setVolumeLevel({ value: 0.5 });
```

### Volume Watching

```typescript
// Start watching volume changes
await VolumeControl.watchVolume({
  disableSystemVolumeHandler: true, // iOS only
  suppressVolumeIndicator: true,    // Android only
}, (event) => {
  console.log('Volume changed:', event.direction, event.level);
});

// Stop watching
await VolumeControl.clearWatch();
```

### Advanced Usage

```typescript
import { VolumeControl, VolumeType } from '@odion-cloud/capacitor-volume-control';

class VolumeService {
  private isWatching = false;

  async initializeVolume() {
    try {
      // Get current music volume
      const musicVolume = await VolumeControl.getVolumeLevel({
        type: VolumeType.MUSIC
      });
      
      console.log('Music volume:', musicVolume.value);
      
      // Set system volume
      await VolumeControl.setVolumeLevel({
        value: 0.8,
        type: VolumeType.SYSTEM
      });
      
    } catch (error) {
      console.error('Volume initialization error:', error);
    }
  }

  async startWatching() {
    if (this.isWatching) return;

    try {
      await VolumeControl.watchVolume({
        disableSystemVolumeHandler: true,
        suppressVolumeIndicator: true
      }, this.handleVolumeChange.bind(this));
      
      this.isWatching = true;
      console.log('Started volume watching');
      
    } catch (error) {
      console.error('Volume watching error:', error);
    }
  }

  async stopWatching() {
    try {
      await VolumeControl.clearWatch();
      this.isWatching = false;
      console.log('Stopped volume watching');
      
    } catch (error) {
      console.error('Stop watching error:', error);
    }
  }

  private handleVolumeChange(event: { direction: 'up' | 'down', level: number }) {
    console.log(`Volume ${event.direction}: ${event.level}`);
    
    // Custom volume handling logic
    if (event.level > 0.9) {
      console.warn('Volume is very high!');
    }
  }

  async getWatchStatus() {
    const status = await VolumeControl.isWatching();
    return status.value;
  }
}

// Usage
const volumeService = new VolumeService();

// Initialize
await volumeService.initializeVolume();

// Start watching
await volumeService.startWatching();

// Check status
const isWatching = await volumeService.getWatchStatus();
console.log('Is watching:', isWatching);

// Stop watching
await volumeService.stopWatching();
```

## Platform-Specific Examples

### Android Specific

```typescript
// Suppress volume indicator on Android
await VolumeControl.watchVolume({
  suppressVolumeIndicator: true
}, (event) => {
  console.log('Volume changed silently:', event);
});

// Control different volume types
await VolumeControl.setVolumeLevel({
  value: 0.7,
  type: VolumeType.NOTIFICATION
});
```

### iOS Specific

```typescript
// Disable system volume handler on iOS
await VolumeControl.watchVolume({
  disableSystemVolumeHandler: true
}, (event) => {
  console.log('Volume changed without system UI:', event);
});

// Control voice call volume
await VolumeControl.setVolumeLevel({
  value: 0.9,
  type: VolumeType.VOICE_CALL
});
```

## Error Handling

```typescript
try {
  await VolumeControl.setVolumeLevel({ value: 1.5 });
} catch (error) {
  if (error.message.includes('between 0.0 and 1.0')) {
    console.error('Invalid volume value');
  } else {
    console.error('Unexpected error:', error);
  }
}

try {
  await VolumeControl.watchVolume({}, callback);
  await VolumeControl.watchVolume({}, callback); // This will fail
} catch (error) {
  if (error.message.includes('already active')) {
    console.error('Volume watching is already active');
  }
}
```

## React Hook Example

```typescript
import { useEffect, useState } from 'react';
import { VolumeControl } from '@odion-cloud/capacitor-volume-control';

export function useVolumeControl() {
  const [volume, setVolume] = useState(0.5);
  const [isWatching, setIsWatching] = useState(false);

  useEffect(() => {
    // Get initial volume
    VolumeControl.getVolumeLevel().then(result => {
      setVolume(result.value);
    });

    // Cleanup on unmount
    return () => {
      VolumeControl.clearWatch();
    };
  }, []);

  const startWatching = async () => {
    if (isWatching) return;

    try {
      await VolumeControl.watchVolume({
        disableSystemVolumeHandler: true,
        suppressVolumeIndicator: true
      }, (event) => {
        setVolume(event.level);
      });
      
      setIsWatching(true);
    } catch (error) {
      console.error('Failed to start watching:', error);
    }
  };

  const stopWatching = async () => {
    try {
      await VolumeControl.clearWatch();
      setIsWatching(false);
    } catch (error) {
      console.error('Failed to stop watching:', error);
    }
  };

  const setVolumeLevel = async (value: number) => {
    try {
      await VolumeControl.setVolumeLevel({ value });
      setVolume(value);
    } catch (error) {
      console.error('Failed to set volume:', error);
    }
  };

  return {
    volume,
    isWatching,
    startWatching,
    stopWatching,
    setVolumeLevel
  };
}
```

## Testing

Run the example:

```bash
npm start
```

Build for production:

```bash
npm run build
```

Test on device:

```bash
npx cap run android
npx cap run ios
```

## Platform Support

### 📱 Supported Devices

| Platform | Support Level | Minimum Version | Features |
|----------|---------------|-----------------|----------|
| **Android** | ✅ Full Support | Android 6.0+ (API 23+) | All volume types, hardware buttons, real-time monitoring |
| **iOS** | ✅ Full Support | iOS 13.0+ | Volume control, hardware buttons, audio session management |
| **Web** | ⚠️ Development Only | All modern browsers | Mock implementation for testing |

### 🏆 Android Version Compatibility

| Android Version | API Level | Support Level | Features |
|-----------------|-----------|---------------|----------|
| Android 14+ | API 34+ | ✅ Full | All features, visual media permissions |
| Android 13 | API 33 | ✅ Full | Granular media permissions |
| Android 10-12 | API 29-32 | ✅ Full | Scoped storage, external volumes |
| Android 6-9 | API 23-28 | ✅ Full | Runtime permissions, SD card access |
| Android 5 | API 21-22 | ⚠️ Basic | Limited external storage access |

### 🎯 Volume Types Support

| Volume Type | Android | iOS | Description |
|-------------|---------|-----|-------------|
| `VolumeType.MUSIC` | ✅ | ✅ | Music, videos, games, and other media |
| `VolumeType.SYSTEM` | ✅ | ❌ | System sounds and notifications |
| `VolumeType.RING` | ✅ | ❌ | Phone ringtone volume |
| `VolumeType.NOTIFICATION` | ✅ | ❌ | Notification sounds |
| `VolumeType.ALARM` | ✅ | ❌ | Alarm clock volume |
| `VolumeType.VOICE_CALL` | ✅ | ✅ | Voice call volume |
| `VolumeType.DTMF` | ✅ | ❌ | DTMF tones |

## API Reference

### Methods

#### `getVolumeLevel(options?)`
Get the current volume level for a specific audio stream.

```typescript
getVolumeLevel({
  type?: VolumeType;        // Volume type to get (default: 'music')
}): Promise<VolumeResult>

// Returns: { value: number } (0.0 to 1.0)
```

#### `setVolumeLevel(options)`
Set the volume level for a specific audio stream.

```typescript
setVolumeLevel({
  value: number;            // Volume level (0.0 to 1.0)
  type?: VolumeType;        // Volume type to set (default: 'music')
}): Promise<VolumeResult>

// Returns: { value: number } (the new volume level)
```

#### `watchVolume(options, callback)`
Start watching for volume changes with hardware button detection.

```typescript
watchVolume({
  disableSystemVolumeHandler?: boolean;  // iOS: disable system UI
  suppressVolumeIndicator?: boolean;     // Android: hide volume UI
}, callback: VolumeChangeCallback): Promise<void>

// Callback receives: { direction: 'up' | 'down', level: number }
```

#### `clearWatch()`
Stop watching for volume changes.

```typescript
clearWatch(): Promise<void>
```

#### `isWatching()`
Check if volume watching is currently active.

```typescript
isWatching(): Promise<WatchStatusResult>

// Returns: { value: boolean }
```

### Configuration Options

| Option | Platform | Description |
|--------|----------|-------------|
| `suppressVolumeIndicator` | Android | Hide system volume UI when changing volume |
| `disableSystemVolumeHandler` | iOS | Disable system volume UI and intercept hardware buttons |
| `type` | Both | Specify volume type (MUSIC, SYSTEM, RING, etc.) |
| `value` | Both | Volume level between 0.0 and 1.0 |

### Error Handling

Common errors and their solutions:

| Error Message | Cause | Solution |
|---------------|-------|----------|
| `Volume value must be between 0.0 and 1.0` | Invalid volume level | Ensure volume is between 0.0 and 1.0 |
| `Volume watching is already active` | Multiple watch calls | Call `clearWatch()` before starting new watch |
| `Volume slider not available` | iOS setup issue | Check audio session configuration |
| `Failed to get volume level` | Permission or system error | Verify permissions and device compatibility |

## Support This Project

Help me improve this plugin and build better tools for the community!

### 🤝 GitHub Sponsors
Support through GitHub's official sponsorship program:
- **[GitHub Sponsors](https://github.com/sponsors/odion-cloud)** - Most transparent and developer-friendly way to support

### ₿ Cryptocurrency Support
Support via crypto donations across multiple networks:

| Network | Address |
|---------|---------|
| **Bitcoin (BTC)** | `bc1q2k0ftm2fgst22kzj683e8gpau3spfa23ttkg26` |
| **USDT (Ethereum)** | `0xd6f4d8733c8C23e7bEC8Aeba37F4b3D2e93172d1` |
| **USDT (BNB Chain)** | `0xd6f4d8733c8C23e7bEC8Aeba37F4b3D2e93172d1` |
| **USDT (TRON/TRC20)** | `TXVy781mQ2tCuQ1BrattXWueUHp1wB5fwt` |
| **USDT (Solana)** | `GZ8jmSUUzc4dQF7Cthj2atomvpBZWqccR81N9DL4o1Be` |
| **USDT (TON)** | `UQAthXSNIlauj3SrzpDAU4VYxgEVV3niOSmeTPCtMBKGfEAE` |

### 💻 Why Support?
Your contributions help me:
- Upgrade to better development hardware
- Improve workspace and productivity
- Dedicate more time to open source projects
- Add new features faster
- Provide better documentation and examples

### 🤝 Other Ways to Help

- **⭐ Star the Project** - Give us a star on [GitHub](https://github.com/odion-cloud/capacitor-volume-control) to show your support!
- **🐛 Report Issues** - Help improve the plugin by reporting bugs and suggesting features
- **📖 Improve Docs** - Contribute to documentation, examples, and tutorials
- **💬 Spread the Word** - Share the plugin with other developers who might find it useful

## Contributing

We welcome contributions! Please see our [Contributing Guide](https://github.com/odion-cloud/capacitor-volume-control/blob/main/CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/odion-cloud/capacitor-volume-control/blob/main/LICENSE) file for details.

## Changelog

See [CHANGELOG.md](https://github.com/odion-cloud/capacitor-volume-control/blob/main/CHANGELOG.md) for a list of changes and version history.