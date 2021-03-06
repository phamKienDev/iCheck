package vn.icheck.android.screen.user.wall.option_edit_my_information

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.ActivityEditMyInformationBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableEndText
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.network.base.*
import vn.icheck.android.network.model.ApiErrorResponse
import vn.icheck.android.network.model.ApiSuccessResponse
import vn.icheck.android.network.model.icklogin.IckUserInfoResponse
import vn.icheck.android.network.model.location.CityItem
import vn.icheck.android.screen.location.CITY
import vn.icheck.android.screen.location.CityPicker
import vn.icheck.android.screen.location.DISTRICT
import vn.icheck.android.screen.location.WARD
import vn.icheck.android.screen.user.contribute_product.CONTRIBUTE_REQUEST
import vn.icheck.android.screen.user.verify_identity.VerifyIdentityActivity
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.util.*
import vn.icheck.android.util.date.DatePickerFragment
import vn.icheck.android.util.date.OnDatePicked
import vn.icheck.android.util.ick.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val TAKE_AVATAR = 1
const val TAKE_WALL = 2
const val VERIFY_IDENTITY = 3

@AndroidEntryPoint
class EditMyInformationFragment : BaseFragmentMVVM() {
    private var _binding: ActivityEditMyInformationBinding? = null
    private val binding get() = _binding!!
    private val ickUserWallViewModel: IckUserWallViewModel by activityViewModels()
    var cityPicker: CityPicker? = null

    private val requestUpdateKyc = 1

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var loginManager: LoginManager

    private val facebookReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            ickUserWallViewModel.inAction = false
            if (intent?.action == FACEBOOK_TOKEN) {
                intent.getStringExtra(FACEBOOK_TOKEN)?.let {

                    ickUserWallViewModel.linkFacebook(it).observe(viewLifecycleOwner, Observer { response ->
                        if (response?.statusCode == "200") {
                            val name = intent.getStringExtra(FACEBOOK_USERNAME)
                            binding.edtConnectFb.setText(name ?: getString(R.string.da_xac_thuc))
                            binding.edtConnectFb.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                            ickUserWallViewModel.getUserInfo().observe(viewLifecycleOwner, Observer { userInfo ->
                                SessionManager.updateUser(userInfo?.data?.createICUser())
                            })
                        } else {
                            requireContext().showShortErrorToast(getString(R.string.tai_khoan_facebook_da_duoc_dang_ky_tren_he_thong))
                        }
                    })

                }
            }
        }
    }

    private val takeImageListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            loadFile(file)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            loadFile(file)
        }

        private fun loadFile(file: File?) {

            if (ickUserWallViewModel.photoType == TAKE_AVATAR) {
                Glide.with(binding.imgAvatar.context.applicationContext)
                        .load(file)
                        .error(R.drawable.ic_avatar_default_84dp)
                        .placeholder(R.drawable.ic_avatar_default_84dp)
                        .into(binding.imgAvatar)
                ickUserWallViewModel.avatar = file
            } else {
                Glide.with(binding.imgBackground.context.applicationContext)
                        .load(file)
                        .error(R.drawable.ic_avatar_default_84dp)
                        .placeholder(R.drawable.ic_avatar_default_84dp)
                        .into(binding.imgBackground)
                ickUserWallViewModel.wall = file
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ActivityEditMyInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        cityPicker = null
        _binding = null
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        fillData()
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(FACEBOOK_TOKEN)
        requireActivity().registerReceiver(facebookReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(facebookReceiver)
    }

    private fun initView() {
        binding.rbMale.setTextColor(ViewHelper.textColorDisableTextUncheckLightBlueChecked(requireContext()))
        binding.rbFemale.setTextColor(ViewHelper.textColorDisableTextUncheckYellowChecked(requireContext()))
        binding.rbGay.setTextColor(ViewHelper.textColorDisableTextUncheckViolentChecked(requireContext()))

        binding.btnUpdate.background = ViewHelper.bgPrimaryCorners4(requireContext())

        binding.txtProvince.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
        binding.txtDistrict.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
        binding.tvWard.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
        binding.txtChangePassword.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)

        binding.edtConnectFb.fillDrawableEndText(R.drawable.ic_arrow_right_light_blue_24dp)
        binding.imgArrowDanhTinh.fillDrawableColor(R.drawable.ic_arrow_right_light_blue_24dp)

        ColorManager.getDisableTextColor(requireContext()).apply {
            binding.edtLastname.setHintTextColor(this)
            binding.edtFirstname.setHintTextColor(this)
            binding.edtEmail.setHintTextColor(this)
            binding.edtAddress.setHintTextColor(this)
        }

        ColorManager.getNormalTextColor(requireContext()).apply {
            binding.edtLastname.setTextColor(this)
            binding.edtFirstname.setTextColor(this)
            binding.txtBirthday.setTextColor(this)
            binding.edtPhone.setTextColor(this)
            binding.edtEmail.setTextColor(this)
            binding.edtAddress.setTextColor(this)
        }


        binding.edtConnectFb.setOnClickListener {
            if (SessionManager.session.user?.linkedFbId.isNullOrEmpty()) {
                if (!ickUserWallViewModel.inAction) {
                    loginManager.logInWithReadPermissions(this, listOf("public_profile"))
                    ickUserWallViewModel.inAction = true
                }
            }
        }
        binding.imgBack.setOnClickListener {
            if (ickUserWallViewModel.initActivity) {
                activity?.finish()
            }
            requireActivity().dismissKeyboard()
            findNavController().popBackStack()
        }
        binding.rdGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbFemale -> {
                    ickUserWallViewModel.editUserInfo[GENDER] = 2
                }
                R.id.rbMale -> {
                    ickUserWallViewModel.editUserInfo[GENDER] = 1
                }
                R.id.rbGay -> {
                    ickUserWallViewModel.editUserInfo[GENDER] = 3
                }
            }
        }
        binding.edtLastname.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickUserWallViewModel.editUserInfo[LAST_NAME] = s?.trim().toString()
            }
        })
        binding.edtFirstname.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickUserWallViewModel.editUserInfo[FIRST_NAME] = s?.trim().toString()
            }
        })
        binding.txtBirthday.setOnClickListener {
            hideKeyboard(requireActivity(), view)
            val pickDate = DatePickerFragment(object : OnDatePicked {
                override fun picked(year: Int, month: Int, day: Int) {
                    val days = if (day < 10) {
                        "0$day"
                    } else {
                        "$day"
                    }
                    val months = if (month + 1 < 10) {
                        "0${month + 1}"
                    } else {
                        "${month + 1}"
                    }
                    val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("$year-$months-$days")
                    if (ickUserWallViewModel.currentDate.before(d)) {
                        requireContext().showShortErrorToast(getString(R.string.ngay_sinh_khong_dung))
                    } else {
                        ickUserWallViewModel.calendar.set(year, month, day)
                        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        ickUserWallViewModel.editUserInfo[BIRTH_DAY] = df.format(ickUserWallViewModel.calendar.time)
                        binding.txtBirthday.text = "$days/$months/$year"
                    }

                }
            }, ickUserWallViewModel.calendar)
            pickDate.show(childFragmentManager, null)
        }
//        binding.edtPhone.setOnClickListener {
//            val action = EditMyInformationActivityDirections.actionEditMyInformationActivityToIckChangePhoneFragment()
//            findNavController().navigate(action)
//        }

        binding.edtEmail.addTextChangedListener {
            ickUserWallViewModel.editUserInfo[EMAIL] = it?.trim().toString()
        }
        binding.edtAddress.addTextChangedListener {
            ickUserWallViewModel.editUserInfo[ADDRESS] = it?.trim().toString()
        }
        binding.txtProvince.setOnClickListener {
            hideKeyboard(requireActivity(), view)
            cityPicker = CityPicker(CITY, object : CityPicker.OnCityClick {
                override fun onClick(city: CityItem) {
                    cityPicker?.dismiss()
                    ickUserWallViewModel.editUserInfo[CITY_ID] = city.id
                    binding.txtProvince.text = city.name

                    binding.txtDistrict.setText(R.string.tuy_chon)
                    binding.tvWard.setText(R.string.tuy_chon)

                    ColorManager.getDisableTextColor(requireContext()).apply {
                        binding.txtDistrict.setTextColor(this)
                        binding.tvWard.setTextColor(this)
                    }
                    binding.edtAddress.setText("")

                    ickUserWallViewModel.editUserInfo.remove(DISTRICT_ID)
                    ickUserWallViewModel.editUserInfo.remove(WARD_ID)
                    ickUserWallViewModel.editUserInfo.remove(ADDRESS)
                }
            }, null)
            cityPicker?.show(childFragmentManager, null)
        }
        binding.txtDistrict.setOnClickListener {
            hideKeyboard(requireActivity(), view)
            when {
                ickUserWallViewModel.editUserInfo[CITY_ID] != null -> {
                    cityPicker = CityPicker(DISTRICT, object : CityPicker.OnCityClick {
                        override fun onClick(city: CityItem) {
                            cityPicker?.dismiss()
                            ickUserWallViewModel.editUserInfo[DISTRICT_ID] = city.id
                            binding.txtDistrict.text = city.name

                            binding.tvWard.setText(R.string.tuy_chon)
                            binding.tvWard.setTextColor(ColorManager.getDisableTextColor(requireContext()))
                            binding.edtAddress.setText("")

                            ickUserWallViewModel.editUserInfo.remove(WARD_ID)
                            ickUserWallViewModel.editUserInfo.remove(ADDRESS)
                        }
                    }, ickUserWallViewModel.editUserInfo[CITY_ID] as Int?)
                    cityPicker?.show(childFragmentManager, null)
                }
                ickUserWallViewModel.userInfo?.data?.city?.id != null -> {
                    cityPicker = CityPicker(DISTRICT, object : CityPicker.OnCityClick {
                        override fun onClick(city: CityItem) {
                            cityPicker?.dismiss()
                            ickUserWallViewModel.editUserInfo[DISTRICT_ID] = city.id
                            binding.txtDistrict.text = city.name

                        }
                    }, ickUserWallViewModel.userInfo?.data?.city?.id)
                    cityPicker?.show(childFragmentManager, null)
                }
                else -> {
                    requireContext().showShortErrorToast(getString(R.string.vui_long_chon_tinh_truoc))
                }
            }
        }
        binding.tvWard.setOnClickListener {
            hideKeyboard(requireActivity(), view)
            when {
                ickUserWallViewModel.editUserInfo[DISTRICT_ID] != null -> {
                    cityPicker = CityPicker(WARD, object : CityPicker.OnCityClick {
                        override fun onClick(city: CityItem) {
                            cityPicker?.dismiss()
                            ickUserWallViewModel.editUserInfo[WARD_ID] = city.id
                            binding.tvWard.text = city.name

                            binding.edtAddress.setText("")

                            ickUserWallViewModel.editUserInfo.remove(ADDRESS)
                        }
                    }, ickUserWallViewModel.editUserInfo[DISTRICT_ID] as Int?)
                    cityPicker?.show(childFragmentManager, null)
                }
                ickUserWallViewModel.userInfo?.data?.ward?.id != null -> {
                    cityPicker = CityPicker(WARD, object : CityPicker.OnCityClick {
                        override fun onClick(city: CityItem) {
                            cityPicker?.dismiss()
                            ickUserWallViewModel.editUserInfo[WARD_ID] = city.id
                            binding.tvWard.text = city.name
                        }
                    }, ickUserWallViewModel.userInfo?.data?.ward?.id)
                    cityPicker?.show(childFragmentManager, null)
                }
                else -> {
                    requireContext().showShortErrorToast(getString(R.string.vui_long_chon_quan_huyen_truoc))
                }
            }
        }
        binding.edtDanhtinh.setOnClickListener {
            startKyc()
        }
        binding.txtChangePassword.setOnClickListener {
            val action = EditMyInformationFragmentDirections.actionEditMyInformationActivityToIckNewPwFragment()
            findNavController().navigate(action)
        }
        binding.imgEditAvatar.setOnClickListener {
            hideKeyboard(requireActivity(), view)
            ickUserWallViewModel.photoType = TAKE_AVATAR
            request()
        }
        binding.imgEditBackground.setOnClickListener {
            hideKeyboard(requireActivity(), view)
            ickUserWallViewModel.photoType = TAKE_WALL
            request()
        }
        binding.btnUpdate.setOnClickListener {
            if (binding.edtEmail.text.toString().isNotEmpty()) {
                if (isValidEmail(binding.edtEmail.text?.trim().toString())) {
                    updateInfo()
                    return@setOnClickListener
                } else {
                    requireContext().showShortErrorToast(getString(R.string.nhap_sai_dinh_dang_email))
//                    binding.edtEmail.setError("Nh???p sai ?????nh d???ng. Th??? l???i!", ResourcesCompat.getDrawable(resources, R.drawable.ic_error_red_18dp, null))
                    return@setOnClickListener
                }
            }
            updateInfo()
        }

        binding.imgDanhtinh.setOnClickListener {
            ickUserWallViewModel.userInfo?.data?.kycStatus?.let { kycStatus ->
               startKyc()
            }
        }
    }

    private fun startKyc() {
        val i = Intent(requireActivity(), VerifyIdentityActivity::class.java)
        i.putExtra(Constant.DATA_1,  ickUserWallViewModel.userInfo?.data?.kycStatus )
        startActivityForResult(i, requestUpdateKyc)
    }

    private fun updateInfo() {
        DialogHelper.showLoading(this)
        if (ickUserWallViewModel.hasImages()) {
            ickUserWallViewModel.uploadImages().observe(viewLifecycleOwner, androidx.lifecycle.Observer { listInfos ->
                val filter = listInfos.firstOrNull {
                    it.state != WorkInfo.State.SUCCEEDED
                }
                val fail = listInfos.firstOrNull {
                    it.state == WorkInfo.State.FAILED
                }
                if (fail != null) {
                    requireActivity().showShortErrorToast(null)
                    DialogHelper.closeLoading(this)
                }
                if (filter == null) {
                    for (item in listInfos) {
                        if (item.outputData.getString("key") == "avatar") {
                            ickUserWallViewModel.editUserInfo["avatar"] = item.outputData.getString(ICK_IMAGE_UPLOADED_SRC)
                        } else if (item.outputData.getString("key") == "wall") {
                            ickUserWallViewModel.editUserInfo["background"] = item.outputData.getString(ICK_IMAGE_UPLOADED_SRC)
                        }
                    }
//                    when (listInfos.size) {
//                        1 -> {
//                            if (ickUserWallViewModel.photoType == TAKE_AVATAR) {
//                                ickUserWallViewModel.editUserInfo["avatar"] = listInfos.first().outputData.getString(ICK_IMAGE_UPLOADED_SRC)
//                            } else {
//                                ickUserWallViewModel.editUserInfo["background"] = listInfos.first().outputData.getString(ICK_IMAGE_UPLOADED_SRC)
//                            }
//                        }
//                        2 -> {
//                            ickUserWallViewModel.editUserInfo["avatar"] = listInfos.last().outputData.getString(ICK_IMAGE_UPLOADED_SRC)
//                            ickUserWallViewModel.editUserInfo["background"] = listInfos.first().outputData.getString(ICK_IMAGE_UPLOADED_SRC)
//                        }
//                    }

                    finalStep()
                }
            })
        } else {
            finalStep()
        }
    }

    private fun finalStep() {
        ickUserWallViewModel.updateProfile().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            DialogHelper.closeLoading(this)
            if (it is ApiSuccessResponse) {
                if (it.body?.statusCode == "200") {
                    ickUserWallViewModel.getUserInfo().observe(viewLifecycleOwner, androidx.lifecycle.Observer { userInfo ->
                        SessionManager.updateUser(userInfo?.data?.createICUser())
                        InsiderHelper.setUserAttributes()
                        requireContext() showShortSuccessToast (getString(R.string.ban_da_luu_thong_tin_thanh_cong))
                        findNavController().popBackStack()
                    })
                }
            } else if (it is ApiErrorResponse) {
                logError(it.error)
            }
        })
    }

    fun request() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CONTRIBUTE_REQUEST
                )
            }
        } else {
            selectPicture()
        }
    }

    private fun selectPicture() {
        activity?.let { TakeMediaDialog.show(childFragmentManager, it, takeImageListener, selectMulti = false, isVideo = false) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTRIBUTE_REQUEST) {
            if (PermissionHelper.checkResult(grantResults)) {
                selectPicture()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestUpdateKyc) {
            if (resultCode == Activity.RESULT_OK) {
                ickUserWallViewModel.getUserInfo().observe(viewLifecycleOwner, {
                    if (it != null) {
                        ickUserWallViewModel.userInfo = it
                        checkKyc(it)
                    }
                })
//                ickUserWallViewModel.userInfo?.data?.kycStatus = 1
//                ickUserWallViewModel.userInfo?.let { user ->
//                    checkKyc(user)
//                }
            }
        }

    }

    private fun fillData() {
        ickUserWallViewModel.userInfo?.let { user ->
            SessionManager.updateUser(user.data?.createICUser())
            if (!user.data?.linkedFbId.isNullOrEmpty()) {
                binding.edtConnectFb.setText(R.string.da_xac_thuc)
                binding.edtConnectFb.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

            }
            Glide.with(requireContext().applicationContext)
                    .load(user.data?.avatar)
                    .error(R.drawable.ic_avatar_default_84dp)
                    .placeholder(R.drawable.ic_avatar_default_84dp)
                    .into(binding.imgAvatar)
            if (!user.data?.firstName.isNullOrEmpty()) {
                binding.edtFirstname.setText(user.data?.firstName)
            }
            user.data?.lastName?.also {
                binding.edtLastname.setText(it)
            }
            if (!user.data?.birthDay.isNullOrEmpty()) {
                try {
                    val c = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val date = sdf.parse(user.data?.birthDay.toString())
                    c.time = date ?: Calendar.getInstance().time
                    ickUserWallViewModel.calendar = c
                    binding.txtBirthday.text = user.data?.birthDay?.convertBirthDay("dd/MM/yyyy")
                } catch (e: Exception) {
                }
            }
            if (!user.data?.phone.isNullOrEmpty()) {
                binding.edtPhone.text = user.data?.phone
            }
            if (!user.data?.wall?.cover.isNullOrEmpty()) {
                Glide.with(requireContext().applicationContext)
                        .load(user.data?.wall?.cover)
                        .error(R.drawable.bg_image_cover_in_wall)
                        .placeholder(R.drawable.bg_image_cover_in_wall)
                        .into(binding.imgBackground)
            }
            when (user.data?.gender) {
                1 -> {
                    binding.rbMale.isChecked = true
                }
                2 -> {
                    binding.rbFemale.isChecked = true
                }
                3 -> {
                    binding.rbGay.isChecked = true
                }
            }
            if (!user.data?.email.isNullOrEmpty()) {
                binding.edtEmail.setText(user.data?.email)
            }
            if (!user.data?.city?.name.isNullOrEmpty()) {
                binding.txtProvince.text = user.data?.city?.name
            }else{
                binding.txtProvince.setHintTextColor(ColorManager.getDisableTextColor(requireContext()))
            }
            if (!user.data?.district?.name.isNullOrEmpty()) {
                binding.txtDistrict.text = user.data?.district?.name
            }else{
                binding.txtDistrict.setHintTextColor(ColorManager.getDisableTextColor(requireContext()))
            }
            if (!user.data?.ward?.name.isNullOrEmpty()) {
                binding.tvWard.text = user.data?.ward?.name
            }else{
                binding.tvWard.setHintTextColor(ColorManager.getDisableTextColor(requireContext()))
            }
            if (!user.data?.address.isNullOrEmpty()) {
                binding.edtAddress.setText(user.data?.address)
            }
            if (!user.data?.background.isNullOrEmpty()) {
                binding.imgBackground.loadImageWithHolder(user.data?.background, R.drawable.bg_image_cover_in_wall)
            }
            checkKyc(user)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.SOCKET_TIMEOUT) {
            DialogHelper.closeLoading(this)
        }
    }

    private fun checkKyc(user: IckUserInfoResponse) {
        if (user.data?.kycStatus == 0 || user.data?.kycStatus == null) {
            binding.edtDanhtinh.beVisible()
            binding.txtConfirmedDanhtinh.beGone()
        } else {
            binding.edtDanhtinh.beGone()
            binding.txtConfirmedDanhtinh.beVisible()

            when (user.data?.kycStatus) {
                1 -> {
                    binding.txtConfirmedDanhtinh.setBackgroundResource(R.drawable.bg_yellow_20_corner_23)
                    binding.txtConfirmedDanhtinh.setText(R.string.cho_xac_thuc)
                    binding.txtConfirmedDanhtinh.setTextColor(ColorManager.getAccentYellowColor(requireContext()))
                    binding.txtConfirmedDanhtinh.setOnClickListener {
                        lifecycleScope.launch {
                            binding.txtConfirmedDanhtinh.isEnabled = false
                            startKyc()
                            delay(200)
                            binding.txtConfirmedDanhtinh.isEnabled = true
                        }
                    }
                }
                2 -> {
                    binding.txtConfirmedDanhtinh.setBackgroundResource(R.drawable.bg_green_20_corner_23)
                    binding.txtConfirmedDanhtinh.setText(R.string.da_xac_thuc)
                    binding.txtConfirmedDanhtinh.setTextColor(ColorManager.getAccentGreenColor(requireContext()))

                    binding.imgDanhtinh.beGone()
                }
                3 -> {
                    binding.txtConfirmedDanhtinh.setBackgroundResource(R.drawable.bg_red_20_corner_23)
                    binding.txtConfirmedDanhtinh.setText(R.string.loi_xac_thuc)
                    binding.txtConfirmedDanhtinh.setTextColor(ColorManager.getAccentRedColor(requireContext()))
                    binding.txtConfirmedDanhtinh.setOnClickListener {
                        lifecycleScope.launch {
                            binding.txtConfirmedDanhtinh.isEnabled = false
                            startKyc()
                            delay(200)
                            binding.txtConfirmedDanhtinh.isEnabled = true
                        }
                    }
                }
            }
        }
    }


}