package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class SelfishMiner extends CompliantMiner implements Miner {

    protected Block curr;
    protected NetworkStatistics network;

    public SelfishMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
    }

    @Override
    public Block currentlyMiningAt() {
        // always mine on the private block
        return this.currentHead;
    }

    @Override
    public Block currentHead() {

        double relativeHashrate = (double) this.getHashRate() / this.network.getTotalHashRate();
        // if cant selfishly mine just announce the block
        if ((relativeHashrate<=0.333
                || this.curr.getHeight() - this.currentHead.getHeight() >= 2 ||
                (relativeHashrate<=0.25&&!hasFiftyPercentChance())) &&
                this.curr.getHeight() >= this.currentHead.getHeight()) {
            return this.curr;
        }
        // else selfishly mine and announce the public block
        return this.currentHead;
    }

@Override
    public void blockMined(Block block, boolean isMinerMe) {
        if (isMinerMe) {
            if (block.getHeight() > this.curr.getHeight()) {
                this.curr = block;
            }
        } else {
            if (block.getHeight() > this.currentHead.getHeight()) {
                this.currentHead = block;
            }

            // If another miner announces a block and it's at the same height as our private block
            if (this.curr.getHeight()-this.currentHead.getHeight()>=1) {
                this.currentHead = this.curr;
            } 

            if (this.currentHead.getHeight() > this.curr.getHeight()) {
                this.curr = this.currentHead;
            }
        }
    }

    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.curr = genesis;
        this.currentHead = genesis;
        this.network = networkStatistics;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.network = statistics;
    }

// not sure if this is correct need to work more on it
private boolean hasFiftyPercentChance() {
    double minerConnectivity = (double) this.getConnectivity();
    double networkConnectivity = (double) this.network.getTotalConnectivity();
    
    double averageOtherMinerConnectivity = (networkConnectivity - minerConnectivity) / (networkConnectivity / minerConnectivity);
    double probability = minerConnectivity / (minerConnectivity + averageOtherMinerConnectivity);
    return probability >= 0.5;
}
}
