package vn.icheck.android.screen.user.page_details.fragment.page.widget.detail

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.map.MapGoogleActivity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICPageDetail
import vn.icheck.android.screen.bottomsheet.BottomSheetPageAdapter
import vn.icheck.android.screen.user.webview.WebViewActivity

class WidgetPageDetailHolder(parent: ViewGroup, val type: String) : BaseViewHolder<ICPageDetail>(ViewHelper.createLayoutPageDetail(parent.context)) {
    override fun bind(obj: ICPageDetail) {
        (itemView as LinearLayout).run {
            if (type != "details") {
                layoutParams = ViewHelper.createLayoutParams().also {
                    it.bottomMargin = SizeHelper.size10
                }
                (getChildAt(6) as AppCompatTextView).run {
                    text = obj.objectType
                }

                (getChildAt(7) as LinearLayout).run {
                    (getChildAt(1) as AppCompatTextView).run {
                        // TODO
//                        if (!obj.pageOverview?.description.isNullOrEmpty()) {
//                            text = obj.pageOverview?.description
//                        }
                    }
                }
            } else {
                (getChildAt(6) as AppCompatTextView).run {
                    visibility = View.GONE
                }

                (getChildAt(7) as LinearLayout).run {
                    visibility = View.GONE
                }

                (getChildAt(8) as AppCompatTextView).run {
                    visibility = View.GONE
                }
            }

            (getChildAt(0) as AppCompatTextView).run {
                visibility = if (type == "details") {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            (getChildAt(1) as CardView).run {
                if (obj.location?.lat != null && obj.location?.lon != null) {
                    visibility = View.VISIBLE

                    (getChildAt(0) as MapView).run {
                        onCreate(null)
                        getMapAsync {
                            MapsInitializer.initialize(itemView.context)

                            setMapLocation(obj.location?.lat!!, obj.location?.lon!!, it)

                            onResume()
                        }
                    }
                } else {
                    visibility = View.GONE
                }
            }

            (getChildAt(2) as LinearLayout).run {
                if (!obj.address.isNullOrEmpty()) {
                    visibility = View.VISIBLE

                    (getChildAt(1) as AppCompatTextView).run {
                        text = obj.address
                    }

                    (getChildAt(2) as AppCompatTextView).run {
                        setOnClickListener {
                            val intent = Intent(ICheckApplication.currentActivity(), MapGoogleActivity::class.java)
                            intent.putExtra(Constant.DATA_1, obj.location?.lat!!)
                            intent.putExtra(Constant.DATA_2, obj.location?.lon!!)
                            ICheckApplication.currentActivity()?.let { act ->
                                act.startActivity(intent)
                            }
                        }
                    }
                } else {
                    visibility = View.GONE
                }
            }

            (getChildAt(3) as AppCompatTextView).run {
                if (!obj.phone.isNullOrEmpty()) {
                    visibility = View.VISIBLE
                    text = obj.phone

                    setOnClickListener {
                        DialogHelper.showConfirm(itemView.context, itemView.context.getString(R.string.thong_bao), itemView.context.getString(R.string.ban_co_muon_goi_dien_thoai_den_so, obj.phone), itemView.context.getString(R.string.huy), itemView.context.getString(R.string.dong_y), true, object : ConfirmDialogListener {
                            override fun onDisagree() {
                            }

                            override fun onAgree() {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:${obj.phone}")
                                ICheckApplication.currentActivity()?.let { act ->
                                    act.startActivity(intent)
                                }
                            }
                        })
                    }
                } else {
                    visibility = View.GONE
                }
            }

            (getChildAt(4) as AppCompatTextView).run {
                if (!obj.mail.isNullOrEmpty()) {
                    visibility = View.VISIBLE
                    text = obj.mail

                    setOnClickListener {
                        val mailIntent = Intent(Intent.ACTION_VIEW)
                        val data = Uri.parse("mailto:?to=${obj.mail}")
                        mailIntent.data = data
                        try {
                            itemView.context.startActivity(Intent.createChooser(mailIntent, "Send mail..."))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    visibility = View.GONE
                }
            }

            (getChildAt(5) as AppCompatTextView).run {
                if (!obj.website.isNullOrEmpty()) {
                    visibility = View.VISIBLE
                    text = obj.website

                    setOnClickListener {
                        ICheckApplication.currentActivity()?.let { act ->
                            val intent = Intent(act, WebViewActivity::class.java)
                            intent.putExtra(Constant.DATA_1, obj.website)
                            act.startActivity(intent)
                            act.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
                        }
                    }
                } else {
                    visibility = View.GONE
                }
            }

            if (obj.infomations != null) {
                if (type == "details") {
                    (getChildAt(9) as AppCompatTextView).run {
                        visibility = View.GONE
                    }

                    (getChildAt(10) as AppCompatTextView).run {
                        visibility = View.GONE
                    }

                } else {
                    (getChildAt(9) as AppCompatTextView).run {
                        if (!obj.infomations?.get(0)?.name.isNullOrEmpty()) {
                            text = obj.infomations?.get(0)?.description
                        }
                    }
                    (getChildAt(10) as AppCompatTextView).run {
                        visibility = if (!obj.infomations?.get(1)?.data.isNullOrEmpty()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
            }

            (getChildAt(11) as RecyclerView).run {

                if (type == "details" && !obj.infomations.isNullOrEmpty()) {
                    layoutParams = ViewHelper.createLayoutParams().also {
                        it.setMargins(0, SizeHelper.size16, 0, SizeHelper.size12)
                    }
                    layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

                    adapter = WidgetPageDetailAdapter(obj.infomations!!)
                } else {
                    if (obj.infomations?.get(1) != null) {
                        if (obj.infomations?.get(1)?.key == "prize") {
                            layoutParams = ViewHelper.createLayoutParams().also {
                                it.setMargins(0, 0, 0, SizeHelper.size12)
                            }
                            layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

                            adapter = BottomSheetPageAdapter(obj.infomations?.get(1)?.data!!, SizeHelper.size40, SizeHelper.size40, 14f, 12f, SizeHelper.size37)
                        } else {
                            visibility = View.GONE
                        }
                    }
                }
            }

            // TODO
//            (getChildAt(12) as LinearLayout).run {
//                if (type == "details") {
//                    visibility = View.GONE
//                } else {
//                    visibility = View.VISIBLE
//
//                    (getChildAt(1) as AppCompatTextView).run {
//                        text = "Giới thiệu về ${obj.name}"
//                    }
//
//                    WidgetUtils.loadImageUrlRounded4((getChildAt(2) as AppCompatImageView), obj.icNewDetail?.avatar)
//
//                    (getChildAt(3) as AppCompatTextView).run {
//                        if (!obj.icNewDetail?.title.isNullOrEmpty()) {
//                            text = obj.icNewDetail?.title
//                        }
//                    }
//
//                    (getChildAt(4) as AppCompatTextView).run {
//                        if (!obj.icNewDetail?.description.isNullOrEmpty()) {
//                            text = obj.icNewDetail?.description
//                        }
//                    }
//
//                    setOnClickListener {
//                        val intent = Intent(itemView.context, NewDetailV2Activity::class.java)
//                        intent.putExtra(Constant.DATA_1, "Giới thiệu về ${obj.name}")
//                        intent.putExtra(Constant.DATA_2, obj.icNewDetail?.avatar)
//                        intent.putExtra(Constant.DATA_3, obj.icNewDetail?.description)
//                        itemView.context.startActivity(intent)
//                    }
//                }
//            }
        }
    }

    private fun setMapLocation(lat: Double, long: Double, googleMap: GoogleMap) {
        if (lat != null || long != null) {
            val map = LatLng(lat, long)

            if (googleMap == null)
                return

            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(map, 16f))
            googleMap?.addMarker(map?.let { MarkerOptions().position(it).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_map_24_30)) })
            googleMap?.uiSettings?.isZoomControlsEnabled = false
            googleMap?.uiSettings?.isScrollGesturesEnabled = false
            googleMap?.uiSettings?.isZoomGesturesEnabled = false
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        } else {
            null
        }
    }
}