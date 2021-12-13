package com.sayit.loader;

import java.io.IOException;

import com.sayit.control.ChatApplication;
import com.sayit.ui.control.frame.AddResponseController;
import com.sayit.ui.control.frame.ChatHomeController;
import com.sayit.ui.control.frame.FindContactController;
import com.sayit.ui.control.frame.ProfileEditController;
import com.sayit.ui.control.frame.StartFrameController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Loader {
	
	  public static FXMLLoader getLoader(String path) {
	        return new FXMLLoader(ChatApplication.class.getResource(path));
	    }

	    /**
	     * Load a node from a loader to reduce try/catch verbosity.
	     *
	     * @param loader Loader Object
	     * @return inflated Node
	     */
	    public static Node loadFromLoader(FXMLLoader loader) {
	        try {
	            return loader.load();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
	    public Parent getParent(String NameLoader) {
	    	
	    	 FXMLLoader loader = getLoader(NameLoader);
	    	 Parent parent = (Parent) loadFromLoader(loader);
	    	 
	    	 return parent;   
	    }
	    
	    public Stage createModalParent(String nameModal, double width, double height, String title) {
	        
	    	Stage stage = new Stage();
	    	
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.setScene(new Scene((this.getParent(nameModal)), width, height));
	        stage.setTitle(title);
	        
	        return stage;
	    }


	    public static String getStyleSheet(String path) {
	        return ChatApplication.class.getResource(path).toExternalForm();
	    }
	    
	    public void loaderStart (String startFrame, String startStyle, StartFrameController startFrameController) {
	    	 FXMLLoader loader = getLoader(startFrame);
	         this.getParent(startFrame).getStylesheets().add(getStyleSheet(startStyle));
	         startFrameController = loader.getController();
	    }
	    
	    public void loaderHome (String homeLayout, String startStyle, ChatHomeController chatHome, Stage  windowStage) {
	    	FXMLLoader loader = getLoader(homeLayout);
	        this.getParent(homeLayout);
	        chatHome = loader.getController();
	        chatHome.setParentWindow(windowStage);
	        var res = getClass().getResource(startStyle);
	        this.getParent(homeLayout).getStylesheets().add(res.toExternalForm());
	    }
	    
	    public void loaderAddContact (String addContactLayout, String addContactStyle, AddResponseController addContactController) {
	    	FXMLLoader loader = getLoader(addContactLayout);

	    	 this.getParent(addContactLayout).getStylesheets().add(getStyleSheet(addContactStyle)); 
	    	 addContactController = loader.getController();     
	    }
	   
	    public void loaderFindContact (String findContactLayout, String findContactStyle, FindContactController findContactController) {
	        FXMLLoader loader = getLoader(findContactLayout);
	        this.getParent(findContactLayout).getStylesheets().add(getStyleSheet(findContactStyle));
	        findContactController = loader.getController();

	    }
	    
	    public void editProfile (String editProfileLayout, String editContactStyle,  ProfileEditController editController) {

	        FXMLLoader loader = getLoader(editProfileLayout);
	        Parent editLayout = (Parent)loadFromLoader(loader);
	        editLayout.getStylesheets().add(getStyleSheet(editContactStyle));
	        editController = loader.getController();
	    	
	    }
}
