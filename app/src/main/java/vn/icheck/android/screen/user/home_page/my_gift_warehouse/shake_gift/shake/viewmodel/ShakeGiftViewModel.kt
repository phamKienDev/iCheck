package vn.icheck.android.screen.user.home_page.my_gift_warehouse.shake_gift.shake.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.Constant.ERROR_EMPTY
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICGridBoxShake
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.network.models.ICOpenShakeGift

class ShakeGiftViewModel : ViewModel() {

    var objItemReward: ICGridBoxShake? = null
    var liveDataObject = MutableLiveData<ICGridBoxShake?>()

    val errorData = MutableLiveData<ICMessageEvent>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val data = MutableLiveData<ICOpenShakeGift>()

    private val interactor = ListCampaignInteractor()

    var campaign: ICCampaign? = null

    fun getDataIntent(intent: Intent?) {
        val data = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICGridBoxShake
        } catch (e: Exception) {
            null
        }

        campaign = try {
            intent?.getSerializableExtra(Constant.DATA_2) as ICCampaign
        } catch (e: Exception) {
            null
        }

        if (data != null) {
            objItemReward = data
            liveDataObject.postValue(data)
        } else {
            errorData.postValue(ICMessageEvent(ICMessageEvent.Type.ERROR_EMPTY))
        }
    }

    fun openGiftBox() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (campaign == null || objItemReward?.id == null) {
            errorData.postValue(ICMessageEvent(ICMessageEvent.Type.ERROR_EMPTY))
            return
        }

        interactor.openShakeGift(campaign!!.id!!, 1, objItemReward?.id!!, object : ICNewApiListener<ICResponse<ICListResponse<ICOpenShakeGift>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICOpenShakeGift>>) {
                if (obj.statusCode == "200" && !obj.data?.rows.isNullOrEmpty()) {
                    data.postValue(obj.data?.rows!![0])
                } else {
                    errorData.postValue(ICMessageEvent(ICMessageEvent.Type.ERROR_EMPTY))
                }
            }

            override fun onError(error: ICResponseCode?) {
                errorData.postValue(ICMessageEvent(ICMessageEvent.Type.ERROR_SERVER, error))
            }
        })
    }

}