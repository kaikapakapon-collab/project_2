// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Pos ใช้จัดตำแหน่งตรงกลาง
import javafx.geometry.Pos;
// นำเข้า VBox กล่องเรียงแนวตั้ง
import javafx.scene.layout.VBox;
// นำเข้า Color
import javafx.scene.paint.Color;
// นำเข้า Font
import javafx.scene.text.Font;
// นำเข้า FontWeight
import javafx.scene.text.FontWeight;
// นำเข้า Text
import javafx.scene.text.Text;
// นำเข้า TextAlignment
import javafx.scene.text.TextAlignment;

/*
 * [เกณฑ์ตาราง] Functionalities (Start Game: หน้าเมนู)
 * MenuView = หน้าเมนูเริ่มเกม (Use case 3.1 Start Game)
 * VBox + Pos.CENTER = เรียงข้อความแนวตั้งกึ่งกลางจอให้เอง ไม่ต้องคำนวณตำแหน่ง
 */
// MenuView หน้าเมนูเริ่มเกม สืบทอดจาก VBox (เรียงข้อความจากบนลงล่าง)
public class MenuView extends VBox {
    // ข้อความแสดงคะแนนสูงสุด (เปลี่ยนค่าได้ภายหลัง)
    private final Text bestText;

    // สร้างหน้าเมนู: ชื่อเกม, วิธีเล่น, ข้อความกด ENTER เริ่ม และคะแนนสูงสุด เรียงแนวตั้งกลางจอบนพื้นดำโปร่ง
    // constructor ประกอบหน้าเมนู
    public MenuView() {
        // ตั้งขนาดให้เต็มจอ
        setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);
        // จัดเนื้อหาให้อยู่กึ่งกลาง
        setAlignment(Pos.CENTER);
        // ตั้งระยะห่างระหว่างข้อความ 18 px
        setSpacing(18);
        // ตั้งพื้นหลังสีดำโปร่งใสด้วย CSS
        setStyle("-fx-background-color: rgba(0,0,0,0.78);");

        // สร้างข้อความชื่อเกม "ARKANOID" สีฟ้า
        Text title = text("ARKANOID", 56, Color.web("#3fa9f5"));
        // สร้างข้อความรอง "ROGUELIKE EDITION" สีเหลือง
        Text subtitle = text("~ ROGUELIKE EDITION ~", 20, Color.web("#ffd93d"));
        // สร้างข้อความอธิบายปุ่มควบคุม (หลายบรรทัด)
        Text controls = text(
                // บรรทัดอธิบายปุ่มลูกศร
                "←  →   Move paddle\n"
                        // บรรทัดอธิบาย SPACE
                        + "SPACE  Launch ball / Fire laser\n"
                        // บรรทัดอธิบายปุ่ม P
                        + "P      Pause\n"
                        // บรรทัดอธิบายการเลือกอัปเกรด และกำหนดขนาด 16 สีเทาอ่อน
                        + "1 2 3  Pick upgrade (or click)", 16, Color.LIGHTGRAY);
        // สร้างข้อความ "PRESS ENTER TO START"
        Text start = text("PRESS ENTER TO START", 24, Color.WHITE);
        // สร้างข้อความคะแนนสูงสุด (เริ่มต้นว่างเปล่า)
        bestText = text("", 16, Color.web("#ff9f43"));

        // ใส่ข้อความทั้งหมดลงใน VBox ตามลำดับ
        getChildren().addAll(title, subtitle, controls, start, bestText);
    }  // จบ constructor

    // สร้างข้อความขนาดและสีที่กำหนด จัดกึ่งกลาง
    // text ตัวช่วยสร้าง Text ในรูปแบบเดียวกัน (ขนาด/สีต่างกัน)
    private static Text text(String s, int size, Color color) {
        // สร้าง Text จากสตริง
        Text t = new Text(s);
        // ตั้งฟอนต์ Monospaced ตัวหนา
        t.setFont(Font.font("Monospaced", FontWeight.BOLD, size));
        // ตั้งสี
        t.setFill(color);
        // จัดข้อความให้อยู่กึ่งกลาง
        t.setTextAlignment(TextAlignment.CENTER);
        // คืน Text
        return t;
    }  // จบ text

    // แสดงคะแนนสูงสุดของรอบที่ผ่านมา   [เธรด] JavaFX Thread
    // setBestScore ตั้งข้อความคะแนนสูงสุด
    public void setBestScore(int best) {
        // ถ้าคะแนนสูงสุด > 0 แสดงข้อความ ไม่งั้นเว้นว่าง
        bestText.setText(best > 0 ? "BEST SCORE: " + best : "");
    }  // จบ setBestScore
}  // จบคลาส MenuView
