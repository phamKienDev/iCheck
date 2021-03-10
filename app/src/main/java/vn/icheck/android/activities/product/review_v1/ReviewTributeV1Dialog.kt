package vn.icheck.android.activities.product.review_v1

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_review_tribute_v1.*
import vn.icheck.android.R

class ReviewTributeV1Dialog : DialogFragment() {

    var onAction: ReviewTributeAction? = null

    fun setAction(action: ReviewTributeAction) {
        this.onAction = action
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_review_tribute_v1, container, false)
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_clear.setOnClickListener {
            dismiss()
            onAction?.onDismiss()
        }
        btn_share.setOnClickListener {
            btn_share.visibility = View.INVISIBLE
            progressBarShare.visibility = View.VISIBLE
            onAction?.onShare()
        }

    }

    interface ReviewTributeAction {
        fun onDismiss()
        fun onShare()
    }
}