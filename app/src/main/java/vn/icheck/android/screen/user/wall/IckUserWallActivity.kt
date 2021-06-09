package vn.icheck.android.screen.user.wall

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.ActivityIckUserWallBinding
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.wall.mainuser.IckUserWallFragment
import vn.icheck.android.screen.user.wall.mainuser.IckUserWallFragmentDirections
import vn.icheck.android.screen.user.wall.manage_page.PageManagementFragment
import vn.icheck.android.util.ick.startClearTopActivity

const val USER_ID = "user_id"
const val OPEN_INFOR = "open_infor"
const val EDIT_MY_PUBLIC_INFO = 1222

@AndroidEntryPoint
class IckUserWallActivity : BaseActivityMVVM() {
    private lateinit var binding: ActivityIckUserWallBinding
    private val ickUserWallViewModel: IckUserWallViewModel by viewModels()

    companion object {
        fun create(id: Long?, activity: Activity) {
            val i = Intent(activity, IckUserWallActivity::class.java)
            i.putExtra(USER_ID, id)
            ActivityHelper.startActivity(activity, i)
        }

        fun create(id: Long?, context: Context) {
            val i = Intent(context, IckUserWallActivity::class.java)
            i.putExtra(USER_ID, id)
            context.startActivity(i)
        }

        fun openInfor(id: Long?, context: Context) {
            val i = Intent(context, IckUserWallActivity::class.java)
            i.putExtra(USER_ID, id)
            i.putExtra(OPEN_INFOR, true)
            context.startActivity(i)
        }

        fun openManagePage(id: Long?, context: Context) {
            val i = Intent(context, IckUserWallActivity::class.java)
            i.putExtra(USER_ID, id)
            i.putExtra("manage_page", true)
            context.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initViewModel()
//        RelationshipManager.checkUpdate()
    }

    private fun initViewModel() {
        var id = intent.getLongExtra(USER_ID, -1)
        if (id == -1L) {
            id = SessionManager.session.user?.id ?: -1L
        }
        ickUserWallViewModel.id = id
        ickUserWallViewModel.showBottomBar.observe(this, Observer {
            if (it) {
                showBottom()
            } else {
                hideBottom()
            }
        })
    }

    private fun initViews() {
        binding = ActivityIckUserWallBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        StatusBarUtils.setOverStatusBarDark(this)

        binding.btnHomepage.setOnClickListener {
            startClearTopActivity(HomeActivity::class.java)
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
        }
        binding.btnMyPage.setOnClickListener {
            lifecycleScope.launch {
                delay(200)
                val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_wall_fragment)
                if (navFragment?.childFragmentManager?.fragments?.last() !is IckUserWallFragment) {
                    binding.btnMyPage.setTextColor(Color.parseColor("#057DDA"))
                    binding.btnMyPage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bottom_wall_fc_27px, 0, 0)
                    binding.btnManagePage.setTextColor(Color.parseColor("#b4b4b4"))
                    binding.btnManagePage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bottombar_group_page_unfc_27px, 0, 0)
                    findNavController(R.id.nav_host_wall_fragment).popBackStack()
                }
            }

        }
        binding.btnManagePage.setOnClickListener {
            lifecycleScope.launch {
                delay(200)
                setChoosePageManage()
                val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_wall_fragment)
                if (navFragment?.childFragmentManager?.fragments?.last() is IckUserWallFragment) {
                    findNavController(R.id.nav_host_wall_fragment).navigate(IckUserWallFragmentDirections.actionIckUserWallFragmentToPageManagementFragment())
                }
            }

        }
        findNavController(R.id.nav_host_wall_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.label == "IckUserWallFragment") {
                showBottom()
            }
        }
        if (intent.getBooleanExtra("manage_page", false)) {
            setChoosePageManage()
            findNavController(R.id.nav_host_wall_fragment).navigate(IckUserWallFragmentDirections.actionIckUserWallFragmentToPageManagementFragment())
        }
    }

    private fun setChoosePageManage() {
        binding.btnMyPage.setTextColor(Color.parseColor("#b4b4b4"))
        binding.btnMyPage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bottombar_wall_unfc_27px, 0, 0)
        binding.btnManagePage.setTextColor(Color.parseColor("#057DDA"))
        binding.btnManagePage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bottombar_group_page_fc_27px, 0, 0)
    }

    private fun hideBottom() {
        binding.navBottom.visibility = View.GONE
    }

    private fun showBottom() {
        binding.navBottom.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_MY_PUBLIC_INFO) {
            if (resultCode == RESULT_OK) {
                ickUserWallViewModel.updateUser.postValue(RESULT_OK)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_wall_fragment)
        val last = navFragment?.childFragmentManager?.fragments?.last()
        if (last is PageManagementFragment) {
            binding.btnMyPage.setTextColor(Color.parseColor("#057DDA"))
            binding.btnMyPage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bottom_wall_fc_27px, 0, 0)
            binding.btnManagePage.setTextColor(Color.parseColor("#b4b4b4"))
            binding.btnManagePage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bottombar_group_page_unfc_27px, 0, 0)
        } else if(last is IckUserWallFragment){
            finish()
        }
    }
}