# Implementation Plan - Multi-Language Support

Add support for English, Kannada, and Hindi to the AgriBridge Android application using MVVM architecture and `AppCompatDelegate`'s per-app language selection.

## User Review Required

> [!IMPORTANT]
> This implementation uses `AppCompatDelegate.setApplicationLocales`, which is the modern Android way to handle per-app language settings. It handles persistence automatically if the `AppLocalesMetadataHolderService` is declared in `AndroidManifest.xml`.

- I will add a simple language selection UI in `MainActivity` (a few buttons or a Spinner) to demonstrate the functionality.

## Proposed Changes

### Dependencies and Configuration

#### [build.gradle.kts](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/build.gradle.kts)
- Add lifecycle-viewmodel-ktx and lifecycle-livedata-ktx if needed (though activity-ktx provides some). I'll add them explicitly to be safe for MVVM.

### Resources

#### [NEW] [strings.xml (kn)](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/res/values-kn/strings.xml)
- Kannada translations for app strings.

#### [NEW] [strings.xml (hi)](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/res/values-hi/strings.xml)
- Hindi translations for app strings.

#### [strings.xml](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/res/values/strings.xml)
- English (default) app strings.

### MVVM Components

#### [NEW] [LanguageViewModel.kt](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/java/com/example/agribridge/viewmodel/LanguageViewModel.kt)
- Manage the selected language state.
- Handle the logic to change the app locale.

#### [NEW] [LocaleHelper.kt](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/java/com/example/agribridge/utils/LocaleHelper.kt)
- Helper class to encapsulate `AppCompatDelegate.setApplicationLocales` logic.

### UI Integration

#### [activity_main.xml](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/res/layout/activity_main.xml)
- Add buttons for English, Kannada, and Hindi.

#### [MainActivity.kt](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/java/com/example/agribridge/MainActivity.kt)
- Initialize `LanguageViewModel`.
- Observe language changes and set click listeners for language buttons.

### Manifest

#### [AndroidManifest.xml](file:///C:/main/syntra-sotwares/AgriBridge/mobile-application/app/src/main/AndroidManifest.xml)
- Declare `AppLocalesMetadataHolderService` to enable automatic persistence of locales by AppCompat for versions below Android 13.

## Verification Plan

### Automated Tests
- I'll create a simple unit test for `LanguageViewModel` to verify it updates the language state correctly.
- Command: `./gradlew test`

### Manual Verification
- Deploy the app to a device/emulator.
- Click on "Kannada", verify the "Hello World" text changes to Kannada.
- Click on "Hindi", verify the "Hello World" text changes to Hindi.
- Click on "English", verify it reverts to English.
- Restart the app and verify the selected language is persisted.
