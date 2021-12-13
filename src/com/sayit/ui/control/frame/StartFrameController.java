package com.sayit.ui.control.frame;

import com.sayit.control.ChatApplication;
import com.sayit.loader.Loader;
import com.sayit.ui.control.ContactManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;

public class StartFrameController {

    @FXML
    private VBox rootContainter;

    private StartController startController;
    private ProfileEditController profileEditController;

    private Parent startLayout;
    private Parent profileLayout;

    public void initialize() {
        FXMLLoader startLoader = new FXMLLoader(getClass().getResource(ChatApplication.START_LAYOUT));
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource(ChatApplication.EDIT_PROFILE_LAYOUT));

        try {
            startLayout = startLoader.load();
            startController = startLoader.getController();
            startLayout.getStylesheets().add(Loader.getStyleSheet(ChatApplication.START_STYLE));
            profileLayout = profileLoader.load();
            profileLayout.getStylesheets().add(Loader.getStyleSheet(ChatApplication.EDIT_CONTACT_STYLE));
            profileEditController = profileLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startController.setButtonAction(e -> loadProfile());
        profileEditController.setBackCallback(this::loadStart);


        loadStart();
    }

    public void setConcludeCallback(ContactManager concludeCallback) {
        profileEditController.setConcludeCallback(concludeCallback);
    }

    public void loadStart() {
        if(rootContainter.getChildren().size() > 0) rootContainter.getChildren().clear();
        rootContainter.getChildren().add(startLayout);
        VBox.setVgrow(startLayout, Priority.ALWAYS);
    }

    public void loadProfile() {
        if(rootContainter.getChildren().size() > 0) rootContainter.getChildren().clear();
        rootContainter.getChildren().add(profileLayout);
        VBox.setVgrow(profileLayout, Priority.ALWAYS);
    }

    public void setOwnerWindow(Window window) {
        profileEditController.setOwnerWindow(window);
    }
}
