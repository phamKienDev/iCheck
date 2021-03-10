package vn.icheck.android.screen.user.rank_of_user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICRankOfUser

class RankOfUserViewModel : ViewModel() {
    private val interactor = UserInteractor()

    val getRank = MutableLiveData<ICRankOfUser>()
    val errorData = MutableLiveData<Int>()
    val listSetting = MutableLiveData<MutableList<ICClientSetting>>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

//    fun getRank() {
//        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
//            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
//            return
//        }
//
//        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
//
//        interactor.getRankOfUser(object :ICNewApiListener<ICUserInfoSocial>{
//            override fun onSuccess(obj: ICUserInfoSocial) {
//                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
//            }
//
//            override fun onError(error: ICResponseCode?) {
//                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
//                errorData.postValue(Constant.ERROR_SERVER)
//            }
//        })
//    }

}