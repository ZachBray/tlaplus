package org.lamport.tla.toolbox.ui.view;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.lamport.tla.toolbox.Activator;
import org.osgi.framework.Bundle;

/**
 * The toolbox view that is shown when no spec is loaded.
 * @author Daniel Ricketts
 * @version $Id$
 */
public class ToolboxWelcomeView extends ViewPart
{

    private Composite parentControl;

    public static final String ID = "toolbox.view.ToolboxWelcomeView";

    public ToolboxWelcomeView()
    {
    }

    public void createPartControl(Composite parent)
    {
        parentControl = parent;
        Browser browser = null;
        try
        {
            browser = new Browser(parent, SWT.NONE);
        } catch (SWTError e)
        {
            Activator.logError("Error instantiating browser widget.", e);
        }
        // this code is necessary for opening a local file in the plugin
        Bundle plugin = Activator.getDefault().getBundle();
        IPath relativePagePath = new Path("welcome\\welcomeView.html");
        URL fileInPlugin = FileLocator.find(plugin, relativePagePath, null);
        URL pageUrl;
        try
        {
            pageUrl = FileLocator.toFileURL(fileInPlugin);
            browser.setUrl(pageUrl.toString());
        } catch (IOException e)
        {
            Activator.logError("Error opening toolbox welcome view file.", e);
        }

        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.lamport.tla.toolbox.WelcomeView");

    }

    public void setFocus()
    {
        if (parentControl != null)
        {
            parentControl.setFocus();
        }
    }

}