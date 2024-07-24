<sup><sub>This repository contains the source code of Cryptool Android application.</sub></sup>

<p align="center">
  <img src=".github/dev/icon.png?raw=true" alt="Cryptool"/>
</p>

# Cryptool

<p align="left">
  <a href="https://crowdin.com/project/cryptool">
    <img src="https://badges.crowdin.net/cryptool/localized.svg" alt="Crowdin translation platform">
  </a>
  <a href="https://github.com/nfdz/Cryptool/actions/workflows/ci.yml">
    <img alt="build and test status" src="https://github.com/nfdz/Cryptool/actions/workflows/ci.yml/badge.svg">
  </a>
</p>

*Cryptography for humans*

Cryptool wants to help you to protect the information that matter most to you. We don't hide anything that's going on under the hood, we show the algorithms and data input/output as it is.

This is a non-profit open source solution and we are not interested in your data. Anyway, we do not ask you to trust, we ask you to **block** the Internet access, **review** the code, or even **build** the app yourself.

## Main features

- Lightweight application.
- Modern UI. Material You + support to light/dark theme.
- Multiple encryption configurations as conversations.
- Multiple message sources.
  - Manual. Handle the input and output of the communication yourself.
  - LAN. Communication within the connected Local Area Network. It is forgotten when the app stops.
  - File. Use two files for communication. You can auto-sync and share the files for real time communication.
  - SMS. Use your SMS provider. This option could have cost depending the contract with your provider.
- Keystore.
- Multiple algorithms and encryption configurations.
- Interoperable encryption.
- Clipboard control.
- Export/Import:
  - Custom code protection.
  - Filter data.
- Access code protection:
  - Forget/Reset.
  - Change.
  - Biometric identification.

## Contributing

If you think something is missing from the application, please create an issue to discuss it or make a pull request if you can implement it yourself.

## Translation

The application is translated by volunteers on a collaborative translation platform. You can help complete and improve the translations by joining the [Crowdin](https://crowdin.com/project/cryptool) project. If you would like to start the translation in a new language, contact or create an issue. Any help is greatly appreciated!

## Google Play Limitation

Google Play does not allow the use of SMS data if it is not selected as the default SMS application of the system. As Cryptool does not want to be an application of this type, the Google Play version has this feature disabled. If you want to use it you will have to use the GitHub version, or build the application yourself.

## Download

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=io.github.nfdz.cryptool"><img width="250" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png?raw=true" alt="Get it on Google Play"/></a>
  <a href="https://apt.izzysoft.de/fdroid/index/apk/io.github.nfdz.cryptool"><img width="250" src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid"/></a>
  <a href="https://github.com/nfdz/Cryptool/releases/latest/download/CryptoolApp-release.apk"><img width="250" src=".github/dev/get-it-on-github.png?raw=true" alt="Get it on GitHub"/></a>
</p>

### Verification
> Signer certificate SHA-1 digest: e5cb650a27c10826cbfd5699d397630d11691359
*Verify locally with the following command: `apksigner verify --print-certs ./CryptoolApp-release.apk`*

## Screenshots

<p align="center">
  <img src=".github/dev/screenshots/en/1.png?raw=true" width="250" alt="Cryptool"/>
  <img src=".github/dev/screenshots/en/2.png?raw=true" width="250" alt="Cryptool"/>
  <img src=".github/dev/screenshots/en/3.png?raw=true" width="250" alt="Cryptool"/>
</p>
<p align="center">
  <img src=".github/dev/screenshots/en/4.png?raw=true" width="250" alt="Cryptool"/>
  <img src=".github/dev/screenshots/en/5.png?raw=true" width="250" alt="Cryptool"/>
  <img src=".github/dev/screenshots/en/6.png?raw=true" width="250" alt="Cryptool"/>
</p>

> *Google Play and the Google Play logo are trademarks of Google Inc.*

> *GitHub and the GitHub logo are trademarks of GitHub Inc.*
