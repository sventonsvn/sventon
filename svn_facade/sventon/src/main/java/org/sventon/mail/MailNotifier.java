/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.mail;

import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RepositoryChangeListener;
import org.sventon.repository.RevisionUpdate;
import org.sventon.util.HTMLCreator;
import org.sventon.util.SVNUtils;
import org.tmatesoft.svn.core.SVNLogEntry;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for notifying users about new revisions.
 */
public final class MailNotifier implements RepositoryChangeListener {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(MailNotifier.class);

  /**
   * The application.
   */
  private Application application;

  /**
   * Set of receivers.
   */
  private final Set<InternetAddress> receivers = new HashSet<InternetAddress>();

  /**
   * Text replacement token for revision number.
   */
  private static final String REVISION_TOKEN = "@@revision@@";

  /**
   * Text replacement token for the repository name
   */
  private static final String NAME_TOKEN = "@@repositoryName@@";

  /**
   * Date formatter instance.
   */
  private DateFormat dateFormat;

  /**
   * Threshold value that decides if an update is too big to send notification mails.
   */
  private int revisionCountThreshold;

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
  private String baseURL;

  /**
   * Executor service.
   */
  private final ExecutorService executorService = Executors.newFixedThreadPool(10);

  /**
   * Initializes the component.
   */
  @PostConstruct
  public void init() {
    final Properties mailProperties = new Properties();
    mailProperties.setProperty("mail.smtp.host", host);
    mailProperties.setProperty("mail.smtp.port", String.valueOf(port));
    mailProperties.setProperty("mail.smtp.auth", auth ? "true" : "false");

    session = Session.getInstance(mailProperties, null);
    session.setDebug(LOGGER.isDebugEnabled());
  }

  @Override
  public void update(final RevisionUpdate revisionUpdate) {

    executorService.execute(new Runnable() {
      public void run() {
        final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();

        if (revisions.size() > revisionCountThreshold) {
          LOGGER.info("Update contains more than max allowed updates, ["
              + revisionCountThreshold + "]. No notification mail sent");
          return;
        }

        for (final SVNLogEntry logEntry : revisions) {
          if (SVNUtils.isAccessible(logEntry)) {
            final RepositoryName repositoryName = revisionUpdate.getRepositoryName();
            final RepositoryConfiguration configuration = application.getRepositoryConfiguration(repositoryName);
            LOGGER.info("Sending notification mail for [" + repositoryName + "], revision: " + logEntry.getRevision());
            sendMailMessage(logEntry, repositoryName, configuration);
          }
        }
      }
    });
  }

  private void sendMailMessage(SVNLogEntry logEntry, RepositoryName repositoryName, RepositoryConfiguration configuration) {
    try {
      final Message msg = createMessage(logEntry, repositoryName, configuration.getMailTemplate());
      final SMTPTransport transport = (SMTPTransport) session.getTransport(ssl ? "smtps" : "smtp");

      try {
        if (auth) {
          transport.connect(host, user, password);
        } else {
          transport.connect();
        }
        transport.sendMessage(msg, msg.getAllRecipients());
        LOGGER.debug("Notification mail was sent successfully");
      } finally {
        transport.close();
      }
    } catch (Exception e) {
      LOGGER.error("Unable to send notification mail", e);
    }
  }

  /**
   * @param logEntry       Log entry
   * @param repositoryName Name
   * @param mailTemplate   Template
   * @return Message
   * @throws MessagingException If a message exception occurs.
   * @throws IOException        if a IO exception occurs while creating the data source.
   */
  private Message createMessage(final SVNLogEntry logEntry, RepositoryName repositoryName, String mailTemplate)
      throws MessagingException, IOException {
    final Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(from));
    msg.setRecipients(Message.RecipientType.BCC, receivers.toArray(new InternetAddress[receivers.size()]));
    msg.setSubject(formatSubject(subject, logEntry.getRevision(), repositoryName));

    msg.setDataHandler(new DataHandler(new ByteArrayDataSource(HTMLCreator.createRevisionDetailBody(
        mailTemplate, logEntry, baseURL, repositoryName, dateFormat, null), "text/html")));

    msg.setHeader("X-Mailer", "sventon");
    msg.setSentDate(new Date());
    return msg;
  }

  /**
   * Creates the mail subject.
   *
   * @param subject        Subject string.
   * @param revision       Revision.
   * @param repositoryName Repository name.
   * @return Substituted subject string.
   */
  protected String formatSubject(final String subject, final long revision, final RepositoryName repositoryName) {
    String result = subject;
    result = result.replace(REVISION_TOKEN, String.valueOf(revision));
    result = result.replace(NAME_TOKEN, repositoryName.toString());
    return result;
  }

  /**
   * Sets the base URL used when creating HTML anchor links back to
   * the installed sventon application.
   *
   * @param baseURL Base URL, eg. http://yourserver.com/svn/.
   */
  public void setBaseURL(final String baseURL) {
    this.baseURL = baseURL;
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
  public void setRevisionCountThreshold(final int revisionCountThreshold) {
    this.revisionCountThreshold = revisionCountThreshold;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
  }

}
