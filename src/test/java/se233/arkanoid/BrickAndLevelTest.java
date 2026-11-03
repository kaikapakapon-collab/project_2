// ประกาศแพ็กเกจของ test
package se233.arkanoid;

// นำเข้า @BeforeAll
import org.junit.jupiter.api.BeforeAll;
// นำเข้า @Test
import org.junit.jupiter.api.Test;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า Brick
import se233.arkanoid.model.Brick;
// นำเข้า BrickType
import se233.arkanoid.model.BrickType;
// นำเข้า Level
import se233.arkanoid.model.Level;

// นำเข้า List
import java.util.List;

// นำเข้า assert ทั้งหมดแบบ static
import static org.junit.jupiter.api.Assertions.*;

/*
 * [เกณฑ์ตาราง] Testing (บล็อก HP/ชนิด และผังด่าน)
 * ทดสอบบล็อก (HP / ชนิดต่าง ๆ) และการสร้างผังด่าน
 */
// คลาสทดสอบ Brick, BrickType และ Level
public class BrickAndLevelTest {

    // ทำครั้งเดียวก่อน test ทั้งคลาส
    @BeforeAll
    // เมธอดเริ่มระบบ JavaFX
    public static void initJfxRuntime() {
        // เรียกตัวช่วยเปิด JavaFX Toolkit
        TestSupport.initJfx();
    }  // จบเมธอด

    // ---------- Brick ----------
    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกธรรมดาแตกในการตี 1 ครั้ง
    public void hit_givenNormalBrick_thenDestroyedInOneHit() {
        // สร้างบล็อก NORMAL
        Brick brick = new Brick(0, 0, BrickType.NORMAL);
        // hit ต้องคืน true (แตก)
        assertTrue(brick.hit());
        // ต้องอยู่ในสถานะแตก
        assertTrue(brick.isDestroyed());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกแข็งต้องตี 2 ครั้ง
    public void hit_givenStrongBrick_thenNeedsTwoHits() {
        // สร้างบล็อก STRONG
        Brick brick = new Brick(0, 0, BrickType.STRONG);
        // ตีครั้งแรกแค่ร้าว ต้องคืน false
        assertFalse(brick.hit(), "First hit only cracks the brick");
        // ต้องยังไม่แตก
        assertFalse(brick.isDestroyed());
        // HP ต้องเหลือ 1
        assertEquals(1, brick.getHitPoints());
        // ตีครั้งที่ 2 ต้องแตก (true)
        assertTrue(brick.hit(), "Second hit destroys it");
        // ต้องอยู่ในสถานะแตก
        assertTrue(brick.isDestroyed());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกแข็งมากต้องตี 3 ครั้ง
    public void hit_givenHardBrick_thenNeedsThreeHits() {
        // สร้างบล็อก HARD
        Brick brick = new Brick(0, 0, BrickType.HARD);
        // ตีครั้งที่ 1 ยังไม่แตก
        assertFalse(brick.hit());
        // ตีครั้งที่ 2 ยังไม่แตก
        assertFalse(brick.hit());
        // ตีครั้งที่ 3 แตก
        assertTrue(brick.hit());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกทองตีเท่าไรก็ไม่แตก
    public void hit_givenUnbreakableBrick_thenNeverDestroyed() {
        // สร้างบล็อก UNBREAKABLE
        Brick brick = new Brick(0, 0, BrickType.UNBREAKABLE);
        // ตี 50 ครั้ง ทุกครั้งต้องคืน false
        for (int i = 0; i < 50; i++) assertFalse(brick.hit());
        // ต้องยังไม่แตก
        assertFalse(brick.isDestroyed());
        // ต้องไม่นับเป็นบล็อกที่ต้องทำลายเพื่อผ่านด่าน
        assertFalse(brick.isRequired(), "Gold bricks do not block stage clear");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกที่แตกแล้วตีซ้ำต้องคืน false
    public void hit_givenAlreadyDestroyed_thenReturnsFalse() {
        // สร้างบล็อก NORMAL
        Brick brick = new Brick(0, 0, BrickType.NORMAL);
        // ตีให้แตก
        brick.hit();
        // ตีซ้ำต้องคืน false (กันคะแนนซ้ำ)
        assertFalse(brick.hit(), "Destroyed brick cannot be destroyed twice (no double score)");
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: destroy ทำลายบล็อกแข็งได้ทันที
    public void destroy_givenStrongBrick_thenDestroyedImmediately() {
        // สร้างบล็อก HARD
        Brick brick = new Brick(0, 0, BrickType.HARD);
        // destroy ต้องคืน true
        assertTrue(brick.destroy());
        // ต้องอยู่ในสถานะแตก
        assertTrue(brick.isDestroyed());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: destroy ใช้กับบล็อกทองไม่ได้
    public void destroy_givenUnbreakableBrick_thenStillNotDestroyed() {
        // สร้างบล็อก UNBREAKABLE
        Brick brick = new Brick(0, 0, BrickType.UNBREAKABLE);
        // destroy ต้องคืน false
        assertFalse(brick.destroy());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกที่แข็งกว่าต้องให้คะแนนมากกว่า
    public void scoreValue_givenStrongerBrick_thenHigherScore() {
        // คะแนนของ HARD
        assertTrue(new Brick(0, 0, BrickType.HARD).getScoreValue()
                // ต้องมากกว่าคะแนนของ NORMAL
                > new Brick(0, 0, BrickType.NORMAL).getScoreValue());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกที่ซ้อนกันต้องชนกัน
    public void collidesWith_givenOverlappingBricks_thenTrue() {
        // บล็อก a ที่ 0,0
        Brick a = new Brick(0, 0, BrickType.NORMAL);
        // บล็อก b ที่ซ้อนกับ a
        Brick b = new Brick(10, 5, BrickType.NORMAL);
        // บล็อก far ที่อยู่ไกลออกไป
        Brick far = new Brick(300, 300, BrickType.NORMAL);
        // a ชน b ต้องเป็น true
        assertTrue(a.collidesWith(b));
        // a ชน far ต้องเป็น false
        assertFalse(a.collidesWith(far));
    }  // จบ test

    // ---------- BrickType ----------
    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: สัญลักษณ์ที่รู้จักต้องแปลงเป็นชนิดที่ถูกต้อง
    public void fromSymbol_givenKnownSymbols_thenReturnsMatchingType() {
        // '1' ต้องเป็น NORMAL
        assertEquals(BrickType.NORMAL, BrickType.fromSymbol('1'));
        // 'X' ต้องเป็น EXPLOSIVE
        assertEquals(BrickType.EXPLOSIVE, BrickType.fromSymbol('X'));
        // 'U' ต้องเป็น UNBREAKABLE
        assertEquals(BrickType.UNBREAKABLE, BrickType.fromSymbol('U'));
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: สัญลักษณ์ที่ไม่รู้จักต้องได้ null
    public void fromSymbol_givenUnknownSymbol_thenNull() {
        // '?' ต้องได้ null
        assertNull(BrickType.fromSymbol('?'));
    }  // จบ test

    // ---------- Level ----------
    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ทุกด่านต้องมีบล็อกที่ทำลายได้
    public void build_givenEveryStage_thenHasBreakableBricks() {
        // วนทุกด่าน
        for (int stage = 1; stage <= Level.TOTAL_STAGES; stage++) {
            // สร้างบล็อกของด่านนั้น
            List<Brick> bricks = Level.build(stage);
            // ต้องไม่ว่างเปล่า
            assertFalse(bricks.isEmpty(), "Stage " + stage + " must not be empty");
            // ต้องมีบล็อกที่ต้องทำลายอย่างน้อย 1 ก้อน (ผ่านด่านได้)
            assertTrue(bricks.stream().anyMatch(Brick::isRequired), "Stage " + stage + " must be clearable");
        }  // จบลูป
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ด่าน 0 ต้องโยน INVALID_LEVEL
    public void build_givenStage0_thenThrowsInvalidLevel() {
        // สร้างด่าน 0 ต้องโยน GameException
        GameException e = assertThrows(GameException.class, () -> Level.build(0));
        // ชนิดต้องเป็น INVALID_LEVEL
        assertEquals(GameException.ErrorType.INVALID_LEVEL, e.getType());
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: ด่านเกินจำนวนต้องโยน GameException
    public void build_givenStageTooHigh_thenThrowsInvalidLevel() {
        // สร้างด่านที่เกินด่านสุดท้ายต้องโยน GameException
        assertThrows(GameException.class, () -> Level.build(Level.TOTAL_STAGES + 1));
    }  // จบ test

    // ระบุว่าเมธอดถัดไปคือ test (JUnit 5 จะรันให้อัตโนมัติ)
    @Test
    // test: บล็อกทุกก้อนต้องอยู่ในจอ
    public void build_thenAllBricksInsideScreen() {
        // วนทุกด่าน
        for (int stage = 1; stage <= Level.TOTAL_STAGES; stage++) {
            // วนทุกบล็อกของด่านนั้น
            for (Brick b : Level.build(stage)) {
                // x ต้องไม่เป็นลบและขอบขวาต้องไม่เกิน 600
                assertTrue(b.getX() >= 0 && b.getX() + b.getW() <= 600, "Brick outside screen in stage " + stage);
            }  // จบลูปบล็อก
        }  // จบลูปด่าน
    }  // จบ test
}  // จบคลาส BrickAndLevelTest
