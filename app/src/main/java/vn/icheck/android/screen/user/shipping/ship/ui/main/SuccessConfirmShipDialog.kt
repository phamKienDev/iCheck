package vn.icheck.android.screen.user.shipping.ship.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import vn.icheck.android.databinding.DialogConfirmShipSuccessBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.shipping.ship.ShipActivity

class SuccessConfirmShipDialog(val orderId:Long):DialogFragment() {
    private var _binding:DialogConfirmShipSuccessBinding? = null
    private val binding get() = _binding!!
    var action:(() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }
        _binding = DialogConfirmShipSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener {
            dismiss()
            action?.invoke()
        }
        binding.btnObserve.apply {
            background = ViewHelper.backgroundPrimaryCorners4(context)
            setOnClickListener {
                ShipActivity.startDetailOrder(requireContext(), orderId)
                dismiss()
                requireActivity().finish()
            }
        }
    }
}