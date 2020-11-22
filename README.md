# Java Minibase Project 

## Lab 2
The objective of this lab is to create different replacer, such as LRU, FIFO, LIFO and LRU-k methods
## LRUK 

### Structure : 
	LRUK object contains 4 variables : 
		k contain the K value for the algorithm
		frames in an array which is the buffer pool. This length is 				defined by mgr.getNumBuffers()
		nframes is the number of frames used, between 0 and the 				length of frames array
		histories contains the histories of the pages already passed 				in the buffer

	HIST object contains the k last references for one frame in the 			buffer
		references are the timestamp of the moment of the page is 		pinned
### How its works ?

#### PIN method
	When we pin a new frame, we update the buffer pool, and the history of its frame. If the page already have an history, we just update this one. If not, the private method getHistOfAPage throws an InvalidFrameNumberException, and we create an new history for this new frame. We add this history to the histories buffer, and don't missing to add a reference to this new frame history

#### Pick a victim method
To pick a victim, we need to find the oldest reference of all frames that aren't pinned. we browse all the histories for each frames, and keep the oldest reference. If all the pages are pinned, we throw a BufferPoolExceededException
