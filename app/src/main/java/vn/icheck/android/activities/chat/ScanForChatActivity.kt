package vn.icheck.android.activities.chat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.scandit.barcodepicker.*
import com.scandit.recognition.Barcode
import kotlinx.android.synthetic.main.custom_scandit_view.*
import kotlinx.android.synthetic.main.fragment_barcode_scan.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.fragments.BarcodeBottomDialog
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.kotlin.ToastUtils
import java.lang.Exception

class ScanForChatActivity : AppCompatActivity(), OnScanListener {
    private lateinit var mPicker: BarcodePicker
    private var showGuide = false
    private var isFlash = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.fragment_barcode_scan, root, false)
        initScandit()
        val rootVg = view.findViewById<FrameLayout>(R.id.root)
        val layout = inflater.inflate(R.layout.custom_scandit_view, rootVg, false)

        mPicker.overlayView.removeAllViews()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mPicker.overlay.clear()
        }
        mPicker.overlayView.addView(layout)
        mPicker.overlayView.bringChildToFront(layout)
        mPicker.overlayView.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
        rootVg.addView(mPicker)
        setContentView(view)
        if (!PermissionHelper.isAllowPermission(this, Manifest.permission.CAMERA)) {
            PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, 1)
        } else {
            lifecycleScope.launch {
                delay(200)
            }
            mPicker.startScanning()
        }
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm.networkCountryIso != "VN") {
            logo_scandit.visibility = View.INVISIBLE
        }
        img_guide.setOnClickListener {
            showGuide = !showGuide
            showGuideUi()
        }
        img_flash.setOnClickListener {
            isFlash = !isFlash
            if (isFlash) {
                img_flash.setImageResource(R.drawable.ic_flash_on_36px)
            } else {
                img_flash.setImageResource(R.drawable.ic_flash_off_36px)
            }
            mPicker.switchTorchOn(!isFlash)
        }

        img_group_barcode.visibility = View.INVISIBLE
        imageView15.visibility = View.INVISIBLE
        textView21.visibility = View.INVISIBLE

        img_group_barcode.setOnClickListener {
            runOnUiThread {
                mPicker.stopScanning()
                img_group_barcode.isEnabled = false

                BarcodeBottomDialog.show(supportFragmentManager, false, object : BarcodeBottomDialog.OnBarCodeDismiss {
                    override fun onDismiss() {
                        mPicker.startScanning()
                        img_group_barcode.isEnabled = true
                    }

                    override fun onSubmit(code: String) {
                        if (code.isEmpty()) {
                            Toast.makeText(this@ScanForChatActivity, "Barcode không hợp lệ", Toast.LENGTH_SHORT).show()
                            mPicker.startScanning()
                        } else {
                            val i = Intent()
                            i.putExtra("barcode", code)
                            setResult(Activity.RESULT_OK, i)
                            finish()
                        }
                        img_group_barcode.isEnabled = true
                    }
                })
            }
        }
    }

    private fun showGuideUi() {
        if (!showGuide) {
//            imageView15.visibility = View.INVISIBLE
            textView21.visibility = View.INVISIBLE
            textView24.visibility = View.INVISIBLE
            imageView16.visibility = View.INVISIBLE
            imageView33.visibility = View.INVISIBLE
            textView30.visibility = View.INVISIBLE
            viewOpa.visibility = View.GONE
            mPicker.startScanning()
        } else {
            mPicker.stopScanning()
//            imageView15.visibility = View.VISIBLE
//            textView21.visibility = View.VISIBLE
            textView24.visibility = View.VISIBLE
            imageView16.visibility = View.VISIBLE
            imageView33.visibility = View.VISIBLE
            textView30.visibility = View.VISIBLE
            viewOpa.visibility = View.VISIBLE

            viewOpa.setOnClickListener {
                showGuide = false
//                imageView15.visibility = View.INVISIBLE
                textView21.visibility = View.INVISIBLE
                textView24.visibility = View.INVISIBLE
                imageView16.visibility = View.INVISIBLE
                imageView33.visibility = View.INVISIBLE
                textView30.visibility = View.INVISIBLE
                viewOpa.visibility = View.GONE
                mPicker.startScanning()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (PermissionHelper.checkResult(grantResults)) {
                mPicker.startScanning()
            } else {
                finish()
            }
        }
    }

    private fun initScandit() {
        ScanditLicense.setAppKey(APIConstants.scanditLicenseKey())
        val settings = ScanSettings.create()
        Barcode.ALL_SYMBOLOGIES.forEach {
            settings.setSymbologyEnabled(it, true)
        }
        mPicker = BarcodePicker(this, settings)
        mPicker.setOnScanListener(this)
        mPicker.stopScanning()

        mPicker?.overlayView?.setVibrateEnabled(false)
    }

    override fun didScan(p0: ScanSession?) {
        var vibrate = SettingManager.getVibrateSetting

        if (vibrate) {
            mPicker?.overlayView?.setVibrateEnabled(true)
        } else {
            mPicker?.overlayView?.setVibrateEnabled(false)
        }

        p0?.pauseScanning()

        when (p0?.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)?.symbology) {
            Barcode.SYMBOLOGY_QR, Barcode.SYMBOLOGY_DATA_MATRIX, Barcode.SYMBOLOGY_MICRO_PDF417, Barcode.SYMBOLOGY_MICRO_QR -> {
                val dataStr = p0.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)!!.data
                val i = Intent()
                i.putExtra("qrcode", dataStr)
                setResult(Activity.RESULT_OK, i)
                finish()
            }

            else -> {
                val code = p0?.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)?.data
                if (!code.isNullOrEmpty()) {
                    val i = Intent()
                    i.putExtra("barcode", code)
                    setResult(Activity.RESULT_OK, i)
                    finish()
                } else {
                    val i = Intent()
                    setResult(Activity.RESULT_CANCELED, i)
                    finish()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mPicker.setOnScanListener(null)
    }
}
