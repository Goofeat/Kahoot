package kahoot;

import static kahoot.Quiz.QUESTION_LIST;
import static kahoot.QuizMaker.currentQuestion;

public class Picture extends Question {

    public Picture() {}

    public Picture(String description, String url, String answer) {
        setDescription(description);
        setURL(url);
        setAnswer(answer);
    }

    @Override
    public String toString() {
        return String.format("%d. %s", currentQuestion + 1, QUESTION_LIST.get(currentQuestion).
                getDescription().replace("{image}", ""));
    }
}
