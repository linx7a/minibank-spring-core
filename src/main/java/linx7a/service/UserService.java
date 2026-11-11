package linx7a.service;

import linx7a.model.User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UserService {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int idCounter = 0;

    public User createUser(String login) {
        if (login.isBlank()) {
            throw new IllegalArgumentException("login mustn't be empty");
        }
        boolean exists = users.values().stream().anyMatch(user -> user.getLogin().equals(login));
        if (exists) {
            throw new IllegalArgumentException("user with login " + login + " already exists");
        }
        idCounter++;
        User user = new User(idCounter, login);
        users.put(user.getId(), user);
        return user;
    }

    public User findById(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("user with id=" + userId + " doesn't exist");
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
