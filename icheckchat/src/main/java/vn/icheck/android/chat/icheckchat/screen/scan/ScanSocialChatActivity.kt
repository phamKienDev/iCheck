package vn.icheck.android.chat.icheckchat.screen.scan

//import android.Manifest
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.app.Activity
//import android.content.Intent
//import android.os.Build
//import android.os.Handler
//import android.view.LayoutInflater
//import android.view.View
//import androidx.lifecycle.lifecycleScope
//import com.scandit.barcodepicker.*
//import com.scandit.recognition.Barcode
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import vn.icheck.android.chat.icheckchat.R
//import vn.icheck.android.chat.icheckchat.base.BaseActivityChat
//import vn.icheck.android.chat.icheckchat.base.ConstantChat.BARCODE
//import vn.icheck.android.chat.icheckchat.base.ConstantChat.QR_CODE
//import vn.icheck.android.chat.icheckchat.base.view.setAllEnabled
//import vn.icheck.android.chat.icheckchat.base.view.showToastError
//import vn.icheck.android.chat.icheckchat.databinding.ActivityScanSocialChatBinding
//import vn.icheck.android.chat.icheckchat.helper.PermissionChatHelper

//class ScanSocialChatActivity : BaseActivityChat<ActivityScanSocialChatBinding>(), OnScanListener {
//
//    private var mPicker: BarcodePicker? = null
//
//    private val guideArr = arrayListOf<View?>()
//
//    private val requestCamera = 1
//
//    private var isStart = false
//    private var showGuide = false
//    private var isFlash = false
//
//    override val bindingInflater: (LayoutInflater) -> ActivityScanSocialChatBinding
//        get() = ActivityScanSocialChatBinding::inflate
//
//    override fun onInitView() {
//
//        Handler().postDelayed({
//            initScandit()
//        }, 300)
//
//        guideArr.add(binding.imgScanTip)
//        guideArr.add(binding.imgNmspTip)
//        guideArr.add(binding.imgTorchTip)
//    }
//
//    private fun initScandit() {
//        if (mPicker != null)
//            binding.root.removeView(mPicker!!)
//
//        ScanditLicense.setAppKey(getString(R.string.scandit_license_key))
//
//        mPicker = BarcodePicker(this, pickerSetting)
//
//        mPicker?.overlayView?.removeAllViews()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            mPicker?.overlay?.clear()
//        }
//
//        mPicker?.setOnScanListener(this)
//        mPicker?.overlayView?.setVibrateEnabled(false)
//        mPicker?.overlayView?.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
//        binding.root.addView(mPicker, 0)
//
//        initListener()
//
//        val permissions = arrayOf(Manifest.permission.CAMERA)
//        if (PermissionChatHelper.checkPermission(this, permissions, requestCamera)) {
//            isStart = true
//            mPicker!!.startScanning()
//        }
//    }
//
//    private val pickerSetting: ScanSettings
//        get() {
//            val settings = ScanSettings.create()
//            Barcode.ALL_SYMBOLOGIES.forEach {
//                settings.setSymbologyEnabled(it, true)
//            }
//            return settings
//        }
//
//    private fun initListener() {
//        isFlash = false
//
//        binding.imgHelp.run {
//            setOnClickListener {
//                showGuide = !showGuide
//                showGuideUi()
//            }
//        }
//        binding.imgFlash.run {
//            setOnClickListener {
//                isFlash = !isFlash
//                if (isFlash) {
//                    setImageResource(R.drawable.ic_flash_on_24dp_chat)
//                } else {
//                    setImageResource(R.drawable.ic_flash_off_24dp_chat)
//                }
//                mPicker?.switchTorchOn(!isFlash)
//            }
//        }
//
//        binding.btnClear.setOnClickListener {
//            onBackPressed()
//        }
//    }
//
//    private fun showGuideUi() {
//        lifecycleScope.launch {
//            delay(300)
//
//            if (!showGuide) {
//                binding.root.setAllEnabled(true)
//
//                mPicker?.startScanning()
//            } else {
//                mPicker?.pauseScanning()
//
//                binding.root.setAllEnabled(false)
//
//                for (item in guideArr) {
//                    if (item != null) {
//                        item.animate()
//                                .alpha(1f)
//                                .setDuration(1000)
//                                .setListener(object : AnimatorListenerAdapter() {
//                                    override fun onAnimationEnd(animation: Animator?) {
//                                        item.animate()
//                                                .alpha(0f)
//                                                .setDuration(1000)
//                                                .setListener(null)
//                                    }
//                                })
//                        delay(2000)
//                    }
//                }
//                mPicker?.startScanning()
//                binding.root.setAllEnabled(true)
//                showGuide = false
//            }
//        }
//    }
//
//    override fun didScan(p0: ScanSession?) {
//        mPicker?.pauseScanning()
//
//        when (p0?.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)?.symbology) {
//            Barcode.SYMBOLOGY_QR, Barcode.SYMBOLOGY_DATA_MATRIX, Barcode.SYMBOLOGY_MICRO_PDF417, Barcode.SYMBOLOGY_MICRO_QR -> {
//                val dataStr = p0.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)!!.data
//                val i = Intent()
//                i.putExtra(QR_CODE, dataStr)
//                setResult(Activity.RESULT_OK, i)
//                finish()
//            }
//
//            else -> {
//                val code = p0?.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)?.data
//                if (!code.isNullOrEmpty()) {
//                    val i = Intent()
//                    i.putExtra(BARCODE, code)
//                    setResult(Activity.RESULT_OK, i)
//                    finish()
//                } else {
//                    val i = Intent()
//                    setResult(Activity.RESULT_CANCELED, i)
//                    finish()
//                }
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (PermissionChatHelper.checkResult(grantResults)) {
//            if (!isStart) {
//                isStart = true
//                mPicker?.startScanning()
//            }
//        } else {
//            showToastError("Vui lòng vào phần cài đặt và cho phép ứng dụng sử dụng camera")
//            onBackPressed()
//        }
//    }
//
//    override fun onDestroy() {
//        isStart = false
//        mPicker?.pauseScanning()
//        super.onDestroy()
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        isStart = false
//        mPicker?.pauseScanning()
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        val permissions = arrayOf(Manifest.permission.CAMERA)
//        if (PermissionChatHelper.isAllowPermission(this, permissions)) {
//            if (!isStart) {
//                isStart = true
//                mPicker?.startScanning()
//            }
//        }
//    }
//}