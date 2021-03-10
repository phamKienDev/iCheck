package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_coming_soon_or_out_of_gift.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

open class ComingSoonOrOutOfGiftDialog(context: Context, val image: Int, val title: String, val message: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_coming_soon_or_out_of_gift
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imageView.setImageResource(image)

        txtTitle.text = title

        txtMessage.text = message

        btnClose.setOnClickListener {
            dismiss()
        }
    }
}