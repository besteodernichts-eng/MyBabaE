package com.example.model

enum class LivestockType(val titleFa: String) {
    ALL("همه دام‌ها"),
    SHEEP("گوسفند"),
    LAMB("بره"),
    EWE("میش"),
    RAM("قوچ"),
    GOAT("بز"),
    KID("بزغاله")
}

enum class LivestockBreed(val titleFa: String, val defaultType: LivestockType) {
    AFSHARI("افشاری زنجان", LivestockType.SHEEP),
    SHAL("شال قزوین", LivestockType.SHEEP),
    GHEZEL("قزل آذربایجان", LivestockType.SHEEP),
    MEHRABAN("مهربان همدان", LivestockType.SHEEP),
    MOGHANI("مغانی اردبیل", LivestockType.SHEEP),
    SANJABI("سنجابی کرمانشاه", LivestockType.SHEEP),
    KORDI("کردی خراسان", LivestockType.SHEEP),
    ROMANOV("رومانف اصلاح‌شده", LivestockType.SHEEP),
    SAANEN("سانن شیری", LivestockType.GOAT),
    ALPINE("آلپاین فرانسه", LivestockType.GOAT),
    BOER("بوئر گوشتی", LivestockType.GOAT)
}

enum class PurchasePurpose(val titleFa: String) {
    ALL("همه اهداف"),
    FATTENING("پرواری (گوشتی)"),
    BREEDING("داشتی (مولد)"),
    SACRIFICE("قربانی و عقیقه")
}

enum class PricingType(val titleFa: String) {
    BY_WEIGHT("کیلویی (وزن زنده × نرخ روز)"),
    CARCASS("لاشه (وزن گوشت خالص)"),
    FIXED_HEAD("راسی (قیمت مقطوع)")
}

enum class TeethStatus(val titleFa: String) {
    MILK_TEETH("شیرمست (دندان شیری)"),
    TWO_TEETH("دو دندان (جوان)"),
    FOUR_TEETH("چهار دندان (بالغ)"),
    SIX_TEETH("شیشک (شش دندان)"),
    FULL_MOUTH("تمام دندان (سن بالا)")
}

enum class PregnancyStatus(val titleFa: String) {
    NOT_APPLICABLE("نامربوط (نر)"),
    NON_PREGNANT("غیر آبستن"),
    PREGNANT_CONFIRMED("آبستن تایید سونوگرافی"),
    IN_HEAT("فحل / آماده جفت‌گیری")
}

enum class DeliveryType(val titleFa: String) {
    LIVE_TRANSPORT("ارسال دام زنده (خودروی مخصوص)"),
    SLAUGHTER_BUTCHERY("خدمات کشتارگاه و قصابی شرعی")
}

enum class ButcheryOption(val titleFa: String, val additionalCostToman: Long) {
    CARCASS_HALVES("شقه و لاشه کامل", 250_000L),
    FOUR_PIECES("چهار تکه (ران و سردست)", 400_000L),
    CUSTOM_PACKAGED("خردشده خورشتی، چرخ‌کرده + پاک‌کردن کله‌پاچه و سیرابی وکیوم", 750_000L)
}

enum class EscrowStatus(val titleFa: String) {
    LOCKED("وجه در حساب امانی مسدود شد"),
    IN_TRANSIT("در مسیر حمل با بارنامه دامپزشکی"),
    WEIGHBRIDGE_INSPECTION("در حال تطبیق باسکول مبدا و مقصد"),
    COMPLETED("تایید سلامت و آزادسازی وجه به دامدار"),
    DISPUTED("اختلاف وزن بیش از افت مجاز")
}

enum class MediaVerificationStatus(val titleFa: String) {
    PENDING("در انتظار بازبینی"),
    APPROVED("تایید شده"),
    REJECTED("رد شده")
}
