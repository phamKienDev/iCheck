package vn.icheck.android.screen.user.share_post_of_page

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import vn.icheck.android.constant.Constant
import vn.icheck.android.model.ICNameValue
import vn.icheck.android.network.models.ICChoosePage
import vn.icheck.android.network.models.ICPageUserManager
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICUser

class SharePostViewModel : ViewModel() {

    var post = MutableLiveData<ICPost>()
    var user = MutableLiveData<ICUser>()
    var page = MutableLiveData<ICPageUserManager>()

    fun getDataIntent(intent: Intent?) {
        val objPost = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICPost
        } catch (e: Exception) {
            null
        }

        val objUser = try {
            intent?.getSerializableExtra(Constant.DATA_2) as ICUser
        }catch (e:Exception){
            null
        }

        val objPage = try {
            intent?.getSerializableExtra(Constant.DATA_3) as ICPageUserManager
        }catch (e:Exception){
            null
        }

        if (objPost != null) {
            post.postValue(objPost!!)
        }

        if (objUser != null){
            user.postValue(objUser!!)
        }

        if (objPage != null){
            page.postValue(objPage!!)
        }
    }

//    private fun foo() : Flow<ICNameValue> = flow{
//        emit(ICNameValue("A","A"))
//        emit(ICNameValue("B","B"))
//        emit(ICNameValue("C","C"))
//    }
//
//    fun getData() {
//        val flowData = foo()
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//                flowData.collect { prinl.postValue(it) }
//            }
//        }
//    }
}