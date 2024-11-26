package loansManager;

import files.FileHandler;
import loans.Loan;
import loans.Request;
import static menus.MainMenu.logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Manager {
    private FileHandler fileHandler;

    public Manager(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public void viewPendingRequests() {
        logger.info("Executing viewPendingRequests");
        List<String> requestData = fileHandler.loadData(FileHandler.REQUESTS_FILE);
        System.out.println("Pending Requests:");
        for (String data : requestData) {
            String[] parts = data.split(" ");
            if (parts.length > 4 && parts[4].equals("Pending")) {
                System.out.println("Request ID: " + parts[0] + ", Client ID: " + parts[1] + ", Loan ID: " + parts[2] + ", Request Type: " + parts[3] + ", Status: " + parts[4]);
            }
        }
    }

    public void processRequest(int requestId) {
        logger.info("Executing processRequest");
        Optional<String> requestRecord = fileHandler.findById(FileHandler.REQUESTS_FILE, requestId);
        if (requestRecord.isPresent()) {
            String[] parts = requestRecord.get().split(" ");
            int clientId = Integer.parseInt(parts[1].trim());
            int loanId = Integer.parseInt(parts[2].trim());
            if (parts[4].equals("Pending")) {
                String requestType = parts[3].trim();
                Request request = new Request(requestId, clientId, loanId, requestType);
                Scanner scanner = new Scanner(System.in);
                System.out.println("Approve (1) or Reject (2) the request?");
                if (scanner.nextInt() == 1) {
                    request.approveRequest();
                    logger.info("Request approved: " + requestId);
                    switch (requestType) {
                        case Request.REQUEST_NEW_LOAN:
                            createLoanForClient(clientId, loanId);
                            break;
                        case Request.REQUEST_LINE_EXTENSION:
                            int extensionMonths = Integer.parseInt(parts[5].trim());
                            extendCreditLine(clientId, loanId, extensionMonths);
                            break;
                        case Request.REQUEST_EARLY_REPAYMENT:
                            repayLoan(loanId);
                            break;
                    }
                } else {
                    request.rejectRequest();
                    logger.info("Request rejected: " + requestId);
                }
                fileHandler.updateById(FileHandler.REQUESTS_FILE, requestId, request.toString());
            } else {
                System.out.println("Request was already processed: " + requestId);
            }
        } else {
            System.out.println("Request not found: " + requestId);
        }
    }

    private void createLoanForClient(int clientId, int loanId) {
        logger.info("Executing createLoanForClient");
        Optional<String> loanRecord = fileHandler.findById(FileHandler.CREDITS_FILE, loanId);
        if (loanRecord.isPresent()) {
            int newApprovedLoanId = fileHandler.getNextUniqueId(FileHandler.APPROVED_CREDITS_FILE);
            String[] loanParts = loanRecord.get().split(" ");
            String bankName = loanParts[1].trim();
            double amount = Double.parseDouble(loanParts[2].trim());
            int termInMonths = Integer.parseInt(loanParts[3].trim());
            double interestRate = Double.parseDouble(loanParts[4].trim());
            String monthlyPayment = loanParts[5].trim();
            String newLoanRecord = newApprovedLoanId + " " + clientId + " " + bankName + " " + amount + " " + termInMonths + " " + interestRate + " " + monthlyPayment;
            fileHandler.appendData(FileHandler.APPROVED_CREDITS_FILE, newLoanRecord);
            System.out.println("Loan created for client: " + clientId);
        } else {
            System.out.println("Loan not found: " + loanId);
        }
    }

    private void extendCreditLine(int clientId, int loanId, int extensionMonths) {
        logger.info("Executing extendCreditLine");
        Optional<String> loanRecord = fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, loanId);
        if (loanRecord.isPresent()) {
            String[] loanParts = loanRecord.get().split(" ");
            int termInMonths = Integer.parseInt(loanParts[4].trim()) + extensionMonths;
            String updatedLoanRecord = loanParts[0] + " " + loanParts[1] + " " + loanParts[2] + " " + loanParts[3] + " " + termInMonths + " " + loanParts[5] + " " + loanParts[6];
            fileHandler.updateById(FileHandler.APPROVED_CREDITS_FILE, loanId, updatedLoanRecord);
            System.out.println("Credit line extended for client: " + clientId);
        } else {
            System.out.println("Loan not found: " + loanId + ". Most likely it was early repaid.");
        }
    }

    private void repayLoan(int loanId) {
        logger.info("Executing repayLoan");
        if (fileHandler.deleteById(FileHandler.APPROVED_CREDITS_FILE, loanId)) {
            System.out.println("Loan with ID " + loanId + " has been fully repaid and deleted.");
        } else {
            System.out.println("Loan with ID " + loanId + " not found.");
        }
    }

    public void viewApprovedLoans() {
        logger.info("Executing viewApprovedLoans");
        List<String> approvedLoansData = fileHandler.loadData(FileHandler.APPROVED_CREDITS_FILE);
        System.out.println("Approved Loans:");
        for (String loanData : approvedLoansData) {
            String[] parts = loanData.split(" ");
            System.out.println("Loan ID: " + parts[0] + ", Client ID: " + parts[1] + ", Bank: " + parts[2] + ", Amount: $" + parts[3]
                    + ", Term: " + parts[4] + " months, Interest Rate: " + parts[5] + "%, Monthly Payment: $" + parts[6]);
        }
    }

    public void addLoan(double amount, String bankName, int termInMonths, double interestRate) {
        logger.info("Executing addLoan");
        int newLoanId = fileHandler.getNextUniqueId(FileHandler.CREDITS_FILE);
        Loan newLoan = new Loan(newLoanId, amount, bankName, termInMonths, interestRate);
        fileHandler.appendData(FileHandler.CREDITS_FILE, newLoan.toString());
        System.out.println("Loan added: " + newLoan);
    }

    public void deleteLoan(int loanId) {
        logger.info("Executing deleteLoan");
        if (fileHandler.deleteById(FileHandler.CREDITS_FILE, loanId)){
            System.out.println("Loan with ID " + loanId + " has been deleted.");
        }
        else {
            System.out.println("No loan with such ID found.");
        }
    }

    public void updateLoan() {
        logger.info("Executing updateLoan");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter loan ID to modify: ");
        int loanId = scanner.nextInt();

        Optional<String> existingLoanRecord = fileHandler.findById(FileHandler.CREDITS_FILE, loanId);
        if (existingLoanRecord.isPresent()) {
            System.out.println("Enter new bank name: ");
            String newBankName = scanner.next();

            System.out.println("Enter new loan amount: ");
            double newAmount = scanner.nextDouble();

            System.out.println("Enter new term in months: ");
            int newTermInMonths = scanner.nextInt();

            System.out.println("Enter new interest rate: ");
            double newInterestRate = scanner.nextDouble();

            Loan existingLoan = new Loan(loanId, newAmount, newBankName, newTermInMonths, newInterestRate);
            fileHandler.updateById(FileHandler.CREDITS_FILE, loanId, existingLoan.toString());
            System.out.println("Loan with ID " + loanId + " has been updated.");
        } else {
            System.out.println("Loan with ID " + loanId + " not found.");
        }
    }
}