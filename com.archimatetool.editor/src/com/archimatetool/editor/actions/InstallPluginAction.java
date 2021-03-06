/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.PluginInstaller;
import com.archimatetool.editor.utils.PlatformUtils;


/**
 * Install Plugin Action
 * 
 * @author Phillip Beauvoir
 */
public class InstallPluginAction extends Action {
    
    public InstallPluginAction() {
        super(Messages.InstallPluginAction_0 + "..."); //$NON-NLS-1$
    }

    @Override
    public void run() {
        // Check that we can write to plugins folder
        if(!PluginInstaller.canWrite()) {
            String message = Messages.InstallPluginAction_4;
            
            if(PlatformUtils.isWindows()) {
                message += " " + Messages.InstallPluginAction_5; //$NON-NLS-1$
            }
            if(PlatformUtils.isMac()) {
                message += " " + Messages.InstallPluginAction_6; //$NON-NLS-1$
            }
            
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                    Messages.InstallPluginAction_0,
                    message);
            return;
        }
        
        File file = askOpenFile();
        if(file == null) {
            return;
        }
        
        try {
            // Not an Archi plugin
            if(!PluginInstaller.isPluginZipFile(file)) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(),
                        Messages.InstallPluginAction_0, Messages.InstallPluginAction_1);
                return;
            }

            // Unpack zip to plugins folder
            PluginInstaller.unpackZipPackageToPluginsFolder(file);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.InstallPluginAction_2, ex.getMessage());
        }

        // Restart now?
        boolean result = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                Messages.InstallPluginAction_0,
                Messages.InstallPluginAction_3);
        if(result) {
            PlatformUI.getWorkbench().restart();
        }
    }

    private File askOpenFile() {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*.zip", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
        String path = dialog.open();
        return path != null ? new File(path) : null;
    }
}
