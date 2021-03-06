package org.lamport.tla.toolbox.ui.contribution;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.lamport.tla.toolbox.Activator;
import org.lamport.tla.toolbox.job.DeleteOutOfSyncJob;
import org.lamport.tla.toolbox.spec.Spec;
import org.lamport.tla.toolbox.ui.handler.AddModuleHandler;
import org.lamport.tla.toolbox.ui.handler.OpenModuleHandler;
import org.lamport.tla.toolbox.util.ResourceHelper;
import org.lamport.tla.toolbox.util.UIHelper;

/**
 * Contribution item for opening the modules
 * @author Simon Zambrovski
 * @version $Id$
 */
public class ModuleListContributionItem extends CompoundContributionItem
{
    private ImageDescriptor rootIcon = UIHelper.imageDescriptor("icons/full/obj16/ftr_mf_obj.gif");
    private ImageDescriptor icon = UIHelper.imageDescriptor("icons/full/obj16/file_obj.gif");
    private ImageDescriptor iconAddModule = UIHelper.imageDescriptor("icons/full/newmodule_wiz.gif");    

    
    /**
     * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
     */
    protected IContributionItem[] getContributionItems()
    {

        Spec spec = Activator.getSpecManager().getSpecLoaded();
        Vector moduleContributions = new Vector();
        HashMap parameters = new HashMap();
        
        Vector outOfSyncResourcesToDelete = new Vector();

        // create the contribution item for add module
        CommandContributionItemParameter param = new CommandContributionItemParameter(UIHelper.getActiveWindow(),
                "toolbox.command.module.add", AddModuleHandler.COMMAND_ID, parameters, iconAddModule, null, null,
                "Add TLA+ Module...", null, "Adds new TLA+ Module to the specification",
                CommandContributionItem.STYLE_PUSH, null, true);

        // add contribution item to the list
        moduleContributions.add(new CommandContributionItem(param));
        moduleContributions.add(new Separator());

        if (spec != null)
        {
            IResource[] modules = spec.getModuleResources();
            IResource rootModule = spec.getRootFile();
            boolean isRoot;
            for (int i = 0; i < modules.length; i++)
            {
                // skip non-modules and non-existing files
                if (!ResourceHelper.isModule(modules[i]))
                {
                    continue;
                } 
                if (!modules[i].isSynchronized(IResource.DEPTH_ZERO)) 
                {
                    outOfSyncResourcesToDelete.add(modules[i]); 
                    continue;
                }
                

                isRoot = rootModule.equals(modules[i]);

                parameters = new HashMap();
                // fill the module name for the handler
                parameters.put(OpenModuleHandler.PARAM_MODULE, ResourceHelper.getModuleNameChecked(
                        modules[i].getName(), false));

                // create the contribution item
                param = new CommandContributionItemParameter(UIHelper.getActiveWindow(), "toolbox.command.module.open."
                        + modules[i].getName(), OpenModuleHandler.COMMAND_ID, parameters, ((isRoot) ? rootIcon : icon),
                        null, null, modules[i].getName(), null, "Opens " + modules[i].getName(),
                        CommandContributionItem.STYLE_PUSH, null, true);

                // add contribution item to the list
                moduleContributions.add(new CommandContributionItem(param));
            }
        }

        DeleteOutOfSyncJob job = new DeleteOutOfSyncJob(outOfSyncResourcesToDelete);
        job.setRule(ResourceHelper.getDeleteRule((IResource[]) outOfSyncResourcesToDelete.toArray(new IResource[outOfSyncResourcesToDelete.size()])));
        job.schedule();
        
        return (IContributionItem[]) moduleContributions.toArray(new IContributionItem[moduleContributions.size()]);
    }
}
