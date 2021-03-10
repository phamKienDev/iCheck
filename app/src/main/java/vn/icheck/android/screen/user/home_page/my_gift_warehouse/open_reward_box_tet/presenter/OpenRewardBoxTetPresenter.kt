package vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box_tet.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.network.models.ICUnBox_Gift
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box_tet.view.IOpenRewardBoxTetView

/**
 * Created by PhongLH on 3/23/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class OpenRewardBoxTetPresenter(val view: IOpenRewardBoxTetView) : BaseActivityPresenter(view) {

    private val interactor = ListCampaignInteractor()

    fun onGetDataId(intent: Intent) {
        val item = try {
            intent.getSerializableExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }
        view.onGetDataIdSuccess(item as ICBoxReward)
    }

    fun onUnboxGift(mId: String?, numberUnboxIcheck: Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(Constant.ERROR_INTERNET)

            return
        }

        if (mId.isNullOrEmpty()) {
            view.onGetDataError(Constant.ERROR_UNKNOW)
            return
        }

        interactor.unboxGift(mId, numberUnboxIcheck, object : ICApiListener<ICListResponse<ICUnBox_Gift>> {
            override fun onSuccess(obj: ICListResponse<ICUnBox_Gift>) {
                if (!obj.rows.isNullOrEmpty()) {
                    view.onUnboxGiftSuccess(obj.rows,numberUnboxIcheck)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(Constant.ERROR_UNKNOW)
            }
        })
    }
}