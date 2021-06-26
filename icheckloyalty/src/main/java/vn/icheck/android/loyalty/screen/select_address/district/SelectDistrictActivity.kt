package vn.icheck.android.loyalty.screen.select_address.district

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_select_district.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.model.ICDistrict
import vn.icheck.android.loyalty.screen.select_address.ISelectAddressListener
import java.util.concurrent.TimeUnit

class SelectDistrictActivity : BaseActivityGame(), ISelectAddressListener<ICDistrict> {
    private val viewModel by viewModels<SelectDistrictViewModel>()

    private val adapter = SelectDistrictAdapter(this)

    private var disposable: Disposable? = null

    override val getLayoutID: Int
        get() = R.layout.activity_select_district

    override fun onInitView() {
        initToolbar()
        initRecyclerView()
        initRxTextView()
        initListener()

        viewModel.getDataIntent(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        txtTitle.setText(R.string.chon_quan_huyen)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun initRxTextView() {
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

    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            if (adapter.isListNotEmpty) {
                showLongError(it.title)
            } else {
                adapter.setError(it.title)
            }
        })

        viewModel.onEmptyString.observe(this, Observer {
            if (it == "ERROR") {
                object : DialogNotification(this@SelectDistrictActivity, null, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), getString(R.string.ok), false) {
                    override fun onDone() {
                        onBackPressed()
                    }
                }.show()
            } else {
                adapter.setError(it)
            }
        })

        viewModel.onSetData.observe(this, Observer {
            adapter.setData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            adapter.addData(it)
        })
    }

    override fun onMessageClicked() {
        viewModel.getListDistrict(false)
    }

    override fun onItemClicked(item: ICDistrict) {
        val intent = Intent()
        intent.putExtra(ConstantsLoyalty.DATA_1, item)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}