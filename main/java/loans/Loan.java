package loans;

import files.FileHandler;
import static menus.MainMenu.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Loan {
    private int id;
    private double amount;
    private String bankName;
    private int termInMonths;
    private double monthlyPayment;
    private double interestRate;
    private static final double MONTHLY_FEE_PERCENTAGE = 1.0;

    public Loan(int id, double amount, String bankName, int termInMonths, double interestRate) {
        this.id = id;
        this.amount = amount;
        this.bankName = bankName;
        this.termInMonths = termInMonths;
        this.interestRate = interestRate;
        this.monthlyPayment = calculateMonthlyPayment();
    }

    public double getAmount() { return amount; }
    public String getBankName() { return bankName; }
    public int getTermInMonths() { return termInMonths; }

    private double calculateMonthlyPayment() {
        logger.info("Executing calculateMonthlyPayment");
        double monthlyInterestRate = interestRate / 12 / 100;
        double denominator = 1 - Math.pow(1 + monthlyInterestRate, -termInMonths);
        double basePayment = (amount * monthlyInterestRate) / denominator;
        double fee = amount * (MONTHLY_FEE_PERCENTAGE / 100);
        return basePayment + fee;
    }

    public static void searchForLoans(FileHandler fileHandler) {
        logger.info("Executing searchForLoans");
        System.out.println("Enter bank name (or * for all available): ");
        Scanner scanner = new Scanner(System.in);
        String bankName = scanner.nextLine();
        logger.info("User entered bank name: " + bankName);
        System.out.println("Enter min loan value: ");
        double minAmount = scanner.nextDouble();
        logger.info("User entered min loan value: " + minAmount);
        System.out.println("Enter max loan value: ");
        double maxAmount = scanner.nextDouble();
        logger.info("User entered max loan value: " + maxAmount);
        System.out.println("Enter min loan term (in months): ");
        int minTerm = scanner.nextInt();
        logger.info("User entered min loan term: " + minTerm);
        System.out.println("Enter max loan term (in months): ");
        int maxTerm = scanner.nextInt();
        logger.info("User entered max loan term: " + maxTerm);
        List<String> loanData = fileHandler.loadData(FileHandler.CREDITS_FILE);
        List<Loan> loans = fileHandler.parseLoans(loanData);
        List<Loan> foundLoans = Loan.filterLoans(loans, bankName, minAmount, maxAmount, minTerm, maxTerm);
        if (foundLoans.isEmpty()) {
            System.out.println("Loans with given parameters are not found.");
        } else {
            for (Loan loan : foundLoans) {
                System.out.println("Loan ID: " + loan.getId() + ", Bank: " + loan.getBankName() + ", Amount: $" + loan.getAmount() +
                        ", Term: " + loan.getTermInMonths() + " months, Interest Rate: " + loan.getInterestRate() + "%" +
                        ", Monthly Payment: $" + String.format("%.2f", loan.getMonthlyPayment()));
            }
        }
    }

    public int getId() { return id; }
    public double getMonthlyPayment() { return monthlyPayment; }
    public double getInterestRate() { return interestRate; }

    public static List<Loan> filterLoans(List<Loan> loans, String bankName, double minAmount, double maxAmount, int minTerm, int maxTerm) {
        logger.info("Executing filterLoans");
        List<Loan> filteredLoans = new ArrayList<>();
        for (Loan loan : loans) {
            boolean matchesBankName = bankName.equals("*") || loan.getBankName().trim().equalsIgnoreCase(bankName.trim());
            boolean matchesAmount = (minAmount <= loan.getAmount() && loan.getAmount() <= maxAmount);
            boolean matchesTerm = (minTerm <= loan.getTermInMonths() && loan.getTermInMonths() <= maxTerm);

            if (matchesBankName && matchesAmount && matchesTerm) {
                filteredLoans.add(loan);
            }
        }
        return filteredLoans;
    }

    @Override
    public String toString() {
        logger.info("Executing toString");
        return id + " " + bankName + " " + amount + " " + termInMonths + " " + interestRate + " " + String.format("%.2f", monthlyPayment);
    }
}
