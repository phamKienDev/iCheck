package vn.icheck.android.room.entity

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import vn.icheck.android.network.models.ICItemCart

class ItemsConverter {
    @TypeConverter
    fun fromImagesJson(stat: MutableList<ICItemCart>?): String? {
        val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
        return gson.toJson(stat)
    }

    @TypeConverter
    fun toImagesList(jsonImages: String?): MutableList<ICItemCart>? {
        val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
        val notesType = object : TypeToken<MutableList<ICItemCart>>() {}.type
        return gson.fromJson<MutableList<ICItemCart>>(jsonImages, notesType)
    }
}