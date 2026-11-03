# T-Diagnostics Manager (Android Studio project)

## Features
- Four account suggestions: gadnew, sidnew, newdb2, ntc
- Password is entered each time and is not stored in the APK
- Opens the official T-Diagnostics portal in an Android WebView
- Attempts to fill the portal login fields using the IDs supplied for the portal (`UserName`, `Password`, `LoginButton`)
- Provides the Patient Details Edit and Delete Aadhaar pages

## Build APK
1. Install Android Studio.
2. Open this folder (`TDiagnosticsManager`) in Android Studio.
3. Allow Gradle sync.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Android Studio will show the generated APK location (normally `app/build/outputs/apk/debug/app-debug.apk`).

## Important
- The app is an unofficial WebView companion, not an official Telangana government app.
- Login automation depends on the portal's current login page and may need adjustment if its form changes or blocks WebView automation. If auto-fill does not work, log in manually in the portal.
- Patient and Aadhaar data are sensitive. Use only authorized accounts/devices, keep Android updated, and log out after use.
- The supplied account passwords are intentionally not embedded in the APK. Enter the password at login.
