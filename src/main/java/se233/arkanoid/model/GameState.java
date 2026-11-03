// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า GameException
import se233.arkanoid.exception.GameException;

/*
 * [เกณฑ์ตาราง] Functionalities (คะแนน/ชีวิต/ด่าน), Display score (ข้อมูลคะแนน), Additional features (ระบบคอมโบ), Testing (ทดสอบง่าย: ไม่มี JavaFX)
 * GameState = ข้อมูลของ "การเล่น 1 รอบ (run)" ทั้งหมด: คะแนน ชีวิต ด่าน คอมโบ และค่าที่ได้จากอัปเกรด
 * เป็น Java ล้วน ไม่มี JavaFX → เขียน unit test ง่าย
 *
 * [เธรด] GameLoop เป็นคนแก้ค่า, DrawingLoop อ่านไปแสดงที่ HUD (คนละเธรดกัน)
 * → ทุกเมธอด public เป็น synchronized (หัวข้อ 3.1.2): ล็อกทีละเธรด และค่าที่แก้จะมองเห็นกันได้ทันที
 *
 * ระบบคอมโบ (ส่วนหนึ่งของฟีเจอร์พิเศษ):
 *   ทำลายบล็อกต่อเนื่องโดยลูกยังไม่กลับมาโดน paddle → combo เพิ่ม → คะแนนคูณเพิ่ม 10% ต่อคอมโบ (สูงสุด x2)
 */
// GameState เก็บตัวเลขสถานะผู้เล่นทั้งหมด ทุกเมธอด synchronized เพราะหลายเธรดใช้ร่วมกัน
public class GameState {
    // จำนวนชีวิตตอนเริ่มเกม = 3
    public static final int START_LIVES = 3;
    // คอมโบเพิ่มตัวคูณได้สูงสุด 10 ขั้น
    public static final int MAX_COMBO_BONUS = 10;       // คอมโบเกิน 11 ไม่คูณเพิ่มแล้ว
    // ตีบล็อกที่ยังไม่แตกได้ 10 แต้ม
    public static final int HIT_SCORE = 10;             // ตีโดนแต่ไม่แตก ได้ 10 แต้ม

    // คะแนนรวม
    private int score;
    // ชีวิตที่เหลือ
    private int lives;
    // ด่านปัจจุบัน
    private int stage;
    // คอมโบ (จำนวนบล็อกที่แตกติดต่อกันโดยบอลยังไม่ตก)
    private int combo;

    // ----- ค่าจากอัปเกรด roguelike (อยู่ถึงจบ run) -----
    // ความกว้าง paddle (ปรับด้วยอัปเกรด)
    private double paddleWidth;
    // ความเร็ว paddle
    private double paddleSpeed;
    // ตัวคูณความเร็วลูก
    private double ballSpeedFactor;   // ตัวคูณความเร็วลูก (1.0 = ปกติ)
    // จำนวนลูกที่แตกเพิ่มตอนปล่อย
    private int extraBalls;           // ปล่อยลูกแล้วแตกเพิ่มกี่ลูก
    // จำนวนบล็อกที่ลูกทะลุได้ต่อการตี paddle 1 ครั้ง
    private int pierce;               // ทะลุบล็อกได้กี่ก้อนต่อการตี paddle 1 ครั้ง
    // ตัวคูณคะแนน
    private double scoreMultiplier;   // ตัวคูณคะแนน
    // เวลาเลเซอร์ (วินาที)
    private int laserSeconds;         // เลเซอร์อยู่ได้กี่วินาที

    // สร้าง state ใหม่ แล้วเรียก reset() ให้เป็นค่าเริ่มต้นทันที
    // constructor
    public GameState() {
        // เรียก reset เพื่อตั้งค่าเริ่มต้น
        reset();
    }  // จบ constructor

    // เริ่ม run ใหม่: ทุกค่ากลับเป็นค่าเริ่มต้น
    // reset คืนค่าทุกอย่างเป็นเริ่มเกมใหม่
    public synchronized void reset() {
        // คะแนน = 0
        score = 0;
        // ชีวิต = ค่าเริ่มต้น
        lives = START_LIVES;
        // ด่าน = 1
        stage = 1;
        // คอมโบ = 0
        combo = 0;
        // paddle กว้างเท่าค่าเริ่มต้น
        paddleWidth = Paddle.DEFAULT_WIDTH;
        // paddle เร็วเท่าค่าเริ่มต้น
        paddleSpeed = Paddle.DEFAULT_SPEED;
        // ความเร็วลูกคูณ 1.0 (ปกติ)
        ballSpeedFactor = 1.0;
        // ไม่มีลูกเพิ่ม
        extraBalls = 0;
        // ไม่มีการทะลุ
        pierce = 0;
        // ตัวคูณคะแนน 1.0
        scoreMultiplier = 1.0;
        // เลเซอร์เริ่มต้น 10 วินาที
        laserSeconds = 10;
    }  // จบ reset

    // ตัวคูณคอมโบ: combo 1 = x1.0, combo 2 = x1.1, ... สูงสุด x2.0
    // คำนวณตัวคูณคอมโบ
    public synchronized double getComboMultiplier() {
        // คอมโบ 0-1 ยังไม่มีโบนัส คืน 1.0
        if (combo <= 1) return 1.0;
        // ตัวคูณ = 1 + (คอมโบ-1) × 0.1 โดยโบนัสสูงสุด 10 ขั้น (รวมสูงสุด 2.0)
        return 1.0 + Math.min(combo - 1, MAX_COMBO_BONUS) * 0.1;
    }  // จบ getComboMultiplier

    // บล็อกแตก: combo +1 แล้วคิดคะแนน = คะแนนบล็อก x ตัวคูณอัปเกรด x ตัวคูณคอมโบ
    // คืนคะแนนที่ได้ (เอาไปโชว์ "+120" ลอยขึ้น)
    // เพิ่มคะแนนตอนบล็อกแตก รับคะแนนพื้นฐานของบล็อก
    public synchronized int addBrickScore(int baseScore) {
        // เพิ่มคอมโบก่อนคำนวณ
        combo++;
        // คะแนนจริง = ฐาน × ตัวคูณอัปเกรด × ตัวคูณคอมโบ ปัดเศษ
        int points = (int) Math.round(baseScore * scoreMultiplier * getComboMultiplier());
        // บวกเข้าคะแนนรวม
        score += points;
        // คืนคะแนนที่ได้ (ไว้โชว์ตัวเลขลอย)
        return points;
    }  // จบ addBrickScore

    // ตีโดนแต่ไม่แตก: ได้คะแนนเล็กน้อย (ไม่เพิ่มคอมโบ)
    // เพิ่มคะแนนตอนตีบล็อกแต่ยังไม่แตก
    public synchronized int addHitScore() {
        // คะแนน = 10 × ตัวคูณคะแนน ปัดเศษ
        int points = (int) Math.round(HIT_SCORE * scoreMultiplier);
        // บวกเข้าคะแนนรวม
        score += points;
        // คืนคะแนนที่ได้
        return points;
    }  // จบ addHitScore

    // ลูกกลับมาโดน paddle / เสียชีวิต → คอมโบหลุด
    // รีเซ็ตคอมโบ (เรียกตอนลูกตกหรือเสียชีวิต)
    public synchronized void resetCombo() {
        // ตั้งคอมโบเป็น 0
        combo = 0;
    }  // จบ resetCombo

    // เสียชีวิต 1 ดวง ถ้าชีวิตเป็น 0 อยู่แล้วแปลว่ามีบั๊กในเกม → throw GameException
    // ลดชีวิต 1
    public synchronized void loseLife() {
        // ถ้าชีวิตเหลือ 0 อยู่แล้ว (ไม่ควรเกิดขึ้น)
        if (lives <= 0) {
            // โยน GameException ชนิด INVALID_STATE
            throw new GameException(GameException.ErrorType.INVALID_STATE, "Cannot lose a life: no lives left");
        }  // จบ if
        // ลดชีวิตลง 1
        lives--;
        // รีเซ็ตคอมโบเมื่อเสียชีวิต
        combo = 0;
    }  // จบ loseLife

    // ชีวิตหมดหรือยัง (≤ 0 = เกมโอเวอร์)
    public synchronized boolean isGameOver() { return lives <= 0; }  // ชีวิตหมดหรือยัง

    // ไปด่านถัดไป
    // ขึ้นด่านถัดไป (stage + 1)
    public synchronized void nextStage() { stage++; }

    // ความเร็วลูกของด่านนี้: ด่านยิ่งสูงยิ่งเร็ว (+0.5 ต่อด่าน) แล้วคูณด้วยอัปเกรด
    // ความเร็วลูก = (ความเร็วฐาน + 0.5 ต่อด่าน) × ตัวคูณจากอัปเกรด
    public synchronized double getBallSpeed() {
        // สูตรคำนวณความเร็วลูกตามด่านและอัปเกรด
        return (Ball.BASE_SPEED + 0.5 * (stage - 1)) * ballSpeedFactor;
    }  // จบ getBallSpeed

    // ----- เมธอดที่อัปเกรดเรียกใช้ (มีเพดานกันโกงเกินไป) -----
    // อัปเกรด: ชีวิต +1
    public synchronized void addLife()                       { lives++; }
    // อัปเกรด: paddle กว้างขึ้น px (เพดาน 200)
    public synchronized void increasePaddleWidth(double px)  { paddleWidth = Math.min(200, paddleWidth + px); }  // อัปเกรด: paddle กว้างขึ้น px (เพดาน 200)
    // อัปเกรด: paddle เร็วขึ้น (เพดาน 16)
    public synchronized void increasePaddleSpeed(double v)   { paddleSpeed = Math.min(16, paddleSpeed + v); }  // อัปเกรด: paddle เร็วขึ้น (เพดาน 16)
    // อัปเกรด: คูณความเร็วลูก จำกัดไว้ระหว่าง 0.6–1.6 เท่า
    public synchronized void multiplyBallSpeed(double f)     { ballSpeedFactor = Math.max(0.6, Math.min(1.6, ballSpeedFactor * f)); }  // อัปเกรด: คูณความเร็วลูก (จำกัด 0.6–1.6 เท่า)
    // อัปเกรด: ลูกที่แตกเพิ่ม +1 (สูงสุด 4)
    public synchronized void addExtraBall()                  { extraBalls = Math.min(4, extraBalls + 1); }  // อัปเกรด: ลูกที่แตกเพิ่มตอนปล่อย +1 (สูงสุด 4)
    // อัปเกรด: ทะลุบล็อกเพิ่ม n ก้อน
    public synchronized void addPierce(int n)                { pierce += n; }  // อัปเกรด: ทะลุบล็อกได้เพิ่ม n ก้อนต่อการตี paddle
    // อัปเกรด: ตัวคูณคะแนนเพิ่ม v
    public synchronized void addScoreMultiplier(double v)    { scoreMultiplier += v; }  // อัปเกรด: ตัวคูณคะแนนเพิ่ม v
    // อัปเกรด: เลเซอร์อยู่นานขึ้น s วินาที
    public synchronized void addLaserSeconds(int s)          { laserSeconds += s; }  // อัปเกรด: เลเซอร์อยู่นานขึ้น s วินาที

    // ----- getter -----
    // getter คะแนน
    public synchronized int getScore()               { return score; }
    // getter ชีวิต
    public synchronized int getLives()               { return lives; }  // ชีวิตที่เหลือ
    // getter ด่าน
    public synchronized int getStage()               { return stage; }  // ด่านปัจจุบัน
    // getter คอมโบ
    public synchronized int getCombo()               { return combo; }  // คอมโบปัจจุบัน
    // getter ความกว้าง paddle
    public synchronized double getPaddleWidth()      { return paddleWidth; }  // ความกว้าง paddle จากอัปเกรด
    // getter ความเร็ว paddle
    public synchronized double getPaddleSpeed()      { return paddleSpeed; }  // ความเร็ว paddle จากอัปเกรด
    // getter ตัวคูณความเร็วลูก
    public synchronized double getBallSpeedFactor()  { return ballSpeedFactor; }  // ตัวคูณความเร็วลูกจากอัปเกรด
    // getter ลูกเพิ่ม
    public synchronized int getExtraBalls()          { return extraBalls; }  // จำนวนลูกที่แตกเพิ่มตอนปล่อย
    // getter จำนวนทะลุ
    public synchronized int getPierce()              { return pierce; }  // จำนวนก้อนที่ทะลุได้ต่อการตี paddle
    // getter ตัวคูณคะแนน
    public synchronized double getScoreMultiplier()  { return scoreMultiplier; }  // ตัวคูณคะแนนจากอัปเกรด
    // getter วินาทีเลเซอร์
    public synchronized int getLaserSeconds()        { return laserSeconds; }  // เวลาเลเซอร์ (วินาที)
}  // จบคลาส GameState
