// ประกาศแพ็กเกจ controller
package se233.arkanoid.controller;

// นำเข้า KeyCode
import javafx.scene.input.KeyCode;
// นำเข้า Color
import javafx.scene.paint.Color;
// นำเข้า LogManager
import org.apache.logging.log4j.LogManager;
// นำเข้า Logger
import org.apache.logging.log4j.Logger;
// นำเข้า ExceptionHandler
import se233.arkanoid.exception.ExceptionHandler;
// นำเข้า GameException
import se233.arkanoid.exception.GameException;
// นำเข้า Ball
import se233.arkanoid.model.Ball;
// นำเข้า Brick
import se233.arkanoid.model.Brick;
// นำเข้า BrickType
import se233.arkanoid.model.BrickType;
// นำเข้า GameState
import se233.arkanoid.model.GameState;
// นำเข้า Laser
import se233.arkanoid.model.Laser;
// นำเข้า Level
import se233.arkanoid.model.Level;
// นำเข้า Paddle
import se233.arkanoid.model.Paddle;
// นำเข้า PowerUp
import se233.arkanoid.model.PowerUp;
// นำเข้า Upgrade
import se233.arkanoid.model.upgrade.Upgrade;
// นำเข้า UpgradeManager
import se233.arkanoid.model.upgrade.UpgradeManager;
// นำเข้า GameStage
import se233.arkanoid.view.GameStage;

// นำเข้า List
import java.util.List;
// นำเข้า Random
import java.util.Random;

/*
 * [เกณฑ์ตาราง] Functionalities (กฎเกมทั้งหมด), Logging (หลายระดับ), Exception handling (catch GameException → handle), Additional features (คอมโบ + ระเบิดลูกโซ่)
 * GameLoop = เธรด "คิด" ของเกม (Controller ใน MVC) ต่อยอดจาก Script 4.15 / 4.23 / 4.29
 *
 * [เธรด] เกมนี้มี 3 เธรดทำงานขนานกัน:
 *   1) JavaFX Application Thread → รับปุ่ม/เมาส์ + วาดภาพจริง
 *   2) GameLoop Thread (ไฟล์นี้)  → กฎของเกมทั้งหมด: ขยับ, ชน, คะแนน, ชีวิต, ด่าน, อัปเกรด (~60 รอบ/วินาที)
 *   3) DrawingLoop Thread        → สั่งอัปเดตภาพตามข้อมูลล่าสุด (~60 รอบ/วินาที)
 * ไฟล์นี้ไม่แตะภาพตรง ๆ เลย ถ้าต้องเปลี่ยนหน้าจอจะเรียกเมธอดของ GameStage ที่ห่อ Platform.runLater ไว้แล้ว
 *
 * Log หลายระดับ: DEBUG = รายละเอียดการชน, INFO = เหตุการณ์/คะแนน, WARN = เสียชีวิต/แพ้, ERROR = exception
 */
// GameLoop คือสมองของเกม: เธรดที่คำนวณกฎทั้งหมด 60 ครั้งต่อวินาที (implements Runnable)
public class GameLoop implements Runnable {
    // logger ประจำคลาส
    private static final Logger logger = LogManager.getLogger(GameLoop.class);

    // อัตราเฟรมของกฎเกม 60 Hz
    public static final int FRAME_RATE = 60;
    // ยิงเลเซอร์ได้ทุก 15 เฟรม (4 ครั้ง/วินาที)
    private static final int LASER_COOLDOWN = 15;        // ยิงเลเซอร์ได้ทุก 15 เฟรม (4 ครั้ง/วินาที)
    // โอกาสที่บล็อกทั่วไปดรอปไอเทม 10%
    private static final double DROP_CHANCE = 0.10;       // บล็อกทั่วไปดรอปไอเทม 10%
    // รัศมีระเบิด 62 px (ครอบคลุมบล็อกติดกันทั้ง 8 ทิศ)
    private static final double EXPLOSION_RADIUS = 62;    // รัศมีระเบิด (ครอบคลุมบล็อกติดกันทั้ง 8 ทิศ)
    // จำนวนการ์ดอัปเกรดที่แจก 3 ใบ
    private static final int UPGRADE_CHOICES = 3;

    // เวทีเกม
    private final GameStage gameStage;
    // สถานะผู้เล่น
    private final GameState state;
    // ตัวสุ่ม/แจกอัปเกรด
    private final UpgradeManager upgradeManager;
    // ตัวสุ่ม (ใช้สุ่มดรอปไอเทม)
    private final Random random;
    // เวลาต่อ 1 เฟรม (ms)
    private final float interval;

    // [เธรด] DrawingLoop/JavaFX Thread อ่านค่าเหล่านี้ด้วย → การเขียน/อ่านข้ามเธรดทำผ่านเมธอด synchronized
    // (setStatus / getStatus / setLaserTicks / getLaserSecondsLeft / stop / isRunning ท้ายไฟล์)
    // สถานะเกมปัจจุบัน เริ่มที่หน้าเมนู
    private GameStatus status = GameStatus.MENU;
    // เวลาเลเซอร์ที่เหลือ (หน่วยเฟรม, 0 = ไม่มีเลเซอร์)
    private int laserTicks = 0;               // เลเซอร์เหลือกี่เฟรม (0 = ไม่มี)
    // ธงบอกว่าลูปยังรันอยู่
    private boolean running = true;
    // ตัวนับคูลดาวน์การยิงเลเซอร์
    private int laserCooldown = 0;
    // ตัวนับเฟรมก่อนซ่อนข้อความกลางจออัตโนมัติ (0 = ไม่ซ่อนเอง)
    private int messageTicks = 0;             // ข้อความกลางจอเหลืออีกกี่เฟรมจะซ่อน (0 = ไม่ซ่อนเอง)
    // คะแนนสูงสุดของรอบที่เล่นมา
    private int bestScore = 0;
    // การ์ดอัปเกรดที่กำลังแสดงอยู่
    private List<Upgrade> currentChoices = List.of();

    // [เธรด] การคลิกการ์ดอัปเกรดเกิดบน JavaFX Thread แต่ GameLoop เป็นคนใช้ค่า
    // → ตัวแปรนี้แตะผ่านเมธอด synchronized เท่านั้น (requestUpgrade / takeRequestedUpgrade / clearRequestedUpgrade)
    // เลขการ์ดที่ผู้เล่นคลิก (-1 = ยังไม่มี) JavaFX Thread เขียน GameLoop Thread อ่าน
    private int requestedUpgrade = -1;   // -1 = ยังไม่มีใครคลิก

    // สร้างลูปกฎเกม: รับเวที, GameState (คะแนน/ชีวิต/ด่าน), ตัวสุ่มอัปเกรด และตัวสุ่มทั่วไป (ตอน test ใส่ seed ได้) ตั้งอัตรา 60 รอบ/วินาที
    // constructor รับของที่ต้องใช้ทั้งหมด
    public GameLoop(GameStage gameStage, GameState state, UpgradeManager upgradeManager, Random random) {
        // เก็บเวที
        this.gameStage = gameStage;
        // เก็บ GameState
        this.state = state;
        // เก็บ UpgradeManager
        this.upgradeManager = upgradeManager;
        // เก็บ Random
        this.random = random;
        // คำนวณเวลาต่อเฟรม = 1000/60 ms
        this.interval = 1000.0f / FRAME_RATE;
    }  // จบ constructor

    // โชว์ข้อความกลางจอ แล้วตั้งเวลานับถอยหลังเป็น "เฟรม" ให้ซ่อนเอง (millis = 0 → ค้างไว้)
    // นับเฟรมเองใน GameLoop แทนการใช้ตัวจับเวลาของ JavaFX (เหมือนที่ชีทใช้ลูป + sleep เป็นนาฬิกา)
    // say แสดงข้อความกลางจอ แล้วตั้งเวลาซ่อน (millis = 0 หมายถึงไม่ซ่อนเอง)
    private void say(String title, String subtitle, Color color, int millis) {
        // สั่งเวทีให้แสดงข้อความ
        gameStage.showMessage(title, subtitle, color);
        // แปลงมิลลิวินาทีเป็นจำนวนเฟรม
        messageTicks = millis * FRAME_RATE / 1000;
    }  // จบ say

    // ======================================================================
    //  1 เฟรม: เลือกงานตามสถานะ (if/else ตามสถานะใน GameStatus)
    // ======================================================================
    // tick ทำงาน 1 เฟรมของเกม แยกตามสถานะ
    private void tick() {
        // ถ้ามีข้อความที่ต้องซ่อนเองอยู่
        if (messageTicks > 0) {          // นับถอยหลังเวลาโชว์ข้อความ
            // ลดตัวนับ
            messageTicks--;
            // ถ้านับถึง 0 ให้ซ่อนข้อความ
            if (messageTicks == 0) gameStage.hideMessage();
        }  // จบ if

        // ถ้าอยู่หน้าเมนู
        if (status == GameStatus.MENU) {
            // ถ้ากด ENTER (อ่านแล้วล้างค่า) เริ่มรอบใหม่
            if (gameStage.getKeys().consume(KeyCode.ENTER)) startNewRun();
        // ถ้ากำลังเล่น
        } else if (status == GameStatus.PLAYING) {
            // ถ้ากด P
            if (gameStage.getKeys().consume(KeyCode.P)) {
                // เปลี่ยนสถานะเป็นหยุดชั่วคราว
                setStatus(GameStatus.PAUSED);
                // แสดงข้อความ PAUSED
                say("PAUSED", "Press P to resume", Color.WHITE, 0);
                // บันทึก log ว่าหยุดเกม
                logger.info("Game paused");
            // ถ้าไม่ได้กด P
            } else {
                // อัปเดตกฎการเล่นปกติ
                updatePlaying();
            }  // จบ if ภายใน
        // ถ้าหยุดอยู่
        } else if (status == GameStatus.PAUSED) {
            // ถ้ากด P อีกครั้ง
            if (gameStage.getKeys().consume(KeyCode.P)) {
                // กลับเป็นสถานะกำลังเล่น
                setStatus(GameStatus.PLAYING);
                // ซ่อนข้อความ PAUSED
                gameStage.hideMessage();
                // บันทึก log ว่าเล่นต่อ
                logger.info("Game resumed");
            }  // จบ if
        // ถ้าอยู่หน้าเลือกอัปเกรด
        } else if (status == GameStatus.UPGRADE) {
            // จัดการการเลือกการ์ด
            handleUpgradeSelection();
        // ถ้าเป็นสถานะอื่น (GAME_OVER หรือ WIN) รอ ENTER กลับเมนู
        } else { // GAME_OVER หรือ WIN → รอ ENTER กลับเมนู
            // ถ้ากด ENTER กลับหน้าเมนู
            if (gameStage.getKeys().consume(KeyCode.ENTER)) backToMenu();
        }  // จบ if-else chain
    }  // จบ tick

    // ======================================================================
    //  เริ่มเกม / โหลดด่าน
    // ======================================================================

    // เริ่ม run ใหม่ตั้งแต่ด่าน 1 (ค่าอัปเกรดรีเซ็ตหมด)
    // startNewRun เริ่มรอบใหม่ทั้งหมด
    private void startNewRun() {
        // รีเซ็ต GameState (คะแนน ชีวิต ด่าน ฯลฯ)
        state.reset();
        // ซ่อนหน้าเมนู
        gameStage.hideMenu();
        // ซ่อนข้อความกลางจอ
        gameStage.hideMessage();
        // โหลดด่านปัจจุบัน
        loadStage();
        // เปลี่ยนสถานะเป็นกำลังเล่น
        setStatus(GameStatus.PLAYING);
        // บันทึก log ว่าเริ่มรอบใหม่
        logger.info("New run started");
    }  // จบ startNewRun

    // โหลดด่านปัจจุบัน: บล็อกใหม่ + พื้นหลัง + ล้างของเก่า + ใส่ค่าอัปเกรดให้ paddle + ลูกใหม่บน paddle
    // loadStage โหลดด่านตามค่า stage ใน GameState
    private void loadStage() {
        // อ่านเลขด่านปัจจุบัน
        int stage = state.getStage();
        // สร้างบล็อกของด่านนั้น (ถ้าผิดจะโยน GameException)
        List<Brick> bricks = Level.build(stage); // ด่านผิด → GameException ไปจบที่ run()
        // ใส่บล็อกลงเวที
        gameStage.loadBricks(bricks);
        // เปลี่ยนภาพพื้นหลัง
        gameStage.setStageBackground(stage);
        // ล้างลูกบอลเก่า
        gameStage.clearBalls();
        // ล้างเลเซอร์เก่า
        gameStage.clearLasers();
        // ล้างไอเทมเก่า
        gameStage.clearPowerUps();
        // ปิดเลเซอร์
        setLaser(0);
        // รีเซ็ตคอมโบ
        state.resetCombo();

        // หยิบ paddle
        Paddle paddle = gameStage.getPaddle();
        // ปรับความกว้าง/ความเร็วตามอัปเกรดที่สะสมไว้
        paddle.applyStats(state.getPaddleWidth(), state.getPaddleSpeed());
        // วาง paddle กลางจอ
        paddle.resetPosition();
        // วางลูกบอลติดบน paddle
        spawnBallOnPaddle();

        // แสดงข้อความชื่อด่าน 1.8 วินาที
        say("STAGE " + stage, "Press SPACE to launch", Color.WHITE, 1800);
        // บันทึก log จำนวนบล็อกและความเร็วลูก
        logger.info("Stage {} loaded: {} bricks, ball speed {}", stage, bricks.size(),
                // ส่วนต่อของ log: ความเร็วลูกทศนิยม 2 ตำแหน่ง
                String.format("%.2f", state.getBallSpeed()));
    }  // จบ loadStage

    // สร้างลูกใหม่เกาะบน paddle
    // spawnBallOnPaddle สร้างลูกที่ติดอยู่บน paddle รอปล่อย
    private void spawnBallOnPaddle() {
        // สร้างลูกด้วยความเร็วตามด่าน
        Ball ball = new Ball(0, 0, state.getBallSpeed());
        // ให้ลูกวางบน paddle
        ball.followPaddle(gameStage.getPaddle());
        // เพิ่มลูกเข้าเวที
        gameStage.addBall(ball);
    }  // จบ spawnBallOnPaddle

    // สร้างลูกที่ปล่อยแล้ว ณ ตำแหน่ง (x, y) พุ่งไปทางมุม angle (ใช้กับ multi-ball)
    // spawnLaunchedBall สร้างลูกที่ถูกปล่อยออกไปแล้วตามมุมที่กำหนด
    private void spawnLaunchedBall(double x, double y, double angle) {
        // สร้างลูกที่ตำแหน่ง x,y
        Ball ball = new Ball(x, y, state.getBallSpeed());
        // ปล่อยลูกตามมุม
        ball.launch(angle);
        // เติมพลังทะลุเริ่มต้นจากอัปเกรด
        ball.setPierceLeft(state.getPierce());
        // เพิ่มลูกเข้าเวที
        gameStage.addBall(ball);
    }  // จบ spawnLaunchedBall

    // ======================================================================
    //  ระหว่างเล่น
    // ======================================================================
    // updatePlaying อัปเดตทุกอย่างตอนกำลังเล่น 1 เฟรม
    private void updatePlaying() {
        // ขยับ paddle ตามปุ่ม
        updatePaddle();
        // จัดการเลเซอร์ของผู้เล่น (นับเวลา/ยิง)
        updateLaser();
        // อัปเดตลูกบอลทั้งหมด
        updateBalls();
        // ถ้าสถานะไม่ใช่ PLAYING แล้ว (เช่นเกมโอเวอร์) ออกจากเมธอดทันที
        if (status != GameStatus.PLAYING) return; // เพิ่งเสียชีวิตจน game over
        // ขยับเลเซอร์และเช็กการชน
        updateLasers();
        // ขยับไอเทมที่ตกลงมา
        updatePowerUps();
        // เช็กว่าเคลียร์ด่านหรือยัง
        checkStageClear();
    }  // จบ updatePlaying

    // อ่านปุ่ม → ตั้งความเร็ว → ขยับ → กันออกนอกจอ (โครงเดียวกับ Script 4.23)
    // updatePaddle ตั้งความเร็ว paddle ตามปุ่มที่กด แล้วขยับ
    private void updatePaddle() {
        // หยิบ paddle
        Paddle paddle = gameStage.getPaddle();
        // ถามว่าปุ่มซ้ายถูกกดอยู่ไหม
        boolean leftPressed  = gameStage.getKeys().isPressed(paddle.getLeftKey());
        // ถามว่าปุ่มขวาถูกกดอยู่ไหม
        boolean rightPressed = gameStage.getKeys().isPressed(paddle.getRightKey());
        // ถ้ากดทั้งสองปุ่มพร้อมกัน
        if (leftPressed && rightPressed) {
            // หยุด paddle
            paddle.stop();
        // ถ้ากดซ้ายอย่างเดียว
        } else if (leftPressed) {
            // เลื่อนไปซ้าย
            paddle.moveLeft();
        // ถ้ากดขวาอย่างเดียว
        } else if (rightPressed) {
            // เลื่อนไปขวา
            paddle.moveRight();
        // ถ้าไม่ได้กดอะไร
        } else {
            // หยุด paddle
            paddle.stop();
        }  // จบ if-else chain
        // ขยับ paddle ตามความเร็วที่ตั้ง
        paddle.move();
        // กันไม่ให้หลุดขอบจอ
        paddle.checkReachWall();
    }  // จบ updatePaddle

    // นับเวลาเลเซอร์ถอยหลัง + ยิงเลเซอร์คู่เมื่อกด SPACE (special attack)
    // updateLaser จัดการเวลาและการยิงเลเซอร์
    private void updateLaser() {
        // ถ้าไม่มีเลเซอร์ ออกจากเมธอด
        if (laserTicks <= 0) return;
        // ลดเวลาเลเซอร์ลง 1 เฟรม
        setLaserTicks(laserTicks - 1);
        // ลดคูลดาวน์การยิง
        if (laserCooldown > 0) laserCooldown--;
        // ถ้าเวลาหมดพอดี
        if (laserTicks == 0) {
            // ปิดเลเซอร์
            setLaser(0);
            // บันทึก log ว่าเลเซอร์หมดเวลา
            logger.info("Laser expired");
            // ออกจากเมธอด
            return;
        }  // จบ if
        // ถ้ากด SPACE และคูลดาวน์หมดแล้ว
        if (gameStage.getKeys().isPressed(KeyCode.SPACE) && laserCooldown == 0) {
            // หยิบ paddle
            Paddle p = gameStage.getPaddle();
            // คำนวณตำแหน่ง y ของกระสุนเหนือ paddle
            double y = p.getY() - Laser.HEIGHT;
            // ยิงเลเซอร์จากปืนซ้าย
            gameStage.addLaser(new Laser(p.getX() + 3, y));                      // ปืนซ้าย
            // ยิงเลเซอร์จากปืนขวา
            gameStage.addLaser(new Laser(p.getX() + p.getW() - 3 - Laser.WIDTH, y)); // ปืนขวา
            // เริ่มคูลดาวน์ใหม่
            laserCooldown = LASER_COOLDOWN;
            // บันทึก log การยิงเลเซอร์คู่
            logger.debug("Dual laser fired");
        }  // จบ if
    }  // จบ updateLaser

    // เปิด/ปิดเลเซอร์: ticks > 0 = เปิดตามจำนวนเฟรม, 0 = ปิด
    // setLaser ตั้งเวลาเลเซอร์ใหม่ (ticks = จำนวนเฟรม, 0 = ปิด)
    private void setLaser(int ticks) {
        // บันทึกเวลาเลเซอร์ (ผ่านเมธอด synchronized)
        setLaserTicks(ticks);
        // รีเซ็ตคูลดาวน์
        laserCooldown = 0;
        // เปิด/ปิดรูป paddle แบบมีปืน
        gameStage.getPaddle().setLaserMode(ticks > 0);
    }  // จบ setLaser

    // ทุกเฟรมกับลูกทุกลูก: ถ้ายังไม่ปล่อย → ตาม paddle (กด SPACE = ปล่อย) / ถ้าลอยอยู่ → ขยับ → เด้งกำแพง → เด้ง paddle → ชนบล็อก → ตกขอบล่างลบทิ้ง
    // ถ้าไม่เหลือลูกเลย = เสียชีวิต 1 ดวง
    // updateBalls อัปเดตลูกบอลทุกลูก
    private void updateBalls() {
        // หยิบ paddle
        Paddle paddle = gameStage.getPaddle();
        // ถามว่า SPACE ถูกกดอยู่ไหม (ไว้ปล่อยลูก)
        boolean launchPressed = gameStage.getKeys().isPressed(KeyCode.SPACE);

        // วนทุกลูกบอล (ลูปบนสำเนาลิสต์)
        for (Ball ball : gameStage.getBalls()) {
            // ยังไม่ปล่อย: เกาะ paddle, กด SPACE = ปล่อย
            // ถ้ายังไม่ถูกปล่อย
            if (!ball.isLaunched()) {
                // ให้ลูกติดตาม paddle
                ball.followPaddle(paddle);
                // ถ้ากด SPACE ให้ปล่อยลูก
                if (launchPressed) launchBall(ball);
                // ข้ามไปลูกถัดไป
                continue;
            }  // จบ if
            // ขยับลูก
            ball.move();
            // เช็กชนกำแพงแล้วเด้ง
            ball.checkReachWall();

            // ถ้าลูกเด้งจาก paddle
            if (ball.bounceOffPaddle(paddle)) {
                // รีเซ็ตคอมโบ เพราะกลับมาแตะ paddle
                state.resetCombo();                   // กลับมาโดน paddle = คอมโบหลุด
                // เติมพลังทะลุใหม่
                ball.setPierceLeft(state.getPierce()); // เติมพลังทะลุใหม่ทุกครั้งที่โดน paddle
                // บันทึก log ความเร็วหลังเด้ง
                logger.debug("Ball hit paddle, vx:{} vy:{}",
                        // ส่วนต่อของ log: ความเร็วแกน X,Y ทศนิยม 2 ตำแหน่ง
                        String.format("%.2f", ball.getXVelocity()), String.format("%.2f", ball.getYVelocity()));
            }  // จบ if

            // เช็กการชนกับบล็อก
            handleBallBrickCollision(ball);

            // ถ้าลูกตกลงล่างจอ
            if (ball.isOut()) {
                // เอาลูกออกจากเกม
                gameStage.removeBall(ball);
                // บันทึก log ตำแหน่งที่ลูกตก
                logger.debug("Ball fell out at x:{}", (int) ball.getX());
            }  // จบ if
        }  // จบลูปลูกบอล

        // ไม่เหลือลูกเลย = เสียชีวิต
        // ถ้าไม่มีลูกเหลือเลย
        if (gameStage.getBalls().isEmpty()) {
            // เสียชีวิต 1 ครั้ง
            loseLife();
        }  // จบ if
    }  // จบ updateBalls

    // ปล่อยลูก + แตกลูกเพิ่มตามอัปเกรด Twin Ball (ลูกเพิ่มเอียงสลับซ้าย/ขวา)
    // launchBall ปล่อยลูกที่ติดบน paddle
    private void launchBall(Ball ball) {
        // ปล่อยที่มุม 20 องศา
        ball.launch(20);
        // เติมพลังทะลุ
        ball.setPierceLeft(state.getPierce());
        // บันทึก log การปล่อยลูก
        logger.info("Ball launched at x:{}", (int) ball.getX());
        // วนสร้างลูกพิเศษตามอัปเกรด Twin Ball
        for (int i = 1; i <= state.getExtraBalls(); i++) {
            // คำนวณมุม: สลับซ้าย/ขวา และกางออกมากขึ้นตามลำดับ
            double angle = (i % 2 == 1 ? -1 : 1) * (15 + 12 * i);
            // สร้างลูกที่ถูกปล่อยออกไปแล้ว
            spawnLaunchedBall(ball.getX(), ball.getY(), angle);
        }  // จบลูป
    }  // จบ launchBall

    // ลูกชนบล็อก: ถ้ายังมีพลังทะลุ → ทะลุและทำลายเลย ไม่เด้ง / ไม่งั้น → เด้งและตี 1 ที
    // ชนได้ 1 ก้อนต่อเฟรม (break) ไม่งั้นชน 2 ก้อนพร้อมกันจะกลับทิศ 2 ครั้ง = ทะลุผ่าน
    // handleBallBrickCollision เช็กลูกชนบล็อก (ชนได้ 1 ก้อนต่อเฟรม)
    private void handleBallBrickCollision(Ball ball) {
        // วนทุกบล็อก
        for (Brick brick : gameStage.getBricks()) {
            // ข้ามบล็อกที่แตกแล้วหรือไม่ได้ชน
            if (brick.isDestroyed() || !ball.collidesWith(brick)) continue;
            // ถ้าลูกยังมีพลังทะลุและบล็อกนี้ทำลายได้
            if (ball.getPierceLeft() > 0 && brick.isRequired()) {
                // ใช้พลังทะลุ 1 ครั้ง
                ball.usePierce();
                // ทำลายบล็อกทันที ถ้าแตกให้ทำงานตอนบล็อกแตก
                if (brick.destroy()) onBrickDestroyed(brick);
                // บันทึก log การทะลุ
                logger.debug("Piercing ball went through brick, pierce left: {}", ball.getPierceLeft());
            // ถ้าไม่ใช่กรณีทะลุ
            } else {
                // ให้ลูกเด้งออกจากบล็อก
                ball.bounceOffBrick(brick);
                // สร้างความเสียหายให้บล็อก
                damageBrick(brick);
            }  // จบ if-else
            // หยุดเช็กบล็อกอื่น (1 ก้อนต่อเฟรม)
            break;
        }  // จบ for
    }  // จบ handleBallBrickCollision

    // ตีบล็อก 1 ที (จากลูกหรือเลเซอร์): แตก → onBrickDestroyed, ไม่แตก → ได้คะแนนเล็กน้อย
    // damageBrick ตีบล็อกหนึ่งครั้ง (จากลูกหรือเลเซอร์)
    private void damageBrick(Brick brick) {
        // ถ้าตีแล้วบล็อกแตก
        if (brick.hit()) {
            // ทำงานตอนบล็อกแตก (คะแนน เอฟเฟกต์ ดรอปไอเทม)
            onBrickDestroyed(brick);
        // ถ้ายังไม่แตกแต่เป็นบล็อกที่ทำลายได้
        } else if (brick.isRequired()) {
            // ให้คะแนนตีโดน
            int points = state.addHitScore();
            // แสดงตัวเลขคะแนนลอยสีเทา
            gameStage.spawnFloatingText(brick.getCenterX() - 10, brick.getY(), "+" + points, Color.LIGHTGRAY);
            // บันทึก log ความเสียหาย
            logger.debug("Brick {} damaged, hp left: {} (+{})", brick.getType(), brick.getHitPoints(), points);
        // ถ้าเป็นบล็อกตีไม่แตก
        } else {
            // บันทึก log ว่าตีบล็อกทอง
            logger.debug("Unbreakable brick hit");
        }  // จบ if
    }  // จบ damageBrick

    // บล็อกแตก: คิดคะแนน (มีคอมโบ) + เอฟเฟกต์ระเบิด + ตัวเลขลอย + อาจดรอปไอเทม + ถ้าเป็นบล็อกระเบิดก็ระเบิดต่อ
    // onBrickDestroyed จัดการทุกอย่างเมื่อบล็อกแตก
    private void onBrickDestroyed(Brick brick) {
        // เพิ่มคะแนน (นับคอมโบด้วย) ได้ค่าคะแนนจริงกลับมา
        int points = state.addBrickScore(brick.getScoreValue());
        // คำนวณจุดกึ่งกลาง X ของบล็อก
        double cx = brick.getCenterX();
        // คำนวณจุดกึ่งกลาง Y ของบล็อก
        double cy = brick.getY() + brick.getH() / 2;
        // สร้างเอฟเฟกต์ระเบิด
        gameStage.spawnExplosion(cx, cy);
        // เลือกสีตัวเลข: คอมโบ ≥ 5 เป็นสีส้ม ไม่งั้นสีเหลือง
        Color color = state.getCombo() >= 5 ? Color.web("#ff9f43") : Color.web("#ffd93d"); // คอมโบสูงตัวเลขเป็นสีส้ม
        // แสดงตัวเลขคะแนนลอย
        gameStage.spawnFloatingText(cx - 14, brick.getY(), "+" + points, color);
        // บันทึก log การแตกของบล็อก
        logger.info("Brick {} destroyed +{} (combo {}), score = {}",
                // ส่วนต่อของ log: ชนิด คะแนน คอมโบ คะแนนรวม
                brick.getType(), points, state.getCombo(), state.getScore());

        // สุ่มดรอปไอเทม
        maybeDropPowerUp(brick);
        // ถ้าเป็นบล็อกระเบิด
        if (brick.getType() == BrickType.EXPLOSIVE) {
            // ระเบิดบล็อกรอบ ๆ
            explode(brick);
        }  // จบ if
    }  // จบ onBrickDestroyed

    // ฟีเจอร์พิเศษ: บล็อกระเบิดทำลายบล็อกรอบตัว ถ้าโดนบล็อกระเบิดอีกก้อนจะระเบิดต่อเป็นลูกโซ่ (เรียกซ้ำ/recursion)
    // explode ระเบิดบล็อกรอบ ๆ (ระเบิดต่อกันเป็นลูกโซ่ได้)
    private void explode(Brick source) {
        // บันทึก log ตำแหน่งระเบิด
        logger.info("Explosion at x:{} y:{}", (int) source.getX(), (int) source.getY());
        // วนทุกบล็อก
        for (Brick other : gameStage.getBricks()) {
            // ข้ามตัวเองและบล็อกที่แตกแล้ว
            if (other == source || other.isDestroyed()) continue;
            // หาระยะห่างแนวนอนจากจุดระเบิด
            double dx = other.getCenterX() - source.getCenterX();
            // หาระยะห่างแนวตั้ง
            double dy = other.getY() - source.getY();
            // ถ้าอยู่ในรัศมีและทำลายสำเร็จ (บล็อกทองไม่แตก)
            if (Math.hypot(dx, dy) <= EXPLOSION_RADIUS && other.destroy()) {
                // เรียก onBrickDestroyed ต่อ ถ้าเป็นระเบิดจะเรียก explode ซ้อน (chain reaction)
                onBrickDestroyed(other); // ถ้า other เป็นบล็อกระเบิด จะเรียก explode ต่อ = chain reaction
            }  // จบ if
        }  // จบ for
    }  // จบ explode

    // บล็อกโบนัสดรอปไอเทมแน่นอน, บล็อกอื่นดรอป 10% (เลเซอร์ 60% / multi-ball 40%)
    // maybeDropPowerUp สุ่มดรอปไอเทม
    private void maybeDropPowerUp(Brick brick) {
        // ดรอปเสมอถ้าเป็นบล็อกโบนัส ไม่งั้นดรอป 10%
        boolean drop = brick.getType() == BrickType.BONUS || random.nextDouble() < DROP_CHANCE;
        // ถ้าไม่ดรอป ออกจากเมธอด
        if (!drop) return;
        // สุ่มชนิด: เลเซอร์ 60% แตกลูกเพิ่ม 40%
        PowerUp.Type type = random.nextDouble() < 0.6 ? PowerUp.Type.LASER : PowerUp.Type.MULTI;
        // สร้างไอเทมที่ตำแหน่งบล็อกแล้วเพิ่มเข้าเวที
        gameStage.addPowerUp(new PowerUp(brick.getCenterX() - PowerUp.WIDTH / 2, brick.getY(), type));
        // บันทึก log การดรอป
        logger.debug("Power-up {} dropped", type);
    }  // จบ maybeDropPowerUp

    // เลเซอร์พุ่งขึ้น: ชนบล็อก = ตีบล็อก 1 ทีแล้วหาย / หลุดขอบบน = หาย
    // updateLasers ขยับเลเซอร์ทุกนัดและเช็กการชน
    private void updateLasers() {
        // วนทุกนัด
        for (Laser laser : gameStage.getLasers()) {
            // ขยับขึ้น
            laser.move();
            // ถ้าพ้นขอบบน
            if (laser.isOut()) {
                // ลบออกจากเกม
                gameStage.removeLaser(laser);
                // ข้ามไปนัดถัดไป
                continue;
            }  // จบ if
            // วนทุกบล็อกเพื่อเช็กชน
            for (Brick brick : gameStage.getBricks()) {
                // ข้ามบล็อกที่แตกแล้วหรือไม่ได้ชน
                if (brick.isDestroyed() || !laser.collidesWith(brick)) continue;
                // ทำความเสียหายกับบล็อก
                damageBrick(brick);
                // ลบเลเซอร์นัดนี้ (ใช้แล้ว)
                gameStage.removeLaser(laser);
                // หยุดเช็กบล็อกอื่น
                break;
            }  // จบ for บล็อก
        }  // จบ for เลเซอร์
    }  // จบ updateLasers

    // ไอเทมหล่นลง: paddle รับได้ = ใช้งาน / หล่นเลยจอ = หาย
    // updatePowerUps ขยับไอเทมที่ตกลงมาและเช็กการเก็บ
    private void updatePowerUps() {
        // หยิบ paddle
        Paddle paddle = gameStage.getPaddle();
        // วนทุกไอเทม
        for (PowerUp powerUp : gameStage.getPowerUps()) {
            // ให้ไอเทมตกลง
            powerUp.move();
            // ถ้าชน paddle (เก็บได้)
            if (powerUp.collidesWith(paddle)) {
                // ลบไอเทมออกจากเกม
                gameStage.removePowerUp(powerUp);
                // เปิดใช้งานตามชนิดไอเทม
                activatePowerUp(powerUp.getType());
            // ถ้าตกพ้นจอ
            } else if (powerUp.isOut()) {
                // ลบไอเทมทิ้ง
                gameStage.removePowerUp(powerUp);
            }  // จบ if
        }  // จบ for
    }  // จบ updatePowerUps

    // ใช้ผลไอเทมที่ paddle รับได้: L = เปิดเลเซอร์คู่ตามเวลา (เพิ่มได้จากอัปเกรด), M = แตกลูกเพิ่ม 2 ลูกจากลูกที่ลอยอยู่
    // activatePowerUp เปิดผลของไอเทมที่เก็บได้
    private void activatePowerUp(PowerUp.Type type) {
        // ถ้าเป็นเลเซอร์
        if (type == PowerUp.Type.LASER) {
            // เปิดเลเซอร์ตามจำนวนวินาทีของผู้เล่น (วินาที × เฟรมต่อวินาที)
            setLaser(state.getLaserSeconds() * FRAME_RATE);
            // แสดงข้อความ LASER! 1 วินาที
            say("LASER!", "Hold SPACE to fire", Color.web("#ff6b6b"), 1000);
            // บันทึก log การเปิดเลเซอร์
            logger.info("Power-up LASER activated for {}s", state.getLaserSeconds());
        // ถ้าเป็น MULTI: แตกลูกเพิ่ม 2 ลูกจากลูกแรกที่ลอยอยู่
        } else { // MULTI: แตกลูกเพิ่ม 2 ลูกจากลูกแรกที่กำลังลอยอยู่
            // วนทุกลูก
            for (Ball ball : gameStage.getBalls()) {
                // ข้ามลูกที่ยังไม่ถูกปล่อย
                if (!ball.isLaunched()) continue;
                // สร้างลูกใหม่เอียงซ้าย 35 องศา
                spawnLaunchedBall(ball.getX(), ball.getY(), -35);
                // สร้างลูกใหม่เอียงขวา 35 องศา
                spawnLaunchedBall(ball.getX(), ball.getY(), 35);
                // ทำครั้งเดียวกับลูกแรกที่เจอแล้วออกลูป
                break;
            }  // จบ for
            // แสดงข้อความ MULTI BALL!
            say("MULTI BALL!", "", Color.web("#3fa9f5"), 800);
            // บันทึก log จำนวนลูกตอนนี้
            logger.info("Power-up MULTI activated, balls = {}", gameStage.getBalls().size());
        }  // จบ if-else
    }  // จบ activatePowerUp

    // ======================================================================
    //  ชีวิต / ผ่านด่าน / อัปเกรด
    // ======================================================================

    // เสียชีวิต: เหลือ 0 = GAME OVER, ยังเหลือ = ลูกใหม่บน paddle
    // loseLife จัดการตอนลูกบอลหมด (เสียชีวิต 1 ครั้ง)
    private void loseLife() {
        // ลดชีวิตใน GameState (และรีเซ็ตคอมโบ)
        state.loseLife();
        // ล้างเลเซอร์ที่ค้างอยู่
        gameStage.clearLasers();
        // ล้างไอเทมที่ค้างอยู่
        gameStage.clearPowerUps();
        // ปิดเลเซอร์
        setLaser(0);
        // บันทึก log ระดับ WARN ว่าเสียชีวิต พร้อมชีวิตที่เหลือ
        logger.warn("Life lost, {} lives left", state.getLives());

        // ถ้าชีวิตหมด
        if (state.isGameOver()) {
            // เปลี่ยนสถานะเป็นเกมโอเวอร์
            setStatus(GameStatus.GAME_OVER);
            // อัปเดตคะแนนสูงสุด
            bestScore = Math.max(bestScore, state.getScore());
            // แสดงข้อความ GAME OVER พร้อมคะแนน (ไม่ซ่อนเอง)
            say("GAME OVER", "Score: " + state.getScore() + "\nPress ENTER", Color.web("#ff6b6b"), 0);
            // บันทึก log เกมโอเวอร์พร้อมด่านและคะแนน
            logger.warn("GAME OVER at stage {} with score {}", state.getStage(), state.getScore());
        // ถ้ายังมีชีวิตเหลือ
        } else {
            // สร้างลูกใหม่บน paddle
            spawnBallOnPaddle();
            // แสดงข้อความ LIFE LOST 1.2 วินาที
            say("LIFE LOST", state.getLives() + " lives left", Color.web("#ff6b6b"), 1200);
        }  // จบ if-else
    }  // จบ loseLife

    // บล็อกที่ "ต้องทำลาย" หมดแล้ว (ไม่นับบล็อกทอง) = ผ่านด่าน
    // checkStageClear เช็กว่าเคลียร์ด่านหรือยัง
    private void checkStageClear() {
        // เคลียร์เมื่อไม่มีบล็อกที่ต้องทำลายเหลืออยู่ (lambda + stream noneMatch)
        boolean cleared = gameStage.getBricks().stream().noneMatch(b -> b.isRequired() && !b.isDestroyed());
        // ถ้ายังไม่เคลียร์ ออกจากเมธอด
        if (!cleared) return;

        // ล้างลูกบอล
        gameStage.clearBalls();
        // ล้างเลเซอร์
        gameStage.clearLasers();
        // ล้างไอเทม
        gameStage.clearPowerUps();
        // ปิดเลเซอร์
        setLaser(0);
        // บันทึก log การเคลียร์ด่าน
        logger.info("Stage {} cleared, score = {}", state.getStage(), state.getScore());

        // ถ้าเป็นด่านสุดท้าย
        if (state.getStage() >= Level.TOTAL_STAGES) {
            // เปลี่ยนสถานะเป็นชนะ
            setStatus(GameStatus.WIN);
            // อัปเดตคะแนนสูงสุด
            bestScore = Math.max(bestScore, state.getScore());
            // แสดงข้อความ YOU WIN! พร้อมคะแนนสุดท้าย
            say("YOU WIN!", "Final score: " + state.getScore() + "\nPress ENTER", Color.web("#ffd93d"), 0);
            // บันทึก log ชัยชนะ
            logger.info("Player won the game with score {}", state.getScore());
        // ถ้ายังไม่ใช่ด่านสุดท้าย
        } else {
            // สุ่มการ์ดอัปเกรด 3 ใบ
            currentChoices = upgradeManager.randomChoices(UPGRADE_CHOICES);
            // ล้างเลขการ์ดที่เคยคลิกค้างไว้
            clearRequestedUpgrade();
            // เปลี่ยนสถานะเป็นเลือกอัปเกรด
            setStatus(GameStatus.UPGRADE);
            // แสดงหน้าเลือกอัปเกรด
            gameStage.showUpgrades(state.getStage(), currentChoices);
            // บันทึก log ตัวเลือกอัปเกรด
            logger.info("Upgrade choices: {}", currentChoices);
        }  // จบ if-else
    }  // จบ checkStageClear

    // รอผู้เล่นเลือกการ์ด (ปุ่ม 1/2/3 หรือคลิก) แล้วใส่อัปเกรด → ไปด่านถัดไป
    // handleUpgradeSelection รับการเลือกอัปเกรดจากแป้นพิมพ์หรือการคลิก
    private void handleUpgradeSelection() {
        // ค่าเริ่มต้น -1 = ยังไม่เลือก
        int choice = -1;
        // กด 1 (แถวบนหรือ numpad) → เลือกใบที่ 0
        if (gameStage.getKeys().consume(KeyCode.DIGIT1) || gameStage.getKeys().consume(KeyCode.NUMPAD1)) choice = 0;
        // กด 2 → เลือกใบที่ 1
        if (gameStage.getKeys().consume(KeyCode.DIGIT2) || gameStage.getKeys().consume(KeyCode.NUMPAD2)) choice = 1;
        // กด 3 → เลือกใบที่ 2
        if (gameStage.getKeys().consume(KeyCode.DIGIT3) || gameStage.getKeys().consume(KeyCode.NUMPAD3)) choice = 2;
        // อ่านเลขการ์ดที่ผู้เล่นคลิก (อ่านแล้วล้างค่า)
        int clicked = takeRequestedUpgrade();
        // ถ้ามีการคลิก ให้ใช้เลขนั้น
        if (clicked >= 0) choice = clicked;
        // ถ้ายังไม่ได้เลือกอะไร ออกจากเมธอด
        if (choice < 0) return; // ยังไม่เลือก

        // หยิบอัปเกรดตามเลข (ถ้าเลขผิดจะโยน GameException)
        Upgrade upgrade = UpgradeManager.select(currentChoices, choice); // เลขผิด → GameException
        // ใช้อัปเกรดกับ GameState (polymorphism: apply ของแต่ละแบบต่างกัน)
        upgrade.apply(state);
        // บันทึก log อัปเกรดที่เลือก
        logger.info("Upgrade selected: {}", upgrade.getName());

        // ซ่อนหน้าเลือกอัปเกรด
        gameStage.hideUpgrades();
        // เลื่อนไปด่านถัดไป
        state.nextStage();
        // โหลดด่านใหม่
        loadStage();
        // แสดงข้อความชื่อด่านและอัปเกรดที่เลือก
        say("STAGE " + state.getStage(), "Upgrade: " + upgrade.getName() + "\nPress SPACE to launch",
                // ส่วนต่อ: สีฟ้า แสดง 2 วินาที
                Color.web("#3fa9f5"), 2000);
        // กลับไปสถานะกำลังเล่น
        setStatus(GameStatus.PLAYING);
    }  // จบ handleUpgradeSelection

    // กลับหน้าเมนู (หลังจบเกม หรือหลังเกิด error)
    // backToMenu กลับหน้าเมนู
    private void backToMenu() {
        // เปลี่ยนสถานะเป็นเมนู
        setStatus(GameStatus.MENU);
        // ซ่อนข้อความกลางจอ
        gameStage.hideMessage();
        // ซ่อนหน้าอัปเกรด
        gameStage.hideUpgrades();
        // ล้างลูกบอล
        gameStage.clearBalls();
        // ล้างเลเซอร์
        gameStage.clearLasers();
        // ล้างไอเทม
        gameStage.clearPowerUps();
        // แสดงเมนูพร้อมคะแนนสูงสุด
        gameStage.showMenu(bestScore);
        // บันทึก log กลับเมนู
        logger.info("Back to menu, best score = {}", bestScore);
    }  // จบ backToMenu

    // [เธรด] เรียกจาก JavaFX Thread ตอนคลิกการ์ดอัปเกรด → แค่ฝากเลขไว้ GameLoop จะหยิบไปใช้เฟรมถัดไป
    // requestUpgrade เรียกจาก JavaFX Thread ตอนผู้เล่นคลิกการ์ด (synchronized)
    public synchronized void requestUpgrade(int index) {
        // จดเลขการ์ดที่คลิกไว้ให้ GameLoop อ่าน
        requestedUpgrade = index;
    }  // จบ requestUpgrade

    // อ่านเลขการ์ดที่ถูกคลิก "แล้วล้างค่าทิ้ง" ในเมธอดเดียว (synchronized จึงไม่มีเธรดอื่นแทรกกลาง) -1 = ไม่มีใครคลิก
    // takeRequestedUpgrade อ่านเลขที่คลิกแล้วล้างค่าในเมธอดเดียว (synchronized)
    private synchronized int takeRequestedUpgrade() {
        // เก็บค่าปัจจุบัน
        int value = requestedUpgrade;
        // ล้างค่ากลับเป็น -1
        requestedUpgrade = -1;
        // คืนค่าที่อ่านได้
        return value;
    }  // จบ takeRequestedUpgrade

    // ล้างเลขที่ค้างไว้ (เรียกตอนเริ่มหน้าเลือกอัปเกรด กันคลิกเก่าค้าง)
    // clearRequestedUpgrade ล้างเลขการ์ดที่ค้าง
    private synchronized void clearRequestedUpgrade() {
        // ตั้งเป็น -1
        requestedUpgrade = -1;
    }  // จบ clearRequestedUpgrade

    // ======================================================================
    //  ลูปหลักของเธรด
    // ======================================================================
    // ทำ 1 เฟรม → วัดเวลาที่ใช้ → sleep ส่วนที่เหลือ ให้ทุกเฟรมห่างกัน ≈ 16.7ms เท่ากัน
    // จุดจัดการ exception: GameException ทุกตัวที่เกิดระหว่างเล่น → ExceptionHandler.handle() แล้วกลับเมนู
    // เขียนทับ run() ของ Runnable
    @Override
    // เมธอด run คือลูปหลักของเธรดกฎเกม
    public void run() {
        // วนต่อไปตราบที่ยังไม่ถูกสั่งหยุด
        while (isRunning()) {
            // จดเวลาเริ่มของเฟรม
            long time = System.currentTimeMillis();
            // เริ่ม try เพื่อดัก GameException
            try {
                // ทำกฎเกม 1 เฟรม
                tick();
            // ถ้าเกิด GameException ระหว่างเฟรม
            } catch (GameException e) {
                // ส่งให้ ExceptionHandler (log + Alert)
                ExceptionHandler.handle(e);
                // กลับเมนูเพื่อไม่ให้เกมค้างในสถานะผิดปกติ
                backToMenu();
            }  // จบ catch
            // คำนวณเวลาที่ใช้ในเฟรมนี้
            time = System.currentTimeMillis() - time;
            // เริ่ม try เพราะ sleep อาจเกิด InterruptedException
            try {
                // sleep เวลาที่เหลือของเฟรม (อย่างน้อย 1 ms) ให้ได้ ~60 เฟรม/วินาที
                Thread.sleep((long) Math.max(1, interval - time));
            // ถ้าถูกขัดจังหวะระหว่าง sleep
            } catch (InterruptedException e) {
                // Thread.sleep บังคับให้ catch InterruptedException → ห่อเป็น GameException ทันที
                // แล้วส่งให้ ExceptionHandler จุดเดียวเหมือนทุก exception ของเกม
                // ห่อเป็น GameException ชนิด THREAD_INTERRUPTED แล้วส่งให้ ExceptionHandler
                ExceptionHandler.handle(new GameException(GameException.ErrorType.THREAD_INTERRUPTED,
                        // ข้อความอธิบายและ exception ต้นเหตุ
                        "GameLoop thread was interrupted", e));
                // สั่งหยุดลูปของตัวเอง
                stop();
            }  // จบ catch
        }  // จบ while
    }  // จบ run

    // ---- เมธอด synchronized ที่เธรดอื่นใช้ติดต่อกับ GameLoop ----
    // stop ตั้งธงหยุดลูป (synchronized เรียกจากเธรดอื่นได้)
    public synchronized void stop() { running = false; }
    // isRunning ถามว่าลูปควรวนต่อไหม
    private synchronized boolean isRunning() { return running; }  // ลูปควรวนต่อไหม
    // setStatus เปลี่ยนสถานะเกม (synchronized เพราะ DrawingLoop อ่านด้วย)
    private synchronized void setStatus(GameStatus s) { status = s; }  // เปลี่ยนสถานะเกม (MENU/PLAYING/PAUSED/UPGRADE/GAME_OVER/WIN)
    // getStatus ถามสถานะเกมตอนนี้
    public synchronized GameStatus getStatus() { return status; }  // ถามสถานะเกมตอนนี้ (DrawingLoop ใช้ตัดสินว่าจะเล่นอนิเมชันไหม)
    // setLaserTicks ตั้งเวลาเลเซอร์ที่เหลือเป็นเฟรม
    private synchronized void setLaserTicks(int ticks) { laserTicks = ticks; }  // ตั้งเวลาเลเซอร์ที่เหลือเป็นจำนวนเฟรม (0 = ปิดเลเซอร์)

    // เวลาเลเซอร์ที่เหลือ (วินาที ปัดขึ้น) ไว้โชว์ใน HUD
    // getLaserSecondsLeft คืนเวลาเลเซอร์ที่เหลือเป็นวินาที (ปัดขึ้น) ให้ HUD แสดง
    public synchronized int getLaserSecondsLeft() {
        // แปลงเฟรมเป็นวินาทีแล้วปัดขึ้น
        return (int) Math.ceil(laserTicks / (double) FRAME_RATE);
    }  // จบ getLaserSecondsLeft
}  // จบคลาส GameLoop
