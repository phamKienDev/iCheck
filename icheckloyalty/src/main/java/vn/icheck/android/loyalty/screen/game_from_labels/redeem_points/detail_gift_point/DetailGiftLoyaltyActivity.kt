package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.detail_gift_point

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail_gift_loyalty.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ichecklibs.util.rText
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
                    btnDoiQua rText R.string.huong_dan_doi_qua
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
                                        R.drawable.ic_error_scan_game, rText(R.string.ban_khong_du_diem_doi_qua),
                                        rText(R.string.tich_cuc_tham_gia_cac_chuong_tring_cua_nhan_hang_de_nhan_diem_thanh_vien_nhé),
                                        null, rText(R.string.tich_diem_ngay), false, R.drawable.bg_button_not_enough_point, R.color.orange_red,
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
                                    DialogHelperGame.dialogNotEnoughPoints(
                                        this,
                                        rText(R.string.ban_khong_du_diem_roi),
                                        rText(R.string.nhap_ma_duoc_dan_tren_bao_bi_san_pham_de_nhan_diem_tich_luy_doi_qua_nhe),
                                        R.drawable.ic_onboarding_nhap,
                                        rText(R.string.nhap_ma_ngay),
                                        true, R.drawable.bg_button_not_enough_point, R.color.orange_red,
                                            object : IClickButtonDialog<ICKNone> {
                                                override fun onClickButtonData(obj: ICKNone?) {

                                                    Handler().postDelayed({
                                                        DialogHelperGame.dialogEnterThePrizeCode(this@DetailGiftLoyaltyActivity,
                                                                R.drawable.ic_nhap_ma_cong_diem,
                                                                rText(R.string.nhap_ma_duoc_dan_tren_dan_pham_de_nhan_diem_tich_luy_doi_qua),
                                                                rText(R.string.nhap_ma_vao_day_i),
                                                                rText(R.string.vui_long_nhap_ma_code), campaignID, R.drawable.bg_gradient_button_orange_yellow,
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
                                    DialogHelperGame.dialogNotEnoughPoints(this, rText(R.string.ban_khong_du_diem_roi),
                                            rText(R.string.quet_tem_qrcode_duoc_dan_tren_bao_bi_san_pham_de_nhan_diem_tich_luy_doi_qua_nhe), R.drawable.ic_onboarding_scan, rText(R.string.quet_tem_ngay), true, R.drawable.bg_button_not_enough_point, R.color.orange_red,
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

                    if (obj?.statusChange?.contains(rText(R.string.het_luot_su_dung)) == true){
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
                                text = context.rText(R.string.dung_ngay)

                                setOnClickListener {
                                    startActivity(Intent(this@DetailGiftLoyaltyActivity, VoucherLoyaltyActivity::class.java).apply {
                                        putExtra(ConstantsLoyalty.DATA_1, obj?.voucher?.code)
                                        putExtra(ConstantsLoyalty.DATA_2, obj?.dateChange)
                                        putExtra(ConstantsLoyalty.DATA_3, obj?.gift?.owner?.logo?.thumbnail)
                                    })
                                }
                            }
                            obj?.voucher?.can_mark_use == true -> {
                                text = context.rText(R.string.danh_dau_da_dung)

                                setOnClickListener {
                                    showCustomErrorToast(this@DetailGiftLoyaltyActivity, context.rText(R.string.chua_co_su_kien))
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
                rText(R.string.qua_xu_icheck)
            }
            "PHONE_CARD" -> {
                rText(R.string.qua_the_cao)
            }
            "RECEIVE_STORE" -> {
                rText(R.string.qua_doi_tai_cua_hang)
            }
            "PRODUCT" -> {
                rText(R.string.qua_giao_tan_noi)
            }
            "VOUCHER" -> {
                rText(R.string.qua_voucher)
            }
            else -> {
                rText(R.string.qua_tinh_than)
            }
        }

        tvProduct.text = if (!obj?.gift?.name.isNullOrEmpty()) {
            obj?.gift?.name
        } else {
            rText(R.string.dang_cap_nhat)
        }

        tvCountGift.text = "${SharedLoyaltyHelper(this@DetailGiftLoyaltyActivity).getLong(ConstantsLoyalty.COUNT_GIFT)} Quà"

        tvPoin.text = if (obj?.points != null) {
            TextHelper.formatMoneyPhay(obj?.points)
        } else {
            rText(R.string.dang_cap_nhat)
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
                    text = context.rText(R.string.cho_xac_nhan)
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.orange))
                    setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                }
                2 -> {
                    setVisible()
                    text = context.rText(R.string.cho_giao)
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.orange))
                    setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                }
                3 -> {
                    setVisible()
                    text = context.rText(R.string.da_nhan_qua)
                    setTextColor(ContextCompat.getColor(this@DetailGiftLoyaltyActivity, R.color.green2))
                    setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                }
                4 -> {
                    setVisible()
                    text = context.rText(R.string.tu_choi)
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