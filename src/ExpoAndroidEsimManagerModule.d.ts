import { NativeModule } from "expo";
import { ExpoAndroidEsimManagerModuleEvents } from "./ExpoAndroidEsimManager.types";
declare class ExpoAndroidEsimManagerModule extends NativeModule<ExpoAndroidEsimManagerModuleEvents> {
    PI: number;
    hello(): string;
    setValueAsync(value: string): Promise<void>;
}
declare const _default: ExpoAndroidEsimManagerModule;
export default _default;
//# sourceMappingURL=ExpoAndroidEsimManagerModule.d.ts.map