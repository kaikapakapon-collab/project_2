// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): ExtraLifeUpgrade
// อัปเกรด: ชีวิต +1
// อัปเกรด "ชีวิต +1" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class ExtraLifeUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public ExtraLifeUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Extra Life", "Gain +1 life");
    }  // จบ constructor

    // เพิ่มชีวิต +1 (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มชีวิต 1 ดวง
        state.addLife();
    }  // จบ apply
}  // จบคลาส ExtraLifeUpgrade
