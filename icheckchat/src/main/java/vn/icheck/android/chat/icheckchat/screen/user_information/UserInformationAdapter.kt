package vn.icheck.android.chat.icheckchat.screen.user_information

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_IMAGE
import vn.icheck.android.chat.icheckchat.base.view.loadImageUrl
import vn.icheck.android.chat.icheckchat.base.view.visibleOrGone
import vn.icheck.android.chat.icheckchat.databinding.ItemImageChatBinding
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.screen.detail_image.ImageDetailActivity

class UserInformationAdapter(callback: IRecyclerViewCallback) : BaseRecyclerView<MCMedia>(callback) {

    fun setListDataMedia(list: MutableList<MCMedia>) {
        checkLoadMoreMedia(list)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListDataMedia(list: MutableList<MCMedia>) {
        checkLoadMoreMedia(list)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    private fun checkLoadMoreMedia(list: MutableList<MCMedia>) {
        isLoadMore = list.isNotEmpty()
        isLoading = false
    }

    override fun getItemType(position: Int): Int {
        return TYPE_IMAGE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageInformationViewHolder(ItemImageChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageInformationViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ImageInformationViewHolder(val binding: ItemImageChatBinding) : BaseViewHolder<MCMedia>(binding) {
        override fun bind(obj: MCMedia) {
            binding.imgBackground.visibleOrGone(obj.type?.contains("video") == true)
            binding.imgPlay.visibleOrGone(obj.type?.contains("video") == true)

            loadImageUrl(binding.imgChat, obj.content, R.drawable.ic_default_image_upload_150_chat, R.drawable.ic_default_image_upload_150_chat)

            binding.imgChat.setOnClickListener {
                ImageDetailActivity.startImageDetail(itemView.context, listData, adapterPosition)
            }
        }
    }
}