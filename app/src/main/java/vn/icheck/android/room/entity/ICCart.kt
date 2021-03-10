package vn.icheck.android.room.entity

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICShop
import vn.icheck.android.network.models.ICThumbnail

@Entity(tableName = "cart_table")
data class ICCart(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long,
        @ColumnInfo(name = "shop_id") val shop_id: Long,
        @ColumnInfo(name = "shop") @TypeConverters(ShopCartConverter::class) val shop: ICShop,
        @ColumnInfo(name = "items") @TypeConverters(ItemsConverter::class) val items: MutableList<ICItemCart>
)