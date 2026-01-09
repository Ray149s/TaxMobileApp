package com.abctax.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

/**
 * Main activity - Entry point of the application
 * Allows users to select document type and initiate scanning
 */
class MainActivity : AppCompatActivity() {

    private lateinit var documentManager: DocumentManager

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch camera with the selected document type
            pendingDocumentType?.let { launchCamera(it) }
            pendingDocumentType = null
        } else {
            Toast.makeText(
                this,
                R.string.camera_permission_required,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private var pendingDocumentType: DocumentType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        documentManager = DocumentManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<MaterialButton>(R.id.btnDriverLicense).setOnClickListener {
            requestCameraAndLaunch(DocumentType.DRIVERS_LICENSE)
        }

        findViewById<MaterialButton>(R.id.btnIdentification).setOnClickListener {
            requestCameraAndLaunch(DocumentType.IDENTIFICATION)
        }

        findViewById<MaterialButton>(R.id.btnTaxForm).setOnClickListener {
            requestCameraAndLaunch(DocumentType.TAX_FORM)
        }

        findViewById<MaterialButton>(R.id.btnSupportingDoc).setOnClickListener {
            requestCameraAndLaunch(DocumentType.SUPPORTING_DOCUMENT)
        }

        findViewById<MaterialButton>(R.id.btnMyDocuments).setOnClickListener {
            showMyDocuments()
        }
    }

    private fun requestCameraAndLaunch(documentType: DocumentType) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera(documentType)
            }
            else -> {
                pendingDocumentType = documentType
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera(documentType: DocumentType) {
        val intent = Intent(this, CameraActivity::class.java).apply {
            putExtra(CameraActivity.EXTRA_DOCUMENT_TYPE, documentType.name)
        }
        startActivity(intent)
    }

    private fun showMyDocuments() {
        val documents = documentManager.getAllDocuments()
        if (documents.isEmpty()) {
            Toast.makeText(this, R.string.no_documents, Toast.LENGTH_SHORT).show()
        } else {
            // In a more complete implementation, this would show a list of documents
            Toast.makeText(
                this,
                "You have ${documents.size} document(s)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
