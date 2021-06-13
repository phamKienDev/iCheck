package vn.icheck.android.screen.user.history_loading_card.history_loaded_topup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history_loaded_topup.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.history_loading_card.history_buy_topup.viewmodel.HistoryBuyTopupViewModel
import vn.icheck.android.screen.user.history_loading_card.history_loaded_topup.adapter.HistoryLoadedTopupAdapter
import vn.icheck.android.screen.user.history_loading_card.history_loaded_topup.view.IHistoryLoadedTopupView

class HistoryLoadedTopupFragment : BaseFragmentMVVM(), IHistoryLoadedTopupView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_loaded_topup, container, false)
    }

    private lateinit var viewModel: HistoryBuyTopupViewModel
    private var adapter = HistoryLoadedTopupAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryBuyTopupViewModel::class.java)
        initRecyclerview()
        listenerGetData()
        viewModel.onGetDataLoadedTopup()
    }

    private fun initRecyclerview() {
        rcvHistoryLoadedTopup.layoutManager = LinearLayoutManager(context)
        rcvHistoryLoadedTopup.adapter = adapter
    }

    private fun listenerGetData(){
        adapter.disableLoadMore()

        viewModel.isLoadMoreData.observe(viewLifecycleOwner,Observer {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.listDataLoadedTopup.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.addListData(it)
            } else {
                adapter.setErrorCode(Constant.ERROR_EMPTY)
            }
        })

        viewModel.statusCode.observe(viewLifecycleOwner, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(context, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            viewModel.onGetDataBuyTopup()
                        }
                    })
                }
                else -> {}
            }
        })

        viewModel.errorData.observe(viewLifecycleOwner, Observer {
            when (it) {
                Constant.ERROR_EMPTY -> {
                    adapter.setErrorCode(Constant.ERROR_EMPTY)
                }

                Constant.ERROR_SERVER -> {
                    adapter.setErrorCode(Constant.ERROR_SERVER)
                }

                Constant.ERROR_INTERNET -> {
                    adapter.setErrorCode(Constant.ERROR_INTERNET)
                }
            }
        })
    }

    override fun onLoadMore() {
        viewModel.onGetDataLoadedTopup(true)
    }

    override fun onRefresh() {
        viewModel.onGetDataLoadedTopup()
    }
}