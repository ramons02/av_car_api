package br.edu.senai.fatesg.avcar.core.validations;

import br.edu.senai.fatesg.avcar.core.exceptions.NegocioException;

/**
 * <b>Validador Genérico</b>
 * <p>
 * Aplica o princípio DRY (Don't Repeat Yourself) ao centralizar as regras de negócio
 * e sanitização comuns (CPF, CNPJ, Placa, Telefones). Impede repetição de código
 * de validação nos services das diversas entidades de negócio do sistema.
 * </p>
 * 
 * @param <T> Tipo da entidade que será validada
 */
public abstract class GenericValidation<T> implements Validator<T> {

    @Override
    public abstract void validar(T entity) throws NegocioException;

    protected void naoNulo(Object valor, String campo) {
        if (valor == null) throw new NegocioException(campo + " é obrigatório");
    }

    protected void naoVazio(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new NegocioException(campo + " é obrigatório");
    }

    protected void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank())
            throw new NegocioException("CPF é obrigatório");
        if (!cpf.matches("\\d{11}"))
            throw new NegocioException("CPF deve ter 11 dígitos");
    }

    protected void validarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank())
            throw new NegocioException("CNPJ é obrigatório");
        if (!cnpj.matches("\\d{14}"))
            throw new NegocioException("CNPJ deve ter 14 dígitos");
    }

    protected void validarPlaca(String placa) {
        if (placa == null || placa.isBlank())
            throw new NegocioException("Placa é obrigatória");
        if (!placa.matches("[A-Z]{3}\\d{1}[A-Z0-9]{1}\\d{2}"))
            throw new NegocioException("Placa deve seguir o formato Mercosul (ABC1D23)");
    }

    protected void validarTelefone(String telefone) {
        if (telefone != null && !telefone.isBlank()) {
            String digits = telefone.replaceAll("\\D", "");
            if (digits.length() < 10 || digits.length() > 11)
                throw new NegocioException("Telefone deve ter 10 ou 11 dígitos");
        }
    }

    protected void validarEmail(String email) {
        if (email != null && !email.isBlank()) {
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$"))
                throw new NegocioException("E-mail inválido");
        }
    }
}
