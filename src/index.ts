import { registerPlugin } from '@capacitor/core';
import { VolumeButtons } from '@capacitor-community/volume-buttons';

import type { VolumeControlPlugin, VolumeButtonPressedEvent, WatchVolumeOptions, PluginListenerHandle } from './definitions';

const VolumeControlNative = registerPlugin<VolumeControlPlugin>('VolumeControl', {
  web: () => import('./web').then(m => new m.VolumeControlWeb()),
});

// Store listeners for event forwarding
const listeners = new Set<(event: VolumeButtonPressedEvent) => void>();
let isVolumeWatching = false;

// Wrapper that combines our get/set volume with community button watching
const VolumeControl: VolumeControlPlugin = {
  // Our custom get/set volume methods
  getVolumeLevel: (options) => VolumeControlNative.getVolumeLevel(options),
  setVolumeLevel: (options) => VolumeControlNative.setVolumeLevel(options),
  
  // Delegate button watching to community package
  watchVolume: async (options: WatchVolumeOptions = {}) => {
    if (listeners.size === 0) {
      // No listeners yet, just mark as watching
      isVolumeWatching = true;
      return;
    }
    
    // Start watching with callback that forwards to all listeners
    await VolumeButtons.watchVolume(
      {
        disableSystemVolumeHandler: options.disableSystemVolumeHandler,
        suppressVolumeIndicator: options.suppressVolumeIndicator,
      },
      (result: { direction: 'up' | 'down' }) => {
        const event: VolumeButtonPressedEvent = { direction: result.direction };
        listeners.forEach(listener => listener(event));
      }
    );
    isVolumeWatching = true;
  },
  
  clearWatch: async () => {
    await VolumeButtons.clearWatch();
    isVolumeWatching = false;
  },
  
  isWatching: async () => {
    const result = await VolumeButtons.isWatching();
    return { value: result.value };
  },
  
  addListener: (
    eventName: 'volumeButtonPressed',
    listenerFunc: (event: VolumeButtonPressedEvent) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle => {
    listeners.add(listenerFunc);
    
    const handle: PluginListenerHandle = {
      remove: async () => {
        listeners.delete(listenerFunc);
      },
    };
    
    return handle as Promise<PluginListenerHandle> & PluginListenerHandle;
  },
  
  removeAllListeners: async () => {
    listeners.clear();
  },
};

export * from './definitions';
export { VolumeControl };