// ประกาศแพ็กเกจ model.upgrade
package se233.arkanoid.model.upgrade;

// นำเข้า GameState
import se233.arkanoid.model.GameState;

/*
 * [เกณฑ์ตาราง] Functionalities (roguelike upgrade), OOP (คลาสแม่แบบ polymorphism)
 * Upgrade = คลาสแม่ของอัปเกรด roguelike ทุกแบบ (abstract)
 * OOP: ลูกแต่ละตัว override apply() เป็นของตัวเอง (polymorphism)
 * → GameLoop เรียก upgrade.apply(state) ได้เลย ไม่ต้องรู้ว่าเป็นอัปเกรดแบบไหน
 */
// Upgrade เป็นคลาสแม่แบบ abstract ของอัปเกรดทุกแบบ สร้าง object ตรง ๆ ไม่ได้
public abstract class Upgrade {
    // ชื่อที่โชว์บนการ์ด
    private final String name;        // ชื่อที่โชว์บนการ์ด
    // คำอธิบายสั้น ๆ
    private final String description; // คำอธิบายสั้น ๆ

    // กำหนดชื่อและคำอธิบายที่โชว์บนการ์ดอัปเกรด
    // constructor แบบ protected ให้เฉพาะคลาสลูกเรียกได้
    protected Upgrade(String name, String description) {
        // เก็บชื่อ
        this.name = name;
        // เก็บคำอธิบาย
        this.description = description;
    }  // จบ constructor

    // ใส่ผลของอัปเกรดลงใน GameState (มีผลถึงจบ run)
    // เมธอด abstract: คลาสลูกแต่ละตัวต้องเขียนว่าจะแก้ GameState อย่างไร
    public abstract void apply(GameState state);

    // getter ชื่อ
    public String getName()        { return name; }  // ชื่อบนการ์ด
    // getter คำอธิบาย
    public String getDescription() { return description; }  // คำอธิบายบนการ์ด

    // @Override เขียนทับ toString ของ Object
    @Override
    // คืนชื่อไว้ใช้ตอน print หรือเขียน log
    public String toString() { return name; }  // ใช้ชื่ออัปเกรดตอน print / เขียน log
}  // จบคลาส Upgrade
