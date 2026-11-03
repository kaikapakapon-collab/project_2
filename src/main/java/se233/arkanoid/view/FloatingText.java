// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Color
import javafx.scene.paint.Color;
// นำเข้า Font
import javafx.scene.text.Font;
// นำเข้า FontWeight
import javafx.scene.text.FontWeight;
// นำเข้า Text
import javafx.scene.text.Text;

/*
 * [เกณฑ์ตาราง] Display score (เลข +คะแนน ลอยตรงบล็อก), Additional animation
 * FloatingText = ตัวเลขคะแนน "+120" ที่ลอยขึ้นแล้วจางหายตรงบล็อกที่แตก
 * → เกณฑ์ Display score: เห็นคะแนนตอนตีโดน/บล็อกแตก
 */
// FloatingText ตัวเลขคะแนนที่ลอยขึ้นแล้วจางหาย สืบทอดจาก Effect
public class FloatingText extends Effect {
    // มีอายุ 45 เฟรม (ประมาณ 0.75 วินาที)
    private static final int LIFETIME = 45; // อยู่กี่เฟรม (≈ 0.75 วินาที)

    // อายุปัจจุบัน
    private int age = 0;

    // สร้างตัวเลขลอยที่ (x, y) ข้อความ message สี color (ตัวหนา มีขอบดำให้อ่านง่าย)
    // constructor รับตำแหน่ง ข้อความ และสี
    public FloatingText(double x, double y, String message, Color color) {
        // สร้างข้อความ
        Text text = new Text(message);
        // ตั้งฟอนต์ Monospaced ตัวหนา ขนาด 14
        text.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        // ตั้งสีตัวอักษร
        text.setFill(color);
        // ตั้งสีขอบเป็นดำ (อ่านง่ายบนทุกพื้น)
        text.setStroke(Color.BLACK);
        // ตั้งความหนาขอบ
        text.setStrokeWidth(0.6);
        // ใส่ข้อความลงใน Pane
        getChildren().add(text);
        // วางตำแหน่ง X
        setTranslateX(x);
        // วางตำแหน่ง Y
        setTranslateY(y);
        // ไม่รับเมาส์
        setMouseTransparent(true);
    }  // จบ constructor

    // ทุกเฟรม: ลอยขึ้น 0.8px และจางลงเรื่อย ๆ จนหาย
    // เขียนทับ update ของ Effect
    @Override
    // update ถูกเรียกทุกเฟรมจอ
    public boolean update() {
        // เพิ่มอายุ
        age++;
        // ลอยขึ้น 0.8 px (ลดค่า Y)
        setTranslateY(getTranslateY() - 0.8);
        // ค่อย ๆ โปร่งใสขึ้นตามอายุ (1.0 → 0.0)
        setOpacity(1.0 - (double) age / LIFETIME);
        // คืน true ถ้ายังไม่หมดอายุ
        return age < LIFETIME;
    }  // จบ update
}  // จบคลาส FloatingText
