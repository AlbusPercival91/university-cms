package ua.foxminded.university.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.university.dao.entities.ClassRoom;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Integer> {

    boolean existsByStreetAndBuildingNumberAndRoomNumber(String street, int buildingNumber, int roomNumber);

    List<ClassRoom> findAllByStreetOrderByIdAsc(String street);

    List<ClassRoom> findAllByStreetAndBuildingNumberOrderByIdAsc(String street, int buildingNumber);
}
