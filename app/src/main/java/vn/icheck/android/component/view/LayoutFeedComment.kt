package vn.icheck.android.component.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.util.kotlin.WidgetUtils

class LayoutFeedComment : FrameLayout {

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView()
    }

    private fun setupView() {
        addView(CircleImageView(context).also {
            it.id = R.id.imgAvatar
            it.layoutParams = LayoutParams(SizeHelper.size24, SizeHelper.size24).also { params ->
                params.gravity = Gravity.CENTER_VERTICAL
            }
        })

        addView(ViewHelper.createText(context,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).also {
                    it.setMargins(SizeHelper.size32, 0, 0, 0)
                },
                ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size1, ContextCompat.getColor(context, R.color.colorLineView), SizeHelper.size4.toFloat()),
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                Constant.getSecondTextColor(context),
                14f
        ).also {
            it.setPadding(SizeHelper.size10, 0, SizeHelper.size74, 0)
            it.setText(R.string.viet_binh_luan)
        })

        addView(AppCompatTextView(context).also {
            it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also { params ->
                params.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                params.setMargins(0, 0, SizeHelper.size42, 0)
            }
            it.setBackgroundResource(ViewHelper.outValue.resourceId)
            it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_camera_off_24px, 0)
        })

        addView(AppCompatTextView(context).also {
            it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also { params ->
                params.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                params.setMargins(0, 0, SizeHelper.size8, 0)
            }
            it.setBackgroundResource(ViewHelper.outValue.resourceId)
            it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_imoji_24px, 0)
        })
    }

    fun setData(avatar: ICThumbnail) {
        WidgetUtils.loadImageUrl(getChildAt(0) as CircleImageView, avatar.medium, R.drawable.ic_circle_avatar_default)
    }

    fun setOnAvatarClick(listener: OnClickListener) {
        getChildAt(0).setOnClickListener(listener)
    }

    fun setOnCommentClick(listener: OnClickListener) {
        getChildAt(1).setOnClickListener(listener)
    }

    fun setOnCameraClick(listener: OnClickListener) {
        getChildAt(2).setOnClickListener(listener)
    }

    fun setOnSmileClick(listener: OnClickListener) {
        getChildAt(3).setOnClickListener(listener)
    }
}