package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 25-04-2016.
 */
/*
{
        "item":{
        "id":"8"},
        "itemconfig":{
        "id":"3"}
        }*/
public class AddToMyBoxRequestBody implements Serializable {

    @SerializedName("item")
    Item item;
    @SerializedName("itemconfig")
    ItemConfig itemConfig;

    public AddToMyBoxRequestBody(Item item, ItemConfig itemConfig) {
        this.item = item;
        this.itemConfig = itemConfig;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemConfig getItemConfig() {
        return itemConfig;
    }

    public void setItemConfig(ItemConfig itemConfig) {
        this.itemConfig = itemConfig;
    }

    public static class Item {
        int id;

        public Item(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class ItemConfig {
        private int id;

        public ItemConfig(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
