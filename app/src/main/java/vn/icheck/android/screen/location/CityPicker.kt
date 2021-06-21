package vn.icheck.android.screen.location

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.databinding.BottomNationDialogBinding
import vn.icheck.android.databinding.ItemCityBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.model.location.CityItem
import vn.icheck.android.network.model.location.CityResponse
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.logError

const val CITY = 1
const val DISTRICT = 2
const val WARD = 3

@AndroidEntryPoint
class CityPicker(val type:Int, private val onCityClick: OnCityClick,val cityId:Int?) : BottomSheetDialogFragment() {

    private var _binding: BottomNationDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var cityAdapter: CityAdapter
    private val locationViewModel:LocationViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            dialog?.context?.let { it1 -> bottomSheet?.background = ViewHelper.bgWhiteCornersTop16(it1) }
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        _binding = BottomNationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogNoKeyboardStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cityAdapter = CityAdapter(onCityClick)
        binding.edtSearch.setHintTextColor(Constant.getDisableTextColor(requireContext()))
        binding.edtSearch.background=ViewHelper.bgGrayCorners4(requireContext())
        binding.rcvNation.layoutManager = WrapContentLinearLayoutManager(requireContext())
        binding.rcvNation.adapter = cityAdapter
        when (type) {
            CITY -> {
                binding.tvTitle.setText(R.string.chon_tinh_thanh)
                locationViewModel.getCities().observe(viewLifecycleOwner, Observer {
                    submitItems(it)
                })
                initSearch()
            }
            DISTRICT -> {
                binding.tvTitle.setText(R.string.chon_quan_huyen)
                locationViewModel.getDistricts(cityId).observe(viewLifecycleOwner, Observer {
                    submitItems(it)
                })
                initSearch()
            }
            WARD -> {
                binding.tvTitle.setText(R.string.chon_phuong_xa)
                locationViewModel.getWards(cityId).observe(viewLifecycleOwner, Observer {
                    submitItems(it)
                })
                initSearch()
            }
        }
        binding.btnClear.setOnClickListener {
            dismiss()
        }
    }


    fun hideKeyBoard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (imm?.isActive == true) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private fun submitItems(it: CityResponse?) {
        val listCity = it?.data?.rows
        cityAdapter.submitList(listCity)
    }

    private fun initSearch() {
        binding.edtSearch.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch {
                    delay(200)
                    locationViewModel.response?.data?.rows?.let { list ->
                        try {
                            val listCity = list.filter {
                                it?.name!!.contains(s.toString(), true)
                            }
                            if (listCity.isNotEmpty() || s.toString().trim().isNotEmpty()) {
                                cityAdapter.submitList(listCity)
                            } else {
                                val origin = locationViewModel.response?.data?.rows
                                cityAdapter.submitList(origin)
                            }
                        } catch (e: Exception) {
                            logError(e)
                        }
                    }
                }
            }
        })
    }
    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<CityItem>() {
            override fun areItemsTheSame(oldItem: CityItem, newItem: CityItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CityItem, newItem: CityItem): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    inner class CityAdapter(private val onCityClick: OnCityClick) : ListAdapter<CityItem, RecyclerView.ViewHolder>(COMPARATOR) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemCity(ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as ItemCity
            holder.bind(getItem(position))
        }

        inner class ItemCity(val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(item:CityItem) {
                binding.text.text = item.name
                binding.text.setOnClickListener {
                    hideKeyBoard()
                    onCityClick.onClick(item)
                }
            }
        }
    }

    interface OnCityClick{
        fun onClick(city:CityItem)
    }
}