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
import vn.icheck.android.ichecklibs.ViewHelper
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
                showSimpleErrorToast("Có lỗi xảy ra vui lòng thử lại sau")
                dismissLoadingScreen()
                finish()
            }
        }
        viewModel = ViewModelProvider(this).get(DetailMyRewardViewModel::class.java)
        viewModel.error.observe(this, Observer{
            showSimpleErrorToast("Có lỗi xảy ra vui lòng thử lại sau")
            dismissLoadingScreen()
        })

        setupView()
        listener()
        listenerGetData()

        binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.recyclerView.scrollY
            if (scrollY > 50.toPx()) {
                binding.toolbar.simpleGoneAnim()
                binding.toolbarAlpha.simpleVisibleAnim()
            } else {
                binding.toolbar.simpleVisibleAnim()
                binding.toolbarAlpha.simpleGoneAnim()
            }
        }
    }

    private fun setupView() {
        binding.btnShare.background = ViewHelper.backgroundPrimaryCorners4(this)
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
                    binding.tvRefuse simpleText "Số serial"
                    binding.tvMathecao.beVisible()
                    binding.tvState.beVisible()
                    binding.tvState.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                    binding.tvState simpleText data.dataRps?.pin
                    binding.tvRefuseDes simpleText data.dataRps?.serial
                    binding.tvState.setTextSize(14f)
                    binding.tvRefuseDes.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                    binding.tvTime simpleText "Hạn sử dụng"
                    binding.tvTimeDes simpleText data.dataRps?.expiredDate?.getDayTime()
                    if (data.usingState == 1) {

                        binding.layoutBottom.beVisible()
                        binding.btnRefuse simpleText "Đánh dấu đã dùng"
                        binding.btnAcceptDaLay simpleText "Dùng ngay"
                        binding.btnRefuse.setOnClickListener {
                            showLoadingTimeOut(10000)
                            try {
                                viewModel.updateUsingState().observe(this, Observer {
                                    dismissLoadingScreen()
                                    showSimpleSuccessToast(getString(R.string.ban_da_danh_dau_nap_the_nay))
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
                        binding.tvTime simpleText "Ngày dùng"
                        binding.tvTimeDes.setTextColor(Color.parseColor("#757575"))
                        binding.tvTimeDes.setText(data.confirmTime?.getDayTime())
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
                                showSimpleErrorToast("Vui lòng chọn ít nhất một lí do")
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
                        binding.tvRefuse simpleText "Hạn lấy quà"
                        binding.tvRefuseDes simpleText "Hết hạn"
                        binding.tvTime simpleText "Loại quà"
                        binding.tvTimeDes simpleText "Quà lấy tại cửa hàng"
                        binding.layoutBottom.beGone()
                        binding.btnAcceptDaLay.setOnClickListener {
                            startActivityForResult(Intent(this, ShipActivity::class.java).apply {
                                putExtra("gift", viewModel.detailReward)
                            }, 1)
//                    simpleStartActivity(ShipActivity::class.java)
                        }
                    } else {
                        binding.tvRefuse simpleText "Hạn lấy quà"
                        binding.tvRefuseDes simpleText TimeHelper.convertDateSvToDateVn(data.expiredAt)
                        binding.tvTime simpleText "Loại quà"
                        binding.tvTimeDes simpleText "Quà lấy tại cửa hàng"
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
                    binding.tvRefuse simpleText "Thời gian xác nhận     "
                    binding.tvRefuse.post {
                        val lp = binding.tvRefuse.layoutParams as ConstraintLayout.LayoutParams
                        lp.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                        lp.setMargins(lp.leftMargin, lp.topMargin, 8.toPx(), lp.bottomMargin)
                        binding.tvRefuse.layoutParams = lp
                    }

                    binding.tvRefuseDes simpleText data.confirmTime?.getHourMinutesTime()
                    resetTvWidth()
                    binding.tvMathecao.beVisible()
                    binding.tvMathecao simpleText "Đã xác nhận giao quà"
                    binding.tvMathecao.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this))
                    binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                    binding.layoutBottom.beGone()
                    binding.tvTime.beInvisible()
                    binding.tvTimeDes.beInvisible()
                } else {
                    binding.tvRefuse simpleText "Thời gian nhận     "
                    binding.tvRefuse.post {
                        val lp = binding.tvRefuse.layoutParams as ConstraintLayout.LayoutParams
                        lp.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                        lp.setMargins(lp.leftMargin, lp.topMargin, 8.toPx(), lp.bottomMargin)
                        binding.tvRefuse.layoutParams = lp
                    }
                    binding.tvRefuseDes simpleText data.dataRps?.shipTime?.getHourMinutesTime()
                    binding.tvTime.beInvisible()
                    binding.tvTimeDes.beInvisible()

                    resetTvWidth()
                    binding.tvMathecao.beVisible()
                    binding.tvMathecao simpleText "Giao quà thành công"
                    binding.tvMathecao.setTextColor(ContextCompat.getColor(this, R.color.colorAccentGreen))
                    binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                    binding.layoutBottom.beGone()
                }
            }
            3 -> {
                binding.tvRefuse simpleText "Lý do từ chối"
                if (!data.reasonOther.isNullOrEmpty()) {
                    binding.tvRefuseDes simpleText data.reasonOther
                } else {
                    binding.tvRefuseDes simpleText "Khác"
                }
                binding.tvTime simpleText "Thời gian từ chối"
                binding.tvTimeDes simpleText data.cancelTime?.getHourMinutesTime()

                resetTvWidth()
                binding.tvMathecao.beVisible()
                binding.tvMathecao simpleText "Bạn đã từ chối nhận quà này"
                binding.tvMathecao.setTextColor(ContextCompat.getColor(this, R.color.colorAccentRed))
                binding.tvMathecao.typeface = Typeface.createFromAsset(assets, "font/barlow_semi_bold.ttf")
                binding.layoutBottom.beGone()
            }
            4 -> {
                binding.tvRefuse simpleText "Thời gian giao"
                binding.tvRefuseDes simpleText data.receiveAt?.getHourMinutesTime()
                binding.tvTime.beGone()
                binding.tvTimeDes.beGone()

                resetTvWidth()
                binding.tvMathecao.beVisible()
                binding.tvMathecao simpleText "Giao quà thành công"
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
                        binding.tvRefuse simpleText "Hạn nhận quà"
                        binding.tvRefuseDes simpleText "Hết hạn"

                        binding.tvTime simpleText "Loại quà"
                        binding.tvTimeDes simpleText "Quà giao tận nơi"
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
                        binding.tvRefuse simpleText "Hạn nhận quà"
                        binding.tvRefuseDes simpleText TimeHelper.convertDateSvToDateVn(data.expiredAt)
                        binding.tvTime simpleText "Loại quà"
                        binding.tvTimeDes simpleText "Quà giao tận nơi"
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
                startActivity(Intent.createChooser(share, "Chia sẻ "))
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