package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.view.IDetailCampaignView

class DetailCampaignPresenter(val view: IDetailCampaignView) : BaseActivityPresenter(view) {

    private val interactor = ListCampaignInteractor()

    var id: String? = null

    fun getDataIntent(intent: Intent?) {
        id = try {
            intent?.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }
        getCampaignDetail(id)
    }

    fun getCampaignDetail(id: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (id.isNullOrEmpty()) {
            view.onGetDataError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        view.onShowLoading(true)

        interactor.getDetailCampaign(id, object : ICApiListener<ICDetail_Campaign> {
            override fun onSuccess(obj: ICDetail_Campaign) {
                view.onShowLoading(false)
                view.onGetDataSucces(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.onGetDataError(getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai))
            }
        })

    }

}