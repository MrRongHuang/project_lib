package com.koi.projectlib.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.drake.tooltip.toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * content 类型的 Uri 转换成绝对路径
 */
object AppFileUtils {
    private const val FILE_TAG = "/rich_editor"
    private const val DOCUMENTS_DIR = "documents"

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (hasKitKat() && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        java.lang.Long.valueOf(id)
                    )
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null && path != "") {
                            return path
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                val fileName = getFileName(context, uri)
                val cacheDir: File = getDocumentCacheDir(context)
                val file: File? = generateFileNamePlus(fileName, cacheDir)
                var destinationPath: String? = null
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }
                return destinationPath
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val contentUri: Uri = when (split[0]) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }

                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    else -> {
                        //其它类型
                        MediaStore.Files.getContentUri("external")
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { // MediaStore (and general)
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            return uri.path
        }
        return null
    }

    private fun generateFileNamePlus(originFileName: String?, directory: File?): File? {
        var name = originFileName ?: return null
        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            return null
        }
        return file
    }


    private fun getFileName(context: Context, uri: Uri): String? {
        val mimeType: String? = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null) {
            val path = getPath(context, uri)
            filename = if (path == null) {
                getName(uri.toString())
            } else {
                val file = File(path)
                file.name
            }
        } else {
            val returnCursor: Cursor? = context.contentResolver.query(
                uri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }


    private fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }


    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`?.read(buf)
            if (`is` != null) {
                do {
                    bos.write(buf)
                } while (`is`.read(buf) != -1)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun hasICS(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
    }

    private fun hasKitKat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    private const val IN_PATH = "/rich_editor/pic/"

    /**
     * 随机生产文件名
     *
     * @return
     */
    private fun generateFileName(): String {
        return "poster" + System.currentTimeMillis()
    }

    fun clearLocalRichEditorCache() {
        val file = File(Environment.getExternalStorageDirectory().path + FILE_TAG)
        deleteDirectory(file)
    }

    fun saveBitmap(bmp: Bitmap): String? {
        val parent = Environment.getExternalStorageDirectory().path + FILE_TAG
        val parentF = File(parent)
        val f = File(parent, generateFileName() + ".png")
        if (!parentF.exists()) {
            parentF.mkdirs()
        }
        if (f.exists()) {
            f.delete()
        }
        try {
            val out = FileOutputStream(f)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return f.absolutePath
    }

    /**
     * 获取视频缩略图
     *
     * @param filePath
     * @return
     */
    fun getVideoThumbnail(filePath: String?): Bitmap? {
        var frameAtTime: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            frameAtTime = retriever.frameAtTime
            return frameAtTime
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
        return frameAtTime
    }


    private fun deleteDirectory(file: File?) {
        if (file == null) {
            return
        }
        if (file.isFile) {
            file.delete()
        } else {
            val childFilePaths: Array<out String>? = file.list()
            if (childFilePaths == null || childFilePaths.isEmpty()) {
                return
            }
            for (childFilePath in childFilePaths) {
                val childFile = File(file.absolutePath + "/" + childFilePath)
                deleteDirectory(childFile)
            }
            file.delete()
        }
    }

    /**
     * @param context 上下文
     * */
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(context: Context): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    /**
     * @param context 上下文
     * */
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createMoviesFile(context: Context): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "MP4_" + timeStamp + "_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".mp4",  /* suffix */
            storageDir /* directory */
        )
    }

    /**
     * @param imageFile 需要进行压缩的图片文件
     * */
    fun compressImage(imageFile: File): File? {
        Log.e("koi", "le = ${FileUtils.getLength(imageFile)}")
        if (FileUtils.getLength(imageFile) <= 0) {
            FileUtils.delete(imageFile)
            return null
        }
        val compressedImageFile = File.createTempFile("compressed_image", ".jpg")
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream) // 50 是压缩质量，可以根据需求调整
        val byteArray = stream.toByteArray()

        val fos = FileOutputStream(compressedImageFile)
        fos.write(byteArray)
        fos.flush()
        fos.close()
        FileUtils.delete(imageFile)
        return compressedImageFile
    }

    fun getFileSize(filePath: String): Long {
        val file = File(filePath)
        return if (file.exists() && file.isFile) {
            file.length() // 返回文件大小，单位为字节
        } else {
            0 // 如果文件不存在，返回 0
        }
    }

    fun getFileSize(file: File): Long {
        return if (file.exists() && file.isFile) {
            file.length() // 返回文件大小，单位为字节
        } else {
            0 // 如果文件不存在，返回 0
        }
    }

    /**
     * 获取本地视频 第N帧图片生成本地jpg
     * */
    fun extractAndSaveVideoFrame(frameAtTime: Long,videoPath: String): String? {
        var filePath: String? = null

        // 1. 使用 MediaMetadataRetriever 提取第一帧
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
            val bitmap: Bitmap? = retriever.getFrameAtTime(frameAtTime) // 获取第一帧 (时间为 0 微秒)

            // 2. 检查是否成功提取 Bitmap
            if (bitmap != null) {
                // 3. 定义 JPG 文件的保存路径
                val now: Calendar = GregorianCalendar()
                val simpleDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                val pictureName = simpleDate.format(now.time) //获取当前时间戳作为文件名称，避免同名

                val storageDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(storageDir, "$pictureName.jpg")

                // 4. 保存 Bitmap 到本地 JPG 文件
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()

                filePath = file.absolutePath
                Log.d("koii", "takeRec: $filePath")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release() // 释放资源
        }

        return filePath
    }
}