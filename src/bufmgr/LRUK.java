package bufmgr;

import java.util.ArrayList;
import java.awt.FlowLayout;

public class LRUK extends  Replacer {

	//frames of the buffer pool
	private int  frames[];
	
	//Value of k in LRU-K method
	private int k;
	
	//the id of the current page
	public int pageid;
	
	  /**
	   * private field
	   * number of frames used
	   */   
	  private int  nframes;
	  
	  
	private ArrayList<HIST> histories;
	
	
	protected LRUK(BufMgr javamgr) {
		super(javamgr);
	      frames = null;
		this.histories = new ArrayList<HIST>();
		
	}
	
	
	  /**
	   * call super class the same method
	   * pin the page in the given frame number 
	   * move the page to the end of list  
	   *
	   * @param	 frameNo	 the frame number to pin
	   * @exception  InvalidFrameNumberException
	   */
	 public void pin(int frameNo) throws InvalidFrameNumberException
	 {
	    super.pin(frameNo);

	    update(frameNo);
	    updateHistory(frameNo);
	    
	 }
	
	  /**
	   * This pushes the given frame to this place in the list.
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
	  /**
	   * We update the history :
	   * 	if the page is already in the buffer, it has already an history. we update this history with the new reference
	   * 
	   * 	if the page is't already in the buffer, we create a new history
	   * 	
	   */
	  private void updateHistory(int frame)
	  {

		  for(HIST hist : histories) {
			  if(hist.getFrame() == frame) {
				  hist.addReference();
				  return;
			  }
		  }

		  //if the page is't already in the buffer
		  HIST hist = new HIST(frame, k);

		  hist.addReference();
		  histories.add(hist);

		  
	  }

	@Override
	public int pick_victim() throws BufferPoolExceededException, PagePinnedException {

		int numBuffers = mgr.getNumBuffers();
	    int frame;
	   
	    if ( nframes < numBuffers ) {

	        frame = nframes++;
	        frames[frame] = frame;
	        state_bit[frame].state = Pinned;
	        (mgr.frameTable())[frame].pin();
	        updateHistory(frame);

	        return frame;
	    }
	    int victim = -1;
	    long min = System.currentTimeMillis(); //initialize with the current timeStamp
	    for ( int i = 0; i < histories.size(); ++i ) {
    		HIST hist = histories.get(i);
	        frame = hist.getFrame();
	        if ( state_bit[frame].state != Pinned && hist.getOldestReference() < min) {
	            min = hist.getOldestReference();
	            victim = frame;
	        }
	        
	    }
	    
	    if(victim < 0 )
	    	throw new BufferPoolExceededException(null, "CAN'T FIND A VICTIM FRAME IN THE BUFFER ");

	    (mgr.frameTable())[victim].pin();
        state_bit[victim].state = Pinned;
        update(victim);
        //we have to remove the 
        
	
	    return victim;
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
	public String name() {
		return "LRUK";
	}

	public int[] getFrames() {
		return frames;
	}

	public long last(int frame) {
		try {
			return getHistOfAPage(frame).getLast();
		}catch(Exception e) {
			return(-1);
		}
	}

	public long HIST(int pagenumber, int i) {
		try {
			HIST history = getHistOfAPage(pagenumber);
			return(history.hist(i));
		}catch(Exception e)
		{
			return -1;
		}
		
	}
	
	private HIST getHistOfAPage(int frame) throws InvalidFrameNumberException 
	{
		for(int i = 0 ; i< histories.size() ; i++) {
			HIST history = histories.get(i);
			if( history.getFrame() == frame) 
				return(history);
			
		}
		throw new InvalidFrameNumberException(null, "CAN'T ACCESS OF THE HISTORY FOR AN INEXISTING PAGE IN THE BUFFER");
	}
	
}
