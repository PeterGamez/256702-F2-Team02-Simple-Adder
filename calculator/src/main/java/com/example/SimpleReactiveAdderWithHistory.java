package com.example;

import java.util.Random;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class SimpleReactiveAdderWithHistory extends Application {
    private static final Random random = new Random();

    private final StringProperty valueA = new SimpleStringProperty("0");
    private final StringProperty valueB = new SimpleStringProperty("0");
    private final IntegerProperty outputValue = new SimpleIntegerProperty(0);
    private final BooleanProperty isValidInput = new SimpleBooleanProperty(true);
    
    private final ObservableList<IntegerPair> history = FXCollections.observableArrayList();
    private ListView<IntegerPair> historyView;
    private ChoiceBox<String> operationChoiceBox; // **แก้ไขตรงนี้**

    private static class IntegerPair {
        private final int a, b;

        public IntegerPair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() { return a; }
        public int getB() { return b; }

        @Override
        public String toString() {
            return String.format("A: %d, B: %d", a, b);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createMainView(), 600, 200);

        // ✅ ใช้ addListener แทน subscribe
        valueA.addListener((obs, oldValue, newValue) -> updateOutput());
        valueB.addListener((obs, oldValue, newValue) -> updateOutput());

        stage.setScene(scene);
        stage.setTitle("Simple Adder");
        stage.show();
    }

    private void updateOutput() {
        try {
            int a = Integer.parseInt(valueA.get());
            int b = Integer.parseInt(valueB.get());
            int result = 0;

            switch (operationChoiceBox.getValue()) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    if (b != 0) {
                        result = a / b;
                    } else {
                        isValidInput.set(false);
                        return;
                    }
                    break;
            }
            outputValue.set(result);
            isValidInput.set(true);
        } catch (NumberFormatException e) {
            isValidInput.set(false);
        }
    }

    private Region createMainView() {
        BorderPane view = new BorderPane();
        view.setTop(createHeading());
        view.setCenter(createCenterContent());
        view.setRight(createHistoryPane());
        return view;
    }

    private Node createHeading() {
        Label heading = new Label("Simple Calculator");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        StackPane headingPane = new StackPane(heading);
        headingPane.setAlignment(Pos.CENTER);

        return headingPane;
    }

    private Node createCenterContent() {
        VBox inputOutputPane = new VBox(20, createInputRow(), createOutputPane());
        inputOutputPane.setPadding(new Insets(20));
        inputOutputPane.setAlignment(Pos.CENTER);

        BorderPane centerContent = new BorderPane();
        centerContent.setCenter(inputOutputPane);
        centerContent.setBottom(createButtonRow());

        return centerContent;
    }

    private Node createInputRow() {
        TextField textFieldA = new TextField();
        textFieldA.textProperty().bindBidirectional(valueA);

        TextField textFieldB = new TextField();
        textFieldB.textProperty().bindBidirectional(valueB);

        operationChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("+", "-", "*", "/"));
        operationChoiceBox.setValue("+");

        HBox inputRow = new HBox(20, textFieldA, operationChoiceBox, textFieldB);
        inputRow.setAlignment(Pos.CENTER);
        return inputRow;
    }

    private Node createOutputPane() {
        StackPane outputPane = new StackPane(createOutputRow(), createWarningLabel());
        return outputPane;
    }

    private Node createOutputRow() {
        Label labelA = new Label("0");
        labelA.textProperty().bind(valueA);

        Label labelB = new Label("0");
        labelB.textProperty().bind(valueB);

        Label operationLabel = new Label();
        operationLabel.textProperty().bind(operationChoiceBox.valueProperty());

        Label outputLabel = new Label("0");
        outputLabel.textProperty().bind(outputValue.asString());

        HBox outputRow = new HBox(10, labelA, operationLabel, labelB, new Label("="), outputLabel);
        outputRow.visibleProperty().bind(isValidInput);
        outputRow.setAlignment(Pos.CENTER);
        return outputRow;
    }

    private Node createWarningLabel() {
        Label warningLabel = new Label("Invalid input format.");
        warningLabel.visibleProperty().bind(isValidInput.not());
        warningLabel.setTextFill(Color.RED);
        return warningLabel;
    }

    private Node createButtonRow() {
        HBox buttonRow = new HBox(20, createRandomizeButton(), createCalculateButton());
        buttonRow.setPadding(new Insets(0, 0, 20, 0));
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    private Button createCalculateButton() {
        Button calculateButton = new Button("Calculate");
        calculateButton.disableProperty().bind(isValidInput.not());
        calculateButton.setOnAction(evt -> {
            updateOutput();
            history.add(new IntegerPair(Integer.parseInt(valueA.get()), Integer.parseInt(valueB.get())));
            historyView.scrollTo(history.size() - 1);
        });
        return calculateButton;
    }

    private Node createRandomizeButton() {
        Button randomizeButton = new Button("Randomize");
        randomizeButton.setOnAction(evt -> {
            valueA.set(String.valueOf(random.nextInt(-1000, 1000)));
            valueB.set(String.valueOf(random.nextInt(-1000, 1000)));
        });
        return randomizeButton;
    }

    private Node createHistoryPane() {
        historyView = new ListView<>(history);
        historyView.setCellFactory(lv -> createCell());

        VBox historyPane = new VBox(historyView);
        historyPane.setPadding(new Insets(10));
        historyPane.setMaxWidth(160);

        return historyPane;
    }

    private ListCell<IntegerPair> createCell() {
        return new ListCell<>() {
            private final Label displayA = new Label();
            private final Label displayB = new Label();
            private final HBox layout = new HBox(10, new Label("A:"), displayA, new Label("B:"), displayB);

            @Override
            protected void updateItem(IntegerPair item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    displayA.setText(String.valueOf(item.getA()));
                    displayB.setText(String.valueOf(item.getB()));
                    setGraphic(layout);
                }
            }
        };
    }
}
