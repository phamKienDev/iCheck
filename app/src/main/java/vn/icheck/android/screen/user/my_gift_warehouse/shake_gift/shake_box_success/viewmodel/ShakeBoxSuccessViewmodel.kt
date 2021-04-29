package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake_box_success.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICOpenShakeGift

class ShakeBoxSuccessViewmodel : BaseViewModel() {
    private val repository = SettingRepository()

    val objCampaign = MutableLiveData<ICCampaign>()
    var campaign : ICCampaign? = null
    var shakeGift : ICOpenShakeGift? = null
    val objICOpenShakeGift = MutableLiveData<ICOpenShakeGift>()
    val onError = MutableLiveData<ICMessageEvent>()

    fun getDataIntent(intent: Intent?) {
        val data = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICCampaign
        } catch (e: Exception) {
            null
        }

        val data2 = try {
            intent?.getSerializableExtra(Constant.DATA_2) as ICOpenShakeGift
        } catch (e: Exception) {
            null
        }

        if (data != null && data2 != null) {
            campaign = data
            shakeGift = data2
            objCampaign.postValue(data!!)
            objICOpenShakeGift.postValue(data2!!)
        } else {
            onError.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR))
        }
    }

    fun getCoin() = request { repository.getCoin() }
}