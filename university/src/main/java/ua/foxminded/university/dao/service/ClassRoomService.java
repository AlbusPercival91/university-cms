package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.interfaces.ClassRoomRepository;
import ua.foxminded.university.validation.Message;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    public int createClassRoom(ClassRoom classRoom) {
        if (classRoomRepository.existsByStreetAndBuildingNumberAndRoomNumber(classRoom.getStreet(),
                classRoom.getBuildingNumber(), classRoom.getRoomNumber())) {
            throw new IllegalStateException(Message.RECORD_EXISTS);
        }
        ClassRoom newClassRoom = classRoomRepository.save(classRoom);
        log.info(Message.CREATE_SUCCESS);
        return newClassRoom.getId();
    }

    public int deleteClassRoomById(int classRoomId) {
        Optional<ClassRoom> optionalClassRoom = classRoomRepository.findById(classRoomId);

        if (optionalClassRoom.isPresent()) {
            classRoomRepository.deleteById(classRoomId);
            log.info(Message.DELETE_SUCCESS);
            return classRoomId;
        } else {
            log.warn(Message.ROOM_NOT_FOUND);
            throw new NoSuchElementException(Message.ROOM_NOT_FOUND);
        }
    }

    public ClassRoom updateClassRoomById(int classRoomId, ClassRoom targetClassRoom) {
        ClassRoom existingClassRoom = classRoomRepository.findById(classRoomId).orElseThrow(() -> {
            log.warn(Message.ROOM_NOT_FOUND, classRoomId);
            return new NoSuchElementException(Message.ROOM_NOT_FOUND);
        });

        if (classRoomRepository.existsByStreetAndBuildingNumberAndRoomNumber(targetClassRoom.getStreet(),
                targetClassRoom.getBuildingNumber(), targetClassRoom.getRoomNumber())) {
            throw new IllegalStateException(Message.RECORD_EXISTS);
        }
        BeanUtils.copyProperties(targetClassRoom, existingClassRoom, "id");
        return classRoomRepository.save(existingClassRoom);
    }

    public List<ClassRoom> getAllClassRooms() {
        return classRoomRepository.findAll();
    }

    public Optional<ClassRoom> findClassRoomById(int classRoomId) {
        return classRoomRepository.findById(classRoomId);
    }

    public List<ClassRoom> findClassRoomsByStreet(String street) {
        return classRoomRepository.findAllByStreet(street);
    }

    public List<ClassRoom> findClassRoomsByStreetAndBuildingNumber(String street, int buildingNumber) {
        return classRoomRepository.findAllByStreetAndBuildingNumber(street, buildingNumber);
    }
}
