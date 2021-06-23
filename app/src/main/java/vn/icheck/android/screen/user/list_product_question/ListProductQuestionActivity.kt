package vn.icheck.android.screen.user.list_product_question

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_list_product_question.*
import kotlinx.android.synthetic.main.activity_list_product_question.imgBack
import kotlinx.android.synthetic.main.activity_list_product_question.layoutPermission
import kotlinx.android.synthetic.main.activity_list_product_question.rcvChildEmoji
import kotlinx.android.synthetic.main.activity_list_product_question.rcvParentEmoji
import kotlinx.android.synthetic.main.activity_list_product_question.rcvPermission
import kotlinx.android.synthetic.main.item_base_send_message_product_v2.*
import vn.icheck.android.R
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEvent
import vn.icheck.android.lib.keyboard.KeyboardVisibilityEventListener
import vn.icheck.android.lib.keyboard.Unregistrar
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICProductQuestion
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.screen.user.commentpost.CommentPermissionAdapter
import vn.icheck.android.screen.user.commentpost.EmojiAdapter
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.screen.user.edit_comment.EditCommentActivity
import vn.icheck.android.screen.user.list_product_question.adapter.ListProductQuestionAdapter
import vn.icheck.android.screen.user.list_product_question.view.IListProductQuestionView
import vn.icheck.android.screen.user.list_product_question.viewmodel.ListProductQuestionViewModel
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.kotlin.WidgetUtils.loadImageFromVideoFile
import java.io.File

class ListProductQuestionActivity : BaseActivityMVVM(), IListProductQuestionView, View.OnClickListener {
    lateinit var viewModel: ListProductQuestionViewModel

    private val questionAdapter = ListProductQuestionAdapter(this)
    private lateinit var parentEmojiAdapter: EmojiAdapter
    private lateinit var childEmojiAdapter: EmojiAdapter
    private lateinit var permissionAdapter: CommentPermissionAdapter

    private var unregistrar: Unregistrar? = null

    private val permissionImage = 1
    private val requestEditQuestion = 1
    private val requestLoginV2 = 2

    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            layoutImage.visibility = View.VISIBLE
            layoutImage.tag = file
            if (file.absolutePath.contains(".mp4")) {
                btnPlay.beVisible()
            } else {
                btnPlay.beInvisible()
            }
            imgImage.loadImageFromVideoFile(file, null, SizeHelper.size4)
            enableCamera(true)
            checkSendStatus()
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            file?.let {
                layoutImage.visibility = View.VISIBLE
                layoutImage.tag = file
                imgImage.loadImageFromVideoFile(file, null, SizeHelper.size4)
                if (file.absolutePath.contains(".mp4")) {
                    btnPlay.beVisible()
                } else {
                    btnPlay.beInvisible()
                }
                enableCamera(true)
                checkSendStatus()
            }
        }
    }

    companion object {
        fun start(activity: Activity, productId: Long, barcode: String?, isShowKeyboard: Boolean?, requestCode: Int?) {
            val intent = Intent(activity, ListProductQuestionActivity::class.java)

            intent.putExtra(Constant.DATA_1, productId)
            if (!barcode.isNullOrEmpty())
                intent.putExtra(Constant.DATA_2, barcode)
            if (isShowKeyboard != null)
                intent.putExtra(Constant.DATA_3, isShowKeyboard)

            if (requestCode != null)
                ActivityUtils.startActivityForResult(activity, intent, requestCode)
            else
                ActivityUtils.startActivity(activity, intent)
        }

        fun start(activity: Activity, productId: Long, barcode: String?, isShowKeyboard: Boolean?, obj: ICProductQuestion, requestCode: Int?) {
            val intent = Intent(activity, ListProductQuestionActivity::class.java)

            intent.putExtra(Constant.DATA_1, productId)
            if (!barcode.isNullOrEmpty())
                intent.putExtra(Constant.DATA_2, barcode)
            if (isShowKeyboard != null)
                intent.putExtra(Constant.DATA_3, isShowKeyboard)
            intent.putExtra(Constant.DATA_4, obj)
            if (requestCode != null)
                ActivityUtils.startActivityForResult(activity, intent, requestCode)
            else
                ActivityUtils.startActivity(activity, intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_question)

        setupView()
        setupListener()
        setupRecyclerView()
        setRecyclerViewPermission()
        setupSwipeLayout()
        setupViewModel()
        checkUserLogin()
    }

    private fun setupView() {
        containerEnter.background = ViewHelper.bgOutlinePrimary1Corners4(this)
        edtContent.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getNormalTextColor(this))
    }

    private fun setupListener() {
        edtContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkSendStatus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        WidgetUtils.setClickListener(this, imgEmoji, imgCamera, tvActor, imgSend, layoutContainer, tvArrow, imgAvatar, imgCloseImage, imgBack)
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = questionAdapter
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
                    layoutImage.visibility = View.VISIBLE
                    layoutImage.tag = item.image
                    WidgetUtils.loadImageUrlRounded(imgImage, item.image, SizeHelper.size4)

                    checkShowEmoji(false)
                    checkSendStatus()
                }
            }
        })
        rcvChildEmoji.adapter = childEmojiAdapter
    }

    private fun selectPicture() {
        TakeMediaDialog.show(supportFragmentManager, this,takeMediaListener)
    }

    private fun setRecyclerViewPermission() {
        permissionAdapter = CommentPermissionAdapter(object : ItemClickListener<ICCommentPermission> {
            override fun onItemClick(position: Int, item: ICCommentPermission?) {
                item?.let { setPermission(it) }
                showLayoutPermission(false)
            }

        })
        rcvPermission.adapter = permissionAdapter
    }

    private fun showLayoutPermission(isShow: Boolean) {
        if (isShow) {
            layoutPermission.beVisible()
            tvArrow.rotation = 180f
        } else {
            layoutPermission.beGone()
            tvArrow.rotation = 0f
        }
    }

    private fun setupSwipeLayout() {
        val swipeColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
            viewModel.getData()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ListProductQuestionViewModel::class.java]

        viewModel.onSetProductName.observe(this, {
            tvProductName.text = it
        })

        viewModel.onSetProductImage.observe(this, {
            WidgetUtils.loadImageFitCenterUrl(imgProduct, it)
        })

        viewModel.onSetListQuestion.observe(this, {
            layoutLoading?.let { layout ->
                layoutContainer.removeView(layout)

                if (intent.getBooleanExtra(Constant.DATA_3, false)) {
                    intent.putExtra(Constant.DATA_3, false)
                    KeyboardUtils.showSoftInput(edtContent)
                }
            }

            if (viewModel.obj != null) {
                for (i in it) {
                    if (i is ICProductQuestion) {
                        if (i.id == viewModel.obj?.id) {
                            onAnswer(i)
                        }
                    }
                }
                viewModel.obj = null
            }

            swipeLayout.isRefreshing = false

            if (it.isNullOrEmpty())
                questionAdapter.setError(R.drawable.ic_empty_questions, "Chưa có câu hỏi cho sản phẩm này.\nHãy đặt câu hỏi để được giải đáp thắc mắc ở đây", -1)
            else
                questionAdapter.setListData(it)
        })

        viewModel.onSetPermission.observe(this, {
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

        viewModel.onAddListQuestion.observe(this, {
            questionAdapter.addListData(it)
        })

        viewModel.onAddQuestion.observe(this, {
            postCommentSuccess()

            questionAdapter.addQuestion(it)
            recyclerView.smoothScrollToPosition(0)
        })

        viewModel.onAddListAnswers.observe(this, {
            questionAdapter.addListAnswer(it)
        })

        viewModel.onAddAnswer.observe(this, {
            postCommentSuccess()

            questionAdapter.addAnswer(it)?.let { position ->
                recyclerView.smoothScrollToPosition(position)
            }
        })

        viewModel.onDeleteComment.observe(this, {
            try {
                questionAdapter.deleteComment(it)
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

        viewModel.onError.observe(this, {
            layoutLoading?.let { layout ->
                layoutContainer.removeView(layout)
            }
            swipeLayout.isRefreshing = false
            if (questionAdapter.isEmpty) {
                questionAdapter.setError(it)
            } else {
                it.message?.let { it1 -> showLongError(it1) }
            }
        })

        viewModel.onShowMessage.observe(this, {
            showLongError(it)
        })

        viewModel.onStatus.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                ICMessageEvent.Type.BACK -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
            }
        })

        viewModel.getData(intent)
    }

    private fun setPermission(obj: ICCommentPermission) {
        imgAvatar.tag = obj
        if (obj.type == Constant.PAGE) {
            WidgetUtils.loadImageUrl(imgAvatar, obj.avatar, R.drawable.ic_business_v2)
        } else {
            WidgetUtils.loadImageUrl(imgAvatar, obj.avatar, R.drawable.ic_user_svg)
        }
    }

    private fun checkUserLogin() {
        if (SessionManager.isUserLogged) {
            imgAvatar.beVisible()
            tvArrow.beVisible()
            WidgetUtils.loadImageUrl(imgAvatar, SessionManager.session.user?.avatar, R.drawable.ic_user_svg)
        } else {
            imgAvatar.beGone()
            tvArrow.beGone()
        }
    }

    private fun postCommentSuccess() {
        KeyboardUtils.hideSoftInput(edtContent)
        edtContent.text = null

        tvActor.performClick()
        imgCloseImage.performClick()

        Handler().postDelayed({
            checkShowEmoji(false)
            enableCamera(false)
            showLayoutPermission(false)
        }, 300)
    }

    private fun checkSendStatus() {
        if (!edtContent.text.isNullOrEmpty() || layoutImage.tag != null) {
            imgSend.setImageResource(R.drawable.ic_chat_send_24px)
            imgSend.isClickable = true
        } else {
            imgSend.setImageResource(R.drawable.ic_chat_send_gray_24_px)
            imgSend.isClickable = false
        }
    }

    override fun hideKeyboard() {
        hideSoftKeyboard()
    }

    override fun onLoadMoreAnswer(obj: ICCommentPostMore) {
        viewModel.getListAnswer(obj)
    }

    override fun onAnswer(obj: ICProductQuestion) {
        tvActor.visibility = View.VISIBLE
        tvActor.text = Html.fromHtml(ViewHelper.setSecondaryHtmlString(resources.getString(R.string.tra_loi_xxx, if (obj.page == null) {
            obj.user!!.getName
        } else {
            obj.page!!.getName
        }),this))
        tvActor.tag = if (obj.parentID == null) obj.id else obj.parentID

        edtContent.requestFocus()
        KeyboardUtils.showSoftInput(edtContent)
    }

    override fun onEdit(obj: ICProductQuestion) {
        Handler().postDelayed({
            EditCommentActivity.start(this, obj, viewModel.barcode, requestEditQuestion)
        }, 100)
    }

    override fun onDelete(obj: ICProductQuestion) {
        DialogHelper.showConfirm(this@ListProductQuestionActivity, "Bạn chắc chắn muốn xóa bình luận này?", null, "Để sau", "Đồng ý", true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                viewModel.deleteComment(obj)
            }
        })
    }

    override fun onMessageClicked() {
        swipeLayout.isRefreshing = true
        viewModel.getData()
    }

    override fun onLoadMore() {
        viewModel.getListQuestion(true)
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
            imgCamera.setImageResource(R.drawable.ic_camera_on_24px)
        } else {
            imgCamera.setImageResource(R.drawable.ic_camera_off_24px)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgCloseImage -> {
                layoutImage.visibility = View.GONE
                layoutImage.tag = null
                imgImage.setImageResource(android.R.color.transparent)
                checkSendStatus()
                enableCamera(false)
            }
            R.id.imgAvatar -> {
                showLayoutPermission(layoutPermission.visibility != View.VISIBLE)
            }
            R.id.tvArrow -> {
                showLayoutPermission(layoutPermission.visibility != View.VISIBLE)
            }
            R.id.imgEmoji -> {
                if (layoutEmoji.visibility == View.VISIBLE) {
                    checkShowEmoji(false)
                } else {
                    checkShowEmoji(true)
                    viewModel.getEmoji()
                }
            }
            R.id.imgCamera -> {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (PermissionHelper.checkPermission(this, permission, permissionImage)) {
                    selectPicture()
                }
            }
            R.id.tvActor -> {
                tvActor.visibility = View.GONE
                tvActor.tag = null
            }
            R.id.imgSend -> {
                if (!edtContent.text.isNullOrEmpty() || layoutImage.tag != null) {
                    if (SessionManager.isUserLogged) {
                        viewModel.send(imgAvatar.tag as ICCommentPermission?, tvActor.tag as Long?, layoutImage.tag, edtContent.text.toString())
                    } else {
                        onRequireLogin(requestLoginV2)
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionImage -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    selectPicture()
                } else {
                    showLongError(getString(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestEditQuestion) {
                (data?.getSerializableExtra(Constant.DATA_1) as ICProductQuestion?)?.let { comment ->
                    questionAdapter.updateComment(comment)
                }
            }
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        if (requestCode == requestLoginV2) {
            checkUserLogin()
            viewModel.getData()
            viewModel.getListPermission()
        }
    }

    override fun onBackPressed() {
        Intent().apply {
            setResult(RESULT_OK, this)
        }
        super.onBackPressed()
    }
}
