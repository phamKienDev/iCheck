package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_box_reward.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.adapter.BoxRewardAdapter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.presenter.RewardBoxPresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_box.view.IRewardBoxView
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box.OpenRewardBoxActivity

class RewardBoxFragment : BaseFragment<RewardBoxPresenter>(), IRewardBoxView {

    private var adapter: BoxRewardAdapter? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_box_reward


    override val getPresenter: RewardBoxPresenter
        get() = RewardBoxPresenter(this)

    private var requestData = 39

    override fun onInitView() {
        shimmer_view_container.startShimmer()
        initRecyclerview()
        presenter.getDataBoxReward()
    }

    private fun initRecyclerview() {
        adapter = BoxRewardAdapter(this)
        rcvBoxReward.layoutManager = LinearLayoutManager(context)
        rcvBoxReward.adapter = adapter
    }

    override fun onRefresh() {
        presenter.getDataBoxReward()
    }

    override fun onGetDataError(errorMessage: String) {
        adapter?.setErrorCode(errorMessage)
    }

    @SuppressLint("SetTextI18n")
    override fun onSetListDataSuccess(list: MutableList<ICBoxReward>, total: Int?, isLoadMore: Boolean) {
        shimmer_view_container.stopShimmer()
        shimmer_view_container.visibility = View.GONE
        rcvBoxReward.visibility = View.VISIBLE

        if (isLoadMore)
            adapter?.addListData(list)
        else
            adapter?.setListData(list)

        if (!list.isNullOrEmpty()) {
            tvCountBoxReward?.visibility = View.VISIBLE
            tvCountBoxReward?.text = "Gói quà hiện có: ${total}"
        } else {
            tvCountBoxReward?.text = ""
            tvCountBoxReward?.visibility = View.GONE
        }
    }

    override fun onLoadMore() {
        presenter.getDataBoxReward(true)
    }

    override fun onClickItem(item: ICBoxReward) {
//        if (SettingManager.getTheme?.break_reward != null) {
//            if (SettingManager.getTheme?.break_reward?.theme_id == 1) {
//                val intent = Intent(context, OpenRewardBoxTetActivity::class.java)
//                intent.putExtra(Constant.DATA_1, item)
//                startActivityForResult(intent, requestData)
//            } else {
//                val intent = Intent(context, OpenRewardBoxActivity::class.java)
//                intent.putExtra(Constant.DATA_1, item)
//                startActivityForResult(intent, requestData)
//            }
//        } else {
            val intent = Intent(context, OpenRewardBoxActivity::class.java)
            intent.putExtra(Constant.DATA_1, item)
            startActivityForResult(intent, requestData)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestData -> {
                    presenter.getDataBoxReward()
                }
            }
        }
    }
}