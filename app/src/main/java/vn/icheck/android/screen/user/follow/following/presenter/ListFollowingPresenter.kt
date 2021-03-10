package vn.icheck.android.screen.user.follow.following.presenter

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
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.user.follow.following.view.IListFollowingView

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListFollowingPresenter(val view: IListFollowingView) : BaseFragmentPresenter(view) {
    private val interaction = FollowInteractor()

    private var userID: Long = -1

    private var offset = 0

    fun getData(bundle: Bundle?) {
        userID = try {
            bundle?.getLong(Constant.DATA_1, -1) ?: -1L
        } catch (e: Exception) {
            -1L
        }

        if (userID != -1L) {
            getListFollowing(false)
        } else {
            view.onGetDataError()
        }
    }

    fun getListFollowing(isLoadMore: Boolean) {
        if (userID == -1L) {
            view.onGetDataError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onShowError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interaction.getUserFollowing(userID, "user", offset, APIConstants.LIMIT, object : ICApiListener<ICListResponse<ICUserFollowing>> {
            override fun onSuccess(obj: ICListResponse<ICUserFollowing>) {
                offset += APIConstants.LIMIT
                view.onGetListSuccess(obj.rows, isLoadMore)
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.khong_the_truy_xuat_du_lieu_vui_long_thu_lai)
                view.onShowError(R.drawable.ic_error_request, message)
            }
        })
    }
}