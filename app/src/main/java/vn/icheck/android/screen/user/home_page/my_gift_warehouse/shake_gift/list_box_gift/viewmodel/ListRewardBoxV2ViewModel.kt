package vn.icheck.android.screen.user.home_page.my_gift_warehouse.shake_gift.list_box_gift.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.feature.mission.MissionInteractor
import vn.icheck.android.network.models.*

class ListRewardBoxV2ViewModel : ViewModel() {

    private val interactor = ListCampaignInteractor()
    private val missionInteractor = MissionInteractor()

    val dataCampaign = MutableLiveData<ICDetail_Campaign?>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val dataListMissionActive = MutableLiveData<MutableList<ICMission>>()

    val icItemReward = MutableLiveData<ICCampaign>()
    val listGridBox = MutableLiveData<MutableList<ICGridBoxShake>>()

    var objCampaign: ICCampaign? = null
    var idCampaign: String? = null

    var typeCheckDialogGift = 1

    var listMissionActive = mutableListOf<ICMission>()
    var isMissionSuccess = false

    fun getDataIntent(intent: Intent?) {
        try {
            objCampaign = intent?.getSerializableExtra(Constant.DATA_1) as ICCampaign
        } catch (e: Exception) {
        }

        try {
            idCampaign = intent?.getSerializableExtra(Constant.DATA_1) as String
        } catch (e: Exception) {
        }

        typeCheckDialogGift = intent?.getIntExtra(Constant.DATA_2, 0) ?: 1

        if (objCampaign != null) {
            idCampaign = objCampaign!!.id
            icItemReward.postValue(objCampaign)
            if (!objCampaign!!.id.isNullOrEmpty()) {
                getListGiftBox(objCampaign?.id!!)
                getListMissionActive(objCampaign?.id!!)
            }
        } else if (idCampaign != null) {
            getInfoCampaign(idCampaign!!)
        }
    }

    fun getDataIntentWithType(intent: Intent?) {
        typeCheckDialogGift = intent?.getIntExtra(Constant.DATA_2, 0) ?: 1
    }

    fun getListGiftBox(id: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        interactor.getListIconCampaign(id, object : ICNewApiListener<ICResponse<ICListResponse<ICGridBoxShake>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICGridBoxShake>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    listGridBox.postValue(obj.data?.rows)
                } else {
                    errorData.postValue(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_EMPTY)
            }
        })
    }

    fun getInfoCampaign(id: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        interactor.getInfoCampaign(id, object : ICNewApiListener<ICResponse<ICCampaign>> {
            override fun onSuccess(obj: ICResponse<ICCampaign>) {
                if (obj.data != null) {
                    objCampaign = obj.data
                    icItemReward.postValue(objCampaign)
                    getListGiftBox(objCampaign?.id!!)
                    getListMissionActive(objCampaign?.id!!)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_EMPTY)
            }
        })
    }

    private fun getListMissionActive(id: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        missionInteractor.getListMission(id, object : ICNewApiListener<ICResponse<ICListResponse<ICMission>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICMission>>) {
                listMissionActive.clear()
                for (misssion in obj.data?.rows ?: mutableListOf()) {
                    if (misssion.finishState == 2) {
                        isMissionSuccess = true
                    } else {
                        if (misssion.finishState != 3) {
                            if (listMissionActive.size < 3) {
                                listMissionActive.add(misssion)
                            }
                        }
                    }
                }
                dataListMissionActive.postValue(listMissionActive)
            }

            override fun onError(error: ICResponseCode?) {

            }
        })
    }


}