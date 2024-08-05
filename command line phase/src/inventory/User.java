package inventory;

import java.util.HashMap;

public class User {
    private HashMap<String, String> info = new HashMap<>();

    public User(String username, String password, String nickname, String email, String securityQuestion, String securityAnswer) {
        info.put("username", username);
        info.put("password", password);
        info.put("nickname", nickname);
        info.put("email", email);
        info.put("securityQ", securityQuestion);
        info.put("securityA", securityAnswer);
    }

    public String getInfo(String key) {
        return info.get(key);
    }

    public void setInfo(String key, String value) {
        info.put(key, value);
    }

    public boolean validatePassword(String password) {
        return info.get("password").equals(password);
    }

    public boolean validateSecurityQuestion(String securityA) {
        return info.get("securityA").equals(securityA);
    }
}