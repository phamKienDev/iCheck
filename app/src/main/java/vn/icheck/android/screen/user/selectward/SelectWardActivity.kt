package vn.icheck.android.screen.user.selectward

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
import vn.icheck.android.network.models.ICWard
import vn.icheck.android.screen.user.selectward.adapter.SelectWardAdapter
import vn.icheck.android.screen.user.selectward.presenter.SelectWardPresenter
import vn.icheck.android.screen.user.selectward.view.SelectWardView
import vn.icheck.android.util.KeyboardUtils
import java.util.concurrent.TimeUnit

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SelectWardActivity : BaseActivityMVVM(), SelectWardView {
    private val adapter = SelectWardAdapter(this)
    private val presenter = SelectWardPresenter(this@SelectWardActivity)
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

        presenter.getData(intent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chon_phuong_xa)

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

    override fun onGetDataError() {
        DialogHelper.showNotification(this@SelectWardActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@SelectWardActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onSetListWard(list: MutableList<ICWard>, isLoadMore: Boolean) {
        if (isLoadMore)
            adapter.addData(list)
        else
            adapter.setData(list)

        if (!isLoadMore)
            KeyboardUtils.showSoftInput(edtSearch)
    }

    override fun onMessageClicked() {
        presenter.getListWard(false)
    }

    override fun onItemClicked(item: ICWard) {
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
        get() = this@SelectWardActivity

    override fun onDestroy() {
        disposable?.dispose()
        disposable = null
        super.onDestroy()
    }
}