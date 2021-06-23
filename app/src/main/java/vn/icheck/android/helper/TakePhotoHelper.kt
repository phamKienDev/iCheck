package vn.icheck.android.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import vn.icheck.android.BuildConfig
import vn.icheck.android.ichecklibs.take_media.PickCameraOptionDialog
import java.io.File
import java.io.IOException

class TakePhotoHelper(val listener: TakePhotoListener) {
    private val requestCamera = 89
    private val requestDocument = 88
    private val requestPickMultiImage = 90

    private val fileProvider = if (BuildConfig.FLAVOR.contentEquals("prod")) {
        "vn.icheck.android.fileprovider"
    } else {
        "vn.icheck.android.develop.fileprovider"
    }

    private var currentPhotoPath: String? = null

    fun takePhoto(fragment: Fragment) {
        fragment.context?.let {
            object : PickCameraOptionDialog(it) {
                override fun onCamera() {
                    startTakePhoto(fragment)
                }

                override fun onDocument() {
                    startPickPhoto(fragment)
                }
            }.show()
        }
    }

    private fun startTakePhoto(fragment: Fragment) {
        fragment.activity?.let { activity ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile = try {
                        createImageFile(activity)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(activity, fileProvider, it)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        fragment.startActivityForResult(takePictureIntent, requestCamera)
                    }
                }
            }
        }
    }


    fun takePhoto(fragment: FragmentActivity) {
        object : PickCameraOptionDialog(fragment) {
            override fun onCamera() {
                startTakePhoto(fragment)
            }

            override fun onDocument() {
                startPickPhoto(fragment)
            }
        }.show()
    }

    fun takeMultiPhoto(fragment: FragmentActivity) {
        object : PickCameraOptionDialog(fragment) {
            override fun onCamera() {
                startTakePhoto(fragment)
            }

            override fun onDocument() {
                startPickMultiImage(fragment)
            }
        }.show()
    }


     fun startTakePhoto(fragment: FragmentActivity) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(fragment.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile = try {
                    createImageFile(fragment)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(fragment, fileProvider, it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    fragment.startActivityForResult(takePictureIntent, requestCamera)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = TimeHelper.getCreteTimeDate()
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private val getPhotoFile: File?
        get() {
            return if (!currentPhotoPath.isNullOrEmpty()) {
                val file = File(currentPhotoPath!!)
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

    private fun startPickPhoto(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        fragment.startActivityForResult(intent, requestDocument)
    }

    private fun startPickPhoto(fragment: FragmentActivity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        fragment.startActivityForResult(intent, requestDocument)
    }

    private fun startPickMultiImage(fragment: FragmentActivity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestPickMultiImage)
    }

    private fun createMultiImage(context: Context, data: Intent?): MutableList<File> {
        val uriList = mutableListOf<Uri>()
        val clipData = data?.clipData

        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                try {
                    uriList.add(uri)
                } catch (e: Exception) {
                    Log.e("takePhotoHelper", "không thấy file")
                }

            }
        } else {
            val uri = data?.data
            try {
                uriList.add(uri!!)
            } catch (e: Exception) {
                Log.e("takePhotoHelper", "không thấy file")
            }
        }

        val listFile = mutableListOf<File>()
        for (uri in uriList) {
            currentPhotoPath = FileHelper.getPath(context, uri)
            listFile.add(getPhotoFile!!)
        }

        return listFile
    }


    fun onActivityResult(context: Context?, requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCamera -> {
                    listener.onTakePhotoSuccess(getPhotoFile)
                }
                requestDocument -> {
                    if (context != null) {
                        data?.data?.also {
                            currentPhotoPath = FileHelper.getPath(context, it)
                            listener.onTakePhotoSuccess(getPhotoFile)
                        }
                    }
                }
                requestPickMultiImage -> {
                    if (context != null) {
                        data?.let {
                            listener.onPickMultiImageSuccess(createMultiImage(context, data))
                        }
                    }
                }
            }
        }
    }

    interface TakePhotoListener {
        fun onTakePhotoSuccess(file: File?)
        fun onPickMultiImageSuccess(file: MutableList<File>)
    }

}