package bufmgr;

import java.util.ArrayList;

public class HIST {
	//Value of k in LRU-K method
	private int k;
	
	//the id of the current page
	private int frame;
	
	/**
	 * The period in millisecond, where whe consider that references are correlated
	 */
	private int CORRELATED_REFERENCE_PERIOD = 100;
	
	private ArrayList<Long> references;
	
	public HIST(int pageid, int k) {
		this.references = new ArrayList<>();
		this.k = k;
		this.frame = pageid;
	}
	
	
	/**
	 * If the references are correlated, we just replace the last reference in the history with a new reference
	 */
	public void addReference() {
		long ref = System.currentTimeMillis();
		
		
		if(references.size()> 0  && ref - references.get(references.size()-1) < CORRELATED_REFERENCE_PERIOD) {
			references.set(references.size()-1, ref);
			return;
		}
		if(references.size() != k)
			references.add(ref);
		else //here, we have to update the list of references, for insert the new reference
			update(ref);		
	}
	
	/**
	 * we eraze the LRU reference value
	 */
	private void update(long reference) {
		if(references.size()<1)
			return;
		for(int i = 1; i < references.size(); ++i) 
			references.set(i, references.get(i-1));
		references.set(references.size()-1, reference);
	}
	
	public long getOldestReference() {
		try {
			return references.get(0);
		}catch(Exception e) {
			//if there is not reference in the history, 
			//we return the current time. In this case, 
			//we know that this page isn't the oldest in the buffer
			return System.currentTimeMillis();
		}
	}
	
	/**
	 * 
	 * @return true if we have at least k references in the buffer
	 */
	public boolean isFull()
	{
		return(this.references.size() == this.k);
	}
	
	/**
	 * This method is called when we pick a victim of the page of this history in LRU-K class
	 * @param ref which is the reference to remove in the history of the page p
	 * @throws InvalidFrameNumberException when the reference( ie frame) is not in the history.
	 */
	public void removeRef(long ref) throws InvalidFrameNumberException
	{
		for(int i=0 ; i<references.size() ; ++i) {
			if(references.get(i) == ref) {
				references.remove(i);
				return;
			}
		}
		throw new InvalidFrameNumberException(null, "BAD HISTORY REFERENCE");
	}
	

	
	/**
	 * The getter is useful to know if the page is in the buffer or not, in the LRUK object
	 * @return value of the pageid variable
	 */
	public int getFrame() {
		return frame;
	}

	public long getLast() {
		if(references.size() >0)
			return references.get(references.size()-1);
		return -1;
	}

	public long hist(int i) throws InvalidFrameNumberException
	{
		if(i<0 | i> references.size())
			throw new InvalidFrameNumberException(null, "INDEX OUT OF BOUND FOR THE METHOD HIST(page, index)");
		return references.get(i-1);
	}
	public boolean isNotCorrelated(long ref)
	{
		return(ref - getLast() > CORRELATED_REFERENCE_PERIOD);
	}
	
	
	

}
