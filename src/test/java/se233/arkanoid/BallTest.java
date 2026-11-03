// ประกาศแพ็กเกจของ test
package se233.arkanoid;

// นำเข้า @BeforeAll (ทำครั้งเดียวก่อน test ทั้งคลาส)
import org.junit.jupiter.api.BeforeAll;
// นำเข้า @BeforeEach (ทำก่อนทุก test)
import org.junit.jupiter.api.BeforeEach;
// นำเข้า @Test
import org.junit.jupiter.api.Test;
// นำเข้า Ball (คลาสที่ทดสอบ)
import se233.arkanoid.model.Ball;
// นำเข้า Brick
import se233.arkanoid.model.Brick;
// นำเข้า BrickType
import se233.arkanoid.model.BrickType;
// นำเข้า Paddle
import se233.arkanoid.model.Paddle;
// นำเข้า GameStage (เอาขนาดจอมาใช้)
import se233.arkanoid.view.GameStage;

// นำเข้า KeyCode
import javafx.scene.input.KeyCode;

// นำเข้า assert ทั้งหมดแบบ static
import static org.junit.jupiter.api.Assertions.*;

/*
 * [เกณฑ์ตาราง] Testing (พฤติกรรมลูกบอล: ปล่อย, เด้งกำแพง/paddle/บล็อก)
 * ทดสอบพฤติกรรมลูกบอล: ปล่อยลูก, เด้งกำแพง, เด้ง paddle, เด้งบล็อก
 * ใช้ JavaFX toolkit เพราะ Ball เป็น Pane ที่มี ImageView (Script 5.23)
 */
// คลาสทดสอบ Ball
public class BallTest {
    // ลูกบอลที่ใช้ทดสอบ
    private Ball ball;
    // paddle ที่ใช้ทดสอบ
    private Paddle paddle;

    // ทำครั้งเดียวก่อนเริ่มทั้งคลาส
    @BeforeAll
    // เมธอดเริ่มระบบ JavaFX (ต้องเป็น static)
    public static void initJfxRuntime() {
        // เรียกตัวช่วยเปิด JavaFX Toolkit
        TestSupport.initJfx();
    }  // จบเมธอด

    // ทำก่อนทุก test
    @BeforeEach
    // เมธอด setUp
    public void setUp() {
        // สร้าง paddle ตำแหน่ง 250,640
        paddle = new Paddle(250, 640, KeyCode.LEFT, KeyCode.RIGHT);
        // สร้างลูกที่ 300,300 ความเร็ว 6
        ball = new Ball(300, 300, 6);
    }  // จบ setUp

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ลูกใหม่ยังไม่ถูกปล่อยและไม่ขยับ
    public void newBall_thenNotLaunched_andDoesNotMove() {
        // เรียก move
        ball.move();
        // ต้องยังไม่ถูกปล่อย
        assertFalse(ball.isLaunched());
        // x ต้องคงเดิม 300
        assertEquals(300, ball.getX(), 0.0001);
        // y ต้องคงเดิม 300
        assertEquals(300, ball.getY(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ปล่อยมุม 0 ต้องพุ่งขึ้นตรงด้วยความเร็วเต็ม
    public void launch_givenAngle0_thenMovesStraightUpAtFullSpeed() {
        // ปล่อยลูกมุม 0
        ball.launch(0);
        // ต้องถูกปล่อยแล้ว
        assertTrue(ball.isLaunched());
        // ความเร็วแกน X ต้องเป็น 0
        assertEquals(0, ball.getXVelocity(), 0.0001);
        // ความเร็วแกน Y ต้องเป็น -6 (ขึ้นบน)
        assertEquals(-6, ball.getYVelocity(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ปล่อยมุมใดก็ตาม ความเร็วรวมต้องคงที่
    public void launch_givenAnyAngle_thenTotalSpeedStaysConstant() {
        // ปล่อยมุม 37 องศา
        ball.launch(37);
        // คำนวณความเร็วรวมจากแกน X และ Y
        double speed = Math.hypot(ball.getXVelocity(), ball.getYVelocity());
        // ต้องได้ 6 เท่าเดิม
        assertEquals(6, speed, 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ปล่อยไปแล้วเรียกซ้ำ ทิศทางต้องไม่เปลี่ยน
    public void launch_givenAlreadyLaunched_thenDirectionDoesNotChange() {
        // ปล่อยมุม 0
        ball.launch(0);
        // พยายามปล่อยซ้ำมุม 45
        ball.launch(45);
        // แกน X ต้องยังเป็น 0
        assertEquals(0, ball.getXVelocity(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: move หลังปล่อย ตำแหน่งเปลี่ยนตามความเร็ว
    public void move_afterLaunch_thenPositionChangesByVelocity() {
        // ปล่อยมุม 0
        ball.launch(0);
        // ขยับ 1 ก้าว
        ball.move();
        // x ต้องคงเดิม
        assertEquals(300, ball.getX(), 0.0001);
        // y ต้องลดลง 6 เป็น 294
        assertEquals(294, ball.getY(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เลยกำแพงซ้ายต้องเด้งไปขวา
    public void checkReachWall_givenBeyondLeftWall_thenBouncesRight() {
        // ปล่อยมุม -30 (เฉียงซ้าย)
        ball.launch(-30);
        // ตั้ง x เป็น -5 (เลยขอบซ้าย)
        ball.setX(-5);
        // เช็กกำแพงต้องคืน true (มีการเด้ง)
        assertTrue(ball.checkReachWall());
        // x ต้องถูกดึงกลับเป็น 0
        assertEquals(0, ball.getX(), 0.0001);
        // ความเร็วแกน X ต้องเป็นบวก (ไปขวา)
        assertTrue(ball.getXVelocity() > 0, "Should move right after hitting left wall");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เลยกำแพงขวาต้องเด้งไปซ้าย
    public void checkReachWall_givenBeyondRightWall_thenBouncesLeft() {
        // ปล่อยมุม 30 (เฉียงขวา)
        ball.launch(30);
        // ตั้ง x เท่าความกว้างจอ
        ball.setX(GameStage.WIDTH);
        // ต้องคืน true
        assertTrue(ball.checkReachWall());
        // ความเร็วแกน X ต้องเป็นลบ (ไปซ้าย)
        assertTrue(ball.getXVelocity() < 0, "Should move left after hitting right wall");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ชนขอบบนต้องเด้งลง
    public void checkReachWall_givenTopWall_thenBouncesDown() {
        // ปล่อยมุม 0 (ขึ้นตรง)
        ball.launch(0);
        // ตั้ง y เหนือเส้นบน 10 px
        ball.setY(GameStage.TOP - 10);
        // ต้องคืน true
        assertTrue(ball.checkReachWall());
        // ความเร็วแกน Y ต้องเป็นบวก (ลง)
        assertTrue(ball.getYVelocity() > 0, "Should move down after hitting the top");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: อยู่กลางพื้นที่เล่นต้องไม่เด้ง
    public void checkReachWall_givenInsideArea_thenNoBounce() {
        // ปล่อยมุม 0
        ball.launch(0);
        // ต้องคืน false
        assertFalse(ball.checkReachWall());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ตีกลาง paddle ต้องพุ่งขึ้นตรง
    public void bounceOffPaddle_givenHitCenter_thenGoesStraightUp() {
        // ปล่อยมุม 0
        ball.launch(0);
        // ตั้งให้กำลังตกลง (Y = +6)
        ball.setVelocity(0, 6); // กำลังตกลง
        // วางลูกกึ่งกลางตรงกับ paddle
        ball.setX(paddle.getCenterX() - ball.getW() / 2);
        // วางลูกซ้อนบน paddle เล็กน้อย
        ball.setY(paddle.getY() - ball.getH() + 2);
        // ต้องเด้งสำเร็จ (true)
        assertTrue(ball.bounceOffPaddle(paddle));
        // ความเร็วแกน X ต้องเป็น 0 (ยอมคลาดเคลื่อน 0.01)
        assertEquals(0, ball.getXVelocity(), 0.01);
        // ความเร็วแกน Y ต้องเป็นลบ (ขึ้นบน)
        assertTrue(ball.getYVelocity() < 0);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ตีขอบขวาของ paddle ต้องพุ่งไปขวา
    public void bounceOffPaddle_givenHitRightEdge_thenGoesRight() {
        // ปล่อยมุม 0
        ball.launch(0);
        // ตั้งให้กำลังตกลง
        ball.setVelocity(0, 6);
        // วางลูกที่ขอบขวาของ paddle
        ball.setX(paddle.getX() + paddle.getW() - ball.getW());
        // วางลูกซ้อนบน paddle เล็กน้อย
        ball.setY(paddle.getY() - ball.getH() + 2);
        // ต้องเด้งสำเร็จ
        assertTrue(ball.bounceOffPaddle(paddle));
        // ความเร็วแกน X ต้องเป็นบวก
        assertTrue(ball.getXVelocity() > 0);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ลูกกำลังขึ้นต้องไม่เด้งจาก paddle
    public void bounceOffPaddle_givenBallMovingUp_thenNoBounce() {
        // ปล่อยมุม 0 (กำลังขึ้น)
        ball.launch(0); // กำลังขึ้น
        // วางลูกตรง paddle
        ball.setX(paddle.getCenterX());
        // วางลูกที่ y ของ paddle
        ball.setY(paddle.getY());
        // ต้องคืน false (ไม่เด้ง)
        assertFalse(ball.bounceOffPaddle(paddle));
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ชนบล็อกจากด้านล่าง ต้องกลับทิศลง
    public void bounceOffBrick_givenHitFromBelow_thenYVelocityFlipsDown() {
        // สร้างบล็อกที่ 200,100
        Brick brick = new Brick(200, 100, BrickType.NORMAL);
        // ปล่อยลูกขึ้นตรง
        ball.launch(0); // ขึ้นตรง
        // วางลูก x = 220
        ball.setX(220);
        // วางลูกซ้อนเข้าไปจากด้านล่างของบล็อกนิดเดียว
        ball.setY(brick.getY() + brick.getH() - 2); // ลูกจมเข้าไปจากด้านล่างบล็อกนิดเดียว
        // สั่งเด้งออกจากบล็อก
        ball.bounceOffBrick(brick);
        // ความเร็วแกน Y ต้องเป็นบวก (ลง)
        assertTrue(ball.getYVelocity() > 0, "Ball should now move down");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ชนด้านซ้ายของบล็อก ต้องกลับไปทางซ้าย
    public void bounceOffBrick_givenHitFromLeftSide_thenXVelocityFlipsLeft() {
        // สร้างบล็อกที่ 200,100
        Brick brick = new Brick(200, 100, BrickType.NORMAL);
        // ปล่อยลูกมุม 60 (ไปทางขวา)
        ball.launch(60); // ไปทางขวา
        // วางลูกซ้อนด้านซ้ายของบล็อก
        ball.setX(brick.getX() - ball.getW() + 2);
        // วางลูกให้อยู่ระดับความสูงของบล็อก
        ball.setY(brick.getY() + 4);
        // สั่งเด้งออกจากบล็อก
        ball.bounceOffBrick(brick);
        // ความเร็วแกน X ต้องเป็นลบ (ซ้าย)
        assertTrue(ball.getXVelocity() < 0, "Ball should now move left");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ลูกอยู่ใต้จอ isOut ต้องเป็น true
    public void isOut_givenBelowScreen_thenTrue() {
        // ตั้ง y เกินความสูงจอ
        ball.setY(GameStage.HEIGHT + 1);
        // ต้องคืน true
        assertTrue(ball.isOut());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ลูกอยู่ในจอ isOut ต้องเป็น false
    public void isOut_givenInsideScreen_thenFalse() {
        // ต้องคืน false
        assertFalse(ball.isOut());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: followPaddle ลูกต้องอยู่เหนือกึ่งกลาง paddle
    public void followPaddle_thenBallSitsAboveCenterOfPaddle() {
        // ให้ลูกตาม paddle
        ball.followPaddle(paddle);
        // กึ่งกลางแนวนอนของลูกต้องตรงกับ paddle
        assertEquals(paddle.getCenterX(), ball.getCenterX(), 0.0001);
        // ลูกต้องอยู่สูงกว่า paddle
        assertTrue(ball.getY() < paddle.getY());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: usePierce ลดค่าลงและไม่ต่ำกว่า 0
    public void usePierce_thenPierceLeftDecreases_andNeverBelowZero() {
        // ตั้งพลังทะลุ 1
        ball.setPierceLeft(1);
        // ใช้ 1 ครั้ง (เหลือ 0)
        ball.usePierce();
        // ใช้อีกครั้ง (ต้องยังเป็น 0 ไม่ติดลบ)
        ball.usePierce();
        // พลังทะลุต้องเป็น 0
        assertEquals(0, ball.getPierceLeft());
    }  // จบ test
}  // จบคลาส BallTest
