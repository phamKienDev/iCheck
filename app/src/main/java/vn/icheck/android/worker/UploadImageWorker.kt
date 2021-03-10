package vn.icheck.android.worker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.ICK_IMAGE_UPLOADED_SRC
import vn.icheck.android.constant.ICK_URI
import vn.icheck.android.network.api.UploadApi
import vn.icheck.android.util.ick.logError
import java.io.ByteArrayOutputStream
import java.io.File

class UploadImageWorker @WorkerInject constructor(
        @Assisted val appContext:Context,
        @Assisted workerParameters: WorkerParameters,
        private val uploadApi: UploadApi
):CoroutineWorker(appContext, workerParameters){

    override suspend fun doWork(): Result {
        return try {
            var imageUri = inputData.getString(ICK_URI)
            if (imageUri == null) {
                Result.failure()
            }
            if(!imageUri!!.contains(".jpg")){
                if (!imageUri.contains(".png")) {
                    imageUri += ".jpg"
                }
            }
            val file = File(imageUri)
//            val bos = prepareBitmap(Uri.fromFile(file))
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("key",file.name, requestBody )
            val response = uploadApi.postImage(body)
//                    uploadApi.postImage(  bos.toByteArray()
//                    .toRequestBody("image/jpeg".toMediaTypeOrNull(),
//                            0, bos.size()))

            Result.success(workDataOf(
                    ICK_IMAGE_UPLOADED_SRC to response.data?.url,
                    "key" to inputData.getString("key")
            ))
        } catch (e: Exception) {
            logError(e)
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SOCKET_TIMEOUT))
            Result.failure()
        }
    }


    private fun prepareBitmap(imageUri:Uri?): ByteArrayOutputStream {
        val bitmap = Glide.with(appContext.applicationContext)
                .asBitmap()
                .load(imageUri)
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