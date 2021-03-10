package vn.icheck.android.screen.location

import android.content.res.AssetManager
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bottom_nation_dialog.*
import vn.icheck.android.R
import vn.icheck.android.model.country.CountryCode
import vn.icheck.android.model.country.Nation
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.util.AfterTextWatcher

class IckNationBottomDialog : BottomSheetDialogFragment() {
    lateinit var nationAdapter: NationAdapter
    var array = arrayListOf<Nation>()
    val ickLoginViewModel: IckLoginViewModel by activityViewModels()
    val callback = object : NationAdapter.OnNationClick {
        override fun onNationClick(nation: Nation) {
            ickLoginViewModel.nationLiveData.postValue(nation)
//            findNavController().popBackStack()
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundResource(R.drawable.rounded_dialog)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.bottom_nation_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.MyDialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_clear.setOnClickListener {
            dismiss()
        }
        edt_search.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    nationAdapter.array.clear()
                    nationAdapter.array.addAll(array)
                    nationAdapter.notifyDataSetChanged()
                } else {
                    nationAdapter.array.clear()
                    nationAdapter.array.addAll(array.filter {
                        it.name.contains(s.toString(), true)
                    })
                    nationAdapter.notifyDataSetChanged()
                }
            }
        })
        val asm = context?.assets
        val s = asm?.readAssetsFile("CountryCodes.json")
        val gson = Gson()
        val arr = gson.fromJson(s, Array<Nation>::class.java)
        arr.sortWith(Comparator<Nation> { o1, o2 -> o1!!.name[0].compareTo(o2!!.name[0]) })
        array.addAll(arr)
        val cop = arrayListOf<Nation>()
        cop.addAll(arr)
        nationAdapter = NationAdapter(cop, callback)
        rcv_nation.adapter = nationAdapter
        rcv_nation.layoutManager = LinearLayoutManager(context)
    }

    fun AssetManager.readAssetsFile(fileName: String): String = open(fileName).bufferedReader().use { it.readText() }

    class NationAdapter(var array: ArrayList<Nation>, val onClickListener: OnNationClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val lf = LayoutInflater.from(parent.context)
            val v = lf.inflate(R.layout.nation_holder, parent, false)
            return NationHolder(v)
        }

        override fun getItemCount(): Int {
            return array.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as NationHolder).bind(array[position])
        }

        inner class NationHolder(val view: View) : RecyclerView.ViewHolder(view) {

            fun bind(countryCode: CountryCode) {
                view.findViewById<TextView>(R.id.tv_nation_name).text = countryCode.getName()
                view.findViewById<TextView>(R.id.tv_code).text = "+" + countryCode.numeric.toString()
            }

            fun bind(nation: Nation) {
                view.findViewById<TextView>(R.id.tv_nation_name).text = nation.name
                view.findViewById<TextView>(R.id.tv_code).text = nation.dialCode
                view.rootView.setOnClickListener { onClickListener.onNationClick(nation) }
            }

        }

        interface OnNationClick {
            fun onNationClick(nation: Nation)
        }
    }
}