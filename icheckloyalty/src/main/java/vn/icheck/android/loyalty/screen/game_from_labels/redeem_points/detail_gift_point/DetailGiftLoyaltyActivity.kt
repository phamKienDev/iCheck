package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.detail_gift_point

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail_gift_loyalty.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.*
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.screen.game_from_labels.game_list.GameFromLabelsListActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business.CampaignOfBusinessActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard.ChangePhoneCardsActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard.ExchangePhonecardSuccessDialog
import vn.icheck.android.loyalty.screen.voucher.VoucherLoyaltyActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class DetailGiftLoyaltyActivity : BaseActivityGame() {

    private val requestCard = 111

    override val getLayoutID: Int
        get() = R.layout.activity_detail_gift_loyalty

    private var campaignID: Long = -1L
    private var countExchanceGift = 0L

    companion object {
        var obj: ICKBoxGifts? = null
    }

    override fun onInitView() {
        StatusBarHelper.setOverStatusBarDark(this@DetailGiftLoyaltyActivity)
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    private fun initListener() {
        campaignID = intent.getLongExtra(ConstantsLoyalty.DATA_3, -1)
        val type = intent.getIntExtra(ConstantsLoyalty.DATA_7, 1) // phân biệt vào từ màn lịch sử hay không?

        tvDateTime.text = if (!obj?.export_gift_from.isNullOrEmpty() && !obj?.export_gift_to.isNullOrEmpty()) {
            TimeHelper.convertDateTimeSvToDateVn(obj?.export_gift_to)
        } else {
            getString(R.string.dang_cap_nhat)
        }

        setStatusGift(obj?.state)

        if (type == 1) {
            tvStatus.setGone()
            layoutCountGift.setVisible()
            btnDoiQua.setVisible()
            layoutPhiVanChuyen.setGone()
            when (obj?.gift?.type) {
                "ICOIN" -> {
                }
                "PHONE_CARD" -> {
                }
                "RECEIVE_STORE" -> {
                    btnDoiQua.text = "Hướng dẫn đổi quà"
                }
                "PRODUCT" -> {
                    layoutPhiVanChuyen.setVisible()
                }
                "VOUCHER" -> {
                }
                else -> {
                    btnDoiQua.setGone()
                }
            }

            btnDoiQua.setOnClickListener {
                if (obj?.points != null) {
                    when (obj?.gift?.type) {
                        "RECEIVE_STORE" -> {
                            DialogHelperGame.dialogTutorialLoyalty(this, R.drawable.bg_gradient_button_orange_yellow)
                        }
                        "PHONE_CARD" -> {
                            if (obj?.points!! <= SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.POINT_USER_LOYALTY)) {
                                obj?.let { data ->
                                    DialogHelperGame.dialogConfirmExchangeGifts(this@DetailGiftLoyaltyActivity, data, campaignID)
                                }
                            } else {
                                DialogHelperGame.dialogScanLoyaltyError(this@DetailGiftLoyaltyActivity,
                                        R.drawable.ic_error_scan_game, "Bạn không đủ điểm đổi quà!",
                                        "Tích cực tham gia các chương trình của\nnhãn hàng để nhận điểm Thành viên nhé",
                                        null, "Tích điểm ngay", false, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                                        object : IClickButtonDialog<ICKNone> {
                                            override fun onClickButtonData(data: ICKNone?) {
                                                startActivity(Intent(this@DetailGiftLoyaltyActivity, CampaignOfBusinessActivity::class.java).apply {
                                                    putExtra(ConstantsLoyalty.DATA_1, campaignID)
                                                })
                                            }
                                        }, object : IDismissDialog {
                                    override fun onDismiss() {

                                    }
                                })
                            }
//                        viewModel.postExchangeCardGift()
                        }
                        else -> {
                            if (obj?.points!! <= SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.POINT_USER_LOYALTY)) {
                                obj?.let { data ->
                                    DialogHelperGame.dialogConfirmExchangeGifts(this@DetailGiftLoyaltyActivity, data, campaignID)
                                }
                            } else {
                                if (SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS)) {
                                    DialogHelperGame.dialogNotEnoughPoints(this, "Bạn không đủ điểm rồi!",
                                            "Nhập mã được dán trên bao bì\nsản phẩm để nhận điểm tích lũy đổi quà nhé!", R.drawable.ic_onboarding_nhap, "Nhập mã ngay", true, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                                            object : IClickButtonDialog<ICKNone> {
                                                override fun onClickButtonData(obj: ICKNone?) {

                                                    Handler().postDelayed({
                                                        DialogHelperGame.dialogEnterThePrizeCode(this@DetailGiftLoyaltyActivity,
                                                                R.drawable.ic_nhap_ma_cong_diem,
                                                                "Nhập mã được dán trên sản phẩm\nđể nhận điểm tích lũy đổi quà!",
                                                                getString(R.string.nhap_ma_vao_day),
                                                                "Vui lòng nhập mã code", campaignID, R.drawable.bg_gradient_button_orange_yellow,
                                                                object : IClickButtonDialog<ICKAccumulatePoint> {
                                                                    override fun onClickButtonData(obj: ICKAccumulatePoint?) {

                                                                        /**
                                                                         * Dialog Nhập mã thành công
                                                                         */

                                                                        Handler().postDelayed({
                                                                            DialogHelperGame.dialogAccumulatePointSuccess(this@DetailGiftLoyaltyActivity,
                                                                                    obj?.point, obj?.statistic?.owner?.logo?.medium,
                                                                                    obj?.statistic?.owner?.name,
                                                                                    obj?.statistic?.campaign_id,
                                                                                    GameFromLabelsListActivity.name,
                                                                                    R.drawable.bg_gradient_button_orange_yellow, null,
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
                                    DialogHelperGame.dialogNotEnoughPoints(this, "Bạn không đủ điểm rồi!",
                                            "Quét tem QRcode được dán trên bao bì\nsản phẩm để nhận điểm tích lũy đổi quà nhé!", R.drawable.ic_onboarding_scan, "Quét tem ngay", true, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                                            object : IClickButtonDialog<ICKNone> {
                                                override fun onClickButtonData(obj: ICKNone?) {
                                                    LoyaltySdk.openActivity("scan?typeLoyalty=accumulate_point&campaignId=$campaignID")
                                                }
                                            })
                                }
                            }
                        }
                    }
                } else {
                    showLongError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }
        } else {
            layoutPhiVanChuyen.setGone()
            btnDoiQua.setGone()
            layoutCountGift.setGone()
            tvStatus.setVisible()

            when (obj?.gift?.type) {
                "ICOIN" -> {
                }
                "PHONE_CARD" -> {
                }
                "RECEIVE_STORE" -> {
                    tvStatus.setGone()
                }
                "PRODUCT" -> {
                }
                "VOUCHER" -> {
                    tvTitleDate.text = obj?.titleDate

                    tvDateTime.text = obj?.dateChange

                    if (obj?.statusChange?.contains("Hết lượt sử dụng") == true){
                        tvTitleDate.setInvisible()
                        tvDateTime.setInvisible()
                    }else{
                        tvTitleDate.setVisible()
                        tvDateTime.setVisible()
                    }

                    tvStatus.apply {
                        text = obj?.statusChange
                        setTextColor(obj?.colorText ?: 0)
                        setBackgroundResource(obj?.colorBackground ?: 0)
                    }

                    btnDoiQua.setVisible()

                    btnDoiQua.apply {
                        when {
                            obj?.voucher?.can_use == true -> {
                                text = "Dùng ngay"

                                setOnClickListener {
                                    startActivity(Intent(this@DetailGiftLoyaltyActivity, VoucherLoyaltyActivity::class.java).apply {
                                        putExtra(ConstantsLoyalty.DATA_1, obj?.voucher?.code)
                                        putExtra(ConstantsLoyalty.DATA_2, obj?.dateChange)
                                        putExtra(ConstantsLoyalty.DATA_3, obj?.gift?.owner?.logo?.thumbnail)
                                    })
                                }
                            }
                            obj?.voucher?.can_mark_use == true -> {
                                text = "Đánh dấu đã dùng"

                                setOnClickListener {
                                    showCustomErrorToast(this@DetailGiftLoyaltyActivity, "Chưa có sự kiện")
                                }
                            }
                            else -> {
                                setGone()
                            }
                        }
                    }
                }
                else -> {
                    tvStatus.setGone()
                }
            }
        }
        WidgetHelper.loadImageUrl(imgBanner, intent.getStringExtra(ConstantsLoyalty.DATA_2))

        WidgetHelper.loadImageUrl(imgProduct, obj?.gift?.image?.medium)

        tvVanChuyen.text = when (obj?.gift?.type) {
            "ICOIN" -> {
                "Quà Xu iCheck"
            }
            "PHONE_CARD" -> {
                "Quà thẻ cào"
            }
            "RECEIVE_STORE" -> {
                "Quà đổi tại cửa hàng"
            }
            "PRODUCT" -> {
                "Quà giao tận nơi"
            }
            "VOUCHER" -> {
                "Quà voucher"
            }
            else -> {
                "Quà tinh thần"
            }
        }

        tvProduct.text = if (!obj?.gift?.name.isNullOrEmpty()) {
            obj?.gift?.name
        } else {
            getString(R.string.dang_cap_nhat)
        }

        tvCountGift.text = "${SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT)} Quà"

        tvPoin.text = if (obj?.points != null) {
            TextHelper.formatMoneyPhay(obj?.points)
        } else {
            getString(R.string.dang_cap_nhat)
        }

        tvDetailGift.settings.javaScriptEnabled = true
        tvDetailGift.loadData(obj?.gift?.description ?: "", "text/html; charset=utf-8", "UTF-8")

        WidgetHelper.loadImageUrl(imgAvatar, obj?.gift?.owner?.logo?.medium)

        tvNameShop.text = if (!obj?.gift?.owner?.name.isNullOrEmpty()) {
            obj?.gift?.owner?.name
        } else {
            getString(R.string.dang_cap_nhat)
        }

        btnDoiQua.isEnabled = SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT) > 0

        btnDoiQua.background = if (SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT) > 0) {
            ContextCompat.getDrawable(this, R.drawable.bg_gradient_button_orange_yellow)
        } else {
            ContextCompat.getDrawable(this, R.drawable.bg_gray_corner_20dp)
        }
    }

    private fun setStatusGift(state: Int?) {
        tvStatus.run {
            when (state) {
                1 -> {
                    text = "Chờ xác nhận"
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.orange))
                    setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                }
                2 -> {
                    setVisible()
                    text = "Chờ giao"
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.orange))
                    setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                }
                3 -> {
                    setVisible()
                    text = "Đã nhận quà"
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.green2))
                    setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                }
                4 -> {
                    setVisible()
                    text = "Từ chối"
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.orange))
                    setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                }
                else -> {
                    setGone()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when(event.type){
            ICMessageEvent.Type.ON_COUNT_GIFT -> {
                tvCountGift.text = "${SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT)} Quà"
                if (SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT) <= 0){
                    btnDoiQua.isEnabled = false
                    btnDoiQua.background = ContextCompat.getDrawable(this, R.drawable.bg_gray_corner_20dp)
                }
            }
            ICMessageEvent.Type.EXCHANGE_PHONE_CARD -> {
                if (event.data is Long) {
                    ChangePhoneCardsActivity.start(this, event.data, ConstantsLoyalty.TDNH, campaignID, requestCard)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == requestCard) {
                val phone = data?.getStringExtra("phone")
                val provider = data?.getStringExtra("provider")

                val dialog = ExchangePhonecardSuccessDialog(phone, provider)
                dialog.show(supportFragmentManager, null)

                SharedLoyaltyHelper(this).putLong(ConstantsLoyalty.COUNT_GIFT, SharedLoyaltyHelper(this).getLong(ConstantsLoyalty.COUNT_GIFT) - 1)
                tvCountGift.text = "${SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT)} Quà"
                countExchanceGift++
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (countExchanceGift > 0) {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT, countExchanceGift))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        obj = null
    }
}