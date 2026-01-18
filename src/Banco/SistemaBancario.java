package Banco;

import Banco.excecao.SaldoInsuficienteException;

import javax.swing.JOptionPane;

public class SistemaBancario {

    public static void main(String[] args) {
        executar(new Banco());
    }

    public static void executar(Banco banco) {
        while (true) {
            String op = JOptionPane.showInputDialog(
                    "AGÊNCIA BANCÁRIA\n\n" +
                            "1 - Gerente (criar conta)\n" +
                            "2 - Pessoa Física (acessar conta)\n" +
                            "3 - Voltar"
            );

            if (op == null || op.equals("3")) return;

            switch (op) {
                case "1":
                    menuGerente(banco);
                    break;
                case "2":
                    menuCliente(banco);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }
        }
    }

    private static void menuGerente(Banco banco) {
        String nome = JOptionPane.showInputDialog("Nome do cliente:");
        if (nome == null) return;

        String cpf = JOptionPane.showInputDialog("CPF:");
        if (cpf == null) return;

        String email = JOptionPane.showInputDialog("E-mail:");
        if (email == null) return;

        String endereco = JOptionPane.showInputDialog("Endereço:");
        if (endereco == null) return;

        int senha;
        try {
            String sSenha = JOptionPane.showInputDialog("Crie uma senha numérica:");
            if (sSenha == null) return;
            senha = Integer.parseInt(sSenha);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Senha inválida. Digite apenas números.");
            return;
        }

        ContaBancaria conta = banco.criarConta(nome, cpf, email, endereco, senha);
        JOptionPane.showMessageDialog(null,
                "Conta criada com sucesso!\nNúmero: " + conta.getNumero());
    }

    private static void menuCliente(Banco banco) {
        Integer numero = lerInt("Número da conta:");
        if (numero == null) return;

        ContaBancaria conta = banco.buscarContaAtiva(numero);
        if (conta == null) {
            JOptionPane.showMessageDialog(null, "Conta não encontrada ou INATIVA.");
            return;
        }

        Integer senha = lerInt("Senha:");
        if (senha == null) return;

        if (senha != conta.getTitular().getSenha()) {
            JOptionPane.showMessageDialog(null, "Senha incorreta.");
            return;
        }

        while (true) {
            String op = JOptionPane.showInputDialog(
                    "CONTA " + conta.getNumero() + " - " + conta.getTitular().getNome() + "\n\n" +
                            "1 - Depositar\n" +
                            "2 - Sacar\n" +
                            "3 - Transferir\n" +
                            "4 - Ver dados da conta\n" +
                            "5 - Ver extrato\n" +
                            "6 - Voltar"
            );

            if (op == null || op.equals("6")) return;

            try {
                switch (op) {
                    case "1": {
                        Double dep = lerDouble("Valor do depósito:");
                        if (dep == null) break;
                        conta.depositar(dep);
                        JOptionPane.showMessageDialog(null,
                                "Depósito realizado!\nSaldo: R$ " + String.format("%.2f", conta.getSaldo()));
                        break;
                    }

                    case "2": {
                        Double saq = lerDouble("Valor do saque:");
                        if (saq == null) break;
                        conta.sacar(saq);
                        JOptionPane.showMessageDialog(null,
                                "Saque realizado!\nSaldo: R$ " + String.format("%.2f", conta.getSaldo()));
                        break;
                    }

                    case "3": {
                        Integer destino = lerInt("Conta destino:");
                        if (destino == null) break;

                        ContaBancaria contaDestino = banco.buscarContaAtiva(destino);
                        if (contaDestino == null) {
                            JOptionPane.showMessageDialog(null, "Conta destino não encontrada ou INATIVA.");
                            break;
                        }

                        Double valor = lerDouble("Valor da transferência:");
                        if (valor == null) break;

                        conta.transferir(contaDestino, valor);

                        JOptionPane.showMessageDialog(null,
                                "Transferência realizada!\nSaldo: R$ " + String.format("%.2f", conta.getSaldo()));
                        break;
                    }

                    case "4":
                        JOptionPane.showMessageDialog(null, conta.toString());
                        break;

                    case "5":
                        mostrarExtrato(conta);
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

    private static void mostrarExtrato(ContaBancaria conta) {
        StringBuilder sb = new StringBuilder("Extrato - Conta " + conta.getNumero() + "\n\n");

        if (conta.getExtrato().isEmpty()) {
            sb.append("Sem movimentações.");
        } else {
            for (Lancamento l : conta.getExtrato()) {
                sb.append("• ").append(l.formatar()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(null, sb.toString());
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



