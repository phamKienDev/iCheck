package vn.icheck.android.screen.user.wall.mainuser

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_user_wall.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.base.dialog.reward_login.RewardLoginCallback
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.post.IPostListener
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.FragmentUserWallBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.network.model.ApiErrorResponse
import vn.icheck.android.network.model.ApiSuccessResponse
import vn.icheck.android.network.model.posts.PostViewModel
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICFriendInvitationMeUserId
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.dialog.report.ReportDialog
import vn.icheck.android.screen.scan.V6ScanditActivity
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.commentpost.CommentPostActivity
import vn.icheck.android.screen.user.createpost.CreateOrUpdatePostActivity
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.list_friend_in_wall.ListFriendOfWallActivity
import vn.icheck.android.screen.user.listnotification.ListNotificationActivity
import vn.icheck.android.screen.user.listnotification.friendrequest.ListFriendRequestActivity
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.dialog.report.ReportSuccessDialog
import vn.icheck.android.screen.user.wall.EDIT_MY_PUBLIC_INFO
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.screen.user.wall.OPEN_INFOR
import vn.icheck.android.screen.user.wall.USER_ID
import vn.icheck.android.screen.user.wall.friend_wall_setting.FriendWallSettingsDialog
import vn.icheck.android.util.ick.*

class IckUserWallFragment : Fragment(), IPostListener,IMessageListener {
    private var _binding: FragmentUserWallBinding? = null
    private var isActivityVisble = false
    private var requestLogin = 11

    private val binding get() = _binding!!
    private var showToolbar = false
    private val ickUserWallViewModel: IckUserWallViewModel by activityViewModels()
    private lateinit var ickUserWallAdapter: IckUserWallAdapter
    private val eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == USER_WALL_BROADCAST) {
                when (intent.getIntExtra(USER_WALL_BROADCAST, 0)) {
                    USER_WALL_EVENT_SETTING -> {
                        val action = IckUserWallFragmentDirections.actionIckUserWallFragmentToUserWallSettingFragment()
                        findNavController().navigate(action)
                    }
                    USER_WALL_PRIVACY_SETTING -> {
                        hideBottomBar()
                        val action = IckUserWallFragmentDirections.actionIckUserWallFragmentToPrivacySettingFragment()
                        findNavController().navigate(action)
                    }
                    USER_WALL_EDIT_PERSONAL -> {
                        hideBottomBar()
                        val action = IckUserWallFragmentDirections.actionIckUserWallFragmentToEditMyInformationActivity()
                        findNavController().navigate(action)
                    }
                    USER_WALL_EDIT_PASSWORD -> {
                        hideBottomBar()
                        val action = IckUserWallFragmentDirections.actionIckUserWallFragmentToIckNewPwFragment()
                        findNavController().navigate(action)
                    }
                    USER_WALL_CREATE_POST -> {
                        createPost()
                    }
                    USER_WALL_OPEN_SCAN -> {
                        if (ContextCompat.checkSelfPermission(
                                        requireActivity(),
                                        Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                requestPermissions(arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                            } else {
                                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                            }
                        } else {
                            V6ScanditActivity.reviewOnly(requireActivity())
                        }
                    }
                    EDIT_POST -> {
                        hideBottomBar()
                        val pos = intent.getIntExtra(POSITION, -1)
                        val postId = ickUserWallViewModel.getPostAt(pos)?.postData?.id
                        ickUserWallViewModel.updatePostAtPos = pos
                        when {
                            intent.getIntExtra(Constant.DATA_1, -1) != -1 -> {
                                ickUserWallViewModel.getPostAt(pos)?.postData?.let { MediaInPostActivity.start(it, this@IckUserWallFragment, intent.getIntExtra(Constant.DATA_1, -1), EDIT_POST) }
                            }
                            ickUserWallViewModel.getPostAt(pos)?.postData?.involveType == "review" -> {
                                val i = Intent(requireActivity(), EditReviewActivity::class.java)
                                i.putExtra(Constant.DATA_1, ickUserWallViewModel.getPostAt(pos)?.postData?.meta?.product?.id)
                                i.putExtra("wall", true)
                                startActivityForResult(i, EDIT_POST)
                            }
                            else -> {
                                val i = Intent(requireActivity(), CreateOrUpdatePostActivity::class.java)
                                i.putExtra(Constant.DATA_1, postId)
                                startActivityForResult(i, EDIT_POST)
                            }
                        }
                    }
                    USER_WALL_FRIEND_SETTINGS -> {
                        FriendWallSettingsDialog(ickUserWallViewModel, viewLifecycleOwner).show(requireActivity().supportFragmentManager, null)
                    }
                    USER_WALL_ADD_FRIEND -> {
                        if (SessionManager.isUserLogged) {
                            ickUserWallViewModel.addFriend().observe(viewLifecycleOwner) {
                                if (ickUserWallViewModel.userInfo?.data?.id != null) {
                                    AppDatabase.getDatabase(requireContext()).friendInvitationMeUserIdDao().insertFriendInvitationMeUserID(ICFriendInvitationMeUserId(ickUserWallViewModel.userInfo?.data?.id!!))
                                }
                            }
                        } else {
                            requireActivity().showLogin()
                        }
                    }
                    USER_WALL_ACCEPT_FRIEND -> {
                        if (SessionManager.isUserLogged) {
                            val interaction = RelationshipInteractor()
                            interaction.updateFriendInvitation(ickUserWallViewModel.id, Constant.FRIEND_REQUEST_ACCEPTED, object : ICNewApiListener<ICResponse<Boolean>> {
                                override fun onSuccess(obj: ICResponse<Boolean>) {
                                    binding.root.isRefreshing = true
                                    ickUserWallViewModel.reachedEnd = false
                                    ickUserWallViewModel.currentOffsetPost = 10
                                    getLayout()
                                    RelationshipManager.removeFriendInvitationMe(ickUserWallViewModel.id)
                                }

                                override fun onError(error: ICResponseCode?) {
                                    requireContext().showShortErrorToast(error?.message ?: requireContext().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                                }
                            })
                        } else {
                            requireActivity().showLogin()
                        }
                    }
                    USER_WALL_SHOW_PUBLIC_INFO -> {
                        hideBottomBar()
                        findNavController().navigate(IckUserWallFragmentDirections.actionIckUserWallFragmentToPublicInfoFragment())
                    }
                    SHOW_LIST_FRIEND -> {
                        val i = Intent(requireContext(), ListFriendOfWallActivity::class.java)
                        i.putExtra(USER_ID, ickUserWallViewModel.userInfo?.data?.id)
                        startActivityForResult(i, SHOW_LIST_FRIEND)
                    }
                    SHOW_LIST_MUTUAL_FRIEND -> {
                        val i = Intent(requireContext(), ListFriendOfWallActivity::class.java)
                        i.putExtra(USER_ID, ickUserWallViewModel.userInfo?.data?.id)
                        i.putExtra(Constant.DATA_1, SHOW_LIST_MUTUAL_FRIEND)
                        i.putExtra(Constant.DATA_2, ickUserWallViewModel.userInfo?.data?.id)
                        startActivityForResult(i, SHOW_LIST_FRIEND)
                    }
                    SHOW_DETAIL_POST -> {
                        hideBottomBar()
                        val obj = intent.getSerializableExtra(Constant.DATA_1)
                        if (obj != null && obj is ICPost) {
                            val i = Intent(requireActivity(), DetailPostActivity::class.java)
                            i.putExtra(Constant.DATA_1, obj)
                            startActivityForResult(i, SHOW_DETAIL_POST)
                            val pos = intent.getIntExtra(POSITION, -1)
                            ickUserWallViewModel.updatePostAtPos = pos
                        }
                    }
                    SHOW_LIST_INVITATION -> {
                        val i = Intent(requireActivity(), ListFriendRequestActivity::class.java)
                        startActivityForResult(i, SHOW_LIST_INVITATION)
                    }
                    SHOW_COMMENT_POST -> {
                        val pos = intent.getIntExtra(POSITION, -1)
                        val post = ickUserWallViewModel.getPostAt(pos)?.postData
                        ickUserWallViewModel.updatePostAtPos = pos
                        val i = Intent(requireActivity(), CommentPostActivity::class.java)
                        i.putExtra(Constant.DATA_1, post)

                        i.putExtra(Constant.DATA_2, 1)
                        startActivityForResult(i, SHOW_COMMENT_POST)
                    }
                }
            }
        }
    }

    private fun createPost(showMedia: Boolean? = false) {
        hideBottomBar()
        val i = Intent(requireActivity(), CreateOrUpdatePostActivity::class.java)
        i.putExtra(Constant.DATA_3, showMedia)
        startActivityForResult(i, USER_WALL_CREATE_POST)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if (ickUserWallViewModel.id==SessionManager.session.user?.id || ickUserWallViewModel.id==-1L) {
            showBottomBar()
        }else{
            hideBottomBar()
        }
        val intentFilter = IntentFilter(USER_WALL_BROADCAST)
        requireActivity().registerReceiver(eventReceiver, intentFilter)
        setNotify()
        isActivityVisble = true
    }

    private fun showBottomBar() {
        ickUserWallViewModel.showBottomBar.postValue(true)
    }

    private fun hideBottomBar() {
        ickUserWallViewModel.showBottomBar.postValue(false)
    }

    override fun onPause() {
        super.onPause()
        isActivityVisble = false
        requireActivity().unregisterReceiver(eventReceiver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        binding.root.isRefreshing = true
        val swipeColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(requireContext())
        binding.root.setColorSchemeColors(swipeColor, swipeColor, swipeColor)
        binding.root.setOnRefreshListener {
            binding.root.isRefreshing = true
            ickUserWallViewModel.reachedEnd = false
            ickUserWallViewModel.currentOffsetPost = 10
            getLayout()
        }
        binding.btnBack.setOnClickListener {
            activity?.finish()
        }

        binding.tvNotificationCount.background= ViewHelper.bgRedCircle22dp(requireContext())
        ickUserWallAdapter = IckUserWallAdapter(this,this)
        binding.rcvIckUserWall.adapter = ickUserWallAdapter
        binding.rcvIckUserWall.layoutManager = WrapContentLinearLayoutManager(requireContext())

        binding.rcvIckUserWall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = binding.rcvIckUserWall.layoutManager!! as LinearLayoutManager
                val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()

                if (!binding.rcvIckUserWall.canScrollVertically(1) && ickUserWallViewModel.updatePost == 0 && ickUserWallViewModel.arrPost.size < ickUserWallViewModel.totalPost && !ickUserWallViewModel.reachedEnd) {
                    ickUserWallViewModel.currentOffsetPost += ickUserWallViewModel.currentLimitPost
                    ickUserWallViewModel.getPosts()
                }
                if (pastVisibleItems > 1) {
                    if (!showToolbar) {
                        binding.toolbar.title simpleText ickUserWallViewModel.userInfo?.data?.createICUser()?.getName
                        binding.toolbar.setBackgroundColor(vn.icheck.android.ichecklibs.ColorManager.getAppBackgroundWhiteColor(requireContext()))
                        binding.toolbar.btn_back.fillDrawableColor(R.drawable.ic_back_blue_24px_new)

                        if (ickUserWallViewModel.userInfo?.data?.id!=SessionManager.session.user?.id) {
                            binding.notify.fillDrawableColor(R.drawable.ic_home_blue_v2_24px)
                        } else {
                            binding.notify.fillDrawableColor(R.drawable.ic_homenoti_empty_blue_24px)
                        }
                        binding.titleDiv.beVisible()
                        if (ickUserWallViewModel.userInfo?.data?.id ==SessionManager.session.user?.id) {
                            binding.tvNewImage.beVisible()
                            binding.verticalDiv.beVisible()
                            binding.tvNewPost.beVisible()
                        }
                        showToolbar = true
                    }
                } else {
                    showToolbar = false
                    binding.toolbar.title simpleText ""
                    binding.toolbar.background = ColorDrawable(Color.TRANSPARENT)
                    binding.toolbar.btn_back.setImageResource(R.drawable.ic_back_black_28px)
                    binding.titleDiv.beGone()
                    if (ickUserWallViewModel.userInfo?.data?.id!=SessionManager.session.user?.id) {
                        binding.notify.setImageResource(R.drawable.ic_home_black_28px)
                    } else {
                        binding.notify.setImageResource(R.drawable.ic_noti_black_28dp)
                    }
                    binding.tvNewImage.beGone()
                    binding.verticalDiv.beGone()
                    binding.tvNewPost.beGone()
                }
            }
        })
        ickUserWallViewModel.listViewModel.observe(viewLifecycleOwner, {
            binding.root.isRefreshing = false
            if (!ickUserWallViewModel.initActivity && ickUserWallViewModel.userInfo != null) {
                if (requireActivity().intent.getBooleanExtra(OPEN_INFOR, false)) {
                    requireActivity().sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, USER_WALL_EDIT_PERSONAL)
                    })
                    ickUserWallViewModel.initActivity = true
                }
            }
            if (ickUserWallViewModel.updatePost == 0) {
                ickUserWallAdapter.updateList(it)
            } else {
                ickUserWallViewModel.updatePost = 0
                ickUserWallAdapter.addPosts(ickUserWallViewModel.arrPost)
            }
            setNotify()
        })


        ickUserWallViewModel.reportUserCategory.observe(viewLifecycleOwner) {
            val reportDialog = ReportDialog(it.rows, R.string.bao_cao_nguoi_dung_nay, R.string.mo_ta_noi_dung_bao_cao)
            reportDialog.show(childFragmentManager, this.tag)

            reportDialog.setListener(object : ReportDialog.DialogClickListener {
                override fun buttonClick(listReasonId: MutableList<Int>, message: String, listReasonContent: MutableList<String>) {
                    ickUserWallViewModel.sendReportUser(listReasonId, message).observe(viewLifecycleOwner, {
                        reportDialog.dismiss()
                        val reportSuccessDialog=ReportSuccessDialog(requireContext())

                        val listReportSuccess = mutableListOf<ICReportForm>()
                        for (item in listReasonContent) {
                            listReportSuccess.add(ICReportForm(null, item))
                        }
                        if (message.isNotEmpty()) {
                            listReportSuccess.add(ICReportForm(null, message))
                        }
                        reportSuccessDialog.show(listReportSuccess)
                    })
                }
            })
        }
        ickUserWallViewModel.postsLiveData.observe(viewLifecycleOwner) {
            if (it is ApiSuccessResponse) {
                if (it.body.data?.rows != null) {

                    for (item in it.body.data?.rows ?: arrayListOf()) {
                        val icViewModel = PostViewModel(item)
                        ickUserWallViewModel.arrPost.add(icViewModel)
                        ickUserWallViewModel.addView(icViewModel)
                    }
                    ickUserWallViewModel.finishAddView()
                } else {
                    ickUserWallViewModel.reachedEnd = true
                }
            } else if (it is ApiErrorResponse) {
                logError(it.error)
            }
        }
        ickUserWallViewModel.mErr.observe(viewLifecycleOwner) {
            if (ickUserWallAdapter.listData.size>1) {
                requireContext() showShortErrorToast it
            }else{
                ickUserWallAdapter.setError(R.drawable.ic_error_request,it)
            }
        }
        binding.tvNewPost.setOnClickListener {
            createPost()
        }
        binding.tvNewImage.setOnClickListener {
            createPost(true)
        }
        binding.notify.setOnClickListener {
            if (ickUserWallViewModel.userInfo?.data?.id!=SessionManager.session.user?.id) {
                requireActivity().startClearTopActivity(HomeActivity::class.java)
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
            } else {
                requireActivity() simpleStartActivity ListNotificationActivity::class.java
            }
        }
        getLayout()
        ickUserWallViewModel.updateUser.observe(viewLifecycleOwner) {
            requireContext().sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_EDIT_PERSONAL)
            })
        }
    }


    private fun setNotify() {
        if (ickUserWallViewModel.userInfo?.data?.id!=SessionManager.session.user?.id) {
            binding.notify.setImageResource(R.drawable.ic_home_black_28px)
            binding.tvNotificationCount.beInvisible()
        } else {
            when {
                RelationshipManager.unreadNotify > 9 -> {
                    binding.tvNotificationCount.beVisible()
                    binding.tvNotificationCount.setText(R.string.count_9)
                }
                RelationshipManager.unreadNotify > 0 -> {
                    binding.tvNotificationCount.beVisible()
                    binding.tvNotificationCount.text = RelationshipManager.unreadNotify.toString()
                }
                else -> binding.tvNotificationCount.beInvisible()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.FRIEND_LIST_UPDATE -> {
                if (event.data as Int == RelationshipManager.FRIEND_LIST_UPDATE) {
                    getLayout()
                }
            }
            ICMessageEvent.Type.SHOW_FULL_MEDIA -> {
                if (isActivityVisble) {
                    if (event.data != null) {
                        ICheckApplication.currentActivity()?.let { activity ->
                            DetailMediaActivity.start(activity, event.data as List<ICMedia>)
                        }
                    }
                }
            }
            ICMessageEvent.Type.DELETE_DETAIL_POST -> {
                if (event.data != null && event.data is Long) {
                    ickUserWallAdapter.deletePost(event.data)
                }
            }
            ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION -> {
                setNotify()
            }
            ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                if (isActivityVisble) {
                    ICheckApplication.currentActivity()?.let { activity ->
                        RewardLoginDialog.show((activity as AppCompatActivity).supportFragmentManager, object : RewardLoginCallback {
                            override fun onLogin() {
                                val intent = Intent(context, IckLoginActivity::class.java)
                                startActivityForResult(intent, requestLogin)
                            }

                            override fun onRegister() {
                                val intent = Intent(context, IckLoginActivity::class.java)
                                intent.putExtra(Constant.DATA_1, Constant.REGISTER_TYPE)
                                startActivityForResult(intent, requestLogin)
                            }

                            override fun onDismiss() {

                            }
                        })
                    }
                }
            }
            ICMessageEvent.Type.UNFRIEND -> {
                if (isActivityVisble) {
                    requireContext().showShortSuccessToast(getString(R.string.ban_da_huy_ket_ban_voi_s, ickUserWallViewModel.userInfo?.data?.getName()))
                }
            }
            ICMessageEvent.Type.ERROR_SERVER -> {
                if (isActivityVisble) {
                    requireContext().showShortErrorToast(null)
                }
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                USER_WALL_CREATE_POST -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post is ICPost && ickUserWallViewModel.posCreatePost != 0) {
                        val icViewModel = PostViewModel(post)
                        ickUserWallViewModel.addView(ickUserWallViewModel.posCreatePost + 1, icViewModel)
                        ickUserWallAdapter.addPost(ickUserWallViewModel.posCreatePost + 1, icViewModel)
                        requireContext().showShortSuccessToast(getString(R.string.ban_da_tao_bai_viet_thanh_cong))
                    }
                }
                EDIT_MY_PUBLIC_INFO -> {
                    lifecycleScope.launchWhenResumed {
                        requireContext().sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                            putExtra(USER_WALL_BROADCAST, USER_WALL_EDIT_PERSONAL)
                        })
                    }
                }
                SHOW_DETAIL_POST, EDIT_POST, SHOW_COMMENT_POST -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post is ICPost) {
                        val icViewModel = PostViewModel(post)
                        ickUserWallViewModel.updatePost(icViewModel)
                        ickUserWallAdapter.updatePost(ickUserWallViewModel.updatePostAtPos, icViewModel)
                    }
                }
                SHOW_LIST_FRIEND, SHOW_LIST_INVITATION, SCAN_REVIEW -> {
                    ickUserWallViewModel.reachedEnd = false
                    ickUserWallViewModel.currentOffsetPost = 10
                    getLayout()
                }
                requestLogin -> {
                    getLayout()
                }
            }
        }
    }

    private fun getLayout() {
        if (NetworkHelper.isNotConnected(requireContext())) {
            binding.root.isRefreshing = false
            if (ickUserWallAdapter.listData.size>1) {
                requireContext() showShortErrorToast requireContext().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            }else{
                ickUserWallAdapter.setError(R.drawable.ic_error_network,requireContext().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            }
        }else{
            ickUserWallViewModel.getLayout().observe(viewLifecycleOwner) {
                if (it != null) {
                    ickUserWallViewModel.initLayout(it)
                }
            }
        }
    }

    override fun onEditPost(obj: ICPost) {
        ICheckApplication.currentActivity()?.let {
            if (obj.customerCriteria.isNullOrEmpty()) {
                val intent = Intent(it, CreateOrUpdatePostActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.id)
                intent.putExtra(Constant.DATA_2, obj.page?.id)
                intent.putExtra(Constant.DATA_3, obj.page?.name)
                intent.putExtra(Constant.DATA_4, obj.page?.avatar)
                intent.putExtra(Constant.DATA_5, obj.page?.isVerify)
                it.startActivity(intent)
            } else {
                val intent = Intent(it, EditReviewActivity::class.java)
                if (obj.targetId != null) {
                    intent.putExtra(Constant.DATA_1, obj.targetId ?: 0L)
                } else {
                    intent.putExtra(Constant.DATA_1, obj.meta?.product?.id)
                }
                it.startActivity(intent)
            }
        }
    }

    override fun followAndUnFollowPage(obj: ICPost) {
    }

    override fun onDeletePost(id: Long) {
        ickUserWallViewModel.deletePost(id).observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.data?.id != null) {
                    requireContext().showShortSuccessToast(getString(R.string.ban_da_xoa_bai_viet_thanh_cong))
                    ickUserWallAdapter.deletePost(it.data?.id!!)
                }
            }
        }
    }

    override fun onMessageClicked() {
        binding.root.isRefreshing=true
        getLayout()
    }

}