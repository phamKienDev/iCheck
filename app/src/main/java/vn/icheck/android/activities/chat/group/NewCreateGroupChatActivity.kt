package vn.icheck.android.activities.chat.group

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_create_group_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import vn.icheck.android.R
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.util.pick_image.PickImageDialog
import java.io.ByteArrayOutputStream
import java.util.*

class NewCreateGroupChatActivity : AppCompatActivity() {
    lateinit var createGroupViewModel: CreateGroupViewModel
    private var createGroupSnackbar:Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_create_group_chat)
        createGroupViewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)
        createGroupViewModel.friendLiveData.observe(this, Observer {
            if (it.count > 0) {
                val addMemFragment = AddMemFragment()
                supportFragmentManager.beginTransaction().add(
                        R.id.create_group_container, addMemFragment, null
                ).addToBackStack(null).commit()
            } else {
                tv_no_friend.visibility = View.VISIBLE
            }
        })
        tv_next.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount == 1) {
                val namingGroupFragment = NamingGroupFragment()
                supportFragmentManager.beginTransaction().replace(
                        R.id.create_group_container, namingGroupFragment, null
                ).addToBackStack(null).commit()
                tv_next.visibility = View.GONE
            }
        }
        tv_create.setOnClickListener {
            val name = createGroupViewModel.groupName.value
            if (name?.trim().isNullOrEmpty()) {
                Snackbar.make(root, "Không để trống tên!", Snackbar.LENGTH_SHORT).show()
            } else {
                createGroupSnackbar = Snackbar.make(root,"Đang tạo nhóm chat", Snackbar.LENGTH_INDEFINITE)
                createGroupSnackbar?.show()
                val createRoom = hashMapOf<String, Any>()
                createRoom.put("name", name!!)
                val logo = createGroupViewModel.groupAvatar.value
                if (!logo.isNullOrEmpty()) {
                    createRoom.put("logo", logo)
                }
                val listMemberId = mutableListOf<String>()
                val data = createGroupViewModel.listMember.value
                if (data != null) {
                    for (item in data) {
                        listMemberId.add("i-"+item.id)
                    }
                }
                createRoom.put("members", listMemberId)
                lifecycleScope.launch {
                    try {
                        ICNetworkClient.getSimpleChat()
                                .createRoomChat(createRoom)
                        createGroupSnackbar?.dismiss()
                        finish()
                    } catch (e: Exception) {
                    }
                }
            }
        }
        createGroupViewModel.groupName.observe(this, Observer {
            if (it.trim().isNotEmpty()) {
                tv_create.visibility = View.VISIBLE
            } else {
                tv_create.visibility = View.GONE
            }
        })
        createGroupViewModel.listMember.observe(this, Observer {
            if (it.isNotEmpty()) {
                tv_next.visibility = View.VISIBLE
            } else {
                tv_next.visibility = View.GONE
            }
        })
        img_back.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
                tv_next.visibility = View.VISIBLE
            } else {
                finish()
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1  -> {
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
//        Glide.with(context!!)
//                .load(uri)
//                .error(R.drawable.group_chat_placeholder)
//                .placeholder(R.drawable.group_chat_placeholder)
//                .into(user_avatar)
        lifecycleScope.launch(Dispatchers.IO) {

            val bitmap: Bitmap = Glide.with(this@NewCreateGroupChatActivity.applicationContext)
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
                createGroupViewModel.groupAvatar.postValue(response.src)
            } catch (e: Exception) {
            }
        }
    }
}
