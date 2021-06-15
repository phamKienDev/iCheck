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
import kotlinx.android.synthetic.main.item_check_box_field.view.*
import kotlinx.android.synthetic.main.item_date_field.view.*
import kotlinx.android.synthetic.main.item_input_field.view.*
import kotlinx.android.synthetic.main.item_select_field.view.*
import kotlinx.android.synthetic.main.item_text_field.view.*
import kotlinx.android.synthetic.main.item_radio_box_field.view.*
import vn.icheck.android.R
import vn.icheck.android.activities.product.contribute.NullHolder
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICFieldGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem
import vn.icheck.android.util.ick.rText


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
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            inputType -> InputFieldHolder(inflater.inflate(R.layout.item_input_field, parent, false))
            textType -> TextFieldHolder(inflater.inflate(R.layout.item_text_field, parent, false))
            selectType -> SelectFieldHolder(inflater.inflate(R.layout.item_select_field, parent, false))
            radioBoxType -> RadioButtonFieldHolder(inflater.inflate(R.layout.item_radio_box_field, parent, false))
            checkboxType -> CheckBoxFieldHolder(inflater.inflate(R.layout.item_check_box_field, parent, false))
            dateType -> DateFieldHolder(inflater.inflate(R.layout.item_date_field, parent, false))
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
                if (listData[position].valueF?.isNullOrEmpty() == false) {
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

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class InputFieldHolder constructor(view: View) : BaseViewHolder<ICFieldGuarantee>(view) {

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listData[adapterPosition].inputContent = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                itemView.tvTitle.rText(R.string.s_bat_buoc, obj.name)
            } else {
                itemView.tvTitle.text = obj.name
            }
            itemView.edtInput.apply {
                hint = context.rText(R.string.nhap_s, obj.name)
            }
            itemView.edtInput.removeTextChangedListener(textWatcher)
            itemView.edtInput.addTextChangedListener(textWatcher)
        }
    }

    inner class TextFieldHolder constructor(view: View) : BaseViewHolder<ICFieldGuarantee>(view) {
        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listData[adapterPosition].inputArea = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                itemView.tvTitleTextArea.rText(R.string.s_bat_buoc, obj.name)
            } else {
                itemView.tvTitleTextArea.text = obj.name
            }
            itemView.edtTextArea.apply {
                hint = context.rText(R.string.nhap_s, obj.name)
            }
            itemView.edtTextArea.removeTextChangedListener(textWatcher)
            itemView.edtTextArea.addTextChangedListener(textWatcher)
        }
    }

    inner class SelectFieldHolder constructor(view: View) : BaseViewHolder<ICFieldGuarantee>(view) {
        private var checkedPos = -1

        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                itemView.tvTitleSelect.rText(R.string.s_bat_buoc, obj.name)
            } else {
                itemView.tvTitleSelect.text = obj.name
            }

            if (!obj.valueF.isNullOrEmpty()) {
                val list = obj.valueF
                val objHint = ValueFItem()
                objHint.id = null
                objHint.value = itemView.context.rText(R.string.chon_s, obj.name)
                list?.add(objHint)

                val adapter = HintSpinnerAdapter(itemView.context, obj.valueF, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                itemView.spinner.adapter = adapter
                itemView.spinner.setSelection(adapter.count)
                itemView.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    inner class RadioButtonFieldHolder constructor(view: View) : BaseViewHolder<ICFieldGuarantee>(view) {
        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                itemView.tvTitleRadiobox.rText(R.string.s_bat_buoc, obj.name)
            } else {
                itemView.tvTitleRadiobox.text = obj.name
            }

            if (!obj.valueF.isNullOrEmpty() && obj.valueF!!.size > 0) {
                itemView.rcvRadioBox.layoutManager = GridLayoutManager(itemView.context, 2)
                itemView.rcvRadioBox.adapter = RadioButtonFieldAdapter(obj.valueF!!)
            }
        }
    }

    inner class CheckBoxFieldHolder constructor(view: View) : BaseViewHolder<ICFieldGuarantee>(view) {
        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                itemView.tvTitleCheckbox.rText(R.string.s_bat_buoc, obj.name)
            } else {
                itemView.tvTitleCheckbox.text = obj.name
            }

            if (!obj.valueF.isNullOrEmpty() && obj.valueF!!.size > 0) {
                itemView.rcvCheckBox.layoutManager = LinearLayoutManager(itemView.context)
                itemView.rcvCheckBox.adapter = CheckBoxFieldAdapter(obj.valueF!!)
            }
        }
    }

    inner class DateFieldHolder constructor(view: View) : BaseViewHolder<ICFieldGuarantee>(view) {
        override fun bind(obj: ICFieldGuarantee) {
            if (obj.require == 1) {
                itemView.tvTitleDate.rText(R.string.s_bat_buoc, obj.name)
            } else {
                itemView.tvTitleDate.text = obj.name
            }

            itemView.setOnClickListener {
                TimeHelper.datePicker(itemView.context, System.currentTimeMillis(), object : DateTimePickerListener {
                    override fun onSelected(dateTime: String, milliseconds: Long) {
                        itemView.edtInputDate.text = dateTime
                        listData[adapterPosition].date = dateTime
                    }
                })
            }
        }
    }
}