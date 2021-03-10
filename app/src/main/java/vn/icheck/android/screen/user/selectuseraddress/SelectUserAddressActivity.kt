package vn.icheck.android.screen.user.selectuseraddress

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_select_address.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.createuseraddress.CreateUserAddressActivity
import vn.icheck.android.screen.user.selectuseraddress.adapter.SelectUserAddressAdapter
import vn.icheck.android.screen.user.selectuseraddress.presenter.SelectUserAddressPresenter
import vn.icheck.android.screen.user.selectuseraddress.view.ISelectUserAddressView

/**
 * Created by VuLCL on 12/27/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 *
 * Response:
 * Result: Activity.RESULT_OK
 * Data: key: Constant.DATA_1, type: Json
 * If no data: Selected item was deleted
 */
class SelectUserAddressActivity : BaseActivity<SelectUserAddressPresenter>(), ISelectUserAddressView {
    private val adapter = SelectUserAddressAdapter(this)

    private val requestCreateAddress = 1

    override val getLayoutID: Int
        get() = R.layout.activity_select_address

    override val getPresenter: SelectUserAddressPresenter
        get() = SelectUserAddressPresenter(this)

    override fun onInitView() {
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefreshLayout()
        setupListener()

        presenter.getData(intent)
        presenter.getListAddress()
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.dia_chi_giao_hang)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter.setMessage(0, getString(R.string.ban_khong_co_dia_chi_nao), null)
        recyclerView.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            getListAddress()
        }

        swipeLayout.post {
            getListAddress()
        }
    }

    private fun getListAddress() {
        swipeLayout.isRefreshing = true
        presenter.getListAddress()
    }

    private fun setupListener() {
        btnDone.setOnClickListener {
            val obj = adapter.getSelectedItem

            if (obj != null) {
                val intent = Intent()
                intent.putExtra(Constant.DATA_1, JsonHelper.toJson(obj))
                setResult(Activity.RESULT_OK, intent)
                finishActivity(null, null)
            } else {
                showShortError(R.string.vui_long_chon_dia_chi)
            }
        }
    }

    override fun onSetAddressID(addressID: Long?) {
        adapter.setAddressID(addressID)
    }

    override fun onGetAddressError(icon: Int, error: String) {
        swipeLayout.isRefreshing = false

        if (adapter.isEmpty) {
            adapter.setError(icon, error, R.string.thu_lai)
        } else {
            showShortError(error)
        }
    }

    override fun onSetAddress(list: MutableList<ICAddress>) {
        swipeLayout.isRefreshing = false
        adapter.setListData(list)
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onMessageClicked() {
        getListAddress()
    }

    override fun onAddUserAddress() {
        startActivityForResult<CreateUserAddressActivity>(requestCreateAddress)
    }

    override fun onDeleteAddress(id: Long) {
        DialogHelper.showConfirm(this@SelectUserAddressActivity, R.string.ban_muon_xoa_dia_chi_nay, true, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                presenter.deleteAddress(id)
            }
        })
    }

    override fun onDeleteAddressSuccess(id: Long) {
        adapter.deleteItem(id)
    }

    override fun onAddAddressSuccess(obj: ICAddress) {
        adapter.addTopItem(obj)
        onSetAddressID(obj.id)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCreateAddress) {
                presenter.getCreatedAddress(data)
            }
        }
    }

    override fun onBackPressed() {
        if (presenter.getDefaultAddressID == null) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
        }

        super.onBackPressed()
    }
}