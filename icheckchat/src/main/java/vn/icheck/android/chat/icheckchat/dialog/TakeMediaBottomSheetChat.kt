package vn.icheck.android.chat.icheckchat.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_choose_image_dialog_chat.*
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.helper.TakeMediaHelperChat
import java.io.File

class TakeMediaBottomSheetChat(val listener: TakeImageListener, private val selectMulti: Boolean = false, private val isVideo: Boolean = true, val activity: Activity) : BottomSheetDialogFragment() {

    companion object {
        var INSTANCE: TakeMediaBottomSheetChat? = null
    }

    var takeMediaHelper: TakeMediaHelperChat? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme).also { dialog ->
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                dialog.setOnShowListener {
                    val bottomSheet = dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_choose_image_dialog_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgClose.setOnClickListener {
            dismiss()
        }

        takeMediaHelper = TakeMediaHelperChat(listener, isVideo).apply {
            onTakeImageSuccess = {
                listener.onTakeMediaSuccess(it)
            }
        }

        if (isVideo) {
            tvTile.setText(R.string.chon_anh_video)
        } else {
            tvTile.setText(R.string.chon_anh)
        }

        val listImage = getImageFromGallery()
        if (listImage.isEmpty()) {
            startCamera()
        } else {
            val adapter = TakeMediaAdapter(listImage, selectMulti, isVideo)
            rcvImage.adapter = adapter

            btnSubmit.setOnClickListener {
                val list = mutableListOf<File>()
                for (i in adapter.getListSelected()) {
                    i.src?.let { it1 -> list.add(it1) }
                }

                if (list.isNotEmpty()) {
                    if (selectMulti) {
                        listener.onPickMuliMediaSucess(list)
                        dismiss()
                    } else {
                        listener.onPickMediaSucess(list[0])
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        INSTANCE = this
    }

    private fun getImageFromGallery(): MutableList<ICIMageFile> {
        val listOfAllImages = mutableListOf<ICIMageFile>()

        ShareHelperChat.getApplicationByReflect().let {
            val orderBy = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"
            val uri = MediaStore.Files.getContentUri("external")
            val selection = if (isVideo) {
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                        " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
            } else {
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            }

            val projection = arrayOf(
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.DURATION)

            val cursor = it.contentResolver.query(
                    uri,
                    projection,
                    selection,
                    null, orderBy)

            listOfAllImages.add(ICIMageFile(File("")))
            val dataColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val duration = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)

            while (cursor.moveToNext()) {
                val data = cursor.getString(dataColumn)
                val type = cursor.getInt(typeColumn)
                val duration = cursor.getLong(duration)

                if (!data.isNullOrEmpty()) {
                    listOfAllImages.add(ICIMageFile(src = File(data), type = type, duration = duration))
                }
            }
        }

        return listOfAllImages
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeMediaHelper?.onActivityResult(requestCode, resultCode)
        dismiss()
    }

    fun startCamera() {
        takeMediaHelper?.startTakeMedia(this@TakeMediaBottomSheetChat, activity)
    }

    data class ICIMageFile(
            val src: File? = null,
            var selected: Boolean = false,
            var position: Int = -1,
            var type: Int? = null, // 3: video , 1 : image,
            var duration: Long = 0 // 3: video , 1 : image,
    )

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null
    }


    interface TakeImageListener : TakeMediaHelperChat.TakeCameraListener {
        fun onPickMediaSucess(file: File)
        fun onPickMuliMediaSucess(file: MutableList<File>)
    }
}