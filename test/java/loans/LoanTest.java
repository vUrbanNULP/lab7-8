package loans;

import files.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanTest {
    private FileHandler fileHandlerMock;
    private Loan loan;

    @BeforeEach
    void setUp() {
        fileHandlerMock = Mockito.mock(FileHandler.class);
        loan = new Loan(1, 1000.0, "TestBank", 12, 5.0);
    }

    @Test
    void testCalculateMonthlyPayment() {
        double expectedMonthlyPayment = loan.getMonthlyPayment();
        assertTrue(expectedMonthlyPayment > 0, "Monthly payment should be greater than zero.");
    }

    @Test
    void testSearchForLoans_Found() {
        String simulatedInput = "TestBank\n500\n1500\n6\n24\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        List<String> loanData = List.of("1 TestBank 1000.0 12 5.0");
        when(fileHandlerMock.loadData(FileHandler.CREDITS_FILE)).thenReturn(loanData);
        when(fileHandlerMock.parseLoans(loanData)).thenReturn(List.of(loan));

        Loan.searchForLoans(fileHandlerMock);
    }

    @Test
    void testSearchForLoans_NotFound() {
        String simulatedInput = "TestBank\n2000\n3000\n12\n24\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        List<String> loanData = List.of("1 TestBank 1000.0 12 5.0");
        when(fileHandlerMock.loadData(FileHandler.CREDITS_FILE)).thenReturn(loanData);
        when(fileHandlerMock.parseLoans(loanData)).thenReturn(List.of(loan));

        Loan.searchForLoans(fileHandlerMock);
    }

    @Test
    void testFilterLoans_BankNameMatch() {
        List<Loan> loans = List.of(
                new Loan(1, 1000.0, "TestBank", 12, 5.0),
                new Loan(2, 1500.0, "OtherBank", 12, 5.5),
                new Loan(3, 9999.0, "StrangeBank", 1, 5.5)

        );

        List<Loan> filteredLoans = Loan.filterLoans(loans, "TestBank", 500, 1500, 6, 18);

        assertEquals(1, filteredLoans.size());
        assertEquals("TestBank", filteredLoans.get(0).getBankName());
    }

    @Test
    void testFilterLoans_BankNameWildcard() {
        List<Loan> loans = List.of(
                new Loan(1, 1000.0, "TestBank", 12, 5.0),
                new Loan(2, 1500.0, "OtherBank", 12, 5.5)
        );

        List<Loan> filteredLoans = Loan.filterLoans(loans, "*", 500, 1500, 6, 18);

        assertEquals(2, filteredLoans.size());
    }

    @Test
    void testFilterLoans_AmountOutOfRange() {
        List<Loan> loans = List.of(
                new Loan(1, 1000.0, "TestBank", 12, 5.0),
                new Loan(2, 1500.0, "OtherBank", 12, 5.5)
        );

        List<Loan> filteredLoans = Loan.filterLoans(loans, "*", 2000, 3000, 6, 18);

        assertTrue(filteredLoans.isEmpty(), "No loans should be found for the given amount range.");
    }

    @Test
    void testFilterLoans_TermOutOfRange() {
        List<Loan> loans = List.of(
                new Loan(1, 1000.0, "TestBank", 12, 5.0),
                new Loan(2, 1500.0, "OtherBank", 24, 5.5)
        );

        List<Loan> filteredLoans = Loan.filterLoans(loans, "*", 500, 2000, 1, 6);

        assertTrue(filteredLoans.isEmpty(), "No loans should be found for the given term range.");
    }

    @Test
    void testFilterLoans_MultipleCriteriaMatch() {
        List<Loan> loans = List.of(
                new Loan(1, 1000.0, "TestBank", 12, 5.0),
                new Loan(2, 1500.0, "TestBank", 24, 5.5)
        );

        List<Loan> filteredLoans = Loan.filterLoans(loans, "TestBank", 500, 1500, 12, 24);

        assertEquals(2, filteredLoans.size());
    }
}