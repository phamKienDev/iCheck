package vn.icheck.android.screen.user.wall.friend_wall_setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.base.fragment.CoroutineBottomSheetDialogFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.DialogFriendWallSettingsBinding
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICMeFollowUser
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.*

class FriendWallSettingsDialog( val ickUserWallViewModel: IckUserWallViewModel, val owner: LifecycleOwner) : CoroutineBottomSheetDialogFragment() {

    private var _binding: DialogFriendWallSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.transparent, null))
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        _binding = DialogFriendWallSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SessionManager.isUserLogged) {
            when {
                RelationshipManager.checkFriend(ickUserWallViewModel.userInfo?.data?.id ?: 0L) -> {
                    if (AppDatabase.getDatabase().meFollowUserDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {
                        ickUserWallViewModel.userInfo?.data?.getName()?.let {
                            binding.tvUnfolow.setText(R.string.bo_theo_doi_s, it)
                        }
                    } else {
                        ickUserWallViewModel.userInfo?.data?.getName()?.let {
                            binding.tvUnfolow.setText(R.string.theo_doi_s, it)
                        }
                    }
                    if (AppDatabase.getDatabase().myFriendIdDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {
                        binding.layoutNotify.beVisible()
                        ickUserWallViewModel.userInfo?.data?.getName()?.let {
                            binding.tvTitleNotify.setText(R.string.huy_ket_ban_s, it)
                        }
                    } else {
                        if (AppDatabase.getDatabase().myFriendInvitationUserIdDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {
                            ickUserWallViewModel.userInfo?.data?.getName()?.let {
                                binding.tvTitleNotify.setText(R.string.huy_loi_moi_ket_ban_voi_s, it)
                            }
                        } else {
                            binding.layoutNotify.beGone()
                        }
                        //                    binding.tvTitleNotify simpleText "Kết bạn với ${ickUserWallViewModel.userInfo?.data?.getName()}"
                    }

                }
                else -> {
                    if (AppDatabase.getDatabase().meFollowUserDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {
                        ickUserWallViewModel.userInfo?.data?.getName()?.let {
                            binding.tvUnfolow.setText(R.string.bo_theo_doi_s, it)
                        }
                    } else {
                        ickUserWallViewModel.userInfo?.data?.getName()?.let {
                            binding.tvUnfolow.setText(R.string.theo_doi_s, it)
                        }
                    }
                    if (AppDatabase.getDatabase().myFriendInvitationUserIdDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {
                        binding.layoutNotify.beVisible()
                        ickUserWallViewModel.userInfo?.data?.getName()?.let {
                            binding.tvTitleNotify.setText(R.string.huy_loi_moi_ket_ban_voi_s, it)
                        }
                    } else {
                        binding.layoutNotify.beGone()
                    }
                }
            }
        } else {
            ickUserWallViewModel.userInfo?.data?.getName()?.let{
                binding.tvUnfolow.setText(R.string.theo_doi_s, )
            }
            binding.layoutNotify.beGone()
        }
        binding.layoutReport.setOnClickListener {
            delayAction({
                if (SessionManager.isUserLogged) {
                    ickUserWallViewModel.getReportUserCategory().observe(viewLifecycleOwner, {
                        ickUserWallViewModel.reportUserCategory.postValue(it?.data)
                        dismiss()
                    })
                } else {
                    dismiss()
                    requireActivity().showLogin()
                }
            })

        }
        binding.layoutNotify.setOnClickListener {
            if (!ickUserWallViewModel.inAction) {
                ickUserWallViewModel.inAction = true
                if (AppDatabase.getDatabase().myFriendIdDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {

                    object : ConfirmDialog(requireContext(), getString(R.string.ban_co_chac_chan_huy_ket_ban_voi_n_s_chu, ickUserWallViewModel.userInfo?.data?.getName()),null , getString(R.string.de_sau), getString(R.string.dong_y), true) {

                        override fun onDisagree() {
                            dismiss()
                            ickUserWallViewModel.inAction = false
                        }

                        override fun onAgree() {
                            ickUserWallViewModel.unFriendUser().observe(owner, {
                                if (it?.statusCode == "200") {
                                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UNFRIEND))
                                    AppDatabase.getDatabase().myFriendIdDao().deleteUserById(ickUserWallViewModel.userInfo?.data?.id!!)
                                    AppDatabase.getDatabase().meFollowUserDao().deleteUserById(ickUserWallViewModel.userInfo?.data?.id!!)
                                    RelationshipManager.removeFriendInvitationMe(ickUserWallViewModel.userInfo?.data?.id!!)
                                } else {
                                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ERROR_SERVER))
                                }
                                dismiss()
                                ickUserWallViewModel.inAction = false
                            })
                        }

                        override fun onDismiss() {
                        }
                    }.show()
                    dismiss()
                } else {
                    ickUserWallViewModel.removeFriendRequest().observe{
                        RelationshipManager.removeMyFriendInvitation(ickUserWallViewModel.id)
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.FRIEND_LIST_UPDATE, RelationshipManager.FRIEND_LIST_UPDATE))
                        dismiss()
                        ickUserWallViewModel.inAction = false
                    }
                }
            }

        }
        binding.layoutfollow.setOnClickListener {
            delayAction({
                if (SessionManager.isUserLogged) {
                    if (AppDatabase.getDatabase().meFollowUserDao().getUserByID(ickUserWallViewModel.userInfo?.data?.id!!) != null) {
                        ickUserWallViewModel.unFollowUser().observe(owner, {
                            if (it?.statusCode == "200") {
                                requireContext().showShortSuccessToast(getString(R.string.ban_da_huy_theo_doi_voi_s, ickUserWallViewModel.userInfo?.data?.getName()))
                                AppDatabase.getDatabase().meFollowUserDao().deleteUserById(ickUserWallViewModel.userInfo?.data?.id!!)
                                dismiss()
                            }
                        })
                    } else {
                        ickUserWallViewModel.followUser().observe(owner, {
                            if (it?.statusCode == "200") {
                                requireContext().showShortSuccessToast(getString(R.string.ban_da_theo_doi_s, ickUserWallViewModel.userInfo?.data?.getName()))
                                AppDatabase.getDatabase().meFollowUserDao().insertMeFollowUser(ICMeFollowUser(ickUserWallViewModel.userInfo?.data?.id!!))
                                dismiss()
                            }
                        })
                    }
                } else {
                    dismiss()
                    requireActivity().showLogin()
                }
            })


        }
    }
}