package com.zenika.zencontact.email;

import com.zenika.zencontact.domain.Email;
import com.zenika.zencontact.resource.auth.AuthenticationService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * @author Clément Garbay
 */
public class EmailService {
    private static EmailService ourInstance = new EmailService();
    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    public static EmailService getInstance() {
        return ourInstance;
    }

    private EmailService() {
    }

    public void sendEmail(Email email) {
        try {
            Message msg = buildMessage();

            msg.setFrom(new InternetAddress(
                    AuthenticationService.getInstance().getUser().getEmail(),
                    AuthenticationService.getInstance().getUsername()));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email.to, email.toName));

            msg.setReplyTo(new Address[]{
                    new InternetAddress("team@imt-2017-11-clement.appspotmail.com",
                            "Application team")});
            msg.setSubject(email.subject);
            msg.setText(email.body);

            Transport.send(msg);
            LOG.warning("mail envoyé!");
        } catch (MessagingException | UnsupportedEncodingException ignored) {}
    }

    public void handleEmail(HttpServletRequest request) {
        try {
            Message message = buildMessage(request.getInputStream());

            LOG.warning("Subject: " + message.getSubject());

            Multipart multipart = (Multipart) message.getContent();
            BodyPart part = multipart.getBodyPart(0);

            LOG.warning("Body: " + part.getContent());

            Stream.of(message.getFrom()).forEach(sender -> LOG.warning("From: " + sender.toString()));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private Message buildMessage(InputStream inputStream) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        return isNull(inputStream) ? new MimeMessage(session) : new MimeMessage(session, inputStream);
    }

    private Message buildMessage() throws MessagingException {
        return buildMessage(null);
    }
}
