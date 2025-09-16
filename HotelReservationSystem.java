import java.util.*;
import java.util.Scanner;

class User {
    private String name;
    private String email;
    private String phoneNumber;

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

class Room {
    private String roomNumber;
    private String roomType;
    private double price;
    private boolean isAvailable;

    public Room(String roomNumber, String roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = true;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}

class Hotel {
    private String name;
    private List<Room> rooms;

    public Hotel(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> findAvailableRooms(String checkIn, String checkOut) {
        // Implement logic to check room availability based on check-in and check-out dates
        // This would involve iterating over rooms and checking if they are available for the given period
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
}

class Reservation {
    private String reservationId;
    private User guest;
    private String checkIn;
    private String checkOut;
    private Room room;
    private boolean isConfirmed;
    private boolean isCancelled;
    private double cancellationFee;

    public Reservation(User guest, String checkIn, String checkOut, Room room, double cancellationFee) {
        this.reservationId = generateReservationId();
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.room = room;
        this.isConfirmed = false;
        this.isCancelled = false;
        this.cancellationFee = cancellationFee;
    }

    private String generateReservationId() {
        // Implement logic to generate a unique reservation ID
        return "R" + System.currentTimeMillis(); // Placeholder
    }

    public String getReservationId() {
        return reservationId;
    }

    public User getGuest() {
        return guest;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void confirmReservation() {
        isConfirmed = true;
        // Send confirmation email or notification to guest
    }

    public void cancelReservation() {
        isCancelled = true;
        // Calculate and charge cancellation fee
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Hotel hotel = new Hotel("My Hotel");
        hotel.addRoom(new Room("101", "Deluxe", 100.0));
        hotel.addRoom(new Room("102", "Standard", 80.0));

        // User interface to search for rooms, make reservations, etc.
        Scanner scanner = new Scanner(System.in);

        // Example usage
        User user = new User("John Doe", "johndoe@example.com", "9876543210");
        List<Room> availableRooms = hotel.findAvailableRooms("2024-09-01", "2024-09-05");
        if (!availableRooms.isEmpty()) {
            Room selectedRoom = availableRooms.get(0); // Assuming user selects first available room
            Reservation reservation = new Reservation(user, "2024-09-01", "2024-09-05", selectedRoom, 50.0);
            reservation.confirmReservation();
            System.out.println("Reservation confirmed. Reservation ID: " + reservation.getReservationId());
        } else {
            System.out.println("No rooms available for the specified dates.");
        }

       
    }
}
