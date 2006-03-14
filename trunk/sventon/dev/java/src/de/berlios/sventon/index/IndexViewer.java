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
package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntryComparator;

import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple viewer utility for the sventon revision index.
 *
 * @author jesper@users.berlios.de
 */
public class IndexViewer {

  public static void main(String[] args) throws Exception {

    System.out.println("Sventon revision index viewer");
    if (args.length == 0) {
      System.out.println("Syntax: IndexViewer [index file]");
      return;
    }

    System.out.println("Viewing index file: " + args[0]);

    RevisionIndex index;
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[0]));
    index = (RevisionIndex) in.readObject();

    System.out.println("Number of index entries: " + index.getUnmodifiableEntries().size());
    System.out.println("--------------------------------------------------------");

    List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>(index.getUnmodifiableEntries());
    Collections.sort(entries, new RepositoryEntryComparator(RepositoryEntryComparator.FULL_NAME, false));

    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
    System.out.println("--------------------------------------------------------");
    System.out.println("Done.");
  }

}
