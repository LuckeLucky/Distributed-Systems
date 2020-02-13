public class Leader implements NodeState {

    private Node node;
    private Watch watch;


    public Leader(Node node) {
        this.node = node;
    }

    @Override
    public void work() {
        sendAppendRequests();
        this.watch = new Watch(74,75,this);
        this.watch.start();

    }

    @Override
    public void reactVoteRequest(VoteRequest message) {
        //leader don't care about vote request
    }

    @Override
    public void reactVoteReply(VoteReply message) {
        //leader don't care about vote reply
    }

    @Override
    public void reactAppendRequest(AppendRequest message) {
        // see if there are leaders
        if( message.getTerm() > this.node.getTerm()){
            this.node.setState("Follower");
        }
    }

    @Override
    public void reactAppendReply(AppendReply message) {
        if ( message.getTo().equals(this.node.getServiceAddress()) && message.getTerm()==this.node.getTerm()){
            //different commit leader and follower
            //try to sync replica
            if(this.node.getCurrentCommitIndex() > message.getCommitIndex()){
                Change c;
                c = this.node.getChange(message.getCommitIndex()+1);
                sendDirectedAppendRequest(message.getTo(),c,message.getCommitIndex()+1);

            }
        }
    }

    @Override
    public void call() {
        //call happens but state is different
        if(!this.getState().equals(this.node.getCurrentState())) {
            return;
        }
        work();
    }

    @Override
    public String getState() {
        return "Leader";
    }

    private void sendAppendRequests(){
        Change c =checkForNewChanges();
        Message m = MessageConstructor.constructApeendRequest(this.node,c);
        this.node.sendUDPMessage(m);
    }

    private Change checkForNewChanges(){
        Change c;
        c = this.node.getChange(this.node.getPreviousCommitIndex()+1);
        if(c!=null) {
            this.node.setPreviousCommitIndex(this.node.getPreviousCommitIndex() + 1);
        }
        return c;
    }
    private void sendDirectedAppendRequest(Address to, Change change, int commit_index){
        Message m = MessageConstructor.constructDirectApeendRequest(this.node,to,change,commit_index);
        this.node.sendUDPMessage(m);
    }

}
