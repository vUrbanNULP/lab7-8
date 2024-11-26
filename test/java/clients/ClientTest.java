package clients;

import files.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ClientTest {

    private FileHandler fileHandler;
    private Client client;

    @BeforeEach
    void setUp() {
        fileHandler = mock(FileHandler.class);
        client = new Client(1, "John Doe", fileHandler);
    }

    @Test
    void testApplyForLoan_ValidLoanId() {
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 10))
                .thenReturn(Optional.of("10 BankName 1000.0 12 5.0"));

        when(fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE))
                .thenReturn(1);

        String simulatedInput = "10\nyes\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.applyForLoan();

        verify(fileHandler).appendData(
                eq(FileHandler.REQUESTS_FILE),
                eq("1 1 10 New_Loan Pending")
        );
    }

    @Test
    void testApplyForLoan_ValidLoanId_Cancelled() {
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 10))
                .thenReturn(Optional.of("10 BankName 1000.0 12 5.0"));

        when(fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE))
                .thenReturn(1);

        String simulatedInput = "10\nno\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.applyForLoan();

        verify(fileHandler, never()).appendData(any(Path.class), anyString());
    }

    @Test
    void testApplyForLoan_InvalidLoanId() {
        when(fileHandler.findById(FileHandler.CREDITS_FILE, 10))
                .thenReturn(Optional.empty());

        String simulatedInput = "10\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.applyForLoan();

        verify(fileHandler, never()).appendData(any(Path.class), anyString());
    }

    @Test
    void testRequestEarlyRepayment_ValidLoanId() {
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 1))
                .thenReturn(Optional.of("1 1 BankName 1000.0 12 5.0 85.0"));

        when(fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE)).thenReturn(2);

        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.requestEarlyRepayment();

        verify(fileHandler).appendData(
                eq(FileHandler.REQUESTS_FILE),
                eq("2 1 1 Early_Repayment Pending")
        );
    }

    @Test
    void testRequestEarlyRepayment_InvalidLoanId() {
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 1))
                .thenReturn(Optional.empty());

        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.requestEarlyRepayment();

        verify(fileHandler, never()).appendData(any(Path.class), anyString());
    }

    @Test
    void testRequestCreditLineExtension_ValidLoanId() {
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 1))
                .thenReturn(Optional.of("1 1 BankName 1000.0 12 5.0 85.0"));

        when(fileHandler.getNextUniqueId(FileHandler.REQUESTS_FILE)).thenReturn(2);

        String simulatedInput = "1\n6\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.requestCreditLineExtension();

        verify(fileHandler).appendData(
                eq(FileHandler.REQUESTS_FILE),
                eq("2 1 1 Line_Extension Pending 6")
        );
    }

    @Test
    void testRequestCreditLineExtension_InvalidLoanId() {
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 1))
                .thenReturn(Optional.empty());

        String simulatedInput = "1\n6\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.requestCreditLineExtension();
        verify(fileHandler, never()).appendData(any(Path.class), anyString());
    }

    @Test
    void testRequestEarlyRepayment_ClientNotOwner() {
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 1))
                .thenReturn(Optional.of("1 2 BankName 1000.0 12 5.0 85.0"));

        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.requestEarlyRepayment();

        verify(fileHandler, never()).appendData(any(Path.class), anyString());
    }

    @Test
    void testRequestCreditLineExtension_ClientNotOwner() {
        when(fileHandler.findById(FileHandler.APPROVED_CREDITS_FILE, 1))
                .thenReturn(Optional.of("1 2 BankName 1000.0 12 5.0 85.0"));

        String simulatedInput = "1\n6\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        client.requestCreditLineExtension();

        verify(fileHandler, never()).appendData(any(Path.class), anyString());
    }

    @Test
    public void testViewClientLoans() {
        List<String> mockApprovedLoans = new ArrayList<>();
        mockApprovedLoans.add("1 101 BankA 5000 12 5.0 450.00");
        mockApprovedLoans.add("2 102 BankB 6000 24 4.5 250.00");

        when(fileHandler.loadData(FileHandler.APPROVED_CREDITS_FILE)).thenReturn(mockApprovedLoans);

        client.clientId = 101;
        client.viewClientLoans();

        verify(fileHandler).loadData(FileHandler.APPROVED_CREDITS_FILE);
    }
}