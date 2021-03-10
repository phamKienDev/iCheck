package vn.icheck.android.screen.user.profile.myprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.dialog.MyProfileSettingDialog
import vn.icheck.android.screen.user.follow.FollowActivity
import vn.icheck.android.screen.user.profile.ProfileActivity
import vn.icheck.android.screen.user.profile.base.adapter.ShortUserFollowingAdapter
import vn.icheck.android.screen.user.profile.confirmphone.ConfirmNewPhoneActivity
import vn.icheck.android.screen.user.profile.myprofile.presenter.MyProfilePresenter
import vn.icheck.android.screen.user.profile.myprofile.view.IMyProfileView
import vn.icheck.android.screen.user.profile.updateprofile.UpdateProfileActivity
import vn.icheck.android.screen.user.viewimage.ViewImageActivity
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

/**
 * Created by VuLCL on 11/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class MyProfileFragment : BaseFragment<MyProfilePresenter>(), IMyProfileView, TakePhotoHelper.TakePhotoListener, View.OnClickListener {
    private val takePhotoHelper = TakePhotoHelper(this)

    private val adapter = ShortUserFollowingAdapter(this)

    private var takePhotoType = 0
    private val requestPermission = 1
    private val requestUpdateInfo = 3
    private val requestConfirmPhone = 4

    override val getLayoutID: Int
        get() = R.layout.fragment_my_profile

    override val getPresenter: MyProfilePresenter
        get() = MyProfilePresenter(this)

    override fun onInitView() {
        initToolbar()
        setupRecyclerView()
        initListener()

        presenter.getUserProfile(true, true)
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

        ViewCompat.setElevation(tvVerify, SizeHelper.size2.toFloat())
    }

    private fun initListener() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.blue), ContextCompat.getColor(context!!, R.color.blue), ContextCompat.getColor(context!!, R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            presenter.getUserProfile(true, false)
        }

        WidgetUtils.setClickListener(this, imgAvatar, tvVerify, imgBackground, imgUpdate, imgPublish, imgSetting, txtViewAll)
    }

    private fun pickPhoto(type: Int) {
        takePhotoType = type
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(permission, requestPermission)
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

    override fun onSendOtpConfirmPhoneSuccess(phone: String) {
        startActivityForResult<ConfirmNewPhoneActivity>(Constant.DATA_1, phone, requestConfirmPhone)
    }

    override fun onGetUserProfileError(error: String) {
        DialogHelper.showConfirm(context, error, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                activity?.onBackPressed()
            }

            override fun onAgree() {
                presenter.getUserProfile(true, false)
            }
        })
    }

    override fun onGetUserProfileSuccess(user: ICUser) {
        WidgetUtils.loadImageUrl(imgAvatar, user.avatar_thumbnails?.medium, R.drawable.ic_circle_avatar_default, R.drawable.ic_circle_avatar_default)
        WidgetUtils.loadImageUrl(imgBackground, user.cover_thumbnails?.medium, R.drawable.bg_header_home_drawer, R.drawable.bg_header_home_drawer)

        tvVerified.visibility =  if (user.phone_verified) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        tvVerify.visibility =  if (user.phone_verified) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        if (user.publish_fields.isNullOrEmpty()) {
            tvPublish.setText(R.string.cong_khai)
            imgPublish.setImageResource(R.drawable.ic_circle_unpublish_white_40dp)
        } else {
            tvPublish.setText(R.string.rieng_tu)
            imgPublish.setImageResource(R.drawable.ic_circle_public_white_40dp)
        }

        tvName.text = (user.last_name + " " + user.first_name)
        txtGender.setText(if (user.gender == "male") R.string.nam else R.string.nu)
        txtBirthday.text = (user.birth_day.toString() + "/" + user.birth_month + "/" + user.birth_year)
        txtPhone.text = user.phone
        txtJoin.text = TimeHelper.convertDateTimeSvToDateVn(user.created_at) ?: "_"
        txtEmail.text = user.email
        txtAddress.text = TextHelper.getFullAddress(user.address, user.ward, user.district, user.city, user.country)
        txtId.text = ("i-${user.id}")
        txtFollowing.text = getString(R.string.xxx_nguoi, user.following_count.toString())
        txtFollower.text = getString(R.string.xxx_nguoi, user.follower_count.toString())

        if (!user.phone.isNullOrEmpty()) {
            txtPhone.setOnClickListener(this)
        } else {
            txtPhone.setOnClickListener(null)
        }

        if (!user.email.isNullOrEmpty()) {
            txtEmail.setOnClickListener(this)
        } else {
            txtEmail.setOnClickListener(null)
        }
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

    override fun onPublishSuccess(isPublish: Boolean) {
        if (isPublish) {
            tvPublish.setText(R.string.rieng_tu)
            imgPublish.setImageResource(R.drawable.ic_circle_public_white_40dp)
        } else {
            tvPublish.setText(R.string.cong_khai)
            imgPublish.setImageResource(R.drawable.ic_circle_unpublish_white_40dp)
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

    override fun onTakePhotoSuccess(file: File?) {
        file?.let {
            presenter.changeAvatarOrCover(it, takePhotoType)
        }
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {

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
            R.id.tvVerify -> {
                presenter.sendOtpConfirmPhone()
            }
            R.id.imgUpdate -> {
                startActivityForResult<UpdateProfileActivity>(requestUpdateInfo)
            }
            R.id.imgPublish -> {
                if (presenter.isPublish) {
                    presenter.publishProfile(false)
                } else {
                    imgPublish.isClickable = false

                    DialogHelper.showConfirm(context, R.string.cong_khai_thong_tin,
                            R.string.ban_co_chac_muon_moi_nguoi_khac_nhin_thay_thong_tin_cua_ban,
                            object : ConfirmDialogListener {
                                override fun onDisagree() {

                                }

                                override fun onAgree() {
                                    presenter.publishProfile(true)
                                }
                            })

                    Handler().postDelayed({
                        imgPublish.isClickable = true
                    }, 500)
                }
            }
            R.id.imgSetting -> {
                context?.let {
                    imgSetting.isClickable = false

                    object : MyProfileSettingDialog(it) {
                        override fun onChangeAvatar() {
                            pickPhoto(1)
                        }

                        override fun onChangeCover() {
                            pickPhoto(2)
                        }

                        override fun onUpdateInfo() {
                            startActivityForResult<UpdateProfileActivity>(requestUpdateInfo)
                        }

                        override fun onDismiss() {
                            imgSetting.isClickable = true
                        }
                    }.show()
                }
            }
            R.id.txtViewAll -> {
                startActivity<FollowActivity, Long>(Constant.DATA_1, presenter.getUserID)
            }
            R.id.txtPhone -> {
                presenter.getUser.phone?.let { phone ->
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    startActivity(callIntent)
                }
            }
            R.id.txtEmail -> {
                presenter.getUser.email?.let { email ->
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:${email}")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showLongError(errorMessage)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            requestPermission -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    takePhotoHelper.takePhoto(this)
                } else {
                    showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhotoHelper.onActivityResult(context, requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestConfirmPhone -> {
                    presenter.getUserProfile(true, false)
                }
                requestUpdateInfo -> {
                    activity?.setResult(Activity.RESULT_OK)
                    presenter.getUserProfile(true, true)
                }
            }
        }
    }
}