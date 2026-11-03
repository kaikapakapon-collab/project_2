// ประกาศแพ็กเกจ exception
package se233.arkanoid.exception;

/*
 * [เกณฑ์ตาราง] Exception handling (exception ของเกมเอง: ทุกที่ throw ชนิดนี้)
 * GameException = exception ของเกมเราเอง (custom exception)
 * เกณฑ์ข้อ Exception: "throw exception ชนิดเดียวกันทั้งหมด แล้วจัดการที่จุดเดียว"
 * → ทุกที่ในเกมที่มีปัญหา จะ throw GameException อย่างเดียว
 * → แล้วไปจัดการรวมที่ ExceptionHandler.handle() ที่เดียว
 *
 * extends RuntimeException (unchecked) → ไม่ต้องเขียน throws ทุกเมธอด
 * ErrorType บอกว่าพังเพราะอะไร เอาไว้แสดง/บันทึก log ให้อ่านง่าย
 */
// GameException สืบทอด RuntimeException จึงเป็น unchecked ไม่ต้องเขียน throws
public class GameException extends RuntimeException {

    // enum ErrorType ระบุชนิดข้อผิดพลาดที่เกมมี
    public enum ErrorType {
        // ชนิด: หาไฟล์รูปไม่เจอ
        ASSET_NOT_FOUND,   // หาไฟล์รูปไม่เจอ
        // ชนิด: เลขด่านหรือผังด่านผิด
        INVALID_LEVEL,     // เลขด่าน/ผังด่านผิด
        // ชนิด: เลือกอัปเกรดผิด
        INVALID_UPGRADE,   // เลือกอัปเกรดผิด
        // ชนิด: สถานะเกมผิดปกติ
        INVALID_STATE,     // สถานะเกมผิด เช่น ลดชีวิตตอนชีวิตเหลือ 0 แล้ว
        // ชนิด: เธรดถูกขัดจังหวะตอน sleep (ตัวสุดท้ายไม่มีเครื่องหมายจุลภาค)
        THREAD_INTERRUPTED // เธรดของเกม (GameLoop/DrawingLoop) ถูกสั่งขัดจังหวะตอนกำลัง sleep
    }  // จบ enum

    // เก็บชนิดข้อผิดพลาด (final = กำหนดได้ครั้งเดียวใน constructor)
    private final ErrorType type;

    // สร้าง exception จากชนิดข้อผิดพลาด + ข้อความอธิบาย
    // constructor แบบมีชนิด + ข้อความ
    public GameException(ErrorType type, String message) {
        // ส่งข้อความให้คลาสแม่ (RuntimeException) เก็บ
        super(message);
        // เก็บชนิดข้อผิดพลาดลงตัวแปรของ object
        this.type = type;
    }  // จบ constructor แรก

    // ใช้ตอนมี exception อื่นเป็นต้นเหตุ (cause) แล้วห่อเป็น GameException
    // constructor แบบมีต้นเหตุ (cause) ด้วย ใช้ห่อ exception อื่น
    public GameException(ErrorType type, String message, Throwable cause) {
        // ส่งข้อความและ cause ให้คลาสแม่เก็บ
        super(message, cause);
        // เก็บชนิดข้อผิดพลาด
        this.type = type;
    }  // จบ constructor ที่สอง

    // getter คืนชนิดข้อผิดพลาด
    public ErrorType getType() { return type; }  // คืนชนิดข้อผิดพลาด (ExceptionHandler เอาไปแสดงและ log)
}  // จบคลาส GameException
