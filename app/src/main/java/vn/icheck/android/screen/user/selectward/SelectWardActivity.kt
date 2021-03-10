package vn.icheck.android.screen.user.selectward

import android.app.Activity
import android.content.Intent
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_select_province.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
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
class SelectWardActivity : BaseActivity<SelectWardPresenter>(), SelectWardView {
    private val adapter = SelectWardAdapter(this)

    private var disposable: Disposable? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_select_province

    override val getPresenter: SelectWardPresenter
        get() = SelectWardPresenter(this)

    override fun onInitView() {
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
        super.onDestroy()
    }
}