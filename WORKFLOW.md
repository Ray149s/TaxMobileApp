# ABC Tax Mobile App - User Workflow

## Application Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         App Launch                               │
│                            ↓                                     │
│                      MainActivity                                │
│    ┌───────────────────────────────────────────────┐            │
│    │  Welcome to ABC Tax Company                    │            │
│    │                                                │            │
│    │  Select Document Type:                         │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  📷 Driver's License                     │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  📷 Identification Document              │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  📷 Tax Form                             │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  📷 Supporting Document                  │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │                                                │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  📁 My Documents                         │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    └───────────────────────────────────────────────┘            │
│                            ↓                                     │
│                   User Selects Document Type                     │
│                            ↓                                     │
│              Camera Permission Check                             │
│         (If not granted, request permission)                     │
│                            ↓                                     │
│                      CameraActivity                              │
│    ┌───────────────────────────────────────────────┐            │
│    │  [Document Type: Driver's License]             │            │
│    │                                                │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │                                          │ │            │
│    │  │         Camera Preview                   │ │            │
│    │  │      (Live camera feed)                  │ │            │
│    │  │                                          │ │            │
│    │  │                                          │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │                                                │            │
│    │              ┌────────┐                        │            │
│    │              │   📸   │  Capture Button        │            │
│    │              └────────┘                        │            │
│    └───────────────────────────────────────────────┘            │
│                            ↓                                     │
│                   User Captures Image                            │
│                            ↓                                     │
│                 DocumentReviewActivity                           │
│    ┌───────────────────────────────────────────────┐            │
│    │  Review Document                               │            │
│    │  Document Type: Driver's License               │            │
│    │                                                │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │                                          │ │            │
│    │  │      Captured Image Preview              │ │            │
│    │  │                                          │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │                                                │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  ✍️  Sign Document                        │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  ☁️  Upload to Dropbox                    │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    │  ┌──────────────────────────────────────────┐ │            │
│    │  │  🔄 Retake                                │ │            │
│    │  └──────────────────────────────────────────┘ │            │
│    └───────────────────────────────────────────────┘            │
│              ↓                        ↓                          │
│    User clicks "Sign"      User clicks "Upload"                 │
│              ↓                        ↓                          │
│     SignatureActivity         Upload to Dropbox                 │
│    ┌────────────────────┐           ↓                           │
│    │  Sign Document      │    (Placeholder Service)             │
│    │                    │           ↓                           │
│    │ ┌────────────────┐ │    Success Message                    │
│    │ │                │ │           ↓                           │
│    │ │  Signature Pad │ │    Return to MainActivity             │
│    │ │  (Draw here)   │ │                                       │
│    │ │                │ │                                       │
│    │ └────────────────┘ │                                       │
│    │                    │                                       │
│    │ [Clear]  [Save]    │                                       │
│    └────────────────────┘                                       │
│              ↓                                                   │
│    User Signs & Saves                                           │
│              ↓                                                   │
│    Upload Signed Document                                       │
│              ↓                                                   │
│    Success → Return to MainActivity                             │
└─────────────────────────────────────────────────────────────────┘
```

## Detailed Feature Breakdown

### 1. Document Type Selection
**Screen**: MainActivity
- **Purpose**: Allow users to select what type of document they want to scan
- **Options**:
  - Driver's License
  - Identification Document
  - Tax Form
  - Supporting Document
- **Actions**:
  - Tap any button → Request camera permission (if needed) → Open camera
  - "My Documents" → View previously scanned documents (shows count)

### 2. Camera Capture
**Screen**: CameraActivity
- **Purpose**: Capture high-quality images of documents
- **Features**:
  - Live camera preview using CameraX
  - Document type displayed at top
  - Large capture button at bottom
  - Auto-focus and exposure adjustment
  - Back camera used by default
- **Actions**:
  - Tap capture button → Take photo → Navigate to review screen
  - Back button → Return to main screen

### 3. Document Review
**Screen**: DocumentReviewActivity
- **Purpose**: Review captured image and decide next steps
- **Features**:
  - Full preview of captured image
  - Document type label
  - Three action buttons
- **Actions**:
  - "Sign Document" → Open signature screen
  - "Upload to Dropbox" → Simulate upload → Show success → Return to main
  - "Retake" → Delete current photo → Return to camera

### 4. Signature Capture
**Screen**: SignatureActivity
- **Purpose**: Capture digital signature for documents
- **Features**:
  - Custom signature canvas (white background)
  - Touch-based signature drawing
  - Clear button to restart
  - Save button to proceed
- **Actions**:
  - Draw signature with finger/stylus
  - "Clear" → Erase signature → Start over
  - "Save" → Save signature as PNG → Upload signed document → Return to main

### 5. Upload Process
**Component**: DropboxService (Placeholder)
- **Current Implementation**:
  - Simulates 2-second network delay
  - Logs upload action
  - Returns success
- **Future Implementation**:
  - OAuth authentication with Dropbox
  - Actual file upload to client's Dropbox folder
  - Error handling and retry logic

## File Storage Structure

```
App External Files Directory/
├── Documents/
│   ├── DRIVERS_LICENSE_20240109_143022.jpg
│   ├── TAX_FORM_20240109_143045.jpg
│   └── SUPPORTING_DOCUMENT_20240109_143108.jpg
└── Signatures/
    ├── SIG_DRIVERS_LICENSE_20240109_143022_20240109_143055.png
    └── SIG_TAX_FORM_20240109_143045_20240109_143112.png
```

## Permission Flow

```
App Launch
    ↓
User selects document type
    ↓
Check CAMERA permission
    ↓
┌──────────────────┐
│ Permission       │
│ Granted?         │
└────┬────────┬────┘
     │ No     │ Yes
     ↓        ↓
Show Dialog   Open Camera
     ↓
User Grants/Denies
     ↓
┌──────────────────┐
│ Granted?         │
└────┬────────┬────┘
     │ No     │ Yes
     ↓        ↓
Show Error   Open Camera
Message
```

## Data Flow

```
User Input (Camera) 
    ↓
Image File (.jpg)
    ↓
Local Storage
    ↓
DocumentReviewActivity
    ↓
Optional: SignatureActivity
    ↓
Signature File (.png)
    ↓
ScannedDocument Object
    ↓
DropboxService
    ↓
[Simulated Upload]
    ↓
Success Notification
```

## Error Handling

### Camera Errors
- **No camera permission**: Show permission rationale dialog
- **Camera unavailable**: Display error message, return to main screen
- **Capture failed**: Show error toast, allow retry

### Storage Errors
- **Insufficient space**: Notify user, prevent capture
- **File write error**: Show error message, allow retry

### Upload Errors
- **No internet connection**: Show error, offer retry (when Dropbox integrated)
- **Authentication failure**: Prompt re-login (when Dropbox integrated)
- **Upload timeout**: Show error, offer retry (when Dropbox integrated)

## Key User Interactions

1. **First Time Use**:
   - Launch app
   - See welcome screen
   - Tap document type
   - See permission dialog
   - Grant camera permission
   - Use camera

2. **Regular Use**:
   - Launch app
   - Tap document type (permission already granted)
   - Capture document
   - Review and upload
   - Return to main screen

3. **With Signature**:
   - Launch app
   - Select document type
   - Capture document
   - Tap "Sign Document"
   - Draw signature
   - Save signature
   - Auto-upload with signature
   - Return to main screen

## Technical Notes

- All navigation uses explicit Intents
- Document type passed via Intent extras
- Files stored in app-private directory
- FileProvider used for sharing images (if needed)
- Coroutines used for async operations
- Glide library for efficient image loading
