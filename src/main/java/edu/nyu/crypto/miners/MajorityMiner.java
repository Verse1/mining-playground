package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class MajorityMiner extends CompliantMiner implements Miner {

    protected Block curr;
    private NetworkStatistics network;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentHead() {
        // if (curr.getHeight() >= this.currentHead.getHeight()) {
        //     return curr;
        // }
        // return this.currentHead;

        return this.curr;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if (isMinerMe) {
            this.curr = block;

            if (getHashRate() > network.getTotalHashRate() / 2) {
                this.currentHead = block;
            }
        } else {
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.network = statistics;
    }

    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.curr = genesis;
        this.network = networkStatistics;
    }

    @Override
    public Block currentlyMiningAt() {
        if (getHashRate() > network.getTotalHashRate() / 2) {
            return curr;
        }
        return currentHead;
    }
}
