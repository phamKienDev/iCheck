package vn.icheck.android.activities.chat.v2

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.multidex.BuildConfig
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat_v2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.activities.chat.ContactAdminDialog
import vn.icheck.android.activities.chat.ScanForChatActivity
import vn.icheck.android.activities.chat.setting.ChatV2SettingActivity
import vn.icheck.android.activities.chat.sticker.StickerAdapter
import vn.icheck.android.activities.chat.sticker.StickerPackagesDao
import vn.icheck.android.activities.chat.sticker.StickerStoreActivity
import vn.icheck.android.activities.chat.sticker.StickerView
import vn.icheck.android.activities.chat.v2.model.ICChatMessage
import vn.icheck.android.activities.image.DetailImagesActivity
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICUserId
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.profile.ProfileActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.pick_image.PickImageDialog
import vn.icheck.android.util.text.MessageTimeUtil
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

class ChatV2Activity : AppCompatActivity() {

    lateinit var chatV2ViewModel: ChatV2ViewModel
    var icBarcodeProductV1: ICBarcodeProductV1? = null
    lateinit var chatPagedAdapter: ChatPagedAdapter
    var chatType = 1
    lateinit var stickerPackageAdapter: StickerAdapter
    lateinit var detailStickerAdapter: StickerAdapter
    var emoji = true
    private val requestStorage = 1

    private var sellerType = false

    companion object {
        const val UPLOAD_INTENT = 1
        const val SCAN = 2
        const val STICKER_STORE = 3
        var instance: ChatV2Activity? = null
        var stickerPackagesDao: StickerPackagesDao? = null
        var positionShowTime = -1
        fun createChatUser(userId: Long?, activity: Activity) {
            val intent = Intent(activity, ChatV2Activity::class.java)
            intent.putExtra("userId", userId)
            activity.startActivityForResult(intent, 1)
        }

        fun createNewChat(userId: Long?, activity: Activity) {
            val intent = Intent(activity, ChatV2Activity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("type", 1)
            activity.startActivityForResult(intent, 1)
        }

        fun createGroupChat(roomID: String, roomName: String, avatar: String?, activity: Activity) {
            val intent = Intent(activity, ChatV2Activity::class.java)
            intent.putExtra("roomId", roomID)
            intent.putExtra("roomName", roomName)
            intent.putExtra("avatar", avatar)
            intent.putExtra("type", 2)
            activity.startActivityForResult(intent, 1)
        }

        fun createChatBot(userId: Long?, barcode: String?, activity: Activity) {
            val intent = Intent(activity, ChatV2Activity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("type", 3)
            if (barcode != null) {
                intent.putExtra("barcode", barcode)
            }
            activity.startActivityForResult(intent, 1)
        }

        fun createChatBotIcheck(barcode: String?, activity: Activity) {
            val intent = Intent(activity, ChatV2Activity::class.java)
            val userId = if (BuildConfig.BUILD_TYPE == "release") {
                102328L
            } else {
                100790L
            }
            if (barcode != null) {
                intent.putExtra("barcode", barcode)
            }
            intent.putExtra("userId", userId)
            intent.putExtra("type", 3)
            activity.startActivityForResult(intent, 1)
        }

        fun createChatIcheck(activity: Activity) {
            val intent = Intent(activity, ChatV2Activity::class.java)
            val userId = if (BuildConfig.BUILD_TYPE == "release") {
                102328L
            } else {
                100790L
            }
            intent.putExtra("userId", userId)
            activity.startActivityForResult(intent, 1)
        }
    }

    private val onStickerClick = object : StickerAdapter.OnStickerClick {
        override fun sendSticker(stickerView: StickerView) {
            this@ChatV2Activity.sendSticker(stickerView)
        }

        override fun getSticker(id: String) {
            this@ChatV2Activity.getSticker(id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_v2)
        EventBus.getDefault().register(this)
        instance = this
        positionShowTime = -1
        chatV2ViewModel = ViewModelProvider(this).get(ChatV2ViewModel::class.java)
        chatPagedAdapter = ChatPagedAdapter()
        stickerPackagesDao = AppDatabase.getDatabase(this@ChatV2Activity).stickerPackagesDao()
        messagesList.adapter = chatPagedAdapter
        messagesList.layoutManager =
                WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        chatType = intent.getIntExtra("type", 1)
        if (chatType == 1) {
            val userId = intent.getLongExtra("userId", 0L)
            chatV2ViewModel.setUpUserChat(userId)
        } else if (chatType == 2) {
            textView12.text = intent.getStringExtra("roomName")
            val groupId = intent.getStringExtra("roomId")
            chatV2ViewModel.setUpGroupChat(groupId)
            FirebaseDatabase.getInstance()
                    .getReference("room-metdata/$groupId/name")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            textView12.text = p0.getValue(String::class.java)
                        }
                    })
        } else {
            val userId = intent.getLongExtra("userId", 0L)
            val barcode = intent.getStringExtra("barcode")
            chatV2ViewModel.setUpChatBot(userId, barcode)
        }
        chatV2ViewModel.userLive.observe(this, Observer {
            textView12.text = it.name
            textView12.setOnClickListener { _ ->
                showUser(it)
            }
            if ("shop" == it.type) {
//                        textView12.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.text_seller, 0)
                imgType.visibility = View.VISIBLE
                imgType.setImageResource(R.drawable.text_seller)
                it.name?.let { it1 ->
                    TekoHelper.tagSellerChatClicked(it1)
                }
                sellerType = true
            }
            if (it?.verified != null && it.verified!!) {
//                        textView12.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.text_verified, 0)
                imgType.visibility = View.VISIBLE
                imgType.setImageResource(R.drawable.text_verified)
                it.name?.let { it1 ->
                    TekoHelper.tagNonSellerChatClicked(it1)
                }
            }
        })
        chatV2ViewModel.onlineLiveData.observe(this, androidx.lifecycle.Observer {
            if (imgType.visibility != View.VISIBLE) {
                if (it) {
                    tv_is_online.text = "Online"
                } else {
                    tv_is_online.text = "Offline"
                }
            }
        })
        chatV2ViewModel.lastOnlineLiveData.observe(this, androidx.lifecycle.Observer {
            if (!chatV2ViewModel.online && imgType.visibility != View.VISIBLE) {
                tv_is_online.text = "Hoạt động " + MessageTimeUtil(it).getTime()
            }
        })
        chatV2ViewModel.listChatMessage.observe(this, Observer {
            lifecycleScope.launch {
                delay(300)
                loading_progress_bar.visibility = View.GONE
                messagesList.visibility = View.VISIBLE
                chatPagedAdapter.submitList(it)
            }
//            messagesList.smoothScrollToPosition(0)
        })

        val shared = getSharedPreferences("STICKER_PACKAGES", Context.MODE_PRIVATE)
        val getFirst = shared.getBoolean("get_sticker_packages_first", true)
        if (getFirst) {
            chatV2ViewModel.insertStickerPackages()
        } else {
            initRcvStickerPackages()
        }

        img_more.setOnClickListener {
            val admin = if (BuildConfig.BUILD_TYPE == "release") {
                102328L
            } else {
                100790L
            }
            if (chatV2ViewModel.userLive.value?.type == "page" || chatV2ViewModel.userLive.value?.type == "shop" && chatV2ViewModel.userLive.value?.id != admin) {
                val contactDialog = ContactAdminDialog()
                contactDialog.show(supportFragmentManager, null)
            } else {
                if (chatType == 1) {
                    ChatV2SettingActivity.create(
                            this,
                            chatV2ViewModel.icChatCodeResponse.dataRep.roomId,
                            1,
                            chatV2ViewModel.userLive.value?.name,
                            chatV2ViewModel.userLive.value?.avatarThumbnails?.small.toString(),
                            chatV2ViewModel.userLive.value?.type,
                            chatV2ViewModel.userLive.value?.verified,
                            chatV2ViewModel.onlineLiveData.value
                    )
                } else if (chatType == 2) {
                    ChatV2SettingActivity.create(
                            this,
                            intent.getStringExtra("roomId"),
                            2,
                            intent.getStringExtra("roomName"),
                            intent.getStringExtra("avatar"),
                            chatV2ViewModel.userLive.value?.type,
                            chatV2ViewModel.userLive.value?.verified,
                            chatV2ViewModel.onlineLiveData.value
                    )
                }

            }
        }
        img_scan_barcode.setOnClickListener {
            val i = Intent(this, ScanForChatActivity::class.java)
            startActivityForResult(i, SCAN)
        }
        btn_submit.setOnClickListener {
            if (!emoji) {
                if (edt_input.text.isNotEmpty()) {
                    chatV2ViewModel.sendMsg(edt_input.text.toString(), sellerType)
                    edt_input.setText("")
                }
                if (icBarcodeProductV1 != null) {
                    chatV2ViewModel.sendProduct(icBarcodeProductV1)
                    icBarcodeProductV1 = null
                    barcode_panel.visibility = View.GONE
                }
            } else {
//                rcv_stickers.visibility = View.VISIBLE
                container_sticker.visibility = View.VISIBLE
                sticker_wrapper.visibility = View.VISIBLE
            }
        }
        img_back.setOnClickListener {
            finish()
        }
        img_add_more.setOnClickListener {
            StickerStoreActivity.start(this, STICKER_STORE)
        }
        edt_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    btn_submit.setImageResource(R.drawable.ic_sticker)
                    emoji = true
                } else {
                    emoji = false
                    btn_submit.setImageResource(R.drawable.ic_send_blue_24px)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        chatV2ViewModel.newItem.observe(this, Observer {
            lifecycleScope.launch {
                delay(200)
                messagesList.smoothScrollToPosition(0)
            }
        })

        chatV2ViewModel.stickers.observe(this, Observer {
            val arr = arrayListOf<StickerView>()
            for (item in it.data) {
                arr.add(StickerView(item.id, item.image).apply {
                    packageId = item.packageID
                })
            }
            if (arr.isNullOrEmpty()) {
                img_empity_sticker.visibility = View.VISIBLE
                txt_empity_sticker.visibility = View.VISIBLE
            } else {
                img_empity_sticker.visibility = View.GONE
                txt_empity_sticker.visibility = View.GONE
            }
            detailStickerAdapter = StickerAdapter(arr, 2)
            detailStickerAdapter.onStickerClick = onStickerClick
            rcv_stickers.adapter = detailStickerAdapter
            rcv_stickers.layoutManager = GridLayoutManager(this, 4)

        })

        img_add_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1)
            } else if (!PermissionHelper.isAllowPermission(this, Manifest.permission.CAMERA)) {
                PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, 2)
            } else {
                val pickImageDialog = PickImageDialog(this, UPLOAD_INTENT)
                pickImageDialog.show(supportFragmentManager, null)
            }

        }
        img_history_sticker.setOnClickListener {
            chatV2ViewModel.getStickerHistory()
        }
    }

    fun initRcvStickerPackages() {
        val arr = arrayListOf<StickerView>()
        stickerPackagesDao?.let {
            for (item in it.getAllStickerPackges()) {
                arr.add(StickerView(item.id, item.thumbnail))
            }
            stickerPackageAdapter = StickerAdapter(arr, 1)
            stickerPackageAdapter.onStickerClick = onStickerClick
            rcv_sticker_packages.adapter = stickerPackageAdapter
            rcv_sticker_packages.layoutManager = WrapContentLinearLayoutManager(this).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }

            if (arr.isNotEmpty()) {
                getSticker(arr[0].id)
            } else {
                chatV2ViewModel.getStickerHistory()
            }
        }
    }

    fun retry(icChatMessage: ICChatMessage) {
        chatV2ViewModel.retry(icChatMessage, sellerType)
    }

    fun getSticker(id: String) {
        try {
            chatV2ViewModel.getStickers(id)
        } catch (e: Exception) {
            Log.e("e", "${e.message}")
        }
    }

    fun sendSticker(stickerView: StickerView) {
//        rcv_stickers.visibility = View.GONE
        container_sticker.visibility = View.GONE
        sticker_wrapper.visibility = View.GONE
        chatV2ViewModel.sendSticker(stickerView, sellerType)
    }

    fun sendChatBot(icChatMessage: ICChatMessage) {
        chatV2ViewModel.sendQuestion(icChatMessage, sellerType)
    }

    override fun onBackPressed() {
//        if (sticker_wrapper.visibility == View.VISIBLE) {
//            rcv_stickers.visibility = View.GONE
//            sticker_wrapper.visibility = View.GONE
        if (container_sticker.visibility == View.VISIBLE) {
            container_sticker.visibility = View.GONE
            sticker_wrapper.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ICheckApplication.getInstance().mFirebase.currentRoomId = ""
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPLOAD_INTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data
                    if (uri != null) {
                        sendImg(uri)
                    }
                }
            }
            SCAN -> {
                lifecycleScope.launch {
                    if (resultCode == Activity.RESULT_OK) {
                        val barcode = data?.getStringExtra("barcode")

                        val result = data?.getStringExtra("qrcode")
                        if (!result.isNullOrEmpty()) {
                            edt_input.setText(result)
                        }

                        if (barcode != null) {
                            val response = ICNetworkClient.getSimpleApiClient().scanBarcode(barcode)
                            if (!response.attachments.isNullOrEmpty()) {
                                Glide.with(this@ChatV2Activity.applicationContext)
                                        .load(response.attachments.first().thumbnails.small)
                                        .error(R.drawable.error_load_image)
                                        .into(img_barcode_product)
                            }
                            tv_barcode_name.text = response.name
                            tv_barcode.text = response.barcode
                            img_close_barcode.setOnClickListener {
                                barcode_panel.visibility = View.GONE
                            }
                            barcode_panel.visibility = View.VISIBLE
                            icBarcodeProductV1 = response
                        }

                    }
                }
            }
            PickImageDialog.TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    imageBitmap?.also {
                        val uri = getImageUri(imageBitmap)
                        sendImg(uri)
                    } ?: kotlin.run {
                        val uri = PickImageDialog.uri
                        uri?.let {
                            sendImg(uri)
                        }
                    }
                }
            }
            STICKER_STORE -> {
                if (resultCode == Activity.RESULT_OK) {
//                    for (item in chatV2ViewModel.stickerPack.value!!.data) {
//                        if (ICheckApplication.getInstance().getStickerPreference(item.id)) {
//                            stickerPackageAdapter.listSticker.add(StickerView(item.id, item.thumbnail))
//                        }
//                    }
                    stickerPackageAdapter.listSticker.clear()
                    stickerPackagesDao?.let {
                        for (item in it.getAllStickerPackges()) {
                            stickerPackageAdapter.listSticker.add(StickerView(item.id, item.thumbnail))
                        }
                        if (it.getAllStickerPackges().isNotEmpty()) {
                            getSticker(it.getAllStickerPackges()[0].id)
                        } else {
                            chatV2ViewModel.getStickerHistory()
                            detailStickerAdapter.listSticker.clear()
                            detailStickerAdapter.notifyDataSetChanged()
                        }
                    }
                    stickerPackageAdapter.notifyDataSetChanged()
                }
            }
            ChatV2SettingActivity.CHAT_SETTING -> {
                if (resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            }
        }
    }

    private fun sendImg(uri: Uri?) {
        lifecycleScope.launch(Dispatchers.IO) {

            val bitmap: Bitmap = Glide.with(this@ChatV2Activity.applicationContext)
                    .asBitmap()
                    .load(uri)
                    .submit()
                    .get()
            val bos = ByteArrayOutputStream()
            val dst = if (bitmap.width > bitmap.height) {
                bitmap.width
            } else {
                bitmap.height
            }

            val scale = dst / 1024
            var dstWidth = bitmap.width
            var dstHeight = bitmap.height
            if (scale >= 1) {
                dstWidth /= scale
                dstHeight /= scale
            }
            Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true)
                    .compress(Bitmap.CompressFormat.JPEG, 100, bos)
            try {
                val response = ICNetworkClient.getNewUploadClient().postImage(
                        bos.toByteArray()
                                .toRequestBody("image/jpeg".toMediaTypeOrNull(),
                                        0, bos.size()))
                chatV2ViewModel.sendImg(response.src, sellerType)
            } catch (e: Exception) {
            }
        }
    }

    fun getImageUri(bitmap: Bitmap): Uri {
        val bos = ByteArrayOutputStream()
        val dst = if (bitmap.width > bitmap.height) {
            bitmap.width
        } else {
            bitmap.height
        }
        val scale = dst / 1024
        var dstWidth = bitmap.width
        var dstHeight = bitmap.height
        if (scale >= 1) {
            dstWidth /= scale
            dstHeight /= scale
        }
        Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true)
                .compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "${Calendar.getInstance().timeInMillis}", null)
        return Uri.parse(path)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.NEW_MESSAGE) {
            messagesList.smoothScrollToPosition(0)
        }
    }

    fun showImage(url: String) {
        DetailImagesActivity.start(arrayListOf(url), this)
    }

    fun showProduct(barcode: String) {
//        ProductDetailActivity.start(barcode, this)
    }

    fun showUser(userId: Long?, userType: String?) {
        when (userType) {
            "user" -> ActivityUtils.startActivity<ProfileActivity, Long>(this, Constant.DATA_1, userId!!)
            "page" -> ActivityUtils.startActivity<PageDetailActivity, Long>(this@ChatV2Activity, Constant.DATA_1, userId!!)
//            "shop" -> ShopDetailActivity.start(userId, this)
        }
    }

    fun showUser(icUserId: ICUserId) {
        when (icUserId.type) {
            "user" -> ActivityUtils.startActivity<ProfileActivity, Long>(this, Constant.DATA_1, icUserId.id!!)
            "page" -> ActivityUtils.startActivity<PageDetailActivity, Long>(this@ChatV2Activity, Constant.DATA_1, icUserId.id!!)
//            "shop" -> ShopDetailActivity.start(icUserId.id, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            requestStorage -> {
                if (!PermissionHelper.checkResult(grantResults)) {
                    ToastUtils.showShortError(this, R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun showDialogCopyText(view: View, menu: Int, text: String, url: String, image: ImageView?) {
        val wrapper = ContextThemeWrapper(this, R.style.PopupMenuCopy)
        val popupMenu = PopupMenu(wrapper, view)
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_copy_text -> {
                    val clip = ClipData.newPlainText("label", text)
                    clipboardManager.setPrimaryClip(clip)
                }
                R.id.item_copy_link -> {
                    val clip = ClipData.newPlainText("label", url)
                    clipboardManager.setPrimaryClip(clip)
                }
                R.id.item_download -> {
                    downloadImage(image!!)
                }
            }
            false
        }
        popupMenu.menuInflater.inflate(menu, popupMenu.menu)
        popupMenu.show()
    }

    fun downloadImage(image: ImageView) {
        if (!PermissionHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, requestStorage)) {
            return
        }

        var outputStream: FileOutputStream? = null
        val drawble = image.drawable as BitmapDrawable
        val bitmap = drawble.bitmap

        val filePath: File = Environment.getExternalStorageDirectory()
        val dir = File(filePath.absolutePath + "/icheck/")
        dir.mkdir()
        val file = File(dir, "${System.currentTimeMillis()}.jpg")

        try {
            outputStream = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.flush()
            outputStream?.close()
        } catch (e: IOException) {

        }
    }

    fun setUpGetStickerPackages() {
        val shared = getSharedPreferences("STICKER_PACKAGES", Context.MODE_PRIVATE)
        var edit: SharedPreferences.Editor = shared.edit()
        edit.putBoolean("get_sticker_packages_first", false)
        edit.apply()
    }

    fun hideTime(position: Int) {
        messagesList.findViewHolderForLayoutPosition(position)?.let {
            if (it is ChatPagedAdapter.IncomingTextHolder) {
                it.hideTextTime()
            } else if (it is ChatPagedAdapter.OutgoingTextHolder) {
                it.hideTextTime()
            }
        }
    }

}
