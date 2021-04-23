package vn.icheck.android.screen.user.product_detail.product.wrongcontribution

import android.graphics.Color
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
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ButtonLightBlueCorners4
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.util.KeyboardUtils

class ReportWrongContributionDialog(val listData: MutableList<ICReportForm>, val title: Int? = null, val inputHint: Int? = null) : BaseBottomSheetDialogFragment() {

    private lateinit var listener: DialogClickListener

    interface DialogClickListener {
        fun buttonClick(position: Int, listReason: MutableList<Int>, message: String, listMessage: MutableList<String>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createScrollView()
    }

    private fun createView() = LinearLayout(context).also { layoutParent ->
        layoutParent.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(700))
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.background = ViewHelper.createShapeDrawable(Color.WHITE, 0, Color.TRANSPARENT,
                SizeHelper.size20.toFloat(), SizeHelper.size20.toFloat(), 0f, 0f)

        // Layout header - 0
        layoutParent.addView(LinearLayout(context).also { layoutHeader ->
            layoutHeader.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size42)
            layoutHeader.orientation = LinearLayout.HORIZONTAL

            layoutHeader.addView(AppCompatTextView(requireContext()).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, SizeHelper.size12, 0, 0, 0)
                it.setBackgroundResource(ViewHelper.outValue.resourceId)
                it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_light_blue_24dp, 0, 0, 0)

                it.setOnClickListener {
                    clickBtnDismiss()
                }
            })

            layoutHeader.addView(ViewHelper.createText(requireContext(),
                    ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0, 0, SizeHelper.size36, 0),
                    null,
                    ViewHelper.createTypeface(requireContext(), R.font.barlow_semi_bold),
                    vn.icheck.android.ichecklibs.Constant.getPrimaryColor(requireContext()),
                    18f,
                    1).also {
                it.gravity = Gravity.CENTER
                if (title == null)
                    it.setText(R.string.tai_sao_thong_tin_dong_gop_nay_sai)
                else
                    it.setText(title)
            })
        })

        // Line gray - 1
        layoutParent.addView(View(context).also {
            it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size1)
            it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        })

        // Layout list reason - 2
        layoutParent.addView(LinearLayout(context).also { radioGroup ->
            radioGroup.layoutParams = ViewHelper.createLayoutParams().also { params ->
                params.topMargin = SizeHelper.size16
            }
            radioGroup.orientation = LinearLayout.VERTICAL

            for (it in listData) {
                radioGroup.addView(CheckBox(context).also { radioButton ->
                    radioButton.layoutParams = ViewHelper.createLayoutParams()
                    radioButton.typeface = ViewHelper.createTypeface(requireContext(), R.font.barlow_medium)
                    radioButton.setBackgroundResource(ViewHelper.outValue.resourceId)
                    radioButton.setTextColor(ViewHelper.createColorStateList(
                            ContextCompat.getColor(requireContext(), R.color.colorSecondText),
                            ContextCompat.getColor(requireContext(), R.color.colorNormalText)))
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

            (radioGroup.getChildAt(radioGroup.childCount - 1) as CheckBox).setOnCheckedChangeListener { compoundButton, boolean ->
                if (boolean) {
                    layoutParent.getChildAt(layoutParent.childCount - 2).visibility = View.VISIBLE
                    KeyboardUtils.showSoftInput(layoutParent.getChildAt(layoutParent.childCount - 2) as AppCompatEditText)
                    (layoutParent.getChildAt(layoutParent.childCount - 2) as AppCompatEditText).requestFocus()
                } else {
                    (layoutParent.getChildAt(layoutParent.childCount - 2) as AppCompatEditText).setText("")
                    layoutParent.getChildAt(layoutParent.childCount - 2).visibility = View.GONE
                    KeyboardUtils.hideSoftInput(layoutParent.getChildAt(layoutParent.childCount - 2) as AppCompatEditText)
                }
            }
        })

        // Input reason - 3
        layoutParent.addView(ViewHelper.createEditText(requireContext(),
                ViewHelper.createLayoutParams(SizeHelper.size12, 0, SizeHelper.size12, 0),
                ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size1, ContextCompat.getColor(requireContext(), R.color.gray), SizeHelper.size4.toFloat()),
                ViewHelper.createTypeface(requireContext(), R.font.barlow_medium),
                ContextCompat.getColor(requireContext(), R.color.colorNormalText),
                14f).also {
            it.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.colorDisableText))
            it.minLines = 3
            it.maxLines = 6
            it.setPadding(SizeHelper.size10, SizeHelper.size6, SizeHelper.size10, SizeHelper.size6)
            if (inputHint != null) {
                it.setHint(inputHint)
            } else {
                it.setHint(R.string.mo_ta_noi_dung_khac)
            }
            it.gravity = Gravity.TOP
            it.visibility = View.GONE
        })

        // Button done - 4
        layoutParent.addView(ButtonLightBlueCorners4(requireContext()).also {
            it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size36, SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size10)
            it.typeface = ViewHelper.createTypeface(requireContext(), R.font.barlow_semi_bold)
            it.setText(R.string.gui_bao_cao)

            it.setOnClickListener {
                selectReason(listData)
            }
        })
    }

    private fun createScrollView() = ScrollView(context).also { layoutParent ->
        layoutParent.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)
        layoutParent.addView(createView())
    }

    private fun clickBtnDismiss() {
        val layoutParent = (view as ViewGroup).getChildAt(0) as LinearLayout
        val reasonLayout = layoutParent.getChildAt(2) as LinearLayout
        var isSelected = false
        for (i in 0 until reasonLayout.childCount) {
            val radioButton = reasonLayout.getChildAt(i) as CheckBox
            if (radioButton.isChecked) {
                isSelected = true
                break
            }
        }



        if (isSelected) {
            DialogHelper.showConfirm(dialog?.context, "Bạn muốn bỏ báo cáo này?", null, "Tiếp tục báo cáo", "Bỏ báo cáo", true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.DISMISS_DIALOG))
                    dismiss()
                }
            })
        } else {
            dismiss()
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

        val layoutParent = (view as ViewGroup).getChildAt(0) as LinearLayout

        val reasonLayout = layoutParent.getChildAt(2) as LinearLayout
        for (i in 0 until reasonLayout.childCount) {
            val radioButton = reasonLayout.getChildAt(i) as CheckBox

            if (radioButton.isChecked) {
                listData[i].id?.let { listReason.add(it) }
                listData[i].name?.let {
                    if (it != "Khác" && it != "Lý do khác")
                        listMessage.add(it)
                }
            }
        }

        val inputReason = layoutParent.getChildAt(3) as AppCompatEditText
        var input = inputReason.text.toString().trim()

        if (listReason.isEmpty() && (!inputReason.isVisible || input.isEmpty())) {
            DialogHelper.showDialogErrorBlack(requireContext(), resources.getString(R.string.vui_long_chon_it_nhat_1_ly_do), null, 2000)
        } else {
            if (inputReason.isVisible && input.isEmpty()) {
                input = requireContext().getString(R.string.khac)
            }
            listener.buttonClick(0, listReason, input, listMessage)
        }
    }

    fun setListener(listener: DialogClickListener) {
        this.listener = listener
    }
}