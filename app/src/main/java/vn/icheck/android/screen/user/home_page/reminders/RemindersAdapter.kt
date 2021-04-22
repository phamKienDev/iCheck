package vn.icheck.android.screen.user.home_page.reminders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.ItemReminderBinding
import vn.icheck.android.network.model.reminders.ReminderResponse
import vn.icheck.android.util.ick.*

class RemindersAdapter(val listData: List<ReminderResponse>, val onDismissReminder:(Int) -> Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = parent.getLayoutInflater()
        return RemindersHolder(ItemReminderBinding.inflate(lf, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RemindersHolder).bind(listData[position])
    }

    override fun getItemCount() = listData.size

    inner class RemindersHolder(val binding: ItemReminderBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(reminderResponse: ReminderResponse) {
            binding.tvReminderContent simpleText reminderResponse.message
            if (!reminderResponse.label.isNullOrEmpty()) {
                binding.tvAction simpleText reminderResponse.label
            }
            binding.tvAction.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.DO_REMINDER, reminderResponse.redirectPath))
//                ICheckApplication.currentActivity()?.let { activity ->
//                    FirebaseDynamicLinksActivity.startTargetPath(activity as FragmentActivity, reminderResponse.redirectPath)
//                }
            }
            binding.imageView14.loadImageWithHolder(reminderResponse.icon, R.drawable.ic_reminder_item)
            if (reminderResponse.type != null) {
                binding.imgClose.beVisible()
                binding.imgClose.setOnClickListener {
                    onDismissReminder(bindingAdapterPosition)
                }
            } else {
                binding.imgClose.beGone()
            }
        }
    }
}