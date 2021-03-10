package vn.icheck.android.screen.user.search_home.result.holder

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_search_result.view.*
import kotlinx.android.synthetic.main.layout_product_search_result_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper.setTextNameProduct
import vn.icheck.android.helper.TextHelper.setTextPriceProduct
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductSearchHolder(parent: ViewGroup, val recyclerViewPool: RecyclerView.RecycledViewPool?) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_product_search_result_holder, parent, false)) {
    fun bind(list: MutableList<ICProductTrend>) {
        Handler().post {
            if (list.isNullOrEmpty()) {
                itemView.tvTitle.beGone()
                itemView.rcv_product_search.beGone()
                itemView.tv_xem_them.beGone()
            }else{
                itemView.tvTitle.beVisible()
                itemView.rcv_product_search.beVisible()
                itemView.tv_xem_them.beVisible()
            }
        }

        val adapter = ItemProductSearchAdapter(list)
        itemView.rcv_product_search.adapter = adapter
        itemView.rcv_product_search.setRecycledViewPool(recyclerViewPool)
        itemView.rcv_product_search.layoutManager = LinearLayoutManager(itemView.context)

        if (list.size > 4) {
            itemView.tv_xem_them.beVisible()
        } else {
            itemView.tv_xem_them.beGone()
        }
        itemView.tv_xem_them.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_SEARCH_PRODUCT))
        }
    }

    inner class ItemProductSearchAdapter(val listData: MutableList<ICProductTrend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemProductSearchHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_search_result, parent, false))
        }

        override fun getItemCount(): Int {
            return if (listData.size > 4) {
                4
            } else {
                listData.size
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ItemProductSearchHolder).bind(listData[position])
        }

        inner class ItemProductSearchHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(obj: ICProductTrend) {
                if (!obj.media.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgProduct, obj.media!![0].content, R.drawable.img_default_product_big,R.drawable.img_default_product_big, SizeHelper.size4)
                } else {
                    WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgProduct, "", R.drawable.img_default_product_big,R.drawable.img_default_product_big, SizeHelper.size4)
                }

                itemView.tv_name_product.setTextNameProduct(obj.name)
                itemView.tv_price_product.setTextPriceProduct(obj.price)


                itemView.tv_verified.visibility = if (obj.verified) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                itemView.setOnClickListener {
                    ICheckApplication.currentActivity()?.let {
                        IckProductDetailActivity.start(it, obj.id)
                    }
                }
            }
        }
    }
}