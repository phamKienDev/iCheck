package vn.icheck.android.util.kotlin

//import vn.teko.android.vnshop.di.module.GlideApp

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.callback.LoadImageListener
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.util.visibleOrGone
import vn.icheck.android.ichecklibs.util.visibleOrInvisible
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.ui.edittext.FocusableEditText
import vn.icheck.android.util.ick.logError
import java.io.File
import java.io.FileInputStream

object WidgetUtils {
    const val FRAME_FRAGMENT_ID = R.id.frameFragment

    const val defaultHolder = R.drawable.ic_default_square
    const val defaultError = R.drawable.ic_default_square

//    val circularProgressDrawable = getCircularProgressDrawable

    private val circularProgressDrawable: CircularProgressDrawable
        get() {
            val circularProgress = CircularProgressDrawable(ICheckApplication.getInstance().applicationContext)
            circularProgress.strokeWidth = 5f
            circularProgress.centerRadius = 30f
            circularProgress.start()
            return circularProgress
        }

    private val circularProgressDrawableBlue: CircularProgressDrawable
        get() {
            val circularProgress = CircularProgressDrawable(ICheckApplication.getInstance().applicationContext)
            circularProgress.strokeWidth = 5f
            circularProgress.centerRadius = 30f
            circularProgress.setColorSchemeColors(R.color.colorPrimary)
            circularProgress.start()
            return circularProgress
        }

    fun setClickListener(clickListener: View.OnClickListener, vararg views: View) {
        for (view in views) {
            view.setOnClickListener(clickListener)
        }
    }

    fun showOrHidePassword(editText: AppCompatEditText, imgShow: AppCompatImageButton) {
        editText.transformationMethod = if (editText.transformationMethod == null) {
            imgShow.setImageResource(R.drawable.ic_login_eye_gray_18)
            PasswordTransformationMethod()
        } else {
            imgShow.setImageResource(R.drawable.ic_login_eye_blue_18)
            null
        }

        editText.setSelection(editText.length())
    }

    fun showOrHideXu(editText: AppCompatTextView, textHide: AppCompatTextView, imgShow: AppCompatImageButton) {
        if (editText.visibility == View.GONE) {
            imgShow.setImageResource(R.drawable.ic_eye_on_24px)
            textHide.visibility = View.VISIBLE
            editText.visibility = View.GONE
        } else {
            imgShow.setImageResource(R.drawable.ic_eye_off_gray_24dp)
            textHide.visibility = View.GONE
            editText.visibility = View.VISIBLE
        }
//        editText.transformationMethod = if (editText.transformationMethod == null) {
//            imgShow.setImageResource(R.drawable.ic_eye_on_24px)
//            textHide.visibility = View.VISIBLE
//            editText.visibility = View.GONE
//            PasswordTransformationMethod()
//        } else {
//            imgShow.setImageResource(R.drawable.ic_eye_off_24px)
//            textHide.visibility = View.GONE
//            editText.visibility = View.VISIBLE
//            null
//        }
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

    /**
     * Scroll ẩn dayView
     * @param view viewIn muốn ẩn
     * @param height quãng đường muốn scroll. 0 là về vị trí ban đầu
     */
    fun slideView(view: View, height: Int) {
        view.clearAnimation()
        view.animate()
                .translationY(height.toFloat())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        view.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationStart(p0: Animator?) {

                    }

                })
                .duration = 200
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

    fun loadImageUrl(image: CircleImageView, url: String?, error: Int?) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error ?: defaultError)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error ?: defaultError)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrl(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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

    fun loadImageUrlNotCrop(image: AppCompatImageView, url: String?, error: Int) {
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
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageView, url: String?, error: Int) {
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

    fun loadImageUrlNotTransform(image: AppCompatImageView, url: String?, error: Int? = null) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error ?: defaultError)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error ?: defaultError)
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrlFitCenter(image: AppCompatImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(defaultError)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(defaultError)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageUrlFitCenter(image: AppCompatImageView, url: String?, error: Int?) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error ?: defaultError)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error ?: defaultError)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageUrlFitCenter(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageUrlFitCenter(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageUrlFitCenter(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageUrl(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFitCenterUrl(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: CircleImageView, file: File?, placeHolder: Int, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: AppCompatImageView, file: File?, error: Int, listener: LoadImageListener) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            listener.onFailed()
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        listener.onFailed()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        listener.onSuccess()
                        return false
                    }
                })
                .into(image)
    }

    fun loadImageFileFitCenter(image: AppCompatImageView, file: File?) {
        loadImageFileFitCenter(image, file, defaultError)
    }

    fun loadImageFileFitCenter(image: AppCompatImageView, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }

    fun loadImageFile(image: AppCompatImageButton, file: File?) {
        loadImageFile(image, file, defaultError)
    }

    fun loadImageFile(image: AppCompatImageButton, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageFile(image: AppCompatImageView, file: File?, error: Int, roundCorners: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }


    fun loadImageFileFitceter(image: AppCompatImageButton, file: File?) {
        loadImageFileFitceter(image, file, defaultError)
    }

    fun loadImageFileFitceter(image: AppCompatImageButton, file: File?, error: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter())
                .into(image)
    }


    fun loadImageFileFitceter(image: AppCompatImageView, file: File?, error: Int, roundCorners: Int) {
        if (file == null || !file.exists()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop())
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(file)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun AppCompatImageView.loadImageFromVideoFile(file: File?, time: Long? = null, corners: Int? = null, error: Int? = null) {
        if (file != null) {
            if (file.absolutePath.contains(".mp4")) {
                try {
                    val inputStream = FileInputStream(file.absolutePath)
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(inputStream.fd)
                    val bitmap = mediaMetadataRetriever.getFrameAtTime(time ?: 0)
                    this.setImageBitmap(bitmap)

                } catch (e: Exception) {
                    loadImageFileRounded(this, file, corners ?: SizeHelper.size4)
                    logError(e)
                }
            } else {
                loadImageFileRounded(this, file, corners ?: SizeHelper.size4)
            }
        } else {
            this.setImageResource(error ?: R.drawable.ic_default_square)
        }
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
                .placeholder(circularProgressDrawable)
                .error(defaultError)
                .transform(CenterCrop())
                .into(image)
    }

    fun loadImageUrlRounded(image: AppCompatImageView, url: String?, roundCorners: Int) {
        loadImageUrlRounded(image, url, defaultError, roundCorners)
    }

    fun loadImageUrlRounded(image: AppCompatImageView, url: String?, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }


        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRounded(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }
        val time1 = System.currentTimeMillis()
        Glide.with(image.context.applicationContext)
                .asBitmap()
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .format(DecodeFormat.PREFER_RGB_565)
                .into(image)
    }

    fun loadImageUrlRoundedTransformation(image: AppCompatImageView, url: String?, roundCorners: Int, cornerType: RoundedCornersTransformation.CornerType) {
        loadImageUrlRoundedTransformation(image, url, defaultError, roundCorners, cornerType)
    }

    fun loadImageUrlRoundedTransformation(image: AppCompatImageView, url: String?, error: Int, roundCorners: Int, cornerType: RoundedCornersTransformation.CornerType) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                .into(image)
    }

    fun loadImageUrlRoundedTransformation(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, roundCorners: Int, cornerType: RoundedCornersTransformation.CornerType) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                .into(image)
    }

    fun loadImageUrlRoundedTransformationCenterCrop(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, roundCorners: Int, cornerType: RoundedCornersTransformation.CornerType) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                .into(image)
    }

    fun loadImageUrlRoundedCenterCrop(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, roundCorners: Int, cornerType: RoundedCornersTransformation.CornerType) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCornersTransformation(image.context, roundCorners, 0, cornerType))
                .into(image)
    }

    fun loadImageUrlRoundedCenterCrop(image: AppCompatImageView, url: String?, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(FitCenter(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRoundedNotTransform(image: AppCompatImageView, url: String?, drawble: Int?, error: Int?, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error ?: defaultError)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(drawble ?: defaultHolder)
                .error(error ?: defaultError)
                .transform(RoundedCorners(roundCorners))
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRounded(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int, roundCorners: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(roundCorners))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
    }

    fun loadImageUrlRoundedListener(image: AppCompatImageView, url: String?, roundCorners: Int, listener: LoadImageListener) {
        loadImageUrlRoundedListener(image, url, circularProgressDrawable, defaultError, roundCorners, listener)
    }

    fun loadImageUrlRoundedListener(image: AppCompatImageView, url: String?, placeHolder: Drawable, error: Int, roundCorners: Int, listener: LoadImageListener) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(RoundedCorners(roundCorners), CenterCrop())
                    .into(image)
            listener.onFailed()
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(RoundedCorners(roundCorners), CenterCrop())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        listener.onFailed()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        listener.onSuccess()
                        return false
                    }
                })
                .into(image)
    }

    fun loadImageUrlRoundedListener(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int, listener: LoadImageListener) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            listener.onFailed()
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        listener.onFailed()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        listener.onSuccess()
                        return false
                    }
                })
                .into(image)
    }

    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?) {
        loadImageUrlRounded4(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?) {
        loadImageUrlRounded10FitCenter(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?, listener: RequestListener<Bitmap>) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(defaultError)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(ICheckApplication.getInstance())
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(defaultError)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size10))
                .into(image)

//        Glide.with(image.context.applicationContext)
//                .load(url)
//                .
//                .placeholder(circularProgressDrawable)
//                .error(error)
//                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
//                .listener(object : RequestListener<Bitmap> {
//                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                .into(image)
    }


    fun loadImageUrlRounded50(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size50))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size50))
                .into(image)
    }

    fun loadImageUrlRounded10FitCenter(image: CircleImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                .into(image)
    }


    fun loadImageUrlRounded4(image: CircleImageView, url: String?) {
        loadImageUrlRounded4(image, url, defaultError)
    }

    fun loadImageUrlRounded4(image: CircleImageView, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }


    fun loadImageUrlRounded4(image: AppCompatImageView, url: String?, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawableBlue)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }


    fun loadImageUrlRounded4(image: AppCompatImageButton, url: String?) {
        loadImageUrlRounded4(image, url, circularProgressDrawable, defaultError)
    }

    fun loadImageUrlRounded4(image: AppCompatImageButton, url: String?, placeHolder: Drawable, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                .into(image)
    }

    fun loadImageUrlRounded4(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size4))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .into(image)
    }

    fun loadImageUrlRounded6(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(error)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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
            Glide.with(image.context.applicationContext)
                    .load(url)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawable)
                .error(error)
                .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                .into(image)
    }

    fun loadImageUrlRounded6(image: AppCompatImageButton, url: String?, placeHolder: Int, error: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(url)
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size6))
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
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

    fun loadGiftUrl(imageView: AppCompatImageView, url: String) {
        Glide.with(imageView.context.applicationContext).load(url).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(imageView)
    }

    fun loadImageUrlNotRounded(image: AppCompatImageView, url: String?, placeHolder: Int, error: Int) {
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

    fun loadImageUrlNotRounded(image: AppCompatImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            Glide.with(image.context.applicationContext)
                    .load(defaultError)
                    .into(image)
            return
        }

        Glide.with(image.context.applicationContext)
                .load(url)
                .placeholder(circularProgressDrawableBlue)
                .error(defaultError)
                .into(image)
    }

    fun changePasswordInput(editText: FocusableEditText) {
        editText.apply {
            if (isFocused) {
                val mTransformationMethod = transformationMethod

                inputType = if (inputType != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    InputType.TYPE_CLASS_NUMBER
                }

                transformationMethod = mTransformationMethod

                setSelection(length())
            }
        }
    }

    fun setButtonKeyboardMargin(imgKeyboard: AppCompatImageView, edtPassword: FocusableEditText) {
        imgKeyboard.apply {
            visibleOrInvisible(edtPassword.isFocused)
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                setMargins(marginLeft, marginTop, marginEnd, edtPassword.getBottomPadding())
            }
        }
    }
}