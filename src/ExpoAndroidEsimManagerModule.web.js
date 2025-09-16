import { registerWebModule, NativeModule } from "expo";
class ExpoAndroidEsimManagerModule extends NativeModule {
    constructor() {
        super(...arguments);
        this.PI = Math.PI;
    }
    async setValueAsync(value) {
        this.emit("onChange", { value });
    }
    hello() {
        return "Hello world! 👋";
    }
}
export default registerWebModule(ExpoAndroidEsimManagerModule, "ExpoAndroidEsimManager");
//# sourceMappingURL=ExpoAndroidEsimManagerModule.web.js.map