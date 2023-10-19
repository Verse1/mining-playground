package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class MajorityMiner extends CompliantMiner implements Miner {

    protected Block curr;
    private NetworkStatistics network;
    private boolean majority;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentHead() {
        if (this.curr.getHeight()>=this.currentHead.getHeight()||this.majority) {
            return this.curr;
        }
        return this.currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if (isMinerMe) {
            if (block.getHeight() > this.curr.getHeight()) {
                this.curr = block;
            }
            if (majority && getHashRate() <= this.network.getTotalHashRate() / 2) {
                this.currentHead = this.curr;
            }
        } else {
            if (block.getHeight() > this.currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.network = statistics;

        if (getHashRate()>this.network.getTotalHashRate() / 2) {
            this.majority = true;
        } else {
            this.majority = false;
            if (this.curr.getHeight() > this.currentHead.getHeight()) {
                this.currentHead = this.curr;
            }
            this.curr = this.currentHead;
        }
    }

    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.curr = genesis;
        this.network = networkStatistics;
        this.currentHead = genesis;
    }

    @Override
    public Block currentlyMiningAt() {
        return this.curr;
    }
}
