package vn.icheck.android.screen.user.shipping.ship.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import vn.icheck.android.databinding.FragmentShipBinding
import vn.icheck.android.screen.user.shipping.ship.adpter.vm.ShipViewModel
import vn.icheck.android.screen.user.shipping.ship.adpter.adapter.ShipAddressAdapter
import vn.icheck.android.util.ick.dismissLoadingScreen
import vn.icheck.android.util.ick.showLoadingTimeOut

class SelectShipAddressFragment : Fragment() {

    companion object {
        fun newInstance() = SelectShipAddressFragment()
    }

    private val viewModel: ShipViewModel by activityViewModels()
    lateinit var shipAddressAdapter: ShipAddressAdapter
    private var _binding: FragmentShipBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentShipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingTimeOut(30000)
        shipAddressAdapter = ShipAddressAdapter(viewModel.arrayAddress, { chooseId ->
            viewModel.arrayAddress.forEach {
                if (it != null) {
                    it.isChecked = false
                }
                if (it?.id == chooseId) {
                    it.isChecked = true
                    viewModel.setChosen(chooseId)
                    binding.btnConfirm.enable()
                }
            }
            shipAddressAdapter.notifyDataSetChanged()
        }, { updateId ->
            viewModel.setUpdate(updateId)
        })
        binding.rcvAddress.adapter = shipAddressAdapter
        binding.btnBack.setOnClickListener {
            if (requireActivity().intent.getBooleanExtra("cart", false)) {
                viewModel.moveToCart()
            } else {
                requireActivity().finish()
            }
        }
        binding.btnConfirm.setOnClickListener {
            viewModel.moveToConfirm()
        }
        viewModel.getShipAddress().observe(viewLifecycleOwner, {
            if (it.statusCode == "200") {
                binding.rcvAddress.post {
                    if (viewModel.getChosen() == -1L) {
                        it.data?.rows?.firstOrNull()?.isChecked = true
                        it.data?.rows?.firstOrNull()?.id?.let { it1 -> viewModel.setChosen(it1) }
                    } else {
                        it.data?.rows?.firstOrNull{add ->
                            add.id == viewModel.getChosen()
                        }?.isChecked = true
                        it.data?.rows?.firstOrNull{add ->
                            add.id == viewModel.getChosen()
                        }?.id?.let { it1 -> viewModel.setChosen(it1) }
                    }
                    if (!it.data?.rows.isNullOrEmpty()) {
                        binding.btnConfirm.enable()
                    }
                    viewModel.arrayAddress.clear()
                    viewModel.arrayAddress.addAll(it.data?.rows ?: arrayListOf())
                    viewModel.arrayAddress.add(null)
                    shipAddressAdapter.notifyDataSetChanged()
                }
            }
            dismissLoadingScreen()
        })
    }

}   