import { ExpoAndroidEsimManagerViewProps } from "./ExpoAndroidEsimManager.types";

export default function ExpoAndroidEsimManagerView(
  props: ExpoAndroidEsimManagerViewProps
) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
