package vn.icheck.android.screen.user.contribute_product

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.WorkInfo
import com.google.android.gms.common.util.JsonUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import vn.icheck.android.network.model.category.CategoryAttributesResponse
import vn.icheck.android.network.model.category.CategoryItem
import vn.icheck.android.network.model.category.IckCategoryResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.ICContribute
import vn.icheck.android.screen.user.contribute_product.source.CategoryDataSource
import vn.icheck.android.screen.user.contribute_product.source.ChildCategoryDataSource
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.screen.user.contribute_product.viewmodel.ImageModel
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.ick.logError
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IckContributeProductViewModel @ViewModelInject constructor(
        val categoryChildrenSource: ChildCategoryDataSource,
        private val categoryDataSource: CategoryDataSource,
        private val ickContributeProductRepository: IckContributeProductRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val arrayListImage = arrayListOf<File?>()
    val listImages = MutableLiveData<List<File?>>()
    private val listSrcUploaded = arrayListOf<String?>()
    var requestBody = hashMapOf<String, Any?>()
    private val unverifiedOwner = hashMapOf<String, Any?>()
    var currentCategory: CategoryItem? = null
    val categoryAttributes = arrayListOf<CategoryAttributesModel>()
    val mErr = ickContributeProductRepository.mErr
    val mException = MutableLiveData<Exception>()
    val listImageModel = arrayListOf<ImageModel>()
    var myContributionId: Long? = null
    var myContribute = 0
    val listCategory = arrayListOf<CategoryItem?>()

    fun setName(str: String) {
        requestBody["name"] = str
    }

    fun setBarcode(str: String?) {
        requestBody["barcode"] = str
    }

    fun setProductId(id: Long) {
        requestBody["productId"] = id
    }

    fun setPrice(price: Long) {
        if (price != -1L) {
            requestBody["price"] = price
        } else {
            requestBody.remove("price")
        }
    }

    fun setCategory(categoryId: Long) {
        requestBody["categoryId"] = categoryId
    }

    fun getCategory(): Long? {
        return if (requestBody.get("categoryId") is Double?) {
            (requestBody.get("categoryId") as Double?)?.toLong()
        } else {
            requestBody.get("categoryId") as Long?
        }
    }

    fun setOwnerName(str: String) {
        unverifiedOwner.put("name", str)
//        if (str.isNotEmpty()) {
//            unverifiedOwner.put("name", str)
//        } else {
//            unverifiedOwner.remove("name")
//        }
    }

    fun setAddress(str: String) {
        unverifiedOwner.put("address", str)
//        if (str.isNotEmpty()) {
//            unverifiedOwner.put("address", str)
//        } else {
//            unverifiedOwner.remove("address")
//        }
    }

    fun setPhone(str: String) {
        unverifiedOwner.put("phone", str)
//        if (str.isNotEmpty()) {
//            unverifiedOwner.put("phone", str)
//        } else {
//            unverifiedOwner.remove("phone")
//        }
    }

    fun setEmail(str: String) {
        unverifiedOwner.put("email", str)
//        if (str.isNotEmpty()) {
//            unverifiedOwner.put("email", str)
//        } else {
//            unverifiedOwner.remove("email")
//        }
    }

    fun setTax(str: String) {
        unverifiedOwner.put("tax", str)
//        if (str.isNotEmpty()) {
//            unverifiedOwner.put("tax", str)
//        } else {
//            unverifiedOwner.remove("tax")
//        }
    }

    fun getCategoryAttributes(categoryId: Long): LiveData<CategoryAttributesResponse?> {
        return liveData {
            emit(ickContributeProductRepository.getCategoryContribute(categoryId))
        }
    }

    fun addImage(file: File?) {
        arrayListImage.add(file)
        postSize()
    }

    fun setImage(file: File?, position: Int = 0) {
        try {
            if (myContributionId == null) {
                addImage(file)
            } else {
                if (arrayListImage.size >= position + 1) {
                    arrayListImage.set(position, file)
                } else {
                    addImage(file)
                }
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    fun addAllImage(arrayList: ArrayList<File>) {
        arrayListImage.addAll(arrayList)
        postSize()
    }

    fun addSrc(src: String?) {
        if (listSrcUploaded.size < listImageModel.size + 2) {
            listSrcUploaded.add(src)
        }
    }

    fun removeSrc(src: String?) {
        listSrcUploaded.remove(src)
    }


    @OptIn(ExperimentalStdlibApi::class)
    fun removeLastImage() {
        arrayListImage.removeLastOrNull()
        postSize()
    }

    fun removeAt(position: Int) {
        arrayListImage.removeAt(position)
        postSize()
    }

    fun addImage(listFile: List<File?>) {
        arrayListImage.addAll(listFile)
        postSize()
    }

    fun postSize() {
        listImages.postValue(arrayListImage)
    }

    fun getMyContribution(productId: Long): LiveData<ResponseBody?> {
        return liveData {
            emit(ickContributeProductRepository.getMyContribution(productId))
        }
    }

    fun getMyContribution(barcode: String?): LiveData<ResponseBody?> {
        return liveData {
            emit(ickContributeProductRepository.getMyContribution(barcode))
        }
    }

    fun getContribution(productId: Long): LiveData<ResponseBody?> {
        return liveData {
            emit(ickContributeProductRepository.getContribution(productId))
        }
    }

    fun finalStep(): LiveData<List<WorkInfo>>? {
        for (item in categoryAttributes) {
            if (item.categoryItem.required != null && item.categoryItem.required == true) {
                if (item.categoryItem.type != "image-single" && item.categoryItem.type != "image" && item.values == null) {
                    mException.postValue(Exception("Không bỏ trống ${item.categoryItem.name}"))
                    return null
                }
            }
        }
        val arr = arrayListOf<File?>()
        arr.add(arrayListImage.get(0))
        arr.add(arrayListImage.get(1))
        for (item in listImageModel) {
            if (item.file != null) {
                arr.add(item.file)
            }
        }
        return ickContributeProductRepository.uploadListImage(arr)
    }

    fun uploadAttributesImages(): LiveData<List<WorkInfo>> {
        val hash = hashMapOf<String, List<File?>>()

        for (item in categoryAttributes) {
            if (item.categoryItem.type == "image-single" || item.categoryItem.type == "image" && item.hasImages()) {
                val arr = ArrayList<File?>()
                arr.addAll(item.getListImages())
                hash.put(item.categoryItem.code!!, arr)
            }

        }
        return ickContributeProductRepository.uploadListImage(hash)
    }

    fun contributeProduct(): LiveData<ICContribute?> {
        return liveData {

            if (unverifiedOwner.isNotEmpty()) {
                requestBody.put("unverifiedOwner", unverifiedOwner)
            }
            if (currentCategory != null) {
                requestBody["categoryId"] = currentCategory!!.id
            }
            val arr = arrayListOf<HashMap<String, Any?>>()

            for (item in categoryAttributes) {

                if (item.values != null) {
                    if (item.values !is ArrayList<*>) {
                        val hashMap = hashMapOf<String, Any?>()
                        hashMap.put("code", item.categoryItem.code)
                        when (item.categoryItem.type) {
                            "group" -> {
                                try {
                                   val jsonObject = JSONObject(item.values.toString())
                                    val value = jsonObject.getString("shortContent" )
                                    hashMap.put("value", hashMapOf("shortContent" to value))
                                } catch (e: Exception) {
                                    hashMap.put("value", hashMapOf("shortContent" to item.values.toString()))
                                }
                            }
                            "image-single", "image" -> {
                                if (item.hasImages()) {
                                    hashMap.put("value", item.values)
                                } else {
                                    hashMap.remove(item.categoryItem.code)
                                }

                            }
                            "select" -> {
                                if (item.values.toString().toIntOrNull() == 0) {
                                    mException.postValue(Exception("Không bỏ trống ${item.categoryItem.name}"))
                                    return@liveData
                                } else {
                                    hashMap.put("value", item.values)
                                }

                            }
                            else -> {
                                hashMap.put("value", item.values)
                            }
                        }

                        arr.add(hashMap)
                    } else {
                        if ((item.values as ArrayList<*>).isNotEmpty()) {
                            val hashMap = hashMapOf<String, Any?>()
                            if (item.categoryItem.type == "image-single" || item.categoryItem.type == "image") {
                                if (item.hasImages()) {
                                    hashMap.put("code", item.categoryItem.code)
                                    hashMap.put("value", item.values)
                                    arr.add(hashMap)
                                } else {
                                    hashMap.remove(item.categoryItem.code)
                                    if (item.categoryItem.required != null && item.categoryItem.required == true) {
                                        mException.postValue(Exception("Không bỏ trống ${item.categoryItem.name}"))
                                        return@liveData
                                    }
                                }
                            } else {
                                hashMap.put("code", item.categoryItem.code)
                                hashMap.put("value", item.values)
                                arr.add(hashMap)
                            }
                        }
                    }
                } else if (item.categoryItem.required != null && item.categoryItem.required == true) {
                    mException.postValue(Exception("Không bỏ trống ${item.categoryItem.name}"))
                    return@liveData
                }
            }

            requestBody.put("images", listSrcUploaded.toArray())

            if (arr.isNotEmpty()) {
                requestBody["attributes"] = arr
            } else {
                requestBody.remove("attributes")
            }
            if (myContributionId != null) {
                requestBody.remove("id")
                emit(ickContributeProductRepository.updateContribution(myContributionId, requestBody))
            } else {
                emit(ickContributeProductRepository.contributeProduct(requestBody))
            }
        }
    }

    fun getCategoryList(): Flow<PagingData<CategoryItem>> {
        return Pager(PagingConfig(pageSize = 10)) {
            categoryDataSource
        }.flow.cachedIn(viewModelScope)
    }

    fun filterCategoryList(filterString: String): Flow<PagingData<CategoryItem>> {
        return Pager(PagingConfig(pageSize = 10)) {
            categoryChildrenSource.apply {
                this.filterString = filterString
            }
        }.flow.cachedIn(viewModelScope)
    }

    suspend fun getChildCategoryList(id: Long): Flow<PagingData<CategoryItem>> {
        return Pager(PagingConfig(pageSize = 10)) {
            categoryChildrenSource.apply {
                onTouch = true
                parentId = id
            }
        }.flow.cachedIn(viewModelScope)
    }

    suspend fun getCount(id: Long) {
        val count = ickContributeProductRepository.getChildren(id)
        if (count == 0) {
            categoryChildrenSource.final.postValue(id)
        }
    }

    fun getCategoryById(id: Long): LiveData<IckCategoryResponse?> {
        return liveData {
            emit(ickContributeProductRepository.getCategoryById(id))
        }
    }
}