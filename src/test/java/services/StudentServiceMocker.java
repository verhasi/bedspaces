package services;

import data.models.Gender;
import dto.RegistrationRequest;
import dto.StudentDto;
import mocker.AbstractMocker;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

public class StudentServiceMocker extends AbstractMocker<StudentService>
{
    private final LocalDateTime time;
    @Mock
    StudentService mockStudentService;

    public StudentServiceMocker(LocalDateTime time)
    {
        try {
            MockitoAnnotations.openMocks(this).close();
        }
        catch (Exception ignore){
        }
        this.time = time;
    }

    public void givenUCStudentRegistration(StudentService realStudentService)
            throws Exception
    {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "securedPassword",
                "MAT100419",
                Gender.MALE);

        StudentDto studentRegistrationResult = StudentDto.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .registrationTime(time)
                .gender(Gender.MALE).build();

        when(mockStudentService.registerStudent(registrationRequest)).thenReturn(studentRegistrationResult);
        setMethods(() -> mockStudentService.registerStudent(registrationRequest),
                () -> realStudentService.registerStudent(registrationRequest));
    }

    public RegistrationRequest givenUCRegisterStudent_duplicateMatricNo(String matricNo)
        throws Exception
    {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "securedPassword",
                matricNo,
                Gender.MALE);
        return registrationRequest;
    }

    //delegate
    StudentDto registerStudent(RegistrationRequest studentDto)
            throws Exception
    {
        return mockStudentService.registerStudent(studentDto);
    }

    @Override
    public StudentService getMock()
    {
        return mockStudentService;
    }
}
