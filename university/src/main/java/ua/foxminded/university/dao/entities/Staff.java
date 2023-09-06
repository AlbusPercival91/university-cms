package ua.foxminded.university.dao.entities;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "staff", schema = "university")
public class Staff extends User {

    @Column(name = "position")
    private String position;

    @Column(name = "function")
    private String function;

    @ToString.Exclude
    @OrderBy("timestamp DESC")
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alert> alerts;

    public Staff(String firstName, String lastName, boolean isActive, String email, String password, String position,
            String function) {
        super(firstName, lastName, isActive, email, password);
        this.position = position;
        this.function = function;
    }
}
