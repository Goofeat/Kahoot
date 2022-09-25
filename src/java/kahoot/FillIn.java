package kahoot;

import static kahoot.Quiz.QUESTION_LIST;
import static kahoot.QuizMaker.currentQuestion;

public class FillIn extends Question {

    public FillIn() {
    }

    public FillIn(String description, String answer) {
        setDescription(description);
        setAnswer(answer);
    }

    @Override
    public String toString() {
        return String.format("%d. %s", currentQuestion + 1, QUESTION_LIST.get(currentQuestion).
                getDescription().replace("{blank}", "__________"));
    }
}
