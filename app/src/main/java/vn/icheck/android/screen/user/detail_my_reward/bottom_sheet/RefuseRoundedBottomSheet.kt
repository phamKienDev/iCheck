package vn.icheck.android.screen.user.detail_my_reward.bottom_sheet

import android.app.Dialog
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_dialog_refuse_gift.*
import vn.icheck.android.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.model.ICNameId

class RefuseRoundedBottomSheet(val mId: String?) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BaseBottomSheetDialogFullScreen

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_dialog_refuse_gift, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            Handler().postDelayed(Runnable {
                val d = dialog as BottomSheetDialog
                val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }, 0)
        }
        return v
    }

    private val getIconDrawable: StateListDrawable
        get() {
            return StateListDrawable().also {
                it.addState(intArrayOf(-android.R.attr.state_checked), ContextCompat.getDrawable(requireContext(), R.drawable.ic_square_unchecked_light_blue_24dp))
                it.addState(intArrayOf(android.R.attr.state_checked), ContextCompat.getDrawable(requireContext(), R.drawable.ic_square_checked_light_blue_24dp))
            }
        }

    private lateinit var listener: DialogClickListener

    interface DialogClickListener {
        fun buttonClick(position: Int, listId: MutableList<Int>, listMessage: MutableList<String>)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        listener()
    }

    private fun initView() {
        val list = mutableListOf<ICNameId>()
        list.add(ICNameId(1, "Tôi không hài lòng với giá trị phần quà"))
        list.add(ICNameId(2, "Phí ship quá cao"))
        list.add(ICNameId(3, "Phần quà không rõ nguồn gốc"))
        list.add(ICNameId(4, "Khác"))

        for (i in list) {
            layoutCheckbox.addView(CheckBox(context).also { radioButton ->
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
                radioButton.text = i.value
            })
        }

        (layoutCheckbox.getChildAt(layoutCheckbox.childCount - 1) as CheckBox).setOnCheckedChangeListener { compoundButton, boolean ->
            if (boolean) {
                edtContent.visibility = View.VISIBLE
            } else {
                edtContent.setText("")
                edtContent.visibility = View.GONE
            }
        }

        btnSendRefuse.setOnClickListener {
            selectReason(list)
        }
    }

    private fun selectReason(list: MutableList<ICNameId>) {
        val listId = mutableListOf<Int>()
        val listMessage = mutableListOf<String>()

        val layoutParent = view as LinearLayout
        val reasonLayout = layoutParent.getChildAt(2) as LinearLayout
        for (i in 0 until reasonLayout.childCount) {
            val radioButton = reasonLayout.getChildAt(i) as CheckBox

            if (radioButton.isChecked) {
                list[i].let { listId.add(it.id) }
                list[i].let { listMessage.add(it.value!!) }
            }
        }

        val inputReason = layoutParent.getChildAt(3) as AppCompatEditText
//        if (inputReason.visibility == View.VISIBLE) {
//            if (inputReason.text.isNullOrEmpty()) {
//                ToastUtils.showLongError(context, R.string.vui_long_nhap_mo_ta)
//                return
//            }
        if (!inputReason.text?.trim().isNullOrEmpty()) {
            listMessage.add(inputReason.text.toString())
            val id = listMessage.indexOfFirst {
                it == "Khác"
            }
            listMessage.removeAt(id)
        }
//        }

//        if (listMessage.isEmpty()) {
//            ToastUtils.showLongError(context, R.string.vui_long_chon_ly_do)
//        } else {
//            listener.buttonClick(0, listId, listMessage)
//        }
//        if (listMessage.isEmpty()) {
//            listMessage.add("Khác")
//        }
        listener.buttonClick(0, listId, listMessage)
    }

    private fun listener() {
        btnClose.setOnClickListener {
            dismiss()
        }
    }


    fun setListener(listener: DialogClickListener) {
        this.listener = listener
    }
}