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
	
	
    // Exception Handlers  
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((translationKey, args) -> {
    	return new TranslationTextComponent("commands.fill.toobig", translationKey, args);
    });
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed"));

    
    // "AIR" - convenience variable
	private static final BlockStateInput AIR = new BlockStateInput( Blocks.AIR.getDefaultState(), Collections.emptySet(), (CompoundNBT)null );

	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		  
		  	// Basic command - duplicate functionality from "/fill"
		    LiteralArgumentBuilder<CommandSource> etaCommand = Commands.literal("etafill")
		         .requires((commandSource) -> commandSource.hasPermissionLevel(1))
		         .then(Commands.argument("from", BlockPosArgument.blockPos())
		         .then(Commands.argument("to"  , BlockPosArgument.blockPos())
		         .then(Commands.argument("block", BlockStateArgument.blockState())
		         .executes((context) -> {
		             return doFill( context.getSource(), 
		            		        new MutableBoundingBox( BlockPosArgument.getLoadedBlockPos(context, "from"), 
		            		        					    BlockPosArgument.getLoadedBlockPos(context, "to")), 
		            		        BlockStateArgument.getBlockState(context, "block"), 
		            		        EtaCommand.Mode.REPLACE, (Predicate<CachedBlockInfo>)null);
		         	}
		         ))));
		    
		    dispatcher.register(etaCommand);

		    // Minecraft By Example sample command
		    //
		    LiteralArgumentBuilder<CommandSource> montyCommand = 
		    		Commands.literal("etacommand")
		    		.requires((commandSource) -> commandSource.hasPermissionLevel(1))
		    		.then(Commands.literal("python")
		    	    .executes(commandContext -> sendMessage(commandContext, QuoteSource.MONTY_PYTHON.getQuote())));
		    
		    dispatcher.register(montyCommand);
		    
		    
		    // Cropcircle command
		    //
		    LiteralArgumentBuilder<CommandSource> cropCircleCommand = Commands.literal("cropcircle")
			         .requires((commandSource) -> commandSource.hasPermissionLevel(1))
			         .then(Commands.argument("pos", BlockPosArgument.blockPos())
			         .then(Commands.argument("block", BlockStateArgument.blockState())
			         .executes((arg) -> {
			             return doFillCropCircle( arg.getSource(),                                                // world info
			            		 				  new BlockPos( BlockPosArgument.getLoadedBlockPos(arg, "pos")),  // location
			                            		  BlockStateArgument.getBlockState(arg, "block"),                 // block type
			                            		  EtaCommand.Mode.REPLACE,                                        // REPLACE
			                            		  (Predicate<CachedBlockInfo>)null);
			         	}
			        )));
		    dispatcher.register(cropCircleCommand);

	  }

	  
	  // doFillCropCircle()
	  //    - Fill a crop circle at the given position using the specified block.
	  //
	  @SuppressWarnings("unused")
	private static int doFillCropCircle( CommandSource source, 
			  							   BlockPos origin,            // "pos"
			  							   BlockStateInput newBlock,   // "block"
			  							   EtaCommand.Mode mode,       // REPLACE
			  							   @Nullable Predicate<CachedBlockInfo> replacingPredicate) throws CommandSyntaxException {
		
	      // Server object
	      ServerWorld serverworld = source.getWorld();		  
		  
	      // Loop bounds
		  int x_min = 0;
		  int x_max = 100;
		  
		  int y_min = 0;
		  int y_max = 100;
		  
		  int z_min = 0;
		  int z_max = 0;
		  		
		  // Radius of circles
		  int inner_radius = 10;
		  int outer_radius = 25;

		  // Center of circle
		  int circle_x = 50;
		  int circle_y = 50;
		  
		  // How many blocks have we placed?
		  int block_count = 0;
		  
		  // Exercises
		  boolean do_exercise1 = true;
		  boolean do_exercise2 = false;
		  boolean do_exercise3 = false;
		  boolean do_exercise4 = false;
		  
		  
		  // Nested for loops
		  //
		  for (int x = x_min; x <= x_max; x++) {
			  for (int y = y_min; y <= y_max; y++) {
				  for (int z = z_min; z <= z_max; z++) {
					  
					  // Shall we place a block?
					  boolean place_block = false;
					  
					  if (do_exercise1) {

						  // EXERCISE #1 - Draw a disc with radius 'outer_radius'.
						  //    - Starting code for computing x-distance-to-center and y-distance-to-center is given.
						  //    - You need to add code to calculate 'dist'.
						  //    - You need to add code to figure out whether to place a block based on 'dist'
						  //  Hints: 
						  //     (1) Math.pow(a,b) returns 'a' raised to the 'b' power
						  //     (2) Math.sqrt(x) returns the square root of 'x'
						  //
						  int distance_to_center_x = (x - circle_x);
						  int distance_to_center_y = (y - circle_y);
						  
						  double dist = 0.0;  // <-- Fill in code here to calculate distance
						  
						  if (     false      ) {     // <-- Fill in code here
							  place_block = true;
						  }
						  
					  }
					  
					  if (do_exercise2) {
						  
						  // EXERCISE #2 - Draw a big '+' in the sky
						  //    Hints: 
						  //      (1) What x,y locations do you need to place blocks on to graph a '+' ?
						  //      (2) Acacia fences look quite good for this exercise!
						  
						  if (  false   ) {           // <-- Fill in code here
							  place_block = true;
						  }
						  						  
					  }
					  
					  if (do_exercise3) {
						  
						  // EXERCISE #3 - Draw a big 'X' in the sky
						  //   Hints:
						  //      (1) What cases of (x,y) give you a line starting at (0,0) going to (100,100)?
						  //      (2) What cases of (x,y) give you a line starting at (0,100) going to (100,0)?
						  //      (3) Stairs look quite good for this exercise!
						  
						  if (  false  ) {
							  place_block = true;     // <-- Fill in code here
						  }
					  }
					  
					  
					  if (place_block) {
						  
						  // We will place the block at (area.minX, area.minY, area.minZ) + (x,y,z)
						  BlockPos bpos = new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
						  
						  fillSingleBlock(serverworld, bpos, newBlock, mode);
					  
						  block_count++;
					  }
				  }
			  }
		  }
			  
		  // EXERCISE #4
		  //    What does this code do?
		  if (do_exercise4) {
		  			  
			  for (double z = 0; z < 100 ; z+=0.05) {
				  
				  double angle  = z / 40.0 * 2 * Math.PI;
				  double offset = 170.0 / 360 * 2 * Math.PI;   // 120-degrees
				  
				  
				  double x1 = 25 * Math.sin(angle);
				  double y1 = 25 * Math.cos(angle);
				  double x2 = 25 * Math.sin(angle + offset);
				  double y2 = 25 * Math.cos(angle + offset);
				  
				  BlockPos bpos1 = new BlockPos(origin.getX() + x1, origin.getY() + z, origin.getZ() + y1);
				  BlockPos bpos2 = new BlockPos(origin.getX() + x2, origin.getY() + z, origin.getZ() + y2);
				  
				  fillSingleBlock(serverworld, bpos1, newBlock, mode);
				  fillSingleBlock(serverworld, bpos2, newBlock, mode);	  
			  
				  // 1 rotation == z=40.   z=40 is 40/0.05 = 800 iterations or 1600 blocks.
				  //    1600 / 10.5 = 152
				  
				  if ((block_count % 152) == 10) {
					  for (double q = 0 ; q < 1; q += 0.05) {
						  BlockPos bpos3 = new BlockPos( interp(bpos1.getX(), bpos2.getX(), q),
								  						 interp(bpos1.getY(), bpos2.getY(), q),
								  						 interp(bpos1.getZ(), bpos2.getZ(), q));
						  fillSingleBlock(serverworld, bpos3, newBlock, mode);
					  }
				  }
				  
				  block_count += 2;
			  }
		  }
		  
          source.sendFeedback(new TranslationTextComponent("commands.fill.success", block_count), true);

		  return 0;
	  }
	  
	  
	  
	  // Interpolate between x1<->x2 given coefficient q [0..1.0].
	  //
	  static double interp(double x1, double x2, double q) {
		  return x1*q + x2*(1.0-q);
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




// Answer Key
//
// #1
// double dist = Math.sqrt( Math.pow(x - circle_x, 2) + Math.pow(y - circle_y, 2));
// if (dist < outer_radius) place_block = true;


// #2
// if ((x == 50) || (y == 50)) place_block = true;

// #3
// if (y == x)       place_block = true;
// if (y == 100 - x) place_block = true;

// #4
// https://en.wikipedia.org/wiki/Nucleic_acid_double_helix
//   "The double helix makes one complete turn about its axis every 10.4–10.5 base pairs in solution."
//

// Other interesting links:
//
// https://github.com/elBukkit/EffectLib/
// https://bukkit.org/threads/creating-a-helix-3d-spiral-out-of-particles.314312/


