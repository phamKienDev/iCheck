package vn.icheck.android.ichecklibs.util

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import vn.icheck.android.ichecklibs.R
import java.io.File

object LoadImageUtils {
    private val defaultError = R.drawable.ic_default_square

    private fun circularProgressDrawableBlue(context: Context): CircularProgressDrawable {
        val circularProgress = CircularProgressDrawable(context)
        circularProgress.strokeWidth = 5f
        circularProgress.centerRadius = 30f
        circularProgress.setColorSchemeColors(R.color.colorPrimary)
        circularProgress.start()
        return circularProgress
    }


    fun loadImageFileNotRounded(image: AppCompatImageView, file: File?) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(defaultError)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawableBlue(image.context))
                .error(defaultError)
                .transform(CenterCrop())
                .into(image)
    }
}