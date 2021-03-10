package vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICAcceptGift
import vn.icheck.android.network.models.ICDetailGift
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse.view.IDetailMyGiftWareHouseView

class DetailMyGiftWareHousePresenter(val view: IDetailMyGiftWareHouseView) : BaseActivityPresenter(view) {

    private val interactor = ListCampaignInteractor()

    fun getDataIntent(intent: Intent?) {
        val idGift = intent?.getStringExtra(Constant.DATA_1)

        if (!idGift.isNullOrEmpty()) {
            view.getDataByIntent(idGift)
            getDetailGift(idGift)
        } else {
            view.onGetDetailError(Constant.ERROR_UNKNOW)
        }
    }

    private fun getDetailGift(idGift: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDetailError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        interactor.getDetailGift(idGift, object : ICApiListener<ICDetailGift> {
            override fun onSuccess(obj: ICDetailGift) {
                view.onShowLoading(false)
                view.getDataDetailGifSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.onGetDetailError(Constant.ERROR_UNKNOW)
            }
        })
    }

    fun getDetailGiftSecond(idGift: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDetailError(Constant.ERROR_INTERNET)
            return
        }

        interactor.getDetailGift(idGift, object : ICApiListener<ICDetailGift> {
            override fun onSuccess(obj: ICDetailGift) {
                view.getDataDetailGifSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDetailError(Constant.ERROR_UNKNOW)
            }
        })
    }

    fun onAcceptDaLayQua(idGift: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDetailError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        interactor.onAcceptDaLayQua(idGift,object :ICApiListener<ICAcceptGift>{
            override fun onSuccess(obj: ICAcceptGift) {
                view.onShowLoading(false)
                view.onAcceptDaLayQuaSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

}