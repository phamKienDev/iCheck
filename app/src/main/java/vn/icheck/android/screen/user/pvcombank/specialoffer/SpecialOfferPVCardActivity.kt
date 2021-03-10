package vn.icheck.android.screen.user.pvcombank.specialoffer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_special_offer_pvcard.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.pvcombank.specialoffer.adapter.SpecialOfferPVCardAdapter
import vn.icheck.android.screen.user.pvcombank.specialoffer.callback.SpecialOfferPVCardListener
import vn.icheck.android.screen.user.pvcombank.specialoffer.viewmodel.SpecialOfferPVCardViewModel

class SpecialOfferPVCardActivity : BaseActivityMVVM(), SpecialOfferPVCardListener {

    private val viewModel : SpecialOfferPVCardViewModel by viewModels()
    private var adapter = SpecialOfferPVCardAdapter(this)

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_offer_pvcard)
        listener()
        initRecyclerView()
        initViewModel()
        viewModel.onGetData()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        adapter.disableLoadMore()

        viewModel.listData.observe(this,{
//            adapter.addListData(it)
        })

        viewModel.isLoadMoreData.observe(this, {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            viewModel.onGetData()
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

        viewModel.errorData.observe(this,  {
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
        viewModel.onGetData(true)
    }

    override fun onTryAgain() {
        viewModel.onGetData()
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            else -> {}
        }
    }
}