package com.example.agribridge.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.agribridge.utils.Constant.DateFormat.YYYYMMDD_HHMMSS
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String? = if (uri.path?.contains("file://") == true) {
    uri.path
    
} else {
    getFileFromContentUri(context, uri, uniqueName).path
}

fun getFileFromContentUri(context: Context, contentUri: Uri, uniqueName: Boolean): File {
    val fileExtension = getFileExtension(context, contentUri) ?: ""
    val timeStamp = SimpleDateFormat(YYYYMMDD_HHMMSS, Locale.getDefault()).format(Date())
    val fileName = ("temp_" + if (uniqueName) (timeStamp + "_" + ((Math.random() * 9000) + 1000).toInt()) else "") + ".$fileExtension"
    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()
    var oStream: FileOutputStream? = null
    var inputStream: InputStream? = null
    
    try {
        oStream = FileOutputStream(tempFile)
        inputStream = context.contentResolver.openInputStream(contentUri)
        inputStream?.let { copy(inputStream, oStream) }
        oStream.flush()
        
    } catch (e: Exception) {
        e.message
        
    } finally {
        inputStream?.close()
        oStream?.close()
    }
    
    return tempFile
}

fun getFileExtension(context: Context, uri: Uri): String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
    MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
    
} else {
    uri.path?.let { path ->
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(path)).toString())
    }
}

fun copy(source: InputStream, target: OutputStream) {
    val bytes = ByteArray(8192)
    var length: Int
    
    while (source.read(bytes).also { buffer ->
            length = buffer
        } > 0) {
        target.write(bytes, 0, length)
    }
}

fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
    return fileUri?.let { uri ->
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
    }
}

fun getImageSize(file: File): Long {
    return file.length().div(1024).div(1000)
}

fun getBitmapFromFile(file: File?): Bitmap? {
    return file?.let { mFile -> ImageDecoder.createSource(mFile) }?.let { source -> ImageDecoder.decodeBitmap(source) }
}