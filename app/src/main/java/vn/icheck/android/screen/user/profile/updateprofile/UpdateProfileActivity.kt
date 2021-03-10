package vn.icheck.android.screen.user.profile.updateprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.android.synthetic.main.toolbar_white.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.profile.ProfileActivity
import vn.icheck.android.screen.user.profile.changepassword.ChangePasswordActivity
import vn.icheck.android.screen.user.profile.confirmphone.ConfirmNewPhoneActivity
import vn.icheck.android.screen.user.profile.updateprofile.presenter.UpdateProfilePresenter
import vn.icheck.android.screen.user.profile.updateprofile.view.IUpdateProfileView
import vn.icheck.android.screen.user.selectdistrict.SelectDistrictActivity
import vn.icheck.android.screen.user.selectprovince.SelectProvinceActivity
import vn.icheck.android.screen.user.selectward.SelectWardActivity
import vn.icheck.android.util.SizeUtils
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

/**
 * Created by VuLCL on 9/15/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class UpdateProfileActivity : BaseActivity<UpdateProfilePresenter>(), IUpdateProfileView, TakePhotoHelper.TakePhotoListener, View.OnClickListener {
    private val takePhotoHelper = TakePhotoHelper(this)

    private var callbackManager: CallbackManager? = null

    private var takeImageType = 0
    private val permissionCamera = 1

    private val requestProvince = 1
    private val requestDistrict = 2
    private val requestWard = 3
    private val requestConfirmPhone = 4
    private val requestUpdate = 5
    private val requestProfile = 6

    override val getLayoutID: Int
        get() = R.layout.fragment_update_profile

    override val getPresenter: UpdateProfilePresenter
        get() = UpdateProfilePresenter(this)

    override fun onInitView() {
        initToolbar()
        setupView()
        setupFacebook()
        initListener()
        presenter.getData(intent)

        presenter.setupAddressHelper()
        presenter.getUserDetail()
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.thong_tin_nguoi_dung)

        val childLayout = layoutToolbar.getChildAt(0)
        val titleBlue = childLayout.findViewById<AppCompatTextView>(R.id.txtTitle)
        titleBlue.setText(R.string.thong_tin_nguoi_dung)
        val imgBackBlue = childLayout.findViewById<AppCompatImageButton>(R.id.imgBack)
        val imgActionBlue = childLayout.findViewById<AppCompatImageButton>(R.id.imgAction)

        imgBackBlue.setOnClickListener {
            onBackPressed()
        }

        imgIcon.setOnClickListener {
            if (presenter.getIsGoToProfile) {
                startActivityForResult<ProfileActivity>(requestProfile)
            } else {
                onBackPressed()
            }
        }
    }

    private fun setupView() {
        ViewCompat.setElevation(imgEditAvatar, SizeHelper.size2.toFloat())
        ViewCompat.setElevation(imgEditBackground, SizeHelper.size2.toFloat())
    }

    private fun setupFacebook() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                presenter.mappingFacebook(loginResult.accessToken.token)
            }

            override fun onCancel() {}

            override fun onError(exception: FacebookException) {
                showError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun initListener() {
        rdGroup.setOnCheckedChangeListener { _, _ ->
            ViewCompat.setElevation(rbMale, if (rbMale.isChecked) {
                SizeUtils.dp2px(2F).toFloat()
            } else {
                0F
            })

            ViewCompat.setElevation(rbFemale, if (rbFemale.isChecked) {
                SizeUtils.dp2px(2F).toFloat()
            } else {
                0F
            })
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView != null && layoutToolbar != null) {
                val scroll = scrollView.scrollY - (layoutToolbar.height / 2)
                val alpha = (1F / layoutToolbar.height) * scroll
                layoutToolbar.alpha = alpha
                viewShadow.alpha = alpha
            }
        }

        WidgetUtils.setClickListener(this, imgEditAvatar, imgEditBackground, imgEditBackground,
                imgEditAvatar, txtCloseConfirmUpdate, txtBirthday, txtConfirmPhone, txtConfirmEmail,
                txtConfirmFacebook, txtChangePassword, btnUpdate, txtProvince, txtDistrict, tvWard)
    }

    private fun pickPhoto(type: Int) {
        takeImageType = type
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (PermissionHelper.checkPermission(this, permission, permissionCamera)) {
            takePhotoHelper.takePhoto(this)
        }
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onGetUserDetailError(errorMessage: String) {
        DialogHelper.showConfirm(this@UpdateProfileActivity, errorMessage, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getUserDetail()
            }
        })
    }

    /**
     * Hiển thị thông tin của người dùng
     */
    override fun onGetDataSuccess(user: ICUser) {
        ViewCompat.setElevation(imgAvatar, SizeUtils.dp2px(2F).toFloat())
        WidgetUtils.loadImageUrl(imgAvatar, user.avatar_thumbnails?.medium, R.drawable.ic_circle_avatar_default, R.drawable.ic_circle_avatar_default)
        WidgetUtils.loadImageUrl(imgBackground, user.cover_thumbnails?.medium, R.drawable.bg_header_home_drawer, R.drawable.bg_header_home_drawer)

        WidgetUtils.loadImageUrlRounded(imgIcon, ImageHelper.getImageUrl(user.avatar, user.avatar_thumbnails?.small, ImageHelper.thumbSmallSize), R.drawable.ic_circle_avatar_default, SizeHelper.size20)

        layoutConfirmUpdate.visibility = if (!user.phone_verified || user.linked_facebook.isNullOrEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (user.gender == "female") {
            rbFemale.isChecked = true
        } else {
            rbMale.isChecked = true
        }

        edtLastName.setText(user.last_name)
        edtFirstName.setText(user.first_name)
        txtBirthday.text = presenter.birthday
        edtPhone.setText(user.phone)

        layoutGiftPhone.visibility = if (!user.phone_verified) {
            View.VISIBLE
        } else {
            View.GONE
        }

        edtEmail.setText(user.email)
        txtProvince.text = user.city?.name
        txtDistrict.text = user.district?.name
        tvWard.text = user.ward?.name
        edtAddress.setText(user.address)

        if (user.linked_facebook.isNullOrEmpty()) {
            txtConfirmFacebook.visibility = View.VISIBLE
            txtConfirmedFacebook.visibility = View.GONE
        } else {
            txtConfirmFacebook.visibility = View.GONE
            txtConfirmedFacebook.visibility = View.VISIBLE
        }
    }

    override fun onSetProvinceName(name: String) {
        txtProvince.text = name
    }

    override fun onSetDistrictName(name: String) {
        txtDistrict.text = name
    }

    override fun onSetWardName(name: String) {
        tvWard.text = name
    }

    override fun onUploadImageSuccess(file: File, type: Int) {
        when (type) {
            1 -> {
                WidgetUtils.loadImageFile(imgAvatar, file)
                WidgetUtils.loadImageFileRounded(imgIcon, file, SizeHelper.size20)
            }
            2 -> {
                WidgetUtils.loadImageFile(imgBackground, file)
            }
        }
    }

    override fun onMappingFacebookSuccess() {
        showLongSuccess(R.string.lien_ket_facebook_thanh_cong)
    }

    override fun onConfirmNewPhone(phone: String, json: String?) {
        val intent = Intent(this, ConfirmNewPhoneActivity::class.java)
        intent.putExtra(Constant.DATA_1, phone)

        if (json != null) {
            intent.putExtra(Constant.DATA_2, json)
        }

        if (json == null)
            ActivityUtils.startActivityForResult(this, intent, requestConfirmPhone)
        else
            ActivityUtils.startActivityForResult(this, intent, requestUpdate)
    }

    override fun onUpdateUserSuccess() {
        setResult(Activity.RESULT_OK)
        DialogHelper.showConfirm(this@UpdateProfileActivity, R.string.cap_nhat_thong_tin_thanh_cong,
                R.string.ban_co_muon_quay_lai_trang_truoc_do,
                R.string.bo_qua, R.string.dong_y, true,
                object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        onBackPressed()
                    }
                })
    }

    override fun onTakePhotoSuccess(file: File?) {
        if (file != null) {
            presenter.uploadImage(file, takeImageType)
        } else {
            showError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {

    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgEditAvatar -> {
                imgEditAvatar.isClickable = false
                pickPhoto(1)
            }
            R.id.imgEditBackground -> {
                imgEditBackground.isClickable = false
                pickPhoto(2)
            }
            R.id.txtCloseConfirmUpdate -> {
                layoutConfirmUpdate.visibility = View.GONE
            }
            R.id.txtBirthday -> {
                TimeHelper.datePicker(this@UpdateProfileActivity, txtBirthday.text.toString(), null, System.currentTimeMillis(), object : DateTimePickerListener {
                    override fun onSelected(dateTime: String, milliseconds: Long) {
                        txtBirthday.text = dateTime
                    }
                })
            }
            R.id.txtProvince -> {
                startActivityForResult<SelectProvinceActivity>(requestProvince)
            }
            R.id.txtDistrict -> {
                presenter.getProvince?.let { province ->
                    startActivityForResult<SelectDistrictActivity, Int>(Constant.DATA_1, province.id.toInt(), requestDistrict)
                }
            }
            R.id.tvWard -> {
                presenter.getDistrict?.let { district ->
                    startActivityForResult<SelectWardActivity, Int>(Constant.DATA_1, district.id, requestWard)
                }
            }
            R.id.txtConfirmPhone -> {
                presenter.confirmPhone(edtPhone.text.toString().trim())
            }
            R.id.txtConfirmEmail -> {

            }
            R.id.txtConfirmFacebook -> {
                txtConfirmFacebook.isClickable = false

                if (NetworkHelper.isNotConnected(this@UpdateProfileActivity)) {
                    showError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                    return
                }

                LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
            }
            R.id.txtChangePassword -> {
                startActivity<ChangePasswordActivity>()
            }
            R.id.btnUpdate -> {
                presenter.updateProfile(
                        rbMale.isChecked,
                        edtLastName.text.toString().trim(),
                        edtFirstName.text.toString().trim(),
                        txtBirthday.text.toString().trim(),
                        edtPhone.text.toString().trim(),
                        edtEmail.text.toString().trim(),
                        edtAddress.text.toString().trim())
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            permissionCamera -> {
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
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        takePhotoHelper.onActivityResult(this, requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestProvince -> {
                    presenter.selectProvince(data)
                }
                requestDistrict -> {
                    presenter.selectDistrict(data)
                }
                requestWard -> {
                    presenter.selectWard(data)
                }
                requestConfirmPhone -> {
                    layoutGiftPhone.visibility = View.GONE
                    presenter.updateCurrentPhone(edtPhone.text.toString())
                }
                requestUpdate -> {
                    layoutGiftPhone.visibility = View.GONE
                    onUpdateUserSuccess()
                }
                requestProfile -> {
                    setResult(Activity.RESULT_OK)
                    presenter.getUserDetail()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        txtConfirmFacebook.isClickable = true
        imgEditAvatar.isClickable = true
        imgEditBackground.isClickable = true
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.clearAllCacheData()

        try {
            LoginManager.getInstance().unregisterCallback(callbackManager)
            callbackManager = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}