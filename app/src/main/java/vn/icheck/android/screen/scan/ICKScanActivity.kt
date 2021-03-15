package vn.icheck.android.screen.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.scandit.barcodepicker.BarcodePicker
import com.scandit.barcodepicker.ScanSettings
import com.scandit.barcodepicker.ScanditLicense
import com.scandit.recognition.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.screen.scan.fragment.ICKScanFragment
import vn.icheck.android.screen.scan.viewmodel.ICKScanViewModel
import vn.icheck.android.screen.user.barcode_qr_of_me.QrAndBarcodeOfMeFragment
import vn.icheck.android.screen.user.scanbuyv2.ScanBuyFragmentSocial
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.toPx
import vn.icheck.android.util.kotlin.HideWebUtils

/**
 * Happy new year
 * 00:00 ngày 01/01/2021
 * Chúc mừng năm mới anh Sơn nha. Chị Bình chúc anh năm mới fix bug không bị reopen :v
 */

@AndroidEntryPoint
class ICKScanActivity : BaseActivityMVVM() {
    private val ickScanViewModel: ICKScanViewModel by viewModels()

    companion object {
        fun create(context: Context, tab: Int = -1) {
            val i = Intent(context, ICKScanActivity::class.java)
            i.putExtra(Constant.DATA_1, tab)
            context.startActivity(i)
        }

        fun scanOnly(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, ICKScanActivity::class.java)
            i.putExtra("scan_only", true)
            context.startActivityForResult(i, requestCode)
        }

        fun scanOnlyChat(context: FragmentActivity, requestCode: Int) {
            val i = Intent(context, ICKScanActivity::class.java)
            i.putExtra("scan_only_chat", true)
            context.startActivityForResult(i, requestCode)
        }

        fun reviewOnly(context: FragmentActivity) {
            val i = Intent(context, ICKScanActivity::class.java)
            i.putExtra("review_only", true)
            context.startActivityForResult(i, 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ick_scan)
        initScandit()
        HideWebUtils.showWeb("Scan")
        ickScanViewModel.scanOnlyChat = intent.getBooleanExtra("scan_only_chat", false)
        ickScanViewModel.scanOnly = intent.getBooleanExtra("scan_only", false)
        ickScanViewModel.reviewOnly = intent.getBooleanExtra("review_only", false)
        selectTab()
        ickScanViewModel.mScreen.observe(this, Observer {
            lifecycleScope.launch {
                delay(300)
                when (it) {
                    ICKScanViewModel.ScanScreen.SCAN_BUY -> {
                        createScanBuyScreen()
                        ickScanViewModel.mPicker?.startScanning()
                    }
                    ICKScanViewModel.ScanScreen.MY_CODE -> {
                        createMyCodeScreen()
                        ickScanViewModel.mPicker?.stopScanning()
                    }
                    ICKScanViewModel.ScanScreen.SCAN -> {
                        createScanScreen()
                        ickScanViewModel.mPicker?.startScanning()
                    }
                    else -> {
                        createScanScreen()
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
    }

    private fun selectTab() {
        when (intent.getIntExtra(Constant.DATA_1, -1)) {
            2 -> {
                createScanBuyScreen()
            }
            3 -> {
                createMyCodeScreen()
            }
            else -> {
                TrackingAllHelper.trackScanClicked()
                createScanScreen()
            }
        }
    }

    private fun createScanBuyScreen() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.root, ScanBuyFragmentSocial())
                .commitAllowingStateLoss()
    }

    private fun createMyCodeScreen() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.root, QrAndBarcodeOfMeFragment())
                .commitAllowingStateLoss()
    }

    private fun createScanScreen() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.root, ICKScanFragment())
                .commitAllowingStateLoss()
    }

    private fun initScandit() {
        try {
            val settings = ScanSettings.create()
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            Barcode.ALL_SYMBOLOGIES.forEach {
                settings.setSymbologyEnabled(it, true)
                settings.getSymbologySettings(it).isColorInvertedEnabled = true
            }
            val width = ((displayMetrics.widthPixels - 294.toPx()) / 2).toFloat()
            val height = ((displayMetrics.heightPixels - 221.toPx()) / 2).toFloat()
            val rectF = RectF(width, height, 294.toPx().toFloat(), 221.toPx().toFloat())
            settings.setActiveScanningArea(0, rectF)
            settings.isRestrictedAreaScanningEnabled = true
            ickScanViewModel.mPicker = BarcodePicker(this, settings)
        } catch (e: Exception) {
            val settings = ScanSettings.create()
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            Barcode.ALL_SYMBOLOGIES.forEach {
                settings.setSymbologyEnabled(it, true)
                settings.getSymbologySettings(it).isColorInvertedEnabled = true
            }
            ickScanViewModel.mPicker = BarcodePicker(this, settings)
            logError(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ickScanViewModel.mPicker?.stopScanning()
    }

    override fun onPause() {
        super.onPause()
        ickScanViewModel.mPicker?.post {
            ickScanViewModel.mPicker?.stopScanning()
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
            ickScanViewModel.mPicker?.post {
                ickScanViewModel.mPicker?.startScanning()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ICK_REQUEST_CAMERA) {
            if (PermissionHelper.checkResult(grantResults)) {
                initScandit()
                ickScanViewModel.mPicker?.startScanning()
            } else {
                finish()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        request()
    }

}