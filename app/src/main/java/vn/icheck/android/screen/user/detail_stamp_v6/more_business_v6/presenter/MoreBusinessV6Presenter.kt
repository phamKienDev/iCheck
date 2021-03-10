package vn.icheck.android.screen.user.detail_stamp_v6.more_business_v6.presenter

import android.content.Intent
import com.google.gson.reflect.TypeToken
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectBusinessV6
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectOtherProductV6
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.detail_stamp_v6.more_business_v6.view.IMoreBusinessV6View

/**
 * Created by PhongLH on 1/3/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class MoreBusinessV6Presenter(val view: IMoreBusinessV6View) : BaseActivityPresenter(view) {
    private var listOtherProduct = mutableListOf<ICObjectOtherProductV6>()

    fun getDataIntent(intent: Intent?) {
        val type = intent?.getIntExtra(Constant.DATA_1, 0)

        if (type == 1) {
            val item = intent.getSerializableExtra(Constant.DATA_2) as ICBarcodeProductV1.VendorPage
            view.onGetDataIntentVendorSuccess(item)
        } else {
            val item = intent?.getSerializableExtra(Constant.DATA_2) as ICObjectBusinessV6
            val list = getListOtherProduct(intent.getStringExtra(Constant.DATA_3)) ?: mutableListOf()
            listOtherProduct.addAll(list)
            view.onGetDataIntentDistributorSuccess(item, listOtherProduct)
        }
    }

    private fun getListOtherProduct(json: String?): MutableList<ICObjectOtherProductV6>? {
        if (json.isNullOrEmpty()) {
            return null
        }

        return try {
            val listType = object : TypeToken<MutableList<ICObjectOtherProductV6>>() {}.type
            JsonHelper.gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }
}