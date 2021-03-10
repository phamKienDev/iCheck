package vn.icheck.android.screen.user.barcode_qr_of_me

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
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
import kotlinx.android.synthetic.main.fragment_qr_and_barcode_of_me.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.scan.viewmodel.ICKScanViewModel
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.ick.*

class QrAndBarcodeOfMeFragment : BaseFragmentMVVM() {
    val viewModel: ICKScanViewModel by activityViewModels()
    private var user: ICUser? = null
    private var setting: ICClientSetting? = null
    private val listKey = mutableListOf<String>()
    var qrWidth = 200.toPx()
    var qrHeight = 200.toPx()

    override val getLayoutID: Int
        get() = R.layout.fragment_qr_and_barcode_of_me

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingTimeOut(10000)
        if (requireActivity().intent.getIntExtra(Constant.DATA_1, -1) == 3) {
            btn_scan.beGone()
            btn_my_code.beGone()
        }

        img_barcode.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (img_barcode != null) {
                    img_barcode.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val viewWidth = img_barcode.width
                    val viewHeight = img_barcode.height
                    val scale = 375.toFloat() / 260
                    val scaleView = requireContext().getDeviceWidth().toFloat() / viewWidth
                    val ratio = scaleView / scale
                    val lp = img_barcode.layoutParams
                    lp.width = (viewWidth * ratio).toInt()
                    lp.height = (viewHeight * ratio).toInt()
                    img_barcode.layoutParams = lp
                }
            }
        })

        img_qr.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (img_qr != null) {
                    img_qr.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val viewWidth = img_qr.width
                    val viewHeight = img_qr.height
                    val scale = 375.toFloat() / 200
                    val scaleView = requireContext().getDeviceWidth().toFloat() / viewWidth
                    val ratio = scaleView / scale
                    val lp = img_qr.layoutParams
                    lp.width = (viewWidth * ratio).toInt()
                    lp.height = (viewHeight * ratio).toInt()
                    qrWidth = lp.width
                    qrHeight = lp.height
                    img_qr.layoutParams = lp
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
        btn_scan.setOnClickListener {
            viewModel.navigate(ICKScanViewModel.ScanScreen.SCAN)
        }
        viewModel.getMyID()
        createQrCodeMarketing()
        initListener()
    }

    private fun createQrCodeMarketing() {
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            if (img_banner == null) {
                return@Observer
            }

            getData(it.toString())
            SettingHelper.getSystemSetting(null, "my-qr-campaign", object : ISettingListener {
                override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                    if (img_banner == null) {
                        return
                    }

                    logDebug(list.toString())
                    if (!list.isNullOrEmpty()) {
                        if (list.firstOrNull { setting ->
                                    setting.key == "my-qr-campaign.button-target"
                                } == null) {
                            btn_campaign.beGone()
                        }
                        if (list.firstOrNull { setting ->
                                    setting.key == "my-qr-campaign.banner"
                                } == null) {
                            img_banner.beGone()
                        }
                        if (list.firstOrNull { setting ->
                                    setting.key == "my-qr-campaign.button-label"
                                } == null) {
                            btn_campaign.beGone()
                        }
                        for (item in list) {
                            when (item.key) {
                                "my-qr-campaign.banner" -> {
                                    Glide.with(requireContext().applicationContext)
                                            .asBitmap()
                                            .load(item.value)
                                            .error(R.drawable.error_load_image)
                                            .placeholder(R.drawable.error_load_image)
                                            .into(object : CustomTarget<Bitmap>() {
                                                override fun onLoadCleared(placeholder: Drawable?) {
                                                }

                                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                    if (img_banner != null) {
                                                        val newBmp = resource.resizeBitmap(img_banner.width, img_banner.height, isCenterCrop = true)
                                                        Glide.with(img_banner.context.applicationContext)
                                                                .load(newBmp)

                                                                .apply(RequestOptions().transform(
                                                                        RoundedCornersTransformation(requireContext(), 18.toPx(), 0, RoundedCornersTransformation.CornerType.TOP)
                                                                ))
                                                                .error(R.drawable.error_load_image)
                                                                .placeholder(R.drawable.error_load_image)
                                                                .into(img_banner)
                                                    }
                                                }
                                            })

                                }
                                "my-qr-campaign.button-label" -> {
                                    btn_campaign simpleText item.value
                                }
                                "my-qr-campaign.button-target" -> {
                                    btn_campaign.setOnClickListener {
                                        WebViewActivity.start(requireActivity(), item.value)
                                    }
                                }
                                "my-qr-campaign.description" -> {
                                    tv_campaign simpleText item.value
                                }
                            }

                        }
                    }
                }

                override fun onRequestError(error: String) {
                    if (img_banner == null) {
                        return
                    }

                    img_banner.beGone()
                    btn_campaign.beGone()
                    tv_campaign.beGone()
                    tv_default_1.beVisible()
                    tv_default_2.beVisible()
                    dismissLoadingScreen()
                }

            })
        })

    }

    private fun initListener() {
        tvClose.setOnClickListener {
            activity?.finish()
        }
    }

    private fun getData(code: String) {
        img_barcode loadSimpleBitmap generateQrCode(code, "Barcode")
        img_qr loadSimpleBitmap generateQrCode(code, "QR-Code")
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

                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300.toPx(), 300.toPx(), hints)
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
                bmp overlayBitmapToCenter logo.resizeBitmap(100.toPx(), 100.toPx())
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