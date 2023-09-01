package ua.foxminded.university.dao.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.entities.Faculty;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.interfaces.FacultyRepository;
import ua.foxminded.university.dao.interfaces.GroupRepository;
import ua.foxminded.university.validation.Message;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final FacultyRepository facultyRepository;

    public int createGroup(Group group) {
        String facultyName = group.getFaculty().getFacultyName();
        Optional<Faculty> faculty = facultyRepository.findFacultyByFacultyName(facultyName);

        if (faculty.get().getGroups().stream().anyMatch(d -> d.getGroupName().equals(group.getGroupName()))) {
            log.warn(Message.GROUP_EXISTS);
            throw new IllegalStateException(Message.GROUP_EXISTS);
        }
        Group newGroup = groupRepository.save(group);
        log.info(Message.CREATE_SUCCESS);
        return newGroup.getId();
    }

    public int deleteGroupById(int groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);

        if (optionalGroup.isPresent()) {
            groupRepository.deleteById(groupId);
            log.info(Message.DELETE_SUCCESS);
            return groupId;
        } else {
            log.warn(Message.GROUP_NOT_FOUND);
            throw new NoSuchElementException(Message.GROUP_NOT_FOUND);
        }
    }

    public Group updateGroupById(int groupId, Group targetGroup) {
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(() -> {
            log.warn(Message.GROUP_NOT_FOUND);
            return new NoSuchElementException(Message.GROUP_NOT_FOUND);
        });

        if (targetGroup.getFaculty().getGroups().stream()
                .anyMatch(d -> d.getGroupName().equals(targetGroup.getGroupName()))) {
            log.warn(Message.GROUP_EXISTS);
            throw new IllegalStateException(Message.GROUP_EXISTS);
        }
        BeanUtils.copyProperties(targetGroup, existingGroup, "id");
        return groupRepository.save(existingGroup);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<Group> findGroupById(int groupId) {
        return groupRepository.findById(groupId);
    }

    public List<Group> findGroupByGroupName(String groupName) {
        return groupRepository.findGroupByGroupName(groupName);
    }

    public List<Group> findAllByFacultyName(String facultyName) {
        return groupRepository.findAllByFacultyFacultyName(facultyName);
    }
}
