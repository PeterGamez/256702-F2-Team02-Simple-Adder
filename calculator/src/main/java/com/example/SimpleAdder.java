package com.example;

import java.text.NumberFormat;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleAdder extends Application {

    private static Random random = new Random();

    private TextField textFieldA, textFieldB;
    private Label labelA, labelB;
    private Label operatorLabel;
    private Label outputLabel, warningLabel;
    private HBox outputRow;
    private ComboBox<String> operationComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createMainView(), 500, 250);

        stage.setScene(scene);
        stage.setTitle("Simple Adder");
        stage.show();
    }

    private BorderPane createMainView() {
        BorderPane view = new BorderPane();
        view.getStylesheets().add(getClass().getResource("/css/simple-adder.css").toExternalForm());
        view.setTop(createHeading());
        view.setCenter(createCenterContent());
        view.setBottom(createButtonRow());
        return view;
    }

    private Label createHeading() {
        Label heading = new Label("Simple Adder");
        HBox.setHgrow(heading, Priority.ALWAYS);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);
        heading.getStyleClass().add("heading-label");
        return heading;
    }

    private VBox createCenterContent() {
        HBox inputRow = createInputRow();
        StackPane outputPane = createOutputPane();

        VBox centerContent = new VBox(20, inputRow, outputPane);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.CENTER);
        return centerContent;
    }

    private HBox createInputRow() {
        textFieldA = new TextField("0");
        textFieldB = new TextField("0");

        operationComboBox = new ComboBox<>();
        operationComboBox.getItems().addAll("+", "-", "x", "/");
        operationComboBox.setValue("+");

        HBox inputRow = new HBox(20, new Label("A:"), textFieldA, operationComboBox, new Label("B:"), textFieldB);
        inputRow.setAlignment(Pos.CENTER);
        return inputRow;
    }

    private StackPane createOutputPane() {
        outputRow = createOutputRow();

        Label warningLabel = createWarningLabel();
        warningLabel.setVisible(false);

        StackPane outputPane = new StackPane(outputRow, warningLabel);
        return outputPane;
    }

    private HBox createOutputRow() {
        labelA = new Label("0");
        labelB = new Label("0");
        outputLabel = new Label("0");
        operatorLabel = new Label(operationComboBox.getValue());
        ;

        HBox outputRow = new HBox(10, labelA, operatorLabel, labelB, new Label("="), outputLabel);
        outputRow.setAlignment(Pos.CENTER);
        return outputRow;
    }

    private Label createWarningLabel() {
        warningLabel = new Label("Invalid input format.");
        warningLabel.getStyleClass().add("warning");
        return warningLabel;
    }

    private HBox createButtonRow() {
        HBox buttonRow = new HBox(20, createRandomizeButton(), createAddButton());
        buttonRow.setPadding(new Insets(0, 0, 20, 0));
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    private Button createRandomizeButton() {
        Button randomizeButton = new Button("Randomize");
        randomizeButton.setOnAction(evt -> {
            textFieldA.setText(formatNumber(rangeRandomInt(-1000, 1000)));
            textFieldB.setText(formatNumber(rangeRandomInt(-1000, 1000)));
        });
        return randomizeButton;
    }

    private Button createAddButton() {
        Button addButton = new Button("Calculate");
        addButton.setOnAction(evt -> calculateEvent(evt));
        return addButton;
    }

    private void calculateEvent(ActionEvent evt) {
        String valueA = textFieldA.getText();
        String valueB = textFieldB.getText();
        String operation = operationComboBox.getValue();

        labelA.setText(formatNumber(valueA));
        labelB.setText(formatNumber(valueB));

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
            operatorLabel.setText(operationComboBox.getValue());
            outputLabel.setText(formatNumber(result));
            showOutput();
        } catch (NumberFormatException e) {
            warningLabel.setText("Invalid input format.");
            showWarning();
        } catch (ArithmeticException e) {
            warningLabel.setText(e.getMessage());
            showWarning();
        }
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

    private String formatNumber(int number) {
        return String.valueOf(NumberFormat.getInstance().format(number));
    }

    private String formatNumber(String number) {
        return String.valueOf(NumberFormat.getInstance().format(Integer.parseInt(number)));
    }
}