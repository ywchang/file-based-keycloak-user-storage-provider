package com.flyer.keycloak.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JSON file based user repository singleton
 *
 * @author Ruifeng Ma
 * @since 2019-May-25
 */

@JBossLog
public class FileUserRepository implements UserRepository {

    private String filePath;
    private Map<String, User> userMap;

    private static FileUserRepository instance;

    private FileUserRepository(String filePath) {
        this.filePath = filePath;
        this.userMap = new HashMap<>();
    }

    public static FileUserRepository getInstance(String filePath) {
        if (instance == null) instance = new FileUserRepository(filePath);
        return instance;
    }

    /**
     * Persist user data changes at the end of transaction
     *
     * @throws IOException
     */
    public void persistUserDataToFile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(this.filePath), this.userMap.values());
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void insertUser(User user) {
        this.userMap.put(user.getUsername(), user);
    }

    @Override
    public User getUser(String username) {
        return this.userMap.get(username);
    }

    @Override
    public void updateUser(User user) {
        this.userMap.replace(user.getUsername(), user);
    }

    @Override
    public void removeUser(String username) {
        this.userMap.remove(username);
    }

    @Override
    public int getUserCount() {
        return this.userMap.size();
    }

    @Override
    public List<User> getAllUsers() {
        return this.userMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<User> findUserByKeyword(String keyword) {
        String query = keyword.toLowerCase();
        return this.userMap.values().stream()
                .filter(user -> user.getUsername().contains(query)
                                || user.getFirstName().contains(query)
                                || user.getLastName().contains(query))
                .collect(Collectors.toList());
    }
}
