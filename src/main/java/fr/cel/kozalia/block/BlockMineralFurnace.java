package fr.cel.kozalia.block;

import fr.cel.kozalia.block.tileentity.TileEntityMineralFurnace;
import fr.cel.kozalia.creativetab.KozaliaCreativeTabs;
import fr.cel.kozalia.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMineralFurnace extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private final boolean isBurning;
    private static boolean keepInventory;
    private final Level level;

    public BlockMineralFurnace(String registryName, String unlocalizedName, boolean isBurning, Level level) {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.isBurning = isBurning;
        this.level = level;

        this.setHardness(3.5f);
        this.setSoundType(SoundType.STONE);

        this.setRegistryName(registryName);
        this.setUnlocalizedName(unlocalizedName);
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(getBlockFurnace(level));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (this.isBurning) {
            EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = (double) pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            switch (enumfacing) {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMineralFurnace) {
                playerIn.displayGUIChest((TileEntityMineralFurnace) tileentity);
                playerIn.addStat(StatList.FURNACE_INTERACTION);
            }

            return true;
        }
    }

    public static void setState(boolean active, World worldIn, BlockPos pos, Level level) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;

        if (active) {
            worldIn.setBlockState(pos, getLitBlockFurnace(level).getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
            worldIn.setBlockState(pos, getLitBlockFurnace(level).getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
        } else {
            worldIn.setBlockState(pos, getBlockFurnace(level).getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
            worldIn.setBlockState(pos, getBlockFurnace(level).getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
        }

        keepInventory = false;

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMineralFurnace(this.level);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMineralFurnace) {
                ((TileEntityMineralFurnace) tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMineralFurnace) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityMineralFurnace) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    @Nullable
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(getBlockFurnace(this.level)));
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    public static Block getBlockFurnace(Level level) {
        return level == Level.BASIC ? ModBlocks.basic_furnace :
                (level == Level.INTERMEDIATE ? ModBlocks.intermediate_furnace :
                        (level == Level.ADVANCED ? ModBlocks.advanced_furnace :
                                (level == Level.SUPREME ? ModBlocks.supreme_furnace :
                                        (level == Level.ULTIMATE ? ModBlocks.ultimate_furnace : Blocks.FURNACE))));
    }

    public static Block getLitBlockFurnace(Level level) {
        return level == Level.BASIC ? ModBlocks.lit_basic_furnace :
                (level == Level.INTERMEDIATE ? ModBlocks.lit_intermediate_furnace :
                        (level == Level.ADVANCED ? ModBlocks.lit_advanced_furnace :
                                (level == Level.SUPREME ? ModBlocks.lit_supreme_furnace :
                                        (level == Level.ULTIMATE ? ModBlocks.lit_ultimate_furnace : Blocks.LIT_FURNACE))));
    }

    public enum Level {

        BASIC(125, 130, 260, 14000, 175, 85, 1400, 17500, 85, 2100),
        INTERMEDIATE(100, 110, 220, 12000, 150, 70, 1200, 15000, 70, 1800),
        ADVANCED(75, 90, 180, 10000, 125, 55, 1000, 12500, 55, 1500),
        SUPREME(50, 70, 140, 8000, 100, 40, 800, 10000, 40, 1200),
        ULTIMATE(25, 50, 100, 6000, 75, 25, 6000, 7500, 25, 900),
        ;

        private final int cookTime;

        private final int burnTimeWoodenSlab;
        private final int burnTimeWood;
        private final int burnTimeCoalBlock;
        private final int burnTimeItemWood;
        private final int burnTimeStick;
        private final int burnTimeCoal;
        private final int burnTimeLava;
        private final int burnTimeSapling;
        private final int burnTimeBlazeRod;


        Level(int cookTime, int burnTimeWoodenSlab, int burnTimeWood, int burnTimeCoalBlock, int burnTimeItemWood, int burnTimeStick, int burnTimeCoal, int burnTimeLava, int burnTimeSapling, int burnTimeBlazeRod) {
            //this.block = block;
            //this.litBlock = litBlock;

            this.cookTime = cookTime;

            this.burnTimeWoodenSlab = burnTimeWoodenSlab;
            this.burnTimeWood = burnTimeWood;
            this.burnTimeCoalBlock = burnTimeCoalBlock;
            this.burnTimeItemWood = burnTimeItemWood;
            this.burnTimeStick = burnTimeStick;
            this.burnTimeCoal = burnTimeCoal;
            this.burnTimeLava = burnTimeLava;
            this.burnTimeSapling = burnTimeSapling;
            this.burnTimeBlazeRod = burnTimeBlazeRod;
        }

        public int getBurnTimeBlazeRod() {
            return burnTimeBlazeRod;
        }

        public int getBurnTimeCoal() {
            return burnTimeCoal;
        }

        public int getBurnTimeCoalBlock() {
            return burnTimeCoalBlock;
        }

        public int getBurnTimeItemWood() {
            return burnTimeItemWood;
        }

        public int getBurnTimeLava() {
            return burnTimeLava;
        }

        public int getBurnTimeSapling() {
            return burnTimeSapling;
        }

        public int getBurnTimeStick() {
            return burnTimeStick;
        }

        public int getBurnTimeWood() {
            return burnTimeWood;
        }

        public int getBurnTimeWoodenSlab() {
            return burnTimeWoodenSlab;
        }

        public int getCookTime() {
            return cookTime;
        }
    }

}
