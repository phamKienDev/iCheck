package vn.icheck.android.screen.user.report_product

import android.graphics.Color
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_report_product.*
import kotlinx.android.synthetic.main.header_report.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.OnItemClickListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextColorHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.util.ick.showSimpleErrorToast

class ReportProductActivity : BaseActivityMVVM() {
    private lateinit var viewModel: ReportProductViewModel

    var listReasonID = mutableListOf<Int>()
    var listReasonName = mutableListOf<String>()

    private val getIconDrawable: StateListDrawable
        get() {
            return StateListDrawable().also {
                it.addState(intArrayOf(-android.R.attr.state_checked), ContextCompat.getDrawable(this, R.drawable.ic_square_unchecked_light_blue_24dp))
                it.addState(intArrayOf(android.R.attr.state_checked), ContextCompat.getDrawable(this, R.drawable.ic_square_checked_light_blue_24dp))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_product)
        viewModel = ViewModelProvider(this).get(ReportProductViewModel::class.java)
        setupToolbar()
        setupViewModel()
        viewModel.getProductID(intent)
    }

    private fun setupToolbar() {
        layoutContainer.setPadding(0, SizeHelper.size4, 0, 0)
    }

    private fun setupViewModel() {
        viewModel.listData.observe(this, Observer {
            initView(it)
        })

        viewModel.listReportSuccess.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                val dialogFragment = ProductReportDialog(it)
                dialogFragment.setOnDoneListener(object : OnItemClickListener {
                    override fun onItemClick(itemView: View, position: Int) {
                        finish()
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK))
                    }
                })
                dialogFragment.show(supportFragmentManager, dialogFragment.tag)
            }
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            finish()
                        }

                        override fun onAgree() {
                            viewModel.getListReason()
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(this, Observer {
            showShortError(it)
        })
    }

    private fun initView(list: MutableList<ICReportForm>) {
        layoutParent.addView(LinearLayout(this).also { radioGroup ->
            radioGroup.layoutParams = ViewHelper.createLayoutParams()
            radioGroup.orientation = LinearLayout.VERTICAL

            for (it in list) {
                radioGroup.addView(CheckBox(this).also { radioButton ->
                    radioButton.layoutParams = ViewHelper.createLayoutParams()
                    radioButton.typeface = ViewHelper.createTypeface(this, R.font.barlow_medium)
                    radioButton.setBackgroundResource(ViewHelper.outValue.resourceId)
                    radioButton.setTextColor(ViewHelper.createColorStateList(ContextCompat.getColor(this, R.color.colorSecondText), TextColorHelper.getColorNormalText(this)))
                    radioButton.includeFontPadding = false
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    radioButton.maxLines = 1
                    radioButton.ellipsize = TextUtils.TruncateAt.END
                    radioButton.gravity = Gravity.CENTER_VERTICAL
                    radioButton.buttonDrawable = null
                    radioButton.compoundDrawablePadding = SizeHelper.size8
                    radioButton.setPadding(SizeHelper.size12, SizeHelper.size8, SizeHelper.size12, SizeHelper.size8)
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(getIconDrawable, null, null, null)
                    radioButton.text = it.name
                })
            }

            (radioGroup.getChildAt(radioGroup.childCount - 1) as CheckBox).setOnCheckedChangeListener { compoundButton, boolean ->
                if (boolean) {
                    layoutParent.getChildAt(layoutParent.childCount - 1).visibility = View.VISIBLE
                } else {
                    (layoutParent.getChildAt(layoutParent.childCount - 1) as AppCompatEditText).setText("")
                    layoutParent.getChildAt(layoutParent.childCount - 1).visibility = View.GONE
                }
            }
        })

        layoutParent.addView(ViewHelper.createEditText(this, ViewHelper.createLayoutParams(SizeHelper.size12, 0, SizeHelper.size12, 0), ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size1, ContextCompat.getColor(this, R.color.gray), SizeHelper.size4.toFloat()), ViewHelper.createTypeface(this, R.font.barlow_medium), TextColorHelper.getColorNormalText(this), 14f).also {
            it.setHintTextColor(ContextCompat.getColor(this, R.color.colorDisableText))
            it.minLines = 3
            it.maxLines = 6
            it.setPadding(SizeHelper.size10, SizeHelper.size6, SizeHelper.size10, SizeHelper.size6)
            it.setHint(R.string.mo_ta_noi_dung_bao_cao_khac)
            it.gravity = Gravity.TOP
            it.visibility = View.GONE
        })

        btnSend.setOnClickListener {
            selectReason(list)
        }

        imgBack.setOnClickListener {
            listReasonID.clear()
            val layoutReason = layoutParent.getChildAt(0) as ViewGroup

            for (i in 0 until layoutReason.childCount) {
                val radioButton = layoutReason.getChildAt(i) as CheckBox

                if (radioButton.isChecked) {
                    list[i].let {
                        if (it.id != null) {
                            listReasonID.add(it.id!!)
                        }
                        if (it.name != null) {
                            listReasonName.add(it.name!!)
                        }
                    }
                }
            }

            if (!listReasonID.isNullOrEmpty()) {
                DialogHelper.showConfirm(this, "Bạn muốn bỏ báo cáo này?", null, "Tiếp tục báo cáo", "Bỏ báo cáo", true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                    override fun onDisagree() {
                        DialogHelper.closeLoading(this@ReportProductActivity)
                    }

                    override fun onAgree() {
                        finish()
                    }
                })
            } else {
                finish()
            }
        }
    }

    private fun selectReason(list: MutableList<ICReportForm>) {
        val layoutReason = layoutParent.getChildAt(0) as ViewGroup

        for (i in 0 until layoutReason.childCount) {
            val radioButton = layoutReason.getChildAt(i) as CheckBox

            if (radioButton.isChecked) {
                list[i].let {
                    if (it.id != null) {
                        listReasonID.add(it.id!!)
                    }
                    if (it.name != null) {
                        listReasonName.add(it.name!!)
                    }
                }
            }
        }

        val inputReason = layoutParent.getChildAt(1) as AppCompatEditText

        if (listReasonID.isEmpty() && !inputReason.isVisible) {
            showSimpleErrorToast(getString(R.string.vui_long_chon_it_nhat_mot_ly_do))
        } else {
            var input = inputReason.text.toString().trim()
            if (inputReason.isVisible && input.isEmpty()) {
                input = getString(R.string.khac)
            }
            viewModel.sendReport(listReasonID, listReasonName, inputReason.text.toString().trim())
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.BACK -> {
                finish()
            }
        }
    }

    override fun onBackPressed() {

    }
}
