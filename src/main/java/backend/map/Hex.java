package backend.map;

public interface Hex {
    boolean isMinionHere(); // มีมินเนี่ยนอยู่ hex นี้มั้ย
    int owner(); // ใครเป็นเจ้าของ hex นี้
    void setOwner(int owner);
    int getRow();
    int getCol();
}
