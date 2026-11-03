import java.util.*;
import java.io.*;

interface Storable {
    String toFileString();
}

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

class Student extends Person implements Storable{
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
        System.out.printf("Student Details:\nID: %s\nName: %s\nDepartment: %s\nSemester: %s\n", getId(), getName(), department, semester);
    }

    void registerCourse(String courseCode) {
        registeredCourses.add(courseCode);
    }

    boolean isRegisteredFor(String courseCode) {
        return registeredCourses.contains(courseCode);
    }

    boolean hasCompleted(String prereq){ 
        return completedCourses.contains(prereq);
    }

    void addCompletedCourse(String courseCode) {
        completedCourses.add(courseCode);

    }

    @Override
    public String toFileString() {
        return getId() + "," + getName() + "," + department + "," + semester;
    }
}

class Course implements Storable{
    private String courseCode;
    private String courseName;
    private int credits;
    private String prereq;
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

    String getCourse() {
        return this.courseName;
    }
    
    String getCourseCode() {
        return this.courseCode;
    }

    String getPrereq() {
        return this.prereq;
    }

    boolean hasSpace() {
        return enrolledCount < maxCapacity;
        
    }
    void enroll() {
        enrolledCount++;
    }

    int getEnrolledCount() {
        return this.enrolledCount;
    }

    void displayInfo() {
        System.out.printf("Course Details: \nCourse Code: %s\nCourse Name: %s\nCredits: %d\nPrereq: %s\nMax Capacity: %d\n", courseCode, courseName, getCredits(), prereq, getMaxCapacity());
    }

    @Override
    public String toFileString() {
        String p = (prereq == null) ? "NONE" : prereq;
        return courseCode + "," + courseName + "," + credits + "," + p + "," + maxCapacity;
    }
}

class Faculty extends Person {
    private String department;

    Faculty(String name, int id, String department) {
        super(name, id);
        this.department = department;
    }

    @Override
    public void displayInfo() {
        System.out.printf("Faculty: %s (ID %d), Department: %s%n", getName(), getId(), department);
    }
}

class RegistrationSystem{
    static final int MAXCREDITS = 20;

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
        if (courseList.containsKey(c.getCourseCode())) {
            throw new RegistrationException("Course already exists");
        }
        else {
            courseList.put(c.getCourseCode(), c);
        }
    }

    int getTotalCredits(Student s) {
        int total = 0;
        for (String code : s.registeredCourses) {
            Course c = courseList.get(code);
            total += c.getCredits();
        }
        return total;
    }

    void displayAllStudents() {
        for (Student  s : studentsList.values()) {
            s.displayInfo();
            System.out.println("_______________");
        }
    }
    
    Student searchStudent(int id) throws RegistrationException{
        Student s = studentsList.get(id);
        if (s == null) throw new RegistrationException("Student not found");
        else return s;
    }
    
    void displayAllCourses() {
        for (Course c : courseList.values()) {
            c.displayInfo();
            System.out.println("_______________");
        }
    }

    Course searchCourse(String courseCode)  throws RegistrationException{
        Course c = courseList.get(courseCode);
        if (c == null) throw new RegistrationException("Course not found");
        else return c;
    }

    void displayStudentsInCourse(String courseCode) throws RegistrationException {
    searchCourse(courseCode);   
    int count = 0;
    for (Student s : studentsList.values()) {
        if (s.isRegisteredFor(courseCode)) {
            System.out.println(s.getId() + " - " + s.getName());
            count++;
        }
    }
    if (count == 0) {
        System.out.println("No students registered in this course.");
    }
}

    void printSummary(int id) throws RegistrationException{
        Student s = searchStudent(id);
        System.out.println("Academic Summary: ");
        System.out.println("ID: " +s.getId());
        System.out.println("Name: "+ s.getName());
        if (s.registeredCourses.isEmpty()) System.out.println("None");
        System.out.println("Registered Courses: ");
        for (String courseCode : s.registeredCourses) {
            Course c = courseList.get(courseCode);
            System.out.println(c.getCourseCode() + " - " + c.getCourse() + " (" + c.getCredits() + " credits)");
        }

        System.out.println("Total credits: " + getTotalCredits(s));
        if (s.completedCourses.isEmpty()) System.out.println("None");
        for (String code : s.completedCourses) {
            System.out.println("Completed courses: " +code);
        }

        System.out.println("Remaining credits: "+ (MAXCREDITS-getTotalCredits(s)));

    }

    void markCompleted(int id, String courseCode) throws RegistrationException {
    Student s = searchStudent(id);
    if (s.hasCompleted(courseCode))
        throw new RegistrationException("Already marked as completed.");
    s.addCompletedCourse(courseCode);
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

        if (c.getPrereq() != null && !s.hasCompleted(c.getPrereq())) {
            throw new RegistrationException("Prerequisite not completed");
        }

        if (!c.hasSpace()) {
            throw new RegistrationException("Course capacity full");
        } 

        if (getTotalCredits(s) + c.getCredits() > MAXCREDITS) {
            int creds = getTotalCredits(s);
            throw new RegistrationException("Credits limit exceeded\nAvailable credits: " + (MAXCREDITS - creds));
        }

        s.registerCourse(courseCode);
        c.enroll();
    }
    
}

class FileManager {
    private static final String STUDENTS = "students.txt";
    private static final String COURSES = "courses.txt";
    private static final String REGISTRATIONS = "registrations.txt";
    private static final String COMPLETED = "completed.txt";

    static void save(RegistrationSystem r) throws IOException {
        try (PrintWriter w = new PrintWriter(new FileWriter(STUDENTS))) {
            for (Student s : r.studentsList.values()) w.println(s.toFileString());
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(COURSES))) {
            for (Course c : r.courseList.values()) w.println(c.toFileString());
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(REGISTRATIONS))) {
            for (Student s : r.studentsList.values())
                for (String code : s.registeredCourses)
                    w.println(s.getId() + "," + code);
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(COMPLETED))) {
            for (Student s : r.studentsList.values())
                for (String code : s.completedCourses)
                    w.println(s.getId() + "," + code);
        }
    }

    private static List<String> readLines(String file) throws IOException {
        List<String> lines = new ArrayList<>();
        File f = new File(file);
        if (!f.exists()) return lines;          
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.isBlank()) lines.add(line);
        }
        return lines;
    }

    static void load(RegistrationSystem r) throws IOException {
        for (String line : readLines(STUDENTS)) {
            String[] p = line.split(",");
            Student s = new Student(p[1], Integer.parseInt(p[0]), p[2], Integer.parseInt(p[3]));
            r.studentsList.put(s.getId(), s);
        }
        for (String line : readLines(COURSES)) {
            String[] p = line.split(",");
            String prereq = p[3].equals("NONE") ? null : p[3];
            Course c = new Course(p[0], p[1], Integer.parseInt(p[2]), prereq, Integer.parseInt(p[4]));
            r.courseList.put(c.getCourseCode(), c);
        }
        for (String line : readLines(COMPLETED)) {
            String[] p = line.split(",");
            Student s = r.studentsList.get(Integer.parseInt(p[0]));
            if (s != null) s.addCompletedCourse(p[1]);
        }
        for (String line : readLines(REGISTRATIONS)) {
            String[] p = line.split(",");
            Student s = r.studentsList.get(Integer.parseInt(p[0]));
            Course c = r.courseList.get(p[1]);
            if (s != null && c != null) {
                s.registerCourse(p[1]);
                c.enroll();                     
            }
        }
    }
}


public class OOPproject {
    static Scanner sc = new Scanner(System.in);
    static RegistrationSystem r = new RegistrationSystem();

    static String readText(String prompt, boolean allowEmpty) throws RegistrationException {
        System.out.print(prompt);
        String t = sc.nextLine().trim();
        if (t.isEmpty() && !allowEmpty) throw new RegistrationException("This field cannot be empty.");
        if (t.contains(",")) throw new RegistrationException("Commas are not allowed.");
        return t;
    }

    static int readInt(String prompt) throws RegistrationException {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new RegistrationException("Please enter a valid number.");
        }
    }

    static void save() {
        try {
            FileManager.save(r);
            System.out.println("Data saved.");
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    static void addStudent() throws RegistrationException {
        int id = readInt("Student ID: ");
        String name = readText("Name: ", false);
        String dept = readText("Department: ", false);
        int sem = readInt("Semester (1-8): ");
        if (sem < 1 || sem > 8) throw new RegistrationException("Invalid semester.");
        r.addStudent(new Student(name, id, dept, sem));
        System.out.println("Student added successfully.");
        save();
    }

    static void viewSearchStudents() throws RegistrationException {
        int c = readInt("1. View all   2. Search by ID: ");
        if (c == 1) {
            if (r.studentsList.isEmpty()) System.out.println("No students yet.");
            else r.displayAllStudents();
        } else if (c == 2) {
            r.searchStudent(readInt("Student ID: ")).displayInfo();
        } else System.out.println("Invalid option.");
    }

    static void addCourse() throws RegistrationException {
        String code = readText("Course code: ", false).toUpperCase();
        String name = readText("Course name: ", false);
        int credits = readInt("Credits: ");
        String prereq = readText("Prerequisite code (leave blank if none): ", true).toUpperCase();
        int cap = readInt("Max capacity: ");
        if (credits < 1) throw new RegistrationException("Credits must be positive.");
        if (cap < 1) throw new RegistrationException("Capacity must be positive.");
        r.addCourse(new Course(code, name, credits, prereq.isEmpty() ? null : prereq, cap));
        System.out.println("Course added successfully.");
        save();
    }

    static void viewSearchCourses() throws RegistrationException {
        int c = readInt("1. View all   2. Search by code: ");
        if (c == 1) {
            if (r.courseList.isEmpty()) System.out.println("No courses yet.");
            else r.displayAllCourses();
        } else if (c == 2) {
            r.searchCourse(readText("Course code: ", false).toUpperCase()).displayInfo();
        } else System.out.println("Invalid option.");
    }

    static void registerStudent() throws RegistrationException {
        int id = readInt("Student ID: ");
        String code = readText("Course code: ", false).toUpperCase();
        r.registerStudent(id, code);
        System.out.println("Student registered successfully.");
        save();
    }

    static void printMenu() {
        System.out.println("\nSTUDENT COURSE REGISTRATION SYSTEM");
        System.out.println("1. Add Student");
        System.out.println("2. View/Search Students");
        System.out.println("3. Add Course");
        System.out.println("4. View/Search Courses");
        System.out.println("5. Register Student for Course");
        System.out.println("6. View Student Academic Summary");
        System.out.println("7. View Students Registered in a Course");
        System.out.println("8. Save Data");
        System.out.println("0. Exit");
    }

    public static void main(String[] args) {
        try {
            FileManager.load(r);
            System.out.println("Saved data loaded.");
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
        }

        while (true) {
            printMenu();
            try {
                int choice = readInt("Enter your choice: ");
                switch (choice) {
                    case 1: addStudent(); break;
                    case 2: viewSearchStudents(); break;
                    case 3: addCourse(); break;
                    case 4: viewSearchCourses(); break;
                    case 5: registerStudent(); break;
                    case 6: r.printSummary(readInt("Student ID: ")); break;
                    case 7: r.displayStudentsInCourse(readText("Course code: ", false).toUpperCase()); break;
                    case 9: save(); break;
                    case 0:
                        save();
                        System.out.println("-----------END OF PROGRAM-----------");
                        return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (RegistrationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}