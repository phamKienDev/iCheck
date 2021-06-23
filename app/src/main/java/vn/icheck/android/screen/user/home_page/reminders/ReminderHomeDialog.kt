package vn.icheck.android.screen.user.home_page.reminders

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.DialogReminderHomeBinding
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.home_page.HomePageViewModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class ReminderHomeDialog:BaseBottomSheetDialogFragment() {
    private var _binding: DialogReminderHomeBinding? = null
    val binding get() = _binding!!
    private val viewModel: HomePageViewModel by activityViewModels()
    private lateinit var remindersAdapter: RemindersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogReminderHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.tvReminderCount.setText(R.string.loi_nhac_d, viewModel.getRemindersCount())
        if (viewModel.getRemindersCount() ?: 0 > 0) {
            binding.imgNoReminder.beGone()
            binding.tvNoReminder.beGone()
            binding.tvSubNoReminder.beGone()
            binding.rcvReminders.beVisible()
            remindersAdapter = RemindersAdapter(viewModel.getListReminders()) {position ->
                object : ConfirmDialog(requireContext(), binding.root.context.getString(R.string.ban_muon_xoa_loi_nhac_nay),null,binding.root.context.getString(R.string.de_sau),binding.root.context.getString(R.string.co), true){
                    override fun onDisagree() {
                    }

                    override fun onAgree() {
                        viewModel.deleteReminder(position).observe(viewLifecycleOwner, Observer {
                            if (it.statusCode == "200") {
                                requireContext().showShortSuccessToast(binding.root.context.getString(R.string.ban_da_xoa_loi_nhan_thanh_cong))
                                remindersAdapter.notifyItemRemoved(position)
                                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_REMINDER))

                            }
                        })
                    }

                    override fun onDismiss() {
                    }
                }.show()
            }
            binding.rcvReminders.adapter = remindersAdapter
        } else {
            binding.imgNoReminder.beVisible()
            binding.tvNoReminder.beVisible()
            binding.tvSubNoReminder.beVisible()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.UPDATE_REMINDER) {
            Handler().post {
                if (viewModel.getRemindersCount() ?: 0 == 0 || viewModel.getListReminders().isEmpty()) {
                    binding.rcvReminders.beGone()
                    binding.imgNoReminder.beVisible()
                    binding.tvNoReminder.beVisible()
                    binding.tvSubNoReminder.beVisible()
                    binding.tvReminderCount.setText(R.string.loi_nhac)
                } else {
                    binding.tvReminderCount.setText(R.string.loi_nhac_d, viewModel.getRemindersCount())
                }
            }
        }else if (event.type == ICMessageEvent.Type.DO_REMINDER) {
            dismiss()
            FirebaseDynamicLinksActivity.startTargetPath(requireActivity(), event.data.toString())
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.DISMISS_REMINDER))
    }
}