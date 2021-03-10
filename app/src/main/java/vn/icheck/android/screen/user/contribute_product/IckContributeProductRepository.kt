package vn.icheck.android.screen.user.contribute_product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import okhttp3.ResponseBody
import vn.icheck.android.constant.ICK_URI
import vn.icheck.android.model.category.CategoryAttributesResponse
import vn.icheck.android.model.category.IckCategoryResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.util.makeSimpleRequest
import vn.icheck.android.network.models.ICContribute
import vn.icheck.android.util.ick.logError
import vn.icheck.android.worker.UploadImageWorker
import java.io.File
import javax.inject.Inject

const val UPLOAD_LIST_IMAGE = "upload_list_images"

class IckContributeProductRepository @Inject constructor(val ickApi: ICKApi, private val workManager: WorkManager) {

    val mErr = MutableLiveData<Exception>()

    fun uploadListImage(listFiles: List<File?>): LiveData<List<WorkInfo>> {
        val listWork = arrayListOf<OneTimeWorkRequest>()
        for (item in listFiles) {
            val worker = OneTimeWorkRequestBuilder<UploadImageWorker>()
            val i = listFiles.indexOf(item)
            when (i) {
                0 -> {
                    worker.setInputData(workDataOf(ICK_URI to item?.absolutePath, "key" to "front"))
                }
                1 -> {
                    worker.setInputData(workDataOf(ICK_URI to item?.absolutePath,"key" to "back"))
                }
                else -> {
                    worker.setInputData(workDataOf(ICK_URI to item?.absolutePath))
                }
            }

            listWork.add(worker.build())
        }
        workManager.beginUniqueWork(UPLOAD_LIST_IMAGE, ExistingWorkPolicy.REPLACE, listWork)
                .enqueue()
        return workManager.getWorkInfosForUniqueWorkLiveData(UPLOAD_LIST_IMAGE)
    }

    fun uploadListImage(listFiles: HashMap<String, List<File?>>): LiveData<List<WorkInfo>> {
        val listWork = arrayListOf<OneTimeWorkRequest>()
        for (item in listFiles.keys) {
            if (listFiles[item] != null) {
                for(file in listFiles[item]!!.toList()) {
                    val worker = OneTimeWorkRequestBuilder<UploadImageWorker>()
                    worker.setInputData(
                            workDataOf(
                                    ICK_URI to file?.absolutePath,
                                    "key" to item
                            )
                    )
                    listWork.add(worker.build())
                }
            }
        }
        workManager.beginUniqueWork(UPLOAD_LIST_IMAGE, ExistingWorkPolicy.REPLACE, listWork)
                .enqueue()
        return workManager.getWorkInfosForUniqueWorkLiveData(UPLOAD_LIST_IMAGE)
    }


    suspend fun contributeProduct(requestBody: HashMap<String, Any?>): ICContribute? {
        return try {
            ickApi.contributeProduct(requestBody)
        } catch (e: Exception) {
            logError(e)
            mErr.postValue(e)
            return null
        }
    }

    suspend fun getCategoryContribute(id: Long): CategoryAttributesResponse? {
        return try {
            ickApi.getCategoryAttributes(id)
        } catch (e: Exception) {
            mErr.postValue(e)
            return null
        }
    }

    suspend fun getMyContribution(productId: Long): ResponseBody? {
        return try {
            ickApi.getMyContribution(productId)
        } catch (e: Exception) {
            mErr.postValue(e)
            return null
        }
    }

    suspend fun getMyContribution(barcode: String?): ResponseBody? {
        return try {
            ickApi.getMyContribution(barcode)
        } catch (e: Exception) {
            mErr.postValue(e)
            return null
        }
    }

    suspend fun getContribution(productId: Long): ResponseBody? {
        return try {
            ickApi.getContribution(productId)
        } catch (e: Exception) {
            mErr.postValue(e)
            return null
        }
    }
    suspend fun getCategoryById(id: Long): IckCategoryResponse? {
        return try {
            ickApi.getCategoryById(id)
        } catch (e: Exception) {
            mErr.postValue(e)
            null
        }
    }

    suspend fun updateContribution(id: Long?, requestBody: HashMap<String, Any?>): ICContribute? {
        return try {
            ickApi.updateContribution(id, requestBody)
        } catch (e: Exception) {
            logError(e)
            mErr.postValue(e)
            return null
        }
    }

    suspend fun updateContribution(barcode: String?, requestBody: HashMap<String, Any?>): ICContribute? {
        return try {
            ickApi.updateContribution(barcode, requestBody)
        } catch (e: Exception) {
            logError(e)
            mErr.postValue(e)
            return null
        }
    }

    suspend fun getChildren(id: Long): Int {
        val res = makeSimpleRequest {
            ickApi.getChildCategories(id, 10, 0)
        }
        return res?.data?.count ?: 0
    }
}