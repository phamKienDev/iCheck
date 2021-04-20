package vn.icheck.android.helper

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.model.category.CategoryAttributesItem
import vn.icheck.android.network.models.*
import vn.icheck.android.network.util.JsonHelper

class LayoutHelper {

    private fun <T> checkDataObj(json: JsonObject?, layout: ICLayout, viewType: Int, clazz: Class<T>, func: Unit?) {
        try {
            val data = getObject(json, layout.key, clazz)
            if (data != null) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <T> getObject(json: JsonObject?, key: String, clazz: Class<T>): T? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseJson(json?.getAsJsonObject(key).toString(), clazz)
        } else {
            null
        }
    }

    fun getButtonOfPage(json: JsonObject?): MutableList<ICButtonOfPage>? {
        return if (json?.get("pageOverview")?.isJsonNull != true) {
            val arrayString = (json!!.get("pageOverview") as JsonObject).getAsJsonArray("buttonConfigs").toString()

            val listButton = mutableListOf<ICButtonOfPage>()
            val jsonArray = JSONArray(arrayString)
            for (i in 0 until jsonArray.length()) {
                val btn = JsonHelper.parseJson(jsonArray[i].toString(), ICButtonOfPage::class.java)
                if (btn != null) {
                    listButton.add(btn)
                }
            }
            return listButton
        } else {
            null
        }
    }

    fun getAdsType(objectType: String): Int {
        return when (objectType) {
            "product_approach" -> { // Product - Tiếp cận
                ICViewTypes.ADS_PRODUCT
            }
            "product_change_buy" -> { // Product - Chuyển đổi mua
                ICViewTypes.ADS_PRODUCT
            }
            "page_approach" -> { // Page - Tiếp cận
                ICViewTypes.ADS_PAGE
            }
            "page_change_subscribe" -> { // Page - Chuyển đổi tham gia
                ICViewTypes.ADS_PAGE
            }
            "page_contact" -> { // Page - Liên hệ
                ICViewTypes.ADS_PAGE
            }
            "news_approach" -> { // News - Tiếp cận
                ICViewTypes.ADS_NEWS
            }
            "campaign_approach" -> { // Campaign - Tiếp cận
                ICViewTypes.ADS_CAMPAIGN
            }
            "campaign_change_subscribe" -> { // Campaign - Chuyển đổi tham gia
                ICViewTypes.ADS_CAMPAIGN
            }
            else -> { // other_banner - Banner - Banner
                ICViewTypes.ADS_BANNER
            }
        }
    }

    fun getListMediaPage(json: JsonObject?, key: String): MutableList<ICMediaPage>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListAttachmentPage(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListMedia(json: JsonObject?, key: String): MutableList<ICMedia>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListAttachment(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }


    fun getEnableContribution(json: JsonObject?, key: String): Boolean? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseVerify(json?.getAsJsonPrimitive(key).toString())
        } else {
            null
        }
    }


    fun getVerify(json: JsonObject?, key: String): Boolean? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseVerify(json?.getAsJsonPrimitive(key).toString())
        } else {
            null
        }
    }

    fun getListString(json: JsonObject?, key: String): MutableList<String>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListCertificate(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListVendor(json: JsonObject?, key: String): MutableList<ICPage>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListVendor(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListQuestion(json: JsonObject?, key: String): MutableList<ICProductQuestion>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListQuestion(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListShopVariant(json: JsonObject?, key: String): MutableList<ICShopVariantV2>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListShopVariant(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListInformation(json: JsonObject?, key: String): MutableList<ICProductInformations>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListInformation(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListProductTrend(json: JsonObject?, key: String): MutableList<ICProductTrend>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListSuggestProduct(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getReviewsProduct(json: JsonObject?, key: String): MutableList<ICPost>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListReviewsProduct(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListPageTrends(json: JsonObject?, key: String): MutableList<ICPageTrend>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListPageTrends(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListCampaigns(json: JsonObject?, key: String): MutableList<ICCampaign>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListCampaigns(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListRelatedPage(json: JsonObject?, key: String): MutableList<ICRelatedPage>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListRelatedPage(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getListCategoriesProduct(json: JsonObject?, key: String): MutableList<ICCategoriesProduct>? {
        return if (json?.get(key)?.isJsonNull != true) {
            return JsonHelper.parseListCategoriesProduct(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    fun getContributionInfoList(json: JsonObject?, key: String): List<CategoryAttributesItem>? {
        return if (json?.get(key)?.isJsonNull != true) {
            parseContributionInfo(json?.getAsJsonArray(key).toString())
        } else {
            null
        }
    }

    private fun parseContributionInfo(json: String?): List<CategoryAttributesItem>? {
        return try {
            val listType = object : TypeToken<List<CategoryAttributesItem>>() {}.type
            Gson().fromJson<List<CategoryAttributesItem>>(json, listType)
//            JsonHelper.gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }
}

fun MutableList<ICAdsNew>.getLayoutAdsRandom(): MutableList<ICLayout> {
    this.shuffle()
    val listData = mutableListOf<ICLayout>()

    if (this.size > 1) {
        this[0].let { ads ->
            listData.add(ICLayout("", "", ICRequest(), null, null, ads.objectType.getAdsType(), ads, ICViewTypes.ADS_TYPE))
        }
        this[1].let { ads ->
            listData.add(ICLayout("", "", ICRequest(), null, null, ads.objectType.getAdsType(), ads, ICViewTypes.ADS_TYPE))
        }
    }

    return listData
}

fun String.getAdsType(): Int {
    return when (this) {
        "product_approach" -> { // Product - Tiếp cận
            ICViewTypes.ADS_PRODUCT
        }
        "product_change_buy" -> { // Product - Chuyển đổi mua
            ICViewTypes.ADS_PRODUCT
        }
        "page_approach" -> { // Page - Tiếp cận
            ICViewTypes.ADS_PAGE
        }
        "page_change_subscribe" -> { // Page - Chuyển đổi tham gia
            ICViewTypes.ADS_PAGE
        }
        "page_contact" -> { // Page - Liên hệ
            ICViewTypes.ADS_PAGE
        }
        "news_approach" -> { // News - Tiếp cận
            ICViewTypes.ADS_NEWS
        }
        "campaign_approach" -> { // Campaign - Tiếp cận
            ICViewTypes.ADS_CAMPAIGN
        }
        "campaign_change_subscribe" -> { // Campaign - Chuyển đổi tham gia
            ICViewTypes.ADS_CAMPAIGN
        }
        else -> { // other_banner - Banner - Banner
            ICViewTypes.ADS_BANNER
        }
    }
}