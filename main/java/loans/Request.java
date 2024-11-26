package loans;

import static menus.MainMenu.logger;

public class Request {
    private int requestId;
    private int clientId;
    private int loanId;
    private String requestType;
    private String status;

    public static final String REQUEST_NEW_LOAN = "New_Loan";
    public static final String REQUEST_EARLY_REPAYMENT = "Early_Repayment";
    public static final String REQUEST_LINE_EXTENSION = "Line_Extension";

    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    public Request(int requestId, int clientId, int loanId, String requestType) {
        logger.info("Creating new Request: " + requestId + ", Client ID: " + clientId + ", Loan ID: " + loanId + ", Request Type: " + requestType);
        this.requestId = requestId;
        this.clientId = clientId;
        this.loanId = loanId;
        this.requestType = requestType;
        this.status = STATUS_PENDING;
        logger.info("Request status set to Pending");
    }

    public void approveRequest() {
        logger.info("Approving Request: " + requestId);
        this.status = STATUS_APPROVED;
    }

    public void rejectRequest() {
        logger.info("Rejecting Request: " + requestId);
        this.status = STATUS_REJECTED;
    }

    @Override
    public String toString() {
        logger.info("Executing toString for Request: " + requestId);
        return requestId + " " + clientId + " " + loanId + " " + requestType + " " + status;
    }
}