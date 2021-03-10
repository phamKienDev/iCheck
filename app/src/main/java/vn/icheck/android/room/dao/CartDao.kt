package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single
import vn.icheck.android.room.entity.ICCart

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_table")
    fun getAllCartRealTime(): Flowable<MutableList<ICCart>>

    @Query("SELECT * FROM cart_table")
    fun getAllCart(): Single<MutableList<ICCart>>

    @Query("SELECT * FROM cart_table WHERE id = :id")
    fun getCartByID(id: Long): ICCart?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCart(obj: ICCart)

    @Query("DELETE FROM cart_table WHERE id = :id")
    fun deleteProvinceByID(id: Int)

    @Query("DELETE FROM cart_table")
    fun deleteAll()
}