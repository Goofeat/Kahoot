package kahoot.domain;

public abstract class Question {
    private String description;
    private String answer;
    private String url;
    private String[] options;

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOptionAt(int index) {
        return options[index];
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
}
