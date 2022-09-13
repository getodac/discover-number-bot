package md.moro.tbot.numberdiscovery;

record GuessResult(int digits,int positions) {

    public boolean isGuessed() {
        return digits == 4 && positions == 4;
    }
}
