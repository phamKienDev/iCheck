package vn.icheck.android.screen.user.detail_stamp_v6.history_guarantee_v6

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history_guarantee_v6.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6.ObjectLogHistoryV6
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.DetaiHistoryGuaranteeV6Activity
import vn.icheck.android.screen.user.detail_stamp_v6.history_guarantee_v6.adapter.HistoryGuaranteeV6Adapter
import vn.icheck.android.screen.user.detail_stamp_v6.history_guarantee_v6.presenter.HistoryGuaranteeV6Presenter
import vn.icheck.android.screen.user.detail_stamp_v6.history_guarantee_v6.view.IHistoryGuaranteeV6View
import vn.icheck.android.util.ick.rText

class HistoryGuaranteeV6Activity : BaseActivityMVVM(),IHistoryGuaranteeV6View {

    val presenter = HistoryGuaranteeV6Presenter(this@HistoryGuaranteeV6Activity)

    private lateinit var adapter: HistoryGuaranteeV6Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_guarantee_v6)
        onInitView()
    }

    fun onInitView() {
        presenter.getDataIntent(intent)
        listener()
        initRecyclerView()
        txtTitle rText R.string.lich_su_bao_hanh
    }

    private fun listener(){
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        adapter = HistoryGuaranteeV6Adapter(this)
        rcvHistoryGuarantee.layoutManager = LinearLayoutManager(this)
        rcvHistoryGuarantee.adapter = adapter
    }

    override fun setOnItemClick(item: RESP_Log_History_v6) {
        val intent = Intent(this, DetaiHistoryGuaranteeV6Activity::class.java)
        intent.putExtra(Constant.DATA_1, item)
        startActivity(intent)
    }

    override fun onSetDataSuccess(data: ObjectLogHistoryV6?) {
        if (!data?.logs.isNullOrEmpty()) {
            adapter.setListData(data?.logs)
        }else{
            adapter.setError(Constant.ERROR_EMPTY)
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@HistoryGuaranteeV6Activity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@HistoryGuaranteeV6Activity, isShow)
    }

    override fun getDataIntentError(errorType: Int) {
        when(errorType){
            Constant.ERROR_INTERNET -> showShortError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            else -> showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }
}
