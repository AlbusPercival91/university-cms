package ua.foxminded.university.dao.service;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.university.dao.interfaces.StudentRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StudentService {
	private final StudentRepository studentRepository;
}
