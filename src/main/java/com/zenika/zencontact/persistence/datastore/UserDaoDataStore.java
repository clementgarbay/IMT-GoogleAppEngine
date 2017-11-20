package com.zenika.zencontact.persistence.datastore;

import com.google.appengine.api.datastore.*;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserEntityHelper;
import com.zenika.zencontact.persistence.UserDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;

/**
 * @author Clément Garbay
 */
public class UserDaoDataStore implements UserDao {

    private static final String KIND = "User";
    public static final UserDaoDataStore instance = new UserDaoDataStore();
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final UserEntityHelper userEntityHelper = new UserEntityHelper(KIND);

    private UserDaoDataStore() {}

    @Override
    public long save(User contact) {
        Entity entity = isNull(contact.id)
                ? userEntityHelper.toEntity(contact)
                : getEntity(contact.id)
                    .map(e -> userEntityHelper.toEntity(contact, e))
                    .orElse(userEntityHelper.toEntity(contact));

        return datastore.put(entity).getId();
    }

    @Override
    public void delete(Long id) {
        datastore.delete(userEntityHelper.getKeyFromId(id));
    }

    @Override
    public Optional<User> get(Long id) {
        return getEntity(id).map(userEntityHelper::fromEntity);
    }

    @Override
    public List<User> getAll() {
        Query query = new Query(KIND)
                .addProjection(new PropertyProjection("firstname", String.class))
                .addProjection(new PropertyProjection("lastname", String.class))
                .addProjection(new PropertyProjection("email", String.class))
                .addProjection(new PropertyProjection("notes", String.class));

        PreparedQuery preparedQuery = datastore.prepare(query);

        return StreamSupport.stream(preparedQuery.asIterable().spliterator(), false)
                .map(userEntityHelper::fromEntity).collect(Collectors.toList());
    }

    private Optional<Entity> getEntity(Long id) {
        try {
            return Optional.of(datastore.get(userEntityHelper.getKeyFromId(id)));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }
}
