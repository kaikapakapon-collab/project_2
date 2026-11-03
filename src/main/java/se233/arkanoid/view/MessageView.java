// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Pos
import javafx.geometry.Pos;
// นำเข้า VBox
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
 * [เกณฑ์ตาราง] Functionalities (feedback: ผ่านด่าน/เสียชีวิต/จบเกม/หยุดเกม)
 * MessageView = ข้อความใหญ่กลางจอ: "STAGE 2", "LIFE LOST", "GAME OVER", "YOU WIN!", "PAUSED"
 * → เกณฑ์ feedback: แจ้งตอนผ่านด่าน / เลือกอัปเกรด / เสียชีวิต / จบเกม
 * การนับเวลาเพื่อซ่อนข้อความทำใน GameLoop (นับเฟรม) ไม่ใช่ที่นี่ → คลาสนี้เป็นแค่ "ภาพ"
 */
// MessageView ข้อความกลางจอ เช่น PAUSED / GAME OVER / YOU WIN
public class MessageView extends VBox {
    // ข้อความหัวข้อใหญ่
    private final Text title;
    // ข้อความบรรทัดรอง
    private final Text subtitle;

    // สร้างข้อความใหญ่ (หัวข้อ + บรรทัดรอง) กลางจอ เริ่มต้นซ่อนอยู่
    // constructor ประกอบ view
    public MessageView() {
        // ตั้งขนาดให้เต็มจอ
        setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);
        // จัดกึ่งกลาง
        setAlignment(Pos.CENTER);
        // ระยะห่าง 10 px
        setSpacing(10);
        // ไม่รับเมาส์ เพื่อไม่บังการคลิก
        setMouseTransparent(true); // ไม่บังการคลิก
        // สร้างข้อความหัวข้อ
        title = new Text();
        // ตั้งฟอนต์ตัวหนาขนาด 46
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 46));
        // ตั้งสีเริ่มต้นเป็นขาว
        title.setFill(Color.WHITE);
        // ตั้งสีขอบเป็นดำ
        title.setStroke(Color.BLACK);
        // ตั้งความหนาขอบ
        title.setStrokeWidth(1.5);
        // สร้างข้อความรอง
        subtitle = new Text();
        // ตั้งฟอนต์ตัวหนาขนาด 18
        subtitle.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        // ตั้งสีเหลือง
        subtitle.setFill(Color.web("#ffd93d"));
        // จัดข้อความกึ่งกลาง
        subtitle.setTextAlignment(TextAlignment.CENTER);
        // ใส่ทั้งสองข้อความลงใน VBox
        getChildren().addAll(title, subtitle);
        // เริ่มต้นซ่อนไว้
        setVisible(false);
    }  // จบ constructor

    // โชว์ข้อความ (ค้างไว้จนกว่าจะมีคนสั่ง hide())   [เธรด] JavaFX Thread
    // show แสดงข้อความ รับหัวข้อ ข้อความรอง และสีหัวข้อ
    public void show(String titleText, String subtitleText, Color color) {
        // ตั้งข้อความหัวข้อ
        title.setText(titleText);
        // ตั้งสีหัวข้อ
        title.setFill(color);
        // ตั้งข้อความรอง
        subtitle.setText(subtitleText);
        // ทำให้มองเห็น
        setVisible(true);
    }  // จบ show

    // ซ่อนข้อความ
    // hide ซ่อนข้อความ
    public void hide() {
        // ตั้งให้มองไม่เห็น
        setVisible(false);
    }  // จบ hide
}  // จบคลาส MessageView
