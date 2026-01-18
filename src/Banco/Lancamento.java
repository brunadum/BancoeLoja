package Banco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Lancamento {
    private final LocalDateTime dataHora;
    private final String tipo;      // "DEPÓSITO", "SAQUE", "TRANSFERÊNCIA", etc.
    private final String descricao; // texto livre
    private final double valor;     // pode ser + ou -

    public Lancamento(String tipo, String descricao, double valor) {
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
    }

    public String formatar() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String sinal = (valor >= 0) ? "+" : "-";
        double abs = Math.abs(valor);

        return String.format("[%s] %-15s %sR$ %.2f  |  %s",
                dataHora.format(fmt),
                tipo,
                sinal,
                abs,
                descricao
        );
    }
}

