package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import sample.Database.ConnectionPool;
import sample.Database.Management;
import sample.Webhook.HookManagement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {
    public static Stage stage;

    private final TableView<LicenseData> tableView = new TableView<>();
    private final TextField customerNameTextField = new TextField();
    private final TextField customerDcIDTextFiled = new TextField();
    private final TextField customerIPTextField = new TextField();
    private final Button addIpButton = new Button("Müşteri Kayıt");
    private final Button refreshButton = new Button("Yenile");
    private Label loadingLabel;

    private VBox loadingBox;
    private Task<List<LicenseData>> loadDataTask;
    private ProgressBar progressBar;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        setIcon();
        primaryStage.setTitle("Discord Yönetim Paneli Lisans Yönetim Sistemi");

        loadingLabel = new Label("Veriler çekiliyor...");
        loadingLabel.setFont(Font.font(16));
        loadingBox = new VBox(loadingLabel);
        loadingBox.setAlignment(Pos.BOTTOM_CENTER);
        progressBar = new ProgressBar();
        loadPage();
        setTableView();
    }


    private void setTableView() {
        TableColumn<LicenseData, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<LicenseData, String> nameColumn = new TableColumn<>("Müşteri İsmi");
        TableColumn<LicenseData, Long> dcidColumn = new TableColumn<>("Müşteri Discord ID");
        TableColumn<LicenseData, String> ipColumn = new TableColumn<>("IP Adresi");
        TableColumn<LicenseData, String> statusColumn = new TableColumn<>("Durum");
        TableColumn<LicenseData, LocalDate> dateColumn = new TableColumn<>("Tarih");
        TableColumn<LicenseData, Button> actionColumn = new TableColumn<>("Eylem");
        TableColumn<LicenseData, Button> deleteColumn = new TableColumn<>("Lisansı Kaldır");
        TableColumn<LicenseData, Button> blockColumn = new TableColumn<>("Lisansı Blokla");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dcidColumn.setCellValueFactory(new PropertyValueFactory<>("dc"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        idColumn.setPrefWidth(25);
        statusColumn.setPrefWidth(45);
        dcidColumn.setPrefWidth(150);

        actionColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAction()));
        deleteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDeleteAction()));
        blockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBlockButton()));

        actionColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                col.setPrefWidth(100);
                super.updateItem(button, empty);
                if (!empty) {
                    setGraphic(button);
                    setPrefSize(100, 30);
                    setAlignment(Pos.CENTER);
                } else {
                    setGraphic(null);
                }
            }
        });

        blockColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                col.setPrefWidth(100);
                super.updateItem(button, empty);
                if (!empty) {
                    setGraphic(button);
                    setPrefSize(100, 30);
                    setAlignment(Pos.CENTER);
                } else {
                    setGraphic(null);
                }
            }
        });

        deleteColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);
                if (!empty) {
                    setGraphic(button);
                    setAlignment(Pos.BASELINE_RIGHT);
                } else {
                    setGraphic(null);
                }
            }
        });

        tableView.getColumns().addAll(idColumn, nameColumn, dcidColumn, ipColumn, statusColumn, dateColumn, actionColumn, blockColumn, deleteColumn);

        registerButtonEvents();
        setScene();
    }


    private void registerButtonEvents() {
        addIpButton.setOnAction(event -> {
            String newName = customerNameTextField.getText().trim();
            long newDcId = Long.parseLong(customerDcIDTextFiled.getText().trim());
            String newIp = customerIPTextField.getText().trim();
            if (!newIp.isEmpty()) {
                if (Management.checkIfIpExists(newIp)) {
                    utilities.showInformationAlert("Hata", "IP Adresi zaten var.", "Eklenen IP adresi zaten sistemde mevcut.").showAndWait();
                    return;
                }
                addNewIP(newName, newDcId, newIp);
                customerIPTextField.clear();
                customerNameTextField.clear();
                customerDcIDTextFiled.clear();
            }
        });

        refreshButton.setOnAction(event -> {
            loadPage();
        });
    }

    private void setScene() {
        HBox addIpBox = new HBox(customerNameTextField, customerDcIDTextFiled, customerIPTextField, addIpButton, refreshButton);
        customerNameTextField.setPromptText("Müşteri ismi girin.");
        customerDcIDTextFiled.setPromptText("Müşteri Discord ID girin.");
        customerIPTextField.setPromptText("IP adresini girin.");
        addIpBox.setSpacing(10);
        tableView.setPrefHeight(450);
        VBox vbox = new VBox(10, addIpBox, tableView);
        vbox.getChildren().add(loadingBox);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void addNewIP(String name, long dcID, String ip) {
        Management.addNewIP(ip);
        Management.addNewCustomer(ip, name, dcID);
        HookManagement.sendMsg("Yeni makine eklendi. IP: "+ip);
        loadPage();
    }


    private void loadPage() {
        loadDataTask = new Task<>() {
            @Override
            protected List<LicenseData> call() {
                List<LicenseData> licenseDataList = new ArrayList<>();
                String bigdata = Management.getAllLicenseData();
                String[] bigdataList = bigdata.split("\n");
                for (String bigData : bigdataList) {
                    int id = utilities.getLicenseID(bigData);
                    String ip = utilities.getLicenseIP(bigData);
                    int customerID = utilities.getCustomerID(bigData);
                    String name = utilities.getCustomerName(bigData);
                    long DcID = utilities.getCustomerDiscordID(bigData);
                    int active = utilities.getLicenseActive(bigData);
                    int block = utilities.getLicenseBlock(bigData);
                    String date = utilities.getLicenseDate(bigData);

                    Button actionButton = new Button(active == 1 ? "Deaktif Et" : "Aktif Et");
                    actionButton.setStyle("-fx-background-color: " + (active == 1 ? "#FF6B6B;" : "#65D36E;"));
                    actionButton.setTextFill(Color.WHITE);

                    Button blockButton = new Button(block == 1 ? "Blok Kaldır" : "Blokla");
                    blockButton.setStyle("-fx-background-color: " + (block == 1 ? "#919090;" : "#424242;"));
                    blockButton.setTextFill(Color.WHITE);

                    Button deleteButton = new Button("Sil");
                    deleteButton.setStyle("-fx-background-color: #949292;");
                    deleteButton.setTextFill(Color.WHITE);

                    LicenseData licenseData = new LicenseData(id, name, DcID, ip, (active == 1 ? "Aktif" : "Deaktif"), date, actionButton, blockButton, deleteButton);
                    licenseDataList.add(licenseData);


                    actionButton.setOnAction(event -> {
                        if ("Aktif Et".equals(actionButton.getText())) {
                            Management.updateActiveByID(id, 1);
                            actionButton.setText("Deaktif Et");
                            actionButton.setStyle("-fx-background-color: #FF6B6B;");
                            licenseData.setStatus("Aktif");
                        } else {
                            Management.updateActiveByID(id, 0);
                            actionButton.setText("Aktif Et");
                            actionButton.setStyle("-fx-background-color: #65D36E;");
                            licenseData.setStatus("Deaktif");
                        }
                        tableView.refresh();
                    });

                    blockButton.setOnAction(event -> {
                        if ("Blokla".equals(blockButton.getText())) {
                            Management.updateBlockByID(id, 1);
                            blockButton.setText("Blok Kaldır");
                            blockButton.setStyle("-fx-background-color: #919090;");
                        } else {
                            Management.updateBlockByID(id, 0);
                            blockButton.setText("Blokla");
                            blockButton.setStyle("-fx-background-color: #424242;");
                        }
                        tableView.refresh();
                    });

                    deleteButton.setOnAction(event -> {
                        Management.deleteID(id);
                        Management.deleteCustomer(customerID);
                        licenseDataList.remove(licenseData);
                        tableView.getItems().remove(licenseData);

                        HookManagement.sendMsg("Makine silindi IP: "+ip);
                    });

                    updateProgress(id, bigdataList.length);
                }
                return licenseDataList;
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<LicenseData> licenseDataList = loadDataTask.getValue();
            tableView.getItems().clear();
            tableView.getItems().addAll(licenseDataList);

            Platform.runLater(() -> {
                loadingLabel.textProperty().unbind();
                loadingLabel.setText("Veriler yüklendi.");
            });
        });

        loadingLabel.textProperty().bind(loadDataTask.messageProperty());
        progressBar.setPrefWidth(400);
        progressBar.progressProperty().bind(loadDataTask.progressProperty());
        if (!loadingBox.getChildren().contains(progressBar))
            loadingBox.getChildren().add(progressBar);

        new Thread(loadDataTask).start();
    }

    @Override
    public void stop() {
        try {
            if (ConnectionPool.getConnection() != null) {
                ConnectionPool.getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setIcon() {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/icon.png")));
        stage.getIcons().add(icon);
    }
}

