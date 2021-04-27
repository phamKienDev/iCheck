package vn.icheck.android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_filter_shop.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ViewHelper

class FilterShopSettingBottomDialog(private var begin: String?,
                                    private var end: String?) : BottomSheetDialogFragment() {

    var onFilterDismiss: OnFilterDismiss? = null

    fun setFilterListener(onFilterDismiss: OnFilterDismiss) {
        this.onFilterDismiss = onFilterDismiss
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundResource(R.drawable.rounded_dialog)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return inflater.inflate(R.layout.dialog_filter_shop, container, false)
    }

    interface OnFilterDismiss {
        fun onSuccess(begin: String?, end: String?)
        fun onError()
        fun onBeginEndNull()
        fun onErrorEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtBegin.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    if (current.length <= s.toString().length) {
                        edtBegin.removeTextChangedListener(this)
                        val cleanString = s.toString().replace("[,.]".toRegex(), "")
                        val formatted = String.format("%,d", cleanString.toLong())
                        current = formatted
                        edtBegin.setText(formatted)
                        edtBegin.setSelection(formatted.length)
                        edtBegin.addTextChangedListener(this)
                    } else {
                        edtBegin.removeTextChangedListener(this)
                        val cleanString = s.toString().replace("[,.]".toRegex(), "")
                        if (cleanString.length > 1) {
                            val formatted = String.format("%,d", cleanString.substring(0, cleanString.length).toLong())
                            current = formatted
                            edtBegin.setText(formatted)
                            edtBegin.setSelection(formatted.length)
                        } else {
                            edtBegin.setText("")
                            current = ""
                        }
                        edtBegin.addTextChangedListener(this)
                    }
                }
            }
        })

        edtEnd.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    if (current.length <= s.toString().length) {
                        edtEnd.removeTextChangedListener(this)
                        val cleanString = s.toString().replace("[,.]".toRegex(), "")
                        val formatted = String.format("%,d", cleanString.toLong())
                        current = formatted
                        edtEnd.setText(formatted)
                        edtEnd.setSelection(formatted.length)
                        edtEnd.addTextChangedListener(this)
                    } else {
                        edtEnd.removeTextChangedListener(this)
                        val cleanString = s.toString().replace("[,.]".toRegex(), "")
                        if (cleanString.length > 1) {
                            val formatted = String.format("%,d", cleanString.substring(0, cleanString.length).toLong())
                            current = formatted
                            edtEnd.setText(formatted)
                            edtEnd.setSelection(formatted.length)
                        } else {
                            edtEnd.setText("")
                            current = ""
                        }
                        edtEnd.addTextChangedListener(this)
                    }
                }
            }
        })

        edtBegin.setText(begin)
        edtEnd.setText(end)

        val exit = view.findViewById<AppCompatImageView>(R.id.imgClose)
        exit.setOnClickListener {
            dismiss()
        }

        view.findViewById<AppCompatTextView>(R.id.tvSuccess).setOnClickListener {
            successFilter()
        }
        view.findViewById<AppCompatButton>(R.id.btnTryAgain).apply {
            background = ViewHelper.bgPrimaryCorners18(context)
            setOnClickListener {
                tryAgain()
            }
        }
    }

    private fun successFilter() {
        if (!edtBegin.text.isNullOrEmpty() && !edtEnd.text.isNullOrEmpty()) {
            val i = edtBegin.text.toString().replace(".", "").replace(",", "").toInt()
            val j = edtEnd.text.toString().replace(".", "").replace(",", "").toInt()

            if (i <= j) {
                dismiss()
                onFilterDismiss?.onSuccess(edtBegin.text.toString(), edtEnd.text.toString())
            } else {
                onFilterDismiss?.onError()
            }
        } else {
            if (edtBegin.text.isNullOrEmpty() && edtEnd.text.isNullOrEmpty()) {
                dismiss()
                onFilterDismiss?.onBeginEndNull()
            } else {
                onFilterDismiss?.onErrorEmpty()
            }
        }
    }

    private fun tryAgain() {
        edtBegin.text = null
        edtEnd.text = null
    }
}