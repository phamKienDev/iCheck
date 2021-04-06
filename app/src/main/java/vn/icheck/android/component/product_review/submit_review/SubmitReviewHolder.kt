package vn.icheck.android.component.product_review.submit_review

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.list_image_send.ListImageSendAdapter
import vn.icheck.android.component.review.ReviewBottomSheetAdapter
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICReqCriteriaReview
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.ui.layout.CustomLinearLayoutManager
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File
import java.lang.Exception

class SubmitReviewHolder(parent: ViewGroup, val recycledViewPool: RecyclerView.RecycledViewPool?, val listener: ISubmitReviewListener) : RecyclerView.ViewHolder(ViewHelper.createFormSubmitReview(parent.context)) {
    lateinit var rcvRating: RecyclerView
    lateinit var rcvImage: RecyclerView
    lateinit var edtEnter: AppCompatEditText
    lateinit var imgCamera: AppCompatImageView
    lateinit var imgPermission: CircleImageView
    lateinit var btnPermission: AppCompatImageView
    lateinit var btnSubmit: AppCompatTextView

    /*lưu giá trị gửi đánh giá*/
    lateinit var listImageAdapter: ListImageSendAdapter
    private val listImageSend = mutableListOf<File>()
    private var listImageString = mutableListOf<String>()

    fun bind(obj: SubmitReviewModel) {
        (itemView as ViewGroup).run {
            rcvRating = getChildAt(2) as RecyclerView
            (getChildAt(3) as LinearLayout).run {
                edtEnter = getChildAt(0) as AppCompatEditText
                imgCamera = getChildAt(2) as AppCompatImageView
            }
            rcvImage = getChildAt(4) as RecyclerView
            (getChildAt(5) as ViewGroup).run {
                imgPermission = getChildAt(0) as CircleImageView
                btnPermission = getChildAt(1) as AppCompatImageView
                btnSubmit = getChildAt(2) as AppCompatTextView
            }
        }

        setUpRcvImage()

        if (!obj.data.isNullOrEmpty()) {
            rcvRating.adapter = ReviewBottomSheetAdapter(obj.data, true)
            rcvRating.layoutManager = CustomLinearLayoutManager(itemView.context)
        }

        if (!SessionManager.isUserLogged) {
            imgPermission.visibility = View.GONE
            btnPermission.visibility = View.GONE
        } else {
            imgPermission.visibility = View.VISIBLE
            btnPermission.visibility = View.VISIBLE
        }

        if (SettingManager.getPostPermission() != null) {
            WidgetUtils.loadImageUrl(imgPermission, SettingManager.getPostPermission()?.avatar, if (SettingManager.getPostPermission()?.type == Constant.USER) {
                R.drawable.ic_avatar_default_84px
            } else {
                R.drawable.ic_business_v2
            })
        }

        imgCamera.setOnClickListener {
            listener.onTakeImage(adapterPosition)
        }
        imgPermission.setOnClickListener {
            showSelectPermission()
        }
        btnPermission.setOnClickListener {
            showSelectPermission()
        }
        edtEnter.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener if (edtEnter.isFocused) {
                EventBus.getDefault().post(bindingAdapterPosition)
                true
            } else {
                false
            }
        }
        btnSubmit.setOnClickListener {
            btnSubmit.isClickable = false
            var validate = true
            val criteria = mutableListOf<ICReqCriteriaReview>()
            for (i in obj.data.indices) {
                val item = ICReqCriteriaReview(0, 0f, 0)
                if (obj.data[i].criteriaId != null) {
                    item.criteriaId = obj.data[i].criteriaId!!
                    item.criteriaSetId = obj.data[i].criteriaSetId!!
                    if (obj.data[i].point == 0f) {
                        validate = false
                    } else {
                        item.point = obj.data[i].point
                    }
                    criteria.add(item)
                }
            }
            if (validate) {
                if (SessionManager.isUserLogged) {
                    ICheckApplication.currentActivity()?.let { activity ->
                        DialogHelper.showLoading(activity)
                    }
                    listImageString.clear()
                    if (!listImageSend.isNullOrEmpty()) {
                        uploadImageToServer(obj, edtEnter.text.toString(), criteria)
                    }else{
                        postReview(obj, edtEnter.text.toString(), criteria)
                    }
                } else {
                    btnSubmit.isClickable = true
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REQUEST_POST_REVIEW, adapterPosition))
                }
            } else {
                btnSubmit.isClickable = true
                ToastUtils.showShortError(itemView.context, itemView.context.getString(R.string.vui_long_dien_day_du_tieu_chi))
            }
        }
    }

    private fun setUpRcvImage() {
        listImageAdapter = ListImageSendAdapter(object : ListImageSendAdapter.IListImageSendListener {
            override fun onClickDeleteImageSend(position: Int) {
                listImageAdapter.deleteItem(position)
                listImageSend.removeAt(position)
            }
        })
        rcvImage.layoutManager = CustomLinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, reverseLayout = false, isScrollEnabled = true)
        rcvImage.setRecycledViewPool(recycledViewPool)
        rcvImage.adapter = listImageAdapter
    }

    private fun showSelectPermission() {
        ICheckApplication.currentActivity()?.let {
            PermissionBottomSheet(object : PermissionBottomSheet.PermissionListener {
                override fun getPermission(permission: ICCommentPermission?) {
                    if (permission != null) {
                        WidgetUtils.loadImageUrl(imgPermission, SettingManager.getPostPermission()?.avatar, if (permission.type == Constant.USER) {
                            R.drawable.ic_user_orange_circle
                        } else {
                            R.drawable.ic_business_v2
                        })
                        listener.onClickReviewPermission()
                    }
                }

            }).show((it as AppCompatActivity).supportFragmentManager, null)

        }
    }

    fun setImage(list: MutableList<File>) {
        listImageAdapter.addData(list)
        listImageSend.addAll(list)
    }

    fun setImage(file: File) {
        listImageAdapter.addItem(file)
        listImageSend.add(file)
    }

    private fun uploadImageToServer(objReview: SubmitReviewModel, message: String, criteria: MutableList<ICReqCriteriaReview>) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            ICheckApplication.currentActivity()?.let { activity ->
                DialogHelper.closeLoading(activity)
            }
            btnSubmit.isClickable = true
            ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val listCall = mutableListOf<Deferred<Any?>>()
            listImageSend.forEach {
                listCall.add(async {
                    try {
                        val response = withTimeout(60000) { ImageHelper.uploadMediaV2(it) }
                        if (!response.data?.src.isNullOrEmpty()) {
                            listImageString.add(response.data?.src!!)
                        }
                    } catch (e: Exception) {
                        logError(e)
                    }
                })
            }

            listCall.awaitAll()

            if (!listImageString.isNullOrEmpty()) {
                postReview(objReview, message, criteria)
            } else {
                ICheckApplication.currentActivity()?.let { activity ->
                    DialogHelper.closeLoading(activity)
                }
                btnSubmit.isClickable = true
                ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        }
    }

    private fun postReview(objReview: SubmitReviewModel, message: String, criteria: MutableList<ICReqCriteriaReview>) {
        val listImage = mutableListOf<ICMedia>()
        listImageString.forEach {
            listImage.add(ICMedia(it, if (it.contains(".mp4")) {
                Constant.VIDEO
            } else {
                Constant.IMAGE
            }))
        }

        val pageId = if (SettingManager.getPostPermission() != null) {
            if (SettingManager.getPostPermission()!!.type == Constant.PAGE) {
                SettingManager.getPostPermission()!!.id
            } else {
                null
            }
        } else {
            null
        }

        ProductReviewInteractor().postReview(objReview.productId, message, criteria, listImage, pageId, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                ICheckApplication.currentActivity()?.let { activity ->
                    DialogHelper.closeLoading(activity)
                }
                if (obj.data != null) {
                    listener.onPostReviewSuccess(obj.data!!)
                } else {
                    ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                ICheckApplication.currentActivity()?.let { activity ->
                    DialogHelper.closeLoading(activity)
                }
                btnSubmit.isClickable = true
                ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

}