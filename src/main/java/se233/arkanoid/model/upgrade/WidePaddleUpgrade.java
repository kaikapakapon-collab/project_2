// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): WidePaddleUpgrade
// อัปเกรด: paddle กว้างขึ้น 25px (สูงสุด 200)
// อัปเกรด "paddle กว้างขึ้น +25 px" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class WidePaddleUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public WidePaddleUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Wide Paddle", "Paddle +25 px wider");
    }  // จบ constructor

    // ขยาย paddle +25 px (สูงสุด 200) (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มความกว้าง paddle 25 px
        state.increasePaddleWidth(25);
    }  // จบ apply
}  // จบคลาส WidePaddleUpgrade
