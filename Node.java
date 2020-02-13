import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Node implements Runnable {
    private Address service_address;
    private MulticastHandler multicast_handler;
    private PlacesManager places_manager;

    private Address leader_address;
    private Address candidate_address;
    private int term;
    private int current_commit_index;
    private int previous_commit_index;
    private NodeState follower_state;
    private NodeState candidate_state;
    private NodeState leader_state;
    private NodeState current_state;

    private HashMap<Integer, Change> changes;

    public Node(Address service_address, Address multicast_address, PlacesManager places_manager) {
        this.service_address = service_address;
        this.multicast_handler = new MulticastHandler(multicast_address);
        this.places_manager = places_manager;

        this.leader_address = null;
        this.candidate_address = null;
        this.term = 0;
        this.current_commit_index=0;
        this.previous_commit_index=0;
        this.follower_state = new Follower(this);
        this.candidate_state = new Candidate(this);
        NodeState ifs = new Leader(this);
        this.leader_state=(Leader) ifs;
        this.leader_state = new Leader(this);
        this.current_state = null;

        changes = new HashMap<>();

        Thread t = new Thread(this);
        t.start();

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

    public void sendUDPMessage(Message message){
        try {
            this.multicast_handler.sendMulticastMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reactToMessage(Message message){
        //Ignore sent message
        if(this.service_address.equals(message.getFrom())){
            return;
        }
        //Ignore messages from inferior term
        if(this.getTerm() > message.getTerm()){
            return;
        }
        switch(message.getType()){
            case "VoteRequest":
                this.current_state.reactVoteRequest((VoteRequest) message);
                break;
            case "VoteReply":
                this.current_state.reactVoteReply((VoteReply) message);
                break;
            case "AppendRequest":
                this.current_state.reactAppendRequest((AppendRequest) message);
                break;
            case "AppendReply":
                this.current_state.reactAppendReply((AppendReply) message);
                break;
        }
    }

    public void setState(String state){

        switch (state){
            case "Follower":
                this.current_commit_index=0;
                this.setCandidateAddress(null);
                this.setLeaderAddress(null);
                this.current_state = this.follower_state;
                break;
            case "Candidate":
                this.setCandidateAddress(this.service_address);
                this.setLeaderAddress(null);
                this.current_state = this.candidate_state;
                break;
            case "Leader":
				this.changes.clear();
                this.current_commit_index=0;
                this.previous_commit_index=0;
                this.setLeaderAddress(this.service_address);
                this.current_state = this.leader_state;
                break;
        }
        this.current_state.work();
    }

    public Address getServiceAddress() {
        return service_address;
    }

    public Address getCandidateAddress() {
        return candidate_address;
    }

    public void setCandidateAddress(Address candidate_address) {
        this.candidate_address = candidate_address;
    }

    public Address getLeaderAddress() {
        return leader_address;
    }

    public void setLeaderAddress(Address leader_address) {
        this.leader_address = leader_address;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getCurrentCommitIndex() {
        return current_commit_index;
    }

    public void setCurrentCommitIndex(int current_commit_index) {
        this.current_commit_index = current_commit_index;
    }

    public int getPreviousCommitIndex() {
        return previous_commit_index;
    }

    public void setPreviousCommitIndex(int previous_commit_index) {
        this.previous_commit_index = previous_commit_index;
    }

    public void newChange(String method, Place p){
        if(p!=null) {
            Change c = new Change(method, p);
            this.setCurrentCommitIndex(this.getCurrentCommitIndex() + 1);
            changes.put(this.getCurrentCommitIndex(), c);
        }
    }

    public void setAllPlaces(ArrayList<Place> places){
        this.places_manager.setAllPlaces(places);
    }

    public void removePlace(Place p){
        try {
            this.places_manager.removePlace(p.getPostalCode());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addPlace(Place p){
        try {
            this.places_manager.addPlace(p);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Change getChange(int index){
        return changes.get(index);
    }

    public String getCurrentState(){
        return this.current_state.getState();
    }

    @Override
    public void run() {
        setState("Follower");
        listenUDPMessages();

    }
}
