package vn.icheck.android.screen.account.icklogin.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.ICK_IMAGE_UPLOADED_SRC
import vn.icheck.android.databinding.FragmentUserInfoBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.network.model.location.CityItem
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.account.icklogin.viewmodel.CHOOSE_TOPIC
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.location.CITY
import vn.icheck.android.screen.location.CityPicker
import vn.icheck.android.screen.location.DISTRICT
import vn.icheck.android.screen.location.WARD
import vn.icheck.android.screen.user.contribute_product.CONTRIBUTE_REQUEST
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.date.DatePickerFragment
import vn.icheck.android.util.date.OnDatePicked
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ToastUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class IckUserInfoFragment : CoroutineFragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val ickLoginViewModel: IckLoginViewModel by activityViewModels()

    private val takeImageListener = object : TakeMediaListener {
        var avatar: ImageView? = null
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
            ickLoginViewModel.file = file
            avatar?.let {
                Glide.with(it.context.applicationContext)
                        .load(file)
                        .error(R.drawable.user_placeholder)
                        .placeholder(R.drawable.user_placeholder)
                        .into(it)
            }
        }
    }

    var cityPicker: CityPicker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cityPicker = null
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.requestFocus()

        takeImageListener.avatar = binding.userAva
        ickLoginViewModel.setGender(1)
        binding.radioGroupGender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_gender_male -> {
                    ickLoginViewModel.setGender(1)
                }
                R.id.btn_gender_female -> {
                    ickLoginViewModel.setGender(2)
                }
                R.id.btb_gender_other -> {
                    ickLoginViewModel.setGender(3)
                }
            }
        }

        binding.imgEditAvatar.setOnClickListener {
            hideKeyboard()
            request()
        }
        binding.edtBirthday.setOnClickListener {
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
                    val timeInMills = Calendar.getInstance().timeInMillis
                    ickLoginViewModel.calendar.set(year, month, day)
                    if (timeInMills < ickLoginViewModel.calendar.timeInMillis) {
                        showError("Không cho phép chọn ngày sinh là ngày tương lai!")
                    } else {
                        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        ickLoginViewModel.setBirthDay(df.format(ickLoginViewModel.calendar.time))
                        binding.edtBirthday.setText("$days/$months/$year")
                    }
                }
            }, ickLoginViewModel.calendar)

            pickDate.show(childFragmentManager, null)
        }
        ickLoginViewModel.facebookAvatar?.let {
            Glide.with(view.context.applicationContext)
                    .load(it)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(binding.userAva)
        }
        if (!ickLoginViewModel.facebookUsername.isNullOrEmpty()) {
            binding.tvFirstname.setText(ickLoginViewModel.facebookUsername)
            ickLoginViewModel.setFirstName(ickLoginViewModel.facebookUsername)
        }
        binding.edtTinh.enableRightClick = false
        binding.edtQuan.enableRightClick = false
        binding.edtPhuongXa.enableRightClick = false
        binding.edtTinh.setOnClickListener {
            hideKeyboard()
            if (cityPicker?.isVisible == true) {
                cityPicker?.dismiss()
            }
            cityPicker = CityPicker(CITY, object : CityPicker.OnCityClick {
                override fun onClick(city: CityItem) {
                    cityPicker?.dismiss()
//                    ickLoginViewModel.city = city
                    ickLoginViewModel.setCity(city.id)
                    binding.edtTinh.setText(city.name)

                    binding.edtQuan.setText("")
                    binding.edtPhuongXa.setText("")
                    binding.edtDiaChi.setText("")

                    ickLoginViewModel.removeDistrict()
                    ickLoginViewModel.removeWard()
                    ickLoginViewModel.removeAddress()
                }
            }, null)
            cityPicker?.show(childFragmentManager, null)
        }
        binding.edtQuan.setOnClickListener {
            hideKeyboard()
            if (ickLoginViewModel.getCity() != null) {
                if (cityPicker?.isVisible == true) {
                    cityPicker?.dismiss()
                }
                cityPicker = CityPicker(DISTRICT, object : CityPicker.OnCityClick {
                    override fun onClick(city: CityItem) {
                        cityPicker?.dismiss()
                        ickLoginViewModel.setDistrict(city.id)
                        binding.edtQuan.setText(city.name)

                        binding.edtPhuongXa.setText("")
                        binding.edtDiaChi.setText("")

                        if (ickLoginViewModel.hasWard()) {
                            ickLoginViewModel.removeWard()
                        }
                        if (ickLoginViewModel.hasAddress()) {
                            ickLoginViewModel.removeAddress()
                        }
                    }
                }, ickLoginViewModel.getCity())
                cityPicker?.show(childFragmentManager, null)
            } else {
                binding.edtTinh.setError("Vui lòng chọn tỉnh/thành phố")
//                showError("Vui lòng chọn tỉnh thành phố")
            }

        }
        binding.edtPhuongXa.setOnClickListener {
            hideKeyboard()
            if (ickLoginViewModel.getDistrict() != null) {
                if (cityPicker?.isVisible == true) {
                    cityPicker?.dismiss()
                }
                cityPicker = CityPicker(WARD, object : CityPicker.OnCityClick {
                    override fun onClick(city: CityItem) {
                        cityPicker?.dismiss()
                        ickLoginViewModel.setWard(city.id)
                        binding.edtPhuongXa.setText(city.name)

                        binding.edtDiaChi.setText("")

                        if (ickLoginViewModel.hasAddress()) {
                            ickLoginViewModel.removeAddress()
                        }
                    }
                }, ickLoginViewModel.getDistrict())
                cityPicker?.show(childFragmentManager, null)
            } else {
                binding.edtQuan.setError("Vui lòng chọn quận/huyện")
//                showError("Vui lòng chọn quận huyện")
            }
        }
        binding.tvFirstname.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.setFirstName(s?.trim().toString())
            }
        })
        binding.edtLastName.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                ickLoginViewModel.setLastName(s?.trim().toString())
            }
        })
        binding.edtEmailInput.addTextChangedListener {
//            binding.tvErrorEmail.beGone()
            if (it?.trim().toString().isValidEmail()) {
                ickLoginViewModel.setEmail(it?.trim().toString())
            }
        }
        binding.edtMgt.addTextChangedListener {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (!it.isNullOrEmpty()) {
                    binding.edtMgt.letterSpacing = 0.2f
                } else {
                    binding.edtMgt.letterSpacing = 0.05f
                }
            }
            if (!it.isNullOrEmpty()) {
                binding.edtMgt.setTypeface(Typeface.createFromAsset(requireContext().assets, "font/barlow_semi_bold.ttf"))
            } else {
                binding.edtMgt.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
            }
            ickLoginViewModel.setMgt(it?.trim().toString())
        }
        binding.edtDiaChi.addTextChangedListener {
            ickLoginViewModel.setAddress(it?.trim().toString())
        }
        binding.btnContinue.setOnClickListener {
            if (binding.edtEmailInput.text!!.trim().isNotEmpty()) {
                if (binding.edtEmailInput.text!!.trim().isValidEmail()) {
                    finalStep()
                } else {
                    binding.edtEmailInput.setError("Nhập sai định dạng. Thử lại!")
//                    binding.tvErrorEmail.beVisible()
                    return@setOnClickListener
                }
            }
            finalStep()
        }
        binding.btnSkip.setOnClickListener {
            hideKeyboard()
            ickLoginViewModel.mState.postValue(CHOOSE_TOPIC)
        }
        hideKeyboard()
    }

    private fun finalStep() {
        showLoadingTimeOut(5000)
        //            DialogHelper.showLoading(this)
        hideKeyboard()
        if (ickLoginViewModel.file != null) {

            ickLoginViewModel.uploadImage().observe(viewLifecycleOwner, Observer { listInfos ->
                val filter = listInfos.firstOrNull {
                    it.state != WorkInfo.State.SUCCEEDED
                }
                if (filter == null) {
                    for (item in listInfos) {
                        if (item.state == WorkInfo.State.SUCCEEDED) {
                            ickLoginViewModel.updateUserInfo(item.outputData.getString(ICK_IMAGE_UPLOADED_SRC)).observe(viewLifecycleOwner, {
                                ickLoginViewModel.getUserInfo().observe(viewLifecycleOwner, { res ->
                                    SessionManager.session = SessionManager.session.apply {
                                        user = res?.data?.createICUser()
                                    }
                                    DialogHelper.closeLoading(this)
                                    if (it?.statusCode == "200") {
                                        ickLoginViewModel.mState.postValue(CHOOSE_TOPIC)
                                    } else {
                                        it?.message?.let { msg ->
                                            ToastUtils.showShortError(requireContext(), msg)
                                        }
                                    }
                                })

                            })
                        }
                    }

                }
            })
        } else {
            ickLoginViewModel.updateUserInfo(null).observe(viewLifecycleOwner, Observer {
                ickLoginViewModel.getUserInfo().observe(viewLifecycleOwner, { res ->
                    SessionManager.session = SessionManager.session.apply {
                        user = res?.data?.createICUser()
                    }
                    DialogHelper.closeLoading(this)
                    if (it?.statusCode == "200") {
                        ickLoginViewModel.mState.postValue(CHOOSE_TOPIC)
                    } else {
                        it?.message?.let { msg ->
                            ToastUtils.showShortError(requireContext(), msg)
                        }
                    }
                })

            })
        }
    }

    private fun hideKeyboard() {
        requireActivity().forceHideKeyboard()
    }


    fun request() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CONTRIBUTE_REQUEST
            )
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
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                selectPicture()
            }
        }
    }
}