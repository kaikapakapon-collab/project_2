// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า Rectangle2D ใช้กำหนดช่องที่จะตัดจาก sprite sheet
import javafx.geometry.Rectangle2D;
// นำเข้า Image รูปภาพ
import javafx.scene.image.Image;
// นำเข้า ImageView ตัวแสดงรูปที่เราสืบทอด
import javafx.scene.image.ImageView;

/*
 * [เกณฑ์ตาราง] Sprite sheet (เล่นเฟรมจาก sheet + สลับแถวอนิเมชัน)
 * AnimatedSprite = ตัวเล่นภาพเคลื่อนไหวจาก sprite sheet (Script 4.32 ของจาร)
 * sprite sheet = รูปใหญ่ 1 รูปที่มีหลายเฟรมเรียงเป็นตาราง
 * viewport = "หน้าต่าง" ที่เลือกโชว์แค่ 1 เฟรม → เลื่อนหน้าต่างไปเรื่อย ๆ = ภาพขยับ
 *
 * ที่เพิ่มจากชีท: changeAnimation() สลับไปเล่นแถวอื่นของ sheet
 * เช่น ลูกบอลแถว 0 = กลิ้ง, แถว 1 = เด้ง (squash), แถว 2 = ลูกไฟ
 * → ทำให้ "ท่าเคลื่อนที่" กับ "ท่าเด้ง" แยกกัน ตามเกณฑ์ Sprite sheet
 */
// AnimatedSprite ต่อยอด ImageView เพื่อเล่นอนิเมชันจาก sprite sheet
public class AnimatedSprite extends ImageView {
    // ตัวแปรเก็บข้อมูลชีต: จำนวนเฟรม, คอลัมน์, แถว, ตำแหน่งเริ่ม, ขนาดเฟรม และดัชนีเฟรมปัจจุบัน
    int count, columns, rows, offsetX, offsetY, width, height, curIndex, curColumnIndex = 0, curRowIndex = 0;

    // count = จำนวนเฟรม, columns/rows = ขนาดตารางเฟรม
    // offsetX/offsetY = มุมซ้ายบนของเฟรมแรก (เลือกว่าจะเล่นแถวไหน), width/height = ขนาด 1 เฟรม
    // constructor รับรูป sprite sheet และค่ากำหนดการตัดเฟรม
    public AnimatedSprite(Image image, int count, int columns, int rows, int offsetX, int offsetY, int width, int height) {
        // ตั้งรูปให้ ImageView
        this.setImage(image);
        // เก็บจำนวนเฟรมทั้งหมด
        this.count = count;
        // เก็บจำนวนคอลัมน์
        this.columns = columns;
        // เก็บจำนวนแถว
        this.rows = rows;
        // เก็บตำแหน่งเริ่ม X ของเฟรมแรก
        this.offsetX = offsetX;
        // เก็บตำแหน่งเริ่ม Y
        this.offsetY = offsetY;
        // เก็บความกว้างต่อเฟรม
        this.width = width;
        // เก็บความสูงต่อเฟรม
        this.height = height;
        // ตั้ง viewport (กรอบตัดภาพ) ให้โชว์เฟรมแรก
        this.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
    }  // จบ constructor

    // ไปเฟรมถัดไป (Script 4.11): curIndex 0,1,2,3 → แปลงเป็นคอลัมน์/แถว แล้ววนกลับ 0
    // [เธรด] DrawingLoop เรียกบน JavaFX Thread เท่านั้น เพราะเปลี่ยน viewport = เปลี่ยนภาพ
    // tick เลื่อนไปเฟรมถัดไป
    public void tick() {
        // หาคอลัมน์ของเฟรมปัจจุบัน (ดัชนี mod จำนวนคอลัมน์)
        curColumnIndex = curIndex % columns;
        // หาแถวของเฟรมปัจจุบัน (ดัชนี หาร จำนวนคอลัมน์)
        curRowIndex = curIndex / columns;
        // เพิ่มดัชนีเฟรมและวนกลับเป็น 0 เมื่อครบทุกช่อง
        curIndex = (curIndex + 1) % (columns * rows);
        // อัปเดตกรอบตัดภาพ
        interpolate();
    }  // จบ tick

    // เลื่อน viewport ไปที่เฟรมปัจจุบัน: x = คอลัมน์ * ความกว้างเฟรม + offset
    // interpolate คำนวณกรอบตัดภาพจากคอลัมน์/แถวปัจจุบัน
    protected void interpolate() {
        // ตำแหน่ง x ในชีต = คอลัมน์ × กว้างเฟรม + จุดเริ่ม
        final int x = curColumnIndex * width + offsetX;
        // ตำแหน่ง y ในชีต = แถว × สูงเฟรม + จุดเริ่ม
        final int y = curRowIndex * height + offsetY;
        // ตั้ง viewport ใหม่เป็นเฟรมนั้น
        this.setViewport(new Rectangle2D(x, y, width, height));
    }  // จบ interpolate

    // สลับไปเล่นอนิเมชันชุดอื่น (อีกแถวของ sheet) แล้วเริ่มที่เฟรมแรก
    // ถ้าเป็นชุดเดิมอยู่แล้วไม่ต้องทำอะไร (กันอนิเมชันรีเซ็ตทุกเฟรม)
    // changeAnimation สลับไปเล่นอนิเมชันชุดอื่นของชีต (เปลี่ยนจุดเริ่ม)
    public void changeAnimation(int newOffsetX, int newOffsetY) {
        // ถ้าเป็นชุดเดิมอยู่แล้วไม่ต้องทำอะไร (ไม่รีเซ็ตเฟรม)
        if (newOffsetX == offsetX && newOffsetY == offsetY) return;
        // เก็บจุดเริ่ม X ใหม่
        offsetX = newOffsetX;
        // เก็บจุดเริ่ม Y ใหม่
        offsetY = newOffsetY;
        // รีเซ็ตดัชนีเฟรมเป็น 0
        curIndex = 0;
        // รีเซ็ตคอลัมน์เป็น 0
        curColumnIndex = 0;
        // รีเซ็ตแถวเป็น 0
        curRowIndex = 0;
        // อัปเดตภาพทันที
        interpolate();
    }  // จบ changeAnimation

    // แสดงเฟรมที่ index ตรง ๆ (ใช้กับบล็อกที่เลือกรูปตาม HP ไม่ได้วนเล่น)
    // showFrame แสดงเฟรมที่ระบุตรง ๆ (ใช้กับบล็อกที่เปลี่ยนรูปตาม HP)
    public void showFrame(int index) {
        // คำนวณคอลัมน์จากดัชนี
        curColumnIndex = index % columns;
        // คำนวณแถวจากดัชนี
        curRowIndex = index / columns;
        // อัปเดตภาพ
        interpolate();
    }  // จบ showFrame

    // getter คืนแถว (offsetY) ของชีตที่กำลังเล่นอยู่
    public int getOffsetY() { return offsetY; }  // คืนแถวของ sheet ที่กำลังเล่นอยู่
}  // จบคลาส AnimatedSprite
