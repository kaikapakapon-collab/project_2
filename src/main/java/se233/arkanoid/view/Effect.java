// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Pane กล่องของ JavaFX
import javafx.scene.layout.Pane;

/*
 * [เกณฑ์ตาราง] Additional animation (คลาสแม่ของเอฟเฟกต์), OOP
 * Effect = คลาสแม่ของเอฟเฟกต์ภาพชั่วคราว (ระเบิด, ตัวเลขคะแนนลอย)
 * เอฟเฟกต์เป็น "ภาพล้วน" ไม่มีผลกับเกม → อัปเดตใน DrawingLoop บน JavaFX Thread อย่างเดียว
 */
// Effect คือคลาสแม่แบบ abstract ของเอฟเฟกต์ภาพชั่วคราว (ระเบิด, ตัวเลขลอย)
public abstract class Effect extends Pane {
    // เล่นเอฟเฟกต์ไป 1 เฟรม: คืน true = ยังเล่นอยู่, false = จบแล้ว ให้ลบทิ้ง
    // เมธอด abstract update: คลาสลูกต้องเขียนเอง คืน true = ยังเล่นอยู่, false = หมดอายุให้ลบ
    public abstract boolean update();
}  // จบคลาส Effect
