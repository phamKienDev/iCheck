package vn.icheck.android.screen.user.search_home.page

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_page.*
import kotlinx.android.synthetic.main.layout_edittext_search_screen.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper.setTextChooseSearch
import vn.icheck.android.helper.TextHelper.setTextDataSearch
import vn.icheck.android.helper.TextHelper.setTextEmpitySearch
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.ICCategorySearch
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.search_home.dialog.FilterCategoryDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterLocationVnDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterPageDialog
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.visibleOrGone
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit

class SearchPageActivity : BaseActivityMVVM(), View.OnClickListener, IRecyclerViewSearchCallback {
    lateinit var adapter: SearchPageAdapter
    private val viewModel: SearchPageViewModel by viewModels()
    private var dispose: Disposable? = null

    private var isActivityVisible = true
    private var requestLogin = 1
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_page)

        initView()
        initSwipeLayout()
        initRecyclerView()
        refeshData()
    }

    private fun initView() {
        if (intent.getStringExtra(Constant.DATA_1) != null) {
            edtSearch.setText(intent.getStringExtra(Constant.DATA_1))
            edtSearch.setSelection(intent.getStringExtra(Constant.DATA_1).toString().length)
        } else {
            edtSearch.setText("")
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
                .subscribe { key ->
                    refeshData()
                }

        WidgetUtils.setClickListener(this, btn_filer, btn_filer_location, btn_filter_verified, btn_filer_categories, imgBack, imgClear)
    }


    private fun initSwipeLayout() {
        swipe_container.isRefreshing = true
        val swipeColor = if (vn.icheck.android.ichecklibs.Constant.primaryColor.isNotEmpty()) {
            Color.parseColor(vn.icheck.android.ichecklibs.Constant.primaryColor)
        } else {
            ContextCompat.getColor(this, vn.icheck.android.ichecklibs.R.color.colorPrimary)
        }
        swipe_container.setColorSchemeColors(swipeColor, swipeColor, swipeColor)
        swipe_container.setOnRefreshListener {
            refeshData()
        }
    }

    private fun initRecyclerView() {
        adapter = SearchPageAdapter(this)
        rcv_search_pages.adapter = adapter
        rcv_search_pages.layoutManager = LinearLayoutManager(this)
    }

    private fun getData() {
        viewModel.getData(edtSearch.text.toString(), offset).observe(this, {
            swipe_container.isRefreshing = false
            when (it.status) {
                Status.SUCCESS -> {
                    if (offset == 0) {
                        adapter.setListData(it.data?.data?.rows ?: mutableListOf())
                    } else {
                        adapter.addListData(it.data?.data?.rows ?: mutableListOf())
                    }
                    offset += APIConstants.LIMIT
                }
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
            }
        })
    }

    private fun refeshData() {
        imgClear.visibleOrGone(edtSearch.text.toString().isNotEmpty())
        swipe_container.isRefreshing = true
        if (::adapter.isInitialized) {
            adapter.disableLoading()
        }
        offset = 0
        getData()
    }

    fun setFilterLocation() {
        if (viewModel.getListCity.isNullOrEmpty() || viewModel.getListCity[0].name == getString(R.string.tat_ca)) {
            btn_filer_location.setTextEmpitySearch(R.string.tinh_thanh_pho)
        } else {
            val listString = mutableListOf<String>()
            viewModel.getListCity.filterIndexed { index, icProvince ->
                index < 3
            }.forEach {
                listString.add(it.name)
            }
            var city = listString.toString().substring(1, listString.toString().length - 1)
            if (viewModel.getListCity.size > 3) {
                city = "$city,..."
            }
            btn_filer_location.setTextDataSearch(city)
        }
    }

    fun setFilterVerified() {
        btn_filter_verified.setTextChooseSearch(viewModel.getIsVerified)
    }

    fun setFilterCategory() {
        if (viewModel.getListCategory.isNullOrEmpty()) {
            btn_filer_categories.setTextEmpitySearch(R.string.danh_muc)
        } else {
            btn_filer_categories.setTextDataSearch(viewModel.getListCategory.last().name.toString())
        }
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
                p0.onDelayClick({
                    val dialog = FilterPageDialog(viewModel.getListCity, viewModel.getIsVerified, viewModel.getListCategory, object : FilterPageDialog.FilterPageCallback {
                        override fun filterPage(location: MutableList<ICProvince>?, verified: Boolean, category: MutableList<ICCategorySearch>?) {
                            viewModel.setCity(location)
                            viewModel.setIsVerified(verified)
                            viewModel.setListCategory(category)

                            setFilterLocation()
                            setFilterVerified()
                            setFilterCategory()
                            edtSearch.text = edtSearch.text
                        }
                    })
                    dialog.show(supportFragmentManager, null)
                })
            }
            R.id.btn_filer_location -> {
                p0.onDelayClick({
                    FilterLocationVnDialog(object : FilterLocationVnDialog.LocationCallback {
                        override fun getLocation(obj: MutableList<ICProvince>?) {
                            viewModel.setCity(obj)
                            setFilterLocation()
                            edtSearch.text = edtSearch.text
                        }

                    }, viewModel.getListCity).show(supportFragmentManager, null)
                })
            }
            R.id.btn_filter_verified -> {
                viewModel.setIsVerified(!viewModel.getIsVerified)
                setFilterVerified()
                edtSearch.text = edtSearch.text
            }
            R.id.btn_filer_categories -> {
                p0.onDelayClick({
                    FilterCategoryDialog(viewModel.getListCategory, object : FilterCategoryDialog.CategoryCallback {
                        override fun getSelected(obj: MutableList<ICCategorySearch>?) {
                            viewModel.setListCategory(obj)
                            setFilterCategory()
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
        refeshData()
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (isActivityVisible) {
            when (event.type) {
                ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                    onRequireLogin(requestLogin)
                }
                ICMessageEvent.Type.OPEN_DETAIL_PAGE -> {
                    val id = event.data as Long
                    ActivityUtils.startActivity<PageDetailActivity, Long>(this, Constant.DATA_1, id)
                }
            }
        }
    }


    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requestLogin) {
            refeshData()
        }
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
