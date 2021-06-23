package vn.icheck.android.screen.user.search_home.review

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_review.*
import kotlinx.android.synthetic.main.layout_edittext_search_screen.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper.setTextChooseSearch
import vn.icheck.android.helper.TextHelper.setTextDataSearch
import vn.icheck.android.helper.TextHelper.setTextEmpitySearch
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.user.search_home.dialog.FilterReviewDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterReviewFromDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterYearDialog
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.visibleOrGone
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit

class SearchReviewActivity : BaseActivityMVVM(), View.OnClickListener, IRecyclerViewSearchCallback {
    lateinit var adapter: SearchReviewAdapter
    lateinit var viewModel: SearchReviewViewModel
    private var dispose: Disposable? = null

    private var isActivityVisible = true
    private var requestLoginV2 = 1
    private var requestReviewDetail = 2
    private var requestPostDetail = 3

    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SearchReviewViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_review)

        initView()
        initSwipeLayout()
        initRecyclerView()
        refreshData()
    }

    private fun initView() {
        if (intent.getStringExtra(Constant.DATA_1) == null) {
            edtSearch.setText("")
        } else {
            edtSearch.setText(intent.getStringExtra(Constant.DATA_1))
            edtSearch.setSelection(intent.getStringExtra(Constant.DATA_1).toString().length)
        }

        edtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardUtils.hideSoftInput(v)
                true
            } else {
                false
            }
        }

        dispose = RxTextView.textChangeEvents(edtSearch)
                .skipInitialValue()
                .distinctUntilChanged()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    refreshData()
                }

        edtSearch.background= ViewHelper.bgGrayCorners4(this)
        WidgetUtils.setClickListener(this, btn_filer, btn_filter_watched, btn_filer_time, btn_filer_from, imgBack, imgClear)
    }

    private fun initSwipeLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipe_container.isRefreshing = true
        swipe_container.setColorSchemeColors(primaryColor, primaryColor, primaryColor)
        swipe_container.setOnRefreshListener {
            refreshData()
        }
    }

    private fun initRecyclerView() {
        adapter = SearchReviewAdapter(this)
        rcv_search_reviews.adapter = adapter
        rcv_search_reviews.layoutManager = LinearLayoutManager(this)
    }

    private fun getData() {
        DialogHelper.showLoading(this)
        viewModel.getData(edtSearch.text.toString(), offset).observe(this, {
            DialogHelper.closeLoading(this)
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
                    if (offset > 0) {
                        adapter.addListData(it.data?.data?.rows ?: mutableListOf())
                    } else {
                        adapter.setListData(it.data?.data?.rows ?: mutableListOf())
                        rcv_search_reviews.smoothScrollToPosition(0)
                    }
                    offset += APIConstants.LIMIT
                }
            }

        })
    }

    private fun refreshData() {
        imgClear.visibleOrGone(edtSearch.text.toString().isNotEmpty())
        swipe_container.isRefreshing = true
        if (::adapter.isInitialized) {
            adapter.disableLoading()
        }
        offset = 0
        getData()
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (isActivityVisible) {
            when (event.type) {
                ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                    onRequireLogin(requestLoginV2)
                }
                ICMessageEvent.Type.OPEN_DETAIL_POST -> {
                    if (event.data != null && event.data is ICPost) {
                        DetailPostActivity.start(this, event.data, requestPostDetail)
                    }
                }
                ICMessageEvent.Type.OPEN_MEDIA_IN_POST -> {
                    if (event.data != null && event.data is ICPost) {
                        val intent = Intent(this, MediaInPostActivity::class.java)
                        intent.putExtra(Constant.DATA_1, event.data)
                        if(event.data.positionMedia != -1){
                            intent.putExtra(Constant.DATA_3, event.data.positionMedia)
                        }
                        startActivityForResult(intent, requestReviewDetail)
                    }
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
                        adapter.updateReview(post)
                    }
                }
                requestPostDetail -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post != null && post is ICPost) {
                        adapter.updateReview(post)
                    }
                }
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requestLoginV2) {
            refreshData()
        }
    }

    fun setFilterTime() {
        if (viewModel.getReviewYear.isNullOrEmpty()) {
            btn_filer_time.setTextEmpitySearch(R.string.year)
        } else {
            val listString = viewModel.getReviewYear.filterIndexed { index: Int, _: String -> index < 3 }
            var years = listString.toString().substring(1, listString.toString().length - 1)
            if (viewModel.getReviewYear.size > 3) {
                years = "$years,..."
            }

            btn_filer_time.setTextDataSearch(years)
        }
    }

    fun setFilterFrom() {
        if (viewModel.getReviewFrom.isNullOrEmpty()) {
            btn_filer_from.setTextEmpitySearch(R.string.nguoi_dang)
        } else {
            btn_filer_from.setTextDataSearch(viewModel.getReviewFrom.toString().substring(1, viewModel.getReviewFrom.toString().length - 1))
        }
    }

    fun setFilterWatched() {
        btn_filter_watched.setTextChooseSearch(viewModel.getIsWatched)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgClear -> {
                edtSearch.setText("")
            }
            R.id.btn_filer -> {
                KeyboardUtils.hideSoftInput(edtSearch)
                p0.onDelayClick({
                    val dialog = FilterReviewDialog(viewModel.getIsWatched, viewModel.getReviewYear, viewModel.getReviewFrom, object : FilterReviewDialog.ReviewCallback {
                        override fun filterReview(isWatched: Boolean, time: MutableList<String>?, from: MutableList<String>?) {
                            viewModel.setWatched(isWatched)
                            viewModel.setTime(time)
                            viewModel.setFrom(from)

                            setFilterWatched()
                            setFilterTime()
                            setFilterFrom()

                            edtSearch.text = edtSearch.text
                        }
                    })
                    dialog.show(supportFragmentManager, dialog.tag)
                })
            }
            R.id.btn_filter_watched -> {
                viewModel.setWatched(!viewModel.getIsWatched)
                setFilterWatched()
                edtSearch.text = edtSearch.text
            }
            R.id.btn_filer_time -> {
                KeyboardUtils.hideSoftInput(edtSearch)
                p0.onDelayClick({
                    FilterYearDialog(viewModel.getReviewYear, object : FilterYearDialog.TimeCallBack {
                        override fun getTime(obj: MutableList<String>?) {
                            viewModel.setTime(obj)
                            setFilterTime()
                            edtSearch.text = edtSearch.text
                        }
                    }).show(supportFragmentManager, null)
                })
            }
            R.id.btn_filer_from -> {
                KeyboardUtils.hideSoftInput(edtSearch)
                p0.onDelayClick({
                    FilterReviewFromDialog(viewModel.getReviewFrom, object : FilterReviewFromDialog.ReviewFromCallback {
                        override fun getFrom(from: MutableList<String>?) {
                            viewModel.setFrom(from)
                            setFilterFrom()
                            edtSearch.text = edtSearch.text
                        }
                    }).show(supportFragmentManager, null)
                })
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

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }
}
