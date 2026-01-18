package Loja;

import Banco.Banco;
import Banco.ContaBancaria;
import Banco.excecao.SaldoInsuficienteException;

import javax.swing.JOptionPane;
import java.util.List;

public class SistemaLoja {

    // Uma loja “global” para o sistema (fica viva enquanto o programa roda)
    private static final LojaVarejo loja = new LojaVarejo();

    // Entrada do módulo Loja (será chamada pelo SistemaPrincipal)
    public static void executar(Banco banco) {

        // Se ainda não cadastrou a loja, força cadastro antes de operar
        if (!loja.isCadastrada()) {
            int r = JOptionPane.showConfirmDialog(
                    null,
                    "Loja ainda não cadastrada.\nDeseja cadastrar agora?",
                    "Cadastro da Loja",
                    JOptionPane.YES_NO_OPTION
            );
            if (r != JOptionPane.YES_OPTION) return;

            cadastrarLoja(banco);
            if (!loja.isCadastrada()) return; // se cancelou no meio
        }

        while (true) {
            String op = JOptionPane.showInputDialog(
                    "MÓDULO LOJA DE VAREJO\n\n" +
                            "Status: " + loja.getStatusFormatado() + "\n" +
                            String.format("Caixa: R$ %.2f\n", loja.getCaixa()) +
                            (loja.temCompraPendente() ? "⚠ Compra pendente: SIM\n\n" : "\n") +
                            "1 - Entrar como Cliente\n" +
                            "2 - Entrar como Administrador\n" +
                            "3 - Ver dados da loja\n" +
                            "4 - Voltar"
            );

            if (op == null || op.equals("4")) return;

            try {
                switch (op) {
                    case "1":
                        menuCliente(banco);
                        break;
                    case "2":
                        menuAdmin(banco);
                        break;
                    case "3":
                        JOptionPane.showMessageDialog(null, loja.resumoCompleto());
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opção inválida.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
            }
        }
    }

    // ---------------- CADASTRO DA LOJA ----------------

    private static void cadastrarLoja(Banco banco) {
        String nome = inputObrigatorio("Nome da loja:");
        if (nome == null) return;

        String cnpj = inputObrigatorio("CNPJ (14 dígitos numéricos):");
        if (cnpj == null) return;
        if (!ValidacaoLoja.cnpjValido(cnpj)) {
            JOptionPane.showMessageDialog(null, "CNPJ inválido. Deve conter 14 dígitos numéricos.");
            return;
        }

        String cep = inputObrigatorio("CEP (8 dígitos numéricos):");
        if (cep == null) return;
        if (!ValidacaoLoja.cepValido(cep)) {
            JOptionPane.showMessageDialog(null, "CEP inválido. Deve conter 8 dígitos numéricos.");
            return;
        }

        String estado = inputObrigatorio("Estado:");
        if (estado == null) return;

        String cidade = inputObrigatorio("Cidade:");
        if (cidade == null) return;

        String bairro = inputObrigatorio("Bairro:");
        if (bairro == null) return;

        String rua = inputObrigatorio("Rua:");
        if (rua == null) return;

        String numero = inputObrigatorio("Número (até 5 dígitos):");
        if (numero == null) return;
        if (!ValidacaoLoja.numeroValido(numero)) {
            JOptionPane.showMessageDialog(null, "Número inválido. Use 1 a 5 dígitos numéricos.");
            return;
        }

        EnderecoLoja endereco = new EnderecoLoja(cep, estado, cidade, bairro, rua, numero);

        CategoriaLoja categoria = escolherCategoria();

        String telefone = inputObrigatorio("Telefone com DDD (apenas dígitos):");
        if (telefone == null) return;
        if (!ValidacaoLoja.telefoneValido(telefone)) {
            JOptionPane.showMessageDialog(null, "Telefone inválido. Use 10 ou 11 dígitos.");
            return;
        }

        String email = inputObrigatorio("E-mail:");
        if (email == null) return;

        ContatoLoja contato = new ContatoLoja(telefone, email);

        // Vincular conta bancária da loja (tem que existir e estar ativa)
        Integer numContaLoja = lerInt("Número da conta bancária da loja (precisa existir e estar ativa):");
        if (numContaLoja == null) return;

        ContaBancaria contaLoja = banco.buscarContaAtiva(numContaLoja);
        if (contaLoja == null) {
            JOptionPane.showMessageDialog(null, "Conta da loja não existe ou está inativa.");
            return;
        }

        loja.cadastrar(nome, cnpj, endereco, categoria, contato, contaLoja);

        JOptionPane.showMessageDialog(null, "Loja cadastrada com sucesso!\n\n" + loja.resumoCompleto());
    }

    private static CategoriaLoja escolherCategoria() {
        Object[] opcoes = {"Vestuário", "Eletrônicos", "Alimentos", "Outros"};
        int r = JOptionPane.showOptionDialog(
                null,
                "Categoria da loja:",
                "Categoria",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        switch (r) {
            case 0: return CategoriaLoja.VESTUARIO;
            case 1: return CategoriaLoja.ELETRONICOS;
            case 2: return CategoriaLoja.ALIMENTOS;
            default: return CategoriaLoja.OUTROS;
        }
    }

    // ---------------- MENU ADMIN ----------------

    private static void menuAdmin(Banco banco) {
        while (true) {
            String op = JOptionPane.showInputDialog(
                    "ADMINISTRADOR - LOJA\n\n" +
                            "Status: " + loja.getStatusFormatado() + "\n" +
                            String.format("Caixa: R$ %.2f\n\n", loja.getCaixa()) +
                            "1 - Cadastrar produto\n" +
                            "2 - Editar produto\n" +
                            "3 - Excluir produto\n" +
                            "4 - Comprar estoque (Fornecedor)\n" +
                            "5 - Finalizar compra pendente\n" +
                            "6 - Ver histórico de vendas\n" +
                            "7 - Alterar status da loja\n" +
                            "8 - Voltar"
            );

            if (op == null || op.equals("8")) return;

            try {
                switch (op) {
                    case "1": adminCadastrarProduto(); break;
                    case "2": adminEditarProduto(); break;
                    case "3": adminExcluirProduto(); break;
                    case "4": adminCompraFornecedor(banco); break;
                    case "5":
                        loja.finalizarCompraPendente();
                        JOptionPane.showMessageDialog(null, "Compra pendente finalizada!");
                        break;
                    case "6": mostrarHistorico(); break;
                    case "7": alterarStatus(); break;
                    default: JOptionPane.showMessageDialog(null, "Opção inválida.");
                }
            } catch (SaldoInsuficienteException e) {
                JOptionPane.showMessageDialog(null, "Saldo insuficiente!\n" + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private static void adminCadastrarProduto() {
        int codigo = pedirCodigoProduto();
        String nome = inputObrigatorio("Nome do produto:");
        if (nome == null) return;

        Double preco = lerDouble("Preço de venda:");
        if (preco == null || preco <= 0) return;

        Integer estoque = lerInt("Estoque inicial:");
        if (estoque == null || estoque < 0) return;

        loja.cadastrarProduto(new Produto(codigo, nome, preco, estoque));
        JOptionPane.showMessageDialog(null, "Produto cadastrado!");
    }

    private static void adminEditarProduto() {
        int codigo = pedirCodigoProduto();
        Produto p = loja.buscarProduto(codigo);
        if (p == null) throw new IllegalArgumentException("Produto não encontrado.");

        String novoNome = JOptionPane.showInputDialog("Novo nome (atual: " + p.getNome() + "):");
        if (novoNome != null && !novoNome.isBlank()) p.setNome(novoNome);

        String sPreco = JOptionPane.showInputDialog("Novo preço (atual: " + p.getPrecoVenda() + "):");
        if (sPreco != null && !sPreco.isBlank()) {
            double novoPreco = Double.parseDouble(sPreco.replace(",", "."));
            if (novoPreco <= 0) throw new IllegalArgumentException("Preço inválido.");
            p.setPrecoVenda(novoPreco);
        }

        JOptionPane.showMessageDialog(null, "Produto atualizado:\n" + p);
    }

    private static void adminExcluirProduto() {
        int codigo = pedirCodigoProduto();
        loja.excluirProduto(codigo);
        JOptionPane.showMessageDialog(null, "Produto excluído!");
    }

    private static void adminCompraFornecedor(Banco banco) {
        // regra: loja bloqueada não opera
        loja.validarOperacaoPermitida();

        String nota = inputObrigatorio("Número da nota fiscal do fornecedor:");
        if (nota == null) return;

        CompraFornecedor compra = new CompraFornecedor(nota);

        // loop para adicionar itens na nota
        while (true) {
            String sCod = JOptionPane.showInputDialog("Código do produto (4 dígitos) ou cancelar para terminar a nota:");
            if (sCod == null) break;

            int cod = Integer.parseInt(sCod);
            ValidacaoLoja.validarCodigoProduto(cod);

            String nome = inputObrigatorio("Nome do produto:");
            if (nome == null) return;

            Integer qtd = lerInt("Quantidade:");
            if (qtd == null || qtd <= 0) throw new IllegalArgumentException("Quantidade inválida.");

            Double valorCompra = lerDouble("Valor de compra UNITÁRIO:");
            if (valorCompra == null || valorCompra <= 0) throw new IllegalArgumentException("Valor inválido.");

            compra.adicionarItem(new ItemCompraFornecedor(cod, nome, qtd, valorCompra));

            int r = JOptionPane.showConfirmDialog(null, "Adicionar mais itens nessa nota?", "Fornecedor", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) break;
        }

        if (compra.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum item informado na nota.");
            return;
        }

        JOptionPane.showMessageDialog(null,
                "Total da nota: R$ " + String.format("%.2f", compra.totalCompra()) + "\n" +
                        "Preço de venda será +30% do valor de compra unitário."
        );

        loja.processarCompraFornecedor(compra);
        JOptionPane.showMessageDialog(null, "Compra registrada no estoque!");
    }

    private static void mostrarHistorico() {
        List<Venda> vendas = loja.getHistoricoVendas();
        if (vendas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Sem vendas ainda.");
            return;
        }
        StringBuilder sb = new StringBuilder("HISTÓRICO DE VENDAS\n\n");
        for (Venda v : vendas) {
            sb.append("---------------------------------\n")
                    .append(v.resumo()).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static void alterarStatus() {
        Object[] opcoes = {"ATIVA", "INATIVA", "PENDENTE", "BLOQUEADA"};
        int r = JOptionPane.showOptionDialog(
                null,
                "Escolha o novo status:",
                "Status da Loja",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );
        if (r < 0) return;

        StatusLojaTipo tipo = StatusLojaTipo.valueOf(opcoes[r].toString());
        String motivo = null;

        if (tipo == StatusLojaTipo.INATIVA || tipo == StatusLojaTipo.PENDENTE) {
            motivo = inputObrigatorio("Informe o motivo:");
            if (motivo == null) return;
        }

        loja.setStatus(tipo, motivo);
        JOptionPane.showMessageDialog(null, "Status atualizado: " + loja.getStatusFormatado());
    }

    // ---------------- MENU CLIENTE ----------------

    private static void menuCliente(Banco banco) {
        loja.validarOperacaoPermitida(); // bloqueada não vende

        Integer contaCliente = lerInt("Informe o número da sua conta bancária (obrigatório):");
        if (contaCliente == null) return;

        // valida conta existe e está ativa
        ContaBancaria conta = banco.buscarContaAtiva(contaCliente);
        if (conta == null) {
            JOptionPane.showMessageDialog(null, "Conta inexistente ou INATIVA. Compra bloqueada.");
            return;
        }

        while (true) {
            String op = JOptionPane.showInputDialog(
                    "CLIENTE - LOJA\n\n" +
                            "1 - Listar produtos disponíveis\n" +
                            "2 - Comprar produto\n" +
                            "3 - Voltar"
            );

            if (op == null || op.equals("3")) return;

            try {
                switch (op) {
                    case "1":
                        listarProdutos();
                        break;
                    case "2":
                        comprar(banco, contaCliente);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opção inválida.");
                }
            } catch (SaldoInsuficienteException e) {
                JOptionPane.showMessageDialog(null, "Saldo insuficiente!\n" + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private static void listarProdutos() {
        List<Produto> produtos = loja.listarProdutos();
        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto disponível.");
            return;
        }

        StringBuilder sb = new StringBuilder("PRODUTOS DISPONÍVEIS\n\n");
        for (Produto p : produtos) sb.append("• ").append(p).append("\n");
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static void comprar(Banco banco, int contaCliente) {
        loja.validarOperacaoPermitida();

        if (loja.listarProdutos().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há produtos cadastrados.");
            return;
        }

        Venda venda = new Venda(contaCliente);

        while (true) {
            int codigo = pedirCodigoProduto();
            Produto p = loja.buscarProduto(codigo);
            if (p == null) {
                JOptionPane.showMessageDialog(null, "Produto não encontrado.");
                continue;
            }

            Integer qtd = lerInt("Quantidade:");
            if (qtd == null) return;
            if (qtd <= 0) throw new IllegalArgumentException("Quantidade inválida.");
            if (qtd > p.getEstoque()) throw new IllegalArgumentException("Estoque insuficiente.");

            venda.adicionarItem(p, qtd);

            int r = JOptionPane.showConfirmDialog(null, "Adicionar mais itens?", "Carrinho", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) break;
        }

        if (venda.getTotal() <= 0) {
            JOptionPane.showMessageDialog(null, "Compra cancelada (nenhum item).");
            return;
        }

        int confirma = JOptionPane.showConfirmDialog(null,
                venda.resumo() + "\n\nConfirmar compra?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirma != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "Compra cancelada.");
            return;
        }

        // baixa estoque + integra com banco (debita cliente e credita loja)
        loja.realizarVenda(banco, venda);

        JOptionPane.showMessageDialog(null,
                "Compra realizada!\n" + String.format("Total: R$ %.2f", venda.getTotal()));
    }

    // ---------------- Helpers ----------------

    private static int pedirCodigoProduto() {
        Integer cod = lerInt("Código do produto (4 dígitos):");
        if (cod == null) throw new IllegalArgumentException("Operação cancelada.");
        ValidacaoLoja.validarCodigoProduto(cod);
        return cod;
    }

    private static String inputObrigatorio(String msg) {
        String s = JOptionPane.showInputDialog(msg);
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        return s;
    }

    private static Integer lerInt(String msg) {
        try {
            String s = JOptionPane.showInputDialog(msg);
            if (s == null) return null;
            s = s.trim();
            if (s.isEmpty()) return null;
            return Integer.parseInt(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Valor inválido. Digite apenas números.");
            return null;
        }
    }

    private static Double lerDouble(String msg) {
        try {
            String s = JOptionPane.showInputDialog(msg);
            if (s == null) return null;
            s = s.trim().replace(",", ".");
            if (s.isEmpty()) return null;
            return Double.parseDouble(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Valor inválido. Ex: 10.50");
            return null;
        }
    }
}
