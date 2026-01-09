package com.abctax.mobile

import java.io.File
import java.util.Date

/**
 * Represents different types of documents that can be scanned
 */
enum class DocumentType(val displayName: String) {
    DRIVERS_LICENSE("Driver's License"),
    IDENTIFICATION("Identification Document"),
    TAX_FORM("Tax Form"),
    SUPPORTING_DOCUMENT("Supporting Document")
}

/**
 * Represents a scanned document with metadata
 */
data class ScannedDocument(
    val id: String,
    val type: DocumentType,
    val imageFile: File,
    val timestamp: Date,
    var signatureFile: File? = null,
    var isUploaded: Boolean = false
)
