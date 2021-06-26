package vn.icheck.android.screen.user.selectuseraddress

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_select_address.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
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
class SelectUserAddressActivity : BaseActivityMVVM(), ISelectUserAddressView {
    private val adapter = SelectUserAddressAdapter(this)
    private val presenter = SelectUserAddressPresenter(this@SelectUserAddressActivity)
    private val requestCreateAddress = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_address)
        onInitView()
    }

    fun onInitView() {
        setupToolbar()
        setupView()
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

    private fun setupView() {
        btnDone.background=ViewHelper.btnSecondaryCorners26(this)
    }

    private fun setupRecyclerView() {
        adapter.setMessage(0, getString(R.string.ban_khong_co_dia_chi_nao), null)
        recyclerView.adapter = adapter
    }

    private fun setupSwipeRefreshLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

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

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@SelectUserAddressActivity, isShow)
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

        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@SelectUserAddressActivity

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