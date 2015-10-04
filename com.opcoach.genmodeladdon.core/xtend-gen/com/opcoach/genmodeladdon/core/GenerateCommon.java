package com.opcoach.genmodeladdon.core;

import java.io.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A class to provide some generation common methods
 */
@SuppressWarnings("all")
public class GenerateCommon {
  /**
   * Extract the project name from the genmodel resource
   */
  public static String getProjectName(final GenModel gm) {
    Resource _eResource = gm.eResource();
    final URI genModelUri = _eResource.getURI();
    final String gmUriStr = genModelUri.toString();
    boolean _startsWith = gmUriStr.startsWith("platform:/resource/");
    if (_startsWith) {
      final String p = gmUriStr.replaceFirst("platform:/resource/", "");
      final int pos = p.indexOf("/");
      return p.substring(0, pos);
    } else {
      boolean _startsWith_1 = gmUriStr.startsWith("file:");
      if (_startsWith_1) {
        IWorkspace _workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = _workspace.getRoot();
        final java.net.URI wsloc = root.getLocationURI();
        String _string = wsloc.toString();
        String _plus = (_string + File.separator);
        final String p_1 = gmUriStr.replaceFirst(_plus, "");
        final int pos_1 = p_1.indexOf("/");
        return p_1.substring(0, pos_1);
      }
    }
    return null;
  }
  
  /**
   * Find the project from a genmodel
   */
  public static IProject getProject(final GenModel gm) {
    IProject _xblockexpression = null;
    {
      final String projectName = GenerateCommon.getProjectName(gm);
      IWorkspace _workspace = ResourcesPlugin.getWorkspace();
      IWorkspaceRoot _root = _workspace.getRoot();
      _xblockexpression = _root.getProject(projectName);
    }
    return _xblockexpression;
  }
}