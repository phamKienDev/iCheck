package vn.icheck.android.loyalty.screen.select_address.province

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_select_province.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.model.ICProvince
import vn.icheck.android.loyalty.screen.select_address.ISelectAddressListener
import java.util.concurrent.TimeUnit

class SelectProvinceActivity : BaseActivityGame(), ISelectAddressListener<ICProvince> {
    private val viewModel by viewModels<SelectProvinceViewModel>()

    private val adapter = SelectProvinceAdapter(this)

    private var disposable: Disposable? = null

    override val getLayoutID: Int
        get() = R.layout.activity_select_province

    override fun onInitView() {
        initToolbar()
        initRecyclerView()
        initRxTextView()
        initListener()

        viewModel.getListProvince(false)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        txtTitle.setText(R.string.chon_tinh_thanh_pho)

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
            adapter.setError(it)
        })

        viewModel.onSetData.observe(this, Observer {
            adapter.setData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            adapter.addData(it)
        })
    }

    override fun onMessageClicked() {
        viewModel.getListProvince(false)
    }

    override fun onItemClicked(item: ICProvince) {
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