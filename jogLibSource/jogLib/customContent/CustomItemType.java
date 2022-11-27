package jogLib.customContent;

import jogUtil.data.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.*;
import org.bukkit.plugin.*;

import java.util.*;

public abstract class CustomItemType<ObjectType extends CustomItemType.CustomItem> extends CustomObjectType<ItemMeta>
{
	Material material;
	String name;
	
	public CustomItemType(NamespacedKey key, Material material, String name)
	{
		super(key);
		this.material = material;
		this.name = name;
	}
	
	protected abstract void configureMeta(ItemMeta meta);
	protected abstract void finalizeStack(ItemStack stack);
	protected abstract ObjectType getObject(ItemMeta meta);
	protected abstract ObjectType getObject(ItemStack stack);
	
	@Override
	CustomItem getObject(PersistentDataHolder holder)
	{
		if (holder instanceof ItemMeta)
		{
			return new CustomItem((ItemMeta)holder);
		}
		else
			return null;
	}
	
	@Override
	protected ItemMeta createObject()
	{
		return createStack().getItemMeta();
	}
	
	public ItemStack createStack()
	{
		ItemStack stack = new ItemStack(material);
		ItemMeta meta = stack.getItemMeta();
		configureMeta(meta);
		ContentManager.makeCustomObject(meta, id);
		meta.setDisplayName(name);
		stack.setItemMeta(meta);
		finalizeStack(stack);
		return stack;
	}
	
	public static boolean isCustomItem(ItemStack item)
	{
		if (item == null)
			return false;
		return isCustomItem(item.getItemMeta());
	}
	
	public static CustomItem getCustomObject(ItemStack stack)
	{
		if (isCustomItem(stack))
			return getType(stack).getObject(stack);
		else
			return null;
	}
	
	public static boolean isCustomItem(ItemMeta meta)
	{
		if (meta == null)
			return false;
		CustomObjectType<?> type = ContentManager.getCustomObjectType(meta);
		return (type instanceof CustomItemType);
	}
	
	public static CustomItemType<?> getType(ItemMeta meta)
	{
		if (isCustomItem(meta))
			return (CustomItemType<?>)ContentManager.getCustomObjectType(meta);
		else
			return null;
	}
	
	public static CustomItemType<?> getType(ItemStack stack)
	{
		return getType(stack.getItemMeta());
	}
	
	public static class CustomItem extends CustomObject
	{
		ItemStack stack;
		
		protected CustomItem(ItemStack item)
		{
			this(item.getItemMeta());
			stack = item;
		}
		
		protected CustomItem(ItemMeta meta)
		{
			super(meta);
		}
		
		void updatedMeta()
		{
			if (stack != null)
				stack.setItemMeta((ItemMeta)super.object);
		}
		
		public Data getData()
		{
			return ContentManager.getObjectData(object);
		}
		
		public void setData(Data data)
		{
			ContentManager.setObjectData(object, data);
			updatedMeta();
		}
		
		public ItemStack stack()
		{
			return stack;
		}
		
		public void setMeta(ItemMeta meta)
		{
			Data data = ContentManager.getCustomData(object);
			ContentManager.setCustomData(meta, data);
			object = meta;
			updatedMeta();
		}
		
		public ItemMeta getMeta()
		{
			return (ItemMeta)object;
		}
	}
	
	private void handlePlayerInteractEntity(ItemStack item, Player player, Entity clickedEntity, EquipmentSlot hand, PlayerInteractEntityEvent event)
	{
		playerInteractEntity(getObject(item), player, clickedEntity, hand, event);
	}
	
	protected void playerInteractEntity(ObjectType item, Player player, Entity clickedEntity, EquipmentSlot hand, PlayerInteractEntityEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handlePlayerInteract(ItemStack item, Player player, Block block, Action action, BlockFace face, EquipmentSlot slot, PlayerInteractEvent event)
	{
		playerInteract(getObject(item), player, block, action, face, slot, event);
	}
	
	protected void playerInteract(ObjectType item, Player player, Block block, Action action, BlockFace face, EquipmentSlot slot, PlayerInteractEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handleEnchantItem(ItemStack item, Player player, InventoryView view, int level, Map<Enchantment, Integer> enchants, int button, EnchantItemEvent event)
	{
		enchantItem(getObject(item), player, view, level, enchants, button, event);
	}
	
	protected void enchantItem(ObjectType item, Player player, InventoryView view, int level, Map<Enchantment, Integer> enchants, int button, EnchantItemEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handlePrepareItemEnchant(ItemStack item, Player player, InventoryView view, Block table, EnchantmentOffer[] offers, int bonus, PrepareItemEnchantEvent event)
	{
		prepareItemEnchant(getObject(item), player, view, table, offers, bonus, event);
	}
	
	protected void prepareItemEnchant(ObjectType item, Player player, InventoryView view, Block table, EnchantmentOffer[] offers, int bonus, PrepareItemEnchantEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handlePrepareAnvil(ItemStack item, int slot, InventoryView view, AnvilInventory inventory, ItemStack result, PrepareAnvilEvent event)
	{
		prepareAnvil(getObject(item), slot, view, inventory, result, event);
	}
	
	protected void prepareAnvil(ObjectType item, int slot, InventoryView view, AnvilInventory inventory, ItemStack result, PrepareAnvilEvent event)
	{
		event.setResult(null);
	}
	
	private void handlePrepareItemCraft(ItemStack item, int slot, CraftingInventory inventory, InventoryView view, boolean isRepair, Recipe recipe, PrepareItemCraftEvent event)
	{
		prepareItemCraft(getObject(item), slot, inventory, view, isRepair, recipe, event);
	}
	
	protected void prepareItemCraft(ObjectType item, int slot, CraftingInventory inventory, InventoryView view, boolean isRepair, Recipe recipe, PrepareItemCraftEvent event)
	{
		inventory.setResult(null);
	}
	
	private void handleBlockCook(ItemStack item, boolean isSource, ItemStack otherItem, Block block, BlockCookEvent event)
	{
		blockCook(getObject(item), isSource, otherItem, block, event);
	}
	
	protected void blockCook(ObjectType item, boolean isSource, ItemStack otherItem, Block block, BlockCookEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handleFurnaceBurn(ItemStack item, Block furnace, int burnTime, FurnaceBurnEvent event)
	{
		furnaceBurn(getObject(item), furnace, burnTime, event);
	}
	
	protected void furnaceBurn(ObjectType item, Block furnace, int burnTime, FurnaceBurnEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handleBrewingStandFuel(ItemStack item, Block brewingStand, int fuelPower, BrewingStandFuelEvent event)
	{
		brewingStandFuel(getObject(item), brewingStand, fuelPower, event);
	}
	
	protected void brewingStandFuel(ObjectType item, Block brewingStand, int fuelPower, BrewingStandFuelEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handleBrew(ItemStack item, Block brewer, BrewerInventory inventory, List<ItemStack> results, int fuelLevel, BrewEvent event)
	{
		brew(getObject(item), brewer, inventory, results, fuelLevel, event);
	}
	
	protected void brew(ObjectType item, Block brewer, BrewerInventory inventory, List<ItemStack> results, int fuelLevel, BrewEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handlePlayerConsume(ItemStack item, Player player, PlayerItemConsumeEvent event)
	{
		playerConsume(getObject(item), player, event);
	}
	
	protected void playerConsume(ObjectType item, Player player, PlayerItemConsumeEvent event)
	{
		event.setCancelled(true);
	}
	
	private void handleEntityDamageByEntity(ItemStack item, Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, double damage, EntityDamageByEntityEvent event)
	{
		entityDamageByEntity(getObject(item), damager, damagee, cause, damage, event);
	}
	
	protected void entityDamageByEntity(ObjectType weapon, Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, double damage, EntityDamageByEntityEvent event)
	{
		event.setCancelled(true);
	}
	
	static class CustomItemManager implements Listener
	{
		static void init(Plugin plugin)
		{
			Bukkit.getPluginManager().registerEvents(new CustomItemManager(), plugin);
		}
		
		@EventHandler
		public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
		{
			if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
				&& event.getDamager() instanceof LivingEntity)
			{
				ItemStack weapon = ((LivingEntity)event.getDamager()).getEquipment().getItemInMainHand();
				if (isCustomItem(weapon))
				{
					CustomItemType<?> type = getType(weapon);
					type.handleEntityDamageByEntity(weapon, event.getEntity(), event.getDamager(), event.getCause(), event.getDamage(), event);
				}
			}
		}
		
		@EventHandler
		public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
		{
			if (isCustomItem(event.getPlayer().getInventory().getItem(event.getHand())))
			{
				CustomItemType<?> type = getType(event.getPlayer().getInventory().getItem(event.getHand()));
				type.handlePlayerInteractEntity(event.getPlayer().getInventory().getItem(event.getHand()), event.getPlayer(), event.getRightClicked(), event.getHand(), event);
			}
		}
		
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			if (event.hasItem() && isCustomItem(event.getItem()))
			{
				CustomItemType<?> type = getType(event.getItem());
				type.handlePlayerInteract(event.getItem(), event.getPlayer(), event.getClickedBlock(), event.getAction(), event.getBlockFace(), event.getHand(), event);
			}
		}
		
		@EventHandler
		public void onEnchantItem(EnchantItemEvent event)
		{
			if (isCustomItem(event.getItem()))
			{
				CustomItemType<?> type = getType(event.getItem());
				type.handleEnchantItem(event.getItem(), event.getEnchanter(), event.getView(), event.getExpLevelCost(), event.getEnchantsToAdd(), event.whichButton(), event);
			}
		}
		
		@EventHandler
		public void onPrepareItemEnchant(PrepareItemEnchantEvent event)
		{
			if (isCustomItem(event.getItem()))
			{
				CustomItemType<?> type = getType(event.getItem());
				type.handlePrepareItemEnchant(event.getItem(), event.getEnchanter(), event.getView(), event.getEnchantBlock(), event.getOffers(), event.getEnchantmentBonus(), event);
			}
		}
		
		@EventHandler
		public void onPrepareAnvil(PrepareAnvilEvent event)
		{
			for (int slot = 0; slot < 2; slot++)
			{
				ItemStack item = event.getInventory().getItem(slot);
				if (isCustomItem(item))
				{
					CustomItemType<?> type = getType(item);
					type.handlePrepareAnvil(item, slot, event.getView(), event.getInventory(), event.getResult(), event);
				}
			}
		}
		
		@EventHandler
		public void onPrepareItemCraft(PrepareItemCraftEvent event)
		{
			CraftingInventory inventory = event.getInventory();
			ItemStack[] matrix = inventory.getMatrix();
			for (int index = 0; index < matrix.length; index++)
			{
				if (isCustomItem(matrix[index]))
				{
					CustomItemType<?> type = getType(matrix[index]);
					type.handlePrepareItemCraft(matrix[index], index, inventory, event.getView(), event.isRepair(), event.getRecipe(), event);
				}
			}
		}
		
		@EventHandler
		public void onBlockCook(BlockCookEvent event)
		{
			if (isCustomItem(event.getSource()))
			{
				CustomItemType<?> type = getType(event.getSource());
				type.handleBlockCook(event.getSource(), true, event.getResult(), event.getBlock(), event);
			}
			if (isCustomItem(event.getResult()))
			{
				CustomItemType<?> type = getType(event.getResult());
				type.handleBlockCook(event.getResult(), false, event.getSource(), event.getBlock(), event);
			}
		}
		
		@EventHandler
		public void onFurnaceBurn(FurnaceBurnEvent event)
		{
			if (isCustomItem(event.getFuel()))
			{
				CustomItemType<?> type = getType(event.getFuel());
				type.handleFurnaceBurn(event.getFuel(), event.getBlock(), event.getBurnTime(), event);
			}
		}
		
		@EventHandler
		public void onBrewingStandFuel(BrewingStandFuelEvent event)
		{
			if (isCustomItem(event.getFuel()))
			{
				CustomItemType<?> type = getType(event.getFuel());
				type.handleBrewingStandFuel(event.getFuel(), event.getBlock(), event.getFuelPower(), event);
			}
		}
		
		@EventHandler
		public void onBrew(BrewEvent event)
		{
			if (isCustomItem(event.getContents().getIngredient()))
			{
				CustomItemType<?> type = getType(event.getContents().getIngredient());
				type.handleBrew(event.getContents().getIngredient(), event.getBlock(), event.getContents(), event.getResults(), event.getFuelLevel(), event);
			}
		}
		
		@EventHandler
		public void onPlayerItemConsume(PlayerItemConsumeEvent event)
		{
			if (isCustomItem(event.getItem()))
			{
				CustomItemType<?> type = getType(event.getItem());
				type.handlePlayerConsume(event.getItem(), event.getPlayer(), event);
			}
		}
	}
}