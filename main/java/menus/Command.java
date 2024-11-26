package menus;

import clients.Client;
import files.FileHandler;
import loans.Loan;
import loansManager.Manager;
import java.util.Scanner;
import static menus.MainMenu.logger;

public interface Command {
    void execute();
    String getName();
}

class LoanSearchCommand implements Command {
    private FileHandler fileHandler;

    public LoanSearchCommand(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Override
    public void execute() {
        logger.info("Executing LoanSearchCommand");
        Loan.searchForLoans(fileHandler);
    }

    @Override
    public String getName() {
        return "Search for loans";
    }
}

class ApplyForLoanCommand implements Command {
    private Client client;

    public ApplyForLoanCommand(Client client) {
        this.client = client;
    }

    @Override
    public void execute() {
        logger.info("Executing ApplyForLoanCommand");
        client.applyForLoan();
    }

    @Override
    public String getName() {
        return "Apply for a loan";
    }
}

class ViewClientLoansCommand implements Command {
    private Client client;

    public ViewClientLoansCommand(Client client) {
        this.client = client;
    }

    @Override
    public void execute() {
        logger.info("Executing ViewClientLoansCommand");
        client.viewClientLoans();
    }

    @Override
    public String getName() {
        return "View your loans";
    }
}

class RequestEarlyRepaymentCommand implements Command {
    private Client client;

    public RequestEarlyRepaymentCommand(Client client) {
        this.client = client;
    }

    @Override
    public void execute() {
        logger.info("Executing RequestEarlyRepaymentCommand");
        client.requestEarlyRepayment();
    }

    @Override
    public String getName() {
        return "Request early repayment";
    }
}

class RequestCreditLineExtensionCommand implements Command {
    private Client client;

    public RequestCreditLineExtensionCommand(Client client) {
        this.client = client;
    }

    @Override
    public void execute() {
        logger.info("Executing RequestCreditLineExtensionCommand");
        client.requestCreditLineExtension();
    }

    @Override
    public String getName() {
        return "Request credit line extension";
    }
}

class AddNewLoanCommand implements Command {
    private Manager manager;

    public AddNewLoanCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        logger.info("Executing AddNewLoanCommand");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter loan amount: ");
        double amount = scanner.nextDouble();
        logger.info("Loan amount: " + amount);

        System.out.println("Enter bank name: ");
        String bankName = scanner.next();
        logger.info("Bank name: " + bankName);

        System.out.println("Enter term in months: ");
        int termInMonths = scanner.nextInt();
        logger.info("Term in months: " + termInMonths);

        System.out.println("Enter interest rate: ");
        double interestRate = scanner.nextDouble();
        logger.info("Interest rate: " + interestRate);

        manager.addLoan(amount, bankName, termInMonths, interestRate);
    }

    @Override
    public String getName() {
        return "Add a new loan";
    }
}

class RemoveLoanCommand implements Command {
    private Manager manager;

    public RemoveLoanCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        logger.info("Executing RemoveLoanCommand");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter loan ID to remove: ");
        int loanId = scanner.nextInt();
        logger.info("Loan ID to remove: " + loanId);
        manager.deleteLoan(loanId);
    }

    @Override
    public String getName() {
        return "Remove a loan";
    }
}

class ModifyLoanDetailsCommand implements Command {
    private Manager manager;

    public ModifyLoanDetailsCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        logger.info("Executing ModifyLoanDetailsCommand");
        manager.updateLoan();
    }

    @Override
    public String getName() {
        return "Modify loan details";
    }
}

class ViewPendingRequestsCommand implements Command {
    private Manager manager;

    public ViewPendingRequestsCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        logger.info("Executing ViewPendingRequestsCommand");
        manager.viewPendingRequests();
    }

    @Override
    public String getName() {
        return "View client requests";
    }
}

class ProcessRequestCommand implements Command {
    private Manager manager;

    public ProcessRequestCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        logger.info("Executing ProcessRequestCommand");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter request ID to process: ");
        int requestId = scanner.nextInt();
        logger.info("Request ID to process: " + requestId);
        manager.processRequest(requestId);
    }

    @Override
    public String getName() {
        return "Process a request";
    }
}

class ViewApprovedLoansCommand implements Command {
    private Manager manager;

    public ViewApprovedLoansCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        logger.info("Executing ViewApprovedLoansCommand");
        manager.viewApprovedLoans();
    }

    @Override
    public String getName() {
        return "View approved loans";
    }
}