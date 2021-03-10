package vn.icheck.android.screen.user.search_home.result.holder

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICPageQuery
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICShopQuery
import vn.icheck.android.screen.user.search_home.result.model.ICSearchResult

class SetTypeSearchHolder(parent: ViewGroup, val recyclerViewPool: RecyclerView.RecycledViewPool?) : BaseViewHolder<ICSearchResult>(createView(parent)) {
    lateinit var adapter: TypeSearchAdapter
    lateinit var recyclerView: RecyclerView
    override fun bind(obj: ICSearchResult) {
        recyclerView = itemView as RecyclerView
        recyclerView.setRecycledViewPool(recyclerViewPool)
        recyclerView.layoutManager = LinearLayoutManager(itemView.context)
        adapter = TypeSearchAdapter(obj.data as MutableList<Any>)
        recyclerView.adapter = adapter
    }

    fun updateReview() {
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun createView(parent: ViewGroup): View {
            return RecyclerView(parent.context).also {
                it.layoutParams = ViewHelper.createLayoutParams()
            }
        }
    }

    class TypeSearchAdapter(val data: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                Constant.REVIEW_TYPE -> {
                    ReviewSearchHolder(parent)
                }
                Constant.SHOP_TYPE -> {
                    ShopSearchHolder(parent)
                }
                else -> {
                    PageSearchHolder(parent)
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                data.firstOrNull() is ICPost -> {
                    Constant.REVIEW_TYPE
                }
                data.firstOrNull() is ICShopQuery -> {
                    Constant.SHOP_TYPE
                }
                data.firstOrNull() is ICPageQuery -> {
                    Constant.PAGE_TYPE
                }
                else -> {
                    super.getItemViewType(position)
                }
            }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is ReviewSearchHolder -> {
                    holder.bind(data[position] as ICPost)
                }
                is ShopSearchHolder -> {
//                    holder.bind(data[position] as ICShopQuery)
                }
                is PageSearchHolder -> {
                    holder.bind(data[position] as ICPageQuery)
                }
            }
        }

    }
}