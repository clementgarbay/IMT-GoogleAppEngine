package com.zenika.zencontact.resource;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.fetch.PartnerBirthdayService;
import com.zenika.zencontact.persistence.UserDao;
import com.zenika.zencontact.persistence.objectify.UserDaoObjectify;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "UserResource", value = "/api/v0/users")
public class UserResource extends HttpServlet {

    private final UserDao userDao = UserDaoObjectify.getInstance();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
    protected static final String CONTACTS_CACHE_KEY = "com.zenika.zencontact.cache";
    protected static final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> contacts = (List<User>) cache.get(CONTACTS_CACHE_KEY);
        if (isNull(contacts)) {
            contacts = userDao.getAll();
            boolean isCached = contacts.size() > 0 && cache.put(
              CONTACTS_CACHE_KEY,
              contacts,
              Expiration.byDeltaMillis(240),
              MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT
            );
            LOG.log(Level.INFO, "Cached? " + isCached);
        } else {
            LOG.log(Level.INFO, "Cache HITS");
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().println(new Gson().toJsonTree(contacts).getAsJsonArray());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = new Gson().fromJson(request.getReader(), User.class);

        String birthday = PartnerBirthdayService.getInstance().findBirthday(user.firstName, user.lastName);

        if (birthday != null) {
            try {
                user.birthdate(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            } catch (ParseException ignored) {}
        }

        user.id(userDao.save(user));

        response.setContentType("application/json; charset=utf-8");
        response.setStatus(201);
        response.getWriter().println(new Gson().toJson(user));
    }
}
