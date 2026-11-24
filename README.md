# InVxTermux:Styling

Unofficial fork of [termux/termux-styling](https://github.com/termux/termux-styling)
for **[InVxTermux](https://github.com/involvex/termux-app)**
(`com.involvex.termux_app`).

| | Stock Termux | This fork |
|---|---|---|
| applicationId | `com.termux.styling` | `com.involvex.termux_app.styling` |
| sharedUserId | `com.termux` | `com.involvex.termux_app` |
| Reload broadcast | `com.termux.app.reload_style` | `com.involvex.termux_app.app.reload_style` |

Must be signed with the **same** key as the main InVxTermux APK (public test key
`testkey_untrusted.jks` matches the main app’s debug/release test builds). Stock
`com.termux.styling` from F-Droid will **not** install alongside InVxTermux.

## Build

```bash
./gradlew :app:assembleDebug
# APK under app/build/outputs/apk/debug/
```

Install next to InVxTermux (same signature / sharedUserId):

```bash
adb install -r app/build/outputs/apk/debug/termux-styling-app_v0.32.2+debug.apk
```

## How to use

1. Inside InVxTermux, long-press the terminal → **More…** → **Style**.
2. Choose color or font.

## Upstream

Fonts/colors assets and most UI come from upstream Termux:Styling. Prefer
sending theme/font PRs upstream when they are not fork-specific.
