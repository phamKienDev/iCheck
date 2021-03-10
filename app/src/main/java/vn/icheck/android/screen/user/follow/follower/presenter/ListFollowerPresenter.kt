package vn.icheck.android.screen.user.follow.follower.presenter

import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.follow.FollowInteractor
import vn.icheck.android.network.models.ICUserFollower
import vn.icheck.android.screen.user.follow.follower.view.IListFollowerView

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListFollowerPresenter(val view: IListFollowerView) : BaseFragmentPresenter(view) {
    private val interaction = FollowInteractor()

    private var userID: Long = -1

    private var page = 0

    fun getData(bundle: Bundle?) {
        userID = try {
            bundle?.getLong(Constant.DATA_1, -1) ?: -1L
        } catch (e: Exception) {
            -1L
        }

        if (userID != -1L) {
            getListFollower(false)
        } else {
            view.onGetDataError()
        }
    }

    fun getListFollower(isLoadMore: Boolean) {
        if (userID == -1L) {
            view.onGetDataError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onShowError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isLoadMore) {
            page = 0
        }

        interaction.getUserFollowers(userID, "user", page, object : ICApiListener<ICListResponse<ICUserFollower>> {
            override fun onSuccess(obj: ICListResponse<ICUserFollower>) {
                page += APIConstants.LIMIT
                view.onGetListSuccess(obj.rows, isLoadMore)
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    view.onShowError(R.drawable.ic_error_request, it)
                }
            }
        })
    }
}