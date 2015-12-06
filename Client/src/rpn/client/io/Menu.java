package rpn.client.io;

import rpn.client.model.Command;

import java.util.Scanner;

/**
 * Represents the menu handling within the project. Displays menus to the user and prompts
 * which allow the user to interact.
 */
public class Menu {

    private Command[] commands;

    private Command terminator;

    private Scanner scanner = new Scanner(System.in);

    public Menu(Command[] commands, Command terminator) {
        this.commands = commands;
        this.terminator = terminator;
    }

    public Command getTerminator() {
        return terminator;
    }

    public Command getMainChoice() {
        System.out.println("|⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻ STOCK  MARKET ⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻|");
        System.out.println("|                                     |");
        System.out.println("|~~~~~~~~~~~ CHOOSE OPTION ~~~~~~~~~~~|");
        for (Command command : commands) {
            System.out.println("| " + command.getValue() + ") " + command.getDescription());
        }
        System.out.println("|_____________________________________|\n");
        return requestOption();
    }

    private Command requestOption() {
        Command option = null;
        System.out.print("Option: ");
        try {
            option = Command.valueOf(scanner.nextInt());
        } catch (Exception e) {
            scanner.nextLine();
        }

        if (option == null) {
            System.out.println("Invalid option. Please try again.\n");
            return requestOption();
        } else return option;
    }

    public String getStockName() {
        try {
            System.out.print("Please enter a stock name: ");
            return scanner.next();
        } catch (Exception e) {
            return getStockName();
        }
    }

    public int getQuantity() {
        try {
            System.out.print("Please enter an amount: ");
            return scanner.nextInt();
        } catch (Exception e) {
            return getQuantity();
        }
    }
}
