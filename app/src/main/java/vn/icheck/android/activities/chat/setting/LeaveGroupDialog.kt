package vn.icheck.android.activities.chat.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.leave_group_dialog.*
import vn.icheck.android.R
import java.lang.ref.WeakReference

class LeaveGroupDialog(val typeDialog:Int, val chatSetting: WeakReference<ChatV2SettingActivity>):DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.leave_group_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (typeDialog == ChatV2SettingActivity.USER) {
            tv_title.text = "Bạn muốn xóa cuộc hội thoại?"
        } else {
            tv_title.text = "Bạn muốn rời nhóm chat?"
        }
        btn_yes.setOnClickListener {
            dismiss()
        }
        btn_no.setOnClickListener {
            chatSetting.get()?.leaveChat()
            dismiss()
        }
    }
}