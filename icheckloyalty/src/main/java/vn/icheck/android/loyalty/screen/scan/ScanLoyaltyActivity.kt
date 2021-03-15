package vn.icheck.android.loyalty.screen.scan

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Handler
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.scandit.barcodepicker.*
import com.scandit.recognition.Barcode
import kotlinx.android.synthetic.main.fragment_barcode_scan.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.helper.PermissionHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKNone
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity

class ScanLoyaltyActivity : BaseActivityGame(), OnScanListener {

    private val viewModel by viewModels<ScanLoyaltyViewModel>()

    private var mPicker: BarcodePicker? = null

    private val requestCamera = 1

    private var isStart = false
    private var showGuide = false
    private var isFlash = false

    /**
     * @param type == 0 -> Scan tích điểm đổi quà
     * @param type == 1 -> Scan tích điểm dài hạn
     */
    var type = 0

    var name = ""

    override val getLayoutID: Int
        get() = R.layout.fragment_barcode_scan

    override fun onInitView() {
        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)
        type = intent.getIntExtra(ConstantsLoyalty.DATA_2, 0)
        name = intent.getStringExtra(ConstantsLoyalty.DATA_3) ?: "Chương trình của doanh nghiệp"

        Handler().postDelayed({
            initScandit()
        }, 300)
    }

    private fun initScandit() {
        if (mPicker != null)
            root.removeView(mPicker!!)

        ScanditLicense.setAppKey(getString(R.string.scandit_license_key))

        mPicker = BarcodePicker(this, pickerSetting)

        val layout = LayoutInflater.from(this).inflate(R.layout.custom_scan_loyalty, root, false)
        mPicker?.overlayView?.removeAllViews()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mPicker?.overlay?.clear()
        }

        mPicker?.setOnScanListener(this)
        mPicker?.overlayView?.setVibrateEnabled(false)
        mPicker?.overlayView?.addView(layout)
        mPicker?.overlayView?.bringChildToFront(layout)
        mPicker?.overlayView?.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
        root.addView(mPicker)

        initListener()

        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (PermissionHelper.checkPermission(this, permissions, requestCamera)) {
            isStart = true
            mPicker!!.startScanning()
        }
    }

    private fun initListener() {
        isFlash = false

        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm.networkCountryIso != "VN") {
            mPicker?.findViewById<View>(R.id.logo_scandit)?.setInvisible()
        }

        mPicker?.findViewById<TextView>(R.id.textView19)?.setInvisible()

        mPicker?.findViewById<ImageView>(R.id.img_guide)?.run {
            setOnClickListener {
                showGuide = !showGuide
                showGuideUi()
            }
        }
        mPicker?.findViewById<ImageView>(R.id.img_flash)?.run {
            setOnClickListener {
                isFlash = !isFlash
                if (isFlash) {
                    setImageResource(R.drawable.ic_flash_on_36px)
                } else {
                    setImageResource(R.drawable.ic_flash_off_36px)
                }
                mPicker?.switchTorchOn(!isFlash)
            }
        }

        mPicker?.findViewById<ImageView>(R.id.img_close)?.setOnClickListener {
            onBackPressed()
        }

        viewModel.onErrorString.observe(this, Observer {
            mPicker?.startScanning()
            showLongError(it)
        })

        viewModel.onAccumulatePoint.observe(this, Observer {
            mPicker?.stopScanning()

            Handler().postDelayed({
                if (type == 1) {
                    DialogHelperGame.dialogAccumulatePointSuccess(this@ScanLoyaltyActivity,
                            it?.point,
                            it?.statistic?.owner?.logo?.medium,
                            it?.statistic?.owner?.name, it?.statistic?.business_owner_id ?: it?.statistic?.owner?.id,
                            this@ScanLoyaltyActivity.name,
                            R.drawable.bg_gradient_button_blue, it?.statistic?.point_loyalty?.point_name,
                            object : IClickButtonDialog<Long> {
                                override fun onClickButtonData(obj: Long?) {
                                    this@ScanLoyaltyActivity.startActivity<GiftShopActivity, Long>(ConstantsLoyalty.ID, obj ?: -1)
                                }
                            }, object : IDismissDialog {
                        override fun onDismiss() {
                            mPicker?.startScanning()
                        }
                    }, "Đổi quà bằng điểm tích lũy ngay để\nnhận những phần quà cực hấp dẫn!")
                } else {
                    DialogHelperGame.dialogAccumulatePointSuccess(this@ScanLoyaltyActivity,
                            it?.point,
                            it?.statistic?.owner?.logo?.medium,
                            it?.statistic?.owner?.name, viewModel.collectionID,
                            "NameCampaign",
                            R.drawable.bg_gradient_button_orange_yellow, null,
                            object : IClickButtonDialog<Long> {
                                override fun onClickButtonData(obj: Long?) {
                                    onBackPressed()
                                }
                            }, object : IDismissDialog {
                        override fun onDismiss() {
                            mPicker?.startScanning()
                        }
                    })
                }
            }, 300)
        })

        viewModel.onInvalidTarget.observe(this, Observer {
            mPicker?.stopScanning()

            if (type == 1) {
                DialogHelperGame.dialogScanLoyaltyError(this@ScanLoyaltyActivity,
                        R.drawable.ic_error_scan_game_1, it,
                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                        null, "Quét tiếp", false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                        object : IClickButtonDialog<ICKNone> {
                            override fun onClickButtonData(obj: ICKNone?) {
                                mPicker?.startScanning()
                            }
                        }, object : IDismissDialog {
                    override fun onDismiss() {
                        mPicker?.startScanning()
                    }
                })
            } else {
                DialogHelperGame.dialogScanLoyaltyError(this@ScanLoyaltyActivity,
                        R.drawable.ic_error_scan_game_1, it,
                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                        null, "Quét tiếp", false, R.drawable.bg_gradient_button_orange_yellow, R.color.white,
                        object : IClickButtonDialog<ICKNone> {
                            override fun onClickButtonData(obj: ICKNone?) {
                                mPicker?.startScanning()
                            }
                        }, object : IDismissDialog {
                    override fun onDismiss() {
                        mPicker?.startScanning()
                    }
                })
            }
        })

        viewModel.onUsedTarget.observe(this, Observer {
            mPicker?.stopScanning()

            if (type == 1) {
                DialogHelperGame.dialogScanLoyaltyError(this@ScanLoyaltyActivity,
                        R.drawable.ic_error_scan_game, it,
                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                        null, "Quét tiếp", false, R.drawable.bg_button_not_enough_point_blue, R.color.blueVip,
                        object : IClickButtonDialog<ICKNone> {
                            override fun onClickButtonData(obj: ICKNone?) {
                                mPicker?.startScanning()
                            }
                        }, object : IDismissDialog {
                    override fun onDismiss() {
                        mPicker?.startScanning()
                    }
                })
            } else {
                DialogHelperGame.dialogScanLoyaltyError(this@ScanLoyaltyActivity,
                        R.drawable.ic_error_scan_game, it,
                        "Thử quét với những mã QRcode khác\nđể cộng điểm tích lũy nhé!",
                        null, "Quét tiếp", false, R.drawable.bg_gradient_button_orange_yellow, R.color.white,
                        object : IClickButtonDialog<ICKNone> {
                            override fun onClickButtonData(obj: ICKNone?) {
                                mPicker?.startScanning()
                            }
                        }, object : IDismissDialog {
                    override fun onDismiss() {
                        mPicker?.startScanning()
                    }
                })
            }
        })

        viewModel.onCustomer.observe(this, Observer {
            mPicker?.stopScanning()

            if (type == 1) {
                DialogHelperGame.dialogCustomerError(this@ScanLoyaltyActivity, R.drawable.ic_error_scan_game, it, "Liên hệ với ${SharedLoyaltyHelper(this@ScanLoyaltyActivity).getString(ConstantsLoyalty.OWNER_NAME)} để biết thêm chi tiết", object : IClickButtonDialog<ICKNone> {
                    override fun onClickButtonData(obj: ICKNone?) {
                        this@ScanLoyaltyActivity.onBackPressed()
                    }
                })
            }
        })
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

    private fun showGuideUi() {
        val textView24 = mPicker?.findViewById<TextView>(R.id.textView24)
        val imageView16 = mPicker?.findViewById<ImageView>(R.id.imageView16)
        val imageView33 = mPicker?.findViewById<ImageView>(R.id.imageView33)
        val textView30 = mPicker?.findViewById<TextView>(R.id.textView30)

        val viewOpa = mPicker?.findViewById<View>(R.id.viewOpa)
        if (!showGuide) {
            textView24?.setInvisible()
            imageView16?.setInvisible()
            imageView33?.setInvisible()
            textView30?.setInvisible()
            viewOpa?.setGone()
            mPicker?.startScanning()
        } else {
            mPicker?.stopScanning()
            textView24?.setVisible()
            imageView16?.setVisible()
            imageView33?.setVisible()
            textView30?.setVisible()
            viewOpa?.run {
                setVisible()

                setOnClickListener {
                    showGuide = false
                    textView24?.setInvisible()
                    imageView16?.setInvisible()
                    imageView33?.setInvisible()
                    textView30?.setInvisible()
                    setGone()
                    mPicker?.startScanning()
                }
            }
        }
    }

    override fun didScan(p0: ScanSession?) {
        p0?.stopScanning()
        try {
            var code = p0!!.allRecognizedCodes?.get(p0.allRecognizedCodes.lastIndex)!!.data.trim()
            if (code.matches("^[0-9]*$".toRegex()) && code.length == 12) {
                code = "0$code"
            }

            if (!code.contains(" ")) {
                code = code.replace("\n".toRegex(), "")
            }
            checkCode(code)
        } catch (e: Exception) {
            mPicker?.startScanning()
            showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    private fun checkCode(code: String) {
        val nc: String = when {
            code.contains("https://qcheck-dev.vn/") -> {
                code.replace("https://qcheck-dev.vn/", "")
            }
            code.contains("http://dev.qcheck.vn/") -> {
                code.replace("http://dev.qcheck.vn/", "")
            }
            code.contains("https://dev.qcheck.vn/") -> {
                code.replace("https://dev.qcheck.vn/", "")
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
            showLongError("Đây không phải là QR code vui lòng quét lại!")
            mPicker?.startScanning()
        } else {
            viewModel.postAccumulatePoint(nc)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionHelper.checkResult(grantResults)) {
            if (!isStart) {
                isStart = true
                mPicker?.startScanning()
            }
        } else {
            object : DialogNotification(this@ScanLoyaltyActivity, null, "Vui lòng vào phần cài đặt và cho phép ứng dụng sử dụng camera", "OK", false) {
                override fun onDone() {
                    onBackPressed()
                }
            }.show()
        }
    }

    override fun onDestroy() {
        isStart = false
        mPicker?.stopScanning()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()

        isStart = false
        mPicker?.stopScanning()
    }

    override fun onResume() {
        super.onResume()

        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (PermissionHelper.isAllowPermission(this, permissions)) {
            if (!isStart) {
                isStart = true
                mPicker?.startScanning()
            }
        }
    }
}