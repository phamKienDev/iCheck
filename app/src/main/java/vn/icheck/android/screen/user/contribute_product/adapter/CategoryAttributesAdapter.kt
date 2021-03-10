package vn.icheck.android.screen.user.contribute_product.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.databinding.ItemIntegerBinding
import vn.icheck.android.screen.user.contribute_product.holder.*
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.getLayoutInflater

const val IMAGE = 1
const val NUMBER = 2
const val BOOLEAN = 3
const val GROUP = 4
const val SELECT = 5
const val DATE = 6
const val MULTI_SELECT = 7
const val INTEGER = 8
const val NULL = -1

class CategoryAttributesAdapter(val listCategory:List<CategoryAttributesModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            IMAGE -> MultiImageHolder.create(parent)
            NUMBER -> NumberTextHolder.create(parent)
            BOOLEAN -> BooleanHolder.create(parent)
            GROUP -> GroupHolder.create(parent)
            SELECT -> SelectHolder.create(parent)
            DATE -> DateHolder.create(parent)
            MULTI_SELECT -> MultiSelectHolder.create(parent)
            INTEGER -> IntegerHolder(ItemIntegerBinding.inflate(parent.getLayoutInflater(), parent, false))
            else -> NullHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            IMAGE -> (holder as MultiImageHolder).bind(listCategory[position])
            NUMBER ->{
                (holder as NumberTextHolder).bind(listCategory[position])
            }
            BOOLEAN -> (holder as BooleanHolder).bind(listCategory[position])
            GROUP -> {
                (holder as GroupHolder).bind(listCategory[position])
            }
            SELECT -> (holder as SelectHolder).bind(listCategory[position])
            DATE -> (holder as DateHolder).bind(listCategory[position])
            MULTI_SELECT -> (holder as MultiSelectHolder).bind(listCategory[position])
            INTEGER -> (holder as IntegerHolder).bind(listCategory[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listCategory[position].categoryItem.type) {
            "image-single","image" -> IMAGE
            "number"-> NUMBER
            "boolean" -> BOOLEAN
            "group","htmleditor", "textarea","text" -> GROUP
            "select" -> SELECT
            "date" -> DATE
            "integer" -> INTEGER
            "multiselect" -> MULTI_SELECT
            else -> NULL
        }
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

}