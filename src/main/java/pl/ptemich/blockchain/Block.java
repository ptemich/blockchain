package pl.ptemich.blockchain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Slf4j
public class Block {

    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String data, String previousHash, long timeStamp) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateBlockHash();
    }

    public String calculateBlockHash() {
        StringBuffer buffer = new StringBuffer();
        String dataToHash = previousHash + timeStamp + nonce + data;

        MessageDigest digest;
        byte[] bytes;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

            for (byte b : bytes) {
                buffer.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException ex) {
            log.debug("Failed", ex);
        }

        return buffer.toString();
    }

    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }

    public boolean blockDataAndHashMatch() {
        return hash != null && hash.equals(calculateBlockHash());
    }

    public boolean isSuccessorForBlockWithHash(String previousBlockHash) {
        return previousBlockHash != null && previousBlockHash.equals(previousHash);
    }

    public boolean hashStartsWithPrefix(int prefixLength, String requiredPrefix) {
        return hash != null && hash.substring(0, prefixLength).equals(requiredPrefix);
    }

    @Override
    public String toString() {
        return "Block{" +
                " previousHash= " + previousHash +
                ", hash= " + hash +
                ", timeStamp= " + timeStamp +
                ", nonce= " + nonce +
                ", data= " + data +
                '}';
    }
}
