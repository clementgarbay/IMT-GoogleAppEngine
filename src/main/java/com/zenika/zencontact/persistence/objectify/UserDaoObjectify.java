package com.zenika.zencontact.persistence.objectify;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserDao;

import java.util.List;
import java.util.Optional;

/**
 * @author Clément Garbay
 */
public class UserDaoObjectify implements UserDao {

    private static final UserDaoObjectify INSTANCE = new UserDaoObjectify();

    private UserDaoObjectify() {
        ObjectifyService.factory().register(User.class);
    }

    public static UserDaoObjectify getInstance() {
        return INSTANCE;
    }

    @Override
    public long save(User contact) {
        return ObjectifyService.ofy().save().entity(contact).now().getId();
    }

    @Override
    public void delete(Long id) {
        ObjectifyService.ofy().delete().key(getKey(id)).now();
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.of(ObjectifyService.ofy().load().key(getKey(id)).now());
    }

    @Override
    public List<User> getAll() {
        return ObjectifyService.ofy().load().type(User.class).list();
    }

    private Key<User> getKey(Long id) {
        return Key.create(User.class, id);
    }

    public BlobKey fetchOldBlob(Long id) {
        return this.get(id).map(user -> user.photoKey).orElse(null); // TODO: do not use orElse of null
    }
}
