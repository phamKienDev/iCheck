package vn.icheck.android.screen.user.createqrcode.createlocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_create_location_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICPointDetail
import vn.icheck.android.network.models.ICPoints
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.user.createqrcode.createlocation.adapter.SearchLocationAdapter
import vn.icheck.android.screen.user.createqrcode.createlocation.presenter.CreateLocationQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createlocation.view.ICreateLocationQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.util.KeyboardUtils
import java.util.concurrent.TimeUnit

class CreateLocationQrCodeFragment : BaseFragmentMVVM(), OnMapReadyCallback, ICreateLocationQrCodeView {
    private var mMap: GoogleMap? = null

    private val presenter = CreateLocationQrCodePresenter(this@CreateLocationQrCodeFragment)

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null

    private var adapter = SearchLocationAdapter(this)

    private val requestAddNew = 1

    private var disposable: Disposable? = null
    private var isSetLocation = false
    private val requestPermission = 1

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_location_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        initToolbar()
        setupView()
        setupGoogleMap()
        checkPermission()

        setupListLocation()
        setupSearchInput()
        initListener()
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_vi_tri)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        edtSearch.background=ViewHelper.bgWhiteCorners4(requireContext())
        edtSearch.setTextColor(vn.icheck.android.ichecklibs.Constant.getNormalTextColor(requireContext()))

        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
    }

    private fun setupGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkPermission() {
        PermissionDialog.checkPermission(context, PermissionDialog.LOCATION, object : PermissionDialog.PermissionListener {
            override fun onPermissionAllowed() {
                checkGps()
                startLocationUpdates()
            }

            override fun onRequestPermission() {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), requestPermission)
            }

            override fun onPermissionNotAllow() {

            }
        })
    }

    private fun checkGps(): Boolean {
        return if (!NetworkHelper.isOpenedGPS(requireContext())) {
            DialogHelper.showConfirm(context, R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban, true, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    NetworkHelper.openSettingGPS(requireActivity())
                }
            })

            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (PermissionHelper.isAllowPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap?.isMyLocationEnabled = true

            setupLocationUpdateSetting()
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }

    private fun setupLocationUpdateSetting() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        mLocationRequest?.interval = (10 * 1000)

        mLocationRequest?.let {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(it)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(requireContext())
            settingsClient.checkLocationSettings(locationSettingsRequest)
        }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (isSetLocation)
                    return

                val location = locationResult?.lastLocation
                location?.let {
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
                    isSetLocation = true
                }
            }
        }
    }

    private fun setupListLocation() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        viewBackground.setOnClickListener {
            adapter.clearData()
            viewBackground.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }
    }

    private fun setupSearchInput() {
        disposable = RxTextView.afterTextChangeEvents(edtSearch)
                .skipInitialValue()
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (it.view().text.toString().isNotEmpty()) {
                            edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_clear_gray_17dp, 0)
                        } else {
                            edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search_gray_17, 0)
                        }
                    }

                    presenter.searchLocation(it.view().text.toString().trim())
                }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        val drawableEnd = 2

        edtSearch.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (edtSearch.right - edtSearch.compoundDrawables[drawableEnd].bounds.width())) {
                    if (!edtSearch.text.isNullOrEmpty()) {
                        edtSearch.text = null
                    }
                    return@setOnTouchListener true
                }
            }

            return@setOnTouchListener false
        }

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        imgLocation.setOnClickListener {
            if (PermissionHelper.isAllowPermission(context, permissions)) {
                if (checkGps()) {
                    mFusedLocationClient?.lastLocation?.addOnSuccessListener {
                        if (it != null) {
                            mMap?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                        } else {
                            showError(getString(R.string.khong_lay_duoc_vi_tri_cua_ban_vui_long_thu_lai))
                        }
                    }?.addOnFailureListener {
                        showError(getString(R.string.khong_lay_duoc_vi_tri_cua_ban_vui_long_thu_lai))
                    }
                }
            }
        }

        btnCreate.setOnClickListener {
            val center = mMap?.cameraPosition?.target
            presenter.validData(center?.latitude, center?.longitude)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val iCheckCompany = LatLng(21.010109, 105.809382)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(iCheckCompany, 15f))
        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        mMap?.uiSettings?.isMapToolbarEnabled = false
    }

    override fun onSearchLocationSuccess(list: MutableList<ICPoints.Predictions>) {
        val visible = if (list.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        recyclerView.visibility = visible
        viewBackground.visibility = visible
        adapter.setData(list)
    }

    override fun onLocationClicked(obj: ICPoints.Predictions) {
        KeyboardUtils.hideSoftInput(edtSearch)

        disposable?.dispose()
        disposable = null
        edtSearch.setText(null)
        setupSearchInput()

        adapter.clearData()
        recyclerView.visibility = View.INVISIBLE
        viewBackground.visibility = View.INVISIBLE

        presenter.getLocationDetail(obj.description)
    }

    override fun onGetLocationDetailSuccess(obj: ICPointDetail.Location) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(obj.lat, obj.lng)))
    }

    override fun onValidSuccess(text: String) {
        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestAddNew)
    }

    override fun showError(errorMessage: String) {
        requireContext().showLongErrorToast(errorMessage)
    }

    override val mContext: Context?
        get() = requireContext()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.BACK) {
            activity?.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                checkGps()
                startLocationUpdates()
            } else {
                showLongWarning(R.string.vui_long_cap_quyen_su_dung_vi_tri_de_ung_dung_tim_duoc_vi_tri_cua_ban)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        try {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == requestAddNew){
                activity?.onBackPressed()
            }
        }
    }
}