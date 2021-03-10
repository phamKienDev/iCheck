package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_id_in_cart")
data class ICProductIdInCart(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long,
        @ColumnInfo(name = "price") val price: Long,
        @ColumnInfo(name = "count") val count: Long
)