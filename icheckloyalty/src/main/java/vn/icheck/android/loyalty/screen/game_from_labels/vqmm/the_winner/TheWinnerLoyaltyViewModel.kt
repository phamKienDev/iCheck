package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.the_winner

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.network.BaseModel
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKCampaign
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.VQMMRepository

internal class TheWinnerLoyaltyViewModel : BaseViewModel<BaseModel<ICKCampaign>>() {
    private val topWinnerRepository = VQMMRepository()
    private val theWinnerRepository = VQMMRepository()

    val topWinner = MutableLiveData<BaseModel<ICKCampaign>>()

    fun getTopWinnerLoyalty() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        topWinnerRepository.dispose()

        topWinnerRepository.getTopTheWinnerLoyalty(collectionID, object : ICApiListener<ICKResponse<ICKListResponse<ICKCampaign>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKCampaign>>) {
                topWinner.postValue(BaseModel(obj.data?.rows
                        ?: mutableListOf(), null, ICKViewType.HEADER_TYPE))

                getTheWinnerLoyalty()
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }

    fun getTheWinnerLoyalty(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            theWinnerRepository.dispose()
        }

        theWinnerRepository.getTheWinnerLoyalty(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKCampaign>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKCampaign>>) {

                offset += APIConstants.LIMIT

                val listData = mutableListOf<BaseModel<ICKCampaign>>()

                for (item in obj.data?.rows ?: mutableListOf()) {
                    listData.add(BaseModel(null, item, ICKViewType.ITEM_TYPE))
                }

                if (!isLoadMore) {
                    onSetData.postValue(listData ?: mutableListOf())
                } else {
                    onAddData.postValue(listData ?: mutableListOf())
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}