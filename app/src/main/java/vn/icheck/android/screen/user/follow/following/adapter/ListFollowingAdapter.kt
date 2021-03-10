package vn.icheck.android.screen.user.follow.following.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_following.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.user.campaign.calback.IRecyclerViewAdapterListener
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.util.kotlin.WidgetUtils

class ListFollowingAdapter(val listener: IRecyclerViewAdapterListener<ICUserFollowing>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICUserFollowing>()

    private var iconMessage: Int = 0
    private var errorMessage: String? = null

    private var isLoadMore = false
    private var isLoading = false

    private val itemType = 1
    private val loadingType = 2

    private fun onLoadSuccess(listSize: Int) {
        iconMessage = R.drawable.ic_error_request
        isLoadMore = listSize >= APIConstants.LIMIT
        isLoading = false
        errorMessage = ""
    }

    fun setData(list: MutableList<ICUserFollowing>) {
        onLoadSuccess(list.size)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICUserFollowing>) {
        onLoadSuccess(list.size)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setError(icon: Int, error: String) {
        listData.clear()
        isLoading = false
        isLoadMore = false

        iconMessage = icon
        errorMessage = error
        notifyDataSetChanged()
    }

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                loadingType
            }
        } else {
            super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        } else {
            if (errorMessage == null) {
                0
            } else {
                1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_following, parent, false))
            loadingType -> LoadingHolder(parent)
            else -> LongMessageHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])

                holder.itemView.setOnClickListener {
                    listener.onItemClicked(listData[position])
                }
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    listener.onLoadMore()
                    isLoading = true
                }
            }
            is LongMessageHolder -> {
                errorMessage?.let {
                    if (it.isEmpty()) {
                        val message = holder.itemView.context.getString(R.string.ban_chua_co_nguoi_theo_doi_nao)

                        holder.bind(iconMessage, message, 0)
                    } else {
                        holder.bind(iconMessage, it, R.string.thu_lai)

                        holder.setListener(View.OnClickListener {
                            listener.onMessageClicked()
                        })
                    }
                }
            }
        }
    }

    class ViewHolder(view: View) : BaseViewHolder<ICUserFollowing>(view) {
        override fun bind(obj: ICUserFollowing) {
            itemView.viewTop.visibility = if (adapterPosition != 0) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            obj.user?.let {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, ImageHelper.getImageUrl(it.avatar, it.avatar_thumbnails?.small, ImageHelper.smallSize),
                        R.drawable.ic_square_avatar_default,
                        R.drawable.ic_square_avatar_default)

                itemView.tvName.text = it.name
            }
        }
    }
}