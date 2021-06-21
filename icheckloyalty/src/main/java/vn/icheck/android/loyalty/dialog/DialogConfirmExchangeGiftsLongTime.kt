package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.dialog_confirm_exchange_gifts.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.*
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKRedemptionHistory
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository
import vn.icheck.android.loyalty.screen.loyalty_customers.accept_ship_gift.AcceptShipGiftActivity
import vn.icheck.android.loyalty.screen.redemption_history.RedemptionHistoryActivity

open class DialogConfirmExchangeGiftsLongTime(
        context: Context,
        private val image: String?,
        private val name: String?,
        private val points: Long?,
        private val type: String?,
        private val countGift: Int?,
        private val backgroundButton: Int,
        private val idGift: Long) : BaseDialog(context, R.style.DialogTheme) {
    private val repository = LoyaltyCustomersRepository()

    override val getLayoutID: Int
        get() = R.layout.dialog_confirm_exchange_gifts
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {

        btnHuy.setGone()

        btnAccept.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
            it.setMargins(SizeHelper.size70, 0, SizeHelper.size70, 0)
        }

        btnAccept.setBackgroundResource(backgroundButton)

        WidgetHelper.loadImageUrlRounded6(imgProduct, image)

        tvNameProduct.text = if (!name.isNullOrEmpty()) {
            name
        } else {
            context.rText(R.string.dang_cap_nhat)
        }

        tvPoint.text = if (points != null) {
            TextHelper.formatMoneyPhay(points)
        } else {
            context.rText(R.string.dang_cap_nhat)
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        btnAccept.setOnClickListener {
            dismiss()
            if (countGift ?: 0 > 0) {
                when (type) {
                    "ICOIN" -> {
                        exchangeGift()
                    }
                    "VOUCHER" -> {
                        exchangeGift(true)
                    }
                    "PHONE_CARD" -> {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.EXCHANGE_PHONE_CARD, idGift))
                    }
                    "PRODUCT" -> {
                        context.startActivity(Intent(context, AcceptShipGiftActivity::class.java).apply {
                            putExtra(ConstantsLoyalty.DATA_2, idGift)
                            putExtra(ConstantsLoyalty.TYPE, 1)
                        })
                    }
                }
            } else {
                ComingSoonOrOutOfGiftDialog(context, R.drawable.ic_out_of_gift, context.rText(R.string.tiec_qua_ban_vua_bo_lo_mat_roi), context.rText(R.string.mon_qua_cuoi_cung_da_duoc_doi_roi_hay_lua_chon_mon_qua_khac_nhe)).show()
            }
        }
    }

    private fun exchangeGift(isVoucher: Boolean = false) {
        if (NetworkHelper.isNotConnected(context)) {
            ToastHelper.showLongError(context, context.rText(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        repository.dispose()

        repository.exchangeGift(idGift, null, null, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
            override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                if (obj.status == "FAIL") {
                    ToastHelper.showLongError(context, obj.data?.message
                            ?: context.rText(R.string.co_loi_xay_ra_vui_long_thu_lai))
                } else {
                    if (isVoucher) {
                        DialogHelperGame.dialogAcceptShipGiftSuccess(context, obj.data?.gift?.image?.thumbnail
                                ?: "", obj.data?.owner?.id
                                ?: -1, R.drawable.bg_gradient_button_blue,
                                object : IDismissDialog {
                                    override fun onDismiss() {
                                        onBackPressed()
                                    }
                                },
                                object : IClickButtonDialog<Long> {
                                    override fun onClickButtonData(data: Long?) {
                                        context.startActivity(Intent(context, RedemptionHistoryActivity::class.java).apply {
                                            putExtra(ConstantsLoyalty.DATA_1, obj.data?.owner?.id)
                                            putExtra(ConstantsLoyalty.DATA_2, 1)
                                        })
                                    }
                                })
                    } else {
                        DialogHelperGame.dialogExchangeGiftsPointSuccess(context, obj.data?.gift?.icoin, null, R.drawable.bg_gradient_button_blue)
                    }
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                ToastHelper.showLongError(context, error?.message
                        ?: context.rText(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}