package Loja;

public class ValidacaoLoja {

    public static boolean soDigitos(String s) {
        if (s == null) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    public static boolean cnpjValido(String cnpj) {
        return cnpj != null && cnpj.length() == 14 && soDigitos(cnpj);
    }

    public static boolean cepValido(String cep) {
        return cep != null && cep.length() == 8 && soDigitos(cep);
    }

    public static boolean numeroValido(String numero) {
        return numero != null && numero.length() >= 1 && numero.length() <= 5 && soDigitos(numero);
    }

    public static boolean telefoneValido(String tel) {
        // com DDD: 10 (fixo) ou 11 (celular)
        return tel != null && (tel.length() == 10 || tel.length() == 11) && soDigitos(tel);
    }

    public static void validarCodigoProduto(int codigo) {
        if (codigo < 1000 || codigo > 9999) {
            throw new IllegalArgumentException("Código inválido. Use 4 dígitos (1000 a 9999).");
        }
    }
}

