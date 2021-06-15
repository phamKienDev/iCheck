package vn.icheck.android.screen.user.detail_my_reward

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ActivityDetailMyRewardBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.network.models.campaign.DetailRewardData
import vn.icheck.android.network.models.campaign.DetailRewardResponse
import vn.icheck.android.screen.dialog.CallPhoneDialog
import vn.icheck.android.screen.user.detail_my_reward.bottom_sheet.RefuseRoundedBottomSheet
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.text.HtmlImageGetter
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailMyRewardActivity : BaseActivityMVVM() {

    private lateinit var viewModel: DetailMyRewardViewModel

    private lateinit var bottomRefuse: RefuseRoundedBottomSheet

    private lateinit var binding: ActivityDetailMyRewardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMyRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(10000)
            if (!viewModel.loadSuccess) {
                showShortErrorToast(rText(R.string.co_loi_xay_ra_vui_long_thu_lai_sau))
                dismissLoadingScreen()
                finish()
            }
        }
        viewModel = ViewModelProvider(this).get(DetailMyRewardViewModel::class.java)
        viewModel.error.observe(this, Observer{
            showShortErrorToast(rText(R.string.co_loi_xay_ra_vui_long_thu_lai_sau))
            dismissLoadingScreen()
        })
        listener()
        listenerGetData()

        binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.recyclerView.scrollY
            if (scrollY > 50.dpToPx()) {
                binding.toolbar.simpleGoneAnim()
                binding.toolbarAlpha.simpleVisibleAnim()
            } else {
                binding.toolbar.simpleVisibleAnim()
                binding.toolbarAlpha.simpleGoneAnim()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DialogHelper.showLoading(this)

        viewModel.getDetailReward(intent?.getStringExtra(Constant.DATA_1)).observe(this, Observer {
            dismissLoadingScreen()
            viewModel.loadSuccess = true
            checkData(it)
        })
    }

    private fun checkData(it: DetailRewardResponse?) {
        try {
            lifecycleScope.launch {
                withTimeoutOrNull(10000) {
                    it?.data?.let { data ->
                        if (data.rewardType != "LUCKY") {
                            binding.recyclerView.beVisible()
                            bindData(data)
                        } else {
                            binding.recyclerView.beGone()
                            //                binding.groupLanding.beVisible()
                            WebViewActivity.start(this@DetailMyRewardActivity, it.data?.landingUrl)
                            finish()
                            //                binding.landingPage.loadSimpleImage(data.image)
                            //                binding.btnShare.setOnClickListener {
                            //                    data.landingUrl?.createShareAction(this)
                            //                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            dismissLoadingScreen()
        }
    }

    private fun bindData(data: DetailRewardData) {
        binding.imgBanner loadSimpleBanner data.campaignBanner
        binding.tvNameProduct simpleText data.name
        binding.tvCongty simpleText data.shopName
        binding.imgLogo.loadImageWithHolder(data.image, R.drawable.default_product_image)
        binding.txtTitle simpleText data.name

        binding.imgLogoSupplier.loadImageWithHolder(data.shopImage, R.drawable.ic_business_v2)
        binding.tvNameSupplier simpleText data.shopName
        if (!data.desc.isNullOrEmpty()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                binding.tvInformation.setText(Html.fromHtml(data.desc, Html.FROM_HTML_MODE_COMPACT, HtmlImageGetter().apply {
                    try {
                        size = binding.tvInformation.width
                    } catch (e: Exception) {
                    }
                }, null))
            } else {
                binding.tvInformation.setText(Html.fromHtml(data.desc, HtmlImageGetter().apply {
                    try {
                        size = binding.tvInformation.width
                    } catch (e: Exception) {
                    }
                }, null))
            }
        } else {
            binding.tvInformation.beGone()
        }
        when (data.rewardType) {
            "CARD" -> {
                if (data.dataRps != null) {
                    binding.tvRefuse rText R.string.so_serial
                    binding.tvMathecao.beVisible()
                    binding.tvState.beVisible()
                    binding.tvState.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                    binding.tvState simpleText data.dataRps?.pin
                    binding.tvRefuseDes simpleText data.dataRps?.serial
                    binding.tvState.setTextSize(14f)
                    binding.tvRefuseDes.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                    binding.tvTime rText R.string.han_su_dung
                    binding.tvTimeDes simpleText data.dataRps?.expiredDate?.getDayTime()
                    if (data.usingState == 1) {

                        binding.layoutBottom.beVisible()
                        binding.btnRefuse rText R.string.danh_dau_da_dung
                        binding.btnAcceptDaLay rText R.string.dung_ngay
                        binding.btnRefuse.setOnClickListener {
                            showLoadingTimeOut(10000)
                            try {
                                viewModel.updateUsingState().observe(this, Observer {
                                    dismissLoadingScreen()
                                    showShortSuccessToast(getString(R.string.ban_da_danh_dau_nap_the_nay))
                                    getData()
    //                                binding.layoutBottom.beGone()
    //                                binding.imgLogo.alpha = 0.5f
    //                                binding.imgUsed.beVisible()
    //                                binding.tvState.setTextColor(Color.parseColor("#757575"))
    //                                binding.tvRefuseDes.setTextColor(Color.parseColor("#757575"))
    //                                binding.tvTime simpleText "Ngày dùng"
    //                                binding.tvTimeDes.setTextColor(Color.parseColor("#757575"))
    //                                binding.tvTimeDes.setText(data.confirmTime?.getDayTime())
                                })
                            } catch (e: Exception) {
                                dismissLoadingScreen()
                            }

                        }
                        binding.btnAcceptDaLay.setOnClickListener {
                            object : CallPhoneDialog(this, data.dataRps?.pin.toString()) {
                                override fun actionCancel() {
                                }

                                override fun actionOk(phone: String) {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:*100*${phone}${Uri.encode("#")}")
                                    startActivity(intent)
                                }
                            }.show()
                        }
                    } else {
                        binding.layoutBottom.beGone()
                        binding.imgLogo.alpha = 0.5f
                        binding.imgUsed.beVisible()
                        binding.tvState.setTextColor(Color.parseColor("#757575"))
                        binding.tvRefuseDes.setTextColor(Color.parseColor("#757575"))
                        binding.tvTime rText R.string.ngay_dung
                        binding.tvTimeDes.setTextColor(Color.parseColor("#757575"))
                        binding.tvTimeDes.text = data.confirmTime?.getDayTime()
                    }

                } else {
                    DialogHelper.showLoading(this)
//                    binding.tvRefuse simpleText "Số serial"
//                    binding.tvMathecao.beVisible()
//                    binding.tvState.beVisible()
//                    binding.tvState.setTextColor(Color.parseColor("#85C440"))
//                    binding.tvState simpleText "Đang cập nhật"
//                    binding.tvRefuseDes simpleText "Đang cập nhật"
//                    binding.tvState.setTextSize(14f)
//                    binding.tvRefuseDes.setTextColor(Color.parseColor("#85C440"))
//                    binding.tvTime simpleText "Hạn sử dụng"
//                    binding.tvTimeDes simpleText "Đang cập nhật"
                    viewModel.getCard().observe(this, Observer {
                        DialogHelper.closeLoading(this)
                        checkData(it)
                    })
                }
            }
            "PRODUCT_SHIP" -> {
                binding.btnRefuse.setOnClickListener {
                    bottomRefuse = RefuseRoundedBottomSheet(viewModel.dataReward.id.toString())
                    bottomRefuse.show(supportFragmentManager, bottomRefuse.tag)

                    bottomRefuse.setListener(object : RefuseRoundedBottomSheet.DialogClickListener {
                        override fun buttonClick(position: Int, listId: MutableList<Int>, listMessage: MutableList<String>) {
                            if (listId.isNotEmpty()) {
                                viewModel.refuseGift(listId, listMessage)
                            } else {
                                showShortErrorToast(rText(R.string.vui_long_chon_it_nhat_mot_ly_do))
                            }
                        }
                    })
                    binding.btnAcceptDaLay.setOnClickListener {

                    }
                }
                checkState(data)
            }
            "PRODUCT_IN_SHOP" -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val date = sdf.parse(data.expiredAt.toString()) ?: Calendar.getInstance().time
                    val currentDate = Calendar.getInstance().time
                    if (date.time < currentDate.time) {
                        binding.tvRefuse rText R.string.han_lay_qua
                        binding.tvRefuseDes rText R.string.het_han
                        binding.tvTime rText R.string.loai_qua
                        binding.tvTimeDes rText R.string.qua_lay_tai_cua_hang
                        binding.layoutBottom.beGone()
                        binding.btnAcceptDaLay.setOnClickListener {
                            startActivityForResult(Intent(this, ShipActivity::class.java).apply {
                                putExtra("gift", viewModel.detailReward)
                            }, 1)
//                    simpleStartActivity(ShipActivity::class.java)
                        }
                    } else {
                        binding.tvRefuse rText R.string.han_lay_qua
                        binding.tvRefuseDes simpleText TimeHelper.convertDateSvToDateVn(data.expiredAt)
                        binding.tvTime rText R.string.loai_qua
                        binding.tvTimeDes rText R.string.qua_lay_tai_cua_hang
                        binding.groupAddress.beVisible()
                        binding.tvAddress simpleText data.address
                    }
                } catch (e: Exception) {
                }

            }
        }


    }

    private fun getData() {
        viewModel.getDetailReward(intent?.getStringExtra(Constant.DATA_1)).observe(this, Observer {
            checkData(it)
        })
    }

    private fun checkState(data: DetailRewardData) {
        when (data.state) {
            2 -> {
                if (data.dataRps?.shipTime.isNullOrEmpty()) {
                    binding.tvRefuse rText R.string.thoi_gian_xac_nhan_space
                    binding.tvRefuse.post {
                        val lp = binding.tvRefuse.layoutParams as ConstraintLayout.LayoutParams
                        lp.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                        lp.setMargins(lp.leftMargin, lp.topMargin, 8.dpToPx(), lp.bottomMargin)
                        binding.tvRefuse.layoutParams = lp
                    }

                    binding.tvRefuseDes simpleText data.confirmTime?.getHourMinutesTime()
                    resetTvWidth()
                    binding.tvMathecao.beVisible()
                    binding.tvMathecao rText R.string.da_xac_nhan_giao_qua
                    binding.tvMathecao.setTextColor(Color.parseColor("#057DDA"))
                    binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                    binding.layoutBottom.beGone()
                    binding.tvTime.beInvisible()
                    binding.tvTimeDes.beInvisible()
                } else {
                    binding.tvRefuse rText R.string.thoi_gian_nhan_space
                    binding.tvRefuse.post {
                        val lp = binding.tvRefuse.layoutParams as ConstraintLayout.LayoutParams
                        lp.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                        lp.setMargins(lp.leftMargin, lp.topMargin, 8.dpToPx(), lp.bottomMargin)
                        binding.tvRefuse.layoutParams = lp
                    }
                    binding.tvRefuseDes simpleText data.dataRps?.shipTime?.getHourMinutesTime()
                    binding.tvTime.beInvisible()
                    binding.tvTimeDes.beInvisible()

                    resetTvWidth()
                    binding.tvMathecao.beVisible()
                    binding.tvMathecao rText R.string.giao_qua_thanh_cong
                    binding.tvMathecao.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                    binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                    binding.layoutBottom.beGone()
                }
            }
            3 -> {
                binding.tvRefuse rText R.string.ly_do_tu_choi
                if (!data.reasonOther.isNullOrEmpty()) {
                    binding.tvRefuseDes simpleText data.reasonOther
                } else {
                    binding.tvRefuseDes rText R.string.khac
                }
                binding.tvTime rText R.string.thoi_gian_tu_choi
                binding.tvTimeDes simpleText data.cancelTime?.getHourMinutesTime()

                resetTvWidth()
                binding.tvMathecao.beVisible()
                binding.tvMathecao rText R.string.ban_da_tu_choi_nhan_qua_nay
                binding.tvMathecao.setTextColor(ContextCompat.getColor(this, R.color.colorAccentRed))
                binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                binding.layoutBottom.beGone()
            }
            4 -> {
                binding.tvRefuse rText R.string.thoi_gian_giao
                binding.tvRefuseDes simpleText data.receiveAt?.getHourMinutesTime()
                binding.tvTime.beGone()
                binding.tvTimeDes.beGone()

                resetTvWidth()
                binding.tvMathecao.beVisible()
                binding.tvMathecao rText R.string.giao_qua_thanh_cong
                binding.tvMathecao.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                binding.layoutBottom.beGone()
            }
            else -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val date = sdf.parse(data.expiredAt.toString()) ?: Calendar.getInstance().time
                    val currentDate = Calendar.getInstance().time
                    if (date.time < currentDate.time) {
                        binding.tvRefuse rText R.string.han_nhan_qua
                        binding.tvRefuseDes rText R.string.het_han

                        binding.tvTime rText R.string.loai_qua
                        binding.tvTimeDes rText R.string.qua_giao_tan_noi
                        binding.tvRefuseDes.setTextColor(Color.parseColor("#757575"))
                        binding.tvTimeDes.setTextColor(Color.parseColor("#757575"))
                        binding.layoutBottom.beGone()
                        binding.btnAcceptDaLay.setOnClickListener {
                            TrackingAllHelper.tagGiftDeliveryStarted(viewModel.detailReward?.data?.campaignId, viewModel.detailReward?.data?.name)
                            startActivityForResult(Intent(this, ShipActivity::class.java).apply {
                                putExtra("gift", viewModel.detailReward)
                            }, 1)
                        }
                    } else {
                        binding.tvRefuse rText R.string.han_nhan_qua
                        binding.tvRefuseDes simpleText TimeHelper.convertDateSvToDateVn(data.expiredAt)
                        binding.tvTime rText R.string.loai_qua
                        binding.tvTimeDes rText R.string.qua_giao_tan_noi
                        binding.layoutBottom.beVisible()
                        binding.btnAcceptDaLay.setOnClickListener {
                            TrackingAllHelper.tagGiftDeliveryStarted(viewModel.detailReward?.data?.campaignId, viewModel.detailReward?.data?.name)
                            startActivityForResult(Intent(this, ShipActivity::class.java).apply {
                                putExtra("gift", viewModel.detailReward)
                            }, 1)
                        }
                    }
                } catch (e: Exception) {
                }

            }
        }
    }

    private fun resetTvWidth() {
        val lp = binding.tvMathecao.layoutParams as ConstraintLayout.LayoutParams
        lp.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        binding.tvMathecao.layoutParams = lp
    }

    private fun listener() {
        binding.imgBackGray.setOnClickListener {
            onBackPressed()
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK_TO_SHAKE))
        }

//        binding.btnRefuse.setOnClickListener {
//            if (binding.btnRefuse.text == getString(R.string.danh_dau_da_dung)) {
//                DialogHelper.showDialogSuccessBlack(this, getString(R.string.ban_da_danh_dau_nap_the_nay), R.style.DialogBgDisableTransparentFullScreen)
//
//                binding.layoutBottom.visibility = View.GONE
//                viewModel.dataReward.state = 2
//            } else {
//                bottomRefuse = RefuseRoundedBottomSheet(viewModel.dataReward.id.toString())
//                bottomRefuse.show(supportFragmentManager, bottomRefuse.tag)
//
//                bottomRefuse.setListener(object : RefuseRoundedBottomSheet.DialogClickListener {
//                    override fun buttonClick(position: Int, listId: MutableList<Int>, listMessage: MutableList<String>) {
//                        viewModel.refuseGift( listId, listMessage)
//                    }
//                })
//            }
//        }
//
//        binding.btnAcceptDaLay.setOnClickListener {
//            if (binding.btnAcceptDaLay.text == getString(R.string.dung_ngay)) {
//                object : CallPhoneDialog(this, "12341234") {
//                    override fun actionCancel() {
//                    }
//
//                    override fun actionOk(phone: String) {
//                        val intent = Intent(Intent.ACTION_DIAL)
//                        intent.data = Uri.parse("tel:*100*${phone}${Uri.encode("#")}")
//                        startActivity(intent)
//                    }
//                }.show()
//            } else {
//
//            }
//        }

        binding.btnFollowGift.setOnClickListener {
            if (binding.btnFollowGift.text == getString(R.string.chia_se)) {
                val share = Intent()
                share.setAction(Intent.ACTION_SEND)
                share.putExtra(Intent.EXTRA_TEXT, viewModel.dataReward.image)
                share.setType("text/plain")
                startActivity(Intent.createChooser(share, rText(R.string.chia_se)))
            }
        }
    }

    private fun listenerGetData() {
        viewModel.refuseGift.observe(this, Observer {
            bottomRefuse.dismiss()
            viewModel.getDetailReward(intent?.getStringExtra(Constant.DATA_1)).observe(this, Observer {
                checkData(it)
            })
        })

        viewModel.errorData.observe(this, Observer {
            when (it) {
                Constant.ERROR_UNKNOW -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {

                        }
                    })
                }

                Constant.ERROR_EMPTY -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {

                        }
                    })
                }

                Constant.ERROR_SERVER -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {

                        }
                    })
                }

                Constant.ERROR_INTERNET -> {
                    DialogHelper.showNotification(this, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {

                        }
                    })
                }
            }
        })
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                recreate()
//                dismissLoadingScreen()
//            }
//        }
//    }
}