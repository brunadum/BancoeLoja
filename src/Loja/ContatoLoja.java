package Loja;

public class ContatoLoja {
    private final String telefone;
    private final String email;

    public ContatoLoja(String telefone, String email) {
        this.telefone = telefone;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Telefone: " + telefone + "\nE-mail: " + email;
    }
}
