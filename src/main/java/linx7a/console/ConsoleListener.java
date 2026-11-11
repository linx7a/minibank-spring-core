package linx7a.console;

import linx7a.model.Account;
import linx7a.model.User;
import linx7a.service.AccountService;
import linx7a.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleListener {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final AccountService accountService;

    public ConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void start() {
        System.out.println("MiniBank started. Type EXIT to stop.");
        System.out.println("Available commands: USER_CREATE, SHOW_ALL_USERS, ACCOUNT_CREATE, "
                + "ACCOUNT_DEPOSIT, ACCOUNT_WITHDRAW, ACCOUNT_TRANSFER, ACCOUNT_CLOSE, EXIT");

        while (true) {
            System.out.println("Enter command:");
            String command = scanner.nextLine().toUpperCase().trim();
            if (command.equals("EXIT")) {
                break;
            }
            try {
                switch (command) {
                    case "USER_CREATE" -> createUser();
                    case "SHOW_ALL_USERS" -> showAllUsers();
                    case "ACCOUNT_CREATE" -> createAccount();
                    case "ACCOUNT_DEPOSIT" -> deposit();
                    case "ACCOUNT_WITHDRAW" -> withdraw();
                    case "ACCOUNT_TRANSFER" -> transfer();
                    case "ACCOUNT_CLOSE" -> closeAccount();
                    default -> System.out.println("Error: unknown command " + command);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("MiniBank stopped.");
    }

    private void createUser() {
        System.out.println("Enter login:");
        String login = scanner.nextLine().trim();
        User user = userService.createUser(login);
        accountService.createAccount(user.getId());
        System.out.println("User created: " + user);
    }

    private void showAllUsers() {
        for (User user : userService.getAllUsers()) {
            System.out.println(user);
        }
    }

    private void createAccount() {
        int userId = readInt("Enter user id:");
        Account account = accountService.createAccount(userId);
        System.out.println("Account created: " + account);
    }

    private void deposit() {
        int accountId = readInt("Enter account id:");
        int amount = readInt("Enter amount:");
        Account account = accountService.deposit(accountId, amount);
        System.out.println("Deposited " + amount + " to account " + accountId + ". New balance: " + account.getMoneyAmount());
    }

    private void withdraw() {
        int accountId = readInt("Enter account id:");
        int amount = readInt("Enter amount:");
        Account account = accountService.withdraw(accountId, amount);
        System.out.println("Withdrew " + amount + " from account " + accountId
                + ". New balance: " + account.getMoneyAmount());
    }

    private void transfer() {
        int accountIdFrom = readInt("Enter source account id:");
        int accountIdTo = readInt("Enter target account id:");
        int amount = readInt("Enter amount:");

        int commission = accountService.transfer(accountIdFrom, accountIdTo, amount);

        boolean sameUser = accountService.findById(accountIdFrom).getUserId()
                == accountService.findById(accountIdTo).getUserId();
        if (sameUser) {
            System.out.println("Transfer completed from account " + accountIdFrom +
                    " to account " + accountIdTo + ". Amount: " + amount + " (no commission, same user)");
        } else {
            System.out.println("Transfer completed from account " + accountIdFrom +
                    " to account " + accountIdTo + ". Amount: " + amount + ", commission: " + commission + ", recipient received: " + (amount - commission));
        }
    }

    private void closeAccount() {
        int accountId = readInt("Enter account id to close:");
        int balance = accountService.findById(accountId).getMoneyAmount();
        Account firstAccount = accountService.closeAccount(accountId);
        System.out.println("Account " + accountId + " closed. Remaining balance "
                + balance + " transferred to account " + firstAccount.getId() + ".");
    }

    private int readInt(String line) {
        System.out.println(line);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("please enter a valid number, got '" + input + "'");
        }
    }
}
