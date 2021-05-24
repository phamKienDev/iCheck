package vn.icheck.android.fragments

import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.databinding.IckBarcodeBottomBinding
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.kotlin.WidgetUtils

class BarcodeBottomDialog : BaseBottomSheetDialogFragment() {

    private var onBarCodeDismiss: OnBarCodeDismiss? = null
    private var _binding: IckBarcodeBottomBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun show(fragmentManager: FragmentManager, isCancel: Boolean, listener: OnBarCodeDismiss) {
            if (fragmentManager.findFragmentByTag(BarcodeBottomDialog::class.java.simpleName)?.isAdded != true) {
                BarcodeBottomDialog().apply {
                    setBarcodeListener(listener)
                    isCancelable = isCancel
                    show(fragmentManager, BarcodeBottomDialog::class.java.simpleName)
                }
            }
        }
    }

    fun setBarcodeListener(onBarCodeDismiss: OnBarCodeDismiss?) {
        this.onBarCodeDismiss = onBarCodeDismiss
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = IckBarcodeBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    interface OnBarCodeDismiss {
        fun onDismiss()
        fun onSubmit(mCode: String)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClear.setOnClickListener {
            exitEnterBarcode()
        }
        binding.edtBarcode.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER) && !binding.edtBarcode.text.isNullOrEmpty()) {
                submitBarcode()
            }
            false
        }
        binding.edtBarcode.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotBlank()) {
                    binding.submitBarcode.enable()
                } else {
                    binding.submitBarcode.disable()
                }
            }

        })
        binding.submitBarcode.setOnClickListener {
            if (!binding.edtBarcode.text.isNullOrEmpty()) {
                submitBarcode()
            }
        }

        setupListener()
    }

    private fun setupListener() {
        binding.edtBarcode.setOnFocusChangeListener { _, _ ->
            WidgetUtils.setButtonKeyboardMargin(binding.btnKeyboard, binding.edtBarcode)
        }

        binding.btnKeyboard.setOnClickListener {
            WidgetUtils.changePasswordInput(binding.edtBarcode)
        }
    }

    private fun submitBarcode() {
        if (binding.submitBarcode.isEnabled) {
            dismiss()
            onBarCodeDismiss?.onSubmit(binding.edtBarcode.text.toString())
        }
    }

    private fun exitEnterBarcode() {
        dismiss()
        onBarCodeDismiss?.onDismiss()
    }

}

