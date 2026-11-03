// ประกาศแพ็กเกจของโค้ดทดสอบ
package se233.arkanoid;

// นำเข้า Platform เพื่อเปิดระบบ JavaFX ตอนทดสอบ
import javafx.application.Platform;

/*
 * [เกณฑ์ตาราง] Testing (เตรียม JavaFX toolkit ตามบทที่ 5)
 * TestSupport = เตรียม JavaFX toolkit สำหรับ test ที่สร้าง object ที่เป็น Pane/ImageView (Ball, Brick, Paddle)
 * ตามบทที่ 5 (Script 5.23): Platform.startup(() -> {}) ต้องเรียกก่อนใช้ JavaFX
 * แต่เรียกซ้ำจะโยน IllegalStateException (toolkit เริ่มไปแล้ว) → จับไว้แล้วถือว่าพร้อมใช้
 */
// คลาสช่วยสำหรับ test (final = สืบทอดไม่ได้)
public final class TestSupport {
    // constructor private ห้ามสร้าง object
    private TestSupport() { }

    // initJfx เปิดระบบ JavaFX ให้ test ที่สร้าง Image/Pane ได้ (เรียกกี่ครั้งก็ได้)
    public static void initJfx() {
        // เริ่ม try
        try {
            // สั่งเริ่ม JavaFX Toolkit ด้วยงานว่างเปล่า
            Platform.startup(() -> { });
        // ถ้าเคยเริ่มไปแล้วจะเกิด IllegalStateException
        } catch (IllegalStateException alreadyStarted) {
            // toolkit เริ่มแล้วจาก test class อื่น ใช้ต่อได้เลย
        }  // จบ catch (ไม่ต้องทำอะไร เพราะเริ่มไปแล้วถือว่าใช้ได้)
    }  // จบ initJfx
}  // จบคลาส TestSupport
