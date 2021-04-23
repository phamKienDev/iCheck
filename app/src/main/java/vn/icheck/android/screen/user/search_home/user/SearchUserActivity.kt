package vn.icheck.android.screen.user.search_home.user

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_users.*
import kotlinx.android.synthetic.main.layout_edittext_search_screen.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper.setTextDataSearch
import vn.icheck.android.helper.TextHelper.setTextEmpitySearch
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.search_home.dialog.FilterGenderDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterLocationVnDialog
import vn.icheck.android.screen.user.search_home.dialog.FilterUserDialog
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.wall.USER_ID
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.visibleOrGone
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit

class SearchUserActivity : BaseActivityMVVM(), IRecyclerViewSearchCallback, View.OnClickListener {
    private val viewModel: SearchUserViewModel by viewModels()
    lateinit var adapter: SearchUserAdapter
    private var dispose: Disposable? = null

    private var offset = 0
    private var isActivityVisible = true
    private var requestLogin = 1
    private var requestUser = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

        setupView()
        setupRecyclerView()
        setupSwipeLayout()
        refreshData()
    }

    private fun setupView() {
        if (intent.getStringExtra(Constant.DATA_1) != null) {
            edtSearch.setText(intent.getStringExtra(Constant.DATA_1))
            edtSearch.setSelection(intent.getStringExtra(Constant.DATA_1).toString().length)
        } else {
            edtSearch.setText("")
        }

        dispose = RxTextView.afterTextChangeEvents(edtSearch)
                .skipInitialValue()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    refreshData()
                }

        WidgetUtils.setClickListener(this, btn_filer, btn_filer_location, btn_filter_gender, imgBack, imgClear)
    }

    private fun setupRecyclerView() {
        adapter = SearchUserAdapter(2, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSwipeLayout() {
        val swipeColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)
        swipeLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun getData() {
        viewModel.getData(edtSearch.text.toString(), offset).observe(this, {
            swipeLayout.isRefreshing = false
            when (it.status) {
                Status.SUCCESS -> {
                    if (offset == 0) {
                        adapter.setListData(it.data?.data?.rows ?: mutableListOf())
                        recyclerView.smoothScrollToPosition(0)
                    } else {
                        adapter.addListData(it.data?.data?.rows ?: mutableListOf())
                    }
                    offset += APIConstants.LIMIT
                }
                Status.ERROR_REQUEST, Status.ERROR_NETWORK -> {
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


    private fun refreshData() {
        imgClear.visibleOrGone(edtSearch.text.toString().isNotEmpty())
        if (::adapter.isInitialized)
            adapter.disableLoading()
        swipeLayout.isRefreshing = true
        offset = 0
        getData()
    }

    private fun selectCity(obj: MutableList<ICProvince>?) {
        viewModel.setCity(obj)
        if (!obj.isNullOrEmpty() && obj[0].id != -1L) {
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

        } else {
            btn_filer_location.setTextEmpitySearch(R.string.tinh_thanh_pho)
        }
    }

    private fun selectGender(gender: MutableList<String>?) {
        if (gender.isNullOrEmpty() || gender.size == 3) {
            btn_filter_gender.setTextEmpitySearch(R.string.gioi_tinh)
        } else {
            btn_filter_gender.setTextDataSearch(gender.toString().substring(1, gender.toString().length - 1))
        }
        viewModel.setGender(gender)
    }


    override fun onLoadMore() {
        getData()
    }

    override fun onNotResultClicked() {
        edtSearch.requestFocus()
        KeyboardUtils.showSoftInput(edtSearch)
    }

    override fun onMessageClicked() {
        refreshData()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.btn_filer -> {
                p0.onDelayClick({
                    object : FilterUserDialog(this@SearchUserActivity, viewModel.getListCity, viewModel.getGender) {
                        override fun onSelected(city: MutableList<ICProvince>?, gender: MutableList<String>?) {
                            selectCity(city)
                            selectGender(gender)
                            refreshData()
                        }
                    }.show()
                })
            }
            R.id.btn_filer_location -> {
                p0.onDelayClick({
                    FilterLocationVnDialog(object : FilterLocationVnDialog.LocationCallback {
                        override fun getLocation(obj: MutableList<ICProvince>?) {
                            selectCity(obj)
                            refreshData()
                        }
                    }, viewModel.getListCity).show(supportFragmentManager, null)
                })
            }
            R.id.btn_filter_gender -> {
                p0.onDelayClick({
                    FilterGenderDialog(viewModel.getGender, object : FilterGenderDialog.GenderCallback {
                        override fun getGender(gender: MutableList<String>?) {
                            selectGender(gender)
                            refreshData()
                        }
                    }).show(supportFragmentManager, null)
                })
            }
            R.id.imgClear -> {
                edtSearch.setText("")
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (isActivityVisible) {
            when (event.type) {
                ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                    onRequireLogin(requestLogin)
                }
                ICMessageEvent.Type.OPEN_DETAIL_USER -> {
                    val user = event.data as ICSearchUser
                    startActivityForResult<IckUserWallActivity, Long>(USER_ID, user.id, requestUser)
                }
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requestLogin) {
            refreshData()
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