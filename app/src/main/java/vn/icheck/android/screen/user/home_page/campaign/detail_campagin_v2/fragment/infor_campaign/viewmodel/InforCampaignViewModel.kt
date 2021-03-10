package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.home.model.ICHomeItem

class InforCampaignViewModel : ViewModel() {

    val dataCampaign = MutableLiveData<ICDetail_Campaign>()
    val onAddData = MutableLiveData<MutableList<ICHomeItem>>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    private val interactor = ListCampaignInteractor()

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

    fun getInfoCampaign(idCampaign: String?) {
        if (idCampaign.isNullOrEmpty()){
            errorData.postValue(Constant.ERROR_UNKNOW)
            return
        }

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getDetailCampaignV2(idCampaign,object : ICNewApiListener<ICResponse<ICDetail_Campaign>>{
            override fun onSuccess(obj: ICResponse<ICDetail_Campaign>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj != null){
                    dataCampaign.postValue(obj.data)

                    val list = mutableListOf<ICHomeItem>()
                    list.add(ICHomeItem(ICViewTypes.HEADER_INFOR_CAMPAIGN,"",null,obj.data))
                    list.add(ICHomeItem(ICViewTypes.INFOR_CAMPAIGN,"",null,obj.data))
                    list.add(ICHomeItem(ICViewTypes.CONDITIONS_CAMPAIGN,"",null,obj.data))
                    list.add(ICHomeItem(ICViewTypes.RANK_CAMPAIGN,"",null,obj.data))
                    list.add(ICHomeItem(ICViewTypes.GUIDE_CAMPAIGN,"",null,obj.data))
                    list.add(ICHomeItem(ICViewTypes.SPONSOR_CAMPAIGN,"",null,obj.data))
                    onAddData.postValue(list)
                }else{
                    errorData.postValue(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }

}