package ua.foxminded.university.dao.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "classroom", schema = "university")
public class ClassRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "classroom_id")
	private int id;

	@Column(name = "street")
	private String street;

	@Column(name = "build_no")
	private int buildingNumber;

	@Column(name = "room_no")
	private int roomNumber;

	@ToString.Exclude
	@OneToMany(mappedBy = "classRoom")
	private Set<TimeTable> timeTables;

	public ClassRoom(String street, int buildingNumber, int roomNumber) {
		this.street = street;
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
	}
}
