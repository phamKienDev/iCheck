package vn.icheck.android.chat.icheckchat.screen.detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_RECEIVER
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_SENDER
import vn.icheck.android.chat.icheckchat.databinding.ItemReceiverBinding
import vn.icheck.android.chat.icheckchat.databinding.ItemSenderBinding
import vn.icheck.android.chat.icheckchat.model.MCDetailMessage
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent
import vn.icheck.android.chat.icheckchat.screen.detail_image.ImageDetailActivity

class ChatSocialDetailAdapter(callback: IRecyclerViewCallback) : BaseRecyclerView<MCDetailMessage>(callback) {

    fun setData(obj: MutableList<MCDetailMessage>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (FirebaseAuth.getInstance().currentUser?.uid != listData[position].senderId) {
            TYPE_RECEIVER
        } else {
            TYPE_SENDER
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_RECEIVER) {
            ReceiverHolder(ItemReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            SenderHolder(ItemSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is SenderHolder -> {
                holder.bind(listData[position])
            }
            is ReceiverHolder -> {
                holder.bind(listData[position])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    inner class SenderHolder(val binding: ItemSenderBinding) : BaseViewHolder<MCDetailMessage>(binding) {
        @SuppressLint("RtlHardcoded")
        override fun bind(obj: MCDetailMessage) {

            binding.layoutImageDetail.root.gravity = Gravity.RIGHT

            setGoneView(binding.layoutProduct, binding.layoutImageDetail.root, binding.tvMessage, binding.layoutImageDetail.imgView, binding.layoutImageDetail.layoutOneImage, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.tvCountImage, binding.layoutImageDetail.tvCountImage1)

            binding.layoutImageDetail.root.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
                obj.listMedia?.let { it1 -> ImageDetailActivity.startImageDetail(itemView.context, it1) }
            }

            setUpContentAndLink(binding.tvMessage, obj, itemView.context)

            if (obj.product != null) {
                binding.layoutProduct.setVisible()

                loadImageUrlRounded(binding.imgProduct, obj.product?.image, R.drawable.ic_default_product_chat, dpToPx(10))

                binding.tvNameProduct.text = if (!obj.product?.name.isNullOrEmpty() && obj.product?.name?.contains("null") == false) {
                    obj.product?.name
                } else {
                    itemView.context.getString(R.string.ten_dang_cap_nhat)
                }

                checkNullOrEmpty(binding.tvBarcode, obj.product?.barcode)

                binding.tvPrice.text = if (obj.product?.price != null && obj.product?.price != -1L) {
                    formatMoney(obj.product?.price)
                } else {
                    itemView.context.getString(R.string.gia_dang_cap_nhat)
                }
            }

            if (!obj.listMedia.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                when (obj.listMedia!!.size) {
                    1 -> {
                        binding.layoutImageDetail.layoutOneImage.setVisible()

                        loadImageUrlRounded(binding.layoutImageDetail.img, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                        binding.layoutImageDetail.imgView.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)
                    }
                    2 -> {
                        setUpImage(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    3 -> {
                        setUpImage(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    else -> {
                        setUpImageElse(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.img3, binding.layoutImageDetail.imgView3, binding.layoutImageDetail.img4, binding.layoutImageDetail.imgView4, binding.layoutImageDetail.tvCountImage1)
                    }
                }
            }

            if (!obj.sticker.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                binding.layoutImageDetail.layoutOneImage.setVisible()

                loadImageUrlRounded(binding.layoutImageDetail.img, obj.sticker, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
            }

            binding.tvTime.text = convertDateTimeSvToCurrentDay(obj.time)

            binding.root.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
            }
        }
    }


    inner class ReceiverHolder(val binding: ItemReceiverBinding) : BaseViewHolder<MCDetailMessage>(binding) {
        @SuppressLint("RtlHardcoded")
        override fun bind(obj: MCDetailMessage) {
            loadImageUrl(binding.imgAvatarUser, obj.avatarSender, R.drawable.ic_user_default_52dp, R.drawable.ic_user_default_52dp)

            binding.layoutImageDetail.root.gravity = Gravity.LEFT

            setGoneView(binding.layoutProduct, binding.layoutImageDetail.root, binding.tvMessage, binding.layoutImageDetail.imgView, binding.layoutImageDetail.layoutOneImage, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.tvCountImage, binding.layoutImageDetail.tvCountImage1)

            binding.layoutImageDetail.root.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
                obj.listMedia?.let { it1 -> ImageDetailActivity.startImageDetail(itemView.context, it1) }
            }

            setUpContentAndLink(binding.tvMessage, obj, itemView.context)

            if (obj.product != null) {
                binding.layoutProduct.setVisible()

                loadImageUrlRounded(binding.imgProduct, obj.product?.image, R.drawable.ic_default_product_chat, dpToPx(10))

                binding.tvNameProduct.text = if (!obj.product?.name.isNullOrEmpty() && obj.product?.name?.contains("null") == false) {
                    obj.product?.name
                } else {
                    itemView.context.getString(R.string.ten_dang_cap_nhat)
                }

                checkNullOrEmpty(binding.tvBarcode, obj.product?.barcode)

                binding.tvPrice.text = if (obj.product?.price != null && obj.product?.price != -1L) {
                    formatMoney(obj.product?.price)
                } else {
                    itemView.context.getString(R.string.gia_dang_cap_nhat)
                }
            }

            if (!obj.listMedia.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                when (obj.listMedia!!.size) {
                    1 -> {
                        binding.layoutImageDetail.layoutOneImage.setVisible()

                        loadImageUrlRounded(binding.layoutImageDetail.img, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                        binding.layoutImageDetail.imgView.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)
                    }
                    2 -> {
                        setUpImage(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    3 -> {
                        setUpImage(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    else -> {
                        setUpImageElse(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.img3, binding.layoutImageDetail.imgView3, binding.layoutImageDetail.img4, binding.layoutImageDetail.imgView4, binding.layoutImageDetail.tvCountImage1)
                    }
                }
            }

            if (!obj.sticker.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                binding.layoutImageDetail.layoutOneImage.setVisible()

                loadImageUrlRounded(binding.layoutImageDetail.img, obj.sticker, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
            }

            binding.tvTime.text = convertDateTimeSvToCurrentDay(obj.time)

            binding.root.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
            }
        }
    }

    private fun setUpContentAndLink(view: AppCompatTextView, obj: MCDetailMessage, context: Context) {
        view.apply {
            if (!obj.content.isNullOrEmpty()) {
                setVisible()
                text = obj.content!!.replace("\r", "\n")
                paintFlags = 0
            } else {
                if (!obj.link.isNullOrEmpty()) {
                    setVisible()
                    text = obj.link
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    setOnClickListener {
                        EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(obj.link)))
                    }
                } else {
                    setGone()
                }
            }
        }
    }

    private fun setUpImage(obj: MCDetailMessage, layoutTwoImage: View, img1: AppCompatImageView, imgView1: AppCompatImageView, img2: AppCompatImageView, imgView2: AppCompatImageView, tvCountImage: AppCompatTextView) {
        layoutTwoImage.setVisible()

        loadImageUrlRounded(img1, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

        imgView1.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)

        loadImageUrlRounded(img2, obj.listMedia!![1].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

        tvCountImage.visibleOrGone(obj.listMedia!!.size == 3)

        imgView2.visibleOrGone(obj.listMedia!![1].type?.contains("video") == true && !tvCountImage.isVisible)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpImageElse(obj: MCDetailMessage, layoutTwoImage: View, layoutImage: View, img1: AppCompatImageView, imgView1: AppCompatImageView, img2: AppCompatImageView, imgView2: AppCompatImageView, img3: AppCompatImageView, imgView3: AppCompatImageView, img4: AppCompatImageView, imgView4: AppCompatImageView, tvCountImage1: AppCompatTextView) {
        layoutTwoImage.setVisible()
        layoutImage.setVisible()

        loadImageUrlRounded(img1, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

        imgView1.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)

        loadImageUrlRounded(img2, obj.listMedia!![1].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

        imgView2.visibleOrGone(obj.listMedia!![1].type?.contains("video") == true)

        loadImageUrlRounded(img3, obj.listMedia!![2].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

        imgView3.visibleOrGone(obj.listMedia!![2].type?.contains("video") == true)

        loadImageUrlRounded(img4, obj.listMedia!![3].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

        tvCountImage1.visibleOrGone(obj.listMedia!!.size > 4)

        tvCountImage1.text = "+${obj.listMedia!!.size - 3}"

        imgView4.visibleOrGone(obj.listMedia!![3].type?.contains("video") == true && !tvCountImage1.isVisible)
    }
}
