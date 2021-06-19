package vn.icheck.android.icheckscanditv6

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.WidgetHelper
import vn.icheck.android.ichecklibs.databinding.IckBarcodeBottomBinding

class BarcodeBottomDialog : BaseBottomSheetDialogFragment() {

    private var onBarCodeDismiss: OnBarCodeDismiss? = null
    private lateinit var binding: IckBarcodeBottomBinding

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
        binding = IckBarcodeBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    interface OnBarCodeDismiss {
        fun onDismiss()
        fun onSubmit(mCode: String)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        binding.submitBarcode.invalidate()

        binding.btnClear.setOnClickListener {
            exitEnterBarcode()
        }

        binding.edtBarcode.apply {
            setHintTextColor(Constant.getDisableTextColor(context))

            binding.submitBarcode.disable()

            setOnKeyListener { v, keyCode, event ->
                if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER) && !binding.edtBarcode.text.isNullOrEmpty()) {
                    submitBarcode()
                }
                false
            }
            addTextChangedListener {s ->
                if (s.toString().isNotBlank()) {
                    binding.submitBarcode.enable()
                } else {
                    binding.submitBarcode.disable()
                }
            }
        }

        binding.submitBarcode.setOnClickListener {
            if (!binding.edtBarcode.text.isNullOrEmpty()) {
                submitBarcode()
            }
        }

        setupListener()
    }

    private fun setupView() {
        binding.root.background = ViewHelper.bgWhiteCornersTop16(requireContext())
    }

    private fun setupListener() {
        binding.edtBarcode.setOnFocusChangeListener { _, _ ->
            WidgetHelper.setButtonKeyboardMargin(binding.btnKeyboard, binding.edtBarcode)
        }

        binding.btnKeyboard.setOnClickListener {
            WidgetHelper.changePasswordInput(binding.edtBarcode)
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