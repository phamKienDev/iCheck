package vn.icheck.android.activities.product.review_product_v1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_review_product_v1.*
import kotlinx.android.synthetic.main.item_base_send_message_product.*
import vn.icheck.android.R
import vn.icheck.android.activities.product.review_product_v1.adapter.HorizontalImageSendAdapter
import vn.icheck.android.activities.product.review_product_v1.adapter.ReviewProductAdapter
import vn.icheck.android.activities.product.review_product_v1.holder.ListReviewHolder
import vn.icheck.android.activities.product.review_product_v1.holder.PostReviewProductHolder
import vn.icheck.android.activities.product.review_product_v1.presenter.ReviewProductPresenter
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.activities.product.review_v1.EditReviewV1Activity
import vn.icheck.android.activities.product.review_v1.ReviewTributeV1Dialog
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IHorizontalImageSendListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.TakePhotoHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.network.models.ICShare
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.review_product.model.ICReviewProduct
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText
import java.io.File

class ReviewProductV1Activity : BaseActivityMVVM(), IReviewProductView, TakePhotoHelper.TakePhotoListener, View.OnClickListener, IHorizontalImageSendListener {

    private val adapter = ReviewProductAdapter(this)
    private val imageCommentAdapter = HorizontalImageSendAdapter(this)
    private val permissionCamera = 1
    private val takePhotoHelper = TakePhotoHelper(this)
    private val listImageCriteria = mutableListOf<File>()
    private val listImageComment = mutableListOf<File>()
    private var product: ICBarcodeProductV1? = null
    private var reviewStartInsider = true
    val shareDialog = ReviewTributeV1Dialog()
    private val presenter = ReviewProductPresenter(this@ReviewProductV1Activity)

    companion object {
        //post criteria
        var message = ""
        var listYourCriteria = mutableListOf<HashMap<String, Any>>()

        var sendImageCriteria = true
        const val OK = 1
        const val REVIEW = 11
        const val EDIT = 22
        fun showReviews(barcode: String, productId: Long, activity: Activity) {
            val intent = Intent(activity, ReviewProductV1Activity::class.java)
            intent.putExtra("barcode", barcode)
            intent.putExtra("productId", productId)
            activity.startActivityForResult(intent, REVIEW)
        }

        fun showReviews(id: String, activity: Activity) {
            val intent = Intent(activity, ReviewProductV1Activity::class.java)
            activity.startActivityForResult(intent, REVIEW)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_product_v1)

        onInitView()
    }

    fun onInitView() {
        DialogHelper.showShimmer(shimmer_view_container)
        initRecyclerView()
        initEdittext()
        initSwipeLayout()
        initRecyclerViewImageComment()
        presenter.getBarcodeProduct(intent)
        WidgetUtils.setClickListener(this, img_choose_image, tv_answer_actor, img_send, tv_answer_actor, imgBack, container_comment)

        linearLayoutActor.background=ViewHelper.bgTransparentStrokeLineColor1Corners10(this)
        tv_answer_actor.background=ViewHelper.bgTransparentStrokeLineColor1Corners10(this)
        view28.background=ViewHelper.bgWhiteCornersTop16(this)
    }

    private fun initSwipeLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipe_layout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

        swipe_layout.setOnRefreshListener { getData() }
    }

    private fun getData() {
        swipe_layout.isRefreshing = true
        adapter.disLoadmore()
        presenter.getBarcodeProduct(intent)
    }

    private fun initEdittext() {
        edt_enter_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    img_send.setImageResource(R.drawable.ic_chat_send_24px)
                    img_send.isEnabled = true
                } else {
                    img_send.setImageResource(R.drawable.ic_chat_send_gray_24_px)
                    img_send.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }


    private fun initRecyclerView() {
        recycler_review_product.adapter = adapter
        recycler_review_product.layoutManager = LinearLayoutManager(this)
    }

    private fun initRecyclerViewImageComment() {
        rcv_send_image.adapter = imageCommentAdapter
        rcv_send_image.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun hideShimmer() {
        DialogHelper.hideShimmer(shimmer_view_container, container_header)
        view28.visibility = View.VISIBLE
        swipe_layout.visibility = View.VISIBLE
    }


    override fun onLoadmore() {
        presenter.getProductReviews()
    }

    override fun onClickTryAgain() {
        presenter.getBarcodeProduct(intent)
    }


    override fun onResetData() {
        adapter.resetData()
    }

    override fun onSetInfoProduct(product: ICBarcodeProductV1) {
        swipe_layout.isRefreshing = false
        this.product = product
        txt_title_toolbar.text = product.name
        if (product.attachments.isNotEmpty()) {
            WidgetUtils.loadImageUrl(imgProduct, product.attachments.first().thumbnails.small)
        }
    }


    override fun onYourReview(criteria: ICReviewProduct) {
        hideShimmer()
        recycler_review_product.smoothScrollToPosition(0)
        swipe_layout.isRefreshing = false
        if (criteria.criteria?.productEvaluation?.averagePoint != null || criteria.criteria?.productEvaluation?.averagePoint == 0F) {
            tv_score.text = criteria.criteria?.productEvaluation?.averagePoint?.times(2).toString()
        } else {
            tv_score.text = "0.0"
        }
        criteria.criteria?.productEvaluation?.averagePoint?.let { point ->
            review_rate.rating = point
        }
        criteria.criteria?.productEvaluation?.averagePoint?.let {
            tv_score_text.text = ReviewPointText.getText(it)
        }
        adapter.addYourCriteria(criteria)
    }

    override fun onGetProductCriteriaReviewsSuccess(criteria: MutableList<ICReviewProduct>) {
        hideShimmer()
        swipe_layout.isRefreshing = false
        adapter.addAllReview(criteria)
    }

    override fun onPostProductReview(msg: String, listCriteria: MutableList<HashMap<String, Any>>) {
        if (!SessionManager.isUserLogged) {
            showShortError(R.string.vui_long_dang_nhap_tai_khoan_cua_ban)
            return
        }
        if (listImageCriteria.isNullOrEmpty()) {
            presenter.postProductReview(msg, listCriteria)
        } else {
            presenter.uploadImageReview(msg, listImageCriteria, listCriteria)
        }
    }


    override fun onShareYourReview(review: ICProductReviews.ReviewsRow) {
        product?.let { TrackingAllHelper.trackProductReviewSuccess(it) }
        shareDialog.setAction(object : ReviewTributeV1Dialog.ReviewTributeAction {
            override fun onDismiss() {
                shareDialog.dismiss()

            }

            override fun onShare() {
                share(review.id, review.message, review.averagePoint)
            }

        })
        shareDialog.show(supportFragmentManager, null)
        shareDialog.isCancelable = false
    }

    override fun onClickEditReview(criteria: ICCriteria) {
        product?.let {
            val edit = Intent(this@ReviewProductV1Activity, EditReviewV1Activity::class.java)
            edit.putExtra("criteria", criteria)
            if (it.attachments.isNotEmpty()) {
                edit.putExtra("img", it.attachments.first().thumbnails.original)
            }
            edit.putExtra("name", it.name)
            edit.putExtra("id", it.id)
            startActivityForResult(edit, EDIT)
        }
    }

    override fun onGetDataError(icon: Int, error: String) {
        hideShimmer()
        swipe_layout.isRefreshing = false
        if (adapter.sizeData() > 0) {
            showShortError(error)
        } else {
            adapter.setError(error, icon)
        }
    }


    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        if (event.type == ICMessageEvent.Type.ON_LOG_IN) {
            if (SessionManager.isUserLogged) onPostProductReview(message, listYourCriteria)
        }
    }

    override fun pickPhotoCriteria() {
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (PermissionHelper.checkPermission(this, permission, permissionCamera)) {
            takePhotoHelper.takeMultiPhoto(this)
        }

    }

    override fun deletePhotoCriteria(pos: Int) {
        listImageCriteria.removeAt(pos)
        val holder = recycler_review_product.findViewHolderForAdapterPosition(0)
        if (holder != null && holder is PostReviewProductHolder) {
            holder.deleteItemImage(pos)
        }
    }

    override fun onGetListComments(reviewPosition: Int, positionShowComment: Int, reviewId: Long, dataSize: Int) {
        presenter.getListComment(reviewId, dataSize, reviewPosition, positionShowComment)
    }

    override fun onGetListCommentsSuccess(reviewPosition: Int, positionShowComment: Int, list: MutableList<ICProductReviews.Comments>) {
        hideShimmer()
        val holder = recycler_review_product.findViewHolderForAdapterPosition(reviewPosition)
        if (holder != null && holder is ListReviewHolder) {
            adapter.addListComment(list, reviewPosition)
            holder.addListComment(list)
            holder.hideShowMoreComment(positionShowComment)
        }

    }

    override fun onClickCreateComment(reviewPosition: Int, nameOwner: String, reviewId: Long) {

        container_comment.visibility = View.VISIBLE
        view1.visibility = View.VISIBLE
        progress_send.visibility = View.GONE
        img_send.visibility = View.VISIBLE

        KeyboardUtils.showSoftInput(edt_enter_message)

        tv_answer_actor.text = Html.fromHtml(ViewHelper.setSecondaryHtmlString(resources.getString(R.string.binh_luan_xxx, nameOwner)))

        img_send.setOnClickListener {
            if (edt_enter_message.text.toString().isNotEmpty()) {
                if (!SessionManager.isUserLogged) {
                    showShortError(getString(R.string.vui_long_dang_nhap_tai_khoan_cua_ban))
                } else {
                    img_send.visibility = View.GONE
                    progress_send.visibility = View.VISIBLE
                    if (listImageComment.isNotEmpty()) {
                        presenter.uploadImageComment(edt_enter_message.text.toString(), listImageComment, reviewId, reviewPosition)
                    } else {
                        presenter.postComment(edt_enter_message.text.toString(), reviewId, reviewPosition)

                    }
                }
            }

        }
    }


    override fun onPostCommentSuccess(reviewPosition: Int, comment: ICProductReviews.Comments) {
        KeyboardUtils.hideSoftInput(edt_enter_message)
        edt_enter_message.setText("")
        img_send.visibility = View.VISIBLE
        progress_send.visibility = View.GONE
        container_image.visibility = View.GONE
        container_comment.visibility = View.GONE
        listImageComment.clear()
        imageCommentAdapter.clearData()

        if (comment.owner == null) {
            comment.owner = ICProductReviews.Owner(SessionManager.session.user?.name, SessionManager.session.user?.avatar, ICProductReviews.ImageThumbs(SessionManager.session.user?.avatar_thumbnails?.small, SessionManager.session.user?.avatar_thumbnails?.small, SessionManager.session.user?.avatar_thumbnails?.small), "user", SessionManager.session.user?.id)
        }

        val holder = recycler_review_product.findViewHolderForAdapterPosition(reviewPosition)
        if (holder != null && holder is ListReviewHolder) {
            adapter.addItemCommet(comment, reviewPosition)
            holder.insertComment(comment)
        } else {
            adapter.addItemCommet(comment, reviewPosition)
            adapter.notifyItemChanged(reviewPosition)
        }
    }

    override fun onPostCommentError() {
        showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        progress_send.visibility = View.GONE
        img_send.visibility = View.VISIBLE
    }


    override fun onVoteReviews(id: Long, vote: String) {
        val body = hashMapOf<String, Any?>()
        body.put("action", vote)

        val host = APIConstants.defaultHost + APIConstants.CRITERIAVOTEREVIEW().replace("{id}", id.toString())
        ICNetworkClient.getApiClient().postVoteReview(host, body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : SingleObserver<ICProductReviews.Comments> {
            override fun onSuccess(t: ICProductReviews.Comments) {

            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
            }
        })
    }

    override fun onShowDetailUser(id: Long?, type: String?) {
        if (type == "user" && id != null) {
            IckUserWallActivity.create(id, this)
        }
        if (type == "page" && id != null) {
            PageDetailActivity.start(this, id, Constant.PAGE_ENTERPRISE_TYPE)
        }
        if (type == "shop" && id != null) {
            PageDetailActivity.start(this, id, Constant.PAGE_ENTERPRISE_TYPE)
//            ShopDetailActivity.start(id, this@ReviewProductV1Activity)
        }
    }

    override fun onProductReviewStartInsider() {
        if (reviewStartInsider) {
            product?.let { TrackingAllHelper.trackProductReviewStart(it) }
            reviewStartInsider = false
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@ReviewProductV1Activity

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@ReviewProductV1Activity, isShow)
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
        when (requestCode) {
            EDIT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data?.getIntExtra("result", 0)
                    if (result == OK) {
                        recreate()
                    }
                }
            }
        }
    }

    override fun onTakePhotoSuccess(file: File?) {
        if (sendImageCriteria) {
            file?.let {
                listImageCriteria.add(it)
                val holder = recycler_review_product.findViewHolderForAdapterPosition(0)
                if (holder != null && holder is PostReviewProductHolder) {
                    holder.createListImage(listImageCriteria)
                }
            }
        } else {
            file?.let {
                container_image.visibility = View.VISIBLE
                listImageComment.add(it)
                imageCommentAdapter.setItem(it)
            }
        }
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {
        if (sendImageCriteria) {
            listImageCriteria.addAll(file)
            val holder = recycler_review_product.findViewHolderForAdapterPosition(0)
            if (holder != null && holder is PostReviewProductHolder) {
                holder.createListImage(listImageCriteria)
            }
        } else {
            container_image.visibility = View.VISIBLE
            listImageComment.addAll(file)
            imageCommentAdapter.setData(file)
        }
    }

    fun share(id: Long, message: String, averagePoint: Float) {
        val host = APIConstants.defaultHost + APIConstants.SHARELINK()
        ICNetworkClient.getShareClient().testShareLink(id, "product").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : SingleObserver<ICShare> {
            override fun onSuccess(t: ICShare) {
                if (t.link.isNotEmpty()) {
                    val share = Intent()
                    share.setAction(Intent.ACTION_SEND)
                    share.putExtra(Intent.EXTRA_SUBJECT, product?.name)
                    share.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.chia_se_danh_gia, averagePoint * 2, message, t.link))
                    share.setType("text/plain")
                    startActivity(Intent.createChooser(share, "Chia sẻ ${product?.name}"))
                }
                shareDialog.dismiss()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                shareDialog.dismiss()
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_answer_actor -> {
                container_comment.visibility = View.GONE
                container_image.visibility = View.GONE
                view1.visibility = View.GONE
                edt_enter_message.setText("")
                listImageComment.clear()
                listImageCriteria.clear()
                KeyboardUtils.hideSoftInput(this)
            }
            R.id.img_choose_image -> {
                pickPhotoCriteria()
                sendImageCriteria = false
            }
            R.id.imgBack -> {
                onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun onClickDeleteImageSend(position: Int, size: Int) {
        if (size > 1) {
            listImageComment.removeAt(position)
            imageCommentAdapter.deleteItem(position)
        } else {
            listImageComment.clear()
            container_image.visibility = View.GONE
            imageCommentAdapter.deleteData()
        }
    }

}
