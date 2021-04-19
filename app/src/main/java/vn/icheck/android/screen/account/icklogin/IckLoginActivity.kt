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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_ick_login.*
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
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.ShareSessionToModule
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ick_login)
        nav_host_fragment_login.view?.background = ResourcesCompat.getDrawable(resources, R.drawable.ick_bg_top_corner_20, null)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        btn_exit.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btn_register.setOnClickListener {
            if (!ickLoginViewModel.onAction && ickLoginViewModel.state == 1) {
                ickLoginViewModel.onAction = true
                launchRegister()
            }
        }
        btn_login.setOnClickListener {
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
                    val i = Intent(this, SuggestTopicActivity::class.java)
                    startActivity(i)
                }
                SHOW_LOGIN_REGISTER -> {
                    showBtn()
                }
            }
        })
        lifecycleScope.launch {
            if (intent.getIntExtra("requestCode", 0) == 1) {
                btn_login.setTextColor(Color.parseColor("#80ffffff"))
                btn_register.setTextColor(Color.WHITE)
                findNavController(R.id.nav_host_fragment).popBackStack(R.id.ickLoginFragment, false)
                val action = IckLoginFragmentDirections.actionIckLoginFragmentToIckLoginOtpFragment(REGISTER)
                findNavController(R.id.nav_host_fragment).navigate(action)
                btn_login.setTextSize(16F)
                btn_register.setTextSize(18F)
                ickLoginViewModel.state = 2
            } else {
                btn_login.setTextSize(18F)
                btn_register.setTextSize(16F)
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
                                            findNavController(R.id.nav_host_fragment).navigate(action)
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
        btn_login.setTextSize(18F)
        btn_register.setTextSize(16F)
        btn_login.setTextColor(Color.WHITE)
        btn_register.setTextColor(Color.parseColor("#80ffffff"))
        findNavController(R.id.nav_host_fragment_login).popBackStack(R.id.ickLoginFragment, false)
        forceHideKeyboard()
        ickLoginViewModel.stateRegister.postValue(1)
        ickLoginViewModel.state = 1
        ickLoginViewModel.onAction = false
    }

    private fun launchRegister() {
        ickLoginViewModel.state = 2
        ickLoginViewModel.stateRegister.postValue(2)
        if (btn_login.textSize != 16f * getResources().getDisplayMetrics().scaledDensity) {
            btn_login.setTextColor(Color.parseColor("#80ffffff"))
            btn_register.setTextColor(Color.WHITE)
            btn_login.setTextSize(16F)
            btn_register.setTextSize(18F)
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
        btn_login.visibility = View.INVISIBLE
        btn_register.visibility = View.INVISIBLE
    }

    private fun showBtn() {
        btn_login.beVisible()
        btn_register.beVisible()
    }
}
