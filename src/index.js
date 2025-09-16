import { requireNativeModule } from "expo-modules-core";
// Import the native module. The name 'ExpoAndroidEsimManager' here needs to match
// the name defined in the Kotlin module's `definition` block (`Name("ExpoAndroidEsimManager")`).
const ExpoAndroidEsimManagerModule = requireNativeModule("ExpoAndroidEsimManagerModule");
/**
 * Checks if the device supports eSIM functionality.
 * @returns A promise that resolves with `true` if eSIM is supported, `false` otherwise.
 */
export async function isSupported() {
    return await ExpoAndroidEsimManagerModule.isSupported();
}
/**
 * Initiates the eSIM installation process using an activation code.
 * @param activationCode The activation code string (e.g., "1$smdp.address$matchingId").
 * @returns A promise that resolves with "Success" if the download is initiated successfully
 *          (or completes immediately), or rejects with an error if initiation fails
 *          or the download fails later (potentially after user interaction).
 */
export async function install(activationCode) {
    // Basic validation to prevent sending empty strings
    if (!activationCode || typeof activationCode !== "string") {
        throw new Error("Invalid activation code provided.");
    }
    return await ExpoAndroidEsimManagerModule.install(activationCode);
}
// If your module originally exported types or views, you might need to re-add those exports here.
// For example, if you had types:
// export * from './ExpoAndroidEsimManager.types';
//# sourceMappingURL=index.js.map