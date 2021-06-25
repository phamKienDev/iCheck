package vn.icheck.android.screen.user.map_history

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map_history.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.component.header_page.bottom_sheet_header_page.InforPageBottomSheet
import vn.icheck.android.component.view.MySpannable
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class MapHistoryActivity : BaseActivityMVVM() {
    lateinit var viewModel: MapHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_history)

        viewModel = ViewModelProvider(this).get(MapHistoryViewModel::class.java)

        view.background = ViewHelper.bgWhiteCornersTop20(this)
        initToolbar()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getMapPageHistory()
    }

    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            DialogHelper.showConfirm(this, it.message, false, object : ConfirmDialogListener {
                override fun onDisagree() {
                    onBackPressed()
                }

                override fun onAgree() {
                    getData()
                }
            })
        })

        viewModel.liveData.observe(this, Observer { obj ->
            swipeLayout.isRefreshing = false

            WidgetUtils.loadImageUrl(imgAvatar, obj.newDetail?.avatar)

            tvName.run {
                if (obj.verified) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_icon_chu, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                text = if (!obj.newDetail?.title.isNullOrEmpty()) {
                    obj.newDetail?.title
                } else {
                    getString(R.string.dang_cap_nhat)
                }
            }

            tvAddress.run {
                text = if (!obj.address.isNullOrEmpty()) {
                    obj.address
                } else {
                    getString(R.string.dang_cap_nhat)
                }
            }

            tvPhone.run {
                text = if (!obj.phone.isNullOrEmpty()) {
                    obj.phone
                } else {
                    getString(R.string.dang_cap_nhat)
                }
            }

            tvDescription.run {
                if (!obj.newDetail?.description.isNullOrEmpty()) {
                    text = obj.newDetail?.description
                    makeTextViewResizable(
                        this@MapHistoryActivity,
                        tvDescription,
                        5,
                        context.getString(R.string.doc_tiep),
                        vn.icheck.android.ichecklibs.ColorManager.getPrimaryColorCode(this@MapHistoryActivity),
                        obj.newDetail?.description!!
                    )
                }
            }

            map.run {
                if (obj.location?.lat != null || obj.location?.lon != null || obj.lat != null || obj.lon != null) {

                    onCreate(null)
                    getMapAsync {
                        MapsInitializer.initialize(this@MapHistoryActivity)

                        setMapLocation(obj.location?.lat!!, obj.location?.lon!!, it, R.drawable.ic_shop_orange_36)

                        setMapLocation(obj.lat, obj.lon, it, R.drawable.ic_location_me_30)

                        onResume()
                    }
                }
            }
        })
    }

    private fun setMapLocation(lat: Double, long: Double, googleMap: GoogleMap, icon: Int) {
        val map = LatLng(lat, long)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map, 13f))
        googleMap.addMarker(map.let { MarkerOptions().position(it).icon(BitmapDescriptorFactory.fromResource(icon)) })
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    private fun makeTextViewResizable(context: Context, tv: TextView, maxLine: Int, expandText: String, color: String, description: String) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                val lineEndIndex: Int = tv.layout.getLineEnd(maxLine - 2)
                text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + "..." + expandText
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(addClickablePartTextViewResizable(context, Html.fromHtml(tv.text.toString()), expandText, color, description), TextView.BufferType.SPANNABLE)
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        context: Context,
        strSpanned: Spanned,
        spanableText: String,
        color: String,
        description: String
    ): SpannableStringBuilder? {
        val str: String = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false, color) {
                override fun onClick(widget: View) {
                    InforPageBottomSheet(description, this@MapHistoryActivity).show()
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }
}