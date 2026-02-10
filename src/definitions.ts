import type { PluginListenerHandle } from '@capacitor/core';

export enum VolumeType {
  VOICE_CALL = 'voice_call',
  SYSTEM = 'system',
  RING = 'ring',
  DEFAULT = 'default',
  MUSIC = 'music',
  ALARM = 'alarm',
  NOTIFICATION = 'notification',
  DTMF = 'dtmf'
}

export interface VolumeOptions {
  /**
   * The type of volume to control
   * @default VolumeType.MUSIC
   */
  type?: VolumeType;
}

export interface SetVolumeOptions extends VolumeOptions {
  /**
   * Volume level as a float between 0.0 and 1.0
   * @example 0.5
   */
  value: number;
}

export interface VolumeResult {
  /**
   * Current volume level as a float between 0.0 and 1.0
   */
  value: number;
}

export interface WatchVolumeOptions {
  /**
   * Disable system volume handler (iOS only)
   * When true, prevents system volume UI from appearing
   * @default false
   */
  disableSystemVolumeHandler?: boolean;
  
  /**
   * Suppress volume indicator (Android only)
   * When true, prevents volume UI from appearing
   * @default false
   */
  suppressVolumeIndicator?: boolean;
}

export interface VolumeButtonPressedEvent {
  /**
   * Direction of volume button press
   */
  direction: 'up' | 'down';

  /**
   * Current volume level as a float between 0.0 and 1.0 (best-effort).
   */
  value?: number;
}

export interface VolumeLevelChangedEvent extends VolumeResult {
  /**
   * Direction inferred from the change (best-effort).
   */
  direction?: 'up' | 'down';

  /**
   * The volume stream/type (Android only; iOS is global output volume).
   */
  type?: VolumeType;
}

export interface WatchStatusResult {
  /**
   * Whether volume watching is currently active
   */
  value: boolean;
}

export interface VolumeControlPlugin {
  /**
   * Get current volume level
   * @param options Volume options
   * @returns Promise resolving to current volume level
   * @since 1.0.0
   */
  getVolumeLevel(options?: VolumeOptions): Promise<VolumeResult>;

  /**
   * Set volume level
   * @param options Volume options with value
   * @returns Promise resolving to new volume level
   * @since 1.0.0
   */
  setVolumeLevel(options: SetVolumeOptions): Promise<VolumeResult>;

  /**
   * Start watching volume button presses
   * @param options Watch options
   * @returns Promise resolving when watching starts
   * @since 1.0.0
   */
  watchVolume(options: WatchVolumeOptions): Promise<void>;

  /**
   * Clear volume watch
   * @returns Promise resolving when watch is cleared
   * @since 1.0.0
   */
  clearWatch(): Promise<void>;

  /**
   * Check if volume watching is active
   * @returns Promise resolving to watch status
   * @since 1.0.0
   */
  isWatching(): Promise<WatchStatusResult>;

  /**
   * Listen to volume button press events
   * @param eventName - Name of the event to listen for
   * @param listenerFunc - Callback function that receives the event data
   * @returns Promise resolving to a listener handle
   * @since 1.0.0
   * 
   * @example
   * ```typescript
   * const listener = await VolumeControl.addListener('volumeButtonPressed', (event) => {
   *   console.log('Volume button pressed:', event.direction);
   * });
   * ```
   */
  addListener(
    eventName: 'volumeButtonPressed',
    listenerFunc: (event: VolumeButtonPressedEvent) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen to volume level changes (hardware buttons + system UI + setVolumeLevel()).
   * @since 2.0.2
   */
  addListener(
    eventName: 'volumeLevelChanged',
    listenerFunc: (event: VolumeLevelChangedEvent) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all listeners for this plugin
   * @returns Promise resolving when all listeners are removed
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}