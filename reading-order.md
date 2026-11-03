# ลำดับการอ่านโค้ด Arkanoid

เส้นทางอ่านแนะนำ: จากภาพใหญ่ → ของในเกม → สมองของเกม → ส่วนแสดงผล → เทสต์
(path ทั้งหมดอยู่ใต้ `src/main/java/se233/arkanoid/` ยกเว้นระบุไว้)

## ขั้นที่ 0: ตั้งหลัก
1. `README.md` (อ่านตารางปุ่ม และตาราง "ตรงกับเกณฑ์ตรงไหน")
2. ลองรันเกม 1 รอบ (`mvn clean javafx:run`)

## ขั้นที่ 1: จุดเริ่มและสถานะเกม
3. `Launcher.java` (จุดเริ่ม ประกอบทุกชิ้น เปิด 2 เธรด)
4. `controller/GameStatus.java` (สถานะ: MENU / PLAYING / PAUSED / UPGRADE / GAME_OVER / WIN)

## ขั้นที่ 2: ของในเกม (โฟลเดอร์ `model/`)
5. `model/GameObject.java` (คลาสแม่: ตำแหน่ง ขนาด เช็กการชน)
6. `model/Paddle.java`
7. `model/Ball.java` (การเด้ง มุม ลูกไฟ)
8. `model/BrickType.java`
9. `model/Brick.java` (HP กระพริบ รอยร้าว)
10. `model/Level.java` (ผังด่านจากตัวอักษร)
11. `model/GameState.java` (คะแนน คอมโบ ชีวิต ตัวคูณจากอัปเกรด)
12. `model/Laser.java` และ `model/PowerUp.java` (สั้น อ่านเร็ว)

## ขั้นที่ 3: สมองของเกม
13. `controller/GameLoop.java` อ่านตามลำดับการทำงาน:
    1. `tick()`
    2. `updatePlaying()`
    3. `updatePaddle()` และ `updateLaser()`
    4. `updateBalls()`
    5. `handleBallBrickCollision()` → `damageBrick()` → `onBrickDestroyed()`
    6. `explode()` และ `maybeDropPowerUp()`
    7. `updateLasers()` และ `updatePowerUps()`
    8. `loseLife()` และ `checkStageClear()`
    9. `handleUpgradeSelection()`
    10. `run()` (ลูป 60 Hz)

## ขั้นที่ 4: ส่วนแสดงผลและเธรดวาด
14. `view/GameStage.java` (เวที เก็บลิสต์ทุกอย่าง)
15. `controller/DrawingLoop.java` (เธรดวาด `Platform.runLater`)
16. `model/AnimatedSprite.java`, `model/AssetLoader.java`, `model/Keys.java`
17. `view/Hud.java`, `view/MenuView.java`, `view/MessageView.java`, `view/UpgradeView.java`
18. `view/Effect.java` → `view/ExplosionEffect.java`, `view/FloatingText.java`

## ขั้นที่ 5: ระบบอัปเกรดและ exception
19. `model/upgrade/Upgrade.java` → `UpgradeManager.java`
20. อัปเกรดแบบต่าง ๆ อ่านแค่ 1–2 ตัว เช่น `WidePaddleUpgrade.java`, `PiercingBallUpgrade.java` (ที่เหลือหน้าตาเหมือนกัน)
21. `exception/GameException.java` → `exception/ExceptionHandler.java`

## ขั้นที่ 6: เทสต์ (ใช้ยืนยันความเข้าใจ)
(path: `src/test/java/se233/arkanoid/`)
22. `GameStateTest.java`
23. `BallTest.java`
24. `BrickAndLevelTest.java`
25. `UpgradeTest.java`
26. `PaddleAndExceptionTest.java` (`TestSupport.java` เป็นตัวช่วย ข้ามได้)

## ขั้นที่ 7: ไฟล์ประกอบ
27. `JarLauncher.java`, `module-info.java` และ `pom.xml` (เรื่อง build)

---
**เคล็ดลับ:** ถ้างงเรื่อง `synchronized` หรือเธรดใน `GameLoop` ให้ข้ามไปก่อน อ่านกฎเกมให้จบแล้วค่อยย้อนมาดู (README มีตารางสรุปเรื่องเธรด)
