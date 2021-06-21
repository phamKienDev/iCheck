package vn.icheck.android.screen.user.shipping.ship.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import vn.icheck.android.R
import vn.icheck.android.databinding.FragmentAddShipAddressBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableEndText
import vn.icheck.android.network.model.location.CityItem
import vn.icheck.android.screen.location.CITY
import vn.icheck.android.screen.location.CityPicker
import vn.icheck.android.screen.location.DISTRICT
import vn.icheck.android.screen.location.WARD
import vn.icheck.android.screen.user.shipping.ship.adpter.vm.ShipViewModel
import vn.icheck.android.util.ick.isPhoneNumber
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.util.ick.rText

class AddShipAddressFragment : Fragment() {

    companion object {
        fun newInstance() = AddShipAddressFragment()
    }

    private val viewModel: ShipViewModel by activityViewModels()
    private var _binding: FragmentAddShipAddressBinding? = null
    val binding get() = _binding!!
    var cityPicker: CityPicker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddShipAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cityPicker = null
        viewModel.removeUpdate()
        viewModel.clearCurrentCity()
        viewModel.clearDistrict()
        viewModel.clearWard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        initBinding()
        viewModel.arrayAddress.firstOrNull {
            it?.id == viewModel.getAddressUpdateId() && it.id != -1L
        }?.let {
            binding.edtPhone.setText(it.phone)
            binding.edtLastName.setText(it.lastName)
            binding.edtFirstName.setText(it.firstName)

            binding.edtTinhThanh.setText(it.city?.name)
            viewModel.setCity(CityItem(it.city?.name, it.city?.id))

            binding.edtQuan.setText(it.district?.name)
            viewModel.setDistrict(CityItem(it.district?.name, it.district?.id))

            binding.edtPhuongXa.setText(it.ward?.name)
            viewModel.setWard(CityItem(it.ward?.name, it.ward?.id))

            binding.edtAddress.setText(it.address)
        }
    }

    private fun setupView() {
        Constant.getNormalTextColor(requireContext()).apply {
            binding.edtLastName.setTextColor(this)
            binding.edtFirstName.setTextColor(this)
            binding.edtPhone.setTextColor(this)
            binding.edtTinhThanh.setTextColor(this)
            binding.edtQuan.setTextColor(this)
            binding.edtPhuongXa.setTextColor(this)
            binding.edtAddress.setTextColor(this)
        }

        Constant.getDisableTextColor(requireContext()).apply {
            binding.edtLastName.setHintTextColor(this)
            binding.edtFirstName.setHintTextColor(this)
            binding.edtPhone.setHintTextColor(this)
            binding.edtTinhThanh.setHintTextColor(this)
            binding.edtQuan.setHintTextColor(this)
            binding.edtPhuongXa.setHintTextColor(this)
            binding.edtAddress.setHintTextColor(this)
        }


        binding.edtQuan.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
        binding.edtTinhThanh.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
        binding.edtPhuongXa.fillDrawableEndText(R.drawable.ic_arrow_down_blue_24dp)
    }

    private fun initBinding() {
        binding.btnBack.setOnClickListener {
            viewModel.moveToChoose()
        }
        binding.edtTinhThanh.setOnClickListener {
            cityPicker = CityPicker(CITY, object : CityPicker.OnCityClick {
                override fun onClick(city: CityItem) {
                    viewModel.setCity(city)
                    binding.edtTinhThanh.setText(city.name)
                    binding.edtQuan.setText("")
                    binding.edtPhuongXa.setText("")
                    cityPicker?.dismiss()
                }
            }, null)
            cityPicker?.show(requireActivity().supportFragmentManager, null)
        }
        binding.edtQuan.setOnClickListener {
            if (viewModel.getCurrentCity() != null) {
                cityPicker = CityPicker(DISTRICT, object : CityPicker.OnCityClick {
                    override fun onClick(city: CityItem) {
                        viewModel.setDistrict(city)
                        binding.edtQuan.setText(city.name)
                        binding.edtPhuongXa.setText("")
                        cityPicker?.dismiss()
                    }
                }, viewModel.getCurrentCity()!!.id)
                cityPicker?.show(requireActivity().supportFragmentManager, null)
            } else {
                requireContext().showShortErrorToast( rText(R.string.vui_long_chon_tinh_thanh))
            }
        }
        binding.edtPhuongXa.setOnClickListener {
            if (viewModel.getDistrict() != null) {
                cityPicker = CityPicker(WARD, object : CityPicker.OnCityClick {
                    override fun onClick(city: CityItem) {
                        viewModel.setWard(city)
                        binding.edtPhuongXa.setText(city.name)
                        cityPicker?.dismiss()
                    }
                }, viewModel.getDistrict()!!.id)
                cityPicker?.show(requireActivity().supportFragmentManager, null)
            } else {
                requireContext().showShortErrorToast(rText(R.string.vui_long_chon_quan_huyen))
            }
        }
        binding.edtAddress.addTextChangedListener {
            viewModel.setAddress(it?.trim().toString())
        }
        binding.edtLastName.addTextChangedListener {
            viewModel.setLastName(it?.trim().toString())
        }
        binding.edtFirstName.addTextChangedListener {
            viewModel.setFirstName(it?.trim().toString())
        }
        binding.edtPhone.addTextChangedListener {
            viewModel.setPhone(it?.trim().toString())
        }
        binding.btnConfirm.apply {
            background = ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener { view ->
                when {
                    binding.edtLastName.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_nhap_ho))
                        binding.edtLastName.requestFocus()
                    }
                    binding.edtFirstName.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_nhap_ten_dem_va_ten))
                        binding.edtFirstName.requestFocus()
                    }
                    binding.edtPhone.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_nhap_so_dien_thoai))
                        binding.edtPhone.requestFocus()
                    }
                    !binding.edtPhone.text?.trim().toString().isPhoneNumber() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.so_dien_thoai_khong_dung_dinh_dang))
                        binding.edtPhone.requestFocus()
                    }
                    binding.edtTinhThanh.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_chon_tinh_thanh))
                        binding.edtTinhThanh.requestFocus()
                    }
                    binding.edtQuan.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_chon_quan_huyen))
                        binding.edtQuan.requestFocus()
                    }
                    binding.edtPhuongXa.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_chon_phuong_xa))
                        binding.edtPhuongXa.requestFocus()
                    }
                    binding.edtAddress.text?.trim().isNullOrEmpty() -> {
                        requireContext().showShortErrorToast(view.context.rText(R.string.vui_long_nhap_dia_chi))
                        binding.edtAddress.requestFocus()
                    }
                    else -> {
                        viewModel.createShipAddress().observe(viewLifecycleOwner, {
                            if (it.statusCode == "200") {
                                requireContext().showShortSuccessToast(view.context.rText(R.string.cap_nhat_dia_chi_thanh_cong))
//                            if (!viewModel.isUpdate()) {
//                                requireContext().showSimpleSuccessToast("Gửi địa chỉ thành công!")
//                            } else {
//                                requireContext().showSimpleSuccessToast("Cập nhật địa chỉ thành công")
//                            }
                                viewModel.moveToChoose()
                            }
                        })

                    }
                }
            }
        }
    }
}