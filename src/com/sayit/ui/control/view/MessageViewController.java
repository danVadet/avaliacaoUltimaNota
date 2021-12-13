package com.sayit.ui.control.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;


public class MessageViewController {

    @FXML
    private Text mainText;
    @FXML
    private Label timeLabel;

    public void setMain(String text) {
        mainText.setText(text);
    }

    public void setTime(String text) {
        timeLabel.setText(text);
    }
}
