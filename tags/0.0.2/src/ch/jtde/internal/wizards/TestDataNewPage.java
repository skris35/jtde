package ch.jtde.internal.wizards;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.corext.refactoring.*;
import org.eclipse.jdt.internal.ui.dialogs.*;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.*;
import org.eclipse.jdt.internal.ui.wizards.*;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.*;
import ch.jtde.internal.utils.*;

/**
 * Page for the {@link TestDataWizard}.
 * 
 * @author M. Hautle
 */
public class TestDataNewPage extends WizardNewFileCreationPage {
    /** The text field for the base class. */
    @SuppressWarnings("restriction")
    private StringButtonDialogField baseClass;

    /**
     * Default constrcutor.
     * 
     * @param pageName
     * @param selection The current selection
     */
    public TestDataNewPage(String pageName, IStructuredSelection selection) {
        super(pageName, selection);
        setTitle("TestData File");
        setDescription("Wizard to create a new testdata file.");
        setFileExtension("data");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("restriction")
    protected void createAdvancedControls(Composite parent) {
        final Composite panel = new Composite(parent, SWT.LEFT);
        final GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        panel.setLayout(layout);
        final GridData d = new GridData(SWT.BEGINNING | GridData.FILL_HORIZONTAL);
        d.grabExcessHorizontalSpace = true;
        panel.setLayoutData(d);
        final TypeFieldsAdapter adapter = new TypeFieldsAdapter();
        baseClass = new StringButtonDialogField(adapter);
        baseClass.setDialogFieldListener(adapter);
        baseClass.setLabelText("&Baseclass");
        baseClass.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button);
        createBaseClassControls(panel, 3);
    }

    /**
     * Creates the controls for the {@link #baseClass} field.
     * 
     * @param composite the parent composite
     * @param nColumns number of columns to span
     */
    @SuppressWarnings("restriction")
    protected void createBaseClassControls(Composite composite, int nColumns) {
        baseClass.doFillIntoGrid(composite, nColumns);
        final Text text = baseClass.getTextControl(null);
        LayoutUtil.setWidthHint(text, convertWidthInCharsToPixels(40));
        final JavaTypeCompletionProcessor superClassCompletionProcessor = new JavaTypeCompletionProcessor(false, false, true);
        superClassCompletionProcessor.setCompletionContextRequestor(new TypeAutoCompletor());
        ControlContentAssistHelper.createTextContentAssistant(text, superClassCompletionProcessor);
        TextFieldNavigationHandler.install(text);
    }

    /**
     * Returns the type name entered into the type input field.
     * 
     * @return the type or null
     * @throws JavaModelException If something went wrong
     */
    @SuppressWarnings("restriction")
    public IType getType() throws JavaModelException {
        return getJavaProject().findType(baseClass.getText());
    }

    /**
     * Displays a type selection dialog.
     * 
     * @return The selected type or null
     * @throws JavaModelException If something went wrong
     */
    private IType chooseClass() throws JavaModelException {
        final IJavaProject project = getJavaProject();
        if (project == null)
            return null;
        final IJavaElement[] elements = new IJavaElement[] { project };
        final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
        return EclipseUtils.showConcreteTypeDialog(getShell(), scope, "");
    }

    /**
     * Returns the java project of the currently selected element.
     * 
     * @return The java project
     */
    private IJavaProject getJavaProject() {
        try {
            return EclipseUtils.getJavaProject(getContainerFullPath());
        } catch (CoreException e) {
            EclipseUtils.showError(getShell(), "Error while resolving root project", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus validateLinkedResource() {
        return Status.OK_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validatePage() {
        if (!super.validatePage())
            return false;
        try {
            if (getType() != null) {
                setErrorMessage(null);
                return true;
            }
        } catch (JavaModelException e) {

        }
        setErrorMessage("Invalid base class!");
        return false;
    }

    /**
     * Autocompleter for {@link TestDataNewPage#baseClass}.
     * 
     * @author M. Hautle
     */
    @SuppressWarnings("restriction")
    private class TypeAutoCompletor extends CompletionContextRequestor {
        /** The context. */
        private StubTypeContext ctx;

        /** The project used to build the {@link #ctx}. */
        private IJavaProject proj;

        /**
         * {@inheritDoc}
         */
        @Override
        public StubTypeContext getStubTypeContext() {
            final IJavaProject proj = getJavaProject();
            if (ctx == null || !proj.equals(this.proj))
                ctx = TypeContextChecker.createSuperClassStubTypeContext(JavaTypeCompletionProcessor.DUMMY_CLASS_NAME, null, getDefaultPackage(proj));
            return ctx;
        }

        /**
         * Returns the default package of the passed project
         * 
         * @param proj The java project
         * @return The default package
         */
        private IPackageFragment getDefaultPackage(IJavaProject proj) {
            try {
                return proj.getPackageFragmentRoots()[0].createPackageFragment("", true, null);
            } catch (JavaModelException e) {
                EclipseUtils.showError(getShell(), "Error while preparing class lookup", e);
            }
            return null;
        }
    }

    /**
     * Adapter class for {@link TestDataNewPage#baseClass}.
     * 
     * @author M. Hautle
     */
    @SuppressWarnings("restriction")
    private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void changeControlPressed(DialogField field) {
            try {
                final IType type = chooseClass();
                if (type != null)
                    baseClass.setText(SuperInterfaceSelectionDialog.getNameWithTypeParameters(type));
            } catch (JavaModelException e) {
                EclipseUtils.showError(getShell(), "Error while creating superclass dialog", e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dialogFieldChanged(DialogField field) {
            setPageComplete(validatePage());
        }
    }
}