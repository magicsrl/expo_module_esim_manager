import { registerWebModule, NativeModule } from "expo";

import { ExpoAndroidEsimManagerModuleEvents } from "./ExpoAndroidEsimManager.types";

class ExpoAndroidEsimManagerModule extends NativeModule<ExpoAndroidEsimManagerModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit("onChange", { value });
  }
  hello() {
    return "Hello world! 👋";
  }
}

export default registerWebModule(
  ExpoAndroidEsimManagerModule,
  "ExpoAndroidEsimManager"
);
