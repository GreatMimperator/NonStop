package ru.miron.TZ.givenClasses;

import java.util.Map;

import ru.miron.TZ.logic.util.json.JSONConvertable;
import ru.miron.TZ.logic.util.json.types.JSONValue;
import ru.miron.TZ.logic.util.json.types.values.JSONMap;
import ru.miron.TZ.logic.util.json.types.values.JSONString;

/**
 * Contains required {@code Dragon} with optional {@code key} and {@code ownerLogin}
 */
public class DragonWithKeyAndOwner implements JSONConvertable {
    /**
     * Cannot be {@code null}
     */
    private Dragon dragon;
    /**
     * Can be {@code null} if process doesn't need it 
     */
    private String key;
    /**
     * Can be {@code null} if process doesn't need it 
     */
    private String ownerLogin;

    /**
     * for creating own f. methods 
     */
    private DragonWithKeyAndOwner() {}

    /**
     * @param dragon not {@code null} value
     * @param key can be {@code null}
     * @param ownerLogin can be {@code null}
     * 
     * @throws IllegalArgumentException if dragon is {@code null}
     */
    public DragonWithKeyAndOwner(Dragon dragon, String key, String ownerLogin) throws IllegalArgumentException {
        setDragon(dragon);
        setKey(key);
        setOwnerLogin(ownerLogin);
    }

    /**
     * @param dragon not {@code null} value
     * 
     * @throws IllegalArgumentException if dragon is {@code null}
     */
    public DragonWithKeyAndOwner(Dragon dragon) throws IllegalArgumentException {
        this(dragon, null, null);
    }

    public Dragon getDragon() {
        return dragon;
    }

    /**
     * @param dragon not {@code null} value
     * 
     * @throws IllegalArgumentException if dragon is {@code null}
     */
    public void setDragon(Dragon dragon) throws IllegalArgumentException {
        if (dragon == null) {
            throw new IllegalArgumentException();
        }
        this.dragon = dragon;
    }

    public String getKey() {
        return key;
    }

    /**
     * @param key can be {@code null} if process doesn't need it
     */
    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    /**
     * @param ownerLogin can be {@code null} if process doesn't need it
     */
    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    @Override
    public JSONMap toJSON() {
        JSONMap root = new JSONMap();
        if (ownerLogin != null) {
            root.put("owner login", new JSONString(ownerLogin));
        }
        if (key != null) {
            root.put("key", new JSONString(key));
        }
        root.putValues(dragon.toJSON().asMap());
        return root;
    }

    /**
     * @throws IllegalArgumentException if root hasn't any required keys or has wrong types on known keys
     */
    public static DragonWithKeyAndOwner initFromTheRoot(Map<String, JSONValue> root) throws IllegalArgumentException {
        var dragonWithKeyAndOwner = new DragonWithKeyAndOwner();
        var keyJSONValue = root.get("key");
        var ownerLoginJSONValue = root.get("owner login");
        try {
            if (keyJSONValue != null) {
                dragonWithKeyAndOwner.setKey(keyJSONValue.asString().getValue());
            }
            if (ownerLoginJSONValue != null) {
                dragonWithKeyAndOwner.setOwnerLogin(ownerLoginJSONValue.asString().getValue());
            }
            dragonWithKeyAndOwner.setDragon(Dragon.initFromTheRoot(root));
            return dragonWithKeyAndOwner;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isFull() {
        return ownerLogin != null && key != null && dragon.isFull();
    }

    public boolean hasKey() {
        return key != null; 
    }
}
