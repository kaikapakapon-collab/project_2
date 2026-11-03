// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า Pane กล่องของ JavaFX ที่เราใช้เป็นคลาสแม่
import javafx.scene.layout.Pane;

/*
 * [เกณฑ์ตาราง] OOP (คลาสแม่ของ Paddle/Ball/Brick/Laser/PowerUp: ตำแหน่ง ขนาด การชน)
 * GameObject = คลาสแม่ของทุกอย่างในจอเกม (Paddle, Ball, Brick)
 * - extends Pane เหมือน GameCharacter ในบทที่ 4 → ใส่รูป/รูปทรงไว้ข้างในได้
 * - เป็น abstract เพราะไม่มีใครสร้าง GameObject เปล่า ๆ ต้องสร้างเป็นลูกเท่านั้น
 *
 * แนวคิดสำคัญ: "ข้อมูล" กับ "ภาพ" แยกกัน
 * - x, y, w, h = ตำแหน่ง/ขนาดจริงในเกม → GameLoop เป็นคนแก้
 * - setTranslateX/Y = ตำแหน่งภาพบนจอ → DrawingLoop เป็นคนแก้ผ่าน repaint()
 */
// คลาสแม่แบบ abstract ของทุกวัตถุในเกม (Paddle/Ball/Brick/Laser/PowerUp) สืบทอดจาก Pane
public abstract class GameObject extends Pane {
    // ตำแหน่งมุมซ้ายบนแกน X (protected = คลาสลูกใช้ได้)
    protected double x; // มุมซ้ายบน แกน X
    // ตำแหน่งมุมซ้ายบนแกน Y
    protected double y; // มุมซ้ายบน แกน Y
    // ความกว้างของวัตถุ (ใช้ชื่อ w เพราะ Pane มี getWidth() แบบ final แล้ว)
    protected double w; // ความกว้าง (ใช้ชื่อ w เพราะ Pane มี getWidth() แบบ final อยู่แล้ว)
    // ความสูงของวัตถุ
    protected double h; // ความสูง

    // ตั้งค่าตำแหน่ง/ขนาดเริ่มต้น แล้ววางภาพไว้ที่ตำแหน่งนั้นทันที
    // constructor รับตำแหน่ง x,y และขนาด w,h
    public GameObject(double x, double y, double w, double h) {
        // เก็บ x
        this.x = x;
        // เก็บ y
        this.y = y;
        // เก็บ w
        this.w = w;
        // เก็บ h
        this.h = h;
        // ย้ายภาพบนจอตามแกน X
        setTranslateX(x);
        // ย้ายภาพบนจอตามแกน Y
        setTranslateY(y);
    }  // จบ constructor

    // ย้าย "ภาพ" ไปตาม x, y ล่าสุด
    // [เธรด] ต้องเรียกบน JavaFX Application Thread เท่านั้น (DrawingLoop เรียกผ่าน Platform.runLater)
    // repaint ซิงก์ตำแหน่งบนจอให้ตรงกับค่า x,y ที่คำนวณไว้
    public void repaint() {
        // ตั้งตำแหน่งภาพแกน X
        setTranslateX(x);
        // ตั้งตำแหน่งภาพแกน Y
        setTranslateY(y);
    }  // จบ repaint

    // เช็กว่ากล่องสี่เหลี่ยมของเรากับของ other ซ้อนกันไหม (AABB collision)
    // ชนกันเมื่อ: ซ้ายเราอยู่ก่อนขวาเขา && ขวาเราเลยซ้ายเขา && (เช็กแบบเดียวกันแกน Y)
    // [เธรด] GameLoop เรียก
    // เช็กการชนแบบกล่องสี่เหลี่ยม (AABB) กับวัตถุอื่น
    public boolean collidesWith(GameObject other) {
        // ชนกันแนวนอนเมื่อซ้ายเรามาก่อนขวาอีกฝ่าย และขวาเรามาหลังซ้ายอีกฝ่าย
        return x < other.x + other.w && x + w > other.x
                // และชนกันแนวตั้งในทำนองเดียวกัน ถ้าครบทั้งสองแกนคือชน (true)
                && y < other.y + other.h && y + h > other.y;
    }  // จบ collidesWith

    // getter / setter พื้นฐาน
    // getter ตำแหน่ง X
    public double getX() { return x; }
    // getter ตำแหน่ง Y
    public double getY() { return y; }  // ตำแหน่งแกน Y (มุมซ้ายบน)
    // getter ความกว้าง
    public double getW() { return w; }  // ความกว้าง
    // getter ความสูง
    public double getH() { return h; }  // ความสูง
    // คืนจุดกึ่งกลางแนวนอน (x + w/2)
    public double getCenterX() { return x + w / 2; } // จุดกึ่งกลางแนวนอน (ใช้คำนวณมุมเด้ง)
    // setter ตั้ง X
    public void setX(double x) { this.x = x; }  // ตั้งตำแหน่ง X (ใช้ตอนรีเซ็ตตำแหน่งและใน test)
    // setter ตั้ง Y
    public void setY(double y) { this.y = y; }  // ตั้งตำแหน่ง Y
}  // จบคลาส GameObject
