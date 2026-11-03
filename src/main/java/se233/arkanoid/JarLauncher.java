// ประกาศว่าไฟล์นี้อยู่ในแพ็กเกจ se233.arkanoid
package se233.arkanoid;

/*
 * [เกณฑ์ตาราง] Build management (ทำให้ jar ที่ shade แล้วดับเบิลคลิกเปิดได้)
 * JarLauncher (Script 6.10)
 * ทำไมต้องมี? ถ้าตั้ง Launcher (ที่ extends Application) เป็น main class ของไฟล์ .jar ตรง ๆ
 * Java จะขึ้น error "JavaFX runtime components are missing"
 * แก้โดยให้คลาสธรรมดาที่ไม่ extends Application เป็นตัวเริ่ม แล้วค่อยเรียก Launcher ต่อ
 */
// คลาสธรรมดา (ไม่ extends Application) ใช้เป็น main class ของไฟล์ .jar
public class JarLauncher {
    // จุดเริ่มของไฟล์ jar: ส่งต่อให้ Launcher.main (แก้ปัญหา "JavaFX runtime components are missing")
    // เมธอด main จุดเริ่มทำงานของ jar
    public static void main(String[] args) {
        // เรียก Launcher.main เพื่อส่งต่อให้ JavaFX เริ่มโปรแกรม
        Launcher.main(args);
    }  // จบเมธอด main
}  // จบคลาส JarLauncher
