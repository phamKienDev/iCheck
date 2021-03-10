package vn.icheck.android.screen.user.detail_stamp_v6_1.selectprovincestamp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_select_province.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.CitiesItem
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.selectprovincestamp.adapter.SelectProvinceStampAdapter
import vn.icheck.android.screen.user.selectprovincestamp.presenter.SelectProvinceStampPresenter
import vn.icheck.android.screen.user.selectprovincestamp.view.SelectProvinceStampView
import java.util.concurrent.TimeUnit

class SelectProvinceStampActivity : BaseActivity<SelectProvinceStampPresenter>(), SelectProvinceStampView {
    private val adapter = SelectProvinceStampAdapter(this)

    private var disposable: Disposable? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_select_province

    override val getPresenter: SelectProvinceStampPresenter
        get() = SelectProvinceStampPresenter(this)

    override fun onInitView() {
        initToolbar()
        initRecyclerView()
        initListener()

        presenter.getListProvince()
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            txtTitle.text = "Select City"
        } else {
            txtTitle.setText(R.string.chon_tinh_thanh_pho)
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
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
        super.showError(errorMessage)

        if (adapter.isListNotEmpty) {
            showShortError(errorMessage)
        } else {
            adapter.setError(errorMessage)
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        disposable = null
        presenter.disposeApi()
        super.onDestroy()
    }
}