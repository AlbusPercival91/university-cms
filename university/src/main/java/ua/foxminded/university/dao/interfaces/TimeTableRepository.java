package ua.foxminded.university.dao.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.dao.entities.ClassRoom;
import ua.foxminded.university.dao.entities.Group;
import ua.foxminded.university.dao.entities.Teacher;
import ua.foxminded.university.dao.entities.TimeTable;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

    List<TimeTable> findByTeacherOrderByDateAscTimeFromAsc(Teacher teacher);

    List<TimeTable> findByGroupOrderByDateAscTimeFromAsc(Group group);

    List<TimeTable> findByGroupGroupNameOrderByDateAscTimeFromAsc(String groupName);

    List<TimeTable> findByCourseCourseNameOrderByDateAscTimeFromAsc(String courseName);

    List<TimeTable> findByDateOrderByDateAscTimeFromAsc(LocalDate date);

    @Query("""
                SELECT t FROM TimeTable t
                JOIN t.course c
                JOIN c.students s
                WHERE s.id = :studentId
                ORDER BY t.date, t.timeFrom
            """)
    List<TimeTable> findByStudentOrderByDateAscTimeFromAsc(@Param("studentId") int studentId);

    @Query("""
            SELECT t FROM TimeTable t
            JOIN FETCH t.group
            JOIN FETCH t.classRoom
            JOIN FETCH t.teacher
            JOIN FETCH t.course
            WHERE t.date BETWEEN :dateFrom AND :dateTo
            ORDER BY t.date, t.timeFrom
            """)
    List<TimeTable> findByDateOrderByDateAscTimeFromAsc(LocalDate dateFrom, LocalDate dateTo);

    @Query("""
            SELECT t FROM TimeTable t
            WHERE t.date BETWEEN :dateFrom AND :dateTo AND t.teacher = :teacher
            ORDER BY t.date, t.timeFrom
            """)
    List<TimeTable> findByDateAndTeacherOrderByDateAscTimeFromAsc(LocalDate dateFrom, LocalDate dateTo,
            Teacher teacher);

    @Query("""
            SELECT t FROM TimeTable t
            WHERE t.date BETWEEN :dateFrom AND :dateTo AND t.group = :group
            ORDER BY t.date, t.timeFrom
            """)
    List<TimeTable> findByDateAndGroupOrderByDateAscTimeFromAsc(LocalDate dateFrom, LocalDate dateTo, Group group);

    @Query("""
                SELECT t FROM TimeTable t
                JOIN t.course c
                JOIN c.students s
                WHERE s.id = :studentId
                AND t.date BETWEEN :dateFrom AND :dateTo
                ORDER BY t.date, t.timeFrom
            """)
    List<TimeTable> findByDateAndStudentOrderByDateAscTimeFromAsc(@Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo, @Param("studentId") int studentId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " + "FROM TimeTable t "
            + "WHERE t.classRoom = :classRoom " + "AND t.date = :date "
            + "AND ((t.timeFrom <= :timeFrom AND t.timeTo > :timeFrom) "
            + "OR (t.timeFrom < :timeTo AND t.timeTo >= :timeTo) "
            + "OR (t.timeFrom >= :timeFrom AND t.timeTo <= :timeTo))")
    boolean timeTableValidationFailed(LocalDate date, LocalTime timeFrom, LocalTime timeTo, ClassRoom classRoom);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " + "FROM TimeTable t "
            + "WHERE t.teacher = :teacher " + "AND t.date = :date "
            + "AND ((t.timeFrom <= :timeFrom AND t.timeTo > :timeFrom) "
            + "OR (t.timeFrom < :timeTo AND t.timeTo >= :timeTo) "
            + "OR (t.timeFrom >= :timeFrom AND t.timeTo <= :timeTo))")
    boolean teacherIsBusy(LocalDate date, LocalTime timeFrom, LocalTime timeTo, Teacher teacher);

}
