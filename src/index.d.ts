/**
 * Checks if the device supports eSIM functionality.
 * @returns A promise that resolves with `true` if eSIM is supported, `false` otherwise.
 */
export declare function isSupported(): Promise<boolean>;
/**
 * Initiates the eSIM installation process using an activation code.
 * @param activationCode The activation code string (e.g., "1$smdp.address$matchingId").
 * @returns A promise that resolves with "Success" if the download is initiated successfully
 *          (or completes immediately), or rejects with an error if initiation fails
 *          or the download fails later (potentially after user interaction).
 */
export declare function install(activationCode: string): Promise<string>;
//# sourceMappingURL=index.d.ts.map