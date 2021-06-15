package vn.icheck.android.screen.user.detail_stamp_v6_1.select_variant

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_variant.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.util.ick.rText

class SelectVariantActivity : BaseActivity<SelectVariantPresenter>(), ISelectVariantView {

    override val getLayoutID: Int
        get() = R.layout.activity_select_variant

    override val getPresenter: SelectVariantPresenter
        get() = SelectVariantPresenter(this)

    private var mid: Long? = null
    private val adapter = SelectVariantStampAdapter(this)

    override fun onInitView() {
        initHeader()
        listener()
        presenter.getDataIntent(intent)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initHeader() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            txtTitle rText R.string.select_variation
        } else {
            txtTitle rText R.string.chon_bien_the
        }
    }

    override fun onGetDataVariantSuccess(products: MutableList<ICVariantProductStampV6_1.ICVariant.ICObjectVariant>, productId: Long, loadMore: Boolean) {
        mid = productId
        if (loadMore)
            adapter.addListData(products)
        else
            adapter.setListData(products)
    }

    override fun onLoadMore() {
        presenter.getListVariant(mid, true)
    }

    override fun onClickItem(item: ICVariantProductStampV6_1.ICVariant.ICObjectVariant) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, item.extra)
        intent.putExtra(Constant.DATA_2, item.id)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun onRefresh() {
        presenter.getDataIntent(intent)
    }

    override fun onGetDataVariantFail(string: String) {
        adapter.setErrorCode(string)
    }

    override fun onGetDataError(errorType: Int) {
        when(errorType){
            Constant.ERROR_INTERNET -> {
                if (DetailStampActivity.isVietNamLanguage == false){
                    DialogHelper.showConfirm(this@SelectVariantActivity, rText(R.string.checking_network_please_try_again), false, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            presenter.getDataIntent(intent)
                        }
                    })
                } else {
                    DialogHelper.showConfirm(this@SelectVariantActivity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            presenter.getDataIntent(intent)
                        }
                    })
                }
            }
        }
    }
}