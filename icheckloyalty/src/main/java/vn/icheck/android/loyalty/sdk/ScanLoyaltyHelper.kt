package vn.icheck.android.loyalty.sdk

import android.os.Handler
import androidx.fragment.app.FragmentActivity
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.showCustomErrorToast
import vn.icheck.android.loyalty.dialog.DialogErrorScanGame
import vn.icheck.android.loyalty.dialog.DialogSuccessScanGame
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.*
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository
import vn.icheck.android.loyalty.repository.VQMMRepository
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity

object ScanLoyaltyHelper {

    var update = false

    fun checkCodeScanLoyalty(
            activity: FragmentActivity,
            type: String,
            code: String,
            campaignId: Long,
            nameCampaign: String?,
            nameShop: String?,
            avatarShop: String?,
            currentCount: Int?,
            mPickerScan: (stop: Boolean) -> Unit) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            ToastHelper.showShortError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        when (type) {
            "accumulate_point" -> {
                checkCodePoint(activity, 0, code, campaignId, nameCampaign ?: "", mPickerScan)
            }
            "accumulation_long_term_point" -> {
                checkCodePoint(activity, 1, code, campaignId, nameCampaign ?: "", mPickerScan)
            }
            "receive_gift" -> {

            }
            else -> {
                checkCodeMiniGame(activity, code, campaignId, nameCampaign ?: "", nameShop
                        ?: "", avatarShop ?: "", currentCount
                        ?: 0, mPickerScan)
            }
        }
    }

    private fun checkCodeMiniGame(
            activity: FragmentActivity,
            code: String,
            campaignId: Long,
            nameCampaign: String,
            nameShop: String,
            avatarShop: String,
            currentCount: Int,
            mPickerScan: (stop: Boolean) -> Unit) {
        VQMMRepository().getGamePlay(campaignId, code, object : ICApiListener<ReceiveGameResp> {
            override fun onSuccess(obj: ReceiveGameResp) {
                if (obj.statusCode == 200 && obj.data?.play != null) {
                    mPickerScan(true)

                    object : DialogSuccessScanGame(activity, "Bạn có thêm ${obj.data.play} lượt quay", obj.data.campaign?.name
                            ?: nameCampaign, nameShop, avatarShop) {
                        override fun onDone() {
                            dismiss()
                            update = true
                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_GAME, obj.data.play + currentCount))
                            activity.finish()
                        }

                        override fun onDismiss() {
                            activity.finish()
                        }
                    }.show()
                } else {
                    mPickerScan(true)
                    when (obj.status) {
                        "OUT_OF_TURN" -> {
                            object : DialogErrorScanGame(activity, R.drawable.ic_error_scan_game,
                                    "Mã QRcode của sản phẩm này không còn lượt quay", "Thử quét với những mã QRcode khác để thêm lượt quay nhận ngàn quà hay nhé") {
                                override fun onDismiss() {
                                    activity.finish()
                                }

                            }.show()
                        }
                        "INVALID_PARAM" -> {
                            object : DialogErrorScanGame(activity, R.drawable.ic_error_scan_game_1,
                                    "Mã QRcode của sản phẩm này không thuộc chương trình", "Thử quét với những mã QRcode khác để thêm lượt quay nhận ngàn quà hay nhé") {
                                override fun onDismiss() {
                                    activity.finish()
                                }

                            }.show()
                        }
                        else -> {
                            ToastHelper.showShortError(activity, obj.data?.message)
                        }
                    }
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                ToastHelper.showShortError(activity, error?.message
                        ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                activity.finish()
            }
        })
    }

    /**
     * @param type == 0 -> Scan tích điểm đổi quà
     * @param type == 1 -> Scan tích điểm dài hạn
     */
    private fun checkCodePoint(
            activity: FragmentActivity,
            type: Int,
            code: String,
            campaignId: Long,
            nameCampaign: String,
            mPickerScan: (stop: Boolean) -> Unit
    ) {
        RedeemPointRepository().postAccumulatePoint(campaignId, null, code, object : ICApiListener<ICKResponse<ICKAccumulatePoint>> {
            override fun onSuccess(obj: ICKResponse<ICKAccumulatePoint>) {
                if (obj.statusCode != 200) {
                    when (obj.status) {
                        "INVALID_TARGET" -> {
                            mPickerScan(true)

                            if (type == 1) {
                                DialogHelperGame.dialogScanLoyaltyError(activity,
                                        R.drawable.ic_error_scan_game_1, "Mã QRcode của sản phẩm này\nkhông thuộc chương trình",
                                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                                        null, "Quét tiếp", false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                                        object : IClickButtonDialog<ICKNone> {
                                            override fun onClickButtonData(obj: ICKNone?) {
                                                mPickerScan(false)
                                            }
                                        }, object : IDismissDialog {
                                    override fun onDismiss() {
                                        mPickerScan(false)
                                    }
                                })
                            } else {
                                DialogHelperGame.dialogScanLoyaltyError(activity,
                                        R.drawable.ic_error_scan_game_1, "Mã QRcode của sản phẩm này\nkhông thuộc chương trình",
                                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                                        null, "Quét tiếp", false, R.drawable.bg_gradient_button_orange_yellow, R.color.white,
                                        object : IClickButtonDialog<ICKNone> {
                                            override fun onClickButtonData(obj: ICKNone?) {
                                                mPickerScan(false)
                                            }
                                        }, object : IDismissDialog {
                                    override fun onDismiss() {
                                        mPickerScan(false)
                                    }
                                })
                            }
                        }
                        "USED_TARGET" -> {
                            val it = "Mã QRcode của sản phẩm này\nkhông còn điểm cộng"

                            mPickerScan(true)

                            if (type == 1) {
                                DialogHelperGame.dialogScanLoyaltyError(activity,
                                        R.drawable.ic_error_scan_game, it,
                                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                                        null, "Quét tiếp", false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                                        object : IClickButtonDialog<ICKNone> {
                                            override fun onClickButtonData(obj: ICKNone?) {
                                                mPickerScan(false)
                                            }
                                        }, object : IDismissDialog {
                                    override fun onDismiss() {
                                        mPickerScan(false)
                                    }
                                })
                            } else {
                                DialogHelperGame.dialogScanLoyaltyError(activity,
                                        R.drawable.ic_error_scan_game, it,
                                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                                        null, "Quét tiếp", false, R.drawable.bg_gradient_button_orange_yellow, R.color.white,
                                        object : IClickButtonDialog<ICKNone> {
                                            override fun onClickButtonData(obj: ICKNone?) {
                                                mPickerScan(false)
                                            }
                                        }, object : IDismissDialog {
                                    override fun onDismiss() {
                                        mPickerScan(false)
                                    }
                                })
                            }
                        }
                        "INVALID_CUSTOMER" -> {
                            mPickerScan(true)

                            if (type == 1) {
                                DialogHelperGame.dialogCustomerError(activity,
                                        R.drawable.ic_error_scan_game,
                                        "Bạn không thuộc danh sách\ntham gia chương trình",
                                        "Liên hệ với ${SharedLoyaltyHelper(activity).getString(ConstantsLoyalty.OWNER_NAME)} để biết thêm chi tiết", object : IClickButtonDialog<ICKNone> {
                                    override fun onClickButtonData(obj: ICKNone?) {
                                        activity.onBackPressed()
                                    }
                                })
                            }
                        }
                        else -> {
                            mPickerScan(false)
                            ToastHelper.showLongError(activity, obj.data?.message)
                        }
                    }
                } else {
                    PointHelper.updatePoint(campaignId)
                    val it = obj.data

                    mPickerScan(true)

                    Handler().postDelayed({
                        if (type == 1) {
                            DialogHelperGame.dialogAccumulatePointSuccess(activity,
                                    it?.point,
                                    it?.statistic?.owner?.logo?.medium,
                                    it?.statistic?.owner?.name, it?.statistic?.business_owner_id
                                    ?: it?.statistic?.owner?.id,
                                    nameCampaign,
                                    R.drawable.bg_gradient_button_blue, it?.statistic?.point_loyalty?.point_name,
                                    object : IClickButtonDialog<Long> {
                                        override fun onClickButtonData(obj: Long?) {
                                            ActivityHelper.startActivity<GiftShopActivity, Long>(activity, ConstantsLoyalty.ID, obj
                                                    ?: -1)
                                        }
                                    }, object : IDismissDialog {
                                override fun onDismiss() {
                                    mPickerScan(false)
                                }
                            }, "Đổi quà bằng điểm tích lũy ngay để\nnhận những phần quà cực hấp dẫn!")
                        } else {
                            DialogHelperGame.dialogAccumulatePointSuccess(activity,
                                    it?.point,
                                    it?.statistic?.owner?.logo?.medium,
                                    it?.statistic?.owner?.name, campaignId,
                                    nameCampaign,
                                    R.drawable.bg_gradient_button_orange_yellow, null,
                                    object : IClickButtonDialog<Long> {
                                        override fun onClickButtonData(obj: Long?) {
                                            activity.onBackPressed()
                                        }
                                    }, object : IDismissDialog {
                                override fun onDismiss() {
                                    mPickerScan(false)
                                }
                            })
                        }
                    }, 300)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                ToastHelper.showLongError(activity, error?.message)
            }
        })
    }
}