package vn.icheck.android.chat.icheckchat.screen.detail.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.util.Linkify
import android.util.Patterns
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
import com.nguyencse.URLEmbeddedTask
import kotlinx.android.synthetic.main.item_receiver.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.LoadingHolder
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_LOAD_MORE
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_RECEIVER
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_SENDER
import vn.icheck.android.chat.icheckchat.databinding.ItemReceiverBinding
import vn.icheck.android.chat.icheckchat.databinding.ItemSenderBinding
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.chat.icheckchat.model.MCDetailMessage
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent
import vn.icheck.android.chat.icheckchat.model.MCStatus
import vn.icheck.android.chat.icheckchat.model.MCSticker
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.chat.icheckchat.screen.detail_image.ImageDetailActivity
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk.openActivity
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.util.beGone

class ChatSocialDetailAdapter(val callback: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<MCDetailMessage>()

    var isLoadMoreEnable = true
    var isLoading = false
    var isLoadMore = false

    fun setData(list: MutableList<MCDetailMessage>) {
        checkLoadMoreV2(list)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: MutableList<MCDetailMessage>) {
        checkLoadMoreV2(list)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(obj: MCDetailMessage) {
        if (listData.isNullOrEmpty()) {
            listData.add(obj)
            notifyDataSetChanged()
        } else {
            listData.add(0, obj)
            notifyItemInserted(0)
        }
    }

    private fun checkLoadMoreV2(list: MutableList<MCDetailMessage>) {
        list.reverse()
        if (isLoadMoreEnable) {
            isLoadMore = list.size >= NetworkHelper.LIMIT
            for (i in listData.size - 1 downTo 0) {
                if (listData[i].senderId == null) {
                    listData.removeAt(i)
                }
            }
            if (isLoadMore) {
                list.add(MCDetailMessage())
            }
            isLoading = false
        }
    }

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    val isNotEmpty: Boolean
        get() {
            return listData.isNotEmpty()
        }

    val getListData: MutableList<MCDetailMessage>
        get() {
            return listData
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_RECEIVER -> {
                ReceiverHolder(ItemReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            TYPE_LOAD_MORE -> {
                LoadingHolder(parent)
            }
            else -> {
                SenderHolder(ItemSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listData.size) {
            when {
                listData[position].senderId == null -> {
                    TYPE_LOAD_MORE
                }
                FirebaseAuth.getInstance().currentUser?.uid != listData[position].senderId -> {
                    TYPE_RECEIVER
                }
                else -> {
                    TYPE_SENDER
                }
            }
        } else {
            super.getItemViewType(position)
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
            is LoadingHolder -> {
                if (isLoadMore) {
                    if (!isLoading) {
                        isLoading = true
                        callback.onLoadMore()
                    }
                }
            }
        }
    }

    inner class SenderHolder(val binding: ItemSenderBinding) : BaseViewHolder<MCDetailMessage>(binding) {
        @SuppressLint("RtlHardcoded")
        override fun bind(obj: MCDetailMessage) {
            binding.layoutImageDetail.root.gravity = Gravity.RIGHT

            setGoneView(binding.layoutProduct, binding.tvMessage, binding.layoutImageDetail.layoutOneImage, binding.layoutImageDetail.recyclerView, binding.layoutImageDetail.imgView)

            if (!obj.content.isNullOrEmpty()) {
                if (obj.content!!.contains("http://") || obj.content!!.contains("https://")) {
                    binding.tvMessage.setGone()
                    setUpLink(binding.layoutLink, binding.tvLink, binding.imgThumbnailLink, binding.tvTitle, binding.tvLinkDescription, obj.content!!, itemView.context)
                } else {
                    binding.layoutLink.setGone()
                    setUpContent(binding.tvMessage, binding.tvTime, binding.root, obj, itemView.context)
                }
            } else {
                binding.layoutLink.setGone()
                binding.tvMessage.setGone()
            }

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

                binding.btnProductDetail.setOnClickListener {
                    ChatSdk.openActivity("product?id=${obj.product!!.productId}&barcode=${obj.product!!.barcode}")
                }

                binding.layoutProduct.setOnClickListener {
                    ChatSdk.openActivity("product?id=${obj.product!!.productId}&barcode=${obj.product!!.barcode}")
                }
            }
        }

        private fun setupStickers(obj: MCDetailMessage) {
            if (obj.sticker is String) {
                if ((obj.sticker as String).isNotEmpty()) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()
                    binding.layoutImageDetail.layoutOneImage.setBackgroundResource(0)

                    loadImageUrlRounded(binding.layoutImageDetail.img, obj.sticker as String, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
                }
            } else if (obj.sticker is MCSticker) {
                if (obj.sticker != null && !(obj.sticker as MCSticker).thumbnail.isNullOrEmpty()) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()
                    binding.layoutImageDetail.layoutOneImage.setBackgroundResource(0)

                    loadImageUrlRounded(binding.layoutImageDetail.img, (obj.sticker as MCSticker).thumbnail, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
                }
            }
        }

        private fun setupStatus(obj: MCDetailMessage) {
            when (obj.status) {
                MCStatus.SUCCESS -> {
                    binding.imgRetry.setGone()
                    binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_b4))
                    obj.timeText = convertDateTimeSvToCurrentDay(obj.time)
                    binding.tvTime.text = obj.timeText
                    binding.tvMessage.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue)
                }
                MCStatus.LOADING -> {
                    binding.imgRetry.setGone()
                    binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_b4))
                    binding.tvTime.text = itemView.context.getString(R.string.dang_gui)
//                    binding.tvMessage.setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue_opacity))
                    binding.tvMessage.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue)
                }
                else -> {
                    binding.imgRetry.setVisible()
                    binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentRed))
                    binding.tvTime.text = itemView.context.getString(R.string.loi_gui_tin_nhan)
                    binding.tvMessage.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_10_blue_opacity)
                }
            }
        }

        fun setupShowStatus(obj: MCDetailMessage) {
            if (obj.showStatus != 0) {
                binding.tvTime.setVisible()
                binding.root.setPadding(dpToPx(90), 0, dpToPx(12), dpToPx(16))
            } else {
                binding.tvTime.setGone()
                binding.root.setPadding(dpToPx(90), 0, dpToPx(12), dpToPx(2))
            }
        }

        private fun setupMediaUrl(obj: MCDetailMessage) {
            if (!obj.listMedia.isNullOrEmpty()) {

                if (obj.listMedia!!.size == 1) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()

                    loadImageUrlRounded(binding.layoutImageDetail.img, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                    binding.layoutImageDetail.imgView.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)
                } else {
                    binding.layoutImageDetail.recyclerView.setVisible()

                    val listAny = mutableListOf<Any>()

                    for (item in obj.listMedia!!) {
                        listAny.add(item)
                    }

                    binding.layoutImageDetail.recyclerView.adapter = ImageMessageAdapter(listAny)
                }
            }
        }

        private fun setupMediaFile(obj: MCDetailMessage) {
            if (!obj.listMediaFile.isNullOrEmpty()) {

                if (obj.listMediaFile!!.size == 1) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()

                    loadImageFileRounded(binding.layoutImageDetail.img, obj.listMediaFile!![0], R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                    binding.layoutImageDetail.imgView.visibleOrGone(obj.listMediaFile!![0].absolutePath.contains(".mp4"))
                } else {
                    binding.layoutImageDetail.recyclerView.setVisible()

                    val listAny = mutableListOf<Any>()

                    for (item in obj.listMediaFile!!) {
                        listAny.add(item)
                    }

                    binding.layoutImageDetail.recyclerView.adapter = ImageMessageAdapter(listAny)
                }
            }

        }

        private fun initClick(obj: MCDetailMessage) {
            binding.layoutImageDetail.layoutOneImage.setOnClickListener {
                obj.listMedia?.let { it1 -> ImageDetailActivity.startImageDetail(itemView.context, it1, 0) }
            }

            binding.root.setOnClickListener {
                if (obj.showStatus == 0) {
                    binding.tvTime.setVisible()
                    binding.root.setPadding(dpToPx(90), 0, dpToPx(12), dpToPx(10))
                    obj.showStatus = 1
                } else if (obj.showStatus == 1) {
                    binding.tvTime.setGone()
                    binding.root.setPadding(dpToPx(90), 0, dpToPx(12), dpToPx(2))
                    obj.showStatus = 0
                }
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
            setGoneView(binding.layoutProduct, binding.tvMessage, binding.layoutImageDetail.layoutOneImage, binding.layoutImageDetail.recyclerView, binding.layoutImageDetail.imgView)

            if (!obj.content.isNullOrEmpty()) {
                if (obj.content!!.contains("http://") || obj.content!!.contains("https://")) {
                    binding.tvMessage.setGone()
                    setUpLink(binding.layoutLink, binding.tvLink, binding.imgThumbnailLink, binding.tvTitle, binding.tvLinkDescription, obj.content!!, itemView.context)
                } else {
                    binding.layoutLink.setGone()
                    setUpContent(binding.tvMessage, binding.tvTime, binding.root, obj, itemView.context)
                }
            } else {
                binding.layoutLink.setGone()
                binding.tvMessage.setGone()
            }
            obj.timeText = convertDateTimeSvToCurrentDay(obj.time)
            binding.tvTime.text = obj.timeText


            setupProduct(obj)
            setupMedia(obj)
            setupSticker(obj)
            setupShowStatus(obj)
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

                binding.btnProductDetail.setOnClickListener {
                    ChatSdk.openActivity("product?id=${obj.product!!.productId}&barcode=${obj.product!!.barcode}")
                }
            }
        }

        private fun setupMedia(obj: MCDetailMessage) {
            if (!obj.listMedia.isNullOrEmpty()) {

                if (obj.listMedia!!.size == 1) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()

                    loadImageUrlRounded(binding.layoutImageDetail.img, obj.listMedia!![0].content, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))

                    binding.layoutImageDetail.imgView.visibleOrGone(obj.listMedia!![0].type?.contains("video") == true)

                } else {
                    binding.layoutImageDetail.recyclerView.setVisible()

                    val listAny = mutableListOf<Any>()

                    for (item in obj.listMedia!!) {
                        listAny.add(item)
                    }
                    binding.layoutImageDetail.recyclerView.adapter = ImageMessageAdapter(listAny)
                }
            }
        }

        private fun setupSticker(obj: MCDetailMessage) {
            if (obj.sticker is String) {
                if ((obj.sticker as String).isNotEmpty()) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()
                    binding.layoutImageDetail.layoutOneImage.setBackgroundResource(0)

                    loadImageUrlRounded(binding.layoutImageDetail.img, obj.sticker as String, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
                }
            } else if (obj.sticker is MCSticker) {
                if (obj.sticker != null && !(obj.sticker as MCSticker).thumbnail.isNullOrEmpty()) {
                    binding.layoutImageDetail.layoutOneImage.setVisible()
                    binding.layoutImageDetail.layoutOneImage.setBackgroundResource(0)

                    loadImageUrlRounded(binding.layoutImageDetail.img, (obj.sticker as MCSticker).thumbnail, R.drawable.ic_default_image_upload_150_chat, dpToPx(10))
                }
            }
        }


        fun setupShowStatus(obj: MCDetailMessage) {
            obj.timeText = convertDateTimeSvToCurrentDay(obj.time)
            binding.tvTime.text = obj.timeText

            if (obj.showStatus != 0) {
                binding.tvTime.setVisible()
                binding.root.setPadding(dpToPx(12), 0, dpToPx(55), dpToPx(10))
                binding.root.imgAvatarUser.layoutParams = LinearLayout.LayoutParams(SizeHelper.size30, SizeHelper.size30).apply {
                    this.bottomMargin = SizeHelper.size16
                }
            } else {
                binding.tvTime.setGone()
                binding.root.setPadding(dpToPx(12), 0, dpToPx(55), dpToPx(2))
                binding.root.imgAvatarUser.layoutParams = LinearLayout.LayoutParams(SizeHelper.size30, SizeHelper.size30).apply {
                    this.bottomMargin = 0
                }
            }

            if (obj.showStatus == -1) {
                binding.imgAvatarUser.setVisible()
            } else {
                binding.imgAvatarUser.setInvisible()
            }
        }

        private fun initClick(obj: MCDetailMessage) {
            binding.layoutImageDetail.root.setOnClickListener {
                obj.listMedia?.let { it1 -> ImageDetailActivity.startImageDetail(itemView.context, it1, 0) }
            }

            binding.root.setOnClickListener {
                if (obj.showStatus == 1) {
                    binding.tvTime.setGone()
                    binding.root.setPadding(dpToPx(12), 0, dpToPx(55), dpToPx(2))
                    binding.root.imgAvatarUser.layoutParams = LinearLayout.LayoutParams(SizeHelper.size30, SizeHelper.size30).apply {
                        this.bottomMargin = 0
                    }
                    obj.showStatus = 0
                } else if (obj.showStatus == 0) {
                    binding.tvTime.setVisible()
                    binding.root.setPadding(dpToPx(12), 0, dpToPx(55), dpToPx(10))
                    binding.root.imgAvatarUser.layoutParams = LinearLayout.LayoutParams(SizeHelper.size30, SizeHelper.size30).apply {
                        this.bottomMargin = SizeHelper.size16
                    }
                    obj.showStatus = 1
                }
            }

            binding.imgAvatarUser.setOnClickListener {
                if (ChatSocialDetailActivity.toType.contains("page")) {
                    openActivity("page?id=${ChatSocialDetailActivity.toId}")
                } else {
                    openActivity("user?id=${ChatSocialDetailActivity.toId}")
                }
            }
        }
    }

    private fun setUpLink(layoutLink: View, link: AppCompatTextView, image: AppCompatImageView?, title: AppCompatTextView, description: AppCompatTextView, content: String, context: Context) {
        layoutLink.apply {
            setVisible()

            setOnClickListener {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(content)))
            }

            setOnLongClickListener {
                copyText(context, content)

                true
            }
        }

        link.apply {
            text = content
            Linkify.addLinks(this, Linkify.ALL)
            setLinkTextColor(ContextCompat.getColor(context, R.color.white))
        }

        val urlTask = URLEmbeddedTask((URLEmbeddedTask.OnLoadURLListener {
            if (image == null) {
                return@OnLoadURLListener
            }
            image.visibleOrGone(!it.thumbnailURL.isNullOrEmpty())
            title.visibleOrGone(!it.title.isNullOrEmpty())
            description.visibleOrGone(!it.host.isNullOrEmpty())

            loadImageUrl(image, it.thumbnailURL, 0, 0)

            title.text = it.title
            description.text = it.host
        }))

        urlTask.execute(extractLinks(content))
    }

    private fun extractLinks(text: String): String {
        val m = Patterns.WEB_URL.matcher(text)
        var url = ""
        while (m.find()) {
            url = m.group()
        }
        return url
    }

    private fun setUpContent(tvMessage: AppCompatTextView, tvTime: AppCompatTextView, rootView: View, obj: MCDetailMessage, context: Context) {
        tvMessage.apply {
            setVisible()

            if (obj.content!!.contains("icheck://")) {
                val positionStart = obj.content!!.indexOf("icheck://")
                val positionEnd = obj.content!!.indexOf(" ", positionStart)

                val schema = if (positionEnd != -1) {
                    obj.content!!.substring(positionStart, positionEnd)
                } else {
                    obj.content!!
                }

                val content = obj.content!!.replace(schema, "<u>$schema</u>")

                text = Html.fromHtml(content)

                setOnClickListener {
                    ChatSdk.openActivity(schema)
                }
            } else {
                text = obj.content!!.replace("\r", "\n")
                paintFlags = 0

                setOnClickListener {
                    if (obj.showStatus == 0) {
                        tvTime.setVisible()
                        if (FirebaseAuth.getInstance().currentUser?.uid == obj.senderId) {
                            rootView.setPadding(dpToPx(90), 0, dpToPx(12), dpToPx(10))
                        } else {
                            rootView.setPadding(dpToPx(12), 0, dpToPx(55), dpToPx(10))
                        }
                        obj.showStatus = 1
                    } else if (obj.showStatus == 1) {
                        tvTime.beGone()
                        if (FirebaseAuth.getInstance().currentUser?.uid == obj.senderId) {
                            rootView.setPadding(dpToPx(90), 0, dpToPx(12), dpToPx(2))
                        } else {
                            rootView.setPadding(dpToPx(12), 0, dpToPx(55), dpToPx(2))
                        }
                        obj.showStatus = 0
                    }
                }
            }

            setOnLongClickListener {
                copyText(context, obj.content!!)

                true
            }
        }
    }

    private fun copyText(context: Context, text: String) {
        val myClipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val myClip = ClipData.newPlainText("note_copy", text)
        myClipboard.setPrimaryClip(myClip)
        context.showToastSuccess(context.getString(R.string.copy_success))
    }
}
