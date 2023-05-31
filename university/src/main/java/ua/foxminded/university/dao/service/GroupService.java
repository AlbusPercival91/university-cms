package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.interfaces.GroupRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupService {
	private final GroupRepository groupRepository;

	public int createGroup(Group group) {
		Group newGroup = groupRepository.save(group);
		log.info("Created group with id: {}", newGroup.getId());
		return newGroup.getId();
	}

	public int deleteGroupById(int groupId) {
		Optional<Group> optionalGroup = groupRepository.findById(groupId);

		if (optionalGroup.isPresent()) {
			groupRepository.deleteById(groupId);
			log.info("Deleted group with id: {}", groupId);
			return groupId;
		} else {
			log.warn("Group with id {} not found", groupId);
			throw new NoSuchElementException("Group not found");
		}
	}

	public Group updateGroupById(int groupId, Group group) {
		Group existingGroup = groupRepository.findById(groupId).orElseThrow(() -> {
			log.warn("Group with id {} not found", groupId);
			return new NoSuchElementException("Group not found");
		});
		BeanUtils.copyProperties(group, existingGroup, "id");
		return groupRepository.save(existingGroup);
	}

	public List<Group> getAllGroups() {
		return groupRepository.findAll();
	}
}
