package linx7a.service;

import linx7a.config.AccountProperties;
import linx7a.model.Account;
import linx7a.model.User;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountService {
    private final Map<Integer, Account> accounts = new LinkedHashMap<>();
    private int idCounter = 0;

    private final UserService userService;
    private final AccountProperties accountProperties;

    public AccountService(UserService userService, AccountProperties accountProperties) {
        this.userService = userService;
        this.accountProperties = accountProperties;
    }

    public Account findById(int accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("account with id=" + accountId + " doesn't exist");
        }
        return account;
    }

    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive, got " + amount);
        }
    }

    public Account createAccount(int userId) {
        User user = userService.findById(userId);
        idCounter++;
        Account account = new Account(idCounter, userId, accountProperties.getDefaultAmount());
        accounts.put(account.getId(), account);
        user.getAccountList().add(account);
        return account;
    }

    public Account deposit(int accountId, int amount) {
        Account account = findById(accountId);
        validateAmount(amount);
        account.setMoneyAmount(account.getMoneyAmount() + amount);
        return account;
    }

    public Account withdraw(int accountId, int amount) {
        Account account = findById(accountId);
        validateAmount(amount);
        if (account.getMoneyAmount() < amount) {
            throw new IllegalArgumentException("insufficient funds on account id=" + accountId
                    + ", moneyAmount=" + account.getMoneyAmount()  + ", attempted withdraw=" + amount);
        }
        account.setMoneyAmount(account.getMoneyAmount() - amount);
        return account;
    }

    public int transfer(int idAccountFrom, int idAccountTo, int amount) {
        validateAmount(amount);
        Account accountFrom = findById(idAccountFrom);
        Account accountTo = findById(idAccountTo);
        if (accountFrom == accountTo) {
            throw new IllegalArgumentException("source and target accounts must be different");
        }
        if (accountFrom.getMoneyAmount() < amount) {
            throw new IllegalArgumentException("insufficient funds on account id=" + idAccountFrom
                    + ", moneyAmount=" + accountFrom.getMoneyAmount()
                    + ", attempted transfer=" + amount);
        }
        int received = amount;
        if (accountFrom.getUserId() != accountTo.getUserId()) {
            received = (int) Math.round(amount * (1 - accountProperties.getTransferCommission()));
        }
        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + received);
        int commission = amount - received;

        return commission;
    }

    public Account closeAccount(int accountId) {
        Account account = findById(accountId);
        User user = userService.findById(account.getUserId());

        List<Account> allAccounts = user.getAccountList();
        if (allAccounts.size() == 1) {
            throw new IllegalArgumentException("cannot close the only account of user id=" + user.getId());
        }

        allAccounts.remove(account);
        accounts.remove(accountId);

        Account firstAccount = allAccounts.get(0);
        firstAccount.setMoneyAmount(firstAccount.getMoneyAmount() + account.getMoneyAmount());

        return firstAccount;
    }
}
