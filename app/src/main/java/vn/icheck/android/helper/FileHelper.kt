package vn.icheck.android.helper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import vn.icheck.android.ICheckApplication
import java.io.*
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileHelper {
    const val iCheckThemeName = "iCheckTheme.zip"
    const val imageFolder = "images/"

    const val homeBackgroundImage = imageFolder + "homeBackgroundImage.png"
    const val homeHeaderImage = imageFolder + "homeHeaderImage.png"
    const val homeIcon = imageFolder + "homeIcon.png"
    const val newsIcon = imageFolder + "newsIcon.png"
    const val historyIcon = imageFolder + "historyIcon.png"
    const val messageIcon = imageFolder + "messageIcon.png"
    const val scanIcon = imageFolder + "scanIcon.png"

    fun getImagePath(path: String, fileName: String?): String {
        return path + imageFolder + fileName
    }

    fun getPath(context: Context?): String {
        var path = context?.getExternalFilesDir(null)?.absolutePath

        if (path.isNullOrBlank()) {
            path = Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + context?.applicationContext?.packageName + "/files/"
        } else {
            path += "/"
        }

        return path
    }

    val isThemeDownloaded: Boolean
        get() {
            val path = getPath(ICheckApplication.getInstance())
            return File(path + homeBackgroundImage).exists() ||
                    File(path + homeHeaderImage).exists() ||
                    File(path + homeIcon).exists() ||
                    File(path + newsIcon).exists() ||
                    File(path + scanIcon).exists() ||
                    File(path + historyIcon).exists() ||
                    File(path + messageIcon).exists()
        }

    fun deleteTheme(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles() ?: arrayOf()) {
                deleteTheme(child)
            }
        }

        fileOrDirectory.delete()
    }

    fun copyFile(src: File, dst: File): Boolean {
        return try {
            if (!dst.exists()) {
                dst.parentFile?.let {
                    if (!it.exists()) {
                        it.mkdirs()
                    }
                }
                dst.createNewFile()
            }

            val inputStream: InputStream = FileInputStream(src)
            val outputStream: OutputStream = FileOutputStream(dst)
            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()

            true
        } catch (e: Exception) {
            false
        }
    }

    private fun saveImage(image: Bitmap, storageDir: File, imageFileName: String): Boolean {
        var successDirCreated = false
        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir()
        }

        return if (successDirCreated) {
            val imageFile = File(storageDir, imageFileName)
            val savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    fun checkFileExist(context: Context?, fileName: String?): Boolean {
        val path = getPath(context) + fileName
        val file = File(path)
        return file.exists()
    }

    fun downloadFile(linkDownload: String, path: String, fileName: String): Boolean {
        return try { //create url and connect
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val url = URL(linkDownload)
            val connection = url.openConnection()
            connection.connect()
            // this will be useful so that you can show a typical 0-100% progress bar
//            val fileLength: Int = connection.contentLength
            // download the file
            val input: InputStream = BufferedInputStream(connection.getInputStream())

            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }

            val output: OutputStream = FileOutputStream(path + fileName)
            val data = ByteArray(1024)
//            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
//                total += count.toLong()
                // publishing the progress....
//                val resultData = Bundle()
//                resultData.putInt("progress", (total * 100 / fileLength).toInt())
//                receiver.send(UPDATE_PROGRESS, resultData)
                output.write(data, 0, count)
            }
            // close streams
            output.flush()
            output.close()
            input.close()

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun unzipFile(path: String, fileName: String): Boolean {
        try {
            var filename: String
            val inputStream = FileInputStream(path + fileName)
            val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
            var ze: ZipEntry?
            val buffer = ByteArray(1024)
            var count: Int

            while (zipInputStream.nextEntry.also { ze = it } != null) {
                if (ze != null && ze?.name?.contains("_MACOS") == false) {
                    filename = ze!!.name
                    // Need to create directories if not exists, or
                    // it will generate an Exception...
                    if (ze!!.isDirectory) {
                        val fmd = File(path + filename)
                        fmd.mkdirs()
                        continue
                    }

                    val fileOutputStream = FileOutputStream(path + filename)
                    while (zipInputStream.read(buffer).also { count = it } != -1) {
                        fileOutputStream.write(buffer, 0, count)
                    }

                    fileOutputStream.close()
                    zipInputStream.closeEntry()
                }
            }

            inputStream.close()
            zipInputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    fun getTextFromFile(path: String, fileName: String): String {
        val stringBuilder = StringBuilder()

        val file = File(path + fileName)

        if (file.exists()) {
            val buffer = BufferedReader(FileReader(file))
            var line: String?

            while (buffer.readLine().also { line = it } != null) {
                stringBuilder.append(line?.trim())
            }

            buffer.close()
        }

        return stringBuilder.toString()
    }

    /**
     * Method for return file path of Gallery image/ Document / Video / Audio
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection,
                        selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     * The context.
     * @param uri
     * The Uri to query.
     * @param selection
     * (Optional) Filter used in the query.
     * @param selectionArgs
     * (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?,
                      selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection,
                    selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
                .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
                .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
                .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
                .authority
    }

    fun deleteFolder(folder: File) {
        val list = folder.listFiles() ?: arrayOf<File>()

        for (i in list.size - 1 downTo 0) {
            if (list[i].isDirectory) {
                deleteFolder(list[i])
            } else {
                list[i].delete()
            }
        }

        folder.delete()
    }
}