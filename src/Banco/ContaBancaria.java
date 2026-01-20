package Banco;

import Banco.excecao.SaldoInsuficienteException;

import java.util.ArrayList;
import java.util.List;

public class ContaBancaria {
    private int numero;
    private double saldo;
    private Cliente titular;

    private boolean ativa = true;

    private final List<Lancamento> extrato = new ArrayList<>();

    public ContaBancaria(int numero, Cliente titular) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = 0.0;
        registrar("ABERTURA", "Conta criada. Saldo inicial", 0);
    }

    public int getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getTitular() {
        return titular;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void ativar() {
        ativa = true;
        registrar("STATUS", "Conta ativada.", 0);
    }

    public void desativar(String motivo) {
        ativa = false;
        registrar("STATUS", "Conta desativada. Motivo: " + motivo, 0);
    }

    public List<Lancamento> getExtrato() {
        return extrato;
    }

    private void registrar(String tipo, String descricao, double valor) {
        extrato.add(new Lancamento(tipo, descricao, valor));
    }

    private void validarAtiva() {
        if (!ativa) throw new IllegalStateException("Conta inativa.");
    }

    public void depositar(double valor) {
        validarAtiva();
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido.");
        saldo += valor;
        registrar("DEPÓSITO", String.format("Saldo após: R$ %.2f", saldo), +valor);
    }

    public void sacar(double valor) {
        validarAtiva();
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido.");
        if (valor > saldo) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo R$ %.2f insuficiente para sacar R$ %.2f.", saldo, valor)
            );
        }
        saldo -= valor;
        registrar("SAQUE", String.format("Saldo após: R$ %.2f", saldo), -valor);
    }

    public void transferir(ContaBancaria destino, double valor) {
        validarAtiva();
        if (destino == null) throw new IllegalArgumentException("Conta destino inválida.");
        if (!destino.isAtiva()) throw new IllegalStateException("Conta destino inativa.");
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido.");

        if (valor > this.saldo) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo R$ %.2f insuficiente para transferir R$ %.2f.", saldo, valor)
            );
        }

        this.saldo -= valor;
        destino.saldo += valor;

        registrar("TRANSFERÊNCIA",
                "Enviada para conta " + destino.numero + String.format(" | Saldo após: R$ %.2f", this.saldo),
                -valor);

        destino.registrar("TRANSFERÊNCIA",
                "Recebida da conta " + this.numero + String.format(" | Saldo após: R$ %.2f", destino.saldo),
                +valor);
    }

    @Override
    public String toString() {
        return titular +
                "\nConta: " + numero +
                "\nStatus: " + (ativa ? "ATIVA" : "INATIVA") +
                String.format("\nSaldo: R$ %.2f", saldo);
    }
}




