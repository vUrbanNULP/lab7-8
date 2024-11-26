package files;

import loans.Loan;
import static menus.MainMenu.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileHandler {

    public static final Path CLIENTS_FILE = Paths.get("C:\\creditSystem\\clients.txt");
    public static final Path CREDITS_FILE = Paths.get("C:\\creditSystem\\credits.txt");
    public static final Path REQUESTS_FILE = Paths.get("C:\\creditSystem\\requests.txt");
    public static final Path APPROVED_CREDITS_FILE = Paths.get("C:\\creditSystem\\approved_credits.txt");

    public static final Path CLIENTS_FILE_BACKUP = Paths.get("C:\\creditSystemTestBackups\\clients.txt");
    public static final Path CREDITS_FILE_BACKUP = Paths.get("C:\\creditSystemTestBackups\\credits.txt");
    public static final Path REQUESTS_FILE_BACKUP = Paths.get("C:\\creditSystemTestBackups\\requests.txt");
    public static final Path APPROVED_CREDITS_FILE_BACKUP = Paths.get("C:\\creditSystemTestBackups\\approved_credits.txt");

    public int getNextUniqueId(Path filePath) {
        logger.info("Executing getNextUniqueId for file: " + filePath);
        List<String> data = loadData(filePath);
        int maxId = 0;
        for (String record : data) {
            int id = Integer.parseInt(record.split(" ")[0].trim());
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    public List<String> loadData(Path filePath) {
        logger.info("Loading data from file: " + filePath);
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            logger.error("Critical error reading file: " + e.getMessage(), e);
            System.exit(0);
            return new ArrayList<>();
        }
    }

    public void saveData(Path filePath, List<String> data) {
        logger.info("Saving data to file: " + filePath);
        try {
            Files.write(filePath, data);
        } catch (IOException e) {
            logger.error("Critical error writing file: " + e.getMessage(), e);
            System.exit(0);
        }
    }

    public void appendData(Path filePath, String data) {
        logger.info("Appending data to file: " + filePath);
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(filePath));
            lines.add(data);
            Files.write(filePath, lines);
        } catch (IOException e) {
            logger.error("Critical error writing file: " + e.getMessage(), e);
            System.exit(0);
        }
    }

    public Optional<String> findById(Path filePath, int id) {
        logger.info("Finding record by ID from file: " + filePath + " with id: " + id);
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                int currentId = Integer.parseInt(line.split(" ")[0].trim());
                if (currentId == id) {
                    return Optional.of(line);
                }
            }
        } catch (IOException e) {
            logger.error("Critical error reading file: " + e.getMessage(), e);
            System.exit(0);
        }
        return Optional.empty();
    }

    public boolean deleteById(Path filePath, int id) {
        logger.info("Deleting record by ID from file: " + filePath + " with id: " + id);
        List<String> data = loadData(filePath);
        boolean isRemoved = data.removeIf(line -> Integer.parseInt(line.split(" ")[0].trim()) == id);
        if (isRemoved) {
            saveData(filePath, data);
        }
        return isRemoved;
    }

    public void updateById(Path filePath, int id, String updatedRecord) {
        logger.info("Updating record by ID in file: " + filePath + " with id: " + id);
        List<String> data = loadData(filePath);
        for (int i = 0; i < data.size(); i++) {
            int currentId = Integer.parseInt(data.get(i).split(" ")[0].trim());
            if (currentId == id) {
                data.set(i, updatedRecord);
                break;
            }
        }
        saveData(filePath, data);
    }

    public List<Loan> parseLoans(List<String> loanData) {
        logger.info("Parsing loans data");
        List<Loan> loans = new ArrayList<>();
        for (String line : loanData) {
            String[] parts = line.split(" ");
            int id = Integer.parseInt(parts[0].trim());
            String bankName = parts[1].trim();
            double amount = Double.parseDouble(parts[2].trim());
            int termInMonths = Integer.parseInt(parts[3].trim());
            double interestRate = Double.parseDouble(parts[4].trim());
            loans.add(new Loan(id, amount, bankName, termInMonths, interestRate));
        }
        return loans;
    }
}
