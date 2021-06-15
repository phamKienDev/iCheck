package vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_store_stamp_v5.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectStoreV6
import vn.icheck.android.network.models.detail_stamp_v6.ICStoreStampV6
import vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5.adapter.StoreStampV5Adapter
import vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5.presenter.SelectStoreStampV5Presenter
import vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5.view.ISelectStoreStampV5View
import vn.icheck.android.util.ick.rText

class SelectStoreStampV5Activity : BaseActivity<SelectStoreStampV5Presenter>(), ISelectStoreStampV5View {

    override val getLayoutID: Int
        get() = R.layout.activity_select_store_stamp_v5

    override val getPresenter: SelectStoreStampV5Presenter
        get() = SelectStoreStampV5Presenter(this)

    private var adapter : StoreStampV5Adapter? = null

    override fun onInitView() {
        initRecylerview()
        presenter.getDataIntent(intent)
        listener()
    }

    private fun initRecylerview(){
        adapter = StoreStampV5Adapter(this)
        rcvListStore.layoutManager = LinearLayoutManager(this)
        rcvListStore.adapter = adapter
    }

    private fun listener(){
        txtTitle rText R.string.chon_diem_ban

        imgBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            onBackPressed()
        }
    }

    override fun onClickItem(item: ICObjectStoreV6) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_5,item.name)
        intent.putExtra(Constant.DATA_4,item.id)
        setResult(Activity.RESULT_OK,intent)
        onBackPressed()
    }

    override fun onGetDataError(type: Int) {

    }

    override fun onGetListStoreSuccess(obj: ICStoreStampV6) {
        obj.data?.store?.let { adapter?.setListData(it) }
    }
}
