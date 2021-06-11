package vn.icheck.android.screen.user.selectprovince

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
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.screen.user.selectprovince.adapter.SelectProvinceAdapter
import vn.icheck.android.screen.user.selectprovince.presenter.SelectProvincePresenter
import vn.icheck.android.screen.user.selectprovince.view.SelectProvinceView
import vn.icheck.android.util.KeyboardUtils
import java.util.concurrent.TimeUnit

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectProvinceActivity : BaseActivityMVVM(), SelectProvinceView {
    private val adapter = SelectProvinceAdapter(this)
    val presenter = SelectProvincePresenter(this@SelectProvinceActivity)
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_province)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        initRecyclerView()
        initListener()

        presenter.getListProvince(false)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chon_tinh_thanh_pho)

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

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@SelectProvinceActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }


    override fun onSetListProvince(list: MutableList<ICProvince>, isLoadMore: Boolean) {
        if (isLoadMore)
            adapter.addData(list)
        else
            adapter.setData(list)

        if (!isLoadMore)
            KeyboardUtils.showSoftInput(edtSearch)
    }

    override fun onMessageClicked() {
        presenter.getListProvince(false)
    }

    override fun onItemClicked(item: ICProvince) {
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
        get() = this@SelectProvinceActivity

    override fun onDestroy() {
        disposable?.dispose()
        disposable = null
        presenter.disposeApi()
        super.onDestroy()
    }
}