package ua.foxminded.university.dao.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "alert", schema = "university")
public class Alert {

    @ToString.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private int id;

    @Column(name = "alert_timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "message")
    private String message;

    public Alert(LocalDateTime timestamp, Teacher teacher, String message) {
        this.timestamp = timestamp;
        this.teacher = teacher;
        this.message = message;
    }

    public Alert(LocalDateTime timestamp, Student student, String message) {
        this.timestamp = timestamp;
        this.student = student;
        this.message = message;
    }

    public Alert(LocalDateTime timestamp, Staff staff, String message) {
        this.timestamp = timestamp;
        this.staff = staff;
        this.message = message;
    }

    public Alert(LocalDateTime timestamp, Admin admin, String message) {
        this.timestamp = timestamp;
        this.admin = admin;
        this.message = message;
    }
}
