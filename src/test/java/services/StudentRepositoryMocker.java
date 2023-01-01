package services;

import data.models.Gender;
import data.models.Student;
import data.repositories.StudentRepository;
import mocker.AbstractMocker;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class StudentRepositoryMocker extends AbstractMocker<StudentRepository>
{
    private final LocalDateTime time;

    @Mock
    StudentRepository studentRepository;

    public StudentRepositoryMocker(LocalDateTime time)
    {
        try {
            MockitoAnnotations.openMocks(this).close();
        }
        catch (Exception ignore){
        }
        this.time = time;
    }

    public Student givenUCSaveStudent()
    throws Exception
    {
        Student studentToSave = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .registrationTime(time)
                .gender(Gender.MALE).build();
        when(studentRepository.save(any(Student.class))).thenReturn(studentToSave);
        return studentToSave;
    }

    public String givenUCRegisterStudent_duplicateMatricNo() throws Exception
    {
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .matricNo("MAT100419")
                .password("securedPassword")
                .gender(Gender.MALE).build();
        when(studentRepository.findById(anyString())).thenReturn(Optional.of(student));
        return "MAT100419";
    }
    @Override
    public StudentRepository getMock()
    {
        return studentRepository;
    }
}
