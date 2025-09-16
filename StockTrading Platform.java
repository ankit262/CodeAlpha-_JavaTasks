import java.util.*;
import java.io.*;

// ----- Stock Class -----
class Stock {
    String symbol;
    String name;
    double price;

    Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    void updatePrice(double newPrice) {
        this.price = newPrice;
    }

    @Override
    public String toString() {
        return symbol + " (" + name + ") - $" + price;
    }
}

// ----- User Class -----
class User {
    String username;
    double balance;
    Map<String, Integer> portfolio; // symbol -> quantity

    User(String username, double balance) {
        this.username = username;
        this.balance = balance;
        this.portfolio = new HashMap<>();
    }

    void buyStock(Stock stock, int quantity) {
        double cost = stock.price * quantity;
        if (cost > balance) {
            System.out.println("❌ Insufficient balance to buy " + quantity + " shares of " + stock.symbol);
            return;
        }
        balance -= cost;
        portfolio.put(stock.symbol, portfolio.getOrDefault(stock.symbol, 0) + quantity);
        System.out.println("✅ Bought " + quantity + " shares of " + stock.symbol);
    }

    void sellStock(Stock stock, int quantity) {
        if (!portfolio.containsKey(stock.symbol) || portfolio.get(stock.symbol) < quantity) {
            System.out.println("❌ Not enough shares to sell.");
            return;
        }
        portfolio.put(stock.symbol, portfolio.get(stock.symbol) - quantity);
        balance += stock.price * quantity;
        System.out.println("✅ Sold " + quantity + " shares of " + stock.symbol);
    }

    void showPortfolio(Map<String, Stock> market) {
        System.out.println("\n📊 Portfolio of " + username + ":");
        double totalValue = balance;
        for (String symbol : portfolio.keySet()) {
            int qty = portfolio.get(symbol);
            double stockValue = market.get(symbol).price * qty;
            System.out.println(symbol + ": " + qty + " shares worth $" + stockValue);
            totalValue += stockValue;
        }
        System.out.println("💰 Balance: $" + balance);
        System.out.println("📈 Total Portfolio Value: $" + totalValue);
    }
}

// ----- Trading Platform Class -----
public class TradingPlatform {
    private static Map<String, Stock> market = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize Market Data
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 150));
        market.put("GOOG", new Stock("GOOG", "Alphabet Inc.", 2800));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 750));
        market.put("AMZN", new Stock("AMZN", "Amazon Inc.", 3400));

        // Create a user
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        User user = new User(username, 10000); // starting balance $10,000

        // Menu-driven trading
        while (true) {
            System.out.println("\n===== STOCK TRADING PLATFORM =====");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Save Portfolio");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidInt();
            switch (choice) {
                case 1:
                    showMarket();
                    break;
                case 2:
                    buyOperation(user);
                    break;
                case 3:
                    sellOperation(user);
                    break;
                case 4:
                    user.showPortfolio(market);
                    break;
                case 5:
                    savePortfolio(user);
                    break;
                case 6:
                    System.out.println("🚪 Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void showMarket() {
        System.out.println("\n📊 Current Market Data:");
        for (Stock stock : market.values()) {
            System.out.println(stock);
        }
    }

    private static void buyOperation(User user) {
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.next().toUpperCase();
        if (!market.containsKey(symbol)) {
            System.out.println("❌ Stock not found!");
            return;
        }
        System.out.print("Enter quantity: ");
        int qty = getValidInt();
        user.buyStock(market.get(symbol), qty);
    }

    private static void sellOperation(User user) {
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.next().toUpperCase();
        if (!market.containsKey(symbol)) {
            System.out.println("❌ Stock not found!");
            return;
        }
        System.out.print("Enter quantity: ");
        int qty = getValidInt();
        user.sellStock(market.get(symbol), qty);
    }

    private static void savePortfolio(User user) {
        try (FileWriter writer = new FileWriter("portfolio.txt")) {
            writer.write("Portfolio of " + user.username + "\n");
            writer.write("Balance: $" + user.balance + "\n");
            for (String symbol : user.portfolio.keySet()) {
                writer.write(symbol + ": " + user.portfolio.get(symbol) + " shares\n");
            }
            System.out.println("✅ Portfolio saved to portfolio.txt");
        } catch (IOException e) {
            System.out.println("❌ Error saving portfolio: " + e.getMessage());
        }
    }

    private static int getValidInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
    }
}
 
