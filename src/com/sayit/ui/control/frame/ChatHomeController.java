package com.sayit.ui.control.frame;

import com.sayit.control.ChatApplication;
import com.sayit.control.Presentable;
import com.sayit.data.Contact;
import com.sayit.data.Message;
import com.sayit.data.MessageHistory;
import com.sayit.loader.Loader;
import com.sayit.ui.control.view.HistoryCell;
import com.sayit.ui.control.view.MessageCell;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.List;

public class ChatHomeController {

    @FXML
    private HBox inputContainer;
    @FXML
    private HBox tabButtonsContainer;

    @FXML
    private TextArea messageField;
    @FXML
    private Pane findPane;
    @FXML
    private ListView<Message> messageListView;
    @FXML
    private ListView<MessageHistory> historyListView;

    @FXML
    private Label userNameLabel;
    @FXML
    private Label contactNameLabel;
    @FXML
    private Label contactStatusLabel;

    @FXML
    private Circle userImage;
    @FXML
    private Circle contactImage;

    private Window parentWindow;
    private Pane findRoot;

    private Presentable presentable;
    private FindContactController findContactController;
    private ObservableList<Message> messageObservableList;
    private ObservableList<MessageHistory> historyObservableList;

    //Open contact animation
    private TranslateTransition translateTransition;

    //Resize text property
    private Text messageText = new Text();

    public void initialize() {

        //Start list views
        messageObservableList = FXCollections.observableArrayList();
        historyObservableList = FXCollections.observableArrayList();

        messageListView.setItems(messageObservableList);
        historyListView.setItems(historyObservableList);

        //Config cell factories
        messageListView.setCellFactory(e -> new MessageCell());

        historyListView.setCellFactory(e -> {
            HistoryCell historyCell = new HistoryCell();
            historyCell.setOnMouseClicked(ev -> {
                var historyInfo = historyCell.getItem();
                setReceiverProfile(historyInfo.getContact());

            });

            return historyCell;
        });

        //Config message box line break
        messageField.setOnKeyReleased(e -> resizeTextArea());

        messageField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ENTER) {
                if(e.isShiftDown()) {
                    messageField.insertText(messageField.getCaretPosition(), "\n");
                } else {
                    sendMessage();
                }
                e.consume();

            }
        });

        //load content

        loadContactView();
        setStartupPage();
    }


    private void setStartupPage() {
        tabButtonsContainer.setVisible(false);
        contactImage.setVisible(false);
        inputContainer.setVisible(false);
        contactNameLabel.setText("");
        contactStatusLabel.setText("");

    }

    private void showChatComponents() {
        contactImage.setVisible(true);
        inputContainer.setVisible(true);
        //tabButtonsContainer.setVisible(false);
    }


    private void loadContactView() {
        //config contact list
        FXMLLoader loader = Loader.getLoader(ChatApplication.FIND_CONTACT_LAYOUT);

        findRoot = (Pane) Loader.loadFromLoader(loader);
        findRoot.getStylesheets().add(Loader.getStyleSheet(ChatApplication.FIND_CONTACT_STYLE));
        findContactController = loader.getController();

        findContactController.setContactResult(contact -> {
            setReceiverProfile(contact);
            closeFindContact();
        });
        findContactController.setCloseCallback(this::closeFindContact);
        findPane.getChildren().add(findRoot);


        findPane.heightProperty().addListener(e -> findRoot.setPrefHeight(findPane.getHeight()));

        configSlideAnimation();
    }


    private void configSlideAnimation() {
        Duration transitionDuration = Duration.millis(300);
        translateTransition = new TranslateTransition(transitionDuration, findRoot);


        translateTransition.setInterpolator(Interpolator.EASE_OUT);
    }

    public void setPresentable(Presentable presentable) {

        this.presentable = presentable;
    }

    public void showFindContact() {
        findContactController.setContactList(presentable.getContactList());

        findPane.setManaged(true);
        findPane.setVisible(true);
        findRoot.setPrefWidth(findPane.getWidth());

        translateTransition.setFromX(findPane.getWidth());
        translateTransition.setToX(0);
        translateTransition.setOnFinished(null);
        translateTransition.playFromStart();
    }

    private void closeFindContact() {

        translateTransition.setFromX(0);
        translateTransition.setToX(findPane.getWidth());
        translateTransition.setInterpolator(Interpolator.EASE_IN);
        translateTransition.setOnFinished(e -> {
            findPane.setManaged(false);
            findPane.setVisible(false);
        });
        translateTransition.playFromStart();


    }


    private void resizeTextArea() {
        //fixme upgrade height calculation
        messageText.setText(messageField.getText());
        messageText.setWrappingWidth(messageField.getWidth());
        messageText.setFont(messageField.getFont());

        if(messageText.getLayoutBounds().getHeight() >= (messageText.getFont().getSize() + messageText.getLineSpacing()) * 5) {
            messageText.setText("\n\n\n\n");
        }

        messageField.setPrefHeight(messageText.getLayoutBounds().getHeight() + 10);

    }

    public void showAddContact() {
        presentable.openAddScene();
    }

    public void showEditProfile() {
        presentable.openEditProfileScene();
    }


    public void sendMessage() {
        if(!messageField.getText().isEmpty()) {
            presentable.sendMessage(messageField.getText());
            messageField.setText("");
        }
    }

    private void getArchive() {

    }

    public void sendArchive() {
        //fixme sendArchive
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(parentWindow);
    }


    public void setParentWindow(Window parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void setUserProfile(Contact userProfile) {
        userImage.setFill(new ImagePattern(userProfile.getPhoto()));
        userNameLabel.setText(userProfile.getName());
    }

    private void setReceiverProfile(Contact receiverProfile) {

        final var profileId = receiverProfile.getId();

        receiverProfile = presentable.getContactInfo(profileId);
        contactImage.setFill(new ImagePattern(receiverProfile.getPhoto()));
        contactNameLabel.setText(receiverProfile.getName());
        showChatComponents();
        //fixme add a status to contact

        Platform.runLater(() -> {

            for (int i = 0; i < historyObservableList.size(); i++) {
                if(profileId == historyObservableList.get(i).getContact().getId()) {
                    historyListView.getSelectionModel().select(i);
                    historyListView.scrollTo(i);
                    break;
                }
            }
        });

        setMessageList(presentable.requestMessageList(profileId));
        messageListView.scrollTo(messageObservableList.size() - 1);
    }

    public void setHistoryList(List<MessageHistory> messageHistories) {
        historyObservableList.clear();
        if(messageHistories.size() > 0) historyObservableList.addAll(messageHistories);
    }

    public void setMessageList(List<Message> messageList) {
        messageObservableList.clear();
        if(messageList.size() > 0) messageObservableList.addAll(messageList);
    }
}
