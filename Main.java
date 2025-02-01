package hotelManagementSystemLLD;

import java.util.*;
import java.util.Map.*;
/*1. 
 * The system should support the booking of different room types like standard, deluxe,
family suite, etc.
2. Guests should be able to search room inventory and book any available room.
3. The system should be able to retrieve information like who book a particular room or
what are the rooms booked by a specific customer.
4. The system should allow customers to cancel their booking. Full refund if the
cancelation is done before 24 hours of check-in date.
5. The system should be able to send notifications whenever the booking is near checkin
or check-out date.
6. The system should maintain a room housekeeping log to keep track of all
housekeeping tasks.
7. Any customer should be able to add room services and food items.
8. Customers can ask for different amenities.
9. The customers should be able to pay their bills through credit card, check or cash.
*/

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HotelManagementSystem hotelManagementSystem = new HotelManagementSystem();
		hotelManagementSystem.displayRooms();
		hotelManagementSystem.bookRoom(RoomCategory.STANDARD, "Subodh", 1, 3);
		hotelManagementSystem.searchReservation(SearchType.NAME, "Subodh");
	}

}
//singleton
class HotelManagementSystem {
	private RoomManager roomManager;
	private ReservationManager reservationManager;

	public HotelManagementSystem() {
		this.roomManager = RoomManager.getInstance();
		this.reservationManager = new ReservationManager();
	}
	public void bookRoom(RoomCategory category, String user, int startDate, int endDate) {
		Reservation reservation = roomManager.bookRoom(category, user, startDate, endDate);
		reservationManager.addReservation(reservation);
		System.out.println(reservation.toString());
	}
	public void displayRooms() {
		roomManager.displayInventory();
	}
	public void searchReservation(SearchType SearchType, String keyword) {
		List<Reservation> searchList = reservationManager.searchReservation(SearchType, keyword);
		for (Reservation reservation : searchList) {
			System.out.println("Search results");
			System.out.println(reservation.toString());
		}
	}
}

enum SearchType {
	NAME,
	ROOM
}

interface SearchObject {
	public List<Reservation> search(Map<String, Reservation> reservationList, String keyword);
}

class searchByName implements SearchObject{

	@Override
	public List<Reservation> search(Map<String, Reservation> reservationList, String keyword) {
		// TODO Auto-generated method stub
		List<Reservation> result = new ArrayList<Reservation>();
		for (Entry<String, Reservation> entry : reservationList.entrySet()) {
			Reservation reservation = entry.getValue();
			if (reservation.getBookBy().contains(keyword)) {
				result.add(reservation);
			}
		}
		return result;
	}
	
}

class SearchFactory {

	public static SearchObject createSearchObject(SearchType searchType) {
		// TODO Auto-generated method stub
		switch(searchType) {
		case NAME :
			return new searchByName();
		default :
			return null;
		}
	}
	
}

class ReservationManager {
	private Map<String, Reservation> reservationList;

	public ReservationManager() {
		super();
		this.reservationList = new HashMap<String, Reservation>();
	}

	public void addReservation(Reservation reservation) {
		// TODO Auto-generated method stub
		reservationList.put(reservation.getReservationID(), reservation);
	}
	public List<Reservation> searchReservation(SearchType SearchType, String keyword) {
		SearchObject searchObj = SearchFactory.createSearchObject(SearchType);
		return searchObj.search(reservationList, keyword);
	}
	
}

class Reservation {
	private String reservationID;
	private Room room;
	private String bookBy;
	private int startDate;
	private int endDate;
	
	public Reservation(Room room, String bookBy, int startDate, int endDate) {
		super();
		this.reservationID = UUID.randomUUID().toString();
		this.room = room;
		this.bookBy = bookBy;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	

	public String getReservationID() {
		return reservationID;
	}


	public void setReservationID(String reservationID) {
		this.reservationID = reservationID;
	}


	public Room getRoom() {
		return room;
	}


	public void setRoom(Room room) {
		this.room = room;
	}


	public String getBookBy() {
		return bookBy;
	}


	public void setBookBy(String bookBy) {
		this.bookBy = bookBy;
	}


	public int getStartDate() {
		return startDate;
	}


	public void setStartDate(int startDate) {
		this.startDate = startDate;
	}


	public int getEndDate() {
		return endDate;
	}


	public void setEndDate(int endDate) {
		this.endDate = endDate;
	}


	public double calculateBill() {
		return room.calculateBill(startDate, endDate);
	}

	@Override
	public String toString() {
		return "Reservation [reservationID=" + reservationID + ", room=" + room + ", bookBy=" + bookBy + ", startDate="
				+ startDate + ", endDate=" + endDate + "]";
	}
}

class RoomManager {
	private static RoomManager instance;
	private Map<RoomCategory, List<Room>> roomInventory;
	private RoomManager() {
		this.roomInventory = new HashMap<RoomCategory, List<Room>>();
		Room room1 = new Room("11", new StandardRoom(RoomCategory.STANDARD));
		Room room2 = new Room("12", new StandardRoom(RoomCategory.STANDARD));
		Room room3 = new Room("13", new DeluxeRoom(RoomCategory.DELUXE));
		List<Room> standardRooms = List.of(room1, room2);
		List<Room> deluxeRooms = List.of(room3);
		roomInventory.put(RoomCategory.DELUXE, deluxeRooms);
		roomInventory.put(RoomCategory.STANDARD, standardRooms);
	}
	public static RoomManager getInstance() {
		if (instance == null) {
			synchronized(RoomManager.class) {
				if (instance == null) {
					instance = new RoomManager();
				}
			}
		}
		return instance;
	}
	public Reservation bookRoom(RoomCategory category, String user, int startDate, int endDate) {
		Room room = findAvailableRoom(category, startDate, endDate);
		Reservation reservation = new Reservation(room, user, 1, 3);
		room.blockDate(startDate, endDate, reservation.getReservationID());
		return reservation;
	}
	private Room findAvailableRoom(RoomCategory category, int startDate, int endDate) {
		// TODO Auto-generated method stub
		return roomInventory.get(category).get(0);
	}
	public void displayInventory() {
		for (Entry<RoomCategory, List<Room>> entry : roomInventory.entrySet()) {
			RoomCategory currentcategory = entry.getKey();
			List<Room> roomList = entry.getValue();
			System.out.println("Showing rooms for "+ currentcategory);
			for (Room room : roomList) {
				System.out.println(room.toString());
			}
		}
	}
}

enum RoomCategory {
	STANDARD,
	DELUXE,
	SUITE
}

class Calender {
	private String[] days = new String[31];// each place has a string representing a booking id
	public Calender() {
		for (int i = 0; i < 31; i++) {
			days[i] = null;
		}
	}
	public void blockDates(int startDate, int endDate, String reservationID) {
		for (int i = startDate; i < endDate; i++) {
			days[i] = reservationID;
		}
	}
}

class Room {
	private String roomID;
	private RoomType roomType; 
	private Calender calenderForMonth;
	public Room(String roomID, RoomType category) {
		this.roomID = roomID;
		this.roomType = category;
		this.calenderForMonth = new Calender();
	}
	public void blockDate(int startDate, int endDate, String reservationID) {
		// TODO Auto-generated method stub
		calenderForMonth.blockDates(startDate, endDate, reservationID);
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
	public RoomType getRoomType() {
		return roomType;
	}
	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}
	public Calender getCalenderForMonth() {
		return calenderForMonth;
	}
	public void setCalenderForMonth(Calender calenderForMonth) {
		this.calenderForMonth = calenderForMonth;
	}
	public boolean isAvailable(int startDay, int endDay) {
		return true;
	}
	public double calculateBill(int startDate, int endDate) {
		return roomType.calculateCost(endDate - startDate);
	}
	@Override
	public String toString() {
		return "Room [roomID=" + roomID + ", roomType=" + roomType + ", calenderForMonth=" + calenderForMonth + "]";
	}
	
}

abstract class RoomType {
	private RoomCategory roomCategory;
	
	public RoomType(RoomCategory roomCategory) {
		super();
		this.roomCategory = roomCategory;
	}

	public abstract double calculateCost(int noOfDays);
}
class StandardRoom extends RoomType{

	
	public StandardRoom(RoomCategory roomCategory) {
		super(roomCategory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateCost(int noOfDays) {
		// TODO Auto-generated method stub
		return noOfDays * 1000;
	}
	
}

class DeluxeRoom extends RoomType{

	
	public DeluxeRoom(RoomCategory roomCategory) {
		super(roomCategory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateCost(int noOfDays) {
		// TODO Auto-generated method stub
		return noOfDays * 2000;
	}
	
}
class SuiteRoom extends RoomType{

	
	public SuiteRoom(RoomCategory roomCategory) {
		super(roomCategory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateCost(int noOfDays) {
		// TODO Auto-generated method stub
		return noOfDays * 3000;
	}
	
}
