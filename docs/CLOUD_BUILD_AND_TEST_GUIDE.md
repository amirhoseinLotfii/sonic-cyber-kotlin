# ☁️ راهنمای جامع بیلد و اجرای ابری (Cloud Build & Cloud Emulator)

این راهنما برای زمانی طراحی شده است که سیستم کاربر توانایی اجرای **Android Studio** یا **شبیه‌ساز سنگین اندروید (AVD)** را ندارد. در این روش، **۱۰۰٪ پروسه کامپایل و اجرای بازی روی سرورهای ابری** انجام می‌شود.

---

## ۱. بیلد خودکار ابری با GitHub Actions (کاملاً رایگان)

ما فایل ورک‌فلو را در پروژه شما آماده کرده‌ایم: [build-apk.yml](file:///c:/Users/amirhosein/Desktop/Apps/android-game-dev/sonic-cyber-kotlin/.github/workflows/build-apk.yml)

### مراحل بیلد در سرورهای ابری گیت‌هاب:

1. پروژه را در یک ریپازیتوری در حساب کاربری خود در **GitHub** پوش (Push) کنید:
   ```bash
   cd c:\Users\amirhosein\Desktop\Apps\android-game-dev\sonic-cyber-kotlin
   git init
   git add .
   git commit -m "Initial Sonic Cyber Kotlin Game"
   git remote add origin https://github.com/YOUR_USERNAME/sonic-cyber-kotlin.git
   git push -u origin main
   ```
2. به تب **Actions** در ریپازیتوری گیت‌هاب خود بروید.
3. سرور ابری گیت‌هاب به‌صورت خودکار:
   - جاوا ۱۷ و اندروید SDK را آماده می‌کند.
   - تست‌های فیزیک (`SonicPhysicsTest`) را اجرا می‌کند.
   - دستور `./gradlew assembleDebug` را اجرا کرده و فایل نهایی `app-debug.apk` را می‌سازد.
4. پس از حدود ۲ دقیقه، فایل `sonic-cyber-overdrive-apk` در بخش **Artifacts** برای دانلود آماده است!

---

## ۲. اجرای شبیه‌ساز اندروید روی وب با Appetize.io (بدون نیاز به نصب)

سرویس [Appetize.io](https://appetize.io) یک شبیه‌ساز واقعی اندروید است که مستقیماً داخل مرورگر شما (کروم/فایرفاکس) اجرا می‌شود و هیچ فشاری به رم یا گرافیک لپ‌تاپ/سیستم شما نمی‌آورد.

### مراحل اجرا:

1. وارد سایت [appetize.io/upload](https://appetize.io/upload) شوید.
2. فایل `app-debug.apk` را که از گیت‌هاب دانلود کرده‌اید، داخل کادر بکشید و رها کنید (Drag & Drop).
3. روی دکمه **Play** کلیک کنید.
4. یک گوشی مجازی اندروید روی مرورگر شما ظاهر می‌شود که می‌توانید بازی را با ماوس تست کنید و صدای آن را بشنوید!

---

## ۳. بهترین و روان‌ترین راه: نصب مستقیم روی گوشی واقعی

اگر گوشی اندروید دارید:

1. فایل `app-debug.apk` را از طریق تلگرام، کابل USB یا Google Drive به گوشی خود انتقال دهید.
2. روی فایل تپ کرده و گزینه **Install** را بزنید.
3. بازی با نهایت قدرت سخت‌افزاری پردازنده گوشی (اسنپدراگون، مدیاتک و...) با نرخ فریم ۶۰ الی ۱۲۰ هرتز واقعی اجرا می‌شود.
