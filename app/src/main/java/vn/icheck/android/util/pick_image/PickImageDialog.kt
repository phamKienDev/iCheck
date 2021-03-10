package vn.icheck.android.util.pick_image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import vn.icheck.android.BuildConfig
import vn.icheck.android.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PickImageDialog(val activity: Activity, val requestCode: Int) : DialogFragment() {

    companion object{
        const val TAKE_PHOTO = 33
        var uri:Uri? = null

        fun prepareBitmap(uri: Uri, context: Context):ByteArrayOutputStream {
            val bitmap = Glide.with(context.applicationContext)
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
            return bos
        }
    }

    private val fileProvider = if (BuildConfig.FLAVOR.contentEquals("prod")) {
        "vn.icheck.android.fileprovider"
    } else {
        "vn.icheck.android.develop.fileprovider"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.choose_image_dialog, container, false)
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.tv_pick_gallery).setOnClickListener {
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(pickImage, requestCode)
            dismiss()
        }
        view.findViewById<TextView>(R.id.tv_pick_camera).setOnClickListener {
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePicture.resolveActivity(activity.packageManager) != null) {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            activity,
                            fileProvider,
                            it
                    )
                    uri = photoURI
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePicture, TAKE_PHOTO)
                }

            }
            dismiss()
        }
    }

}