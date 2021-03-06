package vn.icheck.android.screen.user.detail_stamp_v6_1.selectdistrictstamp

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
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.DistrictsItem
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.selectdistrictstamp.adapter.SelectDistrictStampAdapter
import vn.icheck.android.screen.user.selectdistrict.presenter.SelectDistrictStampPresenter
import vn.icheck.android.screen.user.selectdistrict.view.SelectDistrictStampView
import java.util.concurrent.TimeUnit

class SelectDistrictStampActivity : BaseActivityMVVM(), SelectDistrictStampView {
    private val adapter = SelectDistrictStampAdapter(this)

    private var disposable: Disposable? = null

    val presenter = SelectDistrictStampPresenter(this@SelectDistrictStampActivity)

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

        presenter.getData(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            txtTitle.setText(R.string.select_district)
        } else {
            txtTitle.setText(R.string.chon_quan_huyen)
        }
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
        edtSearch.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(this@SelectDistrictStampActivity)
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

    override fun onGetDataError() {
        DialogHelper.showNotification(this@SelectDistrictStampActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@SelectDistrictStampActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }


    override fun onSetListDistrict(list: MutableList<DistrictsItem>?) {
        adapter.setData(list)
    }

    override fun onMessageClicked() {
        presenter.getListDistrict()
    }

    override fun onItemClicked(item: DistrictsItem) {
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
        get() = this@SelectDistrictStampActivity

    override fun onDestroy() {
        disposable?.dispose()
        disposable = null
        super.onDestroy()
    }
}