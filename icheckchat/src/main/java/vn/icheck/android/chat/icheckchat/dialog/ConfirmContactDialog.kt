package vn.icheck.android.chat.icheckchat.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import vn.icheck.android.chat.icheckchat.databinding.DialogConfirmContactBinding

open class ConfirmContactDialog(context: Context, val onAgree: () -> Unit, val onTerm: () -> Unit) : Dialog(context) {

    val binding = DialogConfirmContactBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        onInitView()
    }

    private fun onInitView() {
        binding.btnDisagree.setOnClickListener {
            dismiss()
        }
        binding.btnAgree.setOnClickListener {
            onAgree.invoke()
            dismiss()
        }
        binding.tvDksd.setOnClickListener {
            onTerm.invoke()
        }
    }
}