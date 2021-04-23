package vn.icheck.android.screen.user.search_home.product

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_product.*
import kotlinx.android.synthetic.main.layout_edittext_search_screen.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper.setTextChooseSearch
import vn.icheck.android.helper.TextHelper.setTextDataSearch
import vn.icheck.android.helper.TextHelper.setTextEmpitySearch
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.Status
import vn.icheck.android.screen.user.search_home.dialog.FilterPriceDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterProductDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterRateDialog
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.visibleOrGone
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit

class SearchProductActivity : BaseActivityMVVM(), View.OnClickListener, IRecyclerViewSearchCallback {

    private var dispose: Disposable? = null
    private val viewModel: SearchProductViewModel by viewModels()
    lateinit var adapter: SearchProductAdapter
    private var offset = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        initView()
        initSwipeLayout()
        initRecyclerView()
        refreshData()
    }

    private fun initView() {
        if (intent.getStringExtra(Constant.DATA_1) != null) {
            edtSearch.setText(intent.getStringExtra(Constant.DATA_1))
            edtSearch.setSelection(intent.getStringExtra(Constant.DATA_1).toString().length)
        } else {
            edtSearch.setText("")
        }

        dispose = RxTextView.textChangeEvents(edtSearch)
                .skipInitialValue()
                .distinctUntilChanged()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    refreshData()
                }

        edtSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (edtSearch.text.toString().isNotBlank()) {
                    KeyboardUtils.hideSoftInput(edtSearch)
                }
                true
            } else {
                false
            }
        }

        WidgetUtils.setClickListener(this, btn_product, btn_price, btn_verify, btn_review, imgClear, imgBack)
    }

    private fun initSwipeLayout() {
        val swipeColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipe_container.setColorSchemeColors(swipeColor, swipeColor, swipeColor)
        swipe_container.setOnRefreshListener {
            refreshData()
        }
    }

    private fun initRecyclerView() {
        adapter = SearchProductAdapter(this)
        val layoutManager = GridLayoutManager(this, 2)

        rcv_product_collection.adapter = adapter
        rcv_product_collection.layoutManager = layoutManager

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position != adapter.getListData.size) {
                    1
                } else {
                    2
                }
            }
        }
    }

    private fun getData() {
        viewModel.getData(edtSearch.text.toString(), offset).observe(this, {
            swipe_container.isRefreshing = false
            when (it.status) {
                Status.ERROR_NETWORK, Status.ERROR_REQUEST -> {
                    if (adapter.isEmpty) {
                        val icon = if (it.status == Status.ERROR_NETWORK) {
                            R.drawable.ic_error_network
                        } else {
                            R.drawable.ic_error_request
                        }
                        adapter.setError(icon, it.message
                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai), R.string.thu_lai)
                    } else {
                        ToastUtils.showShortError(this, it.message)
                        adapter.disableLoading()
                    }
                }
                Status.SUCCESS -> {
                    if (offset == 0) {
                        adapter.setListData(it.data?.data?.rows ?: mutableListOf())
                    } else {
                        adapter.addListData(it.data?.data?.rows ?: mutableListOf())
                    }
                    offset += APIConstants.LIMIT
                }
            }
        })
    }

    private fun refreshData() {
        imgClear.visibleOrGone(edtSearch.text.toString().isNotEmpty())
        if (::adapter.isInitialized)
            adapter.disableLoading()
        swipe_container.isRefreshing = true
        offset = 0
        getData()
    }

    fun setFilterPrice() {
        if (viewModel.getPrice.isNullOrEmpty() || viewModel.getPrice == getString(R.string.tat_ca)) {
            btn_price.setTextEmpitySearch(R.string.gia_tu)
        } else {
            btn_price.setTextDataSearch(viewModel.getPrice!!)
        }
    }

    fun setFilterReview() {
        if (viewModel.getReviews.isNullOrEmpty()) {
            btn_review.setTextEmpitySearch(R.string.danh_gia)
        } else {
            val listRate = viewModel.getReviews.filterIndexed { index, s ->
                index < 3
            }
            var rates = listRate.toString().substring(1, listRate.toString().length - 1)
            if (viewModel.getReviews.size > 3) {
                rates = "$rates,..."
            }
            btn_review.setTextDataSearch(rates)
        }
    }

    fun setFilterVerified() {
        btn_verify.setTextChooseSearch(viewModel.getIsVerify)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.btn_product -> {
                p0.onDelayClick({
                    FilterProductDialog(object : FilterProductDialog.FilterProductCallback {
                        override fun onSelected(verify: Boolean, price: String?, reviews: MutableList<String>?) {

                            viewModel.setIsVerify(verify)
                            viewModel.setPrice(price)
                            viewModel.setReviews(reviews)

                            setFilterPrice()
                            setFilterReview()
                            setFilterVerified()
                            edtSearch.text = edtSearch.text
                        }
                    }, viewModel.getIsVerify, viewModel.getPrice, viewModel.getReviews).show(supportFragmentManager, null)
                })
            }
            R.id.btn_review -> {
                p0.onDelayClick({
                    val filterRateDialog = FilterRateDialog(viewModel.getReviews, object : FilterRateDialog.FilterRateCallback {
                        override fun setRate(listSelected: MutableList<String>) {
                            viewModel.setReviews(listSelected)
                            setFilterReview()
                            edtSearch.text = edtSearch.text
                        }
                    })
                    filterRateDialog.show(supportFragmentManager, null)
                })
            }
            R.id.btn_verify -> {
                viewModel.setIsVerify(!viewModel.getIsVerify)
                setFilterVerified()
                edtSearch.text = edtSearch.text
            }
            R.id.btn_price -> {
                p0.onDelayClick({
                    val filterPriceDialog = FilterPriceDialog(viewModel.getPrice, object : FilterPriceDialog.FilterPriceCallback {
                        override fun selectFilter(price: String?) {
                            viewModel.setPrice(price)
                            setFilterPrice()
                            edtSearch.text = edtSearch.text
                        }
                    })
                    filterPriceDialog.show(supportFragmentManager, null)
                })
            }
            R.id.imgClear -> {
                edtSearch.setText("")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        dispose?.dispose()
    }

    override fun onMessageClicked() {
        refreshData()
    }

    override fun onLoadMore() {
        getData()
    }

    override fun onNotResultClicked() {
        edtSearch.requestFocus()
        KeyboardUtils.showSoftInput(edtSearch)
    }
}