package Loja;

import Banco.Banco;
import Banco.ContaBancaria;

import java.util.ArrayList;
import java.util.List;

public class LojaVarejo {

    private boolean cadastrada = false;

    private String nome;
    private String cnpj;
    private EnderecoLoja endereco;
    private CategoriaLoja categoria;
    private ContatoLoja contato;

    private StatusLojaTipo status = StatusLojaTipo.ATIVA;
    private String motivoStatus = null;

    private CompraFornecedor compraFornecedorPendente = null;


    private double caixa = 0.0;

    private ContaBancaria contaLoja;

    private final List<Produto> produtos = new ArrayList<>();
    private final List<Venda> historicoVendas = new ArrayList<>();

    private boolean compraPendente = false;

    private int operacoesComCaixaZerado = 0;

    public boolean isCadastrada() {
        return cadastrada;
    }

    public void cadastrar(String nome, String cnpj, EnderecoLoja endereco, CategoriaLoja categoria, ContatoLoja contato, ContaBancaria contaLoja) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.categoria = categoria;
        this.contato = contato;
        this.contaLoja = contaLoja;
        this.cadastrada = true;
    }

    public double getCaixa() {
        return caixa;
    }

    public boolean temCompraPendente() {
        return compraPendente;
    }

    public void finalizarCompraPendente() {
        if (!compraPendente || compraFornecedorPendente == null) {
            throw new IllegalStateException("Não existe compra pendente para finalizar.");
        }

        double total = compraFornecedorPendente.totalCompra();

        if (caixa < total) {
            throw new IllegalStateException(
                    "Ainda não há caixa suficiente para finalizar.\n" +
                            String.format("Total pendente: R$ %.2f | Caixa atual: R$ %.2f", total, caixa)
            );
        }

        caixa -= total;

        for (ItemCompraFornecedor i : compraFornecedorPendente.getItens()) {
            Produto p = buscarProduto(i.getCodigo());
            double precoVenda = i.getValorCompraUnit() * 1.30;

            if (p == null) {
                p = new Produto(i.getCodigo(), i.getNome(), precoVenda, 0);
                produtos.add(p);
            } else {
                p.setPrecoVenda(precoVenda);
            }

            p.adicionarEstoque(i.getQtd());
        }

        compraFornecedorPendente = null;
        compraPendente = false;

        status = StatusLojaTipo.ATIVA;
        motivoStatus = null;
    }


    public List<Venda> getHistoricoVendas() {
        return historicoVendas;
    }

    public String getStatusFormatado() {
        if (status == StatusLojaTipo.INATIVA || status == StatusLojaTipo.PENDENTE) {
            return status + " (" + motivoStatus + ")";
        }
        return status.toString();
    }

    public void setStatus(StatusLojaTipo novo, String motivo) {
        this.status = novo;
        this.motivoStatus = motivo;
    }

    public void validarOperacaoPermitida() {
        if (status == StatusLojaTipo.BLOQUEADA) throw new IllegalStateException("Loja BLOQUEADA.");
        if (status == StatusLojaTipo.INATIVA) throw new IllegalStateException("Loja INATIVA: " + motivoStatus);
    }

    public String resumoCompleto() {
        if (!cadastrada) return "Loja não cadastrada.";

        return "LOJA: " + nome +
                "\nCNPJ: " + cnpj +
                "\nCategoria: " + categoria +
                "\n\nENDEREÇO:\n" + endereco +
                "\n\nCONTATO:\n" + contato +
                "\n\nCONTA DA LOJA: " + contaLoja.getNumero() +
                "\nSTATUS: " + getStatusFormatado() +
                String.format("\nCAIXA: R$ %.2f", caixa);
    }

    public void cadastrarProduto(Produto p) {
        if (buscarProduto(p.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe produto com esse código.");
        }
        produtos.add(p);
    }

    public Produto buscarProduto(int codigo) {
        for (Produto p : produtos) {
            if (p.getCodigo() == codigo) return p;
        }
        return null;
    }

    public void excluirProduto(int codigo) {
        Produto p = buscarProduto(codigo);
        if (p == null) throw new IllegalArgumentException("Produto não encontrado.");
        produtos.remove(p);
    }

    public List<Produto> listarProdutos() {
        List<Produto> disp = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getEstoque() > 0) disp.add(p);
        }
        return disp;
    }

    public void processarCompraFornecedor(CompraFornecedor compra) {
        if (status == StatusLojaTipo.BLOQUEADA) throw new IllegalStateException("Loja BLOQUEADA.");
        if (status == StatusLojaTipo.INATIVA) throw new IllegalStateException("Loja INATIVA: " + motivoStatus);

        double totalNota = compra.totalCompra();

        boolean primeiraNota = produtos.isEmpty() && historicoVendas.isEmpty() && caixa == 0.0;

        if (!primeiraNota) {
            if (caixa < totalNota) {
                compraFornecedorPendente = compra;
                compraPendente = true;
                status = StatusLojaTipo.PENDENTE;
                motivoStatus = "Sem caixa para pagar fornecedor. Vender produtos para entrar dinheiro e finalizar.";

                throw new IllegalStateException(
                        "Caixa insuficiente para pagar a nota.\n" +
                                "Compra marcada como PENDENTE.\n" +
                                String.format("Total da nota: R$ %.2f | Caixa atual: R$ %.2f", totalNota, caixa)
                );
            }

            caixa -= totalNota;
        }

        for (ItemCompraFornecedor i : compra.getItens()) {
            Produto p = buscarProduto(i.getCodigo());
            double precoVenda = i.getValorCompraUnit() * 1.30;

            if (p == null) {
                p = new Produto(i.getCodigo(), i.getNome(), precoVenda, 0);
                produtos.add(p);
            } else {
                p.setPrecoVenda(precoVenda);
            }

            p.adicionarEstoque(i.getQtd());
        }

        compraFornecedorPendente = null;
        compraPendente = false;
        if (status == StatusLojaTipo.PENDENTE) {
            status = StatusLojaTipo.ATIVA;
            motivoStatus = null;
        }
    }

    public void realizarVenda(Banco banco, Venda venda) {
        validarOperacaoPermitida();

        ContaBancaria contaCliente = banco.buscarContaAtiva(venda.getContaCliente());
        if (contaCliente == null) throw new IllegalStateException("Conta do cliente inexistente ou INATIVA.");

        if (contaLoja == null || !contaLoja.isAtiva()) {
            throw new IllegalStateException("Conta da loja inexistente ou INATIVA.");
        }

        for (ItemVenda item : venda.getItens()) {
            Produto p = buscarProduto(item.getCodigo());
            if (p == null) throw new IllegalStateException("Produto não encontrado (código " + item.getCodigo() + ").");
            if (item.getQtd() > p.getEstoque()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto " + p.getNome() + ".");
            }
        }

        contaCliente.transferir(contaLoja, venda.getTotal());

        for (ItemVenda item : venda.getItens()) {
            Produto p = buscarProduto(item.getCodigo());
            p.baixarEstoque(item.getQtd());
        }

        caixa += venda.getTotal();
        historicoVendas.add(venda);

        if (caixa == 0.0) {
            operacoesComCaixaZerado++;
            if (operacoesComCaixaZerado >= 3) {
                status = StatusLojaTipo.BLOQUEADA;
                motivoStatus = null;
            }
        } else {
            operacoesComCaixaZerado = 0;
        }
    }
}

