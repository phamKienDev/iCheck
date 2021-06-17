package vn.icheck.android.screen.user.search_home.result.holder

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_shop_search_result_holder.view.*
import kotlinx.android.synthetic.main.layout_shop_search_result_holder.view.img_avatar
import kotlinx.android.synthetic.main.layout_shop_search_result_holder.view.tv_follow_shop
import kotlinx.android.synthetic.main.layout_shop_search_result_holder.view.tv_following
import kotlinx.android.synthetic.main.layout_shop_search_result_holder.view.tv_name
import kotlinx.android.synthetic.main.layout_shop_search_result_holder.view.tv_verified
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICShopQuery
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ShopSearchHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_shop_search_result_holder, parent, false)) {

    fun bind(obj: ICShopQuery) {
        itemView.rootView.background=ViewHelper.bgWhiteStrokeLineColor0_5Corners4(itemView.context)
        itemView.tv_following.background=ViewHelper.bgGrayCorners4(itemView.context)


        WidgetUtils.loadImageUrl(itemView.img_avatar, obj.avatar)
//        checkStatusOwner(obj.id)
        checkStatusFollow(obj.id)
        itemView.tv_name.text = obj.name
        itemView.tv_info_shop.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.info_shop_x, obj.productCount.toString(), (obj.rating ?: 0 * 2).toString())))

        if (obj.isOffline) {
            itemView.img_avatar.setPadding(SizeHelper.size3, SizeHelper.size3, SizeHelper.size3, SizeHelper.size3)
            itemView.img_avatar.background=ViewHelper.bgCircleWhiteStrokeOrange1Size45(itemView.context)
            itemView.tv_verified.visibility = View.VISIBLE
        } else {
            itemView.img_avatar.setPadding(0, 0, 0, 0)
            itemView.img_avatar.setBackgroundColor(Color.TRANSPARENT)
            itemView.tv_verified.visibility = View.GONE
        }

        itemView.setOnClickListener {
            ToastUtils.showLongError(itemView.context, "Shop Detail")
        }

        itemView.tv_follow_shop.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                followShop(obj)
            }
        }

        itemView.tv_following.setOnClickListener {
            unFollow(obj)
        }
    }

    fun checkStatusOwner(id: Long) {
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            //check owner page
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myOwnerPageIdList, id.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        itemView.tv_follow_shop.visibility = View.GONE
                        itemView.tv_follow_shop.visibility = View.GONE
                    } else {
                        checkStatusFollow(id)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            itemView.tv_follow_shop.visibility = View.VISIBLE
            itemView.tv_following.visibility = View.INVISIBLE
        }
    }

    private fun checkStatusFollow(id: Long) {
        ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFollowingPageIdList, id.toString(), object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null && snapshot.value is Long) {
                    itemView.tv_follow_shop.visibility = View.INVISIBLE
                    itemView.tv_following.visibility = View.VISIBLE
                } else {
                    itemView.tv_follow_shop.visibility = View.VISIBLE
                    itemView.tv_following.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun followShop(objShop: ICShopQuery) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!SessionManager.isUserLogged) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                return
            }

            if (NetworkHelper.isNotConnected(itemView.context)) {
                ToastUtils.showLongError(itemView.context, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            RelationshipInteractor().followPage(listOf(objShop.id), object : ICNewApiListener<ICResponse<Boolean>> {
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

    private fun unFollow(objShop: ICShopQuery) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!SessionManager.isUserLogged) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                return
            }

            DialogHelper.showLoading(activity)

            ICheckApplication.currentActivity()?.let { act ->
                if (NetworkHelper.isNotConnected(act)) {
                    ToastUtils.showLongError(act, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    return
                }

                RelationshipInteractor().unFollowPage(objShop.id, object : ICNewApiListener<ICResponse<Boolean>> {
                    override fun onSuccess(obj: ICResponse<Boolean>) {
                        DialogHelper.closeLoading(activity)
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        ToastUtils.showLongError(act, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                })
            }
        }
    }
}