package vn.icheck.android.activities.chat.setting

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_v2_setting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import vn.icheck.android.R
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.util.DimensionUtil
import vn.icheck.android.util.pick_image.PickImageDialog
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.util.*

class ChatV2SettingActivity : AppCompatActivity() {

    var settingType = ERROR
    lateinit var chatV2SettingViewModel: ChatV2SettingViewModel
    lateinit var groupMemberAdapter: GroupMemberAdapter
    lateinit var fbId: String

    companion object {
        const val USER = 1
        const val GROUP = 2
        const val ERROR = 3
        const val CHAT_SETTING = 4
        const val UPLOAD_INTENT = 1
        fun create(activity: AppCompatActivity,
                   fbId: String,
                   type: Int,
                   name: String?,
                   avatar: String?,
                   userType: String?,
                   verified: Boolean?,
                   isOnline: Boolean?) {
            val i = Intent(activity, ChatV2SettingActivity::class.java)
            if (type != USER && type != GROUP) {
                throw ActivityNotFoundException()
            } else {
                i.putExtra("type", type)
            }
            i.putExtra("fbId", fbId)
            i.putExtra("name", name)
            i.putExtra("avatar", avatar)
            i.putExtra("isOnline", isOnline)
            i.putExtra("userType", userType)
            i.putExtra("verified", verified)
            activity.startActivityForResult(i, CHAT_SETTING)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_v2_setting)
        img_back.setOnClickListener {
            finish()
        }
        settingType = intent.getIntExtra("type", ERROR)
        fbId = intent.getStringExtra("fbId")
        chatV2SettingViewModel = ViewModelProvider(this, ChatV2SettingFactory(fbId, settingType))
                .get(ChatV2SettingViewModel::class.java)
//        val currentUser = "i-${SessionManager.session.user?.id}"
//        FirebaseDatabase.getInstance().getReference("room-metadata/$fbId/members/$currentUser/notification/turnOn")
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//
//                    override fun onDataChange(p0: DataSnapshot) {
//                        try {
//                            switch_notify.isChecked = p0.value as Boolean
//                        } catch (e: Exception) {
//                        }
//                    }
//
//                    override fun onCancelled(p0: DatabaseError) {
//                    }
//
//                })
        chatV2SettingViewModel.notifyLiveData.observe(this, Observer {
            switch_notify.isChecked = it
        })
        chatV2SettingViewModel.leaveChatLd.observe(this, Observer {
            setResult(Activity.RESULT_OK)
            finish()
        })
        switch_notify.setOnCheckedChangeListener { buttonView, isChecked ->
//            FirebaseDatabase.getInstance().getReference("room-metadata/$fbId/members/$currentUser/notification/turnOn")
//                    .setValue(isChecked)
            chatV2SettingViewModel.setNotify(isChecked)
        }
        btn_delete_chat.setOnClickListener {
//            FirebaseDatabase.getInstance()
//                    .getReference("room-users/$currentUser/$fbId")
//                    .removeValue(object : DatabaseReference.CompletionListener {
//                        override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {
//                            setResult(Activity.RESULT_OK)
//                            finish()
//                        }
//                    })
            val leaveGroupDialog = LeaveGroupDialog(settingType, WeakReference(this))
            leaveGroupDialog.show(supportFragmentManager, null)
//            chatV2SettingViewModel.leaveChat()
        }
        val name = intent.getStringExtra("name")
        tv_user_name.text = name
        if (settingType == USER) {
            rcv_members.visibility = View.GONE
            tv_title.text = "Cài đặt"
            tv_member_count.visibility = View.GONE

            div.visibility = View.GONE
            gr_member.visibility = View.GONE


            val ava = intent.getStringExtra("avatar")
            val userType = intent.getStringExtra("userType")
            if (userType == "user") {
                val isO = intent.getBooleanExtra("isOnline", false)
                if (isO) {
                    ic_online.setImageResource(R.drawable.ic_online_big)
                } else {
                    ic_online.setImageResource(R.drawable.ic_offline_big)
                }

                Glide.with(this.applicationContext)
                        .load(ava)
                        .placeholder(R.drawable.ic_avatar_default_84px)
                        .error(R.drawable.ic_avatar_default_84px)
                        .into(user_avatar)
            } else if (userType == "page") {
                val verified = intent.getBooleanExtra("verified", false)
                user_avatar.borderWidth = DimensionUtil.convertDpToPixel(2f, this).toInt()
                user_avatar.borderColor = Color.parseColor("#057DDA")
                if (verified) {
                    img_text.visibility = View.VISIBLE
                    ic_online.setImageResource(R.drawable.ic_home_verify_16dp)
                } else {
                    ic_online.visibility = View.GONE
                }
                Glide.with(this.applicationContext)
                        .load(ava)
                        .placeholder(R.drawable.img_default_business_logo_big)
                        .error(R.drawable.img_default_business_logo_big)
                        .into(user_avatar)
            } else {
                ic_online.visibility = View.GONE
                user_avatar.borderWidth = DimensionUtil.convertDpToPixel(2f, this).toInt()
                user_avatar.borderColor = Color.parseColor("#FF6422")
                img_text.setImageResource(R.drawable.text_seller)
                img_text.visibility = View.VISIBLE
                Glide.with(this.applicationContext)
                        .load(ava)
                        .placeholder(R.drawable.img_shop_default_big)
                        .error(R.drawable.img_shop_default_big)
                        .into(user_avatar)
            }

        } else if (settingType == GROUP) {
            tv_user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_edit_name, 0)
            ic_online.visibility = View.GONE
            tv_user_name.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val textLocation = intArrayOf(0, 0)
                    tv_user_name.getLocationOnScreen(textLocation)
                    if (event.getRawX() >= textLocation[0] + tv_user_name.width - tv_user_name.totalPaddingRight) {
                        val dialog = ChangeNameDialog(tv_user_name.text.toString(), WeakReference(this))
                        dialog.show(supportFragmentManager, null)
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener true
            }

            img_edit_img.visibility = View.VISIBLE
            img_edit_img.setOnClickListener {
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
            val ava = intent.getStringExtra("avatar")
            Glide.with(this.applicationContext)
                    .load(ava)
                    .placeholder(R.drawable.group_chat_placeholder)
                    .error(R.drawable.group_chat_placeholder)
                    .into(user_avatar)
            FirebaseDatabase.getInstance()
                    .getReference("room-metadata/$fbId/logo")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            try {
                                Glide.with(this@ChatV2SettingActivity.applicationContext)
                                        .load(p0.getValue(String::class.java))
                                        .placeholder(R.drawable.group_chat_placeholder)
                                        .error(R.drawable.group_chat_placeholder)
                                        .into(user_avatar)
                            } catch (e: Exception) {
                            }
                        }
                    })
            chatV2SettingViewModel.addMembers.observe(this, Observer {
                switch_add_mem.isChecked = it
            })
            switch_add_mem.setOnCheckedChangeListener { buttonView, isChecked ->
                chatV2SettingViewModel.changeAddMem(isChecked)
            }
            chatV2SettingViewModel.listUserById.observe(this, Observer {
                tv_member_count.text = "${it.size} thành viên"
                groupMemberAdapter = GroupMemberAdapter(it)
                rcv_members.adapter = groupMemberAdapter
                rcv_members.layoutManager = LinearLayoutManager(this).apply {
                    this.orientation = LinearLayoutManager.HORIZONTAL
                }
            })
//            FirebaseDatabase.getInstance()
//                    .getReference("room-metadata/$fbId/members")
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onCancelled(p0: DatabaseError) {
//                        }
//
//                        override fun onDataChange(p0: DataSnapshot) {
//                            for (item in p0.children) {
//                                listUser.add(item.key)
//                            }
//                        }
//                    })
        }
    }

    fun changeName(name: String?) {
        FirebaseDatabase.getInstance()
                .getReference("room-metadata/$fbId/name")
                .setValue(name)
        tv_user_name.text = name
    }

    fun leaveChat() {
        chatV2SettingViewModel.leaveChat()
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


    private fun sendImg(uri: Uri?) {
        user_avatar.setImageURI(uri)
        lifecycleScope.launch(Dispatchers.IO) {

            val bitmap: Bitmap = Glide.with(this@ChatV2SettingActivity.applicationContext)
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
                        RequestBody.create(
                                "image/jpeg".toMediaTypeOrNull(),
                                bos.toByteArray()
                        ))
                FirebaseDatabase.getInstance()
                        .getReference("room-metadata/$fbId/logo")
                        .setValue(response.src)
            } catch (e: Exception) {
            }
        }
    }
}
