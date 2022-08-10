package data.repositories;

import data.models.Student;
import exceptions.HostelManagementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentRepositoryImplTest {
    private StudentRepository studentStudentRepository = new StudentRepository();
    private StudentRepositoryStub studentRepositoryStub = new StudentRepositoryStub();
    private Student studentOne;
    private Student studentTwo;
    @BeforeEach
    void setUp() throws HostelManagementException {
        studentOne = Student.builder().firstName("Ehis")
                .lastName("Edemakhiota")
                .matricNo("PSC1004396")
                .password("Edemakhiota17.")
                .build();
        studentTwo = Student.builder().firstName("Nosa")
                .lastName("Edemakhiota")
                .matricNo("PSC1004300")
                .password("Edema")
                .build();

        studentStudentRepository.save(studentOne);
        studentStudentRepository.save(studentTwo);
    }



    @Test
    void findByMatricNumber() throws Exception {
        try{
            studentRepositoryStub.useCase_findByMatricNumber(studentOne);
            Student studentStub = studentRepositoryStub.getStub().findById("PSC1004396")
                    .orElseThrow(()->new HostelManagementException("No student found in the stub with specified matric number"));;
            Student student = studentStudentRepository.findById("PSC1004396")
                    .orElseThrow(()->new HostelManagementException("No student found with specified matric number"));
            assertThat(student, equalTo(studentStub));
        }
        catch (HostelManagementException exception){
            Assertions.fail(exception);
        }
    }

    @Test
    void findByName() {
        studentRepositoryStub.useCase_findByName(studentOne);
        List<Student> studentListStub = studentRepositoryStub.getStub().findByName("Ehis Edemakhiota");
        List<Student> studentList = studentStudentRepository
                                    .findByName("Ehis Edemakhiota");
        assertEquals(studentList, studentListStub);
    }

    @Test
    void delete() throws Exception {
        studentStudentRepository.delete(studentTwo);
        assertThat(studentStudentRepository.findById("PSC1004300"), is(Optional.empty()));
    }

    @Test
    void findAll() {
        List<Student> studentList = studentStudentRepository.findAll();
        assertThat(studentList, hasItems(studentOne, studentTwo));
    }

    @Test
    void testCanDeleteById() throws Exception {
        studentStudentRepository.delete("PSC1004300");
        assertThat(studentStudentRepository.findById("PSC1004300"), is(Optional.empty()));
    }
}