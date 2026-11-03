// ประกาศแพ็กเกจ controller
package se233.arkanoid.controller;

// นำเข้า Platform สำหรับ runLater
import javafx.application.Platform;
// นำเข้า LogManager
import org.apache.logging.log4j.LogManager;
// นำเข้า Logger
import org.apache.logging.log4j.Logger;
// นำเข้า ExceptionHandler
import se233.arkanoid.exception.ExceptionHandler;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า Ball (ลูกบอล)
import se233.arkanoid.model.Ball;
// นำเข้า Brick (บล็อก)
import se233.arkanoid.model.Brick;
// นำเข้า GameState
import se233.arkanoid.model.GameState;
// นำเข้า Laser
import se233.arkanoid.model.Laser;
// นำเข้า PowerUp (ไอเทมที่ตกลงมา)
import se233.arkanoid.model.PowerUp;
// นำเข้า GameStage
import se233.arkanoid.view.GameStage;

/*
 * [เกณฑ์ตาราง] Sprite sheet (สั่งเล่นอนิเมชัน), Additional animation (เอฟเฟกต์/กระพริบ), Display score (อัปเดต HUD)
 * DrawingLoop = เธรด "วาด" ของเกม อ้างอิง Script 4.10 / 4.28
 * หน้าที่: อ่านข้อมูลที่ GameLoop คำนวณไว้ แล้วสั่งให้ภาพบนจอตรงกับข้อมูลนั้น ไม่คำนวณกฎเกมเลย
 *
 * [เธรด] กฎ JavaFX: แก้ภาพที่อยู่บนจอได้เฉพาะบน JavaFX Application Thread
 * เธรดนี้ไม่ใช่ JavaFX Thread → ใช้ Platform.runLater(...) = "ฝากงานไปต่อคิวให้ JavaFX Thread ทำ"
 * แล้วเธรดนี้ไป sleep รอเฟรมถัดไปได้เลยไม่ต้องรอ
 */
// DrawingLoop implements Runnable จึงส่งให้ Thread รันได้
public class DrawingLoop implements Runnable {
    // logger ประจำคลาส
    private static final Logger logger = LogManager.getLogger(DrawingLoop.class);
    // อัตราวาด 60 เฟรมต่อวินาที
    private static final int FRAME_RATE = 60;
    // เปลี่ยนภาพ sprite ทุก 4 เฟรม
    private static final int SPRITE_EVERY = 4; // เปลี่ยนเฟรม sprite ทุก 4 เฟรมจอ (60/4 = 15 ภาพ/วินาที ไม่เร็วเกิน)

    // เวทีเกมที่จะสั่งอัปเดตภาพ
    private final GameStage gameStage;
    // GameLoop ไว้ถามสถานะเกมและเวลาเลเซอร์ที่เหลือ
    private final GameLoop gameLoop;
    // GameState ไว้เอาคะแนนไปแสดงบน HUD
    private final GameState state;
    // เวลา (มิลลิวินาที) ต่อ 1 เฟรม
    private final float interval;
    // ธงบอกว่าลูปยังรันอยู่ไหม
    private boolean running = true; // อ่าน/เขียนผ่านเมธอด synchronized
    // ตัวนับจำนวนเฟรมที่วาดไปแล้ว
    private long frameCount = 0;

    // สร้างลูปวาด: รับเวที (GameStage), GameLoop (ถามสถานะ/เวลาเลเซอร์ที่เหลือ) และ GameState (เอาคะแนนไปโชว์ HUD) ตั้งอัตราวาด 60 เฟรม/วินาที
    // constructor รับเวที, GameLoop, GameState
    public DrawingLoop(GameStage gameStage, GameLoop gameLoop, GameState state) {
        // เก็บเวทีลงตัวแปรของ object
        this.gameStage = gameStage;
        // เก็บ GameLoop
        this.gameLoop = gameLoop;
        // เก็บ GameState
        this.state = state;
        // คำนวณ interval = 1000 / 60 ≈ 16.67 ms
        this.interval = 1000.0f / FRAME_RATE;
    }  // จบ constructor

    // วาด 1 เฟรม (ทุกอย่างข้างในรันบน JavaFX Thread)
    // เมธอด paint วาด 1 เฟรม
    private void paint() {
        // เพิ่มตัวนับเฟรม
        frameCount++;
        // หยุดเกมอยู่ = ไม่เล่นอนิเมชัน (ภาพนิ่ง)
        // animate เป็น true เมื่อถึงรอบเปลี่ยนเฟรม sprite และเกมอยู่สถานะ PLAYING
        boolean animate = frameCount % SPRITE_EVERY == 0 && gameLoop.getStatus() == GameStatus.PLAYING;
        // ฝากงานวาดไปให้ JavaFX Thread ทำ
        Platform.runLater(() -> {
          // เริ่ม try ดัก GameException ในงานที่ฝากไว้
          try { // try-catch ในงานที่ฝากให้ JavaFX Thread (เหมือน Script 2.54) เพื่อไม่ให้ error เงียบหาย
            // ให้ paddle ย้ายภาพไปตำแหน่งปัจจุบัน
            gameStage.getPaddle().repaint();
            // ถ้าถึงรอบ animate ให้ paddle เปลี่ยนเฟรม sprite
            if (animate) gameStage.getPaddle().updateSprite();

            // วนทุกลูกบอลในเกม (ได้สำเนาลิสต์)
            for (Ball ball : gameStage.getBalls()) {
                // ย้ายภาพลูกบอลตามตำแหน่งจริง
                ball.repaint();
                // เล่นเฟรมถัดไปของลูกบอลเมื่อถึงรอบ
                if (animate) ball.updateSprite();
            }  // จบลูปบอล
            // วนทุกบล็อก
            for (Brick brick : gameStage.getBricks()) {
                // ให้บล็อกวาดตัวเองใหม่ (รูปตาม HP, กระพริบ, ซ่อนเมื่อแตก)
                brick.repaint(); // เปลี่ยนรูปตาม HP + กระพริบตอนโดนตี + ซ่อนถ้าแตก
            }  // จบลูปบล็อก
            // วนทุกเลเซอร์
            for (Laser laser : gameStage.getLasers()) {
                // ย้ายภาพเลเซอร์
                laser.repaint();
                // เล่นเฟรมถัดไปของเลเซอร์
                if (animate) laser.updateSprite();
            }  // จบลูปเลเซอร์
            // วนทุกไอเทมที่ตกลงมา
            for (PowerUp powerUp : gameStage.getPowerUps()) {
                // ย้ายภาพไอเทม
                powerUp.repaint();
                // เล่นเฟรมถัดไปของไอเทม
                if (animate) powerUp.updateSprite();
            }  // จบลูปไอเทม
            // อัปเดตเอฟเฟกต์ (ระเบิด/ตัวเลขลอย) และลบอันที่หมดอายุ
            gameStage.updateEffects();
            // อัปเดต HUD ด้วยคะแนน ชีวิต ด่าน และเวลาเลเซอร์ที่เหลือ
            gameStage.getHud().update(state, gameLoop.getLaserSecondsLeft());
          // ถ้าเกิด GameException ระหว่างวาด
          } catch (GameException e) {
            // ส่งให้ ExceptionHandler จัดการที่จุดเดียว
            ExceptionHandler.handle(e); // จัดการ GameException ที่จุดเดียวกับทุกที่
          }  // จบ catch
        });  // จบ lambda ของ runLater
    }  // จบเมธอด paint

    // ลูปหลัก: วาด → วัดเวลา → sleep ส่วนที่เหลือ (โครงเดียวกับ GameLoop.run)
    // เขียนทับ run() ของ Runnable เมื่อ Thread start จะเรียกเมธอดนี้
    @Override
    // เมธอด run คือลูปหลักของเธรดวาด
    public void run() {
        // วนต่อไปตราบที่ยังไม่ถูกสั่งหยุด
        while (isRunning()) {
            // จดเวลาเริ่มของเฟรมนี้
            long time = System.currentTimeMillis();
            // เรียก paint เพื่อวาดเฟรม
            paint();
            // คำนวณเวลาที่ใช้วาด (เวลาตอนนี้ลบเวลาเริ่ม)
            time = System.currentTimeMillis() - time;
            // เริ่ม try เพราะ sleep อาจเกิด InterruptedException
            try {
                // sleep เวลาที่เหลือของเฟรม (อย่างน้อย 1 ms) เพื่อคุมให้ได้ ~60 FPS
                Thread.sleep((long) Math.max(1, interval - time));
            // ถ้าถูกขัดจังหวะระหว่าง sleep
            } catch (InterruptedException e) {
                // ห่อเป็น GameException แล้วส่งให้ ExceptionHandler จุดเดียว (เหมือน GameLoop)
                // ห่อเป็น GameException ชนิด THREAD_INTERRUPTED แล้วส่งให้ ExceptionHandler
                ExceptionHandler.handle(new GameException(GameException.ErrorType.THREAD_INTERRUPTED,
                        // ข้อความอธิบายและ exception ต้นเหตุ
                        "DrawingLoop thread was interrupted", e));
                // สั่งหยุดลูปของตัวเอง
                stop();
            }  // จบ catch
        }  // จบ while
    }  // จบเมธอด run

    // stop() ตั้งธงเป็น false เรียกจากเธรดอื่นได้เพราะเป็น synchronized
    public synchronized void stop() { running = false; }  // สั่งหยุดลูป (เรียกจากเธรดอื่นได้ เพราะ synchronized)
    // isRunning() อ่านธงแบบ synchronized เพื่อให้เห็นค่าล่าสุดข้ามเธรด
    private synchronized boolean isRunning() { return running; }  // ถามว่าลูปควรวนต่อไหม (อ่านค่าแบบ synchronized)
}  // จบคลาส DrawingLoop
