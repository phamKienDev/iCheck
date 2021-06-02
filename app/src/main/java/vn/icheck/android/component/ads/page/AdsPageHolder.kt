package vn.icheck.android.component.ads.page

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.android.synthetic.main.item_ads_page.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAdsNew
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.ads_more.AdsMoreActivity
import vn.icheck.android.ui.layout.CustomGridLayoutManager

class AdsPageHolder (parent: ViewGroup) : BaseVideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ads_page, parent, false)) {
    private val adsAdapter = AdsPageAdapter()

    fun bind(obj: ICAdsNew) {
        itemView.tvTitle.text = obj.name ?: ""

        itemView.viewBackground.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            itemView.tvStart.visibility = view.visibility
            itemView.tvEnd.visibility = view.visibility
        }

        itemView.recyclerView.apply {
            onFlingListener = null
            adsAdapter.clearData()
            adapter = null

            when (obj.type) {
                Constant.SLIDE -> {
                    itemView.layoutTitle.visibility = View.GONE
                    itemView.viewBackground.visibility = View.VISIBLE
                    itemView.tvStart.visibility = View.VISIBLE
                    itemView.tvEnd.visibility = View.VISIBLE

                    itemView.layoutParent.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                    setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                    setPadding(SizeHelper.size6, SizeHelper.size10, SizeHelper.size6, SizeHelper.size10)

                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    PagerSnapHelper().attachToRecyclerView(this)

                    adapter = adsAdapter

                    adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_SLIDE_TYPE, obj.targetType, obj.targetId)
                }
                Constant.HORIZONTAL -> {
                    itemView.layoutTitle.visibility = View.VISIBLE
                    itemView.viewBackground.visibility = View.GONE
                    itemView.tvStart.visibility = View.GONE
                    itemView.tvEnd.visibility = View.GONE

                    itemView.layoutParent.setBackgroundColor(Color.TRANSPARENT)
                    setBackgroundColor(Color.TRANSPARENT)
                    setPadding(SizeHelper.size5, 0, SizeHelper.size5, 0)

                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                    adapter = adsAdapter

                    adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_HORIZONTAL_TYPE, obj.targetType, obj.targetId,5)
                }
                else -> {
                    itemView.layoutTitle.visibility = View.VISIBLE
                    itemView.viewBackground.visibility = View.GONE
                    itemView.tvStart.visibility = View.GONE
                    itemView.tvEnd.visibility = View.GONE

                    itemView.layoutParent.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                    setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))
                    setPadding(SizeHelper.size6, SizeHelper.size8, SizeHelper.size6, SizeHelper.size8)

                    layoutManager = CustomGridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
                    adapter = adsAdapter

                    adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_GRID_TYPE, obj.targetType, obj.targetId,6)
                }
            }

            setupRecyclerView(this)
        }

        itemView.btnMore.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (!obj.targetType.isNullOrEmpty()) {
                    FirebaseDynamicLinksActivity.startTarget(activity, obj.targetType, obj.targetId)
                }else {
                    itemView.context.startActivity(Intent(itemView.context, AdsMoreActivity::class.java).apply {
                        putExtra(Constant.DATA_1, obj)
                    })
                }
            }
        }

        itemView.tvStart.setOnClickListener {
            itemView.recyclerView.layoutManager.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
                    val visiblePosition = layoutManager.findFirstVisibleItemPosition()

                    if (visiblePosition > 0) {
                        itemView.recyclerView.smoothScrollToPosition(visiblePosition - 1)
                    }
                }
            }
        }

        itemView.tvEnd.setOnClickListener {
            itemView.recyclerView.layoutManager.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
                    val visiblePosition = layoutManager.findFirstVisibleItemPosition()

                    if (visiblePosition < adsAdapter.itemCount - 1) {
                        itemView.recyclerView.smoothScrollToPosition(visiblePosition + 1)
                    }
                }
            }
        }
    }

    override fun onPlayVideo(): Boolean {
        return ExoPlayerManager.checkPlayVideoHorizontal(itemView.recyclerView)
    }

    init {
        itemView.recyclerView.adapter = adsAdapter
    }
}