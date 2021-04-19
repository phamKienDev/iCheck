package vn.icheck.android.screen.user.wall.report_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.fragment.CoroutineBottomSheetDialogFragment
import vn.icheck.android.databinding.DialogReportUserBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.showSimpleErrorToast

class ReportUserDialog : CoroutineBottomSheetDialogFragment() {
    private var _binding: DialogReportUserBinding? = null
    private val binding get() = _binding!!
    val ickUserWallViewModel: IckUserWallViewModel by activityViewModels()
    lateinit var adapter: ReportUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.transparent, null))
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        _binding = DialogReportUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ickUserWallViewModel.reportCategory?.data?.rows?.let {
            ickUserWallViewModel.arrReport.clear()
            for (item in it) {
                ickUserWallViewModel.arrReport.add(ReportUserViewModel(item))
            }
            adapter = ReportUserAdapter(ickUserWallViewModel.arrReport) { position, data ->
                if (data is Boolean) {
                    ickUserWallViewModel.arrReport[position].checked = data
                }
                if (data is String) {
                    ickUserWallViewModel.arrReport[position].content = data
                }
            }
            binding.rcvReportUserCategory.adapter = adapter
        }
        binding.imgCancel.setOnClickListener {
            val filter = ickUserWallViewModel.arrReport.firstOrNull {
                it.checked
            }

            if (filter != null) {
                delayAction({
                    DialogHelper.showConfirm(dialog?.context, "Bạn muốn bỏ báo cáo này?", null, "Tiếp tục báo cáo", "Bỏ báo cáo", true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            this@ReportUserDialog.dismiss()
                        }
                    })
                })
            }else{
                this@ReportUserDialog.dismiss()
            }
        }
        binding.btnSendReport.setOnClickListener {
            delayAction({
                val filter = ickUserWallViewModel.arrReport.firstOrNull {
                    it.checked
                }
                if (filter != null) {
                    ickUserWallViewModel.sendReportUser().observe(viewLifecycleOwner, {
                        ickUserWallViewModel.showSuccessReport.postValue(1)
                        dismiss()
                    })
                } else {
                    requireContext().showSimpleErrorToast("Vui lòng chọn ít nhất một lý do")
                }
            })

        }
    }
}