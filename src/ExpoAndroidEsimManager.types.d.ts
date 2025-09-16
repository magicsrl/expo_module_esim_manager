import type { StyleProp, ViewStyle } from 'react-native';
export type OnLoadEventPayload = {
    url: string;
};
export type ExpoAndroidEsimManagerModuleEvents = {
    onChange: (params: ChangeEventPayload) => void;
};
export type ChangeEventPayload = {
    value: string;
};
export type ExpoAndroidEsimManagerViewProps = {
    url: string;
    onLoad: (event: {
        nativeEvent: OnLoadEventPayload;
    }) => void;
    style?: StyleProp<ViewStyle>;
};
//# sourceMappingURL=ExpoAndroidEsimManager.types.d.ts.map