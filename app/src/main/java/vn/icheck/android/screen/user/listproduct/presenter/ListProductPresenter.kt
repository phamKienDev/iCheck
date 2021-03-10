package vn.icheck.android.screen.user.listproduct.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.listproduct.view.IListProductView

/**
 * Created by VuLCL on 10/23/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListProductPresenter(val view: IListProductView) : BaseActivityPresenter(view) {
    private val interaction = ProductInteractor()

    private var url = ""
    private var params = hashMapOf<String, Any>()

    private var offset = 0

    fun getData(intent: Intent?) {
        url = try {
            intent?.getStringExtra(Constant.DATA_1) ?: ""
        } catch (e: Exception) {
            ""
        }

        val mParams = try {
            intent?.getSerializableExtra(Constant.DATA_2) as HashMap<String, Any>
        } catch (e: Exception) {
            null
        }

        mParams?.let {
            params.putAll(it)
        }

        val title = try {
            intent?.getStringExtra(Constant.DATA_3)
        } catch (e: Exception) {
            null
        }

        if (!title.isNullOrEmpty()) {
            view.onSetCollectionName(title)
        }

        if (url.isEmpty()) {
            view.onGetCollectionIDError()
        } else {
            getListProduct(false)
        }
    }

    fun getListProduct(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetProductError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        interaction.getListProduct(url, params, object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                offset += APIConstants.LIMIT
                if (!obj.data?.rows.isNullOrEmpty()) {
                    view.onGetProductSuccess(obj.data?.rows!!, isLoadMore)
                }
            }

            override fun onError(error: ICResponseCode?) {
                val message = error?.message ?: getString(R.string.khong_the_truy_xuat_du_lieu_vui_long_thu_lai)
                view.onGetProductError(message)
            }
        })
    }
}