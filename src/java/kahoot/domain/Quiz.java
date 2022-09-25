package kahoot.domain;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static kahoot.App.RADIO_BUTTONS;
import static kahoot.App.TOGGLE_GROUPS;

public class Quiz {
    public static final ArrayList<Question> QUESTION_LIST = new ArrayList<>();
    private String quizName;

    public Quiz() {}

    public void setQuizName(String quizName) {
        this.quizName = quizName.replace(".txt", "");
    }

    public String getQuizName() {
        return quizName;
    }

    @Override
    public String toString() {
        return getQuizName();
    }

    public void loadFromFile(File file) throws FileNotFoundException {

        Scanner fileInput = new Scanner(file);

        String description;
        String url;
        String answer;

        String[] temp = new String[4];

        while (fileInput.hasNext()) {

            description = fileInput.nextLine();

            if (description.contains("{blank}")) {

                answer = fileInput.nextLine();

                addQuestion(new FillIn(description, answer));

            } else if (description.contains("{image}")) {

                url = fileInput.nextLine();

                answer = fileInput.nextLine();

                addQuestion(new Picture(description, url, answer));

            } else {

                answer = fileInput.nextLine();

                temp[0] = answer;

                for (int i = 1; i < 4; i++)
                    temp[i] = fileInput.nextLine();

                addQuestion(new Test(description, answer, temp));

            }

            if (fileInput.hasNext()) fileInput.nextLine();
        }
    }

    public void addQuestion(Question question) {

        QUESTION_LIST.add(question);

        TOGGLE_GROUPS.add(new ToggleGroup());

        RADIO_BUTTONS.add(new RadioButton[4]);
    }
}