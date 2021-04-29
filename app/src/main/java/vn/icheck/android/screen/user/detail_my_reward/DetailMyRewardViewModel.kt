package vn.icheck.android.screen.user.detail_my_reward

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.network.models.campaign.DetailRewardResponse
import vn.icheck.android.screen.user.home_page.model.ICHomeItem
import vn.icheck.android.util.ick.logError

class DetailMyRewardViewModel @ViewModelInject constructor(val ickApi: ICKApi, @Assisted val savedStateHandle: SavedStateHandle) : ViewModel() {

    var dataReward = ICItemReward()

    val onAddData = MutableLiveData<MutableList<ICHomeItem>>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    val refuseGift = MutableLiveData<String>()
    var detailReward: DetailRewardResponse? = null
    val error = MutableLiveData<Int>()
    var loadSuccess = false

    fun getHeaderAlpha(totalScroll: Int, layoutHeader: Int): Float {
        return when {
            totalScroll > 0 -> {
                if (totalScroll <= layoutHeader) {
                    (1f / layoutHeader) * totalScroll
                } else {
                    1f
                }
            }
            totalScroll < 0 -> {
                if (totalScroll < -layoutHeader) {
                    (-1f / layoutHeader) * totalScroll
                } else {
                    0f
                }
            }
            else -> {
                0f
            }
        }
    }


    fun getDetailReward(id: String?): LiveData<DetailRewardResponse?> {
        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
        return liveData {
            try {
                detailReward = ickApi.getDetailReward(id)
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                emit(detailReward)
            } catch (e: Exception) {
                logError(e)
            }
        }

    }

    fun refuseGift(listId: MutableList<Int>, listMessage: MutableList<String>) {
        viewModelScope.launch {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["id"] = detailReward?.data?.id
            requestBody["reasonCode"] = listId
            requestBody["reasonOther"] = listMessage.joinToString()
            val res = ickApi.refuseGift(requestBody)
            if (res.statusCode == "200") {
                refuseGift.postValue(ICheckApplication.getInstance().getString(R.string.ban_da_tu_mon_qua_nay))
            }
        }

    }

    fun getCard() :LiveData<DetailRewardResponse?> {
        return liveData {
            ickApi.getMobileCard(detailReward?.data?.id, hashMapOf())
            detailReward = ickApi.getDetailReward(detailReward?.data?.id)
            statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
            emit(detailReward)
        }

    }

    fun updateUsingState():LiveData<ICResponse<*>> {
        return liveData {
            try {
                val request = hashMapOf<String, Any?>()
                request["usingState"] = 3
                val res = ickApi.updateUsingState(request, detailReward?.data?.id)
                emit(res)
            } catch (e: Exception) {
                error.postValue(1)
            }
        }

    }

}