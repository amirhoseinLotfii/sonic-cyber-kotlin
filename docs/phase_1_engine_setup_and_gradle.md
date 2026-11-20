# فاز ۱: راه‌اندازی ساختار اندروید، Gradle و هوک‌های اکتیویتی (Engine Setup & Lifecycle)

## ۱. اهداف فاز ۱
- ایجاد ساختار استاندارد پروژه اندروید با زبان کاتلین (`build.gradle.kts`، `settings.gradle.kts`، `AndroidManifest.xml`).
- راه‌اندازی `MainActivity` با مد Immersive Sticky (حذف نوار وضعیت و کلیدهای ناوبری برای بازی فول‌اسکرین).
- پیاده‌سازی هوک‌های چرخه حیات اندروید (`onPause`، `onResume`، `onDestroy`) برای جلوگیری از مصرف باتری و نشت حافظه.
- ایجاد کلاس پایه `SonicGameView` مبتنی بر `SurfaceView` و رابط `SurfaceHolder.Callback`.

## ۲. استراتژی و فلوچارت فاز ۱

```mermaid
flowchart TD
    AppLaunch[اجرای اپلیکیشن اندروید] --> InitActivity[فراخوانی MainActivity.onCreate]
    InitActivity --> SetFullscreen[فعال‌سازی Immersive Sticky Mode و KEEP_SCREEN_ON]
    SetFullscreen --> CreateView[ایجاد و الصاق SonicGameView به لایه ریشه]
    CreateView --> SurfaceCreated[رویداد surfaceCreated در SurfaceHolder]
    SurfaceCreated --> StartThread[استارت GameThread با اولویت بالا THREAD_PRIORITY_URGENT_DISPLAY]
    StartThread --> Running[حلقه بازی با رفرش‌ریت ۶۰/۱۲۰ هرتز]
    
    Running --> UserLeave[کاربر از برنامه خارج می‌شود یا تماس دریافت می‌شود]
    UserLeave --> OnPause[رویداد onPause]
    OnPause --> StopThread[توقف ایمن GameThread با join]
    StopThread --> ReleaseResources[آزادباش منابع صدا و ذخیره استیت]
```

## ۳. خروجی‌های این فاز
- فایل‌های پیکربندی Gradle.
- فایل مانیفست اندروید با دسترسی‌های لازم و جهت‌گیری افقی اجباری (`android:screenOrientation="landscape"`).
- کلاس‌های `MainActivity.kt` و `SonicGameView.kt`.
