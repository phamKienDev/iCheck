package vn.icheck.android.screen.user.follow.follower

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_list_follow.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUserFollower
import vn.icheck.android.screen.user.campaign.calback.IRecyclerViewAdapterListener
import vn.icheck.android.screen.user.profile.ProfileActivity
import vn.icheck.android.screen.user.follow.follower.adapter.ListFollowerAdapter
import vn.icheck.android.screen.user.follow.follower.presenter.ListFollowerPresenter
import vn.icheck.android.screen.user.follow.follower.view.IListFollowerView

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListFollowerFragment : BaseFragment<ListFollowerPresenter>(), IListFollowerView, IRecyclerViewAdapterListener<ICUserFollower> {
    private val adapter = ListFollowerAdapter(this)

    companion object {
        fun newInstance(userID: Long): ListFollowerFragment {
            val fragment = ListFollowerFragment()

            val bundle = Bundle()
            bundle.putLong(Constant.DATA_1, userID)
            fragment.arguments = bundle

            return fragment
        }
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_list_follow

    override val getPresenter: ListFollowerPresenter
        get() = ListFollowerPresenter(this)

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
            swipeLayout.isRefreshing = true
            presenter.getListFollower(false)
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

    override fun onGetListSuccess(list: MutableList<ICUserFollower>, isLoadMore: Boolean) {
        swipeLayout.isRefreshing = false

        if (isLoadMore) {
            adapter.addData(list)
        } else {
            adapter.setData(list)
        }
    }

    override fun onLoadMore() {
        presenter.getListFollower(true)
    }

    override fun onItemClicked(obj: ICUserFollower) {
        obj.account.id.let {
            if (it == SessionManager.session.user?.id?.toLong()) {
                startActivity<ProfileActivity>()
            } else {
                startActivity<ProfileActivity, Long>(Constant.DATA_1, it)
            }
        }
    }

    override fun onMessageClicked() {
        swipeLayout.isRefreshing = true
        presenter.getListFollower(false)
    }
}