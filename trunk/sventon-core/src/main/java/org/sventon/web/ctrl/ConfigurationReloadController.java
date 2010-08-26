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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.CacheException;
import org.sventon.web.command.ConfigCommand;
import org.sventon.web.command.ConfigCommandValidator;

import javax.validation.Valid;

import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Simple controller to reload configuration files and reinitialize application.
 *
 * @author patrik@sventon.org
 */
@Controller("configurationReloadController")
@RequestMapping("/config/reload")
public final class ConfigurationReloadController {

  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The application.
   */
  private final Application application;

  /**
   * Constructor.
   *
   * @param application Application instance
   */
  @Autowired
  public ConfigurationReloadController(final Application application) {
    this.application = application;
  }

  @RequestMapping(method = GET)
  @ResponseBody
  public ResponseEntity<String> reloadConfigAndReinitializeApplication() {

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.TEXT_PLAIN);

    try {
      application.reinit();
      return new ResponseEntity<String>("Configuration reloaded.", responseHeaders, OK);
    } catch (Exception e) {
      logger.error("Failed to reload configuration files.", e);
      return new ResponseEntity<String>("Internal error.", responseHeaders, INTERNAL_SERVER_ERROR);
    }
  }
}
