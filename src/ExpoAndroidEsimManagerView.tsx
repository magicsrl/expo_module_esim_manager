import { requireNativeViewManager } from "expo-modules-core";
import type { ComponentType } from "react";
import type { ExpoAndroidEsimManagerViewProps } from "./ExpoAndroidEsimManager.types";

const NativeView: ComponentType<ExpoAndroidEsimManagerViewProps> =
  requireNativeViewManager("ExpoAndroidEsimManager");

export default function ExpoAndroidEsimManagerView(
  props: ExpoAndroidEsimManagerViewProps
) {
  return <NativeView {...props} />;
}
