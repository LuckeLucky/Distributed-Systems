public class Candidate implements NodeState {

    private Node node;
    private Watch watch;

    private int number_accepts;
    private int number_rejects;
    private int number_votes;

    public Candidate(Node node) {
        this.node = node;
    }

    @Override
    public void work() {
        this.watch = new Watch(150,300,this);
        startCandidacy();
    }

    @Override
    public void reactVoteRequest(VoteRequest message) {
        //candidate in same term
        if(message.getTerm() == this.node.getTerm()){
            Message vr = MessageConstructor.constructVoteReply(this.node,message.getFrom(),false);
            this.node.sendUDPMessage(vr);
        }

    }

    @Override
    public void reactVoteReply(VoteReply message) {
        //votes to me
        if(this.node.getServiceAddress().equals(message.getTo())){
            if(message.isGranted()){
                this.number_accepts++;
            }
            else{
                this.number_rejects++;
            }
            this.number_votes++;
        }
    }

    @Override
    public void reactAppendRequest(AppendRequest message) {
        //if there is a leader step down
        if (message.getTerm() >= this.node.getTerm()){
            this.node.setState("Follower");
        }
    }

    @Override
    public void reactAppendReply(AppendReply message) {
        //Candidate don't care about append reply's
    }

    @Override
    public void call() {
        //call happens but state is different
        if(!this.getState().equals(this.node.getCurrentState())){
            return;
        }
        if( winCandidacy()){
            this.node.setLeaderAddress(this.node.getServiceAddress());
            this.node.setState("Leader");
        }
        else{
            this.node.setState("Follower");
        }
    }

    @Override
    public String getState() {
        return "Candidate";
    }



    private boolean winCandidacy(){
        return (this.number_accepts > this.number_votes*0.5 && this.number_accepts!=this.number_rejects);
    }

    private void startCandidacy(){
        //Increment term
        this.node.setTerm(this.node.getTerm()+1);

        //send vote request
        Message m= MessageConstructor.contructVoteRequest(this.node);
        this.node.sendUDPMessage(m);

        //vote in me
        this.number_accepts=1;
        this.number_votes=1;
        this.number_rejects=0;
        //set timer
        this.watch.start();
    }
}
