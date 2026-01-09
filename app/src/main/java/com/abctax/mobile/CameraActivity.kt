package com.abctax.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Camera activity for capturing document images
 */
class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var documentManager: DocumentManager
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var documentType: DocumentType

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_DOCUMENT_TYPE = "document_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Get document type from intent
        val typeString = intent.getStringExtra(EXTRA_DOCUMENT_TYPE)
        documentType = DocumentType.valueOf(typeString ?: DocumentType.SUPPORTING_DOCUMENT.name)

        previewView = findViewById(R.id.previewView)
        documentManager = DocumentManager(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Update UI with document type
        findViewById<TextView>(R.id.tvDocumentType).text = documentType.displayName

        // Setup capture button
        findViewById<MaterialButton>(R.id.btnCapture).setOnClickListener {
            captureImage()
        }

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                // Image capture
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, "Camera initialization failed", e)
                Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        // Create file to save image
        val photoFile = documentManager.createImageFile(documentType)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "Image saved: ${photoFile.absolutePath}")
                    
                    // Navigate to review screen
                    val intent = Intent(this@CameraActivity, DocumentReviewActivity::class.java).apply {
                        putExtra(DocumentReviewActivity.EXTRA_IMAGE_PATH, photoFile.absolutePath)
                        putExtra(DocumentReviewActivity.EXTRA_DOCUMENT_TYPE, documentType.name)
                    }
                    startActivity(intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Image capture failed", exception)
                    Toast.makeText(
                        this@CameraActivity,
                        "Failed to capture image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdownNow()
    }
}
