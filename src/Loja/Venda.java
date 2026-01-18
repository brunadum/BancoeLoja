package Loja;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private final int contaCliente;
    private final LocalDateTime dataHora = LocalDateTime.now();
    private final List<ItemVenda> itens = new ArrayList<>();
    private double total = 0;

    public Venda(int contaCliente) {
        this.contaCliente = contaCliente;
    }

    public void adicionarItem(Produto p, int qtd) {
        ItemVenda item = new ItemVenda(p, qtd);
        itens.add(item);
        total += item.subtotal();
    }

    public double getTotal() { return total; }
    public int getContaCliente() { return contaCliente; }
    public List<ItemVenda> getItens() { return itens; }

    public String resumo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("Venda em ").append(dataHora.format(fmt)).append("\n");
        sb.append("Conta do cliente: ").append(contaCliente).append("\n\n");
        for (ItemVenda i : itens) sb.append("• ").append(i).append("\n");
        sb.append("\nTotal: ").append(String.format("R$ %.2f", total));
        return sb.toString();
    }
}
