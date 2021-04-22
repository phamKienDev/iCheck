package vn.icheck.android.screen.user.search_home.shop

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_shop.*
import kotlinx.android.synthetic.main.layout_edittext_search_screen.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextColorHelper
import vn.icheck.android.helper.TextHelper.setTextDataSearch
import vn.icheck.android.helper.TextHelper.setTextEmpitySearch
import vn.icheck.android.network.models.ICCategorySearch
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.screen.user.search_home.dialog.FilterCategoryDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterLocationVnDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterPageDialog
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit

class SearchShopActivity : BaseActivityMVVM(), View.OnClickListener, IRecyclerViewSearchCallback {
    lateinit var adapter: SearchShopAdapter
    lateinit var viewModel: SearchShopViewModel
    private var dispose: Disposable? = null

    private var isActivityVisible = true
    private var requestLogin = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SearchShopViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_shop)

        initView()
        initSwipeLayout()
        initRecyclerView()
        listenerData()
    }


    private fun initView() {
        if (intent.getStringExtra(Constant.DATA_1) != null) {
            edtSearch.setText(intent.getStringExtra(Constant.DATA_1))
            edtSearch.setSelection(intent.getStringExtra(Constant.DATA_1).toString().length)
        } else {
            edtSearch.setText("")
        }
        getData()

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
                    getData()
                }

        WidgetUtils.setClickListener(this, btn_filer, btn_filer_location, btn_filter_verified, btn_filer_categories, imgBack, imgClear)
    }

    private fun initSwipeLayout() {
        swipe_container.isRefreshing = true
        swipe_container.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorPrimary))
        swipe_container.setOnRefreshListener {
            getData()
        }
    }

    private fun getData() {
        if (edtSearch.text.toString().isEmpty()) {
            imgClear.beGone()
        } else {
            imgClear.beVisible()
        }

        swipe_container.isRefreshing = true
        if (::adapter.isInitialized)
            adapter.disableLoading()
        viewModel.getData(edtSearch.text.toString())
    }

    private fun initRecyclerView() {
        adapter = SearchShopAdapter(this)
        rcv_search_shop.adapter = adapter
        rcv_search_shop.layoutManager = LinearLayoutManager(this)
    }

    private fun listenerData() {
        viewModel.onSetData.observe(this, Observer {
            swipe_container.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            swipe_container.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onError.observe(this, Observer {
            swipe_container.isRefreshing = false
            if (adapter.isEmpty) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1, R.string.thu_lai) }
            } else {
                ToastUtils.showShortError(this, it.message)
            }
        })
    }


    fun setFilterLocation() {
        if (viewModel.getListCity.isNullOrEmpty() || viewModel.getListCity?.get(0)?.name == getString(R.string.tat_ca)) {
            btn_filer_location.setTextEmpitySearch(R.string.tat_ca)
        } else {
            var name = ""
            for (i in viewModel.getListCity!!.indices) {
                name += if (i < viewModel.getListCity!!.size - 1) {
                    "${viewModel.getListCity!![i].name}, "
                } else {
                    viewModel.getListCity!![i].name
                }
            }
            btn_filer_location.text = name
            btn_filer_location.setTextDataSearch(name)
        }
    }

    fun setFilterVerified(verified: Boolean) {
        if (verified) {
            btn_filter_verified.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_corners_4_light_blue_solid)
            btn_filter_verified.setTextColor(Color.WHITE)
        } else {
            btn_filter_verified.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_corner_gray_4)
            btn_filter_verified.setTextColor(TextColorHelper.getColorNormalText(this))
        }
    }


    fun setFilterCategory() {
        if (viewModel.getCategory.isNullOrEmpty()) {
            btn_filer_categories.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_corner_gray_4)
            btn_filer_categories.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_bottom_filter_8dp, 0)
            btn_filer_categories.setTextColor(TextColorHelper.getColorNormalText(this))
            btn_filer_categories.text = getString(R.string.danh_muc)
        } else {
            btn_filer_categories.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_corners_4_light_blue_solid)
            btn_filer_categories.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_filter_white_8dp, 0)
            btn_filer_categories.setTextColor(Color.WHITE)
            btn_filer_categories.text = viewModel.getCategory!!.last().name.toString()
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
                val dialog = FilterPageDialog(viewModel.getListCity, viewModel.getIsVerified, viewModel.getCategory, object : FilterPageDialog.FilterPageCallback {
                    override fun filterPage(location: MutableList<ICProvince>?, verified: Boolean, category: MutableList<ICCategorySearch>?) {

                        viewModel.setCity(location)
                        viewModel.setIsVerified(verified)
                        viewModel.setCategory(category)
                        edtSearch.text = edtSearch.text

                        setFilterLocation()
                        setFilterVerified(verified)
                        setFilterCategory()
                    }
                })
                dialog.show(supportFragmentManager, dialog.tag)
            }
            R.id.btn_filer_location -> {
                FilterLocationVnDialog(object : FilterLocationVnDialog.LocationCallback {
                    override fun getLocation(obj: MutableList<ICProvince>?) {
                        viewModel.setCity(obj)
                        edtSearch.text = edtSearch.text
                        setFilterLocation()
                    }

                }, viewModel.getListCity).show(supportFragmentManager, null)
            }
            R.id.btn_filter_verified -> {
                viewModel.setIsVerified(!viewModel.getIsVerified)
                edtSearch.text = edtSearch.text
                setFilterVerified(viewModel.getIsVerified)
            }
            R.id.btn_filer_categories -> {
                FilterCategoryDialog(viewModel.getCategory, object : FilterCategoryDialog.CategoryCallback {
                    override fun getSelected(obj: MutableList<ICCategorySearch>?) {
                        viewModel.setCategory(obj)
                        edtSearch.text = edtSearch.text
                        setFilterCategory()
                    }
                }).show(supportFragmentManager, null)
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (isActivityVisible) {
            when (event.type) {
                ICMessageEvent.Type.ON_LOG_IN -> {
                    onRequireLogin(requestLogin)
                }
                else -> {
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

    override fun onDestroy() {
        super.onDestroy()
        dispose?.dispose()
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getData(edtSearch.text.toString(), true)
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
