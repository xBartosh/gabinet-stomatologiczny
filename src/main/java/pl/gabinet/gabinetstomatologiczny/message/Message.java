package pl.gabinet.gabinetstomatologiczny.message;

public enum Message {
    NEW_VISIT("Nowa wizyta, godzina: %s, zabiegi: %s"),
    RESCHEDULE_VISIT("Zmieniono termin wizyty, z: %s, na: %s"),
    CANCEL_VISIT("Anulowano wizytę, godzina wizyty: %s"),
    PAY_VISIT("Zapłacono za wizytę, czas: %s, kwota: %.2f"),
    ADD_BALANCE("Dodano środki, kwota: %.2f");

    private final String message;
    Message(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
