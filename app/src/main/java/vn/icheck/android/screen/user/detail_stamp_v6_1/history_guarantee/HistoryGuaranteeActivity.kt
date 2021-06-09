package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.toolbar_light_blue.view.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ActivityHistoryGuaranteeBinding
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.NotificationDialogListener
import vn.icheck.android.network.base.Status
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.adapter.HistoryGuaranteeAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.viewmodel.WarrantyHistoryViewModel
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class HistoryGuaranteeActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    private val adapter = HistoryGuaranteeAdapter(this)

    private val viewModel by viewModels<WarrantyHistoryViewModel>()

    private lateinit var binding: ActivityHistoryGuaranteeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryGuaranteeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.txtTitle.text =  if (StampDetailActivity.isVietNamLanguage == false) {
            "Warranty log"
        } else {
            "Lịch sử bảo hành"
        }

        setupRecyclerView()
        setupListener()
        setupViewModel()
    }

    private fun setupListener() {
        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.rcvHistoryGuarantee.apply {
            layoutManager = LinearLayoutManager(this@HistoryGuaranteeActivity)
            adapter = this@HistoryGuaranteeActivity.adapter
        }
    }

    private fun setupViewModel() {
        viewModel.onGetDataResult.observe(this, {
            this@HistoryGuaranteeActivity.apply {
                if (it) {
                    getHistoryGuarantee()
                } else {
                    DialogHelper.showNotification(this@HistoryGuaranteeActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            ActivityUtils.finishActivity(this@HistoryGuaranteeActivity)
                        }
                    })
                }
            }
        })

        viewModel.getData(intent)
    }

    private fun getHistoryGuarantee() {
        viewModel.getHistoryGuarantee().observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (it.message.isNullOrEmpty()) {
                        DialogHelper.showLoading(this@HistoryGuaranteeActivity)
                    } else {
                        DialogHelper.closeLoading(this@HistoryGuaranteeActivity)
                    }
                }
                Status.ERROR_NETWORK -> {
                    adapter.setError(Constant.ERROR_INTERNET)
                }
                Status.ERROR_REQUEST -> {
                    adapter.setError(Constant.ERROR_UNKNOW)
                }
                Status.SUCCESS -> {
                    if (it.data?.data.isNullOrEmpty()) {
                        adapter.setError(Constant.ERROR_EMPTY)
                    } else {
                        adapter.setListData(it.data!!.data!!)
                    }
                }
            }
        })
    }

    override fun onLoadMore() {

    }

    override fun onMessageClicked() {
        getHistoryGuarantee()
    }
}