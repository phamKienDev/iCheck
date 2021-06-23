package vn.icheck.android.screen.user.ads_more

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.network.models.ICAdsData
import vn.icheck.android.network.models.ICAdsNew

class AdsMoreViewModel : BaseViewModel<Any>() {

    var adsModel: ICAdsNew? = null

    val setData = MutableLiveData<ICAdsNew>()
    val setError = MutableLiveData<String>()

    fun getDataIntent(intent: Intent) {
        adsModel = intent.getSerializableExtra(Constant.DATA_1) as ICAdsNew?

        if (adsModel != null) {
            setData.postValue(adsModel)
        } else {
            setError.postValue(ICheckApplication.getString(R.string.khong_co_du_lieu))
        }
    }
}