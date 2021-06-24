package vn.icheck.android.screen.user.contribute_product.holder

import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.graphics.Color
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.internal.LinkedTreeMap
import com.skydoves.balloon.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.CoroutineViewHolder
import vn.icheck.android.constant.ATTRIBUTES_POSITION
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemGroupBinding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class GroupHolder(private val itemGroupBinding: ItemGroupBinding) : CoroutineViewHolder(itemGroupBinding.root) {
    var balloon: Balloon? = null
    var pasteBalloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        itemGroupBinding.edtInfo.apply {
            background=ViewHelper.bgTransparentStrokeLineColor1Corners10(itemGroupBinding.edtInfo.context)
            setHintTextColor(ColorManager.getDisableTextColor(itemView.context))
        }

        if (pasteBalloon == null) {
            pasteBalloon = createBalloon(itemView.context) {
                setLayout(R.layout.popup_tooltip)
                setBackgroundColor(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(itemView.context))
                setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            }
            pasteBalloon?.getContentView()?.findViewById<TextView>(R.id.tv_copy)?.setOnClickListener {
                if (!itemGroupBinding.edtInfo.text.isNullOrEmpty()) {
                    ICheckApplication.currentActivity()?.let { act ->
                        val clipboard = act.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(null, itemGroupBinding.edtInfo.text.toString())
                        clipboard.setPrimaryClip(clip)
                        it.context.showShortSuccessToast(it.context.getString(R.string.sao_chep_thanh_cong))
                        pasteBalloon?.dismiss()
                    }
                }
            }
            pasteBalloon?.getContentView()?.findViewById<TextView>(R.id.tv_paste)?.setOnClickListener {
                ICheckApplication.currentActivity()?.let { act ->
                    val clipboard = act.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    // If it does contain data, decide if you can handle the data.
                    // If it does contain data, decide if you can handle the data.
                    if (!clipboard.hasPrimaryClip()) {

                    } else if (!clipboard.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {

                        // since the clipboard has data but it is not plain text
                    } else {

                        //since the clipboard contains plain text.
                        val item = clipboard.primaryClip!!.getItemAt(0)

                        // Gets the clipboard as text.
                        val pasteData = item.text.toString()
                        if (!itemGroupBinding.edtInfo.text.isNullOrEmpty()) {
                            val text = itemGroupBinding.edtInfo.text.toString()
                            val sb = StringBuilder(text)
                            sb.append(pasteData)
                            itemGroupBinding.edtInfo.setText(sb.toString())
                        } else {
                            itemGroupBinding.edtInfo.setText(pasteData)
                        }
                        itemGroupBinding.edtInfo.setSelection(itemGroupBinding.edtInfo.text!!.length)
                    }
                    pasteBalloon?.dismiss()
                }
            }
        }
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            itemGroupBinding.imgHelp.beGone()
//            TooltipCompat.setTooltipText(itemGroupBinding.imgHelp,null)
        } else {
//            TooltipCompat.setTooltipText(itemGroupBinding.imgHelp,categoryAttributesModel.categoryItem.description)
            balloon = createBalloon(itemView.context) {
                setLayout(R.layout.item_popup)
                setHeight(BalloonSizeSpec.WRAP)
                setWidth(BalloonSizeSpec.WRAP)
                setAutoDismissDuration(5000L)
                setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                setArrowOrientation(ArrowOrientation.TOP)
                setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                setBackgroundColor(Color.parseColor("#2D9CDB"))
            }
            balloon?.getContentView()?.findViewById<TextView>(R.id.tv_popup)?.text = categoryAttributesModel.categoryItem.description.toString()
            itemGroupBinding.imgHelp.setOnClickListener {
                if (balloon?.isShowing == false) {
                    balloon?.showAlignBottom(itemGroupBinding.imgHelp)
                } else {
                    balloon?.dismiss()
                }
            }
            itemGroupBinding.imgHelp.beVisible()
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            itemGroupBinding.tvTitle.setText(R.string.s_bat_buoc, categoryAttributesModel.categoryItem.name?:"")
            itemGroupBinding.edtInfo.apply {
                hint = context.getString(R.string.nhap_s_bat_buoc, categoryAttributesModel.categoryItem.name?:"")
            }
        } else {
            itemGroupBinding.tvTitle.text = categoryAttributesModel.categoryItem.name
            itemGroupBinding.edtInfo.apply {
                hint = context.getString(R.string.nhap_s, categoryAttributesModel.categoryItem.name)
            }
        }
        itemGroupBinding.edtInfo.setOnLongClickListener {
            pasteBalloon?.show(itemGroupBinding.edtInfo)
            return@setOnLongClickListener true
        }
        if (categoryAttributesModel.values != null && categoryAttributesModel.values.toString().isNotEmpty()) {

            try {
                val text = categoryAttributesModel.values as LinkedTreeMap<String, String>
//                val jsonObject = JSONObject("{\"${text.keys.first()}\":\"${text.values.first()}\"")
                val jsonObject = JSONObject(text.toMap())
                itemGroupBinding.edtInfo.setText(jsonObject.getString("shortContent"))
            } catch (e: Exception) {
                try {
                    val text = categoryAttributesModel.values as HashMap<String, String>
//                val jsonObject = JSONObject("{\"${text.keys.first()}\":\"${text.values.first()}\"")
                    val jsonObject = JSONObject(text.toMap())
                    itemGroupBinding.edtInfo.setText(jsonObject.getString("shortContent"))
                } catch (e: Exception) {
                    itemGroupBinding.edtInfo.setText(categoryAttributesModel.values.toString())
                }
            }
        } else {
            itemGroupBinding.edtInfo.setText("")
        }
        itemGroupBinding.edtInfo.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
        itemGroupBinding.edtInfo.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    job = if (job?.isActive == true) {
                        job?.cancel()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(400)
                            itemGroupBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, s.toString())
                            })
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(400)
                            itemGroupBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, s.toString())
                            })
                        }
                    }
                }
            }
        })
    }

    companion object {
        fun create(parent: ViewGroup): GroupHolder {
            val itemGroupBinding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GroupHolder(itemGroupBinding)
        }
    }
}