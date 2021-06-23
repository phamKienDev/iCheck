package vn.icheck.android.screen.user.detail_stamp_v6_1.selectprovincestamp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_select_province.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.CitiesItem
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.selectprovincestamp.adapter.SelectProvinceStampAdapter
import vn.icheck.android.screen.user.selectprovincestamp.presenter.SelectProvinceStampPresenter
import vn.icheck.android.screen.user.selectprovincestamp.view.SelectProvinceStampView
import java.util.concurrent.TimeUnit

class SelectProvinceStampActivity : BaseActivityMVVM(), SelectProvinceStampView {
    private val adapter = SelectProvinceStampAdapter(this)
    private val presenter = SelectProvinceStampPresenter(this@SelectProvinceStampActivity)
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_province)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        initView()
        initRecyclerView()
        initListener()

        presenter.getListProvince()
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            txtTitle.setText(R.string.select_city)
        } else {
            txtTitle.setText(R.string.chon_tinh_thanh_pho)
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
          edtSearch.background= ViewHelper.bgTransparentStrokeLineColor1Corners4(this@SelectProvinceStampActivity)
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun initListener() {
        disposable = RxTextView.afterTextChangeEvents(edtSearch)
                .skipInitialValue()
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.view().text.isNotEmpty()) {
                        adapter.search(it.view().text.toString().trim())
                    } else {
                        adapter.search("")
                    }
                }
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@SelectProvinceStampActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }


    override fun onSetListProvince(list: MutableList<CitiesItem>?) {
        adapter.setData(list)
    }

    override fun onMessageClicked() {
        presenter.getListProvince()
    }

    override fun onItemClicked(item: CitiesItem) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, item)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {

        if (adapter.isListNotEmpty) {
            showShortError(errorMessage)
        } else {
            adapter.setError(errorMessage)
        }
    }

    override val mContext: Context
        get() = this@SelectProvinceStampActivity

    override fun onDestroy() {
        disposable?.dispose()
        disposable = null
        presenter.disposeApi()
        super.onDestroy()
    }
}