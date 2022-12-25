package services;

import data.models.*;
import data.repositories.HostelRepository;
import data.repositories.StudentRepository;
import dto.RegistrationRequest;
import dto.StudentDto;
import exceptions.DuplicateIdException;
import exceptions.HostelManagementException;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
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
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time =  LocalDateTime.now();
        studentService = new StudentServiceImpl(studentRepository, hostelRepository);
    }


    @Test
    void registerStudentTest() throws Exception {
//Given
        Student studentToSave = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .registrationTime(LocalDateTime.now())
                .gender(Gender.MALE).build();
        when(studentRepository.save(any(Student.class))).thenReturn(studentToSave);
//When
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "securedPassword",
                "MAT100419",
                Gender.MALE);
        StudentDto studentDto = studentService.registerStudent(registrationRequest);
//Then
        assertThat(studentDto, hasProperty("firstName", equalTo("John")));
        assertThat(studentDto, hasProperty("lastName", equalTo("Doe")));
        assertThat(studentDto, hasProperty("matricNo", equalTo("MAT100419")));
        assertThat(studentDto, hasProperty("gender", equalTo(Gender.MALE)));
    }

    @Test
    void registerStudentTest_WithRegistrationTime() throws Exception {
//Given
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .registrationTime(time)
                .gender(Gender.MALE).build();

        when(studentRepository.save(any(Student.class))).thenAnswer(answer -> student);
//When
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "securedPassword",
                "MAT100419",
                Gender.MALE);
        StudentDto studentDto = studentService.registerStudent(registrationRequest);
//Then
        assertThat(studentDto, hasProperty("firstName", equalTo("John")));
        assertThat(studentDto, hasProperty("lastName", equalTo("Doe")));
        assertThat(studentDto, hasProperty("matricNo", equalTo("MAT100419")));
        assertThat(studentDto, hasProperty("registrationTime", equalTo(time)));
        assertThat(studentDto, hasProperty("gender", equalTo(Gender.MALE)));
    }

    @Test
    void findStudentByIdTest() throws Exception {
//Given
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .gender(Gender.MALE).build();
        when(studentRepository.findById(anyString())).thenReturn(Optional.of(student));
//When
        Student returnValue = studentRepository.findById("string").orElse(null);
//Then
        assertNotNull(returnValue);
        assertThat(returnValue, hasProperty("firstName", equalTo("John")));
        assertThat(returnValue, hasProperty("lastName", equalTo("Doe")));
        assertThat(returnValue, hasProperty("matricNo", equalTo("MAT100419")));
        assertThat(returnValue, hasProperty("password", equalTo("securedPassword")));
        assertThat(returnValue, hasProperty("gender", equalTo(Gender.MALE)));
    }

    @Test
    void testThrowDuplicateIdException() throws Exception {
//Given
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .gender(Gender.MALE).build();
        when(studentRepository.findById(anyString())).thenReturn(Optional.of(student));
//When+Then
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "securedPassword",
                "MAT100419",
                Gender.MALE);
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