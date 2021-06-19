package vn.icheck.android.screen.user.commentpost

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_comment_post.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.component.commentpost.ICommentPostView
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.edit_comment.EditCommentActivity
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.kotlin.WidgetUtils.loadImageFromVideoFile
import java.io.File

/**
 * Created by VuLCL on 7/7/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CommentPostActivity : BaseActivityMVVM(), ICommentPostView {
    private lateinit var viewModel: CommentPostViewModel

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val adapter = CommentPostAdapter(this)
    private lateinit var permissionAdapter: CommentPermissionAdapter
    private lateinit var parentEmojiAdapter: EmojiAdapter
    private lateinit var childEmojiAdapter: EmojiAdapter

    private val requestTakePicture = 1

    private var behavior: CoordinatorLayout.Behavior<*>? = null
    private var unregistrar: Unregistrar? = null

    private val requestEdit = 1
    private val requestLoginV2 = 2

    companion object {
        fun start(activity: Activity, obj: ICPost, type: Int? = null) {
            val intent = Intent(activity, CommentPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, obj)

            if (type != null)
                intent.putExtra(Constant.DATA_2, type)

            ActivityUtils.startActivityWithoutAnimation(activity, intent)
        }

        fun start(activity: Activity, postID: Long, type: Int? = null) {
            val intent = Intent(activity, CommentPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, postID)

            if (type != null)
                intent.putExtra(Constant.DATA_2, type)

            ActivityUtils.startActivityWithoutAnimation(activity, intent)
        }

        fun startForResult(activity: FragmentActivity, postID: Long, type: Int? = null, requestCode: Int) {
            val intent = Intent(activity, CommentPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, postID)

            if (type != null)
                intent.putExtra(Constant.DATA_2, type)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.none_no_time, R.anim.none_no_time)
        }

    }

    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            showLayoutImage(file)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            showLayoutImage(file)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_post)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        layoutInputContent.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(this)
        tvActor.background=ViewHelper.bgTransparentStrokeLineColor1Corners10(this)

        setupBottomSheet()
        setupRecyclerView()
//        setupEmoji()
        setUpSwipeLayout()
        setupPermission()
        setupViewModel()
        setupListener()
    }


    private fun setupBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Handler().postDelayed({
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }, 200)


        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    Intent().apply {
                        putExtra(Constant.DATA_1, viewModel.post)
                        setResult(RESULT_OK, this)
                    }
                    ActivityUtils.finishActivityWithoutAnimation(this@CommentPostActivity)
                }
            }
        })
    }


    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        behavior = (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
                    bottomSheet.requestLayout()
                } else {
                    (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
                }
            }
        })
    }

    private fun setupEmoji() {
        parentEmojiAdapter = EmojiAdapter(1, object : ItemClickListener<Any> {
            override fun onItemClick(position: Int, item: Any?) {
                if (item != null && item is StickerPackages) {
                    viewModel.getEmoji(item.id)
                }
            }
        })
        rcvParentEmoji.adapter = parentEmojiAdapter

        rcvChildEmoji.setHasFixedSize(true)
        childEmojiAdapter = EmojiAdapter(2, object : ItemClickListener<Any> {
            override fun onItemClick(position: Int, item: Any?) {
                if (item != null && item is Stickers) {
                    KeyboardUtils.showSoftInput(edtContent)
//                    layoutImage.visibility = View.VISIBLE
//                    layoutImage.tag = item.image
//                    WidgetUtils.loadImageUrlRounded(imgImage, item.image, SizeHelper.size4)

                    checkShowEmoji(false)
                    checkSendStatus()
                }
            }
        })
        rcvChildEmoji.adapter = childEmojiAdapter
    }

    private fun setUpSwipeLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)
        swipeLayout.isNestedScrollingEnabled = false
        swipeLayout.setOnRefreshListener {
            viewModel.getData()
        }
    }

    private fun setupPermission() {
        permissionAdapter = CommentPermissionAdapter(object : ItemClickListener<ICCommentPermission> {
            override fun onItemClick(position: Int, item: ICCommentPermission?) {
                if (item != null) {
                    if (item.type == Constant.PAGE) {
                        WidgetUtils.loadImageUrl(imgAvatar, item.avatar, R.drawable.ic_business_v2)
                    } else {
                        WidgetUtils.loadImageUrl(imgAvatar, item.avatar, R.drawable.ic_user_svg)
                    }
                }
                showLayoutPermission(false)
            }
        })
        recPermission.adapter = permissionAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CommentPostViewModel::class.java)

        viewModel.onSetPostInfo.observe(this, {
            if (it != null) {
                checkLikePost(it)

                WidgetUtils.loadImageUrl(imgAvatar, SessionManager.session.user?.avatar, R.drawable.ic_user_svg)
            } else {
                DialogHelper.showNotification(this@CommentPostActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
            checkPrivacyConfig()
            adapter.showOrHideAnswer(it.involveType)
        })

        viewModel.onSetPermission.observe(this, {
            permissionAdapter.setData(it)

            when {
                it.size >= 3 -> {
                    recPermission.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(126))
                }
                it.size == 3 -> {
                    recPermission.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(106))
                }
                else -> {
                    recPermission.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
            }

            permissionAdapter.getSelectedPermission?.let { item ->
                WidgetUtils.loadImageUrl(imgAvatar, item.avatar, R.drawable.ic_user_svg)
            }
        })

        viewModel.onSetListComment.observe(this, {
            closeLoading()

            Handler().postDelayed({
                intent?.getIntExtra(Constant.DATA_2, 0)?.let { type ->
                    when (type) {
                        1 -> {
                            edtContent.requestFocus()
                            KeyboardUtils.showSoftInput(edtContent)
                        }
                        2 -> {
                            imgCamera.performClick()
                        }
                        3 -> {
                            imgEmoji.performClick()
                        }
                    }
                    intent = null
                }
            }, 200)

            adapter.setMessage(0, getString(R.string.chua_co_binh_luan_nao), -1)
            adapter.setListData(it)
        })
        viewModel.onAddListComment.observe(this, { listComment ->
            listComment.firstOrNull()?.let {
                if ((it as ICCommentPost).isReply != viewModel.isReply) {
                    for (item in listComment) {
                        (item as ICCommentPost).isReply = viewModel.isReply
                    }
                }
            }
            adapter.addListData(listComment)
        })
        viewModel.onAddComment.observe(this, {
            postCommentSuccess()
            adapter.addComment(it)
            imgSend.isEnabled = true
            recyclerView.smoothScrollToPosition(0)
        })

        viewModel.onAddListReplies.observe(this, {
            adapter.addListReplies(it)
        })

        viewModel.onAddReply.observe(this, {
            postCommentSuccess()
            adapter.addAnswer(it)?.let { position ->
                recyclerView.smoothScrollToPosition(position)
            }
        })

        viewModel.onDeleteComment.observe(this, {
            try {
                adapter.deleteComment(it)
                showShortSuccessToast(getString(R.string.xoa_binh_luan_thanh_cong))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        viewModel.onSetParentEmoji.observe(this, {
            parentEmojiAdapter.setParentData(it)
        })

        viewModel.onSetChildEmoji.observe(this, {
            childEmojiAdapter.setChildData(it)
        })

        viewModel.onLikeComment.observe(this, {
            viewModel.post?.let { post ->
                checkLikePost(post)
            }

        })

        viewModel.onError.observe(this, {
            closeLoading()

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                it.message?.let { it1 -> showLongError(it1) }
            }
        })

        viewModel.onShowMessage.observe(this, {
            imgSend.isEnabled = true
            showShortErrorToast(it)
        })

        viewModel.onStatus.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    showLoading()
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    closeLoading()
                }
                ICMessageEvent.Type.BACK -> {
                    closeLoading()

                    DialogHelper.showNotification(this@CommentPostActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
            }
        })

        viewModel.getData(intent)
    }

    private fun checkPrivacyConfig() {
        if (viewModel.post?.page == null) {
            if (viewModel.post?.user?.id != SessionManager.session.user?.id) {
                when (viewModel.post?.user?.userPrivacyConfig?.whoCommentYourPost) {
                    Constant.EVERYONE -> {
                        containerComment.beVisible()
                    }
                    Constant.FRIEND -> {
                        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null && SessionManager.session.user?.id != null) {
                            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFriendIdList, viewModel.post?.user?.id.toString(), object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.value != null && snapshot.value is Long) {
                                        containerComment.beVisible()
                                    } else {
                                        notAllowReply()
                                        containerComment.beGone()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    logError(error.toException())
                                }
                            })
                        }
                    }
                    else -> {
                        if (viewModel.post?.user?.id == SessionManager.session.user?.id) {
                            containerComment.beVisible()
                        } else {
                            notAllowReply()
                            containerComment.beGone()
                        }
                    }

                }
            } else {
                containerComment.beVisible()
            }
        } else {
            containerComment.beVisible()
        }
    }

    private fun notAllowReply() {
        viewModel.isReply = false
        for (i in 0 until adapter.getListData.size) {
            if (adapter.getListData[i] is ICCommentPost) {
                (adapter.getListData[i] as ICCommentPost).isReply = false
                adapter.notifyItemChanged(i)
            }
        }
    }

    private fun checkLikePost(post: ICPost) {
        tvLike.text = TextHelper.formatCount(post.expressiveCount)

        if (!post.expressive.isNullOrEmpty()) {
            tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_on_24dp, 0, 0, 0)
            tvLike.setTextColor(ContextCompat.getColor(this,R.color.red_like_question))
        } else {
            tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_off_24dp, 0, 0, 0)
            tvLike.setTextColor(ContextCompat.getColor(this,R.color.black_75))
        }
    }

    private fun setupListener() {
        imgAvatar.setOnClickListener {
            showLayoutPermission(true)
        }

        layoutPermission.setOnClickListener {
            showLayoutPermission(false)
        }

        edtContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkSendStatus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        imgCamera.onDelayClick({
            if (PermissionHelper.checkPermission(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), requestTakePicture)) {
                TakeMediaDialog.show(supportFragmentManager, this, takeMediaListener, isVideo = true)
            }
        }, 2000)

        imgCloseImage.setOnClickListener {
            showLayoutImage(null)
            enableCamera(false)
            checkSendStatus()
        }

        imgEmoji.setOnClickListener {
            if (layoutEmoji.visibility == View.VISIBLE) {
                checkShowEmoji(false)
            } else {
                checkShowEmoji(true)
                viewModel.getEmoji()
            }
        }

        tvLike.setOnClickListener {
            if (!SessionManager.isUserLogged) {
                DialogHelper.showLoginPopup(this)
            } else {
                viewModel.likeReview()
            }
        }

        tvActor.setOnClickListener {
            tvActor.text = null
            layoutActor.visibility = View.GONE
            layoutActor.tag = null

        }

        imgSend.setOnClickListener {
            imgSend.isEnabled = false
            if (SessionManager.isUserLogged) {
                viewModel.send(permissionAdapter.getPageID, layoutActor.tag as Long?, imgCommentSend.tag as File?, edtContent.text.toString())
            } else {
                imgSend.isEnabled = true
                onRequireLogin(requestLoginV2)
            }
        }
    }

    fun showLayoutImage(file: File?) {
        if (file != null) {
            view2.beVisible()
            imgCloseImage.beVisible()
            cardViewImage.beVisible()
            imgCommentSend.tag = file

            imgCommentSend.loadImageFromVideoFile(file, null, SizeHelper.dpToPx(4))

            if (file.absolutePath.contains(".mp4")) {
                imgPlay.beVisible()
            } else {
                imgPlay.beInvisible()
            }
            enableCamera(true)
        } else {
            view2.beGone()
            imgCloseImage.beGone()
            cardViewImage.beGone()

            imgCommentSend.tag = null

            enableCamera(false)
        }
        checkSendStatus()
    }

    private fun showLoading() {
        DialogHelper.showLoading(this)
    }

    private fun closeLoading() {
        DialogHelper.closeLoading(this)
        swipeLayout.isRefreshing = false
    }

    private fun showLayoutPermission(isShow: Boolean) {
        if (isShow) {
            layoutPermission.visibility = View.VISIBLE
            imgMore.rotation = 270f
        } else {
            layoutPermission.visibility = View.GONE
            imgMore.rotation = 90f
        }
    }

    private fun checkShowEmoji(isShow: Boolean) {
        if (isShow) {
            if (KeyboardVisibilityEvent.isKeyboardVisible(this)) {
                KeyboardUtils.hideSoftInput(this)
            }

            layoutEmoji.visibility = View.VISIBLE
            imgEmoji.setImageResource(R.drawable.ic_imoji_fc_24px)
        } else {
            layoutEmoji.visibility = View.GONE
            imgEmoji.setImageResource(R.drawable.ic_imoji_24px)
        }
    }

    private fun enableCamera(isEnable: Boolean) {
        if (isEnable) {
            imgCamera.fillDrawableColor(R.drawable.ic_camera_off_vector_24dp)
        } else {
            imgCamera.setImageResource(R.drawable.ic_camera_off_24px)
        }
    }

    private fun checkSendStatus() {
        if (edtContent.text.toString().trim().isNotEmpty() || imgCommentSend.tag != null) {
            imgSend.fillDrawableColor(R.drawable.ic_chat_send_24px)
            imgSend.isClickable = true
            layoutInputContent.background=ViewHelper.bgOutlinePrimary1Corners4(this)
        } else {
            imgSend.setImageResource(R.drawable.ic_chat_send_gray_24_px)
            imgSend.isClickable = false
            layoutInputContent.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(this)
        }
    }

    private fun postCommentSuccess() {
        KeyboardUtils.hideSoftInput(edtContent)
        edtContent.text = null

        tvActor.text = null
        layoutActor.visibility = View.GONE
        layoutActor.tag = null
        imgCloseImage.performClick()

        checkShowEmoji(false)
        enableCamera(false)
        checkSendStatus()
    }

    override fun onMessageClicked() {
        adapter.setMessage(0, "", -1)
        viewModel.getListComments()
    }

    override fun onLoadMore() {
        viewModel.getListComments(true)
    }

    override fun onLoadMoreChildComment(obj: ICCommentPostMore) {
        viewModel.getListReplies(obj)
    }

    override fun onAnswer(obj: ICCommentPost) {
        layoutActor.visibility = View.VISIBLE
        layoutActor.tag = if (obj.parentID == null) obj.id else obj.parentID

        tvActor.text = if (obj.page != null) {
            Html.fromHtml(ViewHelper.setSecondaryHtmlString(resources.getString(R.string.tra_loi_xxx, obj.page?.name)))
        } else {
            Html.fromHtml(ViewHelper.setSecondaryHtmlString(resources.getString(R.string.tra_loi_xxx, obj.user?.getName)))
        }

        edtContent.text = null
        edtContent.requestFocus()
        KeyboardUtils.showSoftInput(edtContent)
    }

    override fun onEditComment(obj: ICCommentPost) {
        EditCommentActivity.start(this, obj, viewModel.barcode, requestEdit)
    }

    override fun onDelete(obj: ICCommentPost) {
        viewModel.deleteComment(obj)
    }

    override fun onBackPressed() {
        if (layoutEmoji.visibility == View.VISIBLE) {
            checkShowEmoji(false)
        } else if (sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
            bottomSheet.requestLayout()

            Handler(Looper.getMainLooper()).postDelayed({
                sheetBehavior.isHideable = true
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }, 100)
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.SHOW_FULL_MEDIA -> {
                if (event.data != null) {
                    ICheckApplication.currentActivity()?.let { activity ->
                        DetailMediaActivity.start(activity, event.data as List<ICMedia>)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        unregistrar = KeyboardVisibilityEvent.registerEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    if (layoutEmoji.visibility == View.VISIBLE) {
                        checkShowEmoji(false)
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        unregistrar?.unregister()
        unregistrar = null
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.RESULT_COMMENT_POST_ACTIVITY, viewModel.post))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestTakePicture) {
            if (PermissionHelper.checkResult(grantResults)) {
                imgCamera.performClick()
            } else {
                showLongWarning(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestEdit -> {
                if (resultCode == Activity.RESULT_OK) {
                    (data?.getSerializableExtra(Constant.DATA_1) as ICCommentPost?)?.let {
                        adapter.updateComment(it)
                    }
                }
            }
            requestLoginV2 -> {
                viewModel.getData()
            }
        }
    }
}