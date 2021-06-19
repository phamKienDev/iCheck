package vn.icheck.android.screen.account.registeruser.success

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_success.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseFragmentActivity
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.constant.Constant

/**
 * Created by VuLCL on 9/15/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class RegisterSuccessFragment : BaseFragmentMVVM() {

    companion object {
        fun newInstance(phone: String): RegisterSuccessFragment {
            val fragment = RegisterSuccessFragment()

            val bundle = Bundle()
            bundle.putString(Constant.DATA_1, phone)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
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