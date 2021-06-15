package vn.icheck.android.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    var userLat: Double = 0.0
    var userLng: Double = 0.0
    var targetLat: Double = 0.0
    var targetLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
//        lifecycleScope.launch {
//            val icLocationProvider = ICheckApplication.getInstance().icLocationProvider
//            icLocationProvider.refresh()
//            delay(200)
//            icLocationProvider.location?.let {
//                userLat = it.latitude
//                userLng = it.longitude
//                targetLat = it.latitude
//                targetLng = it.longitude
//            }
//        }


        val mapFragment = SupportMapFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit()
        mapFragment.getMapAsync(this)
        targetLat = intent.getDoubleExtra("lat", userLat)
        targetLng = intent.getDoubleExtra("lng", userLng)
        findViewById<TextView>(R.id.tv_shop_name).text = intent.getStringExtra("name")
        findViewById<TextView>(R.id.tv_shop_address).text = intent.getStringExtra("address")
        if (intent.getDoubleExtra("distance_value", 0.0) != 0.0) {
            findViewById<TextView>(R.id.tv_distance).text = getString(
                R.string.format_distance,
                intent.getDoubleExtra("distance_value", 0.0),
                intent.getStringExtra("distance_unit")
            )
        }
        findViewById<Button>(R.id.btn_guide).setOnClickListener {
//            val uriString = String.format(
//                "https://www.google.com/maps/dir/?api=1" +
//                        "&daddr=$targetLat,$targetLng"
//            )
            val uri = Uri.parse(
                getString(
                    R.string.format_url_google_map,
                    targetLat,
                    targetLng,
                    intent.getStringExtra("name")
                )
            )
            val directionIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(directionIntent)
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        val targetLatLng = LatLng(targetLat, targetLng)
        mMap?.isMyLocationEnabled = true
        mMap?.addMarker(MarkerOptions().position(targetLatLng).title(intent.getStringExtra("name")))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 14f))
    }
}
