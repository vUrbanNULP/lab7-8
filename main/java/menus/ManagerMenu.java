package menus;

import files.FileHandler;
import loansManager.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static menus.MainMenu.logger;

class ManagerMenu {
    private Manager manager;
    List<Command> commands = new ArrayList<>();
    private FileHandler fileHandler;

    public ManagerMenu(Manager manager, FileHandler fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;
        initializeCommands();
    }

    private void initializeCommands() {
        logger.info("Initializing manager commands");
        commands.add(new LoanSearchCommand(fileHandler));
        commands.add(new AddNewLoanCommand(manager));
        commands.add(new RemoveLoanCommand(manager));
        commands.add(new ModifyLoanDetailsCommand(manager));
        commands.add(new ViewPendingRequestsCommand(manager));
        commands.add(new ProcessRequestCommand(manager));
        commands.add(new ViewApprovedLoansCommand(manager));
    }

    public void displayMenu() {
        logger.info("\nDisplaying manager commands\n");
        System.out.println("\nManager Menu:");
        for (int i = 0; i < commands.size(); i++) {
            System.out.println((i + 1) + ". " + commands.get(i).getName());
        }
        System.out.println("0. Exit");

        handleInput();
    }

    void handleInput() {
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        logger.info("\nChosen command: " + choice + "\n");

        if (choice == 0) {
            System.out.println("Exiting manager menu.");
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