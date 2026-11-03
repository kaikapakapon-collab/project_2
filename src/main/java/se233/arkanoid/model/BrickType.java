// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

/*
 * [เกณฑ์ตาราง] Functionalities (ชนิดบล็อก 6 แบบ), Additional features (บล็อกระเบิด)
 * BrickType = ชนิดของบล็อก (enum)
 * แต่ละชนิดกำหนด: ตัวอักษรในผังด่าน, HP เริ่มต้น, คะแนน
 * hp = -1 หมายถึงตีไม่แตก
 */
// enum ชนิดบล็อกทั้งหมด แต่ละชนิดมี สัญลักษณ์ในผังด่าน, HP, คะแนน
public enum BrickType {
    // NORMAL สัญลักษณ์ '1', HP 1, 100 คะแนน
    NORMAL('1', 1, 100),       // ฟ้า ตี 1 ทีแตก
    // STRONG สัญลักษณ์ '2', HP 2, 200 คะแนน
    STRONG('2', 2, 200),       // ส้ม ตี 2 ที
    // HARD สัญลักษณ์ '3', HP 3, 300 คะแนน
    HARD('3', 3, 300),         // เทา ตี 3 ที
    // EXPLOSIVE สัญลักษณ์ 'X', HP 1, 150 คะแนน (แตกแล้วระเบิดรอบ ๆ)
    EXPLOSIVE('X', 1, 150),    // แดง แตกแล้วระเบิดบล็อกรอบ ๆ (ฟีเจอร์พิเศษ)
    // BONUS สัญลักษณ์ 'B', HP 1, 250 คะแนน (แตกแล้วดรอปไอเทมแน่นอน)
    BONUS('B', 1, 250),        // เขียว แตกแล้วดรอปไอเทมแน่นอน
    // UNBREAKABLE สัญลักษณ์ 'U', HP -1 (ตีไม่แตก), 0 คะแนน
    UNBREAKABLE('U', -1, 0);   // ทอง ตีไม่แตก ไม่ต้องทำลายก็ผ่านด่านได้

    // ตัวอักษรที่ใช้แทนชนิดนี้ในผังด่าน
    private final char symbol;
    // HP เริ่มต้น
    private final int hitPoints;
    // คะแนนตอนแตก
    private final int score;

    // constructor ของ enum (ใช้ตอนประกาศค่าด้านบน)
    BrickType(char symbol, int hitPoints, int score) {
        // เก็บสัญลักษณ์
        this.symbol = symbol;
        // เก็บ HP
        this.hitPoints = hitPoints;
        // เก็บคะแนน
        this.score = score;
    }  // จบ constructor

    // แปลงตัวอักษรในผังด่านเป็นชนิดบล็อก, ไม่เจอคืน null (Level จะ throw เอง)
    // fromSymbol แปลงตัวอักษรในผังด่านเป็นชนิดบล็อก
    public static BrickType fromSymbol(char c) {
        // วนทุกชนิดใน enum
        for (BrickType t : values()) {
            // ถ้าสัญลักษณ์ตรงกัน คืนชนิดนั้น
            if (t.symbol == c) return t;
        }  // จบ for
        // ไม่เจอ คืน null (ใช้แทนช่องว่าง)
        return null;
    }  // จบ fromSymbol

    // getter สัญลักษณ์
    public char getSymbol()   { return symbol; }  // ตัวอักษรในผังด่าน
    // getter HP เริ่มต้น
    public int getHitPoints() { return hitPoints; }  // HP เริ่มต้น (-1 = ตีไม่แตก)
    // getter คะแนน
    public int getScore()     { return score; }  // คะแนนตอนแตก
}  // จบ enum BrickType
