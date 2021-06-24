package vn.icheck.android.helper

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import androidx.annotation.AnyRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.BuildConfig
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.upload.UploadResponse
import java.io.ByteArrayOutputStream
import java.io.File


object ImageHelperV1 : BaseInteractor() {

    val thumbSmallSize: String
        get() {
            return getString(R.string.thumb_small_size)
        }

    val thumbMediumSize: String
        get() {
            return getString(R.string.thumb_medium_size)
        }

    val thumbLargeSize: String
        get() {
            return getString(R.string.thumb_large_size)
        }

    val smallSize: String
        get() {
            return getString(R.string.small_size)
        }

    val mediumSize: String
        get() {
            return getString(R.string.medium_size)
        }

    val largeSize: String
        get() {
            return getString(R.string.large_size)
        }

    val originalSize: String
        get() {
            return getString(R.string.original_size)
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

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    fun getUriToDrawable(context: Context, @AnyRes drawableId: Int): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.resources.getResourcePackageName(drawableId)
                + '/'.toString() + context.resources.getResourceTypeName(drawableId)
                + '/'.toString() + context.resources.getResourceEntryName(drawableId))
    }

    fun uploadImage(context: Context, file: File, listener: ICApiListener<UploadResponse>) {
        Glide.with(context.applicationContext)
                .asBitmap()
                .load(file)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        val error = ICBaseResponse()
                        error.statusCode = -1
                        error.message = context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                        listener.onError(error)
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        startUploadImage(context, resource, listener)
                    }
                })
    }

    fun uploadImage(context: Context, uri: Uri, listener: ICApiListener<UploadResponse>) {
//        MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)

        Glide.with(context.applicationContext)
                .asBitmap()
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        /*
                        this is called when imageView is cleared on lifecycle call or for some other reason.
                        if you are referencing the bitmap somewhere else too other than this imageView
                        clear it here as you can no longer have the bitmap
                        */
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        val error = ICBaseResponse()
                        error.statusCode = -1
                        error.message = context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                        listener.onError(error)
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        startUploadImage(context, resource, listener)
                    }
                })
    }

    private fun startUploadImage(context: Context, bitmap: Bitmap, listener: ICApiListener<UploadResponse>) {
        val scale = if (bitmap.width > bitmap.height) {
            bitmap.width / 1024
        } else {
            bitmap.height / 1024
        }

        var dstWidth = bitmap.width
        var dstHeight = bitmap.height

        if (scale >= 1) {
            dstWidth /= scale
            dstHeight /= scale
        }

        val bos = ByteArrayOutputStream()
        Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true).compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val body = RequestBody.create("image/jpeg".toMediaTypeOrNull(), bos.toByteArray())

//        requestApi(ICNetworkClient.getNewUploadClient().uploadImage(body), listener)

        val host = APIConstants.uploadFileHostV1() + APIConstants.UPLOADIMAGEV1()
        ICNetworkClient.getNewUploadClientV1().uploadImageV1(host, body).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                val obj = response.body()

                if (obj != null) {
                    listener.onSuccess(obj)
                } else {
                    val error = ICBaseResponse()
                    error.statusCode = -1
                    error.message = context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    listener.onError(error)
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                val error = ICBaseResponse()
                error.statusCode = -1
                error.message = context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                listener.onError(error)
            }
        })
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