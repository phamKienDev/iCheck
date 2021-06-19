package vn.icheck.android.screen.scan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.FragmentQrAndBarcodeOfMeBinding
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.util.dpToPx
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.scan.viewmodel.V6ViewModel
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.ick.*

class MyQrActivity : BaseActivityMVVM() {
    lateinit var binding: FragmentQrAndBarcodeOfMeBinding
    private var user: ICUser? = null
    private var setting: ICClientSetting? = null
    private val listKey = mutableListOf<String>()
    var qrWidth = 200.dpToPx()
    var qrHeight = 200.dpToPx()
    val viewModel by viewModels<V6ViewModel>()


    companion object {
        fun createOnly(context: Context) {
            context.startActivity(Intent(context, MyQrActivity::class.java).apply {
                putExtra(Constant.DATA_1, 3)
            })

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentQrAndBarcodeOfMeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        showLoadingTimeOut(10000)
        if (intent.getIntExtra(Constant.DATA_1, -1) == 3) {
            binding.btnScan.beInvisible()
            binding.btnMyCode.beInvisible()
        }

        binding.imgBarcode.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (binding.imgBarcode != null) {
                    binding.imgBarcode.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val viewWidth = binding.imgBarcode.width
                    val viewHeight = binding.imgBarcode.height
                    val scale = 375.toFloat() / 260
                    val scaleView = getDeviceWidth().toFloat() / viewWidth
                    val ratio = scaleView / scale
                    val lp = binding.imgBarcode.layoutParams
                    lp.width = (viewWidth * ratio).toInt()
                    lp.height = (viewHeight * ratio).toInt()
                    binding.imgBarcode.layoutParams = lp
                }
            }
        })

        binding.imgQr.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (binding.imgQr != null) {
                    binding.imgQr.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val viewWidth = binding.imgQr.width
                    val viewHeight = binding.imgQr.height
                    val scale = 375.toFloat() / 200
                    val scaleView = getDeviceWidth().toFloat() / viewWidth
                    val ratio = scaleView / scale
                    val lp = binding.imgQr.layoutParams
                    lp.width = (viewWidth * ratio).toInt()
                    lp.height = (viewHeight * ratio).toInt()
                    qrWidth = lp.width
                    qrHeight = lp.height
                    binding.imgQr.layoutParams = lp
                }
            }
        })
        user = SessionManager.session.user
        setting = SettingManager.clientSetting

        listKey.clear()
        listKey.add("MY_QR_CAMPAIGN_BUTTON")
        listKey.add("MY_QR_CAMPAIGN_TARGET")
        listKey.add("MY_QR_CAMPAIGN_DESCRIPTION")
        listKey.add("MY_QR_CAMPAIGN_BANNER")
        binding.btnScan.setOnClickListener {
            val intent = Intent(this, V6ScanditActivity::class.java)
            intent.putExtra("fromMyQr", true)
            ActivityHelper.startActivity(this, intent)
            finish()
        }
        viewModel.getMyID()
        createQrCodeMarketing()
        initListener()
    }

    private fun setupView() {
        binding.btnCampaign.background = ViewHelper.bgPrimaryCorners4(this)
    }

    private fun createQrCodeMarketing() {
        viewModel.liveData.observe(this, Observer {

            getData(it.toString())
            SettingHelper.getSystemSetting(null, "my-qr-campaign", object : ISettingListener {
                override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {

                    logDebug(list.toString())
                    if (!list.isNullOrEmpty()) {
                        if (list.firstOrNull { setting ->
                                    setting.key == "my-qr-campaign.button-target"
                                } == null) {
                            binding.btnCampaign.beGone()
                        }
                        if (list.firstOrNull { setting ->
                                    setting.key == "my-qr-campaign.banner"
                                } == null) {
                            binding.imgBanner.beGone()
                        }
                        if (list.firstOrNull { setting ->
                                    setting.key == "my-qr-campaign.button-label"
                                } == null) {
                            binding.btnCampaign.beGone()
                        }
                        for (item in list) {
                            when (item.key) {
                                "my-qr-campaign.banner" -> {
                                    Glide.with(applicationContext)
                                            .asBitmap()
                                            .load(item.value)
                                            .error(R.drawable.error_load_image)
                                            .placeholder(R.drawable.error_load_image)
                                            .into(object : CustomTarget<Bitmap>() {
                                                override fun onLoadCleared(placeholder: Drawable?) {
                                                }

                                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                    val newBmp = resource.resizeBitmap(binding.imgBanner.width, binding.imgBanner.height, isCenterCrop = true)
                                                    Glide.with(binding.imgBanner.context.applicationContext)
                                                            .load(newBmp)

                                                            .apply(RequestOptions().transform(
                                                                    RoundedCornersTransformation(this@MyQrActivity, 18.dpToPx(), 0, RoundedCornersTransformation.CornerType.TOP)
                                                            ))
                                                            .error(R.drawable.error_load_image)
                                                            .placeholder(R.drawable.error_load_image)
                                                            .into(binding.imgBanner)
                                                }
                                            })

                                }
                                "my-qr-campaign.button-label" -> {
                                    binding.btnCampaign simpleText item.value
                                    binding.btnCampaign.beVisible()
                                }
                                "my-qr-campaign.button-target" -> {
                                    binding.btnCampaign.setOnClickListener {
                                        WebViewActivity.start(this@MyQrActivity, item.value)
                                    }
                                }
                                "my-qr-campaign.description" -> {
                                    binding.tvCampaign simpleText item.value
                                }
                            }

                        }
                    }
                }

                override fun onRequestError(error: String) {

                    binding.imgBanner.beGone()
                    binding.btnCampaign.beGone()
                    binding.tvCampaign.beGone()
                    binding.tvDefault1.beVisible()
                    binding.tvDefault2.beVisible()
                    dismissLoadingScreen()
                }

            })
        })

    }

    private fun initListener() {
        binding.tvClose.setOnClickListener {
            finish()
        }
    }

    private fun getData(code: String) {
        binding.imgBarcode loadSimpleBitmap generateQrCode(code, "Barcode")
        binding.imgQr loadSimpleBitmap generateQrCode(code, "QR-Code")
    }

    private fun generateQrCode(text: String?, type: String): Bitmap? {
        if (text.isNullOrEmpty()) {
            dismissLoadingScreen()
            return null
        }

        return try {
            if (type == "QR-Code") {
                val writer = QRCodeWriter()

                val hints = mutableMapOf<EncodeHintType, Any>()
                hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
                hints[EncodeHintType.MARGIN] = 0
                hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300.dpToPx(), 300.dpToPx(), hints)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                    }
                }
                val logo = BitmapFactory.decodeResource(resources, R.drawable.logo_icheck_qr)
                dismissLoadingScreen()
                bmp overlayBitmapToCenter logo.resizeBitmap(100.dpToPx(), 100.dpToPx())
            } else {
                val bm = MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, SizeHelper.size400, SizeHelper.size80)

                val width: Int = bm.width
                val height: Int = bm.height
                val pixels = IntArray(width * height)
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        if (bm.get(j, i)) {
                            pixels[i * width + j] = 0xff000000.toInt()
                        } else {
                            pixels[i * width + j] = 0xffffffff.toInt()
                        }
                    }
                }
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                dismissLoadingScreen()
                bitmap
            }

        } catch (e: Exception) {
            dismissLoadingScreen()
            null
        }
    }
}