# 🏛️ معماری نرم‌افزار، الگوهای طراحی و رول‌های سیستم (Architecture & Design Patterns)

این سند ساختار معماری، نقش‌ها (Roles)، الگوهای طراحی (Design Patterns) و لایه‌های فنی موتور بازی **Sonic Cyber Overdrive** در کاتلین را تشریح می‌کند.

---

## ۱. لایه‌بندی معماری (Architectural Layers)

```
┌───────────────────────────────────────────────────────────┐
│                    Presentation Layer                     │
│  MainActivity  •  SonicGameView  •  TouchInputController │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                       Engine Layer                        │
│     GameEngine  •  GameThread (Fixed Timestep) • Audio     │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                     Game Logic Layer                      │
│ SonicCharacter (FSM) • SonicPhysicsEngine • ChunkManager   │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                    Data & Geometry Layer                  │
│   SonicChunk  •  BlockData (16x16) • HeightArray • Pool    │
└───────────────────────────────────────────────────────────┘
```

---

## ۲. رول‌ها و مسئولیت‌های هر کامپوننت (Component Roles)

| کامپوننت | رول (نقش) | مسئولیت‌ها |
| :--- | :--- | :--- |
| **`MainActivity`** | سیستم هماهنگ‌کننده اندروید | اتصال به چرخه حیات، فعال‌سازی حالت تمام‌صفحه (Immersive Sticky Mode) و نگه‌داشتن صفحه روشن (`KEEP_SCREEN_ON`). |
| **`SonicGameView`** | رابط نمایش سخت‌افزاری | پیاده‌سازی `SurfaceHolder.Callback`، دریافت رویدادهای لمسی چندانگشتی (`MotionEvent`) و ارسال آن به کنترلر ورودی. |
| **`GameThread`** | زمان‌بندی و لوپ اصلی | اجرای حلقه Fixed Timestep ($60 \text{ Hz}$)، جلوگیری از درهم‌تنیدگی فیزیک و رندر و همگام‌سازی با نمایشگر. |
| **`SonicPhysicsEngine`** | پردازشگر قوانین فیزیک سگا | محاسبه $G_{spd}$، تبدیل سرعت زمینی به مختصات دکارتی ($X, Y$)، نیروی گرانش شیب و بررسی آستانه لغزش. |
| **`SensorArray`** | رادار برخوردهای محیطی | محاسبه ۴ جفت سنسور (کف، سقف، دیواره چپ و دیواره راست) و خواندن داده‌های ارتفاع بلوک‌های ۱۶×۱۶. |
| **`SonicCharacter`** | مدیریت وضعیت و انیمیشن | پیاده‌سازی ماشین وضعیت محدود (FSM) برای تغییر حالت بین دویدن، غلتیدن، پرش، اسپین‌دش و آسیب. |
| **`ChunkManager`** | تولید و استریم نقشه | استریم بی‌نهایت چانک‌های ۱۲۸×۱۲۸، بازیافت بلوک‌ها بدون ایجاد زباله حافظه (Object Pool). |
| **`SonicAudioManager`** | موتور صوت کم‌تاخیر | استفاده از `SoundPool` اختصاصی اندروید با استریم فرکانس بالا برای افکت رینگ، پرش، اسپین‌دش و موانع. |

---

## ۳. الگوهای طراحی نرم‌افزاری (Design Patterns)

1. **الگوی ماشین وضعیت (State Pattern):**
   - کلاس `SonicCharacter` رفتار خود را بر اساس اینترفیس `PlayerState` تغییر می‌دهد.
   - حالات `IdleState`، `RunningState`، `RollingState` و `SpinDashState` هر کدام منطق ورودی و بردار نیروی اختصاصی دارند.

2. **الگوی مخزن اشیاء (Object Pool Pattern):**
   - برای ذرات نورانی نئونی (Neon Particles) و حلقه‌های پرتاب‌شده هنگام برخورد، کلاس‌های `RingPool` و `ParticlePool` پیاده‌سازی می‌شوند تا هیچ شیء جدیدی حین گیم‌پلی تولید نشود.

3. **الگوی مگس‌وزن (Flyweight Pattern):**
   - بلوک‌های ۱۶×۱۶ در چانک‌ها داده‌های ثابت ارتفاع (Height Arrays) و زوایای پیش‌فرض را به اشتراک می‌گذارند و فقط موقعیت مکانی هر چانک ذخیره می‌شود.

4. **الگوی حلقه بازی (Game Loop Pattern):**
   - پیاده‌سازی الگوریتم *Fix Your Timestep* با انباشت‌گر زمان (Accumulator) برای جلوگیری از تفاوت سرعت فیزیک در گوشی‌های با رفرش‌ریت متفاوت (۶۰، ۹۰، ۱۲۰ هرتز).

---

## ۴. پیکربندی کاتلین و وابستگی‌های Gradle

```kotlin
// build.gradle.kts (Module: app)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.cyber.sonic.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cyber.sonic.game"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
}
```
