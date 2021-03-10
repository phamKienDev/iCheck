package vn.icheck.android.activities.chat.v2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.icheck.android.ICheckApplication
import vn.icheck.android.activities.chat.sticker.StickerDao
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.activities.chat.sticker.StickerView
import vn.icheck.android.activities.chat.v2.model.ChatBotQA
import vn.icheck.android.room.dao.ChatMessageDao
import vn.icheck.android.activities.chat.v2.model.ChatMsgType
import vn.icheck.android.activities.chat.v2.model.ICChatMessage
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.network.api.ChatBotApi
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.social.SocialRepository
import vn.icheck.android.network.models.ICChatCodeResponse
import vn.icheck.android.network.models.ICUserId
import vn.icheck.android.network.models.chat.DetailSticker
import vn.icheck.android.network.models.chat.StickerWrapper
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.room.database.AppDatabase
import kotlin.collections.set

class ChatV2ViewModel : ViewModel() {

    //    val createRoomLive = MutableLiveData<ICChatCodeResponse>()
    lateinit var icChatCodeResponse: ICChatCodeResponse
    val userLive = MutableLiveData<ICUserId>()
    val onlineLiveData = MutableLiveData<Boolean>()
    val lastOnlineLiveData = MutableLiveData<Long>()
    var online = false
    private val icFactory: DataSource.Factory<Int, ICChatMessage>
    private val chatMessageDao: ChatMessageDao = AppDatabase.getDatabase(ICheckApplication.getInstance())
            .chatMessageDao()
    var listChatMessage: LiveData<PagedList<ICChatMessage>>
    var init = false
    val newItem = MutableLiveData<Int>()
    lateinit var icProductV1: ICBarcodeProductV1
    private val listBot = arrayListOf<ChatBotQA>()
    var lastKey = ""
    var sendProductBot = false
    var stickerPack = MutableLiveData<StickerWrapper>()
    var stickers = MutableLiveData<DetailSticker>()
    private val stickerDao: StickerDao = AppDatabase.getDatabase(ICheckApplication.getInstance())
            .stickerDao()
    var groupId: String = ""
    val arrProduct = arrayListOf<ICBarcodeProductV1>()
    val arrUser = arrayListOf<ICUserId>()

    private val socialRepository = SocialRepository()

    init {
        chatMessageDao.delete()
        icFactory = chatMessageDao.getChatMessages()
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()
        listChatMessage = LivePagedListBuilder(icFactory, config)
                .build()
//        viewModelScope.launch {
//            try {
//                val result = ICNetworkClient.getSimpleChat().getStickerPackages()
//                stickerPack.postValue(result)
//
//            } catch (e: Exception) {
//            }
//        }
    }

    fun insertStickerPackages() {
        var i = 0
        viewModelScope.launch {
            try {
                val result = socialRepository.getStickerPackages()
                for (item in result.data) {
                    if (i < 2) {
                        val sticker = StickerPackages(
                                item.id, item.name, item.thumbnail, item.count
                        )
                        ChatV2Activity.stickerPackagesDao?.insertStickerPackages(sticker)
                    } else {
                        break
                    }
                    i++
                }
                ChatV2Activity.instance?.initRcvStickerPackages()
                ChatV2Activity.instance?.setUpGetStickerPackages()
            } catch (e: Exception) {
            }
        }
    }


    fun setUpGroupChat(groupId: String) {
        this.groupId = groupId
        ICheckApplication.getInstance().mFirebase.currentRoomId = groupId
//        FirebaseDatabase.getInstance()
//                .getReference("room-messages/$groupId")
//                .orderByChild("timestamp")
////                .limitToLast(20)
//                .addChildEventListener(object : ChildEventListener {
//                    var currentData: DataSnapshot? = null
//                    override fun onCancelled(p0: DatabaseError) {
//                    }
//
//                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                    }
//
//                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//                    }
//
//                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                        viewModelScope.launch {
//                            val ownerId = "i-${SessionManager.session?.user?.id}"
//                            if (currentData == null) {
//                                currentData = p0
//                                insertGroupItem(p0, null, ownerId)
//                            } else {
//                                insertGroupItem(p0, currentData, ownerId)
//                                currentData = p0
//                            }
//                        }
//                    }
//
//                    override fun onChildRemoved(p0: DataSnapshot) {
//                    }
//                })
        ICheckApplication.getInstance()
                .mFirebase.roomMessage
                ?.child(groupId)
                ?.orderByChild("timestamp")
                ?.limitToLast(200)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        viewModelScope.launch {
                            try {
                                val ownerId = "i-${SessionManager.session?.user?.id}"
                                if (!init) {
                                    init = true
                                    val reversed = p0.children.reversed()
                                    for (i in reversed.indices) {
                                        if (i < reversed.size) {
                                            if (i == 0 || i == reversed.size - 1) {
                                                insertGroupItem(reversed[i], null, ownerId)
                                            } else {
                                                insertGroupItem(reversed[i], reversed[i + 1], ownerId)
                                            }
                                        } else {
                                            insertGroupItem(reversed[i], null, ownerId)
                                        }
                                    }
                                } else {
                                    insertGroupItem(p0.children.last(), null, ownerId)
                                    newItem.postValue(1)
                                }
                                ICNetworkClient.getSimpleChat().markAsRead(this@ChatV2ViewModel.groupId)
                            } catch (e: Exception) {

                            }
                        }
                    }
                })
    }

    fun setUpChatBot(userId: Long, barcode: String?) {
        val createRoom = hashMapOf<String, Any>()
        createRoom["userId"] = "i-$userId"
        viewModelScope.launch {
            try {
                val userById = ICNetworkClient.getSimpleApiClient().getUserById(userId)
                userLive.postValue(userById)
                icChatCodeResponse = ICNetworkClient.getSimpleChat().createRoomChat(createRoom)
//            createRoomLive.postValue(icChatCodeResponse)
                icProductV1 = ICNetworkClient.getSimpleApiClient().scanBarcodeChatV2(barcode)
//                insertHeader(userLive.value!!)
                if (userId == ChatMsgType.customerServiceId) {
                    val retrofit = Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("https://storage.googleapis.com/")
                            .build()
                    val api = retrofit.create(ChatBotApi::class.java)
                    val responseBot = api.getListQuestionBot()
                    listBot.addAll(responseBot)
                } else if (userById.type.equals("shop")) {
                    listBot.add(ChatBotQA(
                            101,
                            "Sản phẩm này còn hàng không?",
                            ""
                    ))
                    listBot.add(ChatBotQA(
                            102,
                            "Shop đang có chương trình khuyến mãi gì không?",
                            ""
                    ))
                } else if (userById.type.equals("page")) {
                    listBot.add(ChatBotQA(
                            201,
                            "Tôi muốn mua sản phẩm tại cửa hàng gần tôi nhất",
                            ""
                    ))
                    listBot.add(ChatBotQA(
                            202,
                            "Sản phẩm này giá bao nhiêu?",
                            ""
                    ))
                    listBot.add(ChatBotQA(
                            203,
                            "Tôi cần thêm thông tin chi tiết về sản phẩm.",
                            ""
                    ))
                    listBot.add(ChatBotQA(
                            204,
                            "Tôi cần nhân viên tư vấn về sản phẩm.",
                            ""
                    ))
                    listBot.add(ChatBotQA(
                            205,
                            "Sản phẩm có trang web chính thức không?",
                            ""
                    ))
                    listBot.add(ChatBotQA(
                            206,
                            "Tôi muốn làm đại lý bán hàng thì làm như thế nào?",
                            ""
                    ))
                }
                insertChatBot(userById)
                ICheckApplication.getInstance()
                        .mFirebase.roomMessage
                        ?.child(icChatCodeResponse.dataRep.roomId)
                        ?.orderByChild("timestamp")
                        ?.limitToLast(200)
                        ?.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                viewModelScope.launch {
                                    try {
                                        val ownerId = "i-${SessionManager.session?.user?.id}"
                                        if (!init) {
                                            init = true
                                            val reversed = p0.children.reversed()
                                            for (i in reversed.indices) {
                                                if (i < reversed.size) {
                                                    if (i == 0 || i == reversed.size - 1) {
                                                        insertItem(reversed[i], null, userLive.value!!, ownerId)
                                                    } else {
                                                        insertItem(reversed[i], reversed[i + 1], userLive.value!!, ownerId)
                                                    }
                                                } else {
                                                    insertItem(reversed[i], null, userLive.value!!, ownerId)
                                                }
                                            }

                                        } else {
                                            if (p0.childrenCount == 1L) {
                                                insertItem(p0.children.last(), null, userLive.value!!, ownerId)
                                            } else {
                                                insertItem(p0.children.last(), null, userLive.value!!, ownerId)
                                            }
                                            newItem.postValue(1)
                                        }
                                        ICNetworkClient.getSimpleChat().markAsRead(icChatCodeResponse.dataRep.roomId)
                                    } catch (e: Exception) {
                                        Log.e("e", "${e.message}")
                                    }
                                }

                            }
                        })
            } catch (e: Exception) {
            }
        }
    }

    fun getStickers(id: String) {
        viewModelScope.launch {
            try {
                val result = socialRepository.getStickers(id)
                stickers.postValue(result)
            } catch (e: Exception) {
            }
        }
    }

    fun getStickerHistory() {
        val result = stickerDao.getLastSticker()
        val arr = arrayListOf<Stickers>()
        for (item in result) {
            arr.add(Stickers(item.id, item.image, item.packageId))
        }
        val detailSticker = DetailSticker(arr)
        stickers.postValue(detailSticker)
    }

    fun setUpUserChat(userId: Long) {
        val createRoom = hashMapOf<String, Any>()
        createRoom["userId"] = "i-$userId"
        viewModelScope.launch {
            try {
                val userById = ICNetworkClient.getSimpleApiClient().getUserById(userId)
                userLive.postValue(userById)
                icChatCodeResponse = ICNetworkClient.getSimpleChat().createRoomChat(createRoom)
//                createRoomLive.postValue(icChatCodeResponse)
                ICheckApplication.getInstance().mFirebase.currentRoomId = icChatCodeResponse.dataRep.roomId
//                FirebaseDatabase.getInstance()
//                        .getReference("room-messages/${icChatCodeResponse.dataRep.roomId}")
//                        .orderByChild("timestamp")
////                        .limitToLast(40)
//                        .addChildEventListener(object : ChildEventListener{
//                            var currentData: DataSnapshot? = null
//                            override fun onCancelled(p0: DatabaseError) {
//                            }
//
//                            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                            }
//
//                            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                            }
//
//                            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                                viewModelScope.launch {
//                                    val ownerId = "i-${SessionManager.session?.user?.id}"
//                                    if (currentData == null) {
//                                        currentData = p0
//                                        insertItem(p0, null, userLive.value!!, ownerId)
//                                    } else {
//                                        insertItem(p0, currentData!!, userLive.value!!, ownerId)
//                                        currentData = p0
//                                    }
//                                }
//                            }
//
//                            override fun onChildRemoved(p0: DataSnapshot) {
//                            }
//                        })
                ICheckApplication.getInstance()
                        .mFirebase.roomMessage
                        ?.child(icChatCodeResponse.dataRep.roomId)
                        ?.orderByChild("timestamp")
                        ?.limitToLast(200)
                        ?.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                viewModelScope.launch(Dispatchers.IO) {
                                    try {
                                        val ownerId = "i-${SessionManager.session?.user?.id}"
                                        if (!init) {
                                            init = true
                                            val reversed = p0.children.reversed()
                                            for (i in reversed.indices) {

                                                if (i < reversed.size) {
                                                    if (i == 0 || i == reversed.size - 1) {
                                                        insertItem(reversed[i], null, userLive.value!!, ownerId)
                                                    } else {
                                                        insertItem(reversed[i], reversed[i + 1], userLive.value!!, ownerId)
                                                    }
                                                } else {
                                                    insertItem(reversed[i], null, userLive.value!!, ownerId)
                                                }
                                            }
                                        } else {
                                            if (p0.childrenCount == 1L) {
                                                insertItem(p0.children.last(), null, userLive.value!!, ownerId)
                                            } else {
                                                insertItem(p0.children.last(), null, userLive.value!!, ownerId)
                                            }
                                            newItem.postValue(1)
                                        }
                                        ICNetworkClient.getSimpleChat().markAsRead(icChatCodeResponse.dataRep.roomId)
                                    } catch (e: Exception) {
                                        Log.e("e", "")
                                    }
                                }

                            }
                        })
                if (userLive.value!!.type.equals("user", true)) {
                    ICheckApplication.getInstance().mFirebase.users
                            ?.child("i-${userLive.value?.id}")
                            ?.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (p0.hasChild("isOnline")) {
                                        try {
                                            val result = p0.child("isOnline").value as Boolean?
                                            result?.let {
                                                online = it
                                                onlineLiveData.postValue(it)
                                            }
                                        } catch (e: Exception) {
                                        }

                                    }
                                    if (p0.hasChild("lastActivityTime")) {
                                        try {
                                            val res = p0.child("lastActivityTime").value as Long?
                                            res?.let {
                                                lastOnlineLiveData.postValue(res)
                                            }
                                        } catch (e: Exception) {

                                        }
                                    }
                                }
                            })
                }
            } catch (e: Exception) {
                Log.e("e", "${e.message}")
            }
        }
    }

    private fun insertChatBot(icUserId: ICUserId) {
        val last = System.currentTimeMillis()
        for (i in listBot.indices) {
            val botmsg = ICChatMessage(
                    listBot[i].id.toString(),
                    last
            )
            if (i == listBot.size - 1) {
                botmsg.imageMsg = String.format("<p>Chào bạn, <span style='color:#3C5A99'>%s</span> rất vui nếu có " +
                        "thể giúp bạn mua hàng chính hãng." +
                        " Hãy cho chúng tôi biết bạn đang cần tư vấn gì?</p>", icUserId.name)
                botmsg.userSentAvatar = icUserId.avatarThumbnails?.small.toString()
                botmsg.productName = icUserId.name
            }
            botmsg.chatMsgType = ChatMsgType.TYPE_CHAT_BOT_QA
            botmsg.textMessage = listBot[i].question
            chatMessageDao.insertChatMessage(botmsg)
        }
    }

    private suspend fun insertItem(item: DataSnapshot, nextItem: DataSnapshot?, userById: ICUserId, ownerId: String) {
        val sentBy = item.child("sentBy").value as String?
        val msg = ICChatMessage(item.key!!, item.child("timestamp").value as Long)
        try {
            msg.showAvatar = true
            nextItem?.let {
                val ns = it.child("sentBy").value as String?
                msg.showAvatar = ns != sentBy
            }

            msg.userSentAvatar = userById.avatarThumbnails?.small.toString()
            if (ownerId != sentBy) {
                msg.sendByUser = userById.id
                msg.userType = userById.type
                if (item.child("message").hasChild("content")) {
                    msg.chatMsgType = ChatMsgType.TYPE_INCOMING_TEXT
                    msg.textMessage = item.child("message").child("content")
                            .value as String
                }
                if (item.child("message").hasChild("image")) {
                    msg.chatMsgType = ChatMsgType.TYPE_INCOMING_IMG
                    msg.imageMsg = item.child("message")
                            .child("image").value as String?
                }
                if (item.child("message").hasChild("question")) {
                    msg.chatMsgType = ChatMsgType.TYPE_INCOMING_TEXT
                    msg.textMessage = item.child("message").child("question").child("content")
                            .value as String
                }
                if (item.child("message").hasChild("icheck_product")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_INCOMING_PRODUCT
                    msg.textMessage = item.child("message").child("content")
                            .value as String?
                    checkProduct(item, msg)
                }
                if (item.child("message").hasChild("sticker")) {
                    msg.chatMsgType = ChatMsgType.TYPE_INCOMING_IMG
                    msg.imageMsg = item.child("message")
                            .child("sticker")
                            .child("image").value as String?
                }

            } else {
                msg.sendByUser = SessionManager.session.user!!.id
                if (item.child("message").hasChild("content")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_OUTGOING_TEXT
                    msg.textMessage = item.child("message").child("content")
                            .value as String
                }
                if (item.child("message").hasChild("image")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_OUTGOING_IMG
                    msg.imageMsg = item.child("message")
                            .child("image").value as String?
                }
                if (item.child("message").hasChild("question") && item.child("message").hasChild("icheck_product")) {
                    msg.chatMsgType = ChatMsgType.TYPE_FIRST_BOT
                    msg.textMessage = item.child("message").child("question").child("content")
                            .value as String
                    checkProduct(item, msg)
                } else {
                    if (item.child("message").hasChild("question")) {
                        msg.chatMsgType = ChatMsgType.TYPE_OUTGOING_TEXT
                        msg.textMessage = item.child("message").child("question").child("content")
                                .value as String
                    }
                    if (item.child("message").hasChild("icheck_product")) {
                        msg.chatMsgType =
                                ChatMsgType.TYPE_OUTGOING_PRODUCTT
                        msg.textMessage = item.child("message").child("content")
                                .value as String?
                        checkProduct(item, msg)
                    }
                }

                if (item.child("message").hasChild("sticker")) {
                    msg.chatMsgType = ChatMsgType.TYPE_OUTGOING_IMG
                    msg.imageMsg = item.child("message")
                            .child("sticker")
                            .child("image").value as String?
                }
            }
            lastKey = msg.id
            chatMessageDao.insertChatMessage(msg)
        } catch (e: Exception) {
        }
    }

    private suspend fun insertGroupItem(item: DataSnapshot, nextItem: DataSnapshot?, ownerId: String) {
        val sentBy = item.child("sentBy").value as String?
        val msg = ICChatMessage(item.key!!, item.child("timestamp").value as Long)
        msg.showAvatar = true
        nextItem?.let {
            val ns = it.child("sentBy").value as String?
            msg.showAvatar = ns != sentBy
        }
        if (item.hasChild("message")) {
            if (sentBy != ownerId) {
                val uid = sentBy!!.replace("i-", "").toLong()
                val userById = ICNetworkClient.getSimpleApiClient()
                        .getUserById(uid)
                msg.sendByUser = userById.id
                msg.userType = userById.type
                msg.userSentAvatar = userById.avatarThumbnails?.small.toString()
                if (item.child("message").hasChild("content")) {
                    msg.chatMsgType = ChatMsgType.TYPE_INCOMING_TEXT
                    msg.textMessage = item.child("message").child("content")
                            .value as String
                }
                if (item.child("message").hasChild("image")) {
                    msg.chatMsgType = ChatMsgType.TYPE_INCOMING_IMG
                    msg.imageMsg = item.child("message")
                            .child("image").value as String?
                }
                if (item.child("message").hasChild("icheck_product")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_INCOMING_PRODUCT
                    msg.textMessage = item.child("message").child("content")
                            .value as String?
                    checkProduct(item, msg)
                }
                if (item.child("message").hasChild("product")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_INCOMING_PRODUCT
                    msg.textMessage = item.child("message").child("content")
                            .value as String?
                    checkProduct(item, msg)
                }
            } else {
                if (item.child("message").hasChild("content")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_OUTGOING_TEXT
                    msg.textMessage = item.child("message").child("content")
                            .value as String
                }
                if (item.child("message").hasChild("image")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_OUTGOING_IMG
                    msg.imageMsg = item.child("message")
                            .child("image").value as String?
                }
                if (item.child("message").hasChild("icheck_product")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_OUTGOING_PRODUCTT
                    msg.textMessage = item.child("message").child("content")
                            .value as String?
                    checkProduct(item, msg)
                }
                if (item.child("message").hasChild("product")) {
                    msg.chatMsgType =
                            ChatMsgType.TYPE_OUTGOING_PRODUCTT
                    msg.textMessage = item.child("message").child("content")
                            .value as String?
                    checkProduct(item, msg)
                }
            }
        } else if (item.hasChild("systemMessage")) {
            msg.chatMsgType = ChatMsgType.TYPE_SYSTEM_MSG
            val user = ICNetworkClient.getSimpleApiClient()
                    .getUserById(sentBy!!.replace("i-", "").toLong())
            if (item.child("systemMessage").hasChild("create")) {
                msg.textMessage = "${user.name} đã tạo nhóm chat"
            }
            if (item.child("systemMessage").hasChild("leave")) {
                msg.textMessage = "${user.name} đã rời nhóm chat"
            }
            if (item.child("systemMessage").hasChild("invite")) {
                var name = ""
                for (i in item.child("systemMessage").child("inviteTo").children) {
                    val n = ICNetworkClient.getSimpleApiClient()
                            .getUserById(i.key!!.replace("i-", "").toLong())
                    name += " ${n.name}"
                }
                msg.textMessage = "${user.name} đã mời ${item.child("systemMessage").child("inviteTo").childrenCount} thành viên"
            }
            if (item.child("systemMessage").hasChild("changeBackgroundBy")) {
                msg.textMessage = "${user.name} đã thay ảnh nền"
            }
            if (item.child("systemMessage").hasChild("changeLogoBy")) {
                msg.textMessage = "${user.name} đã thay ảnh đại diện"
            }
            if (item.child("systemMessage").hasChild("kick")) {
                val userKick = ICNetworkClient.getSimpleApiClient()
                        .getUserById(item.child("systemMessage").child("kick")
                                .child("target").children.first()
                                .key!!.replace("i-", "").toLong())
                msg.textMessage = "${user.name} đã kick thành viên ${userKick.name}"
            }
            if (item.child("systemMessage").hasChild("changeColorBy")) {
                msg.textMessage = "${user.name} đã thay màu"
            }
        }
        chatMessageDao.insertChatMessage(msg)
    }

    private suspend fun checkProduct(item: DataSnapshot, msg: ICChatMessage) {
        if (item.child("message").hasChild("product")) {
            val barcode = item.child("message")
                    .child("product").child("gtin_code").value as String?
            val filter = arrProduct.firstOrNull {
                it.barcode == barcode
            }
            if (filter != null) {
                msg.productBarcode = filter.barcode
                msg.productImg = filter.attachments.firstOrNull()?.thumbnails?.small.toString()
                msg.productPrice = filter.price
                msg.productName = filter.name
            } else {
                val product = ICNetworkClient.getSimpleApiClient().scanBarcodeChatV2(barcode)
                msg.productBarcode = product.barcode
                msg.productImg = product.attachments.firstOrNull()?.thumbnails?.small.toString()
                msg.productPrice = product.price
                msg.productName = product.name
                arrProduct.add(product)
            }
        } else if (item.child("message").hasChild("icheck_product")) {
            val barcode = item.child("message")
                    .child("icheck_product").child("gtin_code").value as String?
            val filter = arrProduct.firstOrNull {
                it.barcode == barcode
            }
            if (filter != null) {
                msg.productBarcode = filter.barcode
                msg.productImg = filter.attachments.firstOrNull()?.thumbnails?.small.toString()
                msg.productPrice = filter.price
                msg.productName = filter.name
            } else {
                val product = ICNetworkClient.getSimpleApiClient().scanBarcodeChatV2(barcode)
                msg.productBarcode = product.barcode
                msg.productImg = product.attachments.firstOrNull()?.thumbnails?.small.toString()
                msg.productPrice = product.price
                msg.productName = product.name
                arrProduct.add(product)
            }
        }
    }

    fun retry(icChatMessage: ICChatMessage, sellerType: Boolean) {
        chatMessageDao.deleteMessage(icChatMessage)
        sendMsg(icChatMessage.textMessage!!, sellerType)
    }

    fun sendMsg(msg: String, sellerType: Boolean) {
        val messageBody = hashMapOf<String, Any>()
        messageBody["content"] = msg
        val requestBody = hashMapOf<String, Any>()
        if (::icChatCodeResponse.isInitialized) {
            requestBody["roomId"] = icChatCodeResponse.dataRep.roomId
        } else {
            requestBody["roomId"] = this.groupId
        }
        requestBody["message"] = messageBody
        val icChatMessage = ICChatMessage("${System.currentTimeMillis()}", System.currentTimeMillis())
        icChatMessage.stateSendMessage = 1
        icChatMessage.textMessage = msg
        icChatMessage.sendByUser = SessionManager.session?.user?.id
        icChatMessage.chatMsgType = ChatMsgType.TYPE_OUTGOING_TEXT
        chatMessageDao.insertChatMessage(icChatMessage)
        viewModelScope.launch {
            try {
                ICNetworkClient.getSimpleChat().sendChat(requestBody)
                chatMessageDao.deleteMessage(icChatMessage)
                if (sellerType) {
                    TekoHelper.tagSellerChatSuccessful()
                } else {
                    TekoHelper.tagNonSellerChatSuccessful()
                }
            } catch (e: Exception) {
                icChatMessage.stateSendMessage = 3
                chatMessageDao.insertChatMessage(icChatMessage)
            }
        }
    }

    fun sendImg(url: String, sellerType: Boolean) {
        val messageBody = hashMapOf<String, Any>()
        messageBody["image"] = url
        val requestBody = hashMapOf<String, Any>()
        if (::icChatCodeResponse.isInitialized) {
            requestBody["roomId"] = icChatCodeResponse.dataRep.roomId
        } else {
            requestBody["roomId"] = this.groupId
        }
        requestBody["message"] = messageBody

        viewModelScope.launch {
            try {
                ICNetworkClient.getSimpleChat().sendChat(requestBody)
                if (sellerType) {
                    TekoHelper.tagSellerChatSuccessful()
                } else {
                    TekoHelper.tagNonSellerChatSuccessful()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun sendSticker(stickerView: StickerView, sellerType: Boolean) {
        val sticker = hashMapOf<String, Any>()
        sticker["id"] = stickerView.id
        sticker["image"] = stickerView.image
        sticker["packageID"] = stickerView.packageId
        val requestBody = hashMapOf<String, Any>()
        if (::icChatCodeResponse.isInitialized) {
            requestBody["roomId"] = icChatCodeResponse.dataRep.roomId
        } else {
            requestBody["roomId"] = this.groupId
        }
        val message = hashMapOf<String, Any>()
        message["sticker"] = sticker
        requestBody["message"] = message
        stickerView.lastUse = System.currentTimeMillis()
        viewModelScope.launch {
            try {
                stickerDao.insertSticker(stickerView)
                ICNetworkClient.getSimpleChat().sendChat(requestBody)
                if (sellerType) {
                    TekoHelper.tagSellerChatSuccessful()
                } else {
                    TekoHelper.tagNonSellerChatSuccessful()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun sendProduct(icBarcodeProductV2: ICBarcodeProductV1?) {
        if (icBarcodeProductV2 != null) {
            val productMap = hashMapOf<String, Any>()
            productMap["gtin_code"] = icBarcodeProductV2.barcode
            productMap["image_default"] = icBarcodeProductV2.image
            productMap["product_name"] = icBarcodeProductV2.name
            productMap["price_default"] = icBarcodeProductV2.price.toString()
            val requestBody = hashMapOf<String, Any>()
            if (::icChatCodeResponse.isInitialized) {
                requestBody["roomId"] = icChatCodeResponse.dataRep.roomId
            } else {
                requestBody["roomId"] = this.groupId
            }
            val message = hashMapOf<String, Any>()
            message["icheck_product"] = productMap
            requestBody["message"] = message
            viewModelScope.launch {
                try {
                    ICNetworkClient.getSimpleChat().sendChat(requestBody)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun sendQuestion(icChatMessage: ICChatMessage, sellerType: Boolean) {
        val questionBody = hashMapOf<String, Any?>()
        questionBody.put("id", icChatMessage.id)
        questionBody.put("content", icChatMessage.textMessage)
        val messageBody = hashMapOf<String, Any>()
        messageBody.put("question", questionBody)
        if (!sendProductBot) {
            sendProductBot = true
            val productMap = hashMapOf<String, Any>()
            productMap["gtin_code"] = icProductV1.barcode
            productMap["image_default"] = icProductV1.image
            productMap["product_name"] = icProductV1.name
            productMap["price_default"] = icProductV1.price.toString()
            messageBody.put("icheck_product", productMap)
        }
        val requestBody = hashMapOf<String, Any>()
        requestBody.put("roomId", icChatCodeResponse.dataRep.roomId)
        requestBody.put("message", messageBody)

        viewModelScope.launch {
            try {
                ICNetworkClient.getSimpleChat().sendChat(requestBody)
                if (sellerType) {
                    TekoHelper.tagSellerChatSuccessful()
                } else {
                    TekoHelper.tagNonSellerChatSuccessful()
                }
            } catch (e: java.lang.Exception) {
            }
        }

    }
}