package vn.icheck.android.loyalty.dialog.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
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
import vn.icheck.android.loyalty.screen.scan.V6ScanLoyaltyActivity

object DialogHelperGame {

    private const val DEFAULT = "Đang cập nhật"


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
                ?: "Đổi điểm tích lũy để nhận\nnhững phần quà cực cool nhé!") {
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
            dialogNotEnoughPoints(context, "Nhập mã cộng điểm",
                    "Nhập mã được dán trên bao bì\nsản phẩm để nhận điểm tích lũy đổi quà nhé!", R.drawable.ic_onboarding_nhap, "Nhập mã ngay", false, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {

                            Handler().postDelayed({
                                dialogEnterThePrizeCode(context,
                                        R.drawable.ic_nhap_ma_cong_diem,
                                        "Nhập mã được dán trên sản phẩm\nđể nhận điểm tích lũy đổi quà!",
                                        context.getString(R.string.nhap_ma_vao_day),
                                        "Vui lòng nhập mã code", id, R.drawable.bg_gradient_button_orange_yellow,
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
            dialogNotEnoughPoints(context, "Quét tem cộng điểm",
                    "Quét tem QRcode được dán trên bao bì\nsản phẩm để nhận điểm tích lũy đổi quà nhé!", R.drawable.ic_onboarding_scan, "Quét tem ngay", false, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {
                            context.startActivity(Intent(context, V6ScanLoyaltyActivity::class.java).apply {
                                putExtra(ConstantsLoyalty.DATA_1, id)
                            })
                        }
                    })
        }
    }

    fun scanOrEnterAccumulatePointLongTime(context: Context, id: Long, name: String? = "") {
        if (SharedLoyaltyHelper(context).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_POINT_LONG_TIME)) {
            dialogNotEnoughPoints(context, "Nhập mã cộng điểm",
                    "Nhập mã dự thưởng trên sản phẩm để\nnhận điểm thành viên và đổi quà!", R.drawable.ic_onboarding_nhap, "Nhập mã ngay", false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {

                            Handler().postDelayed({
                                dialogEnterThePrizeCode(context,
                                        R.drawable.ic_nhap_ma_cong_diem,
                                        "Nhập mã được dán trên sản phẩm\nđể nhận điểm tích lũy đổi quà!",
                                        context.getString(R.string.nhap_ma_vao_day),
                                        "Vui lòng nhập mã code", id, R.drawable.bg_gradient_button_blue,
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
                                                    }, "Đổi quà bằng điểm tích lũy ngay để\nnhận những phần quà cực hấp dẫn!")
                                                }, 300)
                                            }
                                        })
                            }, 300)
                        }
                    })
        } else {
            dialogNotEnoughPoints(context, "Quét tem cộng điểm",
                    "Quét tem QR code trên sản phẩm để\nnhận điểm thành viên và đổi quà!", R.drawable.ic_onboarding_scan, "Quét tem ngay", false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                    object : IClickButtonDialog<ICKNone> {
                        override fun onClickButtonData(obj: ICKNone?) {
                            context.startActivity(Intent(context, V6ScanLoyaltyActivity::class.java).apply {
                                putExtra(ConstantsLoyalty.DATA_1, id)
                                putExtra(ConstantsLoyalty.DATA_2, 1)
                                putExtra(ConstantsLoyalty.DATA_3, name)
                            })
                        }
                    })
        }
    }
}