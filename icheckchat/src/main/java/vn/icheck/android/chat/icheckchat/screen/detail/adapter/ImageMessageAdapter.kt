package vn.icheck.android.chat.icheckchat.screen.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.databinding.ItemMediaChatBinding
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.screen.detail_image.ImageDetailActivity
import java.io.File

class ImageMessageAdapter(val listData: MutableList<Any>) : RecyclerView.Adapter<ImageMessageAdapter.ImageMessageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageMessageAdapter.ImageMessageHolder {
        return ImageMessageHolder(ItemMediaChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageMessageAdapter.ImageMessageHolder, position: Int) {
        if (listData[position] is MCMedia) {
            holder.bind(listData[position] as MCMedia)
        } else {
            holder.bind(listData[position] as File)
        }
    }

    override fun getItemCount(): Int {
        return when {
            listData.size == 3 -> 2
            listData.size > 4 -> 4
            else -> listData.size
        }
    }

    inner class ImageMessageHolder(val binding: ItemMediaChatBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(obj: MCMedia) {
            loadImageUrlRounded(binding.imgMessage, obj.content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
            binding.imgView.visibleOrGone(obj.type?.contains("video") == true)

            binding.tvCountImage.apply {
                when {
                    listData.size == 3 -> {
                        if (adapterPosition == 1){
                            text = "+2"
                            setVisible()
                        }
                    }
                    listData.size > 4 -> {
                        if (adapterPosition == 3){
                            text = "+${listData.size - 3}"
                            setVisible()
                        }
                    }
                    else -> {
                        setGone()
                    }
                }
            }

            itemView.setOnClickListener {
                ImageDetailActivity.startImageDetail(itemView.context, listData as MutableList<MCMedia>, adapterPosition)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(obj: File) {
            loadImageFileRounded(binding.imgMessage, obj, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
            binding.imgView.visibleOrGone(obj.absolutePath.contains(".mp4"))

            binding.tvCountImage.apply {
                when {
                    listData.size == 3 -> {
                        if (adapterPosition == 1){
                            text = "+2"
                            setVisible()
                        }
                    }
                    listData.size > 4 -> {
                        if (adapterPosition == 3){
                            text = "+${listData.size - 3}"
                            setVisible()
                        }
                    }
                    else -> {
                        setGone()
                    }
                }
            }
        }
    }
}