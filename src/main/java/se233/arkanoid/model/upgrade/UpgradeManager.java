// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameException
import se233.arkanoid.exception.GameException;

// นำเข้า ArrayList
import java.util.ArrayList;
// นำเข้า List
import java.util.List;
// นำเข้า Random
import java.util.Random;

/*
 * [เกณฑ์ตาราง] Functionalities (สุ่มอัปเกรด 3 จาก 8), Exception handling (เลือกผิด → GameException), Testing (ควบคุม seed ได้)
 * UpgradeManager = เก็บอัปเกรดทั้งหมด แล้วสุ่มให้เลือกตอนผ่านด่าน (หัวใจของความเป็น roguelike)
 * รับ Random เข้ามาทาง constructor → ตอน unit test ใส่ seed ตายตัวได้ ผลสุ่มจะเหมือนเดิมทุกครั้ง
 */
// UpgradeManager ถือ "กองอัปเกรด" ทั้งหมดและสุ่มแจกการ์ด
public class UpgradeManager {
    // ลิสต์กองอัปเกรดทั้งหมด (8 แบบ)
    private final List<Upgrade> pool = new ArrayList<>();
    // ตัวสุ่ม (รับมาจากภายนอก เพื่อให้ test กำหนดค่าได้)
    private final Random random;

    // สร้างกองอัปเกรดทั้ง 8 แบบ รับ Random มาเพื่อให้ test ควบคุมผลสุ่มได้
    // constructor รับ Random
    public UpgradeManager(Random random) {
        // เก็บ Random
        this.random = random;
        // ใส่อัปเกรด: paddle กว้างขึ้น
        pool.add(new WidePaddleUpgrade());
        // ใส่อัปเกรด: paddle เร็วขึ้น
        pool.add(new FastPaddleUpgrade());
        // ใส่อัปเกรด: ชีวิต +1
        pool.add(new ExtraLifeUpgrade());
        // ใส่อัปเกรด: ลูกแตกเพิ่ม
        pool.add(new MultiBallUpgrade());
        // ใส่อัปเกรด: ลูกทะลุ
        pool.add(new PiercingBallUpgrade());
        // ใส่อัปเกรด: คะแนนเพิ่ม
        pool.add(new ScoreBoostUpgrade());
        // ใส่อัปเกรด: ลูกช้าลง
        pool.add(new SlowBallUpgrade());
        // ใส่อัปเกรด: เลเซอร์นานขึ้น
        pool.add(new LaserBoostUpgrade());
    }  // จบ constructor

    // สุ่มอัปเกรด count แบบที่ไม่ซ้ำกัน: ก๊อปกองไว้ → ทำซ้ำ count ครั้ง: สุ่มตำแหน่งด้วย random.nextInt แล้ว "ดึงออกจากกอง" ใส่ตะกร้า
    // (ดึงออกแล้วจึงสุ่มซ้ำใบเดิมไม่ได้) ขอเกินจำนวนที่มี → throw GameException
    // randomChoices สุ่มอัปเกรด count ใบโดยไม่ซ้ำกัน
    public List<Upgrade> randomChoices(int count) {
        // ถ้าจำนวนที่ขอน้อยกว่า 1 หรือมากกว่ากอง
        if (count < 1 || count > pool.size()) {
            // โยน GameException ชนิด INVALID_UPGRADE
            throw new GameException(GameException.ErrorType.INVALID_UPGRADE,
                    // พร้อมข้อความอธิบาย
                    "Cannot pick " + count + " upgrades from pool of " + pool.size());
        }  // จบ if
        // ทำสำเนากองไว้สุ่มดึง (ไม่แตะกองจริง)
        List<Upgrade> copy = new ArrayList<>(pool);
        // ลิสต์เก็บผลลัพธ์
        List<Upgrade> choices = new ArrayList<>();
        // วนตามจำนวนใบที่ต้องการ
        for (int i = 0; i < count; i++) {
            // สุ่มตำแหน่งในกองที่เหลือ
            int pos = random.nextInt(copy.size()); // สุ่มตำแหน่ง 0 .. ขนาดกองที่เหลือ-1
            // ดึงใบนั้นออกจากสำเนาและเพิ่มเข้าผลลัพธ์ (จึงไม่ซ้ำ)
            choices.add(copy.remove(pos));         // remove(index) คืนตัวที่ดึงออกมา
        }  // จบลูป
        // คืนผลลัพธ์
        return choices;
    }  // จบ randomChoices

    // หยิบอัปเกรดตามลำดับที่ผู้เล่นเลือก (0, 1, 2) ถ้าเลือกนอกช่วง → throw GameException
    // select (static) หยิบใบที่ผู้เล่นเลือกจากตัวเลือก
    public static Upgrade select(List<Upgrade> choices, int index) {
        // ถ้าตัวเลือกเป็น null หรือเลขอยู่นอกช่วง
        if (choices == null || index < 0 || index >= choices.size()) {
            // โยน GameException ชนิด INVALID_UPGRADE
            throw new GameException(GameException.ErrorType.INVALID_UPGRADE,
                    // พร้อมข้อความบอกเลขที่ผิด
                    "Invalid upgrade choice: " + index);
        }  // จบ if
        // คืนอัปเกรดตามเลขที่เลือก
        return choices.get(index);
    }  // จบ select

    // คืนจำนวนอัปเกรดทั้งหมดในกอง
    public int getPoolSize() { return pool.size(); }  // จำนวนอัปเกรดทั้งหมดในกอง
}  // จบคลาส UpgradeManager
