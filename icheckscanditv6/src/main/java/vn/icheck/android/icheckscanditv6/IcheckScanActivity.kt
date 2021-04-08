package vn.icheck.android.icheckscanditv6

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.async.Callback
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.*
import com.scandit.datacapture.core.ui.DataCaptureView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ichecklibs.*
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.icheckscanditv6.databinding.IckScanCustomViewBinding
import vn.icheck.android.network.BuildConfig
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICProductDetail
import java.net.URL
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
    private var phoneNumber: String = ""


    val scanImage = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("scan_only_chat", false)) {
            viewModel.scanOnlyChat = true
        }
        request()
    }

    private fun initDataCapture() {
//        takeImageListener = object : TakeMediaDialog.TakeImageListener {
//            override fun onPickMediaSucess(file: File) {
//                try {
//                    val bm = BitmapFactory.decodeFile(file.getAbsolutePath())
//                    val width = bm.width
//                    val height = bm.height
//                    val ruler = if (width >= height) width else height
//                    if (ruler > 500) {
//                        val scale = ruler / 500
//                        val scaled = bm.scale(width / scale, height / scale)
//                        val source = BitmapFrameSource.of(scaled)
//                        source?.switchToDesiredState(FrameSourceState.ON)
//                        dataCaptureContext.setFrameSource(source)
//                    } else {
//                        val source = BitmapFrameSource.of(bm)
//                        source?.switchToDesiredState(FrameSourceState.ON)
//                        dataCaptureContext.setFrameSource(source)
//                    }
//                    scanImage.set(true)
//                } catch (e: Exception) {
//                    resetCamera()
//                    logError(e)
//                    barcodeCapture.isEnabled = true
//                }
//            }
//
//            override fun onPickMuliMediaSucess(file: MutableList<File>) {
//
//            }
//
//            override fun onTakeMediaSuccess(file: File?) {
//                try {
//                    val bm = BitmapFactory.decodeFile(file?.getAbsolutePath())
//                    val width = bm.width
//                    val height = bm.height
//                    val ruler = if (width >= height) width else height
//                    if (ruler > 500) {
//                        val scale = ruler / 500
//                        val scaled = bm.scale(width / scale, height / scale)
//                        val source = BitmapFrameSource.of(scaled)
//                        source?.switchToDesiredState(FrameSourceState.ON)
//                        dataCaptureContext.setFrameSource(source)
//                    } else {
//                        val source = BitmapFrameSource.of(bm)
//                        source?.switchToDesiredState(FrameSourceState.ON)
//                        dataCaptureContext.setFrameSource(source)
//                    }
//                    scanImage.set(true)
//                } catch (e: Exception) {
//                    logError(e)
//                    resetCamera()
//                }
//            }
//        }
//        takeImageDialog = TakeMediaDialog(takeImageListener, false, cropImage = false, isVideo = false)

        val key = if (BuildConfig.FLAVOR.contentEquals("dev")) getString(R.string.scandit_v6_key_dev) else getString(R.string.scandit_v6_key_live)
        dataCaptureContext = DataCaptureContext.forLicenseKey(key)
        val settings = BarcodeCaptureSettings().apply {
            Symbology.values().forEach {
                if (it != Symbology.MICRO_PDF417 && it != Symbology.PDF417) {
                    enableSymbology(it, true)
                    getSymbologySettings(it).isColorInvertedEnabled = true
                }
            }
        }
        settings.getSymbologySettings(Symbology.EAN13_UPCA).setExtensionEnabled("remove_leading_upca_zero", true)
        settings.getSymbologySettings(Symbology.UPCE).setExtensionEnabled("remove_leading_upca_zero", true)

        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings)

        barcodeCapture.addListener(this)
        cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        cameraSettings.preferredResolution = VideoResolution.HD
        camera = Camera.getDefaultCamera(cameraSettings)
        resetCamera()

        val dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext)

        _binding = IckScanCustomViewBinding.inflate(layoutInflater, dataCaptureView, false)
        dataCaptureView.addView(binding.root, getDeviceWidth(), getDeviceHeight())
        setContentView(dataCaptureView)
        val lp = dataCaptureView.layoutParams
        lp.height = getDeviceHeight() + 50.toPx(resources)
        dataCaptureView.layoutParams = lp
        initViews()


    }

    private fun resetCamera() {
        lifecycleScope.launch {
            delay(400)
            if (camera?.currentState != FrameSourceState.ON) {
                camera?.switchToDesiredState(FrameSourceState.ON, object : Callback<Boolean> {
                    override fun run(result: Boolean) {
                        if (result) {
                            dataCaptureContext.setFrameSource(camera)
                            barcodeCapture.isEnabled = true
                        } else {
                            resetCamera()
                        }
                    }
                })

            }
        }
    }

    private fun offCamera() {
        if (camera?.currentState != FrameSourceState.OFF) {
            camera?.switchToDesiredState(FrameSourceState.OFF, object : Callback<Boolean> {
                override fun run(result: Boolean) {
                    if (!result) {
                        offCamera()
                    }
                }
            })
        }
    }

    override fun onSessionUpdated(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {
        super.onSessionUpdated(barcodeCapture, session, data)
        if (scanImage.get()) {
            scanImage.set(false)
            Handler().postDelayed({
                if (session.newlyRecognizedBarcodes.isEmpty()){
                    runOnUiThread {
                        DialogHelper.showConfirm(this, null, "Không thể tìm thấy mã vạch hoặc mã QR từ ảnh được chọn. Vui lòng thử lại với ảnh khác!", object : ConfirmDialogListener {
                            override fun onDisagree() {
                                resetCamera()
                            }

                            override fun onAgree() {
                                resetCamera()
                            }
                        })

                    }
                }
            }, 200)

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
        binding?.imgHelp?.setOnClickListener {
            viewModel.setGuide()
        }
        binding?.imgFlash?.setOnClickListener {
            viewModel.setFlash()
        }
        binding?.btnMyCode?.setOnClickListener {
//            simpleStartActivity(MyQrActivity::class.java)
            finish()
        }
        binding.btnClear.setOnClickListener {
            barcodeCapture.isEnabled = false
            if (viewModel.scanOnlyChat) {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
        binding?.root?.setOnClickListener {
            viewModel.offGuide()
        }
        binding.imgSdha.beGone()
        binding.imgSdha.setOnClickListener {
//            request(takeImageDialog)
        }
        binding?.imgNmbt?.setOnClickListener {
            binding.imgNmbt.isEnabled = false
            BarcodeBottomDialog.show(supportFragmentManager, false, object : BarcodeBottomDialog.OnBarCodeDismiss {
                override fun onDismiss() {
                    binding.imgNmbt.isEnabled = true
                }

                override fun onSubmit(code: String) {
                    binding.imgNmbt.isEnabled = true

                    if (viewModel.scanOnlyChat) {
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra("BARCODE", code)
                        })
                        finish()
                        return
                    }
//                    if (code.startsWith("u-")) {
//                        val id = code.replace("u-", "").toLongOrNull()
//                        if (id != null) {
//                            IckUserWallActivity.create(id, this@V6ScanditActivity)
//                            return
//                        }
//                    }

                    when {
                        viewModel.scanOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, obj.data) })
                                        finish()
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    showSimpleErrorToast(error?.message)
                                }
                            })
                        }
                        viewModel.reviewOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            showSimpleErrorToast("Không tìm thấy sản phẩm")
                                            barcodeCapture.isEnabled = true
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    showSimpleErrorToast("Không tìm thấy sản phẩm")
                                                    barcodeCapture.isEnabled = true
                                                }
                                                else -> {
                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            showSimpleErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu")
                                                            barcodeCapture.isEnabled = true
                                                        }
                                                        "adminDeactive" -> {
                                                            showSimpleErrorToast("Sản phẩm không cho quét")
                                                            barcodeCapture.isEnabled = true
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
                                        showSimpleErrorToast("Không tìm thấy sản phẩm")
                                        barcodeCapture.isEnabled = true
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    showSimpleErrorToast(error?.message)
                                    barcodeCapture.isEnabled = true
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
//                            }
//                            IckProductDetailActivity.start(this@V6ScanditActivity, code, true)
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
//                mPicker?.switchTorchOn(model.isFlash)

                if (model.showGuide) {
                    binding.root.setAllEnabled(false)
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
                    barcodeCapture.isEnabled = true
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
//            try {
//                takeImageDialog?.show(supportFragmentManager, null)
//            } catch (e: Exception) {
//                logError(e)
//            }
        }
    }

    override fun onBarcodeScanned(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {
        if (session.newlyRecognizedBarcodes.isEmpty()) return
        val barcode = session.newlyRecognizedBarcodes[0]
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
                if (code.startsWith("u-")) {
                    val id = code.replace("u-", "").toLongOrNull()
//                    if (id != null) {
//                        IckUserWallActivity.create(id, this)
//                    }
                }
                if (symbology == Symbology.QR) {
//                    TrackingAllHelper.trackScanStart(Constant.MA_QR)
                    viewModel.checkQrStampSocial()
                } else {
//                    TrackingAllHelper.trackScanStart(Constant.MA_VACH)
                    when {
                        viewModel.scanOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, obj.data) })
                                        finish()
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
//                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    showSimpleErrorToast(error?.message)
                                }
                            })
                        }
                        viewModel.reviewOnly -> {
                            viewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
//                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                            showSimpleErrorToast("Không tìm thấy sản phẩm")
                                            barcodeCapture.isEnabled = true
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
//                                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                                    showSimpleErrorToast("Không tìm thấy sản phẩm")
                                                    barcodeCapture.isEnabled = true
                                                }
                                                else -> {

                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
//                                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                                            showSimpleErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu")
                                                            barcodeCapture.isEnabled = true
                                                        }
                                                        "adminDeactive" -> {
//                                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                                            showSimpleErrorToast("Sản phẩm không cho quét")
                                                            barcodeCapture.isEnabled = true
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
//                                        TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                        showSimpleErrorToast("Không tìm thấy sản phẩm")
                                        barcodeCapture.isEnabled = true
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
//                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    showSimpleErrorToast(error?.message)
                                    barcodeCapture.isEnabled = true
                                }
                            })
                        }
                        else -> {
                            if (code.startsWith("u-") || code.startsWith("U-")) {
                                if (code.count { "-".contains(it) } == 1) {
                                    try {
                                        val userID = code.split("-")[1]
//                                        if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
//                                            IckUserWallActivity.create(userID.toLong(), this)
//                                        }
                                    } catch (e: Exception) {
//                                        TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                        e.printStackTrace()
                                    }
                                }
                            }
//                            IckProductDetailActivity.start(this, code, true)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        offCamera()
        dataCaptureContext.release()
    }

    override fun onPause() {
        super.onPause()
        offCamera()
    }

    override fun onResume() {
        super.onResume()
        barcodeCapture.isEnabled = true
        resetCamera()
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
            showSimpleErrorToast("Kết nối mạng của bạn có vấn đề.\nVui lòng thử lại.")
            barcodeCapture.isEnabled = true
        })
        viewModel.stampFake.observe(this, {
//            TrackingAllHelper.trackScanFailed(Constant.MA_QR)
//            DialogHelper.showNotification(this, it, false, object : NotificationDialogListener {
//                override fun onDone() {
//                    barcodeCapture.isEnabled = true
//                }
//            })
        })

        viewModel.errorQr.observe(this, {
            checkStampQr(it)
        })

        viewModel.stampHoaPhat.observe(this, {
//            ActivityUtils.startActivity<DetailStampHoaPhatActivity, String>(this, Constant.DATA, viewModel.codeScan)
        })

        viewModel.stampThinhLong.observe(this, {
//            ActivityUtils.startActivity<DetailStampThinhLongActivity, String>(this, Constant.DATA, viewModel.codeScan)
        })

        viewModel.showDialogSuggestApp.observe(this, {
//            showDialogSuggestApp(it, it.code ?: viewModel.codeScan)
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

//                    WebViewActivity.start(this, link + params.toString(), 1, null, false)
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
//                Constant.isMarketingStamps(it) -> {
//                    WebViewActivity.start(this, it, 1, null, true)
//                }
//                it.contains("qcheck-dev.vn") || it.contains("qcheck.vn") || it.contains("qrcode.icheck.com.vn") -> {
//                    ActivityUtils.startActivity<DetailStampActivity, String>(this, Constant.DATA, it)
//                }
                it.contains("ktra.vn") -> {
                    var path = URL(it).path

                    if (path.isNotEmpty() && path.first() == '/') {
                        path = path.removeRange(0, 1)
                    }

//                    if (!path.contains("/") && !path.contains("?") && !path.contains(".")) {
//                        ActivityHelper.startActivity(this, Intent(this, WebViewActivity::class.java).apply {
//                            putExtra(Constant.DATA_1, getString(R.string.stamp_v3_format, path, DeviceUtils.getUniqueDeviceId()))
//                            putExtra(Constant.DATA_2, 1)
//                        })
//                    } else {
//                        ActivityUtils.startActivity<DetailStampV6Activity, String>(this, Constant.DATA, it)
//                    }
                }
//                it.contains("cg.icheck.com.vn") -> {
//                    ActivityUtils.startActivity<DetailStampV5Activity, String>(this, Constant.DATA, it)
//                }
//                it.startsWith("http") || it.startsWith("https") -> {
//                    WebViewActivity.start(this, it, 1)
//                }
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
//                WebViewActivity.start(this, data, 0, "Chi tiết Qr Code")
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
//                val intent = Intent(this, WebViewActivity::class.java)
//                intent.putExtra(Constant.DATA_1, data)
//                startActivity(intent)
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
                        showSimpleErrorToast("Không tìm thấy ứng dụng google map!")
                        barcodeCapture.isEnabled = true
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

//                    val START = TimeHelper.convertDateVnToMillisecond2(arr.single {
//                        it.contains("DTSTART", true)
//                    }.replace("DTSTART:", "", true))
//
//                    val END = TimeHelper.convertDateVnToMillisecond2(arr.single {
//                        it.contains("DTEND", true)
//                    }.replace("DTEND:", "", true))

//                    intent.putExtra(CalendarContract.Events.TITLE, SUMMARY)
//                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, LOCATION)
//                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, START)
//                    intent.putExtra(CalendarContract.Events.DTSTART, START)
//                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, END)
//                    intent.putExtra(CalendarContract.Events.DTEND, END)
//                    intent.putExtra(CalendarContract.Events.ALL_DAY, false)// periodicity
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
//                    val wifiManager = ICheckApplication.getInstance().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
//                    val netId = wifiManager.addNetwork(wifiConfig)
//                    wifiManager.disconnect()
//                    wifiManager.enableNetwork(netId, true)
//                    wifiManager.reconnect()
                }
                barcodeCapture.isEnabled = true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (viewModel.scanOnlyChat) {
            setResult(Activity.RESULT_CANCELED)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ICK_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initDataCapture()

            } else {
                finish()
            }
        }
    }
}