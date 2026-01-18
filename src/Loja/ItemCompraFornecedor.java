package Loja;

public class ItemCompraFornecedor {
    private final int codigo;
    private final String nome;
    private final int qtd;
    private final double valorCompraUnit;

    public ItemCompraFornecedor(int codigo, String nome, int qtd, double valorCompraUnit) {
        ValidacaoLoja.validarCodigoProduto(codigo);
        this.codigo = codigo;
        this.nome = nome;
        this.qtd = qtd;
        this.valorCompraUnit = valorCompraUnit;
    }

    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getQtd() { return qtd; }
    public double getValorCompraUnit() { return valorCompraUnit; }
}
