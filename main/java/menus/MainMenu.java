package menus;

import clients.Client;
import files.FileHandler;
import loansManager.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class MainMenu {
    public static final Logger logger = LogManager.getLogger(MainMenu.class);

    public static void main(String[] args) {
        logger.info("\nStart of program\n");
        FileHandler fileHandler = new FileHandler();
        Scanner scanner = new Scanner(System.in);
        String userType;

        while (true) {
            System.out.println("Welcome! Are you a client or manager? (client/manager) or enter 0 to exit: ");
            userType = scanner.next();
            logger.info("\nUserType: " + userType + "\n");

            if (userType.equalsIgnoreCase("client") || userType.equalsIgnoreCase("c")) {
                System.out.println("Enter your full name: ");
                String clientName = scanner.next();
                logger.info("\nEntered client name: " + clientName + "\n");
                int clientId = identifyOrCreateClient(clientName, fileHandler);
                logger.info("\nIdentified client ID: " + clientId + "\n");
                Client client = new Client(clientId, clientName, fileHandler);
                ClientMenu clientMenu = new ClientMenu(client, fileHandler);
                clientMenu.displayMenu();
            } else if (userType.equalsIgnoreCase("manager") || userType.equalsIgnoreCase("m")) {
                logger.info("\nLogged as manager\n");
                Manager manager = new Manager(fileHandler);
                ManagerMenu managerMenu = new ManagerMenu(manager, fileHandler);
                managerMenu.displayMenu();
            } else if (userType.equals("0")) {
                System.out.println("Exiting the program. Goodbye!");
                break;
            } else {
                System.out.println("Invalid user type. Please enter 'client/c' or 'manager/m' or 0 to exit.");
            }
        }

        scanner.close();
        logger.info("\nEnd of program\n");
    }

    static int identifyOrCreateClient(String clientName, FileHandler fileHandler) {
        List<String> clientsData = fileHandler.loadData(FileHandler.CLIENTS_FILE);
        for (String clientRecord : clientsData) {
            String[] parts = clientRecord.split(" ");
            String name = parts[1];
            if (name.equalsIgnoreCase(clientName)) {
                System.out.println("Client identified. Your ID is: " + parts[0]);
                return Integer.parseInt(parts[0]);
            }
        }
        int newClientId = fileHandler.getNextUniqueId(FileHandler.CLIENTS_FILE);
        fileHandler.appendData(FileHandler.CLIENTS_FILE, newClientId + " " + clientName);
        System.out.println("New client created. Your ID is: " + newClientId);
        logger.info("\nNew client created. ID is: " + newClientId + "\n");
        return newClientId;
    }
}