package loansManager;

import files.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ManagerTest {
    private FileHandler fileHandler;
    private Manager manager;

    @BeforeEach
    public void setUp() {
        fileHandler = mock(FileHandler.class);
        manager = new Manager(fileHandler);
    }

    @Test
    public void testViewPendingRequests() {
        List<String> mockRequests = new ArrayList<>();
        mockRequests.add("1 101 201 New_Loan Pending");
        mockRequests.add("2 102 202 Early_Repayment Approved");

        when(fileHandler.loadData(FileHandler.REQUESTS_FILE)).thenReturn(mockRequests);

        manager.viewPendingRequests();

        verify(fileHandler).loadData(FileHandler.REQUESTS_FILE);
    }

    @Test
    public void testProcessRequest_ApproveNewLoan() {
        String requestData = "1 101 201 New_Loan Pending";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));
        when(fileHandler.getNextUniqueId(FileHandler.APPROVED_CREDITS_FILE)).thenReturn(1);
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 201)).thenReturn(Optional.of("201 BankA 5000.0 12 5.0 450.00"));

        System.setIn(new java.io.ByteArrayInputStream("1".getBytes()));

        manager.processRequest(1);

        String expectedLoanRecord = "1 101 BankA 5000.0 12 5.0 450.00";
        String expectedRequestRecord = "1 101 201 New_Loan Approved";

        verify(fileHandler).appendData(eq(FileHandler.APPROVED_CREDITS_FILE), eq(expectedLoanRecord));
        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), eq(expectedRequestRecord));
    }

    @Test
    public void testProcessRequest_ApproveExtendCreditLine() {
        String requestData = "1 101 201 Line_Extension Pending 12";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));
        when(fileHandler.getNextUniqueId(FileHandler.APPROVED_CREDITS_FILE)).thenReturn(1);
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 201)).thenReturn(Optional.of("201 BankA 5000 12 5.0 450.00"));
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 201)).thenReturn(Optional.of("1 101 BankA 5000 12 5.0 450.00"));

        System.setIn(new java.io.ByteArrayInputStream("1".getBytes()));

        manager.processRequest(1);

        String expectedUpdatedLoanRecord = "1 101 BankA 5000 24 5.0 450.00";
        String expectedRequestRecord = "1 101 201 Line_Extension Approved";

        verify(fileHandler).updateById(eq(FileHandler.APPROVED_CREDITS_FILE), eq(201), eq(expectedUpdatedLoanRecord));
        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), eq(expectedRequestRecord));
    }

    @Test
    public void testProcessRequest_ExtendCreditLine_LoanNotFound() {
        String requestData = "1 101 201 Line_Extension Pending 12";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 201)).thenReturn(Optional.of("201 BankA 5000 12 5.0 450.00"));
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 201)).thenReturn(Optional.empty());

        System.setIn(new java.io.ByteArrayInputStream("1".getBytes()));

        manager.processRequest(1);

        verify(fileHandler, never()).updateById(eq(FileHandler.APPROVED_CREDITS_FILE), anyInt(), anyString());
        String expectedRequestRecord = "1 101 201 Line_Extension Approved";
        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), eq(expectedRequestRecord));
    }

    @Test
    public void testProcessRequest_RepayLoan_LoanFound() {
        String requestData = "1 101 201 Early_Repayment Pending";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));
        when(fileHandler.deleteById(FileHandler.APPROVED_CREDITS_FILE, 201)).thenReturn(true);

        System.setIn(new java.io.ByteArrayInputStream("1".getBytes()));

        manager.processRequest(1);

        verify(fileHandler).deleteById(FileHandler.APPROVED_CREDITS_FILE, 201);
        String expectedRequestRecord = "1 101 201 Early_Repayment Approved";
        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), eq(expectedRequestRecord));
    }

    @Test
    public void testProcessRequest_RepayLoan_LoanNotFound() {
        String requestData = "1 101 201 Early_Repayment Pending";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));
        when(fileHandler.deleteById(FileHandler.APPROVED_CREDITS_FILE, 201)).thenReturn(false);

        System.setIn(new java.io.ByteArrayInputStream("1".getBytes()));

        manager.processRequest(1);

        verify(fileHandler).deleteById(FileHandler.APPROVED_CREDITS_FILE, 201);
        String expectedRequestRecord = "1 101 201 Early_Repayment Approved";
        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), eq(expectedRequestRecord));
    }


    @Test
    public void testProcessRequest_Reject() {
        String requestData = "1 101 201 New_Loan Pending";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));

        System.setIn(new java.io.ByteArrayInputStream("2".getBytes()));

        manager.processRequest(1);

        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), anyString());
    }

    @Test
    public void testProcessRequest_AlreadyProcessed() {
        String requestData = "1 101 201 Repay_Loan Approved";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));

        manager.processRequest(1);

        verify(fileHandler, never()).deleteById(any(Path.class), anyInt());
        verify(fileHandler, never()).updateById(any(Path.class), anyInt(), anyString());
    }

    @Test
    public void testViewApprovedLoans() {
        List<String> mockApprovedLoans = new ArrayList<>();
        mockApprovedLoans.add("1 101 BankA 5000 12 5.0 450.00");
        mockApprovedLoans.add("2 102 BankB 6000 24 4.5 250.00");

        when(fileHandler.loadData(FileHandler.APPROVED_CREDITS_FILE)).thenReturn(mockApprovedLoans);

        manager.viewApprovedLoans();

        verify(fileHandler).loadData(FileHandler.APPROVED_CREDITS_FILE);
    }

    @Test
    public void testAddLoan() {
        double amount = 5000;
        String bankName = "BankA";
        int termInMonths = 12;
        double interestRate = 5.0;

        when(fileHandler.getNextUniqueId(FileHandler.CREDITS_FILE)).thenReturn(1);

        manager.addLoan(amount, bankName, termInMonths, interestRate);

        verify(fileHandler).appendData(eq(FileHandler.CREDITS_FILE), anyString());
    }

    @Test
    public void testDeleteLoan_Success() {
        when(fileHandler.deleteById(FileHandler.CREDITS_FILE, 1)).thenReturn(true);

        manager.deleteLoan(1);

        verify(fileHandler).deleteById(FileHandler.CREDITS_FILE, 1);
    }

    @Test
    public void testDeleteLoan_Failure() {
        when(fileHandler.deleteById(FileHandler.CREDITS_FILE, 1)).thenReturn(false);

        manager.deleteLoan(1);

        verify(fileHandler).deleteById(FileHandler.CREDITS_FILE, 1);
    }

    @Test
    public void testUpdateLoan_Success() {
        String existingLoanRecord = "1 BankA 5000 12 5.0 450.00";
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 1)).thenReturn(Optional.of(existingLoanRecord));

        System.setIn(new java.io.ByteArrayInputStream("1\nNewBank\n6000\n24\n6\n".getBytes()));

        manager.updateLoan();

        verify(fileHandler).updateById(eq(FileHandler.CREDITS_FILE), eq(1), anyString());
    }

    @Test
    public void testUpdateLoan_NotFound() {
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 1)).thenReturn(Optional.empty());
        System.setIn(new java.io.ByteArrayInputStream("1\nNewBank\n6000\n24\n6\n".getBytes()));

        manager.updateLoan();

        verify(fileHandler).findById(FileHandler.CREDITS_FILE, 1);
    }

    @Test
    public void testProcessRequest_RequestNotFound() {
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.empty());

        manager.processRequest(1);

        verify(fileHandler).findById(FileHandler.REQUESTS_FILE, 1);
    }

    @Test
    public void testProcessRequest_AlreadyProcessedRequest() {
        String requestData = "1 101 201 New_Loan Approved";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));

        manager.processRequest(1);

        verify(fileHandler, never()).deleteById(any(Path.class), anyInt());
        verify(fileHandler, never()).updateById(any(Path.class), anyInt(), anyString());
    }

    @Test
    public void testProcessRequest_LoanNotFound() {
        String requestData = "1 101 201 New_Loan Pending";
        when(fileHandler.findById(FileHandler.REQUESTS_FILE, 1)).thenReturn(Optional.of(requestData));
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 201)).thenReturn(Optional.empty());

        System.setIn(new java.io.ByteArrayInputStream("1".getBytes()));

        manager.processRequest(1);

        String expectedRequestRecord = "1 101 201 New_Loan Approved";
        verify(fileHandler).updateById(eq(FileHandler.REQUESTS_FILE), eq(1), eq(expectedRequestRecord));
    }
}