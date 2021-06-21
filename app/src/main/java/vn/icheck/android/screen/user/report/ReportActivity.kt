package vn.icheck.android.screen.user.report

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.item_message.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionSuccessDialog
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.showShortErrorToast

class ReportActivity : BaseActivityMVVM() {
    lateinit var viewModel: ReportViewModel

    private val listData = mutableListOf<ICReportForm>()
    private val listReason = mutableListOf<Int>()
    private val listMessage = mutableListOf<String>()

    companion object {
        var order = 1

        fun start(typeReport: Int? = null, id: Long?, title: String? = null, activity: Activity) {
            val intent = Intent(activity, ReportActivity::class.java)
            intent.putExtra(Constant.DATA_1, typeReport)
            intent.putExtra(Constant.DATA_2, title)
            intent.putExtra(Constant.DATA_3, id)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        viewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        initToolbar()
        initData()
    }

    private fun initToolbar() {
        imgCancel.setOnClickListener {
            onBackPressed()
        }
        btnDone.setOnClickListener {
            selectReason()
        }
        tvTitle.text = intent.getStringExtra(Constant.DATA_2) ?: getString(R.string.bao_cao)
    }

    private fun initData() {
        viewModel.getData(intent)

        viewModel.onData.observe(this, {
            layoutData.beVisible()
            layoutLoading.beGone()

            listData.clear()
            listData.addAll(it)

            layoutContent.addView(LinearLayout(this).also { radioGroup ->
                radioGroup.layoutParams = ViewHelper.createLayoutParams().also { params ->
                    params.bottomMargin = SizeHelper.size22
                }
                radioGroup.orientation = LinearLayout.VERTICAL

                for (item in it) {
                    radioGroup.addView(CheckBox(this).also { radioButton ->
                        radioButton.layoutParams = ViewHelper.createLayoutParams()
                        radioButton.typeface = ViewHelper.createTypeface(this, R.font.barlow_medium)
                        radioButton.setBackgroundResource(ViewHelper.outValue.resourceId)
                        radioButton.setTextColor(ViewHelper.createColorStateList(
                                ContextCompat.getColor(this, R.color.colorSecondText),
                                ContextCompat.getColor(this, R.color.colorNormalText)))
                        radioButton.includeFontPadding = false
                        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        radioButton.maxLines = 1
                        radioButton.ellipsize = TextUtils.TruncateAt.END
                        radioButton.gravity = Gravity.CENTER_VERTICAL
                        radioButton.buttonDrawable = null
                        radioButton.compoundDrawablePadding = SizeHelper.size8
                        radioButton.setPadding(SizeHelper.size12, SizeHelper.size5, SizeHelper.size12, SizeHelper.size5)
                        radioButton.setCompoundDrawablesWithIntrinsicBounds(getIconDrawable, null, null, null)
                        radioButton.text = item.name
                    })
                }

                (radioGroup.getChildAt(radioGroup.childCount - 1) as CheckBox).setOnCheckedChangeListener { compoundButton, boolean ->
                    if (boolean) {
                        edtContent.beVisible()
                        edtContent.requestFocus()
                        KeyboardUtils.showSoftInput(edtContent)
                    } else {
                        edtContent.setText("")
                        edtContent.beGone()
                        KeyboardUtils.hideSoftInput(edtContent)
                    }
                }
            })
        })

        viewModel.onReport.observe(this, {
            val list = mutableListOf<ICReportForm>()
            for (item in listMessage) {
                if (item != "Khác" && item != "Lí do khác")
                    list.add(ICReportForm(0, item))
            }
            if (edtContent.isVisible) {
                if (edtContent.text.toString().isEmpty())
                    list.add(ICReportForm(0, getString(R.string.khac)))
                else
                    list.add(ICReportForm(0, edtContent.text.toString()))
            }

            val dialog = ReportWrongContributionSuccessDialog(this)
            dialog.show(list, "order", getString(R.string.cam_on_ban_da_bao_loi_don_hang_nay))
            dialog.dialog.setOnDismissListener {
                finish()
            }
        })

        viewModel.onError.observe(this, {
            if (it.button == -1) {
                showShortError(it.message ?: "")
            } else {
                layoutData.beGone()
                layoutLoading.beVisible()

                imgIcon.setImageResource(it.icon)
                txtMessage.text = it.message
                btnTryAgain.beGone()
            }
        })

        viewModel.onState.observe(this, {
            when (it) {
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
    }

    override fun onBackPressed() {
        var isChecked = false
        val reasonLayout = layoutContent.getChildAt(0) as LinearLayout
        for (i in 0 until reasonLayout.childCount) {
            val radioButton = reasonLayout.getChildAt(i) as CheckBox

            if (radioButton.isChecked) {
                isChecked = true
            }
        }

        if (isChecked) {
            DialogHelper.showConfirm(this, getString(R.string.ban_muon_bo_bao_cao_loi_nay), null, getString(R.string.tiep_tuc_bao_loi), getString(R.string.bo_bao_cao), true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    finish()
                }
            })
        } else {
            super.onBackPressed()
        }
    }

    private fun selectReason() {
        listReason.clear()
        listMessage.clear()

        val reasonLayout = layoutContent.getChildAt(0) as LinearLayout
        for (i in 0 until reasonLayout.childCount) {
            val radioButton = reasonLayout.getChildAt(i) as CheckBox

            if (radioButton.isChecked) {
                listData[i].id?.let { listReason.add(it) }
                listData[i].name?.let {
                    if ((it != "Khác" && it != "Lý do khác")||(it != getString(R.string.khac) && it != getString(R.string.ly_do_khac)))
                        listMessage.add(it)
                }
            }
        }

        if (listReason.isEmpty()) {
            showShortErrorToast(R.string.vui_long_chon_it_nhat_mot_ly_do)
        } else {
            viewModel.putOrder(listReason, edtContent.text.toString())
        }
    }

    private val getIconDrawable: StateListDrawable
        get() {
            return StateListDrawable().also {
                it.addState(intArrayOf(-android.R.attr.state_checked), ContextCompat.getDrawable(this, R.drawable.ic_square_unchecked_light_blue_24dp))
                it.addState(intArrayOf(android.R.attr.state_checked), ContextCompat.getDrawable(this, R.drawable.ic_square_checked_light_blue_24dp))
            }
        }
}