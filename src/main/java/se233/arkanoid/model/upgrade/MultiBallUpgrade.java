// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): MultiBallUpgrade
// อัปเกรด: ตอนปล่อยลูก จะแตกเพิ่มอีก 1 ลูก (สะสมได้สูงสุด 4)
// อัปเกรด "ปล่อยลูกเพิ่ม 1 ลูก" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class MultiBallUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public MultiBallUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Twin Ball", "Launch +1 extra ball");
    }  // จบ constructor

    // เพิ่มจำนวนลูกที่แตกเพิ่มตอนปล่อยลูก +1 (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มจำนวนลูกที่แตกเพิ่ม 1 ลูก
        state.addExtraBall();
    }  // จบ apply
}  // จบคลาส MultiBallUpgrade
