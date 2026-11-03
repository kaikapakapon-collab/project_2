// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Platform สำหรับ runLater
import javafx.application.Platform;
// นำเข้า Image
import javafx.scene.image.Image;
// นำเข้า ImageView ใช้แสดงภาพพื้นหลัง
import javafx.scene.image.ImageView;
// นำเข้า KeyCode
import javafx.scene.input.KeyCode;
// นำเข้า Pane
import javafx.scene.layout.Pane;
// นำเข้า Color
import javafx.scene.paint.Color;
// นำเข้า AssetLoader
import se233.arkanoid.model.AssetLoader;
// นำเข้า Ball
import se233.arkanoid.model.Ball;
// นำเข้า Brick
import se233.arkanoid.model.Brick;
// นำเข้า GameObject
import se233.arkanoid.model.GameObject;
// นำเข้า Keys
import se233.arkanoid.model.Keys;
// นำเข้า Laser
import se233.arkanoid.model.Laser;
// นำเข้า Paddle
import se233.arkanoid.model.Paddle;
// นำเข้า PowerUp
import se233.arkanoid.model.PowerUp;
// นำเข้า Upgrade
import se233.arkanoid.model.upgrade.Upgrade;

// นำเข้า ArrayList
import java.util.ArrayList;
// นำเข้า List
import java.util.List;

/*
 * [เกณฑ์ตาราง] Functionalities (เวทีที่รวมทุกอย่างบนจอ), OOP (View ใน MVC)
 * GameStage = "เวที" ของเกม (View ใน MVC) ต่อยอดจาก Script 4.13 / 4.20
 *
 * ชั้นของภาพ (ใส่ก่อน = อยู่ล่าง):
 *   background → playLayer (บล็อก, paddle, ลูก, เลเซอร์, ไอเทม, เอฟเฟกต์) → hud → message → upgrade → menu
 *
 * [เธรด] กฎสำคัญของคลาสนี้:
 * GameLoop (คนละเธรดกับ JavaFX) อยากเพิ่ม/ลบของบนจอ เช่น เพิ่มลูก ยิงเลเซอร์ เปลี่ยนด่าน
 * แต่การแก้ scene graph ที่แสดงอยู่ต้องทำบน JavaFX Thread เท่านั้น
 * → ทุกเมธอดที่ GameLoop เรียก จะ "แก้ลิสต์ข้อมูลทันที" แล้ว "ฝากแก้หน้าจอ" ผ่าน Platform.runLater
 * → รวม runLater ไว้ที่คลาสนี้ที่เดียว GameLoop เลยไม่ต้องสนใจเรื่องเธรดของ JavaFX
 */
// GameStage คือเวทีเกม (หน้าจอทั้งหมด) สืบทอดจาก Pane และเก็บวัตถุทุกอย่าง
public class GameStage extends Pane {
    // ความกว้างจอ 600 px
    public static final int WIDTH = 600;
    // ความสูงจอ 700 px
    public static final int HEIGHT = 700;
    // ความสูงแถบ HUD (ลูกเด้งที่เส้นนี้)
    public static final int TOP = 40; // ความสูงแถบ HUD ลูกเด้งที่เส้นนี้

    // ภาพพื้นหลัง
    private final ImageView background;
    // ชั้นที่ใส่วัตถุเล่นเกม (paddle บล็อก ลูก ฯลฯ)
    private final Pane playLayer = new Pane();
    // แถบ HUD
    private final Hud hud = new Hud();
    // หน้าเมนู
    private final MenuView menuView = new MenuView();
    // ข้อความกลางจอ (PAUSED/GAME OVER/WIN)
    private final MessageView messageView = new MessageView();
    // หน้าเลือกอัปเกรด
    private final UpgradeView upgradeView = new UpgradeView();

    // paddle ของผู้เล่น
    private final Paddle paddle;
    // [เธรด] ลิสต์เหล่านี้ GameLoop กับ DrawingLoop ใช้พร้อมกัน (คนหนึ่งวนอ่าน อีกคนอาจเพิ่ม/ลบ)
    // ถ้าเธรดหนึ่งวน for อยู่แล้วอีกเธรดแก้ลิสต์ จะเกิด ConcurrentModificationException
    // วิธีแก้ (หัวข้อ 3.1.2): ทุกเมธอดที่แตะลิสต์เป็น synchronized (ทีละเธรด)
    // และ getter คืน "สำเนา" new ArrayList<>(list) ให้คนวนลูป → วนบนสำเนา แก้ลิสต์จริงได้ไม่กระทบ
    // ลิสต์ลูกบอลทั้งหมด
    private final List<Ball> balls = new ArrayList<>();
    // ลิสต์บล็อกทั้งหมด
    private final List<Brick> bricks = new ArrayList<>();
    // ลิสต์เลเซอร์ทั้งหมด
    private final List<Laser> lasers = new ArrayList<>();
    // ลิสต์ไอเทมที่ตกลงมา
    private final List<PowerUp> powerUps = new ArrayList<>();
    // เอฟเฟกต์ถูกแตะบน JavaFX Thread อย่างเดียว → ไม่ต้อง synchronized
    // ลิสต์เอฟเฟกต์ (ใช้เฉพาะบน JavaFX Thread จึงไม่ต้อง synchronized)
    private final List<Effect> effects = new ArrayList<>();
    // ตัวจำปุ่มที่กดอยู่
    private final Keys keys = new Keys();

    // สร้างเวที: พื้นหลัง → ชั้นเล่น (มี paddle) → HUD → ข้อความ → หน้าเลือกอัปเกรด → เมนู ซ้อนกันตามลำดับ (ตัวหลังอยู่บนสุด)
    // constructor ประกอบเวที
    public GameStage() {
        // โหลดภาพพื้นหลังด่าน 1
        background = new ImageView(AssetLoader.load("bg1.png"));
        // ตั้งความกว้างภาพเท่าจอ
        background.setFitWidth(WIDTH);
        // ตั้งความสูงภาพเท่าจอ
        background.setFitHeight(HEIGHT);
        // ตั้งขนาดชั้นเล่นเกมเท่าจอ
        playLayer.setPrefSize(WIDTH, HEIGHT);

        // สร้าง paddle วางกลางจอใกล้ขอบล่าง พร้อมกำหนดปุ่มซ้าย/ขวา
        paddle = new Paddle(WIDTH / 2.0 - Paddle.DEFAULT_WIDTH / 2, HEIGHT - 60, KeyCode.LEFT, KeyCode.RIGHT);
        // ใส่ paddle ลงในชั้นเล่น
        playLayer.getChildren().add(paddle);

        // ใส่ทุกชั้นเข้าเวทีเรียงลำดับ (หลังสุดอยู่บนสุด: พื้นหลัง → ชั้นเล่น → HUD → ข้อความ → อัปเกรด → เมนู)
        getChildren().addAll(background, playLayer, hud, messageView, upgradeView, menuView);
    }  // จบ constructor

    // ===================== เรียกจาก GameLoop Thread =====================

    // เปลี่ยนบล็อกทั้งหมดเป็นของด่านใหม่: ลิสต์เปลี่ยนทันที, ภาพเปลี่ยนตอน JavaFX Thread ว่าง
    // loadBricks ใส่ชุดบล็อกของด่านใหม่ (synchronized กันสองเธรดแก้ลิสต์พร้อมกัน)
    public synchronized void loadBricks(List<Brick> newBricks) {
        // ทำสำเนาบล็อกเก่าไว้ใช้ลบออกจากจอ
        List<Brick> old = new ArrayList<>(bricks);
        // ล้างลิสต์บล็อกเดิม
        bricks.clear();
        // ใส่บล็อกใหม่ทั้งหมด
        bricks.addAll(newBricks);
        // ฝากงานแก้จอไปให้ JavaFX Thread
        Platform.runLater(() -> {
            // เอาบล็อกเก่าออกจากจอ
            playLayer.getChildren().removeAll(old);
            // ใส่บล็อกใหม่ที่ตำแหน่งล่างสุดของชั้น (อยู่ใต้ paddle และลูก)
            playLayer.getChildren().addAll(0, newBricks); // index 0 = อยู่ใต้ paddle/ลูก
        });  // จบ lambda
    }  // จบ loadBricks

    // เปลี่ยนพื้นหลังตามด่าน (bg1.png ... bg5.png)
    // setStageBackground เปลี่ยนภาพพื้นหลังตามด่าน
    public void setStageBackground(int stage) {
        // โหลดรูป bg{ด่าน}.png
        Image image = AssetLoader.load("bg" + stage + ".png");
        // ให้ JavaFX Thread เปลี่ยนภาพ
        Platform.runLater(() -> background.setImage(image));
    }  // จบ setStageBackground

    // เพิ่ม/ลบ object ทั่วไป (ลูก, เลเซอร์, ไอเทม) → ใช้ generic <T> เขียนครั้งเดียวใช้ได้ทุกชนิด
    // addObject เมธอด generic เพิ่มวัตถุใดก็ได้ที่เป็น GameObject ลงลิสต์และจอ
    private synchronized <T extends GameObject> void addObject(List<T> list, T obj) {
        // เพิ่มลงลิสต์
        list.add(obj);
        // ให้ JavaFX Thread ใส่ลงชั้นเล่น
        Platform.runLater(() -> playLayer.getChildren().add(obj));
    }  // จบ addObject

    // เอา object ออกจากลิสต์ทันที แล้วฝาก JavaFX Thread ลบภาพออกจากจอ
    // removeObject ลบวัตถุออกจากลิสต์และจอ
    private synchronized <T extends GameObject> void removeObject(List<T> list, T obj) {
        // ลบออกจากลิสต์
        list.remove(obj);
        // ให้ JavaFX Thread ลบออกจากชั้นเล่น
        Platform.runLater(() -> playLayer.getChildren().remove(obj));
    }  // จบ removeObject

    // ล้างทั้งลิสต์: เก็บของเก่าไว้ → ล้างลิสต์ → ฝาก JavaFX Thread ลบภาพของเก่าทั้งหมด
    // clearObjects ลบวัตถุทั้งหมดในลิสต์
    private synchronized <T extends GameObject> void clearObjects(List<T> list) {
        // ทำสำเนาไว้ใช้ลบออกจากจอ
        List<T> old = new ArrayList<>(list);
        // ล้างลิสต์
        list.clear();
        // ให้ JavaFX Thread ลบทั้งหมดออกจากชั้นเล่น
        Platform.runLater(() -> playLayer.getChildren().removeAll(old));
    }  // จบ clearObjects

    // เพิ่มลูกบอลเข้าเกมและจอ
    public synchronized void addBall(Ball ball)            { addObject(balls, ball); }  // เพิ่มลูกบอลเข้าเกมและจอ
    // ลบลูกบอลหนึ่งลูก
    public synchronized void removeBall(Ball ball)         { removeObject(balls, ball); }  // ลบลูกบอลลูกเดียว
    // ลบลูกบอลทั้งหมด
    public synchronized void clearBalls()                  { clearObjects(balls); }  // ลบลูกบอลทั้งหมด
    // เพิ่มเลเซอร์
    public synchronized void addLaser(Laser laser)         { addObject(lasers, laser); }  // เพิ่มเลเซอร์
    // ลบเลเซอร์หนึ่งนัด
    public synchronized void removeLaser(Laser laser)      { removeObject(lasers, laser); }  // ลบเลเซอร์หนึ่งนัด
    // ลบเลเซอร์ทั้งหมด
    public synchronized void clearLasers()                 { clearObjects(lasers); }  // ลบเลเซอร์ทั้งหมด
    // เพิ่มไอเทม
    public synchronized void addPowerUp(PowerUp powerUp)   { addObject(powerUps, powerUp); }  // เพิ่มไอเทม
    // ลบไอเทมหนึ่งอัน
    public synchronized void removePowerUp(PowerUp p)      { removeObject(powerUps, p); }  // ลบไอเทมหนึ่งอัน
    // ลบไอเทมทั้งหมด
    public synchronized void clearPowerUps()               { clearObjects(powerUps); }  // ลบไอเทมทั้งหมด

    // สร้างเอฟเฟกต์ระเบิดที่จุดนี้
    // spawnExplosion สร้างเอฟเฟกต์ระเบิดที่จุดที่กำหนด
    public void spawnExplosion(double centerX, double centerY) {
        // ฝากให้ JavaFX Thread สร้างและเพิ่มเอฟเฟกต์
        Platform.runLater(() -> addEffect(new ExplosionEffect(centerX, centerY)));
    }  // จบ spawnExplosion

    // สร้างตัวเลขลอย เช่น "+120"
    // spawnFloatingText สร้างตัวเลขคะแนนลอย
    public void spawnFloatingText(double x, double y, String text, Color color) {
        // ฝากให้ JavaFX Thread สร้างและเพิ่มเอฟเฟกต์
        Platform.runLater(() -> addEffect(new FloatingText(x, y, text, color)));
    }  // จบ spawnFloatingText

    // โชว์หน้าเมนูพร้อมคะแนนสูงสุด (ฝาก JavaFX Thread)
    // showMenu แสดงหน้าเมนูพร้อมคะแนนสูงสุด
    public void showMenu(int bestScore) {
        // ฝากงานให้ JavaFX Thread
        Platform.runLater(() -> {
            // ตั้งข้อความคะแนนสูงสุด
            menuView.setBestScore(bestScore);
            // ทำให้เมนูมองเห็น
            menuView.setVisible(true);
        });  // จบ lambda
    }  // จบ showMenu

    // ซ่อนหน้าเมนู
    // hideMenu ซ่อนเมนู
    public void hideMenu() {
        // ฝากให้ JavaFX Thread ซ่อน
        Platform.runLater(() -> menuView.setVisible(false));
    }  // จบ hideMenu

    // โชว์หน้าเลือกอัปเกรดของด่านที่เพิ่งผ่าน พร้อมการ์ดตัวเลือก
    // showUpgrades แสดงหน้าเลือกอัปเกรด
    public void showUpgrades(int clearedStage, List<Upgrade> choices) {
        // ฝากให้ JavaFX Thread แสดงการ์ด
        Platform.runLater(() -> upgradeView.show(clearedStage, choices));
    }  // จบ showUpgrades

    // ซ่อนหน้าเลือกอัปเกรด
    // hideUpgrades ซ่อนหน้าเลือกอัปเกรด
    public void hideUpgrades() {
        // ฝากให้ JavaFX Thread เรียก hide (method reference)
        Platform.runLater(upgradeView::hide);
    }  // จบ hideUpgrades

    // โชว์ข้อความใหญ่กลางจอ (ค้างจนกว่าจะ hideMessage; GameLoop นับเวลาซ่อนเอง)
    // showMessage แสดงข้อความกลางจอ
    public void showMessage(String title, String subtitle, Color color) {
        // ฝากให้ JavaFX Thread แสดง
        Platform.runLater(() -> messageView.show(title, subtitle, color));
    }  // จบ showMessage

    // ซ่อนข้อความกลางจอ
    // hideMessage ซ่อนข้อความกลางจอ
    public void hideMessage() {
        // ฝากให้ JavaFX Thread ซ่อน
        Platform.runLater(messageView::hide);
    }  // จบ hideMessage

    // ===================== เรียกบน JavaFX Thread (จาก DrawingLoop) =====================

    // เพิ่มเอฟเฟกต์เข้าลิสต์และจอ (เรียกบน JavaFX Thread เท่านั้น)
    // addEffect เพิ่มเอฟเฟกต์ (เรียกบน JavaFX Thread เท่านั้น)
    private void addEffect(Effect effect) {
        // เพิ่มลงลิสต์เอฟเฟกต์
        effects.add(effect);
        // ใส่ลงในชั้นเล่น
        playLayer.getChildren().add(effect);
    }  // จบ addEffect

    // เล่นเอฟเฟกต์ทุกอันไป 1 เฟรม อันไหนจบแล้วลบออก
    // วนจาก "ท้ายมาหน้า" เพราะลบสมาชิกแล้วตัวที่อยู่หลังจะเลื่อน index ขึ้น ถ้าวนจากหน้าจะข้ามบางตัว
    // updateEffects อัปเดตเอฟเฟกต์ทุกเฟรม และลบอันที่หมดอายุ
    public void updateEffects() {
        // วนจากท้ายลิสต์มาหน้า (ลบแล้วดัชนีไม่เพี้ยน)
        for (int i = effects.size() - 1; i >= 0; i--) {
            // หยิบเอฟเฟกต์ตัวที่ i
            Effect effect = effects.get(i);
            // สั่ง update แล้วรับผลว่ายังเล่นอยู่ไหม
            boolean alive = effect.update();
            // ถ้าหมดอายุแล้ว
            if (!alive) {
                // เอาออกจากจอ
                playLayer.getChildren().remove(effect);
                // เอาออกจากลิสต์ตามดัชนี
                effects.remove(i);
            }  // จบ if
        }  // จบลูป
    }  // จบ updateEffects

    // ===================== getter =====================
    // getter paddle
    public Paddle getPaddle()           { return paddle; }
    // คืนสำเนาลิสต์ลูกบอล (กัน ConcurrentModificationException)
    public synchronized List<Ball> getBalls()        { return new ArrayList<>(balls); }  // สำเนาลิสต์ลูกบอล
    // คืนสำเนาลิสต์บล็อก
    public synchronized List<Brick> getBricks()      { return new ArrayList<>(bricks); }  // สำเนาลิสต์บล็อก
    // คืนสำเนาลิสต์เลเซอร์
    public synchronized List<Laser> getLasers()      { return new ArrayList<>(lasers); }  // สำเนาลิสต์เลเซอร์
    // คืนสำเนาลิสต์ไอเทม
    public synchronized List<PowerUp> getPowerUps()  { return new ArrayList<>(powerUps); }  // สำเนาลิสต์ไอเทม
    // getter ตัวจำปุ่ม
    public Keys getKeys()               { return keys; }  // ตัวจำปุ่มที่กดอยู่
    // getter HUD
    public Hud getHud()                 { return hud; }  // แถบคะแนนด้านบน
    // getter หน้าเลือกอัปเกรด
    public UpgradeView getUpgradeView() { return upgradeView; }  // หน้าเลือกอัปเกรด
}  // จบคลาส GameStage
