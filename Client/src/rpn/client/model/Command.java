package rpn.client.model;

/**
 * This command is used within the menu for the user and has a value which can be used to
 * identify the command.
 */
public enum Command {
    BUY(0, "Buy Stock"),
    SELL(1, "Sell Stock"),
    REFRESH(2, "View Stock List"),
    OWNED(3, "View My Stock List"),
    BALANCE(4, "Check My Account Balance"),
    QUIT(5, "Quit");

    int value;
    String description;

    Command(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static Command[] list() {
        return new Command[]{
                BUY,
                SELL,
                REFRESH,
                OWNED,
                BALANCE,
                QUIT
        };
    }

    public static Command valueOf(int value) {
        switch (value) {
            case 0:
                return BUY;
            case 1:
                return SELL;
            case 2:
                return REFRESH;
            case 3:
                return OWNED;
            case 4:
                return BALANCE;
            case 5:
                return QUIT;
            default:
                return null;
        }
    }
}
