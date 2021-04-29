package vn.icheck.android.screen.user.page_details.fragment.page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_page_detail.*
import kotlinx.android.synthetic.main.item_header_infor_page.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.reward_login.RewardLoginCallback
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialogV2
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.header_page.bottom_sheet_header_page.IListReportView
import vn.icheck.android.component.image_video_slider.ICImageVideoSliderModel
import vn.icheck.android.component.image_video_slider.ICMediaType
import vn.icheck.android.component.image_video_slider.MediaLogic
import vn.icheck.android.component.post.IPostListener
import vn.icheck.android.component.view.ViewHelper.setScrollSpeed
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICMediaPage
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.createpost.CreateOrUpdatePostActivity
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.page_details.PageDetailViewModel
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionDialog
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionSuccessDialog
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.startClearTopActivity
import vn.icheck.android.util.ick.toPx
import vn.icheck.android.util.ick.visibleOrGone
import java.io.File

class PageDetailFragment : BaseFragmentMVVM(), IRecyclerViewCallback, IListReportView, IPostListener {
    private val viewModel: PageDetailViewModel by activityViewModels()
    private var adapter = PageDetailAdapter(this, this, this)

    private var listContributionReport = mutableListOf<ICReportForm>()
    private lateinit var dialog: ReportWrongContributionDialog

    private var typeEditImage: Int? = null
    private var pageOverViewPosition = -1
    private var companyViewInsider = true
    private val requestPermissionImage = 1
    private val requireLogin = 2
    private var isActivityVisible = false

    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            viewModel.uploadImage(typeEditImage!!, file)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {}
        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            file?.let { viewModel.uploadImage(typeEditImage!!, it) }
        }
    }

    companion object {
        fun newInstance(pageID: Long, pageType: Int): PageDetailFragment {
            val fragment = PageDetailFragment()

            val bundle = Bundle()
            bundle.putLong(Constant.DATA_1, pageID)
            bundle.putInt(Constant.DATA_2, pageType)
            fragment.arguments = bundle

            return fragment
        }
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_page_detail

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSwipeLayout()
        setupViewModel()
    }

    private fun setupToolbar() {
        layoutToolbar.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)
        layoutToolbarAlpha.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)

        imgBack.setImageResource(R.drawable.ic_back_blue_v2_24px)
        imgAction.setImageResource(R.drawable.ic_home_blue_v2_24px)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        imgAction.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
            requireContext().startClearTopActivity(HomeActivity::class.java)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setScrollSpeed()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val viewHolder = recyclerView.findViewHolderForAdapterPosition(pageOverViewPosition)?.itemView
                if (viewHolder != null) {
                    val containerOverviewHeight = viewHolder.containerOverview.height + 48

                    if ((viewHolder.y + viewHolder.height - containerOverviewHeight) <= layoutToolbarAlpha.height) {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.HIDE_OR_SHOW_FOLLOW, true))
                    } else {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.HIDE_OR_SHOW_FOLLOW, false))
                    }
                } else {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.HIDE_OR_SHOW_FOLLOW, true))
                }


                val scrollPosition = recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.y
                if (scrollPosition != null && scrollPosition > -layoutToolbarAlpha.height) {
                    val alpha = (1f / -layoutToolbarAlpha.height) * scrollPosition
                    layoutToolbarAlpha.alpha = alpha
                    viewShadow.alpha = alpha
                } else {
                    layoutToolbarAlpha.alpha = 1f
                    viewShadow.alpha = 1f
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, layoutToolbarAlpha.height)
                }
            }
        })
    }

    private fun setupSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorSecondary), ContextCompat.getColor(requireContext(), R.color.colorSecondary), ContextCompat.getColor(requireContext(), R.color.colorPrimary))

        swipeLayout.setOnRefreshListener {
            getData()
        }
    }

    private fun setupViewModel() {
        viewModel.getData(arguments)

        viewModel.onClearData.observe(viewLifecycleOwner, {
            swipeLayout.isRefreshing = true
            adapter.resetData(true)
            viewModel.offsetPost = 0
        })

        viewModel.onAddData.observe(viewLifecycleOwner, {
            adapter.addItem(it)
            closeLoading()
            updateOverviewPosition()

            if (it.viewType == ICViewTypes.HEADER_INFOR_PAGE) {
                if (companyViewInsider) {
                    viewModel.pageOverview?.let { page ->
                        TrackingAllHelper.trackCompanyView(page)
                    }
                    companyViewInsider = false
                }
            }
        })

        viewModel.onUpdateData.observe(viewLifecycleOwner, {
            adapter.updateItem(it)
            updateOverviewPosition()
        })

        viewModel.onUpdateListData.observe(viewLifecycleOwner, {
            adapter.updateItem(it)
            updateOverviewPosition()
        })

        viewModel.onUpdateAds.observe(viewLifecycleOwner, {
            if (it == true) {
                adapter.updateAds()
                updateOverviewPosition()
            }
        })

        viewModel.onError.observe(viewLifecycleOwner, {
            closeLoading()
            it.message?.let { it1 -> adapter.setError(it.icon, it1, null) }
        })

        viewModel.onShowMessage.observe(viewLifecycleOwner, {
            showShortError(it)
        })

        viewModel.onUpdatePost.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty())
                viewModel.offsetPost += APIConstants.LIMIT
            adapter.isLoadMoreEnable = true
            adapter.addPost(it, viewModel.offsetPost)
        })

        viewModel.onUpdatePageSuccess.observe(viewLifecycleOwner, { image ->
            for (i in 0 until adapter.getListData.size) {
                if (adapter.getListData[i].viewType == typeEditImage) {
                    if (adapter.getListData[i].data is ICImageVideoSliderModel) {
                        (adapter.getListData[i].data as ICImageVideoSliderModel).listSrc.clear()
                        (adapter.getListData[i].data as ICImageVideoSliderModel).listSrc.add(MediaLogic(image, ICMediaType.TYPE_IMAGE))
                        adapter.notifyItemChanged(i)
                        DialogHelper.showDialogSuccessBlack(requireContext(), getString(R.string.thay_doi_anh_bia_thanh_cong))

                    } else if (adapter.getListData[i].data is ICPageOverview) {
                        (adapter.getListData[i].data as ICPageOverview).avatar = image
                        adapter.notifyItemChanged(i)
                        DialogHelper.showDialogSuccessBlack(requireContext(), getString(R.string.thay_doi_anh_dai_dien_thanh_cong))
                    }
                }
            }
            typeEditImage = null
        })

        viewModel.onUpdatePageError.observe(viewLifecycleOwner, {
            typeEditImage = null
            if (it == ICViewTypes.HEADER_INFOR_PAGE) {
                DialogHelper.showDialogErrorBlack(requireContext(), getString(R.string.thay_doi_anh_dai_dien_that_bai))
            } else {
                DialogHelper.showDialogErrorBlack(requireContext(), getString(R.string.thay_doi_anh_bia_that_bai))
            }
        })

        viewModel.onDetailPost.observe(viewLifecycleOwner, {
            adapter.updatePost(it)
        })

        viewModel.onUpdateState.observe(viewLifecycleOwner, {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.listReportForm.observe(viewLifecycleOwner, {
            listContributionReport.clear()
            listContributionReport.addAll(it)

            dialog = ReportWrongContributionDialog(listContributionReport, R.string.bao_cao_trang)

            dialog.setListener(object : ReportWrongContributionDialog.DialogClickListener {
                override fun buttonClick(position: Int, listReason: MutableList<Int>, message: String, listMessage: MutableList<String>) {
                    viewModel.sendReportPage(listReason, message, listMessage)
                }
            })
            dialog.show(childFragmentManager, dialog.tag)
        })

        viewModel.listReportSuccess.observe(viewLifecycleOwner, {
            dialog.dismiss()
            if (!it.isNullOrEmpty()) {
                ICheckApplication.currentActivity()?.let { activity ->
                    val dialogFragment = ReportWrongContributionSuccessDialog(activity)
                    dialogFragment.show(it, "", context?.getString(R.string.report_wrong_contribution_success_page_title))
                }
            }
        })

        viewModel.onDeletePost.observe(viewLifecycleOwner, {
            DialogHelper.showDialogSuccessBlack(requireContext(), getString(R.string.ban_da_xoa_bai_viet_thanh_cong), null, 1000)
            adapter.deletePost(it)
        })
    }


    fun getData() {
        viewModel.getLayoutPage()
    }

    private fun updateOverviewPosition() {
        pageOverViewPosition = -1
        adapter.getListData.apply {
            for (i in 0 until size) {
                if (get(i).viewType == ICViewTypes.HEADER_INFOR_PAGE) {
                    pageOverViewPosition = i
                    return
                }
            }
        }
    }

    private fun closeLoading() {
        if (swipeLayout.isRefreshing) {
            swipeLayout.isRefreshing = false
        }

        if (layoutLoading != null) {
            layoutContainer.removeView(layoutLoading)
        }
    }

    fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListPosts()
    }

    override fun onShowReportForm() {
        viewModel.getListReportFormPage()
    }

    override fun onRequireLogin() {
        showRequireLogin()
    }

    override fun followAndUnFollowPage(obj: ICPageOverview) {
        obj.id?.let {
            actionFollowAndUnfollow(it)
        }
    }

    private fun actionFollowAndUnfollow(id: Long) {
        if (SessionManager.isUserLogged) {
            if (viewModel.isFollowPage) {
                DialogHelper.showConfirm(requireContext(), getString(R.string.ban_chac_chan_bo_theo_doi_trang_nay), null, getString(R.string.de_sau), getString(R.string.dong_y), true, object : ConfirmDialogListener {
                    override fun onDisagree() {}

                    override fun onAgree() {
                        viewModel.unFollowPage(id)
                    }
                })
            } else {
                viewModel.followPage(id)
            }
        } else {
            showRequireLogin()
        }
    }

    override fun unSubcribeNotification(obj: ICPageOverview) {
        obj.id?.let { viewModel.unSubcribePage(it) }
    }


    override fun subcribeNotification(obj: ICPageOverview) {
        obj.id?.let { viewModel.reSubcribePage(it) }
    }

    override fun onClickImage(item: ICMediaPage) {
        activity?.let {
            if (it is PageDetailActivity) {
                it.openPageImage(item)
            }
        }
    }

    private fun scrollPostOfPage() {
        var positionPost = -1
        for (i in 0 until adapter.getListData.size) {
            if (adapter.getListData[i].viewType == ICViewTypes.LIST_POST_TYPE) {
                positionPost = i
                break
            }
        }

        if (positionPost != -1) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionPost, 55.toPx())
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.CLICK_LISTPOST_OF_PAGE, true))
        } else {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.CLICK_LISTPOST_OF_PAGE, false))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.ONCLICK_PAGE_OVERVIEW -> {
                recyclerView.scrollToPosition(0)
            }
            ICMessageEvent.Type.ONCLICK_LISTPOST_OF_PAGE -> {
                scrollPostOfPage()
            }
            ICMessageEvent.Type.SHOW_FULL_MEDIA -> {
                if (isActivityVisible) {
                    if (event.data != null) {
                        ICheckApplication.currentActivity()?.let { activity ->
                            DetailMediaActivity.start(activity, event.data as List<ICMedia>)
                        }
                    }
                }
            }
            ICMessageEvent.Type.ON_UPDATE_PAGE_NAME -> {
                tvTitle.text = event.data as String
            }
            ICMessageEvent.Type.ON_CREATE_POST -> {
                (event.data as ICPageOverview?)?.let {
                    val intent = Intent(context, CreateOrUpdatePostActivity::class.java)
                    intent.putExtra(Constant.DATA_2, it.id)
                    intent.putExtra(Constant.DATA_3, it.name)
                    intent.putExtra(Constant.DATA_4, it.avatar)
                    intent.putExtra(Constant.DATA_5, it.isVerify)
                    startActivity(intent)
                }
            }
            ICMessageEvent.Type.SHOW_BOTTOM_SHEET_REPORT -> {
                onShowReportForm()
            }
            ICMessageEvent.Type.ON_UPDATE_AUTO_PLAY_VIDEO -> {
                if (isVisible) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, layoutToolbarAlpha.height)
                }
            }
            ICMessageEvent.Type.FOLLOW_PAGE -> {
                updateFollowState(true)
                val index = adapter.getListData.indexOfFirst { it.viewType == ICViewTypes.INVITE_FOLLOW_TYPE }
                if (index != -1) {
                    adapter.getListData[index].data = viewModel.pageOverview
                    adapter.notifyItemChanged(index)
                }
            }
            ICMessageEvent.Type.UNFOLLOW_PAGE -> {
                updateFollowState(false)
                val index = adapter.getListData.indexOfFirst { it.viewType == ICViewTypes.INVITE_FOLLOW_TYPE }
                if (index != -1) {
                    adapter.getListData[index].data = viewModel.pageOverview
                    adapter.notifyItemChanged(index)
                }
            }
            ICMessageEvent.Type.UPDATE_SUBCRIBE_STATUS -> {
                adapter.updateSubcribeState(event.data as Boolean)
                DialogHelper.showDialogSuccessBlack(requireContext(), if (event.data) {
                    requireContext().getString(R.string.ban_da_bat_thong_bao_trang_nay)
                } else {
                    requireContext().getString(R.string.ban_da_tat_thong_bao_trang_nay)
                })
            }
            ICMessageEvent.Type.UPDATE_COUNT_CART -> {
                val count = event.data as String?
                tvCartCount.visibleOrGone(count != null)
                tvCartCount.text = count
            }
            ICMessageEvent.Type.TAKE_IMAGE_DIALOG -> {
                typeEditImage = event.data as Int
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, requestPermissionImage)
            }
            ICMessageEvent.Type.PIN_POST -> {
                (event.data as Long?)?.let {
                    adapter.pinPost(it)
                    DialogHelper.showDialogSuccessBlack(requireContext(), this.getString(R.string.ghim_bai_viet_thanh_cong))
                }
            }
            ICMessageEvent.Type.UN_PIN_POST -> {
                (event.data as Long?)?.let {
                    adapter.unPinPost(it)
                    DialogHelper.showDialogSuccessBlack(requireContext(), this.getString(R.string.bo_ghim_bai_viet_thanh_cong))
                }
            }
            ICMessageEvent.Type.RESULT_EDIT_POST -> {
                (event.data as ICPost?)?.let {
                    adapter.updatePost(it)
                    DialogHelper.showDialogSuccessBlack(requireContext(), this.getString(R.string.ban_da_chinh_sua_bai_viet_thanh_cong))
                }
            }
            ICMessageEvent.Type.RESULT_CREATE_POST -> {
                (event.data as ICPost?)?.let {
                    adapter.createPost(it)
                    val index = adapter.getListData.indexOfFirst { it.viewType == ICViewTypes.LIST_POST_TYPE }
                    if (index != -1)
                        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index, 55.toPx())

                }
            }
            ICMessageEvent.Type.SKIP_INVITE_FOLLOW_PAGE -> {
                viewModel.pageOverview?.isIgnoreInvite = true
                val index = adapter.getListData.indexOfFirst { it.viewType == ICViewTypes.INVITE_FOLLOW_TYPE }
                if (index != -1) {
                    adapter.getListData[index].data = viewModel.pageOverview
                    adapter.notifyItemChanged(index)
                }
            }
            ICMessageEvent.Type.RESULT_DETAIL_POST_ACTIVITY -> {
                if (event.data != null && event.data is ICPost) {
                    adapter.updatePost(event.data)
                }
            }
            ICMessageEvent.Type.RESULT_COMMENT_POST_ACTIVITY -> {
                if (event.data != null && event.data is ICPost) {
                    adapter.updatePost(event.data)
                }
            }
            ICMessageEvent.Type.RESULT_MEDIA_POST_ACTIVITY -> {
                if (event.data != null && event.data is ICPost) {
                    adapter.updatePost(event.data)
                }
            }
            ICMessageEvent.Type.ON_LOG_IN_FIREBASE -> {
                val index = adapter.getListData.indexOfFirst { it.viewType == ICViewTypes.HEADER_INFOR_PAGE }
                if (index != -1)
                    adapter.notifyItemChanged(index)
            }
            ICMessageEvent.Type.DELETE_DETAIL_POST -> {
                if (event.data != null && event.data is Long) {
                    adapter.deletePost(event.data)
                }
            }
            else -> {
            }
        }
    }

    private fun updateFollowState(data: Boolean) {
        if (data) {
            DialogHelper.showDialogSuccessBlack(requireContext(), requireContext().getString(R.string.ban_da_theo_doi_trang_nay))
        } else {
            DialogHelper.showDialogSuccessBlack(requireContext(), requireContext().getString(R.string.ban_da_huy_theo_doi_trang_nay))
            adapter.skipInviteFollowWidget()
        }
    }

    private fun showRequireLogin() {
        ICheckApplication.currentActivity()?.let { activity ->
            RewardLoginDialogV2.show((activity as AppCompatActivity).supportFragmentManager, object : RewardLoginCallback {
                override fun onLogin() {
                    val intent = Intent(context, IckLoginActivity::class.java)
                    startActivityForResult(intent, requireLogin)
                }

                override fun onRegister() {
                    val intent = Intent(context, IckLoginActivity::class.java)
                    intent.putExtra(Constant.DATA_1, Constant.REGISTER_TYPE)
                    startActivityForResult(intent, requireLogin)
                }

                override fun onDismiss() {
                    onRequireLoginCancel()
                }
            })
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requireLogin) {
                viewModel.getLayoutPage()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getOrUpdateAds()
        viewModel.updateCartCount()
        isActivityVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disposeApi()
    }

    override fun onEditPost(obj: ICPost) {
        ICheckApplication.currentActivity()?.let {
            if (obj.customerCriteria.isNullOrEmpty()) {
                val intent = Intent(it, CreateOrUpdatePostActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.id)
                intent.putExtra(Constant.DATA_2, obj.page?.id)
                intent.putExtra(Constant.DATA_3, obj.page?.getName)
                intent.putExtra(Constant.DATA_4, obj.page?.avatar)
                intent.putExtra(Constant.DATA_5, obj.page?.isVerify)
                it.startActivity(intent)
            } else {
                val intent = Intent(it, EditReviewActivity::class.java)
                if (obj.targetId != null) {
                    intent.putExtra(Constant.DATA_1, obj.targetId)
                } else {
                    intent.putExtra(Constant.DATA_1, obj.meta?.product?.id)
                }
                it.startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun followAndUnFollowPage(obj: ICPost) {
        obj.page?.id?.let {
            actionFollowAndUnfollow(it)
        }
    }

    override fun onDeletePost(id: Long) {
        viewModel.deletePost(id)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestPermissionImage) {
            if (PermissionHelper.checkResult(grantResults)) {
                if (typeEditImage == ICViewTypes.HEADER_INFOR_PAGE) {
                    TakeMediaDialog.show(this@PageDetailFragment.requireActivity().supportFragmentManager, this@PageDetailFragment.requireActivity(), takeMediaListener, selectMulti = false, cropImage = false, ratio = "1:1", isVideo = false)
                } else {
                    TakeMediaDialog.show(this@PageDetailFragment.requireActivity().supportFragmentManager, this@PageDetailFragment.requireActivity(), takeMediaListener, selectMulti = false, cropImage = false, ratio = "375:192", isVideo = false)
                }
            } else {
                showLongWarning(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}