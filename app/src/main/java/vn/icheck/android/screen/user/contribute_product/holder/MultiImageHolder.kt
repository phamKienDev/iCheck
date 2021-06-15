package vn.icheck.android.screen.user.contribute_product.holder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.*
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemContributeImageBinding
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.ick.simpleText

class MultiImageHolder(private val itemContributeImageBinding: ItemContributeImageBinding) : RecyclerView.ViewHolder(itemContributeImageBinding.root) {
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            itemContributeImageBinding.imgHelp.beGone()
//            TooltipCompat.setTooltipText(itemContributeImageBinding.imgHelp,null)
        } else {
            itemContributeImageBinding.imgHelp.beVisible()
//            TooltipCompat.setTooltipText(itemContributeImageBinding.imgHelp,categoryAttributesModel.categoryItem.description)
            balloon = createBalloon(itemView.context){
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
            itemContributeImageBinding.imgHelp.setOnClickListener {
                if (balloon?.isShowing == false) {
                    balloon?.showAlignBottom(itemContributeImageBinding.imgHelp)
                } else {
                    balloon?.dismiss()
                }
            }
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            itemContributeImageBinding.tvTitle.rText(R.string.s_bat_buoc, categoryAttributesModel.categoryItem.name)
        } else {
            itemContributeImageBinding.tvTitle simpleText categoryAttributesModel.categoryItem.name
        }
        categoryAttributesModel.listImageAdapter.parentPosition = bindingAdapterPosition
//        if (categoryAttributesModel.isEditable) {
        itemContributeImageBinding.rcvImages.adapter = categoryAttributesModel.listImageAdapter
//            itemContributeImageBinding.img.setOnClickListener {
//                itemContributeImageBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
//                    putExtra(CONTRIBUTIONS_ACTION, TAKE_IMAGE)
//                    putExtra(TAKE_IMAGE, bindingAdapterPosition)
//                })
//            }
//        }
//        if (categoryAttributesModel.values as File? != null) {
//            itemContributeImageBinding.img loadSimpleFile categoryAttributesModel.values as File?
//            itemContributeImageBinding.imgDelete visibleIf true
//        }
    }

    companion object {
        fun create(parent: ViewGroup): MultiImageHolder {
            val itemContributeImageBinding = ItemContributeImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MultiImageHolder(itemContributeImageBinding)
        }
    }
}