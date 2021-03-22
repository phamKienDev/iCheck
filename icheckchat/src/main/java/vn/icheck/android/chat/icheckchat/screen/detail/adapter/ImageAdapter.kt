package vn.icheck.android.chat.icheckchat.screen.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_IMAGE
import vn.icheck.android.chat.icheckchat.databinding.ItemImageUploadBinding
import java.io.File

class ImageAdapter : BaseRecyclerView<File>() {

    fun setListImage(obj: MutableList<File>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setImage(obj: File) {
        listData.add(obj)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemType(position: Int): Int {
        return TYPE_IMAGE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageHolder(ItemImageUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ImageHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ImageHolder(val binding: ItemImageUploadBinding) : BaseViewHolder<File>(binding) {
        override fun bind(obj: File) {
            try {
                binding.imgUpload.loadImageFromVideoFile(obj, null, dpToPx(4))
                if (obj.absolutePath.contains(".mp4")) {
                    binding.imgVideo.setVisible()
                } else {
                    binding.imgVideo.setInvisible()
                }
            } catch (e: Exception) {
                binding.imgUpload.setGone()
            }

            binding.imgClose.setOnClickListener {
                deleteItem(adapterPosition)
            }
        }
    }
}