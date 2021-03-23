package vn.icheck.android.chat.icheckchat.screen.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_chat_social_detail.*
import kotlinx.coroutines.launch
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseActivityChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.BARCODE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_1
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_2
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_3
import vn.icheck.android.chat.icheckchat.base.ConstantChat.QR_CODE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.SCAN
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_PACKAGE
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_STICKER
import vn.icheck.android.chat.icheckchat.databinding.ActivityChatSocialDetailBinding
import vn.icheck.android.chat.icheckchat.dialog.TakeMediaBottomSheetChat
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.chat.icheckchat.helper.PermissionChatHelper
import vn.icheck.android.chat.icheckchat.model.*
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.chat.icheckchat.screen.detail.adapter.ChatSocialDetailAdapter
import vn.icheck.android.chat.icheckchat.screen.detail.adapter.ImageAdapter
import vn.icheck.android.chat.icheckchat.screen.detail.adapter.StickerAdapter
import vn.icheck.android.chat.icheckchat.screen.scan.ScanSocialChatActivity
import vn.icheck.android.chat.icheckchat.screen.user_information.UserInformationActivity
import java.io.File

class ChatSocialDetailActivity : BaseActivityChat<ActivityChatSocialDetailBinding>(), IRecyclerViewCallback, View.OnClickListener {
    companion object {
        fun createRoomChat(context: Context, userId: Long, type: String) {
            context.startActivity(Intent(context, ChatSocialDetailActivity::class.java).apply {
                putExtra(DATA_2, userId)
                putExtra(DATA_3, type)
            })
        }
    }

    private lateinit var viewModel: ChatSocialDetailViewModel

    private val adapter = ChatSocialDetailAdapter(this)

    private val adapterImage = ImageAdapter()

    private var conversation: MCConversation? = null

    private var product: MCProductFirebase? = null

    private lateinit var takeMediaDialog: TakeMediaBottomSheetChat

    private val requestCameraPermission = 3

    private val listImageSrc = mutableListOf<MCMedia>()

    private var userId: Long? = null
    private var userType = "user"

    var deleteAt = -1L

    override val bindingInflater: (LayoutInflater) -> ActivityChatSocialDetailBinding
        get() = ActivityChatSocialDetailBinding::inflate

    override fun onInitView() {
        ListConversationFragment.isOpenChat = true

        viewModel = ViewModelProvider(this@ChatSocialDetailActivity)[ChatSocialDetailViewModel::class.java]

        setClickListener(this@ChatSocialDetailActivity, binding.imgDelete, binding.imgScan, binding.imgCamera, binding.imgSticker, binding.edtMessage, binding.imgSend, binding.layoutToolbar.imgBack, binding.layoutToolbar.imgAction)

        initToolbar()
        initRecyclerView()
        getPackageSticker()
        initEditText()
        setUpTakeImage()
    }

    private fun initToolbar() {
        conversation = intent.getSerializableExtra(DATA_1) as MCConversation?
        userId = intent.getLongExtra(DATA_2, -1)
        userType = intent.getStringExtra(DATA_3) ?: "user"

        if (conversation == null) {
            createRoom()
        } else {
            binding.layoutToolbar.txtTitle.text = conversation?.targetUserName

            viewModel.loginFirebase({
                if (!conversation?.key.isNullOrEmpty()) {
                    getChatRoom(conversation?.key!!)
                }
            }, {

            })
        }

        binding.layoutToolbar.imgAction.setVisible()

        binding.layoutToolbar.imgAction.setImageResource(R.drawable.ic_setting_blue_24dp_chat)
    }

    private fun createRoom() {
        val listMember = mutableListOf<MCMember>()
        listMember.add(MCMember(FirebaseAuth.getInstance().uid.toString().toLong(), "user", "admin"))
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

                        for (i in it.data.data.members ?: mutableListOf()) {
                            if (i.source_id.toString().contains(userId.toString())) {
                                viewModel.getChatSender(i.id.toString(), { success ->
                                    conversation?.targetUserName = success.child("name").value.toString()
                                    conversation?.imageTargetUser = success.child("image").value.toString()

                                    binding.layoutToolbar.txtTitle.text = conversation?.targetUserName
                                }, {

                                })
                            }
                        }

                        conversation?.key = it.data.data.room_id
                        conversation?.keyRoom = it.data.data.room_id

                        if (!it.data.data.room_id.isNullOrEmpty()) {
                            getChatRoom(it.data.data.room_id)
                        }
                    }
                }
            }
        })
    }

    private fun selectedTextView(view: AppCompatCheckedTextView, layout: View, isEnabled: Boolean = false) {
        if (view.isChecked) {
            view.isChecked = false
            layout.setGone()
            binding.imgSend.isChecked = false
            binding.imgSend.isEnabled = false
        } else {
            unCheckAll()
            view.isChecked = true
            layout.setVisible()
            binding.imgSend.isChecked = isEnabled
            binding.imgSend.isEnabled = isEnabled
        }
    }

    private fun unCheckAll() {
        binding.imgScan.isChecked = false
        binding.imgCamera.isChecked = false
        binding.imgSticker.isChecked = false
        binding.imgSend.isChecked = false
        binding.imgSend.isEnabled = false
        product = null
        listImageSrc.clear()

        binding.layoutBlock.setGone()
        binding.layoutSticker.setGone()
        binding.recyclerViewImage.setGone()
        binding.layoutProduct.setGone()
        binding.layoutUserBlock.setGone()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this@ChatSocialDetailActivity).apply {
            stackFromEnd = true
        }

        binding.recyclerView.adapter = adapter

        binding.recyclerViewImage.layoutManager = LinearLayoutManager(this@ChatSocialDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImage.adapter = adapterImage

        binding.recyclerViewPackageSticker.layoutManager = LinearLayoutManager(this@ChatSocialDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSticker.layoutManager = GridLayoutManager(this@ChatSocialDetailActivity, 4)
    }

    private fun sendMessageText(key: String) {
        val element = MCDetailMessage().apply {
            senderId = "user|${FirebaseAuth.getInstance().currentUser?.uid}"
        }

//        if (binding.edtMessage.text.toString().trim().contains("http://") || binding.edtMessage.text.toString().trim().contains("https://")) {
//            element.link = binding.edtMessage.text.toString().trim()
//        } else {
        element.content = binding.edtMessage.text.toString().trim()
//        }

        if (!adapterImage.isEmpty) {
            binding.recyclerViewImage.setGone()
            element.type = "media"

            uploadImage {
                element.listMedia = listImageSrc
                sendMessage(key, "user", element)
            }
        }

        if (binding.layoutProduct.isVisible && product != null) {
            element.type = "product"
            element.product = product
        }

        if (element.type != "media") {
            sendMessage(key, "user", element)
        }
    }

    private fun initEditText() {
        binding.edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.imgSend.isChecked = !s?.trim().isNullOrEmpty() || binding.layoutProduct.isVisible && product != null
                binding.imgSend.isEnabled = !s?.trim().isNullOrEmpty() || binding.layoutProduct.isVisible && product != null

                if (s.isNullOrEmpty()) {
                    binding.layoutEditText.setBackgroundResource(R.drawable.bg_corner_4_gray)
                } else {
                    binding.layoutEditText.setBackgroundResource(R.drawable.bg_corner_4_no_solid_light_blue)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun getChatRoom(key: String) {
        setGoneView(binding.layoutChat, binding.layoutUserBlock, binding.layoutBlock)
        binding.layoutToolbar.imgAction.setVisible()

        viewModel.getChatRoom(key,
                { obj ->
                    if (obj.value != null) {
                        var toId = ""
                        var toType = ""

                        if (obj.child("members").hasChildren()) {
                            for (item in obj.child("members").children) {
                                if (!FirebaseAuth.getInstance().uid.toString().contains(item.child("source_id").value.toString())) {
                                    toId = item.child("source_id").value.toString()
                                    toType = item.child("type").value.toString()
                                } else {
                                    deleteAt = if (item.child("deleted_at").value != null) {
                                        item.child("deleted_at").value.toString().toLong()
                                    } else {
                                        -1
                                    }
                                }
                            }
                        }

                        getChatMessage(key)

                        if (obj.child("is_block").value != null) {
                            binding.layoutToolbar.imgAction.setGone()
                            if (obj.child("is_block").child("from_id").value != null && obj.child("is_block").child("from_id").value.toString().contains(FirebaseAuth.getInstance().uid.toString())) {
                                binding.layoutBlock.setVisible()
                                setGoneView(binding.layoutChat, binding.layoutUserBlock)

                                binding.tvTitle.text = "Bạn đã chặn tin nhắn của ${conversation?.targetUserName}"

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
                                listImageSrc.clear()
                                setGoneView(binding.layoutChat, binding.layoutBlock)
                                binding.layoutUserBlock.setVisible()
                                binding.tvUserTitle.text = "Bạn đã bị ${conversation?.targetUserName} chặn tin nhắn"
                            }
                        } else {
                            setGoneView(binding.layoutUserBlock, binding.layoutBlock)
                            setVisibleView(binding.layoutToolbar.imgAction, binding.layoutChat)
                        }
                    }
                },
                {
                    setGoneView(binding.layoutUserBlock, binding.layoutBlock)
                    setVisibleView(binding.layoutToolbar.imgAction, binding.layoutChat)
                })
    }

    private fun getChatMessage(key: String, isLoadMore: Boolean = false) {
        viewModel.getChatMessage(key,
                { obj ->
                    val listChatMessage = mutableListOf<MCDetailMessage>()
                    var oldItem = MCDetailMessage()
                    if (obj.hasChildren()) {
                        for (item in obj.children.reversed()) {
                            if (item.child("time").value.toString().toLong() > deleteAt) {
                                val element = MCDetailMessage().apply {
                                    time = item.child("time").value as Long?
                                    senderId = item.child("sender").child("source_id").value.toString()
                                    userId = FirebaseAuth.getInstance().currentUser?.uid
                                    type = item.child("message").child("type").value.toString()
                                    avatarSender = conversation?.imageTargetUser
                                    showTime = if (senderId != oldItem.senderId) {
                                        true
                                    } else {
                                        chenhLechGio(time, oldItem.time, 1)
                                    }

                                    if (item.child("message").value != null) {
                                        if (item.child("message").child("media").hasChildren()) {
                                            val listImage = mutableListOf<MCMedia>()

                                            for (i in item.child("message").child("media").children) {
                                                listImage.add(MCMedia(i.child("content").value.toString(), i.child("type").value.toString()))
                                            }

                                            listMedia = listImage
                                        }

                                        if (item.child("message").child("product").value != null) {
                                            val itemProduct = item.child("message").child("product")

                                            product = MCProductFirebase().apply {
                                                barcode = itemProduct.child("barcode").value.toString()
                                                image = itemProduct.child("image").value.toString()
                                                name = itemProduct.child("name").value.toString()
                                                state = itemProduct.child("state").value.toString()
                                                productId = if (itemProduct.child("product_id").value is Long) {
                                                    itemProduct.child("product_id").value as Long?
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

//                                        if (!item.child("message").child("link").value.toString().contains("null")) {
//                                            link = item.child("message").child("link").value.toString()
//                                        }

                                        if (!item.child("message").child("text").value.toString().contains("null")) {
                                            if (item.child("message").child("text").value.toString().trim().contains("https://") || item.child("message").child("link").value.toString().trim().contains("http://")) {
                                                link = item.child("message").child("text").value.toString()
                                            } else {
                                                content = item.child("message").child("text").value.toString()
                                            }
                                        }

                                        if (!item.child("message").child("sticker").value.toString().contains("null")) {
                                            sticker = item.child("message").child("sticker").value.toString()
                                        }
                                    }
                                }

                                listChatMessage.add(element)
                                oldItem = element
                            }
                        }

                        if (!listChatMessage.isNullOrEmpty()) {
                            adapter.setData(listChatMessage.reversed().toMutableList())
                            binding.recyclerView.smoothScrollToPosition(adapter.getListData.size)
                        }

//                            if (!isLoadMore) {
//                                if (listChatMessage.isNullOrEmpty()) {
//
//                                } else {
//                                    adapter.setListData(listChatMessage)
//                                }
//                            } else {
//                                adapter.addListData(listChatMessage)
//                            }
                    }
                },
                { error ->
                    showToastError(error.message ?: getString(R.string.error_default))
                })
    }

    private fun sendMessage(key: String, memberType: String, obj: MCDetailMessage) {
        viewModel.sendMessage(key, memberType, obj).observe(this@ChatSocialDetailActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    binding.recyclerView.smoothScrollToPosition(adapter.getListData.size)

                    binding.edtMessage.setText("")

                    if (obj.type?.contains("sticker") == false) {
                        unCheckAll()
                    }
                    binding.recyclerViewImage.setVisible()
                }
            }
        })
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
            }
        })
    }

    private fun uploadImage(success: () -> Unit) {
        if (NetworkHelper.isNotConnected(this@ChatSocialDetailActivity)) {
            showToastError(getString(R.string.khong_co_mang))
            return
        }

        if (!adapterImage.isEmpty) {
            viewModel.uploadImage(adapterImage.getListData[0], { obj ->
                val type = if (obj.src.endsWith(".mp4")) {
                    "video"
                } else {
                    "image"
                }
                listImageSrc.add(MCMedia(obj.src, type))
                adapterImage.getListData.removeAt(0)
                uploadImage(success)
            }, { error ->
                uploadImage(success)
            })
        } else {
            adapterImage.clearData()
            success()
        }
    }

    private fun setUpTakeImage() {
        takeMediaDialog = TakeMediaBottomSheetChat(object : TakeMediaBottomSheetChat.TakeImageListener {
            override fun onPickMediaSucess(file: File) {
                selectedTextView(binding.imgCamera, binding.recyclerViewImage, true)
                adapterImage.setImage(file)
            }

            override fun onPickMuliMediaSucess(file: MutableList<File>) {
                selectedTextView(binding.imgCamera, binding.recyclerViewImage, true)
                adapterImage.setListImage(file)
            }

            override fun onTakeMediaSuccess(file: File?) {
                if (file != null) {
                    selectedTextView(binding.imgCamera, binding.recyclerViewImage, true)
                    adapterImage.setImage(file)
                }
            }
        }, true, activity = this@ChatSocialDetailActivity)
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
                                    if (!conversation?.members.isNullOrEmpty()) {
                                        senderId = if (conversation?.members?.firstOrNull()?.contains("page") == true) {
                                            "page|${FirebaseAuth.getInstance().currentUser?.uid}"
                                        } else {
                                            "user|${FirebaseAuth.getInstance().currentUser?.uid}"
                                        }
                                    }
                                    type = "sticker"
                                    sticker = obj.thumbnail
                                }

                                if (!conversation?.key.isNullOrEmpty()) {
                                    sendMessage(conversation?.key!!, "user", element)
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        if (!conversation?.key.isNullOrEmpty()) {
            getChatMessage(conversation?.key!!, true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeMediaDialog.takeMediaHelper?.onActivityResult(requestCode, resultCode)

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
                                binding.edtMessage.setText(qrCode)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCameraPermission -> {
                if (PermissionChatHelper.checkResult(grantResults)) {
                    try {
                        takeMediaDialog.show(supportFragmentManager, null)
                    } catch (e: Exception) {

                    }
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
            }
            MCMessageEvent.Type.BLOCK -> {
                unCheckAll()
                binding.layoutChat.setGone()
                binding.layoutBlock.setVisible()
            }
            MCMessageEvent.Type.HIDE_KEYBOARD -> {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.edtMessage.windowToken, 0)
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
                if (!binding.imgScan.isChecked) {
                    startActivityForResult(Intent(this@ChatSocialDetailActivity, ScanSocialChatActivity::class.java), SCAN)
                }
            }
            R.id.imgCamera -> {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (PermissionChatHelper.checkPermission(this, permission, requestCameraPermission)) {
                    takeMediaDialog.show(supportFragmentManager, takeMediaDialog.tag)
                }
            }
            R.id.imgSticker -> {
                checkKeyboard()
                selectedTextView(binding.imgSticker, binding.layoutSticker, true)
                binding.recyclerViewImage.setVisible()
            }
            R.id.edtMessage -> {
                binding.imgSticker.isChecked = false
                binding.layoutSticker.setGone()
            }
            R.id.imgSend -> {
                if (!conversation?.key.isNullOrEmpty()) {
                    sendMessageText(conversation?.key!!)
                }

                binding.imgSend.isChecked = false
                binding.imgSend.isEnabled = false
            }
            R.id.imgAction -> {
                binding.edtMessage.setText("")
                startActivity(Intent(this@ChatSocialDetailActivity, UserInformationActivity::class.java).apply {
                    putExtra(DATA_1, conversation)
                })
            }
        }
    }

    private fun checkKeyboard() {
        val imm by lazy { this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
        val windowHeightMethod = InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
        val height = windowHeightMethod.invoke(imm) as Int

//        val imm by lazy { this@ChatSocialDetailActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

        if (height > 0) {
            //keyboard is shown
            this@ChatSocialDetailActivity.hideKeyboard()
        } else {
            //keyboard is hidden
        }
    }
}