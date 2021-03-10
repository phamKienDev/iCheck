package vn.icheck.android.util.ui

import android.graphics.Color
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.image.DetailImagesActivity
import vn.icheck.android.util.DimensionUtil

class GlideUtil {
    companion object {
        // Load url with loading animation into image view
        fun loading(url: String?, imageView: ImageView) {
            val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
            circularProgressDrawable.strokeWidth = 8f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide.with(ICheckApplication.getInstance()).load(url)
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.ic_error_ic_image)
                    .apply(RequestOptions().transform(RoundedCorners(DimensionUtil.convertDpToPixel(10f, imageView.context).toInt()))).into(imageView)
        }
        // Load url  into image view
        fun bind(url: String?, imageView: ImageView, placeHolderRes: Int) {
            Glide.with(ICheckApplication.getInstance()).load(url).centerCrop()
                    .error(R.drawable.ic_error_ic_image)
                    .placeholder(placeHolderRes)
                    .apply(RequestOptions().transform(RoundedCorners(DimensionUtil.convertDpToPixel(10f, imageView.context).toInt()))).into(imageView)
        }

        fun loadAva(url: String?, imageView: ImageView) {
            Glide.with(ICheckApplication.getInstance()).load(url).centerCrop()
                    .error(R.drawable.ic_error_load_url)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .apply(RequestOptions().transform(RoundedCorners(DimensionUtil.convertDpToPixel(10f, imageView.context).toInt()))).into(imageView)
        }


        fun bind(resId: Int, imageView: ImageView) {
            Glide.with(ICheckApplication.getInstance()).load(resId).centerCrop()
                    .error(R.drawable.ic_error_ic_image)
                    .apply(RequestOptions().transform(RoundedCorners(DimensionUtil.convertDpToPixel(10f, imageView.context).toInt()))).into(imageView)
        }
    }
}