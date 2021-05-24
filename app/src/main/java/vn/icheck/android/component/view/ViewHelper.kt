package vn.icheck.android.component.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.*
import android.os.Build
import android.os.Handler
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.widget.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.MapView
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.avatar_user.AvatarUserComponent
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.postofuser.ProductInFeedComponent
import vn.icheck.android.component.rating_star.RatingStarComponent
import vn.icheck.android.ui.view.TextBarlowSemiBold
import vn.icheck.android.ui.view.TextBarlowMedium
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.MiddleMultilineTextView
import vn.icheck.android.ui.colorcardview.ColorCardView
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.ui.layout.HeightWrappingViewPager
import vn.icheck.android.ui.view.SquareImageView
import vn.icheck.android.util.kotlin.WidgetUtils
import kotlin.math.abs

object ViewHelper {

    fun convertViewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    /*
    * Base View
    * */
    var outValue = TypedValue()

    fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun createLayoutParams(
        width: Int,
        height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height)
    }

    fun createLayoutParams(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height, weight)
    }

    fun createLayoutParams32Dp(
        height: Int,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height).also {
            it.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        }
    }

    fun createLayoutParams(
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        }
    }

    fun createLayoutParams(
        width: Int,
        height: Int,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height).also {
            it.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        }
    }

    fun createLayoutParams(
        width: Int,
        height: Int,
        weight: Float,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height, weight).also {
            it.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        }
    }

    val sansSerif = "sans-serif"
    val sansSerifMedium = "sans-serif-medium"
    val barlowMedium = "barlow_medium"
    val barlowSemibold = "barlow_semi_bold"

    fun createTypeface(familyName: String = sansSerif, style: Int = Typeface.NORMAL): Typeface {
        return Typeface.create(familyName, style)
    }

    fun createTypeface(context: Context, font: Int): Typeface {
        return ResourcesCompat.getFont(context, font)!!
    }

    fun createEditText(
        context: Context,
        layoutParams: ViewGroup.LayoutParams,
        background: Drawable?,
        typeFace: Typeface,
        textColor: Int,
        fontSize: Float
    ): AppCompatEditText {
        return AppCompatEditText(context).also {
            it.layoutParams = layoutParams
            it.typeface = typeFace
            it.background = background
            it.setTextColor(textColor)
            it.includeFontPadding = false
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            it.ellipsize = TextUtils.TruncateAt.END
            it.gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun createText(
        context: Context,
        layoutParams: ViewGroup.LayoutParams,
        background: Drawable?,
        typeFace: Typeface,
        textColor: Int,
        fontSize: Float
    ): AppCompatTextView {
        return AppCompatTextView(context).also {
            it.layoutParams = layoutParams
            it.typeface = typeFace
            it.background = background
            it.setTextColor(textColor)
            it.includeFontPadding = false
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            it.ellipsize = TextUtils.TruncateAt.END
            it.gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun createText(
        context: Context,
        layoutParams: ViewGroup.LayoutParams,
        backgroundResource: Int,
        typeFace: Typeface,
        textColor: Int,
        fontSize: Float
    ): AppCompatTextView {
        return AppCompatTextView(context).also {
            it.layoutParams = layoutParams
            it.typeface = typeFace
            it.setBackgroundResource(backgroundResource)
            it.setTextColor(textColor)
            it.includeFontPadding = false
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            it.ellipsize = TextUtils.TruncateAt.END
            it.gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun createText(
        context: Context,
        layoutParams: ViewGroup.LayoutParams,
        backgroundResource: Int,
        typeFace: Typeface,
        textColor: ColorStateList?,
        fontSize: Float
    ): AppCompatTextView {
        return AppCompatTextView(context).also {
            it.layoutParams = layoutParams
            it.typeface = typeFace
            it.setBackgroundResource(backgroundResource)
            it.setTextColor(textColor)
            it.includeFontPadding = false
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            it.ellipsize = TextUtils.TruncateAt.END
            it.gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun createText(
        context: Context,
        layoutParams: ViewGroup.LayoutParams,
        background: Drawable?,
        typeFace: Typeface,
        textColor: Int,
        fontSize: Float,
        maxLines: Int
    ): AppCompatTextView {
        return AppCompatTextView(context).also {
            it.layoutParams = layoutParams
            it.typeface = typeFace
            it.background = background
            it.setTextColor(textColor)
            it.includeFontPadding = false
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            it.maxLines = maxLines
            it.ellipsize = TextUtils.TruncateAt.END
            it.gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun createText(
        context: Context,
        layoutParams: ViewGroup.LayoutParams,
        backgroundResource: Int,
        typeFace: Typeface,
        textColor: Int,
        fontSize: Float,
        maxLines: Int
    ): AppCompatTextView {
        return AppCompatTextView(context).also {
            it.layoutParams = layoutParams
            it.typeface = typeFace
            it.setBackgroundResource(backgroundResource)
            it.setTextColor(textColor)
            it.includeFontPadding = false
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            it.maxLines = maxLines
            it.ellipsize = TextUtils.TruncateAt.END
            it.gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun getDrawableFillColor(resource: Int, color: String): Drawable? {
        return ContextCompat.getDrawable(ICheckApplication.getInstance(), resource)?.apply {
            DrawableCompat.setTint(this, Color.parseColor(color))
        }
    }

    fun getBitmapFromUrl(context: Context, url: String, size: Int, call: (Bitmap) -> Unit) {
        Glide.with(context.applicationContext)
            .asDrawable()
            .load(url)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.let {
                        call.invoke(it.toBitmap(size, size))
                    }
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    call.invoke(resource.toBitmap(size, size))
                }
            })
    }

    fun getDrawableFromUrl(context: Context, url: String, size: Int, call: (Drawable) -> Unit) {
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.let {
                        call.invoke(
                            BitmapDrawable(
                                context.resources,
                                Bitmap.createScaledBitmap(it.toBitmap(), size, size, false)
                            )
                        )
                    }
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    call.invoke(
                        BitmapDrawable(
                            context.resources,
                            Bitmap.createScaledBitmap(resource, size, size, false)
                        )
                    )
                }
            })
    }

    fun getDrawableSize(context: Context, drawable: Drawable, width: Int, height: Int): Drawable {
        return BitmapDrawable(
            context.resources,
            Bitmap.createScaledBitmap((drawable as BitmapDrawable).bitmap, width, height, true)
        )
    }

    fun getBitmapSize(context: Context, bitmap: Bitmap, width: Int, height: Int): Drawable {
        return BitmapDrawable(
            context.resources,
            Bitmap.createScaledBitmap(bitmap, width, height, true)
        )
    }

    fun createColorStateList(unCheckColor: Int, checkedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(unCheckColor, checkedColor)
        )
    }

    fun createColorStateList(
        disableColor: Int,
        enableColor: Int,
        pressedColor: Int
    ): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed)
            ),
            intArrayOf(disableColor, enableColor, pressedColor)
        )
    }

    fun createDrawableStateList(
        uncheckedResource: Drawable?,
        checkedResource: Drawable?
    ): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        statesListDrawable.addState(intArrayOf(-android.R.attr.state_checked), uncheckedResource)
        statesListDrawable.addState(intArrayOf(android.R.attr.state_checked), checkedResource)

        return statesListDrawable
    }

    fun createCheckedDrawable(uncheckedResource: Int, checkedResource: Int): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        statesListDrawable.addState(
            intArrayOf(-android.R.attr.state_checked),
            ContextCompat.getDrawable(ICheckApplication.getInstance(), uncheckedResource)
        )
        statesListDrawable.addState(
            intArrayOf(android.R.attr.state_checked),
            ContextCompat.getDrawable(ICheckApplication.getInstance(), checkedResource)
        )

        return statesListDrawable
    }

    fun createStateListDrawable(
        enableColor: Int,
        pressedColor: Int,
        enableStrokeColor: Int,
        pressedStrokeColor: Int,
        strokeWidth: Int,
        radius: Float
    ): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createRippleDrawable(enableColor, strokeWidth, enableStrokeColor, radius)
            )
        } else {
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createShapeDrawable(enableColor, strokeWidth, enableStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_pressed),
                createShapeDrawable(pressedColor, strokeWidth, pressedStrokeColor, radius)
            )
        }

        return statesListDrawable
    }

    fun createStateListDrawable(
        disableColor: Int, enableColor: Int, pressedColor: Int,
        disableStrokeColor: Int, enableStrokeColor: Int, pressedStrokeColor: Int,
        strokeWidth: Int, radius: Float
    ): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                createShapeDrawable(disableColor, strokeWidth, disableStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createRippleDrawable(enableColor, strokeWidth, enableStrokeColor, radius)
            )
        } else {
            statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                createShapeDrawable(disableColor, strokeWidth, disableStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createShapeDrawable(enableColor, strokeWidth, enableStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_pressed),
                createShapeDrawable(pressedColor, strokeWidth, pressedStrokeColor, radius)
            )
        }

        return statesListDrawable
    }

    @SuppressLint("NewApi")
    fun createRippleDrawable(context: Context, drawable: Drawable): RippleDrawable {
        return RippleDrawable(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.black_10
                )
            ), drawable, null
        )
    }

    @SuppressLint("NewApi")
    fun createRippleDrawable(
        color: Int,
        strokeWidth: Int,
        strokeColor: Int,
        radius: Float
    ): RippleDrawable {
        return RippleDrawable(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    ICheckApplication.getInstance(),
                    R.color.black_10
                )
            ), createShapeDrawable(color, strokeWidth, strokeColor, radius), null
        )
    }

    fun createShapeDrawable(color: Int, radius: Float): GradientDrawable {
        return GradientDrawable().also {
            it.setColor(color)
            it.cornerRadius = radius
        }
    }

    fun createShapeDrawable(
        color: Int,
        strokeWidth: Int,
        strokeColor: Int,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable().also {
            it.setColor(color)
            it.setStroke(strokeWidth, strokeColor)
            it.cornerRadius = radius
        }
    }

    fun createShapeDrawable(
        color: Int,
        strokeWidth: Int,
        strokeColor: Int,
        radiusTopLeft: Float,
        radiusTopLRight: Float,
        radiusBottomRight: Float,
        radiusBottomLeft: Float
    ): GradientDrawable {
        return GradientDrawable().also { gradientDrawable ->
            gradientDrawable.setColor(color)
            gradientDrawable.setStroke(strokeWidth, strokeColor)
            gradientDrawable.cornerRadii = floatArrayOf(
                radiusTopLeft,
                radiusTopLeft,
                radiusTopLRight,
                radiusTopLRight,
                radiusBottomRight,
                radiusBottomRight,
                radiusBottomLeft,
                radiusBottomLeft
            )
        }
    }
    /*
    * End Base View
    * */

    fun createCampaignHeader(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParent.setPadding(
            SizeHelper.size12,
            SizeHelper.size12,
            SizeHelper.size12,
            SizeHelper.size12
        )
        layoutParent.orientation = LinearLayout.HORIZONTAL

        val tvTitle = AppCompatTextView(context)
        tvTitle.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        tvTitle.typeface = Typeface.create(sansSerifMedium, Typeface.NORMAL)
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
        tvTitle.includeFontPadding = false
        tvTitle.setPadding(0, 0, SizeHelper.size12, 0)
        layoutParent.addView(tvTitle)

        val tvViewMore = AppCompatTextView(context)
        tvViewMore.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvViewMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvViewMore.typeface = Typeface.create(sansSerif, Typeface.NORMAL)
        tvViewMore.setTextColor(ContextCompat.getColor(context, R.color.darkGray3))
        tvViewMore.background = createStateListDrawable(
            Color.WHITE, ContextCompat.getColor(context, R.color.lightGray),
            Color.TRANSPARENT, Color.TRANSPARENT,
            0, 0f
        )
        tvViewMore.includeFontPadding = false
        tvViewMore.setPadding(0, SizeHelper.size5, 0, SizeHelper.size5)
        layoutParent.addView(tvViewMore)

        return layoutParent
    }

    fun createBannerHolder(context: Context, marginTop: Int, marginBottom: Int): ViewGroup {
        val layoutParent = ConstraintLayout(context)
        layoutParent.id = R.id.layoutContainer
        val parentParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        parentParams.setMargins(SizeHelper.size12, marginTop, SizeHelper.size12, marginBottom)
        layoutParent.layoutParams = parentParams

        val imgBanner = AppCompatImageView(context)
        imgBanner.id = R.id.imgBanner
        imgBanner.layoutParams = ConstraintLayout.LayoutParams(0, 0)
        imgBanner.scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParent.addView(imgBanner)

        val bannerSet = ConstraintSet()
        bannerSet.clone(layoutParent)
        bannerSet.connect(imgBanner.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        bannerSet.connect(imgBanner.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        bannerSet.connect(imgBanner.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)
        bannerSet.connect(imgBanner.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        bannerSet.setDimensionRatio(imgBanner.id, "H, 750:300")
        bannerSet.applyTo(layoutParent)

        return layoutParent
    }

    fun createBannerHolder(context: Context): ViewGroup {
        val layoutParent = ConstraintLayout(context)
        layoutParent.id = R.id.layoutContainer
        val parentParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        parentParams.setMargins(
            SizeHelper.size12,
            SizeHelper.size6,
            SizeHelper.size12,
            SizeHelper.size6
        )
        layoutParent.layoutParams = parentParams

        val imgBanner = AppCompatImageView(context)
        imgBanner.id = R.id.imgBanner
        imgBanner.layoutParams = ConstraintLayout.LayoutParams(0, 0)
        imgBanner.scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParent.addView(imgBanner)

        val bannerSet = ConstraintSet()
        bannerSet.clone(layoutParent)
        bannerSet.connect(imgBanner.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        bannerSet.connect(imgBanner.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        bannerSet.connect(imgBanner.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)
        bannerSet.connect(imgBanner.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        bannerSet.setDimensionRatio(imgBanner.id, "H, 351:117")
        bannerSet.applyTo(layoutParent)

        return layoutParent
    }

    fun createCollectionBannerImage(layoutParent: ConstraintLayout, size: String) {
        val imgBanner = AppCompatImageView(layoutParent.context)
        imgBanner.id = R.id.imgBanner
        imgBanner.layoutParams = ConstraintLayout.LayoutParams(0, 0).also {
            it.bottomMargin = SizeHelper.size12
        }
        imgBanner.scaleType = ImageView.ScaleType.FIT_CENTER
        layoutParent.addView(imgBanner)

        val bannerSet = ConstraintSet()
        bannerSet.clone(layoutParent)
        bannerSet.connect(imgBanner.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        bannerSet.connect(imgBanner.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        bannerSet.connect(imgBanner.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)
        bannerSet.connect(imgBanner.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        try {
            val mSize = size.split("x")
            bannerSet.setDimensionRatio(imgBanner.id, "H, ${mSize[0]}:${mSize[1]}")
        } catch (e: Exception) {
            bannerSet.setDimensionRatio(imgBanner.id, "H, 750:300")
        }
        bannerSet.applyTo(layoutParent)
    }

    fun createFullBannerHolder(context: Context): View {
        val layoutParent = ConstraintLayout(context)
        layoutParent.id = R.id.layoutContainer
        layoutParent.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val imgBanner = AppCompatImageView(context)
        imgBanner.id = R.id.imgBanner
        imgBanner.layoutParams = ConstraintLayout.LayoutParams(0, 0)
        imgBanner.scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParent.addView(imgBanner)

        val bannerSet = ConstraintSet()
        bannerSet.clone(layoutParent)
        bannerSet.connect(imgBanner.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        bannerSet.connect(imgBanner.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        bannerSet.connect(imgBanner.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)
        bannerSet.connect(imgBanner.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        bannerSet.setDimensionRatio(imgBanner.id, "H, 750:300")
        bannerSet.applyTo(layoutParent)

        return layoutParent
    }

    fun setupBanner(
        layoutParent: ConstraintLayout,
        imgBanner: AppCompatImageView,
        bannerSize: String?,
        bannerUrl: String?
    ) {
        val size = try {
            bannerSize?.split("x")
        } catch (e: Exception) {
            null
        }

        if (size != null && size.size > 1) {
            val cs = ConstraintSet()
            cs.clone(layoutParent)
            cs.setDimensionRatio(imgBanner.id, "${size[0]}:${size[1]}")
            cs.applyTo(layoutParent)
        }

        WidgetUtils.loadImageUrlRounded(
            imgBanner,
            bannerUrl,
            R.drawable.ic_default_horizontal,
            SizeHelper.size10
        )
    }

    fun createProductHorizontal(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(
            SizeHelper.size152,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            SizeHelper.size6,
            0,
            SizeHelper.size6,
            0
        )
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.background = createStateListDrawable(
            Color.WHITE, ContextCompat.getColor(context, R.color.lightGray),
            Color.TRANSPARENT, Color.TRANSPARENT,
            0, SizeHelper.size8.toFloat()
        )
        layoutParent.setPadding(
            SizeHelper.size6,
            SizeHelper.size6,
            SizeHelper.size6,
            SizeHelper.size6
        )

        /* Layout Image */
        val layoutImage = RelativeLayout(context)
        layoutImage.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParent.addView(layoutImage)

        val imgProduct = SquareImageView(context)
        imgProduct.id = R.id.imgProduct
        imgProduct.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        imgProduct.scaleType = ImageView.ScaleType.FIT_CENTER
        layoutImage.addView(imgProduct)

        val tvVerified = AppCompatTextView(context)
        tvVerified.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                it.addRule(RelativeLayout.ALIGN_END, R.id.imgProduct)
            } else {
                it.addRule(RelativeLayout.ALIGN_RIGHT, R.id.imgProduct)
            }
            it.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgProduct)
        }
        tvVerified.background = ContextCompat.getDrawable(context, R.drawable.bg_product_verified)
        tvVerified.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
        tvVerified.compoundDrawablePadding = SizeHelper.size4
        tvVerified.typeface = Typeface.create(sansSerif, Typeface.BOLD)
        tvVerified.includeFontPadding = false
        tvVerified.setPadding(
            SizeHelper.size6,
            SizeHelper.size2,
            SizeHelper.size6,
            SizeHelper.size2
        )
        tvVerified.setText(R.string.verified)
        tvVerified.setTextColor(Color.WHITE)
        tvVerified.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        tvVerified.gravity = Gravity.CENTER_VERTICAL
        layoutImage.addView(tvVerified)
        /* End Layout Image */

        val tvName = AppCompatTextView(context)
        val productNameParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.topMargin = SizeHelper.size8
        }
        tvName.layoutParams = productNameParams
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvName.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        tvName.setTextColor(ContextCompat.getColor(context, R.color.colorNormalText))
        tvName.includeFontPadding = false
        tvName.minLines = 2
        tvName.maxLines = 2
        tvName.ellipsize = TextUtils.TruncateAt.END
        layoutParent.addView(tvName)

        val ratingBar =
            LayoutInflater.from(context).inflate(R.layout.rating_bar_10dp, layoutParent, false)
//        ratingBar.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
//            it.topMargin = SizeHelper.size6
//        }
        layoutParent.addView(ratingBar)

        val tvPrice = AppCompatTextView(context)
        tvPrice.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ).also {
            it.topMargin = SizeHelper.size8
        }
        tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvPrice.typeface = Typeface.create(sansSerifMedium, Typeface.NORMAL)
        tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
        tvPrice.includeFontPadding = false
        tvPrice.isSingleLine = true
        tvPrice.ellipsize = TextUtils.TruncateAt.END
        layoutParent.addView(tvPrice)

        return layoutParent
    }

    fun createProductHorizontalNew(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(
            SizeHelper.size150,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0,
            0,
            SizeHelper.size1,
            0
        )
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(Color.parseColor("#ffffff"))
        layoutParent.setPadding(
            SizeHelper.size4,
            SizeHelper.size4,
            SizeHelper.size4,
            SizeHelper.size4
        )

        //Image Product

        val imgProduct = AppCompatImageView(context)
        imgProduct.id = R.id.imgProduct
        imgProduct.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            SizeHelper.size140
        )
        imgProduct.scaleType = ImageView.ScaleType.FIT_XY
        imgProduct.adjustViewBounds = true
        layoutParent.addView(imgProduct)

        //TextView Name Product
        val tvName = TextBarlowMedium(context)
        val productNameParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.topMargin = SizeHelper.size4
        }
        tvName.layoutParams = productNameParams
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvName.setTextColor(ContextCompat.getColor(context, R.color.darkGray1))
        tvName.includeFontPadding = false
        tvName.minLines = 2
        tvName.maxLines = 2
        tvName.ellipsize = TextUtils.TruncateAt.END
        layoutParent.addView(tvName)

        //Container rating- review

        val ratingLayout = LinearLayout(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.topMargin = SizeHelper.size4
            }
        }
        ratingLayout.orientation = LinearLayout.HORIZONTAL
        ratingLayout.gravity = Gravity.CENTER_VERTICAL

        val ratingBar = LayoutInflater.from(context)
            .inflate(R.layout.item_product_rating_bar_new, layoutParent, false)
        ratingLayout.addView(ratingBar)

        val tvPoint = TextBarlowMedium(context).also {
            it.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size6, 0, SizeHelper.size6, 0)
            }
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            it.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            it.includeFontPadding = false
            it.maxLines = 1
            it.ellipsize = TextUtils.TruncateAt.END
        }
        ratingLayout.addView(tvPoint)
        val tvReviewCount = TextBarlowMedium(context).also {
            it.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
            it.includeFontPadding = false
            it.maxLines = 1
            it.ellipsize = TextUtils.TruncateAt.END
        }

        ratingLayout.addView(tvReviewCount)
        layoutParent.addView(ratingLayout)

        //TextView Price
        val tvPrice = TextBarlowSemiBold(context)
        tvPrice.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ).also {
            it.topMargin = SizeHelper.size4
        }
        tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        tvPrice.includeFontPadding = false
        tvPrice.isSingleLine = true
        tvPrice.ellipsize = TextUtils.TruncateAt.END
        layoutParent.addView(tvPrice)


        //TextView Verified

        val tvVerified = TextBarlowMedium(context)
        tvVerified.layoutParams = createLayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.topMargin = SizeHelper.size2
        }
        tvVerified.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified_18px, 0, 0, 0)
        tvVerified.compoundDrawablePadding = SizeHelper.size2
        tvVerified.includeFontPadding = false
        tvVerified.setText(R.string.verified)
        tvVerified.setTextColor(ContextCompat.getColor(context, R.color.colorAccentGreen))
        tvVerified.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        tvVerified.gravity = Gravity.CENTER_VERTICAL
        layoutParent.addView(tvVerified)

        return layoutParent
    }

    fun createReviewHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.gravity = Gravity.CENTER_HORIZONTAL

        val viewPager = HeightWrappingViewPager(context)
        viewPager.id = R.id.viewPager
        viewPager.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParent.addView(viewPager)

        val indicator = LayoutInflater.from(context)
            .inflate(R.layout.item_indicator, layoutParent, false) as PageIndicatorView
        indicator.setDynamicCount(true)
        indicator.setInteractiveAnimation(true)
        indicator.setAnimationType(AnimationType.WORM)
        indicator.selectedColor = ContextCompat.getColor(context, R.color.colorSecondary)
        indicator.unselectedColor = ContextCompat.getColor(context, R.color.colorPrimary)
        indicator.setViewPager(viewPager)
        layoutParent.addView(indicator)

        return layoutParent
    }

    fun createBusinessItemHolder(context: Context): View {
        val imageButton = AppCompatImageButton(context)
        val layoutParams = LinearLayout.LayoutParams(SizeHelper.size74, SizeHelper.size74)
        layoutParams.setMargins(SizeHelper.size4, 0, SizeHelper.size4, SizeHelper.size8)
        imageButton.layoutParams = layoutParams
        imageButton.setPadding(
            SizeHelper.size1,
            SizeHelper.size1,
            SizeHelper.size1,
            SizeHelper.size1
        )
        imageButton.background =
            ContextCompat.getDrawable(context, R.drawable.bg_outline_blue_1_corners_10)
        return imageButton
    }

    fun createListProductHorizontal(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(0, SizeHelper.size6, 0, SizeHelper.size6)
        }
        layoutParent.orientation = LinearLayout.VERTICAL

        layoutParent.addView(
            createText(
                context,
                createLayoutParams(
                    SizeHelper.size12,
                    SizeHelper.size12,
                    SizeHelper.size12,
                    SizeHelper.size12
                ), null,
                createTypeface(sansSerifMedium, Typeface.NORMAL),
                ContextCompat.getColor(context, R.color.colorSecondary),
                16f, 1
            )
        )

        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        recyclerView.setPadding(SizeHelper.size6, 0, SizeHelper.size6, 0)
        recyclerView.clipToPadding = false
        layoutParent.addView(recyclerView)
        return layoutParent
    }

    fun createListProductHorizontalNew(
        context: Context,
        recycledViewPool: RecyclerView.RecycledViewPool?
    ): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.topMargin = SizeHelper.size6
        }
        layoutParent.orientation = LinearLayout.VERTICAL

        layoutParent.addView(LinearLayout(context).also { layoutTitle ->
            layoutTitle.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutTitle.orientation = LinearLayout.HORIZONTAL
            layoutTitle.setBackgroundColor(Color.WHITE)
            layoutTitle.setPadding(
                SizeHelper.size12,
                SizeHelper.size12,
                SizeHelper.size12,
                SizeHelper.size10
            )


            val tvTitle = AppCompatTextView(context)
            tvTitle.layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            tvTitle.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
            tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
            tvTitle.includeFontPadding = false
            tvTitle.isSingleLine = true
            tvTitle.setPadding(0, 0, SizeHelper.size12, 0)
            layoutTitle.addView(tvTitle)

            val tvViewMore = AppCompatTextView(context)
            tvViewMore.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            tvViewMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            tvViewMore.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            tvViewMore.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
            tvViewMore.includeFontPadding = false
            tvViewMore.setPadding(0, SizeHelper.size5, 0, SizeHelper.size5)
            tvViewMore.setText(R.string.xem_tat_ca)
            layoutTitle.addView(tvViewMore)
        })

        layoutParent.addView(View(context).also {
            it.layoutParams =
                createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size1)
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray6))
        })

        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams()
            it.orientation = LinearLayout.HORIZONTAL

            it.addView(View(context).also { view ->
                view.layoutParams =
                    createLayoutParams(SizeHelper.size12, LinearLayout.LayoutParams.MATCH_PARENT)
                view.setBackgroundColor(Color.WHITE)
            })

            it.addView(RecyclerView(context).also { recyclerView ->
                recyclerView.setRecycledViewPool(recycledViewPool)
                recyclerView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                recyclerView.clipToPadding = false
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false).apply {
                        initialPrefetchItemCount = 3
                    }
            })
        })

        return layoutParent
    }

    fun createTitleWidthViewMore(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams()
        layoutParent.setPadding(
            SizeHelper.size12,
            SizeHelper.size10,
            SizeHelper.size12,
            SizeHelper.size10
        )
        layoutParent.orientation = LinearLayout.HORIZONTAL

        layoutParent.addView(createText(
            context,
            createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
            null,
            createTypeface(context, R.font.barlow_semi_bold),
            ContextCompat.getColor(context, R.color.colorSecondary),
            18f
        ).also {
            it.setPadding(0, 0, SizeHelper.size12, 0)
        })

        layoutParent.addView(createText(
            context,
            createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT),
            outValue.resourceId,
            createTypeface(context, R.font.barlow_medium),
            ContextCompat.getColor(context, R.color.colorSecondText),
            14f
        ).also {
            it.setPadding(0, SizeHelper.size5, 0, SizeHelper.size5)
            it.setText(R.string.xem_tat_ca)
        })

        return layoutParent
    }

    fun createTitle(context: Context): AppCompatTextView {
        return createText(
            context,
            createLayoutParams(),
            null,
            createTypeface(context, R.font.barlow_semi_bold),
            ContextCompat.getColor(context, R.color.colorSecondary),
            18f
        ).also {
            it.setPadding(
                SizeHelper.size12,
                SizeHelper.size12,
                SizeHelper.size12,
                SizeHelper.size12
            )
        }
    }

    fun createTitleNoPadding(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams()
        layoutParent.setPadding(SizeHelper.size12, SizeHelper.size16, SizeHelper.size12, 0)
        layoutParent.orientation = LinearLayout.HORIZONTAL

        layoutParent.addView(createText(
            context,
            createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
            null,
            createTypeface(context, R.font.barlow_semi_bold),
            ContextCompat.getColor(context, R.color.colorSecondary),
            18f
        ).also {
            it.setPadding(0, 0, SizeHelper.size12, 0)
        })

        layoutParent.addView(createText(
            context,
            createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT),
            outValue.resourceId,
            createTypeface(context, R.font.barlow_semi_bold),
            ContextCompat.getColor(context, R.color.colorSecondary),
            14f
        ).also {
            it.setPadding(0, SizeHelper.size5, 0, 0)
            it.setText(R.string.xem_tat_ca)
        })

        return layoutParent
    }

    fun createSlideBannerHolder(context: Context): View {
        val layoutParent = FrameLayout(context)
        layoutParent.layoutParams = createLayoutParams()
        layoutParent.setPadding(SizeHelper.size12, 0, SizeHelper.size12, 0)
        layoutParent.setBackgroundColor(ContextCompat.getColor(context,R.color.colorBackgroundGray))

        layoutParent.addView(View(context).also {
            it.layoutParams =
                createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size10)
            it.setBackgroundColor(ContextCompat.getColor(it.context, R.color.colorBackgroundGray))
        })

        val viewPager = HeightWrappingViewPager(context)
        viewPager.layoutParams = createLayoutParams().also {
            it.topMargin = SizeHelper.size10
        }
        layoutParent.addView(viewPager)

        val layoutIndicator = IndicatorLineHorizontal(context)
        layoutIndicator.layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeHelper.size4).also {
                it.gravity = Gravity.BOTTOM
                it.setMargins(0, 0, 0, SizeHelper.size3)
            }
        layoutIndicator.setupViewPager(viewPager)
        layoutParent.addView(layoutIndicator)

        return layoutParent
    }

    fun createSurvey(context: Context): CardView {
        val layoutParent = CardView(context)
        layoutParent.layoutParams = createLayoutParams(
            SizeHelper.size12,
            SizeHelper.size6,
            SizeHelper.size12,
            SizeHelper.size6
        )
        layoutParent.setCardBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.gray_item_search
            )
        )
        layoutParent.radius = SizeHelper.size10.toFloat()

        val layoutContent = LinearLayout(context)
        layoutContent.layoutParams = createLayoutParams()
        layoutContent.orientation = LinearLayout.VERTICAL
        layoutParent.addView(layoutContent)

        /* Layout Header */
        val layoutHeader = LinearLayout(context)
        layoutHeader.layoutParams = createLayoutParams()
        layoutHeader.orientation = LinearLayout.VERTICAL
        layoutHeader.background =
            ContextCompat.getDrawable(context, R.drawable.bg_blue_corners_top_10)
        layoutContent.addView(layoutHeader)

        val tvName = AppCompatTextView(context)
        tvName.layoutParams =
            createLayoutParams(SizeHelper.size12, SizeHelper.size16, SizeHelper.size12, 0)
        tvName.typeface = Typeface.create(sansSerifMedium, Typeface.NORMAL)
        tvName.gravity = Gravity.CENTER_HORIZONTAL
        tvName.includeFontPadding = false
        tvName.setTextColor(Color.WHITE)
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        layoutHeader.addView(tvName)

        val tvTitle = AppCompatTextView(context)
        tvTitle.layoutParams =
            createLayoutParams(SizeHelper.size12, SizeHelper.size16, SizeHelper.size12, 0)
        tvTitle.typeface = Typeface.create(sansSerifMedium, Typeface.NORMAL)
        tvTitle.gravity = Gravity.CENTER_HORIZONTAL
        tvTitle.includeFontPadding = false
        tvTitle.setTextColor(Color.WHITE)
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        layoutHeader.addView(tvTitle)

        val progressBar = ProgressBar(context, null, android.R.style.Widget_ProgressBar_Horizontal)
        progressBar.layoutParams = createLayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            SizeHelper.size6,
            SizeHelper.size14,
            SizeHelper.size16,
            SizeHelper.size14,
            SizeHelper.size12
        )
        progressBar.max = 100
        progressBar.progress = 0
        progressBar.progressDrawable =
            ContextCompat.getDrawable(context, R.drawable.progress_orange_background_blue_corners_3)
        layoutHeader.addView(progressBar)
        /* End Layout Header */

        /* Layout Option */
        val layoutOption = FrameLayout(context)
        layoutOption.layoutParams =
            createLayoutParams(SizeHelper.size6, SizeHelper.size14, SizeHelper.size6, 0)
        layoutContent.addView(layoutOption)
        /* End Layout Option */

        /* Layout Button */
        val layoutButton = RelativeLayout(context)
        layoutButton.layoutParams = createLayoutParams(0, SizeHelper.size14, 0, SizeHelper.size20)
        layoutButton.setPadding(SizeHelper.size12, 0, SizeHelper.size12, 0)
        layoutContent.addView(layoutButton)

        val btnLeft = AppCompatTextView(context)
        btnLeft.layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, SizeHelper.size36)
                .also {
                    it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, SizeHelper.size4)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        it.addRule(RelativeLayout.ALIGN_PARENT_START)
                    } else {
                        it.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    }
                }
        btnLeft.setPadding(SizeHelper.size16, 0, SizeHelper.size16, 0)
        btnLeft.minWidth = SizeHelper.size90
        btnLeft.typeface = Typeface.create(sansSerif, Typeface.BOLD)
        btnLeft.gravity = Gravity.CENTER
        btnLeft.includeFontPadding = false
        btnLeft.setTextColor(Color.WHITE)
        btnLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        btnLeft.background = createStateListDrawable(
            ContextCompat.getColor(context, R.color.colorDisableText),
            ContextCompat.getColor(context, R.color.darkGray3),
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            0,
            (SizeHelper.size16 + SizeHelper.size2).toFloat()
        )
        layoutButton.addView(btnLeft)

        val btnRight = AppCompatTextView(context)
        btnRight.layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, SizeHelper.size36)
                .also {
                    it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        it.addRule(RelativeLayout.ALIGN_PARENT_END)
                    } else {
                        it.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    }
                }
        btnRight.setPadding(SizeHelper.size16, 0, SizeHelper.size16, 0)
        btnRight.minWidth = SizeHelper.size90
        btnRight.typeface = Typeface.create(sansSerif, Typeface.BOLD)
        btnRight.gravity = Gravity.CENTER
        btnRight.includeFontPadding = false
        btnRight.setTextColor(Color.WHITE)
        btnRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        btnRight.background = createStateListDrawable(
            ContextCompat.getColor(context, R.color.gray),
            ContextCompat.getColor(context, R.color.colorSecondary),
            ContextCompat.getColor(context, R.color.darkBlue),
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            0,
            (SizeHelper.size16 + SizeHelper.size2).toFloat()
        )
        layoutButton.addView(btnRight)
        /* End Layout Button */

        return layoutParent
    }

    fun createSurveySuccess(context: Context): View {
        val layoutParent = CardView(context)
        layoutParent.layoutParams = createLayoutParams(
            SizeHelper.size12,
            SizeHelper.size6,
            SizeHelper.size12,
            SizeHelper.size6
        )
        layoutParent.radius = SizeHelper.size12.toFloat()

        val layoutContent = LinearLayout(context)
        layoutContent.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutContent.orientation = LinearLayout.VERTICAL
        layoutParent.addView(layoutContent)

        val imageView = AppCompatImageView(context)
        imageView.layoutParams = createLayoutParams()
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.setImageResource(R.drawable.ic_direct_survey_success)
        layoutContent.addView(imageView)

        val tvContent = AppCompatTextView(context)
        tvContent.layoutParams = createLayoutParams(
            SizeHelper.size24,
            SizeHelper.size20,
            SizeHelper.size24,
            SizeHelper.size20
        )
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvContent.typeface = Typeface.create(sansSerif, Typeface.NORMAL)
        tvContent.setTextColor(ContextCompat.getColor(context, R.color.black_blue))
        tvContent.includeFontPadding = false
        tvContent.isSingleLine = false
        tvContent.gravity = Gravity.CENTER_HORIZONTAL
        tvContent.setText(R.string.text_direct_survey_success)
        layoutContent.addView(tvContent)

        return layoutParent
    }

    fun createFastSurvey(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(0, SizeHelper.size6, 0, SizeHelper.size6)
        layoutParent.setPadding(
            SizeHelper.size12,
            SizeHelper.size8,
            SizeHelper.size12,
            SizeHelper.size10
        )
        layoutParent.orientation = LinearLayout.VERTICAL

        // Layout Header
        layoutParent.addView(LinearLayout(context).also { layoutHeader ->
            layoutHeader.layoutParams = createLayoutParams()
            layoutHeader.orientation = LinearLayout.HORIZONTAL

            // Layout Image
            layoutHeader.addView(RelativeLayout(context).also { layoutImage ->
                layoutImage.layoutParams =
                    LinearLayout.LayoutParams(SizeHelper.size40, SizeHelper.size40)

                val imgLogo = CircleImageView(context)
                imgLogo.id = R.id.imgLogo
                imgLogo.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                imgLogo.scaleType = ImageView.ScaleType.CENTER_CROP
                layoutImage.addView(imgLogo)

                val tvVerified = AppCompatTextView(context)
                tvVerified.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        it.addRule(RelativeLayout.ALIGN_END, R.id.imgLogo)
                    } else {
                        it.addRule(RelativeLayout.ALIGN_RIGHT, R.id.imgLogo)
                    }
                    it.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgLogo)
                }
                tvVerified.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_home_verify_16dp,
                    0
                )
                layoutImage.addView(tvVerified)
            })

            // Layout Info
            layoutHeader.addView(LinearLayout(context).also { layoutInfo ->
                layoutInfo.layoutParams = createLayoutParams()
                layoutInfo.orientation = LinearLayout.VERTICAL

                layoutInfo.addView(
                    createText(
                        context,
                        createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f),
                        null,
                        createTypeface(context, R.font.barlow_semi_bold),
                        ContextCompat.getColor(context, R.color.colorNormalText),
                        16f
                    ), 1
                )

                layoutInfo.addView(
                    createText(
                        context,
                        createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f),
                        null,
                        createTypeface(context, R.font.barlow_medium),
                        ContextCompat.getColor(context, R.color.colorSecondText),
                        12f
                    ), 1
                )
            })
        })

        // Title
        layoutParent.addView(
            createText(
                context,
                createLayoutParams(0, SizeHelper.size12, 0, 0),
                null,
                createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorNormalText),
                14f
            )
        )

        // Layout Product
        layoutParent.addView(LinearLayout(context).also { layoutProduct ->
            layoutProduct.layoutParams = createLayoutParams(0, SizeHelper.size12, 0, 0)
            layoutProduct.orientation = LinearLayout.HORIZONTAL

            layoutProduct.addView(SquareImageView(context).also {
                it.layoutParams = createLayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f,
                    0,
                    0,
                    SizeHelper.size5,
                    0
                )
                it.setPadding(
                    SizeHelper.size12,
                    SizeHelper.size12,
                    SizeHelper.size12,
                    SizeHelper.size12
                )
            })

            layoutProduct.addView(SquareImageView(context).also {
                it.layoutParams = createLayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f,
                    SizeHelper.size5,
                    0,
                    0,
                    0
                )
                it.setPadding(
                    SizeHelper.size12,
                    SizeHelper.size12,
                    SizeHelper.size12,
                    SizeHelper.size12
                )
            })
        })

        // Layout Button
        layoutParent.addView(FrameLayout(context))

        return layoutParent
    }

    fun createFastSurveyButton(context: Context): LinearLayout {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams()
            layoutParent.orientation = LinearLayout.HORIZONTAL

            layoutParent.addView(
                createText(
                    context,
                    createLayoutParams(0, SizeHelper.size36, 1f, 0, 0, SizeHelper.size5, 0),
                    createStateListDrawable(
                        Color.WHITE,
                        ContextCompat.getColor(context, R.color.lightGray),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        SizeHelper.size1,
                        SizeHelper.size36.toFloat()
                    ),
                    createTypeface(context, R.font.barlow_semi_bold),
                    ContextCompat.getColor(context, R.color.colorNormalText),
                    16f
                )
            )

            layoutParent.addView(
                createText(
                    context,
                    createLayoutParams(0, SizeHelper.size36, 1f, SizeHelper.size5, 0, 0, 0),
                    createStateListDrawable(
                        Color.WHITE,
                        ContextCompat.getColor(context, R.color.lightGray),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        SizeHelper.size1,
                        SizeHelper.size36.toFloat()
                    ),
                    createTypeface(context, R.font.barlow_semi_bold),
                    ContextCompat.getColor(context, R.color.colorNormalText),
                    16f
                )
            )
        }
    }

    fun createFastSurveyProgress(context: Context): LinearLayout {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams()
            layoutParent.orientation = LinearLayout.VERTICAL

            // Layout Top
            layoutParent.addView(LinearLayout(context).also { layoutTitle ->
                layoutTitle.layoutParams = createLayoutParams()
                layoutTitle.orientation = LinearLayout.HORIZONTAL

                layoutTitle.addView(
                    createText(
                        context,
                        createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
                        null,
                        createTypeface(context, R.font.barlow_semi_bold),
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        16f
                    )
                )

                layoutTitle.addView(
                    createText(
                        context,
                        createLayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ),
                        null,
                        createTypeface(context, R.font.barlow_semi_bold),
                        ContextCompat.getColor(context, R.color.colorSecondText),
                        16f
                    )
                )
            })

            // ProgressBar
            layoutParent.addView(
                ProgressBar(
                    context,
                    null,
                    android.R.style.Widget_ProgressBar_Horizontal
                ).also { progressBar ->
                    progressBar.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size8,
                        SizeHelper.size14,
                        SizeHelper.size16,
                        SizeHelper.size14,
                        SizeHelper.size12
                    )
                    progressBar.max = 100
                    progressBar.progress = 0
                    progressBar.progressDrawable = ContextCompat.getDrawable(
                        context,
                        R.drawable.progress_light_blue_background_grey_corners_4
                    )
                })

            // Layout bottom
            layoutParent.addView(LinearLayout(context).also { layoutTitle ->
                layoutTitle.layoutParams = createLayoutParams(0, SizeHelper.size6, 0, 0)
                layoutTitle.orientation = LinearLayout.HORIZONTAL

                layoutTitle.addView(
                    createText(
                        context,
                        createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
                        null,
                        createTypeface(context, R.font.barlow_medium),
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        14f
                    )
                )

                layoutTitle.addView(
                    createText(
                        context,
                        createLayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ),
                        null,
                        createTypeface(context, R.font.barlow_medium),
                        ContextCompat.getColor(context, R.color.colorDisableText),
                        14f
                    )
                )
            })
        }
    }

    fun createMallCategoryHorizontal(context: Context): AppCompatTextView {
        return createText(
            context,
            createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                SizeHelper.size30,
                SizeHelper.size6,
                0,
                SizeHelper.size6,
                0
            ).also { params ->
                params.gravity = Gravity.CENTER_VERTICAL
            },
            createStateListDrawable(
                Color.WHITE,
                ContextCompat.getColor(context, R.color.lightGray),
                ContextCompat.getColor(context, R.color.gray),
                ContextCompat.getColor(context, R.color.gray),
                SizeHelper.size1,
                SizeHelper.size36.toFloat()
            ),
            createTypeface(sansSerifMedium),
            ContextCompat.getColor(context, R.color.colorDisableText),
            14f,
            1
        ).also {
            it.setPadding(SizeHelper.size20, 0, SizeHelper.size20, 0)
        }
    }

    fun createMallCategoryVertical(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context).also {
            it.layoutParams = createLayoutParams(0, SizeHelper.size8, 0, SizeHelper.size8)
            it.orientation = LinearLayout.VERTICAL
            it.setPadding(SizeHelper.size10, SizeHelper.size8, SizeHelper.size10, SizeHelper.size8)
            it.setBackgroundResource(outValue.resourceId)
        }

        layoutParent.addView(AppCompatImageButton(context).also {
            it.layoutParams =
                createLayoutParams(SizeHelper.size60, SizeHelper.size60).also { layoutParams ->
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                }
            it.background =
                ContextCompat.getDrawable(context, R.drawable.bg_outline_blue_1_corners_10)
            it.setPadding(
                SizeHelper.size10,
                SizeHelper.size10,
                SizeHelper.size10,
                SizeHelper.size10
            )
            it.isClickable = false
            it.isFocusable = false
        })

        layoutParent.addView(createText(
            context,
            createLayoutParams(0, SizeHelper.size6, 0, 0),
            null,
            createTypeface(),
            ContextCompat.getColor(context, R.color.black),
            10f,
            2
        ).also {
            it.gravity = Gravity.CENTER
        })

        return layoutParent
    }

    fun createProductShopVariant(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context).also {
            it.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
            it.orientation = LinearLayout.VERTICAL
            it.setBackgroundColor(Color.WHITE)
        }

        val tvTitle = TextBarlowSemiBold(context)
        tvTitle.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.bottomMargin = SizeHelper.size5
        }
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
        tvTitle.includeFontPadding = false
        tvTitle.setPadding(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)

        layoutParent.addView(tvTitle)

        return layoutParent
    }

    fun createProductNeedReview(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context).also {
            it.layoutParams = createLayoutParams()
            it.orientation = LinearLayout.VERTICAL
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGray))
        }

        val title = TextBarlowSemiBold(context)
        title.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        title.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
        title.includeFontPadding = false
        title.text = context.getString(R.string.chung_toi_can_ban_danh_gia)
        title.setPadding(SizeHelper.size12, SizeHelper.size20, 0, 0)
        layoutParent.addView(title)

        val recycleView = RecyclerView(context)
        recycleView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        recycleView.setPadding(SizeHelper.size6, 0, 0, 0)
        layoutParent.addView(recycleView)

        return layoutParent
    }

    fun createRecyclerViewWithTitleHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.setBackgroundColor(Color.WHITE)
        layoutParent.orientation = LinearLayout.VERTICAL

        // ALayout title
        layoutParent.addView(createTitleNoPadding(context))

        // List
        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        recyclerView.setPadding(SizeHelper.size3, 0, SizeHelper.size3, 0)
        recyclerView.clipToPadding = false
        layoutParent.addView(recyclerView)
        return layoutParent
    }

    fun createImageAssets(context: Context): LinearLayout {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            layoutParent.addView(LinearLayout(context).also { layoutTitle ->
                layoutTitle.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                layoutTitle.orientation = LinearLayout.HORIZONTAL
                layoutTitle.gravity = Gravity.CENTER_VERTICAL

                layoutTitle.addView(TextBarlowSemiBold(context).also { title ->
                    title.layoutParams =
                        createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    title.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                    title.includeFontPadding = false
                    title.text = "Hình ảnh"
                })

                layoutTitle.addView(TextBarlowSemiBold(context).also { tvMore ->
                    tvMore.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    tvMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    tvMore.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                    tvMore.includeFontPadding = false
                    tvMore.text = "Xem tất cả"
                })
            })

            layoutParent.addView(RecyclerView(context).also { listImage ->
                listImage.layoutParams = createLayoutParams()
                listImage.setPadding(SizeHelper.size12, SizeHelper.size10, 0, SizeHelper.size10)
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createShoppingCatalog(context: Context): LinearLayout {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams()
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            layoutParent.addView(View(context).also {
                it.layoutParams =
                    createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size10)
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGray))
            })

            layoutParent.addView(LinearLayout(context).also { layoutTitle ->
                layoutTitle.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
                layoutTitle.gravity = Gravity.CENTER_HORIZONTAL
                layoutTitle.orientation = LinearLayout.HORIZONTAL

                layoutTitle.addView(TextBarlowSemiBold(context).also { title ->
                    title.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                    )
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    title.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                    title.includeFontPadding = false
                    title.text = "Trải nghiệm mua sắm"
                    title.setPadding(SizeHelper.size12, 0, 0, 0)
                })

                layoutTitle.addView(AppCompatImageView(context).also { more ->
                    more.layoutParams = createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT)
                    more.setImageResource(R.drawable.bg_more_icheck_mall)
                })
            })

            layoutParent.addView(RecyclerView(context).also { recyclerView ->
                recyclerView.layoutParams =
                    createLayoutParams(SizeHelper.size6, 0, 0, SizeHelper.size6)
            })
        }
    }

    fun createItemCatalogShopping(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(
            SizeHelper.widthOfScreen / 4,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0,
            SizeHelper.size10,
            0,
            SizeHelper.size10
        )
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.gravity = Gravity.CENTER

        layoutParent.addView(CircleImageView(context).also { image ->
            image.layoutParams = createLayoutParams(SizeHelper.size64, SizeHelper.size64)
        })

        layoutParent.addView(TextBarlowMedium(context).also { title ->
            title.layoutParams = createLayoutParams(
                SizeHelper.size64,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0,
                SizeHelper.size10,
                0,
                0
            )
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            title.setTextColor(ContextCompat.getColor(context, R.color.colorNormalText))
            title.gravity = Gravity.CENTER
        })

        return layoutParent
    }

    fun createFeed(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context).also {
            it.layoutParams = createLayoutParams()
            it.orientation = LinearLayout.VERTICAL
        }

        // Layout header
        layoutParent.addView(ConstraintLayout(context).also { layoutHeader ->
            layoutHeader.id = R.id.layoutHeader
            layoutHeader.layoutParams = createLayoutParams()
            layoutHeader.setPadding(SizeHelper.size12, 0, SizeHelper.size12, 0)

            val layoutAvatar =
                AvatarUserComponent(context, SizeHelper.size40, SizeHelper.size16).also {
                    it.id = R.id.layoutAvatar
                    it.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                }
            layoutHeader.addView(layoutAvatar)

            val tvName = createText(
                context,
                ConstraintLayout.LayoutParams(0, 0).also {
                    it.setMargins(SizeHelper.size8, SizeHelper.size6, SizeHelper.size8, 0)
                },
                0,
                createTypeface(context, R.font.barlow_semi_bold),
                ContextCompat.getColor(context, R.color.colorNormalText),
                16f,
                1
            ).also {
                it.id = R.id.tvName
            }
            layoutHeader.addView(tvName)

            val layoutRating = LinearLayout(context).also { layoutRating ->
                layoutRating.id = R.id.layoutRating
                layoutRating.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                layoutRating.orientation = LinearLayout.HORIZONTAL
                layoutRating.gravity = Gravity.CENTER_VERTICAL

                // Text rating
                layoutRating.addView(createText(
                    context,
                    createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0,
                        0,
                        SizeHelper.size6,
                        0
                    ),
                    null,
                    createTypeface(context, R.font.barlow_medium),
                    Color.BLACK,
                    12f
                ).also {
                    it.setPadding(
                        SizeHelper.size6,
                        SizeHelper.size4,
                        SizeHelper.size6,
                        SizeHelper.size4
                    )
                })

                // Rating bar
                layoutRating.addView(
                    LayoutInflater.from(context)
                        .inflate(R.layout.rating_bar_12dp, layoutRating, false)
                )

                // Icon arrow down
                layoutRating.addView(AppCompatTextView(context).also {
                    it.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        SizeHelper.size6,
                        0,
                        0,
                        0
                    )
                    it.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_down_gray_10dp,
                        0,
                        0,
                        0
                    )
                })
            }
            layoutHeader.addView(layoutRating)

            val imgMore = AppCompatTextView(context).also {
                it.id = R.id.imgMore
                it.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                it.setBackgroundResource(ViewHelper.outValue.resourceId)
                it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_disable_24dp, 0)
            }
            layoutHeader.addView(imgMore)

            val bannerSet = ConstraintSet()
            bannerSet.clone(layoutHeader)
            bannerSet.connect(
                layoutAvatar.id,
                ConstraintSet.START,
                layoutHeader.id,
                ConstraintSet.START
            )
            bannerSet.connect(
                layoutAvatar.id,
                ConstraintSet.TOP,
                layoutHeader.id,
                ConstraintSet.TOP
            )

            bannerSet.connect(tvName.id, ConstraintSet.TOP, layoutHeader.id, ConstraintSet.TOP)
            bannerSet.connect(tvName.id, ConstraintSet.START, layoutAvatar.id, ConstraintSet.END)
            bannerSet.connect(tvName.id, ConstraintSet.END, imgMore.id, ConstraintSet.START)
            bannerSet.connect(tvName.id, ConstraintSet.BOTTOM, layoutRating.id, ConstraintSet.TOP)

            bannerSet.connect(imgMore.id, ConstraintSet.TOP, tvName.id, ConstraintSet.TOP)
            bannerSet.connect(imgMore.id, ConstraintSet.END, layoutHeader.id, ConstraintSet.END)

            bannerSet.connect(layoutRating.id, ConstraintSet.START, tvName.id, ConstraintSet.START)
            bannerSet.connect(layoutRating.id, ConstraintSet.END, tvName.id, ConstraintSet.END)
            bannerSet.connect(
                layoutRating.id,
                ConstraintSet.BOTTOM,
                layoutAvatar.id,
                ConstraintSet.BOTTOM
            )

            bannerSet.applyTo(layoutHeader)
        })

        // Text content
        layoutParent.addView(
            createText(
                context,
                createLayoutParams(0, SizeHelper.size10, 0, SizeHelper.size10),
                null,
                createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorNormalText),
                14f,
                3
            )
        )

        // Layout image
        layoutParent.addView(LayoutImageInPostComponent(context).also {
            it.layoutParams = createLayoutParams()
        })

        // Layout Product
        layoutParent.addView(ProductInFeedComponent(context).also {
            it.layoutParams =
                createLayoutParams(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
        })

        // Layout action
        layoutParent.addView(LayoutFeedAction(context).also {
            it.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
        })

        // Layout comment
        layoutParent.addView(LayoutFeedComment(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SizeHelper.size36,
                SizeHelper.size12,
                SizeHelper.size10,
                SizeHelper.size12,
                0
            )
        })

        // Text time
        layoutParent.addView(
            createText(
                context,
                createLayoutParams(
                    SizeHelper.size12,
                    SizeHelper.size6,
                    SizeHelper.size12,
                    SizeHelper.size10
                ),
                null,
                createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorSecondText),
                12f
            )
        )

        return layoutParent
    }

    fun createShortMessage(context: Context): LinearLayout {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams()
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.gravity = Gravity.CENTER
            layoutParent.setPadding(
                SizeHelper.size40,
                SizeHelper.size40,
                SizeHelper.size40,
                SizeHelper.size40
            )
            layoutParent.setBackgroundColor(Color.WHITE)

            layoutParent.addView(AppCompatImageView(context).also {
                it.layoutParams = createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT)
                it.scaleType = ImageView.ScaleType.CENTER_INSIDE
            })

            layoutParent.addView(createText(
                context,
                createLayoutParams().also { params ->
                    params.topMargin = SizeHelper.size16
                },
                null,
                createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.darkGray3),
                14f
            ).also {
                it.gravity = Gravity.CENTER_HORIZONTAL
            })

            layoutParent.addView(createText(
                context,
                createLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    SizeHelper.size44
                ).also { params ->
                    params.topMargin = SizeHelper.size16
                },
                createStateListDrawable(
                    Color.WHITE,
                    ContextCompat.getColor(context, R.color.lightGray),
                    ContextCompat.getColor(context, R.color.colorSecondary),
                    ContextCompat.getColor(context, R.color.colorSecondary),
                    SizeHelper.size1,
                    SizeHelper.size6.toFloat()
                ),
                createTypeface(context, R.font.barlow_semi_bold),
                ContextCompat.getColor(context, R.color.colorSecondary),
                16f
            ).also {
                it.gravity = Gravity.CENTER
                it.setPadding(SizeHelper.size16, 0, SizeHelper.size16, 0)
                it.setText(R.string.thu_lai)
            })
        }
    }

    fun createLongMessage(context: Context): NestedScrollView {
        return NestedScrollView(context).also { scroll ->
            scroll.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            scroll.isFillViewport = true

            scroll.addView(LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.gravity = Gravity.CENTER
                layoutParent.setPadding(
                    SizeHelper.size20,
                    SizeHelper.size40,
                    SizeHelper.size20,
                    SizeHelper.size40
                )

                layoutParent.addView(AppCompatImageView(context).also {
                    it.layoutParams = createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT)
                    it.scaleType = ImageView.ScaleType.CENTER_INSIDE
                })

                layoutParent.addView(createText(
                    context,
                    createLayoutParams().also { params ->
                        params.topMargin = SizeHelper.size16
                    },
                    null,
                    createTypeface(context, R.font.barlow_medium),
                    ContextCompat.getColor(context, R.color.darkGray3),
                    14f
                ).also {
                    it.gravity = Gravity.CENTER
                })

                layoutParent.addView(createText(
                    context,
                    createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size44
                    ).also { params ->
                        params.topMargin = SizeHelper.size16
                    },
                    createStateListDrawable(
                        ContextCompat.getColor(context, R.color.colorSecondText),
                        Color.WHITE,
                        ContextCompat.getColor(context, R.color.colorSecondText),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        ContextCompat.getColor(context, R.color.colorSecondary),
                        SizeHelper.size1,
                        SizeHelper.size6.toFloat()
                    ),
                    createTypeface(context, R.font.barlow_semi_bold),
                    ContextCompat.getColor(context, R.color.colorSecondary),
                    16f
                ).also {
                    it.gravity = Gravity.CENTER
                    it.setPadding(SizeHelper.size16, 0, SizeHelper.size16, 0)
                    it.setText(R.string.thu_lai)
                })
            })
        }
    }

    fun createExperienceNewProducts(context: Context): View {
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = createLayoutParams().also {
            it.setMargins(0, SizeHelper.size20, 0, 0)
        }
        linearLayout.gravity = Gravity.CENTER_HORIZONTAL
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

        linearLayout.addView(createText(
            context,
            createLayoutParams(SizeHelper.size12, SizeHelper.size16, 0, 0),
            null,
            Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"),
            ContextCompat.getColor(context, R.color.colorSecondary),
            18f,
            1
        ).also {
            it.text = "Trải nghiệm sản phẩm mới"
        })

        linearLayout.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams(SizeHelper.size4, SizeHelper.size16, 0, 0)
        })

        linearLayout.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams(0, SizeHelper.size12, 0, 0)
        })

        linearLayout.addView(createText(
            context,
            createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, SizeHelper.size16, 0, SizeHelper.size16)
            },
            null,
            Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"),
            ContextCompat.getColor(context, R.color.colorPrimary),
            16f,
            1
        ).also {
            it.text = "Xem thêm"
            it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next_14dp, 0)
            it.compoundDrawablePadding = SizeHelper.size4
        })

        return linearLayout
    }

    fun createHomeCategoryHorizontal(context: Context): AppCompatTextView {
        return createText(
            context,
            createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                SizeHelper.size8,
                0,
                0,
                0
            ).also { params ->
                params.gravity = Gravity.CENTER_VERTICAL
            },
            createStateListDrawable(
                Color.WHITE,
                ContextCompat.getColor(context, R.color.colorSecondText),
                ContextCompat.getColor(context, R.color.colorSecondText),
                ContextCompat.getColor(context, R.color.colorSecondText),
                SizeHelper.size1,
                SizeHelper.size16.toFloat()
            ),
            Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"),
            ContextCompat.getColor(context, R.color.colorSecondText),
            14f,
            1
        ).also {
            it.setPadding(SizeHelper.size12, SizeHelper.size6, SizeHelper.size12, SizeHelper.size6)
        }
    }

    fun createRecyclerViewWithTitleHolderV2(
        context: Context,
        recycledViewPool: RecyclerView.RecycledViewPool?
    ): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(Color.WHITE)
        layoutParent.addView(createTitleV2(context))

        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        recyclerView.clipToPadding = false
        recyclerView.layoutManager = CustomGridLayoutManager(context, 2)
        recyclerView.setRecycledViewPool(recycledViewPool)
        layoutParent.addView(recyclerView)
        return layoutParent
    }

    private fun createTitleV2(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParent.setPadding(
            SizeHelper.size12,
            SizeHelper.size12,
            SizeHelper.size12,
            SizeHelper.size10
        )
        layoutParent.orientation = LinearLayout.HORIZONTAL

        val tvTitle = AppCompatTextView(context)
        tvTitle.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        tvTitle.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
        tvTitle.includeFontPadding = false
        tvTitle.setPadding(0, 0, SizeHelper.size12, 0)
        layoutParent.addView(tvTitle)

        val tvViewMore = AppCompatTextView(context)
        tvViewMore.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvViewMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvViewMore.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        tvViewMore.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
        tvViewMore.includeFontPadding = false
        tvViewMore.setPadding(0, 0, 0, 0)
        tvViewMore.setText(R.string.xem_tat_ca)
        layoutParent.addView(tvViewMore)

        return layoutParent
    }

    fun createTopTendency(context: Context): View {
        return LinearLayout(context).also { parent ->
            parent.addView(View(context).also {
                it.layoutParams =
                    createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size10)
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGray))
            })

            parent.addView(LinearLayout(context).also {
                it.layoutParams = createLayoutParams(SizeHelper.size12, 0, 0, 0)
                it.orientation = LinearLayout.VERTICAL
                it.gravity = Gravity.CENTER_HORIZONTAL
                it.background = ContextCompat.getDrawable(context, R.drawable.bg_top_tendency)

                it.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams =
                        createLayoutParams(SizeHelper.size12, SizeHelper.size16, 0, 0)
                    text.textSize = 18f
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    text.text = "Top xu hướng"
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.includeFontPadding = false
                })

                it.addView(RecyclerView(context).also { rcv ->
                    rcv.layoutParams = createLayoutParams(0, SizeHelper.size12, 0, 0)
                })

                it.addView(RecyclerView(context).also { rcv ->
                    rcv.layoutParams =
                        createLayoutParams(0, SizeHelper.size20, 0, SizeHelper.size16)
                })
            })
        }
    }

    fun createItemProductTopTendency(context: Context): View {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams(
                SizeHelper.size220,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                SizeHelper.size12,
                0,
                0,
                0
            )
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.background =
                ContextCompat.getDrawable(context, R.drawable.bg_yellow_corner_4)

            layoutParent.addView(AppCompatImageView(context).also { img ->
                img.layoutParams =
                    createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size120)
            })

            layoutParent.addView(ConstraintLayout(context).also { constraintLayout ->
                constraintLayout.id = R.id.constraintLayout
                constraintLayout.layoutParams = createLayoutParams(
                    SizeHelper.size8,
                    SizeHelper.size9,
                    SizeHelper.size8,
                    SizeHelper.size9
                )
                val img = CircleImageView(context)
                img.layoutParams = createLayoutParams(SizeHelper.size36, SizeHelper.size36)
                img.id = R.id.imgGift

                val tvName = AppCompatTextView(context)
                tvName.id = R.id.textView
                tvName.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also { params ->
                            params.setMargins(SizeHelper.size8, 0, 0, 0)
                        }
                tvName.textSize = 14f
                tvName.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                tvName.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                tvName.includeFontPadding = false
                tvName.ellipsize = TextUtils.TruncateAt.END
                tvName.isSingleLine = true

                val rate = AppCompatTextView(context)
                rate.id = R.id.textView1
                rate.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).also { params ->
                    params.setMargins(0, SizeHelper.size6, 0, 0)
                }
                rate.textSize = 12f
                rate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                rate.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                rate.includeFontPadding = false
                rate.ellipsize = TextUtils.TruncateAt.END
                rate.isSingleLine = true
                rate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_star_full_12px, 0)
                rate.compoundDrawablePadding = SizeHelper.size6
                rate.gravity = Gravity.CENTER_VERTICAL

                val text = AppCompatTextView(context)
                text.id = R.id.textView2
                text.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also { params ->
                            params.setMargins(SizeHelper.size10, 0, 0, 0)
                        }
                text.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_corners_14_light_blue_no_solid)
                text.textSize = 12f
                text.setPadding(
                    SizeHelper.size6,
                    SizeHelper.size2,
                    SizeHelper.size6,
                    SizeHelper.size2
                )
                text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                text.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                text.includeFontPadding = false
                text.ellipsize = TextUtils.TruncateAt.END
                text.isSingleLine = true
                text.gravity = Gravity.CENTER_VERTICAL

                constraintLayout.addView(img)
                constraintLayout.addView(tvName)
                constraintLayout.addView(rate)
                constraintLayout.addView(text)

                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)

                constraintSet.connect(
                    img.id,
                    ConstraintSet.START,
                    constraintLayout.id,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    img.id,
                    ConstraintSet.TOP,
                    constraintLayout.id,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    img.id,
                    ConstraintSet.BOTTOM,
                    constraintLayout.id,
                    ConstraintSet.BOTTOM
                )

                constraintSet.connect(tvName.id, ConstraintSet.START, img.id, ConstraintSet.END)
                constraintSet.connect(tvName.id, ConstraintSet.TOP, img.id, ConstraintSet.TOP)
                constraintSet.connect(
                    tvName.id,
                    ConstraintSet.END,
                    constraintLayout.id,
                    ConstraintSet.END
                )
                constraintSet.connect(tvName.id, ConstraintSet.BOTTOM, rate.id, ConstraintSet.TOP)

                constraintSet.connect(rate.id, ConstraintSet.TOP, tvName.id, ConstraintSet.BOTTOM)
                constraintSet.connect(rate.id, ConstraintSet.BOTTOM, img.id, ConstraintSet.BOTTOM)
                constraintSet.connect(rate.id, ConstraintSet.START, tvName.id, ConstraintSet.START)
                constraintSet.connect(rate.id, ConstraintSet.END, text.id, ConstraintSet.START)

                constraintSet.connect(text.id, ConstraintSet.START, rate.id, ConstraintSet.END)
                constraintSet.connect(text.id, ConstraintSet.TOP, rate.id, ConstraintSet.TOP)
                constraintSet.connect(text.id, ConstraintSet.BOTTOM, rate.id, ConstraintSet.BOTTOM)

                constraintSet.applyTo(constraintLayout)
            })
        }
    }

    fun createItemBusinessTopTendency(context: Context): View {
        return LinearLayout(context).also {
            it.layoutParams = createLayoutParams(
                SizeHelper.size220,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                SizeHelper.size12,
                0,
                0,
                0
            )
            it.orientation = LinearLayout.VERTICAL
            it.background = ContextCompat.getDrawable(context, R.drawable.bg_yellow_corner_4)

            it.addView(AppCompatImageView(context).also { img ->
                img.layoutParams =
                    createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size120)
            })

            it.addView(ConstraintLayout(context).also { constraintLayout ->
                constraintLayout.id = R.id.constraintLayout
                constraintLayout.layoutParams = createLayoutParams(
                    SizeHelper.size8,
                    SizeHelper.size9,
                    SizeHelper.size8,
                    SizeHelper.size9
                )
                val img = CircleImageView(context)
                img.layoutParams = createLayoutParams(SizeHelper.size36, SizeHelper.size36)
                img.id = R.id.imgGift

                constraintLayout.addView(img)

                val tvName = AppCompatTextView(context)
                tvName.id = R.id.textView
                tvName.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also { params ->
                            params.setMargins(SizeHelper.size8, 0, 0, 0)
                        }
                tvName.textSize = 14f
                tvName.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                tvName.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                tvName.includeFontPadding = false
                tvName.ellipsize = TextUtils.TruncateAt.END
                tvName.isSingleLine = true

                constraintLayout.addView(tvName)

                val follow = AppCompatTextView(context)
                follow.id = R.id.textView1
                follow.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also { params ->
                            params.setMargins(0, SizeHelper.size6, 0, 0)
                        }
                follow.textSize = 12f
                follow.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                follow.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                follow.includeFontPadding = false
                follow.ellipsize = TextUtils.TruncateAt.END
                follow.isSingleLine = true
                follow.gravity = Gravity.CENTER_VERTICAL

                constraintLayout.addView(follow)

                val text = AppCompatTextView(context)
                text.id = R.id.textView2
                text.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also { params ->
                            params.setMargins(SizeHelper.size10, 0, 0, 0)
                        }
                text.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_solid)
                text.textSize = 12f
                text.setPadding(
                    SizeHelper.size8,
                    SizeHelper.size4,
                    SizeHelper.size8,
                    SizeHelper.size4
                )
                text.setTextColor(ContextCompat.getColor(context, R.color.white))
                text.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                text.includeFontPadding = false
                text.ellipsize = TextUtils.TruncateAt.END
                text.text = "Theo dõi"
                text.isSingleLine = true
                text.gravity = Gravity.CENTER

                constraintLayout.addView(text)

                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)

                constraintSet.connect(
                    img.id,
                    ConstraintSet.START,
                    constraintLayout.id,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    img.id,
                    ConstraintSet.TOP,
                    constraintLayout.id,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    img.id,
                    ConstraintSet.BOTTOM,
                    constraintLayout.id,
                    ConstraintSet.BOTTOM
                )
                constraintSet.connect(img.id, ConstraintSet.END, tvName.id, ConstraintSet.START)

                constraintSet.connect(tvName.id, ConstraintSet.START, img.id, ConstraintSet.END)
                constraintSet.connect(tvName.id, ConstraintSet.TOP, img.id, ConstraintSet.TOP)
                constraintSet.connect(tvName.id, ConstraintSet.END, text.id, ConstraintSet.START)
                constraintSet.connect(tvName.id, ConstraintSet.BOTTOM, follow.id, ConstraintSet.TOP)

                constraintSet.connect(follow.id, ConstraintSet.TOP, tvName.id, ConstraintSet.BOTTOM)
                constraintSet.connect(follow.id, ConstraintSet.BOTTOM, img.id, ConstraintSet.BOTTOM)
                constraintSet.connect(
                    follow.id,
                    ConstraintSet.START,
                    tvName.id,
                    ConstraintSet.START
                )
                constraintSet.connect(follow.id, ConstraintSet.END, tvName.id, ConstraintSet.END)

                constraintSet.connect(text.id, ConstraintSet.START, tvName.id, ConstraintSet.END)
                constraintSet.connect(text.id, ConstraintSet.TOP, tvName.id, ConstraintSet.TOP)
                constraintSet.connect(
                    text.id,
                    ConstraintSet.BOTTOM,
                    follow.id,
                    ConstraintSet.BOTTOM
                )
                constraintSet.connect(
                    text.id,
                    ConstraintSet.END,
                    constraintLayout.id,
                    ConstraintSet.END
                )

                constraintSet.applyTo(constraintLayout)
            })
        }
    }

    fun makeTextViewResizable(
        tv: TextView,
        maxLine: Int,
        expandText: String,
        viewMore: Boolean,
        color: String
    ) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                    addClickablePartTextViewResizable(
                        Html.fromHtml(tv.text.toString()),
                        tv,
                        lineEndIndex,
                        expandText,
                        viewMore,
                        color
                    ), TextView.BufferType.SPANNABLE
                )
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned,
        tv: TextView,
        maxLine: Int,
        spanableText: String,
        viewMore: Boolean,
        color: String
    ): SpannableStringBuilder? {
        val str: String = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false, color) {
                override fun onClick(widget: View) {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "Thu gọn", false, color)
                    } else {
                        makeTextViewResizable(tv, 3, "Xem thêm", true, color)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }


    fun createRecyclerViewSuggestPage(context: Context): RecyclerView {
        val rcv = RecyclerView(context)
        rcv.layoutParams = createLayoutParams().also {
            it.setMargins(SizeHelper.size22, SizeHelper.size12, SizeHelper.size22, 0)
        }
        rcv.clipToPadding = false
        return rcv
    }

    fun createFlashSaleViewHolder(context: Context): LinearLayout {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams()
        layoutParent.orientation = LinearLayout.VERTICAL

        layoutParent.addView(View(context).also {
            it.layoutParams =
                createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size10)
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGray))
        })

        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also { layoutParams ->
                layoutParams.marginStart = SizeHelper.size12
            }
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL
            //img flash sale
            it.addView(AppCompatImageView(context).also {
                it.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                it.background = ContextCompat.getDrawable(context, R.drawable.img_flash_sale)
            })

            //tv giờ
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    SizeHelper.dpToPx(23),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.leftMargin = SizeHelper.size12
                }
                it.background = ContextCompat.getDrawable(context, R.drawable.bg_black_corners_2)
                it.setTextColor(ContextCompat.getColor(context, R.color.white))
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.gravity = Gravity.CENTER
                it.includeFontPadding = false
                it.textSize = 14f
            })

            //tv phút
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    SizeHelper.dpToPx(23),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.leftMargin = SizeHelper.size4
                }
                it.background = ContextCompat.getDrawable(context, R.drawable.bg_black_corners_2)
                it.setTextColor(ContextCompat.getColor(context, R.color.white))
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.gravity = Gravity.CENTER
                it.includeFontPadding = false
                it.textSize = 14f
            })

            //tv giây
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    SizeHelper.dpToPx(23),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.leftMargin = SizeHelper.size4
                }
                it.background = ContextCompat.getDrawable(context, R.drawable.bg_black_corners_2)
                it.setTextColor(ContextCompat.getColor(context, R.color.white))
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.gravity = Gravity.CENTER
                it.includeFontPadding = false
                it.textSize = 14f
            })

            //tv xem tất cả
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.rightMargin = SizeHelper.size12
                }
                it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                it.gravity = Gravity.END
                it.textSize = 14f
                it.includeFontPadding = false
                it.text = context.getString(R.string.xem_tat_ca)
            })
        })

        layoutParent.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams().also { layoutParams ->
                layoutParams.marginStart = SizeHelper.size12
            }
        })

        return layoutParent
    }

    fun createListFlashSale(context: Context): ConstraintLayout {
        val layoutParent = ConstraintLayout(context)
        layoutParent.layoutParams = ConstraintLayout.LayoutParams(
            SizeHelper.dpToPx(287),
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(0, SizeHelper.size10, SizeHelper.size20, 0)
        }
        layoutParent.id = R.id.constraintLayout

        //img Product
        val imgProduct = AppCompatImageView(context).also {
            it.layoutParams =
                ConstraintLayout.LayoutParams(SizeHelper.dpToPx(77), SizeHelper.dpToPx(70))
            it.id = R.id.imgProduct
        }
        layoutParent.addView(imgProduct)

        //tv số lượng bán
        val tvSaleCount = AppCompatTextView(context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size6, 0, SizeHelper.size6, SizeHelper.size4)
            }
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.background = ContextCompat.getDrawable(context, R.drawable.bg_red_corners_7)
            it.setPadding(SizeHelper.size9, SizeHelper.size1, SizeHelper.size9, SizeHelper.size1)
            it.setTextColor(ContextCompat.getColor(context, R.color.white))
            it.textSize = 10f
            it.includeFontPadding = false
            it.includeFontPadding = false
            it.id = R.id.tvSaleCount

        }
        layoutParent.addView(tvSaleCount)

        //tv name Product
        val tvNameProduct = AppCompatTextView(context).also {
            it.layoutParams =
                ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, 0, 0)
                }
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.includeFontPadding = false
            it.ellipsize = TextUtils.TruncateAt.END
            it.isSingleLine = true
            it.includeFontPadding = false
            it.setTextColor(ContextCompat.getColor(context, R.color.darkGray1))
            it.textSize = 14f
            it.id = R.id.tvName

        }
        layoutParent.addView(tvNameProduct)

        //tv special price
        val tvSpecialPrice = AppCompatTextView(context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, SizeHelper.size4, 0, 0)
            }
            it.setPadding(0, 0, SizeHelper.size8, 0)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.includeFontPadding = false
            it.ellipsize = TextUtils.TruncateAt.END
            it.isSingleLine = true
            it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
            it.textSize = 14f
            it.includeFontPadding = false
            it.id = R.id.tv_special_price

        }
        layoutParent.addView(tvSpecialPrice)

        //tv  price
        val tvPrice = AppCompatTextView(context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, SizeHelper.size4, 0, 0)
            }
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.includeFontPadding = false
            it.ellipsize = TextUtils.TruncateAt.END
            it.isSingleLine = true
            it.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            it.textSize = 14f
            it.includeFontPadding = false
            it.id = R.id.tv_price

        }
        layoutParent.addView(tvPrice)

        //tv verified
        val tvVerified = AppCompatTextView(context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, SizeHelper.size5, 0, 0)
            }
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.includeFontPadding = false
            it.setTextColor(ContextCompat.getColor(context, R.color.green_text_verified_product))
            it.textSize = 12f
            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified_18px, 0, 0, 0)
            it.compoundDrawablePadding = SizeHelper.size2
            it.includeFontPadding = false
            it.id = R.id.tvVerified
            it.gravity = Gravity.CENTER_VERTICAL
            it.text = context.getString(R.string.verified)

        }
        layoutParent.addView(tvVerified)

        //view line
        val viewLine = View(context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                SizeHelper.size0_5
            ).also {
                it.topMargin = SizeHelper.size10
            }
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray6))
            it.id = R.id.view
        }
        layoutParent.addView(viewLine)

        val constraintSet = ConstraintSet()
        constraintSet.clone(layoutParent)

        //img Product
        constraintSet.connect(
            imgProduct.id,
            ConstraintSet.START,
            layoutParent.id,
            ConstraintSet.START
        )
        constraintSet.connect(imgProduct.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)

        //tvCountSale
        constraintSet.connect(
            tvSaleCount.id,
            ConstraintSet.START,
            imgProduct.id,
            ConstraintSet.START
        )
        constraintSet.connect(tvSaleCount.id, ConstraintSet.END, imgProduct.id, ConstraintSet.END)
        constraintSet.connect(
            tvSaleCount.id,
            ConstraintSet.BOTTOM,
            imgProduct.id,
            ConstraintSet.BOTTOM
        )

        //tv Name
        constraintSet.connect(
            tvNameProduct.id,
            ConstraintSet.START,
            imgProduct.id,
            ConstraintSet.END
        )
        constraintSet.connect(tvNameProduct.id, ConstraintSet.TOP, imgProduct.id, ConstraintSet.TOP)
        constraintSet.connect(
            tvNameProduct.id,
            ConstraintSet.END,
            layoutParent.id,
            ConstraintSet.END
        )

        //tv Special Price
        constraintSet.connect(
            tvSpecialPrice.id,
            ConstraintSet.START,
            tvNameProduct.id,
            ConstraintSet.START
        )
        constraintSet.connect(
            tvSpecialPrice.id,
            ConstraintSet.TOP,
            tvNameProduct.id,
            ConstraintSet.BOTTOM
        )

        //TV Price
        constraintSet.connect(tvPrice.id, ConstraintSet.START, tvSpecialPrice.id, ConstraintSet.END)
        constraintSet.connect(tvPrice.id, ConstraintSet.TOP, tvNameProduct.id, ConstraintSet.BOTTOM)

        //TV Verified
        constraintSet.connect(
            tvVerified.id,
            ConstraintSet.START,
            tvNameProduct.id,
            ConstraintSet.START
        )
        constraintSet.connect(tvVerified.id, ConstraintSet.TOP, tvPrice.id, ConstraintSet.BOTTOM)

        //view line
        constraintSet.connect(
            viewLine.id,
            ConstraintSet.START,
            layoutParent.id,
            ConstraintSet.START
        )
        constraintSet.connect(viewLine.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)
        constraintSet.connect(viewLine.id, ConstraintSet.TOP, imgProduct.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(layoutParent)

        return layoutParent
    }


    fun createNoContributionHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams().also {
            it.topMargin = SizeHelper.size10
        }
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        layoutParent.gravity = Gravity.CENTER_HORIZONTAL

        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.topMargin = SizeHelper.size12
            }
            it.gravity = Gravity.CENTER_HORIZONTAL
            it.text = context.getString(R.string.hien_tai_san_pham_nay_chua_co_day_du_thong_tin)
            it.setTypeface(Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf"))
            it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
            it.textSize = 14f
        })
        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams(
                SizeHelper.dpToPx(300),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, SizeHelper.size16, 0, SizeHelper.size10)
            }
            it.text = context.getString(R.string.dong_gop_thong_tin_ngay)
            it.gravity = Gravity.CENTER
            it.setTextColor(ContextCompat.getColor(context, R.color.white))
            it.setTypeface(Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"))
            it.background =
                ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_solid)
            it.setPadding(0, SizeHelper.size8, 0, SizeHelper.size8)
            it.textSize = 16f
        })
        return layoutParent
    }

    fun createContributionHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams().also {
            it.topMargin = SizeHelper.size10
        }
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        layoutParent.setPadding(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)

        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams()
            it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
            it.text = context.getString(R.string.thong_tin_duoc_dong_gop_boi)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            it.includeFontPadding = false
        })

        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also {
                it.topMargin = SizeHelper.size6
            }
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL

            //avatar
            it.addView(RelativeLayout(context).also { layoutImage ->
                layoutImage.layoutParams = createLayoutParams(SizeHelper.size40, SizeHelper.size40)

                val imgLogo = CircleImageView(context)
                imgLogo.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                imgLogo.scaleType = ImageView.ScaleType.CENTER_CROP
                imgLogo.id = R.id.img_logo
                imgLogo.borderColor = ContextCompat.getColor(context, R.color.gray)
                imgLogo.borderWidth = SizeHelper.size0_5
                imgLogo.setPadding(0, SizeHelper.size8, 0, 0)
                layoutImage.addView(imgLogo)

                val tvVerified = AppCompatTextView(context).also {
                    it.layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            it.addRule(RelativeLayout.ALIGN_END, R.id.img_logo)
                        } else {
                            it.addRule(RelativeLayout.ALIGN_RIGHT, R.id.img_logo)
                        }
                        it.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.img_logo)
                    }
                    it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
                }
                layoutImage.addView(tvVerified)

                val imgRank = AppCompatImageView(context).also {
                    it.layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also { params ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            params.addRule(RelativeLayout.ALIGN_END, R.id.img_logo)
                        } else {
                            params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.img_logo)
                        }
                        params.addRule(RelativeLayout.ALIGN_TOP, R.id.img_logo)
                    }
                }
                layoutImage.addView(imgRank)
            })

            //name user
            it.addView(MiddleMultilineTextView(context).also {
                it.layoutParams =
                    createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                        it.leftMargin = SizeHelper.size8
                    }
                it.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                it.text = context.getString(R.string.thong_tin_duoc_dong_gop_boi)
                it.maxLines=2
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                it.includeFontPadding = false
            })

            //vote correct
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                it.gravity = Gravity.CENTER_HORIZONTAL
                it.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                it.includeFontPadding = false
                it.lineHeight = SizeHelper.size20
                it.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_contribute_correct_fc_30px,
                    0,
                    0
                )
            })

            //vote incorrect
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.leftMargin = SizeHelper.size20
                }
                it.setTextColor(ContextCompat.getColor(context, R.color.colorAccentYellow))
                it.gravity = Gravity.CENTER_HORIZONTAL
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                it.includeFontPadding = false
                it.lineHeight = SizeHelper.size20
                it.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_contribute_incorrect_fc_30px,
                    0,
                    0
                )
            })
        })

        //btn chinh sua dong gop
        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams(
                SizeHelper.dpToPx(170),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.topMargin = SizeHelper.size8
            }
            it.setPadding(0, SizeHelper.size4, 0, SizeHelper.size4)
            it.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            it.gravity = Gravity.CENTER
            it.text = context.getString(R.string.chinh_sua_dong_gop)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.includeFontPadding = false
            it.background =
                ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_no_solid)
        })

        //view line
        layoutParent.addView(View(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SizeHelper.dpToPx(1)
            ).also {
                it.topMargin = SizeHelper.size10
            }
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
        })

        // list avatar
        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also {
                it.topMargin = SizeHelper.size10
            }
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL

            it.addView(ListAvatar(context).also {
                it.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })

            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.leftMargin = SizeHelper.size8
                }
                it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                it.includeFontPadding = false
            })

        })

        //tv all
        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.topMargin = SizeHelper.size6
                it.bottomMargin = SizeHelper.dpToPx(15)
            }
            it.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            it.gravity = Gravity.CENTER_VERTICAL
            it.text = context.getString(R.string.xem_tat_ca)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.includeFontPadding = false
            it.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_right_light_blue_24dp,
                0
            )
        })

        return layoutParent

    }

    fun createFormSubmitReview(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.gravity = Gravity.CENTER_HORIZONTAL
        layoutParent.setPadding(0, SizeHelper.size10, 0, 0)
        layoutParent.setBackgroundColor(Color.WHITE)

        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)
            }
            it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
            it.gravity = Gravity.CENTER_HORIZONTAL
            it.text = context.getString(R.string.danh_gia_cua_ban_se_rat_huu_ich_cho_cong_dong)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            it.includeFontPadding = false
        })

        //view line
        layoutParent.addView(View(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SizeHelper.dpToPx(3)
            ).also {
                it.setMargins(SizeHelper.size12, SizeHelper.size8, SizeHelper.size12, 0)
            }
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray6))
        })

        //rcv rating
        layoutParent.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(
                    SizeHelper.size12,
                    SizeHelper.size5,
                    SizeHelper.size12,
                    SizeHelper.size5
                )
            }
        })

        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)
            }
            it.orientation = LinearLayout.VERTICAL
            it.background = ContextCompat.getDrawable(context, R.drawable.bg_border_gray_4dp_shop)
            it.setPadding(SizeHelper.size10, 0, SizeHelper.size10, SizeHelper.size4)

            //edit text
            it.addView(AppCompatEditText(context).also { edt ->
                edt.layoutParams = createLayoutParams()
                edt.hint = context.getString(R.string.viet_danh_gia)
                edt.setLines(4)
                edt.setBackgroundColor(Color.TRANSPARENT)
                edt.gravity = Gravity.TOP
                edt.setTypeface(Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf"))
                edt.setHintTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                edt.setTextColor(ContextCompat.getColor(context, R.color.colorNormalText))
                edt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            })

            //line
            it.addView(View(context).also { line ->
                line.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    SizeHelper.dpToPx(1)
                ).also {
                    it.topMargin = SizeHelper.size8
                }
                line.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
            })

            //camera
            it.addView(AppCompatImageView(context).also { imgCamera ->
                imgCamera.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.topMargin = SizeHelper.size4
                }
                imgCamera.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_camera_off_24px)
            })

        })

        //rcv list ảnh
        layoutParent.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
            }
        })

        // gửi đánhh giá
        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(
                    SizeHelper.size12,
                    SizeHelper.size10,
                    SizeHelper.size10,
                    SizeHelper.size10
                )

            }
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL

            it.addView(CircleImageView(context).also {
                it.layoutParams = createLayoutParams(SizeHelper.dpToPx(30), SizeHelper.dpToPx(30))
                it.borderWidth = SizeHelper.size0_5
                it.borderColor = ContextCompat.getColor(context, R.color.gray)
            })
            it.addView(AppCompatImageView(context).also { imgCamera ->
                imgCamera.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imgCamera.setImageResource(R.drawable.ic_arrow_down_blue_24px)
            })
            it.addView(AppCompatTextView(context).also {
                it.layoutParams = createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                it.setTextColor(ContextCompat.getColor(context, R.color.white))
                it.gravity = Gravity.CENTER
                it.setPadding(0, SizeHelper.size8, 0, SizeHelper.size8)
                it.text = context.getString(R.string.dang_danh_gia)
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                it.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_solid)
            })
        })

        return layoutParent
    }


    fun createItemListRatingStar36dp(parent: ViewGroup): View {
        val layoutParent = LinearLayout(parent.context)
        layoutParent.layoutParams = createLayoutParams()
            .also {
                it.topMargin = SizeHelper.size5
                it.bottomMargin = SizeHelper.size5
            }
        layoutParent.orientation = LinearLayout.HORIZONTAL
        layoutParent.gravity = Gravity.CENTER_VERTICAL

        layoutParent.addView(AppCompatTextView(parent.context).also {
            it.layoutParams = createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            it.gravity = Gravity.CENTER_VERTICAL
            it.setTextColor(ContextCompat.getColor(parent.context, R.color.colorNormalText))
            it.typeface = Typeface.createFromAsset(parent.context.assets, "font/barlow_medium.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_blue_12px, 0, 0, 0)
            it.compoundDrawablePadding = SizeHelper.size2
            it.includeFontPadding = false
        })

        val star = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_rating_36dp, parent, false)

        layoutParent.addView(star)

        return layoutParent
    }

    fun createItemListReviewHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams().also {
            it.topMargin = SizeHelper.size10
        }
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(Color.WHITE)

        layoutParent.addView(LinearLayout(context).also { layoutHeader ->
            layoutHeader.layoutParams = createLayoutParams()
            layoutHeader.orientation = LinearLayout.HORIZONTAL
            layoutHeader.setPadding(SizeHelper.size12, 0, SizeHelper.size12, 0)

            //avatar user
            layoutHeader.addView(
                AvatarUserComponent(context, SizeHelper.size40, SizeHelper.size16).also {
                    it.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                })

            //layout name- time
            layoutHeader.addView(LinearLayout(context).also {
                it.layoutParams =
                    createLayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).also {
                        it.leftMargin = SizeHelper.size8
                        it.topMargin = SizeHelper.size8
                    }
                it.orientation = LinearLayout.VERTICAL

                //tv Name
                it.addView(AppCompatTextView(context).also { tvName ->
                    tvName.layoutParams =
                        createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1f)
                    tvName.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    tvName.gravity = Gravity.TOP
                    tvName.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    tvName.isSingleLine = true
                    tvName.ellipsize = TextUtils.TruncateAt.END
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    tvName.includeFontPadding = false
                })

                //tv time
                it.addView(AppCompatTextView(context).also { tvTime ->
                    tvTime.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    tvTime.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                    it.gravity = Gravity.BOTTOM
                    tvTime.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    tvTime.includeFontPadding = false
                })
            })

            //icon like - comment
            layoutHeader.addView(AppCompatTextView(context).also {
                it.layoutParams =
                    createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.topMargin = SizeHelper.size8
                    }
                it.gravity = Gravity.CENTER
                it.setPadding(
                    SizeHelper.size4,
                    SizeHelper.size6,
                    SizeHelper.size6,
                    SizeHelper.size6
                )
                it.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondText
                    )
                )
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_12px, 0, 0, 0)
                it.compoundDrawablePadding = SizeHelper.size4
                it.background = ContextCompat.getDrawable(context, R.drawable.gray_stroke_corner_4)
                it.includeFontPadding = false
            })
            layoutHeader.addView(AppCompatTextView(context).also {
                it.layoutParams =
                    createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.leftMargin = SizeHelper.size10
                        it.topMargin = SizeHelper.size8
                    }
                it.gravity = Gravity.CENTER
                it.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondText
                    )
                )
                it.setPadding(
                    SizeHelper.size4,
                    SizeHelper.size6,
                    SizeHelper.size6,
                    SizeHelper.size6
                )
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_12px, 0, 0, 0)
                it.compoundDrawablePadding = SizeHelper.size4
                it.background = ContextCompat.getDrawable(context, R.drawable.gray_stroke_corner_4)
                it.includeFontPadding = false
            })
        })

        //rating star
        layoutParent.addView(RatingStarComponent(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size12, SizeHelper.size14, SizeHelper.size12, 0)
            }
        })

        //text review
        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, SizeHelper.size6, SizeHelper.size12, 0)
            }
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
            it.includeFontPadding = false
            it.maxLines = 3
        })

        //ẢNH
        layoutParent.addView(LayoutImageInPostComponent(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, SizeHelper.size6, SizeHelper.size12, 0)
            }
        })

        //line
        layoutParent.addView(View(context).also { line ->
            line.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SizeHelper.dpToPx(1)
            ).also {
                it.topMargin = SizeHelper.size10
            }
            line.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.gray
                )
            )
        })
        return layoutParent
    }

    fun createListQuestionProductHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(Color.WHITE)

        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
            }
            it.gravity = Gravity.BOTTOM
            it.orientation = LinearLayout.HORIZONTAL

            it.addView(AppCompatTextView(context).also { tvName ->
                tvName.layoutParams =
                    createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                tvName.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                tvName.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                tvName.isSingleLine = true
                tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                tvName.includeFontPadding = false
            })

            it.addView(AppCompatTextView(context).also { tvAll ->
                tvAll.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                tvAll.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                tvAll.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                tvAll.isSingleLine = true
                tvAll.text = context.getString(R.string.xem_tat_ca)
                tvAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                tvAll.includeFontPadding = false
            })
        })

        layoutParent.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams()
            it.layoutManager = LinearLayoutManager(context)
        })

        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams(
                SizeHelper.size12,
                SizeHelper.size16,
                SizeHelper.size12,
                SizeHelper.size10
            )
            it.setPadding(0, SizeHelper.size10, 0, SizeHelper.size10)
            it.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            it.typeface = createTypeface(context, R.font.barlow_semi_bold)
            it.isSingleLine = true
            it.gravity = Gravity.CENTER
            it.text = context.getString(R.string.dat_cau_hoi)
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            it.includeFontPadding = false
            it.background =
                ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_no_solid)
        })

        return layoutParent
    }

    fun createListReviewProductHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams(0, SizeHelper.size10, 0, 0)
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray6))

        layoutParent.addView(LinearLayout(context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
            }
            it.gravity = Gravity.BOTTOM
            it.orientation = LinearLayout.HORIZONTAL

            it.addView(AppCompatTextView(context).also { tvName ->
                tvName.layoutParams =
                    createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                tvName.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                tvName.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                tvName.isSingleLine = true
                tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                tvName.includeFontPadding = false
            })

            it.addView(AppCompatTextView(context).also { tvAll ->
                tvAll.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                tvAll.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                tvAll.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                tvAll.isSingleLine = true
                tvAll.text = context.getString(R.string.xem_tat_ca)
                tvAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                tvAll.includeFontPadding = false
            })
        })

        layoutParent.addView(RecyclerView(context).also {
            it.layoutParams = createLayoutParams().also {
                it.topMargin = SizeHelper.size10
            }
            it.layoutManager = LinearLayoutManager(context)
        })

        return layoutParent
    }

    fun createNotQuestionHolder(context: Context): View {
        val layoutParent = LinearLayout(context)
        layoutParent.layoutParams = createLayoutParams()
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.gravity = Gravity.CENTER_HORIZONTAL
        layoutParent.setBackgroundColor(Color.WHITE)

        layoutParent.addView(AppCompatImageView(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.background =
                ContextCompat.getDrawable(context, R.drawable.ic_img_not_question_product_203dp)
        })

        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams().also {
                it.topMargin = SizeHelper.size2
            }
            it.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorNormalText
                )
            )
            it.gravity = Gravity.CENTER_HORIZONTAL
            it.text = context.getString(R.string.ban_co_thac_mac_gi_ve_san_pham_khong)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.includeFontPadding = false
        })

        layoutParent.addView(AppCompatTextView(context).also {
            it.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.dpToPx(32), SizeHelper.size10, SizeHelper.dpToPx(32), 0)
            }
            it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
            it.gravity = Gravity.CENTER_HORIZONTAL
            it.text = context.getString(R.string.san_pham_nay_chua_co_cau_hoi_nao)
            it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.includeFontPadding = false
        })

        return layoutParent
    }

    fun createMyReviewHolder(parent: ViewGroup): View {
        val layoutParent = LinearLayout(parent.context)
        layoutParent.layoutParams = createLayoutParams().also {
            it.topMargin = SizeHelper.size10
        }
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.setBackgroundColor(Color.WHITE)
        layoutParent.setPadding(0, 0, 0, SizeHelper.size20)

        layoutParent.addView(LinearLayout(parent.context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, SizeHelper.size18, SizeHelper.size12, 0)
            }
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL

            it.addView(LinearLayout(parent.context).also { layoutHeader ->
                layoutHeader.layoutParams = createLayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                layoutHeader.orientation = LinearLayout.VERTICAL

                //tv đánh giá của bạn
                layoutHeader.addView(AppCompatTextView(parent.context).also {
                    it.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    it.setTextColor(
                        ContextCompat.getColor(
                            parent.context,
                            R.color.colorSecondText
                        )
                    )
                    it.text = parent.context.getString(R.string.danh_gia_cua_ban)
                    it.typeface =
                        Typeface.createFromAsset(parent.context.assets, "font/barlow_medium.ttf")
                    it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    it.includeFontPadding = false
                })

                //tv điểm đánh giá
                layoutHeader.addView(AppCompatTextView(parent.context).also {
                    it.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.topMargin = SizeHelper.size4
                    }
                    it.setTextColor(
                        ContextCompat.getColor(
                            parent.context,
                            R.color.colorPrimary
                        )
                    )
                    it.typeface =
                        Typeface.createFromAsset(parent.context.assets, "font/barlow_semi_bold.ttf")
                    it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    it.includeFontPadding = false
                })
            })

            //view star
            val viewStar = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_rating_36dp, parent, false)
            it.addView(viewStar)
        })

        //tv xem chi tiết
        val layoutTvXemChiTiet = LinearLayout(parent.context).also {
            it.layoutParams = createLayoutParams()
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL or Gravity.END

            it.addView(AppCompatTextView(parent.context).also {
                it.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.setMargins(
                        SizeHelper.size12,
                        SizeHelper.size8,
                        SizeHelper.size12,
                        SizeHelper.size10
                    )
                }
                it.setTextColor(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.colorPrimary
                    )
                )
                it.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                it.text = parent.context.getString(R.string.xem_chi_tiet)
                it.typeface =
                    Typeface.createFromAsset(parent.context.assets, "font/barlow_medium.ttf")
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                it.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_down_blue_24px,
                    0
                )
                it.includeFontPadding = false
            })
        }

        layoutParent.addView(layoutTvXemChiTiet)

        //view line
        layoutParent.addView(View(parent.context).also {
            it.layoutParams =
                createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size2).also {
                    it.setMargins(0, 0, 0, SizeHelper.size10)

                }
            it.setBackgroundColor(ContextCompat.getColor(parent.context, R.color.darkGray6))
        })

        //tv review
        layoutParent.addView(AppCompatTextView(parent.context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, SizeHelper.size10)
            }
            it.setTextColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorNormalText
                )
            )
            it.typeface = Typeface.createFromAsset(parent.context.assets, "font/barlow_medium.ttf")
            it.maxLines = 3
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            it.includeFontPadding = false
        })

        //ẢNH
        layoutParent.addView(LayoutImageInPostComponent(parent.context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, SizeHelper.size12)
            }
        })

        // đánh giá với vai trò khác
        val layoutPermission = LinearLayout(parent.context).also {
            it.layoutParams = createLayoutParams().also {
                it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)
            }
            it.orientation = LinearLayout.HORIZONTAL
            it.gravity = Gravity.CENTER_VERTICAL
        }
        layoutPermission.addView(CircleImageView(parent.context).also {
            it.layoutParams = createLayoutParams(SizeHelper.dpToPx(30), SizeHelper.dpToPx(30))
            it.borderWidth = SizeHelper.size0_5
            it.borderColor = ContextCompat.getColor(parent.context, R.color.gray)
        })

        layoutPermission.addView(AppCompatTextView(parent.context).also {
            it.layoutParams =
                createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                    it.setMargins(SizeHelper.size14, 0, 0, 0)
                }
            it.gravity = Gravity.CENTER
            it.setPadding(0, SizeHelper.size8, 0, SizeHelper.size8)
            it.setText(R.string.danh_gia_voi_vai_tro_khac)
            it.typeface = createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold)
            it.textSize = 16f
            it.setBackgroundResource(R.drawable.bg_corners_4_light_blue_no_solid)
            it.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary))
        })

        layoutParent.addView(layoutPermission)

        return layoutParent
    }


    fun createItemSocialNetwork(context: Context): View {
        return LinearLayout(context).also { layoutParams ->
            layoutParams.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size8, SizeHelper.size4, 0, 0)
            }
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            layoutParams.orientation = LinearLayout.HORIZONTAL
            layoutParams.background =
                ContextCompat.getDrawable(context, R.drawable.bg_corners_white_4_border_05)

            layoutParams.addView(AppCompatImageView(context).also { img ->
                img.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                img.setPadding(SizeHelper.size6, SizeHelper.size6, 0, SizeHelper.size6)
            })

            layoutParams.addView(AppCompatTextView(context).also { text ->
                text.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text.setPadding(SizeHelper.size8)
                text.setLineSpacing(4f, 0f)
                text.ellipsize = TextUtils.TruncateAt.END
                text.isSingleLine = true
                text.textSize = 12f
                text.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                text.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
            })
        }
    }

    fun createItemBrandPage(context: Context): View {
        return CardView(context).also { cardView ->
            cardView.layoutParams =
                createLayoutParams(SizeHelper.size90, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size12, 0, 0, SizeHelper.size5)
                }
            cardView.radius = SizeHelper.size4.toFloat()
            cardView.cardElevation = SizeHelper.size4.toFloat()
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

            cardView.addView(LinearLayout(context).also { layoutParams ->
                layoutParams.layoutParams = createLayoutParams()
                layoutParams.orientation = LinearLayout.VERTICAL
                layoutParams.gravity = Gravity.CENTER

                layoutParams.addView(CircleImageView(context).also { img ->
                    img.layoutParams =
                        createLayoutParams(SizeHelper.size50, SizeHelper.size50).also {
                            it.setMargins(
                                SizeHelper.size20,
                                SizeHelper.size12,
                                SizeHelper.size20,
                                0
                            )
                        }
                })

                layoutParams.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(SizeHelper.size7, 0, SizeHelper.size7, 0)
                    }
                    text.isSingleLine = true
                    text.ellipsize = TextUtils.TruncateAt.END
                    text.setTextColor(ContextCompat.getColor(context, R.color.black))
                    text.textSize = 14f
                    text.gravity = Gravity.CENTER
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.includeFontPadding = false
                })

                layoutParams.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(SizeHelper.size7, 0, SizeHelper.size7, SizeHelper.size12)
                    }
                    text.isSingleLine = true
                    text.ellipsize = TextUtils.TruncateAt.END
                    text.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                    text.textSize = 12f
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.includeFontPadding = false
                })
            })
        }
    }

    fun createItemCampaign(context: Context): View {
        return CardView(context).also { cardView ->
            cardView.layoutParams = createLayoutParams(
                SizeHelper.size280,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size12, 0, 0, SizeHelper.size20)
            }
            cardView.radius = SizeHelper.size4.toFloat()
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            cardView.cardElevation = SizeHelper.size4.toFloat()

            cardView.addView(LinearLayout(context).also { layoutParams ->
                layoutParams.layoutParams = createLayoutParams()
                layoutParams.orientation = LinearLayout.VERTICAL

                layoutParams.addView(AppCompatImageView(context).also {
                    it.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size120
                    )
                })

                layoutParams.addView(AppCompatTextView(context).also {
                    it.layoutParams = createLayoutParams().also { params ->
                        params.setMargins(
                            SizeHelper.size10,
                            SizeHelper.size10,
                            SizeHelper.size10,
                            0
                        )
                    }
                    it.textSize = 12f
                    it.isSingleLine = true
                    it.ellipsize = TextUtils.TruncateAt.END
                    it.includeFontPadding = false
                    it.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    it.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed))
                })

                layoutParams.addView(AppCompatTextView(context).also {
                    it.layoutParams = createLayoutParams().also { params ->
                        params.setMargins(SizeHelper.size10, SizeHelper.size6, SizeHelper.size10, 0)
                    }
                    it.textSize = 16f
                    it.isSingleLine = true
                    it.ellipsize = TextUtils.TruncateAt.END
                    it.includeFontPadding = false
                    it.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    it.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                })

                layoutParams.addView(AppCompatTextView(context).also {
                    it.layoutParams = createLayoutParams().also { params ->
                        params.setMargins(
                            SizeHelper.size10,
                            SizeHelper.size6,
                            SizeHelper.size10,
                            SizeHelper.size12
                        )
                    }
                    it.textSize = 12f
                    it.isSingleLine = true
                    it.ellipsize = TextUtils.TruncateAt.END
                    it.includeFontPadding = false
                    it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                })

                layoutParams.addView(LinearLayout(context).also {
                    it.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size40,
                        SizeHelper.size40,
                        SizeHelper.size12,
                        SizeHelper.size40,
                        SizeHelper.size16
                    )
                    it.orientation = LinearLayout.HORIZONTAL
                    it.gravity = Gravity.CENTER
                    it.setBackgroundResource(R.drawable.bg_corners_4_light_blue_solid)
                    it.addView(AppCompatTextView(context).also { text ->
                        text.layoutParams = createLayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text.setPadding(
                            SizeHelper.size6,
                            SizeHelper.size10,
                            SizeHelper.size6,
                            SizeHelper.size10
                        )
                        text.isSingleLine = true
                        text.ellipsize = TextUtils.TruncateAt.END
                        text.setTextColor(ContextCompat.getColor(context, R.color.white))
                        text.textSize = 16f
                        text.typeface =
                            Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                        text.includeFontPadding = false
                        text.text = "Tham gia"
                    })
                    it.addView(AppCompatImageView(context).also { img ->
                        img.layoutParams =
                            LinearLayout.LayoutParams(SizeHelper.size14, SizeHelper.size14)
                        img.setBackgroundResource(R.drawable.ic_star_white_14)
                    })
                })
            })
        }
    }

    fun createLayoutPageDetail(context: Context): View {
        return LinearLayout(context).also { layoutParams ->
            layoutParams.layoutParams = createLayoutParams().also {
                it.setMargins(0, SizeHelper.size10, 0, 0)
            }
            layoutParams.orientation = LinearLayout.VERTICAL
            layoutParams.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            //0
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, 0, 0)
                }
                params.text = "Giới thiệu"
                params.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                params.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                params.isSingleLine = true
                params.ellipsize = TextUtils.TruncateAt.END
                params.includeFontPadding = false
                params.textSize = 16f
            })


            //1
            layoutParams.addView(CardView(context).also { cardView ->
                cardView.layoutParams = createLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    SizeHelper.size120
                ).also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }

                cardView.radius = SizeHelper.size4.toFloat()
                cardView.cardElevation = 0F

                cardView.addView(MapView(context).also { params ->
                    params.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                })
            })


            //2
            layoutParams.addView(LinearLayout(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size16, SizeHelper.size12, 0)
                }
                params.orientation = LinearLayout.HORIZONTAL

                params.addView(AppCompatImageView(context).also { img ->
                    img.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    img.setImageResource(R.drawable.ic_location_black_14)
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams =
                        createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                            it.setMargins(SizeHelper.size10, 0, 0, 0)
                        }
                    text.textSize = 14f
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.includeFontPadding = false
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text.setPadding(SizeHelper.size10, 0, 0, 0)
                    text.textSize = 14f
                    text.text = "Xem chỉ đường"
                    text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.includeFontPadding = false
                })
            })

            //3
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.includeFontPadding = false
                params.isSingleLine = true
                params.gravity = Gravity.CENTER_VERTICAL
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size10
                params.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_phone_black_14,
                    0,
                    0,
                    0
                )
            })

            //4
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.includeFontPadding = false
                params.isSingleLine = true
                params.gravity = Gravity.CENTER_VERTICAL
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size10
                params.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_black_14, 0, 0, 0)
            })

            //5
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.includeFontPadding = false
                params.isSingleLine = true
                params.gravity = Gravity.CENTER_VERTICAL
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size10
                params.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_network_black_15,
                    0,
                    0,
                    0
                )
            })

            //6
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.includeFontPadding = false
                params.isSingleLine = true
                params.gravity = Gravity.CENTER_VERTICAL
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size10
                params.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_library_black_14,
                    0,
                    0,
                    0
                )
            })

            //7
            layoutParams.addView(LinearLayout(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.orientation = LinearLayout.HORIZONTAL

                params.addView(AppCompatImageView(context).also { img ->
                    img.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    img.setImageResource(R.drawable.ic_information_black_15)
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams =
                        createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                            it.setMargins(SizeHelper.size10, 0, 0, 0)
                        }
                    text.textSize = 14f
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.includeFontPadding = false
                    text.minLines = 1
                    text.maxLines = 3
                    text.gravity = Gravity.CENTER_VERTICAL
                    text.ellipsize = TextUtils.TruncateAt.END
                })
            })

            //8
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                params.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                params.includeFontPadding = false
                params.isSingleLine = true
                params.gravity = Gravity.CENTER_VERTICAL
                params.text = "Tổng quan về công ty"
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size10
                params.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_overview_page_black_15,
                    0,
                    0,
                    0
                )
            })

            //9
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also { params ->
                    params.setMargins(SizeHelper.size37, SizeHelper.size6, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.includeFontPadding = false
                params.minLines = 1
                params.maxLines = 5
                params.ellipsize = TextUtils.TruncateAt.END
                params.gravity = Gravity.CENTER_VERTICAL
            })

            //10
            layoutParams.addView(AppCompatTextView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                }
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                params.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                params.includeFontPadding = false
                params.isSingleLine = true
                params.gravity = Gravity.CENTER_VERTICAL
                params.text = "Giải thưởng"
                params.visibility = View.GONE
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size10
                params.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_prizes_page_15,
                    0,
                    0,
                    0
                )
            })

            //11
            layoutParams.addView(RecyclerView(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(0, 0, 0, SizeHelper.size9)
                }
            })

            //12
            layoutParams.addView(LinearLayout(context).also { params ->
                params.layoutParams = createLayoutParams().also {
                    it.setMargins(0, SizeHelper.size10, 0, 0)
                }
                params.orientation = LinearLayout.VERTICAL
                params.gravity = Gravity.CENTER

                params.addView(View(context).also { view ->
                    view.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size10
                    )
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray6))
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                    }
                    text.textSize = 16f
                    text.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                    text.isSingleLine = true
                    text.ellipsize = TextUtils.TruncateAt.END
                    text.includeFontPadding = false
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                })

                params.addView(AppCompatImageView(context).also { img ->
                    img.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size120
                    ).also {
                        it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
                    }
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(SizeHelper.size12, SizeHelper.size18, SizeHelper.size12, 0)
                    }
                    text.textSize = 18f
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.isSingleLine = true
                    text.ellipsize = TextUtils.TruncateAt.END
                    text.includeFontPadding = false
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(SizeHelper.size12, SizeHelper.size8, SizeHelper.size12, 0)
                    }
                    text.textSize = 14f
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.minLines = 1
                    text.maxLines = 3
                    text.ellipsize = TextUtils.TruncateAt.END
                    text.includeFontPadding = false
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                })

                params.addView(View(context).also { view ->
                    view.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size0_5
                    ).also {
                        it.setMargins(0, SizeHelper.size12, 0, 0)
                    }
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorSecondText
                        )
                    )
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.setMargins(0, SizeHelper.size12, 0, SizeHelper.size12)
                    }
                    text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    text.gravity = Gravity.CENTER_VERTICAL
                    text.text = "Đọc tiếp"
                    text.gravity = Gravity.CENTER
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    text.includeFontPadding = false
                    text.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_right_light_blue_24dp,
                        0
                    )
                    text.compoundDrawablePadding = SizeHelper.size3
                })
            })
        }
    }

    fun createItemInformationPage(context: Context): View {
        return LinearLayout(context).also { layoutParams ->
            layoutParams.layoutParams = createLayoutParams(
                SizeHelper.size259,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size12, 0, 0, 0)
            }
            layoutParams.orientation = LinearLayout.HORIZONTAL
            layoutParams.setBackgroundResource(R.drawable.bg_stroke_gray_corner_4)

            layoutParams.addView(AppCompatImageView(context).also { params ->
                params.layoutParams =
                    createLayoutParams(SizeHelper.size40, SizeHelper.size40).also {
                        it.setMargins(SizeHelper.size10, SizeHelper.size18, 0, SizeHelper.size18)
                    }
            })

            layoutParams.addView(LinearLayout(context).also { params ->
                params.layoutParams =
                    createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                        it.setMargins(SizeHelper.size10, 0, SizeHelper.size10, 0)
                    }
                params.orientation = LinearLayout.VERTICAL

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(0, SizeHelper.size10, 0, 0)
                    }
                    text.textSize = 14f
                    text.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    text.includeFontPadding = false
                    text.isSingleLine = true
                    text.ellipsize = TextUtils.TruncateAt.END
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(0, SizeHelper.size6, 0, SizeHelper.size10)
                    }
                    text.textSize = 14f
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.includeFontPadding = false
                    text.minLines = 1
                    text.maxLines = 2
                    text.ellipsize = TextUtils.TruncateAt.END
                })
            })
        }
    }

    fun createMessage(context: Context): LinearLayout {
        return LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = createLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.gravity = Gravity.CENTER
            layoutParent.setPadding(
                SizeHelper.size40,
                SizeHelper.size40,
                SizeHelper.size40,
                SizeHelper.size40
            )

            layoutParent.addView(AppCompatImageView(context).also {
                it.layoutParams = createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT)
                it.scaleType = ImageView.ScaleType.CENTER_INSIDE
            })

            layoutParent.addView(createText(
                context,
                createLayoutParams().also { params ->
                    params.topMargin = SizeHelper.size20
                },
                null,
                createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorNormalText),
                16f
            ).also {
                it.gravity = Gravity.CENTER_HORIZONTAL
            })

            layoutParent.addView(createText(
                context,
                createLayoutParams().also { params ->
                    params.topMargin = SizeHelper.size10
                },
                null,
                Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf"),
                ContextCompat.getColor(context, R.color.colorSecondText),
                14f
            ).also {
                it.gravity = Gravity.CENTER_HORIZONTAL
            })

            layoutParent.addView(createText(
                context,
                createLayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    SizeHelper.size44
                ).also { params ->
                    params.topMargin = SizeHelper.size16
                },
                createStateListDrawable(
                    Color.WHITE,
                    ContextCompat.getColor(context, R.color.lightGray),
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    SizeHelper.size1,
                    SizeHelper.size6.toFloat()
                ),
                Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"),
                ContextCompat.getColor(context, R.color.colorPrimary),
                16f
            ).also {
                it.gravity = Gravity.CENTER
                it.setPadding(SizeHelper.size16, 0, SizeHelper.size16, 0)
                it.setText(R.string.thu_lai)
            })
        }
    }


    fun createItemPrizePageHolder(
        context: Context,
        width: Int,
        height: Int,
        textSizeName: Float,
        textSizeDate: Float,
        left: Int
    ): View {
        return LinearLayout(context).also { layoutParams ->
            layoutParams.layoutParams = createLayoutParams().also {
                it.setMargins(0, SizeHelper.size10, 0, 0)
            }
            layoutParams.orientation = LinearLayout.HORIZONTAL

            layoutParams.addView(CircleImageView(context).also { params ->
                params.layoutParams = createLayoutParams(width, height).also {
                    it.setMargins(left, 0, 0, 0)
                }
            })

            layoutParams.addView(LinearLayout(context).also { params ->
                params.layoutParams =
                    createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).also {
                        it.setMargins(SizeHelper.size12, 0, SizeHelper.size12, 0)
                    }
                params.orientation = LinearLayout.VERTICAL

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams()
                    text.isSingleLine = true
                    text.includeFontPadding = false
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    text.textSize = textSizeName
                    text.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                    text.ellipsize = TextUtils.TruncateAt.END
                })

                params.addView(AppCompatTextView(context).also { text ->
                    text.layoutParams = createLayoutParams().also {
                        it.setMargins(0, SizeHelper.size6, 0, 0)
                    }
                    text.isSingleLine = true
                    text.includeFontPadding = false
                    text.typeface =
                        Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text.textSize = textSizeDate
                    text.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                    text.ellipsize = TextUtils.TruncateAt.END
                })

                params.addView(View(context).also { view ->
                    view.layoutParams = createLayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        SizeHelper.size1
                    ).also {
                        it.setMargins(0, SizeHelper.size14, 0, 0)
                    }
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
                })
            })
        }
    }

    fun createItemBrand(context: Context): View {
        return ConstraintLayout(context).also { layoutParams ->
            layoutParams.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
            }
            layoutParams.id = R.id.constraintLayout
            layoutParams.background =
                ContextCompat.getDrawable(context, R.drawable.bg_white_corners_4)
            layoutParams.setPadding(SizeHelper.size12)

            val circleImageView = CircleImageView(context).also { params ->
                params.layoutParams =
                    ConstraintLayout.LayoutParams(SizeHelper.size60, SizeHelper.size60)
                params.id = R.id.circleImageView
            }
            layoutParams.addView(circleImageView)

            val nameBrand = AppCompatTextView(context).also { params ->
                params.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also {
                            it.setMargins(SizeHelper.size8, 0, SizeHelper.size8, 0)
                        }
                params.id = R.id.txtName
                params.isSingleLine = true
                params.includeFontPadding = false
                params.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                params.ellipsize = TextUtils.TruncateAt.END
            }
            layoutParams.addView(nameBrand)

            val followCount = AppCompatTextView(context).also { params ->
                params.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also {
                            it.topMargin = SizeHelper.size4
                        }
                params.id = R.id.tvFollower
                params.isSingleLine = true
                params.includeFontPadding = false
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.textSize = 12f
                params.setTextColor(ContextCompat.getColor(context, R.color.colorSecondText))
                params.ellipsize = TextUtils.TruncateAt.END
            }
            layoutParams.addView(followCount)

            val peopleLike = AppCompatTextView(context).also { params ->
                params.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also {
                            it.topMargin = SizeHelper.size8
                        }
                params.id = R.id.textView
                params.isSingleLine = true
                params.includeFontPadding = false
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.textSize = 12f
                params.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                params.ellipsize = TextUtils.TruncateAt.END
                params.compoundDrawablePadding = SizeHelper.size4
                params.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_friend_gray_12,
                    0,
                    0,
                    0
                )
            }
            layoutParams.addView(peopleLike)

            val categoryBrand = AppCompatTextView(context).also { params ->
                params.layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also {
                            it.topMargin = SizeHelper.size4
                            it.leftMargin = SizeHelper.size16
                        }
                params.id = R.id.textView1
                params.isSingleLine = true
                params.includeFontPadding = false
                params.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                params.textSize = 12f
                params.setTextColor(ContextCompat.getColor(context, R.color.black_21_v2))
                params.ellipsize = TextUtils.TruncateAt.END
            }
            layoutParams.addView(categoryBrand)

            val btnFollow = AppCompatTextView(context).also { params ->
                params.layoutParams =
                    ConstraintLayout.LayoutParams(SizeHelper.size100, SizeHelper.size28).also {
                        it.setMargins(0, SizeHelper.size6, 0, 0)
                    }
                params.id = R.id.button
                params.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_solid)
                params.gravity = Gravity.CENTER
                params.includeFontPadding = false
                params.typeface =
                    Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                params.textSize = 14f
                params.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            layoutParams.addView(btnFollow)

            val constraintSet = ConstraintSet()
            constraintSet.clone(layoutParams)

            constraintSet.connect(
                circleImageView.id,
                ConstraintSet.START,
                layoutParams.id,
                ConstraintSet.START
            )
            constraintSet.connect(
                circleImageView.id,
                ConstraintSet.TOP,
                layoutParams.id,
                ConstraintSet.TOP
            )
            constraintSet.connect(
                circleImageView.id,
                ConstraintSet.END,
                nameBrand.id,
                ConstraintSet.START
            )

            constraintSet.connect(
                nameBrand.id,
                ConstraintSet.START,
                circleImageView.id,
                ConstraintSet.END
            )
            constraintSet.connect(
                nameBrand.id,
                ConstraintSet.TOP,
                circleImageView.id,
                ConstraintSet.TOP
            )
            constraintSet.connect(
                nameBrand.id,
                ConstraintSet.END,
                btnFollow.id,
                ConstraintSet.START
            )

            constraintSet.connect(
                followCount.id,
                ConstraintSet.TOP,
                nameBrand.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                followCount.id,
                ConstraintSet.START,
                nameBrand.id,
                ConstraintSet.START
            )
            constraintSet.connect(
                followCount.id,
                ConstraintSet.END,
                nameBrand.id,
                ConstraintSet.END
            )

            constraintSet.connect(peopleLike.id, ConstraintSet.END, btnFollow.id, ConstraintSet.END)
            constraintSet.connect(
                peopleLike.id,
                ConstraintSet.TOP,
                followCount.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                peopleLike.id,
                ConstraintSet.START,
                followCount.id,
                ConstraintSet.START
            )

            constraintSet.connect(
                categoryBrand.id,
                ConstraintSet.START,
                peopleLike.id,
                ConstraintSet.START
            )
            constraintSet.connect(
                categoryBrand.id,
                ConstraintSet.END,
                peopleLike.id,
                ConstraintSet.END
            )
            constraintSet.connect(
                categoryBrand.id,
                ConstraintSet.TOP,
                peopleLike.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                categoryBrand.id,
                ConstraintSet.BOTTOM,
                layoutParams.id,
                ConstraintSet.BOTTOM
            )

            constraintSet.connect(btnFollow.id, ConstraintSet.TOP, nameBrand.id, ConstraintSet.TOP)
            constraintSet.connect(
                btnFollow.id,
                ConstraintSet.START,
                nameBrand.id,
                ConstraintSet.END
            )
            constraintSet.connect(
                btnFollow.id,
                ConstraintSet.END,
                layoutParams.id,
                ConstraintSet.END
            )

            constraintSet.applyTo(layoutParams)
        }
    }

    private fun addClickablePartTextViewResizableColor(
        strSpanned: String,
        textColor: String,
        spanableText: String
    ): SpannableStringBuilder? {
        val ssb = SpannableStringBuilder(strSpanned)
        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(
                ForegroundColorSpan(Color.parseColor(textColor)),
                strSpanned.indexOf(spanableText),
                strSpanned.indexOf(spanableText) + spanableText.length,
                0
            )
            ssb.setSpan(
                StyleSpan(Typeface.BOLD),
                strSpanned.indexOf(spanableText),
                strSpanned.indexOf(spanableText) + spanableText.length,
                0
            )
        }
        return ssb
    }

    fun setExpandTextWithoutAction(
        textView: TextView,
        maxLine: Int,
        expandText: String,
        textColor: String? = null
    ) {
        if (textView.tag == null) {
            textView.tag = textView.text
        }

        textView.viewTreeObserver.apply {
            addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    textView.layout?.apply {
                        if (lineCount > maxLine) {
                            val lineEndIndex = getLineEnd(maxLine - 1)
                            val text = if (lineEndIndex - getLineStart(maxLine - 1) >= 50) {
                                (textView.text.subSequence(0, lineEndIndex - expandText.length * 2)
                                    .toString().trim() + "… " + expandText)
                            } else {
                                (textView.text.subSequence(0, lineEndIndex).toString()
                                    .trim() + "… " + expandText)
                            }
                            textView.text = text
                            textView.movementMethod = LinkMovementMethod.getInstance()
                            textView.setText(
                                addClickablePartTextViewResizableColor(
                                    text, textColor
                                        ?: "#3C5A99", expandText
                                ), TextView.BufferType.SPANNABLE
                            )
                        }
                    }
                }
            })
        }
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: String,
        spanableText: String
    ): SpannableStringBuilder? {
        val ssb = SpannableStringBuilder(strSpanned)
        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(
                null,
                strSpanned.indexOf(spanableText),
                strSpanned.indexOf(spanableText) + spanableText.length,
                0
            )
        }
        return ssb
    }

    init {
        ICheckApplication.getInstance().applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )
    }

    fun RecyclerView.setScrollSpeed(speed: Int = 12000) {
        onFlingListener = object : RecyclerView.OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                if (abs(velocityY) > speed) {
                    return if (velocityY > 0) {
                        fling(velocityX, speed)
                        true
                    } else {
                        fling(velocityX, -speed)
                        true
                    }
                }
                return false
            }
        }
    }

    fun View.delayTimeoutClick(timeOut: Long = 1000L) {
        this.isEnabled = false
        Handler().postDelayed({
            this.isEnabled = true
        }, timeOut)
    }

    fun View.onDelayClick(action: () -> Unit, timeOut: Long = 1000L) {
        this.setOnClickListener {
            this.isEnabled = false
            Handler().postDelayed({
                this.isEnabled = true
            }, timeOut)
            action()
        }
    }
}