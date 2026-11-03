// ประกาศแพ็กเกจของ test
package se233.arkanoid;

// นำเข้า @BeforeEach (ทำก่อนทุก test)
import org.junit.jupiter.api.BeforeEach;
// นำเข้า @Test (ระบุว่าเมธอดนี้คือ test)
import org.junit.jupiter.api.Test;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า GameState (คลาสที่ทดสอบ)
import se233.arkanoid.model.GameState;

// นำเข้าเมธอด assert ทั้งหมดแบบ static (assertEquals, assertTrue ฯลฯ)
import static org.junit.jupiter.api.Assertions.*;

/*
 * [เกณฑ์ตาราง] Testing (คะแนน, คอมโบ, ชีวิต, ด่าน)
 * ทดสอบ GameState: คะแนน, คอมโบ, ชีวิต, ด่าน (ไม่ต้องใช้ JavaFX เลย)
 * ตั้งชื่อ test แบบ method_givenXxx_thenYyy ตามชีทบทที่ 5
 */
// คลาสทดสอบ GameState
public class GameStateTest {
    // ตัวแปรเก็บ GameState ที่ใช้ทดสอบ
    private GameState state;

    // รันก่อนทุก test
    @BeforeEach
    // เมธอด setUp
    public void setUp() {
        // สร้าง GameState ใหม่สะอาดทุกครั้ง
        state = new GameState();
    }  // จบ setUp

    // ระบุว่าเป็น test
    @Test
    // test: เกมใหม่เริ่มด้วย 3 ชีวิต 0 คะแนน ด่าน 1
    public void newGame_thenStartsWith3Lives_Score0_Stage1() {
        // ชีวิตต้องเป็น 3
        assertEquals(3, state.getLives(), "Initial lives");
        // คะแนนต้องเป็น 0
        assertEquals(0, state.getScore(), "Initial score");
        // ด่านต้องเป็น 1
        assertEquals(1, state.getStage(), "Initial stage");
        // ยังไม่เกมโอเวอร์
        assertFalse(state.isGameOver());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: บล็อกแรกได้คะแนนเท่าฐาน (ยังไม่มีโบนัสคอมโบ)
    public void addBrickScore_givenFirstBrick_thenScoreIncreasesByBaseScore() {
        // เพิ่มคะแนนบล็อกฐาน 100
        int points = state.addBrickScore(100);
        // คะแนนที่ได้ต้องเป็น 100
        assertEquals(100, points);
        // คะแนนรวมต้องเป็น 100
        assertEquals(100, state.getScore());
        // คอมโบต้องเป็น 1
        assertEquals(1, state.getCombo());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: คอมโบต่อเนื่องทำให้ตัวคูณเพิ่ม
    public void addBrickScore_givenCombo_thenMultiplierIncreases() {
        // บล็อกที่ 1 ได้ 100 (×1.0)
        state.addBrickScore(100);                 // combo 1 → x1.0 = 100
        // บล็อกที่ 2 ได้ 110 (×1.1)
        int second = state.addBrickScore(100);    // combo 2 → x1.1 = 110
        // บล็อกที่ 3 ได้ 120 (×1.2)
        int third = state.addBrickScore(100);     // combo 3 → x1.2 = 120
        // บล็อกที่ 2 ต้องได้ 110
        assertEquals(110, second);
        // บล็อกที่ 3 ต้องได้ 120
        assertEquals(120, third);
        // คะแนนรวม 100+110+120 = 330
        assertEquals(330, state.getScore());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: คอมโบยาวมากตัวคูณไม่เกิน ×2
    public void comboMultiplier_givenVeryLongCombo_thenCappedAtX2() {
        // เพิ่มคะแนน 30 ครั้งติดกัน
        for (int i = 0; i < 30; i++) state.addBrickScore(10);
        // ตัวคูณต้องไม่เกิน 2.0 (ยอมคลาดเคลื่อน 0.0001)
        assertEquals(2.0, state.getComboMultiplier(), 0.0001);
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: รีเซ็ตคอมโบแล้วบล็อกถัดไปไม่มีโบนัส
    public void resetCombo_thenNextBrickHasNoBonus() {
        // เพิ่มคะแนนครั้งที่ 1
        state.addBrickScore(100);
        // เพิ่มคะแนนครั้งที่ 2
        state.addBrickScore(100);
        // รีเซ็ตคอมโบ
        state.resetCombo();
        // บล็อกถัดไปต้องได้แค่ 100
        assertEquals(100, state.addBrickScore(100));
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: คะแนนตีโดนเพิ่มคะแนนแต่ไม่เพิ่มคอมโบ
    public void addHitScore_thenScoreIncreasesButComboDoesNot() {
        // เพิ่มคะแนนตีโดน
        state.addHitScore();
        // คะแนนต้องเท่า HIT_SCORE
        assertEquals(GameState.HIT_SCORE, state.getScore());
        // คอมโบต้องยังเป็น 0
        assertEquals(0, state.getCombo());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: เสียชีวิตแล้วชีวิตลดลง 1
    public void loseLife_thenLivesDecreaseByOne() {
        // ลดชีวิต 1 ครั้ง
        state.loseLife();
        // ชีวิตต้องเหลือ 2
        assertEquals(2, state.getLives());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: ชีวิตหมดแล้วเป็นเกมโอเวอร์
    public void loseLife_givenAllLivesLost_thenGameOver() {
        // ลดชีวิตครั้งที่ 1
        state.loseLife();
        // ลดชีวิตครั้งที่ 2
        state.loseLife();
        // ลดชีวิตครั้งที่ 3
        state.loseLife();
        // ชีวิตต้องเป็น 0
        assertEquals(0, state.getLives());
        // ต้องเป็นเกมโอเวอร์
        assertTrue(state.isGameOver());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: ลดชีวิตตอนไม่เหลือแล้วต้องโยน GameException
    public void loseLife_givenNoLivesLeft_thenThrowsGameException() {
        // ลดชีวิตจนหมด 3 ครั้ง
        for (int i = 0; i < 3; i++) state.loseLife();
        // ลดชีวิตอีกครั้งต้องโยน GameException และเก็บ exception ไว้ตรวจ
        GameException e = assertThrows(GameException.class, () -> state.loseLife());
        // ชนิดต้องเป็น INVALID_STATE
        assertEquals(GameException.ErrorType.INVALID_STATE, e.getType());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: เสียชีวิตแล้วคอมโบถูกรีเซ็ต
    public void loseLife_thenComboIsReset() {
        // เพิ่มคะแนนครั้งที่ 1
        state.addBrickScore(100);
        // เพิ่มคะแนนครั้งที่ 2 (คอมโบ 2)
        state.addBrickScore(100);
        // เสียชีวิต
        state.loseLife();
        // คอมโบต้องกลับเป็น 0
        assertEquals(0, state.getCombo());
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: ขึ้นด่านถัดไปแล้วลูกเร็วขึ้น
    public void nextStage_thenBallGetsFaster() {
        // เก็บความเร็วลูกด่าน 1
        double stage1 = state.getBallSpeed();
        // ขึ้นด่านถัดไป
        state.nextStage();
        // ด่านต้องเป็น 2
        assertEquals(2, state.getStage());
        // ความเร็วลูกต้องมากกว่าด่าน 1
        assertTrue(state.getBallSpeed() > stage1, "Ball should be faster on stage 2");
    }  // จบ test

    // ระบุว่าเป็น test
    @Test
    // test: reset แล้วทุกอย่างกลับเป็นค่าเริ่มต้น
    public void reset_thenEverythingReturnsToDefault() {
        // เพิ่มคะแนน
        state.addBrickScore(500);
        // เสียชีวิต
        state.loseLife();
        // ขึ้นด่าน
        state.nextStage();
        // เพิ่มชีวิต
        state.addLife();
        // เพิ่มตัวคูณคะแนน
        state.addScoreMultiplier(1.0);
        // รีเซ็ตทุกอย่าง
        state.reset();
        // คะแนนต้องเป็น 0
        assertEquals(0, state.getScore());
        // ชีวิตต้องเป็น 3
        assertEquals(3, state.getLives());
        // ด่านต้องเป็น 1
        assertEquals(1, state.getStage());
        // ตัวคูณคะแนนต้องเป็น 1.0
        assertEquals(1.0, state.getScoreMultiplier(), 0.0001);
    }  // จบ test
}  // จบคลาส GameStateTest
