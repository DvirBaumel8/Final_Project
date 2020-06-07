package Company;

public class Person {
    private String fullName;
    private String id;
    private int age;
    private String address;

    public Person(String fullName, String id, int age, String address) {
        this.fullName = fullName;
        this.id = id;
        this.age = age;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return String.format("Person Details:\n -Full Name: %s\n -ID: %s\n -Age: %d\n -Address: %s\n", fullName, id, age, address);
    }
}
