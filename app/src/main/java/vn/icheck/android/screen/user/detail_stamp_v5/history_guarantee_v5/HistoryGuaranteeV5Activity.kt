package vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history_guarantee_v5.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6.ObjectLogHistoryV6
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.screen.user.detail_stamp_v5.detail_history_guarantee_v5.DetaiHistoryGuaranteeV5Activity
import vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.adapter.HistoryGuaranteeV5Adapter
import vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.presenter.HistoryGuaranteeV5Presenter
import vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.view.IHistoryGuaranteeV5View
import vn.icheck.android.screen.user.detail_stamp_v6.detail_history_guarantee_v6.DetaiHistoryGuaranteeV6Activity

class HistoryGuaranteeV5Activity : BaseActivity<HistoryGuaranteeV5Presenter>(), IHistoryGuaranteeV5View {

    override val getLayoutID: Int
        get() = R.layout.activity_history_guarantee_v5

    override val getPresenter: HistoryGuaranteeV5Presenter
        get() = HistoryGuaranteeV5Presenter(this)

    private lateinit var adapter: HistoryGuaranteeV5Adapter

    override fun onInitView() {
        presenter.getDataIntent(intent)
        listener()
        initRecyclerView()
        txtTitle.text = "Lịch sử bảo hành"
    }

    private fun listener(){
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        adapter = HistoryGuaranteeV5Adapter(this)
        rcvHistoryGuarantee.layoutManager = LinearLayoutManager(this)
        rcvHistoryGuarantee.adapter = adapter
    }

    override fun getDataIntentError(errorType: Int) {
        when(errorType){
            Constant.ERROR_INTERNET -> showShortError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            else -> showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }

    override fun setOnItemClick(item: RESP_Log_History_v6) {
        val intent = Intent(this, DetaiHistoryGuaranteeV5Activity::class.java)
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
}
