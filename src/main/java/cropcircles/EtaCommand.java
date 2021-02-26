package cropcircles;

//import static net.minecraft.util.math.MathHelper.clamp;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
//import minecraftbyexample.mbe45_commands.MBEquoteCommand.QuoteSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateInput;
//import net.minecraft.command.impl.FillCommand;
import net.minecraft.command.impl.SetBlockCommand;
//import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ChatType;
//import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.command.arguments.BlockPosArgument;
//import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
//import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IClearable;

public class EtaCommand {
	
	
      
      private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_208897_0_, p_208897_1_) -> {
          return new TranslationTextComponent("commands.fill.toobig", p_208897_0_, p_208897_1_);
      });
      private static final BlockStateInput AIR = new BlockStateInput(Blocks.AIR.getDefaultState(), Collections.emptySet(), (CompoundNBT)null);
      private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed"));

	
	  public static void register(CommandDispatcher<CommandSource> dispatcher) {
		  
		    LiteralArgumentBuilder<CommandSource> etaCommand = Commands.literal("etafill")
		         .requires((commandSource) -> commandSource.hasPermissionLevel(1))
		         .then(Commands.argument("from", BlockPosArgument.blockPos())
		         .then(Commands.argument("to", BlockPosArgument.blockPos())
		         .then(Commands.argument("block", BlockStateArgument.blockState()).executes((p_198472_0_) -> {
		             return doFill( p_198472_0_.getSource(), 
		            		        new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198472_0_, "from"), 
		            		        BlockPosArgument.getLoadedBlockPos(p_198472_0_, "to")), 
		            		        BlockStateArgument.getBlockState(p_198472_0_, "block"), 
		            		        EtaCommand.Mode.REPLACE, (Predicate<CachedBlockInfo>)null);
		         }))));

		       
		    dispatcher.register(etaCommand);

		    LiteralArgumentBuilder<CommandSource> cropCircleCommand = Commands.literal("cropcircle")
			         .requires((commandSource) -> commandSource.hasPermissionLevel(1))
			         .then(Commands.argument("from", BlockPosArgument.blockPos())
			         .then(Commands.argument("to", BlockPosArgument.blockPos())
			         .then(Commands.argument("block", BlockStateArgument.blockState()).executes((arg) -> {
			             return doFillCropCircle( arg.getSource(), 
			                            		  new MutableBoundingBox( BlockPosArgument.getLoadedBlockPos(arg, "from"), 
			                            				  				  BlockPosArgument.getLoadedBlockPos(arg, "to")), 
			                            		  BlockStateArgument.getBlockState(arg, "block"), 
			                            		  EtaCommand.Mode.REPLACE, 
			                            		  (Predicate<CachedBlockInfo>)null);
			         	}
			        ))));
		    dispatcher.register(cropCircleCommand);

		    
		    
		    LiteralArgumentBuilder<CommandSource> montyCommand = 
		    		Commands.literal("etacommand").requires((commandSource) -> commandSource.hasPermissionLevel(1))
		    		.then(Commands.literal("python").executes(commandContext -> sendMessage(commandContext, QuoteSource.MONTY_PYTHON.getQuote())));
		    
		    dispatcher.register(montyCommand);

	  }

	  
	  
	  private static int doFillCropCircle(CommandSource source, MutableBoundingBox area, BlockStateInput newBlock, EtaCommand.Mode mode, @Nullable Predicate<CachedBlockInfo> replacingPredicate) throws CommandSyntaxException {
		
	      // Server object
	      ServerWorld serverworld = source.getWorld();		  
		  
		  int x_min = 0;
		  int x_max = 100;
		  
		  int y_min = 0;
		  int y_max = 100;
		  
		  int z_min = 0;
		  int z_max = 0;
		  		  
		  int inner_radius = 10;
		  int outer_radius = 25;

		  int circle_x = 50;
		  int circle_y = 50;
		  
		  int count = 0;
		  
		  for (int x = x_min; x <= x_max; x++) {
			  for (int y = y_min; y <= y_max; y++) {
				  for (int z = z_min; z <= z_max; z++) {
					  
					  
					  double dist = Math.sqrt( Math.pow(x - circle_x, 2) + 
							                   Math.pow(y - circle_y, 2));

					  if ((dist > inner_radius) && (dist < outer_radius)) {

						  // Place the block at (area.minX, area.minY, area.minZ) + (x,y,z)
						  BlockPos bpos = new BlockPos(area.minX + x, area.minY + y, area.minZ + z);
						  fillSingleBlock(serverworld, bpos, newBlock, mode);
					  
						  count++;
					  }
				  }
			  }
			  
		  }
		  
          source.sendFeedback(new TranslationTextComponent("commands.fill.success", count), true);

		  return 0;
	  }
	  
	  
	  
	  private static void fillSingleBlock(ServerWorld serverworld, BlockPos blockpos, BlockStateInput newBlock, EtaCommand.Mode mode) {
		  
        
        // Get the tile already at that position
        TileEntity tileentity = serverworld.getTileEntity(blockpos);
        
        // Clear that object
        IClearable.clearObj(tileentity);
        
        // Place the block!
        newBlock.place(serverworld, blockpos, 2);
        
    	// Confirm the block type??
        Block block = serverworld.getBlockState(blockpos).getBlock();
        
        // Notify neighbors of state change
        serverworld.func_230547_a_(blockpos, block);        
        
     
	  }
	  
	  
	  
	  
	  
	  // Copied from FillCommand.class
	  //
	  private static int doFill(CommandSource source, MutableBoundingBox area, BlockStateInput newBlock, EtaCommand.Mode mode, @Nullable Predicate<CachedBlockInfo> replacingPredicate) throws CommandSyntaxException {
	      
		  // i - How many blocks in the specified region?
		  int i = area.getXSize() * area.getYSize() * area.getZSize();
	      
	      // Code can only handle 32768 elements!
	      if (i > 32768) {
	         throw TOO_BIG_EXCEPTION.create(32768, i);
	      } else {
	    	  
	    	 // Allocate an ArrayList object
	         List<BlockPos> list = Lists.newArrayList();
	         
	         // Server object
	         ServerWorld serverworld = source.getWorld();
	         
	         // j - How many blocks did we successfully fill?
	         int j = 0;

	         //
	         // SPECIAL KIND OF FOR LOOP!!  for (int x : <list-of-stuff>) {
	         //
	         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ)) {
	            
	        	 // Sanity check... <ignore>
	        	 if (replacingPredicate == null || replacingPredicate.test(new CachedBlockInfo(serverworld, blockpos, true))) {
	        		 
	        	   // What is the state of the block at 'blockpos' ?
	               BlockStateInput blockstateinput = mode.filter.filter(area, blockpos, newBlock, serverworld);
	               
	               // Presumably if it returns 'null', something bad has happened.
	               if (blockstateinput != null) {
	            	   
	            	  // Get the tile already at that position
	                  TileEntity tileentity = serverworld.getTileEntity(blockpos);
	                  
	                  // Clear that object
	                  IClearable.clearObj(tileentity);
	                  
	                  // PLACE the block!
	                  if (blockstateinput.place(serverworld, blockpos, 2)) {
	                	  
	                	 // Successful place?
	                	 //     - Call toImmutable()
	                	 //     - Add to list.
	                     list.add(blockpos.toImmutable());
	                     
	                     // One more block done
	                     ++j;
	                  }
	               }
	            }
	         }

	         // Walk through the list of blocks we placed using 'blockpos1'
	         for(BlockPos blockpos1 : list) {
	        	 
	        	// Get the actual block type
	            Block block = serverworld.getBlockState(blockpos1).getBlock();
	            
	            // Call func_230547_a_ for that position and that block. (!!!)
	            serverworld.func_230547_a_(blockpos1, block);
	         }

	         // If j==0, what does that mean!?
	         if (j == 0) {
	            throw FAILED_EXCEPTION.create();
	         } else {
	            source.sendFeedback(new TranslationTextComponent("commands.fill.success", j), true);
	            return j;
	         }
	      }
	   } 
	  
	  
	  
	  
	  // Copied from FillCommand.class
	  static enum Mode {
	      REPLACE((area, blockpos, newBlock, serverworld) -> {
	         return newBlock;
	      }),
	      OUTLINE((area, blockpos, newBlock, serverworld) -> {
	         return blockpos.getX() != area.minX && blockpos.getX() != area.maxX && blockpos.getY() != area.minY && blockpos.getY() != area.maxY && blockpos.getZ() != area.minZ && blockpos.getZ() != area.maxZ ? null : newBlock;
	      }),
	      HOLLOW((area, blockpos, newBlock, serverworld) -> {
	         return blockpos.getX() != area.minX && blockpos.getX() != area.maxX && blockpos.getY() != area.minY && blockpos.getY() != area.maxY && blockpos.getZ() != area.minZ && blockpos.getZ() != area.maxZ ? EtaCommand.AIR : newBlock;
	      }),
	      DESTROY((area, blockpos, newBlock, serverworld) -> {
	    	  serverworld.destroyBlock(blockpos, true);
	         return newBlock;
	      });

	      public final SetBlockCommand.IFilter filter;

	      private Mode(SetBlockCommand.IFilter filterIn) {
	         this.filter = filterIn;
	      }
	   }  

	  
	  
		  static int sendMessage(CommandContext<CommandSource> commandContext, String message) throws CommandSyntaxException {
		    TranslationTextComponent finalText = new TranslationTextComponent("chat.type.announcement",
		            commandContext.getSource().getDisplayName(), new StringTextComponent(message));

		    Entity entity = commandContext.getSource().getEntity();
		    if (entity != null) {
		      commandContext.getSource().getServer().getPlayerList().func_232641_a_(finalText, ChatType.CHAT, entity.getUniqueID());
		      //func_232641_a_ is sendMessage()
		    } else {
		      commandContext.getSource().getServer().getPlayerList().func_232641_a_(finalText, ChatType.SYSTEM, Util.DUMMY_UUID);
		    }
		    return 1;
		  }

		  enum QuoteSource {
			  
		    MONTY_PYTHON   (new String [] {"Nobody expects the Spanish Inquisition!",
		                                "What sad times are these when passing ruffians can say 'Ni' at will to old ladies.",
		                                "That's the machine that goes 'ping'.",
		                                "Have you got anything without spam?",
		                                "We interrupt this program to annoy you and make things generally more irritating.",
		                                "My brain hurts!"}),
		    YOGI_BERRA     (new String [] {"When you come to a fork in the road... take it.",
		                              "It ain't over till it's over.",
		                              "The future ain't what it used to be.",
		                              "If the world was perfect, it wouldn't be."}),
		    YOGI_BEAR      (new String[] {"I'm smarter than the average bear."}),
		    BLUES_BROTHERS (new String [] {"Four fried chickens and a Coke, please.",
		                                  "It's 106 miles to Chicago, we've got a full tank, half pack of cigarettes, it's dark out, and we're wearing sunglasses. Hit it.",
		                                  "Are you the police?  No ma'am, we're musicians."});

		    public String getQuote() {
		      return quotes[new Random().nextInt(quotes.length)];
		    }

		    QuoteSource(String [] quotes) {
		      this.quotes = quotes;
		    }

		    private String [] quotes;
		  }
		  
		  
		  

}