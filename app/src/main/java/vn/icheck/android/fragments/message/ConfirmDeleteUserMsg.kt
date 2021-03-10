package vn.icheck.android.fragments.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import vn.icheck.android.R
import vn.icheck.android.fragments.message.model.ICMessage
import vn.icheck.android.fragments.message.model.UserToUserModel

class ConfirmDeleteUserMsg(val icMessage: ICMessage):DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.confirm_delete_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.textView88).text = "Xóa hội thoại"
        view.findViewById<TextView>(R.id.tv_description).text =
                "Bạn có chắc muốn xoá cuộc hội thoại này không?"
        view.findViewById<TextView>(R.id.btn_yes).setOnClickListener {
            dismiss()
            NewMessagesFragment.instance?.deleteMessage(icMessage)
        }
        view.findViewById<TextView>(R.id.btn_no).setOnClickListener {
            dismiss()
        }
    }
}