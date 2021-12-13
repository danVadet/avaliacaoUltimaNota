package com.sayit.ui.control.view;

import com.sayit.control.ChatApplication;
import com.sayit.data.Message;
import com.sayit.loader.Loader;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MessageCell extends ListCell<Message> {

    private VBox container;
    private Parent rootNode;
    private MessageViewController messageController;

    public MessageCell() {
        container = new VBox();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ChatApplication.MESSAGE_VIEW));

        try {
            rootNode = fxmlLoader.load();
            rootNode.getStylesheets().add(Loader.getStyleSheet(ChatApplication.MESSAGE_STYLE));
            messageController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setStyle("-fx-background-color: transparent");
        container.setPadding(new Insets(0, 60, 0, 60));
        container.getChildren().add(rootNode);
    }


    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) setGraphic(null);
        else {
            setGraphic(container);
            messageController.setMain(item.getTextContent());
            messageController.setTime(item.getFormattedTime());
            if(item.isSendByMe()) {
                rootNode.setId("user-message");
                container.setAlignment(Pos.CENTER_RIGHT);
            } else {
                rootNode.setId("");
                container.setAlignment(Pos.CENTER_LEFT);
            }
        }
    }
}
