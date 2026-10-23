# DNS Toggle by Deon

One-tap **Private DNS ON / OFF** for Android — no more digging through Settings just to flip your DNS.

## Features

- 🔀 Big one-tap toggle for Android Private DNS
- 🏠 **Two home-screen widgets** (Square + Round) — app-icon sized, freely resizable, tap to switch ON/OFF
- ⚡ Quick Settings tile for the notification shade
- ✏️ Customisable DNS hostname (default: AdGuard `dns.adguard-dns.com`)
- 🎯 One-tap presets — AdGuard, AdGuard Family, Cloudflare, Google, Quad9
- 🌗 Teal theme, English UI

## One-time setup

Android only lets apps change the Private DNS setting after a single permission grant. No root, no PC needed — the free **Shizuku** + **aShell You** apps can do it right on the phone:

1. Install [Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api) and start it via Wireless debugging (pairing code).
2. Install [aShell You](https://f-droid.org/packages/in.hridayan.ashell/) (free, on F-Droid) and connect it to the running Shizuku device.
3. Run this one command:

```
pm grant com.deon.dnstoggle android.permission.WRITE_SECURE_SETTINGS
```

That's it — after that, the widget, the Quick Settings tile and the in-app button all work with one tap. The grant stays until the app is uninstalled.

## Download

Grab the latest APK from the [Releases](https://github.com/D30N/DNSToggle/releases) page.

## Build it yourself

```bash
bash offline-build.sh assembleRelease
```

## License

Free to use and share. All rights reserved — © Deepak Deon.

---

*This app was made by [Deon](https://deepakdeon.github.io)*
