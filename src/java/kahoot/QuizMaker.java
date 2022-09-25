package kahoot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import static kahoot.Quiz.QUESTION_LIST;
import static kahoot.StyleProject.*;

public class QuizMaker extends Application {
    public static final ArrayList<ToggleGroup> TOGGLE_GROUPS = new ArrayList<>();
    public static final ArrayList<RadioButton[]> RADIO_BUTTONS = new ArrayList<>();
    public static final int WIDTH = 680;
    public static final int HEIGHT = 460;

    public static int currentQuestion;

    private final VBox startVBox = new VBox(10);
    private final Button chooseFileButton = new Button("Choose a file");
    private final FileChooser fileChooser = new FileChooser();
    private final CheckBox shuffleCheckBox = new CheckBox("Enable shuffling questions");
    private final Button startButton = new Button("Start!");
    private final Button previousButton = new Button("←"); // \u2190
    private final Button nextButton = new Button("→"); // \u2192
    private final Button completeButton = new Button("✓"); // \u2713

    private Stage kahoot;
    private MediaPlayer songPlayer;
    private Timeline timeLine;
    private Label timerLabel;
    private TextField userAnswerField;
    private RadioButton redRadio, orangeRadio, blueRadio, greenRadio;
    private ImageView backgroundView;
    private ImageView testView;
    private ImageView fillInView;
    private ImageView logoView;
    private ImageView resultView;
    private Quiz quiz;

    private String[] userFillInAnswers;
    private int numberOfQuestions;
    private int totalScore;
    private int timeInSeconds;
    private boolean isQuestionShuffled;

    private final Label START_LABEL = new Label("Please select a text document to start Quiz:", chooseFileButton);

    public void importAll() throws FileNotFoundException {

        Image backgroundImage = new Image(new FileInputStream("/Users/goofeat/IdeaProjects/project/src/resources/img/background.jpg"));
        backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(WIDTH);
        backgroundView.setFitHeight(HEIGHT);

        Image testImage = new Image(new FileInputStream("/Users/goofeat/IdeaProjects/project/src/resources/img/logo.png"));
        testView = new ImageView(testImage);
        testView.setFitWidth(373);
        testView.setFitHeight(209);

        Image fillInImage = new Image(new FileInputStream("/Users/goofeat/IdeaProjects/project/src/resources/img/fillin.png"));
        fillInView = new ImageView(fillInImage);
        fillInView.setFitWidth(373);
        fillInView.setFitHeight(209);

        Image logoImage = new Image(new FileInputStream("/Users/goofeat/IdeaProjects/project/src/resources/img/k.png"));
        logoView = new ImageView(logoImage);
        logoView.setFitHeight(20);
        logoView.setPreserveRatio(true);

        Image resultImage = new Image(new FileInputStream("/Users/goofeat/IdeaProjects/project/src/resources/img/result.png"));
        resultView = new ImageView(resultImage);
        resultView.setFitWidth(367);
        resultView.setFitHeight(218);

        File songFile = new File("/Users/goofeat/IdeaProjects/project/src/resources/kahoot_music.mp3");
        Media songMedia = new Media(songFile.toURI().toString());
        songPlayer = new MediaPlayer(songMedia);
        songPlayer.setVolume(0.);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        quiz = new Quiz();

        importAll();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);

        chooseFileButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(kahoot);

            if (file != null) {
                quiz.setQuizName(file.getName());

                START_LABEL.setText("You have selected \"" + file.getName() + "\" file.");

                if (!(startVBox.getChildren().contains(shuffleCheckBox))) {
                    startVBox.getChildren().addAll(shuffleCheckBox, startButton);
                }
            }

            startButton.setOnAction(ev -> {
                isQuestionShuffled = shuffleCheckBox.isSelected();

                try {
                    quiz.loadFromFile(file);
                } catch (FileNotFoundException ignored) {}

                numberOfQuestions = QUESTION_LIST.size();

                userFillInAnswers = new String[numberOfQuestions];

                songPlayer.play();

                timerLabel = new Label();
                timerLabel.setText(timeFormat(timeInSeconds));
                setTimerStyle(timerLabel);

                timeLine = new Timeline();

                timeLine.setCycleCount(Timeline.INDEFINITE);

                timeLine.getKeyFrames().add(new KeyFrame(
                        Duration.seconds(1),
                        event -> timerLabel.setText(timeFormat(++timeInSeconds))));

                timeLine.play();

                if (isQuestionShuffled) Collections.shuffle(QUESTION_LIST);

                kahoot.setTitle(String.format("Quiz \"%s\" (%d/%d question)",
                        quiz.getQuizName(), currentQuestion + 1, numberOfQuestions));

                kahoot.setScene(new Scene(showQuestion(currentQuestion), WIDTH, HEIGHT));
            });
        });

        kahoot = primaryStage;

        kahoot.setResizable(false);

        kahoot.setTitle("Raiymbek's Kahoot Project");

        kahoot.setScene(new Scene(showGreeting(), WIDTH, HEIGHT));

        kahoot.show();
    }

    public BorderPane showGreeting() {

        START_LABEL.setFont(FONT);
        START_LABEL.setTextFill(Color.WHITE);
        START_LABEL.setContentDisplay(ContentDisplay.BOTTOM);
        START_LABEL.setGraphicTextGap(10);

        chooseFileButton.setFont(FONT);

        shuffleCheckBox.setFont(FONT);
        shuffleCheckBox.setTextFill(Color.WHITE);

        startButton.setFont(FONT);

        startVBox.getChildren().add(START_LABEL);
        startVBox.setAlignment(Pos.CENTER);

        StackPane stackMenu = new StackPane();
        stackMenu.getChildren().addAll(backgroundView, startVBox);

        BorderPane mainMenu = new BorderPane();
        mainMenu.setCenter(stackMenu);

        VBox.setMargin(START_LABEL, new Insets(50, 0, 0, 0));

        return mainMenu;
    }

    public BorderPane showQuestion(int index) {

        BorderPane questionPane = new BorderPane();

        FillIn fillIn = new FillIn();
        Picture picture = new Picture();
        Test test = new Test();

        Label question;

        if (QUESTION_LIST.get(index) instanceof FillIn ||
                QUESTION_LIST.get(index) instanceof Picture) {

            if (QUESTION_LIST.get(index) instanceof Picture) {

                ImageView urlView = new ImageView(QUESTION_LIST.get(index).getURL());

                urlView.setPreserveRatio(true);
                urlView.setFitHeight(209);

                timerLabel.setGraphic(urlView);

                question = new Label(picture.toString(), logoView);

            } else {

                timerLabel.setGraphic(fillInView);

                question = new Label(fillIn.toString(), logoView);

            }

            userAnswerField = new TextField();
            userAnswerField.setAlignment(Pos.CENTER);
            userAnswerField.setPromptText("Your answer...");
            userAnswerField.setPrefWidth(400);
            userAnswerField.setFont(FONT);

            if (userFillInAnswers[index] != null)
                userAnswerField.setText(userFillInAnswers[index]);

            Label typeYourAnswer = new Label("Type your answer here:", userAnswerField);
            typeYourAnswer.setContentDisplay(ContentDisplay.BOTTOM);
            typeYourAnswer.setFont(FONT);

            BorderPane textFieldPane = new BorderPane();
            textFieldPane.setPrefHeight(157);
            textFieldPane.setTop(typeYourAnswer);

            BorderPane.setAlignment(typeYourAnswer, Pos.CENTER);
            BorderPane.setAlignment(textFieldPane, Pos.CENTER);

            questionPane.setBottom(textFieldPane);

        } else {

            timerLabel.setGraphic(testView);

            question = new Label(test.toString());

            Rectangle redRectangle = createButton("e21b3c");
            Rectangle orangeRectangle = createButton("ffa602");
            Rectangle blueRectangle = createButton("1368ce");
            Rectangle greenRectangle = createButton("26890c");

            redRadio = new RadioButton(QUESTION_LIST.get(index).getOptionAt(0));
            orangeRadio = new RadioButton(QUESTION_LIST.get(index).getOptionAt(1));
            blueRadio = new RadioButton(QUESTION_LIST.get(index).getOptionAt(2));
            greenRadio = new RadioButton(QUESTION_LIST.get(index).getOptionAt(3));

            setRadioStyle(index, redRadio, orangeRadio, blueRadio, greenRadio);

            try {
                redRadio.setSelected(TOGGLE_GROUPS.get(index).getToggles().get(0).isSelected());
                orangeRadio.setSelected(TOGGLE_GROUPS.get(index).getToggles().get(1).isSelected());
                blueRadio.setSelected(TOGGLE_GROUPS.get(index).getToggles().get(2).isSelected());
                greenRadio.setSelected(TOGGLE_GROUPS.get(index).getToggles().get(3).isSelected());
            } catch (NullPointerException ignored) {}

            StackPane redStack = stackButton(redRectangle, redRadio);
            StackPane orangeStack = stackButton(orangeRectangle, orangeRadio);
            StackPane blueStack = stackButton(blueRectangle, blueRadio);
            StackPane greenStack = stackButton(greenRectangle, greenRadio);

            redStack.setOnMouseClicked(e -> redRadio.setSelected(true));
            orangeStack.setOnMouseClicked(e -> orangeRadio.setSelected(true));
            blueStack.setOnMouseClicked(e -> blueRadio.setSelected(true));
            greenStack.setOnMouseClicked(e -> greenRadio.setSelected(true));

            VBox redOrangeBOX = new VBox(3);
            redOrangeBOX.getChildren().addAll(redStack, orangeStack);
            redOrangeBOX.setPadding(new Insets(3));

            VBox blueGreenBOX = new VBox(3);
            blueGreenBOX.getChildren().addAll(blueStack, greenStack);
            blueGreenBOX.setPadding(new Insets(3));

            HBox buttonsBOX = new HBox(redOrangeBOX, blueGreenBOX);
            buttonsBOX.setMaxHeight(150);

            questionPane.setBottom(buttonsBOX);
        }

        question.setFont(BOLD_FONT);
        question.setWrapText(true);
        question.setAlignment(Pos.CENTER);
        question.setMaxWidth(650);

        previousButton.setVisible(index != 0);
        setButtonStyle(previousButton);

        if (index != numberOfQuestions - 1) {
            nextButton.setVisible(true);
            questionPane.setRight(nextButton);
        }
        setButtonStyle(nextButton);

        if (index == numberOfQuestions - 1) {
            completeButton.setVisible(true);
            questionPane.setRight(completeButton);
        }
        setButtonStyle(completeButton);

        previousButton.setOnAction(e -> goToPrevious(index));

        nextButton.setOnAction(e -> goToNext(index));

        completeButton.setOnAction(e -> completeQuiz(index));

        BorderPane.setMargin(question, new Insets(15, 0, 0, 0));
        BorderPane.setAlignment(question, Pos.CENTER);
        BorderPane.setAlignment(previousButton, Pos.CENTER);
        BorderPane.setAlignment(nextButton, Pos.CENTER);
        BorderPane.setAlignment(completeButton, Pos.CENTER);

        questionPane.setTop(question);
        questionPane.setCenter(timerLabel);
        questionPane.setLeft(previousButton);

        return questionPane;
    }

    public void goToPrevious(int index) {
        if (QUESTION_LIST.get(index) instanceof FillIn ||
                QUESTION_LIST.get(index) instanceof Picture)
            setTypedAnswer(index);

        setSelectedToggle(index);

        kahoot.setTitle(String.format("Quiz \"%s\" (%d/%d question)",
                quiz.getQuizName(), currentQuestion, numberOfQuestions));

        kahoot.setScene(new Scene(showQuestion(--currentQuestion), WIDTH, HEIGHT));
    }

    public void goToNext(int index) {
        if (QUESTION_LIST.get(index) instanceof FillIn ||
                QUESTION_LIST.get(index) instanceof Picture)
            setTypedAnswer(index);

        setSelectedToggle(index);

        kahoot.setTitle(String.format("Quiz \"%s\" (%d/%d question)",
                quiz.getQuizName(), currentQuestion + 2, numberOfQuestions));

        kahoot.setScene(new Scene(showQuestion(++currentQuestion), WIDTH, HEIGHT));
    }

    public void completeQuiz(int index) {
        if (QUESTION_LIST.get(index) instanceof FillIn ||
                QUESTION_LIST.get(index) instanceof Picture)
            setTypedAnswer(index);

        setSelectedToggle(index);

        check();

        kahoot.setTitle(String.format("Results of quiz \"%s\"", quiz.getQuizName()));

        kahoot.setScene(new Scene(showResults(), WIDTH, HEIGHT));

        songPlayer.stop();

        timeLine.stop();
    }

    public void setSelectedToggle(int index) {

        try {
            TOGGLE_GROUPS.get(index).getToggles().get(0).setSelected(redRadio.isSelected());
            TOGGLE_GROUPS.get(index).getToggles().get(1).setSelected(orangeRadio.isSelected());
            TOGGLE_GROUPS.get(index).getToggles().get(2).setSelected(blueRadio.isSelected());
            TOGGLE_GROUPS.get(index).getToggles().get(3).setSelected(greenRadio.isSelected());

        } catch (IndexOutOfBoundsException ignored) {}
    }

    public void setTypedAnswer(int index) {

        try {
            if (userAnswerField.getText() != null) {

                userFillInAnswers[index] = userAnswerField.getText();

            }
        } catch (Exception ignored) {}
    }

    public BorderPane showResults() {

        BorderPane resultMenu = new BorderPane();

        resultMenu.setBottom(resultView);

        Text yourResult = new Text("Your result:");
        yourResult.setFont(RES_BOLD_FONT);

        Text percentage = new Text(String.format("%.2f%%", (totalScore * 100.) / numberOfQuestions));
        percentage.setFont(RES_FONT);

        Text numberOfCorrect = new Text(String.format("Number of correct answer: %d/%d", totalScore, numberOfQuestions));
        numberOfCorrect.setFont(RES_FONT);

        Text finishedTime = new Text(String.format("Finished in %s", timerLabel.getText()));
        finishedTime.setFont(RES_FONT);

        StackPane showButton = createResultButton("Show answers", "0542b9");

        StackPane closeButton = createResultButton("Close test", "c60929");

        VBox resultBox = new VBox(5);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPrefHeight(214);
        resultBox.getChildren().addAll(yourResult, percentage, numberOfCorrect, finishedTime);
        resultBox.getChildren().addAll(showButton, closeButton);

        VBox.setMargin(yourResult, new Insets(10, 0, 0, 0));
        VBox.setMargin(percentage, new Insets(20, 0, 0, 0));
        VBox.setMargin(numberOfCorrect, new Insets(10, 0, 0, 0));

        BorderPane.setAlignment(resultBox, Pos.CENTER);
        BorderPane.setAlignment(resultView, Pos.CENTER);

        resultMenu.setCenter(resultBox);

        showButton.setOnMouseClicked(e -> {
            currentQuestion = 0;

            kahoot.setTitle(String.format("Answers of quiz \"%s\" (%d/%d question)",
                    quiz.getQuizName(), currentQuestion + 1, numberOfQuestions));

            kahoot.setScene(new Scene(showAnswers(currentQuestion), WIDTH, HEIGHT));
        });

        closeButton.setOnMouseClicked(e -> System.exit(0));

        return resultMenu;
    }

    public BorderPane showAnswers(int index) {

        BorderPane showAnswersMenu = new BorderPane();

        Button nextResult = nextButton;
        nextResult.setVisible(currentQuestion != numberOfQuestions - 1);
        showAnswersMenu.setRight(nextResult);

        Button previousResult = previousButton;
        previousResult.setVisible(currentQuestion != 0);
        showAnswersMenu.setLeft(previousResult);

        nextResult.setOnAction(e -> {
            kahoot.setTitle(String.format("Answers of quiz \"%s\" (%d/%d question)",
                    quiz.getQuizName(), currentQuestion + 2, numberOfQuestions));

            kahoot.setScene(new Scene(showAnswers(++currentQuestion), WIDTH, HEIGHT));
        });

        previousResult.setOnAction(e -> {
            kahoot.setTitle(String.format("Answers of quiz \"%s\" (%d/%d question)",
                    quiz.getQuizName(), currentQuestion, numberOfQuestions));

            kahoot.setScene(new Scene(showAnswers(--currentQuestion), WIDTH, HEIGHT));
        });

        StackPane correctAnswerStack = new StackPane();
        BorderPane.setAlignment(correctAnswerStack, Pos.CENTER);

        Rectangle backgroundRect = new Rectangle(WIDTH - 80, 310);

        if (checkAt(index)) {
            backgroundRect.setFill(Color.valueOf("aaffaa"));
        } else {
            backgroundRect.setFill(Color.valueOf("ffaaaa"));
        }

        Text questionView = new Text(getQuestionString());
        questionView.setFont(Font.font("Arial Rounded MT Bold", 18));
        questionView.setWrappingWidth(400);
        questionView.setTextAlignment(TextAlignment.CENTER);
        BorderPane.setMargin(questionView, new Insets(100, 0, 0, 0));
        BorderPane.setAlignment(questionView, Pos.CENTER);

        Text answersView = new Text(getAnswerString());
        answersView.setFont(Font.font("Arial", 18));
        answersView.setWrappingWidth(400);
        answersView.setTextAlignment(TextAlignment.CENTER);
        BorderPane.setMargin(answersView, new Insets(0, 0, 50, 0));

        BorderPane questionWithAnswers = new BorderPane();
        questionWithAnswers.setPrefHeight(100);
        questionWithAnswers.setTop(questionView);
        questionWithAnswers.setCenter(answersView);
        correctAnswerStack.getChildren().addAll(backgroundRect, questionWithAnswers);

        HBox controlButtons = new HBox();
        controlButtons.setPrefHeight(80);

        BorderPane backToResultsPane = new BorderPane();
        backToResultsPane.setPrefWidth(WIDTH / 2.);

        StackPane backToResultsButton = createResultButton("Back to results", "0542b9");

        backToResultsPane.setCenter(backToResultsButton);

        backToResultsButton.setOnMouseClicked(e -> {
            kahoot.setTitle(String.format("Results of quiz \"%s\"", quiz.getQuizName()));

            kahoot.setScene(new Scene(showResults(), WIDTH, HEIGHT));
        });

        BorderPane closeTestPane = new BorderPane();
        closeTestPane.setPrefWidth(WIDTH / 2.);

        StackPane closeTestButton = createResultButton("Close test", "c60929");

        closeTestPane.setCenter(closeTestButton);

        closeTestPane.setOnMouseClicked(e -> System.exit(0));

        controlButtons.getChildren().addAll(backToResultsPane, closeTestPane);

        showAnswersMenu.setCenter(correctAnswerStack);
        showAnswersMenu.setBottom(controlButtons);

        return showAnswersMenu;
    }

    public void check() {

        for (int i = 0; i < numberOfQuestions; i++) {

            if (QUESTION_LIST.get(i) instanceof FillIn || QUESTION_LIST.get(i) instanceof Picture) {

                if (userFillInAnswers[i].equalsIgnoreCase(QUESTION_LIST.get(i).getAnswer())) {

                    totalScore++;
                }

            } else {

                for (int j = 0; j < 4; j++) {

                    if (TOGGLE_GROUPS.get(i).getToggles().get(j).isSelected()) {

                        if (RADIO_BUTTONS.get(i)[j].getText().equalsIgnoreCase(QUESTION_LIST.get(i).getAnswer())) {

                            totalScore++;
                        }
                    }
                }
            }
        }
    }

    public boolean checkAt(int i) {

        if (QUESTION_LIST.get(i) instanceof FillIn || QUESTION_LIST.get(i) instanceof Picture) {

            return userFillInAnswers[i].equalsIgnoreCase(QUESTION_LIST.get(i).getAnswer());

        } else {

            for (int j = 0; j < 4; j++) {

                if (TOGGLE_GROUPS.get(i).getToggles().get(j).isSelected()) {

                    if (RADIO_BUTTONS.get(i)[j].getText().equalsIgnoreCase(QUESTION_LIST.get(i).getAnswer())) {

                        return true;

                    }
                }
            }
        }

        return false;
    }

    public String getQuestionString() {

        FillIn fillIn = new FillIn();
        Picture image = new Picture();
        Test test = new Test();

        if (QUESTION_LIST.get(currentQuestion) instanceof FillIn) {

            return fillIn.toString();

        } else if (QUESTION_LIST.get(currentQuestion) instanceof Picture) {

            return image.toString();

        } else return test.toString();
    }

    public String getAnswerString() {

        if (QUESTION_LIST.get(currentQuestion) instanceof FillIn ||
                QUESTION_LIST.get(currentQuestion) instanceof Picture) {

            if (userFillInAnswers[currentQuestion].isEmpty()) {

                return String.format("Correct answer: %s%nYou did not answer to this question",
                        QUESTION_LIST.get(currentQuestion).getAnswer());

            } else {

                return String.format("Correct answer: %s%nYour answer: %s",
                        QUESTION_LIST.get(currentQuestion).getAnswer(),
                        userFillInAnswers[currentQuestion]);
            }

        } else {

            for (int j = 0; j < 4; j++) {

                if (TOGGLE_GROUPS.get(currentQuestion).getToggles().get(j).isSelected()) {

                    return String.format("Correct answer: %s%nYour answer: %s",
                            QUESTION_LIST.get(currentQuestion).getAnswer(),
                            RADIO_BUTTONS.get(currentQuestion)[j].getText());
                }
            }
        }

        return String.format("Correct answer: %s%nYou did not answer to this question",
                QUESTION_LIST.get(currentQuestion).getAnswer());
    }
}