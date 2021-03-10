package vn.icheck.android.room.entity

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import vn.icheck.android.network.models.ICShop

class ShopCartConverter {
    @TypeConverter
    fun fromImagesJson(stat: ICShop?): String? {
        val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
        return gson.toJson(stat)
    }

    @TypeConverter
    fun toImagesList(jsonImages: String?): ICShop? {
        val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
        return gson.fromJson(jsonImages, ICShop::class.java)
    }
}