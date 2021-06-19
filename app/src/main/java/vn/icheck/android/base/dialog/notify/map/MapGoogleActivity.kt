package vn.icheck.android.base.dialog.notify.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map_google.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper

class MapGoogleActivity : BaseActivityMVVM(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    private var lat: Double? = null
    private var lon: Double? = null

    private var isSetLocation = false
    private var isFirstOpen = true
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_google)
        getData()
        initGps()
        initMap()
        listener()
    }

    private fun getData() {
        try {
            lat = intent?.getDoubleExtra(Constant.DATA_1, -1.0)
            lon = intent?.getDoubleExtra(Constant.DATA_2, -1.0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (lat == null || lat == -1.0 || lon == null || lon == -1.0) {
            lat = null
            lon = null
        }
    }

    private fun initGps() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = (10 * 1000)
        mLocationRequest?.fastestInterval = 2000

        mLocationRequest?.let {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(it)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(this)
            settingsClient.checkLocationSettings(locationSettingsRequest)
        }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initUpdateLocation() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PermissionHelper.isAllowPermission(permission, this)) {
            mMap?.isMyLocationEnabled = true

        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun listener() {
        layoutView.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (lat != null && lon != null) {
            mMap = googleMap
            mMap?.uiSettings?.isZoomControlsEnabled = false

            if (!isSetLocation) {
                val latLng = if (lat != null && lon != null) {
                    isSetLocation = true
                    LatLng(lat!!, lon!!)
                } else {
                    LatLng(21.027380, 105.834046)
                }
                mMap?.addMarker(MarkerOptions().position(latLng))
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }

            mMap?.setOnCameraIdleListener {
                if (isFirstOpen) {
                    isFirstOpen = false
                    return@setOnCameraIdleListener
                }

                isSetLocation = true
            }
            initUpdateLocation()
        } else {
            DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
