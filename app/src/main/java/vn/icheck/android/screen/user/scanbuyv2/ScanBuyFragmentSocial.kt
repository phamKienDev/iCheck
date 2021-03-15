package vn.icheck.android.screen.user.scanbuyv2

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.scandit.barcodepicker.*
import com.scandit.recognition.Barcode
import kotlinx.android.synthetic.main.fragment_scan_buy_social.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.fragments.InputBarcodeBottomDialog
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICProductVariant
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.util.kotlin.WidgetUtils
import kotlin.random.Random

class ScanBuyFragmentSocial : BaseFragmentMVVM(), OnScanListener, View.OnClickListener {
    private var mPicker: BarcodePicker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val adapter = ScanBuyAdapterV2()
    private val layoutManager = LinearSnapHelper()

    override val getLayoutID: Int
        get() = R.layout.fragment_scan_buy_social

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            initScandit()
        }, 300)

        TekoHelper.tagScanAndBuyClicked()
        ICheckApplication.currentActivity()?.let { act ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(act)
        }

        setUpRecyclerView()
        initListener()
    }

    private fun initScandit() {
        ScanditLicense.setAppKey(APIConstants.scanditLicenseKey())

        mPicker = BarcodePicker(context, pickerSetting)
        mPicker!!.setOnScanListener(this)
        mPicker!!.overlayView.removeAllViews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mPicker!!.overlay.clear()
        }

        mPicker!!.overlayView.setGuiStyle(ScanOverlay.GUI_STYLE_NONE)
        layoutContainer.addView(mPicker, 0)

        mPicker!!.startScanning()
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

    private fun setUpRecyclerView() {
        recyclerView.adapter = adapter
//        recyclerView.addItemDecoration(OffsetItemDecoration(context))
        layoutManager.attachToRecyclerView(recyclerView)
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, imgClose, tvInputByHand, tvFlash)
    }

    override fun didScan(p0: ScanSession?) {
        if (p0 != null && !p0.allRecognizedCodes.isNullOrEmpty()) {
            val data = p0.allRecognizedCodes.get(p0.allRecognizedCodes.lastIndex).data

            if (data != null) {
                findShop(data)
                mPicker?.pauseScanning()
            }
        }
    }

    private fun findShop(barcode: String) {
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mPicker?.resumeScanning()

                    if (location != null) {
                        val a = ICProductVariant()
                        a.id = Random.nextLong(0,100)
                        a.image = "https://znews-photo.zadn.vn/w1024/Uploaded/kbd_bcvi/2019_11_23/5d828d976f24eb1a752053b5.jpg"
                        a.name = "Test"
                        a.variant = "XXL"
                        a.sale_off = true
                        a.price = 200
                        a.special_price = 100
                        a.quantity = 10
                        a.name_shop = "ádasdasds"
                        a.image_shop = "https://kynguyenlamdep.com/wp-content/uploads/2020/01/hinh-anh-dep-hoa-bo-cong-anh.jpg"
                        adapter.addItem(a)
                        recyclerView.smoothScrollToPosition(0)
                    } else {

                    }
                }
                .addOnFailureListener {
                    mPicker?.resumeScanning()
                }
                .addOnCanceledListener {
                    mPicker?.resumeScanning()
                }
    }

    override fun onDestroy() {
        mPicker?.stopScanning()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgClose -> {
                activity?.onBackPressed()
            }
            R.id.tvInputByHand -> {
                mPicker?.pauseScanning()
                tvInputByHand.isClickable = false

                context?.let {
                    Handler().postDelayed({
                        object : InputBarcodeBottomDialog(it) {
                            override fun onDone(barcode: String) {
                                findShop(barcode)
                                tvInputByHand?.isClickable = true
                            }

                            override fun onDismiss() {
                                mPicker?.resumeScanning()
                                tvInputByHand?.isClickable = true
                            }
                        }.show()
                    }, 200)
                }
            }
            R.id.tvFlash -> {
                tvFlash.isChecked = !tvFlash.isChecked
                mPicker?.switchTorchOn(tvFlash.isChecked)
            }
        }
    }
}