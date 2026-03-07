package bolts;

import java.util.List;

public class AggregateException extends Exception {
    private static final long serialVersionUID = 1;
    private List<Exception> errors;

    public AggregateException(List<Exception> errors2) {
        super("There were multiple errors.");
        this.errors = errors2;
    }

    public List<Exception> getErrors() {
        return this.errors;
    }
}
