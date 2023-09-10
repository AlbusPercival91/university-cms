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

    @Column(name = "sender")
    private String sender;

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

    @Column(name = "is_read")
    private boolean isRead;

    public Alert(LocalDateTime timestamp, String sender, Teacher teacher, String message) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.teacher = teacher;
        this.message = message;
    }

    public Alert(LocalDateTime timestamp, String sender, Student student, String message) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.student = student;
        this.message = message;
    }

    public Alert(LocalDateTime timestamp, String sender, Staff staff, String message) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.staff = staff;
        this.message = message;
    }

    public Alert(LocalDateTime timestamp, String sender, Admin admin, String message) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.admin = admin;
        this.message = message;
    }
}
