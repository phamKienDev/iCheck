package vn.icheck.android.activities.chat.v2

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.incoming_image.view.*
import kotlinx.android.synthetic.main.incoming_text_holder.view.*
import kotlinx.android.synthetic.main.outgoing_image.view.*
import kotlinx.android.synthetic.main.outgoing_text_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.activities.chat.v2.model.ChatMsgType
import vn.icheck.android.activities.chat.v2.model.ICChatMessage
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.DimensionUtil
import vn.icheck.android.util.text.ICheckTextUtils
import vn.icheck.android.util.text.MessageTimeUtil
import java.util.regex.Pattern


class ChatPagedAdapter : PagedListAdapter<ICChatMessage, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ICChatMessage>() {
            override fun areItemsTheSame(oldItem: ICChatMessage, newItem: ICChatMessage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ICChatMessage, newItem: ICChatMessage): Boolean {
                return oldItem.id == newItem.id
            }
        }
        private val handler = Handler()
        private val regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        private val regexHttps = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        fun findUrl(input: String, reg: String): String {

            val links = arrayListOf<String>()

            val p = Pattern.compile(reg);
            val m = p.matcher(input);
            while (m.find()) {
                var urlStr = m.group();

                if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                    urlStr = urlStr.substring(1, urlStr.length - 1);
                }

                links.add(urlStr);
            }
            return if (links.isNotEmpty()) {
                links[0]
            } else {
                ""
            }
        }

        fun onTouchListener(event: MotionEvent, runnable: Runnable): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(runnable, 1000)
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(runnable)
                }
                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(runnable)
                }
            }
            return false
        }

        fun resizeImage(resource: Bitmap, view: ImageView) {
//                val w = resource.width
//  val h = resource.height
//  val dp = DimensionUtil.convertDpToPixel(80f, view.context)
//  val big = if (w < h) h else w
//  val scale = dp / big
//  val resizeBitmap = if (scale > 1.0) Bitmap.createScaledBitmap(
//          resource, w * scale.toInt(), h * scale.toInt(), false
//  ) else resource
//  getImg(R.id.tv_image).setImageBitmap(resizeBitmap)

            val scale = if (resource.width > resource.height) {
                resource.width / 1024
            } else {
                resource.height / 1024
            }

            var dstWidth = resource.width
            var dstHeight = resource.height

            if (scale >= 1) {
                dstWidth /= scale
                dstHeight /= scale
            }
            val resizeBitmap = if (scale > 1) {
                Bitmap.createScaledBitmap(resource, dstWidth, dstHeight, false)
            } else {
                resource
            }
            view.setImageBitmap(resizeBitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ChatMsgType.TYPE_INCOMING_TEXT -> IncomingTextHolder.create(parent)
            ChatMsgType.TYPE_OUTGOING_TEXT -> OutgoingTextHolder.create(parent)
            ChatMsgType.TYPE_INCOMING_IMG -> IncomingImageHolder.create(parent)
            ChatMsgType.TYPE_OUTGOING_IMG -> OutgoingImageHolder.create(parent)
            ChatMsgType.TYPE_INCOMING_PRODUCT -> IncomingProductHolder.create(parent)
            ChatMsgType.TYPE_OUTGOING_PRODUCTT -> OutgoingProductHolder.create(parent)
            ChatMsgType.TYPE_CHAT_BOT_HEADER -> ChatBotHeader.create(parent)
            ChatMsgType.TYPE_CHAT_BOT_QA -> ChatBotHolder.create(parent)
            ChatMsgType.TYPE_SYSTEM_MSG -> SystemMessageHolder.create(parent)
            ChatMsgType.TYPE_FIRST_BOT -> FirstChatBotHolder.create(parent)
            else -> NullHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ChatMsgType.TYPE_INCOMING_TEXT -> {
                (holder as IncomingTextHolder).bind(getItem(position))
            }
            ChatMsgType.TYPE_OUTGOING_TEXT -> (holder as OutgoingTextHolder).bind(getItem(position))
            ChatMsgType.TYPE_INCOMING_IMG -> (holder as IncomingImageHolder).bind(getItem(position))
            ChatMsgType.TYPE_OUTGOING_IMG -> (holder as OutgoingImageHolder).bind(getItem(position))
            ChatMsgType.TYPE_INCOMING_PRODUCT -> (holder as IncomingProductHolder).bind(getItem(position))
            ChatMsgType.TYPE_OUTGOING_PRODUCTT -> (holder as OutgoingProductHolder).bind(getItem(position))
            ChatMsgType.TYPE_CHAT_BOT_HEADER -> (holder as ChatBotHeader).bind(getItem(position))
            ChatMsgType.TYPE_CHAT_BOT_QA -> (holder as ChatBotHolder).bind(getItem(position))
            ChatMsgType.TYPE_SYSTEM_MSG -> (holder as SystemMessageHolder).bind(getItem(position))
            ChatMsgType.TYPE_FIRST_BOT -> (holder as FirstChatBotHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) != null) {
            getItem(position)!!.chatMsgType
        } else {
            ChatMsgType.TYPE_ERROR
        }
    }

    class SystemMessageHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                getTv(R.id.tv_system_msg).text = it.textMessage
            }
        }

        companion object {
            fun create(parent: ViewGroup): SystemMessageHolder {
                return SystemMessageHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.system_message_holder, parent, false))
            }
        }
    }

    class FirstChatBotHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?){
            icChatMessage?.let {
                getTv(R.id.tv_content).text = it.textMessage
                if (it.showAvatar) {
                    getTv(R.id.tv_time_outgoing).visibility = View.VISIBLE
                    getTv(R.id.tv_time_outgoing).text = MessageTimeUtil(it.sentTime).getTime()
                } else {
                    getTv(R.id.tv_time_outgoing).visibility = View.GONE
                }
                if (it.productPrice != null && it.productPrice != 0L) {
                    ICheckTextUtils.setPrice(getTv(R.id.tv_price), it.productPrice!!)
                }
                if (it.productPrice != null && it.productPrice != 0L) {
                    ICheckTextUtils.setPrice(getTv(R.id.tv_price), it.productPrice!!)
                }
                getTv(R.id.tv_name).text = it.productName
                Glide.with(view.context.applicationContext)
                        .load(it.productImg)
                        .error(R.drawable.error_load_image)
                        .into(getImg(R.id.imgProduct))
                setOnClick(R.id.tv_msg, View.OnClickListener { _ ->
                    if (!it.productBarcode.isNullOrEmpty()) {
                        ChatV2Activity.instance?.showProduct(it.productBarcode!!)
                    }
                })
                val tvMsg = itemView.findViewById<ViewGroup>(R.id.tv_msg)
                val runnableShowDialog = Runnable {
                    ChatV2Activity.instance?.showDialogCopyText(tvMsg, R.menu.menu_download_image_text, getTv(R.id.tv_name).text.toString(), "", getImg(R.id.imgProduct))
                }

                tvMsg.setOnTouchListener { v, event ->
                    onTouchListener(event, runnableShowDialog)
                }
            }
        }
        companion object{
            fun create(parent: ViewGroup):FirstChatBotHolder {
                return FirstChatBotHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.first_bot_holder, parent, false))
            }
        }
    }

    class ChatBotHeader(view: View) : BaseHolder(view) {

        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                getTv(R.id.tv_business_name).text = it.productName
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getTv(R.id.tv_first).text = Html.fromHtml(it.textMessage, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    getTv(R.id.tv_first).text = Html.fromHtml(it.textMessage)
                }
                if (!it.userSentAvatar.isNullOrEmpty()) {
                    Glide.with(view.context.applicationContext)
                            .load(it.userSentAvatar)
                            .error(R.drawable.user_placeholder)
                            .placeholder(R.drawable.user_placeholder)
                            .into(getImg(R.id.user_avatar))
                    setOnClick(R.id.user_avatar, View.OnClickListener { _ ->
                        ChatV2Activity.instance?.showUser(it.sendByUser, it.userType)
                    })
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ChatBotHeader {
                return ChatBotHeader(LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_bot_header, parent, false))
            }
        }
    }

    class ChatBotHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                getTv(R.id.tv_second).text = it.textMessage
                setOnClick(R.id.tv_second, View.OnClickListener { _ ->
                    ChatV2Activity.instance?.sendChatBot(it)
                })
                if (!it.imageMsg.isNullOrEmpty()) {
                    showView(R.id.tv_business_name)
                    showView(R.id.tv_first)
                    showView(R.id.user_avatar)
                    showView(R.id.img_verified)
                    getTv(R.id.tv_business_name).text = it.productName
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        getTv(R.id.tv_first).text = Html.fromHtml(it.imageMsg, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        getTv(R.id.tv_first).text = Html.fromHtml(it.imageMsg)
                    }
                    if (!it.userSentAvatar.isNullOrEmpty()) {
                        Glide.with(view.context.applicationContext)
                                .load(it.userSentAvatar)
                                .error(R.drawable.user_placeholder)
                                .placeholder(R.drawable.user_placeholder)
                                .into(getImg(R.id.user_avatar))
                        setOnClick(R.id.user_avatar, View.OnClickListener { _ ->
                            ChatV2Activity.instance?.showUser(it.sendByUser, it.userType)
                        })
                    }
                } else {
                    hideView(R.id.tv_business_name)
                    hideView(R.id.tv_first)
                    inviView(R.id.user_avatar)
                    hideView(R.id.img_verified)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ChatBotHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.chat_bot_header, parent, false)
                return ChatBotHolder(view)
            }
        }
    }

    class OutgoingTextHolder(view: View) : BaseHolder(view) {
        fun hideTextTime() {
            if (getTv(R.id.tv_time_outgoing).visibility == View.VISIBLE) {
                getTv(R.id.tv_time_outgoing).visibility = View.GONE
            }
        }

        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                if (it.stateSendMessage == 1) {
                    getVg(R.id.root).alpha = 0.5f
                    getTv(R.id.tv_retry).visibility = View.GONE
                    getTv(R.id.tv_time_outgoing).visibility = View.GONE
                }else if (it.stateSendMessage == 2) {
                    getVg(R.id.root).alpha = 1f
                    getTv(R.id.tv_retry).visibility = View.GONE
                    getTv(R.id.tv_time_outgoing).visibility = View.VISIBLE
                    if (it.showAvatar) {
                        getTv(R.id.tv_time_outgoing).visibility = View.VISIBLE
                        setOnClick(R.id.tv_msg, View.OnClickListener {
                            ChatV2Activity.instance?.hideTime(ChatV2Activity.positionShowTime)
                            ChatV2Activity.positionShowTime = -1
                        })
                    } else {
                        getTv(R.id.tv_time_outgoing).visibility = View.GONE
                        setOnClick(R.id.tv_msg, View.OnClickListener {
                            if (getTv(R.id.tv_time_outgoing).visibility == View.GONE) {
                                ChatV2Activity.instance?.hideTime(ChatV2Activity.positionShowTime)
                                ChatV2Activity.positionShowTime = adapterPosition
                                getTv(R.id.tv_time_outgoing).visibility = View.VISIBLE
                            } else {
                                ChatV2Activity.positionShowTime=-1
                                getTv(R.id.tv_time_outgoing).visibility = View.GONE
                            }
                        })
                    }
                } else {
                    getVg(R.id.root).alpha = 1f
                    getTv(R.id.tv_retry).visibility = View.VISIBLE
                    getTv(R.id.tv_time_outgoing).visibility = View.GONE
                    setOnClick(R.id.tv_retry, View.OnClickListener {
                        ChatV2Activity.instance?.retry(icChatMessage)
                    })
                }
                getTv(R.id.tv_msg).text = it.textMessage
                getTv(R.id.tv_time_outgoing).text = MessageTimeUtil(it.sentTime).getTime()

            }

            val mess = getTv(R.id.tv_msg).text.toString().trim()
            val runnableShowDialog = Runnable {
                when {
                    mess.contains("https://") -> {
                        if (findUrl(mess, regexHttps).isNotEmpty()) {
                            if (mess.length == findUrl(mess, regexHttps).length) {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy_link, mess, findUrl(mess, regexHttps), null)
                            } else {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy, mess, findUrl(mess, regexHttps), null)
                            }
                        } else {
                            ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy_text, mess, findUrl(mess, regexHttps), null)
                        }
                    }
                    mess.contains("http://") -> {
                        if (findUrl(mess, regex).isNotEmpty()) {
                            if (mess.length == findUrl(mess, regex).length) {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy_link, mess, findUrl(mess, regex), null)
                            } else {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy, mess, findUrl(mess, regex), null)
                            }
                        } else {
                            ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy_text, mess, findUrl(mess, regex), null)
                        }
                    }
                    else -> {
                        ChatV2Activity.instance?.showDialogCopyText(itemView.tv_msg, R.menu.menu_copy_text, mess, "", null)
                    }
                }
            }
            itemView.tv_msg.setOnTouchListener { v, event ->
                onTouchListener(event, runnableShowDialog)
            }
        }


        companion object {
            fun create(parent: ViewGroup): OutgoingTextHolder {
                return OutgoingTextHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.outgoing_text_holder, parent, false))
            }
        }


    }

    class IncomingTextHolder(view: View) : BaseHolder(view) {
        fun hideTextTime() {
            if (getTv(R.id.tv_time).visibility == View.VISIBLE) {
                getTv(R.id.tv_time).visibility = View.GONE
            }
        }

        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                getTv(R.id.tv_message).text = it.textMessage
                getTv(R.id.tv_time).text = MessageTimeUtil(it.sentTime).getTime()
                if (it.showAvatar) {
                    getImg(R.id.user_avatar).visibility = View.VISIBLE
                    getTv(R.id.tv_time).visibility = View.VISIBLE
                    if (!it.userSentAvatar.isNullOrEmpty()) {
                        when (it.userType) {
                            "shop" -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.img_default_shop_logo)
                                        .placeholder(R.drawable.img_default_shop_logo)
                                        .into(getImg(R.id.user_avatar))
                            }
                            "page" -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.img_default_business_logo)
                                        .placeholder(R.drawable.img_default_business_logo)
                                        .into(getImg(R.id.user_avatar))
                            }
                            else -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.user_placeholder)
                                        .placeholder(R.drawable.user_placeholder)
                                        .into(getImg(R.id.user_avatar))
                            }
                        }

                        setOnClick(R.id.user_avatar, View.OnClickListener { _ ->
                            ChatV2Activity.instance?.showUser(it.sendByUser, it.userType)
                        })
                    } else {
                        when (it.userType) {
                            "page" -> getImg(R.id.user_avatar).setImageResource(R.drawable.img_default_business_logo)
                            "shop" -> getImg(R.id.user_avatar).setImageResource(R.drawable.img_default_shop_logo)
                        }
                    }
                    setOnClick(R.id.tv_message, View.OnClickListener {
                        ChatV2Activity.instance?.hideTime(ChatV2Activity.positionShowTime)
                        ChatV2Activity.positionShowTime = -1

                    })
                } else {
                    setOnClick(R.id.tv_message, View.OnClickListener {
                        if (getTv(R.id.tv_time).visibility == View.GONE) {
                            ChatV2Activity.instance?.hideTime(ChatV2Activity.positionShowTime)
                            ChatV2Activity.positionShowTime = adapterPosition
                            getTv(R.id.tv_time).visibility = View.VISIBLE
                        } else {
                            ChatV2Activity.positionShowTime=-1
                            getTv(R.id.tv_time).visibility = View.GONE
                        }
                    })
                    getImg(R.id.user_avatar).visibility = View.INVISIBLE
                    getTv(R.id.tv_time).visibility = View.GONE
                }

            }
            val mess = getTv(R.id.tv_message).text.toString().trim()
            val runnableShowDialog = Runnable {
                when {
                    mess.contains("https://") -> {
                        if (findUrl(mess, regexHttps).isNotEmpty()) {
                            if (mess.length == findUrl(mess, regexHttps).length) {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy_link, mess, findUrl(mess, regexHttps), null)
                            } else {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy, mess, findUrl(mess, regexHttps), null)
                            }
                        } else {
                            ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy_text, mess, "", null)
                        }
                    }
                    mess.contains("http://") -> {
                        if (findUrl(mess, regex).isNotEmpty()) {
                            if (mess.length == findUrl(mess, regex).length) {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy_link, mess, findUrl(mess, regex), null)
                            } else {
                                ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy, mess, findUrl(mess, regex), null)
                            }
                        } else {
                            ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy_text, mess, "", null)
                        }
                    }
                    else -> {
                        ChatV2Activity.instance?.showDialogCopyText(itemView.tv_message, R.menu.menu_copy_text, mess, "", null)
                    }
                }
            }

            itemView.tv_message.setOnTouchListener { v, event ->
                onTouchListener(event, runnableShowDialog)
            }
        }

        companion object {
            fun create(parent: ViewGroup): IncomingTextHolder {
                return IncomingTextHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.incoming_text_holder, parent, false))
            }
        }
    }

    class IncomingImageHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                if (it.showAvatar) {
                    getImg(R.id.user_avatar).visibility = View.VISIBLE
                    getTv(R.id.tv_time).visibility = View.VISIBLE
                    getTv(R.id.tv_time).text = MessageTimeUtil(it.sentTime).getTime()
                    if (!it.userSentAvatar.isNullOrEmpty()) {
                        when (it.userType) {
                            "shop" -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.img_default_shop_logo)
                                        .placeholder(R.drawable.img_default_shop_logo)
                                        .into(getImg(R.id.user_avatar))
                            }
                            "page" -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.img_default_business_logo)
                                        .placeholder(R.drawable.img_default_business_logo)
                                        .into(getImg(R.id.user_avatar))
                            }
                            else -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.user_placeholder)
                                        .placeholder(R.drawable.user_placeholder)
                                        .into(getImg(R.id.user_avatar))
                            }
                        }
                        setOnClick(R.id.user_avatar, View.OnClickListener { _ ->
                            ChatV2Activity.instance?.showUser(it.sendByUser, it.userType)
                        })
                    }
                } else {
                    getImg(R.id.user_avatar).visibility = View.INVISIBLE
                    when (it.userType) {
                        "page" -> getImg(R.id.user_avatar).setImageResource(R.drawable.img_default_business_logo)
                        "shop" -> getImg(R.id.user_avatar).setImageResource(R.drawable.img_default_shop_logo)
                    }
                    getTv(R.id.tv_time).visibility = View.GONE
                }
                if (!it.imageMsg.isNullOrEmpty()) {
                    if (URLUtil.isValidUrl(it.imageMsg)) {
                        Glide.with(view.context.applicationContext)
                                .asBitmap()
                                .load(it.imageMsg)
                                .apply(RequestOptions().transform(
                                        RoundedCornersTransformation(view.context, DimensionUtil.convertDpToPixel(10f, view.context).toInt(), 0, RoundedCornersTransformation.CornerType.RIGHT)
                                ))
                                .error(R.drawable.error_load_image)
                                .placeholder(R.drawable.error_load_image)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        resizeImage(resource, getImg(R.id.tv_image))
                                    }
                                })
                    } else {
                        Glide.with(view.context.applicationContext)
                                .asBitmap()
                                .load(ImageHelper.getImageUrl(it.imageMsg, ImageHelper.thumbSmallSize))
                                .apply(RequestOptions().transform(
                                        RoundedCornersTransformation(view.context, DimensionUtil.convertDpToPixel(10f, view.context).toInt(), 0, RoundedCornersTransformation.CornerType.RIGHT)
                                ))
                                .error(R.drawable.error_load_image)
                                .placeholder(R.drawable.error_load_image)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        resizeImage(resource, getImg(R.id.tv_image))
                                    }
                                })
                    }
                    setOnClick(R.id.tv_image, View.OnClickListener { _ ->
                        ChatV2Activity.instance?.showImage(it.imageMsg!!)
                    })

                    val runnableShowDialog = Runnable {
                        ChatV2Activity.instance?.showDialogCopyText(itemView.tv_image, R.menu.menu_download_image, "", "", itemView.tv_image)
                    }
                    itemView.tv_image.setOnTouchListener { v, event ->
                        onTouchListener(event, runnableShowDialog)
                    }
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): IncomingImageHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.incoming_image, parent, false)
                return IncomingImageHolder(view)
            }
        }
    }

    class OutgoingImageHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                if (it.showAvatar) {
                    getTv(R.id.tv_time_outgoing).visibility = View.VISIBLE
                    getTv(R.id.tv_time_outgoing).text = MessageTimeUtil(it.sentTime).getTime()
                } else {
                    getTv(R.id.tv_time_outgoing).visibility = View.GONE
                }
                if (!it.imageMsg.isNullOrEmpty()) {
                    if (URLUtil.isValidUrl(it.imageMsg)) {
                        Glide.with(getImg(R.id.tv_img).context.applicationContext)
                                .asBitmap()
                                .load(it.imageMsg)
                                .apply(RequestOptions().transform(
                                        RoundedCornersTransformation(view.context, DimensionUtil.convertDpToPixel(10f, view.context).toInt(), 0, RoundedCornersTransformation.CornerType.LEFT)
                                ))
                                .error(R.drawable.error_load_image)
                                .placeholder(R.drawable.error_load_image)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        resizeImage(resource, getImg(R.id.tv_img))
                                    }
                                })
                    } else {
                        Glide.with(getImg(R.id.tv_img).context.applicationContext)
                                .asBitmap()
                                .load(ImageHelper.getImageUrl(it.imageMsg, ImageHelper.thumbSmallSize))
                                .apply(RequestOptions().transform(
                                        RoundedCornersTransformation(view.context, DimensionUtil.convertDpToPixel(10f, view.context).toInt(), 0, RoundedCornersTransformation.CornerType.LEFT)
                                ))
                                .error(R.drawable.error_load_image)
                                .placeholder(R.drawable.error_load_image)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }

                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        resizeImage(resource, getImg(R.id.tv_img))
                                    }
                                })
                    }
                    setOnClick(R.id.tv_img, View.OnClickListener { _ ->
                        ChatV2Activity.instance?.showImage(it.imageMsg!!)
                    })
                    itemView.tv_img.setOnClickListener {
                        ChatV2Activity.instance?.showImage(icChatMessage.imageMsg!!)
                    }
                    val runnableShowDialog = Runnable {
                        ChatV2Activity.instance?.showDialogCopyText(itemView.tv_img, R.menu.menu_download_image, "", "", itemView.tv_img)
                    }
                    itemView.tv_img.setOnTouchListener { v, event ->
                        onTouchListener(event, runnableShowDialog)
                    }

                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): OutgoingImageHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.outgoing_image, parent, false)
                return OutgoingImageHolder(view)
            }
        }
    }

    class IncomingProductHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                if (it.showAvatar) {
                    getImg(R.id.user_avatar).visibility = View.VISIBLE
                    getTv(R.id.tv_time).visibility = View.VISIBLE
                    getTv(R.id.tv_time).text = MessageTimeUtil(it.sentTime).getTime()
                    if (!it.userSentAvatar.isNullOrEmpty()) {
                        when (it.userType) {
                            "shop" -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.img_default_shop_logo)
                                        .placeholder(R.drawable.img_default_shop_logo)
                                        .into(getImg(R.id.user_avatar))
                            }
                            "page" -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.img_default_business_logo)
                                        .placeholder(R.drawable.img_default_business_logo)
                                        .into(getImg(R.id.user_avatar))
                            }
                            else -> {
                                Glide.with(view.context.applicationContext)
                                        .load(it.userSentAvatar)
                                        .error(R.drawable.user_placeholder)
                                        .placeholder(R.drawable.user_placeholder)
                                        .into(getImg(R.id.user_avatar))
                            }
                        }
                        setOnClick(R.id.user_avatar, View.OnClickListener { _ ->
                            ChatV2Activity.instance?.showUser(it.sendByUser, it.userType)
                        })
                    }
                } else {
                    getImg(R.id.user_avatar).visibility = View.INVISIBLE
                    when (it.userType) {
                        "page" -> getImg(R.id.user_avatar).setImageResource(R.drawable.img_default_business_logo)
                        "shop" -> getImg(R.id.user_avatar).setImageResource(R.drawable.img_default_shop_logo)
                    }
                    getTv(R.id.tv_time).visibility = View.GONE
                }
                if (it.productPrice != null && it.productPrice != 0L) {
                    ICheckTextUtils.setPrice(getTv(R.id.tv_price), it.productPrice!!)
                }
                getTv(R.id.tv_name).text = it.productName
                Glide.with(view.context.applicationContext)
                        .load(it.productImg)
                        .error(R.drawable.error_load_image)
                        .into(getImg(R.id.imgProduct))
                setOnClick(R.id.tv_message, View.OnClickListener { _ ->
                    if (!it.productBarcode.isNullOrEmpty()) {
                        ChatV2Activity.instance?.showProduct(it.productBarcode!!)
                    }
                })
                val tvMessage = itemView.findViewById<ConstraintLayout>(R.id.tv_message)
                val runnableShowDialog = Runnable {
                    ChatV2Activity.instance?.showDialogCopyText(tvMessage, R.menu.menu_download_image_text, getTv(R.id.tv_name).text.toString(), "", getImg(R.id.imgProduct))
                }
                tvMessage.setOnTouchListener { v, event ->
                    onTouchListener(event, runnableShowDialog)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): IncomingProductHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.incoming_product_holder, parent, false)
                return IncomingProductHolder(view)
            }
        }
    }

    class OutgoingProductHolder(view: View) : BaseHolder(view) {
        fun bind(icChatMessage: ICChatMessage?) {
            icChatMessage?.let {
                if (it.showAvatar) {
                    getTv(R.id.tv_time_outgoing).visibility = View.VISIBLE
                    getTv(R.id.tv_time_outgoing).text = MessageTimeUtil(it.sentTime).getTime()
                } else {
                    getTv(R.id.tv_time_outgoing).visibility = View.GONE
                }
                if (it.productPrice != null && it.productPrice != 0L) {
                    ICheckTextUtils.setPrice(getTv(R.id.tv_price), it.productPrice!!)
                }
                if (it.productPrice != null && it.productPrice != 0L) {
                    ICheckTextUtils.setPrice(getTv(R.id.tv_price), it.productPrice!!)
                }
                getTv(R.id.tv_name).text = it.productName
                Glide.with(view.context.applicationContext)
                        .load(it.productImg)
                        .error(R.drawable.error_load_image)
                        .into(getImg(R.id.imgProduct))
                setOnClick(R.id.tv_msg, View.OnClickListener { _ ->
                    if (!it.productBarcode.isNullOrEmpty()) {
                        ChatV2Activity.instance?.showProduct(it.productBarcode!!)
                    }
                })
                val tvMsg = itemView.findViewById<ConstraintLayout>(R.id.tv_msg)
                val runnableShowDialog = Runnable {
                    ChatV2Activity.instance?.showDialogCopyText(tvMsg, R.menu.menu_download_image_text, getTv(R.id.tv_name).text.toString(), "", getImg(R.id.imgProduct))
                }

                tvMsg.setOnTouchListener { v, event ->
                    onTouchListener(event, runnableShowDialog)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): OutgoingProductHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.outgoing_product_holder, parent, false)
                return OutgoingProductHolder(view)
            }
        }
    }

    class NullHolder(view: View) : BaseHolder(view) {
        companion object {
            fun create(parent: ViewGroup): NullHolder {
                val view = View(parent.context)
                return NullHolder(view)
            }
        }
    }


}