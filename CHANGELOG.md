# Changelog

All notable changes to this project will be documented in this file.

## [2.0.0] - 2025-11-06

### Breaking Changes
- **API Change**: `watchVolume()` no longer accepts a callback parameter
- **New Pattern**: Use `addListener('volumeButtonPressed', callback)` before calling `watchVolume(options)`
- Event listeners now persist for continuous hardware button detection instead of one-time callbacks

### Fixed
- **Critical**: Volume watching now works continuously instead of stopping after the first button press
- **Android**: Changed from `call.resolve()` to `notifyListeners()` for persistent event emission
- **Android**: Switched to ACTION_DOWN for more responsive button detection
- **Android**: Fixed `suppressVolumeIndicator` option to work correctly with event listeners
- **iOS**: Changed from `savedCall.resolve()` to `notifyListeners()` for persistent event emission
- **iOS**: Fixed `disableSystemVolumeHandler` option to work correctly with event listeners
- **TypeScript**: Updated definitions to use proper Capacitor event listener pattern
- **Web**: Added explicit `addListener()` override to match interface signature

### Improved
- **Documentation**: Comprehensive README updates with correct usage examples
- **Documentation**: Updated all framework examples (React, Vue, Angular) to use new API
- **Documentation**: Added migration notice for breaking changes
- **Testing**: Updated test suite to match new event listener pattern

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