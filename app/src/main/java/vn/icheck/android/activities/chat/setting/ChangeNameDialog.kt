package vn.icheck.android.activities.chat.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.change_name_dialog.*
import vn.icheck.android.R
import vn.icheck.android.util.kotlin.ToastUtils
import java.lang.ref.WeakReference

class ChangeNameDialog(val currentName:String, val act:WeakReference<ChatV2SettingActivity>): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.change_name_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edt_name.setText(currentName)
        btn_clear.setOnClickListener {
            edt_name.setText("")
        }
        btn_no.setOnClickListener {
            if (edt_name.text.isNotEmpty()) {
                act.get()?.changeName(edt_name.text.toString())
                dismiss()
            } else {
                ToastUtils.showShortError(view.context, "Không để trống tên!")
            }
        }
        btn_yes.setOnClickListener {
            dismiss()
        }
    }
}