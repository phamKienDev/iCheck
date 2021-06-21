package vn.icheck.android.screen.scan

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.scandit.datacapture.barcode.capture.*
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.async.Callback
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.common.feedback.Sound
import com.scandit.datacapture.core.common.feedback.Vibration
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.*
import com.scandit.datacapture.core.ui.DataCaptureView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.item_ads_product_grid.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.internal_stamp.InternalStampDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.constant.SCAN_REVIEW
import vn.icheck.android.databinding.IckScanCustomViewBinding
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.ichecklibs.util.getDeviceWidth
import vn.icheck.android.ichecklibs.util.showDurationErrorToast
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.icheckscanditv6.BarcodeBottomDialog
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.screen.gift_detail_voucher.GiftVoucherStaffActivity
import vn.icheck.android.loyalty.sdk.ScanLoyaltyHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.ICProductDetail
import vn.icheck.android.network.models.ICValidStampSocial
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.dialog.DialogFragmentNotificationFirebaseAds
import vn.icheck.android.screen.scan.viewmodel.V6ViewModel
import vn.icheck.android.screen.user.contribute_product.CONTRIBUTE_REQUEST
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.DetailStampHoaPhatActivity
import vn.icheck.android.screen.user.detail_stamp_thinh_long.home.DetailStampThinhLongActivity
import vn.icheck.android.screen.user.detail_stamp_v5.home.DetailStampV5Activity
import vn.icheck.android.screen.user.detail_stamp_v6.home.DetailStampV6Activity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ContactUtils
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class V6ScanditActivity : BaseActivityMVVM(), BarcodeCaptureListener {

    companion object {
        var listPopupShowed: MutableList<Long> = mutableListOf()

        fun create(context: Context, tab: Int = -1) {
            val i = Intent(context, V6ScanditActivity::class.java)
            i.putExtra(Constant.DATA_1, tab)
            context.startActivity(i)
        }

        fun scanOnly(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, V6ScanditActivity::class.java)
            i.putExtra("scan_only", true)
            context.startActivityForResult(i, requestCode)
        }

        fun scanOnlyLoyalty(
            context: FragmentActivity,
            type: String,
            campaignId: Long,
            nameCampaign: String?,
            nameShop: String?,
            avatarShop: String?,
            currentCount: Int?
        ) {
            val i = Intent(context, V6ScanditActivity::class.java)
            i.putExtra("loyalty_only", true)
            i.putExtra("type", type)
            i.putExtra("campaignId", campaignId)
            i.putExtra("nameCampaign", nameCampaign)
            i.putExtra("nameShop", nameShop)
            i.putExtra("avatarShop", avatarShop)
            i.putExtra("currentCount", currentCount)
            context.startActivity(i)
        }

        fun scanOnlyChat(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, V6ScanditActivity::class.java)
            i.putExtra("scan_only_chat", true)
            context.startActivityForResult(i, requestCode)
        }

        fun reviewOnly(context: FragmentActivity) {
            val i = Intent(context, V6ScanditActivity::class.java)
            i.putExtra("review_only", true)
            context.startActivityForResult(i, 1)
        }

        fun reviewOnly(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, V6ScanditActivity::class.java)
            i.putExtra("review_only", true)
            context.startActivityForResult(i, requestCode)
        }
    }

    lateinit var dataCaptureContext: DataCaptureContext
    lateinit var barcodeCapture: BarcodeCapture
    lateinit var cameraSettings: CameraSettings
    var camera: Camera? = null
    var _binding: IckScanCustomViewBinding? = null
    val binding get() = _binding!!
    val viewModel by viewModels<V6ViewModel>()
    private val guideArr = arrayListOf<View?>()
    private val requestPhone = 2
    private val requestCropMedia = 3
    private var phoneNumber: String = ""
    lateinit var dataCaptureView: DataCaptureView
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    private val takeImageListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {

            comPressImage(file)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
//            CropImageActivity.start(this@V6ScanditActivity, filePath, null, ratio, requestCropMedia)
            UCrop.of(Uri.fromFile(File(filePath.toString())), Uri.fromFile(File(cacheDir.absolutePath + "/" + System.currentTimeMillis() + ".png")))
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(getDeviceWidth(), getDeviceHeight())
                .withOptions(UCrop.Options().apply {
                    setToolbarTitle("")
                    setHideBottomControls(true)
                })
                .start(this@V6ScanditActivity);
        }

        override fun onDismiss() {

//            if (!scanImage.get()) {
//                pushUpHeight()
//                resetCamera()
//            }

            pushUpHeight()
            resetCamera()
        }

        override fun onTakeMediaSuccess(file: File?) {
            comPressImage(file)
        }
    }

    private fun comPressImage(file: File?) {
        try {
            lifecycleScope.launch {
                delay(600)
                val bm = BitmapFactory.decodeFile(file?.getAbsolutePath())
                val scaled = if (dataCaptureView.width <= 1000) bm.scale(dataCaptureView.width, dataCaptureView.width) else
                    bm.scale(1000, 1000)
                val source = BitmapFrameSource.of(scaled)
                dataCaptureContext.setFrameSource(source)
                source.addListener(object : FrameSourceListener {
                    var lastState: FrameSourceState? = null
                    override fun onStateChanged(frameSource: FrameSource, newState: FrameSourceState) {
                        super.onStateChanged(frameSource, newState)
                        if (lastState == FrameSourceState.STOPPING && newState == FrameSourceState.OFF && scanImage.get()) {
                            scanImage.set(false)
                            offCamera()
                            runOnUiThread {
                                DialogHelper.showNotification(
                                    this@V6ScanditActivity,
                                    R.string.thong_bao,
                                    R.string.khong_thay_ma_vach,
                                    true,
                                    object : NotificationDialogListener {

                                        override fun onDone() {
                                            resetCamera()
                                        }

                                    })
                            }
                            frameSource.removeListener(this)
                        } else {
                            if (newState == FrameSourceState.OFF) {
                                resetCamera()
                            }
                            lastState = newState
                        }
                    }

                })
                resetHeight()

                source?.switchToDesiredState(FrameSourceState.ON)
                offCameraNotDisable()
                scanImage.set(true)
            }
        } catch (e: Exception) {
            logError(e)
            resetCamera()
            enableCapture()
        }
    }

    private lateinit var takeImageDialog: TakeMediaDialog
    val scanImage = AtomicBoolean(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request()

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    }

    private fun initDataCapture() {
        initTakeImageDialog()
        initBarcodeCapture()
        initCamera()
        resetCamera()
        initDataCaptureView()
        checkIsLoyalty()
        checkIsReview()
        checkIsScan()
        initViews()
        pushUpHeight()

        if (intent.getIntExtra(Constant.DATA_1, 1) == -1) {
            viewModel.getPopup()
        }
    }

    private fun checkIsLoyalty() {
        if (intent.getBooleanExtra("loyalty_only", false)) {
            viewModel.scanOnlyLoyalty = true
            _binding?.btnMyCode.beGone()
            _binding?.btnQm.beGone()
            _binding?.imgNmbt.beGone()
            _binding?.imgSdha.beGone()
        } else {
            viewModel.scanOnlyLoyalty = false
        }
    }

    private fun checkIsScan() {
        if (intent.getBooleanExtra("scan_only", false)) {
            viewModel.scanOnly = true
            _binding?.btnMyCode.beGone()
            _binding?.btnQm.beGone()
        } else {
            viewModel.scanOnly = false
        }
    }

    private fun checkIsReview() {
        if (intent.getBooleanExtra("review_only", false)) {
            viewModel.reviewOnly = true
            _binding?.btnMyCode.beGone()
            _binding?.btnQm.beGone()
        } else {
            viewModel.reviewOnly = false
        }
    }

    private fun initTakeImageDialog() {
        takeImageDialog = TakeMediaDialog()
        takeImageDialog.setListener(this, takeImageListener, selectMulti = false, cropImage = true, isVideo = false, saveImageToGallery = false, disableTakeImage = false)
    }

    private fun initBarcodeCapture() {
        dataCaptureContext = ICheckApplication.getInstance().dataCaptureContext
        barcodeCapture = ICheckApplication.getInstance().barcodeCapture
        val vib: Vibration? = if (SettingManager.getVibrateSetting) Vibration() else null
        val sound: Sound? = if (SettingManager.getSoundSetting) Sound.defaultSound() else null
        barcodeCapture.feedback.success = Feedback(vib, sound)
        barcodeCapture.addListener(this)
    }

    private fun initCamera() {
        cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        camera = Camera.getDefaultCamera(cameraSettings)
    }

    private fun initDataCaptureView() {
        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext)
//        dataCaptureView.addOverlay(BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView).apply {
//            shouldShowScanAreaGuides = true
//        })
        _binding = IckScanCustomViewBinding.inflate(layoutInflater, dataCaptureView, false)
        dataCaptureView.addView(binding.root, getDeviceWidth(), getDeviceHeight())
        setContentView(dataCaptureView)
    }

    private fun pushUpHeight() {
        if (getUserCountry(this).contains("vn", false)) {
            if (!::dataCaptureView.isInitialized) {
                dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext)

            }

            if (::dataCaptureView.isInitialized) {
                dataCaptureView.post {
                    val lp = dataCaptureView.layoutParams
                    if (lp.height != getDeviceHeight()) {
                        lp.height = getDeviceHeight() + 50.dpToPx()
                        lp.width = getDeviceWidth()
                        dataCaptureView.layoutParams = lp
                    }
                }
            }
        }
    }

    private fun resetHeight() {
        dataCaptureView.post {
            if (getUserCountry(this).contains("vn", false)) {
                val lp = dataCaptureView.layoutParams
                if (lp.height != getDeviceHeight()) {
                    lp.height = getDeviceHeight()
                    lp.width = getDeviceWidth()
                    dataCaptureView.layoutParams = lp
                }
            }
        }
    }

    private fun resetCamera() {
        lifecycleScope.launch {
            camera?.switchToDesiredState(FrameSourceState.ON, object : Callback<Boolean> {
                override fun run(result: Boolean) {
                    if (result) {
                        dataCaptureContext.setFrameSource(camera)
                        enableCapture()
                        pushUpHeight()
                    } else {
                        resetCamera()
                    }
                }
            })
        }
    }


    private fun offCamera() {
        try {
            if (::barcodeCapture.isInitialized) {
                barcodeCapture.isEnabled = false
            } else {
                ICheckApplication.getInstance().initScandit()
                barcodeCapture = ICheckApplication.getInstance().barcodeCapture
                barcodeCapture.isEnabled = false
            }
            camera?.switchToDesiredState(FrameSourceState.OFF, object : Callback<Boolean> {
                override fun run(result: Boolean) {
                    if (!result) {
                        offCamera()
                    }
                }
            })
        } catch (e: Exception) {
        }
    }

    fun offCameraNotDisable() {
        camera?.switchToDesiredState(FrameSourceState.OFF, object : Callback<Boolean> {
            override fun run(result: Boolean) {
                if (!result) {
                    offCameraNotDisable()
                }
            }
        })
    }


    private fun getUserCountry(context: Context): String {
        return try {
            val tm: TelephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val simCountry: String = tm.simCountryIso
            simCountry.toLowerCase(Locale.US)
        } catch (e: java.lang.Exception) {
            ""
        }
    }

    fun initViews() {
        if (viewModel.scanOnly || viewModel.reviewOnly) {
            binding.btnMyCode.beGone()
            binding.btnScanBuy.beGone()
            binding.btnQm.beGone()
        }

        guideArr.add(binding?.imgScanTip)
        guideArr.add(binding?.imgHdSdha)
        guideArr.add(binding?.imgNmspTip)
        guideArr.add(binding?.imgTorchTip)
        guideArr.add(binding?.imgXmdd)
        binding?.imgHelp?.setOnClickListener {
            viewModel.setGuide()
        }
        binding?.imgFlash?.setOnClickListener {
            viewModel.setFlash()
        }
        binding?.btnMyCode?.setOnClickListener {
            if (SessionManager.isUserLogged) {
                simpleStartActivity(MyQrActivity::class.java)
                finish()
            } else {
                onRequireLogin()
            }

        }
        binding.btnClear.setOnClickListener {
            barcodeCapture.isEnabled = false
            finish()
        }
        binding?.root?.setOnClickListener {
            viewModel.offGuide()
        }
        binding.imgSdha.setOnClickListener {
            lifecycleScope.launch {
                binding.imgSdha.isEnabled = false
                offCamera()
//                resetHeight()
                request(takeImageDialog)
                delay(500)
                binding.imgSdha.isEnabled = true
            }
        }
        binding?.imgNmbt?.setOnClickListener {
            binding.imgNmbt.isEnabled = false
            offCamera()
            BarcodeBottomDialog.show(supportFragmentManager, false, object : BarcodeBottomDialog.OnBarCodeDismiss {
                override fun onDismiss() {
                    binding.imgNmbt.isEnabled = true
                    resetCamera()
                }

                override fun onSubmit(mCode: String) {
                    binding.imgNmbt.isEnabled = true
                    val code = mCode.trim()
                    if (viewModel.scanOnlyChat) {
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra(Constant.DATA_1, false)
                            putExtra(Constant.DATA_2, code)
                        })
                        finish()
                        return
                    }
                    if (code.startsWith("u-") || code.startsWith("U-")) {
                        if (code.count { "-".contains(it) } == 1) {
                            try {
                                val userID = code.split("-")[1]
                                if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                                    IckUserWallActivity.create(userID.toLong(), this@V6ScanditActivity)
                                    return
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    when {
                        viewModel.scanOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {

                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            showErrorNotFoundBarcode()
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showErrorNotFoundBarcode()
                                                }
                                                else -> {

                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showErrorBusinessDeactiveBarcode()
                                                        }
                                                        "adminDeactive" -> {
                                                            showErrorAdminDeactiveBarcode()
                                                        }
                                                        else -> {
                                                            setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, obj.data) })
                                                            finish()
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    } else {
                                        showErrorNotFoundBarcode()
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    showErrorCustom(error?.message)
                                }
                            })
                        }
                        viewModel.reviewOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            showErrorNotFoundBarcode(false)
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showErrorNotFoundBarcode(false)
                                                }
                                                else -> {
                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showErrorBusinessDeactiveBarcode(false)
                                                        }
                                                        "adminDeactive" -> {
                                                            showErrorAdminDeactiveBarcode(false)
                                                        }
                                                        else -> {
                                                            startActivityForResult(Intent(this@V6ScanditActivity, EditReviewActivity::class.java).apply {
                                                                putExtra(Constant.DATA_1, obj.data?.id)
                                                            }, SCAN_REVIEW)
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    } else {
                                        showErrorAdminDeactiveBarcode(false)
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    showErrorCustom(error?.message,false)
                                }
                            })
                        }
                        else -> {
                            if (code.startsWith("u-") || code.startsWith("U-")) {
                                if (code.count { "-".contains(it) } == 1) {
                                    try {
                                        val userID = code.split("-")[1]
                                        if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                                            IckUserWallActivity.create(userID.toLong(), this@V6ScanditActivity)
                                            return
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                IckProductDetailActivity.start(this@V6ScanditActivity, code, true)
                            }
                        }
                    }
                }
            })
        }

        viewModel.ickScanModelLiveData.observe(this, { model ->
            lifecycleScope.launch {
                delay(300)

                if (model.isFlash) {
                    binding.imgFlash.setImageResource(R.drawable.ic_flash_on_24px)
                } else {
                    binding.imgFlash.setImageResource(R.drawable.ic_flash_off_24px)
                }
                if (model.isFlash) {
                    camera?.desiredTorchState = TorchState.ON
                } else {
                    camera?.desiredTorchState = TorchState.OFF
                }

                if (model.showGuide) {
                    binding.root.setAllEnabled(false)
                    offCamera()
                    for (item in guideArr) {
                        if (item != null) {
                            item.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        item.animate()
                                            .alpha(0f)
                                            .setDuration(1000)
                                            .setListener(null)
                                    }
                                })
                            delay(2000)
                        }
                    }
                    resetCamera()
                    binding.root.setAllEnabled(true)
                    model.showGuide = false
                }
            }
        })

        viewModel.onPopupAds.observe(this, {
            if(!listPopupShowed.contains(it.id)){
                DialogFragmentNotificationFirebaseAds.showPopupAds(this, it)
                listPopupShowed.add(it.id?:-1L)
            }
        })

        initListener()
    }

    fun request(dialog: TakeMediaDialog) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CONTRIBUTE_REQUEST
                )
            }
        } else {
            try {
                if (supportFragmentManager.findFragmentByTag(TakeMediaDialog::class.java.simpleName)?.isAdded != true) {
                    dialog.show(supportFragmentManager, null)
                }
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    override fun onBarcodeScanned(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {
        if (session.newlyRecognizedBarcodes.isEmpty()) {
            return
        }
        barcodeCapture.isEnabled = false
        scanImage.set(false)
        job?.cancel()
        val barcode = session.newlyRecognizedBarcodes[0]

        runOnUiThread {
            val code = barcode.data?.trim()

            if (!code.isNullOrEmpty()) {
                val symbology = barcode.symbology
                viewModel.codeScan = code

                if (viewModel.scanOnlyLoyalty) {

                    val nc: String = when {
                        code.contains("https://qcheck-dev.vn/") -> {
                            code.replace("https://qcheck-dev.vn/", "")
                        }
                        code.contains("http://dev.qcheck.vn/") -> {
                            code.replace("http://dev.qcheck.vn/", "")
                        }
                        code.contains("https://dev.qcheck.vn/") -> {
                            code.replace("https://dev.qcheck.vn/", "")
                        }
                        code.contains("https://qcheck.vn/") -> {
                            code.replace("https://qcheck.vn/", "")
                        }
                        code.contains("http://qcheck.vn/") -> {
                            code.replace("http://qcheck.vn/", "")
                        }
                        code.contains("http://qcheck-dev.vn/") -> {
                            code.replace("http://qcheck-dev.vn/", "")
                        }
                        else -> {
                            code
                        }
                    }
                    if (code == nc) {
                        showLongError(getString(R.string.day_khong_phai_la_tem_icheck_vui_long_quet_lai))
                        resetCamera()
                    } else {
                        val type = intent.getStringExtra("type")
                        val campaignId = intent.getLongExtra("campaignId", -1)
                        val nameCampaign = intent.getStringExtra("nameCampaign")
                        val nameShop = intent.getStringExtra("nameShop")
                        val avatarShop = intent.getStringExtra("avatarShop")
                        val currentCount = intent.getIntExtra("currentCount", -1)

                        ScanLoyaltyHelper.checkCodeScanLoyalty(
                            this@V6ScanditActivity, type
                                ?: "", nc, campaignId, nameCampaign, nameShop, avatarShop, currentCount
                        ) { stop ->
                            if (stop) {
                                offCamera()
                            } else {
                                resetCamera()
                            }
                        }
                    }
                    return@runOnUiThread
                }

                if (viewModel.scanOnlyChat) {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        if (symbology == Symbology.QR) {
                            putExtra(Constant.DATA_1, true)
                        } else {
                            putExtra(Constant.DATA_1, false)
                        }
                        putExtra(Constant.DATA_2, code)
                    })
                    finish()
                }
                if (symbology == Symbology.QR || symbology == Symbology.MICRO_QR) {
                    TrackingAllHelper.trackScanStart(Constant.MA_QR)
                    when {
                        viewModel.scanOnly || viewModel.reviewOnly -> {
                            showErrorNotFoundBarcode(false)
                            return@runOnUiThread
                        }
                        code.startsWith("icv") -> {
                            offCamera()
                            CampaignLoyaltyHelper.scanCheckVoucher(this@V6ScanditActivity, voucher = code.replace("icv", ""),
                                {
                                    startActivity<GiftVoucherStaffActivity, String>(ConstantsLoyalty.DATA_1, code.replace("icv", ""))
                                },
                                { error ->
                                    resetCamera()
                                    showShortErrorToast(error)
                                })
                        }
                        else -> viewModel.checkQrStampSocial()
                    }
                } else {
                    TrackingAllHelper.trackScanStart(Constant.MA_VACH)
                    when {
                        viewModel.scanOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            showErrorNotFoundBarcode()
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showErrorNotFoundBarcode()
                                                }
                                                else -> {

                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showErrorBusinessDeactiveBarcode()
                                                        }
                                                        "adminDeactive" -> {
                                                            showErrorAdminDeactiveBarcode()
                                                        }
                                                        else -> {
                                                            setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, obj.data) })
                                                            finish()
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                    } else {
                                        showErrorNotFoundBarcode()
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    showShortErrorToast(error?.message)
                                }
                            })

                            return@runOnUiThread
                        }
                        viewModel.reviewOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            showErrorNotFoundBarcode()
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showErrorNotFoundBarcode()
                                                }
                                                else -> {

                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showErrorBusinessDeactiveBarcode()
                                                        }
                                                        "adminDeactive" -> {
                                                            showErrorAdminDeactiveBarcode()
                                                        }
                                                        else -> {
                                                            startActivityForResult(Intent(this@V6ScanditActivity, EditReviewActivity::class.java).apply {
                                                                putExtra(Constant.DATA_1, obj.data?.id)
                                                            }, SCAN_REVIEW)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        showErrorNotFoundBarcode()
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    showShortErrorToast(error?.message)
                                    enableCapture()
                                }
                            })
                            return@runOnUiThread
                        }
                        else -> {
                            if (code.startsWith("u-") || code.startsWith("U-")) {

                                if (code.count { "-".contains(it) } == 1) {
                                    try {
                                        val userID = code.split("-")[1]
                                        if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                                            IckUserWallActivity.create(userID.toLong(), this)
                                        }
                                    } catch (e: Exception) {
                                        TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                IckProductDetailActivity.start(this, code, true)
                                return@runOnUiThread
                            }
                        }
                    }
                }
            } else {
                DialogHelper.showNotification(this@V6ScanditActivity, code, false, object : NotificationDialogListener {
                    override fun onDone() {
                        enableCapture()
                    }
                })
            }
        }
    }

    private fun showErrorCustom(message:String?,tracking:Boolean=true) {
        if (tracking) {
            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
        }
        message?.let { showDurationErrorToast(it,3000) }
        enableCapture()
    }

    private fun showErrorAdminDeactiveBarcode(tracking:Boolean=true) {
        if (tracking) {
            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
        }
        showDurationErrorToast("Sản phẩm không cho quét",3000)
        Handler().postDelayed({enableCapture()},3000)
    }

    private fun showErrorBusinessDeactiveBarcode(tracking:Boolean=true) {
        if (tracking) {
            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
        }
        showDurationErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu",3000)
        Handler().postDelayed({enableCapture()},3000)
    }

    private fun showErrorNotFoundBarcode(tracking:Boolean=true) {
        if (tracking) {
            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
        }
        showDurationErrorToast("Không tìm thấy sản phẩm",3000)
        Handler().postDelayed({enableCapture()},3000)
    }

    private fun enableCapture() {
        lifecycleScope.launch {
            if (this@V6ScanditActivity::barcodeCapture.isInitialized) {
                this@V6ScanditActivity.barcodeCapture.isEnabled = true
            } else {
                barcodeCapture = ICheckApplication.getInstance().barcodeCapture
                this@V6ScanditActivity.barcodeCapture.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.scanOnly = false
        viewModel.reviewOnly = false
        offCamera()
        if (::barcodeCapture.isInitialized) {
            barcodeCapture.removeListener(this)
        }
    }

    override fun onPause() {
        super.onPause()
        offCamera()
    }

    override fun onResume() {
        super.onResume()
        if (!scanImage.get()) {
            resetCamera()
        }
    }

    private fun request() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
            }
        } else {
            initDataCapture()
        }
    }

    private fun initListener() {
        viewModel.errorString.observe(this, {
            showShortErrorToast(getString(R.string.ket_noi_mang_cua_ban_co_van_de_n_vui_long_thu_lai))
            enableCapture()
        })
        viewModel.stampFake.observe(this, {
            TrackingAllHelper.trackScanFailed(Constant.MA_QR)
            DialogHelper.showNotification(this, it, false, object : NotificationDialogListener {
                override fun onDone() {
                    enableCapture()
                }
            })
        })

        viewModel.errorQr.observe(this, {
            checkStampQr(it)
        })

        viewModel.stampHoaPhat.observe(this, {
            TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
            ActivityUtils.startActivity<DetailStampHoaPhatActivity, String>(this, Constant.DATA, viewModel.codeScan)
        })

        viewModel.stampThinhLong.observe(this, {
            TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
            ActivityUtils.startActivity<DetailStampThinhLongActivity, String>(this, Constant.DATA, viewModel.codeScan)
        })

        viewModel.showDialogSuggestApp.observe(this, {
            showDialogSuggestApp(it, it.code ?: viewModel.codeScan)
        })

        viewModel.checkStampSocial.observe(this, Observer {
            when {
                it.onlyIdentity == true -> {
                    val link = if (it.urlIdentity.isNullOrEmpty()) {
                        viewModel.codeScan
                    } else {
                        it.urlIdentity!!
                    }

                    val params = StringBuilder("")
                    val currentTime = System.currentTimeMillis()
                    val sourceApp = Base64.encodeToString("isIcheck=true&time=$currentTime".toByteArray(Charsets.UTF_8), Base64.DEFAULT).toString().trim()

                    params.append("?sourceTime=$currentTime")
                    params.append("&sourceApp=$sourceApp")
                    SessionManager.session.user?.id?.let { userID ->
                        if (userID != 0L) {
                            params.append("&user_id=$userID")
                        }
                    }
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                    WebViewActivity.start(this, link + params.toString(), 1, null, false)
                }
                it.code.isNullOrEmpty() -> {
                    checkStampQr(viewModel.codeScan)
                }
                else -> {
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
                    ActivityUtils.startActivity<StampDetailActivity, String>(this, Constant.DATA, it.code!!)
                }
            }
        })
    }

    private fun checkStampQr(it: String) {
        Handler().postDelayed({
            when {
                it.startsWith("u-") || it.startsWith("U-") -> {
                    when {
                        viewModel.scanOnly || viewModel.reviewOnly -> {
                            showErrorNotFoundBarcode(false)
                        }
                        else -> {
                            if (it.count { "-".contains(it) } == 1) {
                                try {
                                    val userID = it.split("-")[1]
                                    if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                                        IckUserWallActivity.create(userID.toLong(), this)
                                        finish()
                                    }
                                } catch (e: Exception) {
                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    e.printStackTrace()
                                }
                            }
                            enableCapture()
                        }
                    }
                }
                Constant.isMarketingStamps(it) -> {
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                    WebViewActivity.start(this, it, 1, null, true)
                }
                it.contains("dev-qcheck.icheck.vn") || it.contains("qcheck-dev.vn") || it.contains("qcheck.vn") || it.contains("qrcode.icheck.com.vn") -> {
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
                    ActivityUtils.startActivity<StampDetailActivity, String>(this, Constant.DATA, it)
                }
                it.contains("ktra.vn") -> {
                    var path = URL(it).path

                    if (path.isNotEmpty() && path.first() == '/') {
                        path = path.removeRange(0, 1)
                    }

                    if (!path.contains("/") && !path.contains("?") && !path.contains(".")) {
                        TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                        ActivityHelper.startActivity(this, Intent(this, WebViewActivity::class.java).apply {
                            putExtra(Constant.DATA_1, getString(R.string.stamp_v3_format, path, DeviceUtils.getUniqueDeviceId()))
                            putExtra(Constant.DATA_2, 1)
                        })
                    } else {
                        TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
                        ActivityUtils.startActivity<DetailStampV6Activity, String>(this, Constant.DATA, it)
                    }
                }
                it.contains("cg.icheck.com.vn") -> {
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
                    ActivityUtils.startActivity<DetailStampV5Activity, String>(this, Constant.DATA, it)
                }
                it.startsWith("http") || it.startsWith("https") -> {
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                    WebViewActivity.start(this, it, 1)
                }
                else -> {
                    TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                    handleQr(getQrType(it), it)
                }
            }
        }, 800)
    }

    private fun getQrType(qr: String): Int {
        if (qr.startsWith("http", true) or qr.startsWith("https", true) or qr.startsWith("URL", true)) {
            return Constant.TYPE_URL
        }
        if (qr.startsWith("smsto", true)) {
            return Constant.TYPE_SMS
        }
        if (qr.startsWith("MAILTO", true) or qr.startsWith("mailto:email", true)) {
            return Constant.TYPE_MAIL
        }
        if (qr.startsWith("tel", true)) {
            return Constant.TYPE_PHONE_NUMBER
        }
        if (qr.startsWith("geo", true)) {
            return Constant.TYPE_COORDINATE
        }
        if (qr.startsWith("BEGIN:VCARD", true)) {
            return Constant.TYPE_CONTACT
        }
        if (qr.startsWith("BEGIN:VEVENT", true)) {
            return Constant.TYPE_CALENDAR
        }
        if (qr.startsWith("WIFI", true)) {
            return Constant.TYPE_WIFI
        }
        return Constant.TYPE_UNDEFINED
    }


    private fun handleQr(type: Int, data: String) {
        when (type) {
            Constant.TYPE_URL -> {
                WebViewActivity.start(this, data, 0, getString(R.string.chi_tiet_qr_code))
            }
            Constant.TYPE_SMS -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("smsto:" + data.split(":").get(1)))
                    intent.putExtra("sms_body", data.split(":").get(2))
                    startActivity(intent)
                } catch (e: Exception) {
                }
            }
            Constant.TYPE_MAIL -> {

                vn.icheck.android.ichecklibs.Constant.sendMail(this@V6ScanditActivity, data)
            }
            Constant.TYPE_PHONE_NUMBER -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(data.replace(" ", "").toLowerCase())
                startActivity(intent)
            }
            Constant.TYPE_UNDEFINED -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(Constant.DATA_1, data)
                startActivity(intent)
            }
            Constant.TYPE_CONTACT -> {
                try {
                    val arr = data.split("\n")
                    val phone = arr.find {
                        it.contains("TEL")
                    }?.replace("TEL:", "", true)

                    val list = arr.iterator()
                    var name = ""
                    var email = ""
                    var address = ""
                    var note = ""
                    while (list.hasNext()) {
                        val item = list.next()
                        when {
                            item.contains("N:", true) -> {
                                name = item.replace("N:", "", true).replace(";", " ")
                            }
                            item.contains("EMAIL:", true) -> {
                                email = item.replace("EMAIL:", "", true)
                            }
                            item.contains("ADR:", true) -> {
                                address = item.replace("ADR:", "", true).replace(";", "")
                            }
                            item.contains("NOTE:", true) -> {
                                note = item.replace("NOTE:", "", true)
                            }
                        }
                    }

                    val intent = Intent(Intent.ACTION_INSERT)
                    intent.type = ContactsContract.Contacts.CONTENT_TYPE
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                    intent.putExtra(ContactsContract.Intents.Insert.COMPANY, address)
                    intent.putExtra(ContactsContract.Intents.Insert.NOTES, note)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.d("TAG", "handleQr: ${e.message}")
                }
            }
            Constant.TYPE_COORDINATE -> {
                val a = data.replace("GEO:", "")
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(a))
                mapIntent.setPackage("com.google.android.apps.maps")

                try {
                    startActivity(mapIntent)
                } catch (e: Exception) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=$a"))
                        startActivity(intent);
                    } catch (exception: ActivityNotFoundException) {
                        showShortErrorToast(getString(R.string.khong_tim_thay_ung_dung_google_map))
                        enableCapture()
                    }
                }
            }
            Constant.TYPE_CALENDAR -> {
                try {
                    val intent = Intent(Intent.ACTION_EDIT)
//                        intent.type = "vnd.android.cursor.item/event"
                    intent.data = CalendarContract.Events.CONTENT_URI

                    val arr = data.split("\n")
                    val SUMMARY = arr.single {
                        it.contains("SUMMARY", true)
                    }.replace("SUMMARY:", "", true)

                    val LOCATION = arr.single {
                        it.contains("LOCATION", true)
                    }.replace("LOCATION:", "", true)

                    val START = TimeHelper.convertDateVnToMillisecond2(arr.single {
                        it.contains("DTSTART", true)
                    }.replace("DTSTART:", "", true))

                    val END = TimeHelper.convertDateVnToMillisecond2(arr.single {
                        it.contains("DTEND", true)
                    }.replace("DTEND:", "", true))

                    intent.putExtra(CalendarContract.Events.TITLE, SUMMARY)
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, LOCATION)
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, START)
                    intent.putExtra(CalendarContract.Events.DTSTART, START)
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, END)
                    intent.putExtra(CalendarContract.Events.DTEND, END)
                    intent.putExtra(CalendarContract.Events.ALL_DAY, false)// periodicity
                    val DESCRIPTION = arr.single {
                        it.contains("URL", true)
                    }.replace("URL:", "", false)
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, DESCRIPTION)
                    startActivity(intent)
                } catch (e: Exception) {
                }
            }
            Constant.TYPE_WIFI -> {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
                        val arr = data.split(";")
                        val ssid = arr.single {
                            it.contains("WIFI", true)
                        }.replace("wifi:s:", "", true)
                        val key = arr.single {
                            it.contains("P:", true)
                        }.replace("p:", "", true)
                        // do post connect processing here
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                            val nwSpecifier = WifiNetworkSpecifier.Builder()
                                .setSsid(ssid)
                                .setWpa2Passphrase(key)
                                .build()
                            val nw = NetworkRequest.Builder()
                                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .setNetworkSpecifier(nwSpecifier)
                                .build()
                            connectivityManager?.requestNetwork(nw, object : ConnectivityManager.NetworkCallback() {
                                override fun onAvailable(network: Network) {

                                }
                            })
                        } else {
                            if (::requestPermissionLauncher.isInitialized) {
                                requestPermissionLauncher.launch(Manifest.permission.CHANGE_NETWORK_STATE)
                            }
                        }
                    } else {
                        val wifiConfig = WifiConfiguration()
                        val arr = data.split(";")
                        val ssid = arr.single {
                            it.contains("WIFI", true)
                        }.replace("wifi:s:", "", true)
                        wifiConfig.SSID = String.format("\"%s\"", ssid)
                        val key = arr.single {
                            it.contains("P:", true)
                        }.replace("p:", "", true)
                        wifiConfig.preSharedKey = String.format("\"%s\"", key)
                        val wifiManager = ICheckApplication.getInstance().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                        val netId = wifiManager.addNetwork(wifiConfig)
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(netId, true)
                        wifiManager.reconnect()
                    }
                    enableCapture()
                } catch (e: Exception) {
                    logError(e)
                }
            }
        }
    }

    private fun showDialogSuggestApp(obj: ICValidStampSocial, codeStamp: String) {
        object : InternalStampDialog(this, obj.suggest_apps!!, obj.code) {
            override fun onDismiss() {
                enableCapture()
            }

            override fun onGoToDetail(code: String?) {
                TrackingAllHelper.trackScanQrcode(viewModel.codeScan, true)
                ActivityUtils.startActivity<StampDetailActivity, String>(this@V6ScanditActivity, Constant.DATA, codeStamp)
            }

            override fun onGoToSms(target: String?, content: String?) {
                TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$target")).apply {
                    putExtra("sms_body", content)
                })
            }

            override fun onGoToEmail(target: String?, content: String?) {
                TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$target")
                }, "Send Email"))
            }

            override fun onGoToLink(target: String?, content: String?) {
                TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                if (target != null) {
                    startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(target)
                    })
                }
            }

            override fun onGoToPhone(target: String?) {
                TrackingAllHelper.trackScanQrcode(viewModel.codeScan, false)
                if (target != null) {
                    phoneNumber = target
                    if (PermissionHelper.checkPermission(this@V6ScanditActivity, Manifest.permission.CALL_PHONE, requestPhone)) {
                        ContactUtils.callFast(this@V6ScanditActivity, target)
                    }
                }
            }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ICK_REQUEST_CAMERA) {
            if (PermissionHelper.checkResult(grantResults)) {
                initDataCapture()

            } else {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_REVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else if (requestCode == requestCropMedia) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra(Constant.DATA_1)?.let { url ->
                    comPressImage(File(url))
                    takeImageDialog.dismiss()
                }

            } else {
                scanImage.set(true)
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                comPressImage(File(UCrop.getOutput(data!!).toString().replace("file:///", "")))
                takeImageDialog.dismiss()
            } else {
                scanImage.set(true)
            }
        }
    }
}