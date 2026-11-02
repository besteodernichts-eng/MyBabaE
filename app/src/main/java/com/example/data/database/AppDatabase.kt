package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.entities.DailyPriceRuleEntity
import com.example.data.entities.EscrowOrderEntity
import com.example.data.entities.LivestockEntity
import com.example.data.entities.MediaItemEntity
import com.example.data.entities.PlatformSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        LivestockEntity::class,
        DailyPriceRuleEntity::class,
        MediaItemEntity::class,
        EscrowOrderEntity::class,
        PlatformSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun livestockDao(): LivestockDao
    abstract fun priceRuleDao(): PriceRuleDao
    abstract fun mediaDao(): MediaDao
    abstract fun escrowDao(): EscrowDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dambazar_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            // Seed Price Rules
            val priceRules = listOf(
                DailyPriceRuleEntity(
                    livestockType = "SHEEP",
                    breed = "AFSHARI",
                    province = "زنجان",
                    basePricePerKg = 345_000L,
                    lastUpdatedJalali = "امروز - ۰۹:۱۵"
                ),
                DailyPriceRuleEntity(
                    livestockType = "SHEEP",
                    breed = "SHAL",
                    province = "قزوین",
                    basePricePerKg = 360_000L,
                    lastUpdatedJalali = "امروز - ۰۹:۱۵"
                ),
                DailyPriceRuleEntity(
                    livestockType = "SHEEP",
                    breed = "GHEZEL",
                    province = "آذربایجان شرقی",
                    basePricePerKg = 330_000L,
                    lastUpdatedJalali = "امروز - ۰۹:۱۵"
                ),
                DailyPriceRuleEntity(
                    livestockType = "SHEEP",
                    breed = "ROMANOV",
                    province = "تهران",
                    basePricePerKg = 380_000L,
                    lastUpdatedJalali = "امروز - ۰۹:۱۵"
                ),
                DailyPriceRuleEntity(
                    livestockType = "GOAT",
                    breed = "SAANEN",
                    province = "اصفهان",
                    basePricePerKg = 295_000L,
                    lastUpdatedJalali = "امروز - ۰۹:۱۵"
                ),
                DailyPriceRuleEntity(
                    livestockType = "GOAT",
                    breed = "BOER",
                    province = "فارس",
                    basePricePerKg = 320_000L,
                    lastUpdatedJalali = "امروز - ۰۹:۱۵"
                )
            )
            db.priceRuleDao().insertAllRules(priceRules)

            // Seed Platform Settings
            db.priceRuleDao().savePlatformSettings(
                PlatformSettingsEntity(
                    id = 1,
                    commissionPercent = 2.5,
                    transportCostPerKm = 25_000L,
                    baseButcheryCost = 350_000L
                )
            )

            // Seed Livestock Listings
            val initialLivestock = listOf(
                LivestockEntity(
                    id = 1,
                    title = "قوچ اصیل شال قزوین (پرواری ممتاز)",
                    livestockType = "RAM",
                    breed = "SHAL",
                    purpose = "FATTENING",
                    pricingType = "BY_WEIGHT",
                    weightKg = 84.5,
                    ageMonths = 14,
                    teethStatus = "TWO_TEETH",
                    pregnancyStatus = "NOT_APPLICABLE",
                    rfidCode = "IR-982104581290",
                    vetTrackingCode = "VET-QZ-403891",
                    province = "قزوین",
                    city = "بوئین‌زهرا",
                    distanceKm = 12,
                    basePricePerKg = 360_000L,
                    fixedPrice = 0L,
                    imageRes = "sample_dorper_sheep",
                    isLiveAuction = true,
                    auctionMinBid = 29_000_000L,
                    auctionCurrentBid = 30_420_000L,
                    auctionEndsInMinutes = 48,
                    auctionBidsCount = 7,
                    sellerName = "حاج اصغر بوئینی",
                    sellerPhone = "۰۹۱۲۳۴۵۶۷۸۹",
                    healthCertificateVerified = true,
                    vaccineFootAndMouth = "۱۴۰۳/۰۷/۱۵",
                    vaccinePox = "۱۴۰۳/۰۶/۲۰",
                    vaccineBrucellosis = "۱۴۰۳/۰۵/۱۰",
                    vaccineAnthrax = "۱۴۰۳/۰۴/۱۸",
                    isOfflineCreated = false,
                    isSynced = true,
                    createdAtJalali = "۱۴۰۳/۰۸/۱۰"
                ),
                LivestockEntity(
                    id = 2,
                    title = "میش جوان افشاری ژن‌دار (چندقلو زا)",
                    livestockType = "EWE",
                    breed = "AFSHARI",
                    purpose = "BREEDING",
                    pricingType = "FIXED_HEAD",
                    weightKg = 72.0,
                    ageMonths = 18,
                    teethStatus = "TWO_TEETH",
                    pregnancyStatus = "PREGNANT_CONFIRMED",
                    rfidCode = "IR-982108743122",
                    vetTrackingCode = "VET-ZN-302115",
                    province = "زنجان",
                    city = "ابهر",
                    distanceKm = 45,
                    basePricePerKg = 345_000L,
                    fixedPrice = 28_500_000L,
                    imageRes = "sample_shal_sheep",
                    isLiveAuction = false,
                    sellerName = "دامداری نمونه البرز",
                    sellerPhone = "۰۹۱۹۸۷۶۵۴۳۲",
                    healthCertificateVerified = true,
                    vaccineFootAndMouth = "۱۴۰۳/۰۷/۰۱",
                    vaccinePox = "۱۴۰۳/۰۶/۱۵",
                    vaccineBrucellosis = "۱۴۰۳/۰۵/۰۲",
                    vaccineAnthrax = "۱۴۰۳/۰۴/۰۵",
                    isOfflineCreated = false,
                    isSynced = true,
                    createdAtJalali = "۱۴۰۳/۰۸/۰۹"
                ),
                LivestockEntity(
                    id = 3,
                    title = "بره نر پرواری نژاد قزل آذربایجان",
                    livestockType = "LAMB",
                    breed = "GHEZEL",
                    purpose = "SACRIFICE",
                    pricingType = "BY_WEIGHT",
                    weightKg = 48.0,
                    ageMonths = 7,
                    teethStatus = "MILK_TEETH",
                    pregnancyStatus = "NOT_APPLICABLE",
                    rfidCode = "IR-982101192834",
                    vetTrackingCode = "VET-TB-904123",
                    province = "آذربایجان شرقی",
                    city = "سراب",
                    distanceKm = 85,
                    basePricePerKg = 330_000L,
                    fixedPrice = 0L,
                    imageRes = "sample_dorper_sheep",
                    isLiveAuction = false,
                    sellerName = "کربلایی یعقوب",
                    sellerPhone = "۰۹۱۴۱۱۱۲۲۳۳",
                    healthCertificateVerified = true,
                    vaccineFootAndMouth = "۱۴۰۳/۰۷/۱۰",
                    vaccinePox = "۱۴۰۳/۰۶/۲۵",
                    vaccineBrucellosis = "۱۴۰۳/۰۵/۲۰",
                    vaccineAnthrax = "۱۴۰۳/۰۴/۳۰",
                    isOfflineCreated = false,
                    isSynced = true,
                    createdAtJalali = "۱۴۰۳/۰۸/۰۸"
                ),
                LivestockEntity(
                    id = 4,
                    title = "بز نر بوئر اصلاح نژادی وارداتی",
                    livestockType = "GOAT",
                    breed = "BOER",
                    purpose = "BREEDING",
                    pricingType = "FIXED_HEAD",
                    weightKg = 64.0,
                    ageMonths = 12,
                    teethStatus = "TWO_TEETH",
                    pregnancyStatus = "NOT_APPLICABLE",
                    rfidCode = "IR-982109923841",
                    vetTrackingCode = "VET-FR-601928",
                    province = "فارس",
                    city = "مرودشت",
                    distanceKm = 110,
                    basePricePerKg = 320_000L,
                    fixedPrice = 32_000_000L,
                    imageRes = "sample_boer_goat",
                    isLiveAuction = true,
                    auctionMinBid = 30_000_000L,
                    auctionCurrentBid = 32_500_000L,
                    auctionEndsInMinutes = 115,
                    auctionBidsCount = 12,
                    sellerName = "مجموعه پرورش دام پارس",
                    sellerPhone = "۰۹۱۷۵۵۵۶۶۷۷",
                    healthCertificateVerified = true,
                    vaccineFootAndMouth = "۱۴۰۳/۰۷/۰۵",
                    vaccinePox = "۱۴۰۳/۰۶/۱۰",
                    vaccineBrucellosis = "۱۴۰۳/۰۵/۱۵",
                    vaccineAnthrax = "۱۴۰۳/۰۴/۱۰",
                    isOfflineCreated = false,
                    isSynced = true,
                    createdAtJalali = "۱۴۰۳/۰۸/۰۷"
                ),
                LivestockEntity(
                    id = 5,
                    title = "بزغاله ماده سانن شیری پربازده",
                    livestockType = "KID",
                    breed = "SAANEN",
                    purpose = "BREEDING",
                    pricingType = "BY_WEIGHT",
                    weightKg = 38.5,
                    ageMonths = 9,
                    teethStatus = "MILK_TEETH",
                    pregnancyStatus = "NON_PREGNANT",
                    rfidCode = "IR-982103498127",
                    vetTrackingCode = "VET-ES-712390",
                    province = "اصفهان",
                    city = "نجف‌آباد",
                    distanceKm = 60,
                    basePricePerKg = 295_000L,
                    fixedPrice = 0L,
                    imageRes = "sample_boer_goat",
                    isLiveAuction = false,
                    sellerName = "مزرعه شیرین‌دام",
                    sellerPhone = "۰۹۱۳۲۲۲۳۳۴۴",
                    healthCertificateVerified = true,
                    vaccineFootAndMouth = "۱۴۰۳/۰۷/۱۲",
                    vaccinePox = "۱۴۰۳/۰۶/۱۸",
                    vaccineBrucellosis = "۱۴۰۳/۰۵/۲۲",
                    vaccineAnthrax = "۱۴۰۳/۰۴/۲۵",
                    isOfflineCreated = false,
                    isSynced = true,
                    createdAtJalali = "۱۴۰۳/۰۸/۰۶"
                )
            )
            db.livestockDao().insertAll(initialLivestock)

            // Seed Media Items for moderation / review
            val initialMedia = listOf(
                MediaItemEntity(
                    id = 1,
                    livestockId = 1,
                    mediaType = "360",
                    title = "ویدیو و رندر ۳۶۰ درجه گام‌برداری قوچ شال",
                    imageResName = "sample_dorper_sheep",
                    isWatermarked = true,
                    status = "APPROVED"
                ),
                MediaItemEntity(
                    id = 2,
                    livestockId = 1,
                    mediaType = "TEETH",
                    title = "تصویر واضح دندان‌ها برای تأیید سن (۲ دندان جوان)",
                    imageResName = "sample_dorper_sheep",
                    isWatermarked = true,
                    status = "APPROVED"
                ),
                MediaItemEntity(
                    id = 3,
                    livestockId = 1,
                    mediaType = "RFID",
                    title = "تصویر پلاک گوش بارکددار سازمان دامپزشکی",
                    imageResName = "sample_dorper_sheep",
                    isWatermarked = true,
                    status = "APPROVED"
                ),
                MediaItemEntity(
                    id = 4,
                    livestockId = 2,
                    mediaType = "FULL",
                    title = "نمای کامل میش افشاری در محیط باز",
                    imageResName = "sample_shal_sheep",
                    isWatermarked = true,
                    status = "PENDING"
                ),
                MediaItemEntity(
                    id = 5,
                    livestockId = 4,
                    mediaType = "FULL",
                    title = "تصویر بز بوئر در جایگاه اختصاصی",
                    imageResName = "sample_boer_goat",
                    isWatermarked = true,
                    status = "APPROVED"
                )
            )
            db.mediaDao().insertAll(initialMedia)

            // Seed Escrow Orders
            val initialEscrow = listOf(
                EscrowOrderEntity(
                    id = 1,
                    orderNumber = "ESC-4038901",
                    livestockId = 1,
                    buyerName = "محمد رضایی (تهران)",
                    sellerName = "حاج اصغر بوئینی",
                    deliveryType = "SLAUGHTER_BUTCHERY",
                    butcheryOption = "CUSTOM_PACKAGED",
                    initialWeightKg = 84.5,
                    originWeighbridgeSlip = "قبض باسکول مبدا بوئین‌زهرا: ۸۴.۵ کیلوگرم",
                    destinationWeightKg = 83.2,
                    destinationWeighbridgeSlip = "قبض باسکول کشتارگاه مقصد تهران: ۸۳.۲ کیلوگرم",
                    distanceKm = 145,
                    permittedLossRate = 2.6,
                    actualLossRate = 1.5,
                    totalAmountToman = 31_170_000L,
                    adjustedAmountToman = 31_170_000L,
                    status = "COMPLETED",
                    dateJalali = "۱۴۰۳/۰۸/۰۹"
                ),
                EscrowOrderEntity(
                    id = 2,
                    orderNumber = "ESC-4038902",
                    livestockId = 3,
                    buyerName = "علیرضا صابری",
                    sellerName = "کربلایی یعقوب",
                    deliveryType = "LIVE_TRANSPORT",
                    butcheryOption = "CARCASS_HALVES",
                    initialWeightKg = 48.0,
                    originWeighbridgeSlip = "قبض باسکول سراب: ۴۸.۰ کیلوگرم",
                    destinationWeightKg = 0.0,
                    destinationWeighbridgeSlip = "در انتظار رسیدن به مقصد",
                    distanceKm = 210,
                    permittedLossRate = 3.2,
                    actualLossRate = 0.0,
                    totalAmountToman = 15_840_000L,
                    adjustedAmountToman = 15_840_000L,
                    status = "LOCKED",
                    dateJalali = "۱۴۰۳/۰۸/۱۰"
                )
            )
            db.escrowDao().insertAll(initialEscrow)
        }
    }
}
