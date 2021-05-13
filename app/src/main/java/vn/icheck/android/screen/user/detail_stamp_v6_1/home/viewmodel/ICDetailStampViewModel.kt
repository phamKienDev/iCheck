package vn.icheck.android.screen.user.detail_stamp_v6_1.home.viewmodel

import android.content.Intent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository

class ICDetailStampViewModel : BaseViewModel() {
    private val repository = DetailStampRepository()

    var data = ""
    var code = ""

    fun getData(intent: Intent?) {
        data = intent?.getStringExtra("data") ?: ""

        code = if (data.isNotEmpty()) {
            if (data.contains("http")) {
                val separated: List<String> = data.split("/")
                if (separated.size > 3)
                    separated.lastOrNull() ?: ""
                else {
                    data
                }
            } else {
                data
            }
        } else {
            intent?.getStringExtra(Constant.DATA_1) ?: ""
        }
    }

    fun getStampDetailV61(lat: String?, lng: String?) = request { repository.getDetailStampV61(code, lat, lng) }
}