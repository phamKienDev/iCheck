package vn.icheck.android.network.feature.mission

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICMission
import vn.icheck.android.network.models.ICMissionDetail

class MissionInteractor : BaseInteractor() {

    fun getListMission(id: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICMission>>>) {
        val params = hashMapOf<String, Any?>()
        params["rank"] = 1

        requestNewApi(ICNetworkClient.getSocialApi().getListMission(id, params), listener)
    }

    fun getListMissionActive(id: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICMission>>>) {
        val params = hashMapOf<String, Any?>()
        params["rank"] = 1

        requestNewApi(ICNetworkClient.getSocialApi().getListMissionActive(id, params), listener)
    }


    fun getMissionDetail(id: String, listener: ICNewApiListener<ICResponse<ICMissionDetail>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getMissionsDetail(id), listener)
    }
}