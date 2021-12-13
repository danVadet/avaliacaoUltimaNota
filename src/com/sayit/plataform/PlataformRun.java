package com.sayit.plataform;

import java.util.LinkedList;

import com.sayit.control.ChatApplication;
import com.sayit.data.Contact;
import com.sayit.data.ContactDao;
import com.sayit.ui.control.frame.ChatHomeController;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class PlataformRun {
	 
	private volatile boolean processingRequests = false;
	private ChatApplication app;
	
	public void runMessageContact (long id, Contact currentContact, ContactDao contactDao, ChatHomeController chatHome) {
		
        Platform.runLater(() -> {
            if(currentContact != null && currentContact.getId() == id) {
                chatHome.setMessageList(contactDao.getMessageList(id));
            }
            chatHome.setHistoryList(contactDao.getHistoryList());
        });

    }
	
	public void runProcessingRequests (LinkedList<Contact> requestList) {
		
        if(!processingRequests) {
            Platform.runLater(() -> {
                processingRequests = true;
                while (processingRequests && requestList.size() > 0) {
                    app.openContactRequest(requestList.pollFirst());
                }
                processingRequests = false;
            });
        }
    }
		
	
  public void runScene(Stage primaryStage, Parent parent) {
	
	  Platform.runLater(() -> {
          primaryStage.setScene(new Scene(parent));
          primaryStage.show();
          setWindowPosition(primaryStage);
      });
}
  


  /**
   * Centraliza a janela.
   */
private void setWindowPosition(Stage primaryStage) {
    var screenBounds = Screen.getPrimary().getVisualBounds();

    primaryStage.setX(screenBounds.getMaxX() * 0.5 - primaryStage.getWidth() * 0.5);
    primaryStage.setY(screenBounds.getMaxY() * 0.5 - primaryStage.getHeight() * 0.5);
}

}
