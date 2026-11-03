// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า GameStage เพื่อใช้ความกว้างและเส้นบนของจอ
import se233.arkanoid.view.GameStage;

// นำเข้า ArrayList
import java.util.ArrayList;
// นำเข้า List
import java.util.List;

/*
 * [เกณฑ์ตาราง] Functionalities (หลายด่าน: 5 ผังบล็อก), Exception handling (ผังด่านผิด → GameException)
 * Level = ผังบล็อกของแต่ละด่าน เขียนเป็นตัวอักษร อ่านง่ายแก้ง่าย
 *   '.' = ว่าง, '1' = ธรรมดา, '2' = แข็ง, '3' = แข็งมาก,
 *   'X' = ระเบิด, 'B' = โบนัส (ดรอปไอเทม), 'U' = ตีไม่แตก
 * 1 แถวต้องมี 10 ตัวอักษรพอดี (10 คอลัมน์)
 */
// Level สร้างบล็อกของแต่ละด่านจากผังตัวอักษร (final = สืบทอดไม่ได้)
public final class Level {
    // ผังมี 10 คอลัมน์
    public static final int COLUMNS = 10;
    // ช่องว่างระหว่างบล็อก 4 px
    private static final double GAP = 4;

    // LAYOUTS คืออาร์เรย์ 2 มิติของผังทุกด่าน: แต่ละด่านเป็นอาร์เรย์ของแถว แต่ละแถวเป็นสตริง
    private static final String[][] LAYOUTS = {
        // ด่าน 1: อุ่นเครื่อง (เริ่มอาร์เรย์ของด่าน 1)
        { // ด่าน 1: อุ่นเครื่อง
            // แถว 1: ส้ม (STRONG) 10 ก้อน
            "2222222222",
            // แถว 2: ฟ้า (NORMAL) 10 ก้อน
            "1111111111",
            // แถว 3: ฟ้า มีบล็อกโบนัส B 1 ก้อน
            "1111B11111",
            // แถว 4: ฟ้าล้วน
            "1111111111",
            // แถว 5: ฟ้า มีบล็อกโบนัส B 1 ก้อน
            "11111B1111",
        },
        // ด่าน 2: พีระมิด + บล็อกระเบิด (จุด '.' = ช่องว่าง)
        { // ด่าน 2: พีระมิด + บล็อกระเบิด
            // แถว 1: ยอดพีระมิด
            "....22....",
            // แถว 2
            "...2112...",
            // แถว 3: มีบล็อกระเบิด X สองก้อน
            "..21XX12..",
            // แถว 4: มีบล็อกโบนัส B สองก้อน
            ".211BB112.",
            // แถว 5
            "2111111112",
            // แถว 6: ฐานพีระมิด
            "1111111111",
        },
        // ด่าน 3: กำแพงทอง (U = ตีไม่แตก)
        { // ด่าน 3: กำแพงทอง
            // แถว 1: เทา (HARD) ทั้งแถว
            "3333333333",
            // แถว 2: ส้ม มีบล็อกระเบิด X ใกล้ปลายสองข้าง
            "2X222222X2",
            // แถว 3: ฟ้าล้วน
            "1111111111",
            // แถว 4: กำแพงทองสองข้างและโบนัสตรงกลาง
            "UU11BB11UU",
            // แถว 5: ฟ้าล้วน
            "1111111111",
            // แถว 6: ทองกระจายสองก้อน
            "11U1111U11",
        },
        // ด่าน 4: ลายหมากรุก ระเบิดต่อกันเป็นโซ่
        { // ด่าน 4: ลายหมากรุก ระเบิดต่อกันเป็นโซ่
            // แถว 1: เทาสลับช่องว่าง
            "3.3.3.3.3.",
            // แถว 2: ส้มสลับช่องว่าง
            ".2.2.2.2.2",
            // แถว 3: ระเบิดสลับฟ้า (ระเบิดลูกโซ่)
            "X1X1X1X1X1",
            // แถว 4: ฟ้าสลับโบนัส
            "1B1B1B1B1B",
            // แถว 5: ส้มสลับช่องว่าง
            "2.2.2.2.2.",
            // แถว 6: ฟ้าสลับช่องว่าง
            ".1.1.1.1.1",
        },
        // ด่าน 5: ป้อมปราการ (ด่านสุดท้าย)
        { // ด่าน 5: ป้อมปราการ (ด่านสุดท้าย)
            // แถว 1: ทองที่ขอบ เทาตรงกลาง
            "U33333333U",
            // แถว 2: ทอง เทา และแถวระเบิด
            "U3XXXXXX3U",
            // แถว 3: ทอง เทา และส้ม
            "U32222223U",
            // แถว 4: ทอง ส้ม ฟ้า และโบนัส
            "U21BBBB12U",
            // แถว 5: ทอง ส้ม และฟ้า
            "U21111112U",
            // แถว 6: ทองสามก้อนสองข้างและฟ้าสี่ก้อน
            "UUU1111UUU",
            // แถว 7: ฟ้าล้วน
            "1111111111",
        },
    };  // จบอาร์เรย์ LAYOUTS

    // จำนวนด่านทั้งหมด = จำนวนผังใน LAYOUTS (5 ด่าน)
    public static final int TOTAL_STAGES = LAYOUTS.length;

    // constructor private ห้ามสร้าง object
    private Level() { }  // ห้ามสร้าง object ใช้แบบ static อย่างเดียว

    // สร้างลิสต์บล็อกของด่าน stage (เริ่มที่ 1)
    // เลขด่านผิด หรือมีตัวอักษรแปลก ๆ ในผัง → throw GameException(INVALID_LEVEL)
    // build สร้างรายการบล็อกของด่านที่ระบุ
    public static List<Brick> build(int stage) {
        // ถ้าเลขด่านไม่อยู่ในช่วง 1 ถึง TOTAL_STAGES
        if (stage < 1 || stage > TOTAL_STAGES) {
            // โยน GameException ชนิด INVALID_LEVEL
            throw new GameException(GameException.ErrorType.INVALID_LEVEL,
                    // พร้อมข้อความบอกว่าด่านนั้นไม่มี
                    "Stage " + stage + " does not exist (1-" + TOTAL_STAGES + ")");
        }  // จบ if
        // หยิบผังของด่านนี้ (ดัชนีเริ่มที่ 0 จึงลบ 1)
        String[] layout = LAYOUTS[stage - 1];
        // สร้างลิสต์ว่างเก็บบล็อก
        List<Brick> bricks = new ArrayList<>();
        // คำนวณ x เริ่มต้นเพื่อจัดบล็อกให้อยู่กึ่งกลางจอ
        double startX = (GameStage.WIDTH - COLUMNS * (Brick.WIDTH + GAP)) / 2 + GAP / 2; // จัดกึ่งกลาง
        // y เริ่มต้นอยู่ใต้เส้นบนของจอ 50 px
        double startY = GameStage.TOP + 50;

        // วนทีละแถว
        for (int row = 0; row < layout.length; row++) {
            // หยิบสตริงของแถวนั้น
            String line = layout[row];
            // ถ้าความยาวแถวไม่ใช่ 10
            if (line.length() != COLUMNS) {
                // โยน GameException ชนิด INVALID_LEVEL
                throw new GameException(GameException.ErrorType.INVALID_LEVEL,
                        // พร้อมข้อความบอกแถวที่ผิด
                        "Stage " + stage + " row " + row + " must have " + COLUMNS + " columns");
            }  // จบ if
            // วนทีละคอลัมน์ในแถว
            for (int col = 0; col < COLUMNS; col++) {
                // ดึงตัวอักษรของช่องนี้
                char c = line.charAt(col);
                // ถ้าเป็นจุด (ช่องว่าง) ข้ามไป
                if (c == '.') continue;
                // แปลงตัวอักษรเป็นชนิดบล็อก
                BrickType type = BrickType.fromSymbol(c);
                // ถ้าไม่รู้จักสัญลักษณ์
                if (type == null) {
                    // โยน GameException ชนิด INVALID_LEVEL
                    throw new GameException(GameException.ErrorType.INVALID_LEVEL,
                            // พร้อมข้อความบอกสัญลักษณ์ที่ผิด
                            "Unknown brick symbol '" + c + "' in stage " + stage);
                }  // จบ if
                // คำนวณ x ของบล็อกตามคอลัมน์
                double x = startX + col * (Brick.WIDTH + GAP);
                // คำนวณ y ของบล็อกตามแถว
                double y = startY + row * (Brick.HEIGHT + GAP);
                // สร้างบล็อกใหม่แล้วใส่ลงลิสต์
                bricks.add(new Brick(x, y, type));
            }  // จบลูปคอลัมน์
        }  // จบลูปแถว
        // คืนรายการบล็อกทั้งหมดของด่าน
        return bricks;
    }  // จบ build
}  // จบคลาส Level
