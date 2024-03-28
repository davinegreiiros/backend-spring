package br.com.davinegreiros.picpaydesafiobackend.wallet;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public record Wallet(
    @Id long id,
    String fullName,
    String cpf,
    String email,
    String password,
    int type,
    BigDecimal balance) {
}
