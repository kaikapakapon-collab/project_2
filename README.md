# SE233 Term Project II — Roguelike Arkanoid

## วิธีรัน
- IntelliJ: Open โฟลเดอร์นี้ → แถบ Maven → Plugins → javafx → `javafx:run`
- Terminal: `mvn clean javafx:run`
- ทดสอบ: `mvn test`
- ทำ jar ที่รันได้: `mvn clean package` → `target/arkanoid-1.0-SNAPSHOT.jar` (ดับเบิลคลิกหรือ `java -jar ...`)

## ปุ่ม
| ปุ่ม | หน้าที่ |
|---|---|
| ENTER | เริ่มเกม / กลับเมนูหลังจบ |
| ← → | ขยับ paddle |
| SPACE | ปล่อยลูก / ยิงเลเซอร์ (ตอนมีไอเทม) |
| P | หยุดชั่วคราว |
| 1 2 3 หรือคลิก | เลือกอัปเกรด |

## ตรงกับเกณฑ์ตรงไหน
| เกณฑ์ | ไฟล์/คลาส |
|---|---|
| Functionalities (หลายด่าน + roguelike) | `Level` (5 ด่าน), `UpgradeManager` (สุ่ม 3 จาก 8), `GameLoop` |
| Sprite sheet (ท่าเคลื่อนที่/เด้งแยกกัน + special attack) | `AnimatedSprite`, `Ball` (กลิ้ง/เด้ง/ลูกไฟ), `Paddle` (ไฟวิ่ง/โหมดเลเซอร์), `Laser` (เลเซอร์คู่) |
| OOP | `GameObject` ← `Paddle/Ball/Brick/Laser/PowerUp`, `Upgrade` ← 8 ลูก (polymorphism), `Effect` ← `ExplosionEffect/FloatingText` |
| Display score | `Hud`, `FloatingText` (+คะแนนลอย) |
| Animation | `ExplosionEffect`, บล็อกกระพริบขาว + รอยร้าว |
| Exception | `GameException` (custom, 5 ชนิดใน `ErrorType`) throw ทุกที่ รวมถึงห่อ `InterruptedException` ของ `Thread.sleep` → ส่งให้ `ExceptionHandler.handle()` ที่เดียว (log ERROR + Alert) |
| Logging | Log4j2: DEBUG/INFO/WARN/ERROR/FATAL → console + `logs/game.log` |
| Testing | `src/test` — GameState, Upgrade, Ball, Brick/Level, Paddle/Keys/Exception |
| Build | `pom.xml` (compile, test, javafx:run, shade → executable jar, `JarLauncher`) |
| Additional feature | ระบบ **คอมโบ** (ทำลายต่อเนื่องคะแนนคูณ สูงสุด x2) + **บล็อกระเบิดลูกโซ่** |

## เธรด (เผื่อโดนถาม) — อ้างอิงหนังสือบทที่ 1–3
3 เธรดขนานกัน: JavaFX Thread (ปุ่ม + วาดจริง), `GameLoop` (กฎเกม 60Hz), `DrawingLoop` (สั่งอัปเดตภาพ 60Hz)
| เทคนิคที่ใช้ | อยู่ที่ | อ้างอิงในหนังสือ |
|---|---|---|
| `Thread` + `Runnable` + `Thread.sleep` เป็นนาฬิกาเกม | `GameLoop`, `DrawingLoop` | Script 4.10, 4.15 |
| `setDaemon(true)` | `Launcher` | Script 2.55, 3.28 |
| `Platform.runLater` (แก้ภาพได้เฉพาะ JavaFX Thread) | `GameStage`, `DrawingLoop` | Script 2.54, 5.22 |
| `synchronized` (ล็อก + ทำให้ค่าที่แก้มองเห็นข้ามเธรด) | `Keys`, `GameState`, `Brick`, `Ball`, `Paddle`, `AssetLoader`, `GameLoop` | หัวข้อ 3.1.2 Synchronization |
| `synchronized` ทำให้ "อ่านแล้วล้างค่า" ในเมธอดเดียว (รับเลขการ์ดอัปเกรดที่คลิกจาก JavaFX Thread) | `GameLoop.takeRequestedUpgrade` | หัวข้อ 3.1.2 |
| `Alert` แจ้ง error | `ExceptionHandler` | Script 2.54, 2.56 |
| try-catch + custom exception | `GameException`, `GameLoop.run`, `DrawingLoop`, `Launcher` | หัวข้อ 2.2, 2.2.3 |
| `ArrayList`, `Random.nextInt`, `list.remove(index)`, ลูป `for` + `get(i)` (สุ่มอัปเกรด, ลบเอฟเฟกต์) | `UpgradeManager.randomChoices`, `GameStage.updateEffects` | บทที่ 1.4, 2.4 |
| lambda + stream (`noneMatch`) | `GameLoop.checkStageClear`, `Launcher` | หัวข้อ 2.3 |

**ลิสต์ที่สองเธรดใช้ร่วมกัน (`GameStage`):** `GameLoop` กับ `DrawingLoop` วนลูปอ่านลิสต์บอล/บล็อก/เลเซอร์/ไอเทมพร้อมกัน
ขณะที่ `GameLoop` เพิ่ม/ลบของ ถ้าวนลิสต์จริงตรง ๆ จะเกิด `ConcurrentModificationException` จึงทำ 2 อย่าง:
(1) เมธอดที่เพิ่ม/ลบ/ล้างลิสต์เป็น `synchronized` (ทีละเธรด — หัวข้อ 3.1.2)
(2) getter คืน **สำเนา** `new ArrayList<>(list)` ให้คนวนลูป (รูปแบบเดียวกับ `new ArrayList<>(Arrays.asList(...))` ใน Script 3.26)
→ คนวนลูปวนบนสำเนา ส่วนลิสต์จริงถูกแก้ได้โดยไม่กระทบ

**ทำไม `GameException` เป็น unchecked (extends `RuntimeException`):** หนังสือหัวข้อ 2.2.3 บอกว่า checked เหมาะกับสิ่งที่ผู้เรียก
กู้คืนได้ ส่วน unchecked เหมาะกับข้อผิดพลาดที่เป็นบั๊กหรือข้อมูลผิดที่ผู้เรียกไม่ควรต้องรับผิดชอบทุกจุด (เช่น หารูปไม่เจอ, เลขด่านผิด, ลดชีวิตตอนเป็น 0)
จึงไม่ต้องเขียน `throws` ทุกเมธอด แล้วไปจัดการที่จุดเดียวคือ `ExceptionHandler.handle()`

## Sprite
สร้างเองด้วย `tools/gen_sprites.py` (ภาพทั้งหมดเป็นงานออริจินัล ไม่ได้ใช้ของเกมต้นฉบับ) ถ้าอยากใช้ sprite จาก spriters-resource ให้แทนไฟล์ใน `src/main/resources/se233/arkanoid/assets/` โดยรักษาขนาดเฟรมเท่าเดิม
