package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.icheck.android.room.entity.ICProductIdInCart

@Dao
interface ProductIdInCartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(obj: ICProductIdInCart)

    @Query("SELECT * FROM product_id_in_cart WHERE id= :id")
    fun getProductById(id: Long): ICProductIdInCart?

    @Query("SELECT * FROM product_id_in_cart")
    fun getAll(): MutableList<ICProductIdInCart>

    @Query("DELETE FROM product_id_in_cart")
    fun deleteAll()

    @Query("DELETE FROM product_id_in_cart WHERE id=:id")
    fun deleteProductById(id: Long)

    @Query("UPDATE product_id_in_cart SET price=:price, count=:count WHERE id=:id")
    fun updateProduct(price: Long, count: Long, id: Long)
}