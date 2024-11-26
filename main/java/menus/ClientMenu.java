package menus;

import clients.Client;
import files.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static menus.MainMenu.logger;

class ClientMenu {
    private Client client;
    private List<Command> commands = new ArrayList<>();
    private FileHandler fileHandler;

    public ClientMenu(Client client, FileHandler fileHandler) {
        this.client = client;
        this.fileHandler = fileHandler;
        initializeCommands();
    }

    private void initializeCommands() {
        logger.info("Initializing client commands");
        commands.add(new LoanSearchCommand(fileHandler));
        commands.add(new ApplyForLoanCommand(client));
        commands.add(new ViewClientLoansCommand(client));
        commands.add(new RequestEarlyRepaymentCommand(client));
        commands.add(new RequestCreditLineExtensionCommand(client));
    }

    public void displayMenu() {
        logger.info("Displaying client menu");
        System.out.println("\nClient Menu:");
        for (int i = 0; i < commands.size(); i++) {
            System.out.println((i + 1) + ". " + commands.get(i).getName());
        }
        System.out.println("0. Exit");

        handleInput();
    }

    private void handleInput() {
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        logger.info("Chosen command: " + choice);

        if (choice == 0) {
            System.out.println("Exiting client menu.");
            return;
        }

        if (choice > 0 && choice <= commands.size()) {
            commands.get(choice - 1).execute();
        } else {
            System.out.println("Invalid choice, please try again.");
        }

        displayMenu();
    }
}