public class MessageConstructor {

    public static Message contructVoteRequest(Node node){
        VoteRequest vr = new VoteRequest();
        vr.setFrom(node.getServiceAddress());
        vr.setTo(new Address("",0));
        vr.setTerm(node.getTerm());
        return vr;
    }

    public static Message constructVoteReply(Node node,Address to,boolean granted){
        VoteReply vr = new VoteReply();
        vr.setFrom(node.getServiceAddress());
        vr.setTo(to);
        vr.setTerm(node.getTerm());
        vr.setGranted(granted);
        return vr;
    }

    public static Message constructApeendRequest(Node node, Change change){
        AppendRequest ar = new AppendRequest();
        ar.setFrom(node.getServiceAddress());
        ar.setTo(new Address("",0));
        ar.setTerm(node.getTerm());
        ar.setChange(change);
        ar.setCommitIndex(node.getCurrentCommitIndex());
        return ar;
    }

    public static Message contructAppendReply(Node node){
        AppendReply ar = new AppendReply();
        ar.setFrom(node.getServiceAddress());
        ar.setTo(node.getLeaderAddress());
        ar.setTerm(node.getTerm());
        ar.setCommitIndex(node.getCurrentCommitIndex());
        return ar;
    }

    public static Message constructDirectApeendRequest(Node node,Address to,Change change,int commit_index){
        AppendRequest ar = new AppendRequest();
        ar.setFrom(node.getServiceAddress());
        ar.setTo(to);
        ar.setTerm(node.getTerm());
        ar.setChange(change);
        ar.setCommitIndex(commit_index);
        return ar;
    }
}
