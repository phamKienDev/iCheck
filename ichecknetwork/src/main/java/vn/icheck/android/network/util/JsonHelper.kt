package vn.icheck.android.network.util

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonObject
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.network.models.product.detail.ICProductVariant

/**
 * Created by lecon on 11/26/2017
 */
object JsonHelper {
    val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> toJson(list: MutableList<T>): String {
        return gson.toJson(list)
    }

    fun <T> parseJson(json: String?, clazz: Class<T>): T? {
        if (json.isNullOrEmpty())
            return null

        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> parseJson(json: JsonObject, clazz: Class<T>): T? {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            Log.e("e", e.localizedMessage, e)
            null
        }
    }

    fun  <T> parseList(json: String?): MutableList<T>? {
        return try {
            val listType = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListStoreSellHistory(json: String?): MutableList<ICStoreNear>? {
        return try {
            val listType = object : TypeToken<List<ICStoreNear>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListMission(json: String?): MutableList<ICMission>? {
        return try {
            val listType = object : TypeToken<List<ICMission>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListAttachment(json: String?): MutableList<ICMedia>? {
        return try {
            val listType = object : TypeToken<List<ICMedia>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListAttachmentPage(json: String?): MutableList<ICMediaPage>? {
        return try {
            val listType = object : TypeToken<List<ICMediaPage>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseVerify(json: String?): Boolean? {
        return try {
            gson.fromJson(json, Boolean::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListCertificate(json: String?): MutableList<String>? {
        return try {
            val listType = object : TypeToken<MutableList<String>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListVendor(json: String?): MutableList<ICPage>? {
        return try {
            val listType = object : TypeToken<MutableList<ICPage>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListQuestion(json: String?): MutableList<ICProductQuestion>? {
        return try {
            val listType = object : TypeToken<MutableList<ICProductQuestion>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListShopVariant(json: String?): MutableList<ICShopVariantV2>? {
        return try {
            val listType = object : TypeToken<MutableList<ICProductVariant.ProductRow>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListInformation(json: String?): MutableList<ICProductInformations>? {
        return try {
            val listType = object : TypeToken<MutableList<ICProductInformations>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListSuggestProduct(json: String?): MutableList<ICProductTrend>? {
        return try {
            val listType = object : TypeToken<MutableList<ICProductTrend>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListReviewsProduct(json: String?): MutableList<ICPost>? {
        return try {
            val listType = object : TypeToken<MutableList<ICPost>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListPageTrends(json: String?): MutableList<ICPageTrend>? {
        return try {
            val listType = object : TypeToken<MutableList<ICPageTrend>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListCampaigns(json: String?): MutableList<ICCampaign>? {
        return try {
            val listType = object : TypeToken<MutableList<ICCampaign>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListRelatedPage(json: String?): MutableList<ICRelatedPage>? {
        return try {
            val listType = object : TypeToken<MutableList<ICRelatedPage>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseListCategoriesProduct(json: String?): MutableList<ICCategoriesProduct>? {
        return try {
            val listType = object : TypeToken<MutableList<ICRelatedPage>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun parseDomainQr(json: String?): List<ICClientSetting> {
        return try {
            val listType = object : TypeToken<List<ICClientSetting>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            listOf()
        }
    }
}