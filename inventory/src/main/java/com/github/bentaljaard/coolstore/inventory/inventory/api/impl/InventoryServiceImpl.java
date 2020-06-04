package com.github.bentaljaard.coolstore.inventory.inventory.api.impl;

import com.github.bentaljaard.coolstore.inventory.inventory.api.InventoryService;
import com.github.bentaljaard.coolstore.inventory.inventory.models.Inventory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class InventoryServiceImpl implements InventoryService {

    @Override
    public Inventory getInventory(String id) throws NotFoundException {
        return Inventory.findById(Long.parseLong(id));
    }

    @Override
    public List<Inventory> getInventoryAll() throws NotFoundException {
        return Inventory.listAll();
    }
}
