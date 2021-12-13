package com.sayit.ui.control.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class ContactViewController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label descLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Circle roundedImage;

    public String getName() {
        return nameLabel.getText();
    }

    public void setName(String name) {
        nameLabel.setText(name);
    }

    public String getDescription() {
        return descLabel.getText();
    }

    public void setDescription(String description) {
        descLabel.setText(description);
    }

    public String getTime() {
        return timeLabel.getText();
    }

    public void setTime(String time) {
        timeLabel.setText(time);
    }

    public void setRoundedImage(Image image) {
        roundedImage.setFill(new ImagePattern(image));
    }
}
