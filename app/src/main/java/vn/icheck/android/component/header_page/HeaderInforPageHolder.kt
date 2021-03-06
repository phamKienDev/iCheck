package vn.icheck.android.component.header_page

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnDetach
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_header_infor_page.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.header_page.bottom_sheet_header_page.IListReportView
import vn.icheck.android.component.header_page.bottom_sheet_header_page.MoreActionPageBottomSheet
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableStartText
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.network.models.feed.ICAvatarOfFriend
import vn.icheck.android.screen.user.user_follow_page.UserFollowPageActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.ick.startCallPhone
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.Serializable

class HeaderInforPageHolder(parent: ViewGroup, val listener: IListReportView) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header_infor_page, parent, false)) {
    private val listAvatarFollower = mutableListOf<String>()
    private var isFollow: Boolean? = null
    fun bind(data: ICPageOverview) {
        isFollow = null
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFollowingPageIdList, data.id.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        isFollow?.let { follow ->
                            if (!follow)
                                data.followCount += 1
                        }
                        checkFollowState(data, true)
                    } else {
                        isFollow?.let { follow ->
                            if (follow)
                                data.followCount -= 1
                        }
                        checkFollowState(data, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    logDebug(error.toString())
                }
            })

            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myOwnerPageIdList, data.id.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        itemView.imgEditAvatar.beVisible()
                        itemView.containerCreate.beVisible()
                        itemView.viewbg.beVisible()
                    } else {
                        itemView.imgEditAvatar.beGone()
                        itemView.containerCreate.beGone()
                        itemView.viewbg.beGone()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    logDebug(error.toString())
                }
            })

        } else {
            checkFollowState(data, false)
            itemView.imgEditAvatar.beGone()
            itemView.containerCreate.beGone()
            itemView.viewbg.beGone()
        }

        itemView.btnChinh.background = ViewHelper.bgPrimaryCorners4(itemView.context)

        WidgetUtils.loadImageUrl(itemView.imgAvaPage, data.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
        WidgetUtils.loadImageUrl(itemView.user_avatar, data.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
        if (data.isVerify) {
            itemView.tvNamePage.setDrawbleNextEndText(data.name ?: "", R.drawable.ic_verified_18px)
        } else {
            itemView.tvNamePage.text = data.name
        }

        itemView.tvCount.text = (data.productCount ?: 0).toString()
        itemView.tvRating.text = if (data.rating == 0.0) "0/5" else data.rating.toString() + "/5"
        itemView.tvCountScan.text = TextHelper.formatMoney(data.scanCount ?: 0)

        initClick(data)
    }

    private fun initClick(data: ICPageOverview) {
        itemView.containerCreate.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_CREATE_POST, data))
        }

        itemView.img_list_avatar.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                ActivityUtils.startActivity<UserFollowPageActivity, Serializable>(it, Constant.DATA_1, data)
            }
        }

        itemView.containerProduct.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.CLICK_PRODUCT_OF_PAGE))
        }

        itemView.imgAvaPage.onDelayClick ({
            if (!data.avatar.isNullOrEmpty()) {
                val list = mutableListOf<ICMedia>()
                list.add(ICMedia(data.avatar, type= if (data.avatar!!.contains(".mp4")) {
                    Constant.VIDEO
                } else {
                    Constant.IMAGE
                }))
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_FULL_MEDIA, list))
            }
        })

        itemView.tv_number_follow.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                ActivityUtils.startActivity<UserFollowPageActivity, Serializable>(it, Constant.DATA_1, data)
            }
        }

        itemView.btnChinh.setOnClickListener {
            if (itemView.tvChinh.text.contains("Theo d??i")) {
                listener.followAndUnFollowPage(data)
            } else {
                ChatSocialDetailActivity.createRoomChat(it.context, data.id ?: -1, "page")
            }
        }

        itemView.btnPhu.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            fillDrawableStartText(R.drawable.ic_phone_blue_16dp)
            setOnClickListener {
                val phone = data.pageDetail?.phone ?: ""
                if (phone.isNotEmpty()) {
                    DialogHelper.showConfirm(itemView.context, ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.ban_co_muon_goi_dien_thoai_den_s, phone),itemView.context),
                    null,
                    itemView.context.getString(R.string.de_sau),
                    itemView.context.getString(R.string.dong_y), null, null, true, object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            phone.startCallPhone()
                        }
                    })

                } else {
                    DialogHelper.showDialogErrorBlack(
                    itemView.context,
                    itemView.context.getString(R.string.sdt_dang_cap_nhat)
                )
                }
            }
        }

        itemView.btnMore.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            fillDrawableStartText(R.drawable.ic_more_blue_16)

            setOnClickListener {
                object : MoreActionPageBottomSheet(itemView.context, data) {
                    override fun onClickUnfollow() {
                        listener.followAndUnFollowPage(data)
                    }

                    override fun onClickStateNotification() {
                        if (!data.unsubscribeNotice) {
                            listener.subcribeNotification(data)
                        } else {
                            listener.unSubcribeNotification(data)
                        }
                    }

                    override fun onClickReportPage() {
                        if (SessionManager.isUserLogged) {
                            listener.onShowReportForm()
                        } else {
                            listener.onRequireLogin()
                        }
                    }
                }.show()
            }
        }

        itemView.imgEditAvatar.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.TAKE_IMAGE_DIALOG, ICViewTypes.HEADER_INFOR_PAGE))
        }
    }

    private fun checkFollowState(data: ICPageOverview, follow: Boolean) {
        isFollow = follow

        /*TH1: C?? b???n b?? theo d??i c??ng
          TH2: Kh??ng c?? b???n b?? theo d??i c??ng
          Check n???u m??nh theo d??i-> followCount -1
        * */
        if (!data.followers.isNullOrEmpty()) {
            listAvatarFollower.clear()
            for (i in data.followers!!) {
                listAvatarFollower.add(i.avatar)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.img_list_avatar.bind(ICAvatarOfFriend(listAvatarFollower, data.followers!!.size), 2, false, R.drawable.ic_avatar_default_84dp)
            }

            val listName = mutableListOf<String>()
            val size = if (data.followers!!.size > 2) {
                for (i in 0 until 3) {
                    if (!data.followers!![i].firstName.isNullOrEmpty()) {
                        listName.add(data.followers!![i].firstName)
                    }
                }
                data.followCount.minus(3)
            } else {
                for (i in 0 until data.followers!!.size) {
                    if (!data.followers!![i].firstName.isNullOrEmpty()) {
                        listName.add(data.followers!![i].firstName)
                    }
                }
                data.followCount.minus(data.followers!!.size)
            }

            itemView.tv_number_follow.apply {
                text =
                    if (size > 0) {
                        if (!follow) {
                            context.getString(
                                R.string.s_va_s_nguoi_khac_dang_theo_doi_trang_nay,
                                listName.toString().substring(1, listName.toString().length - 1),
                                TextHelper.formatMoney(size)
                            )
                        } else {
                            if (size.minus(1) == 0L) {
                                context.getString(
                                    R.string.s_va_ban_theo_doi_trang_nay,
                                    listName.toString().substring(1, listName.toString().length - 1)
                                )
                            } else {
                                context.getString(
                                    R.string.s_va_s_nguoi_khac_dang_theo_doi_trang_nay,
                                    listName.toString()
                                        .substring(1, listName.toString().length - 1),
                                    TextHelper.formatMoney(size - 1)
                                )
                            }
                        }
                    } else {
                        context.getString(
                            R.string.s_dang_theo_doi_trang_nay,
                            listName.toString().substring(1, listName.toString().length - 1)
                        )
                    }
            }
        } else {
            itemView.img_list_avatar.beGone()
            itemView.tv_number_follow.apply {
                text = if (!follow) {
                    if (data.followCount > 0) {
                        context.getString(
                            R.string.s_nguoi_dang_theo_doi_trang_nay,
                            TextHelper.formatMoney(data.followCount)
                        )
                    } else {
                        context.getString(R.string.chua_co_nguoi_theo_doi_trang_nay)
                    }
                } else {
                    if (data.followCount > 1) {
                        context.getString(
                            R.string.ban_va_d_nguoi_khac_dang_theo_doi_trang_nay,
                            data.followCount.minus(1)
                        )
                    } else {
                        context.getString(R.string.ban_dang_theo_doi_trang_nay)
                    }
                }
            }
        }

        itemView.tvChinh.run {
            if (follow) {
                setText(R.string.nhan_tin)
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_white_16dp, 0, 0, 0)
                data.isFollow = true
            } else {
                setText(R.string.theo_doi)
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_page, 0, 0, 0)
                data.isFollow = false
            }
            initClick(data)
        }
    }
}