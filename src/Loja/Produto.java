package Loja;

public class Produto {
    private int codigo;
    private String nome;
    private double precoVenda;
    private int estoque;

    public Produto(int codigo, String nome, double precoVenda, int estoque) {
        ValidacaoLoja.validarCodigoProduto(codigo);
        this.codigo = codigo;
        this.nome = nome;
        this.precoVenda = precoVenda;
        this.estoque = estoque;
    }

    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public double getPrecoVenda() { return precoVenda; }
    public int getEstoque() { return estoque; }

    public void setNome(String nome) { this.nome = nome; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public void adicionarEstoque(int qtd) { this.estoque += qtd; }

    public void baixarEstoque(int qtd) {
        if (qtd > estoque) throw new IllegalArgumentException("Estoque insuficiente.");
        this.estoque -= qtd;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | R$ %.2f | Estoque: %d", codigo, nome, precoVenda, estoque);
    }
}
