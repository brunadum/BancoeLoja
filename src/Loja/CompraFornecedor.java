package Loja;

import java.util.ArrayList;
import java.util.List;

public class CompraFornecedor {
    private final String notaFiscal;
    private final List<ItemCompraFornecedor> itens = new ArrayList<>();

    public CompraFornecedor(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public void adicionarItem(ItemCompraFornecedor item) {
        itens.add(item);
    }

    public List<ItemCompraFornecedor> getItens() {
        return itens;
    }

    public double totalCompra() {
        double total = 0;
        for (ItemCompraFornecedor i : itens) {
            total += i.getQtd() * i.getValorCompraUnit();
        }
        return total;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }
}
