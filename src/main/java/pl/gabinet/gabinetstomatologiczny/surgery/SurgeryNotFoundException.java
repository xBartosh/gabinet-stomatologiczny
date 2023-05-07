package pl.gabinet.gabinetstomatologiczny.surgery;

public class SurgeryNotFoundException extends RuntimeException {
    SurgeryNotFoundException(){
        super("Surgery not found!");
    }
}
