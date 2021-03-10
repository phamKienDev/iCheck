package vn.icheck.android.screen.user.profile.userprofile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.activities.chat.v2.ChatV2Activity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.user.follow.FollowActivity
import vn.icheck.android.screen.user.profile.ProfileActivity
import vn.icheck.android.screen.user.profile.base.adapter.ShortUserFollowingAdapter
import vn.icheck.android.screen.user.profile.userprofile.presenter.UserProfilePresenter
import vn.icheck.android.screen.user.profile.userprofile.view.IUserProfileView
import vn.icheck.android.screen.user.viewimage.ViewImageActivity
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

/**
 * Created by VuLCL on 11/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class UserProfileFragment : BaseFragment<UserProfilePresenter>(), IUserProfileView, View.OnClickListener {
    private val adapter = ShortUserFollowingAdapter(this)

    companion object {
        fun newInstance(userID: Long): UserProfileFragment {
            val fragment = UserProfileFragment()

            val bundle = Bundle()
            bundle.putLong(Constant.DATA_1, userID)
            fragment.arguments = bundle

            return fragment
        }
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_user_profile

    override val getPresenter: UserProfilePresenter
        get() = UserProfilePresenter(this)

    override fun onInitView() {
        initToolbar()
        setupRecyclerView()
        initListener()

        presenter.getUserID(arguments)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.ca_nhan)

        imgAction.visibility = View.VISIBLE
        imgAction.setImageResource(R.drawable.ic_chat_user_blue_36dp)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        imgAction.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 4))
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun initListener() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.blue), ContextCompat.getColor(requireContext(), R.color.blue), ContextCompat.getColor(requireContext(), R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            presenter.getUserProfile(false)
        }

        WidgetUtils.setClickListener(this, imgAvatar, imgBackground, imgFollow, imgPost, imgMessage, txtViewAll)
    }

    override fun onNotLogged() {
        DialogHelper.showNotification(context, R.string.vui_long_dang_nhap_tai_khoan_cua_ban, false, object : NotificationDialogListener {
            override fun onDone() {
                activity?.onBackPressed()
            }
        })
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onGetUserProfileError(error: String) {
        DialogHelper.showConfirm(context, error, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                activity?.onBackPressed()
            }

            override fun onAgree() {
                presenter.getUserProfile(false)
            }
        })
    }

    override fun onGetUserProfileSuccess(user: ICUser) {
        txtTitle.text = user.name

        WidgetUtils.loadImageUrl(imgAvatar, user.avatar_thumbnails?.medium, R.drawable.ic_circle_avatar_default, R.drawable.ic_circle_avatar_default)
        WidgetUtils.loadImageUrl(imgBackground, user.cover_thumbnails?.medium, R.drawable.bg_header_home_drawer, R.drawable.bg_header_home_drawer)

        tvVerified.visibility = if (user.phone_verified) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (user.user_followed) {
            txtFollowTitle.setText(R.string.dang_theo_doi)
            imgFollow.setImageResource(R.drawable.ic_circle_followed_white_40dp)
        } else {
            txtFollowTitle.setText(R.string.theo_doi)
            imgFollow.setImageResource(R.drawable.ic_circle_unfollow_white_40dp)
        }

        tvName.text = (user.last_name + " " + user.first_name)

        val gender = if (user.gender == "male") R.string.nam else R.string.nu
        txtGender.setText(gender)

        txtBirthday.text = (user.birth_day.toString() + "/" + user.birth_month + "/" + user.birth_year)
        txtPhone.text = user.phone
        txtJoin.text = TimeHelper.convertDateTimeSvToDateVn(user.created_at) ?: "_"
        txtEmail.text = user.email
        txtAddress.text = TextHelper.getFullAddress(user.address, user.ward, user.district, user.city, user.country)
        txtId.text = ("i-${user.id}")
        txtFollowing.text = getString(R.string.xxx_nguoi, user.following_count.toString())
        txtFollower.text = getString(R.string.xxx_nguoi, user.follower_count.toString())
    }

    override fun onShowFollowing(totalCount: Int, list: MutableList<ICUserFollowing>) {
        swipeLayout.isRefreshing = false

        tvCount.text = getString(R.string.xxx_nguoi, totalCount.toString())
        adapter.setData(list)
    }

    override fun onMessageFollowingClicked() {

    }

    override fun onFollowingClicked(obj: ICUserFollowing) {
        obj.user?.id?.let {
            if (it == SessionManager.session.user?.id) {
                startActivity<ProfileActivity>()
            } else {
                startActivity<ProfileActivity, Long>(Constant.DATA_1, it)
            }
        }
    }

    override fun onSetFollow(isFollow: Boolean) {
        if (isFollow) {
            txtFollowTitle.setText(R.string.dang_theo_doi)
            imgFollow.setImageResource(R.drawable.ic_circle_followed_white_40dp)
        } else {
            txtFollowTitle.setText(R.string.theo_doi)
            imgFollow.setImageResource(R.drawable.ic_circle_unfollow_white_40dp)
        }
    }

    override fun onUpdateAvatarOrCoverSuccess(file: File, type: Int) {
        showLongSuccess(R.string.cap_nhat_thanh_cong)

        when (type) {
            1 -> {
                WidgetUtils.loadImageFile(imgAvatar, file)
            }
            2 -> {
                WidgetUtils.loadImageFile(imgBackground, file)
            }
        }
    }

    override fun onClick(view: View?) {
        if (swipeLayout.isRefreshing) {
            return
        }

        when (view?.id) {
            R.id.imgAvatar -> {
                presenter.getListImage?.let {
                    val intent = Intent(context, ViewImageActivity::class.java)
                    intent.putExtra(Constant.DATA_1, it)
                    intent.putExtra(Constant.DATA_2, 0)
                    startActivity(intent)
                }
            }
            R.id.imgBackground -> {
                presenter.getListImage?.let {
                    val intent = Intent(context, ViewImageActivity::class.java)
                    intent.putExtra(Constant.DATA_1, it)
                    intent.putExtra(Constant.DATA_2, 1)
                    startActivity(intent)
                }
            }
            R.id.imgFollow -> {
                if (presenter.isFollow) {
                    imgFollow.isClickable = false

                    DialogHelper.showConfirm(context, R.string.huy_theo_doi,
                            R.string.ban_co_chac_chan_muon_huy_theo_doi_nguoi_nay,
                            R.string.bo_qua, R.string.dong_y, true,
                            object : ConfirmDialogListener {
                                override fun onDisagree() {

                                }

                                override fun onAgree() {
                                    presenter.deleteFollowUser()
                                }
                            })

                    Handler().postDelayed({
                        imgFollow.isClickable = true
                    }, 500)
                } else {
                    presenter.addFollowUser()
                }
            }
            R.id.imgPost -> {

            }
            R.id.imgMessage -> {
                ChatV2Activity.createChatUser(presenter.getUser.id, requireActivity())
            }
            R.id.txtViewAll -> {
                startActivity<FollowActivity, Long>(Constant.DATA_1, presenter.getUserID)
            }
        }
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }
}