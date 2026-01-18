package Banco;

import java.util.ArrayList;

public class Banco {
    private ArrayList<ContaBancaria> contas = new ArrayList<>();
    private int proximoNumero = 2045;

    public ContaBancaria criarConta(String nome, String cpf, String email, String endereco, int senha) {
        Cliente cliente = new Cliente(nome, cpf, email, endereco, senha);
        ContaBancaria conta = new ContaBancaria(proximoNumero++, cliente);
        contas.add(conta);
        return conta;
    }

    public ContaBancaria buscarConta(int numero) {
        for (ContaBancaria c : contas) {
            if (c.getNumero() == numero) return c;
        }
        return null;
    }

    public ContaBancaria buscarContaAtiva(int numero) {
        ContaBancaria c = buscarConta(numero);
        if (c != null && c.isAtiva()) return c;
        return null;
    }

    // ✅ NOVO: usado pela Loja para verificar se existe pelo menos uma conta ativa
    public boolean existeAlgumaContaAtiva() {
        for (ContaBancaria c : contas) {
            if (c.isAtiva()) return true;
        }
        return false;
    }
}


