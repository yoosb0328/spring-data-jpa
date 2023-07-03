package study.datajpa.repository;

public class UsernameOnlyDto {
    private final String username;

    public UsernameOnlyDto(String username) { //parameter명을 보고 분석해서 projection 동작
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
