package Schedule;

import Files.CSVWriter;

import java.util.*;
public class RoomSystem extends Observable {

    public ArrayList<Room> roomList;
    public HashMap<Room, RoomScheduleManager> roomScheduleManagerList;
    public RoomStorage roomStorage;
    public TalkSystem talkSystem;
    public ScheduleSystem scheduleSystem;

    public RoomSystem(){
        this.roomStorage = new RoomStorage();
        this.talkSystem = new TalkSystem();
        this.roomList = new ArrayList<Room>();
        this.roomScheduleManagerList = new HashMap<Room, RoomScheduleManager>();
    }

    public void run(){
        this.addObserver(this.talkSystem.talkManager);
        if (this.talkSystem.orgScheduleController != null) {
            this.addObserver(this.talkSystem.orgScheduleController);
        }
        TxtIterator txtIterator = new TxtIterator("phase1/src/Resources/RoomFile");
        for(String room: txtIterator.getProperties()){
            roomStorage.createRoom(room);
        }
        setRoomStorage();
        setRoomList(this.roomStorage.getRoomList());
        setRoomScheduleManagerList(this.roomStorage.getScheduleList());

        talkSystem.run();
    }

    public void setRoomList(ArrayList<Room> roomlst) {
        this.roomList = roomlst;
        setChanged();
        notifyObservers(roomList);
    }

    public void setRoomScheduleManagerList(HashMap<Room, RoomScheduleManager> roomSchedList) {
        this.roomScheduleManagerList = roomSchedList;
        setChanged();
        notifyObservers(roomScheduleManagerList);
    }

    public void setRoomStorage(){
        setChanged();
        notifyObservers(this.roomStorage);
    }

    public ArrayList<Room> getRoomList() {
        return roomList;
    }

    /**
     * Method to write the changes to the RoomFile, called in MainMenuController.logout().
     */
    public void save() {
        CSVWriter csvWriter = new CSVWriter();
        csvWriter.writeToRooms("phase1/src/Resources/RoomFile", this.getRoomList());
    }
}
