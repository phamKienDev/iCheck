package vn.icheck.android.screen.user.report_product

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import kotlinx.android.synthetic.main.bottom_sheet_dialog_report_product.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.callback.OnItemClickListener
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.ui.SafeFlexboxLayoutManager

class ProductReportDialog(val list: MutableList<ICReportForm>) : BaseBottomSheetDialogFragment() {

    private var listener: OnItemClickListener? = null

    private var adapter : ProductReportReasonAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_dialog_report_product, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCancel.setOnClickListener {
            dismiss()
            listener?.onItemClick(btnCancel,0)
        }

        adapter = ProductReportReasonAdapter(list)
        recyclerView.layoutManager = dialog?.context?.let { SafeFlexboxLayoutManager(it, FlexDirection.ROW, FlexWrap.WRAP) }
        recyclerView.adapter = adapter
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onItemClick(btnCancel,0)
    }

    fun setOnDoneListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}