package br.com.vsgdev.promobeer.models;

/**
 * Classifica os tipos dos produtos
 * <p/>
 * Created by ascaloners on 10/23/15.
 */
public enum ItemType {
    WINE(0, "Vinho"),
    BEER(1, "Cerveja"),
    RUM(2, "Rum"),
    WHISKY(3, "Whisky"),
    VODCA(4, "Vodca"),
    ICE(5, "Ice"),
    OTHER(6,"Outro");

    private int id;
    private String name;

    ItemType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static ItemType getTypeById(final int id) {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.getId() == id) {
                return itemType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}