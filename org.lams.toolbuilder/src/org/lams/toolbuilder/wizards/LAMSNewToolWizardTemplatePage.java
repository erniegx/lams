package org.lams.toolbuilder.wizards;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;



import org.lams.toolbuilder.LAMSToolBuilderPlugin;
import org.lams.toolbuilder.util.LamsToolBuilderLog;
import org.lams.toolbuilder.util.Constants;



/**
 * The second page of the wizard contains a checkbox list of key/value
 * pairs that can be extracted from the source file. Rather than
 * initializing its contents when first created, this page updates its
 * contents whenever it becomes visible by overriding the setVisible()
 * method.
 */
public class LAMSNewToolWizardTemplatePage extends WizardPage
{
   
	private ArrayList<String> availableTemplates;	// a list of available lams tool templates from the workspace
	private String chosenTemplate;					// the template chosen
	private boolean templatesAvailable;				// false if there are no templates available
	private List dispList;
	ISelection selection;
	
	/**
	 * Constructor
	 */
	public LAMSNewToolWizardTemplatePage(ISelection selection) 
	{
		super("selectTemplate");
		this.selection = selection;
		setTitle("Select a Template");
    	setDescription("Select a LAMS tool template to build your tool from");
    	templatesAvailable = false; 	// set to false to begin with
    	setAvailableTemplates();
    	
	}
   
	/**
	 * Checks the workspace to find what tool projects are present
	 * Sets the availableTemplates list
	 */
	public void setAvailableTemplates() 
	{
		availableTemplates = new ArrayList<String>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		LamsToolBuilderLog.logInfo("Getting available templates from workspace");
		
		Constants.initToolProjects();
		String[] defaultTools = Constants.defaultTools;
		for (String dir : defaultTools) 
		{
			IPath path = new Path(dir);
			if (root.exists(path))
			{
				LamsToolBuilderLog.logInfo("Tool project found: " + path.toPortableString());
				availableTemplates.add(dir);
			}
			
		}
		
		if (availableTemplates.size() > 0)
		{
			templatesAvailable = true;
		}
		else
		{
			LamsToolBuilderLog.logInfo("No templates found in workspace");
			//this.setVisible(false);
			//TODO: Dialog box goes here "Do you want to use default template?"
			// choose a default template to use. Hopefully an empty example tool
		}
	
	}

   /**
    * Creates the top level control for this wizard page under the
    * given parent composite, then calls <code>setControl</code> so
    * that the created control can be accessed via
    * <code>getControl</code>
    * 
    * @param parent the parent composite
    */
   public void createControl(Composite parent) 
   {
	    Composite container = new Composite(parent, SWT.NULL);
	    container.setLayout(new FormLayout());
	    setControl(container);
	   
	    LamsToolBuilderLog.logInfo("Drawing LAMS Tool Template Wizard");
	    Composite control = (Composite)getControl();
	    
	    GridLayout layout = new GridLayout();
	    layout.verticalSpacing = 10;
   		control.setLayout(layout);
	   
   		createLabel(control, "Please choose a LAMS tool template from the list below to build a new tool." +
				"\n\nThis list is derived from your current workspace. To add more tools to your workspace," +
				"\nsimply use the 'import' option from the file menu, then enter the following details in" +
				"\nthe 'Import Projects From CVS' menu:" +
				"\n\n\t* access method: pserver " +
				"\n\t* user name: anonymous" +
				"\n\t* server name: lamscvs.melcoe.mq.edu.au" +
				"\n\t* location: /usr/local/cvsroot\n\n");
   		
   		//create a group
		Group templatesGroup = new Group(control, SWT.NONE);
		templatesGroup.setText("Available LAMS Tool Templates");
		GridLayout templatesGroupLayout = new GridLayout();
		templatesGroupLayout.numColumns = 1;
		templatesGroup.setLayout(templatesGroupLayout);
		templatesGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				
		Group templatesGroup2 = new Group(templatesGroup, SWT.NONE);

		FillLayout fillLayout = new FillLayout();
 		fillLayout.type = SWT.VERTICAL;
		templatesGroup2.setLayout(fillLayout);
		templatesGroup2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		dispList = new List(templatesGroup2, SWT.SINGLE);
		
		// add all available templates to the list
		for(String dir:availableTemplates)
		{
			dispList.add(dir);
		}
		
		// select the first entry on the displist
		if (dispList.getItemCount()>=1)
			dispList.select(0);
		
		// add an event listenter that is run when the user selects a list item
		dispList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event)
			{
				String[] selected = dispList.getSelection();
				if (selected.length==1)
				{
					chosenTemplate = selected[0];
				}
			}
			
		});
      
   }
   
   private Label createLabel(Composite container, String text) {
		Label label = new Label(container, SWT.NONE);
		label.setText(text);
		GridData gd = new GridData();
		gd.horizontalIndent = 30;
		label.setLayoutData(gd);
		return label;
	}

   public String getTemplate () {return chosenTemplate;}
   
}



