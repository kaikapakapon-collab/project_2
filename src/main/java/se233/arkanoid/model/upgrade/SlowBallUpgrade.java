// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): SlowBallUpgrade
// อัปเกรด: ลูกช้าลง 15%
// อัปเกรด "ลูกช้าลง 15%" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class SlowBallUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public SlowBallUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Slow Ball", "Ball 15% slower, easier to control");
    }  // จบ constructor

    // ลดความเร็วลูก 15% (คูณ 0.85) (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // คูณความเร็วลูกด้วย 0.85 (ช้าลง 15%)
        state.multiplyBallSpeed(0.85);
    }  // จบ apply
}  // จบคลาส SlowBallUpgrade
