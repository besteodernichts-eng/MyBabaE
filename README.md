# ببعی من (BabeiMan) 🐑✨
### پلتفرم و اپلیکیشن جامع معاملات هوشمند دام سبک و پنل مدیریت پیشرفته

![Version](https://img.shields.io/badge/version-v1.0.0-emerald)
![Platform](https://img.shields.io/badge/platform-Android%20%7C%20Jetpack%20Compose-blue)
![Language](https://img.shields.io/badge/language-Kotlin-purple)
![License](https://img.shields.io/badge/license-MIT-green)

اپلیکیشن **«ببعی من»** یک اکوسیستم کامل برای خرید و فروش دام سبک (گوسفند، بره، میش، قوچ، بز و بزغاله) با تمرکز ویژه بر شفافیت قیمت، پلاک ملی الکترونیکی (RFID)، گواهی‌های سلامت دامپزشکی، تضمین پرداخت امانی (Escrow) و پنل مدیریت متمرکز نرخ روز است.

---

## 📱 دریافت و نصب مستقیم فایل APK

فایل نصبی آماده اپلیکیشن در مسیر زیر قرار دارد:
```bash
release-apk/BabeiMan-v1.0.0.apk
```
شما می‌توانید فایل APK بالا را مستقیماً روی گوشی اندروید یا شبیه‌ساز نصب نمایید.

---

## 🏷️ برچسب‌های نسخه‌بندی (Git Versioning & Rollback)

این پروژه با استاندارد Semantic Versioning تگ‌گذاری شده است:
- **`v1.0.0`**: نسخه پایه پایدار شامل اپلیکیشن خریدار/دامدار، شناسنامه دیجیتال، ویوور ۳۶۰ درجه، پنل ادمین و دیتابیس Room.

### نحوه بازگشت به نسخه قبل (Rollback):
برای سوئیچ یا بازگشت به هر نسخه دلخواه، دستورات زیر را اجرا کنید:
```bash
# مشاهده تمام تگ‌ها و نسخه‌ها
git tag -l

# بازگشت به نسخه 1.0.0
git checkout v1.0.0

# یا ساخت یک شاخه جدید از روی نسخه قبلی
git checkout -b rollback-branch v1.0.0
```

---

## 🚀 ویژگی‌های کلیدی

### ۱. بخش کاربر و دامدار (Mobile App):
- 🐑 **فیلتر و دسته‌بندی تخصصی:** نژادهای ایرانی (شال، افشاری، قزل، بوئر، سانن...)، سن و وضعیت دندان، بازه وزن و هدف خرید.
- 🔄 **نمایشگر تعاملی ۳۶۰ درجه:** چرخش ۳۶۰ درجه با حرکت انگشت، کنترل سرعت و زوایای دید.
- 🏷️ **پلاک ملی ۱۵ رقمی (RFID):** احراز هویت یکتای هر رأس دام و کد رهگیری دامپزشکی.
- 📈 **ماشین‌حساب هوشمند پیش‌بینی رشد (Dynamic Weight Predictor):** محاسبه وزن زنده بر اساس سن و رژیم غذایی.
- ⚖️ **محاسبه‌گر افت وزن حمل‌ونقل:** فرمول دقیق درصد افت مجاز بین باسکول مبدا و مقصد.
- 🥩 **خدمات ارزش افزوده قصابی:** انتخاب ارسال دام زنده یا ذبح شرعی، خرد کردن سفارشی و بسته‌بندی وکیوم.
- 🔒 **حساب امانی (Escrow):** مسدودسازی وجه تا زمان تحویل و تأیید باسکول مقصد.
- 📡 **حالت ثبت آفلاین روستایی:** ثبت آگهی بدون اینترنت و همگام‌سازی خودکار پس از اتصال.

### ۲. پنل مدیریت پیشرفته (Admin Panel):
- 💲 **Dynamic Live Weight Pricing:** اسلایدرهای تغییر آنی نرخ هر کیلوگرم گوسفند و بز همراه با دکمه اعمال سراسری (`Update Prices Live`).
- 📁 **Gallery & Media Uploader:** مدیریت عکس‌ها و ویدیوها با درج خودکار واترمارک اصالت «ببعی من».
- 🩺 **Veterinary Certificate Status:** جدول استعلام وضعیت گواهی‌های سلامت (تأییدشده، در انتظار، نیازمند بررسی).
- 📊 **Real-time Sales Analytics:** نمودار وکتوری روان (Bezier Curves) حجم معاملات ماهانه، درآمد کارمزدها و تعداد رأس معامله‌شده.
- 🌗 **سوئیچ تم دوگانه:** جابجایی آنی بین حالت روز (کنتراست بالا برای محیط دامداری) و حالت شب.

---

## 🛠️ ساختار فنی (Architecture)

```
app/src/main/java/com/example/
├── data/
│   ├── database/     # Room Database & DAOs
│   ├── entities/     # جدول‌های SQLite (Livestock, DailyPriceRule, Escrow, Media)
│   └── repository/   # لایه Repository و همگام‌سازی داده‌ها
├── engine/
│   └── PricingEngine.kt  # فرمول‌های قیمت‌گذاری، افت وزن و محاسبات امانی
├── model/
│   └── LivestockModels.kt # انام‌ها و مدل‌های پایه دام سبک
├── ui/
│   ├── components/   # کامپوننت‌های ماژولار (360Viewer, WeightPredictor, Cards, Badges)
│   ├── screens/      # صفحات اصلی (Marketplace, Detail, NewListing, AdminScreen)
│   ├── theme/        # تم، رنگ‌های یشمی و گندمی، و فونت وزیرمتن
│   └── viewmodel/    # مدیریت State با StateFlow
└── MainActivity.kt
```

---

## 📦 نحوه اتصال و ارسال به گیت‌هاب (Push to GitHub)

برای ارسال این پروژه به ریپازیتوری گیت‌هاب خود در `https://github.com/besteodernichts-eng/babei-man`:

```bash
# ۱. ساخت ریپازیتوری جدید در گیت‌هاب با نام babei-man

# ۲. اتصال و ارسال سورس‌کد و تگ‌ها
git remote add origin https://github.com/besteodernichts-eng/babei-man.git
git branch -M main
git push -u origin main --tags
```
