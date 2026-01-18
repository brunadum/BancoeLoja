package Loja;

public class EnderecoLoja {
    private final String cep;
    private final String estado;
    private final String cidade;
    private final String bairro;
    private final String rua;
    private final String numero;

    public EnderecoLoja(String cep, String estado, String cidade, String bairro, String rua, String numero) {
        this.cep = cep;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "CEP: " + cep +
                "\nEstado: " + estado +
                "\nCidade: " + cidade +
                "\nBairro: " + bairro +
                "\nRua: " + rua +
                "\nNúmero: " + numero;
    }
}
