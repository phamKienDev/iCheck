package vn.icheck.android.screen.user.list_campaign

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICCampaign

class ListCampaignViewModel : ViewModel() {
    val setData = MutableLiveData<MutableList<ICCampaign>>()
    val addData = MutableLiveData<MutableList<ICCampaign>>()
    val statusCode = MutableLiveData<ICMessageEvent>()

    var sizeInprogess = 0

    private val interactor = ListCampaignInteractor()

    private var offset = 0

    fun getListCampaign(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null)))
            return
        }

        if (!isLoadMore)
            offset = 0

        interactor.getListCampaign(offset, APIConstants.LIMIT, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {

                    val listAll = mutableListOf<ICCampaign>()
                    val listInprogess = mutableListOf<ICCampaign>()
                    val listEnded = mutableListOf<ICCampaign>()
                    var addEnd = true
                    sizeInprogess = 0

                    for (campaign in obj.data?.rows ?: mutableListOf()) {
                        if (campaign.state.toString().toDouble().toInt() == 3) {
                            if (!isLoadMore) {
                                if (addEnd) {
                                    listEnded.add(ICCampaign().also {
                                        it.id = Constant.TITLE
                                    })
                                    addEnd = false
                                }
                            }
                            listEnded.add(campaign)
                        } else {
                            sizeInprogess++
                            listInprogess.add(campaign)
                        }
                    }

                    listAll.addAll(listInprogess)
                    listAll.addAll(listEnded)

                    if (obj.data?.rows.isNullOrEmpty()) {
                        setData.postValue(mutableListOf(ICCampaign().also {
                            it.id = null
                        }))
                    } else {
                        setData.postValue(listAll)
                    }
                } else {
                    addData.postValue(obj.data?.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, ICError(R.drawable.ic_error_request, error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null)))

            }
        })
    }

}