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
import vn.icheck.android.ichecklibs.base_dialog.BaseBottomSheetDialogFragment
import vn.icheck.android.ichecklibs.util.rText
import java.io.File

class TakeMediaDialog : BaseBottomSheetDialogFragment() {

    private var listener: TakeMediaListener? = null

    private var activity: Activity? = null
    private var selectMulti: Boolean = false // cho chọn nhiều ảnh/video
    private var cropImage: Boolean = false  // cho phép chuyển sang màn Crop
    private var ratio: String? = null  // tỉ lệ Crop ảnh
    private var isVideo: Boolean = true // cho chọn video hay không?
    var disableTakeImage: Boolean = false // cho chụp ảnh hay không?
    var saveImageToGallery: Boolean = false // cho phép lưu ảnh
    var maxSelectCount: Int? = null // số lượng chọn tối đa

    fun setListener(
            activity: Activity,
            listener: TakeMediaListener,
            selectMulti: Boolean = false, // cho chọn nhiều ảnh/video
            cropImage: Boolean = false,  // cho phép chuyển sang màn Crop
            ratio: String? = null,  // tỉ lệ Crop ảnh
            isVideo: Boolean = true, // cho chọn video hay không?
            disableTakeImage: Boolean = false, // cho chụp ảnh hay không?
            saveImageToGallery: Boolean = false, // cho phép lưu ảnh
            maxSelectCount: Int? = null, // số lượng chọn tối đa
    ) {
        this.activity = activity
        this.listener = listener
        this.selectMulti = selectMulti
        this.cropImage = cropImage
        this.ratio = ratio
        this.isVideo = isVideo
        this.disableTakeImage = disableTakeImage
        this.saveImageToGallery = saveImageToGallery
        this.maxSelectCount = maxSelectCount
    }

    companion object {
        var INSTANCE: TakeMediaDialog? = null
        const val CROP_IMAGE_GALLERY = 1

        fun show(fragmentManager: FragmentManager, activity: Activity, listener: TakeMediaListener, selectMulti: Boolean = false, cropImage: Boolean = false, ratio: String? = null, isVideo: Boolean = false, disableTakeImage: Boolean = false, saveImageToGallery: Boolean = false, maxSelectCount: Int? = null) {
            if (fragmentManager.findFragmentByTag(TakeMediaDialog::class.java.simpleName)?.isAdded != true) {

                TakeMediaDialog().apply {
                    setListener(activity, listener, selectMulti, cropImage, ratio, isVideo, disableTakeImage, saveImageToGallery, maxSelectCount)
                    show(fragmentManager, TakeMediaDialog::class.java.simpleName)
                }


//                TakeMediaDialog(activity, listener, selectMulti, cropImage, ratio, isVideo, disableTakeImage, saveImageToGallery, maxSelectCount).show(fragmentManager, TakeMediaDialog::class.java.simpleName)
            }
        }
    }

    var takeMediaHelper: TakeMediaHelper? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_take_media, container, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgClose.setOnClickListener {
            dismiss()
        }

        if (cropImage) {
            takeMediaHelper = listener?.let {
                TakeMediaHelper(activity, it).apply {
                    onTakeImageSuccess = {

                        listener?.onStartCrop(it?.absolutePath, null, ratio, CROP_IMAGE_GALLERY)
                    }
                }
            }
        } else {
            takeMediaHelper = listener?.let {
                TakeMediaHelper(activity, it, isVideo).apply {
                    onTakeImageSuccess = {
                        listener?.onTakeMediaSuccess(it)
                    }
                }
            }
        }

        if (saveImageToGallery) {
            takeMediaHelper?.context = requireContext()
            takeMediaHelper?.saveImageToGallery = true
        }

        if (isVideo) {
            tvTile rText R.string.chon_anh_video
        } else {
            tvTile rText R.string.chon_anh
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
                        listener?.onPickMuliMediaSucess(list)
                        dismiss()
                    } else {
                        if (cropImage) {
                            listener?.onStartCrop(list[0].absolutePath, null, ratio, CROP_IMAGE_GALLERY)
                        } else {
                            listener?.onPickMediaSucess(list[0])
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

            val cursor = it?.contentResolver?.query(
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
                    listener?.onPickMediaSucess(File(url))
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