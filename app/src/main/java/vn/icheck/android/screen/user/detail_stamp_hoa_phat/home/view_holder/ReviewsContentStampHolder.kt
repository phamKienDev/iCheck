package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ctsp_reviewcontent_holder_v1.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.image.DetailImagesActivity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewsTimeUtils
import vn.icheck.android.util.ui.GlideUtil

class ReviewsContentStampHolder(parent: ViewGroup) : BaseViewHolder<ICProductReviews.ReviewsRow>(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_reviewcontent_holder_v1, parent, false)) {
    var useful = 0
    var unuseful = 0

    override fun bind(obj: ICProductReviews.ReviewsRow) {
        WidgetUtils.loadImageUrl(itemView.one_user, obj.owner.avatarThumb?.small)
        itemView.user_1_rating.rating = obj.averagePoint
        itemView.tv_user_1_name.text = obj.owner.name
        itemView.tv_user_1_name.setOnClickListener {
            showDetailUser(obj.customerId)
        }

        val message = obj.message
        itemView.tv_user_1_comment.text = message

        itemView.tv_1_useful.text = setTextUseful(obj.useful)
        itemView.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)

        if (message != null && message.isNotEmpty() && obj.customerId != SessionManager.session.user?.id) {
            itemView.gv_useful.visibility = View.VISIBLE
        } else {
            itemView.gv_useful.visibility = View.GONE
        }

        obj.actionUseful?.let {
            if ("useful" == it) {
                itemView.tv_1_useful.text = setTextUseful(obj.useful)
                itemView.tv_1_useful.setTextColor(Color.parseColor("#3C5A99"))
                itemView.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_fc_24px, 0, 0, 0)
                useful = 1
            } else if ("unuseful" == it) {
                itemView.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                itemView.tv_1_unuseful.setTextColor(Color.parseColor("#3C5A99"))
                itemView.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_fc_24px, 0, 0, 0)
                unuseful = 1
            }
        }
        itemView.tv_1_useful.setOnClickListener {
            if (SessionManager.isUserLogged && obj.customerId != SessionManager.session.user?.id) {
                if (useful == 0) {
                    obj.useful++
                    itemView.tv_1_useful.text = setTextUseful(obj.useful)
                    itemView.tv_1_useful.setTextColor(Color.parseColor("#3C5A99"))
                    itemView.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_fc_24px, 0, 0, 0)
                    useful = 1
                    if (unuseful == 1) {
                        obj.unuseful--
                        itemView.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                        itemView.tv_1_unuseful.setTextColor(Color.parseColor("#828282"))
                        itemView.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_unfc_24px, 0, 0, 0)
                        unuseful = 0
                    }
                    voteReviews(obj.id, "useful")
                } else {
                    obj.useful--
                    itemView.tv_1_useful.text = setTextUseful(obj.useful)
                    itemView.tv_1_useful.setTextColor(Color.parseColor("#828282"))
                    itemView.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_unfc_24px, 0, 0, 0)
                    useful = 0
                    voteReviews(obj.id, "")
                }

            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOGIN))
            }
        }
        itemView.tv_1_unuseful.setOnClickListener {
            if (SessionManager.isUserLogged && obj.customerId != SessionManager.session.user?.id) {
                if (unuseful == 0) {
                    obj.unuseful++
                    itemView.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                    itemView.tv_1_unuseful.setTextColor(Color.parseColor("#3C5A99"))
                    itemView.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_fc_24px, 0, 0, 0)
                    unuseful = 1
                    if (useful == 1) {
                        obj.useful--
                        itemView.tv_1_useful.text = setTextUseful(obj.useful)
                        itemView.tv_1_useful.setTextColor(Color.parseColor("#828282"))
                        itemView.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_unfc_24px, 0, 0, 0)
                        useful = 0
                    }
                    voteReviews(obj.id, "unuseful")
                } else {
                    obj.unuseful--
                    itemView.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                    itemView.tv_1_unuseful.setTextColor(Color.parseColor("#828282"))
                    itemView.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_unfc_24px, 0, 0, 0)
                    unuseful = 0
                    voteReviews(obj.id, "")
                }

            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOGIN))
            }
        }

        itemView.tv_1_detail.setOnClickListener {
            if (itemView.rcv_rating.visibility == View.GONE) {
                itemView.rcv_rating.visibility = View.VISIBLE
                itemView.detail.visibility = View.GONE
                itemView.coplapse.visibility = View.VISIBLE


            } else {
                itemView.rcv_rating.visibility = View.GONE
                itemView.detail.visibility = View.VISIBLE
                itemView.coplapse.visibility = View.GONE
                itemView.tv_1_time.text = ReviewsTimeUtils(obj).getTime()
            }
        }
        itemView.one_user.setOnClickListener {
            showDetailUser(obj.customerId)
        }
        if (obj.customerCriteria.isNotEmpty()) {

            val listCriteriaChild = mutableListOf<CriteriaChild>()
            for (item in obj.customerCriteria.indices) {
                listCriteriaChild.add(CriteriaChild(obj.customerCriteria.get(item), obj.customerCriteria.get(item).point?.toFloat(), true))
            }
            val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
            itemView.rcv_rating.adapter = criteriaAdapter
            itemView.rcv_rating.isNestedScrollingEnabled = false
            itemView.rcv_rating.layoutManager = LinearLayoutManager(itemView.context)
        }
        itemView.tv_1_time.text = ReviewsTimeUtils(obj).getTime()

        if (obj.imageThumbs.isNotEmpty()) {
            itemView.img_collection.visibility = View.VISIBLE
            itemView.img_collection.setOnClickListener {
                val listImg = arrayListOf<String?>()
                for (item in obj.imageThumbs) {
                    listImg.add(item.original)
                }

                ICheckApplication.currentActivity()?.let { activity ->
                    DetailImagesActivity.start(listImg, activity)
                }
            }
            when (obj.imageThumbs.size) {
                1 -> {
                    WidgetUtils.loadImageUrlRounded(itemView.img_1, obj.imageThumbs.first().small, SizeHelper.size10)
                }
                2 -> {
                    WidgetUtils.loadImageUrlRounded(itemView.img_1, obj.imageThumbs.first().small, SizeHelper.size10)
                    WidgetUtils.loadImageUrlRounded(itemView.img_2, obj.imageThumbs.last().small, SizeHelper.size10)
                }
                3 -> {
                    WidgetUtils.loadImageUrlRounded(itemView.img_1, obj.imageThumbs.first().small, SizeHelper.size10)
                    WidgetUtils.loadImageUrlRounded(itemView.img_2, obj.imageThumbs.get(1).small, SizeHelper.size10)
                    WidgetUtils.loadImageUrlRounded(itemView.img3, obj.imageThumbs.last().small, SizeHelper.size10)
                }
                else -> {
                    WidgetUtils.loadImageUrlRounded(itemView.img_1, obj.imageThumbs.first().small, SizeHelper.size10)
                    WidgetUtils.loadImageUrl(itemView.img_2, obj.imageThumbs.get(1).small, SizeHelper.size10)
                    GlideUtil.bind(R.drawable.group_more_product, itemView.img3)
                }
            }
        }
    }

    fun setTextUseful(number: Long): String {
        return if (number > 0) {
            String.format("Hữu ích (%d)", number)
        } else {
            "Hữu ích"
        }
    }

    fun setTextUnUseful(number: Long): String {
        return if (number > 0) {
            String.format("Không hữu ích (%d)", number)
        } else {
            "Không hữu ích"
        }
    }

    private fun showDetailUser(id: Long) {
//        ICheckApplication.currentActivity()?.let { activity ->
//            UserInteractor().getUserChatByID(id, object : ICApiListener<ICUserId> {
//                override fun onSuccess(obj: ICUserId) {
//                    if (obj.type == "user") {
//                        ActivityUtils.startActivity<ProfileActivity, Long>(activity, Constant.DATA_1, obj.id!!)
//                    }
//                    if (obj.type == "page") {
//                        ICheckApplication.currentActivity()?.let {
//                            if (obj.id != null) {
//                                PageDetailActivity.start(it, obj.id!!)
//                            }
//                        }
//                    }
//                    if (obj.type == "shop") {
//                        ShopDetailActivity.start(obj.id, activity)
//                    }
//                }
//
//                override fun onError(error: ICBaseResponse?) {
//
//                }
//            })
//        }
    }

    fun voteReviews(id: Long, vote: String?) {
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
}