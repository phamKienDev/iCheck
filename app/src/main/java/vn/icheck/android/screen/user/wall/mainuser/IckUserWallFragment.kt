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
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.post.IPostListener
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.FragmentUserWallBinding
import vn.icheck.android.model.ApiErrorResponse
import vn.icheck.android.model.ApiSuccessResponse
import vn.icheck.android.model.posts.PostViewModel
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICFriendInvitationMeUserId
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.scan.V6ScanditActivity
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
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionSuccessDialog
import vn.icheck.android.screen.user.wall.EDIT_MY_PUBLIC_INFO
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.screen.user.wall.OPEN_INFOR
import vn.icheck.android.screen.user.wall.USER_ID
import vn.icheck.android.screen.user.wall.friend_wall_setting.FriendWallSettingsDialog
import vn.icheck.android.screen.user.wall.report_user.ReportUserDialog
import vn.icheck.android.util.checkTypeUser
import vn.icheck.android.util.ick.*

class IckUserWallFragment : Fragment(), IPostListener {
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
//                        val i = Intent(requireContext(), ICKScanActivity::class.java)
//                        requireActivity().finish()
//                        startActivity(i)
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
                            val i = Intent(context, V6ScanditActivity::class.java)
                            i.putExtra("review_only", true)
                            startActivityForResult(i, SCAN_REVIEW)
                        }

//                        ICKScanActivity.reviewOnly(requireActivity())
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

    private fun hideBottomBar() {
        ickUserWallViewModel.showBottomBar.postValue(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        showBottomBar()
        val intentFilter = IntentFilter(USER_WALL_BROADCAST)
        requireActivity().registerReceiver(eventReceiver, intentFilter)
        setNotify()
        isActivityVisble = true
    }

    private fun showBottomBar() {
        ickUserWallViewModel.showBottomBar.postValue(true)
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
        binding.root.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.blue), ContextCompat.getColor(requireContext(), R.color.blue), ContextCompat.getColor(requireContext(), R.color.lightBlue))
        binding.root.setOnRefreshListener {
            binding.root.isRefreshing = true
            ickUserWallViewModel.reachedEnd = false
            ickUserWallViewModel.currentOffsetPost = 10
            getLayout()
        }
        binding.btnBack.setOnClickListener {
            activity?.finish()
        }
        ickUserWallAdapter = IckUserWallAdapter(this)
        binding.rcvIckUserWall.adapter = ickUserWallAdapter
        binding.rcvIckUserWall.layoutManager = WrapContentLinearLayoutManager(requireContext())

        binding.rcvIckUserWall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = binding.rcvIckUserWall.layoutManager!!.itemCount
                val linearLayoutManager = binding.rcvIckUserWall.layoutManager!! as LinearLayoutManager
                val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()

                if (!binding.rcvIckUserWall.canScrollVertically(1) && ickUserWallViewModel.updatePost == 0 && ickUserWallViewModel.arrPost.size < ickUserWallViewModel.totalPost && !ickUserWallViewModel.reachedEnd) {
                    ickUserWallViewModel.currentOffsetPost += ickUserWallViewModel.currentLimitPost
                    ickUserWallViewModel.getPosts()
                }
                if (pastVisibleItems > 1) {
                    if (!showToolbar) {
                        binding.toolbar.title simpleText ickUserWallViewModel.userInfo?.data?.createICUser()?.getName
                        binding.toolbar.background = ColorDrawable(Color.WHITE)
                        binding.toolbar.btn_back.setImageResource(R.drawable.ic_back_blue_24px_new)
                        if (checkTypeUser(ickUserWallViewModel.userInfo?.data?.id) != MAIN_USER) {
                            binding.notify.setImageResource(R.drawable.ic_home_blue_v2_24px)
                        } else {
                            binding.notify.setImageResource(R.drawable.ic_homenoti_empty_blue_24px)
                        }
                        binding.titleDiv.beVisible()
                        binding.tvNewImage.beVisible()
                        binding.verticalDiv.beVisible()
                        binding.tvNewPost.beVisible()
                        showToolbar = true
                    }
                } else {
                    showToolbar = false
                    binding.toolbar.title simpleText ""
                    binding.toolbar.background = ColorDrawable(Color.TRANSPARENT)
                    binding.toolbar.btn_back.setImageResource(R.drawable.ic_back_black_28px)
                    binding.titleDiv.beGone()
                    if (checkTypeUser(ickUserWallViewModel.userInfo?.data?.id) != MAIN_USER) {
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
//                if (ickUserWallViewModel.arrPost.size < ickUserWallViewModel.totalPost) {
//                    ickUserWallAdapter.addPosts(ickUserWallViewModel.arrPost)
//                } else {
//                    ickUserWallViewModel.updatePost = 0
//                    if (it.size < ickUserWallViewModel.totalPost + 4) {
//                        ickUserWallAdapter.updateList(it)
//                    }
//                }
            } else {
                ickUserWallViewModel.updatePost = 0
                ickUserWallAdapter.addPosts(ickUserWallViewModel.arrPost)

//                if (it.size < ickUserWallViewModel.totalPost + 4) {
//                    ickUserWallAdapter.updateList(it)
//                }
            }
            setNotify()
        })

        ickUserWallViewModel.reportUserCategory.observe(viewLifecycleOwner) {
            ickUserWallViewModel.reportCategory = it
            ReportUserDialog().show(childFragmentManager, null)
        }
        ickUserWallViewModel.postsLiveData.observe(viewLifecycleOwner) {
            if (it is ApiSuccessResponse) {
                if (it.body.data?.rows != null) {

                    for (item in it.body.data.rows) {
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
            requireContext() showShortError it
        }
        ickUserWallViewModel.showSuccessReport.observe(viewLifecycleOwner, Observer {
//            ReportUserSuccessDialog().show(childFragmentManager, null)
            val listReason = ickUserWallViewModel.arrReport.filter { it.checked }.map { ICReportForm(null, it.data?.name) }.toMutableList()
            ickUserWallViewModel.arrReport.lastOrNull()?.content?.let { content ->
                if (content.isNotEmpty()) {
                    listReason.add(ICReportForm(null, content))
                    val ortherIndex = listReason.indexOfFirst {
                        it.name == "Khác" || it.name == "Lý do khác"
                    }
                    if (ortherIndex != -1)
                        listReason.removeAt(ortherIndex)
                }
            }

            ReportWrongContributionSuccessDialog(requireContext()).apply {
                show(listReason)
            }
        })
        binding.tvNewPost.setOnClickListener {
            createPost()
        }
        binding.tvNewImage.setOnClickListener {
            createPost(true)
        }
        binding.notify.setOnClickListener {
            if (checkTypeUser(ickUserWallViewModel.userInfo?.data?.id) != MAIN_USER) {
                requireActivity().startClearTopActivity(HomeActivity::class.java)
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
//        if (ickUserWallViewModel.userInfo?.data?.id == SessionManager.session.user?.id) {
//            RelationshipManager.getFriendList().observe(viewLifecycleOwner, {
//                if (it.size != ickUserWallViewModel.totalFriend) {
//                    ickUserWallViewModel.updateFriendList()
//                    ickUserWallAdapter.notifyFriendList()
//                }
//            })
//        }
    }


    private fun setNotify() {
        if (checkTypeUser(ickUserWallViewModel.userInfo?.data?.id) != MAIN_USER) {
            binding.notify.setImageResource(R.drawable.ic_home_black_28px)
            binding.tvNotificationCount.beInvisible()
        } else {
            when {
                RelationshipManager.unreadNotify > 9 -> {
                    binding.tvNotificationCount.beVisible()
                    binding.tvNotificationCount.setText("9+")
                }
                RelationshipManager.unreadNotify > 0 -> {
                    binding.tvNotificationCount.beVisible()
                    binding.tvNotificationCount.setText(RelationshipManager.unreadNotify.toString())
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
//                    ickUserWallAdapter.notifyFriendList()
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
                        object : RewardLoginDialog(activity) {
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
                        }.show()
                    }
                }
            }
            ICMessageEvent.Type.UNFRIEND -> {
                if (isActivityVisble) {
                    requireContext().showSimpleSuccessToast("Bạn đã hủy kết bạn với ${ickUserWallViewModel.userInfo?.data?.getName()}")
                }
            }
            ICMessageEvent.Type.ERROR_SERVER -> {
                if (isActivityVisble) {
                    requireContext().showSimpleErrorToast(null)
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
                        requireContext().showSimpleSuccessToast("Bạn đã tạo bài viết thành công!")
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
        ickUserWallViewModel.getLayout().observe(viewLifecycleOwner) {
            if (it != null) {
                ickUserWallViewModel.initLayout(it)
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
                    requireContext().showSimpleSuccessToast(getString(R.string.ban_da_xoa_bai_viet_thanh_cong))
                    ickUserWallAdapter.deletePost(it.data?.id!!)
                }
            }
        }
    }

}