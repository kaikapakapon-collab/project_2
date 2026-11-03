// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า KeyCode ใช้เก็บปุ่มซ้าย/ขวา
import javafx.scene.input.KeyCode;
// นำเข้า GameStage เพื่ออ่านความกว้างหน้าจอ
import se233.arkanoid.view.GameStage;

/*
 * [เกณฑ์ตาราง] Sprite sheet (ท่าเคลื่อนที่ + โหมดเลเซอร์), Functionalities (ควบคุมด้วยคีย์บอร์ด), OOP (ลูกของ GameObject)
 * Paddle = ไม้ตีของผู้เล่น ขยับได้แค่ซ้าย-ขวา
 * - วิธีขยับเหมือน Script 4.22: ปุ่มไม่ได้ย้ายตำแหน่งตรง ๆ แต่ไปตั้ง "ความเร็ว" แล้ว move() เอาไปบวกตำแหน่ง
 * - ภาพเป็น AnimatedSprite จาก paddle.png (4 เฟรม x 2 แถว)
 *     แถว 0 = paddle ปกติ, แถว 1 = paddle โหมดเลเซอร์ (ปืนสองข้าง)
 *     อนิเมชันไฟวิ่งจะเล่น "เฉพาะตอนขยับ" → ท่าเคลื่อนที่ของ paddle
 */
// Paddle (แป้นรับลูก) สืบทอดจาก GameObject
public class Paddle extends GameObject {
    // ความกว้างเริ่มต้น 100 px
    public static final double DEFAULT_WIDTH = 100;
    // ความสูง 16 px
    public static final double HEIGHT = 16;
    // ความเร็วเริ่มต้น 8 px ต่อเฟรม
    public static final double DEFAULT_SPEED = 8;
    // ขนาด 1 เฟรมใน sprite sheet (กว้าง 64 สูง 16)
    private static final int FRAME_W = 64, FRAME_H = 16; // ขนาด 1 เฟรมใน sprite sheet

    // ความเร็วสูงสุดที่ paddle ขยับได้ต่อเฟรม
    private double speed = DEFAULT_SPEED; // ขยับได้กี่ pixel ต่อเฟรม
    // ความเร็วขณะนี้ (ลบ = ซ้าย, บวก = ขวา, 0 = หยุด)
    private double xVelocity = 0;         // ความเร็วตอนนี้: ติดลบ = ซ้าย, บวก = ขวา, 0 = หยุด
    // อยู่ในโหมดเลเซอร์หรือไม่ (เธรดสองตัวใช้ร่วมกัน จึงเข้าถึงผ่านเมธอด synchronized)
    private boolean laserMode = false; // [เธรด] GameLoop ตั้งค่า, DrawingLoop อ่าน → เข้าถึงผ่านเมธอด synchronized
    // ปุ่มเลื่อนซ้าย
    private final KeyCode leftKey;
    // ปุ่มเลื่อนขวา
    private final KeyCode rightKey;
    // sprite ของ paddle
    private final AnimatedSprite sprite;

    // สร้าง paddle ที่ (x, y) กำหนดปุ่มซ้าย/ขวา และโหลด sprite sheet paddle.png (4 เฟรม x 2 แถว: ปกติ / โหมดเลเซอร์)
    // constructor รับตำแหน่งและปุ่มซ้าย/ขวา
    public Paddle(double x, double y, KeyCode leftKey, KeyCode rightKey) {
        // ส่งตำแหน่งและขนาดให้คลาสแม่ GameObject
        super(x, y, DEFAULT_WIDTH, HEIGHT);
        // เก็บปุ่มซ้าย
        this.leftKey = leftKey;
        // เก็บปุ่มขวา
        this.rightKey = rightKey;
        // สร้าง sprite จาก paddle.png (4 เฟรม 4 คอลัมน์ 1 แถว)
        sprite = new AnimatedSprite(AssetLoader.load("paddle.png"), 4, 4, 1, 0, 0, FRAME_W, FRAME_H);
        // ยืดภาพให้กว้างเท่า paddle จริง
        sprite.setFitWidth(DEFAULT_WIDTH);  // ยืดรูป 64px ให้กว้างเท่า paddle จริง
        // ตั้งความสูงภาพ
        sprite.setFitHeight(HEIGHT);
        // ใส่ sprite เข้าไปใน Pane
        getChildren().add(sprite);
    }  // จบ constructor

    // ตั้งความเร็วตามทิศ (ยังไม่ขยับจริงจนกว่าจะเรียก move())
    // moveLeft ตั้งความเร็วไปทางซ้าย
    public void moveLeft()  { xVelocity = -speed; }
    // moveRight ตั้งความเร็วไปทางขวา
    public void moveRight() { xVelocity = speed; }  // (ตั้งความเร็วไปขวา)
    // stop หยุด (ความเร็ว 0)
    public void stop()      { xVelocity = 0; }  // (ตั้งความเร็วเป็น 0 = หยุด)

    // ขยับจริง: ตำแหน่งใหม่ = ตำแหน่งเดิม + ความเร็ว   [เธรด] GameLoop
    // move ขยับ paddle ตามความเร็ว
    public void move() {
        // เพิ่มตำแหน่ง x ด้วยความเร็วปัจจุบัน
        x = x + xVelocity;
    }  // จบ move

    // ห้ามออกนอกจอซ้าย/ขวา   [เธรด] GameLoop
    // checkReachWall กันไม่ให้ paddle หลุดขอบจอ
    public void checkReachWall() {
        // ถ้าเลยขอบซ้าย
        if (x < 0) {
            // ดึงกลับมาที่ 0
            x = 0;
        // ถ้าเลยขอบขวา
        } else if (x + w > GameStage.WIDTH) {
            // ดึงกลับให้ชิดขอบขวา
            x = GameStage.WIDTH - w;
        }  // จบ if/else
    }  // จบ checkReachWall

    // ใส่ค่าจากอัปเกรด (ความกว้าง/ความเร็ว) โดยขยายจากตรงกลาง ไม่ใช่ขยายไปทางขวาอย่างเดียว
    // applyStats ปรับความกว้าง/ความเร็วตามอัปเกรด
    public void applyStats(double newWidth, double newSpeed) {
        // ขยับ x เพื่อให้ขยายออกสองข้างเท่ากัน (กึ่งกลางคงเดิม)
        x = x + (w - newWidth) / 2;
        // ตั้งความกว้างใหม่
        w = newWidth;
        // ตั้งความเร็วใหม่
        speed = newSpeed;
        // เช็กไม่ให้ล้นขอบจอ
        checkReachWall();
    }  // จบ applyStats

    // กลับไปกลางจอ (ใช้ตอนเริ่มด่าน)
    // resetPosition วาง paddle กลางจอ
    public void resetPosition() {
        // คำนวณ x ให้กึ่งกลาง paddle ตรงกลางจอ
        x = GameStage.WIDTH / 2.0 - w / 2;
        // หยุดการเคลื่อนที่
        xVelocity = 0;
    }  // จบ resetPosition

    // ถามว่ากำลังขยับอยู่ไหม (ความเร็ว ≠ 0)
    public boolean isMoving() { return xVelocity != 0; }  // กำลังขยับอยู่ไหน (ใช้เลือกว่าจะเล่นอนิเมชันไฟวิ่ง)

    // อัปเดตภาพเคลื่อนไหว   [เธรด] DrawingLoop (JavaFX Thread)
    // เลือกแถว: โหมดเลเซอร์ = แถว 1, ปกติ = แถว 0 / เล่นเฟรมถัดไปเฉพาะตอนกำลังขยับ
    // updateSprite เลือกแถวอนิเมชันและเล่นเฟรม
    public synchronized void updateSprite() {
        // ใช้แถวที่ 1 (โหมดเลเซอร์) หรือแถวที่ 0 (ปกติ)
        sprite.changeAnimation(0, laserMode ? FRAME_H : 0);
        // ถ้ากำลังขยับ
        if (isMoving()) {
            // เลื่อนไปเฟรมถัดไป (ไฟวิ่ง)
            sprite.tick();
        }  // จบ if
    }  // จบ updateSprite

    // ย้ายภาพ + ปรับความกว้างภาพให้ตรงกับ w (เผื่อเพิ่งอัปเกรดขยาย)   [เธรด] DrawingLoop
    // เขียนทับ repaint ของคลาสแม่
    @Override
    // ประกาศเมธอด repaint
    public void repaint() {
        // เรียก repaint ของแม่เพื่อย้ายตำแหน่ง
        super.repaint();
        // ปรับความกว้างภาพให้ตรงกับความกว้างปัจจุบัน
        sprite.setFitWidth(w);
    }  // จบ repaint

    // getter ปุ่มซ้าย
    public KeyCode getLeftKey()  { return leftKey; }  // ปุ่มไปซ้าย
    // getter ปุ่มขวา
    public KeyCode getRightKey() { return rightKey; }  // ปุ่มไปขวา
    // getter ความเร็วสูงสุด
    public double getSpeed()     { return speed; }  // ความเร็วสูงสุด (pixel ต่อเฟรม)
    // getter ความเร็วตอนนี้
    public double getXVelocity() { return xVelocity; }  // ความเร็วตอนนี้
    // ถามว่าอยู่ในโหมดเลเซอร์ไหน
    public synchronized boolean isLaserMode() { return laserMode; }  // อยู่ในโหมดเลเซอร์ไหน
    // เปิด/ปิดโหมดเลเซอร์ (เปลี่ยนรูป paddle)
    public synchronized void setLaserMode(boolean laserMode) { this.laserMode = laserMode; }  // เปิด/ปิดโหมดเลเซอร์ (เปลี่ยนรูป paddle เป็นแบบมีปืน)
}  // จบคลาส Paddle
