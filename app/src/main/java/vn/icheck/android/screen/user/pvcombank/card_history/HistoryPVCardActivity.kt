package vn.icheck.android.screen.user.pvcombank.card_history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_history_p_v_card.*
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.toolbar_pvcombank.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.user.pvcombank.card_history.adapter.HistoryPVCardAdapter
import vn.icheck.android.screen.user.pvcombank.card_history.adapter.HistoryPVTransactionAdapter
import vn.icheck.android.screen.user.pvcombank.confirmunlockcard.ConfirmUnlockPVCardActivity
import vn.icheck.android.ui.carousel_recyclerview.CenterScrollListener
import vn.icheck.android.ui.carousel_recyclerview.ZoomCenterCardLayoutManager
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.visibleOrGone

class HistoryPVCardActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    private lateinit var cardAdapter: HistoryPVCardAdapter
    private lateinit var transactionAdapter: HistoryPVTransactionAdapter
    private lateinit var viewModel: HistoryPVCardViewModel
    private lateinit var manager: ZoomCenterCardLayoutManager
    private lateinit var snapHelper: PagerSnapHelper

    private var oldPosition = -1
    private var actionScroll = false
    private var isInit = false
    private var cardId: String? = null

    private val requestFullCard = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_p_v_card)

        viewModel = ViewModelProvider(this).get(HistoryPVCardViewModel::class.java)
        initView()
        initRcvCard()
        initRcvTransaction()
        initData()
        getListCard()
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    private fun initView() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
        txtTitle.text = getString(R.string.lich_su_giao_dich)
    }

    private fun initRcvCard() {
        snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerviewCard)
        cardAdapter = HistoryPVCardAdapter()
        manager = ZoomCenterCardLayoutManager(this)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerviewCard.layoutManager = manager
        recyclerviewCard.adapter = cardAdapter
    }

    private fun initRcvTransaction() {
        transactionAdapter = HistoryPVTransactionAdapter(this)
        recyclerviewTransaction.adapter = transactionAdapter
        recyclerviewTransaction.layoutManager = LinearLayoutManager(this)
    }

    private fun initData() {
        viewModel.onSetTransaction.observe(this, {
            if (it.isNullOrEmpty()) {
                transactionAdapter.setError(R.drawable.ic_group_120dp, "Chưa có lịch sử giao dịch", -1)
            } else {
                transactionAdapter.setListData(it)
            }
            recyclerviewTransaction.smoothScrollToPosition(0)
        })

        viewModel.onAddTransaction.observe(this, {
            transactionAdapter.addListData(it)
        })

        viewModel.onError.observe(this, {
            if (cardAdapter.isEmpty && transactionAdapter.isEmpty) {
                transactionAdapter.setError(R.drawable.ic_group_120dp, "Chưa có lịch sử giao dịch", -1)
            } else {
                showShortError(it.message ?: "")
            }
        })
        viewModel.statusCode.observe(this, {
            when (it) {
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
    }

    private fun getListCard() {
        viewModel.getListCard().observe(this, { result ->
            when (result.status) {
                Status.LOADING -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
                }
                Status.ERROR_NETWORK -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    viewModel.onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
                }
                Status.ERROR_REQUEST -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                }
                Status.SUCCESS -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    val listCards = result.data?.data?.rows ?: mutableListOf()

                    if (!isInit) {
//                        recyclerviewCard.addOnScrollListener(CenterScrollListener())
//                        recyclerviewCard.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                            override fun onGlobalLayout() {
//                                recyclerviewCard.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                                val range = resources.getDimension(R.dimen.width_your_card_item_horizontal).toInt()
//                                val margin = resources.getDimension(R.dimen.margin_your_card_item_horizontal).toInt()
//                                val extent: Int = (recyclerviewCard.width - range) / 2 - margin
//                                recyclerviewCard.setPadding(extent, 0, extent, 0)
//                            }
//                        })
                        recyclerviewCard.setPadding(SizeHelper.dpToPx(3.5), 0, SizeHelper.dpToPx(3.5), 0)
                        recyclerviewCard.clipToPadding = false
                        recyclerviewCard.setHasFixedSize(true)
                        isInit = true
                    }

                    recyclerviewCard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            when (newState) {
                                RecyclerView.SCROLL_STATE_IDLE -> {
                                    val view = snapHelper.findSnapView(manager)
                                    val position = view?.let { manager.getPosition(it) }
                                    if (position != oldPosition && actionScroll) {
                                        actionScroll = false
                                        oldPosition = position ?: -1
                                        cardId = listCards[position!!].cardId
                                        viewModel.getListTransaction(cardId)
                                    }
                                }
                                RecyclerView.SCROLL_STATE_DRAGGING -> {
                                    actionScroll = true
                                }
                                RecyclerView.SCROLL_STATE_SETTLING -> {
                                    actionScroll = true
                                }
                                else -> {
                                }
                            }
                        }
                    })

                    cardId = listCards.firstOrNull()?.cardId
                    cardAdapter.disableLoadMore()
                    cardAdapter.setListData(listCards)
                    viewMid.visibleOrGone(!listCards.isNullOrEmpty())
                    viewModel.getListTransaction(cardId)
                }
            }
        })
    }

    override fun onMessageClicked() {
        viewModel.getListTransaction(cardId)
    }

    override fun onLoadMore() {
        viewModel.getListTransaction(cardId, true)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            ICMessageEvent.Type.SHOW_OR_HIDE_PV_FULLCARD -> {
                if (event.data != null && event.data is ICListCardPVBank) {
                    if (event.data.isShow) {
                        //Ẩn thông tin
                        cardAdapter.showOrHide(event.data.cardId, false)
                    } else {
                        //Request để lấy full thông tin
                        val intent = Intent(this@HistoryPVCardActivity, ConfirmUnlockPVCardActivity::class.java)
                        intent.putExtra(Constant.DATA_1, event.data)
                        intent.putExtra(Constant.DATA_2, "full_card")
                        startActivityForResult(intent, requestFullCard)
                    }
                }
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestFullCard -> {
                    val id = data?.getStringExtra(Constant.DATA_2)
                    if (id != null) {
                        cardAdapter.showOrHide(id, true)
                    }
                }
            }
        }
    }
}