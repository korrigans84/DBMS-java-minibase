package bufmgr;

import java.util.Arrays;

public class LIFO extends Replacer {

	  /**
	   * private field
	   * An array to hold number of frames in the buffer pool
	   */

	    private int  frames[];
	 
	  /**
	   * private field
	   * number of frames used
	   */   
	  private int  nframes;
	  
	  
	protected LIFO(BufMgr javamgr) {
		super(javamgr);
		frames = null;
	}
	
	@Override
	public String name() {
		
		return "LIFO";
	}	
	
	
  /**
   * Calling super class the same method
   * Initializing the frames[] with number of buffer allocated
   * by buffer manager
   * set number of frame used to zero
   *
   * @param	mgr	a BufMgr object
   * @see	BufMgr
   * @see	Replacer
   */
    public void setBufferManager( BufMgr mgr )
     {
        super.setBufferManager(mgr);
	frames = new int [ mgr.getNumBuffers() ];
	nframes = 0;
     }

    
	@Override
	public int pick_victim() throws BufferPoolExceededException {
		
		  int numBuffers = mgr.getNumBuffers();
		  int frame;
		   
		if ( nframes < numBuffers ) {
		    frame = nframes++;
		    frames[frame] = frame;
		    state_bit[frame].state = Pinned;
		    (mgr.frameTable())[frame].pin();
		    return frame;
		}
		
		for ( int i = numBuffers - 1; i >=0  ; --i ) {
		     frame = frames[i];
		    if ( state_bit[frame].state != Pinned ) {
		        state_bit[frame].state = Pinned;
		        (mgr.frameTable())[frame].pin();
		        update(frame);
		        return frame;
		    }
		}
		//if we are here, the condition state_bit[frame].state != Pinned is always false, and the buffer is full. So, we throw a BufferPoolExceededException
		throw new BufferPoolExceededException(null, "there are no frames in the queue with pin_count==0") ;
	}
	

	
	  
	  /**
	   * call super class the same method
	   * pin the page in the given frame number 
	   * 
	   * move the page to the end of list if it's not already in
	   *
	   * @param	 frameNo	 the frame number to pin
	   * @exception  InvalidFrameNumberException
	   */
	 public void pin(int frameNo) throws InvalidFrameNumberException
	 {
	    super.pin(frameNo);
	    
	    //Using JAVA 8 : we insert the new frame only if it's not already in
	    if(!Arrays.stream(this.frames).anyMatch(x -> x == frameNo) )
		    update(frameNo);
	    
	    
	 }
	  /**
	   * This pushes the given frame to the end of the list.
	   * We call this function if we pin a new page. In this case, index will be equal to nframes, and we'll just insert the frame at the end of the list
	   * We call this function when we pick a victim in a full buffer. Then, wee 
	   * @param frameNo	the frame number
	   */
	  private void update(int frameNo)
	  {
		     int index;
		     for ( index=0; index < nframes; ++index ) {
		         if ( frames[index] == frameNo ) 
		         	break;	 
		     }
		        
		            

		    while ( ++index < nframes )
		        frames[index-1] = frames[index];
		        frames[nframes-1] = frameNo;
	  }

}
