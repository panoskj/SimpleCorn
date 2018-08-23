package panda.corn.entity;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MyEntityFireworkRocket extends Entity{
	
	private static final DataParameter<ItemStack> FIREWORK_ITEM = EntityDataManager.<ItemStack>createKey(MyEntityFireworkRocket.class, DataSerializers.ITEM_STACK);
	/** The age of the firework in ticks. */
	public int fireworkAge;
	/** The lifetime of the firework in ticks. When the age reaches the lifetime the firework explodes. */
	private int lifetime;

	public MyEntityFireworkRocket(World worldIn) {
		super(worldIn);
		this.setSize(0.25F, 0.25F);
	}

	public MyEntityFireworkRocket(World worldIn, double x, double y, double z, @Nullable ItemStack givenItem)
	{
		super(worldIn);
		this.fireworkAge = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(x, y, z);
		int i = 1;


		if (!givenItem.isEmpty() && givenItem.hasTagCompound())
        {
            this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
            NBTTagCompound nbttagcompound = givenItem.getTagCompound();
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Fireworks");
            i += nbttagcompound1.getByte("Flight");
        }

		this.motionX = this.rand.nextGaussian() * 0.001D;
		this.motionZ = this.rand.nextGaussian() * 0.001D;
		this.motionY = 0.05D;
		this.lifetime = 10 * i + this.rand.nextInt(6) + this.rand.nextInt(7);
	}
	
	

	@Override
	protected void entityInit()
	{
		 this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.onUpdate();
		this.motionX *= 1.15D;
		this.motionZ *= 1.15D;
		this.motionY += 0.04D;
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

		for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
		{
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
		{
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F)
		{
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
		{
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

		if (this.fireworkAge == 0 && !this.isSilent())
		{
			this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
		}

		++this.fireworkAge;

		if (this.world.isRemote && this.fireworkAge % 2 < 2)
		{
			this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY - 0.3D, this.posZ, this.rand.nextGaussian() * 0.05D, -this.motionY * 0.5D, this.rand.nextGaussian() * 0.05D, new int[0]);
		}

		if (!this.world.isRemote && this.fireworkAge > this.lifetime)
		{
			this.world.setEntityState(this, (byte)17);
			this.setDead();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id)
	{
		if (id == 17 && this.world.isRemote)
		{
			ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
			NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.getSubCompound("Fireworks");

			if (!itemstack.isEmpty() && itemstack.hasTagCompound())
			{
				nbttagcompound = itemstack.getTagCompound().getCompoundTag("Fireworks");
			}

			//this.world.makeFireworks(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, nbttagcompound);
			Minecraft.getMinecraft().effectRenderer.addEffect(EntityParticlePopcornFX.generatePopcornParticles(world, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, nbttagcompound, fireworkAge)); 
		}

		super.handleStatusUpdate(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt(x * x + z * z);
			this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
			this.rotationPitch = (float)(MathHelper.atan2(y, f) * (180D / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}
	}


	public static void registerFixesFireworkRocket(DataFixer fixer)
	{
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(MyEntityFireworkRocket.class, new String[] {"FireworksItem"}));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
    2override */
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setInteger("Life", this.fireworkAge);
        compound.setInteger("LifeTime", this.lifetime);
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);

        if (!itemstack.isEmpty())
        {
            compound.setTag("FireworksItem", itemstack.writeToNBT(new NBTTagCompound()));
        }
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		this.fireworkAge = compound.getInteger("Life");
		this.lifetime = compound.getInteger("LifeTime");
		NBTTagCompound nbttagcompound = compound.getCompoundTag("FireworksItem");

		if (nbttagcompound != null)
		{
			ItemStack itemstack = new ItemStack(nbttagcompound);

            if (!itemstack.isEmpty())
            {
                this.dataManager.set(FIREWORK_ITEM, itemstack);
            }
		}
	}

	/**
	 * Gets how bright this entity is.
	 */
	@Override
	public float getBrightness()
	{
		return super.getBrightness();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender()
	{
		return super.getBrightnessForRender();
	}

	/**
	 * Returns true if it's possible to attack this entity with an item.
	 */
	@Override
	public boolean canBeAttackedWithItem()
	{
		return false;
	}

}
