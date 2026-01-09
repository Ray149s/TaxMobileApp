# TaxMobileApp

ABC Tax Company Android Mobile Application

## Overview

This Android mobile application allows ABC Tax Company clients to:
- Scan driver's licenses, identification documents, tax forms, and supporting documents using the phone's camera
- Review captured documents
- Add digital signatures to documents
- Upload documents to Dropbox (placeholder functionality implemented)
- Submit signed documents

## Features

### Document Scanning
- **Camera Integration**: Uses CameraX API for high-quality document capture
- **Document Types Supported**:
  - Driver's License
  - Identification Documents
  - Tax Forms
  - Supporting Documents

### Document Management
- Review captured images before submission
- Retake photos if needed
- Store documents locally on device

### Digital Signature
- Custom signature pad for capturing client signatures
- Signatures saved as PNG images
- Attach signatures to scanned documents

### Dropbox Integration
- Placeholder implementation for Dropbox upload functionality
- Ready to integrate with actual Dropbox API when credentials are available
- To implement actual Dropbox integration, add the SDK: `implementation 'com.dropbox.core:dropbox-core-sdk:5.4.4'`

## Technical Stack

- **Language**: Kotlin
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 33 (Android 13)
- **Build System**: Gradle 8.0
- **Key Libraries**:
  - AndroidX Core KTX
  - Material Design Components
  - CameraX for camera functionality
  - Glide for image loading
  - Kotlin Coroutines for asynchronous operations

## Project Structure

```
app/src/main/java/com/abctax/mobile/
├── MainActivity.kt              # Entry point, document type selection
├── CameraActivity.kt            # Camera capture functionality
├── DocumentReviewActivity.kt    # Review and upload documents
├── SignatureActivity.kt         # Capture digital signatures
├── SignatureView.kt            # Custom view for signature capture
├── DocumentType.kt             # Document type enumeration and data models
├── DocumentManager.kt          # Document storage and retrieval
└── DropboxService.kt           # Dropbox integration (placeholder)
```

## Building the App

### Prerequisites

1. Android Studio Arctic Fox or later
2. JDK 8 or later
3. Android SDK with API level 33

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/Ray149s/TaxMobileApp.git
   cd TaxMobileApp
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build the app:
   ```bash
   ./gradlew assembleDebug
   ```

5. Run on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

## Permissions

The app requires the following permissions:
- **Camera**: To capture document images
- **Storage**: To save captured images and signatures (scoped storage on Android 10+)
- **Internet**: For future Dropbox integration

## User Flow

1. **Launch App**: User sees the main screen with document type options
2. **Select Document Type**: User chooses the type of document to scan
3. **Grant Camera Permission**: If not already granted, user approves camera access
4. **Capture Document**: User takes a photo of the document
5. **Review Document**: User reviews the captured image
6. **Sign Document** (optional): User can add a digital signature
7. **Upload**: Document is uploaded to Dropbox (placeholder)

## Future Enhancements

- Actual Dropbox API integration with OAuth authentication
- Document list view to manage all scanned documents
- PDF generation from scanned images
- OCR (Optical Character Recognition) for form data extraction
- Multi-page document scanning
- Image enhancement (auto-crop, brightness adjustment)
- Encrypted local storage
- Biometric authentication

## Security Considerations

- All documents are stored in the app's private directory
- Use HTTPS for all network communications
- Implement proper authentication when integrating with Dropbox
- Consider encrypting sensitive documents at rest
- Implement secure signature verification

## Notes

- The Dropbox upload functionality is currently a placeholder that simulates the upload process
- To implement actual Dropbox integration, you'll need:
  1. Dropbox API credentials (App Key and App Secret)
  2. Add the Dropbox SDK dependency
  3. Implement OAuth 2.0 authentication flow
  4. Update the `DropboxService.kt` with actual API calls

## License

Copyright © 2024 ABC Tax Company. All rights reserved.
 
