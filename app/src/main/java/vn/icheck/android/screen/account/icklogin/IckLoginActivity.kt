package vn.icheck.android.screen.account.icklogin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.facebook.login.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseCoroutineActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.FACEBOOK_AVATAR
import vn.icheck.android.constant.FACEBOOK_TOKEN
import vn.icheck.android.constant.FACEBOOK_USERNAME
import vn.icheck.android.databinding.ActivityIckLoginBinding
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.ShareSessionToModule
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNetworkManager
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.*
import vn.icheck.android.util.toICBaseResponse
import vn.icheck.android.screen.account.icklogin.fragment.IckLoginFragmentDirections
import vn.icheck.android.screen.account.icklogin.viewmodel.CHOOSE_TOPIC
import vn.icheck.android.screen.account.icklogin.viewmodel.HIDE_LOGIN_REGISTER
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.account.icklogin.viewmodel.SHOW_LOGIN_REGISTER
import vn.icheck.android.screen.user.suggest_topic.SuggestTopicActivity
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.forceHideKeyboard
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.showSimpleErrorToast
import javax.inject.Inject

const val LOGIN_OTP = 1
const val FORGOT_PW = 2
const val REGISTER = 3

/**i
 * Happy new year
 * 00:00 ngày 01/01/2021
 * Chúc mừng năm mới anh Sơn nha. Năm mới kiếm được người yêu nha anh :v
 */
@AndroidEntryPoint
class IckLoginActivity : BaseCoroutineActivity() {
    val ickLoginViewModel: IckLoginViewModel by viewModels()

    private lateinit var facebookReceiver: BroadcastReceiver

    @Inject
    lateinit var loginManager: LoginManager

    private fun loginFacebookSuccess(it: String) {
        val icSession = ICSessionData()
        if (!SessionManager.isUserLogged) {
            icSession.token = it
        }
        SessionManager.session = icSession
        ickLoginViewModel.getUserInfo().observe(this@IckLoginActivity, Observer { data ->
            data?.data?.let { userInfoRes ->
                SessionManager.session = SessionManager.session.apply {
                    user = userInfoRes.createICUser()
                    userType = 2
                    token = it
                }

                ShareSessionToModule.setSession(SessionManager.session)
                CartHelper().getCartSocial()
                InsiderHelper.onLogin()
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
            }
        })
    }

    private lateinit var binding: ActivityIckLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIckLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewBackground.background=ViewHelper.bgWhiteRadiusTop20(this)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        binding.btnExit.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding.btnRegister.setOnClickListener {
            if (!ickLoginViewModel.onAction && ickLoginViewModel.state == 1) {
                ickLoginViewModel.onAction = true
                launchRegister()
            }
        }
        binding.btnLogin.setOnClickListener {
            if (!ickLoginViewModel.onAction && ickLoginViewModel.state == 2) {
                ickLoginViewModel.onAction = true
                launchLogin()
            }
        }
        ickLoginViewModel.mErr.observe(this, Observer {
            showSimpleErrorToast(it.toICBaseResponse()?.message ?: it.message)
//            ToastUtils.showShortError(this, it.toICBaseResponse()?.message ?: it.message)
        })
        ickLoginViewModel.mState.observe(this, Observer {
            when (it) {
                HIDE_LOGIN_REGISTER -> {
                    hideBtns()
                }
                CHOOSE_TOPIC -> {
                    startActivity<SuggestTopicActivity>()
                }
                SHOW_LOGIN_REGISTER -> {
                    showBtn()
                }
            }
        })
        lifecycleScope.launch {
            if (intent.getIntExtra("requestCode", 0) == 1) {
                binding.btnLogin.setTextColor(Color.parseColor("#80ffffff"))
                binding.btnRegister.setTextColor(Color.WHITE)
                findNavController(R.id.nav_host_fragment_login).popBackStack(R.id.ickLoginFragment, false)
                val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckLoginOtpFragment(REGISTER)
                findNavController(R.id.nav_host_fragment_login).navigate(action)
                binding.btnLogin.setTextSize(16F)
                binding.btnRegister.setTextSize(18F)
                ickLoginViewModel.state = 2
            } else {
                binding.btnLogin.setTextSize(18F)
                binding.btnRegister.setTextSize(16F)
            }
        }
        forceHideKeyboard()
        facebookReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == FACEBOOK_TOKEN) {
                    ickLoginViewModel.facebookAvatar = intent.getStringExtra(FACEBOOK_AVATAR)
                    ickLoginViewModel.facebookUsername = intent.getStringExtra(FACEBOOK_USERNAME)
                    intent.getStringExtra(FACEBOOK_TOKEN)?.let {
                        ickLoginViewModel.facebookToken = it
                        ickLoginViewModel.loginFacebook(it).observe(this@IckLoginActivity, Observer { response ->
                            response?.let { dataRes ->
                                when (dataRes.statusCode) {
                                    "U3014" -> {
                                        try {
                                            val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckFacebookLoginFragment(
                                                    intent.getStringExtra(FACEBOOK_AVATAR),
                                                    intent.getStringExtra(FACEBOOK_USERNAME),
                                                    it
                                            )
                                            findNavController(R.id.nav_host_fragment_login).navigate(action)
                                        } catch (e: Exception) {
                                            logError(e)
                                        }
                                    }
                                    "U3004" -> {
                                        showSimpleErrorToast(response.message)
                                    }
                                    "200" -> {
                                        loginFacebookSuccess(response.data?.token.toString())
                                    }
                                }
                            }
                        })
                    }

                }
            }
        }
        val intentFilter = IntentFilter(FACEBOOK_TOKEN)
        registerReceiver(facebookReceiver, intentFilter)
        if (intent.getStringExtra(Constant.DATA_1) == Constant.REGISTER_TYPE) {
            if (!ickLoginViewModel.onAction){
                ickLoginViewModel.onAction = true
                launchRegister()
            }
        }
        loginManager.logOut()
    }

    private fun launchLogin() {

        if (ickLoginViewModel.hasData() && ickLoginViewModel.registerType == REGISTER) {
            DialogHelper.showConfirm(this, "Bạn chắc chắn muốn <br /> thay đổi phương thức đăng nhập?", null, "Không", "Chắc chắn", true, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    chooseLogin()
                    ickLoginViewModel.resetData()
                }
            })
        } else {
            chooseLogin()
        }

    }

    private fun chooseLogin() {
        binding.btnLogin.setTextSize(18F)
        binding.btnRegister.setTextSize(16F)
        binding.btnLogin.setTextColor(Color.WHITE)
        binding.btnRegister.setTextColor(Color.parseColor("#80ffffff"))
        findNavController(R.id.nav_host_fragment_login).popBackStack(R.id.ickLoginFragment, false)
        forceHideKeyboard()
        ickLoginViewModel.stateRegister.postValue(1)
        ickLoginViewModel.state = 1
        ickLoginViewModel.onAction = false
    }

    private fun launchRegister() {
        ickLoginViewModel.state = 2
        ickLoginViewModel.stateRegister.postValue(2)
        if (binding.btnLogin.textSize != 16f * getResources().getDisplayMetrics().scaledDensity) {
            binding.btnLogin.setTextColor(Color.parseColor("#80ffffff"))
            binding.btnRegister.setTextColor(Color.WHITE)
            binding.btnLogin.setTextSize(16F)
            binding.btnRegister.setTextSize(18F)
            findNavController(R.id.nav_host_fragment_login).popBackStack(R.id.ickLoginFragment, false)
            val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckLoginOtpFragment(REGISTER)
            findNavController(R.id.nav_host_fragment_login).navigate(action)
            forceHideKeyboard()
            ickLoginViewModel.onAction = false
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
         
        try {
            if (event.type == ICMessageEvent.Type.ON_LOG_IN) {
                setResult(Activity.RESULT_OK)
                overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
                ICNetworkManager.getLoginProtocol?.onLogin()
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(facebookReceiver)
        EventBus.getDefault().unregister(this)
    }

    private fun hideBtns() {
        binding.btnLogin.visibility = View.INVISIBLE
        binding.btnRegister.visibility = View.INVISIBLE
    }

    private fun showBtn() {
        binding.btnLogin.beVisible()
        binding.btnRegister.beVisible()
    }
}
