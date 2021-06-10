package vn.icheck.android.loyalty.helper

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.view.View
import androidx.fragment.app.FragmentActivity
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.base.showCustomErrorToast
import vn.icheck.android.loyalty.dialog.DialogChucBanMayMan
import vn.icheck.android.loyalty.dialog.DialogReceiveGiftSuccess
import vn.icheck.android.loyalty.dialog.DialogSuccessScanGame
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.repository.CampaignRepository
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.OnBoardingActivity
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.GameActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity
import vn.icheck.android.loyalty.sdk.CampaignType

object CampaignLoyaltyHelper {
    const val REQUEST_GET_GIFT = 19
    const val REQUEST_CHECK_CODE = 20

    fun getCampaign(activity: FragmentActivity, barcode: String, listener: IClickListener, callback: ILoginListener) {
        CampaignRepository().getCampaign(barcode, object : ICApiListener<ICKResponse<ICKLoyalty>> {
            override fun onSuccess(obj: ICKResponse<ICKLoyalty>) {
                if (obj.data != null) {
                    if (obj.data?.introduction_image != null) {
                        DialogHelperGame.dialogAdsCampaign(activity, obj.data?.introduction_image!!.original, object : IDismissDialog {
                            override fun onDismiss() {

                            }
                        })
                    }

                    if (obj.data?.has_chance_code == true) {
                        listener.onClick(obj.data!!)
                    } else {
                        if (SessionManager.isLogged) {
                            when (obj.data?.type) {
                                "receive_gift" -> {
                                    getReceiveGift(activity, barcode, null, obj.data?.name
                                            ?: "", null)
                                }
                                "accumulate_point" -> {
                                    getAccumulatePoint(activity, obj.data!!, null, barcode, null)
                                }
                                "accumulation_long_term_point" -> {
                                    getPointLongTime(activity, obj.data!!, null, barcode, null)
                                }
                                else -> {
                                    getMiniGame(activity, obj.data!!, null, barcode, null)
                                }
                            }
                        } else {
                            /**
                             * Dialog Login
                             */
                            callback.showDialogLogin(obj.data!!, null)
                        }
                    }
                }
            }

            override fun onError(error: ICKBaseResponse?) {

            }
        })
    }

    fun getCampaignQrMar(activity: FragmentActivity, barcode: String?, callback: ILoginListener) {
        CampaignRepository().getCampaignQrMar(barcode ?: "", object : ICApiListener<ICKResponse<ICKLoyalty>> {
            override fun onSuccess(obj: ICKResponse<ICKLoyalty>) {
                if (obj.data != null) {
                    if (obj.data?.introduction_image != null) {
                        DialogHelperGame.dialogAdsCampaign(activity, obj.data?.introduction_image!!.original, object : IDismissDialog {
                            override fun onDismiss() {

                            }
                        })
                    }

                    if (obj.data?.has_chance_code == false) {
                        if (SessionManager.isLogged) {
                            when (obj.data?.type) {
                                CampaignType.RECEIVE_GIFT_QR_MAR -> {
                                    getReceiveGift(activity, null, barcode, obj.data?.name
                                            ?: "", null)
                                }
                                CampaignType.ACCUMULATE_POINT_QR_MAR -> {
                                    getAccumulatePoint(activity, obj.data!!, null, barcode, null)
                                }
                                CampaignType.ACCUMULATE_LONG_TERM_POINT_QR_MAR -> {
                                    getPointLongTime(activity, obj.data!!, null, barcode, null)
                                }
                                else -> {
                                    getMiniGame(activity, obj.data!!, barcode, null, null)
                                }
                            }
                        } else {
                            /**
                             * Dialog Login
                             */
                            callback.showDialogLogin(obj.data!!, null)
                        }
                    }
                }
            }

            override fun onError(error: ICKBaseResponse?) {

            }
        })
    }

    fun checkCodeLoyalty(activity: FragmentActivity, data: ICKLoyalty, code: String, barcode: String, listener: IRemoveHolderInputLoyaltyListener?, callback: ILoginListener) {
        if (code.isEmpty()) {
            showCustomErrorToast(activity, "Mã dự thưởng không được để trống\nVui lòng kiểm tra lại")
            return
        }

        if (NetworkHelper.isNotConnected(activity)) {
            ToastHelper.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (SessionManager.isLogged) {
            when (data.type) {
                "accumulate_point" -> {
                    /**
                     * Tích điểm đổi quà
                     */
                    getAccumulatePoint(activity, data, code, barcode, listener)
                }
                "accumulation_long_term_point" -> {
                    /**
                     * Tích điểm dài hạn
                     */
                    getPointLongTime(activity, data, code, barcode, listener)
                }
                "receive_gift" -> {
                    /**
                     * Nhận quà
                     */
                    getReceiveGift(activity, barcode, code, data.name ?: "", listener)
                }
                else -> {
                    /**
                     * Vòng quay may mắn
                     */
                    getMiniGame(activity, data, code, barcode, listener)
                }
            }
        } else {
            /**
             * Dialog Login
             */
            callback.showDialogLogin(data, code)
        }
    }

    private fun getAccumulatePoint(activity: FragmentActivity, data: ICKLoyalty, code: String?, target: String?, listener: IRemoveHolderInputLoyaltyListener?) {
        CampaignRepository().postAccumulatePoint(data.id
                ?: -1, code, target, object : ICApiListener<ICKResponse<ICKAccumulatePoint>> {
            override fun onSuccess(obj: ICKResponse<ICKAccumulatePoint>) {
                if (obj.statusCode != 200) {

                    when (obj.status) {
                        "INVALID_CUSTOMER" -> {
                            listener?.onRemoveHolderInput()
                            DialogHelperGame.dialogCustomerError(activity,
                                    R.drawable.ic_error_scan_game,
                                    "Bạn không thuộc danh sách\ntham gia chương trình",
                                    "Liên hệ với Doanh nghiệp để biết thêm " +
                                            "chi tiết", object : IClickButtonDialog<ICKNone> {
                                override fun onClickButtonData(obj: ICKNone?) {

                                }
                            })
                        }
                        "USED_CODE" -> {
                            listener?.onRemoveHolderInput()
                            showCustomErrorToast(activity, obj.data?.message
                                    ?: "Mã $code không hợp lệ")
                        }
                        else -> {
                            showCustomErrorToast(activity, obj.data?.message
                                    ?: "Mã $code không hợp lệ")
                        }
                    }
                } else {
                    listener?.onRemoveHolderInput()

                    Handler().postDelayed({
                        DialogHelperGame.dialogAccumulatePointSuccess(activity,
                                obj.data?.point,
                                obj.data?.statistic?.owner?.logo?.medium,
                                obj.data?.statistic?.owner?.name,
                                obj.data?.statistic?.campaign_id,
                                data.name ?: "",
                                R.drawable.bg_gradient_button_orange_yellow,
                                null,
                                object : IClickButtonDialog<Long> {
                                    override fun onClickButtonData(id: Long?) {
                                        SharedLoyaltyHelper(activity).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS, data.has_chance_code
                                                ?: false)
                                        activity.startActivity(Intent(activity, OnBoardingActivity::class.java).apply {
                                            putExtra(ConstantsLoyalty.DATA_1, id)
                                            putExtra(ConstantsLoyalty.DATA_2, data.image?.original)
                                            putExtra(ConstantsLoyalty.DATA_3, data.description)
                                        })
                                    }

                                }, object : IDismissDialog {
                            override fun onDismiss() {

                            }
                        })
                    }, 300)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                showCustomErrorToast(activity, error?.message
                        ?: "Mã $code không hợp lệ")
            }
        })
    }

    private fun getPointLongTime(activity: FragmentActivity, data: ICKLoyalty, code: String?, target: String?, listener: IRemoveHolderInputLoyaltyListener?) {
        CampaignRepository().postAccumulatePoint(data.id
                ?: -1, code, target, object : ICApiListener<ICKResponse<ICKAccumulatePoint>> {
            override fun onSuccess(obj: ICKResponse<ICKAccumulatePoint>) {
                if (obj.statusCode != 200) {

                    when (obj.status) {
                        "INVALID_CUSTOMER" -> {
                            listener?.onRemoveHolderInput()
                            DialogHelperGame.dialogCustomerError(activity,
                                    R.drawable.ic_error_scan_game,
                                    "Bạn không thuộc danh sách\ntham gia chương trình",
                                    "Liên hệ với Doanh nghiệp để biết thêm chi tiết", object : IClickButtonDialog<ICKNone> {
                                override fun onClickButtonData(obj: ICKNone?) {

                                }
                            })
                        }
                        "USED_CODE" -> {
                            listener?.onRemoveHolderInput()
                            showCustomErrorToast(activity, obj.data?.message
                                    ?: "Mã $code không hợp lệ")
                        }
                        else -> {
                            showCustomErrorToast(activity, obj.data?.message
                                    ?: "Mã $code không hợp lệ")
                        }
                    }
                } else {
                    listener?.onRemoveHolderInput()

                    Handler().postDelayed({
                        DialogHelperGame.dialogAccumulatePointSuccess(activity,
                                obj.data?.point,
                                obj.data?.statistic?.owner?.logo?.medium,
                                obj.data?.statistic?.owner?.name,
                                obj.data?.statistic?.business_owner_id
                                        ?: obj.data?.statistic?.owner?.id,
                                data.name ?: "",
                                R.drawable.bg_gradient_button_blue,
                                obj.data?.statistic?.point_loyalty?.point_name,
                                object : IClickButtonDialog<Long> {
                                    override fun onClickButtonData(obj: Long?) {
                                        SharedLoyaltyHelper(activity).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_POINT_LONG_TIME, data.has_chance_code
                                                ?: false)
                                        activity.startActivity(Intent(activity, GiftShopActivity::class.java).apply {
                                            putExtra(ConstantsLoyalty.ID, obj ?: -1)
                                        })
                                    }

                                }, object : IDismissDialog {
                            override fun onDismiss() {

                            }
                        }, "Đổi quà bằng điểm tích lũy ngay để\nnhận những phần quà cực hấp dẫn!")
                    }, 300)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                showCustomErrorToast(activity, error?.message
                        ?: "Mã $code không hợp lệ")
            }
        })
    }

    fun getReceiveGift(activity: FragmentActivity, barcode: String?, code: String?, nameCampaign: String, listener: IRemoveHolderInputLoyaltyListener?) {
        CampaignRepository().postReceiveGift(barcode, code, object : ICApiListener<ICKResponse<ICKReceiveGift>> {
            override fun onSuccess(obj: ICKResponse<ICKReceiveGift>) = if (obj.data != null) {
                if (obj.statusCode != 200) {
                    when (obj.status) {
                        "OUT_OF_GIFT" -> {
                            object : DialogChucBanMayMan(activity) {

                            }.show()
                        }
                        "USED_CODE" -> {
                            listener?.onRemoveHolderInput()
                            showCustomErrorToast(activity, obj.data?.message
                                    ?: "Mã $code không hợp lệ")
                        }
                        "INVALID_CUSTOMER" -> {
                            listener?.onRemoveHolderInput()
                            DialogHelperGame.dialogCustomerError(activity,
                                    R.drawable.ic_error_scan_game,
                                    "Bạn không thuộc danh sách\ntham gia chương trình",
                                    "Liên hệ với Doanh nghiệp để biết thêm chi tiết", object : IClickButtonDialog<ICKNone> {
                                override fun onClickButtonData(obj: ICKNone?) {

                                }
                            })
                        }
                        else -> {
                            showCustomErrorToast(activity, obj.data?.message
                                    ?: "Mã $code không hợp lệ")
                        }
                    }
                } else {
                    when {
                        obj.data?.message.isNullOrEmpty() && obj.data?.gift != null -> {
                            listener?.onRemoveHolderInput()

                            if (obj.data?.gifts?.size!! > 1) {
                                DialogReceiveGiftSuccess.showDialogReceiveGiftSuccess(activity, null, obj.data?.gift?.name, null, nameCampaign, obj.data?.gift?.image?.original, obj.data?.gifts, obj.data?.winner?.id?.toLong()
                                        ?: -1, false, isVoucher = false)
                            } else {
                                when (obj.data?.gift?.type) {
                                    "ICOIN" -> {
                                        DialogReceiveGiftSuccess.showDialogReceiveGiftSuccess(activity, null, obj.data?.gift?.name, null, nameCampaign, obj.data?.gift?.image?.original, obj.data?.gifts, obj.data?.winner?.id?.toLong()
                                                ?: -1, true, isVoucher = false)
                                    }
                                    "VOUCHER" -> {
                                        DialogReceiveGiftSuccess.showDialogReceiveGiftSuccess(activity, null, obj.data?.gift?.name, null, nameCampaign, obj.data?.gift?.image?.original, obj.data?.gifts, obj.data?.winner?.id?.toLong()
                                                ?: -1, isCoin = false, isVoucher = true)
                                    }
                                    else -> {
                                        DialogReceiveGiftSuccess.showDialogReceiveGiftSuccess(activity, null, obj.data?.gift?.name, null, nameCampaign, obj.data?.gift?.image?.original, obj.data?.gifts, obj.data?.winner?.id?.toLong()
                                                ?: -1, isCoin = false, isVoucher = false)
                                    }
                                }
                            }
                        }
                        else -> {
                            object : DialogChucBanMayMan(activity) {

                            }.show()
                        }
                    }
                }
            } else {
                showCustomErrorToast(activity, obj.data?.message
                        ?: "Mã $code không hợp lệ")
            }

            override fun onError(error: ICKBaseResponse?) {
                showCustomErrorToast(activity, error?.message
                        ?: "Mã không hợp lệ")
            }
        })
    }

    private fun getMiniGame(activity: FragmentActivity, data: ICKLoyalty, code: String?, target: String?, listener: IRemoveHolderInputLoyaltyListener?) {
        CampaignRepository().postGameGift(data.id
                ?: -1, target, code, object : ICApiListener<ICKResponse<DataReceiveGameResp>> {
            override fun onSuccess(obj: ICKResponse<DataReceiveGameResp>) {
                if (obj.statusCode != 200) {
                    if (!obj.data?.message.isNullOrEmpty()) {
                        when (obj.status) {
                            "OUT_OF_TURN", "USED_TARGET" -> {
                                listener?.onRemoveHolderInput()

                                showCustomErrorToast(activity, obj.data?.message!!)
                            }
//                            "INVALID_PARAM" -> {
//
//                            }
                            "INVALID_CUSTOMER" -> {
                                listener?.onRemoveHolderInput()

                                DialogHelperGame.dialogCustomerError(activity,
                                        R.drawable.ic_error_scan_game,
                                        "Bạn không thuộc danh sách\ntham gia chương trình",
                                        "Liên hệ với Doanh nghiệp để biết thêm chi tiết", object : IClickButtonDialog<ICKNone> {
                                    override fun onClickButtonData(obj: ICKNone?) {

                                    }
                                })
                            }
                            else -> {
                                showCustomErrorToast(activity, obj.data?.message
                                        ?: "Mã ${code ?: target} không hợp lệ!")
                            }
                        }
                    } else {
                        showCustomErrorToast(activity, obj.data?.message
                                ?: "Mã ${code ?: target} không hợp lệ!")
                    }
                } else {
                    listener?.onRemoveHolderInput()

                    SharedLoyaltyHelper(activity).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM, obj.data?.campaign?.hasChanceCode
                            ?: false)

                    object : DialogSuccessScanGame(activity, "Bạn có thêm ${obj.data?.play ?: 0} lượt quay", data.name
                            ?: obj.data?.campaign?.name ?: "", data.owner?.name
                            ?: "", data.owner?.logo?.thumbnail ?: "") {
                        override fun onDone() {
                            dismiss()
                            if (data.has_chance_code != null) {
                                SharedLoyaltyHelper(activity).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM, obj.data?.campaign?.hasChanceCode
                                        ?: false)

                                val item = obj.data?.campaign

                                val campaign = ListGameCampaign(header_image_rotation = obj.data?.campaign?.header_image_rotation, background_rotation = obj.data?.campaign?.background_rotation)

                                val rowItem = RowsItem(obj.data?.play, item?.updatedAt, data.owner?.icheck_id, null, null, item?.createdAt, campaign, item?.id, item?.deletedAt, obj.data?.campaign?.id)

                                activity.startActivity(Intent(activity, GameActivity::class.java).apply {
                                    putExtra(ConstantsLoyalty.DATA_1, data.id)
                                    putExtra("owner", data.owner?.name)
                                    putExtra("banner", data.image?.original)
                                    putExtra("state", data.status_time)
                                    putExtra("campaignName", obj.data?.campaign?.name)
                                    putExtra("landing", obj.data?.campaign?.landing_page)
                                    putExtra("titleButton", obj.data?.campaign?.title_button)
                                    putExtra("schema", obj.data?.campaign?.schema_button)
                                    putExtra("data", rowItem)
                                })

                            } else {
                                ToastHelper.showLongError(activity, ApplicationHelper.getApplicationByReflect().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                            }
                        }

                        override fun onDismiss() {

                        }
                    }.show()
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                showCustomErrorToast(activity, error?.message
                        ?: "Mã $code không hợp lệ")
            }
        })
    }

    interface IRemoveHolderInputLoyaltyListener {
        fun onRemoveHolderInput()
    }

    interface ILoginListener {
        fun showDialogLogin(data: ICKLoyalty, code: String?)
    }
}