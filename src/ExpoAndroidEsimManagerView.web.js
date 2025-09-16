import { jsx as _jsx } from "react/jsx-runtime";
export default function ExpoAndroidEsimManagerView(props) {
    return (_jsx("div", { children: _jsx("iframe", { style: { flex: 1 }, src: props.url, onLoad: () => props.onLoad({ nativeEvent: { url: props.url } }) }) }));
}
//# sourceMappingURL=ExpoAndroidEsimManagerView.web.js.map