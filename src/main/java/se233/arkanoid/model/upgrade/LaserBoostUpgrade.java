// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): LaserBoostUpgrade
// อัปเกรด: ไอเทมเลเซอร์อยู่นานขึ้น 5 วินาที
// อัปเกรด "เลเซอร์นานขึ้น +5 วินาที" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class LaserBoostUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public LaserBoostUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Laser Mastery", "Laser power-up lasts +5 s");
    }  // จบ constructor

    // เพิ่มเวลาเลเซอร์ +5 วินาที (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มเวลาเลเซอร์ 5 วินาที
        state.addLaserSeconds(5);
    }  // จบ apply
}  // จบคลาส LaserBoostUpgrade
