// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): PiercingBallUpgrade
// อัปเกรด: ลูกไฟ ตี paddle 1 ครั้ง ทะลุบล็อกได้ 2 ก้อน (ทะลุ = แตกทันที)
// อัปเกรด "ลูกทะลุบล็อก 2 ก้อน" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class PiercingBallUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public PiercingBallUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Piercing Ball", "Ball passes through 2 bricks after each paddle hit");
    }  // จบ constructor

    // ให้ลูกทะลุบล็อกได้ 2 ก้อนต่อการตี paddle 1 ครั้ง (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มพลังทะลุ 2 ก้อนต่อการตี paddle
        state.addPierce(2);
    }  // จบ apply
}  // จบคลาส PiercingBallUpgrade
