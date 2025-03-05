package com.example;

import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleAdder extends Application {

    private static Random random = new Random();

    private TextField textFieldA;
    private TextField textFieldB;
    private Label labelA;
    private Label labelB;
    private Label outputLabel;
    private Label warningLabel;
    private Node outputRow;
    private ComboBox<String> operationComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var scene = new Scene(createMainView(), 500, 250);

        stage.setScene(scene);
        stage.setTitle("Simple Adder");
        stage.show();
    }

    private Region createMainView() {
        var view = new BorderPane();
        view.getStylesheets().add(getClass().getResource("/css/simple-adder.css").toExternalForm());
        view.setTop(createHeading());
        view.setCenter(createCenterContent());
        view.setBottom(createButtonRow());
        return view;
    }

    private Node createHeading() {
        var heading = new Label("Simple Adder");
        HBox.setHgrow(heading, Priority.ALWAYS);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);
        heading.getStyleClass().add("heading-label");
        return heading;
    }

    private Node createCenterContent() {
        var inputRow = createInputRow();
        var outputPane = createOutputPane();

        var centerContent = new VBox(20, inputRow, outputPane);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.CENTER);

        return centerContent;
    }

    private Node createInputRow() {
        textFieldA = new TextField("0");
        textFieldB = new TextField("0");
        operationComboBox = new ComboBox<>();
        operationComboBox.getItems().addAll("+", "-", "x", "/");
        operationComboBox.setValue("+");
        var inputRow = new HBox(20, new Label("A:"), textFieldA, operationComboBox, new Label("B:"), textFieldB);
        inputRow.setAlignment(Pos.CENTER);
        return inputRow;
    }

    private Node createOutputPane() {
        outputRow = createOutputRow();
        var warningLabel = createWarningLabel();
        warningLabel.setVisible(false);
        var outputPane = new StackPane(outputRow, warningLabel);
        return outputPane;
    }

    private Node createOutputRow() {
        labelA = new Label("0");
        labelB = new Label("0");
        outputLabel = new Label("0");
        var operatorLabel = new Label();
        operationComboBox.setOnAction(evt -> operatorLabel.setText(operationComboBox.getValue()));
        operatorLabel.setText(operationComboBox.getValue());
        var outputRow = new HBox(10, labelA, operatorLabel, labelB, new Label("="), outputLabel);
        outputRow.setAlignment(Pos.CENTER);
        return outputRow;
    }

    private Node createWarningLabel() {
        warningLabel = new Label("Invalid input format.");
        warningLabel.getStyleClass().add("warning");
        return warningLabel;
    }

    private Node createButtonRow() {
        var buttonRow = new HBox(20, createRandomizeButton(), createAddButton());
        buttonRow.setPadding(new Insets(0, 0, 20, 0));
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    private Node createRandomizeButton() {
        var randomizeButton = new Button("Randomize");
        randomizeButton.setOnAction(evt -> {
            textFieldA.setText(String.valueOf(rangeRandomInt(-1000, 1000)));
            textFieldB.setText(String.valueOf(rangeRandomInt(-1000, 1000)));
        });
        return randomizeButton;
    }

    private Node createAddButton() {
        var addButton = new Button("Calculate");
        addButton.setOnAction(evt -> {
            String valueA = textFieldA.getText();
            String valueB = textFieldB.getText();
            String operation = operationComboBox.getValue();
            labelA.setText(valueA);
            labelB.setText(valueB);
            try {
                int numA = Integer.parseInt(valueA);
                int numB = Integer.parseInt(valueB);
                int result = 0;
                switch (operation) {
                    case "+":
                        result = numA + numB;
                        break;
                    case "-":
                        result = numA - numB;
                        break;
                    case "x":
                        result = numA * numB;
                        break;
                    case "/":
                        if (numB == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        result = numA / numB;
                        break;
                }
                outputLabel.setText(String.valueOf(result));
                showOutput();
            } catch (NumberFormatException e) {
                warningLabel.setText("Invalid input format.");
                showWarning();
            } catch (ArithmeticException e) {
                warningLabel.setText(e.getMessage());
                showWarning();
            }
        });
        return addButton;
    }

    private void showOutput() {
        outputRow.setVisible(true);
        warningLabel.setVisible(false);
    }

    private void showWarning() {
        outputRow.setVisible(false);
        warningLabel.setVisible(true);
    }

    private int rangeRandomInt(int start, int end) {
        return random.nextInt(start, end);
    }
}
