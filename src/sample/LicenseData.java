package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

import java.util.Date;

public class LicenseData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleLongProperty dc;
    private final SimpleStringProperty ip;
    private final SimpleStringProperty status;
    private final SimpleStringProperty date;
    private final Button action;
    private final Button deleteAction;
    private final Button blockButton;

    public LicenseData(int id, String name, long dc, String ip, String status, String date, Button action, Button blockButton, Button deleteAction) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.dc = new SimpleLongProperty(dc);
        this.ip = new SimpleStringProperty(ip);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
        this.action = action;
        this.blockButton = blockButton;
        this.deleteAction = deleteAction;
    }

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public long getDc() {
        return dc.get();
    }

    public String getIp() {
        return ip.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getDate() {
        return date.get();
    }

    public Button getAction() {
        return action;
    }

    public Button getBlockButton() {
        return blockButton;
    }

    public Button getDeleteAction() {
        return deleteAction;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}





