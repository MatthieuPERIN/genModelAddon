package com.opcoach.genmodeladdon.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.opcoach.genmodeladdon.core.GenerateDevStructure;
import com.opcoach.genmodeladdon.ui.dialog.ConfirmFileSelectionDialog;
import com.opcoach.genmodeladdon.ui.dialog.DerivedSourceParametersDialog;

public class GeneratedDerivedSourceFolder extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		Shell parentShell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		if (sel instanceof IStructuredSelection)
		{
			Object selected = ((IStructuredSelection) sel).getFirstElement();
			if (selected instanceof GenModel)
			{
				GenModel gm = (GenModel) selected;
				Shell s = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				s.setText("Enter the parameter for the development structure generation");
				DerivedSourceParametersDialog dial = new DerivedSourceParametersDialog(s);
				dial.setGenModel(gm);

				int status = dial.open();
				if (status == Dialog.OK)
				{
					String ip = dial.getDevInterfacePattern();
					String cp = dial.getDevClassPattern();
					String src = dial.getSrcDir();

					GenerateDevStructure gds = new GenerateDevStructure(gm, cp, ip, src);
					gds.generateDevStructure();

					Map<String, Object> filesNotGenerated = gds.filesNotGenerated;
					StringBuffer filesInError = new StringBuffer();

					if (!filesNotGenerated.isEmpty())
					{
						ConfirmFileSelectionDialog cfs = new ConfirmFileSelectionDialog(parentShell, filesNotGenerated,
								src);
						int confirmCode = cfs.open();

						if (confirmCode == ConfirmFileSelectionDialog.OK)
						{
							// Must ask if the not generated files can override
							// existing files...
							for (String fn : cfs.getFilesToBeGenerated())
							{
								// Open the file and generate contents
								try
								{
									FileWriter fw = new FileWriter(fn);
									fw.write(filesNotGenerated.get(fn).toString());
									fw.flush();
									fw.close();
								} catch (IOException e)
								{
									Bundle bundle = FrameworkUtil.getBundle(this.getClass());
									ILog log = Platform.getLog(bundle);
									String mess = "Unable to generate the file : " + fn;
									Status st = new Status(Status.WARNING, bundle.getSymbolicName(), Status.OK, mess, e);
									log.log(st);
									int pos = fn.indexOf(src);
									filesInError.append(fn.substring(pos)).append("\n");
								}

							}
						}
					}

					// Display a sum up dialog
					if (filesInError.length() == 0)
						MessageDialog.openInformation(parentShell, "Files generated",
								"Files have been generated in this directory : \n\n" + gds.getSrcAbsolutePath());
					else
					{
						MessageDialog.openWarning(
								parentShell,
								"Files generated with errors",
								"Some errors occured during the generation.\n\n These files were not generated : \n"
										+ filesInError
										+ "\n\nThe other files have been generated without any error here : \n"
										+ gds.getSrcAbsolutePath() + "\n\n---------------------------------------------------\nCheck the logs in : "
										+ Platform.getLogFileLocation().toPortableString());
					}

				}
			}
		}

		return null;
	}

}