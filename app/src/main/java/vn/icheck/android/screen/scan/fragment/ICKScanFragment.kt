package vn.icheck.android.screen.scan.fragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.scandit.barcodepicker.BarcodePicker
import com.scandit.barcodepicker.OnScanListener
import com.scandit.barcodepicker.ScanOverlay
import com.scandit.barcodepicker.ScanSession
import com.scandit.recognition.Barcode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.internal_stamp.InternalStampDialog
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.constant.SCAN_REVIEW
import vn.icheck.android.databinding.FragmentBarcodeScanBinding
import vn.icheck.android.databinding.IckScanCustomViewBinding
import vn.icheck.android.fragments.BarcodeBottomDialog
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICProductDetail
import vn.icheck.android.network.models.ICValidStampSocial
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.scan.viewmodel.ICKScanViewModel
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.DetailStampHoaPhatActivity
import vn.icheck.android.screen.user.detail_stamp_thinh_long.home.DetailStampThinhLongActivity
import vn.icheck.android.screen.user.detail_stamp_v5.home.DetailStampV5Activity
import vn.icheck.android.screen.user.detail_stamp_v6.home.DetailStampV6Activity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.setAllEnabled
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ContactUtils
import vn.icheck.android.util.kotlin.ToastUtils
import java.net.URL

class ICKScanFragment : BaseFragmentMVVM(), OnScanListener {
    private var mPicker: BarcodePicker? = null
    private val viewArr = arrayListOf<View?>()
    private val guideArr = arrayListOf<View?>()
    private val ickScanViewModel: ICKScanViewModel by activityViewModels()
    var rootBinding: FragmentBarcodeScanBinding? = null
    var layoutBinding: IckScanCustomViewBinding? = null

    private val requestPhone = 2
    private var phoneNumber: String = ""

    override val getLayoutID: Int
        get() = 0

//    private val barcodeBottomDialog = BarcodeBottomDialog()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootBinding = FragmentBarcodeScanBinding.inflate(inflater, container, false)
        layoutBinding = IckScanCustomViewBinding.inflate(inflater, rootBinding?.root, false)
        return rootBinding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        rootBinding = null
        layoutBinding = null
        mPicker?.stopScanning()
        ickScanViewModel.dispose()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ickScanViewModel.scanOnly || ickScanViewModel.reviewOnly) {
            layoutBinding?.btnMyCode?.beGone()
            layoutBinding?.btnScanBuy?.beGone()
            layoutBinding?.btnQm?.beGone()
        }
        setScandit()

        layoutBinding?.btnMyCode?.setOnClickListener {
            ickScanViewModel.navigate(ICKScanViewModel.ScanScreen.MY_CODE)
            onDestroy()
        }
        layoutBinding?.btnScanBuy?.setOnClickListener {
            ickScanViewModel.navigate(ICKScanViewModel.ScanScreen.SCAN_BUY)
            onDestroy()
        }

        viewArr.add(layoutBinding?.btnClear)
        viewArr.add(layoutBinding?.imgNmbt)
        viewArr.add(layoutBinding?.imgFlash)
        viewArr.add(layoutBinding?.imgHelp)
        viewArr.add(layoutBinding?.btnQm)
//        viewArr.add(layoutBinding.btnScanBuy)
        viewArr.add(layoutBinding?.btnMyCode)

        guideArr.add(layoutBinding?.imgScanTip)
        guideArr.add(layoutBinding?.imgNmspTip)
        guideArr.add(layoutBinding?.imgTorchTip)
        guideArr.add(layoutBinding?.imgXmdd)
//        guideArr.add(layoutBinding.imgScanBuyTip)

        layoutBinding?.imgHelp?.setOnClickListener {
            ickScanViewModel.setGuide()
        }
        layoutBinding?.imgFlash?.setOnClickListener {
            ickScanViewModel.setFlash()
        }
        layoutBinding?.btnClear?.setOnClickListener {
            mPicker?.stopScanning()
            requireActivity().finish()
        }
        layoutBinding?.root?.setOnClickListener {
            ickScanViewModel.offGuide()
        }
        layoutBinding?.imgNmbt?.setOnClickListener {
            layoutBinding?.imgNmbt?.isEnabled = false
            this@ICKScanFragment.apply {
                BarcodeBottomDialog.show(requireActivity().supportFragmentManager, false, object : BarcodeBottomDialog.OnBarCodeDismiss {
                    override fun onDismiss() {
                        layoutBinding?.imgNmbt?.isEnabled = true
                        mPicker?.startScanning()
                    }

                    override fun onSubmit(code: String) {
                        layoutBinding?.imgNmbt?.isEnabled = true
                        mPicker?.startScanning()

                        if (ickScanViewModel.scanOnlyChat) {
                            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra(Constant.DATA_1, false)
                                putExtra(Constant.DATA_2, code)
                            })
                            requireActivity().finish()
                            return
                        }
                        if (code.startsWith("u-")) {
                            val id = code.replace("u-", "").toLongOrNull()
                            if (id != null) {
                                IckUserWallActivity.create(id, requireActivity())
                                return
                            }
                        }

                        when {
                            ickScanViewModel.scanOnly -> {
                                ickScanViewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                    override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                        if (obj.data != null) {
                                            requireActivity().setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, obj.data) })
                                            requireActivity().finish()
                                        }
                                    }

                                    override fun onError(error: ICResponseCode?) {
                                        requireContext().showSimpleErrorToast(error?.message)
                                    }
                                })
                            }
                            ickScanViewModel.reviewOnly -> {
                                ickScanViewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                    override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                        if (obj.data != null) {
                                            if (obj.data?.state == null || obj.data?.state == null) {
                                                requireActivity().showSimpleErrorToast("Không tìm thấy sản phẩm")
                                                mPicker?.startScanning()
                                            } else {
                                                when (obj.data?.status) {
                                                    "notFound" -> {
                                                        mPicker?.startScanning()
                                                        requireActivity().showSimpleErrorToast("Không tìm thấy sản phẩm")
                                                    }
                                                    else -> {
                                                        when (obj.data?.state) {
                                                            "businessDeactive" -> {
                                                                mPicker?.startScanning()
                                                                requireActivity().showSimpleErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu")
                                                            }
                                                            "adminDeactive" -> {
                                                                mPicker?.startScanning()
                                                                requireActivity().showSimpleErrorToast("Sản phẩm không cho quét")
                                                            }
                                                            else -> {
                                                                startActivityForResult(Intent(requireActivity(), EditReviewActivity::class.java).apply {
                                                                    putExtra(Constant.DATA_1, obj.data?.id)
                                                                }, SCAN_REVIEW)
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        } else {
                                            mPicker?.startScanning()
                                            requireActivity().showSimpleErrorToast("Không tìm thấy sản phẩm")
                                        }
                                    }

                                    override fun onError(error: ICResponseCode?) {
                                        requireContext().showSimpleErrorToast(error?.message)
                                        mPicker?.startScanning()
                                    }
                                })
                            }
                            else -> {
                                if (code.startsWith("u-") || code.startsWith("U-")) {
                                    if (code.count { "-".contains(it) } == 1) {
                                        try {
                                            val userID = code.split("-")[1]
                                            if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                                                IckUserWallActivity.create(userID.toLong(), requireContext())
                                                return
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                                if (isAdded) {
                                    IckProductDetailActivity.start(requireActivity(), code, true)
                                }
                            }
                        }
                    }
                })
            }
        }

        ickScanViewModel.ickScanModelLiveData.observe(viewLifecycleOwner, { model ->
            lifecycleScope.launch {
                delay(300)

                if (model.isFlash) {
                    layoutBinding?.imgFlash?.setImageResource(R.drawable.ic_flash_on_24px)
                } else {
                    layoutBinding?.imgFlash?.setImageResource(R.drawable.ic_flash_off_24px)
                }
                mPicker?.switchTorchOn(model.isFlash)
                if (model.showGuide) {
                    layoutBinding?.root?.setAllEnabled(false)

                    mPicker?.stopScanning()
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
                    mPicker?.startScanning()
                    layoutBinding?.root?.setAllEnabled(true)
                    model.showGuide = false
                }
            }
        })

        initListener()
    }

    private fun setScandit() {
        try {//        _rootBinding.root.removeAllViews()
            ickScanViewModel.mPicker?.overlayView?.removeAllViews()
            if (mPicker == null) {
                mPicker = ickScanViewModel.mPicker
            } else {
                rootBinding?.root?.removeView(mPicker)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mPicker?.overlay?.clear()
            }
            mPicker?.setOnScanListener(this)
            mPicker?.overlayView?.setVibrateEnabled(false)
            mPicker?.overlayView?.addView(layoutBinding?.root)
            mPicker?.overlayView?.bringChildToFront(layoutBinding?.root)
            mPicker?.overlayView?.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
            if (rootBinding?.root?.childCount == 0) {
                (mPicker?.parent as ViewGroup?)?.removeAllViews()
                rootBinding?.root?.addView(mPicker)
            }
            mPicker?.startScanning()
        } catch (e: Exception) {
            logError(e)
        }
    }

//    private fun request() {
//        if (ContextCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.CAMERA
//                ) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
//        }
//        else {
//            mPicker?.post {
//                ScanditLicense.setAppKey(APIConstants.scanditLicenseKey())
//                mPicker?.startScanning()
//            }
////            setScandit()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        request()
//    }

    private fun initListener() {
        ickScanViewModel.errorString.observe(viewLifecycleOwner, {
            requireContext().showSimpleErrorToast("Kết nối mạng của bạn có vấn đề.\nVui lòng thử lại.")
            mPicker?.startScanning()
        })
        ickScanViewModel.stampFake.observe(viewLifecycleOwner, {
            TrackingAllHelper.trackScanFailed(Constant.MA_QR)
            DialogHelper.showNotification(requireContext(), it, false, object : NotificationDialogListener {
                override fun onDone() {
                    mPicker?.startScanning()
                }
            })
        })

        ickScanViewModel.errorQr.observe(viewLifecycleOwner, {
            checkStampQr(it)
        })

        ickScanViewModel.stampHoaPhat.observe(viewLifecycleOwner, {
            ActivityUtils.startActivity<DetailStampHoaPhatActivity, String>(requireActivity(), Constant.DATA, ickScanViewModel.codeScan)
        })

        ickScanViewModel.stampThinhLong.observe(viewLifecycleOwner, {
            ActivityUtils.startActivity<DetailStampThinhLongActivity, String>(requireActivity(), Constant.DATA, ickScanViewModel.codeScan)
        })

        ickScanViewModel.showDialogSuggestApp.observe(viewLifecycleOwner, {
            showDialogSuggestApp(it, it.code ?: ickScanViewModel.codeScan)
        })

        ickScanViewModel.checkStampSocial.observe(viewLifecycleOwner, Observer {
            when {
                it.onlyIdentity == true -> {
                    val link = if (it.urlIdentity.isNullOrEmpty()) {
                        ickScanViewModel.codeScan
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

                    WebViewActivity.start(requireActivity(), link + params.toString(), 1, null, false)
                }
                it.code.isNullOrEmpty() -> {
                    checkStampQr(ickScanViewModel.codeScan)
                }
                else -> {
                    ActivityUtils.startActivity<DetailStampActivity, String>(requireActivity(), Constant.DATA, it.code!!)
                }
            }
        })
    }

    private fun checkStampQr(it: String) {
        Handler().postDelayed({
            if (isVisible) {
                when {
                    Constant.isMarketingStamps(it) -> {
                        WebViewActivity.start(requireActivity(), it, 1, null, true)
                    }
                    it.contains("qcheck-dev.vn") || it.contains("qcheck.vn") || it.contains("qrcode.icheck.com.vn") -> {
                        ActivityUtils.startActivity<DetailStampActivity, String>(requireActivity(), Constant.DATA, it)
                    }
                    it.contains("ktra.vn") -> {
                        var path = URL(it).path

                        if (path.isNotEmpty() && path.first() == '/') {
                            path = path.removeRange(0, 1)
                        }

                        if (!path.contains("/") && !path.contains("?") && !path.contains(".")) {
                            ActivityHelper.startActivity(requireActivity(), Intent(requireActivity(), WebViewActivity::class.java).apply {
                                putExtra(Constant.DATA_1, getString(R.string.stamp_v3_format, path, DeviceUtils.getUniqueDeviceId()))
                                putExtra(Constant.DATA_2, 1)
                            })
                        } else {
                            ActivityUtils.startActivity<DetailStampV6Activity, String>(requireActivity(), Constant.DATA, it)
                        }
                    }
                    it.contains("cg.icheck.com.vn") -> {
                        ActivityUtils.startActivity<DetailStampV5Activity, String>(requireActivity(), Constant.DATA, it)
                    }
                    it.startsWith("http") || it.startsWith("https") -> {
                        WebViewActivity.start(requireActivity(), it, 1)
                    }
                    else -> {
                        handleQr(getQrType(it), it)
                    }
                }
            }
        }, 800)
    }

    private fun showDialogSuggestApp(obj: ICValidStampSocial, codeStamp: String) {
        object : InternalStampDialog(requireContext(), obj.suggest_apps!!, obj.code) {
            override fun onDismiss() {
                mPicker?.startScanning()
            }

            override fun onGoToDetail(code: String?) {
                ActivityUtils.startActivity<DetailStampActivity, String>(requireActivity(), Constant.DATA, codeStamp)
            }

            override fun onGoToSms(target: String?, content: String?) {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$target")).apply {
                    putExtra("sms_body", content)
                })
            }

            override fun onGoToEmail(target: String?, content: String?) {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$target")
                }, "Send Email"))
            }

            override fun onGoToLink(target: String?, content: String?) {
                if (target != null) {
                    startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(target)
                    })
                }
            }

            override fun onGoToPhone(target: String?) {
                if (target != null) {
                    phoneNumber = target
                    if (PermissionHelper.checkPermission(activity!!, Manifest.permission.CALL_PHONE, requestPhone)) {
                        ContactUtils.callFast(activity!!, target)
                    }
                }
            }
        }.show()
    }

    override fun didScan(p0: ScanSession?) {
        p0?.let { scanSession ->
            val code = scanSession.allRecognizedCodes?.get(scanSession.allRecognizedCodes.lastIndex)?.data

            if (code != null) {
//                if (code.matches("^[0-9]*$".toRegex()) && code.length == 12) {
//                    code = "0$code"
//                }

                ickScanViewModel.codeScan = code

                val symbology = scanSession.allRecognizedCodes?.get(scanSession.allRecognizedCodes.lastIndex)?.symbology
                val qrArray = arrayListOf(Barcode.SYMBOLOGY_QR, Barcode.SYMBOLOGY_DATA_MATRIX, Barcode.SYMBOLOGY_MICRO_PDF417, Barcode.SYMBOLOGY_MICRO_QR)

                if (ickScanViewModel.scanOnlyChat) {
                    requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                        if (qrArray.contains(symbology)) {
                            putExtra(Constant.DATA_1, true)
                        } else {
                            putExtra(Constant.DATA_1, false)
                        }
                        putExtra(Constant.DATA_2, code)
                    })
                    requireActivity().finish()
                    return
                }
                if (code.startsWith("u-")) {
                    val id = code.replace("u-", "").toLongOrNull()
                    if (id != null) {
                        IckUserWallActivity.create(id, requireActivity())
                        return
                    }
                }
                if (qrArray.contains(symbology)) {
                    TrackingAllHelper.trackScanStart(Constant.MA_QR)
                    ickScanViewModel.checkQrStampSocial()
                } else {
                    TrackingAllHelper.trackScanStart(Constant.MA_VACH)
                    when {
                        ickScanViewModel.scanOnly -> {
                            ickScanViewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        requireActivity().setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, obj.data) })
                                        requireActivity().finish()
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    requireContext().showSimpleErrorToast(error?.message)
                                }
                            })
                        }
                        ickScanViewModel.reviewOnly -> {
                            ickScanViewModel.repository.getProductDetailByBarcode(code, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                                    if (obj.data != null) {
                                        if (obj.data?.state == null || obj.data?.state == null) {
                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                            mPicker?.startScanning()
                                            requireActivity().showSimpleErrorToast("Không tìm thấy sản phẩm")
                                        } else {
                                            when (obj.data?.status) {
                                                "notFound" -> {
                                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                                    requireActivity().showSimpleErrorToast("Không tìm thấy sản phẩm")
                                                    mPicker?.startScanning()
                                                }
                                                else -> {

                                                    when (obj.data?.state) {
                                                        "businessDeactive" -> {
                                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                                            mPicker?.startScanning()
                                                            requireActivity().showSimpleErrorToast("Sản phẩm bị ẩn bởi doanh nghiệp sở hữu")
                                                        }
                                                        "adminDeactive" -> {
                                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                                            mPicker?.startScanning()
                                                            requireActivity().showSimpleErrorToast("Sản phẩm không cho quét")
                                                        }
                                                        else -> {
                                                            startActivityForResult(Intent(requireActivity(), EditReviewActivity::class.java).apply {
                                                                putExtra(Constant.DATA_1, obj.data?.id)
                                                            }, SCAN_REVIEW)
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    } else {
                                        TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                        mPicker?.startScanning()
                                        requireActivity().showSimpleErrorToast("Không tìm thấy sản phẩm")
                                    }
                                }

                                override fun onError(error: ICResponseCode?) {
                                    TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                    mPicker?.startScanning()
                                    requireContext().showSimpleErrorToast(error?.message)
                                }
                            })
                        }
                        else -> {
                            if (isAdded) {
                                if (code.startsWith("u-") || code.startsWith("U-")) {
                                    if (code.count { "-".contains(it) } == 1) {
                                        try {
                                            val userID = code.split("-")[1]
                                            if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                                                IckUserWallActivity.create(userID.toLong(), requireContext())
                                                return
                                            }
                                        } catch (e: Exception) {
                                            TrackingAllHelper.trackScanFailed(Constant.MA_VACH)
                                            e.printStackTrace()
                                        }
                                    }
                                }
                                IckProductDetailActivity.start(requireActivity(), code, true)
                            }
                        }
                    }
                }
            }

            p0.stopScanning()
        }
    }

    private fun handleQr(type: Int, data: String) {
        when (type) {
            Constant.TYPE_URL -> {
                WebViewActivity.start(requireActivity(), data, 0, "Chi tiết Qr Code")
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
                val intent = Intent(requireActivity(), WebViewActivity::class.java)
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
                        requireContext().showSimpleErrorToast("Không tìm thấy ứng dụng google map!")
                        mPicker?.startScanning()
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
                    val connectivityManager = requireActivity().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
                    val arr = data.split(";")
                    val ssid = arr.single {
                        it.contains("WIFI", true)
                    }.replace("wifi:s:", "", true)
                    val key = arr.single {
                        it.contains("P:", true)
                    }.replace("p:", "", true)
                    // do post connect processing here
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                        val nwSpecifier = WifiNetworkSpecifier.Builder()
                                .setSsid(ssid)
                                .setWpa2Passphrase(key)
                                .build()
                        val nw = NetworkRequest.Builder()
                                .addTransportType(TRANSPORT_WIFI)
                                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .setNetworkSpecifier(nwSpecifier)
                                .build()
                        connectivityManager?.requestNetwork(nw, object : ConnectivityManager.NetworkCallback() {
                            override fun onAvailable(network: Network) {

                            }
                        })
                    } else {
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
                    val wifiManager = ICheckApplication.getInstance().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    val netId = wifiManager.addNetwork(wifiConfig)
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(netId, true)
                    wifiManager.reconnect()
                }
                mPicker?.startScanning()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {

        } else {

        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ICK_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPicker?.startScanning()
                } else {
                    activity?.finish()
                }
            }
            requestPhone -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    ContactUtils.callFast(requireActivity(), phoneNumber)
                } else {
                    ToastUtils.showShortError(context, R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_REVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                requireActivity().setResult(Activity.RESULT_OK)
                requireActivity().finish()
            } else {
                requireActivity().setResult(Activity.RESULT_CANCELED)
                requireActivity().finish()
            }
        }
    }
}