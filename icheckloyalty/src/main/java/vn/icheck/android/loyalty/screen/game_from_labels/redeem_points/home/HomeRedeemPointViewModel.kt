package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

class HomeRedeemPointViewModel : BaseViewModel<ICKBoxGifts>() {
    private val overViewRepository = RedeemPointRepository()
    private val repository = RedeemPointRepository()

    val onOverView = MutableLiveData<ICKPointUser>()

    fun getOverView() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        overViewRepository.getPointUser(collectionID, object : ICApiListener<ICKResponse<ICKPointUser>> {
            override fun onSuccess(obj: ICKResponse<ICKPointUser>) {
                if (obj.data == null) {
                    SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).putLong(ConstantsLoyalty.POINT_USER_LOYALTY, 0)
                } else {
                    SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).putLong(ConstantsLoyalty.POINT_USER_LOYALTY, obj.data?.points
                            ?: 0)
                }
                obj.data?.let {
                    onOverView.postValue(it)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }

    fun getProductList(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getListRedemptionHistory(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKBoxGifts>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKBoxGifts>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        setErrorEmpty(0, "", "", "", 0, 0)
                    } else {
                        onSetData.postValue(obj.data?.rows)
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}