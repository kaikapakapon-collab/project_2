// ประกาศแพ็กเกจของ test
package se233.arkanoid;

// นำเข้า KeyCode
import javafx.scene.input.KeyCode;
// นำเข้า @BeforeAll
import org.junit.jupiter.api.BeforeAll;
// นำเข้า @BeforeEach
import org.junit.jupiter.api.BeforeEach;
// นำเข้า @Test
import org.junit.jupiter.api.Test;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า Keys
import se233.arkanoid.model.Keys;
// นำเข้า Paddle
import se233.arkanoid.model.Paddle;
// นำเข้า GameStage
import se233.arkanoid.view.GameStage;

// นำเข้า assert ทั้งหมดแบบ static
import static org.junit.jupiter.api.Assertions.*;

/*
 * [เกณฑ์ตาราง] Testing (paddle, Keys, GameException)
 * ทดสอบ paddle (ขยับ / ชนขอบจอ / ปรับขนาดจากอัปเกรด), Keys และ GameException
 */
// คลาสทดสอบ Paddle, Keys และ GameException
public class PaddleAndExceptionTest {
    // paddle ที่ใช้ทดสอบ
    private Paddle paddle;

    // ทำครั้งเดียวก่อน test ทั้งคลาส
    @BeforeAll
    // เมธอดเริ่มระบบ JavaFX
    public static void initJfxRuntime() {
        // เรียกตัวช่วยเปิด JavaFX Toolkit
        TestSupport.initJfx();
    }  // จบเมธอด

    // ทำก่อนทุก test
    @BeforeEach
    // เมธอด setUp
    public void setUp() {
        // สร้าง paddle ที่ 250,640 ปุ่มลูกศรซ้าย/ขวา
        paddle = new Paddle(250, 640, KeyCode.LEFT, KeyCode.RIGHT);
    }  // จบ setUp

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เลื่อนซ้ายแล้ว move ตำแหน่งลดลงเท่าความเร็ว
    public void moveLeft_thenMove_thenXDecreasesBySpeed() {
        // ตั้งให้เลื่อนซ้าย
        paddle.moveLeft();
        // ขยับ 1 ก้าว
        paddle.move();
        // x ต้องเป็น 250 − ความเร็ว
        assertEquals(250 - paddle.getSpeed(), paddle.getX(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เลื่อนขวาแล้ว move ตำแหน่งเพิ่มขึ้นเท่าความเร็ว
    public void moveRight_thenMove_thenXIncreasesBySpeed() {
        // ตั้งให้เลื่อนขวา
        paddle.moveRight();
        // ขยับ 1 ก้าว
        paddle.move();
        // x ต้องเป็น 250 + ความเร็ว
        assertEquals(250 + paddle.getSpeed(), paddle.getX(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: สั่งหยุดแล้ว move ตำแหน่งต้องไม่เปลี่ยน
    public void stop_thenMove_thenXDoesNotChange() {
        // ตั้งให้เลื่อนขวา
        paddle.moveRight();
        // สั่งหยุด
        paddle.stop();
        // ขยับ 1 ก้าว
        paddle.move();
        // x ต้องคงเดิม 250
        assertEquals(250, paddle.getX(), 0.0001);
        // ต้องไม่อยู่ในสถานะขยับ
        assertFalse(paddle.isMoving());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เลยขอบซ้ายต้องถูกดึงกลับเป็น 0
    public void checkReachWall_givenBeyondLeft_thenClampedToZero() {
        // ตั้ง x เป็น -30
        paddle.setX(-30);
        // เช็กกำแพง
        paddle.checkReachWall();
        // x ต้องเป็น 0
        assertEquals(0, paddle.getX(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เลยขอบขวาต้องถูกดึงกลับให้ชิดขอบขวา
    public void checkReachWall_givenBeyondRight_thenClampedToRightEdge() {
        // ตั้ง x เท่าความกว้างจอ
        paddle.setX(GameStage.WIDTH);
        // เช็กกำแพง
        paddle.checkReachWall();
        // x ต้องเป็นความกว้างจอ − ความกว้าง paddle
        assertEquals(GameStage.WIDTH - paddle.getW(), paddle.getX(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: paddle กว้างขึ้นต้องขยายจากกึ่งกลาง
    public void applyStats_givenWiderPaddle_thenGrowsFromCenter() {
        // เก็บจุดกึ่งกลางก่อนขยาย
        double centerBefore = paddle.getCenterX();
        // ปรับเป็นกว้าง 150 เร็ว 10
        paddle.applyStats(150, 10);
        // ความกว้างต้องเป็น 150
        assertEquals(150, paddle.getW(), 0.0001);
        // ความเร็วต้องเป็น 10
        assertEquals(10, paddle.getSpeed(), 0.0001);
        // จุดกึ่งกลางต้องคงเดิม
        assertEquals(centerBefore, paddle.getCenterX(), 0.0001);
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: เปิด/ปิดโหมดเลเซอร์ต้องทำงานถูก
    public void setLaserMode_thenIsLaserMode() {
        // เริ่มต้นต้องไม่ใช่โหมดเลเซอร์
        assertFalse(paddle.isLaserMode());
        // เปิดโหมดเลเซอร์
        paddle.setLaserMode(true);
        // ต้องเป็นโหมดเลเซอร์
        assertTrue(paddle.isLaserMode());
    }  // จบ test

    // ---------- Keys ----------
    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: กดแล้วปล่อยปุ่ม isPressed ต้องตามสถานะ
    public void keys_givenPressedThenReleased_thenIsPressedFollows() {
        // สร้าง Keys
        Keys keys = new Keys();
        // เริ่มต้นปุ่ม A ต้องไม่ถูกกด
        assertFalse(keys.isPressed(KeyCode.A));
        // บันทึกว่ากดปุ่ม A
        keys.add(KeyCode.A);
        // ต้องถูกกด
        assertTrue(keys.isPressed(KeyCode.A));
        // บันทึกว่าปล่อยปุ่ม A
        keys.remove(KeyCode.A);
        // ต้องไม่ถูกกดแล้ว
        assertFalse(keys.isPressed(KeyCode.A));
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: consume ต้องคืน true แค่ครั้งเดียวต่อการกด
    public void keys_consume_thenReturnsTrueOnlyOncePerPress() {
        // สร้าง Keys
        Keys keys = new Keys();
        // กดปุ่ม ENTER
        keys.add(KeyCode.ENTER);
        // consume ครั้งแรกต้องได้ true
        assertTrue(keys.consume(KeyCode.ENTER));
        // consume ซ้ำต้องได้ false (กดค้างไม่นับซ้ำ)
        assertFalse(keys.consume(KeyCode.ENTER), "Holding the key must not trigger twice");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ไม่เคยกดแล้ว consume ต้องได้ false
    public void keys_consume_givenNeverPressed_thenFalse() {
        // consume ปุ่ม P ที่ไม่เคยกดต้องได้ false
        assertFalse(new Keys().consume(KeyCode.P));
    }  // จบ test

    // ---------- GameException ----------
    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: GameException ต้องเก็บชนิดและข้อความ
    public void gameException_thenKeepsTypeAndMessage() {
        // สร้าง GameException ชนิด ASSET_NOT_FOUND
        GameException e = new GameException(GameException.ErrorType.ASSET_NOT_FOUND, "missing.png");
        // getType ต้องตรง
        assertEquals(GameException.ErrorType.ASSET_NOT_FOUND, e.getType());
        // getMessage ต้องตรง
        assertEquals("missing.png", e.getMessage());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: GameException ต้องเก็บ cause ไว้
    public void gameException_givenCause_thenCauseIsKept() {
        // สร้าง exception ต้นเหตุ
        RuntimeException cause = new RuntimeException("root cause");
        // ห่อด้วย GameException พร้อม cause
        GameException e = new GameException(GameException.ErrorType.INVALID_STATE, "wrapped", cause);
        // getCause ต้องเป็น object เดียวกัน
        assertSame(cause, e.getCause());
    }  // จบ test
}  // จบคลาส PaddleAndExceptionTest
