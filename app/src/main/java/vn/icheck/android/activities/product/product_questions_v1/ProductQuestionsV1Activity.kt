package vn.icheck.android.activities.product.product_questions_v1

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_product_questions_v1.*
import kotlinx.android.synthetic.main.item_base_send_message_product.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.callback.IHorizontalImageSendListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.TakePhotoHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.activities.product.product_questions_v1.adapter.ProductQuestionsAdapter
import vn.icheck.android.screen.user.product_questions.presenter.ProductQuestionsPresenter
import vn.icheck.android.activities.product.product_questions_v1.view.IProductQuestionsView
import vn.icheck.android.activities.product.review_product_v1.adapter.HorizontalImageSendAdapter
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICQuestionRow
import vn.icheck.android.network.models.v1.ICQuestionsAnswers
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

class ProductQuestionsV1Activity : BaseActivity<ProductQuestionsPresenter>(), IProductQuestionsView, TakePhotoHelper.TakePhotoListener, View.OnClickListener, IHorizontalImageSendListener {
    private val questionAdapter = ProductQuestionsAdapter(this)
    private var imageAdapter = HorizontalImageSendAdapter(this)

    private val takePhotoHelper = TakePhotoHelper(this)
    private val permissionCamera = 1

    override val getLayoutID: Int
        get() = R.layout.activity_product_questions_v1

    override val getPresenter: ProductQuestionsPresenter
        get() = ProductQuestionsPresenter(this)

    private var listFile = mutableListOf<File>()
    private var listFileEmpity = mutableListOf<String>()
    private var questionId = -1L
    private var positionAnswer = -1

    override fun onInitView() {
        initRecyclerView()
        initRecyclerViewImage()
        initSwipeLayout()
        initEdittext()
        imgBack.setOnClickListener {
            onBackPressed()
        }

        DialogHelper.showLoading(this)
        presenter.getProductID(intent)

        container_comment.visibility = View.VISIBLE
        tv_answer_actor.visibility = View.GONE
        WidgetUtils.setClickListener(this, img_send, img_choose_image, tv_answer_actor)
    }

    private fun initRecyclerView() {
        rcvQuestions.adapter = questionAdapter
        rcvQuestions.layoutManager = LinearLayoutManager(this)
    }

    private fun initRecyclerViewImage() {
        rcv_send_image.adapter = imageAdapter
        rcv_send_image.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initSwipeLayout() {
        swipe_container.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorPrimary))

        swipe_container.setOnRefreshListener {
            getProductQuestion()
        }

    }

    private fun initEdittext() {
        edt_enter_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    img_send.setImageResource(R.drawable.ic_chat_send_24px)
                    img_send.isClickable = true
                } else {
                    img_send.setImageResource(R.drawable.ic_chat_send_gray_24_px)
                    img_send.isClickable = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun getProductQuestion() {
        swipe_container.isRefreshing = true
        presenter.getListProductQuestions(false)
    }


    override fun onSetToolbar(product: ICBarcodeProductV1) {
        txt_title_toolbar.text = product.name
        product.attachments.let {}
        if (product.attachments.isNullOrEmpty()) {
            WidgetUtils.loadImageUrlRounded10FitCenter(imgProduct, "", R.drawable.ic_default_square, R.drawable.ic_default_square)

        } else {
            WidgetUtils.loadImageUrlRounded10FitCenter(imgProduct, product.attachments[0].thumbnails.original, R.drawable.ic_default_square, R.drawable.ic_default_square)

        }
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onGetListProductQuestionsSuccess(list: MutableList<ICQuestionRow>, isLoadmore: Boolean) {
        swipe_container.isRefreshing = false
        if (isLoadmore) {
            questionAdapter.addData(list)
        } else {
            questionAdapter.setData(list)
        }
    }

    override fun onGetListProductQuestionsError(error: String) {
        DialogHelper.closeLoading(this)
        swipe_container.isRefreshing = false
        if (questionAdapter.totalItem > 0) {
            showShortError(error)
        } else {
            questionAdapter.setErrorCode(error)
        }
    }

    override fun onLoadmore() {
        presenter.getListProductQuestions(true)
    }

    override fun onClickTryAgain() {
        getProductQuestion()
    }

    override fun onClickGetListAnswer(position: Int, questionId: Long, offset: Int) {
        presenter.getListAnswersByQuestion(position, questionId, offset)
    }

    override fun onGetListAnswerSuccess(position: Int, list: MutableList<ICQuestionsAnswers>) {
        rcvQuestions.findViewHolderForAdapterPosition(position)?.let {
            if (it is ProductQuestionsAdapter.ItemQuestionHolder) {
                it.updateAnswers(list)
            }
        }
    }

    override fun onCreateQuestionSuccess(obj: ICQuestionRow) {
        KeyboardUtils.hideSoftInput(edt_enter_message)

        container_image.visibility = View.GONE
        img_send.visibility = View.VISIBLE
        progress_send.visibility = View.GONE

        questionAdapter.addObj(obj)
        imageAdapter.clearData()
        rcvQuestions.smoothScrollToPosition(0)

        listFile.clear()
        edt_enter_message.setText("")
    }


    override fun onClickCreateAnswer(questionId: Long, actorName: String, position: Int) {
        KeyboardUtils.showSoftInput(this)
        edt_enter_message.requestFocus()
        tv_answer_actor.visibility = View.VISIBLE
        tv_answer_actor.text = Html.fromHtml(resources.getString(R.string.tra_loi_xxx, actorName))
        this.questionId = questionId
        positionAnswer = position
    }

    override fun onCreateAnswerSuccess(obj: ICQuestionsAnswers, position: Int) {
        KeyboardUtils.hideSoftInput(this)
        edt_enter_message.setText("")

        container_image.visibility = View.GONE
        img_send.visibility = View.VISIBLE
        progress_send.visibility = View.GONE
        tv_answer_actor.visibility = View.GONE


        listFile.clear()
        positionAnswer = -1
        questionId = -1L
        imageAdapter.clearData()

        questionAdapter.addAnswer(position, obj)

    }

    override fun onCreateAnswerError(error: String) {
        showShortError(error)
        img_send.visibility = View.VISIBLE
        progress_send.visibility = View.GONE

    }


    override fun onClickDetailUser(type: String, id: Long) {
        if (type == "user") {
            IckUserWallActivity.create(id,this)
        } else if (type == "page") {
            PageDetailActivity.start(this, id, Constant.PAGE_ENTERPRISE_TYPE)
        } else {
//            ShopDetailActivity.start(id, this@ProductQuestionsV1Activity)
        }
    }

    private fun pickPhoto() {
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (PermissionHelper.checkPermission(this, permission, permissionCamera)) {
            takePhotoHelper.takeMultiPhoto(this)
        }
    }

    override fun onTakePhotoSuccess(file: File?) {
        if (file != null) {
            listFile.add(file)
            container_image.visibility = View.VISIBLE
            imageAdapter.setItem(file)
        } else {
            showError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {
        if (!file.isNullOrEmpty()) {
            listFile.addAll(file)
            container_image.visibility = View.VISIBLE
            imageAdapter.setData(file)
        } else {
            showError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.img_send -> {
                if (edt_enter_message.text.toString().isEmpty()) {
                    img_send.isClickable = false
                    return
                }
                if (!SessionManager.isUserLogged) {
                    startActivity<IckLoginActivity>()
                    return
                }
                img_send.isClickable = false
                img_send.visibility = View.GONE
                progress_send.visibility = View.VISIBLE

                //typeCreate=1: tạo câu hỏi
                //typeCreate=2:tạo trả lời
                if (listFile.isNullOrEmpty()) {
                    if (tv_answer_actor.visibility == View.GONE) {
                        presenter.createQuestion(edt_enter_message.text.toString(), listFileEmpity)
                    } else {
                        presenter.createAnswer(questionId, positionAnswer, edt_enter_message.text.toString(), listFileEmpity)
                    }
                } else {
                    if (tv_answer_actor.visibility == View.GONE) {
                        presenter.uploadImage(questionId, positionAnswer, 1, edt_enter_message.text.toString(), listFile)
                    } else {
                        presenter.uploadImage(questionId, positionAnswer, 2, edt_enter_message.text.toString(), listFile)
                    }
                }


                Handler().postDelayed({
                    img_send.isClickable = true
                }, 500)
            }
            R.id.img_choose_image -> {
                img_choose_image.isClickable = false
                pickPhoto()
                Handler().postDelayed({
                    img_choose_image.isClickable = true
                }, 500)
            }
            R.id.tv_answer_actor -> {
                tv_answer_actor.visibility = View.GONE
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            permissionCamera -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    takePhotoHelper.takeMultiPhoto(this)
                } else {
                    showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhotoHelper.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyLoad()
    }

    override fun onClickDeleteImageSend(position: Int, size: Int) {
        if (size > 1) {
            imageAdapter.deleteItem(position)
        } else {
            container_image.visibility = View.GONE
            imageAdapter.deleteData()
        }
    }
}
