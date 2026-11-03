// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า Pane
import javafx.scene.layout.Pane;
// นำเข้า Color
import javafx.scene.paint.Color;
// นำเข้า Rectangle
import javafx.scene.shape.Rectangle;
// นำเข้า Font
import javafx.scene.text.Font;
// นำเข้า FontWeight
import javafx.scene.text.FontWeight;
// นำเข้า Text
import javafx.scene.text.Text;
// นำเข้า GameState เพื่ออ่านคะแนน/ชีวิต
import se233.arkanoid.model.GameState;
// นำเข้า Level เพื่ออ่านจำนวนด่านทั้งหมด
import se233.arkanoid.model.Level;

/*
 * [เกณฑ์ตาราง] Display score (แถบคะแนน/ด่าน/ชีวิต/คอมโบ/เลเซอร์)
 * Hud = แถบด้านบนจอ แสดง คะแนน / ด่าน / ชีวิต / คอมโบ / เวลาเลเซอร์ที่เหลือ
 * [เธรด] update() ถูกเรียกใน DrawingLoop ผ่าน Platform.runLater (JavaFX Thread)
 */
// Hud แถบข้อมูลด้านบนจอ (คะแนน ด่าน ชีวิต คอมโบ เลเซอร์)
public class Hud extends Pane {
    // ข้อความคะแนน (สีขาว)
    private final Text scoreText = makeText(Color.WHITE);
    // ข้อความด่าน (สีเทาอ่อน)
    private final Text stageText = makeText(Color.LIGHTGRAY);
    // ข้อความชีวิต (สีแดงอ่อน)
    private final Text livesText = makeText(Color.web("#ff6b6b"));
    // ข้อความคอมโบ (สีเหลือง)
    private final Text comboText = makeText(Color.web("#ffd93d"));
    // ข้อความเวลาเลเซอร์ (สีส้ม)
    private final Text laserText = makeText(Color.web("#ff9f43"));

    // สร้างแถบด้านบน: พื้นดำโปร่ง + ข้อความ 5 ช่อง (คะแนน, ด่าน, ชีวิต, คอมโบ, เวลาเลเซอร์) แล้ววางตำแหน่งแต่ละช่อง
    // constructor ประกอบ HUD
    public Hud() {
        // สร้างแถบพื้นหลังสีดำโปร่งใส กว้างเท่าจอ สูงเท่าพื้นที่ HUD
        Rectangle bar = new Rectangle(GameStage.WIDTH, GameStage.TOP, Color.web("#000000", 0.75));
        // วางตำแหน่งข้อความแต่ละอันในแถบ (x, y = เส้นฐานตัวอักษร)
        // วางข้อความคะแนนที่ซ้ายสุด
        place(scoreText, 10, 26);
        // วางข้อความด่าน
        place(stageText, 190, 26);
        // วางข้อความชีวิต
        place(livesText, 320, 26);
        // วางข้อความคอมโบ (แถวบน)
        place(comboText, 420, 18);
        // วางข้อความเลเซอร์ (แถวล่าง)
        place(laserText, 420, 34);
        // ใส่แถบและข้อความทั้งหมดลงใน Pane
        getChildren().addAll(bar, scoreText, stageText, livesText, comboText, laserText);
    }  // จบ constructor

    // สร้างข้อความสไตล์เดียวกันทั้งแถบ (ตัวหนา สีตามที่ส่งมา)
    // makeText สร้าง Text สีที่กำหนดด้วยฟอนต์มาตรฐานของ HUD
    private static Text makeText(Color color) {
        // สร้าง Text เปล่า
        Text t = new Text();
        // ตั้งฟอนต์ Monospaced ตัวหนา ขนาด 16
        t.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        // ตั้งสี
        t.setFill(color);
        // คืน Text
        return t;
    }  // จบ makeText

    // กำหนดตำแหน่ง (x, y) ของข้อความ
    // place วางข้อความที่ตำแหน่ง x,y
    private static void place(Text t, double x, double y) {
        // ตั้ง X
        t.setX(x);
        // ตั้ง Y
        t.setY(y);
    }  // จบ place

    // อัปเดตตัวเลขทั้งหมดจาก GameState
    // laserSecondsLeft > 0 = กำลังมีเลเซอร์ / combo >= 2 ถึงจะโชว์คอมโบ
    // update อัปเดตข้อความทั้งหมดตามสถานะล่าสุด (เรียกบน JavaFX Thread)
    public void update(GameState state, int laserSecondsLeft) {
        // แสดงคะแนนเติมศูนย์ให้ครบ 6 หลัก
        scoreText.setText(String.format("SCORE %06d", state.getScore()));
        // แสดงด่านปัจจุบัน/ด่านทั้งหมด
        stageText.setText("STAGE " + state.getStage() + "/" + Level.TOTAL_STAGES);
        // แสดงชีวิตที่เหลือ
        livesText.setText("LIVES " + state.getLives());
        // อ่านค่าคอมโบมาเก็บไว้
        int combo = state.getCombo();
        // ถ้าคอมโบ ≥ 2 แสดงคอมโบพร้อมตัวคูณ ไม่งั้นเป็นข้อความว่าง
        comboText.setText(combo >= 2 ? String.format("COMBO %d (x%.1f)", combo, state.getComboMultiplier()) : "");
        // ถ้ามีเวลาเลเซอร์เหลือ แสดงวินาทีที่เหลือ ไม่งั้นเป็นข้อความว่าง
        laserText.setText(laserSecondsLeft > 0 ? "LASER " + laserSecondsLeft + "s" : "");
    }  // จบ update
}  // จบคลาส Hud
