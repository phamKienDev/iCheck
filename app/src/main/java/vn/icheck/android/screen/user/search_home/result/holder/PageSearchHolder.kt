package vn.icheck.android.screen.user.search_home.result.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_page_search_result_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICPageQuery
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICMyFollowingPage
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class PageSearchHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_page_search_result_holder, parent, false)) {

    private var isFollow: Boolean? = null

    fun bind(obj: ICPageQuery) {
        WidgetUtils.loadImageUrl(itemView.img_avatar, obj.avatar, R.drawable.img_default_business_logo_big)
        itemView.tv_name.text = if (!obj.name.isNullOrEmpty()) {
            obj.name
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        isFollow = null
        checkStatusFollow(obj)

        checkFollowCount(obj)

        itemView.tv_verified.visibility = if (obj.isVerify) {
            View.VISIBLE
        } else {
            View.GONE
        }

        itemView.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_PAGE, obj.id))
        }

        itemView.tv_follow_shop.setOnClickListener {
            followPage(obj)
        }

        itemView.tv_following.setOnClickListener {
//            unFollow(obj)
        }
    }

    private fun checkFollowCount(obj: ICPageQuery) {
        when {
            obj.followCount >= 1000 -> {
                val like = obj.followCount / 1000
                itemView.tv_count_like.text = itemView.context.getString(R.string.like_page_xxx, like.toString() + "K")
            }
            obj.followCount > 0 -> {
                itemView.tv_count_like.text = itemView.context.getString(R.string.like_page_xxx, obj.followCount.toString())
            }
            else -> {
                itemView.tv_count_like.setText(R.string.chua_co_nguoi_theo_doi)
            }
        }
    }


    private fun checkStatusFollow(objPage: ICPageQuery) {
        ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFollowingPageIdList, objPage.id.toString(), object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null && snapshot.value is Long) {
                    itemView.tv_follow_shop.visibility = View.INVISIBLE
                    itemView.tv_following.visibility = View.VISIBLE
                    isFollow?.let { follow ->
                        if (!follow) {
                            objPage.followCount += 1
                            checkFollowCount(objPage)
                        }
                    }
                    isFollow = true
                } else {
                    itemView.tv_follow_shop.visibility = View.VISIBLE
                    itemView.tv_following.visibility = View.INVISIBLE
                    isFollow?.let { follow ->
                        if (follow) {
                            objPage.followCount -= 1
                            checkFollowCount(objPage)
                        }
                    }
                    isFollow = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun followPage(objPage: ICPageQuery) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!SessionManager.isUserLogged) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                return
            }

            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(itemView.context, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            RelationshipInteractor().followPage(listOf(objPage.id), object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                }


                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(itemView.context, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }


    private fun unFollow(objPage: ICPageQuery) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!SessionManager.isUserLogged) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
                return
            }

            if (NetworkHelper.isNotConnected(itemView.context)) {
                ToastUtils.showLongError(itemView.context, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            RelationshipInteractor().unFollowPage(objPage.id, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(itemView.context, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }
}