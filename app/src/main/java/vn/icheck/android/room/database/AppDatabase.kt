package vn.icheck.android.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import vn.icheck.android.ICheckApplication
import vn.icheck.android.activities.chat.sticker.StickerDao
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.activities.chat.sticker.StickerPackagesDao
import vn.icheck.android.activities.chat.sticker.StickerView
import vn.icheck.android.model.chat.ChatConversation
import vn.icheck.android.room.dao.*
import vn.icheck.android.room.entity.*

@Database(entities = [ICProvince::class, ICDistrict::class, ICWard::class, ICCart::class, ICQrScan::class,
    StickerView::class, StickerPackages::class, ICMyFollowingPage::class,ChatConversation::class,
    ICOwnerPage::class, ICMeFollowUser::class, ICMyFriendIdUser::class, ICMyFriendInvitationUserId::class, ICFriendInvitationMeUserId::class, ICProductIdInCart::class], version = 26)
@TypeConverters(ShopCartConverter::class, ItemsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun provinceDao(): ProvinceDao
    abstract fun districtDao(): DistrictDao
    abstract fun wardDao(): WardDao
    abstract fun cartDao(): CartDao
    abstract fun qrScanDao(): QrScanDao
    abstract fun stickerDao(): StickerDao
    abstract fun stickerPackagesDao(): StickerPackagesDao
    abstract fun pageFollowsDao(): PageFollowDao
    abstract fun ownerPageDao(): OwnerPageDao
    abstract fun meFollowUserDao(): MeFollowUserDao
    abstract fun myFriendIdDao(): MyFriendIdDao
    abstract fun myFriendInvitationUserIdDao(): MyFriendInvitationUserIdDao
    abstract fun friendInvitationMeUserIdDao(): FriendInvitationMeUserIDDao
    abstract fun chatConversationDao(): ConversationDao
    abstract fun productIdInCartDao(): ProductIdInCartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context = ICheckApplication.getInstance()): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this codelab.
//                        .addCallback(AppDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}