package com.sayit.ui.control.frame;

import com.sayit.data.Contact;
import com.sayit.ui.control.ContactManager;
import com.sayit.ui.control.SearchCallback;
import com.sayit.ui.control.view.ContactCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class FindContactController {

    private Runnable closeCallback;
    private ContactManager contactManager;
    private SearchCallback searchCallback;

    @FXML
    private TextField nameField;
    @FXML
    private ListView<Contact> contactListView;
    private ObservableList<Contact> contactObservableList;

    public void initialize() {
        contactObservableList = FXCollections.observableArrayList();
        contactListView.setItems(contactObservableList);
        contactListView.setCellFactory(e -> {
            ContactCell contactCell = new ContactCell();
            contactCell.setOnMouseClicked(ev -> {
                if(contactManager != null) contactManager.contactResult(contactCell.getItem());
            });


            return contactCell;
        });

    }


    public void close() {
        if(closeCallback != null) closeCallback.run();
    }

    public void search() {
        if(searchCallback != null) searchCallback.seachResult(nameField.getText());

        if(contactObservableList.size() > 0) {
            contactObservableList.sort((c1, c2) -> {
                //fixme resolve match size
                if(c1.getName().contains(nameField.getText())) return -1;
                else if(c2.getName().contains(nameField.getText())) return 1;
                return 0;
            });
        }

    }

    public void setContactList(List<Contact> contactObservableList) {
        this.contactObservableList.clear();
        this.contactObservableList.addAll(contactObservableList);
    }

    public void setCloseCallback(Runnable closeCallback) {
        this.closeCallback = closeCallback;
    }

    public void setContactResult(ContactManager contactManager) {
        this.contactManager = contactManager;
    }

    public void addContact(Contact contact) {
        contactObservableList.add(contact);
    }

    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    public void requestSearchFocus() {
        nameField.requestFocus();
    }
}
