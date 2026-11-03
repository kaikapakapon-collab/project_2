// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า KeyCode รหัสปุ่มของ JavaFX
import javafx.scene.input.KeyCode;

// นำเข้า HashMap
import java.util.HashMap;

/*
 * [เกณฑ์ตาราง] Functionalities (รับปุ่มคีย์บอร์ด), Build/Concurrency (synchronized ข้ามเธรด)
 * Keys = ตัวจำว่าตอนนี้ปุ่มไหน "ถูกกดค้างอยู่" (Script 4.19 ของจาร ใช้ HashMap เหมือนเดิม)
 *
 * [เธรด] มี 2 เธรดใช้พร้อมกัน:
 * - JavaFX Thread   → เรียก add()/remove() ตอนผู้เล่นกด/ปล่อยปุ่ม
 * - GameLoop Thread → เรียก isPressed()/consume() ทุกเฟรม
 * HashMap ไม่ปลอดภัยเมื่อหลายเธรดใช้พร้อมกัน → ใส่ synchronized ทุกเมธอด (หัวข้อ 3.1.2 Synchronization)
 * synchronized = เธรดที่เข้าเมธอดจะถือ "ล็อก" ของ object นี้ เธรดอื่นต้องรอจนกว่าจะออก
 * และเมื่อออกจากเมธอด ค่าที่แก้จะถูกทำให้เธรดอื่นมองเห็น (ตามที่ชีทอธิบาย)
 */
// Keys จำว่าปุ่มไหนกดอยู่ (JavaFX Thread เขียน, GameLoop Thread อ่าน)
public class Keys {
    // แผนที่ ปุ่ม → true (กดอยู่) / false (ปล่อยแล้ว)
    private final HashMap<KeyCode, Boolean> keys; // ปุ่ม → true = กดอยู่, false = ปล่อยแล้ว

    // สร้างแผนที่ปุ่ม (ยังไม่มีปุ่มไหนถูกกด)
    // constructor
    public Keys() {
        // สร้าง HashMap เปล่า
        keys = new HashMap<>();
    }  // จบ constructor

    // เรียกตอนกดปุ่ม: จำว่าปุ่มนี้กดอยู่
    // add ถูกเรียกตอนกดปุ่ม เป็น synchronized กันสองเธรดชนกัน
    public synchronized void add(KeyCode key) {
        // บันทึกว่าปุ่มนี้กดอยู่
        keys.put(key, true);
    }  // จบ add

    // เรียกตอนปล่อยปุ่ม: จำว่าปุ่มนี้ไม่ได้กดแล้ว
    // remove ถูกเรียกตอนปล่อยปุ่ม
    public synchronized void remove(KeyCode key) {
        // บันทึกว่าปุ่มนี้ปล่อยแล้ว
        keys.put(key, false);
    }  // จบ remove

    // ถามว่าปุ่มนี้กดอยู่ไหม (ใช้กับปุ่มที่กดค้างได้ เช่น ลูกศรซ้าย/ขวา)
    // isPressed ถามว่าปุ่มนี้กดค้างอยู่ไหม
    public synchronized boolean isPressed(KeyCode key) {
        // คืนค่าที่จำไว้ ถ้าไม่เคยมีให้เป็น false
        return keys.getOrDefault(key, false);
    }  // จบ isPressed

    // ถามแล้ว "ใช้ปุ่มนี้ทิ้ง": ถ้ากดอยู่ คืน true แล้วตั้งเป็น false ทันที
    // ใช้กับปุ่มที่ต้องทำงานครั้งเดียวต่อการกด 1 ครั้ง เช่น ENTER, P, 1/2/3
    // (ถ้าใช้ isPressed กับปุ่ม P กดค้างนิดเดียว เกมจะ pause/unpause สลับรัว ๆ 60 ครั้งต่อวินาที)
    // อ่านแล้วเขียนทั้งก้อนอยู่ใน synchronized เดียว → ไม่มีเธรดอื่นแทรกกลางได้
    // consume อ่านแล้วล้าง (ใช้กับปุ่มที่ต้องการ 1 ครั้งต่อการกด เช่น ENTER, P)
    public synchronized boolean consume(KeyCode key) {
        // ถ้าปุ่มถูกกดอยู่
        if (keys.getOrDefault(key, false)) {
            // ล้างสถานะเป็นปล่อยแล้ว กันนับซ้ำ
            keys.put(key, false);
            // คืน true แปลว่ากดจริงและถูกใช้ไปแล้ว
            return true;
        }  // จบ if
        // ไม่ได้กดอยู่ คืน false
        return false;
    }  // จบ consume
}  // จบคลาส Keys
