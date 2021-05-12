package vn.icheck.android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.databinding.IckBarcodeBottomBinding
import vn.icheck.android.ichecklibs.visibleOrGone
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.util.AfterTextWatcher

class BarcodeBottomDialog : BaseBottomSheetDialogFragment() {

    private var onBarCodeDismiss: OnBarCodeDismiss? = null
    private var _binding: IckBarcodeBottomBinding? = null
    private val binding get() = _binding!!

    private var unregistrar: Unregistrar? = null

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

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme).also { dialog ->
//            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//            dialog.setOnShowListener {
//                val bottomSheet = dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
//                bottomSheet?.setBackgroundResource(R.drawable.rounded_dialog)
//                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = IckBarcodeBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        unregistrar = KeyboardVisibilityEvent.registerEventListener(requireActivity(), object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                binding.btnKeyboard.visibleOrGone(isOpen && binding.edtBarcode.isFocused)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        unregistrar?.unregister()
        unregistrar = null
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
        binding.edtBarcode.setOnFocusChangeListener { v, hasFocus ->
            binding.btnKeyboard.visibleOrGone(hasFocus)
        }

        binding.btnKeyboard.setOnClickListener {
            binding.edtBarcode.apply {
                inputType = if (inputType != InputType.TYPE_CLASS_TEXT) {
//                    binding.btnKeyboard.setText(R.string.ban_phim_so)
                    InputType.TYPE_CLASS_TEXT
                } else {
//                    binding.btnKeyboard.setText(R.string.ban_phim_chu)
                    InputType.TYPE_CLASS_NUMBER
                }
                setSelection(length())
            }
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

