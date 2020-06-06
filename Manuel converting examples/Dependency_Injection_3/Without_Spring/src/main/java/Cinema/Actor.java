package Cinema;

public class Actor {
    public enum Gender {Male, Female}

    private Integer ID;
    private String fullName;
    private Integer age;
    private Gender gender;

    public Actor(Integer ID, String fullName, Integer age, Gender gender) {
        this.ID = ID;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
    }

    public Actor() {

    }

    public Actor(Integer ID, String fullName) {
        this.ID = ID;
        this.fullName = fullName;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("ID- " + ID + "\n");
        str.append("Full Name- " + fullName + "\n");
        str.append("Age- " + age + "\n");
        str.append("Gender- " + gender + "\n\n");

        return str.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return ((Actor)obj).getID() == this.ID;
    }

    @Override
    public int hashCode() {
        return this.ID;
    }
}
