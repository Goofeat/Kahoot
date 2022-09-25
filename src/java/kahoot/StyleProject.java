package kahoot;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static kahoot.QuizMaker.RADIO_BUTTONS;
import static kahoot.QuizMaker.TOGGLE_GROUPS;

public class StyleProject {
    public static final Font FONT = Font.font("Arial", 14);
    public static final Font BOLD_FONT = Font.font("Arial", FontWeight.BOLD, 16);
    public static final Font RES_FONT = Font.font("Arial", 16);
    public static final Font RES_BOLD_FONT = Font.font("Arial", FontWeight.BOLD, 18);
    public static final Font MTBOLD_FONT = Font.font("Arial Rounded MT Bold", 14);

    public static void setRadioStyle(int index, RadioButton... massiveButtons) {
        for (RadioButton radio : massiveButtons) {
            radio.setFont(FONT);
            radio.setTextFill(Color.WHITE);
            radio.setToggleGroup(TOGGLE_GROUPS.get(index));
        }

        RADIO_BUTTONS.remove(index);
        RADIO_BUTTONS.add(index, massiveButtons);
    }

    public static Rectangle createButton(String rgb){
        return new Rectangle(335, 74, Color.valueOf(rgb));
    }

    public static void setButtonStyle(Button button) {
        button.setMaxHeight(50);
        button.setMaxWidth(35);
        button.setFont(MTBOLD_FONT);
    }

    public static void setTimerStyle(Label timer) {
        timer.setAlignment(Pos.TOP_CENTER);
        timer.setMaxWidth(464);
        timer.setMaxHeight(248);
        timer.setContentDisplay(ContentDisplay.BOTTOM);
        timer.setTextFill(Color.BLACK);
        timer.setFont(FONT);
    }

    public static StackPane createResultButton(String string, String color) {
        Text text = new Text(string);
        text.setFill(Color.WHITE);
        text.setFont(MTBOLD_FONT);

        Rectangle rectangle = new Rectangle(264, 50, Color.valueOf(color));

        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(200);
        stackPane.setPrefHeight(150);
        stackPane.getChildren().addAll(rectangle, text);

        return stackPane;
    }

    public static StackPane stackButton(Rectangle rectangle, RadioButton radio) {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(rectangle, radio);

        return stackPane;
    }

    public static String timeFormat(int second) {
        return String.format("%02d:%02d", second / 60, second % 60);
    }
}
