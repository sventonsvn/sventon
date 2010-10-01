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
package org.sventon.web.ctrl;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.sventon.AuthenticationException;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.*;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.NoRouteToHostException;

/**
 *
 */
public class BaseController {
  /**
   * Validator for input.
   */
  protected Validator validator;

  protected DefaultControllerHandler controllerHandler;


  public BaseController(Validator validator, DefaultControllerHandler controllerHandler) {
    this.validator = validator;
    this.controllerHandler = controllerHandler;
  }

  @InitBinder("baseCommand")
  public void initValidator(WebDataBinder binder){
    if (validator != null){
      binder.setValidator(validator);
    }
  }

  @RequestMapping()
  public String defaultRequestHandler(
      @PathVariable("name") final RepositoryName name,
      @RequestParam(value="userName", defaultValue="") final String username,
      @RequestParam(value="userPassword", defaultValue="") final String password,
      @ModelAttribute final BaseCommand command,
      @ModelAttribute UserContext userContext,
      final BindingResult error,
      final HttpServletRequest request,
      final Model model) throws Exception{
    command.setName(name);
    setupUserContext(userContext, command, username, password);

    //TODO: Parse to apply Bugtraq link

    return controllerHandler != null ? controllerHandler.execute(command, userContext, request, model, error) : null;
  }

  protected static void setupUserContext(UserContext userContext, BaseCommand command, String username, String password) {
    if (!(username.isEmpty() || password.isEmpty())){
      UserRepositoryContext userRepositoryContext = userContext.getOrCreateUserRepositoryContext(command.getName());
      userRepositoryContext.setCredentials(new Credentials(username, password));
    }
  }

  public void setControllerContext(DefaultControllerHandler controllerHandler) {
    this.controllerHandler = controllerHandler;
  }

  public interface ControllerClosure<T> {
    T execute(final RepositoryService service, final SVNConnection connection, BaseCommand command, Model model) throws Exception;
  }

  public interface ControllerHandler<T> {
    T execute(final BaseCommand command, final UserContext userContext, HttpServletRequest request, Model model, BindingResult error) throws Exception;
  }

  public static class DefaultControllerHandler implements ControllerHandler<String> {
    private final RepositoryService repositoryService;
    private final ConnectionFactory connectionFactory;
    private final Application application;
    private final ControllerClosure<String> closure;
    private static final String AUTHENTICATION_REQUIRED_VIEW = "error/authenticationRequired";
    private static final String ERROR_VIEW = "goto";

    public DefaultControllerHandler(Application application, RepositoryService repositoryService, SVNConnectionFactory connectionFactory, ControllerClosure<String> closure) {
      this.repositoryService = repositoryService;
      this.connectionFactory = new ExtendedConnectionFactory(application, connectionFactory);
      this.application = application;
      this.closure = closure;
    }


    @Override
    public String execute(final BaseCommand command, final UserContext userContext, final HttpServletRequest request, final Model model, BindingResult error) throws Exception {
      SVNConnection connection = null;
      String view = ERROR_VIEW;
      try {
        connection = connectionFactory.create(command, userContext);

        // Translate revision relative head revision
        final Long headRevision = repositoryService.getLatestRevision(connection);
        final Revision revision = repositoryService.translateRevision(connection, command.getRevision(), headRevision);
        command.setRevision(revision);
        command.setHeadRevision(Revision.create(headRevision));

        view = closure.execute(repositoryService, connection, command, model);

        setupDefaultModelAttributes(model, command);
      } catch (AuthenticationException ae){
        model.addAttribute("parameters", request.getParameterMap());
        model.addAttribute("action", request.getRequestURL());
        model.addAttribute("repositoryNames", application.getRepositoryNames());
        model.addAttribute("isEditableConfig", application.isEditableConfig());

        view = AUTHENTICATION_REQUIRED_VIEW;
      } catch (Exception e) {
        final RepositoryConfiguration repositoryConfiguration = application.getConfiguration(command.getName());
        model.addAttribute("command", command);
        model.addAttribute("repositoryURL", repositoryConfiguration != null ? repositoryConfiguration.getRepositoryDisplayUrl() : "");

        if (e.getCause() instanceof NoRouteToHostException || e.getCause() instanceof ConnectException) {
          error.reject("error.message.no-route-to-host");
        } else {
          error.reject(null, e.getMessage());
        }

        view = ERROR_VIEW;
      } finally {
        if (connection != null){
          connection.closeSession();
        }
      }

      return view;
    }

    /**
     * Setup default model attributes that are used by all views. For specific attributes, set them in resp. controller
     *
     * @param model the Model to be modified
     * @param command the BaseCommand object
     */
    private void setupDefaultModelAttributes(final Model model, final BaseCommand command){
      model.addAttribute("command", command);
      model.addAttribute("repositoryURL", application.getConfiguration(command.getName()).getRepositoryDisplayUrl());
      model.addAttribute("headRevision", command.getHeadRevision().getNumber()); //TODO: BaseCommand contains already this info. Use this in all views instead
      model.addAttribute("isHead", command.isHeadRevision()); //TODO: BaseCommand contains already this info. Use this in all views instead
      model.addAttribute("baseURL", application.getBaseURL());
    }
  }


  public interface ConnectionFactory{
    SVNConnection create(BaseCommand command, UserContext userContext) throws SventonException;
  }

  public static class ExtendedConnectionFactory implements ConnectionFactory{
    /**
     * The application.
     */
    private Application application;

    /**
     * The repository connection factory.
     */
    private SVNConnectionFactory connectionFactory;


    public ExtendedConnectionFactory(final Application application, final SVNConnectionFactory connectionFactory) {
      this.application = application;
      this.connectionFactory = connectionFactory;
    }

    @Override
    public SVNConnection create(BaseCommand command, UserContext userContext)  throws SventonException{
      final RepositoryConfiguration configuration = application.getConfiguration(command.getName());

      final SVNConnection connection;
      final RepositoryName repositoryName = configuration.getName();
      final SVNURL svnurl = configuration.getSVNURL();

      if (configuration.isAccessControlEnabled()) {
        UserRepositoryContext repositoryContext = userContext.getOrCreateUserRepositoryContext(command.getName());
        connection = connectionFactory.createConnection(repositoryName, svnurl, repositoryContext.getCredentials());
      } else {
        connection = connectionFactory.createConnection(repositoryName, svnurl, configuration.getUserCredentials());
      }

      return connection;
    }
  }
}
