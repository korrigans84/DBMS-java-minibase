# Java Minibase Project 

## Lab 2
The objective of this lab is to create different replacer, such as LRU, FIFO, LIFO and LRU-k methods

## LRU
In this method, we always update the buffer when we pin a page. For example, if page p was consulted 20 pages ago, then there are 10 pages, and our buffer is of size 20, then page p will not be the victim, because it was used more recently. In the FIFO method, page p would have been the victim (if it is not pinned).

## FIFO 
The difference with the LRU method is in the pin method. We update the buffer only if the frame is a new frame : 
```
if(!Arrays.stream(this.frames).anyMatch(x -> x == frameNo) )
	update(frameNo);
```


## LIFO 

### Differences with LIFO method
The only difference is when we pick a victim.  
Instead of retrieving the first unpinned page from the buffer, we retrieve the last which was inserted in the buffer, and which is not pinned.
```
for ( int i = numBuffers - 1; i >=0  ; --i ) {
     frame = frames[i];
    if ( state_bit[frame].state != Pinned ) {
	state_bit[frame].state = Pinned;
	(mgr.frameTable())[frame].pin();
	update(frame);
	return frame;
    }
}
```

## LRUK 

### Structure : 
LRUK object contains 4 variables : 
> k contain the K value for the algorithm  
> frames in an array which is the buffer pool. This length is defined by mgr.getNumBuffers()  
> nframes is the number of frames used, between 0 and the length of frames array  
> histories contains the histories of the pages already passed in the buffer  

HIST object contains the k last references for one frame in the buffer  
	references are the timestamp of the moment of the page is pinned
### How it works ?

#### PIN method
When we pin a new frame, we update the buffer pool, and the history of its frame. If the page already have an history, we just update this one. If not, the private method getHistOfAPage throws an InvalidFrameNumberException, and we create an new history for this new frame. We add this history to the histories buffer, and don't missing to add a reference to this new frame history

#### Pick a victim method
To pick a victim, we need to find the oldest reference of all frames that aren't pinned. we browse all the histories for each frames, and keep the oldest reference. If all the pages are pinned, we throw a BufferPoolExceededException


## TIPS 
> the file Code/copyCode.sh is a script file to update *.java files in Code folder with the class used in src/bufmgr
