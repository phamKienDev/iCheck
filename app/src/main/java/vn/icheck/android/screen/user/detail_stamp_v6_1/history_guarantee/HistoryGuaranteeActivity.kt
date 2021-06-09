package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history_guarantee.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.DetaiHistoryGuaranteeActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.adapter.HistoryGuaranteeAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.presenter.HistoryGuaranteePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.view.IHistoryGuaranteeView
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity

class HistoryGuaranteeActivity : BaseActivityMVVM(), IHistoryGuaranteeView {

    private lateinit var adapter: HistoryGuaranteeAdapter

    val presenter = HistoryGuaranteePresenter(this@HistoryGuaranteeActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_guarantee)
        onInitView()
    }

    fun onInitView() {
        presenter.getDataIntent(intent)
        listener()
        initRecyclerView()
        if (DetailStampActivity.isVietNamLanguage == false) {
            txtTitle.text = "Warranty log"
        } else {
            txtTitle.text = "Lịch sử bảo hành"
        }
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        adapter = HistoryGuaranteeAdapter(this,DetailStampActivity.isVietNamLanguage)
        rcvHistoryGuarantee.layoutManager = LinearLayoutManager(this)
        rcvHistoryGuarantee.adapter = adapter
    }

    override fun getDataIntentError(errorType: Int) {

    }

    override fun onGetDataHistoryGuaranteeSuccess(data: MutableList<ICListHistoryGuarantee>) {
        if (data.isNullOrEmpty()) {
            adapter.setError(Constant.ERROR_EMPTY)
        } else {
            adapter.setListData(data)
        }
    }

    override fun onGetDataHistoryGuaranteeFail() {
        adapter.setError(Constant.ERROR_UNKNOW)
    }

    override fun setOnItemClick(item: ICListHistoryGuarantee) {
        val intent = Intent(this, DetaiHistoryGuaranteeActivity::class.java)
        intent.putExtra(Constant.DATA_1, item)
        startActivity(intent)
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@HistoryGuaranteeActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@HistoryGuaranteeActivity, isShow)
    }
}
