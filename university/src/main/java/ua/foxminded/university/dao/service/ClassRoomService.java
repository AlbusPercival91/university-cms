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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ClassRoomService {
	private final ClassRoomRepository classRoomRepository;

	public int createClassRoom(ClassRoom classRoom) {
		ClassRoom newClassRoom = classRoomRepository.save(classRoom);
		log.info("Created room with id: {}", newClassRoom.getId());
		return newClassRoom.getId();
	}

	public int deleteClassRoomById(int classRoomId) {
		Optional<ClassRoom> optionalClassRoom = classRoomRepository.findById(classRoomId);

		if (optionalClassRoom.isPresent()) {
			classRoomRepository.deleteById(classRoomId);
			log.info("Deleted room with id: {}", classRoomId);
			return classRoomId;
		} else {
			log.warn("Room with id {} not found", classRoomId);
			throw new NoSuchElementException("Class room not found");
		}
	}

	public ClassRoom updateClassRoomById(int classRoomId, ClassRoom classRoom) {
		ClassRoom existingClassRoom = classRoomRepository.findById(classRoomId).orElseThrow(() -> {
			log.warn("Room with id {} not found", classRoomId);
			return new NoSuchElementException("Class room not found");
		});
		BeanUtils.copyProperties(classRoom, existingClassRoom, "id");
		return classRoomRepository.save(existingClassRoom);
	}

	public List<ClassRoom> getAllClassRooms() {
		return classRoomRepository.findAll();
	}
}
