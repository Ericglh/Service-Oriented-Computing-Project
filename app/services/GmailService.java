package services;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by htyleo on 3/30/17.
 */
@Singleton
public class GmailService {

  public static void main(String[] args) throws Exception {
    GmailService gmailService = new GmailService();
    Message message = gmailService.sendEmail("htyleo@gmail.com", "SOC test", "SOC");
    System.out.println("Message id: " + message.getId());
    System.out.println(message.toPrettyString());
  }

  private Gmail gmail;

  public GmailService() {
    try {
      gmail = GmailApiAuthorization.getGmailService();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Message sendEmail(String to, String subject, String bodyText)
      throws MessagingException, IOException {
    MimeMessage emailContent = createEmail(to, "me", subject, bodyText);
    return sendMessage(gmail, "me", emailContent);
  }

  /**
   * Create a MimeMessage using the parameters provided.
   *
   * @param to email address of the receiver
   * @param from email address of the sender, the mailbox account
   * @param subject subject of the email
   * @param bodyText body text of the email
   * @return the MimeMessage to be used to send email
   */
  private static MimeMessage createEmail(String to, String from, String subject, String bodyText)
      throws MessagingException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage email = new MimeMessage(session);
    email.setFrom(new InternetAddress(from));
    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
    email.setSubject(subject);
    email.setText(bodyText);
    return email;
  }

  /**
   * Send an email from the user's mailbox to its recipient.
   *
   * @param service Authorized Gmail API instance.
   * @param userId User's email address. The special value "me" can be used to indicate the
   * authenticated user.
   * @param emailContent Email to be sent.
   * @return The sent message
   */
  private static Message sendMessage(Gmail service, String userId, MimeMessage emailContent)
      throws MessagingException, IOException {
    Message message = createMessageWithEmail(emailContent);
    message = service.users().messages().send(userId, message).execute();
    return message;
  }

  /**
   * Create a message from an email.
   *
   * @param emailContent Email to be set to raw of message
   * @return a message containing a base64url encoded email
   */
  private static Message createMessageWithEmail(MimeMessage emailContent)
      throws MessagingException, IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }

}
