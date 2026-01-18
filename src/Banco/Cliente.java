package Banco;

public class Cliente {
    private String nome;
    private String cpf;
    private String email;
    private String endereco;
    private int senha;

    public Cliente(String nome, String cpf, String email, String endereco, int senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.endereco = endereco;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }
    public int getSenha() {
        return senha;
    }

    @Override
    public String toString() {
        return "Nome: " + nome + "\nCPF: " + cpf + "\nE-mail: " + email + "\nEndereço: " + endereco;
    }
}
