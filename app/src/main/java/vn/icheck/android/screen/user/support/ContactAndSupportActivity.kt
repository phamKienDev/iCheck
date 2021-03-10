package vn.icheck.android.screen.user.support

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_contact_and_support.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.activity.BaseFragmentActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.screen.user.support.adapter.ContactAndSupportAdapter
import vn.icheck.android.screen.user.support.view.IContactAndSupportView
import vn.icheck.android.util.kotlin.ActivityUtils

class ContactAndSupportActivity : BaseActivity<BaseActivityPresenter>(), IContactAndSupportView {

    override val getLayoutID: Int
        get() = R.layout.activity_contact_and_support

    override val getPresenter: BaseActivityPresenter
        get() = BaseActivityPresenter(this)

    override fun onInitView() {
        initToolbar()
        initRecyclerView()
    }

    companion object{
        fun start(activity: FragmentActivity) {
            ActivityUtils.startActivity<ContactAndSupportActivity>(activity)
        }
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.lien_he_ho_tro)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        val listSupport = SettingManager.clientSetting?.supports

        if (!listSupport.isNullOrEmpty()) {
            recyclerView.adapter = ContactAndSupportAdapter(this, listSupport)
        } else {
            DialogHelper.showNotification(this@ContactAndSupportActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        }
    }

    override fun onCallPhone(phone: String) {
        val number = Uri.parse("tel:$phone")
        val callIntent = Intent(Intent.ACTION_DIAL, number)
        startActivity(callIntent)
    }

    override fun onSendEmail(email: String) {
        val mailIntent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:?to=$email")
        mailIntent.setData(data)
        try {
            startActivity(Intent.createChooser(mailIntent, "Send mail..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}