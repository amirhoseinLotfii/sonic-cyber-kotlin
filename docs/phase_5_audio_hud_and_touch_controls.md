# فاز ۵: موتور صوت SoundPool، لایه ورودی چندلمسی و HUD اندروید (Audio & Touch HUD)

## ۱. اهداف فاز ۵
- پیاده‌سازی `TouchController.kt` با پردازش رویدادهای `MotionEvent` برای مالتی‌تاچ هم‌زمان (نگه داشتن جهت با یک انگشت و پرش با انگشت دیگر).
- بهینه‌سازی تاخیر لمسی (`ACTION_DOWN`، `ACTION_POINTER_DOWN`، `ACTION_UP`).
- طراحی `SonicAudioManager.kt` با استفاده از `SoundPool` نیتیو اندروید و صدای فرکانس بالای رینگ، پرش و اسپین‌دش.
- رندر HUD سایبری (امتیاز، زمان با دقت میلی‌ثانیه، تعداد رینگ و سرعت‌سنج بر حسب کیلومتر بر ساعت).

## ۲. استراتژی و فلوچارت فاز ۵

```mermaid
flowchart TD
    TouchScreen[رویداد لمسی کاربر MotionEvent] --> ParsePointers[تفکیک انگشتان با PointerIndex]
    ParsePointers --> CheckLeftZone{آیا در نیمه چپ صفحه است؟}
    CheckLeftZone -- بله --> UpdateDirection[تنظیم جهت چپ، راست یا نشستن]
    CheckLeftZone -- خیر --> CheckRightZone{آیا در دکمه پرش یا بوست است؟}
    CheckRightZone -- دکمه پرش --> TriggerJump[فراخوانی متد jump کاراکتر و پخش صدای پرش SoundPool]
    CheckRightZone -- دکمه بوست --> TriggerBoost[شارژ اسپین‌دش یا شتاب ناگهانی]
    UpdateDirection --> GameLoop
    TriggerJump --> GameLoop
    TriggerBoost --> GameLoop
```

## ۳. خروجی‌های این فاز
- `TouchController.kt`
- `SonicAudioManager.kt`
- `HudRenderer.kt`
