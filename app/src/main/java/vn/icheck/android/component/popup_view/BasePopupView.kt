package vn.icheck.android.component.popup_view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewStub
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.base_popup_view.*
import vn.icheck.android.R

abstract class BasePopupView(context: Context, theme: Int) : Dialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_popup_view)
        initView()
        window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(getIsCancelable)
        setCanceledOnTouchOutside(getIsCancelable)

        onInitView()
    }

    private fun initView() {
        val viewStub = findViewById<ViewStub>(R.id.view_stub)
        if (viewStub != null) {
            viewStub.layoutResource = getLayoutID
            viewStub.inflate()
        }

        btn_clear.setOnClickListener {
            dismiss()
            onDismiss()
        }
    }

    protected abstract val getLayoutID: Int
    protected abstract val getIsCancelable: Boolean
    protected abstract fun onDismiss()
    protected abstract fun onInitView()
}