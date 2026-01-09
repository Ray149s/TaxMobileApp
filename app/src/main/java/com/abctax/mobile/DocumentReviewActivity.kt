package com.abctax.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.io.File

/**
 * Activity for reviewing captured documents
 */
class DocumentReviewActivity : AppCompatActivity() {

    private lateinit var documentType: DocumentType
    private lateinit var imageFile: File
    private val dropboxService = DropboxService()

    companion object {
        const val EXTRA_IMAGE_PATH = "image_path"
        const val EXTRA_DOCUMENT_TYPE = "document_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_review)

        // Get data from intent
        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH) ?: run {
            finish()
            return
        }
        val typeString = intent.getStringExtra(EXTRA_DOCUMENT_TYPE)
        documentType = DocumentType.valueOf(typeString ?: DocumentType.SUPPORTING_DOCUMENT.name)
        imageFile = File(imagePath)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Display document type
        findViewById<TextView>(R.id.tvDocType).text = documentType.displayName

        // Load and display image
        val imageView = findViewById<ImageView>(R.id.ivDocument)
        Glide.with(this)
            .load(imageFile)
            .into(imageView)
    }

    private fun setupClickListeners() {
        findViewById<MaterialButton>(R.id.btnSign).setOnClickListener {
            openSignatureActivity()
        }

        findViewById<MaterialButton>(R.id.btnUpload).setOnClickListener {
            uploadToDropbox()
        }

        findViewById<MaterialButton>(R.id.btnRetake).setOnClickListener {
            retakePhoto()
        }
    }

    private fun openSignatureActivity() {
        val intent = Intent(this, SignatureActivity::class.java).apply {
            putExtra(SignatureActivity.EXTRA_IMAGE_PATH, imageFile.absolutePath)
            putExtra(SignatureActivity.EXTRA_DOCUMENT_TYPE, documentType.name)
        }
        startActivity(intent)
        finish()
    }

    private fun uploadToDropbox() {
        lifecycleScope.launch {
            try {
                // Create a ScannedDocument object
                val document = ScannedDocument(
                    id = imageFile.nameWithoutExtension,
                    type = documentType,
                    imageFile = imageFile,
                    timestamp = java.util.Date()
                )

                // Show loading state
                Toast.makeText(
                    this@DocumentReviewActivity,
                    "Uploading to Dropbox...",
                    Toast.LENGTH_SHORT
                ).show()

                // Upload to Dropbox (placeholder)
                val success = dropboxService.uploadDocument(document)

                if (success) {
                    Toast.makeText(
                        this@DocumentReviewActivity,
                        R.string.upload_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Return to main activity
                    navigateToMain()
                } else {
                    Toast.makeText(
                        this@DocumentReviewActivity,
                        R.string.error_occurred,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@DocumentReviewActivity,
                    "Upload failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun retakePhoto() {
        // Delete the current photo
        imageFile.delete()
        
        // Go back to camera
        val intent = Intent(this, CameraActivity::class.java).apply {
            putExtra(CameraActivity.EXTRA_DOCUMENT_TYPE, documentType.name)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}
