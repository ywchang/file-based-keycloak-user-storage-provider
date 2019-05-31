package com.flyer.keycloak.extension;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JBossLog
public class FileUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserRegistrationProvider {
    private final KeycloakSession session;
    private final ComponentModel model; // represents how the provider is enabled and configured within a specific realm
    private final FileUserRepository userRepository;
    private final Map<String, UserModel> loadedUsers;


    public FileUserStorageProvider(KeycloakSession session, ComponentModel model, FileUserRepository userRepository) {
        this.session = session;
        this.model = model;
        this.userRepository = userRepository;
        this.loadedUsers = new HashMap<>();
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        StorageId storageId = new StorageId(id);
        String externalId = storageId.getExternalId();
        return getUserByUsername(externalId, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        UserModel adapter = loadedUsers.get(username);
        if (adapter == null) {
            User user = userRepository.getUser(username);
            if (user != null) {
                adapter = createAdapter(realm, user);
                loadedUsers.put(username, adapter);
            }
        }
        return adapter;
    }

    private UserModel createAdapter(RealmModel realm, User user) {
        UserModel local = session.userLocalStorage().getUserByUsername(user.getUsername(), realm);
        if (local == null) {
            local = session.userLocalStorage().addUser(realm, user.getUsername());
            local.setFederationLink(model.getId());
        }
        return new UserModelDelegate(local) {
            @Override
            public void setUsername(String username) {
                log.infov("=======> setting username is called");
                user.setUsername(username);
                userRepository.persistUserDataToFile();
                super.setUsername(username);
            }

            @Override
            public void setEmail(String email) {
                log.infov("=======> setting email is called");
                user.setEmail(email);
                userRepository.persistUserDataToFile();
                super.setEmail(email);
            }

            @Override
            public void setFirstName(String firstName) {
                user.setFirstName(firstName);
                userRepository.persistUserDataToFile();
                super.setFirstName(firstName);
            }

            @Override
            public void setLastName(String lastName) {
                user.setLastName(lastName);
                userRepository.persistUserDataToFile();
                super.setLastName(lastName);
            }

            @Override
            public void setAttribute(String name, List<String> values) {
                user.getAttributes().put(name, values);
                userRepository.persistUserDataToFile();
            }
        };
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }


    @Override
    public UserModel addUser(RealmModel realm, String username) {
        User user = new User(username);
        userRepository.insertUser(user);
        userRepository.persistUserDataToFile();
        UserModel userModel = createAdapter(realm, user);
        return userModel;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        try {
            userRepository.removeUser(user.getUsername());
            userRepository.persistUserDataToFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {

    }

}
