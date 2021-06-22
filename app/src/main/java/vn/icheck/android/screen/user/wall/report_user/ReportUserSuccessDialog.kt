package vn.icheck.android.screen.user.wall.report_user

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.icheck.android.R
import vn.icheck.android.databinding.DialogReportUserSuccessBinding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.ichecklibs.util.dpToPx

class ReportUserSuccessDialog:BottomSheetDialogFragment() {
    private var _binding:DialogReportUserSuccessBinding? = null
    private val binding get() = _binding!!
    val ickUserWallViewModel:IckUserWallViewModel by activityViewModels()

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
        _binding = DialogReportUserSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (item in ickUserWallViewModel.arrReport) {
            if (item.checked) {
                if (item.data?.name != "Khác") {
                    binding.linearLayout5.addView(TextView(requireContext()).apply {
                        setText(item.data?.name)
                        background = ViewHelper.bgGrayCorners32(requireContext())
                        setPadding(8.dpToPx())
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            setMargins(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 0)
                        }
                        typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                        setTextColor(ColorManager.getNormalTextColor(context))
                    })
                } else if(item.content.isNullOrEmpty()) {
                    binding.linearLayout5.addView(TextView(requireContext()).apply {
                        setText(item.data?.name)
                        background = ViewHelper.bgGrayCorners32(requireContext())
                        setPadding(8.dpToPx())
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            setMargins(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 0)
                        }
                        typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                        setTextColor(ColorManager.getNormalTextColor(context))
                    })
                }
            }
            if (item.content.isNotEmpty()) {
                binding.linearLayout5.addView(TextView(requireContext()).apply {
                    setText(item.content)
                    background = ViewHelper.bgGrayCorners32(requireContext())
                    setPadding(8.dpToPx())
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 0)
                    }
                    typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                    setTextColor(ColorManager.getNormalTextColor(context))
                })
            }
        }
        binding.btnDone.apply {
            background = ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                dismiss()
            }
        }
    }
}