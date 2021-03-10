package vn.icheck.android.screen.user.commentpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment_permission.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.util.kotlin.WidgetUtils

class CommentPermissionAdapter(val listener: ItemClickListener<ICCommentPermission>) : RecyclerView.Adapter<CommentPermissionAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICCommentPermission>()

    private var selectedPosition = 0

    val getSelectedPermission: ICCommentPermission?
        get() {
            return if (listData.isNotEmpty()) {
                return listData[selectedPosition]
            } else {
                null
            }
        }

    val getPageID: Long?
        get() {
            return if (listData.isNotEmpty()) {
                return listData[selectedPosition].id
            } else {
                null
            }
        }

    fun setData(list: MutableList<ICCommentPermission>) {
        selectedPosition = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICCommentPermission>(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_permission, parent, false)) {

        override fun bind(obj: ICCommentPermission) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, if (obj.type == Constant.PAGE) {
                R.drawable.img_default_business_logo_big
            } else {
                R.drawable.ic_user_orange_circle
            })
            itemView.tvName.text = obj.name

            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    selectedPosition = adapterPosition
                    listener.onItemClick(adapterPosition, obj)
                } else {
                    listener.onItemClick(adapterPosition, null)
                }
            }
        }
    }
}