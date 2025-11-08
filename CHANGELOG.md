# Changelog

All notable changes to this project will be documented in this file.

## [2.0.0] - 2025-11-08

### Breaking Changes
- **API Change**: `watchVolume()` no longer accepts a callback parameter
- **New Pattern**: Use `addListener('volumeButtonPressed', callback)` before calling `watchVolume(options)`
- Event listeners now persist for continuous hardware button detection instead of one-time callbacks

### Fixed
- **Critical**: Hardware volume buttons now work correctly using proven community package!
- **Button Watching**: Now powered by `@capacitor-community/volume-buttons` (battle-tested implementation)
- **Android**: No MainActivity integration required - buttons work automatically!
- **iOS**: No additional setup needed - buttons work out of the box!
- **TypeScript**: Updated definitions to use proper Capacitor event listener pattern
- **Web**: Added explicit `addListener()` override to match interface signature

### Improved
- **Reliability**: Volume button watching now uses proven `@capacitor-community/volume-buttons` package
- **Setup**: Zero configuration required - just install and use!
- **Documentation**: Simplified README with no complex setup instructions
- **Documentation**: Updated all framework examples (React, Vue, Angular) to use new API
- **Documentation**: Added migration notice for breaking changes
- **Testing**: Updated test suite to match new event listener pattern
- **Architecture**: Clean separation: community package for buttons, our code for get/set volume

### Migration from v1.x

**Update your code** to use event listener pattern:
```javascript
// OLD (v1.x)
await VolumeControl.watchVolume({}, callback);

// NEW (v2.0.0)
await VolumeControl.addListener('volumeButtonPressed', callback);
await VolumeControl.watchVolume({});
```

**No MainActivity changes required!** The plugin now uses MediaSession to automatically intercept volume buttons.

## [1.0.14] - 2025-11-06

### Fixed
- **Android**: Updated Kotlin version from 1.7.10 to 1.9.10 to fix "Module was compiled with an incompatible version of Kotlin" error
- **Android**: Updated Android Gradle Plugin from 7.2.1 to 7.4.2 for better compatibility
- **Android**: Updated kotlin-stdlib dependency to 1.9.10

### Known Issues
- **iOS**: If using the package with a scoped npm name (`@odion-cloud/capacitor-volume-control`), you may need to reference it in package.json as: `"capacitor-volume-control": "npm:@odion-cloud/capacitor-volume-control@^1.0.13"` to ensure the Pod name matches the podspec file name

## [1.0.0] - 2024-01-XX

### Added
- Initial release of Capacitor Volume Control Plugin
- Native Android implementation with Kotlin
- Native iOS implementation with Swift
- Web implementation with limited functionality
- TypeScript definitions and type safety
- Volume level getting and setting (0.0-1.0 range)
- Support for multiple volume types:
  - Voice call
  - System
  - Ring
  - Music (default)
  - Alarm
  - Notification
  - DTMF
- Volume watching functionality with hardware button detection
- Platform-specific options:
  - Android: `suppressVolumeIndicator`
  - iOS: `disableSystemVolumeHandler`
- Comprehensive error handling and validation
- Watch status checking with `isWatching()`
- Complete test suite with Jest
- Detailed documentation and examples

### Features
- **Android**: Uses AudioManager and ContentObserver for volume control
- **iOS**: Uses AVAudioSession and MPVolumeView for volume control
- **Web**: Mock implementation for testing and development
- **Cross-platform**: Consistent API across all platforms
- **Type Safety**: Full TypeScript support with strict typing
- **Error Handling**: Comprehensive error messages and validation

### Technical Details
- Built for Capacitor 5.0+
- Supports Android API 22+
- Supports iOS 13.0+
- Uses modern async/await patterns
- Implements proper cleanup for memory management
- Follows Capacitor plugin development best practices

### Documentation
- Complete API reference
- Usage examples and best practices
- Platform-specific implementation notes
- Testing instructions
- Contributing guidelines