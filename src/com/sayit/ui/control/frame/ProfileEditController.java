package com.sayit.ui.control.frame;

import com.sayit.control.ChatApplication;
import com.sayit.data.Contact;
import com.sayit.ui.control.ContactManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.nio.file.Path;

public class ProfileEditController {

    private Runnable backCallback;
    private ContactManager concludeCallback;
    private Window ownerWindow;

    private Contact contact;
    @FXML
    private Circle roundImage;
    @FXML
    private TextField nameField;

    public void initialize() {
        contact = new Contact("", new Image("com/sayit/resources/icons/avatar.png"), "127.0.1.1");
        setContact(contact);
    }

    public void requestEditFocus() {
        nameField.requestFocus();
    }


    public void close() {
        if(backCallback != null) backCallback.run();
    }

    public void confirm() {
        if(concludeCallback != null) {
            if(!nameField.getText().isEmpty()) {

                contact.setName(nameField.getText());
                concludeCallback.contactResult(contact);
            } else if(nameField.getText().length() > ChatApplication.MAX_NAME_LENGTH) {
                showNameAlert("Nome muito grande.");
            }else {
                showNameAlert("Nome inválido.");
            }
        }
    }

    private void showNameAlert(String text) {
        Alert nameAlert = new Alert(Alert.AlertType.WARNING);
        nameAlert.setTitle("Erro no nome.");
        nameAlert.setHeaderText(null);
        nameAlert.setContentText(text);
        nameAlert.showAndWait();
    }

    public void getImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Image");
        var imagePath = fileChooser.showOpenDialog(ownerWindow);
        if(imagePath != null) {
            Path path = imagePath.toPath();
            Image image = new Image(path.toUri().toString());
            roundImage.setFill(new ImagePattern(image));
            contact.setPhoto(image);
        }
    }

    public void setBackCallback(Runnable backCallback) {
        this.backCallback = backCallback;
    }

    public void setConcludeCallback(ContactManager concludeCallback) {
        this.concludeCallback = concludeCallback;
    }

    public void setOwnerWindow(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    public void setContact(Contact contact) {
        this.contact.setName(contact.getName());
        this.contact.setPhoto(contact.getPhoto());
        roundImage.setFill(new ImagePattern(contact.getPhoto()));
        nameField.setText(contact.getName());
    }
}
