public interface NodeState {

    void work();
    void reactVoteRequest(VoteRequest message);
    void reactVoteReply(VoteReply message);
    void reactAppendRequest(AppendRequest message);
    void reactAppendReply(AppendReply message);
    void call();
    String getState();
}
