package ua.foxminded.university.dao.service;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.interfaces.TeacherRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TeacherService {
	private final TeacherRepository teacherRepository;
}
