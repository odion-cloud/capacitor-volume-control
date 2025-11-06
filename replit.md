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

## Recent Changes
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
