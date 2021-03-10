package vn.icheck.android.loyalty.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.icheck.android.loyalty.room.entity.ICKCampaignId

@Dao
interface IdCampaignDao {
    @Query("SELECT * FROM campaign_table WHERE id = :id")
    fun getIDCampaignByID(id: Long): ICKCampaignId?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIDCampaign(obj: ICKCampaignId)

    @Query("DELETE FROM campaign_table")
    fun deleteAll()
}