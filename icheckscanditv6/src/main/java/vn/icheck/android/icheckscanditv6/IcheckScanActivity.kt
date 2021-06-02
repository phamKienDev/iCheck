package vn.icheck.android.icheckscanditv6

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.async.Callback
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.common.feedback.Sound
import com.scandit.datacapture.core.common.feedback.Vibration
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.*
import com.scandit.datacapture.core.ui.DataCaptureView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ichecklibs.*
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.ichecklibs.util.*
import vn.icheck.android.ichecklibs.web.WebViewActivity
import vn.icheck.android.icheckscanditv6.databinding.IckScanCustomViewBinding
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.ICProductDetail
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

const val CONTRIBUTE_REQUEST = 1
const val ICK_REQUEST_CAMERA = 2

class IcheckScanActivity : AppCompatActivity(), BarcodeCaptureListener {

    companion object {
        fun create(context: Context, tab: Int = -1) {
            val i = Intent(context, IcheckScanActivity::class.java)
            i.putExtra("data_1", tab)
            context.startActivity(i)
        }

        fun scanOnly(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, IcheckScanActivity::class.java)
            i.putExtra("scan_only", true)
            context.startActivityForResult(i, requestCode)
        }

        fun scanOnlyChat(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, IcheckScanActivity::class.java)
            i.putExtra("scan_only_chat", true)
            context.startActivityForResult(i, requestCode)
        }

        fun reviewOnly(context: FragmentActivity) {
            val i = Intent(context, IcheckScanActivity::class.java)
            i.putExtra("review_only", true)
            context.startActivityForResult(i, 1)
        }

        fun scanLoyalty(context: FragmentActivity, intent: Intent) {
            intent.putExtra("scan_loyalty", true)
            context.startActivity(intent)
        }
    }

    lateinit var dataCaptureContext: DataCaptureContext
    lateinit var barcodeCapture: BarcodeCapture
    lateinit var cameraSettings: CameraSettings
    var camera: Camera? = null
    var _binding: IckScanCustomViewBinding? = null
    val binding get() = _binding!!
    val viewModel by viewModels<IcheckScanViewModel>()
    private val guideArr = arrayListOf<View?>()
    private val requestPhone = 2
    private val requestCropMedia = 3
    private var phoneNumber: String = ""
    lateinit var dataCaptureView: DataCaptureView

    /**
     * @param type == 0 -> Scan tích điểm đổi quà
     * @param type == 1 -> Scan tích điểm dài hạn
     */
    var type = 0

    var name = ""


    private val takeImageListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {

            comPressImage(file)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
            CropImageActivity.start(this@IcheckScanActivity, filePath, null, ratio, requestCropMedia)
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
                                DialogHelper.showNotification(this@IcheckScanActivity, R.string.thong_bao, R.string.khong_thay_ma_vach, true, object :
                                    NotificationDialogListener {

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
            resetCamera()
            enableCapture(barcodeCapture)
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

    private lateinit var takeImageDialog: TakeMediaDialog
    val scanImage = AtomicBoolean(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request()
    }

    private fun initDataCapture() {
        initTakeImageDialog()
        initBarcodeCapture()
        initCamera()
        resetCamera()
        initDataCaptureView()
        checkIsReview()
        checkIsScan()
        checkIsScanChatOnly()
        initViews()
    }


    private fun checkIsScan() {
        if (intent.getBooleanExtra("scan_only", false)) {
            viewModel.scanOnly = true
            _binding?.btnMyCode?.visibility = View.GONE
            _binding?.btnQm.beGone()
        } else {
            viewModel.scanOnly = false
        }
    }

    private fun checkIsScanChatOnly() {
        if (intent.getBooleanExtra("scan_only_chat", false)) {
            viewModel.scanOnlyChat = true
            _binding?.btnMyCode.beGone()
            _binding?.btnQm.beGone()
            _binding?.imgSdha.beGone()
            _binding?.imgHdSdha.beGone()
        } else {
            viewModel.scanOnlyChat = false
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
//        val key = if (BuildConfig.FLAVOR.contentEquals("dev")) getString(R.string.scandit_v6_key_dev) else getString(R.string.scandit_v6_key_live)
//        dataCaptureContext = DataCaptureContext.forLicenseKey(key)
//        val settings = BarcodeCaptureSettings().apply {
//            Symbology.values().forEach {
//                if (it != Symbology.MICRO_PDF417 && it != Symbology.PDF417  && it != Symbology.USPS_INTELLIGENT_MAIL) {
//                    enableSymbology(it, true)
//                    getSymbologySettings(it).isColorInvertedEnabled = true
//                }
//            }
//        }
//        settings.getSymbologySettings(Symbology.EAN13_UPCA).setExtensionEnabled("remove_leading_upca_zero", true)
//        settings.getSymbologySettings(Symbology.UPCE).setExtensionEnabled("remove_leading_upca_zero", true)
//
//        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings)
        if (DataCaptureManager.barcodeCapture == null) {
            showShortErrorToast("Đã xảy ra lỗi vui lòng thử lại sau")
            finish()
        }
        if (DataCaptureManager.dataCaptureContext == null) {
            showShortErrorToast("Đã xảy ra lỗi vui lòng thử lại sau")
            finish()
        }
        barcodeCapture = DataCaptureManager.barcodeCapture!!
        dataCaptureContext = DataCaptureManager.dataCaptureContext!!
        val vib: Vibration? = if (SettingManager.getVibrateSetting) Vibration() else null
        val sound: Sound? = if (SettingManager.getSoundSetting) Sound.defaultSound() else null
        barcodeCapture.feedback.success = Feedback(vib, sound)
        barcodeCapture.addListener(this)
    }

    private fun initCamera() {
        cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        cameraSettings.preferredResolution = VideoResolution.HD
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
                        enableCapture(barcodeCapture)
                        pushUpHeight()
                    } else {
                        resetCamera()
                    }
                }
            })

        }
    }


    private fun offCamera() {
        barcodeCapture.isEnabled = false
        camera?.switchToDesiredState(FrameSourceState.OFF, object : Callback<Boolean> {
            override fun run(result: Boolean) {
                if (!result) {
                    offCamera()
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
        guideArr.add(binding?.imgNmspTip)
        guideArr.add(binding?.imgTorchTip)
        guideArr.add(binding?.imgXmdd)
        guideArr.add(binding?.imgHdSdha)
        binding?.imgHelp?.setOnClickListener {
            viewModel.setGuide()
        }
        binding?.imgFlash?.setOnClickListener {
            viewModel.setFlash()
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
                binding.imgSdha.isEnabled = true
            }
        }
        binding?.imgNmbt?.setOnClickListener {
            offCamera()
            BarcodeBottomDialog.show(supportFragmentManager, false, object : BarcodeBottomDialog.OnBarCodeDismiss {
                override fun onDismiss() {
                    resetCamera()
                }

                override fun onSubmit(mCode: String) {
                    val code = mCode.trim()
                    resetCamera()
                    if (viewModel.scanOnlyChat) {
                        setResult(Activity.RESULT_OK, Intent().apply {

                            putExtra("BARCODE", code)

                        })
                        finish()
                        return
                    }


                    when {
                        viewModel.scanOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {

                                        if (obj.data?.state == null || obj.data?.state == null) {

                                            showShortErrorToast("Không tìm thấy sản phẩm")
                                            enableCapture(barcodeCapture)
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showShortErrorToast("Không tìm thấy sản phẩm")
                                                    enableCapture(barcodeCapture)
                                                }
                                                else -> {

                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showShortErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu")
                                                            enableCapture(barcodeCapture)
                                                        }
                                                        "adminDeactive" -> {
                                                            showShortErrorToast("Sản phẩm không cho quét")
                                                            enableCapture(barcodeCapture)
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
                                        showShortErrorToast("Không tìm thấy sản phẩm")
                                        enableCapture(barcodeCapture)
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    showShortErrorToast(error?.message)
                                }
                            })
                        }
                        viewModel.reviewOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            showShortErrorToast("Không tìm thấy sản phẩm")
                                            enableCapture(barcodeCapture)
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showShortErrorToast("Không tìm thấy sản phẩm")
                                                    enableCapture(barcodeCapture)
                                                }
                                                else -> {
                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showShortErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu")
                                                            enableCapture(barcodeCapture)
                                                        }
                                                        "adminDeactive" -> {
                                                            showShortErrorToast("Sản phẩm không cho quét")
                                                            enableCapture(barcodeCapture)
                                                        }
                                                        else -> {
//                                                            startActivityForResult(Intent(this@V6ScanditActivity, EditReviewActivity::class.java).apply {
//                                                                putExtra(Constant.DATA_1, obj.data?.id)
//                                                            }, SCAN_REVIEW)
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    } else {
                                        showShortErrorToast("Không tìm thấy sản phẩm")
                                        enableCapture(barcodeCapture)
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    showShortErrorToast(error?.message)
                                    enableCapture(barcodeCapture)
                                }
                            })
                        }
                        else -> {
//                            if (code.startsWith("u-") || code.startsWith("U-")) {
//                                if (code.count { "-".contains(it) } == 1) {
//                                    try {
//                                        val userID = code.split("-")[1]
//                                        if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
//                                            IckUserWallActivity.create(userID.toLong(), this@V6ScanditActivity)
//                                            return
//                                        }
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }
//                                }
//                            } else {
//                                IckProductDetailActivity.start(this@V6ScanditActivity, code, true)
//                            }
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
                    offCamera()
                    barcodeCapture.isEnabled = false
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
                ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CONTRIBUTE_REQUEST
                )
            }
        } else {
            try {
                if (!dialog?.isAdded) {
                    dialog?.show(supportFragmentManager, null)
                }
            } catch (e: Exception) {
//                logError(e)
            }
        }
    }

    override fun onBarcodeScanned(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {

        if (session.newlyRecognizedBarcodes.isEmpty()) return
        val barcode = session.newlyRecognizedBarcodes[0]
        scanImage.set(false)
        barcodeCapture.isEnabled = false
        runOnUiThread {
            val code = barcode.data

            if (!code.isNullOrEmpty()) {
                viewModel.codeScan = code

                val symbology = barcode.symbology

                if (viewModel.scanOnlyChat) {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        if (symbology == Symbology.QR) {
                            putExtra("QR_CODE", code)
                        } else {
                            putExtra("BARCODE", code)
                        }
                    })
                    finish()
                }
            }
        }
    }

    private fun enableCapture(barcodeCapture: BarcodeCapture) {
        lifecycleScope.launch {
            barcodeCapture.isEnabled = true
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
                ) != PackageManager.PERMISSION_GRANTED) {
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
            showShortErrorToast("Kết nối mạng của bạn có vấn đề.\nVui lòng thử lại.")
            enableCapture(barcodeCapture)
        })
        viewModel.stampFake.observe(this, {
//            TrackingAllHelper.trackScanFailed(Constant.MA_QR)
            DialogHelper.showNotification(this, it, false, object : NotificationDialogListener {
                override fun onDone() {
                    enableCapture(barcodeCapture)
                }
            })
        })

        viewModel.errorQr.observe(this, {
            checkStampQr(it)
        })

//        viewModel.stampHoaPhat.observe(this, {
//            ActivityUtils.startActivity<DetailStampHoaPhatActivity, String>(this, Constant.DATA, viewModel.codeScan)
//        })
//
//        viewModel.stampThinhLong.observe(this, {
//            ActivityUtils.startActivity<DetailStampThinhLongActivity, String>(this, Constant.DATA, viewModel.codeScan)
//        })

//        viewModel.showDialogSuggestApp.observe(this, {
//            showDialogSuggestApp(it, it.code ?: viewModel.codeScan)
//        })

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

                    WebViewActivity.start(this, link + params.toString(), 1, null, false)
                }
                it.code.isNullOrEmpty() -> {
                    checkStampQr(viewModel.codeScan)
                }
                else -> {
//                    ActivityUtils.startActivity<DetailStampActivity, String>(this, Constant.DATA, it.code!!)
                }
            }
        })
    }

    private fun checkStampQr(it: String) {
        Handler().postDelayed({
            when {
//                it.startsWith("u-") || it.startsWith("U-") -> {
//                    when {
//                        viewModel.scanOnly || viewModel.reviewOnly -> {
//                            showShortErrorToast("Không tìm thấy sản phẩm")
//                            enableCapture(barcodeCapture)
//                        }
//                        else -> {
//                            if (it.count { "-".contains(it) } == 1) {
//                                try {
//                                    val userID = it.split("-")[1]
//                                    if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
//                                        IckUserWallActivity.create(userID.toLong(), this)
//                                    }
//                                } catch (e: Exception) {
//                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
//                                    e.printStackTrace()
//                                }
//                            }
//                            enableCapture(barcodeCapture)
//                        }
//                    }
//                }
//                Constant.isMarketingStamps(it) -> {
//                    WebViewActivity.start(this, it, 1, null, true)
//                }
//                it.contains("qcheck-dev.vn") || it.contains("qcheck.vn") || it.contains("qrcode.icheck.com.vn") -> {
//                    ActivityUtils.startActivity<DetailStampActivity, String>(this, Constant.DATA, it)
//                }
//                it.contains("ktra.vn") -> {
//                    var path = URL(it).path
//
//                    if (path.isNotEmpty() && path.first() == '/') {
//                        path = path.removeRange(0, 1)
//                    }
//
//                    if (!path.contains("/") && !path.contains("?") && !path.contains(".")) {
//                        ActivityHelper.startActivity(this, Intent(this, WebViewActivity::class.java).apply {
//                            putExtra(Constant.DATA_1, getString(R.string.stamp_v3_format, path, DeviceUtils.getUniqueDeviceId()))
//                            putExtra(Constant.DATA_2, 1)
//                        })
//                    } else {
//                        ActivityUtils.startActivity<DetailStampV6Activity, String>(this, Constant.DATA, it)
//                    }
//                }
//                it.contains("cg.icheck.com.vn") -> {
//                    ActivityUtils.startActivity<DetailStampV5Activity, String>(this, Constant.DATA, it)
//                }
                it.startsWith("http") || it.startsWith("https") -> {
                    WebViewActivity.start(this, it, 1)
                }
                else -> {
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
                WebViewActivity.start(this, data, 0, "Chi tiết Qr Code")
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
                val mailTo = data.split("MAILTO:").get(1).split("?").get(0)
                val subject = data.split("MAILTO:").get(1).split("?").get(1).split("subject=").get(1).split("&").get(0)
                val body = data.split("MAILTO:").get(1).split("?").get(1).split("subject=").get(1).split("&").get(1).split("body=").get(1)

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/html"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailTo))
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, body)
                startActivity(intent)
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
                        showShortErrorToast("Không tìm thấy ứng dụng google map!")
                        enableCapture(barcodeCapture)
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
                        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                            if (isGranted) {

                            } else {

                            }
                        }
                        requestPermissionLauncher.launch(Manifest.permission.CHANGE_NETWORK_STATE)
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
                    val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    val netId = wifiManager.addNetwork(wifiConfig)
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(netId, true)
                    wifiManager.reconnect()
                }
                enableCapture(barcodeCapture)
            }
        }
    }

//    private fun showDialogSuggestApp(obj: ICValidStampSocial, codeStamp: String) {
//        object : InternalStampDialog(this, obj.suggest_apps!!, obj.code) {
//            override fun onDismiss() {
//                enableCapture(barcodeCapture)
//            }
//
//            override fun onGoToDetail(code: String?) {
//                ActivityUtils.startActivity<DetailStampActivity, String>(this@V6ScanditActivity, Constant.DATA, codeStamp)
//            }
//
//            override fun onGoToSms(target: String?, content: String?) {
//                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$target")).apply {
//                    putExtra("sms_body", content)
//                })
//            }
//
//            override fun onGoToEmail(target: String?, content: String?) {
//                startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
//                    data = Uri.parse("mailto:$target")
//                }, "Send Email"))
//            }
//
//            override fun onGoToLink(target: String?, content: String?) {
//                if (target != null) {
//                    startActivity(Intent().apply {
//                        action = Intent.ACTION_VIEW
//                        data = Uri.parse(target)
//                    })
//                }
//            }
//
//            override fun onGoToPhone(target: String?) {
//                if (target != null) {
//                    phoneNumber = target
//                    if (PermissionHelper.checkPermission(this@V6ScanditActivity, Manifest.permission.CALL_PHONE, requestPhone)) {
//                        ContactUtils.callFast(this@V6ScanditActivity, target)
//                    }
//                }
//            }
//        }.show()
//    }

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
        if (requestCode == 16) {
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
        }
    }
}