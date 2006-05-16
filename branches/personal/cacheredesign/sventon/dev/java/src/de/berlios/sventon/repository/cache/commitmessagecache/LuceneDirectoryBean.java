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
package de.berlios.sventon.repository.cache.commitmessagecache;

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
  private String path = null;
  private Directory directory = null;
  private boolean create = false;
  private boolean diskPersistent = false;

  public boolean isCreate() {
    return create;
  }

  public void setCreate(final boolean create) {
    this.create = create;
  }

  public boolean isDiskPersistent() {
    return diskPersistent;
  }

  public void setDiskPersistent(final boolean diskPersistent) {
    this.diskPersistent = diskPersistent;
  }

  public String getPath() {
    return path;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  public Object getObject() throws Exception {
    return directory;
  }

  public Class getObjectType() {
    if (diskPersistent) {
      return FSDirectory.class;
    } else {
      return RAMDirectory.class;
    }
  }

  public boolean isSingleton() {
    return true;
  }

  public void afterPropertiesSet() throws Exception {
    if (path == null) {
      throw new BeanInitializationException("No path specified for lucene index directory");
    }
    final File file = new File(path);
    if (!file.isDirectory()) {
      throw new BeanInitializationException("Invalid path for lucene index directory");
    }

    if (diskPersistent) {
      directory = FSDirectory.getDirectory(file, create);
      if (!IndexReader.indexExists(directory)) {
        IndexWriter iw = new IndexWriter(directory, new StandardAnalyzer(), true);
        iw.close();
      }
    } else {
      directory = new RAMDirectory();
    }
  }
}