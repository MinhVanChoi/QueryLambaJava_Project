package vn.edu.crud.model;

public class StudentClass {
    private int id;
    private String classCode;
    private String className;

    public StudentClass() {
    }

    public StudentClass(int id, String classCode, String className) {
        this.id = id;
        this.classCode = classCode;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return String.format("%-4d | %-12s | %s", id, classCode, className);
    }
}
