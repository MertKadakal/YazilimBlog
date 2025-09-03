package mert.kadakal.yazlmblog.ui.hesap;

public class EmailCheckResult {
    private final boolean exists;
    private final Integer id; // null olabilir

    public EmailCheckResult(boolean exists, Integer id) {
        this.exists = exists;
        this.id = id;
    }

    public boolean exists() {
        return exists;
    }

    public Integer getId() {
        return id;
    }
}
