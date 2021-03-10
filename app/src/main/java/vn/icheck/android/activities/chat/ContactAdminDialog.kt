package vn.icheck.android.activities.chat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.contact_icheck_dialog.view.*
import vn.icheck.android.R
import vn.icheck.android.activities.chat.v2.ChatV2Activity

class ContactAdminDialog():DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.attributes?.gravity = Gravity.TOP or Gravity.END
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        return inflater.inflate(R.layout.contact_icheck_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.root.setOnClickListener {
            activity?.also {
//                ChatActivity.createChatIcheck(it)
                ChatV2Activity.createChatIcheck(it)
            }
            dismiss()
        }
    }
}