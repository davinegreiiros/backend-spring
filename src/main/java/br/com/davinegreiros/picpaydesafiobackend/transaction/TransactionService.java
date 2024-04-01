package br.com.davinegreiros.picpaydesafiobackend.transaction;

import org.springframework.stereotype.Service;

import br.com.davinegreiros.picpaydesafiobackend.wallet.Wallet;
import br.com.davinegreiros.picpaydesafiobackend.wallet.WalletRepository;
import br.com.davinegreiros.picpaydesafiobackend.wallet.WalletType;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final WalletRepository walletRepository;

  public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
    this.transactionRepository = transactionRepository;
    this.walletRepository = walletRepository;
  }

  public Transaction create(Transaction transaction) {
    // 1- validar
    validate(transaction);

    // 2 - criar transação
    var newTransaction = transactionRepository.save(transaction);

    // 3 - debitar da carteira
    var wallet = walletRepository.findById(transaction.payer()).get();
    walletRepository.save(wallet.debit(transaction.value()));

    // 4 - chamar serviços externos

    return newTransaction;
  }

  /*
   * - pagador tem carteira do tipo COMUM
   * - se o pagador tem saldo suficiente
   * - pagador não pode ser o recebedor
   */
  @SuppressWarnings("null")
  private void validate(Transaction transaction) {
    walletRepository.findById(transaction.payee())
        .map(payee -> walletRepository.findById(transaction.payer())
            .map(payer -> isTransacitonValid(transaction, payer) ? transaction : null));
  }

  private boolean isTransacitonValid(Transaction transaction, Wallet payer) {
    return payer.type() == WalletType.COMUM.getValue() &&
        payer.balance().compareTo(transaction.value()) >= 0 &&
        !payer.id().equals(Long.valueOf(transaction.payee()));
  }
}