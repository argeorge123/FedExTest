package edu.washu.bms.fedex.fedexintegration.service;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;

public interface EmailService {
    void sendSimpleEmail(final String to, final String subject, final String message);
}
