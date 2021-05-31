package vn.icheck.android.base.dialog.notify.internal_stamp

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_internal_stamp.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.internal_stamp.adapter.InternalStampAdapter
import vn.icheck.android.base.dialog.notify.internal_stamp.view.IInternalStampView
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICSuggestApp

abstract class InternalStampDialog(val context: Context, val suggestApp: MutableList<ICSuggestApp>, val code: String?) : IInternalStampView {

    private var adapter = InternalStampAdapter(this)
    private var dialog :BottomSheetDialog? = null

    fun show() {
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog?.setContentView(R.layout.dialog_internal_stamp)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)

        dialog?.txtCancel?.background= dialog?.context?.let { ViewHelper.bgWhitePressRadius8(it) }
        dialog?.rcvInternalStamp?.layoutManager = LinearLayoutManager(dialog?.context)
        dialog?.rcvInternalStamp?.adapter = adapter
        adapter.setListData(suggestApp,code)

        dialog?.txtCancel?.setOnClickListener {
            dialog?.dismiss()
            onDismiss()
        }

        dialog?.setOnDismissListener {
            onDismiss()
        }

        dialog?.show()
    }

    override fun onClickGoToDetail(target: String?) {
        onGoToDetail(code)
        dialog?.dismiss()
        onDismiss()
    }

    override fun onClickPhone(target: String?) {
        onGoToPhone(target)
        dialog?.dismiss()
        onDismiss()
    }

    override fun onClickLink(target: String?, content: String?) {
        onGoToLink(target,content)
        dialog?.dismiss()
        onDismiss()
    }

    override fun onClickEmail(target: String?, content: String?) {
        onGoToEmail(target,content)
        dialog?.dismiss()
        onDismiss()
    }

    override fun onClickSms(target: String?, content: String?) {
        onGoToSms(target, content)
        dialog?.dismiss()
        onDismiss()
    }

    abstract fun onDismiss()
    abstract fun onGoToDetail(code: String?)
    abstract fun onGoToSms(target: String?, content: String?)
    abstract fun onGoToEmail(target: String?, content: String?)
    abstract fun onGoToLink(target: String?, content: String?)
    abstract fun onGoToPhone(target: String?)
}