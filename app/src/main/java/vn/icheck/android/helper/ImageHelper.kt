package vn.icheck.android.helper

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.upload.UploadResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

object ImageHelper : BaseInteractor() {

    val thumbSmallSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.thumb_small_size)
        }

    val thumbMediumSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.thumb_medium_size)
        }

    val thumbLargeSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.thumb_large_size)
        }

    val smallSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.small_size)
        }

    val mediumSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.medium_size)
        }

    val largeSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.large_size)
        }

    val originalSize: String
        get() {
            return ICheckApplication.getInstance().getString(R.string.original_size)
        }

    /**
     * @param id: id của ảnh
     * @param url: url của ảnh trong thumnails
     * @param size: kích thước của ảnh (tham khảo size ở trên)
     * @return
     */
    fun getImageUrl(id: String?, url: String?, size: String): String? {
        return url ?: if (id?.startsWith("-TheHulk") == true) {
            "http://icheckcdn.net/images/${size}/${id}.jpg"
        } else {
            "https://upload.icheck.vn/hi/${id}"
        }
    }

    fun getImageUrl(id: String?, size: String): String? {
        return if (id?.startsWith("-TheHulk") == true) {
            "http://icheckcdn.net/images/${size}/${id}.jpg"
        } else {
            "https://upload.icheck.vn/hi/${id}"
        }
    }

    fun getNation(nation: String): String {
        return "http://ucontent.icheck.vn/ensign/" + nation.toUpperCase(Locale.getDefault()) + ".png"
    }

    fun uploadMedia(file: File, listener: ICApiListener<UploadResponse>) {

        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        var fileName = file.toString()
        if (!fileName.endsWith(".mp4") && !fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".gif")) {
            fileName += ".jpg"
        }
        val body = MultipartBody.Part.createFormData("key", fileName, requestBody)

        ICNetworkClient.getNewUploadClient().uploadImage(body).enqueue(object : Callback<ICResponse<UploadResponse>> {
            override fun onResponse(call: Call<ICResponse<UploadResponse>>, response: Response<ICResponse<UploadResponse>>) {
                val obj = response.body()

                if (obj != null) {
                    listener.onSuccess(obj.data!!)
                } else {
                    val error = ICBaseResponse()
                    error.statusCode = -1
                    error.message = ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    listener.onError(error)
                }
            }

            override fun onFailure(call: Call<ICResponse<UploadResponse>>, t: Throwable) {
                val error = ICBaseResponse()
                error.statusCode = -1
                error.message = ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                listener.onError(error)
            }
        })
    }

    suspend fun uploadMediaV2(file: File): ICResponse<UploadResponse> {
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        var fileName = file.toString()
        if (!fileName.endsWith(".mp4") && !fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".gif")) {
            fileName += ".jpg"
        }
        val body = MultipartBody.Part.createFormData("key", fileName, requestBody)
        return ICNetworkClient.getNewUploadClient().uploadImageV2(body)
    }


    fun downloadFileByDownloadManager(context: Context, url: String): Long {
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (folder.exists()) {
            folder.mkdirs()
        }

        val fileName = getImageName(url)

        val file = File(folder, fileName)

        if (file.exists()) {
            return -1
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url))

                // Title of the Download Notification
                .setTitle(context.getString(R.string.app_name))

                // Description of the Download Notification
                .setDescription(context.getString(R.string.dang_tai_anh))

                // Visibility of the download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

                // Set the local destination for the downloaded file to a path
                // within the application's external files directory
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

        // Enqueue download and save into referenceId
        return downloadManager.enqueue(request)
    }

    private fun getImageName(url: String): String {
        val split = url.split("/")
        return split[split.size - 1] + ".jgp"
    }
}