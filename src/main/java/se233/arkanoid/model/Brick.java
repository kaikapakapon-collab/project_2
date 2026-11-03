// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

/*
 * [เกณฑ์ตาราง] Functionalities (บล็อก HP), Additional animation (กระพริบขาว + รอยร้าว), Display score (คะแนนต่อบล็อก), OOP (ลูกของ GameObject)
 * Brick = บล็อกที่ต้องตีให้แตก
 * - type บอกชนิด (ดู BrickType), hitPoints = ต้องตีอีกกี่ทีถึงแตก
 * - ภาพจาก bricks.png (10 เฟรมเรียงแถวเดียว) เลือกเฟรมตามชนิด + HP ที่เหลือ (มีรอยร้าว)
 * - โดนตีแล้ว "กระพริบขาว" 2-3 เฟรม = visual feedback ตอนโดนตี
 * - แตกแล้วไม่ลบออกจากลิสต์ทันที แค่ destroyed = true แล้วซ่อน
 *   เพราะลบของออกจากลิสต์ระหว่างอีกเธรดวนลูปอ่านอยู่เสี่ยงพัง
 */
// Brick (บล็อก) สืบทอดจาก GameObject
public class Brick extends GameObject {
    // ความกว้างบล็อก 50 px
    public static final double WIDTH = 50;
    // ความสูงบล็อก 20 px
    public static final double HEIGHT = 20;
    // ลำดับเฟรมสีขาวใน sheet ใช้ทำเอฟเฟกต์กระพริบ
    private static final int FLASH_FRAME = 9; // เฟรมสีขาวใน sheet
    // กระพริบกี่เฟรม
    private static final int FLASH_TICKS = 3; // กระพริบกี่เฟรม

    // ชนิดของบล็อก
    private final BrickType type;
    // HP ที่เหลือ
    private int hitPoints;
    // แตกแล้วหรือยัง (GameLoop เขียน DrawingLoop อ่าน)
    private boolean destroyed = false; // [เธรด] GameLoop เขียน, DrawingLoop อ่าน → เมธอดที่แตะค่านี้เป็น synchronized
    // ตัวนับเฟรมกระพริบที่เหลือ
    private int flashTicks = 0;
    // sprite ของบล็อก
    private final AnimatedSprite sprite;

    // สร้างบล็อกชนิด type ที่ (x, y) HP เริ่มต้นตามชนิด และโหลด sprite sheet bricks.png (10 เฟรม)
    // constructor รับตำแหน่งและชนิด
    public Brick(double x, double y, BrickType type) {
        // ส่งตำแหน่งและขนาดให้คลาสแม่
        super(x, y, WIDTH, HEIGHT);
        // เก็บชนิด
        this.type = type;
        // ตั้ง HP เริ่มต้นตามชนิด
        this.hitPoints = type.getHitPoints();
        // สร้าง sprite จาก bricks.png (10 เฟรมเรียงแนวนอน)
        sprite = new AnimatedSprite(AssetLoader.load("bricks.png"), 10, 10, 1, 0, 0, (int) WIDTH, (int) HEIGHT);
        // แสดงเฟรมที่ตรงกับชนิด/HP
        sprite.showFrame(frameIndex());
        // ใส่ sprite ลง Pane
        getChildren().add(sprite);
    }  // จบ constructor

    // โดนตี 1 ที: ลด HP, ถึง 0 = แตก
    // คืน true = แตกในทีนี้ / false = แค่ร้าว หรือตีไม่แตก หรือแตกไปแล้ว
    // [เธรด] GameLoop → แก้แค่ข้อมูล ไม่แตะภาพ
    // hit เรียกเมื่อลูกตีบล็อก คืน true ถ้าบล็อกแตก
    public synchronized boolean hit() {
        // ถ้าแตกไปแล้ว ไม่ทำอะไร
        if (destroyed) return false;
        // ตั้งตัวนับกระพริบ
        flashTicks = FLASH_TICKS;
        // ถ้าตีไม่แตก (สีทอง) คืน false
        if (type == BrickType.UNBREAKABLE) return false;
        // ลด HP ลง 1
        hitPoints--;
        // ถ้า HP หมด
        if (hitPoints <= 0) {
            // ตั้งสถานะแตกแล้ว
            destroyed = true;
        }  // จบ if
        // คืนว่าแตกหรือไม่
        return destroyed;
    }  // จบ hit

    // ทำลายทันทีไม่สน HP (ใช้ตอนโดนระเบิด / ลูกไฟทะลุ) บล็อกทองยังไม่แตก
    // destroy ทำลายทันที (ใช้ตอนระเบิดลูกโซ่หรือเลเซอร์) คืน true ถ้าทำลายสำเร็จ
    public synchronized boolean destroy() {
        // ถ้าแตกแล้วหรือเป็นบล็อกตีไม่แตก ไม่ทำอะไร
        if (destroyed || type == BrickType.UNBREAKABLE) return false;
        // ตั้ง HP เป็น 0
        hitPoints = 0;
        // ตั้งสถานะแตก
        destroyed = true;
        // คืน true
        return true;
    }  // จบ destroy

    // บล็อกนี้ต้องทำลายถึงจะผ่านด่านไหม (บล็อกทองไม่ต้อง)
    // isRequired บล็อกนี้ต้องทำลายเพื่อผ่านด่านหรือไม่
    public boolean isRequired() {
        // ทุกชนิดยกเว้น UNBREAKABLE ต้องทำลาย
        return type != BrickType.UNBREAKABLE;
    }  // จบ isRequired

    // เลือกเฟรมใน bricks.png ตามชนิดและ HP ที่เหลือ
    // frameIndex เลือกเฟรมตามชนิดและ HP
    private int frameIndex() {
        // ธรรมดา ใช้เฟรม 0
        if (type == BrickType.NORMAL) return 0;
        // แข็ง: HP≥2 เฟรม 1 ไม่งั้นเฟรม 2 (ร้าว)
        if (type == BrickType.STRONG) return hitPoints >= 2 ? 1 : 2;                    // 1 = เต็ม, 2 = ร้าว
        // แข็งมาก: HP≥3 เฟรม 3, HP=2 เฟรม 4, ที่เหลือเฟรม 5
        if (type == BrickType.HARD) return hitPoints >= 3 ? 3 : (hitPoints == 2 ? 4 : 5); // 3 = เต็ม, 4-5 = ร้าวขึ้น
        // ระเบิด ใช้เฟรม 6
        if (type == BrickType.EXPLOSIVE) return 6;
        // โบนัส ใช้เฟรม 7
        if (type == BrickType.BONUS) return 7;
        // ที่เหลือคือตีไม่แตก ใช้เฟรม 8
        return 8; // UNBREAKABLE
    }  // จบ frameIndex

    // [เธรด] DrawingLoop (JavaFX Thread): กระพริบขาวถ้าเพิ่งโดนตี ไม่งั้นโชว์เฟรมตาม HP, แตกแล้วซ่อน
    // เขียนทับ repaint ของแม่
    @Override
    // ประกาศ repaint (synchronized เพราะอ่านค่า destroyed/flashTicks)
    public synchronized void repaint() {
        // ย้ายตำแหน่งตามคลาสแม่
        super.repaint();
        // ถ้ายังต้องกระพริบ
        if (flashTicks > 0) {
            // แสดงเฟรมสีขาว
            sprite.showFrame(FLASH_FRAME);
            // ลดตัวนับกระพริบ
            flashTicks--;
        // ไม่งั้น
        } else {
            // แสดงเฟรมปกติตามชนิด/HP
            sprite.showFrame(frameIndex());
        }  // จบ if/else
        // ซ่อนบล็อกถ้าแตกแล้ว
        setVisible(!destroyed);
    }  // จบ repaint

    // getter ชนิดบล็อก
    public BrickType getType()   { return type; }  // ชนิดของบล็อก
    // getter HP ที่เหลือ
    public int getHitPoints()    { return hitPoints; }  // HP ที่เหลือ
    // getter คะแนนฐานของชนิดนี้
    public int getScoreValue()   { return type.getScore(); }  // คะแนนพื้นฐานตอนแตก (ก่อนคูณคอมโบ/อัปเกรด)
    // ถามว่าแตกแล้วหรือยัง
    public synchronized boolean isDestroyed() { return destroyed; }  // แตกแล้วหรือยัง
}  // จบคลาส Brick
