import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PlacesManager extends UnicastRemoteObject implements PlacesListInterface {
    private ArrayList<Place> places;
    private Node node;

    private Address service_address;

    public PlacesManager(Address service_address,Address multicast_address)  throws RemoteException{
        this.service_address = service_address;
        places = new ArrayList<>();
        node = new Node(service_address,multicast_address,this);
    }

    @Override
    public void addPlace(Place p) throws RemoteException {
        if(this.service_address.equals(this.node.getLeaderAddress())){
            this.node.newChange("ADD",p);
        }
        this.places.add(p);
    }

    @Override
    public void removePlace(String objectID) throws RemoteException {
        Place p = getPlace(objectID);
        if( p ==null){
            return;
        }
        if(this.service_address.equals(this.node.getLeaderAddress())){
            this.node.newChange("REMOVE",p);
        }
        this.places.remove(p);
    }

    @Override
    public ArrayList allPlaces() throws RemoteException {
        return this.places;
    }

    @Override
    public Place getPlace(String objectID) throws RemoteException {
        for(int i=0;i<this.places.size();i++){
            if(this.places.get(i).getPostalCode().equals(objectID))
                return this.places.get(i);
        }
        return null;
    }


    public void setAllPlaces(ArrayList<Place> places){
        this.places=places;
    }
}