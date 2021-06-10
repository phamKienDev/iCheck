package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import javax.inject.Inject

/**
 * Created by PhongLH on 12/12/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class WarrantyHistoryViewModel @Inject constructor() : BaseViewModel() {
    private val repository = DetailStampRepository()

    val onGetDataResult = MutableLiveData<Boolean>()

    private var serial = ""

    fun getData(intent: Intent){
        serial = intent.getStringExtra(Constant.DATA_1) ?: ""
        onGetDataResult.postValue(serial.isNotEmpty())
    }

    fun getHistoryGuarantee() = request { repository.getWarrantyHistory(serial) }
}