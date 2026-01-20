package app;

import Banco.Banco;
import Banco.SistemaBancario;
import Loja.SistemaLoja;

import javax.swing.JOptionPane;

public class SistemaPrincipal {
    public static void main(String[] args) {
        Banco bancoCompartilhado = new Banco();

        while (true) {
            String op = JOptionPane.showInputDialog(
                    "MENU INICIAL\n\n" +
                            "1 - Agência Bancária\n" +
                            "2 - Loja de Varejo\n" +
                            "3 - Sair"
            );

            if (op == null || op.equals("3")) return;

            switch (op) {
                case "1":
                    SistemaBancario.executar(bancoCompartilhado);
                    break;
                case "2":
                    SistemaLoja.executar(bancoCompartilhado);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }
        }
    }
}

