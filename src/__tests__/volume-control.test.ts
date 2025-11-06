import { VolumeControlWeb } from '../web';
import { VolumeType } from '../definitions';

describe('VolumeControlWeb', () => {
  let volumeControl: VolumeControlWeb;

  beforeEach(() => {
    volumeControl = new VolumeControlWeb();
  });

  afterEach(() => {
    // Clean up any active watches
    volumeControl.clearWatch();
  });

  describe('getVolumeLevel', () => {
    it('should return a volume level between 0 and 1', async () => {
      const result = await volumeControl.getVolumeLevel();
      expect(result.value).toBeGreaterThanOrEqual(0);
      expect(result.value).toBeLessThanOrEqual(1);
    });

    it('should handle volume type parameter', async () => {
      const result = await volumeControl.getVolumeLevel({ type: VolumeType.MUSIC });
      expect(result.value).toBeDefined();
    });
  });

  describe('setVolumeLevel', () => {
    it('should accept valid volume values', async () => {
      const result = await volumeControl.setVolumeLevel({ value: 0.5 });
      expect(result.value).toBe(0.5);
    });

    it('should reject volume values below 0', async () => {
      await expect(volumeControl.setVolumeLevel({ value: -0.1 })).rejects.toThrow();
    });

    it('should reject volume values above 1', async () => {
      await expect(volumeControl.setVolumeLevel({ value: 1.1 })).rejects.toThrow();
    });

    it('should handle volume type parameter', async () => {
      const result = await volumeControl.setVolumeLevel({ 
        value: 0.7, 
        type: VolumeType.SYSTEM 
      });
      expect(result.value).toBe(0.7);
    });
  });

  describe('watchVolume', () => {
    it('should start watching volume changes', async () => {
      await volumeControl.watchVolume({});
      
      const watchStatus = await volumeControl.isWatching();
      expect(watchStatus.value).toBe(true);
    });

    it('should reject if already watching', async () => {
      await volumeControl.watchVolume({});
      
      await expect(volumeControl.watchVolume({})).rejects.toThrow();
    });

    it('should handle watch options', async () => {
      await volumeControl.watchVolume({
        disableSystemVolumeHandler: true,
        suppressVolumeIndicator: true
      });
      
      const watchStatus = await volumeControl.isWatching();
      expect(watchStatus.value).toBe(true);
    });

    it('should add event listener for volume button presses', async () => {
      const callback = jest.fn();
      
      const listener = await volumeControl.addListener('volumeButtonPressed', callback);
      expect(listener).toBeDefined();
      expect(typeof listener.remove).toBe('function');
    });
  });

  describe('clearWatch', () => {
    it('should clear volume watch', async () => {
      await volumeControl.watchVolume({});
      await volumeControl.clearWatch();
      
      const watchStatus = await volumeControl.isWatching();
      expect(watchStatus.value).toBe(false);
    });
  });

  describe('isWatching', () => {
    it('should return false initially', async () => {
      const watchStatus = await volumeControl.isWatching();
      expect(watchStatus.value).toBe(false);
    });

    it('should return true when watching', async () => {
      await volumeControl.watchVolume({});
      
      const watchStatus = await volumeControl.isWatching();
      expect(watchStatus.value).toBe(true);
    });
  });

  describe('event listeners', () => {
    it('should add and remove event listeners', async () => {
      const callback = jest.fn();
      
      const listener = await volumeControl.addListener('volumeButtonPressed', callback);
      expect(listener).toBeDefined();
      
      // Remove listener
      if (listener && typeof listener.remove === 'function') {
        await listener.remove();
      }
      
      // Remove all listeners
      await volumeControl.removeAllListeners();
    });

    it('should remove all listeners', async () => {
      const callback1 = jest.fn();
      const callback2 = jest.fn();
      
      await volumeControl.addListener('volumeButtonPressed', callback1);
      await volumeControl.addListener('volumeButtonPressed', callback2);
      
      await volumeControl.removeAllListeners();
      
      // Should not throw
      expect(true).toBe(true);
    });
  });
});