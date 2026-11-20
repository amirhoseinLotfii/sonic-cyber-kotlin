# فاز ۳: معماری مپ، چانک‌های ۱۲۸×۱۲۸ و استریمینگ نامحدود (Map & Chunks)

## ۱. اهداف فاز ۳
- طراحی فرمت چانک بر اساس معماری سگا (هر چانک ۱۲۸×۱۲۸ پیکسل متشکل از بلوک‌های ۱۶×۱۶).
- ذخیره ارتفاع هر بلوک (`HeightArray`) و زاویه سطح (`angleByte`).
- پیاده‌سازی `ChunkManager.kt` با سیستم بافر حلقوی (Circular Ring Buffer) برای استریم بی‌پایان مسیر.
- تولید خودکار شیب‌ها، ریل‌های هوایی، پرتگاه‌ها و لوپ‌های دایره‌ای.

## ۲. استراتژی و فلوچارت فاز ۳

```mermaid
flowchart TD
    PlayerMove[حرکت بازیکن به سمت راست] --> CheckBoundary{آیا بازیکن به مرز چانک جدید رسیده است؟}
    CheckBoundary -- بله --> RecycleOldChunk[چانک پشت سر بازیکن به Object Pool بازگردانده می‌شود]
    RecycleOldChunk --> ProceduralGen[تولید تصادفی الگو: صاف، شیبدار، پرتگاه یا لوپ ۳۶۰]
    ProceduralGen --> SpawnRingsAndObstacles[چیدمان حلقه‌ها و موانع روی چانک جدید]
    SpawnRingsAndObstacles --> PushToActiveChunks[افزودن چانک جدید به صف فعال]
    CheckBoundary -- خیر --> ContinueStreaming[ادامه رندر چانک‌های فعال فعلی]
```

## ۳. خروجی‌های این فاز
- `SonicChunk.kt`
- `BlockData.kt`
- `ChunkManager.kt`
