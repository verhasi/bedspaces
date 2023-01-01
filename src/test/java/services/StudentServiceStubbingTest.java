package services;

import data.models.*;
import data.repositories.HostelRepository;
import data.repositories.StudentRepository;
import dto.RegistrationRequest;
import dto.StudentDto;
import exceptions.DuplicateIdException;
import exceptions.HostelManagementException;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceStubbingTest
{
    private StudentService studentService;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private HostelRepository hostelRepository;

    private StudentServiceMocker studentServiceMocker;
    private StudentRepositoryMocker studentRepositoryMocker;

    private LocalDateTime time;


    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();
        studentServiceMocker = new StudentServiceMocker(time);
        studentRepositoryMocker = new StudentRepositoryMocker(time);
        studentService = new StudentServiceImpl(studentRepositoryMocker.getMock(), hostelRepository);
    }


    @Test
    void registerStudent_normalStudent_registers() throws Exception {
//Given
        studentRepositoryMocker.givenUCSaveStudent();
        studentServiceMocker.givenUCStudentRegistration(studentService);
//When+Then
        studentServiceMocker.whenCalled().thenAssert();
    }

    @Test
    void registerStudent_duplicateMatricNo_throwsDuplicateIdException() throws Exception {
//Given
        String matricNo = studentRepositoryMocker.givenUCRegisterStudent_duplicateMatricNo();
        RegistrationRequest registrationRequest = studentServiceMocker.givenUCRegisterStudent_duplicateMatricNo(matricNo);
//When+Then

        assertThatThrownBy(() -> studentService.registerStudent(registrationRequest))
                .isInstanceOf(DuplicateIdException.class)
                .hasMessage("student record with matric number already exists");
    }

    @Test
    void testAssignBedSpaceToStudent() throws Exception {
//Given
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .gender(Gender.MALE).build();

        when(studentRepository.findById(anyString())).thenReturn( Optional.empty(), Optional.of(student));
        when(studentRepository.save(ArgumentMatchers.isA(Student.class))).thenReturn(student);
        when(hostelRepository.returnAvailableMaleSpace()).thenReturn(new BedSpace("HALL3 Room 1 Bedspace 1"));
//When
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "securedPassword",
                "MAT100419",
                Gender.MALE);
        StudentDto studentDto = studentService.registerStudent(registrationRequest);
        studentService.assignBedSpace(studentDto);
//Then
        assertThat(student.getBedSpaceId(), not(equalTo(null)));
        assertThat(student.getBedSpaceId(), equalTo("HALL3 Room 1 Bedspace 1"));
    }

    @Test
    void testThatThrowsHostelManagementExceptionWhenANullOrEmptyStringIsPassedAsArgument() throws Exception {
//Given
        when(studentRepository.findById(null)).thenThrow(new HostelManagementException("User id cannot be null"));
//When+Then
        assertThrows(HostelManagementException.class, () -> studentRepository.findById(null));
    }
}