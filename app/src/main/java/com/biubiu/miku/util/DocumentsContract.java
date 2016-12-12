package com.biubiu.miku.util;

import android.net.Uri;

import java.util.List;

public class DocumentsContract {
  private static final String PATH_DOCUMENT = "document";

  public static String getDocumentId(Uri documentUri) {
    final List<String> paths = documentUri.getPathSegments();
    if (paths.size() < 2) {
      throw new IllegalArgumentException("Not a document: " + documentUri);
    }
    if (!PATH_DOCUMENT.equals(paths.get(0))) {
      throw new IllegalArgumentException("Not a document: " + documentUri);
    }
    return paths.get(1);
  }

  public static boolean isDocumentUri(Uri uri) {
    final List<String> paths = uri.getPathSegments();
    if (paths.size() < 2) {
      return false;
    }
    if (!PATH_DOCUMENT.equals(paths.get(0))) {
      return false;
    }
    return uri.getAuthority().contains("com.android.providers.media.documents");
  }
}
