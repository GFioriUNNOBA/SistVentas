package ar.edu.unnoba.ssaaii.SistVentas.exeption;


public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
