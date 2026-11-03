// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า GameStage เพื่ออ่านเส้นบนของจอ
import se233.arkanoid.view.GameStage;

/*
 * [เกณฑ์ตาราง] Sprite sheet (special attack: เลเซอร์คู่), OOP (ลูกของ GameObject)
 * Laser = กระสุนเลเซอร์ที่ paddle ยิงขึ้นไป (special attack)
 * ยิงทีละ 2 นัดจากปืนซ้าย-ขวาของ paddle (dual laser)
 * ชนบล็อก = บล็อกโดนตี 1 ที แล้วเลเซอร์หายไป
 */
// Laser (กระสุนเลเซอร์) สืบทอดจาก GameObject
public class Laser extends GameObject {
    // ความกว้าง 4 px
    public static final double WIDTH = 4;
    // ความสูง 16 px
    public static final double HEIGHT = 16;
    // ความเร็วยิงขึ้น 10 px ต่อเฟรม
    private static final double SPEED = 10;

    // ธงว่ายังใช้งานอยู่ไหน (false = ชนแล้ว/ออกจอ รอลบ)
    private boolean active = true; // false = ชนแล้ว/ออกจอ รอลบ (เข้าถึงผ่านเมธอด synchronized)
    // sprite ของเลเซอร์
    private final AnimatedSprite sprite;

    // สร้างกระสุนเลเซอร์ที่ (x, y) กว้าง 4 สูง 16 และโหลด sprite laser.png (2 เฟรม)
    // constructor รับตำแหน่งเริ่ม
    public Laser(double x, double y) {
        // ส่งตำแหน่งและขนาดให้คลาสแม่
        super(x, y, WIDTH, HEIGHT);
        // สร้าง sprite จาก laser.png (2 เฟรม)
        sprite = new AnimatedSprite(AssetLoader.load("laser.png"), 2, 2, 1, 0, 0, 6, 16);
        // ตั้งความกว้างภาพ
        sprite.setFitWidth(WIDTH);
        // ตั้งความสูงภาพ
        sprite.setFitHeight(HEIGHT);
        // ใส่ sprite ลง Pane
        getChildren().add(sprite);
    }  // จบ constructor

    // พุ่งขึ้น   [เธรด] GameLoop
    // move ขยับเลเซอร์ขึ้น
    public void move() {
        // ลดค่า y ด้วยความเร็ว (แกน Y ชี้ลง การลดค่า = ขึ้นบน)
        y = y - SPEED;
    }  // จบ move

    // หลุดขอบบนแล้วหรือยัง
    // isOut เลเซอร์พ้นขอบบนจอหรือยัง
    public boolean isOut() {
        // คืน true ถ้าส่วนล่างของเลเซอร์สูงกว่าเส้นบนของจอ
        return y + h < GameStage.TOP;
    }  // จบ isOut

    // กระพริบ 2 เฟรม   [เธรด] DrawingLoop
    // updateSprite เล่นเฟรมถัดไป
    public void updateSprite() {
        // เลื่อนเฟรม
        sprite.tick();
    }  // จบ updateSprite

    // ถามว่ายังใช้งานอยู่ไหม
    public synchronized boolean isActive()  { return active; }  // ยังใช้งานอยู่ไหน
    // ทำเครื่องหมายว่าใช้แล้ว
    public synchronized void deactivate()   { active = false; }  // ทำเครื่องหมายว่าใช้แล้ว
}  // จบคลาส Laser
