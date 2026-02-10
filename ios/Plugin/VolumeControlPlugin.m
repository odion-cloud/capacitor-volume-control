#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(VolumeControlPlugin, "VolumeControl",
           CAP_PLUGIN_METHOD(getVolumeLevel, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setVolumeLevel, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(watchVolume, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(clearWatch, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isWatching, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(removeAllListeners, CAPPluginReturnPromise);
)