package dev.dev_store_api.common.service.email;

import dev.dev_store_api.common.config.properties.MailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables)
            throws MessagingException, UnsupportedEncodingException {

        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(mailProperties.username(), "Dev Store"); // Set sender name
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendLinkVerify(String to, String link, String username) {
        try {
            Map<String, Object> vars = Map.of(
                    "username", username,
                    "link", link
            );
            // Use new English subject and new template name
            sendHtmlEmail(to, "Activate Your Dev Store Account", "activation-email", vars);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Wrap in a runtime exception to be caught by @Retryable
            throw new RuntimeException("Failed to send activation email to " + to, e);
        }
    }
}
