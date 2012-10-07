package com.github.rakama.plugins.cas;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CAStatic extends JavaPlugin implements Listener
{
    boolean enabled;
    Set<String> worldNames;
    Set<UUID> worlds;

    public CAStatic()
    {
        enabled = true;

        worlds = new HashSet<UUID>();
        worldNames = new HashSet<String>();

        worldNames.add("kalimdor");
        worldNames.add("kalimdor+001");
        worldNames.add("kalimdor+002");
        worldNames.add("kalimdor+003");
        worldNames.add("kalimdor+004");
        worldNames.add("kalimdor+005");
        worldNames.add("kalimdor-001");

        worldNames.add("azeroth");
        worldNames.add("azeroth+001");
        worldNames.add("azeroth+002");
        worldNames.add("azeroth-001");
    }

    public void onDisable()
    {
        worlds.clear();
    }

    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);

        for(String name : worldNames)
        {
            World w = getServer().getWorld(name);
            if(w != null)
                worlds.add(w.getUID());
        }
    }

    protected boolean processEvent(BlockEvent e)
    {
        if(!enabled)
            return false;

        return worlds.contains(e.getBlock().getWorld().getUID());
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        // stop sand and gravel from falling
        if(processEvent(event))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        // stop fire from destroying blocks
        if(processEvent(event))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        // stop fire from spreading or igniting naturally
        if(processEvent(event) 
        &&(event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD
        || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA 
        || event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event)
    {
        // stop liquids from flowing
        if(processEvent(event))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event)
    {
        // stop ice and snow from melting
        if(processEvent(event))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event)
    {
        // stop cacti (81) from growing
        if(processEvent(event))
            if(event.getNewState().getBlock().getTypeId() == 81)
                event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event)
    {
        // stop snow and ice from forming
        if(processEvent(event))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        // stop fire and mushrooms from spreading
        if(processEvent(event))
            event.setCancelled(true);
    }
}