package vn.icheck.android.screen.user.contribute_product.viewmodel

import androidx.annotation.MainThread
import androidx.fragment.app.FragmentManager
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.model.category.CategoryAttributesItem
import vn.icheck.android.network.model.category.CategoryItem
import vn.icheck.android.screen.user.contribute_product.adapter.ListImageAdapter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CategoryAttributesModel(
        val categoryItem: CategoryAttributesItem
) {
    var values:Any? = null
    var isEditable = true
    var fragmentManager:FragmentManager? = null
    var calendar:Calendar? = null
    private val arrayListModel = arrayListOf<ImageModel>()

    val listImageAdapter: ListImageAdapter = ListImageAdapter(arrayListModel).apply {
        type = 1
    }

    fun hasImages(): Boolean {
        if (categoryItem.type == "image" || categoryItem.type == "image-single")
                if (arrayListModel.firstOrNull {
                    it.file != null
                } != null) {
            return true
        }
        return false
    }

    fun addAddImage() {
        arrayListModel.add(ImageModel(null))
    }

    fun addImage(file: File?) {
        arrayListModel.add(ImageModel(file))
        listImageAdapter.notifyDataSetChanged()
    }

    fun addSingleImage(file: File?) {
        arrayListModel.clear()
        arrayListModel.add(ImageModel(file))
        listImageAdapter.notifyDataSetChanged()
    }

    fun addAllImage(files: List<File>) {
        files.forEach {
            arrayListModel.add(ImageModel(it))
        }

    }

    fun getListImages() :List<File?>{
        return arrayListModel
                .filter {
                    it.file != null
                }
                .map {
            it.file
        }
    }

    fun removeAt(position: Int) {
        if (position <= arrayListModel.lastIndex && position > -1) {
            arrayListModel.removeAt(position)
            listImageAdapter.notifyItemRemoved(position)
        }
    }

    fun removeAll() {
        arrayListModel.clear()
        arrayListModel.add(ImageModel(null))
        listImageAdapter.notifyDataSetChanged()
    }

    fun getValues() {
        if (categoryItem.value != null) {
            values = when (categoryItem.value) {
                is Double -> {
                    categoryItem.value?.toString()?.toDouble()?.let {
                        getString(R.string.format_f, it)
                    }
                }
                is Boolean -> {
                    if(categoryItem.value == true) getString(R.string.co) else
                        getString(R.string.khong)
                }
                else -> {
                    categoryItem.value
                }
            }
        }
    }
}