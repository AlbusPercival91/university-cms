package ua.foxminded.university.dao.fakedata;

import org.springframework.beans.factory.annotation.Autowired;

public class TimeTableMaker {

	@Autowired
	private ClassRoomMaker classRoomMaker;

	@Autowired
	private CourseMaker courseMaker;

	@Autowired
	private DepartmentMaker departmentMaker;

	@Autowired
	private FacultyMaker facultyMaker;

	@Autowired
	private GroupMaker groupMaker;

	@Autowired
	private PersonMaker personMaker;

	@Autowired
	private StaffMaker staffMaker;

}
