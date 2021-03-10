package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.gift_campaign_v2_fragment.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign.adapter.GiftCampaignV2Adapter
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class GiftCampaignV2Fragment(val id: String, val title: String) : BaseFragmentMVVM(), IMessageListener {
    lateinit var viewModel: GiftCampaignV2ViewModel
    val adapter = GiftCampaignV2Adapter(this)

    override val getLayoutID: Int
        get() = R.layout.gift_campaign_v2_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(GiftCampaignV2ViewModel::class.java)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
        txtTitle.text = title
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.resetData()
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.checkInternet(id)
    }

    private fun initListener() {
        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.listData.isNullOrEmpty()) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1) }
            } else {
                ToastUtils.showLongError(context, it.message)
            }
        })

        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.setData(it)
        })

        viewModel.iCoinSuccess.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addiCoin(it)
        })

        viewModel.moraleSuccess.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addMorale(it)
        })
    }

    override fun onMessageClicked() {
        getData()
    }
}