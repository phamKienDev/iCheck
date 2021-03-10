package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.infor_campaign_v2_fragment.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.adapter.InforCampaignAdapter
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.viewmodel.InforCampaignViewModel

class InforCampaignV2Fragment(val idCampaign: String) : BaseFragmentMVVM() {

    override val getLayoutID: Int
        get() = R.layout.infor_campaign_v2_fragment

    private lateinit var viewModel: InforCampaignViewModel

    private val adapter = InforCampaignAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(InforCampaignViewModel::class.java)
        viewModel.getInfoCampaign(idCampaign)
        setupRecyclerView()
        listenerGetData()
        listener()
    }

    private fun listener() {
        imgBackGray.setOnClickListener {
            activity?.onBackPressed()
        }

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        var backgroundHeight = 0

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (backgroundHeight <= 0) {
                    backgroundHeight = toolbarAlpha.height + (toolbarAlpha.height / 2)
                }

                val visibility = viewModel.getHeaderAlpha(recyclerView.computeVerticalScrollOffset(), backgroundHeight)
                toolbarAlpha.alpha = visibility
                viewShadow.alpha = visibility
            }
        })
    }

    private fun listenerGetData() {
        viewModel.dataCampaign.observe(viewLifecycleOwner, Observer {
            txtTitle.text = it.title
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            adapter.addData(it)
        })

        viewModel.statusCode.observe(viewLifecycleOwner, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(context, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            viewModel.getInfoCampaign(idCampaign)
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(viewLifecycleOwner, Observer {
            when (it) {
                Constant.ERROR_UNKNOW -> {
                    DialogHelper.showNotification(context, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            activity?.onBackPressed()
                        }
                    })
                }

                Constant.ERROR_EMPTY -> {
                    DialogHelper.showNotification(context, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            activity?.onBackPressed()
                        }
                    })
                }

                Constant.ERROR_SERVER -> {
                    DialogHelper.showNotification(context, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            activity?.onBackPressed()
                        }
                    })
                }

                Constant.ERROR_INTERNET -> {
                    DialogHelper.showNotification(context, R.string.khong_the_truy_cap_vui_long_thu_lai_sau, false, object : NotificationDialogListener {
                        override fun onDone() {
                            activity?.onBackPressed()
                        }
                    })
                }
            }
        })
    }
}