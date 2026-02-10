import { registerPlugin } from '@capacitor/core';

import type { VolumeControlPlugin } from './definitions';

const VolumeControl = registerPlugin<VolumeControlPlugin>('VolumeControl', {
  web: () => import('./web').then(m => new m.VolumeControlWeb()),
});

export * from './definitions';
export { VolumeControl };