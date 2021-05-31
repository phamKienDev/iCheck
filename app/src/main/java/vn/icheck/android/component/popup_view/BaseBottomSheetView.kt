package vn.icheck.android.component.popup_view

import android.content.Context
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.base_bottom_sheet_view.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.ichecklibs.ViewHelper

abstract class BaseBottomSheetView(context: Context, isCancelable: Boolean) : BaseBottomSheetDialog(context, isCancelable) {

    fun onShow() {
        dialog.setContentView(R.layout.base_bottom_sheet_view)
        dialog.window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        initView()
        onInitView()

        dialog.show()
    }

    private fun initView() {
        dialog.linearLayout.background=ViewHelper.bgWhiteRadiusTop20(dialog.context)

        if(!title.isNullOrEmpty()){
            dialog.tvTitle.text = title
        }
        val viewStub = dialog.baseVs
        if (viewStub != null) {
            viewStub.layoutResource = getLayoutID
            viewStub.inflate()
        }

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    protected abstract val getLayoutID: Int
    protected abstract val title: String?
    protected abstract fun onInitView()
}