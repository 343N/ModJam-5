package com.jarhax.jewelersconstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;
import com.jarhax.jewelersconstruct.addons.tcon.AddonManager;
import com.jarhax.jewelersconstruct.addons.tcon.AddonTcon;
import com.jarhax.jewelersconstruct.api.JewelryHelper;
import com.jarhax.jewelersconstruct.api.material.Material;
import com.jarhax.jewelersconstruct.api.modifier.Modifier;
import com.jarhax.jewelersconstruct.api.part.PartType;
import com.jarhax.jewelersconstruct.api.trinket.TrinketType;
import com.jarhax.jewelersconstruct.client.gui.GuiHandler;
import com.jarhax.jewelersconstruct.network.PacketStartPartShape;
import com.jarhax.jewelersconstruct.network.PacketSyncPartShape;
import com.jarhax.jewelersconstruct.network.PacketSyncTrinketForge;
import com.jarhax.jewelersconstruct.proxy.CommonProxy;

import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.ModUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = JewelersConstruct.MOD_ID, name = "Jewelers Construct", version = "@VERSION@", dependencies = "required-after:bookshelf;required-after:baubles;")
public class JewelersConstruct {
    
    public static final String MOD_ID = "jewelersconstruct";
    public static final Logger LOG = LogManager.getLogger("Jewelers Construct");
    public static final RegistryHelper REGISTRY = new RegistryHelper().setTab(new CreativeTabJewelersConstruct()).enableAutoRegistration();
    
    @SidedProxy(clientSide = "com.jarhax.jewelersconstruct.proxy.ClientProxy", serverSide = "com.jarhax.jewelersconstruct.proxy.CommonProxy")
    public static CommonProxy PROXY;
    
    @Mod.Instance(MOD_ID)
    public static JewelersConstruct INSTANCE;
    
    public static final NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    
    @EventHandler
    public void onConstruction (FMLConstructionEvent event) {
        
        AddonManager.init(event.getASMHarvestedData());
    }
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        NETWORK.register(PacketSyncPartShape.class, Side.SERVER);
        NETWORK.register(PacketStartPartShape.class, Side.SERVER);
        NETWORK.register(PacketSyncTrinketForge.class, Side.SERVER);
        
        LOG.info("Creating registries!");
        createForgeRegistry("modifiers", Modifier.class);
        createForgeRegistry("materials", Material.class);
        createForgeRegistry("part_types", PartType.class);
        createForgeRegistry("trinket_types", TrinketType.class);
        LOG.info("Registries created!");
        
        Content.registerBlocks(REGISTRY);
        Content.registerItems(REGISTRY);
        
    }
    
    @EventHandler
    public void init (FMLInitializationEvent event) {
        
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        PROXY.registerRenders();
    }
    
    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        
        logRegistry("modifier", JewelryHelper.MODIFIERS);
        logRegistry("material", JewelryHelper.MATERIALS);
        logRegistry("part type", JewelryHelper.PART_TYPES);
        logRegistry("trinket type", JewelryHelper.TRINKET_TYPES);
        Content.associateItemsToMaterial();
    }
    
    private static void logRegistry (String name, IForgeRegistry<?> registry) {
        
        final Multimap<String, ?> sorted = ModUtils.getSortedEntries(registry);
        LOG.info("The {} registry loaded with {} entries from {} mod(s).", name, sorted.values().size(), sorted.keySet().size());
    }
    
    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createForgeRegistry (String name, Class<T> type) {
        
        return new RegistryBuilder<T>().setName(new ResourceLocation(MOD_ID, name)).setType(type).setMaxID(Integer.MAX_VALUE >> 5).create();
    }
}
