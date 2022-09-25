package kahoot.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static kahoot.domain.Quiz.QUESTION_LIST;
import static kahoot.App.currentQuestion;

public class Test extends Question {
    private final int numberOfOptions = 4;
    private final ArrayList<String> optionsList = new ArrayList<>(numberOfOptions);

    public Test() {
    }

    public Test(String description, String answer, String[] options) {
        setDescription(description);
        setAnswer(answer);
        setOptions(options);
    }

    @Override
    public String getOptionAt(int index) {
        return optionsList.get(index);
    }

    @Override
    public void setOptions(String[] options) {
        optionsList.addAll(Arrays.asList(options));

        Collections.shuffle(optionsList);

        for (int i = 0; i < numberOfOptions; i++) {
            options[i] = optionsList.get(i);
        }

        super.setOptions(options);
    }

    @Override
    public String toString() {
        return String.format("%d. %s", currentQuestion + 1, QUESTION_LIST.get(currentQuestion).getDescription());
    }
}
