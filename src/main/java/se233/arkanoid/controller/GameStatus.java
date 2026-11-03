// ประกาศแพ็กเกจ controller
package se233.arkanoid.controller;

/*
 * [เกณฑ์ตาราง] Functionalities (สถานะเกม: เมนู/เล่น/หยุด/เลือกอัปเกรด/แพ้/ชนะ)
 * GameStatus = สถานะของเกมตอนนี้ (state machine)
 * GameLoop ดูสถานะนี้ทุกเฟรมเพื่อเลือกว่าจะทำอะไร
 *
 *   MENU ──ENTER──> PLAYING ──เคลียร์ด่าน──> UPGRADE ──เลือก──> PLAYING (ด่านถัดไป)
 *                    │  ▲
 *                    P  P                    เคลียร์ด่านสุดท้าย ──> WIN ──ENTER──> MENU
 *                    ▼  │                    ชีวิตหมด          ──> GAME_OVER ──ENTER──> MENU
 *                   PAUSED
 */
// enum สถานะของเกม (state machine) GameLoop ดูค่านี้ทุกเฟรม
public enum GameStatus {
    // MENU = หน้าเมนูเริ่มต้น
    MENU,
    // PLAYING = กำลังเล่น
    PLAYING,
    // PAUSED = หยุดชั่วคราว (กด P)
    PAUSED,
    // UPGRADE = หน้าเลือกอัปเกรดหลังเคลียร์ด่าน
    UPGRADE,
    // GAME_OVER = ชีวิตหมด แพ้
    GAME_OVER,
    // WIN = เคลียร์ด่านสุดท้าย ชนะ (ค่าสุดท้ายไม่ต้องมีจุลภาค)
    WIN
}  // จบ enum
