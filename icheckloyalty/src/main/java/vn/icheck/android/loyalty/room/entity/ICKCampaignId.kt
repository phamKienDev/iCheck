package vn.icheck.android.loyalty.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaign_table")
data class ICKCampaignId(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)