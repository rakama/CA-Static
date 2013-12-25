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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
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
    final static String yaml_name = "prohibited.yml";
    final static String section_name = "prohibited-block-updates";
    
    final static String[] default_prohibited = { 
        "water", "stationary_water", "lava", "stationary_lava", 
        "sand", "gravel", "long_grass", "dead_bush", "yellow_flower", 
        "red_rose",  "brown_mushroom", "red_mushroom", "fire", "soil", 
        "snow", "ice", "snow_block", "cactus", "soul_sand", "vine", 
        "redstone_lamp_on", "redstone_lamp_off", "portal"};

    Set<Material> prohibited;
    
    public CAStatic()
    {
    }

    public void onDisable()
    {
    }

    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);

        YamlConfiguration yaml = loadYamlFile(new File(getDataFolder(), yaml_name));
        List<String> plist = yaml.getStringList(section_name);
        
        prohibited = new HashSet<Material>();
        for(String s : plist)
        {
            Material mat = Material.getMaterial(s.toUpperCase());
            
            if(mat != null)
                prohibited.add(mat);
            else
                Bukkit.getLogger().info("INVALID MATERIAL: " + s);
        }
    }

    protected YamlConfiguration loadYamlFile(File file)
    {
        if(!file.exists())
            return createDefaultYamlFile(file);
        else
            return YamlConfiguration.loadConfiguration(file);
    }
    
    protected YamlConfiguration createDefaultYamlFile(File file)
    {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.createSection(section_name);
        yaml.set(section_name, Arrays.asList(default_prohibited));
        
        try
        {
            yaml.save(file);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        return yaml;
    }
    
    @EventHandler
    public void onMapInitialize(MapInitializeEvent event)
    {   
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        Material new_material = event.getChangedType();
        Material material = event.getBlock().getType();
        
        // stop sand and gravel from falling
        if(prohibited.contains(new_material) || prohibited.contains(material))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        // stop fire from destroying blocks
        if(prohibited.contains(Material.FIRE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        // stop fire from spreading or igniting naturally
        if(prohibited.contains(Material.FIRE)
        &&(event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD
        || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA 
        || event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event)
    {
        Material material = event.getBlock().getType();

        // stop liquids from flowing
        if(prohibited.contains(material))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event)
    {
        Material material = event.getBlock().getType();
        
        // stop ice and snow from melting
        if(prohibited.contains(material))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event)
    {
        Material new_material = event.getNewState().getData().getItemType();
        
        // stop cacti and vines from growing
        if(prohibited.contains(new_material))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event)
    {
        Material new_material = event.getNewState().getData().getItemType();
        
        // stop snow and ice from forming
        if(prohibited.contains(new_material))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        Material material = event.getSource().getType();

        // stop fire and mushrooms from spreading
        if(prohibited.contains(material))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();        
        if(block.getType() == Material.REDSTONE_LAMP_OFF)
        {        
            // turn redstone lamp on
            event.setCancelled(true);
            lightRedstoneLamp(block);
        }
    }   
    
    private void lightRedstoneLamp(Block block)
    {
        Location below = block.getLocation().subtract(0, 1, 0);        
        Block temp = block.getWorld().getBlockAt(below);
        
        int tempid = temp.getTypeId();
        byte tempdata = temp.getData();
        
        temp.setType(Material.REDSTONE_BLOCK);
        block.setType(Material.REDSTONE_LAMP_ON);
        temp.setTypeIdAndData(tempid, tempdata, false);
    }
}