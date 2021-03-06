package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.dialog_confirm_exchange_gifts.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.repository.RedeemPointRepository
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.accept_ship_gift.AcceptShipGiftLoyaltyActivity
import vn.icheck.android.loyalty.screen.redemption_history.RedemptionHistoryActivity

open class DialogConfirmExchangeGifts(context: Context, val obj: ICKBoxGifts, val campaignId: Long) : BaseDialog(context, R.style.DialogTheme) {
    private val repository = RedeemPointRepository()

    override val getLayoutID: Int
        get() = R.layout.dialog_confirm_exchange_gifts
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        WidgetHelper.loadImageUrl(imgProduct, obj.gift?.image?.medium)

        tvNameProduct.text = if (!obj.gift?.name.isNullOrEmpty()) {
            obj.gift?.name
        } else {
            context.getString(R.string.dang_cap_nhat)
        }

        tvPoint.text = if (obj.points != null) {
            TextHelper.formatMoneyPhay(obj.points)
        } else {
            context.getString(R.string.dang_cap_nhat)
        }

        btnHuy.setOnClickListener {
            dismiss()
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        btnAccept.setOnClickListener {
            dismiss()
            when (obj.gift?.type) {
                "ICOIN" -> {
                    exchangeGift()
                }
                "PHONE_CARD" -> {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.EXCHANGE_PHONE_CARD, obj.gift_id))
                }
                "PRODUCT" -> {
                    val intent = Intent(context, AcceptShipGiftLoyaltyActivity::class.java)
                    intent.putExtra(ConstantsLoyalty.DATA_2, obj)
                    intent.putExtra(ConstantsLoyalty.DATA_3, campaignId)
                    context.startActivity(intent)
                }
                "VOUCHER" -> {
                    exchangeGift()
                }
            }
        }
    }

    private fun exchangeGift() {
        if (NetworkHelper.isNotConnected(context)) {
            ToastHelper.showLongError(context, context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        val user = SessionManager.session.user

        repository.postExchangeGift(campaignId, obj.gift_id ?: -1,
                user?.name,
                user?.phone,
                user?.email,
                user?.city_id,
                user?.district_id,
                user?.address,
                user?.city?.name,
                user?.district?.name,
                user?.ward_id,
                user?.ward?.name,
                object : ICApiListener<ICKResponse<ICKBoxGifts>> {
                    override fun onSuccess(obj: ICKResponse<ICKBoxGifts>) {
                        when (obj.data?.gift?.type) {
                            "ICOIN" -> {
                                DialogHelperGame.dialogExchangeGiftsPointSuccess(context, obj.data?.gift?.icoin, campaignId, R.drawable.bg_gradient_button_orange_yellow)
                            }
                            "VOUCHER" -> {
                                DialogHelperGame.dialogAcceptShipGiftSuccess(context, obj.data?.gift?.image?.original
                                        ?: "", campaignId, R.drawable.bg_gradient_button_orange_yellow,
                                        object : IDismissDialog {
                                            override fun onDismiss() {

                                            }
                                        },
                                        object : IClickButtonDialog<Long> {
                                            override fun onClickButtonData(obj: Long?) {
                                                context.startActivity(Intent(context, RedemptionHistoryActivity::class.java).apply {
                                                    putExtra(ConstantsLoyalty.DATA_1, obj)
                                                })
                                            }
                                        })
                            }
                            else -> {
                                ToastHelper.showLongError(context, obj.data?.message)
                            }
                        }
                    }

                    override fun onError(error: ICKBaseResponse?) {
                        ToastHelper.showLongError(context, error?.message
                                ?: context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                })
    }
}