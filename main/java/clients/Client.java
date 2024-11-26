package clients;

import files.FileHandler;
import loans.Request;
import static menus.MainMenu.logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Client {
    int clientId;
    private String name;
    private FileHandler fileHandler;

    public Client(int clientId, String name, FileHandler fileHandler) {
        logger.info("Creating new Client: " + clientId + ", Name: " + name);
        this.clientId = clientId;
        this.name = name;
        this.fileHandler = fileHandler;
    }

    public void applyForLoan() {
        logger.info("Executing applyForLoan for Client ID: " + clientId);
        System.out.println("Enter the ID of the loan you want to apply for: ");
        Scanner scanner = new Scanner(System.in);
        int loanId = scanner.nextInt();
        logger.info("User entered loan ID: " + loanId);
        Optional<String> loanData = fileHandler.findById(FileHandler.CREDITS_FILE, loanId);
        if (loanData.isPresent()) {
            System.out.println("Loan information: " + loanData.get());
            System.out.println("Do you confirm the loan application? (yes/no)");
            String confirmation = scanner.next();
            logger.info("User confirmation: " + confirmation);
            if (confirmation.equalsIgnoreCase("yes")) {
                int requestId = fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE);
                Request request = new Request(requestId, clientId, loanId, Request.REQUEST_NEW_LOAN);
                fileHandler.appendData(FileHandler.REQUESTS_FILE, request.toString());
                System.out.println("Loan application sent.");
            } else {
                System.out.println("Loan application canceled.");
            }
        } else {
            System.out.println("Loan with this ID not found.");
        }
    }

    public void viewClientLoans() {
        logger.info("Executing viewClientLoans for Client ID: " + clientId);
        System.out.println("Your approved loans:");
        List<String> approvedLoansData = fileHandler.loadData(FileHandler.APPROVED_CREDITS_FILE);
        for (String line : approvedLoansData) {
            String[] parts = line.split(" ");
            int recordClientId = Integer.parseInt(parts[1].trim());
            if (recordClientId == clientId) {
                System.out.println("Loan ID: " + parts[0] + ", Client ID: " + parts[1] + ", Bank: " + parts[2] + ", Amount: $" + parts[3] +
                        ", Term: " + parts[4] + " months, Interest Rate: " + parts[5] + "%, Monthly Payment: $" + parts[6]);
            }
        }
    }

    public void requestEarlyRepayment() {
        logger.info("Executing requestEarlyRepayment for Client ID: " + clientId);
        System.out.println("Enter the ID of the loan for early repayment: ");
        Scanner scanner = new Scanner(System.in);
        int loanId = scanner.nextInt();
        logger.info("User entered loan ID for early repayment: " + loanId);
        Optional<String> loanData = fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, loanId);
        if (loanData.isPresent()) {
            String[] parts = loanData.get().split(" ");
            int recordClientId = Integer.parseInt(parts[1].trim());
            if (recordClientId == clientId) {
                int requestId = fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE);
                Request request = new Request(requestId, clientId, loanId, Request.REQUEST_EARLY_REPAYMENT);
                fileHandler.appendData(FileHandler.REQUESTS_FILE, request.toString());
                System.out.println("Early repayment request sent.");
            } else {
                System.out.println("Loan not found or you are not the owner.");
            }
        } else {
            System.out.println("Loan not found or you are not the owner.");
        }
    }

    public void requestCreditLineExtension() {
        logger.info("Executing requestCreditLineExtension for Client ID: " + clientId);
        System.out.println("Enter the ID of the loan for credit line extension: ");
        Scanner scanner = new Scanner(System.in);
        int loanId = scanner.nextInt();
        logger.info("User entered loan ID for credit line extension: " + loanId);
        Optional<String> loanData = fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, loanId);
        if (loanData.isPresent()) {
            String[] parts = loanData.get().split(" ");
            int recordClientId = Integer.parseInt(parts[1].trim());
            if (recordClientId == clientId) {
                System.out.println("How many months do you want to extend the credit?");
                int extensionMonths = scanner.nextInt();
                logger.info("User entered extension months: " + extensionMonths);
                int requestId = fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE);
                Request request = new Request(requestId, clientId, loanId, Request.REQUEST_LINE_EXTENSION);
                fileHandler.appendData(FileHandler.REQUESTS_FILE, request + " " + extensionMonths);
                System.out.println("Credit line extension request sent.");
            } else {
                System.out.println("Loan not found or you are not the owner.");
            }
        } else {
            System.out.println("Loan not found or you are not the owner.");
        }
    }
}