package vn.icheck.android.screen.user.follow.following

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_list_follow.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.user.campaign.calback.IRecyclerViewAdapterListener
import vn.icheck.android.screen.user.profile.ProfileActivity
import vn.icheck.android.screen.user.follow.following.adapter.ListFollowingAdapter
import vn.icheck.android.screen.user.follow.following.presenter.ListFollowingPresenter
import vn.icheck.android.screen.user.follow.following.view.IListFollowingView

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListFollowingFragment : BaseFragment<ListFollowingPresenter>(), IListFollowingView, IRecyclerViewAdapterListener<ICUserFollowing> {
    private val adapter = ListFollowingAdapter(this)

    companion object {
        fun newInstance(userID: Long): ListFollowingFragment {
            val fragment = ListFollowingFragment()

            val bundle = Bundle()
            bundle.putLong(Constant.DATA_1, userID)
            fragment.arguments = bundle

            return fragment
        }
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_list_follow

    override val getPresenter: ListFollowingPresenter
        get() = ListFollowingPresenter(this)

    override fun onInitView() {
        setupRecyclerView()
        setupSwipeLayout()
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun setupSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.blue), ContextCompat.getColor(context!!, R.color.blue), ContextCompat.getColor(context!!, R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            presenter.getListFollowing(false)
        }

        swipeLayout.post {
            swipeLayout.isRefreshing = true
            presenter.getData(arguments)
        }
    }

    override fun onGetDataError() {
        swipeLayout.isRefreshing = false
        adapter.setError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
    }

    override fun onShowError(icon: Int, message: String) {
        swipeLayout.isRefreshing = false

        if (adapter.isEmpty) {
            adapter.setError(icon, message)
        } else {
            showShortError(message)
        }
    }

    override fun onGetListSuccess(list: MutableList<ICUserFollowing>, isLoadMore: Boolean) {
        swipeLayout.isRefreshing = false

        if (isLoadMore) {
            adapter.addData(list)
        } else {
            adapter.setData(list)
        }
    }

    override fun onLoadMore() {
        presenter.getListFollowing(true)
    }

    override fun onItemClicked(obj: ICUserFollowing) {
        obj.user?.id?.let {
            if (it == SessionManager.session.user?.id) {
                startActivity<ProfileActivity>()
            } else {
                startActivity<ProfileActivity, Long>(Constant.DATA_1, it)
            }
        }
    }

    override fun onMessageClicked() {
        swipeLayout.isRefreshing = true
        presenter.getListFollowing(false)
    }
}