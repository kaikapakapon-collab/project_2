// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า Image
import javafx.scene.image.Image;
// นำเข้า Launcher ใช้เป็นจุดอ้างอิงตำแหน่ง resource
import se233.arkanoid.Launcher;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;

// นำเข้า InputStream สตรีมอ่านไฟล์
import java.io.InputStream;
// นำเข้า HashMap
import java.util.HashMap;
// นำเข้า Map
import java.util.Map;

/*
 * [เกณฑ์ตาราง] Sprite sheet (โหลดรูป sheet), Exception handling (หารูปไม่เจอ → GameException)
 * AssetLoader = โหลดรูปจากโฟลเดอร์ resources/se233/arkanoid/assets
 * - ใช้ Launcher.class.getResourceAsStream(...) แบบเดียวกับชีท (Script 4.13)
 * - เก็บ cache ไว้ → รูปเดียวกันโหลดครั้งเดียว (บล็อก 60 ก้อนใช้รูปเดียวกัน ไม่ต้องโหลด 60 รอบ)
 * - หาไฟล์ไม่เจอ → throw GameException(ASSET_NOT_FOUND)
 *
 * [เธรด] ด่านใหม่ถูกสร้างจาก GameLoop Thread ส่วนตอนเริ่มเกมสร้างจาก JavaFX Thread
 * → load() เป็น static synchronized เพราะ HashMap ถูกเรียกจากหลายเธรดได้
 */
// คลาส final โหลดรูปจากโฟลเดอร์ assets
public final class AssetLoader {
    // cache เก็บรูปที่โหลดแล้ว (ชื่อไฟล์ → Image) จะได้ไม่ต้องโหลดซ้ำ
    private static final Map<String, Image> cache = new HashMap<>();

    // constructor private ห้ามสร้าง object
    private AssetLoader() { }  // ห้ามสร้าง object ใช้แบบ static อย่างเดียว

    // โหลดรูปชื่อ fileName (เช่น "ball.png") ถ้าเคยโหลดแล้วคืนจาก cache
    // static synchronized = ล็อกที่ตัวคลาส ทีละเธรดเท่านั้นที่เข้ามาแก้ cache ได้
    // load เป็น synchronized เพราะหลายเธรดอาจเรียกพร้อมกัน
    public static synchronized Image load(String fileName) {
        // ลองหยิบรูปจาก cache ก่อน
        Image image = cache.get(fileName);
        // ถ้ายังไม่เคยโหลด (null)
        if (image == null) {
            // เปิดไฟล์จาก resources โฟลเดอร์ assets/
            InputStream stream = Launcher.class.getResourceAsStream("assets/" + fileName);
            // ถ้าหาไฟล์ไม่เจอ
            if (stream == null) {
                // โยน GameException ชนิด ASSET_NOT_FOUND
                throw new GameException(GameException.ErrorType.ASSET_NOT_FOUND,
                        // พร้อมข้อความบอกชื่อไฟล์ที่หาไม่เจอ
                        "Cannot find image: assets/" + fileName);
            }  // จบ if ไม่เจอไฟล์
            // สร้าง Image จากสตรีม
            image = new Image(stream);
            // เก็บลง cache เพื่อใช้ครั้งต่อไป
            cache.put(fileName, image);
        }  // จบ if ยังไม่เคยโหลด
        // คืนรูปให้ผู้เรียก
        return image;
    }  // จบเมธอด load
}  // จบคลาส AssetLoader
