// ประกาศแพ็กเกจ model
package se233.arkanoid.model;

// นำเข้า GameStage เพื่ออ่านขอบจอ
import se233.arkanoid.view.GameStage;

/*
 * [เกณฑ์ตาราง] Sprite sheet (ท่ากลิ้งแยกจากท่าเด้ง + ลูกไฟ), Functionalities (ฟิสิกส์เด้ง), OOP (ลูกของ GameObject)
 * Ball = ลูกบอล
 * - เก็บความเร็วแยกแกน: xVelocity (ซ้าย/ขวา), yVelocity (ขึ้น/ลง)
 *   จอ JavaFX แกน Y ชี้ลง → yVelocity ติดลบ = ลอยขึ้น, บวก = ตกลง
 * - "เด้ง" = กลับเครื่องหมายความเร็วแกนที่ชน
 * - ก่อนปล่อย (launched = false) ลูกเกาะอยู่บน paddle
 * - ภาพจาก ball.png (4 เฟรม x 3 แถว): แถว 0 = กลิ้ง, แถว 1 = เด้ง (บี้), แถว 2 = ลูกไฟ (ตอนทะลุบล็อกได้)
 */
// Ball (ลูกบอล) สืบทอดจาก GameObject
public class Ball extends GameObject {
    // ขนาดลูก 12 px
    public static final double SIZE = 12;
    // ความเร็วฐานด่าน 1
    public static final double BASE_SPEED = 5;          // ความเร็วด่าน 1
    // มุมเด้งสูงสุดจากขอบ paddle (60 องศา)
    private static final double MAX_BOUNCE_ANGLE = 60;  // มุมเด้งสูงสุดเมื่อโดนขอบ paddle (องศา)
    // ขนาด 1 เฟรมใน sprite sheet
    private static final int FRAME = 16;                 // ขนาด 1 เฟรมใน sheet
    // ท่าเด้งเล่นกี่เฟรม
    private static final int BOUNCE_FRAMES = 4;          // เล่นท่าเด้งกี่เฟรม

    // ความเร็วรวมของลูก
    private double speed;
    // ความเร็วแกน X
    private double xVelocity = 0;
    // ความเร็วแกน Y (ลบ = ขึ้นบน)
    private double yVelocity = 0;
    // ปล่อยลูกแล้วหรือยัง
    private boolean launched = false;
    // ทะลุบล็อกได้อีกกี่ก้อน
    private int pierceLeft = 0;                // ทะลุบล็อกได้อีกกี่ก้อน (จากอัปเกรด Piercing)
    // เหลืออีกกี่เฟรมที่ต้องเล่นท่าเด้ง (หลายเธรดใช้ร่วม)
    private int bounceTicks = 0;               // [เธรด] GameLoop ตั้งตอนเด้ง, DrawingLoop นับลง → เมธอดที่แตะค่านี้เป็น synchronized
    // sprite ของลูกบอล
    private final AnimatedSprite sprite;

    // สร้างลูกบอลที่ (x, y) ความเร็วรวม speed และโหลด sprite sheet ball.png (4 เฟรม x 3 แถว: กลิ้ง / เด้ง / ลูกไฟ)
    // constructor รับตำแหน่งเริ่มและความเร็ว
    public Ball(double x, double y, double speed) {
        // ส่งตำแหน่งและขนาดให้คลาสแม่
        super(x, y, SIZE, SIZE);
        // เก็บความเร็ว
        this.speed = speed;
        // สร้าง sprite จาก ball.png (4 เฟรม)
        sprite = new AnimatedSprite(AssetLoader.load("ball.png"), 4, 4, 1, 0, 0, FRAME, FRAME);
        // ตั้งความกว้างภาพเท่าลูก
        sprite.setFitWidth(SIZE);
        // ตั้งความสูงภาพเท่าลูก
        sprite.setFitHeight(SIZE);
        // ใส่ sprite ลงใน Pane
        getChildren().add(sprite);
    }  // จบ constructor

    // ปล่อยลูกขึ้นไปตามมุมที่กำหนด (ถ้าปล่อยไปแล้วไม่ทำอะไร)
    // launch ปล่อยลูกออกจาก paddle ตามมุมที่กำหนด
    public void launch(double angleDegrees) {
        // ถ้าปล่อยไปแล้วไม่ต้องทำซ้ำ
        if (launched) return;
        // ตั้งสถานะปล่อยแล้ว
        launched = true;
        // คำนวณทิศทางจากมุม
        setDirection(angleDegrees);
    }  // จบ launch

    // แปลง "มุม" เป็นความเร็ว 2 แกน: 0° = ขึ้นตรง, ติดลบ = เอียงซ้าย, บวก = เอียงขวา
    // ใช้ sin/cos → ความเร็วรวมเท่า speed เสมอ ไม่ว่าจะเอียงแค่ไหน
    // setDirection แปลงมุม (องศา) เป็นความเร็วแกน X,Y
    private void setDirection(double angleDegrees) {
        // แปลงองศาเป็นเรเดียน
        double rad = Math.toRadians(angleDegrees);
        // ความเร็วแกน X = ความเร็ว × sin(มุม)
        xVelocity = speed * Math.sin(rad);
        // ความเร็วแกน Y = -ความเร็ว × cos(มุม) (ลบเพราะแกน Y ชี้ลง)
        yVelocity = -speed * Math.cos(rad);
    }  // จบ setDirection

    // วางลูกไว้กลาง paddle ด้านบน (ตอนยังไม่ปล่อย)
    // followPaddle ให้ลูกติดอยู่บน paddle ก่อนปล่อย
    public void followPaddle(Paddle paddle) {
        // วางลูกให้กึ่งกลางตรงกับ paddle
        x = paddle.getCenterX() - w / 2;
        // วางลูกเหนือ paddle เล็กน้อย
        y = paddle.getY() - h - 1;
    }  // จบ followPaddle

    // ขยับตามความเร็ว   [เธรด] GameLoop
    // move ขยับลูกหนึ่งก้าว
    public void move() {
        // ถ้ายังไม่ปล่อย ไม่ขยับ
        if (!launched) return;
        // เพิ่ม x ตามความเร็วแกน X
        x = x + xVelocity;
        // เพิ่ม y ตามความเร็วแกน Y
        y = y + yVelocity;
    }  // จบ move

    // เด้งกำแพงซ้าย/ขวา/บน (ด้านล่างไม่มีกำแพง)
    // ใช้ Math.abs บังคับทิศ แทนการกลับเครื่องหมาย → กันบั๊กลูกติดกำแพงสั่นไปมา
    // คืน true ถ้าเด้ง
    // checkReachWall ให้ลูกเด้งขอบซ้าย/ขวา/บน คืน true ถ้าเด้ง
    public boolean checkReachWall() {
        // ตัวแปรจำว่าเด้งหรือไม่
        boolean bounced = false;
        // ถ้าชนขอบซ้าย
        if (x < 0) {
            // ดันกลับมาที่ขอบ
            x = 0;
            // บังคับให้วิ่งไปทางขวา (ค่าสัมบูรณ์)
            xVelocity = Math.abs(xVelocity);
            // จำว่าเด้งแล้ว
            bounced = true;
        // ถ้าชนขอบขวา
        } else if (x + w > GameStage.WIDTH) {
            // ดันกลับให้ชิดขอบขวา
            x = GameStage.WIDTH - w;
            // บังคับให้วิ่งไปทางซ้าย
            xVelocity = -Math.abs(xVelocity);
            // จำว่าเด้งแล้ว
            bounced = true;
        }  // จบ if ขอบข้าง
        // ถ้าชนขอบบน (เส้นเริ่มพื้นที่เล่น)
        if (y < GameStage.TOP) {
            // ดันกลับมาที่ขอบบน
            y = GameStage.TOP;
            // บังคับให้วิ่งลง
            yVelocity = Math.abs(yVelocity);
            // จำว่าเด้งแล้ว
            bounced = true;
        }  // จบ if ขอบบน
        // ถ้าเด้งให้เริ่มเล่นท่าเด้ง
        if (bounced) startBounceAnimation();
        // คืนผลว่าเด้งไหม
        return bounced;
    }  // จบ checkReachWall

    // เด้ง paddle: ยิ่งโดนห่างจากกลาง paddle ยิ่งเอียงมาก (ผู้เล่นเล็งได้)
    // hit = -1 (ขอบซ้าย) ... 0 (กลาง) ... 1 (ขอบขวา) → คูณ 60° = มุมเด้ง
    // เช็ก yVelocity > 0 = เด้งเฉพาะตอนลูกกำลังตก (กันเด้งซ้ำ)
    // bounceOffPaddle เด้งเมื่อชน paddle คืน true ถ้าเด้งจริง
    public boolean bounceOffPaddle(Paddle paddle) {
        // ถ้าลูกกำลังขึ้น หรือไม่ได้ชน paddle ให้ข้าม
        if (yVelocity <= 0 || !collidesWith(paddle)) return false;
        // คำนวณตำแหน่งที่ตี (-1 ซ้ายสุด ถึง 1 ขวาสุด) จากระยะห่างจากกึ่งกลาง
        double hit = (getCenterX() - paddle.getCenterX()) / (paddle.getW() / 2);
        // บีบค่าให้อยู่ในช่วง -1 ถึง 1
        hit = Math.max(-1, Math.min(1, hit));
        // ตั้งทิศทางใหม่: ยิ่งตีใกล้ขอบ ยิ่งเอียงมาก (สูงสุด 60 องศา)
        setDirection(hit * MAX_BOUNCE_ANGLE);
        // ดันลูกขึ้นเหนือ paddle ไม่ให้จม
        y = paddle.getY() - h; // ดันลูกขึ้นมาเหนือ paddle ไม่ให้จม
        // เริ่มท่าเด้ง
        startBounceAnimation();
        // คืน true
        return true;
    }  // จบ bounceOffPaddle

    // เด้งบล็อก: วัดว่าลูก "จม" เข้าไปในบล็อกจากแต่ละด้านลึกเท่าไหร่ ด้านที่จมน้อยสุด = ด้านที่ชน
    // จมด้านข้างน้อยกว่า → ชนด้านข้าง → กลับ X / จมบน-ล่างน้อยกว่า → กลับ Y
    // bounceOffBrick เด้งออกจากบล็อก โดยดูว่าชนด้านไหนจากระยะซ้อนทับที่น้อยที่สุด
    public void bounceOffBrick(Brick brick) {
        // ระยะซ้อนด้านซ้ายของบล็อก
        double overlapLeft   = (x + w) - brick.getX();
        // ระยะซ้อนด้านขวา
        double overlapRight  = (brick.getX() + brick.getW()) - x;
        // ระยะซ้อนด้านบน
        double overlapTop    = (y + h) - brick.getY();
        // ระยะซ้อนด้านล่าง
        double overlapBottom = (brick.getY() + brick.getH()) - y;

        // ระยะซ้อนแนวนอนที่น้อยที่สุด
        double minX = Math.min(overlapLeft, overlapRight);
        // ระยะซ้อนแนวตั้งที่น้อยที่สุด
        double minY = Math.min(overlapTop, overlapBottom);

        // ถ้าซ้อนแนวนอนน้อยกว่า แปลว่าชนด้านข้าง
        if (minX < minY) {
            // กลับทิศแกน X (ซ้ายถ้าชนด้านซ้าย ขวาถ้าชนด้านขวา)
            xVelocity = overlapLeft < overlapRight ? -Math.abs(xVelocity) : Math.abs(xVelocity);
        // ไม่งั้นชนด้านบน/ล่าง
        } else {
            // กลับทิศแกน Y
            yVelocity = overlapTop < overlapBottom ? -Math.abs(yVelocity) : Math.abs(yVelocity);
        }  // จบ if/else
        // เริ่มท่าเด้ง
        startBounceAnimation();
    }  // จบ bounceOffBrick

    // ลูกตกขอบล่างจอแล้วหรือยัง
    // isOut ลูกตกลงใต้จอหรือยัง
    public boolean isOut() {
        // คืน true ถ้า y เกินความสูงจอ
        return y > GameStage.HEIGHT;
    }  // จบ isOut

    // สั่งเล่นท่าเด้ง BOUNCE_FRAMES เฟรม (DrawingLoop จะเป็นคนเล่นจริง)
    // เริ่มท่าเด้ง (synchronized เพราะอีกเธรดอ่านค่านี้)
    private synchronized void startBounceAnimation() {
        // ตั้งจำนวนเฟรมท่าเด้งที่ต้องเล่น
        bounceTicks = BOUNCE_FRAMES;
    }  // จบ startBounceAnimation

    // อัปเดตภาพเคลื่อนไหว   [เธรด] DrawingLoop (JavaFX Thread)
    // ลำดับความสำคัญ: กำลังเด้ง → ท่าเด้ง (แถว 1) / ทะลุได้ → ลูกไฟ (แถว 2) / ปกติ → กลิ้ง (แถว 0)
    // updateSprite เลือกแถวอนิเมชันตามสถานะลูก
    public synchronized void updateSprite() {
        // ตัวแปรเก็บหมายเลขแถว
        int row;
        // ถ้ากำลังเล่นท่าเด้ง
        if (bounceTicks > 0) {
            // ใช้แถว 1 (ท่าเด้ง)
            row = 1;
            // ลดตัวนับท่าเด้งลง 1
            bounceTicks--;
        // หรือถ้ามีพลังทะลุ
        } else if (pierceLeft > 0) {
            // ใช้แถว 2 (ลูกไฟ)
            row = 2;
        // ไม่งั้น
        } else {
            // ใช้แถว 0 (กลิ้งปกติ)
            row = 0;
        }  // จบ if/else
        // สลับไปแถวนั้นของ sprite sheet
        sprite.changeAnimation(0, row * FRAME);
        // ถ้าปล่อยแล้ว ให้เล่นเฟรมถัดไป
        if (launched) sprite.tick(); // ลูกยังไม่ปล่อย = ไม่กลิ้ง
    }  // จบ updateSprite

    // ถามว่าปล่อยลูกแล้วหรือยัง
    public boolean isLaunched()   { return launched; }  // ปล่อยลูกแล้วหรือยัง
    // getter ความเร็วแกน X
    public double getXVelocity()  { return xVelocity; }  // ความเร็วแกน X ตอนนี้
    // getter ความเร็วแกน Y
    public double getYVelocity()  { return yVelocity; }  // ความเร็วแกน Y ตอนนี้ (ลบ = ขึ้น)
    // setter ตั้งความเร็วตรง ๆ (ใช้ใน unit test)
    public void setVelocity(double vx, double vy) { xVelocity = vx; yVelocity = vy; }  // ตั้งความเร็วตรง ๆ (ใช้ใน unit test)
    // getter ความเร็วรวม
    public double getSpeed()      { return speed; }  // ความเร็วรวม
    // getter จำนวนทะลุที่เหลือ
    public int getPierceLeft()    { return pierceLeft; }  // ทะลุบล็อกได้อีกกี่ก้อน
    // ตั้งจำนวนทะลุ
    public void setPierceLeft(int pierceLeft) { this.pierceLeft = pierceLeft; }  // เติมพลังทะลุ (เรียกตอนปล่อยลูก/ตี paddle)
    // ใช้พลังทะลุไป 1 ก้อน (ไม่ต่ำกว่า 0)
    public void usePierce()       { if (pierceLeft > 0) pierceLeft--; }  // ใช้พลังทะลุไป 1 ก้อน (ไม่ต่ำกว่า 0)
    // getter ท่าเด้งที่เหลือ
    public synchronized int getBounceTicks() { return bounceTicks; }  // ท่าเด้งที่เหลืออีกกี่เฟรม
}  // จบคลาส Ball
