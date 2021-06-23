package vn.icheck.android.loyalty.helper

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.annotation.AnyRes
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.loyalty.R

object ImageHelper {

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

    private fun getImageName(url: String): String {
        val split = url.split("/")
        return split[split.size - 1] + ".jgp"
    }
}