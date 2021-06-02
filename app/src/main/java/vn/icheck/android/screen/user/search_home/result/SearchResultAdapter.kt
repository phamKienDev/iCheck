package vn.icheck.android.screen.user.search_home.result

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.screen.user.search_home.result.holder.NotResultHolder
import vn.icheck.android.screen.user.search_home.result.holder.ProductSearchHolder
import vn.icheck.android.screen.user.search_home.result.holder.SetTypeSearchHolder
import vn.icheck.android.screen.user.search_home.result.holder.UserSearchHolder
import vn.icheck.android.screen.user.search_home.result.model.ICSearchResult
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class SearchResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICSearchResult>()
    private var error: ICError? = null
    private var recyclerViewPool: RecyclerView.RecycledViewPool? = null


    fun addData(list: MutableList<ICSearchResult>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(obj: ICSearchResult) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type == obj.type) {
                if (obj.data != null) {
                    listData[i] = obj
                    notifyItemChanged(i)
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
            }
        }
    }


    val isEmpity: Boolean
        get() {
            return listData.isEmpty()
        }

    fun setError(error: ICError) {
        listData.clear()
        this.error = error
        notifyDataSetChanged()
    }

    fun resetData() {
        listData.clear()
        error = null
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Constant.PRODUCT_TYPE -> ProductSearchHolder(parent, recyclerViewPool)
            Constant.USER_TYPE -> UserSearchHolder(parent, recyclerViewPool)
            Constant.RESULT_EMPTY -> NotResultHolder(parent)
            Constant.ERROR_SERVER -> LongMessageHolder(parent)
            Constant.SEARCH_MORE -> SearchMoreHolder(parent)
            else -> SetTypeSearchHolder(parent, recyclerViewPool)
        }
    }


    override fun getItemCount(): Int {
        return if (error != null) {
            1
        } else {
            if (listData.isNotEmpty()) {
                listData.size
            } else {
                1
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (error != null) {
            Constant.ERROR_SERVER
        } else {
            if (listData.isNotEmpty()) {
                listData[position].type
            } else {
                Constant.RESULT_EMPTY
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductSearchHolder -> {
                if (listData[position].data != null)
                    holder.bind(listData[position].data as MutableList<ICProductTrend>)
            }
            is UserSearchHolder -> {
                if (listData[position].data != null)
                    holder.bind(listData[position].data as MutableList<ICSearchUser>)
            }

            is SetTypeSearchHolder -> {
                if (listData[position].data != null)
                    holder.bind(listData[position])
            }
            is LongMessageHolder -> {
                if (error != null) {
                    holder.bind(error!!.icon, error!!.message, R.string.thu_lai)
                    holder.setListener(View.OnClickListener {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR))
                    })
                }
            }
            is SearchMoreHolder -> {
                holder.bind(listData[position])
            }
            is NotResultHolder -> {
                holder.setListener(View.OnClickListener {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
                })
            }
        }
    }

    inner class SearchMoreHolder(parent: ViewGroup) : BaseViewHolder<ICSearchResult>(LayoutInflater.from(parent.context).inflate(R.layout.item_search_more_holder, parent, false)) {
        override fun bind(obj: ICSearchResult) {
            itemView.rootView.background=ViewHelper.bgWhiteCorners4(itemView.context)
            if (obj.data != null) {
                Handler().post{
                    itemView.beVisible()
                }
                itemView.setOnClickListener {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_SEARCH_REVIEW_OR_PAGE,adapterPosition))
                }
                if (adapterPosition == listData.size - 1) {
                    itemView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.setMargins(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size20)
                    }
                }
            } else {
                Handler().post{
                    itemView.beGone()
                }
            }
        }
    }
}