package vn.icheck.android.screen.user.scanproduct

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.scandit.barcodepicker.*
import com.scandit.recognition.Barcode
import kotlinx.android.synthetic.main.activity_scan_product.*
import kotlinx.android.synthetic.main.activity_scan_product.layoutContainer
import kotlinx.android.synthetic.main.activity_scan_product.tvInputByHand
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.fragments.InputBarcodeBottomDialog
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SettingManager

/**
 * Created by VuLCL on 8/8/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ScanProductActivity : BaseActivityMVVM(), OnScanListener {
    private lateinit var viewModel: ScanProductViewModel

    private var mPicker: BarcodePicker? = null
    private var isStart = false

    private val requestCamera = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_product)

        Handler().postDelayed({
            initScandit()
        }, 300)

        setupView()
        setupViewModel()
        initListener()
    }

    private fun initScandit() {
        ScanditLicense.setAppKey(APIConstants.scanditLicenseKey())

        mPicker = BarcodePicker(this, pickerSetting)
        mPicker!!.setOnScanListener(this)
        mPicker?.overlayView?.setVibrateEnabled(SettingManager.getVibrateSetting)
        mPicker!!.overlayView.removeAllViews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mPicker!!.overlay.clear()
        }

        mPicker!!.overlayView.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
        layoutContainer.addView(mPicker, 0)

        if (PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, requestCamera)) {
            isStart = true
            mPicker!!.startScanning()
        }
    }

    private val pickerSetting: ScanSettings
        get() {
            val settings = ScanSettings.create()
            Barcode.ALL_SYMBOLOGIES.forEach {
                settings.setSymbologyEnabled(it, true)
                settings.getSymbologySettings(it).isColorInvertedEnabled = true
            }
            return settings
        }

    private fun setupView() {
        tvInputByHand.layoutParams = (tvInputByHand.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight + SizeHelper.size16
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ScanProductViewModel::class.java)

        viewModel.onScanSuccess.observe(this, Observer {
            setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, it) })
            onBackPressed()
        })

        viewModel.showMessage.observe(this, Observer {
            mPicker?.resumeScanning()

            if (!it.isNullOrEmpty()) {
                showLongError(it)
            }
        })
    }

    private fun initListener() {
        imgClose.setOnClickListener {
            onBackPressed()
        }

        tvInputByHand.setOnClickListener {
            mPicker?.pauseScanning()
            tvInputByHand.isClickable = false

            Handler().postDelayed({
                object : InputBarcodeBottomDialog(this@ScanProductActivity) {
                    override fun onDone(barcode: String) {
                        viewModel.scan(barcode)
                        tvInputByHand?.isClickable = true
                    }

                    override fun onDismiss() {
                        mPicker?.resumeScanning()
                        tvInputByHand?.isClickable = true
                    }
                }.show()
            }, 200)
        }

        imgHelp.setOnClickListener {
            showHelper()
        }

        imgFlash.setOnClickListener {
            imgFlash.tag = if (imgFlash.tag != null && imgFlash.tag is Boolean) {
                !(imgFlash.tag as Boolean)
            } else {
                false
            }
            mPicker?.switchTorchOn((imgFlash.tag as Boolean))
        }

        viewHelp.setOnClickListener {
            showHelper()
        }
    }

    private fun showHelper() {
        if (viewHelp.visibility == View.VISIBLE) {
            imgFlash.setImageResource(R.drawable.ic_flash_off_24px)
            viewHelp.visibility = View.GONE
            tvHelp1.visibility = View.GONE
            tvHelp2.visibility = View.GONE
            tvHelp3.visibility = View.GONE
        } else {
            imgFlash.setImageResource(R.drawable.ic_flash_on_24px)
            viewHelp.visibility = View.VISIBLE
            tvHelp1.visibility = View.VISIBLE
            tvHelp2.visibility = View.VISIBLE
            tvHelp3.visibility = View.VISIBLE
        }
    }

    override fun didScan(scanSession: ScanSession?) {
        if (scanSession != null && !scanSession.allRecognizedCodes.isNullOrEmpty()) {
            scanSession.allRecognizedCodes[scanSession.allRecognizedCodes.lastIndex].data?.let { barcode ->
                mPicker?.pauseScanning()
                viewModel.scan(barcode)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        isStart = false
        mPicker?.stopScanning()
    }

    override fun onResume() {
        super.onResume()

        if (PermissionHelper.isAllowPermission(this, Manifest.permission.CAMERA)) {
            if (!isStart) {
                isStart = true
                mPicker?.startScanning()
            }
        }
    }

    override fun onDestroy() {
        isStart = false
        mPicker?.stopScanning()
        super.onDestroy()
    }
}