package vn.icheck.android.chat.icheckchat.screen.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_PACKAGE
import vn.icheck.android.chat.icheckchat.base.view.loadImageUrlNotCrop
import vn.icheck.android.chat.icheckchat.databinding.ItemPackageStickerBinding
import vn.icheck.android.chat.icheckchat.databinding.ItemStickerBinding
import vn.icheck.android.chat.icheckchat.model.MCSticker

class StickerAdapter(private val type: Int) : BaseRecyclerView<MCSticker>() {
    var listenerSticker: IStickerListener? = null

    fun setData(obj: MutableList<MCSticker>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return type
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (type == TYPE_PACKAGE) {
            PackageStickerHolder(ItemPackageStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            StickerHolder(ItemStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is PackageStickerHolder -> {
                holder.bind(listData[position])
            }
            is StickerHolder -> {
                holder.bind(listData[position])
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }

    inner class PackageStickerHolder(val binding: ItemPackageStickerBinding) : BaseViewHolder<MCSticker>(binding) {
        override fun bind(obj: MCSticker) {
            loadImageUrlNotCrop(binding.root, obj.thumbnail, R.drawable.ic_default_image_upload_150_chat)

            itemView.setOnClickListener {
                listenerSticker?.onClick(obj)
            }
        }
    }

    inner class StickerHolder(val binding: ItemStickerBinding) : BaseViewHolder<MCSticker>(binding) {
        override fun bind(obj: MCSticker) {
            loadImageUrlNotCrop(binding.root, obj.thumbnail, R.drawable.ic_default_image_upload_150_chat)

            itemView.setOnClickListener {
                listenerSticker?.onClick(obj)
            }
        }
    }

    fun setOnClick(listener: IStickerListener) {
        listenerSticker = listener
    }

    interface IStickerListener {
        fun onClick(obj: MCSticker)
    }
}