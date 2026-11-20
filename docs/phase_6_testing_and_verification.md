# فاز ۶: تست واحد، ارزیابی پروفایلر و مانیتورینگ کارایی (Testing & Profiling)

## ۱. اهداف فاز ۶
- پیاده‌سازی تست‌های واحد (Unit Tests) با JUnit 4/5 برای توابع محاسباتی فیزیک.
- تست راستی‌آزمایی معادلات شتاب، کاهش سرعت، آستانه لغزش در شیب‌های تند و پرش.
- تست عدم نشت حافظه در چرخه استریم چانک‌ها (Zero Allocation Verification).
- چک‌لیست پایداری ۶۰ فریم بر ثانیه روی دستگاه‌های میان‌رده و پرچم‌دار اندروید.

## ۲. استراتژی و فلوچارت فاز ۶

```mermaid
flowchart TD
    StartTest[شروع اجرای تست‌های خودکار Gradle Test] --> TestPhysics[اجرای SonicPhysicsTest]
    TestPhysics --> VerifyAcceleration[بررسی صحت شتاب گیری در زمین صاف]
    VerifyAcceleration --> VerifySlope[بررسی شتاب کاهشی در سربالایی و افزایشی در سرازیری]
    VerifySlope --> VerifySlipThreshold[بررسی جدا شدن از دیوار در سرعت زیر ۲.۵ روی زاویه بیش از ۴۵ درجه]
    VerifySlipThreshold --> VerifySpinDash[بررسی تزریق سرعت پس از رهاسازی اسپین‌دش]
    VerifySpinDash --> PassAll[تایید صحت ۱۰۰٪ فرمول‌های سگا در کاتلین]
```

## ۳. خروجی‌های این فاز
- فایل تست `SonicPhysicsTest.kt`
- متدهای ارزیابی پروفایلر و گزارش عملکرد
