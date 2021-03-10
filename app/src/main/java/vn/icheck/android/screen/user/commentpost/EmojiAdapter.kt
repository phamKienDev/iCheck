package vn.icheck.android.screen.user.commentpost

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.activities.chat.sticker.StickerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.ui.view.SquareImageView
import vn.icheck.android.util.kotlin.WidgetUtils

class EmojiAdapter(val type: Int, val listener: ItemClickListener<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<Any>()

    private val parentType = 1

    private var selectedPosition = 0

    fun setParentData(list: MutableList<StickerPackages>) {
        selectedPosition = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setChildData(list: List<Stickers>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (type == parentType) {
            ParentHolder(parent)
        } else {
            ChildHolder(parent)
        }
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChildHolder -> {
                holder.bind(listData[position] as Stickers)
            }
            is ParentHolder -> {
                holder.bind(listData[position] as StickerPackages)
            }
        }
    }

    inner class ParentHolder(parent: ViewGroup) : BaseViewHolder<StickerPackages>(createParentView(parent.context)) {

        override fun bind(obj: StickerPackages) {
            WidgetUtils.loadImageFitCenterUrl(itemView as AppCompatImageView, obj.thumbnail)

            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    selectedPosition = adapterPosition
                    listener.onItemClick(adapterPosition, obj)
                }
            }
        }
    }

    fun createParentView(context: Context): AppCompatImageView {
        return AppCompatImageView(context).apply {
            layoutParams = RecyclerView.LayoutParams(SizeHelper.size40, SizeHelper.size40).apply {
                setMargins(SizeHelper.size5, 0, SizeHelper.size5, 0)
            }
        }
    }

    inner class ChildHolder(parent: ViewGroup) : BaseViewHolder<Stickers>(createChildView(parent.context)) {

        override fun bind(obj: Stickers) {
            WidgetUtils.loadImageFitCenterUrl(itemView as SquareImageView, obj.image)

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, obj)
            }
        }
    }

    fun createChildView(context: Context): SquareImageView {
        return SquareImageView(context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                setMargins(SizeHelper.size10, SizeHelper.size10, SizeHelper.size10, SizeHelper.size10)
            }
        }
    }
}