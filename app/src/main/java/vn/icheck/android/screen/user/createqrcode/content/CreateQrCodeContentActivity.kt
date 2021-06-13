package vn.icheck.android.screen.user.createqrcode.content

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseFragmentActivity
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.createqrcode.createcontact.CreateContactQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createemail.CreateEmailQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createevent.CreateEventQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createlink.CreateLinkQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createlocation.CreateLocationQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createmessage.CreateMessageQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createtext.CreateTextQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createwifI.CreateWifiQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.cretephone.CreatePhoneQrCodeFragment

class CreateQrCodeContentActivity : BaseFragmentActivity() {

    override val getLayoutID: Int
        get() = R.layout.layout_frame

    override val getFirstFragment: BaseFragmentMVVM
        get() = when (intent?.getIntExtra(Constant.DATA_1, R.id.txtText)) {
            R.id.txtText -> {
                CreateTextQrCodeFragment()
            }
            R.id.txtLink -> {
                CreateLinkQrCodeFragment()
            }
            R.id.txtPhone -> {
                CreatePhoneQrCodeFragment()
            }
            R.id.txtMessage -> {
                CreateMessageQrCodeFragment()
            }
            R.id.txtEmail -> {
                CreateEmailQrCodeFragment()
            }
            R.id.txtLocation -> {
                CreateLocationQrCodeFragment()
            }
            R.id.txtContact -> {
                CreateContactQrCodeFragment()
            }
            R.id.txtEvent -> {
                CreateEventQrCodeFragment()
            }
            else -> {
                CreateWifiQrCodeFragment()
            }
        }

    override fun onInitView() {

    }
}