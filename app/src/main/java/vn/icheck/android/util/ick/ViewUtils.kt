package vn.icheck.android.util.ick

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.media.ThumbnailUtils
import android.text.Editable
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.AfterTextWatcher
import java.io.File

fun View?.beGone() {
    this?.visibility = View.GONE
}


fun View.setAllEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) this.children.forEach { child -> child.isEnabled = enabled }
}

fun View.beVisible() {
    this.visibility = View.VISIBLE
}

fun View.beInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleOrInvisible(logic: Boolean) {
    if (logic) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun View.visibleOrGone(logic: Boolean) {
    if (logic) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

infix fun View.visibleIf(logic: Boolean?) {
    if (logic != null) {
        if (logic) this.visibility = View.VISIBLE
        else this.visibility = View.INVISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

infix fun View.goneIf(logic: Boolean?) {
    if (logic != null) {
        if (logic) this.visibility = View.VISIBLE
        else this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

infix fun TextView.priceText(price: Long?) {
    this.text = String.format("%,dđ", price)
}

infix fun TextView.simpleText(text: String?) {
    this.text = text
}

infix fun ImageView.loadSimpleImage(src: String?) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    Glide.with(this.context.applicationContext)
            .load(src)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_error_ic_image)
            .into(this)
}

infix fun ImageView.loadSimpleBanner(src: String?) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
   Glide.with(this.context.applicationContext)
            .load(src)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.bg_error_campaign)
            .into(this)
}


fun ImageView.loadImageWithHolder(src: String?, @DrawableRes placeholder: Int) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
   Glide.with(this.context.applicationContext)
            .load(src)
            .placeholder(circularProgressDrawable)
            .error(placeholder)
            .into(this)
}

fun ImageView.loadRoundedImage(src: String?, @DrawableRes placeholder: Int = R.drawable.ic_error_ic_image, corner: Int = 10, width:Int? = null) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    if (width == null) {
       Glide.with(this.context.applicationContext)
                .load(src)
                .placeholder(circularProgressDrawable)
                .error(placeholder)
                .transform(CenterInside(), RoundedCorners(corner.dpToPx()))
                .into(this)
    } else {
       Glide.with(this.context.applicationContext)
                .load(src)
                .placeholder(circularProgressDrawable)
                .error(placeholder)
                .transform(CenterInside(), RoundedCorners(corner.dpToPx()))
                .override(width, width)
                .into(this)
    }
}

infix fun ImageView.loadSimpleBitmap(src: Bitmap?) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
   Glide.with(this.context.applicationContext)
            .load(src)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.error_load_image)
            .into(this)
}

infix fun ImageView.loadSimpleFile(src: File?) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
   Glide.with(this.context.applicationContext)
            .load(src)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.error_load_image)
            .into(this)
}

fun ImageView.loadSimpleFile(src: File?, corner: Int) {
    val circularProgressDrawable = CircularProgressDrawable(ICheckApplication.getInstance())
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
   Glide.with(this.context.applicationContext)
            .load(src)
            .apply(RequestOptions().transform(RoundedCorners(corner.dpToPx())))
            .placeholder(circularProgressDrawable)
            .error(R.drawable.error_load_image)
            .into(this)
}


infix fun Bitmap.overlayBitmapToCenter(overlay: Bitmap): Bitmap {
    val bgHeight = this.height
    val bgWidth = this.width
    val overlayHeight = overlay.height
    val overlayWidth = overlay.width
    val marginLeft = bgWidth * 0.5 - overlayHeight * 0.5
    val marginTop = bgHeight * 0.5 - overlayWidth * 0.5

    val overlayBitmap = Bitmap.createBitmap(bgWidth, bgHeight, this.config)
    val canvas = Canvas(overlayBitmap)
    canvas.drawBitmap(this, Matrix(), null)
    canvas.drawBitmap(overlay, marginLeft.toFloat(), marginTop.toFloat(), null)
    return overlayBitmap
}

fun Bitmap.resizeBitmap(newWidth: Int, newHeight: Int, isCenterCrop:Boolean = false): Bitmap {
    val createScaledBitmap = Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    return if (!isCenterCrop) {
        createScaledBitmap
    } else {
        ThumbnailUtils.extractThumbnail(this, newWidth, newHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
    }
}

fun Bitmap.centerCrop(newWidth: Int, newHeight: Int):Bitmap {
    return if (this.getWidth() >= this.getHeight()){

         Bitmap.createBitmap(
                this,
                 newWidth/2 - newHeight/2,
                0,
                 newWidth,
                newHeight
        );

    }else{

       Bitmap.createBitmap(
                this,
                0,
               newHeight/2 - newWidth/2,
                newWidth,
                newHeight
        );
    }
}


fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Float.spToPx(): Float {
    return (this * Resources.getSystem().displayMetrics.scaledDensity)
}


fun Float.toPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, ICheckApplication.getInstance().resources.displayMetrics)
}

fun Int.toDp(): Int {
    val res = ICheckApplication.getInstance().resources
//    return this * (res.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this.toFloat(), res.displayMetrics).toInt()
}

fun Float.toDp(): Float {
    val res = ICheckApplication.getInstance().resources
//    return this * (res.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this.toFloat(), res.displayMetrics)
}

fun EditText.addPriceTextWatcher() {
    this.addTextChangedListener(object : AfterTextWatcher() {
        var current = ""

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                if (current.length <= s.toString().length) {
                    this@addPriceTextWatcher.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[,.đ]".toRegex(), "")
                    val formatted = String.format("%dđ", cleanString.toLong())
                    current = formatted
                    this@addPriceTextWatcher.setText(formatted)
                    this@addPriceTextWatcher.setSelection(formatted.length)
                    this@addPriceTextWatcher.addTextChangedListener(this)
                } else {
                    this@addPriceTextWatcher.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[,.đ]".toRegex(), "")
                    if (cleanString.length > 1) {
                        val formatted = String.format("%dđ", cleanString.substring(0, cleanString.length - 1).toLong())
                        current = formatted
                        this@addPriceTextWatcher.setText(formatted)
                        this@addPriceTextWatcher.setSelection(formatted.length)
                    } else {
                        this@addPriceTextWatcher.setText("")
                        current = ""
                    }
                    this@addPriceTextWatcher.addTextChangedListener(this)
                }
            }
        }
    })
}

fun ViewGroup.getLayoutInflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}

fun CompoundButton.setCustomChecked(value: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}

fun CompoundButton.setCustomChecked(listener: CompoundButton.OnCheckedChangeListener) {
    setOnCheckedChangeListener(null)
    setOnCheckedChangeListener(listener)
}

fun AppCompatImageView.setRankUser(rank: Int?) {
    when (rank) {
        Constant.USER_LEVEL_SILVER -> {
            this.setImageResource(R.drawable.ic_avatar_rank_silver_16dp)
        }
        Constant.USER_LEVEL_GOLD -> {
            this.setImageResource(R.drawable.ic_avatar_rank_gold_16dp)
        }
        Constant.USER_LEVEL_DIAMOND -> {
            this.setImageResource(R.drawable.ic_avatar_rank_diamond_16dp)
        }
        Constant.USER_LEVEL_STANDARD -> {
            this.setImageResource(R.drawable.ic_avatar_rank_standard_16dp)
        }
        else -> {
            this.setImageResource(0)
        }
    }
}

fun AppCompatImageView.setRankUserBig(rank: Int?) {
    when (rank) {
        Constant.USER_LEVEL_SILVER -> {
            this.setImageResource(R.drawable.ic_avatar_silver_24dp)
        }
        Constant.USER_LEVEL_GOLD -> {
            this.setImageResource(R.drawable.ic_avatar_gold_24dp)
        }
        Constant.USER_LEVEL_DIAMOND -> {
            this.setImageResource(R.drawable.ic_avatar_diamond_24dp)
        }
        Constant.USER_LEVEL_STANDARD -> {
            this.setImageResource(R.drawable.ic_avatar_standard_24dp)
        }
        else -> {
            this.setImageResource(0)
        }
    }
}

fun AppCompatImageView.setRankUser36dp(rank: Int?) {
    when (rank) {
        Constant.USER_LEVEL_SILVER -> {
            this.setImageResource(R.drawable.ic_leftmenu_avatar_silver_36dp)
        }
        Constant.USER_LEVEL_GOLD -> {
            this.setImageResource(R.drawable.ic_leftmenu_avatar_gold_36dp)
        }
        Constant.USER_LEVEL_DIAMOND -> {
            this.setImageResource(R.drawable.ic_leftmenu_avatar_diamond_36dp)
        }
        Constant.USER_LEVEL_STANDARD -> {
            this.setImageResource(R.drawable.ic_leftmenu_avatar_standard_36dp)
        }
        else -> {
            this.setImageResource(R.drawable.ic_leftmenu_avatar_standard_36dp)
        }
    }
}

fun setRankUser(rank: Int): Int {
    return when (rank) {
        Constant.USER_LEVEL_SILVER -> {
            R.drawable.ic_avatar_rank_silver_16dp
        }
        Constant.USER_LEVEL_GOLD -> {
            R.drawable.ic_avatar_rank_gold_16dp
        }
        Constant.USER_LEVEL_DIAMOND -> {
            R.drawable.ic_avatar_rank_diamond_16dp
        }
        else -> {
            R.drawable.ic_avatar_rank_standard_16dp
        }

    }
}


inline fun EditText.setImeActionDone(crossinline onDone: () -> Unit, timeout: Long = 500L) {
    this.setImeOptions(EditorInfo.IME_ACTION_DONE)
    this.setRawInputType(InputType.TYPE_CLASS_TEXT)
    this.setOnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(timeout)
                CoroutineScope(Dispatchers.Main).launch {
                    onDone()
                }
            }
        }
        false
    }
}

fun View.simpleVisibleAnim() {
    this.animate().alpha(1f).apply {
        duration = 800
    }.start()
}

fun View.simpleGoneAnim() {
    this.animate().alpha(0f).apply {
        duration = 800
    }.start()
}

fun View.setOnSingleClickListener(l: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(l))
}

fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}

class OnSingleClickListener : View.OnClickListener {

    private val onClickListener: View.OnClickListener

    constructor(listener: View.OnClickListener) {
        onClickListener = listener
    }

    constructor(listener: (View) -> Unit) {
        onClickListener = View.OnClickListener { listener.invoke(it) }
    }

    override fun onClick(v: View) {
        val currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis >= previousClickTimeMillis + DELAY_MILLIS) {
            previousClickTimeMillis = currentTimeMillis
            onClickListener.onClick(v)
        }
    }

    companion object {
        // Tweak this value as you see fit. In my personal testing this
        // seems to be good, but you may want to try on some different
        // devices and make sure you can't produce any crashes.
        private const val DELAY_MILLIS = 200L

        private var previousClickTimeMillis = 0L
    }

}

fun ImageView.setImageDrawableWithAnimation(drawable: Drawable, duration: Int = 300) {
    val currentDrawable = getDrawable()
    if (currentDrawable == null) {
        setImageDrawable(drawable)
        return
    }

    val transitionDrawable = TransitionDrawable(arrayOf(
            currentDrawable,
            drawable
    ))
    setImageDrawable(transitionDrawable)
    transitionDrawable.startTransition(duration)
}


fun ImageView.setImageDrawableWithAnimation(@DrawableRes id:Int, duration: Int = 300) {
    val drawable = ResourcesCompat.getDrawable(this.context.resources, id, null)
    val currentDrawable = getDrawable()
    if (currentDrawable == null) {
        setImageDrawable(drawable)
        return
    }

    val transitionDrawable = TransitionDrawable(arrayOf(
            currentDrawable,
            drawable
    ))
    setImageDrawable(transitionDrawable)
    transitionDrawable.startTransition(duration)
}