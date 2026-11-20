# فاز ۲: هسته فیزیک مومنتوم سگا و سنسورهای ۳۶۰ درجه (Sonic Physics & Sensors)

## ۱. اهداف فاز ۲
- پیاده‌سازی کلاس `SonicPhysicsEngine.kt` با مقادیر کالیبره‌شده بازی‌های کلاسیک سگا.
- پیاده‌سازی متدهای شتاب، ترمز، اصطکاک و تبدیل سرعت زمینی $G_{spd}$ به سرعت دکارتی $(V_x, V_y)$.
- اعمال معادله ریاضی نیروی شیب ($\text{SlopeForce} = -G \cdot \sin(\theta)$).
- بررسی آستانه لغزش ($|G_{spd}| < 2.5$) برای جدا شدن از دیواره‌ها.
- پیاده‌سازی کلاس `SensorArray.kt` شامل ۴ سنسور اصلی (A، B برای کف؛ C، D برای سقف؛ E، F برای دیواره‌ها).

## ۲. استراتژی و فلوچارت فاز ۲

```mermaid
flowchart TD
    FrameTick[آغاز تیک فیزیک با DT ثابت] --> ReadInput[دریافت وضعیت کنترل لمسی یا کلیدها]
    ReadInput --> CalcGroundSpeed[محاسبه G_spd با اعمال شتاب و ترمز]
    CalcGroundSpeed --> ApplySlopeForce[محاسبه نیروی شیب: SlopeForce = -Gravity * sin(theta)]
    ApplySlopeForce --> CheckSlip{شیب بیش از ۴۵ درجه و سرعت کمتر از ۲.۵؟}
    CheckSlip -- بله --> DetachFromWall[قطع چسبندگی به دیوار و تغییر به حالت هوایی Airborne]
    CheckSlip -- خیر --> CheckSensors[بررسی سنسورهای A و B روی بلوک‌های ۱۶x۱۶]
    CheckSensors --> ProjectToXY[تبدیل G_spd به بردار جابجایی X و Y]
    ProjectToXY --> UpdatePosition[بروزرسانی موقعیت کاراکتر در جهان]
```

## ۳. خروجی‌های این فاز
- `SonicPhysicsEngine.kt`
- `SensorArray.kt`
- داده ساختار `Vector2D.kt` برای عملیات برداری دقیق در کاتلین.
