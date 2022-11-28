package jpql;

public class MemberDTO {

    private String username;
    private int age;

    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
