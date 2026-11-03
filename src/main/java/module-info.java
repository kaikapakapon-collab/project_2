// ประกาศโมดูล Java (JPMS) ชื่อ se233.arkanoid เป็นตัวบอกว่าโปรเจกต์นี้ต้องการอะไรและเปิดอะไรให้ภายนอก
module se233.arkanoid {
    // ต้องใช้โมดูล javafx.controls (ปุ่ม, Alert, Label ฯลฯ)
    requires javafx.controls;
    // ต้องใช้โมดูล javafx.fxml (เปิดไว้เผื่อใช้ FXML)
    requires javafx.fxml;
    // ต้องใช้ Log4j2 สำหรับเขียน log
    requires org.apache.logging.log4j;

    // เปิดแพ็กเกจหลักให้ javafx.fxml ใช้ reflection เข้าถึงได้
    opens se233.arkanoid to javafx.fxml;
    // เปิดแพ็กเกจ se233.arkanoid (Launcher) ให้ภายนอกเรียกใช้
    exports se233.arkanoid;
    // เปิดแพ็กเกจ model (ตัวละคร/ข้อมูลเกม)
    exports se233.arkanoid.model;
    // เปิดแพ็กเกจ model.upgrade (ระบบอัปเกรด)
    exports se233.arkanoid.model.upgrade;
    // เปิดแพ็กเกจ view (ส่วนแสดงผล)
    exports se233.arkanoid.view;
    // เปิดแพ็กเกจ controller (ลูปเกม)
    exports se233.arkanoid.controller;
    // เปิดแพ็กเกจ exception (GameException)
    exports se233.arkanoid.exception;
}  // ปิดการประกาศโมดูล
