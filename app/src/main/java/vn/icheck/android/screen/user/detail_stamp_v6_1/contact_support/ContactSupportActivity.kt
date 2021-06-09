package vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_contact_support.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.ICSupport
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.util.kotlin.ContactUtils

class ContactSupportActivity : BaseActivityMVVM(), IContactSupportView {

    val presenter = ContactSupportPresenter(this@ContactSupportActivity)

    private val adapter = ContactSupportAdapter(this)
    private val requestPhone = 1

    private var value:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_support)
        onInitView()
    }

    fun onInitView() {
        if (DetailStampActivity.isVietNamLanguage == false){
            txtTitle.text = "Contact help"
        } else {
            txtTitle.text = "Liên hệ hỗ trợ"
        }

        presenter.getListContact()
        initRecyclerview()
        listener()
    }

    private fun initRecyclerview() {
        rcvContactSupport.layoutManager = LinearLayoutManager(this)
        rcvContactSupport.adapter = adapter
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onGetListSupportSuccess(listSupport: MutableList<ICSupport>?) {
        if (!listSupport.isNullOrEmpty()) {
            adapter.setListData(listSupport)
        } else {
            adapter.setListData(mutableListOf())
        }
    }

    override fun setItemClick(item: ICSupport) {
        when (item.contact_type) {
            "phone" -> {
                value = item.contact
                PermissionDialog.checkPermission(this@ContactSupportActivity, PermissionDialog.CALL, object : PermissionDialog.PermissionListener {
                    override fun onPermissionAllowed() {
                        item.contact?.let { ContactUtils.callFast(this@ContactSupportActivity, it) }
                    }

                    override fun onRequestPermission() {
                        PermissionHelper.checkPermission(this@ContactSupportActivity, Manifest.permission.CALL_PHONE, requestPhone)
                    }

                    override fun onPermissionNotAllow() {
                        showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                    }
                })
            }
            "email" -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:${item.contact}")
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@ContactSupportActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@ContactSupportActivity, isShow)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                value?.let { ContactUtils.callFast(this@ContactSupportActivity, it) }
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}
