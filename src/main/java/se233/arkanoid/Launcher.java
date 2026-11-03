// ประกาศแพ็กเกจของคลาสนี้
package se233.arkanoid;

// นำเข้า Application คลาสแม่ของโปรแกรม JavaFX
import javafx.application.Application;
// นำเข้า Platform ใช้สั่งปิดโปรแกรม (Platform.exit)
import javafx.application.Platform;
// นำเข้า Scene ฉากที่ใส่เนื้อหาในหน้าต่าง
import javafx.scene.Scene;
// นำเข้า Stage หน้าต่างหลักของโปรแกรม
import javafx.stage.Stage;
// นำเข้า LogManager สำหรับสร้าง logger
import org.apache.logging.log4j.LogManager;
// นำเข้า Logger สำหรับเขียน log
import org.apache.logging.log4j.Logger;
// นำเข้า DrawingLoop เธรดวาดภาพ
import se233.arkanoid.controller.DrawingLoop;
// นำเข้า GameLoop เธรดคำนวณกฎเกม
import se233.arkanoid.controller.GameLoop;
// นำเข้า ExceptionHandler จุดจัดการ exception ที่เดียว
import se233.arkanoid.exception.ExceptionHandler;
// นำเข้า GameException exception ของเกม
import se233.arkanoid.exception.GameException;
// นำเข้า GameState เก็บคะแนน/ชีวิต/ด่าน
import se233.arkanoid.model.GameState;
// นำเข้า UpgradeManager ตัวสุ่มและมอบอัปเกรด
import se233.arkanoid.model.upgrade.UpgradeManager;
// นำเข้า GameStage เวที (หน้าจอเกม)
import se233.arkanoid.view.GameStage;

// นำเข้า Random สำหรับสุ่มเลข
import java.util.Random;

/*
 * [แผนที่หัวข้อตาราง 4.1 (กลุ่ม 2 คน) → อยู่ไฟล์ไหน]
 *   Functionalities   : Level (5 ด่าน), UpgradeManager + upgrade/* (roguelike), GameLoop (กฎเกม), GameState (คะแนน/ชีวิต)
 *   Sprite sheet      : AnimatedSprite + Ball (กลิ้ง/เด้ง), Paddle (ไฟวิ่ง/โหมดเลเซอร์), Laser (เลเซอร์คู่), assets/*.png
 *   OOP               : GameObject ← Paddle/Ball/Brick/Laser/PowerUp,  Upgrade ← อัปเกรด 8 แบบ,  Effect ← ExplosionEffect/FloatingText
 *   Display score     : Hud (แถบคะแนน), FloatingText (+คะแนนลอย), GameState.addBrickScore
 *   Additional anim.  : ExplosionEffect, Brick (กระพริบขาว + รอยร้าว)
 *   Exception handling: GameException (ชนิดเดียว) → ExceptionHandler.handle() (จุดเดียว)
 *   Logging           : log4j2.properties + logger.* ใน GameLoop/Launcher (DEBUG/INFO/WARN/ERROR → console + logs/game.log)
 *   Testing           : src/test/java (72 test)
 *   Build management  : pom.xml (compile/test/javafx:run/shade jar) + JarLauncher
 *   Additional feature: ระบบคอมโบ (GameState) + บล็อกระเบิดลูกโซ่ (GameLoop.explode)
 *
 * [เกณฑ์ของไฟล์นี้] Build management (จุดเริ่ม + ประกอบทุกส่วน), Exception handling (จับ GameException ตอนเริ่มเกม), Logging
 * Launcher = จุดเริ่มโปรแกรม JavaFX อ้างอิง Script 4.18 / 4.21
 * ประกอบทุกชิ้นเข้าด้วยกัน: สร้าง model/view/controller → ผูกปุ่ม → เปิดหน้าต่าง → สตาร์ท 2 เธรด
 */
// คลาส Launcher สืบทอดจาก Application จึงเป็นโปรแกรม JavaFX
public class Launcher extends Application {
    // สร้าง logger ประจำคลาสนี้ ใช้เขียน log ผ่านตัวแปร logger
    private static final Logger logger = LogManager.getLogger(Launcher.class);

    // main → launch() → JavaFX สร้าง JavaFX Application Thread แล้วเรียก start() บนเธรดนั้น
    // main เป็นจุดเริ่มโปรแกรมแบบปกติ
    public static void main(String[] args) {
        // launch(args) ให้ JavaFX เตรียมระบบแล้วเรียก start() บน JavaFX Thread
        launch(args);
    }  // จบเมธอด main

    // [เธรด] start() รันบน JavaFX Application Thread
    // @Override บอกว่าเป็นการเขียนทับเมธอด start ของ Application
    @Override
    // start รับ Stage หลักที่ JavaFX สร้างให้
    public void start(Stage stage) {
        // เริ่ม try เพื่อดัก GameException ตอนประกอบเกม
        try {
            // สร้างเวทีเกม (โหลดรูป, วาง paddle/บล็อก/HUD/เมนู)
            GameStage gameStage = new GameStage();
            // สร้าง GameState เก็บข้อมูลคะแนน ชีวิต ด่าน
            GameState gameState = new GameState();
            // สร้าง UpgradeManager พร้อม Random ไว้สุ่มการ์ดอัปเกรด
            UpgradeManager upgradeManager = new UpgradeManager(new Random());
            // สร้าง GameLoop (ลูปกฎเกม) โดยส่งของที่ต้องใช้เข้าไปทั้งหมด
            GameLoop gameLoop = new GameLoop(gameStage, gameState, upgradeManager, new Random());
            // สร้าง DrawingLoop (ลูปวาด) ที่อ่านจาก gameStage, gameLoop, gameState
            DrawingLoop drawingLoop = new DrawingLoop(gameStage, gameLoop, gameState);

            // คลิกการ์ดอัปเกรด → ส่งเลขการ์ดให้ GameLoop (method reference = เขียนย่อของ i -> gameLoop.requestUpgrade(i))
            // เมื่อคลิกการ์ดอัปเกรด ให้เรียก gameLoop.requestUpgrade(เลขการ์ด)
            gameStage.getUpgradeView().setOnSelect(gameLoop::requestUpgrade);

            // สร้าง Scene จากเวที ขนาดเท่าความกว้าง/สูงของเกม
            Scene scene = new Scene(gameStage, GameStage.WIDTH, GameStage.HEIGHT);
            // ผูกปุ่ม (Script 4.21): กด → จำใน Keys, ปล่อย → ลบ
            // lambda พวกนี้ JavaFX Thread เรียก ส่วน GameLoop Thread เป็นคนอ่าน Keys
            // เมื่อกดปุ่ม บันทึกรหัสปุ่มลง Keys (true = กดอยู่)
            scene.setOnKeyPressed(event -> gameStage.getKeys().add(event.getCode()));
            // เมื่อปล่อยปุ่ม ลบ/ตั้งเป็นปล่อยแล้วใน Keys
            scene.setOnKeyReleased(event -> gameStage.getKeys().remove(event.getCode()));

            // ตั้งชื่อหัวหน้าต่าง
            stage.setTitle("SE233 Roguelike Arkanoid");
            // ใส่ Scene เข้าหน้าต่าง
            stage.setScene(scene);
            // ห้ามผู้เล่นย่อ/ขยายหน้าต่าง
            stage.setResizable(false);
            // กำหนดสิ่งที่ต้องทำเมื่อกดปิดหน้าต่าง
            stage.setOnCloseRequest(e -> {
                // สั่งให้ GameLoop หยุดวนลูป
                gameLoop.stop();
                // สั่งให้ DrawingLoop หยุดวนลูป
                drawingLoop.stop();
                // บันทึก log ว่าปิดหน้าต่างและหยุดเกมแล้ว
                logger.info("Window closed, game stopped");
            });  // จบ lambda ของ setOnCloseRequest
            // แสดงหน้าต่างขึ้นจอ
            stage.show();

            // สร้าง Thread จาก Runnable แล้ว start() → run() ของแต่ละลูปทำงานขนานกันทันที
            // (ห้ามเรียก run() ตรง ๆ เพราะจะรันบน JavaFX Thread เอง แล้วจอค้าง)
            // สร้างเธรดใหม่จาก gameLoop (Runnable) พร้อมตั้งชื่อ "GameLoop"
            Thread gameThread = new Thread(gameLoop, "GameLoop");       // ตั้งชื่อ → เห็นใน log ช่อง [%t]
            // สร้างเธรดวาดภาพ ชื่อ "DrawingLoop"
            Thread drawThread = new Thread(drawingLoop, "DrawingLoop");
            // daemon = เธรดเบื้องหลัง ปิดหน้าต่างแล้วโปรแกรมจบได้ ไม่ค้าง
            // ตั้งให้เป็น daemon thread (ปิดโปรแกรมแล้วเธรดไม่ค้าง)
            gameThread.setDaemon(true);
            // ตั้งเธรดวาดเป็น daemon ด้วย
            drawThread.setDaemon(true);
            // เริ่มเธรดกฎเกม (จะเรียก run() ของ GameLoop ขนานกัน)
            gameThread.start();
            // เริ่มเธรดวาดภาพ
            drawThread.start();

            // บันทึก log ว่าเริ่มเกมแล้ว พร้อมขนาดหน้าจอ ({} คือช่องแทนค่า)
            logger.info("Game started ({}x{})", GameStage.WIDTH, GameStage.HEIGHT);
        // ถ้าเกิด GameException ตอนเริ่มเกม ให้เข้า catch นี้
        } catch (GameException e) {
            // เริ่มเกมไม่ได้ (เช่น หารูปไม่เจอ) → จัดการที่จุดเดียวเหมือนกัน
            // ส่ง exception ให้ ExceptionHandler (log + เด้ง Alert)
            ExceptionHandler.handle(e);
            // ปิดโปรแกรม JavaFX เพราะเริ่มเกมไม่ได้
            Platform.exit();
        }  // จบ catch
    }  // จบเมธอด start
}  // จบคลาส Launcher
