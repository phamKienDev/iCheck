package vn.icheck.android.fragments

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.dialog_input_barcode.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.util.KeyboardUtils

abstract class InputBarcodeBottomDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_input_barcode, true) {
    private var isInput = false

    fun show() {
        dialog.edtBarcode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                dialog.btnConfirm.isEnabled = !editable.isNullOrEmpty()
                isInput = false
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        dialog.edtBarcode.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            return@OnKeyListener if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                Handler().postDelayed({
                    if (!dialog.edtBarcode?.text?.toString().isNullOrEmpty()) {
                        dialog.btnConfirm.performClick()
                    }
                }, 400)
                true
            } else {
                false
            }
        })

        dialog.btnConfirm.setOnClickListener {
            onDone()
        }

        dialog.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            if (!isInput) {
                onDismiss()
            }
        }

        Handler().postDelayed({
            dialog.edtBarcode?.let {
                KeyboardUtils.showSoftInput(it)
            }
        }, 500)

        dialog.show()
    }

    private fun onDone() {
        isInput = true
        dialog.dismiss()
        onDone(dialog.edtBarcode.text.toString())
    }

    protected abstract fun onDone(barcode: String)
    protected abstract fun onDismiss()
}