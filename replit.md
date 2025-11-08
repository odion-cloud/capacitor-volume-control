# Capacitor Volume Control Plugin

## Overview
This is a Capacitor plugin library for advanced volume control with native Android and iOS implementations. The plugin provides comprehensive volume management capabilities including volume level control, volume change monitoring, and platform-specific features.

## Project Type
- **Type**: Capacitor Plugin Library (not an app)
- **Language**: TypeScript
- **Build System**: Rollup
- **Package Manager**: npm

## Project Structure
- `src/` - TypeScript source code for the plugin
- `android/` - Native Android implementation (Kotlin)
- `ios/` - Native iOS implementation (Swift/Objective-C)
- `docs/` - Documentation website (HTML/CSS/JS)
- `dist/` - Built plugin files (generated)

## Current Status

✅ **Version 2.0.0 - READY FOR NPM PUBLICATION**
- Plugin is fully fixed and tested
- **REQUIRES MainActivity integration** for volume button detection on Android
- Package ready for production (15.6 kB, clean implementation)
- All native implementations working correctly (notifyListeners pattern)
- Documentation updated with complete MainActivity integration guide
- CHANGELOG documents breaking changes and migration steps
- Ready to publish: `npm publish --access public`

## Implementation Approach
**Android uses Activity-level dispatchKeyEvent** - Users must add volume key event handling to their MainActivity. This is the simple, proven approach used by most Capacitor plugins. See [MAINACTIVITY_INTEGRATION.md](MAINACTIVITY_INTEGRATION.md) for complete guide.

## Recent Changes
- **2025-11-08**: Simplified implementation - removed MediaSession complexity
  - Reverted to simple Activity-level dispatchKeyEvent pattern (MainActivity integration required)
  - Removed androidx.media dependency (reduced package size to 15.6 kB)
  - Clean, straightforward implementation with no external dependencies
  - MainActivity integration is simple and well-documented
  - Build errors completely resolved
- **2025-11-06**: BREAKING CHANGE - Fixed critical volume watching API flaw (v2.0.0)
  - Changed from callback-based to event listener pattern using `notifyListeners()`
  - Android: Updated to use `notifyListeners("volumeButtonPressed", ret)` instead of `call.resolve()`
  - Android: Switched to ACTION_DOWN for more responsive button detection
  - iOS: Updated to use `notifyListeners("volumeButtonPressed", data)` instead of `savedCall.resolve()`
  - TypeScript: Removed callback parameter from `watchVolume()`, added `addListener('volumeButtonPressed', callback)`
  - Web: Added explicit addListener override to match interface signature
  - Fixed `suppressVolumeIndicator` and `disableSystemVolumeHandler` options to work correctly
  - Volume watching now works continuously instead of stopping after first button press
  - Updated all documentation, tests, and examples to use new API pattern
  - Updated CHANGELOG.md with version 2.0.0 breaking changes
- **2025-11-06**: Fixed Android Kotlin version incompatibility issue (#1)
  - Updated Kotlin version from 1.7.10 to 1.9.10 in android/build.gradle
  - Updated Android Gradle Plugin from 7.2.1 to 7.4.2
  - Updated kotlin-stdlib dependency to 1.9.10
  - Added comprehensive troubleshooting section to README
  - Documented iOS Pod naming workaround in README and CHANGELOG
  - Updated CHANGELOG.md with version 1.0.14 changes
- **2025-11-06**: Initial Replit environment setup
  - Installed npm dependencies
  - Built the TypeScript library successfully
  - Set up documentation server on port 5000 with http-server
  - Configured deployment for autoscale (static site)
  - Fixed missing jest-environment-jsdom dependency

## Development Workflow
1. **Build the library**: `npm run build` - Compiles TypeScript and bundles with Rollup
2. **Run tests**: `npm test` - Runs Jest test suite
3. **View documentation**: Documentation website served on port 5000

## Key Scripts
- `npm run build` - Build the plugin library
- `npm test` - Run tests
- `npm run watch` - Watch mode for TypeScript compilation
- `npm run lint` - Run linting
- `serve docs -p 5000` - Serve documentation website

## User Preferences
None set yet.
