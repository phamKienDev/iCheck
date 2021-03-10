package vn.icheck.android.screen.user.search_home.result

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.layout_edittext_search_screen.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper.setScrollSpeed
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.search_home.page.SearchPageActivity
import vn.icheck.android.screen.user.search_home.product.SearchProductActivity
import vn.icheck.android.screen.user.search_home.result.holder.SetTypeSearchHolder
import vn.icheck.android.screen.user.search_home.result.model.ICSearchResult
import vn.icheck.android.screen.user.search_home.review.SearchReviewActivity
import vn.icheck.android.screen.user.search_home.shop.SearchShopActivity
import vn.icheck.android.screen.user.search_home.user.SearchUserActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.wall.USER_ID
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.visibleOrGone
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit

class SearchResultActivity : BaseActivityMVVM(), View.OnClickListener {
    private var dispose: Disposable? = null
    lateinit var viewModel: SearchResultViewModel
    lateinit var adapter: SearchResultAdapter

    private var errorCount = 0
    private var empityCount = 0
    private var notEmpityCount = 0

    private var isActivityVisible = true
    private var requestLogin = 1
    private var requestReviewDetail = 2
    private var requestUser = 3
    private var requestPageOrShop = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SearchResultViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        DialogHelper.showLoading(this)
        initView()
        initSwipelayout()
        initRecyclerView()
        getData()
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
                    getData()
                }

        WidgetUtils.setClickListener(this, btn_review, btn_product, btn_page, btn_shop, btn_user, imgBack, imgClear)
    }

    private fun initSwipelayout() {
        swipe_container.setOnRefreshListener {
            getData()
        }
        swipe_container.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))
    }

    private fun initRecyclerView() {
        adapter = SearchResultAdapter()
        rcv_search_result.adapter = adapter
        rcv_search_result.setScrollSpeed(7000)
        rcv_search_result.layoutManager = WrapContentLinearLayoutManager(this)
    }

    private fun getData() {
        if (refreshData()) return

        viewModel.getProduct(edtSearch.text.toString()).observe(this, {
            if (it.status == Status.SUCCESS) {
                setData(Constant.PRODUCT_TYPE, it.data?.data?.rows)
                checkTotalEmpity(it.data?.data?.rows)
            } else if (it.status == Status.ERROR_REQUEST) {
                adapter.updateData(ICSearchResult(Constant.PRODUCT_TYPE, null))
                checkTotalError()
            }
        })

        viewModel.getReview(edtSearch.text.toString()).observe(this, {
            if (it.status == Status.SUCCESS) {
                setData(Constant.REVIEW_TYPE, it.data?.data?.rows)
                setData(Constant.SEARCH_MORE, it.data?.data?.rows)
                checkTotalEmpity(it.data?.data?.rows)
            } else if (it.status == Status.ERROR_REQUEST) {
                adapter.updateData(ICSearchResult(Constant.REVIEW_TYPE, null))
                checkTotalError()
            }
        })

        viewModel.getUser(edtSearch.text.toString()).observe(this, {
            if (it.status == Status.SUCCESS) {
                setData(Constant.USER_TYPE, it.data?.data?.rows)
                checkTotalEmpity(it.data?.data?.rows)
            } else if (it.status == Status.ERROR_REQUEST) {
                adapter.updateData(ICSearchResult(Constant.USER_TYPE, null))
                checkTotalError()
            }
        })
        viewModel.getPage(edtSearch.text.toString()).observe(this, {
            if (it.status == Status.SUCCESS) {
                setData(Constant.PAGE_TYPE, it.data?.data?.rows)
                setData(Constant.SEARCH_MORE, it.data?.data?.rows)
                checkTotalEmpity(it.data?.data?.rows)
            } else if (it.status == Status.ERROR_REQUEST) {
                adapter.updateData(ICSearchResult(Constant.PAGE_TYPE, null))
                checkTotalError()
            }
        })
    }

    private fun refreshData(): Boolean {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            setError(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return true
        }

        imgClear.visibleOrGone(edtSearch.text.toString().isNotEmpty())
        swipe_container.isRefreshing = true

        errorCount = 0
        empityCount = 0
        notEmpityCount = 0

        adapter.resetData()

        val listLayout = mutableListOf<ICSearchResult>()
        listLayout.add(ICSearchResult(Constant.PRODUCT_TYPE, null))
        listLayout.add(ICSearchResult(Constant.REVIEW_TYPE, null))
        listLayout.add(ICSearchResult(Constant.SEARCH_MORE, null))
        listLayout.add(ICSearchResult(Constant.USER_TYPE, null))
        listLayout.add(ICSearchResult(Constant.PAGE_TYPE, null))
        listLayout.add(ICSearchResult(Constant.SEARCH_MORE, null))

        adapter.addData(listLayout)
        return false
    }

    private fun <T> setData(type: Int, list: MutableList<T>?) {
        DialogHelper.closeLoading(this)
        swipe_container.isRefreshing = false
        if (list.isNullOrEmpty()) {
            adapter.updateData(ICSearchResult(type, null))
        } else {
            adapter.updateData(ICSearchResult(type, list))
        }
    }

    private fun setError(it: ICError) {
        DialogHelper.closeLoading(this)
        swipe_container.isRefreshing = false
        if (adapter.isEmpity) {
            adapter.setError(it)
        } else {
            ToastUtils.showShortError(this, it.message)
        }
    }

    private fun checkTotalError() {
        DialogHelper.closeLoading(this)
        swipe_container.isRefreshing = false
        errorCount++
        if (errorCount == 4) {
            setError(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
        }
    }

    private fun <T> checkTotalEmpity(list: MutableList<T>?) {
        if (list.isNullOrEmpty()) {
            empityCount++
        } else {
            notEmpityCount++
        }

        if (empityCount.plus(notEmpityCount) == 4) {
            if (empityCount == 4) {
                layoutButton.beGone()
            } else {
                layoutButton.beVisible()
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (isActivityVisible) {
            when (event.type) {
                ICMessageEvent.Type.OPEN_SEARCH_PRODUCT -> {
                    ActivityUtils.startActivity<SearchProductActivity>(this, Constant.DATA_1, edtSearch.text.toString())
                }
                ICMessageEvent.Type.OPEN_SEARCH_USER -> {
                    ActivityUtils.startActivity<SearchUserActivity>(this, Constant.DATA_1, edtSearch.text.toString())
                }
                ICMessageEvent.Type.MESSAGE_ERROR -> {
                    getData()
                }
                ICMessageEvent.Type.REFRESH_DATA -> {
                    edtSearch.requestFocus()
                    KeyboardUtils.showSoftInput(edtSearch)
                }
                ICMessageEvent.Type.ON_LOG_IN -> {
                    onRequireLogin(requestLogin)
                }
                ICMessageEvent.Type.OPEN_DETAIL_POST -> {
                    if (isActivityVisible) {
                        if (event.data is Long) {
                            DetailPostActivity.start(this, event.data, false, requestReviewDetail)
                        } else {
                            DetailPostActivity.start(this, (event.data as ICPost).id, true, requestReviewDetail)
                        }
                    }
                }
                ICMessageEvent.Type.OPEN_MEDIA_IN_POST -> {
                    if (event.data != null && event.data is ICPost) {
                        val intent = Intent(this, MediaInPostActivity::class.java)
                        intent.putExtra(Constant.DATA_1, event.data)
                        startActivityForResult(intent, requestReviewDetail)
                    }
                }
                ICMessageEvent.Type.OPEN_DETAIL_USER -> {
                    val user = event.data as ICSearchUser
                    ActivityUtils.startActivityForResult<IckUserWallActivity, Long>(this, USER_ID, user.id, requestUser)
                }
                ICMessageEvent.Type.OPEN_DETAIL_PAGE -> {
                    val id = event.data as Long
                    ActivityUtils.startActivityForResult<PageDetailActivity, Long>(this, Constant.DATA_1, id, requestPageOrShop)
                }
                else -> {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestReviewDetail -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post != null && post is ICPost) {
                        updateItemReview(post)
                    }
                }
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requestLogin) {
            getData()
        }
    }

    private fun updateItemReview(post: ICPost) {
        val reviewIndex = adapter.listData.indexOfFirst { it.type == Constant.REVIEW_TYPE }
        if (reviewIndex != -1) {
            val itemIndex = (adapter.listData[reviewIndex].data as MutableList<ICPost>).indexOfFirst { it.id == post.id }
            if (itemIndex != -1) {
                (adapter.listData[reviewIndex].data as MutableList<ICPost>)[itemIndex] = post
                val holder = rcv_search_result.findViewHolderForAdapterPosition(reviewIndex)
                if (holder is SetTypeSearchHolder) {
                    holder.updateReview()
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.btn_product -> {
                ActivityUtils.startActivity<SearchProductActivity>(this, Constant.DATA_1, edtSearch.text.toString())
            }
            R.id.btn_review -> {
                ActivityUtils.startActivity<SearchReviewActivity>(this, Constant.DATA_1, edtSearch.text.toString())
            }
            R.id.btn_user -> {
                ActivityUtils.startActivity<SearchUserActivity>(this, Constant.DATA_1, edtSearch.text.toString())
            }
            R.id.btn_page -> {
                ActivityUtils.startActivity<SearchPageActivity>(this, Constant.DATA_1, edtSearch.text.toString())
            }
            R.id.btn_shop -> {
                ActivityUtils.startActivity<SearchShopActivity>(this, Constant.DATA_1, edtSearch.text.toString())
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

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }
}
