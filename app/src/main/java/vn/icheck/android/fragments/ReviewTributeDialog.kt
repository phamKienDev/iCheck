package vn.icheck.android.fragments

import android.content.Context
import android.content.DialogInterface
import android.view.View
import kotlinx.android.synthetic.main.dialog_share_review.*
import vn.icheck.android.R
import vn.icheck.android.component.popup_view.BasePopupView
import vn.icheck.android.ichecklibs.ViewHelper

class ReviewTributeDialog(context:Context) : BasePopupView(context, R.style.DialogTheme){

    var onAction: ReviewTributeAction? = null

    fun setAction(action: ReviewTributeAction) {
        this.onAction = action
    }

    override val getLayoutID: Int
        get() = R.layout.dialog_share_review
    override val getIsCancelable: Boolean
        get() = true

    override fun onDismiss() {
        dismiss()
        onAction?.onDismiss()
    }

    override fun onInitView() {
        btn_share.apply {
            background = ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                dismiss()
                onAction?.onShare()
            }
        }
    }


    interface ReviewTributeAction {
        fun onDismiss()
        fun onShare()
    }
}