package rpn.client.model;

public enum Command {
    BUY     (0, "Buy Stock"),
    SELL    (1, "Sell Stock"),
    REFRESH (2, "View Stock List"),
    BALANCE (3, "Check My Account Balance"),
    QUIT    (4, "Quit");

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
        return new Command[] {
                BUY,
                SELL,
                REFRESH,
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
                return BALANCE;
            case 4:
                return QUIT;
            default:
                return null;
        }
    }
}
