package vn.icheck.android.component.ads.product

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.android.synthetic.main.item_ads_product.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.component.ads.page.AdsPageAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAdsNew
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.ads_more.AdsMoreActivity
import vn.icheck.android.ui.layout.CustomGridLayoutManager

class AdsProductHolder(parent: ViewGroup) : BaseVideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ads_product, parent, false)) {

    private val adsAdapter = AdsProductAdapter()

    private var verticalDecoration : DividerItemDecoration? = null
    private var horizontalDecoration : DividerItemDecoration? = null

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
                    setBackgroundColor(Color.WHITE)
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    PagerSnapHelper().attachToRecyclerView(this)
                    verticalDecoration?.let { removeItemDecoration(it) }
                    horizontalDecoration?.let { removeItemDecoration(it) }

                    adapter = adsAdapter

                    adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_SLIDE_TYPE, obj.targetType, obj.targetId)
                }
                Constant.HORIZONTAL -> {
                    itemView.layoutTitle.visibility = View.VISIBLE
                    itemView.viewBackground.visibility = View.GONE
                    itemView.tvStart.visibility = View.GONE
                    itemView.tvEnd.visibility = View.GONE
                    setBackgroundColor(Color.WHITE)
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    verticalDecoration?.let { removeItemDecoration(it) }
                    horizontalDecoration?.let { removeItemDecoration(it) }
                    adapter = adsAdapter

                    adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_HORIZONTAL_TYPE, obj.targetType, obj.targetId,7)
                }
                Constant.GRID -> {
                    itemView.layoutTitle.visibility = View.VISIBLE
                    itemView.viewBackground.visibility = View.GONE
                    itemView.tvStart.visibility = View.GONE
                    itemView.tvEnd.visibility = View.GONE
                    setPadding(0, SizeHelper.size10, 0, SizeHelper.size10)
                    setBackgroundColor(Color.WHITE)
                    layoutManager = CustomGridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

                    verticalDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
                    val verticalDivider = ContextCompat.getDrawable(context, R.drawable.vertical_divider_more_business_stamp) as Drawable
                    verticalDecoration!!.setDrawable(verticalDivider)
                    addItemDecoration(verticalDecoration!!)

                    horizontalDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    val horizontalDivider = ContextCompat.getDrawable(context, R.drawable.horizontal_divider_more_business_stamp) as Drawable
                    horizontalDecoration!!.setDrawable(horizontalDivider)
                    addItemDecoration(horizontalDecoration!!)

                    adapter = adsAdapter

                    adsAdapter.setData(obj.data, obj.objectType, Constant.ADS_GRID_TYPE, obj.targetType, obj.targetId,6)
                }
            }

            setupRecyclerView(this)
        }

        itemView.btnMore.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (!obj.targetType.isNullOrEmpty() && !obj.targetId.isNullOrEmpty()) {
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