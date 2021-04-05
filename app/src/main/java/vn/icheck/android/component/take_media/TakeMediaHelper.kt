package vn.icheck.android.component.take_media

import android.R.attr.bitmap
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.BuildConfig
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.screen.dialog.PickPhotoOptionDialog
import java.io.*


class TakeMediaHelper(
        val callback: TakeCameraListener,
        private val selectVideo: Boolean = false

) {
    private val requestImage = 89
    private val requestVideo = 90
    private var currentImagePath: String? = null
    private var currentVideoPath: String? = null
    var onTakeImageSuccess: ((File?) -> Unit)? = null
    var saveImageToGallery:Boolean = false
    var context:Context? = null

    private val fileProvider = if (BuildConfig.FLAVOR.contentEquals("prod")) {
        "vn.icheck.android.fileprovider"
    } else {
        "vn.icheck.android.develop.fileprovider"
    }

    fun startTakeMedia(fragment: Fragment? = null) {
        ICheckApplication.currentActivity()?.let { activity ->
            val imageCapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val videoCapture = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

            if (selectVideo) {
                object : PickPhotoOptionDialog(activity) {
                    override fun onCamera() {
                        startTakePhotoOrRecordVideo(imageCapture, fragment, activity)
                    }

                    override fun onDocument() {
                        startTakePhotoOrRecordVideo(videoCapture, fragment, activity, true)
                    }
                }.show()
            } else {
                startTakePhotoOrRecordVideo(imageCapture, fragment, activity)
            }
        }
    }

    private fun startTakePhotoOrRecordVideo(intent: Intent, fragment: Fragment?, activity: Activity, pathVideo: Boolean = false): Intent {
        return intent.also { takeMediaIntent ->
            // Ensure that there's a camera activity to handle the intent
            takeMediaIntent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile = try {
                    createMediaFile(activity, pathVideo)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.TAKE_IMAGE))
                    val photoURI: Uri = FileProvider.getUriForFile(activity, fileProvider, it)
                    takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    if (fragment != null) {
                        fragment.startActivityForResult(takeMediaIntent, if (pathVideo) {
                            requestVideo
                        } else {
                            requestImage
                        })
                    } else {
                        activity.startActivityForResult(takeMediaIntent, if (pathVideo) {
                            requestVideo
                        } else {
                            requestImage
                        })
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createMediaFile(context: Context, pathVideo: Boolean = false): File {
        // Create an image file name
        val timeStamp: String = TimeHelper.getCreteTimeDate()
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return if (pathVideo) {
            File.createTempFile(
                    "VID_${timeStamp}_", /* prefix */
                    ".mp4", /* suffix */
                    storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentVideoPath = absolutePath
            }
        } else {
            File.createTempFile(
                    "JPEG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentImagePath = absolutePath
            }
        }
    }


    private val getPhotoFile: File?
        get() {
            return if (!currentImagePath.isNullOrEmpty()) {
                val file = File(currentImagePath!!)
                validFile(file)
            } else {
                null
            }
        }

    private val getVideoFile: File?
        get() {
            return if (!currentVideoPath.isNullOrEmpty()) {
                val file = File(currentVideoPath!!)
                validFile(file)
            } else {
                null
            }
        }

    private fun validFile(file: File): File? {
        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestImage -> {
                    if (saveImageToGallery) {
                        val f = getEi()
                        MediaStore.Images.Media.insertImage(context?.contentResolver, BitmapFactory.decodeFile(f?.path), f?.name, "");
                        if (onTakeImageSuccess != null) {
                            onTakeImageSuccess!!(f)
                        }
                    } else {
                        if (onTakeImageSuccess != null) {
                            onTakeImageSuccess!!(getPhotoFile)
                        } else {
                            callback.onTakeMediaSuccess(getPhotoFile)
                        }
                    }
                }
                requestVideo -> {
                    if (onTakeImageSuccess != null) {
                        onTakeImageSuccess!!(getVideoFile)
                    } else {
                        callback.onTakeMediaSuccess(getVideoFile)
                    }
                }
            }
        }
    }

    fun getBitmap():Bitmap {
        return BitmapFactory.decodeFile(getPhotoFile?.path)
    }

    fun getEi():File {
        val ei = ExifInterface(getPhotoFile?.path.toString())
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
        val rotatedBitmap:Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(getBitmap(), 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(getBitmap(), 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(getBitmap(), 270f)
            }
            else -> {
                getBitmap()
            }
        }
        return  createFile(rotatedBitmap)
    }

    fun createFile(bitmap: Bitmap?):File {
        val file = File(getPhotoFile?.path.toString())
        val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return file
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    interface TakeCameraListener {
        fun onTakeMediaSuccess(file: File?)
    }
}