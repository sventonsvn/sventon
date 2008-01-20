/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.mail;

import com.sun.mail.smtp.SMTPTransport;
import de.berlios.sventon.appl.AbstractRevisionObserver;
import de.berlios.sventon.appl.RevisionUpdate;
import de.berlios.sventon.util.HTMLCreator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class for notifying users about new revisions.
 */
public class MailNotifier extends AbstractRevisionObserver {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(MailNotifier.class);

  /**
   * Set of receivers.
   */
  private final Set<InternetAddress> receivers = new HashSet<InternetAddress>();

  /**
   * Text replacement token for revision number.
   */
  private static final String REVISION_TOKEN = "@@revision@@";

  /**
   * Text replacement token for the instance name.
   */
  private static final String NAME_TOKEN = "@@instanceName@@";

  /**
   * Cached HTML mail body template.
   */
  private String bodyTemplate = null;

  /**
   * Date formatter instance.
   */
  private DateFormat dateFormat;

  /**
   * Threshold value that decides if an update is too big to send notification mails.
   */
  private int revisionCountThreshold;

  /**
   * The mail body template file. Default set to <tt>mailtemplate.html</tt> in classpath root.
   */
  private String bodyTemplateFile = "/mailtemplate.html";

  private String host;
  private int port;
  private String from;
  private String subject;
  private boolean auth;
  private boolean ssl;
  private String user;
  private String password;

  /**
   * The mail session.
   */
  private Session session;

  /**
   * Base URL where sventon is located.
   */
  private String baseUrl;

  /**
   * Initializes the component.
   */
  public void init() {
    final Properties mailProperties = new Properties();
    mailProperties.put("mail.smtp.host", host);
    mailProperties.put("mail.smtp.port", port);
    mailProperties.put("mail.smtp.auth", auth ? "true" : "false");

    session = Session.getInstance(mailProperties, null);
    session.setDebug(LOGGER.isDebugEnabled());
  }

  /**
   * {@inheritDoc}
   */
  public void update(final RevisionUpdate revisionUpdate) {

    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();

    if (revisions.size() > revisionCountThreshold) {
      LOGGER.info("Update contains more than max allowed updates, [" +
          revisionCountThreshold + "]. No notification mail sent");
      return;
    }

    for (final SVNLogEntry logEntry : revisions) {
      final String instanceName = revisionUpdate.getInstanceName();
      LOGGER.info("Sending notification mail for [" + instanceName + "], revision: " + logEntry.getRevision());

      try {
        final Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.BCC, receivers.toArray(new InternetAddress[0]));
        msg.setSubject(formatSubject(subject, logEntry.getRevision(), instanceName));

        msg.setDataHandler(new DataHandler(new ByteArrayDataSource(HTMLCreator.createRevisionDetailBody(
            getBodyTemplate(), logEntry, baseUrl, instanceName, dateFormat, null), "text/html")));

        msg.setHeader("X-Mailer", "sventon");
        msg.setSentDate(new Date());

        final SMTPTransport transport = (SMTPTransport) session.getTransport(ssl ? "smtps" : "smtp");

        try {
          if (auth) {
            transport.connect(host, user, password);
          } else {
            transport.connect();
          }
          transport.sendMessage(msg, msg.getAllRecipients());
        } finally {
          transport.close();
        }
        LOGGER.debug("Notification mail was sent successfully");
      } catch (Exception e) {
        LOGGER.error("Unable to send notification mail", e);
      }
    }
  }

  /**
   * Creates the mail subject.
   *
   * @param subject      Subject string.
   * @param revision     Revision.
   * @param instanceName Instance name.
   * @return Substituted subject string.
   */
  protected String formatSubject(final String subject, long revision, final String instanceName) {
    String result = subject;
    result = result.replace(REVISION_TOKEN, String.valueOf(revision));
    result = result.replace(NAME_TOKEN, instanceName);
    return result;
  }

  /**
   * Gets the HTML mail body template.
   *
   * @return The template.
   * @throws IOException if unable to load template.
   */
  protected String getBodyTemplate() throws IOException {
    if (bodyTemplate == null) {
      final InputStream is = this.getClass().getResourceAsStream(bodyTemplateFile);
      if (is == null) {
        throw new FileNotFoundException("Unable to find: " + bodyTemplateFile);
      }
      bodyTemplate = IOUtils.toString(is);
    }
    return bodyTemplate;
  }

  /**
   * Sets the base URL used when creating HTML anchor links back to
   * the installed sventon application.
   *
   * @param baseUrl Base URL, eg. http://yourserver.com/svn/.
   */
  public void setBaseUrl(final String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Sets the date format.
   *
   * @param dateFormat Date format.
   */
  public void setDateFormat(final String dateFormat) {
    this.dateFormat = new SimpleDateFormat(dateFormat);
  }

  /**
   * Sets the file that should be used as the mail body template.
   *
   * @param bodyTemplateFile Template file.
   */
  public void setBodyTemplateFile(final String bodyTemplateFile) {
    this.bodyTemplateFile = bodyTemplateFile;
  }

  /**
   * Sets the SMTP mail host.
   *
   * @param host Mail host.
   */
  public void setHost(final String host) {
    this.host = host;
  }

  /**
   * Sets the SMTP mail host port.
   *
   * @param port Mail host port.
   */
  public void setPort(final int port) {
    this.port = port;
  }

  /**
   * Sets the sender address of the mail notification.
   *
   * @param from Sender's address.
   */
  public void setFrom(final String from) {
    this.from = from;
  }

  /**
   * Sets the notification mail subject.
   *
   * @param subject Subject.
   */
  public void setSubject(final String subject) {
    this.subject = subject;
  }

  /**
   * Sets the mail notification receivers addresses.
   *
   * @param receivers List of the receiver's email addresses.
   */
  public void setReceivers(final List<String> receivers) {
    for (final String receiver : receivers) {
      try {
        this.receivers.add(new InternetAddress(receiver));
      } catch (AddressException e) {
        LOGGER.warn("Unable to parse email address: " + receiver, e);
      }
    }
  }

  /**
   * Set to true if authorization is required.
   *
   * @param auth True/false
   */
  public void setAuth(final boolean auth) {
    this.auth = auth;
  }

  /**
   * Set to true if SSL should be used.
   *
   * @param ssl True/false
   */
  public void setSsl(final boolean ssl) {
    this.ssl = ssl;
  }

  /**
   * Sets the SMTP host user.
   *
   * @param user User.
   */
  public void setUser(final String user) {
    this.user = user;
  }

  /**
   * Sets the SMTP host user's password.
   *
   * @param password Password.
   */
  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * Sets the threshold value that decides if an update is too big to
   * send notification mails.
   *
   * @param revisionCountThreshold Threshold value. If an update contains more
   *                               revisions than the given threshold, no notification
   *                               mail will be sent.
   */
  public void setRevisionCountThreshold(int revisionCountThreshold) {
    this.revisionCountThreshold = revisionCountThreshold;
  }
}
