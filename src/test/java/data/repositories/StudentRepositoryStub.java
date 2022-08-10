package data.repositories;

import data.models.Student;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class StudentRepositoryStub implements Stub<StudentRepository>
{
    @Mock
    StudentRepository studentRepositoryStub;

    public StudentRepositoryStub()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Override
    public StudentRepository getStub()
    {
        return studentRepositoryStub;
    }

    public void useCase_findByMatricNumber(Student student)
            throws Exception
    {
        when(studentRepositoryStub.findById(student.getId())).thenReturn(Optional.of(student));
    }

    public void useCase_findByName(Student student)
    {
        when(studentRepositoryStub.findByName(student.getFirstName()+" "+student.getLastName()))
                .thenReturn(Collections.singletonList(student));
    }
}
