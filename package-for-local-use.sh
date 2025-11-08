#!/bin/bash
# Script to package the fixed plugin for local installation in your Capacitor project

echo "📦 Building and packaging the fixed Volume Control plugin..."
echo ""

# Build the plugin
echo "1️⃣ Building TypeScript..."
npm run build

if [ $? -ne 0 ]; then
  echo "❌ Build failed!"
  exit 1
fi

# Create package
echo ""
echo "2️⃣ Creating npm package..."
npm pack

if [ $? -ne 0 ]; then
  echo "❌ Package creation failed!"
  exit 1
fi

# Find the created package file
PACKAGE_FILE=$(ls -t odion-cloud-capacitor-volume-control-*.tgz | head -1)

if [ -z "$PACKAGE_FILE" ]; then
  echo "❌ Package file not found!"
  exit 1
fi

echo ""
echo "✅ Package created successfully: $PACKAGE_FILE"
echo ""
echo "📋 Next steps:"
echo ""
echo "1. Copy this file to your Capacitor project:"
echo "   cp $PACKAGE_FILE /path/to/your/capacitor/project/"
echo ""
echo "2. In your Capacitor project, install it:"
echo "   npm install ./$PACKAGE_FILE"
echo ""
echo "3. Sync to native platforms:"
echo "   npx cap sync android"
echo ""
echo "4. Run your app:"
echo "   npx cap run android"
echo ""
echo "🔍 Or use the current path directly in package.json:"
echo "   \"@odion-cloud/capacitor-volume-control\": \"file:$(pwd)\""
echo ""
