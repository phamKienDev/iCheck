package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.dialog.DialogErrorScanGame
import vn.icheck.android.loyalty.dialog.DialogSuccessScanGame
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ReceiveGameResp
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.ScanGameViewModel
import vn.icheck.android.loyalty.screen.scan.V6ScanLoyaltyActivity

//import android.Manifest
//import android.content.Context
//import android.os.Build
//import android.os.Bundle
//import android.telephony.TelephonyManager
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.scandit.barcodepicker.*
//import com.scandit.recognition.Barcode
//import kotlinx.android.synthetic.main.fragment_barcode_scan.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.EventBus
//import vn.icheck.android.loyalty.R
//import vn.icheck.android.loyalty.base.ICMessageEvent
//import vn.icheck.android.loyalty.base.setGone
//import vn.icheck.android.loyalty.base.setInvisible
//import vn.icheck.android.loyalty.base.setVisible
//import vn.icheck.android.loyalty.dialog.DialogErrorScanGame
//import vn.icheck.android.loyalty.dialog.DialogSuccessScanGame
//import vn.icheck.android.loyalty.helper.PermissionHelper
//import vn.icheck.android.loyalty.helper.ToastHelper
//import vn.icheck.android.loyalty.model.ICKBaseResponse
//import vn.icheck.android.loyalty.model.ReceiveGameResp
//import vn.icheck.android.loyalty.network.ICApiListener
//import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
//import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.ScanGameViewModel
//
//class ScanForGameFragment : Fragment(), OnScanListener {
//    private var mPicker: BarcodePicker? = null
//    private var showGuide = false
//    private var isFlash = false
//
//    private var isRequestPermission = false
//    private val requestCamera = 1
//
//    var update = false
//
//    private val args: ScanForGameFragmentArgs by navArgs()
//    private val scanGameViewModel: ScanGameViewModel by activityViewModels()
//    val luckyGameViewModel: LuckyGameViewModel by activityViewModels()
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_barcode_scan, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }
//
//
//    private fun setupScandit() {
//        if (mPicker != null)
//            root.removeView(mPicker!!)
//
//        ScanditLicense.setAppKey(getString(R.string.scandit_license_key))
//        val settings = ScanSettings.create()
//
//        Barcode.ALL_SYMBOLOGIES.forEach {
//            settings.setSymbologyEnabled(it, true)
//            settings.getSymbologySettings(it).isColorInvertedEnabled = true
//        }
//        mPicker = BarcodePicker(context, settings)
//
//        val layout = LayoutInflater.from(context).inflate(R.layout.custom_scan_loyalty, root, false)
//        mPicker?.overlayView?.removeAllViews()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            mPicker?.overlay?.clear()
//        }
//
//        mPicker?.setOnScanListener(this)
//        mPicker?.overlayView?.setVibrateEnabled(false)
//        mPicker?.overlayView?.addView(layout)
//        mPicker?.overlayView?.bringChildToFront(layout)
//        mPicker?.overlayView?.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
//        root.addView(mPicker)
//
//        isFlash = false
//
//        val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        if (tm.networkCountryIso != "VN") {
//            mPicker?.findViewById<View>(R.id.logo_scandit)?.setInvisible()
//        }
//        mPicker?.findViewById<ImageView>(R.id.img_guide)?.run {
//            setOnClickListener {
//                showGuide = !showGuide
//                showGuideUi()
//            }
//        }
//        mPicker?.findViewById<ImageView>(R.id.img_flash)?.run {
//            setOnClickListener {
//                isFlash = !isFlash
//                if (isFlash) {
//                    setImageResource(R.drawable.ic_flash_on_36px)
//                } else {
//                    setImageResource(R.drawable.ic_flash_off_36px)
//                }
//                mPicker?.switchTorchOn(!isFlash)
//            }
//        }
//        mPicker?.findViewById<ImageView>(R.id.img_close)?.setOnClickListener {
//            findNavController().popBackStack()
//        }
//
//        val permissions = arrayOf(Manifest.permission.CAMERA)
//
//        if (PermissionHelper.isAllowPermission(context, permissions)) {
//            mPicker!!.startScanning()
//        } else {
//            isRequestPermission = true
//            requestPermissions(permissions, requestCamera)
//        }
//    }
//
//    private fun showGuideUi() {
//        val textView24 = mPicker?.findViewById<TextView>(R.id.textView24)
//        val imageView16 = mPicker?.findViewById<ImageView>(R.id.imageView16)
//        val imageView33 = mPicker?.findViewById<ImageView>(R.id.imageView33)
//        val textView30 = mPicker?.findViewById<TextView>(R.id.textView30)
//
//        val viewOpa = mPicker?.findViewById<View>(R.id.viewOpa)
//        if (!showGuide) {
//            textView24?.setInvisible()
//            imageView16?.setInvisible()
//            imageView33?.setInvisible()
//            textView30?.setInvisible()
//            viewOpa?.setGone()
//            mPicker?.startScanning()
//        } else {
//            mPicker?.stopScanning()
//            textView24?.setVisible()
//            imageView16?.setVisible()
//            imageView33?.setVisible()
//            textView30?.setVisible()
//            viewOpa?.run {
//                setVisible()
//
//                setOnClickListener {
//                    showGuide = false
//                    textView24?.setInvisible()
//                    imageView16?.setInvisible()
//                    imageView33?.setInvisible()
//                    textView30?.setInvisible()
//                    setGone()
//                    mPicker?.startScanning()
//                }
//            }
//        }
//    }
//
//    override fun didScan(p0: ScanSession?) {
//        p0?.stopScanning()
//        try {
//            var code = p0!!.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)!!.data.trim()
//            if (code.matches("^[0-9]*$".toRegex()) && code.length == 12) {
//                code = "0$code"
//            }
//
//            if (!code.contains(" ")) {
//                code = code.replace("\n".toRegex(), "")
//            }
//            checkCode(code)
//        } catch (e: Exception) {
//            mPicker?.startScanning()
//            ToastHelper.showShortError(context, "???? x???y ra l???i vui l??ng th??? l???i sau")
//        }
//    }
//
//    private fun checkCode(code: String) {
//        lifecycleScope.launch(Dispatchers.Main) {
//            val nc: String = when {
//                code.contains("https://qcheck-dev.vn/") -> {
//                    code.replace("https://qcheck-dev.vn/", "")
//                }
//                code.contains("https://qcheck.vn/") -> {
//                    code.replace("https://qcheck.vn/", "")
//                }
//                code.contains("http://qcheck.vn/") -> {
//                    code.replace("http://qcheck.vn/", "")
//                }
//                code.contains("http://qcheck-dev.vn/") -> {
//                    code.replace("http://qcheck-dev.vn/", "")
//                }
//                else -> {
//                    code
//                }
//            }
//            if (code == nc) {
//                ToastHelper.showLongError(requireContext(), "????y kh??ng ph???i l?? QR code vui l??ng qu??t l???i!")
//                mPicker?.startScanning()
//            } else {
//                scanGameViewModel.scanGameRepository.getGamePlay(args.campaignId, nc, object : ICApiListener<ReceiveGameResp> {
//                    override fun onSuccess(obj: ReceiveGameResp) {
//                        if (obj.statusCode == 200 && obj.data?.play != null) {
//                            mPicker?.stopScanning()
//
//                            object : DialogSuccessScanGame(requireContext(), "B???n c?? th??m ${obj.data.play} l?????t quay", obj.data.campaign?.name ?: args.nameCampaign, args.nameShop, args.avatarShop) {
//                                override fun onDone() {
//                                    dismiss()
//                                    update = true
//                                    findNavController().popBackStack()
//                                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_GAME, obj.data.play + args.currentCount))
//                                }
//
//                                override fun onDismiss() {
//                                    mPicker?.startScanning()
//                                }
//                            }.show()
//                        } else {
//                            mPicker?.stopScanning()
//                            when (obj.status) {
//                                "OUT_OF_TURN" -> {
//                                    object : DialogErrorScanGame(requireContext(), R.drawable.ic_error_scan_game,
//                                            "M?? QRcode c???a s???n ph???m n??y kh??ng c??n l?????t quay", "Th??? qu??t v???i nh???ng m?? QRcode kh??c ????? th??m l?????t quay nh???n ng??n qu?? hay nh??") {
//                                        override fun onDismiss() {
//                                            mPicker?.startScanning()
//                                        }
//
//                                    }.show()
//                                }
//                                "INVALID_PARAM" -> {
//                                    object : DialogErrorScanGame(requireContext(), R.drawable.ic_error_scan_game_1,
//                                            "M?? QRcode c???a s???n ph???m n??y kh??ng thu???c ch????ng tr??nh", "Th??? qu??t v???i nh???ng m?? QRcode kh??c ????? th??m l?????t quay nh???n ng??n qu?? hay nh??") {
//                                        override fun onDismiss() {
//                                            mPicker?.startScanning()
//                                        }
//
//                                    }.show()
//                                }
//                                else -> {
//                                    ToastHelper.showShortError(requireContext(), obj.data?.message)
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onError(error: ICKBaseResponse?) {
//                        ToastHelper.showShortError(requireContext(), error?.message
//                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
//                    }
//                })
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == requestCamera) {
//            if (PermissionHelper.checkResult(grantResults)) {
//                mPicker?.startScanning()
//            } else {
//                isRequestPermission = false
//                ToastHelper.showShortError(requireContext(), R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
//                findNavController().popBackStack()
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        if (isRequestPermission) {
//            isRequestPermission = false
//        } else {
//            setupScandit()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        if (!isRequestPermission) {
//            mPicker?.let { picker ->
//                picker.setOnScanListener(null)
//                picker.stopScanning()
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mPicker?.let { picker ->
//            picker.setOnScanListener(null)
//            picker.stopScanning()
//        }
//        if (!update) {
//            luckyGameViewModel.updatePlay(args.currentCount)
//        }
//    }
//}
class ScanForGameFragment : Fragment() {

    var update = false

    private val args: ScanForGameFragmentArgs by navArgs()
    private val scanGameViewModel: ScanGameViewModel by activityViewModels()
    val luckyGameViewModel: LuckyGameViewModel by activityViewModels()

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (!update) {
            luckyGameViewModel.updatePlay(args.currentCount)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barcode_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        V6ScanLoyaltyActivity.scanOnlyChat(requireActivity(), 77)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
     fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.QR_SCANNED) {
            checkCode(event.data.toString())
        }else if (event.type == ICMessageEvent.Type.SCAN_FAILED) {
            findNavController().popBackStack()
        }
    }


    private fun checkCode(code: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val nc: String = when {
                code.contains("https://qcheck-dev.vn/") -> {
                    code.replace("https://qcheck-dev.vn/", "")
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
                ToastHelper.showLongError(requireContext(), getString(R.string.day_khong_phai_la_qr_code_vui_long_quet_lai))
            } else {
                scanGameViewModel.scanGameRepository.getGamePlay(args.campaignId, nc, object : ICApiListener<ReceiveGameResp> {
                    override fun onSuccess(obj: ReceiveGameResp) {
                        if (obj.statusCode == 200 && obj.data?.play != null) {

                            object : DialogSuccessScanGame(requireContext(), getString(R.string.ban_co_them_d_luot_quay, obj.data.play), obj.data.campaign?.name
                                    ?: args.nameCampaign, args.nameShop, args.avatarShop) {
                                override fun onDone() {
                                    dismiss()
                                    update = true
                                    findNavController().popBackStack()
                                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_GAME, obj.data.play + args.currentCount))
                                }

                                override fun onDismiss() {
                                    findNavController().popBackStack()
                                }
                            }.show()
                        } else {

                            when (obj.status) {
                                "OUT_OF_TURN" -> {
                                    object : DialogErrorScanGame(requireContext(), R.drawable.ic_error_scan_game,
                                            getString(R.string.ma_qrcode_cua_san_pham_nay_khong_con_duoc_quay), getString(R.string.thu_quet_voi_nhung_ma_qrcode_khac_de_them_luot_quay_nhan_ngan_qua_hay_nhe)) {
                                        override fun onDismiss() {
                                            findNavController().popBackStack()
                                        }

                                    }.show()
                                }
                                "INVALID_PARAM" -> {
                                    object : DialogErrorScanGame(requireContext(), R.drawable.ic_error_scan_game_1,
                                            getString(R.string.ma_qrcode_cua_san_pham_nay_khong_thuoc_chuong_trinh), getString(R.string.thu_quet_voi_nhung_ma_qrcode_khac_de_them_luot_quay_nhan_ngan_qua_hay_nhe)) {
                                        override fun onDismiss() {
                                            findNavController().popBackStack()
                                        }

                                    }.show()
                                }
                                else -> {
                                    ToastHelper.showShortError(requireContext(), obj.data?.message)
                                }
                            }
                        }
                    }

                    override fun onError(error: ICKBaseResponse?) {
                        ToastHelper.showShortError(requireContext(), error?.message
                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                        findNavController().popBackStack()
                    }
                })
            }
        }
    }


}