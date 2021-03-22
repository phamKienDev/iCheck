package vn.icheck.android.screen.user.scanbuy

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.scandit.barcodepicker.*
import com.scandit.recognition.Barcode
import kotlinx.android.synthetic.main.activity_scan_buy.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.IScanBuyPopupListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.fragments.InputBarcodeBottomDialog
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICShopVariant
import vn.icheck.android.screen.user.cart.CartActivity
import vn.icheck.android.screen.user.scanbuy.adapter.ScanBuyAdapter
import vn.icheck.android.screen.user.scanbuy.presenter.ScanBuyPresenter
import vn.icheck.android.screen.user.scanbuy.view.IScanBuyView
import vn.icheck.android.ui.common.OffsetItemDecoration
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 12/3/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ScanBuyActivity : BaseActivity<ScanBuyPresenter>(), OnScanListener, IScanBuyView, View.OnClickListener {
    private var mPicker: BarcodePicker? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val adapter = ScanBuyAdapter(this)
    private val layoutManager = LinearSnapHelper()

    private var addToCartID: Long = 0
    private var addToCartCount: Int = 0

    private val requestCamera = 1
    private val requestAddCart = 2
    private val requestCart = 3

    private var isStart = false

    override val getLayoutID: Int
        get() = R.layout.activity_scan_buy

    override val getPresenter: ScanBuyPresenter
        get() = ScanBuyPresenter(this)

    override fun onInitView() {
        Handler().postDelayed({
            initScandit()
        }, 300)

        setupRecyclerView()
        setupLocationClient()
        setupBottomSheet()
        initListener()

        presenter.getListCartOffline()
        presenter.updateCountCart()
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

        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PermissionHelper.checkPermission(this, permissions, requestCamera)) {
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

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(OffsetItemDecoration(this))
        layoutManager.attachToRecyclerView(recyclerView)
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(view: View, newState: Int) {
                when (sheetBehavior.state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (adapter.getListData.isNotEmpty())
                            viewBackground.visibility = View.VISIBLE

                        txtInfo?.setText(R.string.vuot_xuong_de_dong)
                        txtInfo?.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_swipe_bottom_white, 0, 0)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (adapter.getListData.isNotEmpty())
                            viewBackground.visibility = View.VISIBLE

                        txtInfo?.setText(R.string.vuot_len_de_xem_chi_tiet)
                        txtInfo?.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_swipe_top_white, 0, 0)
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        viewBackground.visibility = View.GONE
                    }
                }
            }
        })

        txtInfo.setOnClickListener {
            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun checkGps() {
        if (!NetworkHelper.isOpenedGPS(this)) {
            DialogHelper.showConfirm(this@ScanBuyActivity, R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban, true, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    NetworkHelper.openSettingGPS(this@ScanBuyActivity)
                }
            })
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, imgClose, imgClose2, imgAnswer, tvInputByHand, tvFlash, tvViewCart)
    }

    private fun findShop(barcode: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    mPicker?.resumeScanning()

                    if (location != null) {
                        presenter.findShop(barcode, location.latitude, location.longitude)
                    } else {
                        showError(getString(R.string.khong_lay_duoc_vi_tri_cua_ban_vui_long_kiem_tra_lai_thiet_bi))
                    }
                }
                .addOnFailureListener {
                    mPicker?.resumeScanning()
                }
                .addOnCanceledListener {
                    mPicker?.resumeScanning()
                }
    }

    private fun startShowError(textView: AppCompatTextView) {
        layoutError.addView(textView)
        textView.post {
            WidgetUtils.changeViewHeight(textView, 0, textView.height, 300, object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    Handler().postDelayed({
                        endShowError(textView)
                    }, 2500)
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {
                    textView.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun endShowError(textView: AppCompatTextView?) {
        textView?.let {
            WidgetUtils.changeViewHeight(it, it.height, 0, 300, object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    layoutError?.removeView(it)
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }
            })
        }
    }

    override fun didScan(scanSession: ScanSession?) {
        if (scanSession != null && !scanSession.allRecognizedCodes.isNullOrEmpty()) {
            val data = scanSession.allRecognizedCodes[scanSession.allRecognizedCodes.lastIndex].data

            if (data != null) {
                findShop(data)
                mPicker?.pauseScanning()
            }
        }
    }

    override fun onUpdateListCart(list: MutableList<ICItemCart>, totalPrice: String, totalItem: String?) {
        adapter.setListCart(list)

        tvTotal?.text = getString(R.string.tam_tinh_xxx__d, totalPrice)
        tvCount?.run {
            visibility = if (totalItem != null) View.VISIBLE else View.GONE
            text = totalItem
        }
    }

    override fun onGetShopSuccess(obj: ICShopVariant) {
        sheetBehavior.isHideable = true
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        imgClose.visibility = View.GONE
        imgClose2.visibility = View.VISIBLE
        imgAnswer.visibility = View.VISIBLE

        val layoutParams = imgIcon.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(0, tvInputByHand.height, 0, 0)
        imgIcon.layoutParams = layoutParams

        adapter.addItem(obj)
        recyclerView.smoothScrollToPosition(0)
    }

    override fun onAddToCart(obj: ICShopVariant, count: Int) {
        if (SessionManager.isUserLogged) {
            presenter.addToCart(obj.id, count)
        } else {
            addToCartID = obj.id
            addToCartCount = count
            onRequireLogin(requestAddCart)
        }
    }

    override fun onAddToCartSuccess(id: Long) {
        adapter.removeItem(id)
    }

    override fun onClickDetailProduct(obj: ICShopVariant) {
        if (obj.product_id != null && obj.product_id != 0L) {
//            ShopProductDetailActivity.startShop = false
//            startActivity<ShopProductDetailActivity, Long>(Constant.DATA_1, obj.product_id!!)
        } else {
            showLongError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onClickShopDetail(obj: ICShopVariant) {
        if (obj.shop_id != null && obj.shop_id != 0L) {
        } else {
            showLongError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onUpdateView() {
        Handler().postDelayed({
            if (adapter.getListData.isEmpty()) {
                sheetBehavior.isHideable = false
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                imgClose.visibility = View.VISIBLE
                imgClose2.visibility = View.GONE
                imgAnswer.visibility = View.GONE
                viewBackground.visibility = View.GONE
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                sheetBehavior.isHideable = true
                imgClose.visibility = View.GONE
                imgClose2.visibility = View.VISIBLE
                imgAnswer.visibility = View.VISIBLE
                viewBackground.visibility = View.VISIBLE
            }
        }, 200)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        val textView = AppCompatTextView(this)
        textView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        textView.setPadding(SizeHelper.size16, SizeHelper.size16, SizeHelper.size52, SizeHelper.size16)
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_white_24dp, 0, 0, 0)
        textView.compoundDrawablePadding = SizeHelper.size12
        textView.gravity = Gravity.CENTER
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.warning_scan_buy))
        textView.text = errorMessage
        textView.setTextColor(Color.WHITE)
        textView.visibility = View.INVISIBLE

        textView.setOnClickListener {
            DialogHelper.showPopupScanBuy(this, false, object : IScanBuyPopupListener {
                override fun onClose() {
                    DialogHelper.closeLoading(this@ScanBuyActivity)
                }

                override fun onClickSeller() {
                }

                override fun onClickChat() {
//                    ChatV2Activity.createChatIcheck( this@ScanBuyActivity)
                }
            })
        }

        startShowError(textView)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgClose -> {
                onBackPressed()
            }
            R.id.imgClose2 -> {
                onBackPressed()
            }
            R.id.imgAnswer -> {
                sheetBehavior.isHideable = true
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            R.id.tvInputByHand -> {
                mPicker?.pauseScanning()
                tvInputByHand.isClickable = false

                Handler().postDelayed({
                    object : InputBarcodeBottomDialog(this@ScanBuyActivity) {
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
            R.id.tvFlash -> {
                tvFlash.isChecked = !tvFlash.isChecked
                mPicker?.switchTorchOn(tvFlash.isChecked)
            }
            R.id.tvViewCart -> {
                if (SessionManager.isUserLogged) {
                    tvViewCart.isClickable = false

                    startActivity<CartActivity>()

                    Handler().postDelayed({
                        tvViewCart?.isClickable = true
                    }, 200)
                } else {
                    onRequireLogin(requestCart)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        isStart = false
        mPicker?.stopScanning()
        presenter.disposeApi()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()

        isStart = false
        mPicker?.stopScanning()
    }

    override fun onResume() {
        super.onResume()

        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PermissionHelper.isAllowPermission(this, permissions)) {
            if (!isStart) {
                isStart = true
                mPicker?.startScanning()
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
            requestAddCart -> {
                presenter.addToCart(addToCartID, addToCartCount)
            }
            requestCart -> {
                startActivity<CartActivity>()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionHelper.checkResult(grantResults)) {
            if (!isStart) {
                isStart = true
                mPicker?.startScanning()
            }

            checkGps()
        } else {
            DialogHelper.showNotification(this@ScanBuyActivity, R.string.vui_long_vao_phan_cai_dat_va_cho_phep_ung_dung_su_dung_camera_va_vi_tri_cua_thiet_bi, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        }
    }
}