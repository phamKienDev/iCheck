package vn.icheck.android.loyalty.helper

import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKPointUser
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

object PointHelper {

    fun updatePoint(campaignID: Long) {
        RedeemPointRepository().getPointUser(campaignID, object : ICApiListener<ICKResponse<ICKPointUser>> {
            override fun onSuccess(obj: ICKResponse<ICKPointUser>) {
                if (obj.data?.points != null) {
                    SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).putLong(ConstantsLoyalty.POINT_USER_LOYALTY, obj.data?.points!!)
                }
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT, obj.data?.points))
            }

            override fun onError(error: ICKBaseResponse?) {

            }
        })
    }
}