package vn.icheck.android.component.take_media

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.layout_choose_image_dialog.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.cropimage.CropImageActivity
import java.io.File

class TakeMediaDialog(val listener: TakeImageListener, private val selectMulti: Boolean = false, private val cropImage: Boolean = false, private val ratio: String? = null, private val isVideo: Boolean = true) : BaseBottomSheetDialogFragment() {

    companion object {
        var INSTANCE: TakeMediaDialog? = null
        const val CROP_IMAGE_GALLERY = 1

        fun show(fragmentManager: FragmentManager, listener: TakeImageListener, selectMulti: Boolean = false, cropImage: Boolean = false, ratio: String? = null, isVideo: Boolean = false) {
            if (fragmentManager.findFragmentByTag(TakeMediaDialog::class.java.simpleName)?.isAdded != true) {
                TakeMediaDialog(listener, selectMulti, cropImage, ratio, isVideo).show(fragmentManager, TakeMediaDialog::class.java.simpleName)
            }
        }
    }

    var takeMediaHelper: TakeMediaHelper? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_choose_image_dialog, container, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_DISMISS))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgClose.setOnClickListener {
            dismiss()
        }

        if (cropImage) {
            takeMediaHelper = TakeMediaHelper(listener).apply {
                onTakeImageSuccess = {
                    CropImageActivity.start(this@TakeMediaDialog, it?.absolutePath, null, ratio, CROP_IMAGE_GALLERY)
                }
            }
        } else {
            takeMediaHelper = TakeMediaHelper(listener, isVideo).apply {
                onTakeImageSuccess = {
                    listener.onTakeMediaSuccess(it)
                }
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
                        if (cropImage) {
                            CropImageActivity.start(this@TakeMediaDialog, list[0].absolutePath, null, ratio, CROP_IMAGE_GALLERY)
                        } else {
                            listener.onPickMediaSucess(list[0])
                            dismiss()
                        }
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

        ICheckApplication.currentActivity()?.let {
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


    interface TakeImageListener : TakeMediaHelper.TakeCameraListener {
        fun onPickMediaSucess(file: File)
        fun onPickMuliMediaSucess(file: MutableList<File>)
    }
}