# Arkanoid: ไฟล์เก็บอะไร และอาจารย์ถามต้องไปไฟล์ไหน

ที่มา: ตาราง "ตรงกับเกณฑ์ตรงไหน" และส่วนเธรดใน `README.md` รวมกับโค้ดจริง

## 1. แต่ละไฟล์เก็บอะไร

| ไฟล์ | เก็บข้อมูลอะไร | เมธอดสำคัญ |
|---|---|---|
| `Launcher.java` | logger, GameStage, GameState, UpgradeManager, GameLoop, DrawingLoop, Scene, Thread 2 ตัว | main, start (ประกอบเกม ผูกปุ่ม เปิดเธรด setDaemon จับ GameException) |
| `JarLauncher.java` | ไม่เก็บข้อมูล | main (เรียก Launcher.main) |
| `module-info.java` | รายการ requires / opens ของโมดูล | - |
| `controller/GameLoop.java` | stage, state, upgradeManager, random, status, laserTicks, running, bestScore, ตัวนับคูลดาวน์เลเซอร์, การ์ดอัปเกรดที่แสดง, requestedUpgrade | tick, updatePlaying, updateBalls, handleBallBrickCollision, onBrickDestroyed, explode, maybeDropPowerUp, loseLife, checkStageClear, handleUpgradeSelection, run, takeRequestedUpgrade |
| `controller/DrawingLoop.java` | stage, loop, state, running, ตัวนับเฟรมวาด | paint (Platform.runLater), run, stop |
| `controller/GameStatus.java` | ค่า enum 6 สถานะ | - |
| `model/GameObject.java` | x, y, w, h | repaint, collidesWith, getter/setter ตำแหน่ง |
| `model/Paddle.java` | ความกว้าง/ความเร็ว, ปุ่มซ้าย-ขวา, สถานะโหมดเลเซอร์, sprite | moveLeft, moveRight, stop, move, checkReachWall, applyStats, resetPosition, updateSprite |
| `model/Ball.java` | speed, xVelocity, yVelocity, launched, pierceLeft, bounceFrames, sprite | launch, setDirection, followPaddle, move, checkReachWall, bounceOffPaddle, bounceOffBrick, isOut, updateSprite |
| `model/Brick.java` | type, hp, destroyed, flashTicks, sprite | hit, destroy, isRequired, frameIndex, repaint |
| `model/BrickType.java` | สัญลักษณ์ในผังด่าน, HP, คะแนน ของแต่ละชนิด | fromSymbol |
| `model/Level.java` | ผังด่านทั้ง 5 (LAYOUTS), จำนวนด่าน, ขนาดช่อง | build (แปลงตัวอักษรเป็นบล็อก, โยน INVALID_LEVEL) |
| `model/Laser.java` | active, sprite, ความเร็วยิง | move, isOut, updateSprite, deactivate |
| `model/PowerUp.java` | type (LASER/MULTI), active, sprite, ความเร็วตก | move, isOut, updateSprite |
| `model/GameState.java` | score, lives, stage, combo, paddleWidth, paddleSpeed, ballSpeedMultiplier, extraBalls, pierce, scoreMultiplier, laserSeconds | reset, addBrickScore, addHitScore, loseLife, getComboMultiplier, nextStage, getBallSpeed, เมธอดเพิ่มค่าจากอัปเกรด |
| `model/Keys.java` | แผนที่ ปุ่ม→กดอยู่หรือไม่ | add, remove, isPressed, consume |
| `model/AnimatedSprite.java` | จำนวนเฟรม, คอลัมน์/แถว, จุดเริ่ม, ขนาดเฟรม, ดัชนีเฟรมปัจจุบัน | tick, changeAnimation, showFrame, interpolate |
| `model/AssetLoader.java` | cache รูป (ชื่อไฟล์→Image) | load (โยน ASSET_NOT_FOUND) |
| `model/upgrade/Upgrade.java` | ชื่อ และคำอธิบายการ์ด | apply (abstract) |
| `model/upgrade/UpgradeManager.java` | ลิสต์อัปเกรด 8 แบบ, Random | randomChoices, select |
| `model/upgrade/*Upgrade.java (8 ไฟล์)` | ชื่อ/คำอธิบายของตัวเอง | apply (แก้ค่าใน GameState คนละแบบ) |
| `view/GameStage.java` | background, ชั้นเล่น, Hud, MenuView, MessageView, UpgradeView, paddle, ลิสต์ balls/bricks/lasers/powerUps/effects, Keys | loadBricks, addObject/removeObject, spawnExplosion, spawnFloatingText, show/hide ต่าง ๆ, updateEffects |
| `view/Hud.java` | Text คะแนน/ด่าน/ชีวิต/คอมโบ/เลเซอร์ | update |
| `view/MenuView.java` | ชื่อเกม, คำอธิบายปุ่ม, ข้อความคะแนนสูงสุด | setBestScore |
| `view/MessageView.java` | ข้อความหัวข้อ และข้อความรอง | show, hide |
| `view/UpgradeView.java` | การ์ดอัปเกรด, callback ตอนคลิก, สไตล์ปกติ/hover | show, createCard, setOnSelect, hide |
| `view/Effect.java` | ไม่เก็บข้อมูล (abstract) | update (abstract) |
| `view/ExplosionEffect.java` | sprite ระเบิด, อายุ | update |
| `view/FloatingText.java` | ข้อความ, อายุ | update |
| `exception/GameException.java` | ชนิดข้อผิดพลาด ErrorType (5 ชนิด) | getType |
| `exception/ExceptionHandler.java` | logger | handle (log ERROR + Alert) |
| `src/test/*` | (ไฟล์ทดสอบ 6 ไฟล์) | GameStateTest, UpgradeTest, BallTest, BrickAndLevelTest, PaddleAndExceptionTest, TestSupport |
| `pom.xml` | dependency (JavaFX, Log4j, JUnit), plugin (compiler, surefire, javafx, shade) | - |

## 2. อาจารย์ถามแล้วต้องไปไฟล์ไหน

| หัวข้อ | คำถามที่อาจโดนถาม | ไปเปิดไฟล์ | เมธอด/จุดที่ต้องชี้ |
|---|---|---|---|
| หลายด่าน / Roguelike | มีกี่ด่าน? ผังด่านอยู่ไหน? | `Level.java` | LAYOUTS, build() |
| Roguelike (อัปเกรด) | สุ่มอัปเกรดยังไง? | `UpgradeManager.java` | randomChoices() |
| Roguelike (อัปเกรด) | เลือกแล้วผลเกิดตรงไหน? | `GameLoop.java + *Upgrade.java` | handleUpgradeSelection(), apply() |
| Sprite sheet | ตัดภาพจาก sprite sheet ยังไง? | `AnimatedSprite.java` | tick(), interpolate(), changeAnimation() |
| Sprite sheet | ท่าเคลื่อนที่/เด้งแยกกันตรงไหน? | `Ball.java` | updateSprite() (กลิ้ง/เด้ง/ลูกไฟ) |
| Sprite sheet | Special attack | `Paddle.java, Laser.java, GameLoop.java` | โหมดเลเซอร์, updateLaser() |
| OOP (สืบทอด) | ใครสืบทอดจากใคร? | `GameObject.java` | Paddle/Ball/Brick/Laser/PowerUp extends GameObject |
| OOP (polymorphism) | polymorphism อยู่ตรงไหน? | `Upgrade.java + *Upgrade.java 8 ไฟล์` | apply() ที่แต่ละคลาสเขียนต่างกัน |
| OOP (abstract) | คลาสที่เป็น abstract | `Effect.java, Upgrade.java, GameObject.java` | extends ExplosionEffect/FloatingText |
| แสดงคะแนน | คะแนนโชว์ตรงไหน? | `Hud.java, FloatingText.java` | Hud.update(), ตัวเลขลอย |
| คะแนน/คอมโบ | คำนวณคะแนนและคอมโบยังไง? | `GameState.java` | addBrickScore(), getComboMultiplier() |
| Animation | อนิเมชันมีอะไรบ้าง? | `ExplosionEffect.java, Brick.java` | ระเบิด, กระพริบขาว, รอยร้าว |
| Exception (custom) | exception ของเรา | `GameException.java` | ErrorType 5 ชนิด |
| Exception (จุดจัดการ) | จัดการ error ที่ไหน? | `ExceptionHandler.java` | handle() (log + Alert) |
| Exception (ห่อ sleep) | จัดการ InterruptedException ยังไง? | `GameLoop.java, DrawingLoop.java` | run() → THREAD_INTERRUPTED |
| Exception (unchecked) | ทำไมเป็น RuntimeException? | `GameException.java` | extends RuntimeException (อธิบายใน README) |
| Logging | ใช้ log ระดับไหน เขียนที่ไหน? | `log4j2.properties, GameLoop.java` | console + logs/game.log |
| Testing | เทสต์อะไรบ้าง? | `src/test/*` | GameStateTest, UpgradeTest, BallTest ฯลฯ |
| Build | รัน/ทำ jar ยังไง? | `pom.xml` | javafx-maven-plugin, maven-shade-plugin |
| Build (JarLauncher) | ทำไมต้องมี JarLauncher? | `JarLauncher.java` | เลี่ยงปัญหา JavaFX runtime components missing |
| Additional feature | ฟีเจอร์เพิ่มเติม | `GameState.java, GameLoop.java` | คอมโบ, explode() ระเบิดลูกโซ่ |
| Thread | เธรดมีกี่ตัว? | `Launcher.java` | GameLoop, DrawingLoop, JavaFX Thread |
| Thread (นาฬิกาเกม) | คุม 60 FPS ยังไง? | `GameLoop.java, DrawingLoop.java` | run() + Thread.sleep |
| Thread (daemon) | ปิดโปรแกรมแล้วเธรดไม่ค้าง | `Launcher.java` | setDaemon(true) |
| Thread (runLater) | แก้ภาพจากเธรดอื่นยังไง? | `GameStage.java, DrawingLoop.java` | Platform.runLater |
| Thread (synchronized) | กันเธรดชนกันตรงไหน? | `Keys, GameState, Brick, Ball, Paddle, AssetLoader, GameLoop` | เมธอด synchronized |
| Thread (อ่านแล้วล้าง) | รับการคลิกอัปเกรดข้ามเธรด | `GameLoop.java` | takeRequestedUpgrade() |
| Thread (ลิสต์ร่วม) | ทำไมไม่เกิด ConcurrentModificationException? | `GameStage.java` | getter คืนสำเนา new ArrayList<>(list) |
| Lambda / Stream | lambda และ stream | `GameLoop.java, Launcher.java` | checkStageClear() (noneMatch), setOnCloseRequest |
| Collection | ArrayList / Random | `UpgradeManager.java, GameStage.java` | randomChoices(), updateEffects() |
| ระเบิดลูกโซ่ | ระเบิดเกิดยังไง? | `GameLoop.java` | onBrickDestroyed() → explode() |
| การชน | เช็กชนยังไง? | `GameObject.java, Ball.java` | collidesWith(), bounceOffBrick() |
| สถานะเกม | เกมมีสถานะอะไรบ้าง? | `GameStatus.java, GameLoop.java` | enum, tick() |
