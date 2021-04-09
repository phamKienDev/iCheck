package vn.icheck.android.icheckscanditv6

import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ichecklibs.event.ICMessageEvent
import vn.icheck.android.ichecklibs.helper.ApplicationHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.point_user.ICKPointUser

object PointHelper {

    fun updatePoint(campaignID: Long) {
        RedeemPointRepository().getPointUser(campaignID, object : ICApiListener<ICResponse<ICKPointUser>> {
            override fun onSuccess(obj: ICResponse<ICKPointUser>) {
                if (obj.data?.points != null) {
                    SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).putLong("POINT_USER_LOYALTY", obj.data?.points!!)
                }
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT, obj.data?.points))
            }

            override fun onError(error: ICBaseResponse?) {

            }
        })
    }
}