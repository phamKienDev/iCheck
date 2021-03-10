package vn.icheck.android.screen.user.wall.updatephone

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import vn.icheck.android.R
import vn.icheck.android.databinding.FragmentChangePhoneBinding
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.AfterTextWatcher

class IckChangePhoneFragment:Fragment() {
    private var _binding:FragmentChangePhoneBinding? = null
    private val binding get() = _binding!!
    private val ickUserWallViewModel:IckUserWallViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChangePhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.imgClear.visibility = View.VISIBLE
                binding.divider20.background = ColorDrawable(Color.parseColor("#057DDA"))
            } else {
                binding.imgClear.visibility = View.INVISIBLE
                binding.divider20.background = ColorDrawable(Color.parseColor("#F0F0F0"))
            }
        }
        binding.edtPhone.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                validate()
            }
        })
        binding.imgClear.setOnClickListener {
            binding.edtPhone.setText("")
        }
        binding.btnContinue.setOnClickListener {
            val phone = binding.edtPhone.text.toString()
            val action = IckChangePhoneFragmentDirections.actionIckChangePhoneFragmentToIckConfirmChangePhoneFragment(phone)
            findNavController().navigate(action)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ickUserWallViewModel.changedPhone) {
            findNavController().popBackStack()
        }
    }

    private fun validate() {
        if (binding.edtPhone.text.length == 10) {
            enableContinue()
        } else {
            disableContinue()
        }
    }

    private fun disableContinue() {
      binding.btnContinue.disable()
    }

    private fun enableContinue() {
       binding.btnContinue.enable()
    }
}