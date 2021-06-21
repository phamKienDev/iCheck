package vn.icheck.android.screen.user.detail_post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_detail_post.imgAvatar
import kotlinx.android.synthetic.main.activity_detail_post.imgCamera
import kotlinx.android.synthetic.main.activity_detail_post.imgEmoji
import kotlinx.android.synthetic.main.activity_detail_post.imgSend
import kotlinx.android.synthetic.main.activity_detail_post.layoutPermission
import kotlinx.android.synthetic.main.activity_detail_post.rcvChildEmoji
import kotlinx.android.synthetic.main.activity_detail_post.rcvParentEmoji
import kotlinx.android.synthetic.main.activity_detail_post.tvActor
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.component.commentpost.ICommentPostView
import vn.icheck.android.component.post.PostOptionDialog
import vn.icheck.android.component.view.ViewHelper.delayTimeoutClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.screen.user.commentpost.CommentPermissionAdapter
import vn.icheck.android.screen.user.createpost.CreateOrUpdatePostActivity
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.edit_review.EditReviewActivity
import vn.icheck.android.screen.user.edit_comment.EditCommentActivity
import vn.icheck.android.screen.user.list_product_question.adapter.ListEmojiAdapter
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.kotlin.WidgetUtils.loadImageFromVideoFile
import java.io.File

class DetailPostActivity : BaseActivityMVVM(), View.OnClickListener, ICommentPostView, IDetailPostListener {
    lateinit var viewModel: DetailPostViewModel

    lateinit var adapter: DetailPostAdapter
    lateinit var childEmojiAdapter: ListEmojiAdapter
    lateinit var parentEmojiAdapter: ListEmojiAdapter
    lateinit var permissionAdapter: CommentPermissionAdapter

    private var postDialog: PostOptionDialog? = null

    private val requestCamera = 19
    private val requestUpdateComment = 20
    private val requestEditPost = 21
    private val requestSentComment = 22
    private val requestLoginV2 = 23
    private val requestMedia = 24

    private var isActivityVisible = true


    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            showLayoutImage(true, file)
            showLayoutEmoji(false)
            showBtnSend(true)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            showLayoutImage(true, file)
            showBtnSend(true)
            showLayoutEmoji(false)
        }

    }

    companion object {
        fun start(activity: Activity, postId: Long, showKeyboard: Boolean = false, requestCode: Int = -1) {
            val intent = Intent(activity, DetailPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, postId)
            intent.putExtra(Constant.DATA_2, showKeyboard)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }

        fun start(activity: AppCompatActivity, postId: Long) {
            val intent = Intent(activity, DetailPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, postId)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }

        fun start(activity: Activity, post: ICPost, requestCode: Int = -1) {
            val intent = Intent(activity, DetailPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, post)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)

        viewModel = ViewModelProvider(this).get(DetailPostViewModel::class.java)
        initView()
        initRecyclerView()
//        setUpEmoji()
        setUpPermission()
        listenerData()
        viewModel.getData(intent)
    }


    private fun initView() {
        tvActor.background=ViewHelper.bgTransparentStrokeLineColor1Corners10(this)
        containerEnter.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(this)

        edtEnter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNotEmpty()) {
                    showBtnSend(true)
                } else {
                    if (imgCommentSend.tag == null) {
                        showBtnSend(false)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeRefresh.setColorSchemeColors(primaryColor, primaryColor, primaryColor)
        swipeRefresh.setOnRefreshListener {
            getData()
        }

        WidgetUtils.loadImageUrl(imgAvatar, SessionManager.session.user?.avatar, R.drawable.ic_avatar_default_84dp)

        WidgetUtils.setClickListener(this, imgBack, imgAction, imgEmoji, imgCamera, imgSelectPermission, imgAvatar,
                imgSend, tvActor, layoutPermission, imgClearImage)
    }

    private fun initRecyclerView() {
        adapter = DetailPostAdapter(this, this)
        rcvContent.adapter = adapter
    }

    private fun setUpEmoji() {
        parentEmojiAdapter = ListEmojiAdapter(1, object : ItemClickListener<Any> {
            override fun onItemClick(position: Int, item: Any?) {
                viewModel.getEmoji((item as StickerPackages).id)
            }
        })
        rcvParentEmoji.adapter = parentEmojiAdapter

        childEmojiAdapter = ListEmojiAdapter(2, object : ItemClickListener<Any> {
            override fun onItemClick(position: Int, item: Any?) {
                if (item != null && item is Stickers) {
                    showBtnSend(true)
                    showLayoutEmoji(false)
                    showLayoutImage(false)
                    imgCommentSend.tag = item.image
                    WidgetUtils.loadImageUrlNotRounded(imgCommentSend, item.image)
                }
            }
        })
        rcvChildEmoji.adapter = childEmojiAdapter
    }

    private fun setUpPermission() {
        permissionAdapter = CommentPermissionAdapter(object : ItemClickListener<ICCommentPermission> {
            override fun onItemClick(position: Int, item: ICCommentPermission?) {
                if (item != null) {
                    if (item.type == Constant.PAGE) {
                        WidgetUtils.loadImageUrl(imgAvatar, item.avatar, R.drawable.ic_business_v2)
                    } else {
                        WidgetUtils.loadImageUrl(imgAvatar, item.avatar, R.drawable.ic_user_svg)
                    }
                    viewModel.setPermission(item)
                }
                showLayoutPermission()
            }
        })
        rcvPermission.adapter = permissionAdapter
    }


    private fun listenerData() {
        viewModel.onDetailPost.observe(this, {
            swipeRefresh.isRefreshing = false
            if (it.involveType == Constant.REVIEW) {
                tvNameProduct.setText(R.string.chi_tiet_danh_gia)
            } else {
                tvNameProduct.setText(R.string.chi_tiet_bai_viet)
            }

            if (intent.getBooleanExtra(Constant.DATA_2, false)) {
                intent.putExtra(Constant.DATA_2, false)
                KeyboardUtils.showSoftInput(edtEnter)
            }

            checkPrivacyConfig()
            adapter.addHeader(it)
            adapter.showOrHideAnswer(it.involveType)
        })

        viewModel.onAddComment.observe(this, { listComment ->
            swipeRefresh.isRefreshing = false
            listComment.firstOrNull()?.let {
                if ((it as ICCommentPost).isReply != viewModel.isReply) {
                    for (item in listComment) {
                        (item as ICCommentPost).isReply = viewModel.isReply
                    }
                }
            }
            adapter.addListData(listComment)
        })

        viewModel.onError.observe(this, {
            imgSend.isEnabled = true
            swipeRefresh.isRefreshing = false
            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                DialogHelper.showDialogSuccessBlack(this, it.message)
            }
        })

        viewModel.onStatus.observe(this, {
            imgSend.isEnabled = true
            when (it.type) {
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

        viewModel.onAddChilComment.observe(this, {
            adapter.addChildComment(it)
        })

        viewModel.onSetPermission.observe(this, {
            if (!it.isNullOrEmpty()) {
                viewModel.setPermission(it[0])
            }
            when {
                it.size >= 3 -> {
                    rcvPermission.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(126))
                }
                it.size == 3 -> {
                    rcvPermission.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(106))
                }
                else -> {
                    rcvPermission.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
            }
            permissionAdapter.setData(it)
        })

        viewModel.onSetParentEmoji.observe(this, {
            parentEmojiAdapter.setParentData(it)
        })

        viewModel.onSetChildEmoji.observe(this, {
            childEmojiAdapter.setChildData(it.toMutableList())
        })
        viewModel.onPostComment.observe(this, {
            postCommentSuccess()
            adapter.addComment(it)
            if (adapter.getListData.firstOrNull() is ICPost) {
                adapter.notifyItemChanged(0)
            }
        })
        viewModel.onPostChildComment.observe(this, {
            postCommentSuccess()
            adapter.addChildComment(it)
        })
        viewModel.onDeleteComment.observe(this, {
            adapter.deleteComment(it)
            DialogHelper.showDialogSuccessBlack(this, getString(R.string.xoa_binh_luan_thanh_cong), null, 800)
            if (adapter.getListData.firstOrNull() is ICPost) {
                adapter.notifyItemChanged(0)
            }
        })
        viewModel.onDeletePost.observe(this, {
            DialogHelper.showDialogSuccessBlack(this, getString(R.string.ban_da_xoa_bai_viet_thanh_cong), null, 1000)
            Handler().postDelayed({
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.DELETE_DETAIL_POST, it.id))
                finish()
            }, 1200)
        })
        viewModel.onLikePost.observe(this, {
            if (adapter.getListData.firstOrNull() is ICPost) {
                adapter.notifyItemChanged(0)
            }
        })
    }

    private fun checkPrivacyConfig() {
        if (viewModel.post?.page == null) {
            if (viewModel.post?.user?.id != SessionManager.session.user?.id) {
                if (viewModel.post?.user?.userPrivacyConfig?.whoCommentYourPost!=null) {
                    when (viewModel.post?.user?.userPrivacyConfig?.whoCommentYourPost) {
                        Constant.EVERYONE -> {
                            view3.beVisible()
                            containerSent.beVisible()
                        }
                        Constant.FRIEND -> {
                            if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null && SessionManager.session.user?.id != null) {
                                ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFriendIdList, viewModel.post?.user?.id.toString(), object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value != null && snapshot.value is Long) {
                                                view3.beVisible()
                                                containerSent.beVisible()
                                            } else {
                                                notAllowReply()
                                                view3.beGone()
                                                containerSent.beGone()
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
                                view3.beVisible()
                                containerSent.beVisible()
                            } else {
                                notAllowReply()
                                view3.beGone()
                                containerSent.beGone()
                            }
                        }
                    }
                }else{
                    view3.beVisible()
                    containerSent.beVisible()
                }
            } else {
                view3.beVisible()
                containerSent.beVisible()
            }
        } else {
            view3.beVisible()
            containerSent.beVisible()
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


    private fun getData() {
        swipeRefresh.isRefreshing = true
        adapter.resetData()
        Handler().postDelayed({
            viewModel.getData(intent)
        }, 300)
    }


    fun showLayoutImage(show: Boolean, file: File? = null) {
        if (show) {
            imgCamera.fillDrawableColor(R.drawable.ic_camera_on_24px)
            view2.beVisible()
            imgClearImage.beVisible()
            cardViewImage.beVisible()
            imgCommentSend.tag = file

            imgCommentSend.loadImageFromVideoFile(file, null, SizeHelper.dpToPx(4))

            if (file?.absolutePath?.contains(".mp4") == true) {
                btnPlay.beVisible()
            } else {
                btnPlay.beInvisible()
            }

        } else {
            imgCamera.setImageResource(R.drawable.ic_camera_off_24px)
            view2.beGone()
            imgClearImage.beGone()
            cardViewImage.beGone()
        }
    }

    private fun showLayoutPermission() {
        if (layoutPermission.visibility == View.GONE) {
            imgSelectPermission.rotation = 180f
            layoutPermission.visibility = View.VISIBLE
        } else {
            imgSelectPermission.rotation = 0f
            layoutPermission.visibility = View.GONE
        }
    }

    private fun showBtnSend(enable: Boolean) {
        if (enable) {
            imgSend.isEnabled = true
            imgSend.fillDrawableColor(R.drawable.ic_chat_send_24px)
            containerEnter.background=ViewHelper.bgOutlinePrimary1Corners4(this)
        } else {
            imgSend.isEnabled = false
            imgSend.setImageResource(R.drawable.ic_chat_send_gray_24_px)
            containerEnter.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(this)
        }
    }

    private fun showLayoutEmoji(show: Boolean) {
        if (!show) {
            containerSticker.visibility = View.GONE
            imgEmoji.setImageResource(R.drawable.ic_imoji_24px)
        } else {
            containerSticker.visibility = View.VISIBLE
            imgEmoji.setImageResource(R.drawable.ic_imoji_fc_24px)
        }
    }

    private fun postCommentSuccess() {
        KeyboardUtils.hideSoftInput(edtEnter)
        showLayoutImage(false)
        edtEnter.setText("")
        showLayoutEmoji(false)
        edtEnter.tag = null
        tvActor.visibility = View.GONE
        imgCommentSend.tag = null
        showBtnSend(false)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgAction -> {
                KeyboardUtils.hideSoftInput(edtEnter)
                Handler().postDelayed({
                    viewModel.post?.let { post ->
                        postDialog?.dialog?.dismiss()
                        postDialog = object : PostOptionDialog(this, post) {
                            override fun onPin(isPin: Boolean) {
                                if (post.pinned) {
                                    DialogHelper.showConfirm(dialog.context, rText(R.string.ban_chac_chan_muon_bo_ghim_bai_viet_nay), null, rText(R.string.de_sau), rText(R.string.dong_y), true, null, R.color.colorPrimary, object : ConfirmDialogListener {
                                        override fun onDisagree() {

                                        }

                                        override fun onAgree() {
                                            viewModel.pinPost(post, isPin)
                                        }
                                    })
                                } else {
                                    viewModel.pinPost(post, isPin)
                                }
                            }

                            override fun onEdit() {
                                if (post.involveType != Constant.REVIEW) {
                                    val intent = Intent(this@DetailPostActivity, CreateOrUpdatePostActivity::class.java)
                                    intent.putExtra(Constant.DATA_1, post.id)
                                    intent.putExtra(Constant.DATA_2, post.page?.id)
                                    intent.putExtra(Constant.DATA_3, post.page?.name)
                                    intent.putExtra(Constant.DATA_4, post.page?.avatar)
                                    intent.putExtra(Constant.DATA_5, post.page?.isVerify)
                                    this@DetailPostActivity.startActivity(intent)
                                } else {
                                    if (post.targetId != null) {
                                        startActivity<EditReviewActivity, Long>(Constant.DATA_1, post.targetId!!)
                                    } else if (post.meta?.product?.id != null) {
                                        startActivity<EditReviewActivity, Long>(Constant.DATA_1, post.meta?.product?.id!!)
                                    }
                                }
                            }

                            override fun onFollowOrUnfollowPage(isFollow: Boolean) {
                            }

                            override fun onDelete(id: Long) {
                                viewModel.deletePost(id)
                            }
                        }
                        postDialog?.show()
                    }
                }, 200)
            }
            R.id.imgCamera -> {
                val permissions =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                imgCamera.delayTimeoutClick(2000)
                if (PermissionHelper.isAllowPermission(this, permissions)) {
                    TakeMediaDialog.show(supportFragmentManager, this, takeMediaListener, isVideo = true)
                } else {
                    PermissionHelper.checkPermission(this, permissions, requestCamera)
                }
            }
            R.id.imgEmoji -> {
                if (containerSticker.visibility == View.VISIBLE) {
                    showLayoutEmoji(false)
                } else {
                    showLayoutEmoji(true)
                }
            }
            R.id.imgSend -> {
                imgSend.isEnabled = false
                if (SessionManager.isUserLogged) {
                    if (imgCommentSend.tag != null || edtEnter.text.toString().isNotEmpty()) {
                        viewModel.uploadImage(imgCommentSend.tag, permissionAdapter.getSelectedPermission, edtEnter.text.toString(), edtEnter.tag)
                    }
                } else {
                    imgSend.isEnabled = true
                    onRequireLogin(requestSentComment)
                }
            }
            R.id.imgClearImage -> {
                imgCommentSend.tag = null
                showLayoutImage(false)
                showLayoutEmoji(false)
                if (edtEnter.text.toString().isEmpty()) {
                    showBtnSend(false)
                }
            }
            R.id.tvActor -> {
                tvActor.visibility = View.GONE
                edtEnter.tag = null
            }
            R.id.imgSelectPermission -> {
                showLayoutPermission()
            }
            R.id.imgAvatar -> {
                showLayoutPermission()
            }
            R.id.layoutPermission -> {
                showLayoutPermission()
            }
        }
    }

    override fun onLoadMoreChildComment(obj: ICCommentPostMore) {
        viewModel.getListChildComment(obj)
    }

    override fun onAnswer(obj: ICCommentPost) {
        tvActor.visibility = View.VISIBLE
        tvActor.text = if (obj.page != null) {
            Html.fromHtml(ViewHelper.setSecondaryHtmlString(resources.getString(R.string.tra_loi_xxx, obj.page?.name)))
        } else {
            Html.fromHtml(ViewHelper.setSecondaryHtmlString(resources.getString(R.string.tra_loi_xxx, obj.user?.getName)))
        }

        edtEnter.tag = obj
        edtEnter.requestFocus()
        KeyboardUtils.showSoftInput(edtEnter)
    }

    override fun onEditComment(obj: ICCommentPost) {
        EditCommentActivity.start(this, obj, viewModel.post?.meta?.product?.barcode, requestUpdateComment)
    }

    override fun onDelete(obj: ICCommentPost) {
        viewModel.deleteComment(obj)
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListComment(true)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.ON_EDIT_POST -> {
                startActivityForResult<CreateOrUpdatePostActivity, Long>(Constant.DATA_1, event.data!! as Long, requestEditPost)
            }
            ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                if (isActivityVisible) {
                    onRequireLogin(requestLoginV2)
                }
            }
            ICMessageEvent.Type.OPEN_MEDIA_IN_POST -> {
                if (event.data != null && event.data is ICPost) {
                    val intent = Intent(this, MediaInPostActivity::class.java)
                    intent.putExtra(Constant.DATA_1, event.data)
                    intent.putExtra(Constant.DATA_3, event.data.positionMedia)
                    intent.putExtra(Constant.DATA_4, "post")
                    startActivityForResult(intent, requestMedia)
                }
            }
            ICMessageEvent.Type.FOLLOW_PAGE -> {
                DialogHelper.showDialogSuccessBlack(this, this.getString(R.string.ban_da_theo_doi_trang_nay))
            }
            ICMessageEvent.Type.UNFOLLOW_PAGE->{
                DialogHelper.showDialogSuccessBlack(this, this.getString(R.string.ban_da_huy_theo_doi_trang_nay))
            }
            ICMessageEvent.Type.PIN_POST -> {
                DialogHelper.showDialogSuccessBlack(this, this.getString(R.string.ghim_bai_viet_thanh_cong))
            }
            ICMessageEvent.Type.UN_PIN_POST -> {
                DialogHelper.showDialogSuccessBlack(this, this.getString(R.string.bo_ghim_bai_viet_thanh_cong))
            }
            ICMessageEvent.Type.RESULT_EDIT_POST -> {
                (event.data as ICPost?)?.let {
                    adapter.updatePostOrReview(it)
                }
            }
            ICMessageEvent.Type.SHOW_FULL_MEDIA -> {
                if (event.data != null) {
                    ICheckApplication.currentActivity()?.let { activity ->
                        DetailMediaActivity.start(activity, event.data as List<ICMedia>)
                    }
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
                requestUpdateComment -> {
                    val comment = data?.getSerializableExtra(Constant.DATA_1)
                    if (comment != null && comment is ICCommentPost) {
                        adapter.updateComment(comment)
                    }

                }
                requestMedia -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post != null && post is ICPost) {
                        adapter.updatePostOrReview(post)
                        viewModel.setCommentPostData(post.comments)
                        adapter.updateListComment(post.comments)
                    }
                }
                requestEditPost -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post != null && post is ICPost) {
                        adapter.updatePostOrReview(post)
                    }
                    setResult(RESULT_OK)
                }
                requestLoginV2 -> {
                    getData()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requestSentComment) {
            imgSend.performClick()
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.RESULT_DETAIL_POST_ACTIVITY, adapter.getListData.firstOrNull { it is ICPost }))
        Intent().apply {
            putExtra(Constant.DATA_1, viewModel.post)
            setResult(RESULT_OK, this)
        }
        super.onBackPressed()
    }

    override fun onLikePostDetail() {
        viewModel.likePost()
    }
}