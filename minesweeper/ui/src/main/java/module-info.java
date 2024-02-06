module minesweeperui {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    requires minesweepercore;
    requires java.net.http;
    requires javafx.base;
    
    // Jackson 
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    
    opens ui to javafx.graphics, javafx.fxml, com.fasterxml.jackson.databind;
}
