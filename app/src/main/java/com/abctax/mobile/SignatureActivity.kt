package com.abctax.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Activity for capturing digital signatures
 */
class SignatureActivity : AppCompatActivity() {

    private lateinit var signatureView: SignatureView
    private lateinit var documentType: DocumentType
    private lateinit var imageFile: File
    private lateinit var documentManager: DocumentManager
    private val dropboxService = DropboxService()

    companion object {
        const val EXTRA_IMAGE_PATH = "image_path"
        const val EXTRA_DOCUMENT_TYPE = "document_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        // Get data from intent
        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH) ?: run {
            finish()
            return
        }
        val typeString = intent.getStringExtra(EXTRA_DOCUMENT_TYPE)
        documentType = DocumentType.valueOf(typeString ?: DocumentType.SUPPORTING_DOCUMENT.name)
        imageFile = File(imagePath)
        documentManager = DocumentManager(this)

        signatureView = findViewById(R.id.signatureView)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            signatureView.clear()
        }

        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            saveSignature()
        }
    }

    private fun saveSignature() {
        if (signatureView.isEmpty()) {
            Toast.makeText(this, "Please provide a signature", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Get signature bitmap
            val signatureBitmap = signatureView.getSignatureBitmap()
            
            if (signatureBitmap == null) {
                Toast.makeText(this, "Failed to create signature bitmap", Toast.LENGTH_SHORT).show()
                return
            }

            // Create signature file
            val signatureFile = documentManager.createSignatureFile(imageFile.nameWithoutExtension)

            // Save signature to file
            FileOutputStream(signatureFile).use { out ->
                signatureBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
            }

            Toast.makeText(this, R.string.document_signed, Toast.LENGTH_SHORT).show()

            // Upload signed document
            uploadSignedDocument(signatureFile)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to save signature: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadSignedDocument(signatureFile: File) {
        lifecycleScope.launch {
            try {
                // Create a ScannedDocument object with signature
                val document = ScannedDocument(
                    id = imageFile.nameWithoutExtension,
                    type = documentType,
                    imageFile = imageFile,
                    timestamp = java.util.Date(),
                    signatureFile = signatureFile
                )

                // Show loading state
                Toast.makeText(
                    this@SignatureActivity,
                    "Uploading signed document to Dropbox...",
                    Toast.LENGTH_SHORT
                ).show()

                // Upload to Dropbox (placeholder)
                val success = dropboxService.uploadDocument(document)

                if (success) {
                    Toast.makeText(
                        this@SignatureActivity,
                        "Signed document uploaded successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Return to main activity
                    navigateToMain()
                } else {
                    Toast.makeText(
                        this@SignatureActivity,
                        R.string.error_occurred,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SignatureActivity,
                    "Upload failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}
