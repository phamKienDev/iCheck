package vn.icheck.android.chat.icheckchat.screen.detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
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
import vn.icheck.android.chat.icheckchat.helper.SizeHelper
import vn.icheck.android.chat.icheckchat.model.MCDetailMessage
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent
import vn.icheck.android.chat.icheckchat.model.MCStatus
import vn.icheck.android.chat.icheckchat.screen.detail_image.ImageDetailActivity
import java.io.File

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

            setUpContentAndLink(binding.tvMessage, obj, itemView.context)
            setupProduct(obj)
            if (!obj.listMedia.isNullOrEmpty()) {
                setupMediaUrl(obj)
            } else {
                if (!obj.listMediaFile.isNullOrEmpty()) {
                    setupMediaFile(obj)
                }
            }
            setupStickers(obj)
            setupShowStatus(obj)
            setupStatus(obj)
            initClick(obj)
        }

        private fun setupProduct(obj: MCDetailMessage) {
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
        }

        private fun setupStickers(obj: MCDetailMessage) {
            if (!obj.sticker.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                binding.layoutImageDetail.layoutOneImage.setVisible()

                loadImageUrlRounded(binding.layoutImageDetail.img, obj.sticker, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
            }
        }

        private fun setupStatus(obj: MCDetailMessage) {
            when (obj.status) {
                MCStatus.SUCCESS -> {
                    binding.imgRetry.setGone()
                    binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_b4))
                    obj.timeText = convertDateTimeSvToCurrentDay(obj.time)
                    binding.tvTime.text = obj.timeText
                    binding.tvMessage.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue))
                }
                MCStatus.LOADING -> {
                    binding.imgRetry.setGone()
                    binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_b4))
                    binding.tvTime.text = itemView.context.getString(R.string.dang_gui)
                    binding.tvMessage.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue_opactity))
                    binding.tvMessage.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue_opacity))
                }
                else -> {
                    binding.imgRetry.setVisible()
                    binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                    binding.tvTime.text = itemView.context.getString(R.string.loi_gui_tin_nhan)
                    binding.tvMessage.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue_opacity))
                }
            }
        }

        fun setupShowStatus(obj: MCDetailMessage) {
            if (obj.showStatus) {
                binding.tvTime.setVisible()
                binding.root.setPadding(SizeHelper.dpToPx(90), 0, SizeHelper.dpToPx(12), SizeHelper.dpToPx(16))
            } else {
                binding.tvTime.setGone()
                binding.root.setPadding(SizeHelper.dpToPx(90), 0, SizeHelper.dpToPx(12), SizeHelper.dpToPx(3))
            }
        }

        private fun setupMediaUrl(obj: MCDetailMessage) {
            if (!obj.listMedia.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                when (obj.listMedia!!.size) {
                    1 -> {
                        binding.layoutImageDetail.layoutOneImage.setVisible()

                        loadImageUrlRounded(binding.layoutImageDetail.img, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                        binding.layoutImageDetail.imgView.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)
                    }
                    2 -> {
                        setUpImageUrl(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    3 -> {
                        setUpImageUrl(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    else -> {
                        setUpImageElseUrl(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.img3, binding.layoutImageDetail.imgView3, binding.layoutImageDetail.img4, binding.layoutImageDetail.imgView4, binding.layoutImageDetail.tvCountImage1)
                    }
                }
            }
        }

        private fun setupMediaFile(obj: MCDetailMessage) {
            if (!obj.listMediaFile.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                when (obj.listMediaFile!!.size) {
                    1 -> {
                        binding.layoutImageDetail.layoutOneImage.setVisible()

                        loadImageFileRounded(binding.layoutImageDetail.img, obj.listMediaFile!![0], R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                        binding.layoutImageDetail.imgView.visibleOrGone(obj.listMediaFile!![0].absolutePath.contains(".mp4"))
                    }
                    2 -> {
                        setUpImageFile(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    3 -> {
                        setUpImageFile(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    else -> {
                        setUpImageElseUrlFile(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.img3, binding.layoutImageDetail.imgView3, binding.layoutImageDetail.img4, binding.layoutImageDetail.imgView4, binding.layoutImageDetail.tvCountImage1)
                    }
                }
            }

        }

        private fun initClick(obj: MCDetailMessage) {
            binding.layoutImageDetail.root.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
                obj.listMedia?.let { it1 -> ImageDetailActivity.startImageDetail(itemView.context, it1) }
            }

            binding.root.setOnClickListener {

            }

            binding.imgRetry.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.SEND_RETRY_CHAT, obj))
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
                        setUpImageUrl(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    3 -> {
                        setUpImageUrl(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.tvCountImage)
                    }
                    else -> {
                        setUpImageElseUrl(obj, binding.layoutImageDetail.layoutTwoImage, binding.layoutImageDetail.layoutImage, binding.layoutImageDetail.img1, binding.layoutImageDetail.imgView1, binding.layoutImageDetail.img2, binding.layoutImageDetail.imgView2, binding.layoutImageDetail.img3, binding.layoutImageDetail.imgView3, binding.layoutImageDetail.img4, binding.layoutImageDetail.imgView4, binding.layoutImageDetail.tvCountImage1)
                    }
                }
            }

            if (!obj.sticker.isNullOrEmpty()) {
                binding.layoutImageDetail.root.setVisible()

                binding.layoutImageDetail.layoutOneImage.setVisible()

                binding.layoutImageDetail.layoutOneImage.setBackgroundResource(0)

                loadImageUrlRounded(binding.layoutImageDetail.img, obj.sticker, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
            }

            setupShowStatus(obj)

            binding.root.setOnClickListener {
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.HIDE_KEYBOARD))
            }
        }

        fun setupShowStatus(obj: MCDetailMessage) {
            if (obj.showStatus) {
                binding.tvTime.setVisible()
                obj.timeText = convertDateTimeSvToCurrentDay(obj.time)
                binding.tvTime.text = obj.timeText
                binding.root.setPadding(SizeHelper.dpToPx(12), 0, SizeHelper.dpToPx(55), SizeHelper.dpToPx(16))
            } else {
                binding.tvTime.setGone()
                binding.root.setPadding(SizeHelper.dpToPx(12), 0, SizeHelper.dpToPx(55), SizeHelper.dpToPx(3))
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

                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(obj.link)))
                    }
                } else {
                    setGone()
                }
            }
        }
    }

    private fun setUpImageUrl(obj: MCDetailMessage, layoutTwoImage: View, img1: AppCompatImageView, iconVideo1: AppCompatImageView, img2: AppCompatImageView, iconVideo2: AppCompatImageView, tvCountImage: AppCompatTextView) {
        layoutTwoImage.setVisible()

        loadImageUrl(img1, iconVideo1, obj.listMedia!![0])
        loadImageUrl(img2, iconVideo2, obj.listMedia!![1])

        tvCountImage.visibleOrGone(obj.listMedia!!.size == 3)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpImageElseUrl(obj: MCDetailMessage, layoutTwoImage: View, layoutImage: View, img1: AppCompatImageView, iconVideo1: AppCompatImageView, img2: AppCompatImageView, iconVideo2: AppCompatImageView, img3: AppCompatImageView, iconVideo3: AppCompatImageView, img4: AppCompatImageView, iconVideo4: AppCompatImageView, tvCountImage1: AppCompatTextView) {
        layoutTwoImage.setVisible()
        layoutImage.setVisible()

        loadImageUrl(img1, iconVideo1, obj.listMedia!![0])
        loadImageUrl(img2, iconVideo2, obj.listMedia!![1])
        loadImageUrl(img3, iconVideo3, obj.listMedia!![2])
        loadImageUrl(img4, iconVideo4, obj.listMedia!![3])

        tvCountImage1.visibleOrGone(obj.listMedia!!.size > 4)
        tvCountImage1.text = "+${obj.listMedia!!.size - 3}"
    }

    private fun setUpImageFile(obj: MCDetailMessage, layoutTwoImage: View, img1: AppCompatImageView, iconVideo1: AppCompatImageView, img2: AppCompatImageView, iconVideo2: AppCompatImageView, tvCountImage: AppCompatTextView) {
        layoutTwoImage.setVisible()

        loadImageFile(img1, iconVideo1, obj.listMediaFile!![0])
        loadImageFile(img2, iconVideo2, obj.listMediaFile!![1])

        tvCountImage.visibleOrGone(obj.listMediaFile!!.size == 3)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpImageElseUrlFile(obj: MCDetailMessage, layoutTwoImage: View, layoutImage: View, img1: AppCompatImageView, iconVideo1: AppCompatImageView, img2: AppCompatImageView, iconVideo2: AppCompatImageView, img3: AppCompatImageView, iconVideo3: AppCompatImageView, img4: AppCompatImageView, iconVideo4: AppCompatImageView, tvCountImage1: AppCompatTextView) {
        layoutTwoImage.setVisible()
        layoutImage.setVisible()

        loadImageFile(img1, iconVideo1, obj.listMediaFile!![0])
        loadImageFile(img2, iconVideo2, obj.listMediaFile!![1])
        loadImageFile(img3, iconVideo3, obj.listMediaFile!![2])
        loadImageFile(img4, iconVideo4, obj.listMediaFile!![3])

        tvCountImage1.visibleOrGone(obj.listMediaFile!!.size > 4)
        tvCountImage1.text = "+${obj.listMediaFile!!.size - 3}"
    }

    private fun loadImageUrl(imageView: AppCompatImageView, iconVideo: AppCompatImageView, media: MCMedia) {
        loadImageUrlRounded(imageView, media.content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
        iconVideo.visibleOrGone(media.type?.contains("video") == true)
    }

    private fun loadImageFile(imageView: AppCompatImageView, iconVideo: AppCompatImageView, media: File) {
        loadImageFileRounded(imageView, media, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
        iconVideo.visibleOrGone(media.absolutePath.contains(".mp4"))
    }
}
