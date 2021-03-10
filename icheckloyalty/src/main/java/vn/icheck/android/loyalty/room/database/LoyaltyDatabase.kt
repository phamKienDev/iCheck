package vn.icheck.android.loyalty.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.room.dao.IdCampaignDao
import vn.icheck.android.loyalty.room.entity.ICKCampaignId

@Database(entities = [ICKCampaignId::class], version = 1)
abstract class LoyaltyDatabase : RoomDatabase() {

    abstract fun idCampaignDao(): IdCampaignDao

    companion object {
        @Volatile
        private var INSTANCE: LoyaltyDatabase? = null

        fun getDatabase(context: Context): LoyaltyDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance = Room.databaseBuilder(
                                context.applicationContext,
                                LoyaltyDatabase::class.java,
                                ConstantsLoyalty.ROOM_NAME
                        )
                                .fallbackToDestructiveMigration()
                                .allowMainThreadQueries()
                                .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}