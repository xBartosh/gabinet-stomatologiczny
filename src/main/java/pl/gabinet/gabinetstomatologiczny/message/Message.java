package pl.gabinet.gabinetstomatologiczny.message;

public enum Message {
    NEW_VISIT("New visit scheduled, time: %s, surgeries: %s"),
    RESCHEDULE_VISIT("Visit has been rescheduled, from: %s, to: %s"),
    CANCEL_VISIT("Visit has been canceled, visit time: %s"),
    PAY_VISIT("Paid for a visit, time: %s, amount: %d"),
    ADD_BALANCE("Added balance, amount: %d");

    private final String message;
    Message(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
