package com.github.bentaljaard.coolstore.inventory.inventory.api;

import com.github.bentaljaard.coolstore.inventory.inventory.models.Inventory;

import javax.ws.rs.NotFoundException;
import java.util.List;

public interface InventoryService {
    public Inventory getInventory(String id)
            throws NotFoundException;

    public List<Inventory> getInventoryAll()
            throws NotFoundException;
}
