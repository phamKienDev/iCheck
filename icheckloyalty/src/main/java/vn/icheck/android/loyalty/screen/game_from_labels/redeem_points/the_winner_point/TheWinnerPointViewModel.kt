package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.the_winner_point

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.network.BaseModel
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

internal class TheWinnerPointViewModel : BaseViewModel<BaseModel<ICKPointUser>>() {
    private val topWinnerRepository = RedeemPointRepository()
    private val theWinnerRepository = RedeemPointRepository()

    val onTopWinner = MutableLiveData<BaseModel<ICKPointUser>>()

    fun getListHeaderWinner() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        topWinnerRepository.dispose()

        topWinnerRepository.getTopWinnerPoint(collectionID, object : ICApiListener<ICKResponse<ICKListResponse<ICKPointUser>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKPointUser>>) {

                onTopWinner.postValue(BaseModel(obj.data?.rows
                        ?: mutableListOf(), null, ICKViewType.HEADER_TYPE))

                getListWinner()
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }

    fun getListWinner(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            theWinnerRepository.dispose()
        }

        theWinnerRepository.getTheWinnerPoint(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKPointUser>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKPointUser>>) {

                offset += APIConstants.LIMIT

                val listData = mutableListOf<BaseModel<ICKPointUser>>()

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