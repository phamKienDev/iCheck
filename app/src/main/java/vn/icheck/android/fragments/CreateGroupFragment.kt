package vn.icheck.android.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import vn.icheck.android.R
import vn.icheck.android.activities.chat.CreateGroupChatActivity
import vn.icheck.android.adapters.ContactsAdapter
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICFollowing
import vn.icheck.android.util.pick_image.PickImageDialog
import java.io.ByteArrayOutputStream
import java.util.*

class CreateGroupFragment : Fragment(), ContactsAdapter.OnContactClick {

    private val data = mutableListOf<ICFollowing.Rows>()
    private val contactsAdapter = ContactsAdapter(this)
    companion object {
        const val UPLOAD_INTENT = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data.addAll(CreateGroupChatActivity.getInstance().getData())
        tv_group_numbers.text = "Thành viên (${data.size})"
        rcv_members.adapter = contactsAdapter
        rcv_members.layoutManager = LinearLayoutManager(context)
        img_change_avatar.setOnClickListener {
            startPickImage(UPLOAD_INTENT)
        }
        data.forEach {
            contactsAdapter.addItem(it)
        }
        edt_group_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                CreateGroupChatActivity.getInstance().setName(s.toString())
                if (s != null && s.isNotEmpty()) {
                    CreateGroupChatActivity.getInstance().setSuccess(true)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onContactChildClick(childContact: ICFollowing.Rows) {

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
//        if (resultCode == Activity.RESULT_OK) {
//            val uri = data?.data
//            Glide.with(context!!).load(uri).into(user_avatar)
//            lifecycleScope.launch(Dispatchers.IO) {
//                val bitmap = Glide.with(context!!)
//                        .asBitmap()
//                        .load(uri)
//                        .submit()
//                        .get()
//                val bos = ByteArrayOutputStream()
//                val dst = if (bitmap.width > bitmap.height) {
//                    bitmap.width
//                } else {
//                    bitmap.height
//                }
//                val scale = dst / 1024
//                var dstWidth = bitmap.width
//                var dstHeight = bitmap.height
//                if (scale >= 1) {
//                    dstWidth /= scale
//                    dstHeight /= scale
//                }
//                Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true)
//                        .compress(Bitmap.CompressFormat.WEBP, 100, bos)
//
//                try {
//                    val result = ICNetworkClient.getNewUploadClient().postImage(
//                            RequestBody.create(
//                                    MediaType.parse("image/jpeg"),
//                                    bos.toByteArray()
//                            ))
//
//                    CreateGroupChatActivity.getInstance().setAvatar(result.src)
//                } catch (e: Exception) {
//                    Log.e("e","${e.message}")
//                }
//            }
//        }
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
        val path = MediaStore.Images.Media.insertImage(activity?.contentResolver, bitmap, "${Calendar.getInstance().timeInMillis}", null)
        return Uri.parse(path)
    }

    private fun sendImg(uri: Uri?) {
        Glide.with(requireContext()).load(uri).into(user_avatar)
        lifecycleScope.launch(Dispatchers.IO) {

            val bitmap: Bitmap = Glide.with(requireContext().applicationContext)
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
                CreateGroupChatActivity.getInstance().setAvatar(response.src)
            } catch (e: Exception) {
            }
        }
    }

    private fun startPickImage(actionCode: Int) {
        val pickImageDialog = PickImageDialog(requireActivity(), UPLOAD_INTENT)
        pickImageDialog.show(requireActivity().supportFragmentManager, null)
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, actionCode)
    }
}