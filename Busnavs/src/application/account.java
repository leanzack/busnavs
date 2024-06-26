package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class account extends Application {
	
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/acc.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
        
        Connection connection;
        String url = "jdbc:mariadb://localhost:3306/busnavs";
        String user = "root";
        String pwd = "root";
        
        try {
            // Optionally load the driver manually if automatic loading fails
            Class.forName("org.mariadb.jdbc.Driver"); 
            connection = DriverManager.getConnection(url, user, pwd);
            System.out.println("Done");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // In case the MariaDB driver class is not found
        }
 
    }
}
