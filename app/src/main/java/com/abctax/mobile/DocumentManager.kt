package com.abctax.mobile

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manager class for handling document storage and retrieval
 */
class DocumentManager(private val context: Context) {
    
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    
    companion object {
        private const val TAG = "DocumentManager"
        private const val DOCUMENTS_DIR = "Documents"
        private const val SIGNATURES_DIR = "Signatures"
    }
    
    /**
     * Creates a new file for storing a scanned document
     */
    fun createImageFile(documentType: DocumentType): File {
        val timestamp = dateFormat.format(Date())
        val fileName = "${documentType.name}_$timestamp.jpg"
        val storageDir = getDocumentsDirectory()
        return File(storageDir, fileName)
    }
    
    /**
     * Creates a new file for storing a signature
     */
    fun createSignatureFile(documentId: String): File {
        val timestamp = dateFormat.format(Date())
        val fileName = "SIG_${documentId}_$timestamp.png"
        val storageDir = getSignaturesDirectory()
        return File(storageDir, fileName)
    }
    
    /**
     * Gets the documents directory, creating it if necessary
     */
    private fun getDocumentsDirectory(): File {
        val dir = File(context.getExternalFilesDir(null), DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Gets the signatures directory, creating it if necessary
     */
    private fun getSignaturesDirectory(): File {
        val dir = File(context.getExternalFilesDir(null), SIGNATURES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Lists all scanned documents
     */
    fun getAllDocuments(): List<ScannedDocument> {
        val documents = mutableListOf<ScannedDocument>()
        val docsDir = getDocumentsDirectory()
        
        docsDir.listFiles()?.forEach { file ->
            if (file.extension == "jpg") {
                try {
                    val parts = file.nameWithoutExtension.split("_")
                    if (parts.size >= 2) {
                        val typeName = parts[0]
                        val type = DocumentType.valueOf(typeName)
                        val doc = ScannedDocument(
                            id = file.nameWithoutExtension,
                            type = type,
                            imageFile = file,
                            timestamp = Date(file.lastModified())
                        )
                        documents.add(doc)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document: ${file.name}", e)
                }
            }
        }
        
        return documents.sortedByDescending { it.timestamp }
    }
    
    /**
     * Deletes a document and its associated signature
     */
    fun deleteDocument(document: ScannedDocument): Boolean {
        var success = document.imageFile.delete()
        document.signatureFile?.let {
            success = success && it.delete()
        }
        return success
    }
}
