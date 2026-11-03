// ประกาศแพ็กเกจ exception
package se233.arkanoid.exception;

// นำเข้า Platform เพื่อใช้ runLater ฝากงานให้ JavaFX Thread
import javafx.application.Platform;
// นำเข้า Alert หน้าต่างแจ้งเตือน
import javafx.scene.control.Alert;
// นำเข้า LogManager
import org.apache.logging.log4j.LogManager;
// นำเข้า Logger
import org.apache.logging.log4j.Logger;

/*
 * [เกณฑ์ตาราง] Exception handling (จุดเดียวที่จัดการ GameException ทั้งเกม), Logging (log ระดับ ERROR)
 * ExceptionHandler = "จุดเดียว" ที่จัดการ GameException ทั้งหมดของเกม
 * ทุก catch (GameException e) ในโปรเจกต์ จะเรียก ExceptionHandler.handle(e) อย่างเดียว
 * หน้าที่: 1) บันทึก log ระดับ ERROR  2) เด้งหน้าต่างแจ้งผู้เล่น
 */
// คลาส final (สืบทอดต่อไม่ได้) ทำหน้าที่จัดการ exception ทั้งเกม
public final class ExceptionHandler {
    // logger ประจำคลาส (static final = ใช้ร่วมกันทั้งโปรแกรม)
    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);

    // constructor แบบ private กันไม่ให้ใครสร้าง object จากคลาสนี้
    private ExceptionHandler() { } // ห้ามสร้าง object ใช้แบบ static อย่างเดียว

    // จัดการ GameException: log + แจ้งเตือน
    // [เธรด] ถูกเรียกได้จากทุกเธรด → Alert ต้องสร้างบน JavaFX Thread จึงห่อด้วย Platform.runLater
    // เมธอด static handle รับ GameException ที่ต้องการจัดการ
    public static void handle(GameException e) {
        // เขียน log ระดับ ERROR ใส่ชนิดและข้อความ พร้อมแนบ stack trace (ตัวแปร e ท้ายสุด)
        logger.error("[{}] {}", e.getType(), e.getMessage(), e);
        // ฝากงานสร้าง Alert ไปให้ JavaFX Thread ทำ (สร้าง UI ต้องอยู่เธรดนี้)
        Platform.runLater(() -> {
            // สร้าง Alert ชนิด ERROR
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // ตั้งชื่อหัวหน้าต่างว่า Game Error
            alert.setTitle("Game Error");
            // ตั้งหัวเรื่องเป็นชื่อชนิดข้อผิดพลาด
            alert.setHeaderText(e.getType().toString());
            // ตั้งเนื้อหาเป็นข้อความของ exception
            alert.setContentText(e.getMessage());
            // แสดง Alert แบบไม่บล็อกเกม (show ไม่ใช่ showAndWait)
            alert.show(); // show() ไม่ใช่ showAndWait() → ไม่บล็อกเกม
        });  // จบ lambda ที่ส่งให้ runLater
    }  // จบเมธอด handle
}  // จบคลาส ExceptionHandler
