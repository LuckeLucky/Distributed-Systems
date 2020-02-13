import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FrontEndService extends UnicastRemoteObject implements PlacesListInterface{

    private MulticastHandler multicast_handler;
    private HashMap<Address,Long> nodes;
    private Address leader_address;
    private int commit_index;

    public FrontEndService(Address multicast_address)  throws RemoteException{
        this.multicast_handler = new MulticastHandler(multicast_address);
        this.nodes=new HashMap<>();
        this.leader_address=null;
        listenUDPMessages();
    }

    private void listenUDPMessages(){
        new Thread(() -> {
            while (true) {
                try {
                    Message m=this.multicast_handler.listenMulticastMessages();
                    reactToMessage(m);
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }

    private void reactToMessage(Message message){
        switch(message.getType()){
            case "AppendRequest":
                AppendRequest arequest =(AppendRequest)message;
                this.leader_address=arequest.getFrom();
                this.commit_index=arequest.getCommitIndex();
                break;
            case "AppendReply":
                AppendReply areply =(AppendReply)message;
                if (areply.getCommitIndex()==this.commit_index) {
                    this.nodes.put(message.getFrom(), System.currentTimeMillis());
                }
                break;
        }
        deleteNodesSleeping();
    }

    private void  deleteNodesSleeping(){
        long current_time=System.currentTimeMillis();
        this.nodes.entrySet().removeIf(entries->entries.getValue()+10000 < current_time);
    }
    @Override
    public void addPlace(Place p) throws RemoteException {
        //contact leader
        PlacesListInterface pli;
        try {
            pli=(PlacesListInterface) Naming.lookup("rmi://"+this.leader_address+"/placesmanager");
            pli.addPlace(p);
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlace(String objectID) throws RemoteException {
        //contact leader
        PlacesListInterface pli;
        try {
            pli=(PlacesListInterface) Naming.lookup("rmi://"+this.leader_address+"/placesmanager");
            pli.removePlace(objectID);
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Place> allPlaces() throws RemoteException {
        //contact random service
        PlacesListInterface pli;
        try {
            pli=(PlacesListInterface) Naming.lookup("rmi://"+randomServiceAddress()+"/placesmanager");
            return pli.allPlaces();
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Place getPlace(String objectID) throws RemoteException {
        //contact random service
        PlacesListInterface pli;
        try {
            pli=(PlacesListInterface) Naming.lookup("rmi://"+randomServiceAddress()+"/placesmanager");
            return pli.getPlace(objectID);
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Address randomServiceAddress(){
        ArrayList<Address> allnodes;
        allnodes = new ArrayList<>(this.nodes.keySet());
        allnodes.add(this.leader_address);
        int randomNum = ThreadLocalRandom.current().nextInt(0, allnodes.size());
        return allnodes.get(randomNum);
    }
}
