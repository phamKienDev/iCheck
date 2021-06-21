package vn.icheck.android.loyalty.dialog.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import vn.icheck.android.ichecklibs.util.RStringUtils
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.dialog.*
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKAccumulatePoint
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.model.ICKNone
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

object DialogHelperGame {

    private var DEFAULT = RStringUtils.rText(R.string.dang_cap_nhat)


    var dialog: Dialog? = null

    fun showLoading(context: Context?) {
        context?.let {
            dialog = object : LoadingDialog(it) {
                override fun onDismiss() {
                    dialog = null
                }
            }

            dialog?.show()
        }
    }

    fun closeLoading() {
        closeDialog()
    }

    fun closeDialog() {
        dialog?.dismiss()
        dialog = null
    }

    fun dialogAdsCampaign(context: Context, image: String?, listener: IDismissDialog) {
        object : DialogAdsCampaign(context, image) {
            override fun onDismiss() {
                listener.onDismiss()
            }
        }.show()
    }

    fun dialogScanLoyaltyError(context: Context, icon: Int, title: String, message: String, button: Int?, msgButton: String?, showButton: Boolean, backgroundButton: Int, colorTextButton: Int, listener: IClickButtonDialog<ICKNone>, iListener: IDismissDialog) {
        object : DialogScanLoyaltyError(context, icon, title, message, button, msgButton, showButton, backgroundButton, colorTextButton) {
            override fun onClickButton() {
                dismiss()
                listener.onClickButtonData(null)
            }

            override fun onDismiss() {
                iListener.onDismiss()
            }
        }.show()
    }

    fun dialogCustomerError(context: Context, icon: Int, title: String, message: String, listener: IClickButtonDialog<ICKNone>) {
        object : DialogCustomerError(context, icon, title, message) {
            override fun onDismiss() {
                listener.onClickButtonData(null)
            }
        }.show()
    }

    fun dialogNotEnoughPoints(context: Context, title: String, message: String?, image: Int, button: String, isShow: Boolean, backgroundButton: Int, colorTextButton: Int, listener: IClickButtonDialog<ICKNone>) {
        object : DialogNotEnoughPoints(context, title, message, image, button, isShow, backgroundButton, colorTextButton) {
            override fun onClickButton() {
                dismiss()
                listener.onClickButtonData(null)
            }
        }.show()
    }

    fun dialogEnterThePrizeCode(context: Context, title: Int, message: String, hint: String, error: String, campaignID: Long?, backgroundButton: Int, listener: IClickButtonDialog<ICKAccumulatePoint>) {
        object : DialogEnterThePrizeCode(context, title, message, hint, error, campaignID
                ?: -1, backgroundButton) {
            override fun onNhapMaSuccess(data: ICKAccumulatePoint?) {
                listener.onClickButtonData(data)
            }
        }.show()
    }

    fun dialogAccumulatePointSuccess(context: Context, point: Long?, avatar: String?, nameShop: String?, campaignID: Long?, nameCampaign: String, backgroundButton: Int, pointName: String?, listener: IClickButtonDialog<Long>, iListener: IDismissDialog, message: String? = null) {
        object : DialogAccumulatePointSuccess(context, point ?: 0, avatar
                ?: "", nameCampaign, nameShop ?: DEFAULT, campaignID
                ?: -1, backgroundButton, pointName, message
                ?: context.rText(R.string.doi_diem_tich_luy_de_nhan_nhung_phan_qua_cuc_cool_nhe)) {
            override fun onClick(campaignID: Long) {
                dismiss()
                listener.onClickButtonData(campaignID)
            }

            override fun onDismiss() {
                iListener.onDismiss()
            }
        }.show()
    }

    fun dialogTutorialLoyalty(context: Context, backgroundButton: Int) {
        object : DialogTutorialLoyalty(context, backgroundButton) {

        }.show()
    }

    fun dialogConfirmExchangeGifts(context: Context, obj: ICKBoxGifts, id: Long) {
        object : DialogConfirmExchangeGifts(context, obj, id) {

        }.show()
    }

    fun dialogAcceptShipGiftSuccess(context: Context, image: String, id: Long, backgroundButton: Int, listener: IDismissDialog, listener2: IClickButtonDialog<Long>) {
        object : DialogAcceptShipGiftSuccess(context, image, id, backgroundButton) {
            override fun onDismiss() {
                listener.onDismiss()
            }

            override fun onClick(id: Long) {
                listener2.onClickButtonData(id)
            }
        }.show()
    }

    fun dialogExchangeGiftsPointSuccess(context: Context, point: Long?, campaignID: Long?, backgroundButton: Int) {
        object : DialogExchangeGiftsPointSuccess(context, point, campaignID, backgroundButton) {

        }.show()
    }

    fun dialogConfirmExchangeGifts(context: Context, image: String?, name: String?, points: Long?, type: String?, countGift: Int?, backgroundButton: Int, idGift: Long) {
        object : DialogConfirmExchangeGiftsLongTime(context, image, name, points, type, countGift, backgroundButton, idGift) {

        }.show()
    }

    fun scanOrEnterAccumulatePoint(context: Context, id: Long, name: String = "") {
        if (SharedLoyaltyHelper(context).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS)) {
            dialogNotEnoughPoints(context, context.rText(R.string.nhap_ma_cong_diem), context.rText(R.string.nhap_ma_duoc_dan_tren_bao_bi_san_pham_de_nhan_diem_tich_luy_doi_qua_nhe), R.drawable.ic_onboarding_nhap, context.rText(R.string.nhap_ma_ngay), false, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {

                            Handler().postDelayed({
                                dialogEnterThePrizeCode(context,
                                        R.drawable.ic_nhap_ma_cong_diem,
                                        context.rText(R.string.nhap_ma_duoc_dan_tren_dan_pham_de_nhan_diem_tich_luy_doi_qua),
                                        context.rText(R.string.nhap_ma_vao_day_i),
                                        context.rText(R.string.vui_long_nhap_ma_code), id, R.drawable.bg_gradient_button_orange_yellow,
                                        object : IClickButtonDialog<ICKAccumulatePoint> {
                                            override fun onClickButtonData(obj: ICKAccumulatePoint?) {

                                                /**
                                                 * Dialog Nhập mã thành công
                                                 */

                                                Handler().postDelayed({
                                                    dialogAccumulatePointSuccess(context,
                                                            obj?.point,
                                                            obj?.statistic?.owner?.logo?.medium,
                                                            obj?.statistic?.owner?.name,
                                                            obj?.statistic?.campaign_id,
                                                            name, R.drawable.bg_gradient_button_orange_yellow, null,
                                                            object : IClickButtonDialog<Long> {
                                                                override fun onClickButtonData(obj: Long?) {

                                                                }

                                                            }, object : IDismissDialog {
                                                        override fun onDismiss() {

                                                        }
                                                    })
                                                }, 300)
                                            }
                                        })
                            }, 300)
                        }
                    })
        } else {
            dialogNotEnoughPoints(context, context.rText(R.string.quet_tem_cong_diem),
                context.rText(R.string.quet_tem_qrcode_duoc_dan_tren_bao_bi_san_pham_de_nhan_diem_tich_luy_doi_qua_nhe), R.drawable.ic_onboarding_scan, context.rText(R.string.quet_tem_ngay), false, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {
                            LoyaltySdk.openActivity("scan?typeLoyalty=accumulate_point&campaignId=$id")
                        }
                    })
        }
    }

    fun scanOrEnterAccumulatePointLongTime(context: Context, id: Long, name: String? = "") {
        if (SharedLoyaltyHelper(context).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_POINT_LONG_TIME)) {
            dialogNotEnoughPoints(context, context.rText(R.string.nhap_ma_cong_diem),
                    context.rText(R.string.nhap_ma_du_thuong_tren_san_pham_de_nhan_diem_thanh_vien_va_doi_qua), R.drawable.ic_onboarding_nhap, context.rText(R.string.nhap_ma_ngay), false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {

                            Handler().postDelayed({
                                dialogEnterThePrizeCode(context,
                                        R.drawable.ic_nhap_ma_cong_diem,
                                        context.rText(R.string.nhap_ma_duoc_dan_tren_dan_pham_de_nhan_diem_tich_luy_doi_qua),
                                        context.rText(R.string.nhap_ma_vao_day_i),
                                        context.rText(R.string.vui_long_nhap_ma_code), id, R.drawable.bg_gradient_button_blue,
                                        object : IClickButtonDialog<ICKAccumulatePoint> {
                                            override fun onClickButtonData(obj: ICKAccumulatePoint?) {

                                                /**
                                                 * Dialog Nhập mã thành công
                                                 */

                                                Handler().postDelayed({
                                                    dialogAccumulatePointSuccess(context,
                                                            obj?.point,
                                                            obj?.statistic?.owner?.logo?.medium,
                                                            obj?.statistic?.owner?.name,
                                                            obj?.statistic?.business_owner_id
                                                                    ?: obj?.statistic?.owner?.id,
                                                            name ?: "",
                                                            R.drawable.bg_gradient_button_blue,
                                                            obj?.statistic?.point_loyalty?.point_name,
                                                            object : IClickButtonDialog<Long> {
                                                                override fun onClickButtonData(obj: Long?) {
                                                                    context.startActivity(Intent(context, GiftShopActivity::class.java).apply {
                                                                        putExtra("id", obj)
                                                                    })
                                                                }

                                                            }, object : IDismissDialog {
                                                        override fun onDismiss() {

                                                        }
                                                    }, context.rText(R.string.doi_qua_bang_diem_tich_luy_ngay_de_nhan_nhung_phan_qua_cuc_hap_dan))
                                                }, 300)
                                            }
                                        })
                            }, 300)
                        }
                    })
        } else {
            dialogNotEnoughPoints(context, context.rText(R.string.quet_tem_cong_diem),
                    context.rText(R.string.quet_tem_qr_code_tren_san_pham_de_duoc_nhan_diem_thanh_vien_va_doi_qua), R.drawable.ic_onboarding_scan, context.rText(R.string.quet_tem_ngay), false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {
                            LoyaltySdk.openActivity("scan?typeLoyalty=accumulation_long_term_point&campaignId=$id&nameCampaign=$name")
                        }
                    })
        }
    }
}