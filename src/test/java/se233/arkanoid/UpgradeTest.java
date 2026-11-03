// ประกาศแพ็กเกจของ test
package se233.arkanoid;

// นำเข้า @BeforeEach
import org.junit.jupiter.api.BeforeEach;
// นำเข้า @Test
import org.junit.jupiter.api.Test;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า GameState
import se233.arkanoid.model.GameState;
// นำเข้า Paddle (เอาค่าเริ่มต้นไปเทียบ)
import se233.arkanoid.model.Paddle;
// นำเข้าทุกคลาสในแพ็กเกจ upgrade
import se233.arkanoid.model.upgrade.*;

// นำเข้า HashSet
import java.util.HashSet;
// นำเข้า List
import java.util.List;
// นำเข้า Random
import java.util.Random;
// นำเข้า Set
import java.util.Set;

// นำเข้า assert ทั้งหมดแบบ static
import static org.junit.jupiter.api.Assertions.*;

/*
 * [เกณฑ์ตาราง] Testing (พฤติกรรมอัปเกรด + การสุ่มตัวเลือก)
 * ทดสอบระบบอัปเกรด roguelike: ผลของแต่ละอัปเกรดต่อ GameState และการสุ่มตัวเลือก
 */
// คลาสทดสอบระบบอัปเกรด
public class UpgradeTest {
    // GameState ที่ใช้ทดสอบ
    private GameState state;

    // รันก่อนทุก test
    @BeforeEach
    // เมธอด setUp
    public void setUp() {
        // สร้าง GameState ใหม่ทุกครั้ง
        state = new GameState();
    }  // จบ setUp

    // ระบุว่าเป็น test
    @Test
    // test: Wide Paddle เพิ่มความกว้าง 25
    public void widePaddle_thenPaddleWidthIncreasesBy25() {
        // ใช้อัปเกรด
        new WidePaddleUpgrade().apply(state);
        // ความกว้างต้องเป็นค่าเริ่มต้น + 25
        assertEquals(Paddle.DEFAULT_WIDTH + 25, state.getPaddleWidth(), 0.0001);
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: ใช้ Wide Paddle หลายครั้งต้องไม่เกิน 200
    public void widePaddle_givenManyTimes_thenCappedAt200() {
        // ใช้อัปเกรด 20 ครั้ง
        for (int i = 0; i < 20; i++) new WidePaddleUpgrade().apply(state);
        // ความกว้างต้องหยุดที่ 200
        assertEquals(200, state.getPaddleWidth(), 0.0001);
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Swift Paddle ทำให้เร็วขึ้น
    public void fastPaddle_thenSpeedIncreases() {
        // ใช้อัปเกรด
        new FastPaddleUpgrade().apply(state);
        // ความเร็วต้องมากกว่าค่าเริ่มต้น
        assertTrue(state.getPaddleSpeed() > Paddle.DEFAULT_SPEED);
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Extra Life ทำให้ชีวิตเป็น 4
    public void extraLife_thenLivesIs4() {
        // ใช้อัปเกรด
        new ExtraLifeUpgrade().apply(state);
        // ชีวิตต้องเป็น 4
        assertEquals(4, state.getLives());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Twin Ball เพิ่มลูกเสริมและจำกัดสูงสุด 4
    public void multiBall_thenExtraBallsIncrease_andCappedAt4() {
        // ใช้อัปเกรดครั้งแรก
        new MultiBallUpgrade().apply(state);
        // ลูกเสริมต้องเป็น 1
        assertEquals(1, state.getExtraBalls());
        // ใช้เพิ่มอีก 10 ครั้ง
        for (int i = 0; i < 10; i++) new MultiBallUpgrade().apply(state);
        // ต้องหยุดที่ 4
        assertEquals(4, state.getExtraBalls());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Piercing Ball ได้ทะลุ 2 ก้อน
    public void piercing_thenPierceIs2() {
        // ใช้อัปเกรด
        new PiercingBallUpgrade().apply(state);
        // ค่า pierce ต้องเป็น 2
        assertEquals(2, state.getPierce());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Score Boost ทำให้บล็อกให้คะแนนมากขึ้น
    public void scoreBoost_thenBrickGivesMoreScore() {
        // ใช้อัปเกรด
        new ScoreBoostUpgrade().apply(state);
        // บล็อกฐาน 100 ต้องได้ 150 (×1.5)
        assertEquals(150, state.addBrickScore(100)); // x1.5
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Slow Ball ทำให้ลูกช้าลง
    public void slowBall_thenBallSpeedIsLower() {
        // เก็บความเร็วก่อนใช้
        double before = state.getBallSpeed();
        // ใช้อัปเกรด
        new SlowBallUpgrade().apply(state);
        // ความเร็วต้องน้อยลง
        assertTrue(state.getBallSpeed() < before);
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: ใช้ Slow Ball หลายครั้งต้องไม่ช้ากว่าขั้นต่ำ
    public void slowBall_givenManyTimes_thenNotSlowerThanMinimum() {
        // ใช้ 30 ครั้ง
        for (int i = 0; i < 30; i++) new SlowBallUpgrade().apply(state);
        // ตัวคูณต้องหยุดที่ 0.6
        assertEquals(0.6, state.getBallSpeedFactor(), 0.0001);
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: Laser Mastery ทำให้เลเซอร์นานขึ้น
    public void laserMastery_thenLaserLastsLonger() {
        // เก็บวินาทีเลเซอร์ก่อนใช้
        int before = state.getLaserSeconds();
        // ใช้อัปเกรด
        new LaserBoostUpgrade().apply(state);
        // ต้องเพิ่มขึ้น 5 วินาที
        assertEquals(before + 5, state.getLaserSeconds());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: สุ่ม 3 ใบต้องได้ 3 ใบที่ไม่ซ้ำกัน
    public void randomChoices_given3_thenReturns3DifferentUpgrades() {
        // สร้าง UpgradeManager ด้วย seed 1
        UpgradeManager manager = new UpgradeManager(new Random(1));
        // สุ่ม 3 ใบ
        List<Upgrade> choices = manager.randomChoices(3);
        // ต้องได้ 3 ใบ
        assertEquals(3, choices.size());
        // สร้างเซตเก็บชื่อ
        Set<String> names = new HashSet<>();
        // ใส่ชื่อทุกใบลงเซต
        choices.forEach(u -> names.add(u.getName()));
        // เซตต้องมี 3 ชื่อ (แปลว่าไม่ซ้ำ)
        assertEquals(3, names.size(), "Choices must not repeat");
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: seed เดียวกันต้องได้ผลเหมือนกัน
    public void randomChoices_givenSameSeed_thenSameResult() {
        // สุ่มด้วย seed 42 ชุดที่ 1
        List<Upgrade> a = new UpgradeManager(new Random(42)).randomChoices(3);
        // สุ่มด้วย seed 42 ชุดที่ 2
        List<Upgrade> b = new UpgradeManager(new Random(42)).randomChoices(3);
        // วนเทียบทีละใบ
        for (int i = 0; i < 3; i++) {
            // ชื่อต้องตรงกัน
            assertEquals(a.get(i).getName(), b.get(i).getName());
        }  // จบลูป
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: ขอมากกว่ากองต้องโยน GameException
    public void randomChoices_givenMoreThanPool_thenThrowsGameException() {
        // สร้าง UpgradeManager
        UpgradeManager manager = new UpgradeManager(new Random());
        // ขอ 99 ใบต้องโยน GameException
        GameException e = assertThrows(GameException.class, () -> manager.randomChoices(99));
        // ชนิดต้องเป็น INVALID_UPGRADE
        assertEquals(GameException.ErrorType.INVALID_UPGRADE, e.getType());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: เลือกเลขนอกช่วงต้องโยน GameException
    public void select_givenInvalidIndex_thenThrowsGameException() {
        // สุ่มการ์ด 3 ใบ
        List<Upgrade> choices = new UpgradeManager(new Random()).randomChoices(3);
        // เลือกเลข 3 (เกินช่วง) ต้องโยน GameException
        assertThrows(GameException.class, () -> UpgradeManager.select(choices, 3));
        // เลือกเลข -1 ต้องโยน GameException
        assertThrows(GameException.class, () -> UpgradeManager.select(choices, -1));
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: เลือกเลขถูกต้องต้องได้อัปเกรดใบนั้น
    public void select_givenValidIndex_thenReturnsThatUpgrade() {
        // สุ่มการ์ด 3 ใบ
        List<Upgrade> choices = new UpgradeManager(new Random()).randomChoices(3);
        // ใบที่เลือกต้องเป็น object เดียวกับใบที่ 2 ในลิสต์
        assertSame(choices.get(1), UpgradeManager.select(choices, 1));
    }  // จบ test
}  // จบคลาส UpgradeTest
