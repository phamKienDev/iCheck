package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.os.Handler
import kotlinx.android.synthetic.main.dialog_enter_the_prize_code.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.PointHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.model.ICKAccumulatePoint
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository
import vn.icheck.android.loyalty.utils.KeyboardUtils

abstract class DialogEnterThePrizeCode(
        context: Context,
        val title: Int,
        val message: String,
        val hint: String,
        val error: String,
        val campaignId: Long,
        val backgroundButton: Int
) : BaseDialog(context, R.style.DialogTheme) {
    private val repository = RedeemPointRepository()

    override val getLayoutID: Int
        get() = R.layout.dialog_enter_the_prize_code
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        edtInput.requestFocus()
        KeyboardUtils.showSoftInput(edtInput)

        imgTitle.setImageResource(title)

        tvMessage.text = message

        edtInput.hint = hint

        btnAccept.setBackgroundResource(backgroundButton)

        btnAccept.setOnClickListener {
            if (!edtInput.text?.trim().toString().isNullOrEmpty()) {
                btnAccept.isEnabled = false
                KeyboardUtils.hideSoftInput(edtInput)
                postNhapMaTichDiem(edtInput.text?.trim().toString())

                Handler().postDelayed({
                    btnAccept.isEnabled = true
                }, 2000)
            } else {
                tvError.setVisible()
                tvError.text = error
            }
        }

        imgClose.setOnClickListener {
            KeyboardUtils.hideSoftInput(edtInput)
            dismiss()
        }

        setOnDismissListener {
            KeyboardUtils.hideSoftInput(edtInput)
        }
    }

    private fun postNhapMaTichDiem(code: String) {
        if (NetworkHelper.isNotConnected(context)) {
            ToastHelper.showLongError(context, context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        repository.postAccumulatePoint(campaignId, code, null, object : ICApiListener<ICKResponse<ICKAccumulatePoint>> {
            override fun onSuccess(obj: ICKResponse<ICKAccumulatePoint>) {
                when (obj.statusCode) {
                    200 -> {
                        if (backgroundButton == R.drawable.bg_gradient_button_orange_yellow) {
                            PointHelper.updatePoint(obj.data?.statistic?.campaign_id ?: -1)
                        } else {
                            /**
                             * Update point
                             */
                        }
                        tvError.setGone()
                        dismiss()
                        onNhapMaSuccess(obj.data)
                    }
                    else -> {
                        tvError.setVisible()
                        tvError.text = obj.data?.message
                    }
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                tvError.setVisible()
                tvError.setGone()
            }
        })
    }

    protected abstract fun onNhapMaSuccess(data: ICKAccumulatePoint?)
}