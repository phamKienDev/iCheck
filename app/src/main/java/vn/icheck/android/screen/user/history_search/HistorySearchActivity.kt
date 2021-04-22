package vn.icheck.android.screen.user.history_search

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_history_search.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.forceShowKeyboard
import java.util.concurrent.TimeUnit

class HistorySearchActivity : BaseActivityMVVM(), HistoryScanSearchView {

    private var adapter = HistoryScanSearchAdapter(this)

    val viewModel: HistorySearchViewModel by viewModels()

    private var key: String? = null

    private val requestLoginCart = 1

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_search)
        initView()
        initSwipelayout()
        initRecyclerView()
        initViewModel()
        getData()
        initTextListener()
    }

    private fun initView() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgCart.setOnClickListener {
            if (SessionManager.isUserLogged) {
                startActivity<ShipActivity,Boolean>(Constant.CART,true)
            } else {
                onRequireLogin(requestLoginCart)
            }
        }
    }

    private fun initSwipelayout() {
        val swipeColor = if (vn.icheck.android.ichecklibs.Constant.primaryColor.isNotEmpty()) {
            Color.parseColor(vn.icheck.android.ichecklibs.Constant.primaryColor)
        } else {
            ContextCompat.getColor(this, vn.icheck.android.ichecklibs.R.color.colorPrimary)
        }
        swipe_container.setColorSchemeColors(swipeColor, swipeColor, swipeColor)
        swipe_container.setOnRefreshListener {
            if (!key.isNullOrEmpty()) {
                viewModel.getHistoryByKey(key!!, false, true)
            } else {
                swipe_container.isRefreshing = false
            }
        }
    }

    private fun initRecyclerView() {
        rcvContent.layoutManager = LinearLayoutManager(this)
        rcvContent.adapter = adapter

        if (!adapter.isListNotEmpty){
            adapter.setErrorCode(Constant.ERROR_EMPTY)
        }
    }

    fun getData() {
        swipe_container.isRefreshing = true
        viewModel.getCartCount()
    }

    private fun initViewModel() {
        runOnUiThread {
            viewModel.cartCount.observe(this, {
                if (it != null) {
                    val i = it
                    when {
                        i ?: 0 > 9 -> {
                            tvCountCart.text = "9+"
                            tvCountCart.beVisible()
                        }
                        i ?: 0 > 0 -> {
                            tvCountCart.text = "$i"
                            tvCountCart.beVisible()
                        }
                        else -> {
                            tvCountCart.beGone()
                        }
                    }
                }
            })
        }

        viewModel.listData.observe(this,{
            swipe_container.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.isLoadMoreData.observe(this, {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.errorData.observe(this,  {
            swipe_container.isRefreshing = false
            when (it) {
                Constant.ERROR_EMPTY_SEARCH -> {
                    adapter.setErrorCode(Constant.ERROR_EMPTY_SEARCH)
                }

                Constant.ERROR_EMPTY -> {
                    adapter.setErrorCode(Constant.ERROR_EMPTY)
                }

                Constant.ERROR_SERVER -> {
                    adapter.setErrorCode(Constant.ERROR_SERVER)
                }

                Constant.ERROR_INTERNET -> {
                    adapter.setErrorCode(Constant.ERROR_INTERNET)
                }

                Constant.ERROR_UNKNOW -> {
                    adapter.setErrorCode(Constant.ERROR_SERVER)
                }
            }
        })

        viewModel.statusCode.observe(this, {
            swipe_container.isRefreshing = false
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            key?.let { it1 -> viewModel.getHistoryByKey(it1) }
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

    private fun initTextListener() {
        disposable = RxTextView.afterTextChangeEvents(edtSearch).skipInitialValue().debounce(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            key = it.view().text.toString()
            adapter.clearListData()
            if (it.view().text.toString().isEmpty()){
                adapter.clearListData()
                adapter.setErrorCode(Constant.ERROR_EMPTY)
            } else {
                viewModel.getHistoryByKey(it.view().text.toString(),false,true)
            }

            edtSearch.requestFocus()
            edtSearch.post {
                this.forceShowKeyboard(edtSearch)
            }
        }
    }

    override fun onLoadMore() {
        runOnUiThread {
            viewModel.getHistoryByKey(key ?: "", true)
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
            requestLoginCart -> {
                startActivity<ShipActivity,Boolean>(Constant.CART,true)
            }
        }
    }
}