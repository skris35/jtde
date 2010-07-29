package ch.jtde.internal.wizards;

import java.io.*;
import java.lang.reflect.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.*;
import ch.jtde.*;
import ch.jtde.editors.*;
import ch.jtde.internal.utils.*;
import ch.jtde.internal.xstream.*;
import ch.jtde.model.*;

/**
 * Wizard to create a new testfile.
 * 
 * @author M. Hautle
 */
public class TestDataWizard extends Wizard implements INewWizard {
    /** The page. */
    private TestDataNewPage page;

    /** The current selection. */
    private IStructuredSelection selection;

    /**
     * Default constructor.
     */
    public TestDataWizard() {
        setNeedsProgressMonitor(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        addPage(page = new TestDataNewPage("Test Data", selection));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        try {
            final IPath file = page.getContainerFullPath().append(page.getFileName());
            final IType baseType = page.getType();
            getContainer().run(true, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException {
                    try {
                        doFinish(file, baseType, monitor);
                    } catch (CoreException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            return true;
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            EclipseUtils.showError(getShell(), "Error while creating the file", e.getTargetException());
        } catch (JavaModelException e) {
            EclipseUtils.showError(getShell(), "Error while resolving the entered base type", e);
        }
        return false;
    }

    /**
     * Creates the file.
     * 
     * @param path The file to create
     * @param baseType The base type
     * @param monitor A progressmonitor
     * @throws CoreException If something went wrong
     */
    private void doFinish(IPath path, IType baseType, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Creating " + path.lastSegment(), 2);
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        if (!file.getParent().exists())
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK,
                    ("Container \"" + file.getParent().toString() + "\" does not exist."), null));
        try {
            final IDataElement<IAttribute> el = Activator.getElementManager().create(baseType, new SubProgressMonitor(monitor, 1));
            new XStreamAdapter(EclipseUtils.getJavaProject(file.getProject())).write(el, file.getLocation().toFile());
            file.refreshLocal(IResource.DEPTH_ONE, new SubProgressMonitor(monitor, 1));
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK, "Error while creating the file", e));
        }
        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, IDataEditor.EDITOR_ID);
                } catch (PartInitException e) {
                }
            }
        });
        monitor.worked(1);
    }

    /**
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}