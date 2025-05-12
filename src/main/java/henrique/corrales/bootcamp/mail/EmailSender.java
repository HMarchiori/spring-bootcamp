package henrique.corrales.bootcamp.mail;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class EmailSender implements Serializable {

    private final JavaMailSender javaMailSender;
    private String to;
    private String body;
    private String subject;
    private File attachment;
    private ArrayList<InternetAddress> recipients = new ArrayList<>();

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }

    public EmailSender to(String to) {
        this.to = to;
        this.recipients = getRecipients(to);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    private ArrayList<InternetAddress> getRecipients(String to) {
        String toReplaced = to.replaceAll("\\s", "");
        StringTokenizer tokenizer = new StringTokenizer(toReplaced, ";");

        ArrayList<InternetAddress> recipientsList = new ArrayList<>();
        while (tokenizer.hasMoreElements()) {
            try {
                recipientsList.add(new InternetAddress(tokenizer.nextElement().toString()));
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        }
        return recipientsList;
    }


}
