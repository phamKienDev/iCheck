package vn.icheck.android.screen.user.pvcombank.listcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import kotlinx.android.synthetic.main.activity_list_card_pvcombank.*
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.network.models.pvcombank.ICLockCard
import vn.icheck.android.screen.user.pvcombank.confirmunlockcard.ConfirmUnlockPVCardActivity
import vn.icheck.android.screen.user.pvcombank.home.HomePVCardActivity
import vn.icheck.android.screen.user.pvcombank.listcard.adapter.ListCardPVComBankAdapter
import vn.icheck.android.screen.user.pvcombank.listcard.callbacks.CardPVComBankListener
import vn.icheck.android.screen.user.pvcombank.listcard.viewModel.ListCardPVComBankViewModel
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.ui.carousel_recyclerview.CenterScrollListener
import vn.icheck.android.ui.carousel_recyclerview.LinePagerIndicatorBankDecoration
import vn.icheck.android.ui.carousel_recyclerview.ZoomCenterCardLayoutManager
import vn.icheck.android.util.ick.showSimpleSuccessToast

class ListPVCardActivity : BaseActivityMVVM(), CardPVComBankListener {
    private lateinit var adapter: ListCardPVComBankAdapter

    private val viewModel: ListCardPVComBankViewModel by viewModels()

    //    private val requestLockCard = 1
    private val requestUnLockCard = 2
    private val requestFullCard = 3

    private var isInit = false

    override fun isRegisterEventBus(): Boolean {
        return true
    }

//    val someActivityResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val id = result.data?.getStringExtra(Constant.DATA_1)
//            if (id != null) {
//                showSimpleSuccessToast("Thẻ đã khóa thành công")
//                adapter.setLockCard(id, true)
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_card_pvcombank)
        listener()
        initRecyclerView()
        initViewModel()
        onRefresh()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        adapter = ListCardPVComBankAdapter(this)
        val manager = ZoomCenterCardLayoutManager(this)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        /*
        * Set list data
        */
        viewModel.listData.observe(this, Observer {
            if (!isInit) {
                recyclerView.addOnScrollListener(CenterScrollListener())
                recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val range = resources.getDimension(R.dimen.width_your_card_item_horizontal).toInt()
                        val margin = resources.getDimension(R.dimen.margin_your_card_item_horizontal).toInt()
                        val extent: Int = (recyclerView.width - range) / 2 - margin
                        recyclerView.setPadding(extent, 0, extent, 0)
                    }
                })
                recyclerView.clipToPadding = false
                recyclerView.setHasFixedSize(true)
                recyclerView.smoothScrollBy(5, 0)
                recyclerView.addItemDecoration(LinePagerIndicatorBankDecoration())
                isInit = true
            }

            adapter.setListData(it)
        })


        /*
        * Lock card thành công
        */
        viewModel.dataLockCard.observe(this, Observer {
            if (it.verification?.bypass == "N") {
                showSimpleSuccessToast("Thẻ đã khóa thành công")
                adapter.setLockCard(viewModel.cardId, viewModel.pos, true)
            }
        })

        /*
        * Set card làm thẻ chính
        */
        viewModel.defaultCard.observe(this, Observer {
            adapter.setDefaultCard(viewModel.cardId, viewModel.pos)
        })

        viewModel.errorString.observe(this, Observer {
            showShortError(it)
        })

        viewModel.errorData.observe(this, Observer {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            params.setMargins(0, 0, 0, 0)
            recyclerView.layoutParams = params
            adapter.setErrorCode(it)
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            onRefresh()
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
    }

    /*
    * Refresh data khi lỗi
    */
    override fun onRefresh() {
        viewModel.getListCards().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
                }
                Status.ERROR_NETWORK -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                }
                Status.ERROR_REQUEST -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    viewModel.errorData.postValue(Constant.ERROR_SERVER)
                }
                Status.SUCCESS -> {
                    viewModel.statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (!it.data?.data?.rows.isNullOrEmpty()) {
                        viewModel.listData.postValue(it.data!!.data!!.rows)
                    } else {
                        viewModel.errorData.postValue(Constant.ERROR_EMPTY)
                    }
                }
            }
        })
    }

    /*
        * Dialog set card làm default
        */
    override fun onClickUseDefaulCard(item: ICListCardPVBank, position: Int) {
        if (!item.cardId.isNullOrEmpty()) {
            viewModel.cardId = item.cardId!!
            viewModel.pos = position

            DialogHelper.showDialogLockCardBank(this, R.string.dat_the_chinh, R.string.message_default_card_pvcombank, false, object : ConfirmDialogListener {
                override fun onDisagree() {
                    DialogHelper.closeLoading(this@ListPVCardActivity)
                }

                override fun onAgree() {
                    lifecycleScope.launch {
                        DialogHelper.closeLoading(this@ListPVCardActivity)
                        viewModel.setDefaultCard(item.cardId)
                    }
                }
            })
        }
    }

    /*
    * Dialog lock card
    */
    override fun onClickLockCard(item: ICListCardPVBank, position: Int) {
        if (!item.cardId.isNullOrEmpty()) {
            viewModel.cardId = item.cardId!!
            viewModel.pos = position

            DialogHelper.showDialogLockCardBank(this, R.string.ban_muon_khoa_the_nay, R.string.message_lock_card_pvcombank, false, object : ConfirmDialogListener {
                override fun onDisagree() {
                    DialogHelper.closeLoading(this@ListPVCardActivity)
                }

                override fun onAgree() {
                    lifecycleScope.launch {
                        DialogHelper.closeLoading(this@ListPVCardActivity)
                        viewModel.lockCard(item.cardId)
//                        val intent = Intent(this@ListPVCardActivity, ConfirmLockPVCardActivity::class.java)
//                        intent.putExtra(Constant.DATA_1, item)
                        //someActivityResultLauncher.launch(intent)
//                        startActivityForResult(intent, requestLockCard)
                    }
                }
            })
        }
    }

    /*
    * Dialog unlock card
    */
    override fun onClickUnLockCard(item: ICListCardPVBank, position: Int) {
        if (!item.cardId.isNullOrEmpty()) {
            viewModel.cardId = item.cardId!!
            viewModel.pos = position
            DialogHelper.showDialogLockCardBank(this, R.string.ban_muon_mo_khoa_the_nay, R.string.message_unlock_card_pvcombank, false, object : ConfirmDialogListener {
                override fun onDisagree() {
                    DialogHelper.closeLoading(this@ListPVCardActivity)
                }

                override fun onAgree() {
                    lifecycleScope.launch {
                        DialogHelper.closeLoading(this@ListPVCardActivity)
                        val intent = Intent(this@ListPVCardActivity, ConfirmUnlockPVCardActivity::class.java)
                        intent.putExtra(Constant.DATA_1, item)
                        startActivityForResult(intent, requestUnLockCard)
                    }
                }
            })
        }
    }

    /*
    * Check show/hide thông tin card
    */
    override fun onClickShowOrHide(item: ICListCardPVBank, position: Int) {
        if (!item.isShow) {
            getInfoCard(item, position)
        } else {
            //Ẩn thông tin
            item.cardMasking?.let { adapter.showHide(item.cardId, false, position, it) }
        }
    }


    override fun onClickShow(item: ICListCardPVBank, position: Int) {
        getInfoCard(item, position)
    }

    private fun getInfoCard(item: ICListCardPVBank, position: Int) {
        //Request để lấy full thông tin
        viewModel.cardId = item.cardId!!
        viewModel.pos = position

        val intent = Intent(this@ListPVCardActivity, ConfirmUnlockPVCardActivity::class.java)
        intent.putExtra(Constant.DATA_1, item)
        intent.putExtra(Constant.DATA_2, "full_card")
        ActivityHelper.startActivityForResult(this@ListPVCardActivity, intent, requestFullCard)
    }

    override fun onAuthenCard(item: ICListCardPVBank) {
        viewModel.getKyc().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    DialogHelper.showLoading(this@ListPVCardActivity)
                }
                Status.ERROR_NETWORK -> {
                    DialogHelper.closeLoading(this@ListPVCardActivity)
                    showLongError(ICheckApplication.getError(it.message))
                }
                Status.ERROR_REQUEST -> {
                    DialogHelper.closeLoading(this@ListPVCardActivity)
                    showLongError(ICheckApplication.getError(it.message))
                }
                Status.SUCCESS -> {
                    DialogHelper.closeLoading(this@ListPVCardActivity)
                    if (!it.data?.data?.kycUrl.isNullOrEmpty()) {
                        HomePVCardActivity.redirectUrl = it.data?.data?.redirectUrl
                        WebViewActivity.start(this, it.data?.data?.kycUrl)
                    } else {
                        showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            }
        })
    }

    override fun onClickChangePassword(item: ICListCardPVBank, position: Int) {
        showShortSuccess(getString(R.string.tinh_nang_dang_phat_trien))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
//                requestLockCard -> {
//                    val id = data?.getStringExtra(Constant.DATA_1)
//                    if (id != null) {
//                        showSimpleSuccessToast("Thẻ đã khóa thành công")
//                        adapter.setLockCard(id, true)
//                    }
//                }

                requestUnLockCard -> {
                    val id = data?.getStringExtra(Constant.DATA_1)
                    if (id != null) {
                        showSimpleSuccessToast("Thẻ đã mở khóa thành công")
                        adapter.setLockCard(id, viewModel.pos, false)
                    }
                }

                requestFullCard -> {
                    val cardFull = data?.getSerializableExtra(Constant.DATA_1) as ICLockCard
                    val id = data.getStringExtra(Constant.DATA_2)
                    if (id != null) {
                        if (!cardFull.fullCard.isNullOrEmpty()) {
                            adapter.showHide(id, true, viewModel.pos, cardFull.fullCard!!)
                        }
                    }
                }
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        if (intent?.getIntExtra(Constant.DATA_1, 0) == 1) {
            startActivityAndFinish<HomePVCardActivity, Int>(Constant.DATA_1, 1)
        }
        super.onBackPressed()
    }
}