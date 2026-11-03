# คำถามที่อาจารย์น่าจะถาม (SE233 Term Project II)

> เรียงตาม rubric ในโจทย์ + เรื่องเธรดที่คอร์สเน้น  
> path ของไฟล์ย่อจาก `src/main/java/se233/arkanoid/`  
> หมายเหตุ: เป็นการคาดเดา ไม่ใช่รายการที่อาจารย์ให้มา

---

## 1. Functionalities

**Q: เกมมีกี่ด่าน แต่ละด่านเก็บไว้ที่ไหน?**
- 5 ด่าน เก็บเป็นผังตัวอักษรใน `model/Level.java` (`LAYOUTS`) ผังละ 10 คอลัมน์
- `Level.build(stage)` แปลงตัวอักษรเป็น `Brick` ผ่าน `BrickType.fromSymbol()`

**Q: ระบบ roguelike อัปเกรดทำงานยังไง?**
- เคลียร์ด่าน → `GameLoop.checkStageClear()` สุ่มการ์ด 3 ใบจาก 8 แบบ (`UpgradeManager.randomChoices()`) แล้วเปลี่ยนสถานะเป็น `UPGRADE`
- ผู้เล่นกด 1/2/3 หรือคลิกการ์ด → `handleUpgradeSelection()` → `Upgrade.apply(state)` แก้ค่าใน `GameState`
- ค่าที่แก้มีผลต่อด่านถัดไปและตลอดรอบเล่น

**Q: สุ่มยังไงไม่ให้การ์ดซ้ำ?**
- `randomChoices` ทำสำเนากอง แล้วสุ่มดึงออกทีละใบ (`list.remove(index)`) จึงไม่ซ้ำ

**Q: เกมมีสถานะอะไรบ้าง?**
- `controller/GameStatus.java`: MENU, PLAYING, PAUSED, UPGRADE, GAME_OVER, WIN (`GameLoop.tick()` แยกการทำงานตามสถานะ)

**Q: ชีวิตเริ่มต้นเท่าไร เสียชีวิตตอนไหน?**
- 3 ชีวิต (`GameState`) ลูกตกใต้จอและไม่มีลูกเหลือ → `GameLoop.loseLife()` → ชีวิตหมดเป็น `GAME_OVER`

**Q: คะแนนคิดยังไง?**
- `GameState.addBrickScore()` = คะแนนฐานของบล็อก × ตัวคูณคะแนน (จากอัปเกรด) × ตัวคูณคอมโบ แล้วปัดเศษ
- ตีโดนแต่ยังไม่แตก ได้ 10 × ตัวคูณคะแนน (`addHitScore`)

---

## 2. Sprite sheet

**Q: ท่าเคลื่อนที่กับท่าเด้งแยกกันตรงไหน?**
- `model/Ball.java` `updateSprite()` เลือกแถวของ sprite sheet: แถว 0 = กลิ้ง, แถว 1 = ท่าเด้ง, แถว 2 = ลูกไฟ (ตอนมีพลังทะลุ)
- ใช้ `AnimatedSprite.changeAnimation()` สลับแถว

**Q: ตัดเฟรมจาก sprite sheet ยังไง?**
- `model/AnimatedSprite.java` สืบทอด `ImageView` ใช้ `viewport` (`Rectangle2D`) ตัดช่องตามคอลัมน์/แถว
- `tick()` เลื่อนเฟรม, `interpolate()` คำนวณตำแหน่งตัดภาพ

**Q: special attack คืออะไร?**
- เลเซอร์คู่ (ยิงจากปืนซ้ายและขวาของ paddle) ได้จากไอเทมที่ตกลงมา
- `GameLoop.updateLaser()` ยิงเมื่อกด SPACE มีคูลดาวน์ 15 เฟรม, `model/Laser.java` เป็นกระสุน
- paddle เปลี่ยนไปใช้ sprite แถว 1 ตอนเป็นโหมดเลเซอร์ (`Paddle.setLaserMode`)

---

## 3. OOP

**Q: ใช้ inheritance ตรงไหน?**
- `GameObject` (abstract) เป็นแม่ของ `Paddle`, `Ball`, `Brick`, `Laser`, `PowerUp`
- `Effect` (abstract) เป็นแม่ของ `ExplosionEffect`, `FloatingText`
- `Upgrade` (abstract) เป็นแม่ของอัปเกรด 8 แบบ

**Q: polymorphism อยู่ตรงไหน?**
- `Upgrade.apply(GameState)` เป็น abstract แต่ละคลาสลูกเขียนต่างกัน `UpgradeManager`/`GameLoop` เรียก `apply` โดยไม่ต้องรู้ว่าเป็นชนิดไหน
- `Effect.update()` ก็เช่นกัน

**Q: encapsulation ใช้ยังไง?**
- ฟิลด์เป็น `private`/`protected` เข้าถึงผ่าน getter/setter เช่น `GameState`
- `GameStage` คืน "สำเนาลิสต์" แทนตัวจริง

**Q: ทำไมต้องมี `GameObject` เป็นแม่?**
- เก็บของที่ทุกวัตถุต้องมีไว้ที่เดียว (ตำแหน่ง ขนาด `repaint()` และ `collidesWith()`) ไม่ต้องเขียนซ้ำ

**Q: เช็กการชนทำยังไง?**
- `GameObject.collidesWith()` เป็น AABB (กล่องสี่เหลี่ยมซ้อนกันทั้งแกน X และ Y)
- ลูกเด้งจากบล็อกใน `Ball.bounceOffBrick()` ดูด้านที่ซ้อนทับน้อยที่สุด

---

## 4. Display score / Animation

**Q: คะแนนแสดงตรงไหน?**
- `view/Hud.java` `update()` แสดงคะแนน ด่าน ชีวิต คอมโบ และเวลาเลเซอร์
- `view/FloatingText.java` แสดงตัวเลข +คะแนน ลอยขึ้นตรงบล็อกที่แตก

**Q: มี visual feedback ตอนบล็อกโดนตีไหม?**
- บล็อกกระพริบขาว (`Brick.hit()` ตั้ง `flashTicks`) และเปลี่ยนรูปตาม HP (รอยร้าว)
- บล็อกแตกมี `ExplosionEffect` (6 เฟรม)

---

## 5. Exception handling

**Q: คุณ throw exception ชนิดไหน และจัดการที่ไหน?**
- ชนิดเดียวคือ `GameException` (custom, `extends RuntimeException`) มี `ErrorType` 5 ชนิด:
  `ASSET_NOT_FOUND`, `INVALID_LEVEL`, `INVALID_UPGRADE`, `INVALID_STATE`, `THREAD_INTERRUPTED`
- จัดการที่เดียวคือ `ExceptionHandler.handle()` (เขียน log ระดับ ERROR + เด้ง `Alert`)

**Q: โยนจากตรงไหนบ้าง?**
- `AssetLoader.load()` → หารูปไม่เจอ
- `Level.build()` → เลขด่านหรือผังผิด
- `UpgradeManager` → ขอ/เลือกอัปเกรดผิด
- `GameState.loseLife()` → ลดชีวิตตอนเป็น 0
- `Thread.sleep` ใน `GameLoop.run()` และ `DrawingLoop.run()` → ห่อ `InterruptedException` เป็น `THREAD_INTERRUPTED`

**Q: ทำไมเป็น unchecked (`RuntimeException`)?**
- ข้อผิดพลาดพวกนี้เป็นบั๊กหรือข้อมูลผิด ผู้เรียกกู้คืนไม่ได้ ไม่ควรบังคับให้ทุกเมธอดเขียน `throws`
- แล้วไปจัดการที่จุดเดียวแทน (อ้างอิงหนังสือหัวข้อ 2.2.3)

**Q: ทำไมต้อง `Platform.runLater` ใน `ExceptionHandler`?**
- exception อาจเกิดในเธรดอื่น แต่การสร้าง `Alert` (UI) ต้องทำบน JavaFX Thread

---

## 6. Logging

**Q: ใช้ log library อะไร ตั้งค่าที่ไหน?**
- Log4j2 ตั้งค่าที่ `log4j2.properties` ออก 2 ที่: console และไฟล์ `logs/game.log`

**Q: ใช้ log ระดับไหน ใช้กับอะไร?**
- DEBUG = รายละเอียดการชน/เด้ง
- INFO = เหตุการณ์และคะแนน (ปล่อยลูก บล็อกแตก เลือกอัปเกรด)
- WARN = เสียชีวิต
- ERROR = exception (ผ่าน `ExceptionHandler`)
- ⚠️ ในโค้ดจริงที่ตรวจ ไม่มีการเรียก `logger.fatal` (README เขียนว่ามี FATAL) ถ้าโดนถามเรื่อง FATAL ให้ตอบตามจริง หรือแก้ README/เพิ่ม log ก่อนเดโม

---

## 7. Testing

**Q: เทสต์อะไรบ้าง?**
- 72 test ใน `src/test/java/se233/arkanoid/` แบ่งเป็น `GameStateTest` (คะแนน คอมโบ ชีวิต), `UpgradeTest` (ผลอัปเกรดและการสุ่ม), `BallTest` (ปล่อยลูก เด้งกำแพง/paddle/บล็อก), `BrickAndLevelTest` (HP บล็อก ผังด่าน), `PaddleAndExceptionTest`
- `TestSupport` เปิดระบบ JavaFX ให้เทสต์ที่ต้องสร้าง Image/Pane

**Q: เทสต์ exception ไหม?**
- มี เช่น ลดชีวิตตอนไม่เหลือต้องโยน `GameException` ชนิด `INVALID_STATE`, ด่านเกินช่วงต้องโยน `INVALID_LEVEL`

**Q: รันเทสต์ยังไง?**
- `mvn test`

---

## 8. Build management

**Q: ทำ jar ยังไง?**
- `mvn clean package` → `target/arkanoid-1.0-SNAPSHOT.jar` แล้วรัน `java -jar ...`

**Q: pom.xml มีอะไรบ้าง?**
- dependency: JavaFX, Log4j2, JUnit 5
- plugin: `maven-compiler-plugin` (Java 21), `maven-surefire-plugin` (รันเทสต์), `javafx-maven-plugin` (`mvn javafx:run`), `maven-shade-plugin` (รวมทุกอย่างเป็น jar เดียว)

**Q: ทำไมต้องมี `JarLauncher`?**
- `Launcher` สืบทอด `Application` ถ้าชี้เป็น main class ของ jar ตรง ๆ จะเจอ "JavaFX runtime components are missing"
- `JarLauncher` เป็นคลาสธรรมดา แค่เรียก `Launcher.main()` ต่อ

**Q: Maven lifecycle ที่ใช้มีอะไร?**
- compile → test → package (ตามหนังสือ บทที่ 6)

---

## 9. Additional feature

**Q: ฟีเจอร์เพิ่มเติมที่ไม่มีในเกมต้นฉบับคืออะไร?**
- ระบบ **คอมโบ**: บล็อกแตกติดต่อกันคะแนนคูณขึ้นสูงสุด x2 (รีเซ็ตเมื่อลูกกลับมาแตะ paddle หรือเสียชีวิต)
- **บล็อกระเบิดลูกโซ่**: แตกแล้วทำลายบล็อกรอบ ๆ (รัศมี 62 px) และถ้าเจอบล็อกระเบิดด้วยกันจะระเบิดต่อเป็นทอด (`GameLoop.explode()`)
- ไอเทมตกจากบล็อก (10% / บล็อกโบนัสตกเสมอ): เลเซอร์หรือแตกลูกเพิ่ม

---

## 10. เธรด (คอร์สเน้นมาก)

**Q: มีกี่เธรด ทำอะไร?**
- 3 เธรด: JavaFX Thread (รับปุ่ม + วาดจริง), `GameLoop` (กฎเกม 60 Hz), `DrawingLoop` (สั่งอัปเดตภาพ 60 Hz)

**Q: คุม 60 FPS ยังไง?**
- `run()` วนลูป จับเวลา แล้ว `Thread.sleep(เวลาต่อเฟรม − เวลาที่ใช้)` (อย่างน้อย 1 ms)

**Q: ทำไมต้องใช้ `Platform.runLater`?**
- แก้ UI ได้เฉพาะ JavaFX Thread ส่วน `GameLoop`/`DrawingLoop` เป็นเธรดอื่น จึงต้องฝากงานแก้ภาพไว้ (`GameStage`, `DrawingLoop`)

**Q: `synchronized` ใช้ทำไม ใช้ที่ไหน?**
- ล็อกให้ทำงานทีละเธรด และให้ค่าที่แก้มองเห็นข้ามเธรด ใช้ใน `Keys`, `GameState`, `Brick`, `Ball`, `Paddle`, `AssetLoader`, `GameLoop`
- ตัวอย่างเด่น: `GameLoop.takeRequestedUpgrade()` อ่านแล้วล้างค่าในเมธอดเดียว (รับเลขการ์ดที่คลิกจาก JavaFX Thread)

**Q: ทำไมไม่เกิด `ConcurrentModificationException`?**
- ลิสต์ใน `GameStage` ที่สองเธรดใช้ร่วมกัน: (1) เมธอดเพิ่ม/ลบเป็น `synchronized` (2) getter คืน **สำเนา** `new ArrayList<>(list)` ให้คนวนลูป

**Q: `setDaemon(true)` ทำไม?**
- ให้เธรดเกมปิดตัวเองตอนปิดโปรแกรม ไม่ค้างหลังปิดหน้าต่าง (`Launcher.start`)

**Q: ถ้าลบ `synchronized` ออกจะเกิดอะไร?**
- ค่าอาจอ่านไม่ตรงกันข้ามเธรด (เช่นคะแนน/ปุ่ม) หรือสองเธรดแก้ค่าพร้อมกันจนผลเพี้ยน

**Q: `Thread` กับ `Runnable` ต่างกันยังไง?**
- `Runnable` คือ "งานที่ต้องทำ" (`run()`) ส่วน `Thread` คือตัวรันงาน เราให้ `GameLoop implements Runnable` แล้วส่งเข้า `new Thread(...)`

---

## 11. ลองแก้สด (เตรียมไว้)

| โจทย์ | ทำที่ไหน |
|---|---|
| เพิ่มอัปเกรดใหม่ | สร้างคลาส extends `Upgrade` ใน `model/upgrade/` (constructor ส่งชื่อ+คำอธิบายให้ `super`, เขียน `apply`) แล้ว `add` ใน constructor ของ `UpgradeManager` (ถ้าต้องมีค่าใหม่ ให้เพิ่มฟิลด์+เมธอดใน `GameState`) |
| เปลี่ยนจำนวนชีวิตเริ่มต้น | ค่าคงที่จำนวนชีวิตเริ่มต้นที่บนสุดของ `GameState.java` |
| เปลี่ยนความเร็ว paddle/ลูก | ค่าเริ่มต้นใน `GameState` (paddle) และสูตร `getBallSpeed()` |
| เพิ่มด่านใหม่ | เพิ่มผังใน `Level.LAYOUTS` และต้องมีรูปพื้นหลัง `bg{เลขด่าน}.png` ใน assets (เพราะ `GameStage.setStageBackground` โหลดตามเลขด่าน) |
| เปลี่ยนโอกาสไอเทมตก | ค่าคงที่โอกาสดรอปใน `GameLoop` (`maybeDropPowerUp`) |
| ปรับรัศมีระเบิด | ค่าคงที่รัศมีระเบิดใน `GameLoop` |

---

## 12. เช็กก่อนเดโม

- [ ] รัน `mvn clean package` สำเร็จจริง และ `java -jar target/arkanoid-1.0-SNAPSHOT.jar` เปิดเกมได้ (ผมยังไม่เคย build จริงในที่นี้)
- [ ] รัน `mvn test` ผ่านครบ
- [ ] เรื่อง FATAL ใน README ให้ตรงกับโค้ด
- [ ] ซ้อมเล่นจนถึงเลือกอัปเกรด เกมโอเวอร์ และชนะ เพื่อโชว์ครบทุก flow
- [ ] เปิด `logs/game.log` ไว้ให้ดูได้ทันที
