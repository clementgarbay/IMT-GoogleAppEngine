package com.zenika.zencontact.persistence;

import com.google.appengine.api.datastore.*;
import com.zenika.zencontact.domain.User;

import java.util.Date;

import static java.util.Objects.isNull;

/**
 * @author Clément Garbay
 */
public class UserEntityHelper {

    private String kind;

    public UserEntityHelper(String kind) {
        this.kind = kind;
    }

    public User fromEntity(Entity entity) {
        return User.create().id(entity.getKey().getId())
                .firstName((String) entity.getProperty("firstname"))
                .lastName((String) entity.getProperty("lastname"))
                .email((String) entity.getProperty("email"))
                .birthdate((Date) entity.getProperty("birthday"))
                .notes((String) entity.getProperty("notes"));
    }

    public Entity toEntity(User user, Entity entity) {
        entity.setProperty("firstname", user.firstName);
        entity.setProperty("lastname", user.lastName);
        entity.setProperty("email", user.email);
        if (!isNull(user.birthdate)) {
            entity.setProperty("birthdate", user.birthdate);
        }
        entity.setProperty("notes", user.notes);
        return entity;
    }

    public Entity toEntity(User user) {
        return toEntity(user, new Entity(kind));
    }

    public Key getKeyFromId(Long id) {
        return KeyFactory.createKey(kind, id);
    }
}
