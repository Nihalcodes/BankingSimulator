package com.bank.service;

import com.bank.ConfigLoader;
import com.bank.model.Account;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Logger;

public class AlertService {
    private static final Logger logger = Logger.getLogger(AlertService.class.getName());
    private static final BigDecimal THRESHOLD = new BigDecimal("100.00");
    private static final String SMTP_EMAIL = ConfigLoader.get("smtp.email");
    private static final String SMTP_PASSWORD = ConfigLoader.get("smtp.password");

    public void checkAndAlert(Account account) {
        if (account.getBalance().compareTo(THRESHOLD) < 0) {
            sendLowBalanceAlert(account);
        }
    }

    private void sendLowBalanceAlert(Account account) {
        String recipientEmail = account.getEmail();
        if (recipientEmail == null || recipientEmail.isEmpty() || !recipientEmail.contains("@")) {
            logger.info("📧 No valid email for " + account.getOwnerName() + ". Alert skipped.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_EMAIL, SMTP_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SMTP_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            msg.setSubject("🏦 Low Balance Alert - Banking Simulator");
            String body = String.format(
                    "Dear %s,\n\n" +
                            "Your account balance is now $%.2f, which is below the alert threshold of $%.2f.\n\n" +
                            "Account ID: %d\n" +
                            "Time: %s\n\n" +
                            "This is an automated message from the Banking Transaction Simulator.",
                    account.getOwnerName(),
                    account.getBalance(),
                    THRESHOLD,
                    account.getAccountId(),
                    java.time.LocalDateTime.now()
            );
            msg.setText(body);
            Transport.send(msg);
            logger.info("✅ Low balance alert email sent to " + recipientEmail);
        } catch (Exception e) {
            logger.severe("❌ Failed to send alert to " + recipientEmail + ": " + e.getMessage());
        }
    }
}