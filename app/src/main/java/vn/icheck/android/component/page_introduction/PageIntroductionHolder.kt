package vn.icheck.android.component.page_introduction

import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.HolderPageIntroductionBinding
import vn.icheck.android.network.models.ICPageDetail
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity
import vn.icheck.android.util.ick.*

class PageIntroductionHolder private constructor(val binding: HolderPageIntroductionBinding, val recycledViewPool: RecyclerView.RecycledViewPool?) : RecyclerView.ViewHolder(binding.root) {


    fun bind(pageDetail: ICPageDetail?) {

        if (pageDetail?.location?.lat != null && pageDetail.location?.lon != null) {
            binding.myMapview.beVisible()
            binding.myMapview.post {
                binding.myMapview.onCreate(null)
                binding.myMapview.getMapAsync {
                    MapsInitializer.initialize(itemView.context)
                    setMapLocation(pageDetail.location?.lat, pageDetail.location?.lon, it)
                    binding.myMapview.onResume()
                    it.setOnMapClickListener {
                        val intent = Intent(itemView.context, MapScanHistoryActivity::class.java)
                        intent.putExtra(Constant.DATA_2, pageDetail.id)
                        intent.putExtra(Constant.DATA_3, pageDetail.location?.lat ?: 0.0)
                        intent.putExtra(Constant.DATA_4, pageDetail.location?.lon ?: 0.0)
                        intent.putExtra("isPage", true)
                        intent.putExtra("avatarShop", pageDetail.icPageOverView?.avatar)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        } else {
            binding.myMapview.beGone()
        }

        if (pageDetail?.address.isNullOrEmpty()) {
            binding.tvAddress.beGone()
        } else {
            binding.tvAddress.beVisible()
            binding.tvAddress simpleText pageDetail?.address.getInfo()
        }


        if (pageDetail?.mail.isNullOrEmpty()) {
            binding.tvEmail.beGone()
        } else {
            binding.tvEmail.beVisible()
            binding.tvEmail.text = pageDetail?.mail
            binding.tvEmail.setOnClickListener {
                pageDetail?.mail?.startSentEmail()
            }
        }

        if (pageDetail?.phone.isNullOrBlank()) {
            binding.tvPagePhone.beGone()
        } else {
            binding.tvPagePhone.beVisible()
            binding.tvPagePhone.text = pageDetail?.phone
            binding.tvPagePhone.setOnClickListener {
                pageDetail?.phone?.startCallPhone()
            }
        }

        if (pageDetail?.website.isNullOrEmpty()) {
            binding.tvWebsite.beGone()
        } else {
            binding.tvWebsite.beVisible()
            val charSpe = " - "
            if (pageDetail?.website!!.contains(charSpe)) {
                val listWeb = pageDetail.website!!.toString().split(" - ").toMutableList()
                val spannable = SpannableString(pageDetail.website)

                //set màu đen cho dấu -
                var nextIndex = pageDetail.website!!.indexOf(charSpe)
                var currentOffset: Int

                if (nextIndex != -1) {
                    do {
                        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(binding.tvWebsite.context, R.color.black)), nextIndex + 1, nextIndex + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        currentOffset = nextIndex + charSpe.length
                        nextIndex = pageDetail.website!!.indexOf(charSpe, currentOffset)
                    } while (nextIndex != -1)
                }

                // callback các category ở giữa
                var positionItem = 0
                for (i in 0 until listWeb.size) {
                    val midItem = pageDetail.website!!.indexOf(listWeb[i], positionItem)
                    positionItem = midItem + listWeb[i].length

                    spannable.setSpan(object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            listWeb[i].startWebView()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                ds.underlineColor = Color.TRANSPARENT
                            }
                        }
                    }, midItem, positionItem, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                binding.tvWebsite.text = spannable
                binding.tvWebsite.movementMethod = LinkMovementMethod.getInstance()


            } else {
                binding.tvWebsite.text = pageDetail.website
                binding.tvWebsite.setOnClickListener {
                    pageDetail.website?.startWebView()
                }
            }
        }
    }

    private fun setMapLocation(lat: Double?, long: Double?, googleMap: GoogleMap?) {
        if (lat != null || long != null) {
            val map = LatLng(lat!!, long!!)

            if (googleMap == null)
                return

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map, 16f))
            googleMap.addMarker(map.let { MarkerOptions().position(it).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_map_24_30)) })
            googleMap.uiSettings?.isZoomControlsEnabled = false
            googleMap.uiSettings?.isScrollGesturesEnabled = false
            googleMap.uiSettings?.isZoomGesturesEnabled = false
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    companion object {
        fun create(parent: ViewGroup, recycledViewPool: RecyclerView.RecycledViewPool?): PageIntroductionHolder {
            val binding: HolderPageIntroductionBinding = HolderPageIntroductionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PageIntroductionHolder(binding, recycledViewPool)
        }
    }
}