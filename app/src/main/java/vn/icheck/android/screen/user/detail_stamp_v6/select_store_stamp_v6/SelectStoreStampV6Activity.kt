package vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_store_stamp_v6.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectStoreV6
import vn.icheck.android.network.models.detail_stamp_v6.ICStoreStampV6
import vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.adapter.StoreStampV6Adapter
import vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.presenter.SelectStoreStampV6Presenter
import vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.view.ISelectStoreStampV6View
import vn.icheck.android.util.ick.rText

class SelectStoreStampV6Activity : BaseActivityMVVM(), ISelectStoreStampV6View {

    val presenter = SelectStoreStampV6Presenter(this@SelectStoreStampV6Activity)

    private var adapter: StoreStampV6Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_store_stamp_v6)
        onInitView()
    }

    fun onInitView() {
        initRecylerview()
        presenter.getDataIntent(intent)
        listener()
    }

    private fun initRecylerview() {
        adapter = StoreStampV6Adapter(this)
        rcvListStore.layoutManager = LinearLayoutManager(this)
        rcvListStore.adapter = adapter
    }

    private fun listener() {
        txtTitle rText R.string.chon_diem_ban

        imgBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            onBackPressed()
        }
    }

    override fun onClickItem(item: ICObjectStoreV6) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_5, item.name)
        intent.putExtra(Constant.DATA_4, item.id)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@SelectStoreStampV6Activity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@SelectStoreStampV6Activity, isShow)
    }

    override fun onGetDataError(type: Int) {

    }

    override fun onGetListStoreSuccess(obj: ICStoreStampV6) {
        obj.data?.store?.let { adapter?.setListData(it) }
    }
}
