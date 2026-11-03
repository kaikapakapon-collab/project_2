// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState เพราะต้องแก้ค่าในนี้
import se233.arkanoid.model.GameState;

// [เกณฑ์ตาราง] Functionalities (roguelike upgrade): ScoreBoostUpgrade
// อัปเกรด: ตัวคูณคะแนน +0.5
// อัปเกรด "คะแนน +0.5 เท่า" สืบทอดจาก Upgrade (polymorphism: ทุกอัปเกรดใช้เมธอด apply ชื่อเดียวกัน)
public class ScoreBoostUpgrade extends Upgrade {
    // ตั้งชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor ของอัปเกรดนี้
    public ScoreBoostUpgrade() {
        // ส่งชื่อและคำอธิบายบนการ์ดให้คลาสแม่ Upgrade
        super("Score Boost", "Score x0.5 more for every brick");
    }  // จบ constructor

    // เพิ่มตัวคูณคะแนน +0.5 (ใส่ลง GameState ผลอยู่ถึงจบ run)
    // @Override บอกว่าเขียนทับเมธอด abstract apply ของ Upgrade
    @Override
    // apply ถูกเรียกเมื่อผู้เล่นเลือกการ์ดนี้
    public void apply(GameState state) {
        // เพิ่มตัวคูณคะแนน 0.5
        state.addScoreMultiplier(0.5);
    }  // จบ apply
}  // จบคลาส ScoreBoostUpgrade
