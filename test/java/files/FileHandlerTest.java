package files;

import loans.Loan;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {
    private static final Path CLIENTS_FILE_BACKUP = FileHandler.CLIENTS_FILE_BACKUP;
    private static final Path CREDITS_FILE_BACKUP = FileHandler.CREDITS_FILE_BACKUP;
    private static final Path REQUESTS_FILE_BACKUP = FileHandler.REQUESTS_FILE_BACKUP;
    private static final Path APPROVED_CREDITS_FILE_BACKUP = FileHandler.APPROVED_CREDITS_FILE_BACKUP;

    private FileHandler fileHandler;

    @BeforeEach
    void setUp() throws IOException {
        fileHandler = new FileHandler();

        Files.write(CLIENTS_FILE_BACKUP, new ArrayList<>());
        Files.write(CREDITS_FILE_BACKUP, new ArrayList<>());
        Files.write(REQUESTS_FILE_BACKUP, new ArrayList<>());
        Files.write(APPROVED_CREDITS_FILE_BACKUP, new ArrayList<>());
    }

    @Test
    void testGetNextUniqueId() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya", "2 Max"));
        int nextId = fileHandler.getNextUniqueId(CLIENTS_FILE_BACKUP);
        assertEquals(3, nextId);
    }

    @Test
    void testLoadData() throws IOException {
        List<String> expectedData = List.of("1 Volodya", "2 Max");
        Files.write(CLIENTS_FILE_BACKUP, expectedData);
        List<String> actualData = fileHandler.loadData(CLIENTS_FILE_BACKUP);
        assertEquals(expectedData, actualData);
    }

    @Test
    void testSaveData() {
        List<String> dataToSave = List.of("1 Volodya", "2 Max");
        fileHandler.saveData(CLIENTS_FILE_BACKUP, dataToSave);

        List<String> savedData = fileHandler.loadData(CLIENTS_FILE_BACKUP);
        assertEquals(dataToSave, savedData);
    }

    @Test
    void testAppendData() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya"));
        fileHandler.appendData(CLIENTS_FILE_BACKUP, "2 Max");

        List<String> expectedData = List.of("1 Volodya", "2 Max");
        List<String> actualData = fileHandler.loadData(CLIENTS_FILE_BACKUP);
        assertEquals(expectedData, actualData);
    }

    @Test
    void testFindById() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya", "2 Max"));
        Optional<String> result = fileHandler.findById(CLIENTS_FILE_BACKUP, 2);

        assertTrue(result.isPresent());
        assertEquals("2 Max", result.get());
    }

    @Test
    void testFindByIdNotFound() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya"));
        Optional<String> result = fileHandler.findById(CLIENTS_FILE_BACKUP, 2);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteById() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya", "2 Max"));
        boolean isDeleted = fileHandler.deleteById(CLIENTS_FILE_BACKUP, 2);

        List<String> expectedData = List.of("1 Volodya");
        List<String> actualData = fileHandler.loadData(CLIENTS_FILE_BACKUP);

        assertTrue(isDeleted);
        assertEquals(expectedData, actualData);
    }

    @Test
    void testDeleteByIdNotFound() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya"));
        boolean isDeleted = fileHandler.deleteById(CLIENTS_FILE_BACKUP, 2);

        assertFalse(isDeleted);
    }

    @Test
    void testUpdateById() throws IOException {
        Files.write(CLIENTS_FILE_BACKUP, List.of("1 Volodya", "2 Max"));
        fileHandler.updateById(CLIENTS_FILE_BACKUP, 2, "2 Jane Smith");

        List<String> expectedData = List.of("1 Volodya", "2 Jane Smith");
        List<String> actualData = fileHandler.loadData(CLIENTS_FILE_BACKUP);

        assertEquals(expectedData, actualData);
    }

    @Test
    void testParseLoans() {
        List<String> loanData = List.of("1 BankA 1000.0 12 5.0", "2 BankB 5000.0 24 4.5");
        List<Loan> loans = fileHandler.parseLoans(loanData);

        assertEquals(2, loans.size());
        assertEquals("BankA", loans.get(0).getBankName());
        assertEquals(5000.0, loans.get(1).getAmount());
    }
}