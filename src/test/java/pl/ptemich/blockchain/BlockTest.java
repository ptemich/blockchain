package pl.ptemich.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BlockTest {

    public static List<Block> blockchain = new ArrayList<>();
    public static int prefix = 4;
    public static String prefixString = new String(new char[prefix]).replace('\0', '0');


    @BeforeAll
    public static void setUp() {
        Block genesisBlock = new Block("The is the Genesis Block.", "0", new Date().getTime());
        genesisBlock.mineBlock(prefix);
        blockchain.add(genesisBlock);

        Block firstBlock = new Block("The is the First Block.", genesisBlock.getHash(), new Date().getTime());
        firstBlock.mineBlock(prefix);
        blockchain.add(firstBlock);
    }

    @Test
    public void givenBlockchain_whenNewBlockAdded_thenSuccess() {
        String blockData = "The is a New Block.";
        String prevBlockHash = blockchain.get(blockchain.size() - 1).getHash();
        long operationTime = new Date().getTime();

        Block newBlock = new Block(blockData, prevBlockHash, operationTime);
        newBlock.mineBlock(prefix);
        assertThat(newBlock.getHash().substring(0, prefix)).isEqualTo(prefixString);
        blockchain.add(newBlock);
    }

    @Test
    public void givenBlockchain_whenValidated_thenSuccess() {
        boolean flag = true;
        for (int i = 0; i < blockchain.size(); i++) {
            String previousHash = i == 0 ? "0" : blockchain.get(i - 1).getHash();
            Block currentBlock = blockchain.get(i);
            flag = currentBlock.blockDataAndHashMatch()
                    && currentBlock.isSuccessorForBlockWithHash(previousHash)
                    && currentBlock.hashStartsWithPrefix(prefix, prefixString);
            if (!flag) {
                break;
            }
        }
        assertThat(flag).isTrue();
    }

    @AfterAll
    public static void tearDown() {
        blockchain.forEach(block -> log.debug(block.toString()));
        blockchain.clear();
    }

}
