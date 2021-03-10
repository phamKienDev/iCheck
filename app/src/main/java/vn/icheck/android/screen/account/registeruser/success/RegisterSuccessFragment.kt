package vn.icheck.android.screen.account.registeruser.success

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_register_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseFragmentActivity
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.account.registeruser.success.presenter.RegisterSuccessPresenter
import vn.icheck.android.screen.account.registeruser.success.view.RegisterSuccessView

/**
 * Created by VuLCL on 9/15/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class RegisterSuccessFragment : BaseFragment<RegisterSuccessPresenter>(), RegisterSuccessView {

    companion object {
        fun newInstance(phone: String): RegisterSuccessFragment {
            val fragment = RegisterSuccessFragment()

            val bundle = Bundle()
            bundle.putString(Constant.DATA_1, phone)
            fragment.arguments = bundle

            return fragment
        }
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_register_success

    override val getPresenter: RegisterSuccessPresenter
        get() = RegisterSuccessPresenter(this)

    override fun onInitView() {
        val phone = try {
            arguments?.getString(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        btnLogin.setOnClickListener {
            activity?.run {
                if (this is BaseFragmentActivity) {
                    val intent = Intent()
                    intent.putExtra(Constant.DATA_1, phone)
                    setResult(Activity.RESULT_OK, intent)
                    finishActivity()
                }
            }
//            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.PHONE, phone))
//            activity?.onBackPressed()
        }
    }
}