package vn.icheck.android.component.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper

class LayoutFeedAction : ConstraintLayout {

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
        id = R.id.layoutAction

        val tvLike = ViewHelper.createText(context,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT),
                ViewHelper.outValue.resourceId,
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorSecondText),
                14f
        ).also {
            it.id = R.id.tvLike
            it.compoundDrawablePadding = SizeHelper.size8
            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_off_24dp, 0, 0, 0)
            it.setText(R.string.yeu_thich)
        }
        addView(tvLike)

        val tvComment = ViewHelper.createText(context,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT),
                ViewHelper.outValue.resourceId,
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorSecondText),
                14f
        ).also {
            it.id = R.id.tvComment
            it.compoundDrawablePadding = SizeHelper.size8
            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_24dp, 0, 0, 0)
            it.setText(R.string.binh_luan)
        }
        addView(tvComment)

        val tvShare = ViewHelper.createText(context,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT),
                ViewHelper.outValue.resourceId,
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorSecondText),
                14f
        ).also {
            it.id = R.id.tvShare
            it.compoundDrawablePadding = SizeHelper.size8
            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share_24px, 0, 0, 0)
            it.setText(R.string.chia_se)
        }
        addView(tvShare)

        val bannerSet = ConstraintSet()
        bannerSet.clone(this)

        bannerSet.connect(tvLike.id, ConstraintSet.START, id, ConstraintSet.START)
        bannerSet.connect(tvLike.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
        bannerSet.connect(tvLike.id, ConstraintSet.END, tvComment.id, ConstraintSet.START)

        bannerSet.connect(tvComment.id, ConstraintSet.START, tvLike.id, ConstraintSet.END)
        bannerSet.connect(tvComment.id, ConstraintSet.TOP, tvLike.id, ConstraintSet.TOP)
        bannerSet.connect(tvComment.id, ConstraintSet.END, tvShare.id, ConstraintSet.START)

        bannerSet.connect(tvShare.id, ConstraintSet.START, tvComment.id, ConstraintSet.END)
        bannerSet.connect(tvShare.id, ConstraintSet.TOP, tvLike.id, ConstraintSet.TOP)
        bannerSet.connect(tvShare.id, ConstraintSet.END, id, ConstraintSet.END)

        bannerSet.applyTo(this)
    }

    fun setData(isLike: Boolean?) {
        if (isLike == true) {
            (getChildAt(0) as AppCompatTextView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_12px, 0, 0, 0)
        } else {
            (getChildAt(0) as AppCompatTextView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_off_24dp, 0, 0, 0)
        }
    }

    fun setOnLikeClick(listener: OnClickListener) {
        getChildAt(0).setOnClickListener(listener)
    }

    fun setOnCommentClick(listener: OnClickListener) {
        getChildAt(1).setOnClickListener(listener)
    }

    fun setOnShareClick(listener: OnClickListener) {
        getChildAt(1).setOnClickListener(listener)
    }
}