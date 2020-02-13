import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Follower implements NodeState {
    private Node node;
    private Watch watch;


    public Follower(Node node) {

        this.node = node;

    }

    @Override
    public void work() {
        this.watch = new Watch(150,300,this);
        this.watch.start();
    }

    @Override
    public void reactVoteRequest(VoteRequest message) {
        this.watch.reset();
        Message vr=null;
        //term superior or equal vote true
        if(this.node.getTerm() <= message.getTerm() && this.node.getCandidateAddress() == null ){
            vr = MessageConstructor.constructVoteReply(this.node,message.getFrom(),true);
            this.node.setCandidateAddress(message.getFrom());
            this.node.setTerm(message.getTerm());
        }
        if(this.node.getCandidateAddress()!=null){
            vr = MessageConstructor.constructVoteReply(this.node,message.getFrom(),false);
        }
        this.node.sendUDPMessage(vr);
        this.watch.reset();
    }

    @Override
    public void reactVoteReply(VoteReply message) {
        //followers don't care about vote reply's
    }

    @Override
    public void reactAppendRequest(AppendRequest message) {
        this.watch.reset();

        //synchronise whit leader
        if(this.node.getTerm() < message.getTerm() ||
                (this.node.getTerm()== message.getTerm() && this.node.getLeaderAddress() == null)){
            this.node.setTerm(message.getTerm());
            this.node.setLeaderAddress(message.getFrom());
            this.node.setCurrentCommitIndex(message.getCommitIndex());
			this.node.setCandidateAddress(null);
            retrieveChangesFromLeader();
            this.watch.reset();
            return;
        }


        if(this.node.getLeaderAddress().equals(message.getFrom())){
            if(this.node.getCurrentCommitIndex()+1 == message.getCommitIndex()) {
                Change c = message.getChange();
                if (c != null) {
                    switch (c.getMethod()) {
                        case "ADD":
                            this.node.addPlace(c.getPlace());
                            break;
                        case "REMOVE":
                            this.node.removePlace(c.getPlace());
                            break;
                    }
                    this.node.setCurrentCommitIndex(message.getCommitIndex());
                }
            }
            answerAppendRequest();
        }

        this.watch.reset();
    }

    @Override
    public void reactAppendReply(AppendReply message) {
        //follower don't care about append reply's
    }

    @Override
    public void call() {
        //time is up start candidacy
        this.node.setState("Candidate");
    }

    @Override
    public String getState() {
        return "Follower";
    }


    private void retrieveChangesFromLeader(){
        //contact leader
        PlacesListInterface pli;
        ArrayList places =null;
        try {
            pli=(PlacesListInterface) Naming.lookup("rmi://"+this.node.getLeaderAddress()+"/placesmanager");
            places = pli.allPlaces();
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
        this.node.setAllPlaces(places);
    }

    private void answerAppendRequest(){
        Message ar;
        ar= MessageConstructor.contructAppendReply(this.node);
        this.node.sendUDPMessage(ar);

    }
}
