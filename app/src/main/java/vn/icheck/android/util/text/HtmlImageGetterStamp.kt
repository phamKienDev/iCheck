package vn.icheck.android.util.text

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.*
import vn.icheck.android.ICheckApplication

class HtmlImageGetterStamp() : Html.ImageGetter {
    var size = 0
    lateinit var drawable: Drawable
    override fun getDrawable(source: String): Drawable? {

        runBlocking(Dispatchers.Default) {
            val getImageJob = async(Dispatchers.IO) {
                getImage(source)
            }
            drawable = getImageJob.await()
        }
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return drawable
    }

    suspend fun getImage(url: String): Drawable {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                if (size == 0) {
                    Glide.with(ICheckApplication.getInstance())
                            .asDrawable()
                            .load(url)
                            .error(vn.icheck.android.R.drawable.error_load_image)
                            .submit(600, 600)
                            .get()
                } else {
                    Glide.with(ICheckApplication.getInstance())
                            .asDrawable()
                            .load(url)
                            .error(vn.icheck.android.R.drawable.error_load_image)
                            .submit((size / Resources.getSystem().displayMetrics.density).toInt(), (size / Resources.getSystem().displayMetrics.density).toInt())
                            .get()
                }

            } catch (e: java.lang.Exception) {
                Glide.with(ICheckApplication.getInstance())
                        .asDrawable()
                        .load(vn.icheck.android.R.drawable.error_load_image)
                        .error(vn.icheck.android.R.drawable.error_load_image)
                        .submit(600, 600)
                        .get()
            }
        }
    }
}