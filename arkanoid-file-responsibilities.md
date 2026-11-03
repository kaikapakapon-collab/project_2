# แต่ละไฟล์ทำหน้าที่อะไร (Arkanoid)

ไฟล์ Java อยู่ใต้ `src/main/java/se233/arkanoid/` (ยกเว้น `module-info.java` ที่อยู่ที่ `src/main/java/`) และเทสต์อยู่ใต้ `src/test/java/se233/arkanoid/`


## จุดเริ่มต้น (`src/main/java/` และ `se233/arkanoid/`)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `Launcher.java` | จุดเริ่มโปรแกรม JavaFX สร้างทุกชิ้น ผูกปุ่ม เปิดหน้าต่าง และสตาร์ท 2 เธรด |
| `JarLauncher.java` | ตัวเริ่มสำหรับไฟล์ .jar แก้ปัญหา "JavaFX runtime components are missing" |
| `module-info.java` | ประกาศโมดูล บอกว่าต้องใช้ไลบรารีอะไรและเปิดแพ็กเกจไหนให้ภายนอก |

## `controller/` (ตัวควบคุมเกม)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `GameLoop.java` | สมองของเกม เธรดที่คำนวณกฎทั้งหมด 60 ครั้ง/วินาที (เคลื่อนที่ ชน คะแนน ชีวิต เปลี่ยนด่าน) |
| `DrawingLoop.java` | เธรดวาด สั่งให้ภาพบนจอตรงกับข้อมูลที่ GameLoop คำนวณไว้ ไม่คิดกฎเกม |
| `GameStatus.java` | enum สถานะเกม: MENU, PLAYING, PAUSED, UPGRADE, GAME_OVER, WIN |

## `model/` (ข้อมูลและของในเกม)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `GameObject.java` | คลาสแม่ของวัตถุทุกชิ้น เก็บตำแหน่ง ขนาด และเช็กการชน |
| `Paddle.java` | แป้นที่ผู้เล่นควบคุม เคลื่อนที่ซ้าย/ขวา มีโหมดเลเซอร์ |
| `Ball.java` | ลูกบอล เคลื่อนที่ เด้งกำแพง/paddle/บล็อก และมีพลังทะลุ |
| `Brick.java` | บล็อก มี HP กระพริบเมื่อโดนตี และเปลี่ยนรูปตามความเสียหาย |
| `BrickType.java` | ชนิดบล็อก (ธรรมดา แข็ง ระเบิด โบนัส ตีไม่แตก ฯลฯ) พร้อม HP และคะแนน |
| `Level.java` | สร้างบล็อกของแต่ละด่านจากผังตัวอักษร (5 ด่าน) |
| `Laser.java` | กระสุนเลเซอร์ที่ยิงขึ้นจาก paddle |
| `PowerUp.java` | ไอเทมที่ตกจากบล็อก (เลเซอร์ / แตกลูกเพิ่ม) |
| `GameState.java` | เก็บคะแนน ชีวิต ด่าน คอมโบ และค่าที่ได้จากอัปเกรด |
| `Keys.java` | จำว่าปุ่มไหนกดอยู่ (เธรดหนึ่งเขียน อีกเธรดอ่าน) |
| `AnimatedSprite.java` | ตัดเฟรมจาก sprite sheet เพื่อเล่นอนิเมชัน |
| `AssetLoader.java` | โหลดรูปจากโฟลเดอร์ assets พร้อมเก็บ cache |

## `model/upgrade/` (ระบบอัปเกรด)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `Upgrade.java` | คลาสแม่แบบ abstract ของอัปเกรด มีชื่อ คำอธิบาย และเมธอด apply |
| `UpgradeManager.java` | เก็บกองอัปเกรดทั้งหมดและสุ่มแจก 3 ใบ |
| `WidePaddleUpgrade.java` | paddle กว้างขึ้น +25 px |
| `FastPaddleUpgrade.java` | paddle เร็วขึ้น |
| `ExtraLifeUpgrade.java` | ชีวิต +1 |
| `MultiBallUpgrade.java` | ปล่อยลูกเพิ่มตอนเริ่ม |
| `PiercingBallUpgrade.java` | ลูกทะลุบล็อก 2 ก้อน |
| `ScoreBoostUpgrade.java` | ตัวคูณคะแนนเพิ่ม |
| `SlowBallUpgrade.java` | ลูกช้าลง 15% |
| `LaserBoostUpgrade.java` | เลเซอร์อยู่นานขึ้น +5 วินาที |

## `view/` (ส่วนแสดงผล)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `GameStage.java` | เวทีเกม เก็บวัตถุทุกอย่าง ลิสต์ลูก/บล็อก/เลเซอร์/ไอเทม และจัดการเพิ่ม-ลบบนจอ |
| `Hud.java` | แถบข้อมูลด้านบน (คะแนน ด่าน ชีวิต คอมโบ เวลาเลเซอร์) |
| `MenuView.java` | หน้าเมนูเริ่มเกม |
| `MessageView.java` | ข้อความกลางจอ เช่น PAUSED, GAME OVER, YOU WIN |
| `UpgradeView.java` | หน้าเลือกการ์ดอัปเกรด รองรับการคลิก |
| `Effect.java` | คลาสแม่ของเอฟเฟกต์ภาพชั่วคราว |
| `ExplosionEffect.java` | เอฟเฟกต์ระเบิดตอนบล็อกแตก |
| `FloatingText.java` | ตัวเลขคะแนนที่ลอยขึ้นแล้วจางหาย |

## `exception/` (จัดการข้อผิดพลาด)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `GameException.java` | exception ของเกมเอง พร้อมชนิดข้อผิดพลาด 5 แบบ |
| `ExceptionHandler.java` | จุดเดียวที่จัดการ exception ทั้งเกม (เขียน log และเด้ง Alert) |

## `src/test/` (ชุดทดสอบ)

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `GameStateTest.java` | ทดสอบคะแนน คอมโบ ชีวิต ความเร็วลูกตามด่าน |
| `UpgradeTest.java` | ทดสอบผลของอัปเกรดทุกแบบและการสุ่มการ์ด |
| `BallTest.java` | ทดสอบการปล่อยลูก การเด้งกำแพง/paddle/บล็อก |
| `BrickAndLevelTest.java` | ทดสอบ HP ของบล็อก และความถูกต้องของผังด่าน |
| `PaddleAndExceptionTest.java` | ทดสอบ paddle, Keys และ GameException |
| `TestSupport.java` | ตัวช่วยเปิดระบบ JavaFX ให้เทสต์ใช้ |

## ไฟล์ประกอบอื่น ๆ

| ไฟล์ | หน้าที่หลัก |
|---|---|
| `pom.xml` | กำหนดการ build (คอมไพล์ ทดสอบ รัน และทำ jar) |
| `log4j2.properties` | ตั้งค่า log ออก console และไฟล์ logs/game.log |
| `assets/*.png` | รูปพื้นหลังและ sprite sheet ของเกม |
| `tools/gen_sprites.py` | สคริปต์ที่ใช้สร้างรูป sprite |
