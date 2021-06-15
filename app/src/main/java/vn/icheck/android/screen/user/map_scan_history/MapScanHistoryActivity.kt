package vn.icheck.android.screen.user.map_scan_history

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_map_scan_history.*
import kotlinx.android.synthetic.main.item_routes.view.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.map4d.map.annotations.MFMarker
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.annotations.MFPolyline
import vn.map4d.map.annotations.MFPolylineOptions
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.MFSupportMapFragment
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.types.MFLocationCoordinate

class MapScanHistoryActivity : BaseActivityMVVM(), StoreSellMapHistoryView, OnMapReadyCallback {

    val viewModel: MapScanHistoryViewModel by viewModels()

    private var adapter = StoreSellMapHistoryAdapter(this)

    private var map4D: Map4D? = null
    private var listStore = mutableListOf<ICStoreNear>()
    private var latLngList = mutableListOf<MFLocationCoordinate>()
    private var latLngListServer = mutableListOf<MFLocationCoordinate>()
    private val markersList = mutableListOf<MFMarker>()
    private var polyline: MFPolyline? = null
    private var markerView: MFMarker? = null

    private val requestLocation = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_scan_history)

        setupToolbar()
        initViewModel()
        initRecyclerview()
        viewModel.getData(intent)
        initMap()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initViewModel() {
        viewModel.setListData.observe(this, {
            listStore.clear()
            listStore.addAll(it)

            adapter.setData(it, viewModel.idProduct).let { selectedPos ->
                recyclerView.scrollToPosition(selectedPos)

                Handler(Looper.getMainLooper()).postDelayed({
                    recyclerView.findViewHolderForAdapterPosition(selectedPos)?.itemView?.performClick()
                }, 200)
            }
        })

        viewModel.addListData.observe(this, {
            listStore.addAll(it)
            adapter.addListData(it)
        })

        viewModel.listRoute.observe(this, {
            removePolyline()
            latLngListServer = it
            initMap()
        })

        viewModel.onError.observe(this, {
            when (it) {
                Constant.ERROR_SERVER -> {
                    if (viewModel.latShop != 0.0 && viewModel.lonShop != 0.0) {
                        map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(MFLocationCoordinate(viewModel.latShop, viewModel.lonShop), 16.0))
                    }
                    addMakerToMap()
                }
            }
        })

        viewModel.onShowErrorMessage.observe(this, {
            showLongError(it)
        })
    }

    private fun initRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.myLocationDemo) as MFSupportMapFragment?
        mapFragment?.getMapAsync(this)

        moveCameraDefault()
    }

    private fun moveCameraDefault() {
        if (PermissionHelper.checkPermission(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), requestLocation)) {
            if (viewModel.latShop != 0.0 && viewModel.lonShop != 0.0) {
                map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(MFLocationCoordinate(viewModel.latShop, viewModel.lonShop), 16.0))
            } else {
                Handler().postDelayed({
                    if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
                        map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(MFLocationCoordinate(APIConstants.LATITUDE, APIConstants.LONGITUDE), 16.0))
                    }
                }, 500)
            }
            map4D?.isMyLocationEnabled = true
            NetworkHelper.checkGPS(this@MapScanHistoryActivity)
        }
    }

    private fun createPath() {
        latLngList.clear()
        for (i in latLngListServer) {
            latLngList.add(MFLocationCoordinate(i.getLatitude(), i.getLongitude()))
        }
    }

    override fun onMapReady(map4D: Map4D?) {
        this.map4D = map4D
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map4D?.isMyLocationEnabled = true
        createPath()

        if (viewModel.latShop != 0.0 && viewModel.lonShop != 0.0) {
            map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(MFLocationCoordinate(viewModel.latShop, viewModel.lonShop), 16.0))
        }
//        else if (viewModel.lat != 0.0 && viewModel.lon != 0.0) {
//            map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(MFLocationCoordinate(viewModel.lat, viewModel.lon), 16.0))
//        }
        else {
            Handler().postDelayed({
                if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
                    map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(MFLocationCoordinate(APIConstants.LATITUDE, APIConstants.LONGITUDE), 16.0))
                }
            }, 500)
        }

        addPolylineToMap()
        addMakerToMap()
        map4D?.setOnMarkerClickListener { marker ->
            if (marker?.position?.getLatitude() != null && marker.position?.getLongitude() != null) {
//                viewModel.lat = marker.position?.getLatitude()!!
//                viewModel.lon = marker.position?.getLongitude()!!
//                viewModel.getLocationShop(viewModel.lat, viewModel.lon)

                viewModel.latShop = marker.position?.getLatitude() ?: 0.0
                viewModel.lonShop = marker.position?.getLongitude() ?: 0.0
                viewModel.getLocationShop(viewModel.latShop, viewModel.lonShop)
            }
            false
        }
    }

    private fun addMakerToMap() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_routes, layoutParent, false)

        if (!listStore.isNullOrEmpty()) {
            for (i in listStore) {
                if (i.location?.lat != null && i.location?.lon != null) {
                    if (viewModel.isPage) {
                        WidgetUtils.loadImageUrlFitCenter(view.imgShop, i.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
                        markerView = map4D?.addMarker(MFMarkerOptions().position(MFLocationCoordinate(i.location?.lat!!, i.location?.lon!!)).iconView(view))
                    } else {
                        WidgetUtils.loadImageUrlFitCenter(view.imgShop, i.avatar, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px)
                        markerView = map4D?.addMarker(MFMarkerOptions().position(MFLocationCoordinate(i.location?.lat!!, i.location?.lon!!)).iconView(view))
                    }
                }

                if (markerView != null) {
                    markersList.add(markerView!!)
                }
            }
        } else {
            if (!viewModel.avatarShop.isEmpty()) {
                if (viewModel.isPage) {
                    WidgetUtils.loadImageUrlFitCenter(view.imgShop, viewModel.avatarShop, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
                } else {
                    WidgetUtils.loadImageUrlFitCenter(view.imgShop, viewModel.avatarShop, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px)
                }
                markerView = map4D?.addMarker(MFMarkerOptions().position(MFLocationCoordinate(viewModel.latShop, viewModel.lonShop)).iconView(view))
            } else {
                if (viewModel.isPage) {
                    view.imgShop.setImageResource(R.drawable.ic_business_v2)
                } else {
                    view.imgShop.setImageResource(R.drawable.ic_error_load_shop_40_px)
                }

                markerView = map4D?.addMarker(MFMarkerOptions().position(MFLocationCoordinate(viewModel.latShop, viewModel.lonShop)).iconView(view))
            }
            if (markerView != null) {
                markersList.add(markerView!!)
            }
        }
    }

    private fun addPolylineToMap() {
        polyline = map4D?.addPolyline(MFPolylineOptions().add(*latLngList.toTypedArray()).color(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)).width(8f))
    }

    private fun removePolyline() {
        polyline?.remove()
        polyline = null
        markerView?.remove()
        markerView = null
        markersList.clear()
    }

//    private fun requestLocationPermission(permission: Array<String>) {
//        ActivityCompat.requestPermissions(this, permission, requestLocation)
//    }

//    private fun isLocationPermissionEnable(): Boolean {
//        var isLocationPermissionenabed = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            isLocationPermissionenabed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        }
//        return isLocationPermissionenabed
//    }

    override fun onClickShop(item: ICStoreNear) {
        if (item.location?.lat != null && item.location?.lon != null) {
//            viewModel.lat = item.location?.lat!!
//            viewModel.lon = item.location?.lon!!
//            viewModel.getLocationShop(viewModel.lat, viewModel.lon)

            viewModel.latShop = item.location?.lat ?: 0.0
            viewModel.lonShop = item.location?.lon ?: 0.0
            viewModel.getLocationShop(viewModel.latShop, viewModel.lonShop)
        }
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        viewModel.getStoreNear(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionHelper.checkResult(grantResults)) {
            if (requestCode == requestLocation) {
//                map4D?.moveCamera(MFCameraUpdateFactory.newCoordinate(MFLocationCoordinate(APIConstants.LATITUDE, APIConstants.LONGITUDE)))
//                map4D?.isMyLocationEnabled = true
                moveCameraDefault()
            }
        } else {
            showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
        }
    }
}