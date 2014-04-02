/*
 * �Copyright 2013 Jose F. Maldonado�
 *
 *  This file is part of aFileDialog.
 *
 *  aFileDialog is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published 
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aFileDialog is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with aFileDialog. If not, see <http://www.gnu.org/licenses/>.
 */

package ar.com.daidalos.afiledialog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.LinearLayout;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;


/**
 * A file chooser implemented in a Dialog. 
 */
public class FileChooserDialog extends Dialog implements FileChooser {

	// ----- Attributes ----- //
	
	/**
	 * The core of this file chooser.
	 */
	private FileChooserCore core;
	
	/**
	 * The listeners for the event of select a file.
	 */
	private List<OnFileSelectedListener> listeners;

	private boolean useBackButton=false;

	private File startFolder;
	
	// ----- Constructors ----- //
	
	/**
	 * Creates a file chooser dialog which, by default, lists all the files in the SD card.
	 * 
	 * @param context The current context.
	 */
	public FileChooserDialog(Context context) {
		this(context, null);
	}

	/**
	 * Creates a file chooser dialog which lists all the file of a particular folder.
	 * 
	 * @param context The current context.
	 * @param folderPath The folder which files are going to be listed.
	 */
	public FileChooserDialog(Context context, String folderPath) {
		// Call superclass constructor.
		super(context);
        
		
		LayoutInflater inflater=LayoutInflater.from(context, R.style.Holo_Theme_Dialog_Light);
		
		// Set layout.
		this.setContentView(inflater.inflate(R.layout.daidalos_file_chooser));
		
		
		// Maximize the dialog.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        this.getWindow().setAttributes(lp);
        
        // By default, load the SD card files.
        this.core = new FileChooserCore(this);
        this.core.loadFolder(folderPath);
        this.startFolder = this.core.getCurrentFolder();

        // Initialize attributes.
        this.listeners = new LinkedList<OnFileSelectedListener>();
		
		// Set the background color.
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.rootLayout);
        layout.setBackgroundColor(context.getResources().getColor(R.color.daidalos_backgroud));
                
        // Add a listener for when a file is selected.
        core.addListener(new FileChooserCore.OnFileSelectedListener() {
			public void onFileSelected(File folder, String name) {
				// Call to the listeners.
				for(int i=0; i<FileChooserDialog.this.listeners.size(); i++) {
					FileChooserDialog.this.listeners.get(i).onFileSelected(FileChooserDialog.this, folder, name);
				}
			}
			public void onFileSelected(File file) {
				// Call to the listeners.
				for(int i=0; i<FileChooserDialog.this.listeners.size(); i++) {
					FileChooserDialog.this.listeners.get(i).onFileSelected(FileChooserDialog.this, file);
				}
			}
		});
	}
	
    // ----- Events methods ----- //

	/**
	 * Add a listener for the event of a file selected.
	 * 
	 * @param listener The listener to add.
	 */
	public void addListener(OnFileSelectedListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes a listener for the event of a file selected.
	 * 
	 * @param listener The listener to remove.
	 */
	public void removeListener(OnFileSelectedListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Removes all the listeners for the event of a file selected.
	 */
	public void removeAllListeners() {
		this.listeners.clear();
	}
	
	/**
	 * Interface definition for a callback to be invoked when a file is selected. 
	 */
	public interface OnFileSelectedListener {
		/**
		 * Called when a file has been selected.
		 * 
		 * @param file The file selected.
		 */
		void onFileSelected(Dialog source, File file);
		
		/**
		 * Called when an user wants to be create a file.
		 * 
		 * @param folder The file's parent folder.
		 * @param name The file's name.
		 */
		void onFileSelected(Dialog source, File folder, String name);
	}
	
	// ----- Miscellaneous methods ----- //
	
	
	/**
	 * Set a regular expression to filter the files that can be selected.
	 * 
	 * @param filter A regular expression.
	 */
	public void setFilter(String filter) {
		this.core.setFilter(filter);
	}
	
	/**
	 * Defines if only the files that can be selected (they pass the filter) must be show.
	 * 
	 * @param show 'true' if only the files that can be selected must be show or 'false' if all the files must be show.
	 */
	public void setShowOnlySelectable(boolean show) {
		this.core.setShowOnlySelectable(show);
	}
	
	/**
	 * Loads all the files of the SD card root.
	 */
	public void loadFolder() {
		this.core.loadFolder();
		this.startFolder=core.getCurrentFolder();
	}
	
	/**
	 * Loads all the files of a folder in the file chooser.
	 * 
	 * If no path is specified ('folderPath' is null) the root folder of the SD card is going to be used.
	 * 
	 * @param folderPath The folder's path.
	 */
	public void loadFolder(String folderPath) {
		this.core.loadFolder(folderPath);
		this.startFolder=core.getCurrentFolder();
	}
	
	/**
	 * Defines if the chooser is going to be used to select folders, instead of files.
	 * 
	 * @param folderMode 'true' for select folders or 'false' for select files.
	 */
	public void setFolderMode(boolean folderMode) {
		this.core.setFolderMode(folderMode);
	}
	
	/**
	 * Defines if the user can create files, instead of only select files.
	 * 
	 * @param canCreate 'true' if the user can create files or 'false' if it can only select them.
	 */
	public void setCanCreateFiles(boolean canCreate) {
		this.core.setCanCreateFiles(canCreate);
	}
	
	/**
	 * Defines the value of the labels.
	 * 
	 * @param label The labels.
	 */
	public void setLabels(FileChooserLabels labels) {
		this.core.setLabels(labels);
	}
	
	/**
	 * Allows to define if a confirmation dialog must be show when selecting o creating a file.
	 * 
	 * @param onSelect 'true' for show a confirmation dialog when selecting a file, 'false' if not.
	 * @param onCreate 'true' for show a confirmation dialog when creating a file, 'false' if not.
	 */
	public void setShowConfirmation(boolean onSelect, boolean onCreate) {
		this.core.setShowConfirmationOnCreate(onCreate);
		this.core.setShowConfirmationOnSelect(onSelect);
	}
	
	/**
	 * Allows to define if, in the title, must be show only the current folder's name or the full file's path..
	 * 
	 * @param show 'true' for show the full path, 'false' for show only the name.
	 */
	public void setShowFullPath(boolean show) {
		this.core.setShowFullPathInTitle(show);
	}
	
    // ----- FileChooser methods ----- //
    
	public LinearLayout getRootLayout() {
		View root = this.findViewById(R.id.rootLayout); 
		return (root instanceof LinearLayout)? (LinearLayout)root : null;
	}  

	public void setCurrentFolderName(String name) {
		this.setTitle(name);
	}
	
	public void setNavigationByBackButton(boolean navigationByBackButton)
	{
		this.useBackButton=navigationByBackButton;
	}
	
	@Override
	public void onBackPressed() {
    	// Verify if the dialog must be finished or if the parent folder must be opened.
   	    File current = this.core.getCurrentFolder();
   	    if(!this.useBackButton || current == null || current.getParent() == null || current.getPath().compareTo(this.startFolder.getPath()) == 0) {
   	    	// Close dialog.
   	    	super.onBackPressed();
   	    }else{
   	    	// Open parent.
   	        this.core.loadFolder(current.getParent());
   	    }
	}
}
