# ABC Tax Mobile App - Setup and Development Guide

## Quick Start

### 1. Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (Arctic Fox 2020.3.1 or later)
  - Download from: https://developer.android.com/studio
- **Java Development Kit (JDK)** 8 or 11
- **Android SDK** with the following components:
  - Android SDK Platform 33
  - Android SDK Build-Tools
  - Android Emulator (for testing)

### 2. Clone and Open Project

```bash
git clone https://github.com/Ray149s/TaxMobileApp.git
cd TaxMobileApp
```

Open Android Studio and select "Open an Existing Project", then navigate to the cloned directory.

### 3. Sync Gradle

Android Studio should automatically prompt you to sync Gradle files. If not:
- Click "File" → "Sync Project with Gradle Files"
- Wait for the sync to complete (first time may take several minutes)

### 4. Configure Android SDK

If Android Studio shows SDK errors:
1. Go to "Tools" → "SDK Manager"
2. Ensure Android 13.0 (API 33) is installed under "SDK Platforms"
3. Under "SDK Tools", ensure these are installed:
   - Android SDK Build-Tools
   - Android Emulator
   - Android SDK Platform-Tools

### 5. Build the Project

#### Using Android Studio:
- Click "Build" → "Make Project" or press Ctrl+F9 (Cmd+F9 on Mac)

#### Using Command Line:
```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

### 6. Run the App

#### On Emulator:
1. Go to "Tools" → "Device Manager"
2. Create a new Virtual Device (if none exists)
   - Recommended: Pixel 5 with Android 13 (API 33)
3. Click the "Run" button (green play icon) or press Shift+F10

#### On Physical Device:
1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Click the "Run" button and select your device

## Project Architecture

### Key Components

#### Activities

1. **MainActivity**
   - Entry point of the application
   - Displays document type selection buttons
   - Handles camera permission requests
   - Launches CameraActivity with selected document type

2. **CameraActivity**
   - Implements CameraX for document capture
   - Displays camera preview
   - Captures high-quality images
   - Saves images to app storage
   - Navigates to DocumentReviewActivity

3. **DocumentReviewActivity**
   - Displays captured document image
   - Provides options to:
     - Retake photo
     - Add signature
     - Upload to Dropbox
   - Handles Dropbox upload (placeholder)

4. **SignatureActivity**
   - Provides digital signature capture
   - Uses custom SignatureView
   - Saves signature as PNG
   - Uploads signed document

#### Core Classes

1. **DocumentType** (Enum)
   - DRIVERS_LICENSE
   - IDENTIFICATION
   - TAX_FORM
   - SUPPORTING_DOCUMENT

2. **ScannedDocument** (Data Class)
   - Represents a scanned document with metadata
   - Contains image file, signature, timestamp, etc.

3. **DocumentManager**
   - Manages document file creation and storage
   - Lists all scanned documents
   - Handles document deletion

4. **DropboxService**
   - Placeholder for Dropbox integration
   - Currently simulates upload with delay
   - Ready for actual API integration

5. **SignatureView** (Custom View)
   - Custom drawing view for signatures
   - Captures touch events
   - Generates signature bitmap

## Permissions

The app uses the following permissions (declared in AndroidManifest.xml):

```xml
<!-- Required for camera access -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- For devices running Android 12 and below -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="32" />

<!-- For devices running Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- For Dropbox integration -->
<uses-permission android:name="android.permission.INTERNET" />
```

Runtime permissions (Camera) are requested in MainActivity.

## Storage

Documents and signatures are stored in the app's private external files directory:
- **Documents**: `[App External Files Dir]/Documents/`
- **Signatures**: `[App External Files Dir]/Signatures/`

File naming convention:
- Documents: `[DOCUMENT_TYPE]_[TIMESTAMP].jpg`
- Signatures: `SIG_[DOCUMENT_ID]_[TIMESTAMP].png`

## Testing

### Manual Testing Flow

1. **Launch app** → Verify main screen displays
2. **Tap "Driver's License"** → Camera permission dialog appears
3. **Grant permission** → Camera opens
4. **Point at a document** → Preview shows in real-time
5. **Tap capture button** → Image captured
6. **Review screen appears** → Captured image displays
7. **Tap "Sign Document"** → Signature screen opens
8. **Draw signature** → Signature appears on canvas
9. **Tap "Save"** → Success message shows
10. **Upload completes** → Returns to main screen

### Testing on Different Android Versions

Test on these API levels:
- API 24 (Android 7.0) - Minimum supported
- API 29 (Android 10) - Scoped storage introduction
- API 33 (Android 13) - Target version

## Integrating Dropbox API

To implement actual Dropbox functionality:

### Step 1: Create Dropbox App
1. Go to https://www.dropbox.com/developers/apps
2. Create a new app
3. Note your App Key and App Secret

### Step 2: Add Dropbox SDK

Update `app/build.gradle`:
```gradle
dependencies {
    // ... existing dependencies ...
    implementation 'com.dropbox.core:dropbox-core-sdk:5.4.4'
}
```

### Step 3: Configure Authentication

Add to `AndroidManifest.xml`:
```xml
<activity
    android:name="com.dropbox.core.android.AuthActivity"
    android:configChanges="orientation|keyboard"
    android:launchMode="singleTask">
    <intent-filter>
        <data android:scheme="db-YOUR_APP_KEY" />
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.BROWSABLE" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

### Step 4: Update DropboxService.kt

Replace placeholder implementation with actual API calls:
```kotlin
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import java.io.FileInputStream

class DropboxService(private val accessToken: String) {
    private val client: DbxClientV2
    
    init {
        val config = DbxRequestConfig.newBuilder("TaxMobileApp").build()
        client = DbxClientV2(config, accessToken)
    }
    
    suspend fun uploadFile(file: File, remotePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                FileInputStream(file).use { inputStream ->
                    client.files().uploadBuilder(remotePath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream)
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "Upload failed", e)
                false
            }
        }
    }
}
```

## Troubleshooting

### Build Issues

**Problem**: Gradle sync fails
- **Solution**: Ensure you have a stable internet connection for dependency downloads
- Check that Android SDK is properly installed
- Try "File" → "Invalidate Caches / Restart"

**Problem**: SDK not found
- **Solution**: Set ANDROID_HOME environment variable pointing to your SDK location
- In Android Studio: "File" → "Project Structure" → "SDK Location"

### Runtime Issues

**Problem**: Camera not working
- **Solution**: Ensure camera permission is granted
- Test on a real device (emulators may have camera issues)
- Check that device has a rear camera

**Problem**: App crashes on startup
- **Solution**: Check Logcat for error messages
- Ensure minimum SDK version (24) is met
- Clear app data and try again

## Code Quality

### Linting
```bash
./gradlew lint
```

### Code Style
This project follows Kotlin coding conventions:
- https://kotlinlang.org/docs/coding-conventions.html

## Building for Release

### Step 1: Create Keystore
```bash
keytool -genkey -v -keystore taxmobile.keystore -alias taxmobile -keyalg RSA -keysize 2048 -validity 10000
```

### Step 2: Configure Signing

Create `keystore.properties` in project root:
```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=taxmobile
storeFile=../taxmobile.keystore
```

### Step 3: Update app/build.gradle

Add signing config:
```gradle
android {
    signingConfigs {
        release {
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // ...
        }
    }
}
```

### Step 4: Build Release APK
```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

## Support

For issues and questions:
- Check the README.md for general information
- Review this SETUP.md for configuration help
- Check Android Studio's Logcat for error messages

## Next Steps

1. ✅ Set up development environment
2. ✅ Build and run the app
3. ✅ Test core functionality
4. ⏳ Integrate Dropbox API
5. ⏳ Deploy to Google Play Store (optional)
