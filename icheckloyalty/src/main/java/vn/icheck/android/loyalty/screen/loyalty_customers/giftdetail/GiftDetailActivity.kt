package vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_gift_detail.*
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.helper.StatusBarHelper
import vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard.ChangePhoneCardsActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard.ExchangePhonecardSuccessDialog

/**
 * putExtra(Constant.DATA_2, 1) nếu muốn vào màn chi tiết quà của shop
 */
class GiftDetailActivity : BaseActivityGame() {
    private val requestCard = 111


    companion object {
        fun startActivityGiftDetail(context: Context, idGift: Long, type: Int? = null) {
            val intent = Intent(context, GiftDetailActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, idGift)
            if (type != null) {
                intent.putExtra(ConstantsLoyalty.DATA_2, type)
            }
            context.startActivity(intent)
        }

        fun startActivityGiftDetail(activity: FragmentActivity, idGift: Long, type: Int? = null, requestCode: Int) {
            val intent = Intent(activity, GiftDetailActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, idGift)
            if (type != null) {
                intent.putExtra(ConstantsLoyalty.DATA_2, type)
            }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private val viewModel by viewModels<GiftDetailViewModel>()

    private var adapter: GiftDetailAdapter? = null

    private var type = 0

    override val getLayoutID: Int
        get() = R.layout.activity_gift_detail

    override fun onInitView() {
        StatusBarHelper.setOverStatusBarDark(this)

        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)
        type = intent.getIntExtra(ConstantsLoyalty.DATA_2, 0)

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        var backgroundHeight = 0

        adapter = GiftDetailAdapter(type)

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

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true

        if (type == 0) {
            viewModel.getDetailGift()
        } else {
            viewModel.getDetailGiftStore()
        }
    }

    fun initListener() {
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (adapter?.isEmpty == true) {
                adapter?.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.setData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            txtTitle.text = it?.gift?.name ?: rText(R.string.qua_tang_doi_thuong)

            adapter?.setData(it)
        })
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.ON_COUNT_GIFT -> {
                if (type == 0) {
                    viewModel.getDetailGift()
                } else {
                    viewModel.getDetailGiftStore()
                }
                setResult(RESULT_OK)
            }
            ICMessageEvent.Type.EXCHANGE_PHONE_CARD -> {
                ChangePhoneCardsActivity.start(this, event.data as Long, ConstantsLoyalty.TDDH, requestCard)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCard) {
            if (resultCode == RESULT_OK) {
                getData()
                val phone = data?.getStringExtra("phone")
                val provider = data?.getStringExtra("provider")
                val dialog = ExchangePhonecardSuccessDialog(phone, provider)

                dialog.show(supportFragmentManager, null)
                setResult(RESULT_OK)
            }
        }
    }
}