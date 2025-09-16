import { NativeModule } from "expo";
import { ExpoAndroidEsimManagerModuleEvents } from "./ExpoAndroidEsimManager.types";
declare class ExpoAndroidEsimManagerModule extends NativeModule<ExpoAndroidEsimManagerModuleEvents> {
    PI: number;
    setValueAsync(value: string): Promise<void>;
    hello(): string;
}
declare const _default: typeof ExpoAndroidEsimManagerModule;
export default _default;
//# sourceMappingURL=ExpoAndroidEsimManagerModule.web.d.ts.map