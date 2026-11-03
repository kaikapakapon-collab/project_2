// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): FastPaddleUpgrade
// อัปเกรด: paddle ขยับเร็วขึ้น +2 px/เฟรม
// อัปเกรด "paddle เร็วขึ้น +2" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class FastPaddleUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public FastPaddleUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Swift Paddle", "Paddle moves faster (+2 speed)");
    }  // จบ constructor

    // เพิ่มความเร็ว paddle +2 px/เฟรม (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มความเร็ว paddle 2 หน่วย
        state.increasePaddleSpeed(2);
    }  // จบ apply
}  // จบคลาส FastPaddleUpgrade
