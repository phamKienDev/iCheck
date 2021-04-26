package vn.icheck.android.screen.account.icklogin.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.DialogChangePwSuccessBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.util.ick.startClearTopActivity

class IckChangePasswordSuccessDialog(val login:() -> Unit):DialogFragment() {

    private var _binding:DialogChangePwSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogChangePwSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.btnClear.setOnClickListener {
//            dismiss()
//        }
        binding.btnHomePage.setOnClickListener {
            dismiss()
            requireActivity().startClearTopActivity(HomeActivity::class.java)
        }
        binding.btnLogin.apply {
            background = ViewHelper.backgroundPrimaryCorners4(context)
            setOnClickListener {
                dismiss()
                login()
            }
        }
    }
}