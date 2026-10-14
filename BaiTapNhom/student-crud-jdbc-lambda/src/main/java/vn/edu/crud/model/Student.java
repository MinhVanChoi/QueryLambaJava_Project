package vn.edu.crud.model;

import java.util.Objects;

public class Student {
    private int id;
    private String name;
    private String email;
    private int age;
    private Integer classId;
    private String classCode;

    public Student() {
    }

    public Student(String name, String email, int age, Integer classId) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.classId = classId;
    }

    public Student(int id, String name, String email, int age, Integer classId, String classCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.classId = classId;
        this.classCode = classCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    @Override
    public String toString() {
        return String.format(
                "%-4d | %-24s | %-28s | %-3d | %-10s",
                id,
                name,
                email,
                age,
                classCode == null ? "N/A" : classCode
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
