// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Pos
import javafx.geometry.Pos;
// นำเข้า HBox กล่องเรียงแนวนอน
import javafx.scene.layout.HBox;
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
// นำเข้า Upgrade เพื่ออ่านชื่อ/คำอธิบายการ์ด
import se233.arkanoid.model.upgrade.Upgrade;

// นำเข้า List
import java.util.List;
// นำเข้า IntConsumer (ฟังก์ชันที่รับเลข int ไม่คืนค่า) ใช้เป็น callback ตอนคลิกการ์ด
import java.util.function.IntConsumer;

/*
 * [เกณฑ์ตาราง] Functionalities (roguelike: หน้าเลือกอัปเกรด)
 * UpgradeView = หน้าจอเลือกอัปเกรดหลังผ่านด่าน (Use case 3.3)
 * แสดงการ์ด 3 ใบ เลือกได้ 2 วิธี: กดปุ่ม 1/2/3 (GameLoop อ่านเอง) หรือคลิกการ์ด
 * คลิกการ์ด → เรียก onSelect (GameLoop.requestUpgrade) ส่งเลขการ์ดที่เลือกไป
 */
// UpgradeView หน้าเลือกอัปเกรด 3 ใบ สืบทอดจาก VBox
public class UpgradeView extends VBox {
    // สไตล์ CSS ของการ์ดปกติ (พื้นน้ำเงินเข้ม ขอบฟ้า)
    private static final String CARD_STYLE =
            // ค่า CSS: สีพื้น ขอบ ความหนาขอบ และระยะขอบใน
            "-fx-background-color: #1e1e3f; -fx-border-color: #3fa9f5; -fx-border-width: 2; -fx-padding: 12;";
    // สไตล์ CSS ตอนเมาส์อยู่บนการ์ด (พื้นสว่างขึ้น ขอบเหลือง)
    private static final String CARD_HOVER_STYLE =
            // ค่า CSS ของสไตล์ hover
            "-fx-background-color: #2e2e6f; -fx-border-color: #ffd93d; -fx-border-width: 3; -fx-padding: 12;";

    // ข้อความหัวข้อ
    private final Text header;
    // แถวนอนที่เก็บการ์ด
    private final HBox cards;
    // callback ตอนคลิกการ์ด (ค่าเริ่มต้นคือไม่ทำอะไร)
    private IntConsumer onSelect = i -> { }; // ค่าเริ่มต้น = ไม่ทำอะไร

    // สร้างหน้าเลือกอัปเกรด: หัวข้อ + แถวการ์ด เริ่มต้นซ่อนอยู่
    // constructor ประกอบหน้า
    public UpgradeView() {
        // ตั้งขนาดเต็มจอ
        setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);
        // จัดกึ่งกลาง
        setAlignment(Pos.CENTER);
        // ระยะห่าง 24 px
        setSpacing(24);
        // ตั้งพื้นหลังดำโปร่งใส
        setStyle("-fx-background-color: rgba(0,0,0,0.8);");
        // สร้างข้อความหัวข้อ
        header = new Text();
        // ตั้งฟอนต์ตัวหนาขนาด 26
        header.setFont(Font.font("Monospaced", FontWeight.BOLD, 26));
        // ตั้งสีเหลือง
        header.setFill(Color.web("#ffd93d"));
        // จัดกึ่งกลาง
        header.setTextAlignment(TextAlignment.CENTER);
        // สร้าง HBox เก็บการ์ด ระยะห่าง 14
        cards = new HBox(14);
        // จัดการ์ดให้อยู่กึ่งกลาง
        cards.setAlignment(Pos.CENTER);
        // ใส่หัวข้อและแถวการ์ดลงในหน้า
        getChildren().addAll(header, cards);
        // เริ่มต้นซ่อนไว้
        setVisible(false);
    }  // จบ constructor

    // ตั้งว่าคลิกการ์ดแล้วให้ทำอะไร (Launcher ผูกกับ gameLoop::requestUpgrade)
    // setOnSelect ตั้งฟังก์ชันที่จะเรียกเมื่อผู้เล่นเลือกการ์ด
    public void setOnSelect(IntConsumer onSelect) {
        // เก็บ callback ไว้ในตัวแปร
        this.onSelect = onSelect;
    }  // จบ setOnSelect

    // สร้างการ์ดใหม่ตามอัปเกรดที่สุ่มได้ แล้วแสดง   [เธรด] JavaFX Thread
    // show แสดงหน้าเลือกอัปเกรด รับด่านที่เพิ่งเคลียร์และรายการตัวเลือก
    public void show(int clearedStage, List<Upgrade> choices) {
        // ตั้งข้อความหัวข้อ "STAGE x CLEAR!" พร้อมบรรทัดชวนเลือก
        header.setText("STAGE " + clearedStage + " CLEAR!\nChoose an upgrade");
        // ล้างการ์ดเก่าออก
        cards.getChildren().clear();
        // วนทุกตัวเลือก
        for (int i = 0; i < choices.size(); i++) {
            // สร้างการ์ดของตัวเลือกนั้นแล้วใส่ลงแถว
            cards.getChildren().add(createCard(i, choices.get(i)));
        }  // จบลูป
        // ทำให้หน้ามองเห็น
        setVisible(true);
    }  // จบ show

    // การ์ด 1 ใบ = VBox [ปุ่มลัด] + ชื่อ + คำอธิบาย, ชี้เมาส์แล้วเปลี่ยนสี
    // createCard สร้างการ์ดหนึ่งใบจากอัปเกรด
    private VBox createCard(int index, Upgrade upgrade) {
        // ข้อความหมายเลขปุ่ม เช่น [1]
        Text key = new Text("[" + (index + 1) + "]");
        // ตั้งฟอนต์ตัวหนาขนาด 18
        key.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        // ตั้งสีฟ้า
        key.setFill(Color.web("#3fa9f5"));
        // ข้อความชื่ออัปเกรด
        Text name = new Text(upgrade.getName());
        // ตั้งฟอนต์ตัวหนาขนาด 16
        name.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        // ตั้งสีขาว
        name.setFill(Color.WHITE);
        // ข้อความคำอธิบายอัปเกรด
        Text desc = new Text(upgrade.getDescription());
        // ตั้งฟอนต์ขนาด 12
        desc.setFont(Font.font("Monospaced", 12));
        // ตั้งสีเทาอ่อน
        desc.setFill(Color.LIGHTGRAY);
        // ตัดบรรทัดอัตโนมัติที่ความกว้าง 150 px
        desc.setWrappingWidth(150);
        // จัดข้อความกึ่งกลาง
        desc.setTextAlignment(TextAlignment.CENTER);

        // สร้างการ์ด (VBox) ใส่ข้อความทั้งสามเรียงลงมา ระยะห่าง 10
        VBox card = new VBox(10, key, name, desc);
        // จัดเนื้อหาชิดบนและกึ่งกลาง
        card.setAlignment(Pos.TOP_CENTER);
        // ตั้งขนาดการ์ด 175×170
        card.setPrefSize(175, 170);
        // ตั้งสไตล์ปกติ
        card.setStyle(CARD_STYLE);
        // เมื่อเมาส์เข้า เปลี่ยนเป็นสไตล์ hover
        card.setOnMouseEntered(e -> card.setStyle(CARD_HOVER_STYLE));
        // เมื่อเมาส์ออก กลับเป็นสไตล์ปกติ
        card.setOnMouseExited(e -> card.setStyle(CARD_STYLE));
        // เมื่อคลิก เรียก callback พร้อมเลขการ์ด
        card.setOnMouseClicked(e -> onSelect.accept(index));
        // คืนการ์ด
        return card;
    }  // จบ createCard

    // ซ่อนหน้าเลือกอัปเกรด
    // hide ซ่อนหน้านี้
    public void hide() {
        // ตั้งให้มองไม่เห็น
        setVisible(false);
    }  // จบ hide
}  // จบคลาส UpgradeView
