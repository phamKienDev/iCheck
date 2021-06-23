package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.databinding.*
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICFieldGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem
import vn.icheck.android.ichecklibs.util.setText

class FieldAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICFieldGuarantee>()

    private val inputType = 1
    private val textType = 2
    private val selectType = 3
    private val checkboxType = 4
    private val dateType = 5
    private val radioBoxType = 6

    fun addData(list: MutableList<ICFieldGuarantee>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            inputType -> InputFieldHolder(parent)
            textType -> TextFieldHolder(parent)
            selectType -> SelectFieldHolder(parent)
            radioBoxType -> RadioButtonFieldHolder(parent)
            checkboxType -> CheckBoxFieldHolder(parent)
            dateType -> DateFieldHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InputFieldHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is TextFieldHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is SelectFieldHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is RadioButtonFieldHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is CheckBoxFieldHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is DateFieldHolder -> {
                val item = listData[position]
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listData[position].type) {
            "input" -> {
                inputType
            }
            "textarea" -> {
                textType
            }
            "select" -> {
                if (!listData[position].valueF.isNullOrEmpty()) {
                    if (listData[position].valueF?.size!! > 6) {
                        selectType
                    } else {
                        radioBoxType
                    }
                } else {
                    super.getItemViewType(position)
                }
            }
            "checkbox" -> {
                checkboxType
            }
            "date" -> {
                dateType
            }
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    override fun getItemCount() = listData.size

    inner class InputFieldHolder(parent: ViewGroup, val binding: ItemInputFieldBinding =
            ItemInputFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICFieldGuarantee>(binding.root) {

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listData[adapterPosition].string_values = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                binding.tvTitle.setText(R.string.s_bat_buoc, obj.name)
            } else {
                binding.tvTitle.text = obj.name
            }
            binding.edtInput.apply {
                background = ViewHelper.bgTransparentStrokeLineColor1Corners4(itemView.context)
                setHintTextColor(ColorManager.getSecondTextColor(itemView.context))
                hint = context.getString(R.string.nhap_s, obj.name)
                removeTextChangedListener(textWatcher)
                setText(obj.string_values)
                addTextChangedListener(textWatcher)
            }
        }
    }

    inner class TextFieldHolder(parent: ViewGroup, val binding: ItemTextFieldBinding =
            ItemTextFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICFieldGuarantee>(binding.root) {

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listData[adapterPosition].string_values = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                binding.tvTitleTextArea.setText(R.string.s_bat_buoc, obj.name)
            } else {
                binding.tvTitleTextArea.text = obj.name
            }
            binding.edtTextArea.apply {
                background = ViewHelper.bgTransparentStrokeLineColor1Corners4(itemView.context)
                setHintTextColor(ColorManager.getSecondTextColor(itemView.context))
                hint = context.getString(R.string.nhap_s, obj.name)
                removeTextChangedListener(textWatcher)
                setText(obj.string_values)
                addTextChangedListener(textWatcher)
            }
        }
    }

    inner class SelectFieldHolder(parent: ViewGroup, val binding: ItemSelectFieldBinding =
            ItemSelectFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICFieldGuarantee>(binding.root) {
        private var checkedPos = -1

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                binding.tvTitleSelect.setText(R.string.s_bat_buoc, obj.name)
            } else {
                binding.tvTitleSelect.text = obj.name
            }

            if (!obj.valueF.isNullOrEmpty()) {
                val list = obj.valueF
                val objHint = ValueFItem()
                objHint.id = null
                objHint.value = itemView.context.getString(R.string.chon_s, obj.name)
                list?.add(objHint)

                val adapter = HintSpinnerAdapter(binding.root.context, obj.valueF, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
                binding.spinner.background = ViewHelper.bgWhiteStrokeLineColor1Corners40(itemView.context)

                val selectedPosition = if (!obj.string_values.isNullOrEmpty()) {
                    try {
                        obj.string_values!!.toInt()
                    } catch (e: Exception) {
                        adapter.count
                    }
                } else {
                    adapter.count
                }
                binding.spinner.setSelection(selectedPosition)

                binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (checkedPos != position && checkedPos != -1) {
                            obj.valueF!![checkedPos].isChecked = false
                        }
                        checkedPos = position
                        if (obj.valueF!![checkedPos].id != null) {
                            obj.valueF!![checkedPos].isChecked = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }
        }
    }

    inner class RadioButtonFieldHolder(parent: ViewGroup, val binding: ItemRadioBoxFieldBinding =
            ItemRadioBoxFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICFieldGuarantee>(binding.root) {

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                binding.tvTitleRadiobox.setText(R.string.s_bat_buoc, obj.name)
            } else {
                binding.tvTitleRadiobox.text = obj.name
            }

            if (!obj.valueF.isNullOrEmpty() && obj.valueF!!.size > 0) {
                binding.rcvRadioBox.layoutManager = GridLayoutManager(binding.root.context, 2)
                binding.rcvRadioBox.adapter = RadioButtonFieldAdapter(obj.valueF!!, if (!obj.string_values.isNullOrEmpty()) {
                    try {
                        obj.string_values!!.toInt()
                    } catch (e: Exception) {
                        -1
                    }
                } else {
                    -1
                })
            }
        }
    }

    inner class CheckBoxFieldHolder(parent: ViewGroup, val binding: ItemCheckBoxFieldBinding =
            ItemCheckBoxFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICFieldGuarantee>(binding.root) {

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                binding.tvTitleCheckbox.setText(R.string.s_bat_buoc, obj.name)
            } else {
                binding.tvTitleCheckbox.text = obj.name
            }

            if (!obj.valueF.isNullOrEmpty() && obj.valueF!!.size > 0) {
                binding.rcvCheckBox.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rcvCheckBox.adapter = CheckBoxFieldAdapter(obj.valueF!!)
            }
        }
    }

    inner class DateFieldHolder(parent: ViewGroup, val binding: ItemDateFieldBinding =
            ItemDateFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICFieldGuarantee>(binding.root) {

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                binding.tvTitleDate.setText(R.string.s_bat_buoc, obj.name)
            } else {
                binding.tvTitleDate.text = obj.name
            }

            binding.edtInputDate.apply {
                background = ViewHelper.bgTransparentStrokeLineColor1Corners4(itemView.context)
                setHintTextColor(ColorManager.getSecondTextColor(itemView.context))
                text = vn.icheck.android.ichecklibs.TimeHelper.convertDateTimeSvToDateVn(obj.string_values)

                setOnClickListener {
                    TimeHelper.datePicker(binding.root.context, System.currentTimeMillis(), object : DateTimePickerListener {
                        override fun onSelected(dateTime: String, milliseconds: Long) {
                            binding.edtInputDate.text = dateTime
                            listData[adapterPosition].string_values = vn.icheck.android.ichecklibs.TimeHelper.convertMillisecondToDateTimeSv(milliseconds)
                        }
                    })
                }
            }
        }
    }
}