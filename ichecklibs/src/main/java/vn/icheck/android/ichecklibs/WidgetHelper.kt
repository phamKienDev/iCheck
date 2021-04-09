package vn.icheck.android.ichecklibs

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ichecklibs.helper.ApplicationHelper
import java.io.File

object WidgetHelper {
    val FRAME_FRAGMENT_ID = R.id.frameFragment

    val defaultHolder = R.drawable.ic_default_square
    val defaultError = R.drawable.ic_default_square

//    val circularProgressDrawable = getCircularProgressDrawable

    private val circularProgressDrawable: CircularProgressDrawable
        get() {
            val circularProgress = CircularProgressDrawable(ApplicationHelper.getApplicationByReflect())
            circularProgress.strokeWidth = 5f
            circularProgress.centerRadius = 30f
            circularProgress.start()
            return circularProgress
        }

    fun setClickListener(clickListener: View.OnClickListener, vararg views: View) {
        for (view in views) {
            view.setOnClickListener(clickListener)
        }
    }

    fun changeViewHeight(view: View, fromHeight: Int, toHeight: Int, duration: Long, animatorListener: Animator.AnimatorListener? = null) {
        changeValueAnimation(fromHeight, toHeight, duration, ValueAnimator.AnimatorUpdateListener {
            val layoutParams = view.layoutParams
            layoutParams.height = it?.animatedValue as Int
            view.layoutParams = layoutParams
        }, animatorListener)
    }

    fun changeValueAnimation(start: Int, end: Int, duration: Long, listener: ValueAnimator.AnimatorUpdateListener, animatorListener: Animator.AnimatorListener? = null) {
        val valueAnimator = ValueAnimator.ofInt(start, end).setDuration(duration)
        valueAnimator.addUpdateListener(listener)

        val set = AnimatorSet()
        set.play(valueAnimator)

        animatorListener?.let {
            set.addListener(it)
        }

        set.start()
    }

    fun loadImageUrl(image: CircleImageView, url: String?) {
        loadImageUrl(image, url, defaultError)
    }

    fun loadImageUrl(image: CircleImageView, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrl(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageView, url: String?) {
        loadImageUrl(image, url, defaultError)
    }

    fun loadImageUrl(image: AppCompatImageButton, url: String?) {
        loadImageUrl(image, url, defaultError)
    }

    fun loadImageUrl(image: AppCompatImageButton, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageView, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageView, url: String?) {
        loadImageFitCenterUrl(image, url, defaultError)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageView, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageButton, url: String?) {
        loadImageFitCenterUrl(image, url, defaultError)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageButton, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFile(image: CircleImageView, file: File?) {
        loadImageFile(image, file, defaultError)
    }

    fun loadImageFile(image: CircleImageView, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: CircleImageView, file: File?, placeHolder: Int, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: AppCompatImageView, file: File?) {
        loadImageFile(image, file, defaultError)
    }

    fun loadImageFile(image: AppCompatImageView, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: AppCompatImageButton, file: File?) {
        loadImageFile(image, file, defaultError)
    }

    fun loadImageFile(image: AppCompatImageButton, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: AppCompatImageView, file: File?, error: Int, roundCorners: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }


    fun loadImageFileFitceter(image: AppCompatImageButton, file: File?) {
        loadImageFileFitceter(image, file, defaultError)
    }

    fun loadImageFileFitceter(image: AppCompatImageButton, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFileFitceter(image: AppCompatImageView, file: File?, error: Int, roundCorners: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }


    fun loadImageFileRounded(image: AppCompatImageView, file: File?, roundCorners: Int) {
        loadImageFileRounded(image, file, defaultError, roundCorners)
    }

    fun loadImageFileRounded(image: AppCompatImageView, file: File?, error: Int, roundCorners: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }


    fun loadImageUrlRounded(image: AppCompatImageView, url: String?, roundCorners: Int) {
        loadImageUrlRounded(image, url, defaultError, roundCorners)
    }

    fun loadImageUrlRounded(image: AppCompatImageView, url: String?, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRounded(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }


    fun loadImageUrlRoundedFitCenter(image: AppCompatImageView, url: String?, roundCorners: Int) {
        loadImageUrlRoundedFitCenter(image, url, defaultError, roundCorners)
    }

    fun loadImageUrlRoundedFitCenter(image: AppCompatImageView, url: String?, error: Int, roundCorners: Int) {
        loadImageUrlRoundedFitCenter(image, url, defaultHolder, error, roundCorners)
    }

    fun loadImageUrlRoundedFitCenter(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRoundedFitCenter(image: AppCompatImageButton, url: String?, roundCorners: Int) {
        loadImageUrlRoundedFitCenter(image, url, defaultError, roundCorners)
    }

    fun loadImageUrlRoundedFitCenter(image: AppCompatImageButton, url: String?, error: Int, roundCorners: Int) {
        loadImageUrlRoundedFitCenter(image, url, defaultHolder, error, roundCorners)
    }

    fun loadImageUrlRoundedFitCenter(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(roundCorners))
                .into(image)
    }


    fun loadImageUrlRounded(image: AppCompatImageButton, url: String?, roundCorners: Int) {
        loadImageUrlRounded(image, url, defaultError, roundCorners)
    }

    fun loadImageUrlRounded(image: AppCompatImageButton, url: String?, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRounded(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?) {
        loadImageUrlRounded4(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?) {
        loadImageUrlRounded10FitCenter(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                .into(image)
    }


    fun loadImageUrlRounded50(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size50))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size50))
                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                .into(image)
    }


    fun loadImageUrlRounded4(image: CircleImageView, url: String?) {
        loadImageUrlRounded4(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded4(image: CircleImageView, url: String?, error: Int) {
        loadImageUrlRounded4(image, url, circularProgressDrawable, error)
    }


    fun loadImageUrlRounded4(image: CircleImageView, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded4(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }


    fun loadImageUrlRounded4(image: AppCompatImageButton, url: String?) {
        loadImageUrlRounded4(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded4(image: AppCompatImageButton, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded4(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded6(image: AppCompatImageView, url: String?) {
        loadImageUrlRounded6(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded6(image: AppCompatImageView, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .into(image)
    }

    fun loadImageUrlRounded6(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .into(image)
    }

    fun loadImageUrlRounded6(image: AppCompatImageButton, url: String?) {
        loadImageUrlRounded6(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded6(image: AppCompatImageButton, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(url)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .into(image)
    }

    fun loadImageUrlRounded6(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(url)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .into(image)
    }

    fun loadGif(imageView: AppCompatImageView, resource: Int) {
        Glide.with(imageView.context)
                .asGif()
                .load(resource)
                .fitCenter()
                .into(imageView)
    }


    fun loadImageUrlNotRounded(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }
}