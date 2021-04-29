package vn.icheck.android.screen.user.list_campaign

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_campaign.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.screen.user.campaign_onboarding.CampaignOnboardingActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.ToastUtils

class ListCampaignActivity : BaseActivityMVVM(), ListCampaignCallback {

    private var adapter = ListCampaignAdapter(this)
    private lateinit var viewModel: ListCampaignViewModel

    private val requestGift = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_campaign)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        viewModel = ViewModelProvider(this).get(ListCampaignViewModel::class.java)
        onShowShimmer()
        initRecyclerview()
        initView()
        listenerGetData()
    }

    private fun onShowShimmer() {
        shimmer_view_container.startShimmer()
    }

    private fun initRecyclerview() {
        rcvMission.layoutManager = LinearLayoutManager(this)
        rcvMission.adapter = adapter
    }

    private fun initView() {
        imgBack.setOnClickListener {
            finish()
        }

        swipe.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary), ContextCompat.getColor(this, R.color.colorPrimary), ContextCompat.getColor(this, R.color.colorPrimary))

        swipe.setOnRefreshListener {
            getData()
        }

        swipe.post {
            getData()
        }
    }

    private fun getData() {
        swipe.isRefreshing = true
        adapter.resetData(true)
        viewModel.getListCampaign()
    }

    private fun listenerGetData() {
        viewModel.setData.observe(this) {
            onHideShimmer()
            swipe.isRefreshing = false
            adapter.setListData(it)
            adapter.sizeInprogess = viewModel.sizeInprogess
        }


        viewModel.addData.observe(this) {
            onHideShimmer()
            swipe.isRefreshing = false
            adapter.addListCampaign(it)
        }

        viewModel.statusCode.observe(this) {
            onHideShimmer()
            swipe.isRefreshing = false
            val data = it.data as ICError
            if (adapter.isEmpty) {
                adapter.setError(data)
            } else {
                ToastUtils.showShortError(this, data.message)
            }
        }
    }


    private fun onHideShimmer() {
        shimmer_view_container.stopShimmer()
        swipe.visibility = View.VISIBLE
        shimmer_view_container.visibility = View.GONE
    }

    override fun clickGift(objCampaign: ICCampaign) {
        val intent = if (objCampaign.state.toString().toDouble().toInt() == 0 || objCampaign.state.toString().toDouble().toInt() == 3) {
            Intent(this, CampaignOnboardingActivity::class.java)
        } else {
            Intent(this, if (!objCampaign.hasOnboarding) CampaignOnboardingActivity::class.java else ListShakeGridBoxActivity::class.java)
        }

        TrackingAllHelper.tagCampaignClicked(objCampaign.id)
        objCampaign.hasOnboarding = true
        intent.putExtra(Constant.DATA_1, objCampaign)
        intent.putExtra(Constant.DATA_2, 1)
        startActivityForResult(intent, requestGift)
    }


    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListCampaign(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == requestGift) {
                val campaign = data?.getSerializableExtra(Constant.DATA_1)
                if (campaign is ICCampaign && campaign != null) {
                    adapter.updateCampaign(campaign)
                }
            }
        }
    }
}
