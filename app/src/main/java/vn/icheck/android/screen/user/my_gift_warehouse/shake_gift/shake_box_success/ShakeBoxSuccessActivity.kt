package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake_box_success

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_shake_box_success.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.user.campaign_onboarding.CampaignOnboardingActivity
import vn.icheck.android.screen.user.coinhistory.CoinHistoryActivity
import vn.icheck.android.screen.user.detail_my_reward.DetailMyRewardActivity
import vn.icheck.android.screen.user.detail_my_reward.DetailMyRewardViewModel
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake_box_success.viewmodel.ShakeBoxSuccessViewmodel
import vn.icheck.android.screen.user.mygift.MyGiftActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ShakeBoxSuccessActivity : BaseActivityMVVM() {
    private lateinit var viewModel: ShakeBoxSuccessViewmodel

    private val requestShare = 1

    private val listPermission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var player: MediaPlayer? = null
    private var vibrate: Vibrator? = null

    private var qrCodeBitmap: Bitmap? = null
    private val giftViewModel: DetailMyRewardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_box_success)
        vibrate = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        viewModel = ViewModelProvider(this).get(ShakeBoxSuccessViewmodel::class.java)
        viewModel.getDataIntent(intent)
        setupStatusBar()
        initView()
        listener()
        initViewModel()
    }

    private fun initView() {
        ListShakeGridBoxActivity.numberGiftUser--
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    private fun listener() {
        imgBack.setOnClickListener {
             Intent().apply {
                putExtra(Constant.DATA_2, 2)
                putExtra(Constant.DATA_3, viewModel.campaign?.id)
                putExtra(Constant.DATA_4, viewModel.shakeGift?.rewardType)
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        }

        btnShare.setOnClickListener {
            tvThankNhaTaiTro.visibility = View.VISIBLE
            tvThankNhaTaiTro.text =
                Html.fromHtml("<font color=#828282>Nhà tài trợ</font>" + "<br>" + "${viewModel.campaign?.businessName}" + "</br>")
            layoutTaiApp.visibility = View.VISIBLE
            btnShare.visibility = View.INVISIBLE
            btnMyGift.visibility = View.INVISIBLE

            lifecycleScope.launch {
                btnShare.isClickable = false
                delay(1000)
                btnShare.isClickable = true
            }

            Handler().postDelayed({
                qrCodeBitmap = screenShot(layoutParent)

                PermissionDialog.checkPermission(
                    this@ShakeBoxSuccessActivity,
                    PermissionDialog.STORAGE,
                    object : PermissionDialog.PermissionListener {
                        override fun onPermissionAllowed() {
                            ShareImage(this@ShakeBoxSuccessActivity).execute()
                        }

                        override fun onRequestPermission() {
                            PermissionHelper.checkPermission(
                                this@ShakeBoxSuccessActivity,
                                listPermission,
                                requestShare
                            )
                        }

                        override fun onPermissionNotAllow() {
                            tvThankNhaTaiTro.text = null
                            tvThankNhaTaiTro.visibility = View.GONE
                            layoutTaiApp.visibility = View.INVISIBLE
                            btnShare.visibility = View.VISIBLE
                            btnMyGift.visibility = View.VISIBLE
                            showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                        }
                    })
            }, 500)
        }
    }

    private fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel() {
        viewModel.objCampaign.observe(this, Observer {
            tvThank.text =
                Html.fromHtml("<font color=#828282>Cảm ơn bạn đã tham gia sự kiện</font>" + "<br>" + "${it.title}" + "</br>")
            if (it.businessName == "iCheck") {
                imgLogo.borderColor = ContextCompat.getColor(this, R.color.gray)
                imgLogo.borderWidth = SizeHelper.size2
            } else {
                imgLogo.borderColor = ContextCompat.getColor(this, R.color.white)
                imgLogo.borderWidth = SizeHelper.size2
            }
            WidgetUtils.loadImageUrl(imgLogo, it.logo)
        })

        viewModel.objICOpenShakeGift.observe(this, Observer {
            TrackingAllHelper.tagOpenGiftBoxSuccessful(
                campaign_id = viewModel.campaign?.id,
                gift_type = it.rewardType,
                gift_name = it.name,
                coin = it.icoin,
                code = null
            )

            when (it.type) {
                1 -> {
                    tvNameGift.text = it.name
                    btnMyGift.text = getString(R.string.xem_qua)
                    btnMyGift.setOnClickListener {
                        TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                            viewModel.campaign?.id,
                            viewModel.shakeGift?.rewardType
                        )
                        startActivity<MyGiftActivity, Int>(Constant.DATA_1, 1)
                    }
                }
                3 -> {

                }
                else -> {
                }
            }
            when (it.rewardType) {
                "PRODUCT_SHIP" -> {
                    imageGift.beVisible()
                    imageGiftVoucher.beInvisible()
                    playAudio()
                    runVibrate()
                    tvNameGift.text = it.name
                    btnMyGift.text = getString(R.string.nhan_qua)
                    btnMyGift.setOnClickListener { _ ->
                        giftViewModel.getDetailReward(it.rewardId)
                            .observe(this, Observer { reward ->
                                TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                                    viewModel.campaign?.id,
                                    viewModel.shakeGift?.rewardType
                                )
                                startActivity(Intent(this, ShipActivity::class.java).apply {
                                    putExtra("gift", reward)
                                })
                            })

                    }
                    WidgetUtils.loadImageUrl(imageGift, it.image)
                }
                "PRODUCT_IN_SHOP" -> {
                    imageGift.beVisible()
                    imageGiftVoucher.beInvisible()
                    playAudio()
                    runVibrate()
                    tvNameGift.text = it.name
                    btnMyGift.text = getString(R.string.nhan_qua)
                    btnMyGift.setOnClickListener { _ ->
                        TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                            viewModel.campaign?.id,
                            viewModel.shakeGift?.rewardType
                        )
                        ActivityUtils.startActivity<DetailMyRewardActivity, String>(
                            this,
                            Constant.DATA_1,
                            it.rewardId.toString()
                        )
                    }
                    WidgetUtils.loadImageUrl(imageGift, it.image)
                }
                "CARD" -> {
                    imageGift.beVisible()
                    imageGiftVoucher.beInvisible()
                    playAudio()
                    runVibrate()
                    tvNameGift.text = it.name
                    btnMyGift.text = getString(R.string.xem_qua)
                    btnMyGift.setOnClickListener { _ ->
                        TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                            viewModel.campaign?.id,
                            viewModel.shakeGift?.rewardType
                        )
                        ActivityUtils.startActivity<DetailMyRewardActivity, String>(
                            this,
                            Constant.DATA_1,
                            it.rewardId.toString()
                        )
                    }
                    WidgetUtils.loadImageUrl(imageGift, it.image)
                }
                "LUCKY" -> {
                    imageGift.beVisible()
                    imageGiftVoucher.beInvisible()
                    tvNameGift.text = it.name
                    btnMyGift.text = getString(R.string.xem_qua)
                    btnMyGift.setOnClickListener { _ ->
                        TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                            viewModel.campaign?.id,
                            viewModel.shakeGift?.rewardType
                        )
                        ActivityUtils.startActivity<DetailMyRewardActivity, String>(
                            this,
                            Constant.DATA_1,
                            it.rewardId.toString()
                        )
                    }
                    WidgetUtils.loadImageUrl(imageGift, it.image)
                }
                "CODE" -> {
                    imageGift.beInvisible()
                    imageGiftVoucher.beVisible()

                    tvNameGift.text = it.name
                    tvCodeVoucher.text = it.code

                    btnMyGift.setText(R.string.xem_lich_boc_tham)
                    btnMyGift.setOnClickListener { _ ->
                        TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                            viewModel.campaign?.id,
                            viewModel.shakeGift?.rewardType
                        )
                        val intent = Intent(this, CampaignOnboardingActivity::class.java)
                        intent.putExtra(
                            Constant.DATA_1, it.campaignId ?: viewModel.campaign?.id
                            ?: ""
                        )
                        intent.putExtra(Constant.DATA_2, "CODE")
                        ActivityUtils.startActivity(this, intent)
                    }
                }
                else -> {
                    imageGift.beVisible()
                    imageGiftVoucher.beInvisible()

                    viewModel.getCoin().observe(this, { result ->
                        if (result.status == Status.SUCCESS) {
                            SessionManager.setCoin(result.data?.data?.availableBalance ?: 0)
                        }
                    })

                    playAudio()
                    runVibrate()
                    tvNameGift.text = TextHelper.formatMoney(it.icoin) + "Xu"
                    btnMyGift.text = getString(R.string.quan_ly_xu)
                    btnMyGift.setOnClickListener {
                        TrackingAllHelper.tagOpenGiftBoxProceedCtaClicked(
                            viewModel.campaign?.id,
                            viewModel.shakeGift?.rewardType
                        )
                        startActivity<CoinHistoryActivity>()
                    }
                    WidgetUtils.loadImageUrlFitCenter(
                        imageGift,
                        it.icoin_icon,
                        R.drawable.ic_icheck_xu
                    )

                }
            }
        })

        viewModel.onError.observe(this, Observer {
            when (it.type) {
                ICMessageEvent.Type.MESSAGE_ERROR -> {
                    DialogHelper.showNotification(
                        this,
                        null,
                        R.string.co_loi_xay_ra_vui_long_thu_lai,
                        false,
                        object : NotificationDialogListener {
                            override fun onDone() {
                                onBackPressed()
                            }
                        })
                }
                else -> {
                }
            }
        })
    }

    @SuppressLint("StaticFieldLeak")
    private fun playAudio() {
        val sound = SettingManager.getSoundSetting
        killMediaPlayer()

        if (sound) {
            try {
                player = MediaPlayer.create(this, R.raw.cheerful)
                player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                player?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            killMediaPlayer()
        }
    }

    private fun killMediaPlayer() {
        try {
            player?.release()
            player = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runVibrate() {
        val vibra = SettingManager.getVibrateSetting
        if (vibra) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate?.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrate?.vibrate(500)
            }
        } else {
            vibrate?.cancel()
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (event.type == ICMessageEvent.Type.BACK_TO_SHAKE) {
            val intent = Intent()
            intent.putExtra(Constant.DATA_2, 2)
            setResult(Activity.RESULT_OK, intent)
            TrackingAllHelper.tagOpenGiftBoxDismissClicked(
                viewModel.campaign?.id,
                viewModel.shakeGift?.rewardType
            )
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestShare) {
            if (PermissionHelper.checkResult(grantResults)) {
                ShareImage(this@ShakeBoxSuccessActivity).execute()
            } else {
                tvThankNhaTaiTro.text = null
                tvThankNhaTaiTro.visibility = View.GONE
                layoutTaiApp.visibility = View.INVISIBLE
                btnShare.visibility = View.VISIBLE
                btnMyGift.visibility = View.VISIBLE
                showShortError(R.string.khong_the_truy_cap_khi_ban_chua_cap_quyen)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constant.DATA_2, 2)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    inner class ShareImage(val context: Context) : AsyncTask<Bitmap, String, File>() {
        override fun doInBackground(vararg params: Bitmap?): File? {
            val fileFolder = (Environment.getExternalStorageDirectory()
                .toString() + "/Android/data/" + context.packageName + "/Files" + "/Share")

            val dir = File(fileFolder)

            if (!dir.exists()) dir.mkdirs()

            val filename = dir.absolutePath + "/" + System.currentTimeMillis() + ".png"

            try {
                FileOutputStream(filename).use { out ->
                    qrCodeBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val file = File(filename)

                if (file.exists()) {
                    return file
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)

            if (result != null && result.exists()) {
                val fileprovider = context.getString(R.string.xxx_fileprovider, context.packageName)
                val contentUri = FileProvider.getUriForFile(context, fileprovider, result)

                if (contentUri != null) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    startActivity(Intent.createChooser(intent, "iCheck Share"))
                } else {
                    ToastUtils.showShortError(context, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            } else {
                ToastUtils.showShortError(context, R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tvThankNhaTaiTro.text = null
        tvThankNhaTaiTro.visibility = View.GONE
        layoutTaiApp.visibility = View.INVISIBLE
        btnShare.visibility = View.VISIBLE
        btnMyGift.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        killMediaPlayer()
    }
}