public class Member {
    private String name;
    private int birthyear;
    private String phone;

    public Member(String name, int birthyear, String phone) {
        this.name = name;
        this.birthyear = birthyear;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
