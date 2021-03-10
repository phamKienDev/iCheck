package vn.icheck.android.screen.user.missiondetail

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.mission.MissionInteractor
import vn.icheck.android.network.models.ICMissionDetail

class MissionDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val interaction = MissionInteractor()

    val onMissionDetail = MutableLiveData<ICMissionDetail>()
    val onChangeState = MutableLiveData<ICMessageEvent.Type>()
    val onError = MutableLiveData<String>()

    private var missionID: String = ""
    var missionData: ICMissionDetail? = null

    fun getData(intent: Intent?) {
        missionID = try {
            intent?.getStringExtra(Constant.DATA_1) ?: ""
        } catch (e: Exception) {
            ""
        }

        if (missionID.isEmpty()) {
            onError.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        } else {
            getMissionDetail()
        }
    }

    fun getMissionDetail(isUpdate: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isUpdate)
            onChangeState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.getMissionDetail(missionID, object : ICNewApiListener<ICResponse<ICMissionDetail>> {
            override fun onSuccess(obj: ICResponse<ICMissionDetail>) {
                onChangeState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null) {
                    onMissionDetail.postValue(obj.data)
                    missionData = obj.data
                } else {
                    if (!isUpdate)
                        onError.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                if (!isUpdate) {
                    onChangeState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onError.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }
        })
    }
}