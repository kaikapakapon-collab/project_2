// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า GameStage เพื่ออ่านความสูงจอ
import se233.arkanoid.view.GameStage;

/*
 * [เกณฑ์ตาราง] Functionalities (ไอเทมระหว่างเล่น), Sprite sheet (powerup.png), OOP (ลูกของ GameObject)
 * PowerUp = แคปซูลไอเทมที่หล่นลงมาตอนบล็อกแตก (ไอเทมระหว่างเล่น ตามข้อ 3.6 ของโจทย์)
 * paddle รับได้ = ได้ผลทันที
 *   LASER → ยิงเลเซอร์คู่ได้ช่วงเวลาหนึ่ง (special attack)
 *   MULTI → แตกลูกบอลเพิ่ม 2 ลูกทันที
 */
// PowerUp (ไอเทมที่ตกลงมา) สืบทอดจาก GameObject
public class PowerUp extends GameObject {
    // enum ชนิดไอเทม: LASER (เลเซอร์) หรือ MULTI (แตกลูกเพิ่ม)
    public enum Type { LASER, MULTI }

    // ความกว้าง 32 px
    public static final double WIDTH = 32;
    // ความสูง 14 px
    public static final double HEIGHT = 14;
    // ความเร็วตก 2.5 px ต่อเฟรม
    private static final double FALL_SPEED = 2.5;

    // ชนิดของไอเทมนี้
    private final Type type;
    // ธงว่ายังไม่ถูกเก็บ/ยังไม่หลุดจอ
    private boolean active = true; // false = ชนแล้ว/ออกจอ รอลบ (เข้าถึงผ่านเมธอด synchronized)
    // sprite ของไอเทม
    private final AnimatedSprite sprite;

    // สร้างแคปซูลไอเทมชนิด type ที่ (x, y) โหลดรูป L (เลเซอร์) หรือ M (หลายลูก) จาก powerup.png
    // constructor รับตำแหน่งและชนิด
    public PowerUp(double x, double y, Type type) {
        // ส่งตำแหน่งและขนาดให้คลาสแม่
        super(x, y, WIDTH, HEIGHT);
        // เก็บชนิด
        this.type = type;
        // แถว 0 ของ powerup.png = แคปซูล L (เลเซอร์), แถว 1 = แคปซูล M (multi ball)
        // เลือกแถวของ sprite sheet: LASER แถว 0, MULTI แถว 1
        int row = (type == Type.LASER) ? 0 : 1;
        // สร้าง sprite จาก powerup.png (4 เฟรม) ตัดตามแถวที่เลือก
        sprite = new AnimatedSprite(AssetLoader.load("powerup.png"), 4, 4, 1, 0, row * (int) HEIGHT, (int) WIDTH, (int) HEIGHT);
        // ใส่ sprite ลง Pane
        getChildren().add(sprite);
    }  // จบ constructor

    // หล่นลง   [เธรด] GameLoop
    // move ให้ไอเทมตกลง
    public void move() {
        // เพิ่ม y ด้วยความเร็วตก
        y = y + FALL_SPEED;
    }  // จบ move

    // หล่นเลยขอบล่าง = ผู้เล่นรับไม่ทัน
    // isOut ไอเทมตกพ้นจอล่างหรือยัง
    public boolean isOut() {
        // คืน true ถ้า y เกินความสูงจอ
        return y > GameStage.HEIGHT;
    }  // จบ isOut

    // แสงวิ่งบนแคปซูล   [เธรด] DrawingLoop
    // updateSprite เล่นเฟรมถัดไป
    public void updateSprite() {
        // เลื่อนเฟรม
        sprite.tick();
    }  // จบ updateSprite

    // getter ชนิดไอเทม
    public Type getType()     { return type; }  // ชนิดไอเทม
    // ถามว่ายังใช้งานอยู่ไหม
    public synchronized boolean isActive() { return active; }  // ยังไม่ถูกเก็บ/ยังไม่หลุดจอ
    // ทำเครื่องหมายว่าใช้แล้ว
    public synchronized void deactivate()  { active = false; }  // ทำเครื่องหมายว่าใช้แล้ว
}  // จบคลาส PowerUp
