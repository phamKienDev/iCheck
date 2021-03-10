package vn.icheck.android.screen.user.option_edit_information_public

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.wall.ICUserPublicInfor

class EditInformationPublicViewModel : ViewModel() {

    val data = MutableLiveData<MutableList<ICUserPublicInfor>>()
    val nullInfor = MutableLiveData<Boolean>()
    val errorData = MutableLiveData<Int>()
    val positionResult = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    private val interactor = UserInteractor()

    fun getPublicInfor() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getUserPublicInfor(object : ICNewApiListener<ICResponse<ICListResponse<ICUserPublicInfor>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICUserPublicInfor>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val userMe = SessionManager.session.user
                if (!obj.data?.rows.isNullOrEmpty()) {
                    for (i in obj.data?.rows!!) {
                        i.user = userMe
                    }
                    data.postValue(obj.data?.rows)
                }
//                val userMe = SessionManager.session.user
//                if (userMe?.email.isNullOrEmpty() || userMe?.gender.isNullOrEmpty() || userMe?.bd == null || userMe.city?.name.isNullOrEmpty() || userMe.phone.isNullOrEmpty()) {
//                    nullInfor.postValue(true)
//                    return
//                } else {
//                    if (!obj.data?.rows.isNullOrEmpty()) {
//                        for (i in obj.data?.rows!!) {
//                            i.user = userMe
//                        }
//                        data.postValue(obj.data?.rows)
//                    }
//                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun updateInforPublic(privacyElementId: Int?, checked: Boolean, position: Int) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorData.postValue(Constant.ERROR_INTERNET)
            return
        }

        interactor.updateInforPublic(privacyElementId, checked, object : ICNewApiListener<ICResponseCode> {
            override fun onSuccess(obj: ICResponseCode) {
                if (obj.statusCode == "200") {
                    positionResult.postValue(position)
                } else {
                    errorData.postValue(Constant.ERROR_SERVER)
                }
            }

            override fun onError(error: ICResponseCode?) {
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }
}