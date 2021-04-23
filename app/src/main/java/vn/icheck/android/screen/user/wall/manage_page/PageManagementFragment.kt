package vn.icheck.android.screen.user.wall.manage_page

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.FragmentPageManagementBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.beGone
import vn.icheck.android.ichecklibs.beVisible
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.screen.user.wall.manage_page.my_follow_page.MyFollowPageActivity
import vn.icheck.android.screen.user.wall.manage_page.my_follow_page.MyFollowPageAdapter
import vn.icheck.android.screen.user.wall.manage_page.my_owner_page.MyOwnerPageActivity
import vn.icheck.android.screen.user.wall.manage_page.my_owner_page.MyOwnerPageAdapter
import vn.icheck.android.util.kotlin.ActivityUtils

class PageManagementFragment : Fragment() {
    private var _binding: FragmentPageManagementBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: PageManagementViewModel
    lateinit var followAdaper: MyFollowPageAdapter
    lateinit var ownerAdaper: MyOwnerPageAdapter

    private val requestChangeFollow = 1
    private var pageFollowCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageManagementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(PageManagementViewModel::class.java)

        DialogHelper.showLoading(this)
        initView()
        initSwipeLayout()
        initRecyclerview()

        EventBus.getDefault().register(this)

        getData()
        return binding.root
    }

    private fun initView() {
        binding.tvFollowAll.setOnClickListener {
            activity?.let { activity ->
                val intent = Intent(activity, MyFollowPageActivity::class.java)
                startActivityForResult(intent, requestChangeFollow)
            }
        }

        binding.tvOwnerAll.setOnClickListener {
            activity?.let { activity ->
                ActivityUtils.startActivity<MyOwnerPageActivity>(activity)
            }
        }
    }

    private fun initSwipeLayout() {
        val swipeColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(requireContext())
        binding.swipeLayout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)
        binding.swipeLayout.setOnRefreshListener {
            getData()
        }
    }

    private fun initRecyclerview() {
        ownerAdaper = MyOwnerPageAdapter(true)
        binding.rcvOwner.adapter = ownerAdaper

        followAdaper = MyFollowPageAdapter(true)
        binding.rcvFollow.adapter = followAdaper
    }

    private fun getData() {
        if (NetworkHelper.isNotConnected(context)) {
            setError(ICError(R.drawable.ic_error_network, ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        var countError = 0

        lifecycleScope.launch {
            var myFollowPage: ICResponse<ICListResponse<ICPage>>? = null
            var myOwnerPage: ICResponse<ICListResponse<ICPage>>? = null

            listOf(
                    lifecycleScope.async {
                        try {
                            myFollowPage = withTimeoutOrNull(5000) { viewModel.getMyFollowPage() }
                        } catch (e: Exception) {
                            countError++
                        }
                    },
                    lifecycleScope.async {
                        try {
                            myOwnerPage = withTimeoutOrNull(5000) { viewModel.getMyOwnerPage() }
                        } catch (e: Exception) {
                            countError++
                        }
                    }
            ).awaitAll()

            DialogHelper.closeLoading(this@PageManagementFragment)
            binding.swipeLayout.isRefreshing = false

            if (countError >= 2) {
                setError(ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            } else {
                if (!myFollowPage?.data?.rows.isNullOrEmpty() || !myOwnerPage?.data?.rows.isNullOrEmpty()) {
                    setFollowPage(myFollowPage?.data)
                    setOwnerPage(myOwnerPage?.data)
                } else {
                    setError(ICError(R.drawable.ic_group_120dp, ICheckApplication.getString(R.string.ban_chua_co_trang_nao)))
                }
            }
        }
    }

    private fun getFollowPage() {
        var myFollowPage: ICResponse<ICListResponse<ICPage>>? = null

        lifecycleScope.launch {
            delay(1000)
            withContext(lifecycleScope.coroutineContext) {
                try {
                    myFollowPage = withTimeoutOrNull(5000) { viewModel.getMyFollowPage() }
                } catch (e: Exception) {
                }
            }
            if (myFollowPage?.data?.rows.isNullOrEmpty() && binding.containerOwner.isGone) {
                setError(ICError(R.drawable.ic_group_120dp, ICheckApplication.getString(R.string.ban_chua_co_trang_nao)))
            } else {
                setFollowPage(myFollowPage?.data)
                binding.layoutMessage.containerMessage.beGone()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setFollowPage(it: ICListResponse<ICPage>?) {
        if (it?.rows.isNullOrEmpty()) {
            binding.containerFollow.beGone()
        } else {
            binding.containerFollow.beVisible()
            pageFollowCount = it?.count ?: 0
            binding.containerFollow.beVisible()
            binding.tvFollowTitle.text = "Trang đang theo dõi (${pageFollowCount})"
            followAdaper.setListData(it!!.rows)
        }
    }

    private fun setOwnerPage(it: ICListResponse<ICPage>?) {
        if (it?.rows.isNullOrEmpty()) {
            binding.containerOwner.beGone()
        } else {
            binding.containerOwner.beVisible()
            binding.tvOwnerTitle.text = "Trang của tôi (${it?.count})"
            ownerAdaper.setListData(it!!.rows)
        }
    }

    private fun setError(data: ICError) {
        binding.layoutMessage.containerMessage.beVisible()
        binding.layoutMessage.containerMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_e5))
        binding.layoutMessage.imgIcon.setImageResource(data.icon)
        binding.layoutMessage.txtMessage.text = data.message

        if (data.message == requireContext().getString(R.string.ban_chua_co_trang_nao)) {
            binding.layoutMessage.btnTryAgain.beGone()
        } else {
            binding.layoutMessage.btnTryAgain.beVisible()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestChangeFollow) {
            if (resultCode == Activity.RESULT_OK) {
                getFollowPage()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.UNFOLLOW_PAGE, ICMessageEvent.Type.FOLLOW_PAGE -> {
                if (event.data != null && event.data is Long) {
                    getFollowPage()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        _binding = null
    }
}