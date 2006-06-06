/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.logmessagecache;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanInitializationException;

import java.io.File;

/**
 * Lucene directory initialization bean.
 *
 * @author jesper@users.berlios.de
 */
public class LuceneDirectoryBean implements FactoryBean, InitializingBean {

  /**
   * The <i>lucene</i> directory. Can be RAMDirectory or FSDirectory.
   * Specified by setting <code>diskPersistent</code> property.
   *
   * @see org.apache.lucene.store.FSDirectory
   * @see org.apache.lucene.store.RAMDirectory
   */
  private Directory directory = null;

  /**
   * Path where to store files. Only used if <code>diskPersistent</code> is <code>true</code>.
   */
  private String path = null;

  /**
   * Controls whether lucene cache will be disk persistent or not.
   */
  private boolean diskPersistent = false;

  /**
   * Checks if cache is disk persistent or not.
   *
   * @return True if disk persistent.
   */
  public boolean isDiskPersistent() {
    return diskPersistent;
  }

  /**
   * Sets disk persisten flag.
   *
   * @param diskPersistent True or false.
   */
  public void setDiskPersistent(final boolean diskPersistent) {
    this.diskPersistent = diskPersistent;
  }

  /**
   * Gets the path where files will be store. Only used if <code>diskPersistent</code> is true.
   *
   * @return The path
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the path where files will be store. Only used if <code>diskPersistent</code> is true.
   *
   * @param path The path
   */
  public void setPath(final String path) {
    this.path = path;
  }

  /**
   * {@inheritDoc}
   */
  public Object getObject() throws Exception {
    return directory;
  }

  /**
   * {@inheritDoc}
   */
  public Class getObjectType() {
    if (diskPersistent) {
      return FSDirectory.class;
    } else {
      return RAMDirectory.class;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSingleton() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void afterPropertiesSet() throws Exception {
    if (diskPersistent) {
      if (path == null) {
        throw new BeanInitializationException("No path specified for lucene index directory");
      }

      final File file = new File(path);
      if (!file.isDirectory()) {
        throw new BeanInitializationException("Not a valid directory: " + path);
      }

      directory = FSDirectory.getDirectory(file, false);
      if (!IndexReader.indexExists(directory)) {
        IndexWriter iw = new IndexWriter(directory, new StandardAnalyzer(), true);
        iw.close();
      }
    } else {
      directory = new RAMDirectory();
    }
  }
}