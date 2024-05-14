package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class inside_controller {
	
	  public DBConnectionManager dbManager = new DBConnectionManager();
	  private List<String> selectedRouteNames = new ArrayList<String>();
	  private List<Button> selectedRouteButtons = new ArrayList<Button>();


    
    @FXML
    private Label driverNameLabel;
    @FXML 
    private HBox hbox_route;
    
    @FXML
    private Label today;
    
    @FXML 
    private VBox vbox_route;
    // Constructor
    public inside_controller() {
        // Constructor logic, if any
    }
    
    // Method to set driver name
    public void setDriverName(String driverName) {
        if (driverNameLabel != null) {
            driverNameLabel.setText(driverName);
        }
    }
  
    public void initialize() {
        loadRouteButtons();
    }

    // Method to load route buttons from the database
    public void loadRouteButtons() {
    	String query = "SELECT route_name, fare FROM routes";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            List<HBox> buttonContainers = new ArrayList<>();
            while (rs.next()) {
                String routeName = rs.getString("route_name");
                double fare = rs.getDouble("fare");

                Button routeButton = new Button(routeName);
                Button fareLabel = new Button(String.format("₱%.2f", fare));
                VBox buttonContainer = new VBox(routeButton, fareLabel);
                buttonContainer.setAlignment(null);
                routeButton.getStyleClass().add("route-button");
                fareLabel.getStyleClass().add("fare-button");

                routeButton.setOnAction(e -> handleRouteSelection(routeName));
                
                fareLabel.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(String.format("%.2f", fare));
                    dialog.setTitle("Update Fare");
                    dialog.setHeaderText("Update the fare for " + routeName);
                    dialog.setContentText("New fare:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newFareStr -> {
                        try {
                            double newFare = Double.parseDouble(newFareStr);
                            updateFareInDatabase(routeName, newFare); 
                            fareLabel.setText(String.format("₱%.2f", newFare));
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid fare entered: " + newFareStr);
                        }
                    });
                });

                HBox hbox = new HBox(buttonContainer);
                buttonContainers.add(hbox);
            }
            Platform.runLater(() -> {
                hbox_route.getChildren().clear();
                hbox_route.getChildren().addAll(buttonContainers);
            });
        } catch (SQLException ex) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading routes: " + ex.getMessage());
                alert.showAndWait();
            });
        }
    }
    
    private void updateFareInDatabase(String routeName, double newFare) {
    	
    	   String sql = "UPDATE routes SET fare = ? WHERE route_name = ?";
    	    
    	    try (Connection conn = dbManager.getConnection();
    	            PreparedStatement pst = conn.prepareStatement(sql)) {
    	           
    	           pst.setDouble(1, newFare);
    	           pst.setString(2, routeName);
    	           
    	           int rowsAffected = pst.executeUpdate();
    	           
    	           if (rowsAffected > 0) {
    	                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fare updated successfully for route: " + routeName);
    	                alert.showAndWait();

    	           } else {
    	                Alert alert = new Alert(Alert.AlertType.ERROR, "No records found for route: " + routeName);
    	                alert.showAndWait();

    	               System.out.println("No records found for route: " + routeName);
    	           }
    	       } catch (SQLException e) {
    	    	   Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating fare for route: " + routeName);
	                alert.showAndWait();
    	           e.printStackTrace();
    	       }
    	   }

	


    private void handleRouteSelection(String routeName) {
      	today.setVisible(true);

    	 if (!selectedRouteNames.contains(routeName)) {
             selectedRouteNames.add(routeName);

             // Create a new button for the selected route
             Button s_route = new Button(routeName);
             s_route.getStyleClass().add("route-button");
             s_route.setOnAction(e -> handleRouteDeselection(routeName, s_route));
             selectedRouteButtons.add(s_route);

             
             // Update the VBox with the new set of buttons
             Platform.runLater(() -> {
                 vbox_route.getChildren().clear();
                 vbox_route.getChildren().addAll(selectedRouteButtons);
             });
             

         }

         // Display an alert for the selected route
         Platform.runLater(() -> {
             Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chosen route: " + routeName);
             alert.showAndWait();
         });
     }
    private void handleRouteDeselection(String routeName, Button s_route) {
    	
    	selectedRouteNames.remove(routeName);
        selectedRouteButtons.remove(s_route);

        // Update the VBox to reflect the current state of selected routes
        Platform.runLater(() -> {
            vbox_route.getChildren().clear();
            vbox_route.getChildren().addAll(selectedRouteButtons);
        });
    }
    
    
}
