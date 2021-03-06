package vn.icheck.android.chat.icheckchat.screen.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_social_detail.*
import kotlinx.android.synthetic.main.item_sender.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseActivityChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.BARCODE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_1
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_2
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_3
import vn.icheck.android.chat.icheckchat.base.ConstantChat.KEY
import vn.icheck.android.chat.icheckchat.base.ConstantChat.NAME
import vn.icheck.android.chat.icheckchat.base.ConstantChat.QR_CODE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.SCAN
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.base.view.ConfirmDialogListener
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_PACKAGE
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_STICKER
import vn.icheck.android.chat.icheckchat.databinding.ActivityChatSocialDetailBinding
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.chat.icheckchat.helper.PermissionChatHelper
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.*
import vn.icheck.android.chat.icheckchat.screen.detail.adapter.ChatSocialDetailAdapter
import vn.icheck.android.chat.icheckchat.screen.detail.adapter.ImageAdapter
import vn.icheck.android.chat.icheckchat.screen.detail.adapter.StickerAdapter
import vn.icheck.android.chat.icheckchat.screen.user_information.UserInformationActivity
import vn.icheck.android.ichecklibs.*
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.icheckscanditv6.IcheckScanActivity
import java.io.File
import java.util.regex.Pattern

class ChatSocialDetailActivity : BaseActivityChat<ActivityChatSocialDetailBinding>(), IRecyclerViewCallback, View.OnClickListener {
    companion object {
        var isOpened = false

        fun createRoomChat(context: Context, userId: Long, type: String) {
            finishAllChat()
            context.startActivity(Intent(context, ChatSocialDetailActivity::class.java).apply {
                putExtra(DATA_2, userId)
                putExtra(DATA_3, type)
            })
        }

        fun createRoomChat(activity: Activity, userId: Long, type: String, requestCode: Int = 0) {
            finishAllChat()

            val intent = Intent(activity, ChatSocialDetailActivity::class.java).apply {
                putExtra(DATA_2, userId)
                putExtra(DATA_3, type)
            }

            if (requestCode == 0) {
                activity.startActivity(intent)
            } else {
                activity.startActivityForResult(intent, requestCode)
            }
        }

        fun openRoomChatWithKey(context: Context, key: String) {
            finishAllChat()
            context.startActivity(Intent(context, ChatSocialDetailActivity::class.java).apply {
                putExtra(KEY, key)
            })
        }

        fun finishAllChat() {
            EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.ON_FINISH_ALL_CHAT))
        }

        var toId = ""
        var toType = ""
        var isVerified = false
    }

    private lateinit var viewModel: ChatSocialDetailViewModel

    private val adapter = ChatSocialDetailAdapter(this)

    private val adapterImage = ImageAdapter()

    private var conversation: MCConversation? = null

    private var product: MCProductFirebase? = null

    private val requestCameraPermission = 3
    private val requestScanBarcodePermission = 4

    var inboxRoomID: String? = null
    var inboxUserID: String? = null
    private var keyRoom = ""

    private var userId: Long? = null
    private var userType = "user"
    private var key: String? = null
    private var isLoadData: Boolean = true
    private var newMessage = MCDetailMessage()
    private var isAllowScroll: Boolean = true

    //l??u gi?? tr??? tr?????c khi g???i
//    private var keyConversation: String? = null
//    private var sentMessage: MCDetailMessage? = null

    private val linearAnimation = TranslateAnimation(0f, 0f, 0f, 13f).apply {
        duration = 550
        interpolator = LinearInterpolator()
        repeatCount = -1
        repeatMode = Animation.REVERSE
    }


    var deleteAt = -1L

    override val bindingInflater: (LayoutInflater) -> ActivityChatSocialDetailBinding
        get() = ActivityChatSocialDetailBinding::inflate

    override fun onInitView() {
        isOpened = false
//        ListConversationFragment.isOpenChat = true

        viewModel = ViewModelProvider(this@ChatSocialDetailActivity)[ChatSocialDetailViewModel::class.java]

        setClickListener(this@ChatSocialDetailActivity, binding.tvMessage, binding.imgDelete, binding.imgScan, binding.imgCamera, binding.imgSticker, binding.edtMessage, binding.imgSend, binding.layoutToolbar.imgBack, binding.layoutToolbar.imgAction, binding.layoutNewMessage)

        initToolbar()
        setupView()
        initRecyclerView()
        initEditText()
        getPackageSticker()
        listenMediaData()
    }

    private fun initToolbar() {
        conversation = intent.getSerializableExtra(DATA_1) as MCConversation?
        userId = intent.getLongExtra(DATA_2, -1)
        userType = intent.getStringExtra(DATA_3) ?: "user"
        key = intent.getStringExtra(KEY)

        viewModel.loginFirebase({
            when {
                conversation != null -> {
                    if (!conversation?.key.isNullOrEmpty()) {
                        key = conversation?.key
                        getChatRoom(conversation?.key!!)
                    }
                }
                !key.isNullOrEmpty() -> {
                    getChatRoom(key!!)
                }
                else -> {
                    createRoom()
                }
            }
        }, {
            this@ChatSocialDetailActivity.onBackPressed()
        })
        binding.layoutToolbar.root.setBackgroundColor(Color.WHITE)
        binding.layoutToolbar.imgAction.setVisible()
        binding.layoutToolbar.imgAction.fillDrawableColor(R.drawable.ic_setting_blue_24dp_chat)
    }

    private fun setupView() {
        binding.layoutEditText.background = ViewHelper.bgGrayF0Corners4()
        binding.btnUnBlock.background = ViewHelper.bgPrimaryCorners4(this)
        binding.tvNewMessage.background = ViewHelper.bgWhiteStrokeLineColor1Corners4(this)

        binding.imgScan.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.setCheckedPrimary(R.drawable.ic_scan_gray_24dp_chat,R.drawable.ic_scan_white_24dp_chat, this), null, null, null)
        binding.imgCamera.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.setCheckedPrimary(R.drawable.ic_camera_off_24dp_chat,R.drawable.ic_camera_off_vector_24dp, this), null, null, null)
        binding.imgSticker.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.setCheckedPrimary(R.drawable.ic_emoji_20dp_chat,R.drawable.ic_imoji_fc_24dp_chat, this), null, null, null)
        binding.imgSend.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.setCheckedPrimary(R.drawable.ic_send_dis_24dp_chat,R.drawable.ic_send_active_24dp_chat, this), null, null, null)

        binding.tvMessage.setHintTextColor(ColorManager.getDisableTextColor(this))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        binding.viewClick.setOnTouchListener { v, event ->
            binding.viewClick.setGone()

            binding.imgSticker.isChecked = false
            binding.layoutSticker.setGone()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.edtMessage.windowToken, 0)
            true
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@ChatSocialDetailActivity)
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val holder = recyclerView.findViewHolderForAdapterPosition(0)
                if (holder != null) {
                    isAllowScroll = true
                    binding.layoutNewMessage.beGone()
                    binding.layoutNewMessage.clearAnimation()
                } else {
                    isAllowScroll = false
                }
            }
        })

        binding.recyclerView.adapter = adapter

        binding.recyclerViewImage.layoutManager = LinearLayoutManager(this@ChatSocialDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImage.adapter = adapterImage

        binding.recyclerViewPackageSticker.layoutManager = LinearLayoutManager(this@ChatSocialDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSticker.layoutManager = GridLayoutManager(this@ChatSocialDetailActivity, 4)
    }

    private fun initEditText() {
        binding.edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.imgSend.isChecked = !s?.trim().isNullOrEmpty() || binding.layoutProduct.isVisible && product != null
                binding.imgSend.isEnabled = !s?.trim().isNullOrEmpty() || binding.layoutProduct.isVisible && product != null


                if (s.isNullOrEmpty()) {
                    binding.layoutEditText.background = ViewHelper.bgGrayF0Corners4()
                } else {
                    binding.layoutEditText.background = ViewHelper.bgOutlinePrimary1Corners4(this@ChatSocialDetailActivity)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    fun validNumber(text: String): Boolean {
        return Pattern.compile("^[0-9]+").matcher(text).matches()
    }

    private fun createRoom() {

        val listMember = mutableListOf<MCMember>()

        val uid = FirebaseAuth.getInstance().uid

        if (!uid.isNullOrEmpty() && validNumber(uid)) {
            listMember.add(MCMember(uid.toLong(), "user", "admin"))
        }
        listMember.add(MCMember(userId, userType, "member"))

        viewModel.createRoom(listMember).observe(this@ChatSocialDetailActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    if (it.data?.data != null) {
                        conversation = MCConversation()

                        if (it.data.data.members?.source_id.toString().contains(userId.toString())) {
                            viewModel.getChatSender(it.data.data.members?.id.toString(), { success ->
                                conversation?.targetUserName = success.child("name").value.toString()
                                conversation?.imageTargetUser = success.child("image").value.toString()
                            }, {

                            })
                        }

                        conversation?.key = it.data.data.room_id
                        conversation?.keyRoom = it.data.data.room_id

                        if (!it.data.data.room_id.isNullOrEmpty()) {
                            key = it.data.data.room_id
                            getChatRoom(it.data.data.room_id)
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun getChatRoom(key: String) {
        keyRoom = key
        inboxRoomID = key

        setGoneView(binding.layoutChat, binding.layoutUserBlock, binding.layoutBlock)
        binding.layoutToolbar.imgAction.setVisible()

        viewModel.getChatRoom(key,
            { obj ->
                if (obj.value != null) {

                    if (obj.child("members").hasChildren()) {
                        for (item in obj.child("members").children) {
                            if (!FirebaseAuth.getInstance().uid.toString().contains(item.child("source_id").value.toString())) {
                                toId = item.child("source_id").value.toString()
                                toType = item.child("type").value.toString()
                                inboxUserID = toId

                                    viewModel.getChatSender(item.child("id").value.toString(), { success ->

                                        if (toType.contains("page")){
                                            isVerified = success.child("is_verify").value.toString().toBoolean()

                                            if (isVerified){
                                                binding.layoutToolbar.txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_18px, 0)
                                            }else{
                                                binding.layoutToolbar.txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                                            }
                                        }else{
                                            val isKYC = success.child("kyc_status").value as Long? ?: 0L

                                            if (isKYC == 2L){
                                                binding.layoutToolbar.txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16px, 0)
                                            }else{
                                                binding.layoutToolbar.txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                                            }
                                        }
                                        binding.layoutToolbar.txtTitle.text = success.child("name").value.toString()
                                    }, {

                                })
                            } else {
                                deleteAt = if (item.child("deleted_at").value != null && validNumber(item.child("deleted_at").value.toString())) {
                                    item.child("deleted_at").value.toString().toLong()
                                } else {
                                    -1
                                }
                            }
                        }
                    }

                        getChatMessage(key)
                        listenChangeMessage(key)

                        if (obj.child("is_block").value != null) {
                            binding.layoutToolbar.imgAction.setGone()
                            if (obj.child("is_block").child("from_id").value != null && obj.child("is_block").child("from_id").value.toString().contains(FirebaseAuth.getInstance().uid.toString())) {
                                binding.layoutBlock.setVisible()
                                setGoneView(binding.layoutChat, binding.layoutUserBlock)
                                conversation?.targetUserName?.let {
                                    binding.tvTitle.setText(
                                        R.string.ban_da_chan_tin_nhan_cua_s,
                                        it
                                    )
                                }

                                binding.btnUnBlock.setOnClickListener {
                                    this@ChatSocialDetailActivity.showConfirm(getString(R.string.bo_chan_tin_nhan), getString(R.string.message_unblock), getString(R.string.de_sau), getString(R.string.dong_y), false, object : ConfirmDialogListener {
                                        override fun onDisagree() {

                                        }

                                        override fun onAgree() {
                                            unBlockMessage(key, toId, toType)
                                        }
                                    })
                                }
                            } else {
                                adapterImage.clearData()
                                binding.edtMessage.setText("")
                                checkKeyboard()
                                setGoneView(binding.layoutChat, binding.layoutBlock)
                                binding.layoutUserBlock.setVisible()
                                binding.tvUserTitle.setText(R.string.ban_da_bi_s_chan_tin_nhan, conversation?.targetUserName?:"")
                            }
                        } else {
                            setGoneView(binding.layoutUserBlock, binding.layoutBlock)
                            setVisibleView(binding.layoutToolbar.imgAction, binding.layoutChat)
                        }
                    }

                    binding.tvMessage.isEnabled = true
                },
                {
                    setGoneView(binding.layoutUserBlock, binding.layoutBlock)
                    setVisibleView(binding.layoutToolbar.imgAction, binding.layoutChat)
                })
    }

    private fun getChatMessage(key: String, lastTimeStamp: Long = 0) {
        viewModel.getChatMessage(lastTimeStamp, key,
                { obj ->
                    isLoadData = true
                    val listChatMessage = mutableListOf<MCDetailMessage>()
                    if (obj.hasChildren()) {
                        for (item in obj.children.reversed()) { // ?????o list - tin nh???n c?? ???????c ?????c tr?????c : so s??nh th???i gian v???i tin nh???n tr?????c d??? h??n
                            if (item.child("time").value != null && validNumber(item.child("time").value.toString())) {
                                if (item.child("time").value.toString().toLong() > deleteAt) {
                                    val message = convertDataFirebase(item, newMessage)

                                    listChatMessage.add(message)
                                    newMessage = if (isLoadData) {
                                        if (adapter.getListData.isNullOrEmpty()) {
                                            message
                                        } else {
                                            adapter.getListData.last { it.time != null }
                                        }
                                    } else {
                                        message
                                    }
                                    isLoadData = false
                                }
                            }

                        }

                        markReadMessage(key)

                        if (lastTimeStamp == 0L) {
                            if (listChatMessage.isNullOrEmpty()) {
                                viewModel.checkError(true, dataEmpty = true)
                            } else {
                                adapter.setData(listChatMessage.reversed().toMutableList()) // ?????o list ng?????c l???i ????? add view ????ng th??? t???
                            }
                        } else {
                            adapter.addData(listChatMessage.reversed().toMutableList()) // ?????o list ng?????c l???i ????? add view ????ng th??? t???
                        }

                    }
                },
                { error ->
                    listenChangeMessage(key)
                    showToastError(error.message)
                })


    }

    private fun listenChangeMessage(key: String) {

        viewModel.getChangeMessageChat(key) { data ->

            // m??nh g???i
            if (FirebaseAuth.getInstance().currentUser?.uid == data.child("sender").child("source_id").value.toString()) {
                val index = adapter.getListData.indexOfFirst { it.messageId == data.key }
                Log.d("Message", "index: $index")
                if (index != -1) {
                    adapter.getListData[index].status = MCStatus.SUCCESS
                    adapter.getListData[index].time = data.child("time").value as Long?

                    if (data.child("message").child("media").hasChildren()) {
                        val listImage = mutableListOf<MCMedia>()

                        for (i in data.child("message").child("media").children) {
                            listImage.add(MCMedia(i.child("content").value.toString(), i.child("type").value.toString()))
                        }

                        adapter.getListData[index].listMedia = listImage
                    }

                    //x??a status tin nh???n tr?????c ????
                    if (adapter.getListData.size > 1) {
                        if (adapter.getListData[1].senderId == adapter.getListData[index].senderId) {
                            if (!chenhLechGio(adapter.getListData[1].time, adapter.getListData[index].time, 1)) {
                                val holder = recyclerView.findViewHolderForAdapterPosition(1)
                                adapter.getListData[1].showStatus = 0
                                Log.d("Message", "chenhLechGio: true")

                                if (holder is ChatSocialDetailAdapter.SenderHolder) {
                                    holder.setupShowStatus(adapter.getListData[1])
                                } else {
                                    adapter.notifyItemChanged(1)
                                }
                            }
                        }
                    }

                    adapter.notifyItemChanged(index)
                } else {
                    val lastMessageReceive = adapter.getListData.firstOrNull { it.senderId == FirebaseAuth.getInstance().currentUser?.uid }
                    val message = convertDataFirebase(data, lastMessageReceive ?: MCDetailMessage())
                    message.showStatus = -1
                    message.status = MCStatus.SUCCESS
                    adapter.getListData.add(0, message)
                    adapter.notifyItemInserted(0)

                    //x??a status tin nh???n tr?????c ????
                    if (adapter.getListData.size > 1) {
                        if (adapter.getListData[1].senderId == message.senderId) {
                            if (!chenhLechGio(adapter.getListData[1].time, message.time, 1)) {
                                val holder = recyclerView.findViewHolderForAdapterPosition(1)
                                adapter.getListData[1].showStatus = 0

                                if (holder is ChatSocialDetailAdapter.SenderHolder) {
                                    holder.setupShowStatus(adapter.getListData[1])
                                } else {
                                    adapter.notifyItemChanged(1)
                                }
                            }
                        }
                    }

                    binding.recyclerView.smoothScrollToPosition(0)
                }
                // ?????i ph????ng g???i
            } else {
                markReadMessage(key)

                val lastMessageReceive = adapter.getListData.firstOrNull { it.senderId != FirebaseAuth.getInstance().currentUser?.uid }
                val message = convertDataFirebase(data, lastMessageReceive ?: MCDetailMessage())
                message.showStatus = -1
                adapter.getListData.add(0, message)
                adapter.notifyItemInserted(0)

                //x??a status tin nh???n tr?????c ????
                if (adapter.getListData.size > 1) {
                    if (adapter.getListData[1].senderId == message.senderId) {
                        if (!chenhLechGio(adapter.getListData[1].time, message.time, 1)) {
                            val holder = recyclerView.findViewHolderForAdapterPosition(1)
                            adapter.getListData[1].showStatus = 0

                            if (holder is ChatSocialDetailAdapter.ReceiverHolder) {
                                holder.setupShowStatus(adapter.getListData[1])
                            } else {
                                adapter.notifyItemChanged(1)
                            }
                        }
                    }
                }

                if (isAllowScroll) {
                    binding.recyclerView.smoothScrollToPosition(0)
                    binding.layoutNewMessage.beGone()
                    binding.layoutNewMessage.clearAnimation()
                } else {
                    binding.layoutNewMessage.beVisible()
                    binding.layoutNewMessage.startAnimation(linearAnimation)
                }
            }
        }
    }

    private fun convertDataFirebase(message: DataSnapshot, newMessage: MCDetailMessage): MCDetailMessage {
        val element = MCDetailMessage().apply {
            time = message.child("time").value as Long?
            senderId = message.child("sender").child("source_id").value.toString()
            userId = FirebaseAuth.getInstance().currentUser?.uid
            type = message.child("message").child("type").value.toString()
            avatarSender = conversation?.imageTargetUser
            showStatus = if (senderId != newMessage.senderId) {
                -1
            } else {
                if (chenhLechGio(time, newMessage.time, 1)) {
                    -1
                } else {
                    0
                }
            }


            if (message.child("message").value != null) {
                if (message.child("message").child("media").hasChildren()) {
                    val listImage = mutableListOf<MCMedia>()

                    for (i in message.child("message").child("media").children) {
                        listImage.add(MCMedia(i.child("content").value.toString(), i.child("type").value.toString()))
                    }

                    listMedia = listImage
                }

                if (message.child("message").child("product").value != null) {
                    val itemProduct = message.child("message").child("product")

                    product = MCProductFirebase().apply {
                        barcode = itemProduct.child("barcode").value.toString()
                        image = itemProduct.child("image").value.toString()
                        name = itemProduct.child("name").value.toString()
                        state = itemProduct.child("state").value.toString()
                        productId = if (itemProduct.child("productId").value is Long) {
                            itemProduct.child("productId").value as Long?
                        } else {
                            -1
                        }
                        price = if (itemProduct.child("price").value is Long) {
                            itemProduct.child("price").value as Long?
                        } else {
                            -1
                        }
                    }
                }

                if (!message.child("message").child("text").value.toString().contains("null")) {
                    content = message.child("message").child("text").value.toString()
                }

                if (message.child("message").child("sticker").value != null) {
                    val stickerFirebase = message.child("message").child("sticker")

                    sticker = if (message.child("message").child("sticker").child("thumbnail").value.toString().replace("null", "").isEmpty()) {
                        message.child("message").child("sticker").value.toString()
                    } else {
                        MCSticker().apply {
                            if (stickerFirebase.child("id").value != null && validNumber(stickerFirebase.child("id").value.toString())) {
                                id = stickerFirebase.child("id").value.toString().toLong()
                            }

                            thumbnail = stickerFirebase.child("thumbnail").value.toString()
                            if (stickerFirebase.child("packageId").value != null && validNumber(stickerFirebase.child("packageId").value.toString())) {
                                packageId = stickerFirebase.child("packageId").value.toString().toLong()
                            }
                        }
                    }
                }
            }
        }
        return element
    }

    private fun selectedTextView(view: AppCompatCheckedTextView, layout: View, isEnabled: Boolean = false) {
        if (view.isChecked) {
            view.isChecked = false
            layout.setGone()
            binding.imgSend.isChecked = false
            binding.imgSend.isEnabled = false
        } else {
            unCheckAll(view.id == imgSticker.id)
            view.isChecked = true
            layout.setVisible()
            binding.viewClick.setVisible()
            binding.imgSend.isChecked = isEnabled
            binding.imgSend.isEnabled = isEnabled
        }
    }


    private fun formatMessage() {
        if (!adapterImage.isEmpty) {
            if (adapterImage.getListData.size > 20) {
                showToastError(getString(R.string.chon_20_muc))
                return
            }
        }
        val element = MCDetailMessage().apply {
            senderId = "${FirebaseAuth.getInstance().currentUser?.uid}"
            content = binding.edtMessage.text.toString().trim()
            if (!adapterImage.isEmpty) {
                listMediaFile = mutableListOf()
                listMediaFile!!.addAll(adapterImage.getListData)

                type = "media"
            }
            if (binding.layoutProduct.isVisible && this@ChatSocialDetailActivity.product != null) {
                type = "product"
                this.product = this@ChatSocialDetailActivity.product
            }
        }

        checkSendMessage(element)
    }

    private fun checkSendMessage(obj: MCDetailMessage) {
        sendMessageSuccess(obj)

        if (NetworkHelper.isNotConnected(this)) {
            obj.status = MCStatus.ERROR_NETWORK
            addMessageAdapter(obj)
        } else {
            val idFirebase = FirebaseDatabase.getInstance().reference.push().key.toString()
            obj.messageId = idFirebase

            //loading
            if (adapter.getListData.contains(obj)) {
                val index = adapter.getListData.indexOfFirst { it == obj }
                if (index != -1) {
                    adapter.getListData.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
                obj.status = MCStatus.LOADING
                addMessageAdapter(obj)
            } else {
                obj.status = MCStatus.LOADING
                addMessageAdapter(obj)
            }

            EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.UPDATE_DATA))

            if (obj.type == "media") {
                viewModel.uploadImage(obj)
//                sentMessage = obj
//                keyConversation = key
            } else {
                // Handler l???i v?? ch??a k???p add data v??o adapter n??n khi getList ch??a c?? message n??y
                Handler().postDelayed({
                    sendMessage("user", obj)
                }, 200)
            }
        }
    }

    private fun listenMediaData() {
        viewModel.listMediaData.observe(this, { obj ->
            if (key != null) {
                sendMessage("user", obj)
            }
        })
    }

    private fun addMessageAdapter(obj: MCDetailMessage) {
        obj.showStatus = -1
        if (adapter.getListData.isNullOrEmpty()) {
            adapter.addData(obj)
        } else {
            adapter.addData(obj)
            if (adapter.getListData[1].status == obj.status) {
                val holder = recyclerView.findViewHolderForAdapterPosition(1)
                adapter.getListData[1].showStatus = 0

                if (holder is ChatSocialDetailAdapter.SenderHolder) {
                    holder.setupShowStatus(adapter.getListData[1])
                } else {
                    adapter.notifyItemChanged(1)
                }
            }
        }

        binding.recyclerView.smoothScrollToPosition(0)
    }

    private fun sendMessage(memberType: String, obj: MCDetailMessage) {
        viewModel.sendMessage(key ?: "", memberType, obj).observe(this@ChatSocialDetailActivity, {
            when (it.status) {
                MCStatus.ERROR_REQUEST -> {
                    val index = adapter.getListData.indexOfFirst { it == obj }
                    if (index != -1) {
                        adapter.getListData[index].status = MCStatus.ERROR_REQUEST
                        adapter.notifyItemChanged(index)
                    }
                }
                MCStatus.SUCCESS -> {
                    sendMessageSuccess(obj)
                }
                else -> {
                }
            }
        })
    }

    private fun sendMessageSuccess(obj: MCDetailMessage) {
        if (obj.type?.contains("sticker") == false) {
            adapterImage.clearData()
            binding.view.setGone()
            binding.edtMessage.setText("")

            unCheckAll()
        }
    }

    private fun unCheckAll(clickSticker: Boolean = false) {
        binding.imgScan.isChecked = false
        binding.imgCamera.isChecked = false
        binding.imgSticker.isChecked = false
        binding.imgSend.isChecked = false
        binding.imgSend.isEnabled = false

        binding.layoutBlock.setGone()
        binding.layoutSticker.setGone()
        binding.layoutUserBlock.setGone()

        if (!clickSticker) {
            product = null
            binding.layoutProduct.setGone()
        }
    }


    private fun unBlockMessage(key: String, toId: String, toType: String) {
        viewModel.unBlockMessage(key, toId, toType).observe(this@ChatSocialDetailActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    showToastSuccess(getString(R.string.ban_da_bo_chan_tin_nhan_thanh_cong))
                    binding.layoutChat.setVisible()
                    binding.layoutBlock.setGone()
                    binding.layoutToolbar.imgAction.setVisible()
                }
                else -> {
                }
            }
        })
    }

    private fun getProductBarcode(barcode: String) {
        viewModel.getProductBarcode(barcode).observe(this, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    if (it.data?.data != null) {
                        selectedTextView(binding.imgScan, binding.layoutProduct, true)

                        product = it.data.data.basicInfo?.apply {
                            productId = it.data.data.id
                            image = it.data.data.media?.firstOrNull()?.content
                        }

                        binding.tvBarcode.text = product?.barcode

                        binding.tvNameProduct.text = if (!product?.name.isNullOrEmpty()) {
                            product?.name
                        } else {
                            getString(R.string.ten_dang_cap_nhat)
                        }

                        loadImageUrlRounded(binding.imgProduct, product?.image, R.drawable.ic_default_product_chat_vuong, dpToPx(4))
                    }
                }
                else -> {
                }
            }
        })
    }

    private fun getPackageSticker() {
        viewModel.getPackageSticker().observe(this@ChatSocialDetailActivity, {
            binding.imgSend.isChecked = false
            binding.imgSend.isEnabled = false

            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    if (!it.data?.data?.rows.isNullOrEmpty()) {
                        val adapterPackage = StickerAdapter(TYPE_PACKAGE)
                        binding.recyclerViewPackageSticker.adapter = adapterPackage

                        adapterPackage.setData(it.data?.data?.rows ?: mutableListOf())

                        if (it.data?.data?.rows!![0].id != null) {
                            getSticker(it.data.data.rows[0].id!!)
                        }

                        adapterPackage.setOnClick(object : StickerAdapter.IStickerListener {
                            override fun onClick(obj: MCSticker) {
                                getSticker(obj.id ?: -1)
                            }
                        })
                    }
                }
                else -> {
                }
            }
        })
    }

    private fun getSticker(packageId: Long) {
        viewModel.getSticker(packageId).observe(this@ChatSocialDetailActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    if (!it.data?.data?.rows.isNullOrEmpty()) {
                        val adapterSticker = StickerAdapter(TYPE_STICKER)
                        binding.recyclerViewSticker.adapter = adapterSticker

                        adapterSticker.setData(it.data?.data?.rows ?: mutableListOf())

                        adapterSticker.setOnClick(object : StickerAdapter.IStickerListener {
                            override fun onClick(obj: MCSticker) {
                                val element = MCDetailMessage().apply {
                                    senderId = FirebaseAuth.getInstance().currentUser?.uid
                                    type = "sticker"
                                    sticker = MCSticker().apply {
                                        id = obj.id
                                        this.packageId = obj.packageId
                                        thumbnail = obj.thumbnail
                                    }
                                }

                                if (!key.isNullOrEmpty()) {
                                    checkSendMessage(element)
                                }
                            }
                        })
                    }
                }
                else -> {
                }
            }
        })
    }

    private fun markReadMessage(key: String) {
        viewModel.markReadMessage("user|${ShareHelperChat.getLong(ConstantChat.USER_ID)}", key).observe(this@ChatSocialDetailActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    if (it.data?.data.isNullOrEmpty()) {
                        markReadMessage(key)
                    }
                }
                else -> {

                }
            }
        })
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        if (!key.isNullOrEmpty()) {
            if (adapter.getListData.size > 2) {
                for (i in adapter.getListData.size - 1 downTo 0) {
                    if (adapter.getListData[i].time != null) {
                        getChatMessage(key!!, adapter.getListData[i].time ?: 0)
                        return
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SCAN -> {
                lifecycleScope.launch {
                    if (resultCode == Activity.RESULT_OK) {
                        val barcode = data?.getStringExtra(BARCODE)

                        val qrCode = data?.getStringExtra(QR_CODE)

                        when {
                            !barcode.isNullOrEmpty() -> {
                                getProductBarcode(barcode)
                            }
                            !qrCode.isNullOrEmpty() -> {
                                binding.tvMessage.setGone()
                                binding.edtMessage.setVisible()
                                binding.edtMessage.requestFocus()
                                binding.edtMessage.setText(qrCode)
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCameraPermission -> {
                if (PermissionChatHelper.checkResult(grantResults)) {
                    try {
                        showTakeMedia()
                    } catch (e: Exception) {

                    }
                } else {
                    showToastError(getString(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen))
                }
            }
            requestScanBarcodePermission -> {
                if (PermissionChatHelper.checkResult(grantResults)) {
                    scanBarcode()
                } else {
                    showToastError(getString(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen))
                }
            }
        }
    }

    override fun onMessageEvent(event: MCMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            MCMessageEvent.Type.BACK -> {
                onBackPressed()
                EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.UPDATE_DATA))
            }
            MCMessageEvent.Type.BLOCK -> {
                unCheckAll()
                binding.layoutChat.setGone()
                binding.layoutBlock.setVisible()
            }
            MCMessageEvent.Type.SEND_RETRY_CHAT -> {
                if (!key.isNullOrEmpty()) {
                    if (event.data != null && event.data is MCDetailMessage) {
                        if (!NetworkHelper.isNotConnected(this)) {
                            checkSendMessage(event.data)
                        }
                    }
                }
            }
            else -> {

            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgDelete -> {
                selectedTextView(binding.imgScan, binding.layoutProduct, false)
            }
            R.id.imgScan -> {
                scanBarcode()
            }
            R.id.imgCamera -> {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (PermissionChatHelper.checkPermission(this, permission, requestCameraPermission)) {
                    showTakeMedia()
                }
            }
            R.id.imgSticker -> {
                checkKeyboard()
                selectedTextView(binding.imgSticker, binding.layoutSticker, !binding.edtMessage.text.isNullOrEmpty() || adapterImage.getListData.isNotEmpty())
                binding.recyclerViewImage.setVisible()
            }
            R.id.edtMessage -> {
                binding.imgSticker.isChecked = false
                binding.layoutSticker.setGone()

                binding.viewClick.setVisible()
            }
            R.id.imgSend -> {
                if (!key.isNullOrEmpty()) {
                    formatMessage()
                }

                binding.edtMessage.setText("")

                binding.imgSend.isChecked = false
                binding.imgSend.isEnabled = false
            }
            R.id.imgAction -> {
                binding.edtMessage.setText("")
                startActivity(Intent(this@ChatSocialDetailActivity, UserInformationActivity::class.java).apply {
                    putExtra(KEY, key ?: conversation?.key)
                    putExtra(NAME, conversation?.targetUserName)
                })
            }
            R.id.tvMessage -> {
                binding.imgSticker.isChecked = false
                binding.layoutSticker.setGone()

                binding.edtMessage.setVisible()
                binding.tvMessage.setGone()
                binding.edtMessage.requestFocus()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

                binding.viewClick.setVisible()
            }
            R.id.layoutNewMessage -> {
                recyclerView.smoothScrollToPosition(0)
                binding.layoutNewMessage.beGone()
                binding.layoutNewMessage.clearAnimation()
            }
        }
    }

    private fun scanBarcode() {
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (PermissionChatHelper.checkPermission(this, permission, requestScanBarcodePermission)) {
            if (!binding.imgScan.isChecked) {
                IcheckScanActivity.scanOnlyChat(this, SCAN)
            }
        }
    }

    private fun showTakeMedia() {
        checkKeyboard()
        val listener = object : TakeMediaListener {
            override fun onPickMediaSucess(file: File) {
                binding.view.setVisible()
                if ((adapterImage.getListData.size + 1) <= 20) {
                    adapterImage.setImage(file)
                } else {
                    showToastError(getString(R.string.chon_20_anh))
                }
                chooseImage()
            }

            override fun onPickMuliMediaSucess(file: MutableList<File>) {
                binding.view.setVisible()
                if ((adapterImage.getListData.size + file.size) <= 20) {
                    adapterImage.setListImage(file)
                } else {
                    showToastError(getString(R.string.chon_20_anh))
                }
                chooseImage()
            }

            override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
            }

            override fun onDismiss() {
            }

            override fun onTakeMediaSuccess(file: File?) {
                if (file != null) {
                    binding.view.setVisible()
                    if ((adapterImage.getListData.size + 1) <= 20) {
                        adapterImage.setImage(file)
                    } else {
                        showToastError(getString(R.string.chon_20_anh))
                    }
                    chooseImage()
                }
            }
        }

        TakeMediaDialog.show(supportFragmentManager, this, listener, selectMulti = true, isVideo = true, maxSelectCount = 20)
    }

    private fun chooseImage() {
        binding.imgCamera.isChecked = true
        binding.imgSend.isChecked = true
        binding.imgSend.isEnabled = true

        binding.imgSticker.isChecked = false
        binding.layoutSticker.setGone()

        binding.imgScan.isChecked = false
        binding.layoutProduct.setGone()
        binding.layoutUserBlock.setGone()
        binding.layoutBlock.setGone()
        product = null
    }

    private fun checkKeyboard() {
        val imm by lazy { this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
        val windowHeightMethod = InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
        val height = windowHeightMethod.invoke(imm) as Int

        if (height > 0) {
            //keyboard is shown
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtMessage.windowToken, 0)
        } else {
            //keyboard is hidden
        }
    }

    override fun onPause() {
        super.onPause()
        checkKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        checkKeyboard()
        inboxRoomID = null
        inboxUserID = null
    }

    override fun onStop() {
        super.onStop()
        inboxRoomID = null
        inboxUserID = null
    }

    override fun onResume() {
        super.onResume()
        inboxRoomID = keyRoom
        inboxUserID = toId
    }

    override fun onBackPressed() {
        super.onBackPressed()
        isOpened = true
    }
}