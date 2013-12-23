/*
 * Copyright (c) 2012, RamsesA <ramsesakama@gmail.com>
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */

package org.craftingazeroth.plugins.cas;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CAStatic extends JavaPlugin implements Listener
{
    // TODO: fix redstone
    
    boolean enabled;

    public CAStatic()
    {
        enabled = true;
    }

    public void onDisable()
    {
    }

    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event)
    {   
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        // stop sand and gravel from falling
        if(enabled)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        // stop fire from destroying blocks
        if(enabled)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        // stop fire from spreading or igniting naturally
        if(enabled 
        &&(event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD
        || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA 
        || event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event)
    {
        // stop liquids from flowing
        if(enabled)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event)
    {
        // stop ice and snow from melting
        if(enabled)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event)
    {
        // stop cacti and vines from growing
        if(enabled)
        {
            int id = event.getNewState().getBlock().getTypeId();     
            if(id == 81 || id == 106)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event)
    {
        // stop snow and ice from forming
        if(enabled)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        // stop fire and mushrooms from spreading
        if(enabled)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        if(block.getTypeId() != 123)
            return;
        
        event.setCancelled(true);
        
        Location below = block.getLocation().subtract(0, 1, 0);        
        Block temp = block.getWorld().getBlockAt(below);
        
        int tempid = temp.getTypeId();
        byte tempdata = temp.getData();
        
        temp.setTypeId(152, false);
        block.setTypeId(124, false);
        temp.setTypeIdAndData(tempid, tempdata, false);
    }   
}