package vn.icheck.android.screen.user.history_loading_card.history_buy_topup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history_buy_topup.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.history_loading_card.history_buy_topup.adapter.HistoryBuyTopupAdapter
import vn.icheck.android.screen.user.history_loading_card.history_buy_topup.view.IHistoryBuyTopupView
import vn.icheck.android.screen.user.history_loading_card.history_buy_topup.viewmodel.HistoryBuyTopupViewModel
import vn.icheck.android.util.kotlin.ToastUtils

class HistoryBuyTopupFragment : BaseFragmentMVVM(), IHistoryBuyTopupView {

    private lateinit var viewModel: HistoryBuyTopupViewModel
    private var adapter = HistoryBuyTopupAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_buy_topup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryBuyTopupViewModel::class.java)
        initRecyclerview()
        listenerGetData()
        viewModel.onGetDataBuyTopup()
    }

    private fun initRecyclerview() {
        rcvHistoryBuyTopup.layoutManager = LinearLayoutManager(context)
        rcvHistoryBuyTopup.adapter = adapter
    }

    private fun listenerGetData(){
        adapter.disableLoadMore()

        viewModel.isLoadMoreData.observe(viewLifecycleOwner,Observer {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.listDataBuyTopup.observe(viewLifecycleOwner, Observer {
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
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
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

        viewModel.messageError.observe(viewLifecycleOwner, Observer {
            ToastUtils.showLongError(context, it)
        })
    }

    override fun onLoadMore() {
        viewModel.onGetDataBuyTopup(true)
    }

    override fun onRefresh() {
        viewModel.onGetDataBuyTopup()
        adapter.disableLoadMore()
    }

    override fun onClickLoadNow(item: ICRechargePhone) {
        DialogHelper.showConfirm(context, "Bạn đồng ý nạp mã thẻ này?", "*100*${item.card?.pin}#", true, object : ConfirmDialogListener {
            override fun onDisagree() {
                DialogHelper.closeLoading(this@HistoryBuyTopupFragment)
            }

            override fun onAgree() {
                DialogHelper.closeLoading(this@HistoryBuyTopupFragment)
                val uri = "*100*" + item.card?.pin + "#"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", uri, null))
                startActivity(intent)
            }
        })
    }
}