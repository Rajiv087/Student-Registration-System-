import java.util.*;

class RegistrationException extends Exception{
    RegistrationException(String message) {
        super(message);
    }
}
abstract class Person{
    private String name;
    private int id;

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
    Person(String name, int id){
        this.name = name;
        this.id = id;
    }
    public abstract void displayInfo();
}

class Student extends Person{
    List<String>completedCourses = new ArrayList<>();
    List<String>registeredCourses = new ArrayList<>();

    private String department;
    private int semester;

    Student(String name, int id, String department, int semester){
        super(name, id);
        this.department = department;
        this.semester = semester;
    }

    public void setSemester(int semester) throws RegistrationException{
        if (semester < 1 || semester > 8){
            throw new RegistrationException("Invalid Semester.");
        }
        this.semester = semester;
    }

    @Override 
    public void displayInfo() {
        System.out.printf("Student Details:\nID: %s\nName: %s\n Department: %s\n Semester: %s\n", getId(), getName(), department, semester);
    }

    void registerCourse(String courseCode) {
        registeredCourses.add(courseCode);
    }

    boolean isRegisteredFor(String courseCode) {
        return registeredCourses.contains(courseCode);
    }

}

class Course{
    String courseCode;
    String courseName;
    private int credits;
    String prereq;
    private int maxCapacity;
    private int enrolledCount = 0;

    Course(String courseCode, String courseName, int credits, String prereq, int maxCapacity){
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.prereq = prereq;
        this.maxCapacity = maxCapacity;
    }

    int getCredits() {
        return this.credits;
    }

    int getMaxCapacity() {
        return this.maxCapacity;
    }



    boolean hasSpace() {
        return enrolledCount < maxCapacity;

    }
    void enroll() {
        enrolledCount++;
    }
}


class RegistrationSystem{
    HashMap<Integer, Student> studentsList = new HashMap<>();
    HashMap<String, Course> courseList= new HashMap<>();
        
    void addStudent(Student s) throws RegistrationException{
        if (studentsList.containsKey(s.getId())){
            throw new RegistrationException("Student already exists.");
        }

        else {
            studentsList.put(s.getId(), s);
        }
    }

    void addCourse(Course c) throws RegistrationException{
        if (courseList.containsKey(c.courseCode)) {
            throw new RegistrationException("Course already exists");
        }
        else {
            courseList.put(c.courseCode, c);
        }
    }

    void registerStudent(int id, String courseCode) throws RegistrationException {
        if (!studentsList.containsKey(id)){
            throw new RegistrationException("Student not found");
        }

        if (!courseList.containsKey(courseCode)) {
            throw new RegistrationException("Course not found");
        }

        Student s = studentsList.get(id);
        Course c = courseList.get(courseCode);

        if (s.isRegisteredFor(courseCode)) {
            throw new RegistrationException("Student already registered for the course");
        }

        s.registerCourse(courseCode);
        c.enroll();

    }
    

}

public class OOPproject{
    public static void main(string [] args)
}