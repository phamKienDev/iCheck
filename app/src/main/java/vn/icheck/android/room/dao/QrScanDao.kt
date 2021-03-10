package vn.icheck.android.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import vn.icheck.android.room.entity.ICQrScan

@Dao
interface QrScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQrScan(qrScan: ICQrScan)

    @Query("select * from qr_scan_table where maQr = :id")
    fun search(id: String): LiveData<ICQrScan?>

    @Query("UPDATE qr_scan_table SET noidungQr = :noidungQr, loaima = :loaima, idProduct = :idProduct, imgProduct= :imgProduct, nameProduct= :nameProduct,price= :price,specialPrice= :specialPrice,seller_id= :seller_id,barcode= :barcode WHERE maQr==:maQr")
    suspend fun update(noidungQr: String?, loaima: Int?, maQr: String, idProduct: Long?, imgProduct: String?, nameProduct: String?, price: String?, specialPrice: String?, seller_id: Long?, barcode: String?)

//    @Query("SELECT * FROM qr_scan_table ORDER BY timeCreated DESC LIMIT :limit OFFSET :offset")
//    fun getAllData(limit: Int, offset: Int): Flowable<MutableList<ICQrScan>>

    @Query("SELECT * FROM qr_scan_table ORDER BY timeCreated DESC")
    fun getAllData(): Flowable<MutableList<ICQrScan>>
}