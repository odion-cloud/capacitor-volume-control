# Changelog

All notable changes to this project will be documented in this file.

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