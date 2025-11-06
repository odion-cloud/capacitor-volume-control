import { WebPlugin } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core';

import type { 
  VolumeControlPlugin, 
  VolumeOptions, 
  SetVolumeOptions, 
  VolumeResult, 
  WatchVolumeOptions, 
  WatchStatusResult,
  VolumeButtonPressedEvent
} from './definitions';

export class VolumeControlWeb extends WebPlugin implements VolumeControlPlugin {
  private isWatchingVolume = false;
  private mockVolume = 0.5;

  async getVolumeLevel(options?: VolumeOptions): Promise<VolumeResult> {
    console.log('getVolumeLevel called with options:', options);
    
    // Web implementation - limited to what's available in browsers
    // Most browsers don't expose system volume, so we return a mock value
    return { value: this.mockVolume };
  }

  async setVolumeLevel(options: SetVolumeOptions): Promise<VolumeResult> {
    console.log('setVolumeLevel called with options:', options);
    
    // Validate input
    if (options.value < 0 || options.value > 1) {
      throw new Error('Volume value must be between 0.0 and 1.0');
    }
    
    // Update mock volume
    this.mockVolume = options.value;
    
    // Web implementation - browsers don't allow setting system volume
    // This would typically show a warning or throw an error
    console.warn('Setting system volume is not supported in web browsers');
    
    return { value: options.value };
  }

  async watchVolume(options: WatchVolumeOptions): Promise<void> {
    console.log('watchVolume called with options:', options);
    
    if (this.isWatchingVolume) {
      throw new Error('Volume buttons has already been watched');
    }
    
    this.isWatchingVolume = true;
    
    // Web implementation - limited volume change detection
    // We can't reliably detect hardware volume button presses in browsers
    console.warn('Volume watching has limited functionality in web browsers');
    
    // Simulate volume changes for testing (remove in production)
    if (process.env.NODE_ENV === 'development') {
      setTimeout(() => {
        if (this.isWatchingVolume) {
          this.notifyListeners('volumeButtonPressed', { direction: 'up' });
        }
      }, 3000);
    }
  }

  async clearWatch(): Promise<void> {
    console.log('clearWatch called');
    
    if (!this.isWatchingVolume) {
      throw new Error('Volume buttons has not been watched');
    }
    
    this.isWatchingVolume = false;
  }

  async isWatching(): Promise<WatchStatusResult> {
    return { value: this.isWatchingVolume };
  }

  addListener(
    eventName: 'volumeButtonPressed',
    listenerFunc: (event: VolumeButtonPressedEvent) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle {
    return super.addListener(eventName, listenerFunc);
  }

  async removeAllListeners(): Promise<void> {
    return super.removeAllListeners();
  }
}