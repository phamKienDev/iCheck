package vn.icheck.android.screen.user.wall.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.icheck.android.R
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.DialogBottomWallSettingBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.option_edit_information_public.EditInformationPublicActivity
import vn.icheck.android.screen.user.option_manager_user_watching.ManagerUserWatchingActivity
import vn.icheck.android.screen.user.option_manger_user_follow.ManagerUserFollowActivity
import vn.icheck.android.screen.user.wall.EDIT_MY_PUBLIC_INFO

class UserWallSettingFragment:BottomSheetDialogFragment() {
    private var _binding:DialogBottomWallSettingBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            dialog?.context?.let { it1 -> bottomSheet?.background = ViewHelper.bgWhiteCornersTop16(it1) }
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        _binding = DialogBottomWallSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SessionManager.session.user?.hasPassword == false) {
            binding.tvEditPw.setText(R.string.cap_nhat_mat_khau)
        } else {
            binding.tvEditPw.setText(R.string.thay_doi_mat_khau)
        }
        binding.imgCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvEditInfo.setOnClickListener {
            findNavController().popBackStack()

            val intent = Intent(requireContext(),EditInformationPublicActivity::class.java)
            requireActivity().startActivityForResult(intent, EDIT_MY_PUBLIC_INFO)
        }
        binding.tvEditPersonalInfo.setOnClickListener {
            requireContext().sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_EDIT_PERSONAL)
            })
            findNavController().popBackStack()
        }
        binding.tvEditFollowed.setOnClickListener {
            findNavController().popBackStack()
            val intent = Intent(requireContext(),ManagerUserFollowActivity::class.java)
            startActivity(intent)
        }
        binding.tvEditFollowing.setOnClickListener {
            findNavController().popBackStack()
            val intent = Intent(requireContext(),ManagerUserWatchingActivity::class.java)
            startActivity(intent)
        }
        binding.tvEditPrivacy.setOnClickListener {
            requireContext().sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_PRIVACY_SETTING)
            })
            findNavController().popBackStack()
        }
        binding.tvEditPw.setOnClickListener {

            requireContext().sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_EDIT_PASSWORD)
            })
            findNavController().popBackStack()
        }
    }


}