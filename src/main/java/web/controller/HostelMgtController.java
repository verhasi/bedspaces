package web.controller;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.ModelMapperConfig;
import data.models.Student;
import data.repositories.HostelRepository;
import data.repositories.StudentRepository;
import dto.RegistrationRequest;
import dto.StudentDto;
import exceptions.HostelManagementException;
import lombok.extern.slf4j.Slf4j;
import services.StudentService;
import services.StudentServiceImpl;
import java.util.List;


import static spark.Spark.*;

@Slf4j
public class HostelMgtController {
    private final StudentService studentService;
    private final ObjectMapper objectMapper;

    public HostelMgtController()
    {
        this(new HostelRepository(),new StudentRepository());
    }

    public HostelMgtController(HostelRepository hostelRepository, StudentRepository studentRepository)
    {
        this(new StudentServiceImpl(studentRepository, hostelRepository));
    }

    public HostelMgtController(StudentServiceImpl studentService)
    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.studentService = studentService;
    }

    public static void main(String[] args)
    {
        HostelMgtController hostelMgtController = new HostelMgtController();
        hostelMgtController.start();
    }

    private void start(){
        addPathRegisterRedirect();
        addPathStudents();
        addPathBedSpaces();
    }

    private void addPathBedSpaces()
    {
        path("api/v1/bed-spaces", this::addAssignBedSpaces);
    }

    private void addAssignBedSpaces()
    {
        post("/assign-bed-space", ((request, response) -> {
            response.type("application/json");
            StudentDto studentDto = objectMapper.readValue(request.body(), StudentDto.class);

            try{
                StudentDto studentDto1 = studentService.assignBedSpace(studentDto);

                return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(studentDto1);
            }
            catch(HostelManagementException exception){
                log.info("Exception --> {}", exception.getMessage());
                return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(exception.getMessage());
            }
        }));
    }

    private void addPathStudents()
    {
        path("/api/v1/students", ()->{
            addPathRegister();
            addPathStudentInfo();
            addPathNamesOfStudentsInHostel();
        });
    }

    private void addPathNamesOfStudentsInHostel()
    {
        get("/get-names-of-students-in-hostel/:hostelName", (request, response) -> {
            String hostelName = request.params(":hostelName");
            try{
                List<String> names = studentService.returnNamesOfAllStudentsInAHostel(hostelName);
                return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(names);
            }catch (HostelManagementException exception){
                log.info("Exception occurred --> {}", exception.getMessage());
                return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(exception.getMessage());
            }
        });
    }

    private void addPathStudentInfo()
    {
        get("/get-student-info/:studentId", (request, response)->{
            String studentId = request.params(":studentId");
            try{
                Student student = studentService.findStudentById(studentId);
                StudentDto studentDto = ModelMapperConfig.getMapper().map(student, StudentDto.class);
                return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(studentDto);
            }catch (HostelManagementException exception){
                log.info("Exception occurred --> {}", exception.getMessage());
                return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(exception.getMessage());
            }
        });
    }

    private void addPathRegister()
    {
        post("/register", ((request, response) -> {
            RegistrationRequest registrationRequest = objectMapper.readValue(request.body(), RegistrationRequest.class);
            StudentDto studentDto = studentService.registerStudent(registrationRequest);
            return objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(studentDto);
        }));
    }

    private static void addPathRegisterRedirect()
    {
        redirect.post("/register", "/assign-bed-space");
    }
}
