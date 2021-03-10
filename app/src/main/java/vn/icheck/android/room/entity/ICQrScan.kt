package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.icheck.android.network.models.detail_stamp_v6_1.ICShopVariantStamp
import java.io.Serializable

@Entity(tableName = "qr_scan_table")
open class ICQrScan(
        @PrimaryKey @ColumnInfo(name = "maQr") val maQr: String,
        @ColumnInfo(name = "timeCreated") val timeCreated: Long?,
        @ColumnInfo(name = "noidungQr") var noidungQr: String?,
//      1-tem, 2-van ban, 3-url, 4-sdt, 5-sms, 6-email, 7-vi tri, 8-danh ba, 9-lich su kien, 10-wifi
        @ColumnInfo(name = "loaima") var loaima: Int?,
        @ColumnInfo(name = "idProduct") var idProduct: Long?,
        @ColumnInfo(name = "imgProduct") var imgProduct: String?,
        @ColumnInfo(name = "nameProduct") var nameProduct: String?,
        @ColumnInfo(name = "maQrProduct") var maQrProduct: String?,
        @ColumnInfo(name = "price") var price: String?,
        @ColumnInfo(name = "specialPrice") var specialPrice: String?,
        @ColumnInfo(name = "seller_id") var seller_id: Long?,
        @ColumnInfo(name = "barcode") var barcode: String?,
        @ColumnInfo(name = "isRequest") var isRequest: Boolean,
        @ColumnInfo(name = "tag") var tag: String?,
        @ColumnInfo(name = "isStamp") var isStamp :String?
) : Serializable
