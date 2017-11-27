package com.zenika.zencontact.fetch;

import com.google.appengine.api.urlfetch.*;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Clément Garbay
 */
public class PartnerBirthdayService {
    private static PartnerBirthdayService ourInstance = new PartnerBirthdayService();
    private static final String SERVICE_URL = "http://zenpartenaire.appspot.com";
    private static final Logger LOG = Logger.getLogger(PartnerBirthdayService.class.getName());

    public static PartnerBirthdayService getInstance() {
        return ourInstance;
    }

    private PartnerBirthdayService() {
    }

    public String findBirthday(String firstName, String lastName) {
        String payload = firstName + " " + lastName; // John Doe | Bob Smith

        try {
            URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();

            URL url = new URL("http://zenpartenaire.appspot.com/zenpartenaire");

            HTTPRequest postRequest = new HTTPRequest(url, HTTPMethod.POST, FetchOptions.Builder.withDeadline(30));

            postRequest.setPayload(payload.getBytes());

            HTTPResponse response = fetcher.fetch(postRequest);
            if(response.getResponseCode() != 200) return null;
            String result = new String(response.getContent()).trim();
            LOG.warning("From partners: " + result);

            return result.length() > 0 ? result : null;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }
}
