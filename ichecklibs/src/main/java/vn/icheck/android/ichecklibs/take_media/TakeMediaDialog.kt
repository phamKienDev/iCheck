package vn.icheck.android.ichecklibs.take_media

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_take_media.*
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.base_dialog.BaseBottomSheetDialogFragment
import java.io.File

class TakeMediaDialog(val activity: Activity,
                      val listener: TakeMediaListener,
                      private val selectMulti: Boolean = false, // cho chọn nhiều ảnh/video
                      private val cropImage: Boolean = false,  // cho phép chuyển sang màn Crop
                      private val ratio: String? = null,  // tỉ lệ Crop ảnh
                      private val isVideo: Boolean = true, // cho chọn video hay không?
                      val disableTakeImage: Boolean = false, // cho chụp ảnh hay không?
                      val saveImageToGallery: Boolean = false, // cho phép lưu ảnh
                      val maxSelectCount: Int? = null // số lượng chọn tối đa
) : BaseBottomSheetDialogFragment() {

    companion object {
        var INSTANCE: TakeMediaDialog? = null
        const val CROP_IMAGE_GALLERY = 1

        fun show(fragmentManager: FragmentManager, activity: Activity, listener: TakeMediaListener, selectMulti: Boolean = false, cropImage: Boolean = false, ratio: String? = null, isVideo: Boolean = false, disableTakeImage: Boolean = false, saveImageToGallery: Boolean = false, maxSelectCount: Int? = null) {
            if (fragmentManager.findFragmentByTag(TakeMediaDialog::class.java.simpleName)?.isAdded != true) {
                TakeMediaDialog(activity, listener, selectMulti, cropImage, ratio, isVideo, disableTakeImage, saveImageToGallery, maxSelectCount).show(fragmentManager, TakeMediaDialog::class.java.simpleName)
            }
        }
    }

    var takeMediaHelper: TakeMediaHelper? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_take_media, container, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        imgClose.setOnClickListener {
            dismiss()
        }

        if (cropImage) {
            takeMediaHelper = TakeMediaHelper(activity, listener).apply {
                onTakeImageSuccess = {

                    listener.onStartCrop(it?.absolutePath, null, ratio, CROP_IMAGE_GALLERY)
                }
            }
        } else {
            takeMediaHelper = TakeMediaHelper(activity, listener, isVideo).apply {
                onTakeImageSuccess = {
                    listener.onTakeMediaSuccess(it)
                }
            }
        }

        if (saveImageToGallery) {
            takeMediaHelper?.context = requireContext()
            takeMediaHelper?.saveImageToGallery = true
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
            val adapter = TakeMediaAdapter(listImage, selectMulti, isVideo, disableTakeImage, maxSelectCount)
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
                        if (cropImage) {
                            listener.onStartCrop(list[0].absolutePath, null, ratio, CROP_IMAGE_GALLERY)
                        } else {
                            listener.onPickMediaSucess(list[0])
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        btnSubmit.background = ViewHelper.bgOutlinePrimary1Corners4(requireContext())
    }

    override fun onResume() {
        super.onResume()
        INSTANCE = this
    }

    private fun getImageFromGallery(): MutableList<ICIMageFile> {
        val listOfAllImages = mutableListOf<ICIMageFile>()

        activity.let {
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

            if (!disableTakeImage) {
                listOfAllImages.add(ICIMageFile(File("")))
            }

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
        takeMediaHelper?.onActivityResult(requestCode, resultCode, data)
        if (!cropImage) {
            dismiss()
        } else {
            if (requestCode == CROP_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
                data?.getStringExtra(Constant.DATA_1)?.let { url ->
                    listener.onPickMediaSucess(File(url))
                    dismiss()
                }
            }
        }

    }

    fun startCamera() {
        takeMediaHelper?.startTakeMedia(this@TakeMediaDialog)
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
}