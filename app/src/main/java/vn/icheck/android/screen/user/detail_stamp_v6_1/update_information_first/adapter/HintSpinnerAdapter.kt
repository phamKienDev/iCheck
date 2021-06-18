package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.detail_stamp_v6_1.ValueFItem


class HintSpinnerAdapter : ArrayAdapter<ValueFItem?> {
    @SuppressLint("ResourceType")
    constructor(theContext: Context?, objects: MutableList<ValueFItem>?) : super(theContext!!, R.id.text1, R.id.text1, objects!! as List<ValueFItem?>)

    constructor(theContext: Context?, objects: MutableList<ValueFItem>?, theLayoutResId: Int) : super(theContext!!, theLayoutResId, R.id.text1, objects!! as List<ValueFItem?>)

    constructor(context: Context?, theLayoutResId: Int, objects: MutableList<ValueFItem>?): super(context!!, theLayoutResId, objects!! as List<ValueFItem?>)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        (v as TextView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
//        v.setTextColor(Color.parseColor("#434343"))
        v.setTextColor(Constant.getNormalTextColor(context))
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
//        tv.setTextColor(Color.parseColor("#434343"))
        tv.setTextColor(Constant.getNormalTextColor(context))
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        return view
    }

    override fun getCount(): Int {
        val count = super.getCount()
        return if (count > 0) count - 1 else count
    }
}