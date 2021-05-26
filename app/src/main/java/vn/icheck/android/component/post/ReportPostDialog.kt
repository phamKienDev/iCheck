package vn.icheck.android.component.post

import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_report_post.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.util.kotlin.ToastUtils

class ReportPostDialog(val listData: MutableList<ICReportForm>) : BaseBottomSheetDialogFragment() {

    private lateinit var listener: DialogClickListener

    interface DialogClickListener {
        fun buttonClick(position: Int, listReason: MutableList<Int>, message: String, listMessage: MutableList<String>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_report_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtContent.background=vn.icheck.android.ichecklibs.ViewHelper.bgTransparentRadius4StrokeLineColor1(requireContext())
        setupListener()
    }

    private fun setupListener() {
        imgCancel.setOnClickListener {
            dismiss()
        }

        btnDone.setOnClickListener {
            selectReason(listData)
        }
    }

    private fun setupReportForm() {
        for (it in listData) {
            layoutContent.addView(CheckBox(context).also { radioButton ->
                radioButton.layoutParams = ViewHelper.createLayoutParams()
                radioButton.typeface = ViewHelper.createTypeface(requireContext(), R.font.barlow_medium)
                radioButton.setBackgroundResource(ViewHelper.outValue.resourceId)
                radioButton.setTextColor(ViewHelper.createColorStateList(
                        Constant.getSecondTextColor(requireContext()),
                        Constant.getNormalTextColor(requireContext())))
                radioButton.includeFontPadding = false
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                radioButton.maxLines = 1
                radioButton.ellipsize = TextUtils.TruncateAt.END
                radioButton.gravity = Gravity.CENTER_VERTICAL
                radioButton.buttonDrawable = null
                radioButton.compoundDrawablePadding = SizeHelper.size8
                radioButton.setPadding(SizeHelper.size12, SizeHelper.size5, SizeHelper.size12, SizeHelper.size5)
                radioButton.setCompoundDrawablesWithIntrinsicBounds(getIconDrawable, null, null, null)
                radioButton.text = it.name
            })
        }

        (layoutContent.getChildAt(layoutContent.childCount - 1) as CheckBox).setOnCheckedChangeListener { compoundButton, boolean ->
            if (boolean) {
                edtContent.visibility = View.VISIBLE
            } else {
                edtContent.visibility = View.GONE
                edtContent.setText("")
            }
        }
    }

    private val getIconDrawable: StateListDrawable
        get() {
            return StateListDrawable().also {
                it.addState(intArrayOf(-android.R.attr.state_checked), ContextCompat.getDrawable(requireContext(), R.drawable.ic_square_unchecked_light_blue_24dp))
                it.addState(intArrayOf(android.R.attr.state_checked), ContextCompat.getDrawable(requireContext(), R.drawable.ic_square_checked_light_blue_24dp))
            }
        }

    private fun selectReason(listData: MutableList<ICReportForm>) {
        val listReason = mutableListOf<Int>()
        val listMessage = mutableListOf<String>()

        val layoutParent = view as LinearLayout

        val reasonLayout = layoutParent.getChildAt(2) as LinearLayout
        for (i in 0 until reasonLayout.childCount - 1) {
            val radioButton = reasonLayout.getChildAt(i) as CheckBox

            if (radioButton.isChecked) {
                listData[i].id?.let { listReason.add(it) }
                listData[i].name?.let { listMessage.add(it) }
            }
        }

        val inputReason = layoutParent.getChildAt(3) as AppCompatEditText
        if (inputReason.visibility == View.VISIBLE) {
            if (inputReason.text.isNullOrEmpty()) {
                ToastUtils.showLongError(context, R.string.vui_long_nhap_mo_ta)
                return
            }
        }

        if (listReason.isEmpty()) {
            ToastUtils.showLongError(context, R.string.vui_long_chon_ly_do)
        } else {
            listener.buttonClick(0, listReason, inputReason.text.toString().trim(), listMessage)
        }
    }

    fun setListener(listener: DialogClickListener) {
        this.listener = listener
    }
}