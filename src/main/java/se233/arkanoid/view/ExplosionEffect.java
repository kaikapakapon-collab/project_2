// ประกาศแพ็กเกจ view
package se233.arkanoid.view;

// นำเข้า AnimatedSprite
import se233.arkanoid.model.AnimatedSprite;
// นำเข้า AssetLoader
import se233.arkanoid.model.AssetLoader;

/*
 * [เกณฑ์ตาราง] Additional animation (ระเบิดตอนบล็อกแตก), Sprite sheet (explosion.png)
 * ExplosionEffect = อนิเมชันระเบิดตอนบล็อกแตก (explosion.png 6 เฟรม)
 * เปลี่ยนเฟรมทุก 3 เฟรมของจอ → ระเบิดยาว 6 x 3 = 18 เฟรม ≈ 0.3 วินาที
 */
// ExplosionEffect เอฟเฟกต์ระเบิด สืบทอดจาก Effect
public class ExplosionEffect extends Effect {
    // ขนาดภาพระเบิด 48 px
    private static final int SIZE = 48;
    // มี 6 เฟรม
    private static final int FRAMES = 6;
    // เปลี่ยนเฟรมทุก 3 เฟรมจอ
    private static final int TICKS_PER_FRAME = 3;

    // sprite ของการระเบิด
    private final AnimatedSprite sprite;
    // อายุ (เล่นมาแล้วกี่เฟรมจอ)
    private int age = 0; // เล่นมาแล้วกี่เฟรมจอ

    // (centerX, centerY) = จุดกึ่งกลางระเบิด
    // constructor รับจุดศูนย์กลางของการระเบิด
    public ExplosionEffect(double centerX, double centerY) {
        // สร้าง sprite จาก explosion.png (6 เฟรมเรียงแนวนอน)
        sprite = new AnimatedSprite(AssetLoader.load("explosion.png"), FRAMES, FRAMES, 1, 0, 0, SIZE, SIZE);
        // ใส่ sprite ลงใน Pane
        getChildren().add(sprite);
        // ย้ายตำแหน่ง X ให้กึ่งกลางภาพตรงจุดระเบิด
        setTranslateX(centerX - SIZE / 2.0);
        // ย้ายตำแหน่ง Y ให้กึ่งกลางภาพตรงจุดระเบิด
        setTranslateY(centerY - SIZE / 2.0);
        // ไม่รับเมาส์ (ไม่บังการคลิก)
        setMouseTransparent(true);
    }  // จบ constructor

    // เล่น 1 เฟรมจอ: ทุก 3 เฟรมจอเปลี่ยนรูประเบิด 1 เฟรม ครบ 6 เฟรมคืน false ให้ลบทิ้ง
    // เขียนทับ update ของ Effect
    @Override
    // เมธอด update ถูกเรียกทุกเฟรมจอ
    public boolean update() {
        // เพิ่มอายุ 1
        age++;
        // ถ้าอายุหาร 3 ลงตัว (ถึงรอบเปลี่ยนเฟรม)
        if (age % TICKS_PER_FRAME == 0) {
            // เลื่อน sprite ไปเฟรมถัดไป
            sprite.tick();
        }  // จบ if
        // คืน true ถ้ายังเล่นไม่ครบทุกเฟรม (false = หมดอายุ)
        return age < FRAMES * TICKS_PER_FRAME;
    }  // จบ update
}  // จบคลาส ExplosionEffect
