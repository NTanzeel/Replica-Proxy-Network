package rpn.client.io;

import rpn.client.model.Command;

import java.util.Scanner;

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
        System.out.print("Option: ");

        return Command.valueOf(scanner.nextInt());
    }

    public String getStockName() {
        System.out.print("Please enter a stock name: ");
        return scanner.next();
    }

    public int getQuantity() {
        System.out.print("Please enter an amount: ");
        return scanner.nextInt();
    }
}
