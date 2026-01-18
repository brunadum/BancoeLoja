package Loja;

public class ItemVenda {
    private final int codigo;
    private final String nome;
    private final double precoUnit;
    private final int qtd;

    public ItemVenda(Produto p, int qtd) {
        this.codigo = p.getCodigo();
        this.nome = p.getNome();
        this.precoUnit = p.getPrecoVenda();
        this.qtd = qtd;
    }

    public int getCodigo() {
        return codigo;
    }

    public int getQtd() {
        return qtd;
    }

    public double subtotal() {
        return precoUnit * qtd;
    }

    @Override
    public String toString() {
        return String.format("%dx %s (%d)  |  R$ %.2f", qtd, nome, codigo, subtotal());
    }
}

