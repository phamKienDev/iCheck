package vn.icheck.android.screen.user.wall.public_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import vn.icheck.android.R
import vn.icheck.android.databinding.PublicInfoBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.*

class PublicInfoFragment : Fragment() {
    private var _binding: PublicInfoBinding? = null
    private val binding get() = _binding!!
    private val ickUserWallViewModel: IckUserWallViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PublicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rbMale.setTextColor(ViewHelper.textColorDisableTextUncheckPrimaryChecked(requireContext()))
        ickUserWallViewModel.userInfo?.data?.let { user ->
            when (user.gender) {
                1 -> {
                    binding.rbMale.isChecked = true
                    binding.rbFemale.beGone()
                    binding.rbGay.beGone()
                }
                2 -> {
                    binding.rbFemale.isChecked = true
                    binding.rbMale.beGone()
                    binding.rbGay.beGone()
                }
                3 -> {
                    binding.rbGay.isChecked = true
                    binding.rbFemale.beGone()
                    binding.rbMale.beGone()
                }
            }
            binding.imgBackground.loadImageWithHolder(user.background, R.drawable.bg_image_cover_in_wall)
            binding.imgAvatar.loadImageWithHolder(user.avatar, R.drawable.ic_user_svg)
            binding.edtFirstName simpleText user.firstName
            binding.edtLastName simpleText user.lastName
            binding.txtBirthday simpleText user.birthDay?.getBirthDay()
            if (!user.address.isNullOrEmpty()) {
                binding.rowAddress goneIf user.infoPrivacyConfig?.city
                binding.edtAddress simpleText user.address
            } else {
                binding.rowAddress.beGone()
                binding.viewAddress.beGone()
            }
            binding.edtPhone simpleText user.phone
            binding.edtEmail simpleText user.email
            if (!user.city?.name.isNullOrEmpty()) {
                binding.txtProvince simpleText user.city?.name
            } else {
                binding.rowCity.beGone()
                binding.viewCity.beGone()
            }
            if (!user.district?.name.isNullOrEmpty()) {
                binding.txtDistrict simpleText user.district?.name
            } else {
                binding.rowDistrict.beGone()
                binding.viewDistrict.beGone()
            }
            if (!user.city?.name.isNullOrEmpty()) {
                binding.tvWard simpleText user.ward?.name
            } else {
                binding.rowWard.beGone()
                binding.viewWard.beGone()
            }
            if (user.kycStatus == 2) {
                binding.edtDanhtinh.beGone()
                binding.txtConfirmedDanhtinh.beVisible()
            }
            binding.idUser simpleText "IC-${user.id}"
            binding.totalFollower simpleText user.userFollowingMeCount.toString().getInfo()

            binding.rowPhone goneIf user.infoPrivacyConfig?.phone
            binding.viewPhone goneIf user.infoPrivacyConfig?.phone
            binding.rowEmail goneIf user.infoPrivacyConfig?.email
            if (user.infoPrivacyConfig?.email==false && user.infoPrivacyConfig.phone ==false) {
                binding.viewEmail.beGone()
            }
            binding.rowGender goneIf user.infoPrivacyConfig?.gender
            binding.rowBirthday goneIf user.infoPrivacyConfig?.birthday
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}