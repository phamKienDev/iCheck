package vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box_tet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.android.synthetic.main.activity_open_reward_box_tet.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.network.models.ICUnBox_Gift
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.DetailCampaignActivity
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.MyGiftWareHouseActivity
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box_tet.presenter.OpenRewardBoxTetPresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box_tet.view.IOpenRewardBoxTetView
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File
import java.io.FileOutputStream
import java.util.*

class OpenRewardBoxTetActivity : BaseActivity<OpenRewardBoxTetPresenter>(), IOpenRewardBoxTetView {

    override val getLayoutID: Int
        get() = R.layout.activity_open_reward_box_tet

    override val getPresenter: OpenRewardBoxTetPresenter
        get() = OpenRewardBoxTetPresenter(this)

    private var position = 0
    private var mId: String? = null
    private var campaignId: String? = null
    private var mNumber: Int = 0
    private var mBusiness_type: Int = 0
    private var urlImage: String = ""
    private var title: String? = null

    private var player: MediaPlayer? = null
    private var vibrate: Vibrator? = null

    private val requestStoragePermission = 1

    override fun onInitView() {
        vibrate = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        setupStatusBar()
        setupThemes()
        presenter.onGetDataId(intent)
        listener()
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun setupThemes() {
//        val theme = SettingManager.getTheme ?: return
//        val setupTheme = SetupThemeHelper()
//
//        theme.color?.let {
//            setupTheme.setLayoutBackground(FileHelper.openBoxBackground, it.background_color, imgBackground, layoutContainer)
//        }
//
//        theme.break_reward?.let {
//            val path = FileHelper.getPath(this)
//
//            setupTheme.setImage(FileHelper.imageBackgroundBox,R.drawable.ic_default_square,bgLixi)
//
//            if (it.actions != null){
//                if (it.actions?.size!! > 2 || it.actions?.size == 3){
//                    val drawable1 = Drawable.createFromPath(FileHelper.getImagePath(path, it.actions!![0].source))
//                    val drawable2 = Drawable.createFromPath(FileHelper.getImagePath(path, it.actions!![1].source))
//                    val drawable3 = Drawable.createFromPath(FileHelper.getImagePath(path, it.actions!![2].source))
//
//                    if (drawable1 != null) {
//                        btnInformation.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.getDrawableSize(this, drawable1, SizeHelper.size40, SizeHelper.size40), null, null)
//                    } else {
//                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_default_square)
//                        btnInformation.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.getBitmapSize(this, bitmap, SizeHelper.size40, SizeHelper.size40), null, null)
//                    }
//
//                    if (drawable2 != null) {
//                        btnMyGift.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.getDrawableSize(this, drawable2, SizeHelper.size40, SizeHelper.size40), null, null)
//                    } else {
//                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_default_square)
//                        btnMyGift.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.getBitmapSize(this, bitmap, SizeHelper.size40, SizeHelper.size40), null, null)
//                    }
//
//                    if (drawable3 != null) {
//                        btnCancel.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.getDrawableSize(this, drawable3, SizeHelper.size40, SizeHelper.size40), null, null)
//                    } else {
//                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_default_square)
//                        btnCancel.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.getBitmapSize(this, bitmap, SizeHelper.size40, SizeHelper.size40), null, null)
//                    }
//                }
//            }
//        }
    }

    private fun listener() {
        btnInformation.setOnClickListener {
            campaignId?.let {
                startActivity<DetailCampaignActivity, String>(Constant.DATA_1, it)
            }
        }

        btnMyGift.setOnClickListener {
            startActivity<MyGiftWareHouseActivity>()
        }

        btnCancel.setOnClickListener {
            onBackPressed()
        }

        btnBocLiXi.setOnClickListener {
            if (mNumber > 0) {
                presenter.onUnboxGift(mId, 1)
                btnBocLiXi.visibility = View.GONE
            } else {
                DialogHelper.showNotification(this@OpenRewardBoxTetActivity, R.string.so_luong_qua_da_het_hay_kiem_them_de_mo_qua_nhe, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }

        btnTiepTuc.setOnClickListener {
            btnBocLiXi?.visibility = View.VISIBLE
            btnTiepTuc?.visibility = View.GONE
            btnChiaSe?.visibility = View.GONE
            bgLixi?.visibility = View.GONE
            bgHaoQuang.clearAnimation()
            bgHaoQuang?.visibility = View.GONE
            imgQua?.visibility = View.GONE
        }

        btnChiaSe.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        PermissionDialog.checkPermission(this, PermissionDialog.STORAGE, object : PermissionDialog.PermissionListener {
            override fun onPermissionAllowed() {
                takeScreenshot()
            }

            override fun onRequestPermission() {
                PermissionHelper.checkPermission(this@OpenRewardBoxTetActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), requestStoragePermission)
            }

            override fun onPermissionNotAllow() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataIdSuccess(item: ICBoxReward) {
        item.business_type?.let {
            mBusiness_type = it
        }

        mId = item.id
        campaignId = item.campaign_id
        mNumber = item.number!!.toInt()
        if (!item.logo.isNullOrEmpty()) {
            urlImage = item.logo!!
        }
        title = item.title
        WidgetUtils.loadImageUrl(imgAvatar, item.logo)
    }

    override fun onUnboxGiftSuccess(obj: MutableList<ICUnBox_Gift>, numberUnboxIcheck: Int) {
        setResult(Activity.RESULT_OK)
        mNumber -= obj.size

        btnTiepTuc?.visibility = View.VISIBLE
        btnChiaSe?.visibility = View.VISIBLE

        when (obj[0].type) {
            0 -> {
                bgLixi?.visibility = View.VISIBLE
                /*Tạm comment bao giờ server trả về đủ thì ghép*/
//                imgQua?.setImageResource(R.drawable.cau_doi_1)
                imgQua?.visibility = View.VISIBLE
            }

            1 -> {
                playAudio()
                runVibrate()
                imgQua?.let {
                    WidgetUtils.loadImageUrlRoundedFitCenter(it, obj[0].image, R.drawable.ic_default_square, SizeHelper.size5)
                }
                bgLixi?.visibility = View.VISIBLE
                imgQua?.visibility = View.VISIBLE
                tv1?.visibility = View.VISIBLE
                tvTenQua?.visibility = View.VISIBLE
                tvTenQua?.text = obj[0].name
            }

            2 -> {
                playAudio()
                runVibrate()
                imgQua?.let {
                    WidgetUtils.loadImageUrlRoundedFitCenter(it, obj[0].image, R.drawable.ic_default_square, SizeHelper.size5)
                }
                bgLixi?.visibility = View.VISIBLE
                imgQua?.visibility = View.VISIBLE
                tv1?.visibility = View.VISIBLE
                tvTenQua?.visibility = View.VISIBLE
                tvTenQua?.text = obj[0].name
            }

            3 -> {
                playAudio()
                runVibrate()
                when (obj[0].icoin) {
                    Constant.NAMTRAM -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_500k_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "500.000 iCoin"
                    }
                    Constant.HAITRAM -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_200k_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "200.000 iCoin"
                    }
                    Constant.MOTTRAM -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_100k_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "100.000 iCoin"
                    }
                    Constant.NAMMUOINGHIN -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_50k_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "50.000 iCoin"
                    }
                    Constant.HAIMUOINGHIN -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_20k_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "20.000 iCoin"
                    }
                    Constant.MUOINGHIN -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_10k_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "10.000 iCoin"
                    }
                    Constant.NAMNGHIN -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_5000_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "5000 iCoin"
                    }
                    Constant.HAINGHIN -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_2000_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "2000 iCoin"
                    }
                    Constant.MOTNGHIN -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_1000_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "1000 iCoin"
                    }
                    Constant.NAMTRAMDONG -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_500_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "500 iCoin"
                    }
                    Constant.HAITRAMDONG -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_200_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "200 iCoin"
                    }
                    Constant.MOTTRAMDONG -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_100_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "100 iCoin"
                    }
                    Constant.NAMMUOI -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_50_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "50 iCoin"
                    }
                    Constant.HAIMUOI -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_20_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "20 iCoin"
                    }
                    Constant.MUOI -> {
                        bgHaoQuang?.visibility = View.VISIBLE
                        imgQua?.visibility = View.VISIBLE
                        imgQua?.setImageResource(R.drawable.ic_icoin_10_big_dp)
                        playBackgroundAnimation(bgHaoQuang)
                        tv1?.visibility = View.VISIBLE
                        tvTenQua?.visibility = View.VISIBLE
                        tvTenQua?.text = "10 iCoin"
                    }
                }
            }
        }
    }

    override fun onGetDataError(type: Int) {
        when (type) {
            Constant.ERROR_INTERNET -> {
                DialogHelper.showNotification(this, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
            Constant.ERROR_UNKNOW -> {
                DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }
    }

    private fun playBackgroundAnimation(bg: AppCompatImageView?) {
        bg?.visibility = View.VISIBLE
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 5000
        rotate.repeatCount = -1
        rotate.interpolator = LinearInterpolator()
        bg?.startAnimation(rotate)
    }

    @SuppressLint("StaticFieldLeak")
    private fun playAudio() {
        val sound = SettingManager.getSoundSetting
        Log.e("sou", sound.toString())
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
        Log.e("vii", vibra.toString())
        if (vibra) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrate?.vibrate(500)
            }
        } else {
            vibrate?.cancel()
        }
    }

    private fun takeScreenshot() {
        val now = Date()
        DateFormat.format("dd-mm-yyyy hh:mm:ss", now)
        try {
            val mPath = Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + applicationContext?.packageName + "/capture/"

            val v1 = window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false

            val imageFile = File(mPath)

            if (!imageFile.exists()) {
                imageFile.mkdirs()
            }

            val outputFile = File(imageFile, "$now.jpg")

            val outputStream = FileOutputStream(outputFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            openScreenshot(outputFile)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun openScreenshot(imageFile: File) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        val uri = Uri.fromFile(imageFile)
        intent.setDataAndType(uri, "image/*")
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile))
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    override fun onDestroy() {
        super.onDestroy()
        killMediaPlayer()
    }
}
