package vn.icheck.android.screen.user.detail_stamp_v6_1.home.viewmodel

import android.content.Intent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.models.detail_stamp_v6_1.ICUpdateCustomerGuarantee
import javax.inject.Inject

class ICDetailStampViewModel: BaseViewModel() {
    private val repository = DetailStampRepository()

    var barcode = ""
    var lat: Double? = null
    var lng: Double? = null

    var codeInput = ""
    var loyaltyObj: ICKLoyalty? = null

    var user: ICUpdateCustomerGuarantee? = null

    fun getData(intent: Intent?) {
        val data = intent?.getStringExtra("data") ?: ""

        barcode = if (data.isNotEmpty()) {
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

    fun getStampDetailV61() = request { repository.getDetailStampV61(user, barcode, lat, lng) }

    fun getStampConfig() = request { repository.getStampConfig() }
}