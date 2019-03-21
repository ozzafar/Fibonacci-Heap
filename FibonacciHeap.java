import java.util.Iterator;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 * Oz Zafar , 206039984 , ozzafar
 * Kobi Somech , 204427918 , kobisomech 
 */





public class FibonacciHeap
{
	public static int totalLinks,totalCuts;
	public HeapNode min;
	public int numOfNodes,numOfTrees,numOfMarked;
	public HeapNode[] buckets ;
    
    
	public FibonacciHeap(){

	}


   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */


	
    public boolean empty()
    {
		return min == null;
	}
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {  
    	
		HeapNode newNode = new HeapNode(key);
		if (empty()) {
			min = newNode;
			min.setNext(min);
		}
		else {
			insert_after(min,newNode);
			updateMin(newNode);
		}
		updateNumberOfNodes(1); // add one to number of nodes
		updateNumberOfTrees(1);
		return newNode;
    }

    
    
    
    public void insert_after(HeapNode node , HeapNode newNode) {
    		HeapNode next = node.getNext();
    		node.setNext(newNode);
    		newNode.setNext(next);
    }

    
    
	public void updateMin(HeapNode node) {
		if ( node.getKey() < min.getKey() )
			min = node;
	}

    
	public void updateNumberOfNodes(int i) {
		numOfNodes += i;
	}

	public void updateNumberOfTrees(int i) {
		numOfTrees += i;
	}
	
   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()  {
    		if (empty())
    			return;
    		HeapNode tmp = min;
    		HeapNode prev = min.getPrev();
		HeapNode next = min.getNext();
		updateNumberOfNodes(-1);
		if (min.hasChild()) {
			HeapNode child = min.getChild();
			int min_rank = min.getRank();
 			cleanParents(child);
			if ( min.hasSibling()  ) {  
				child.getPrev().setNext(next);  // swapped this and next line -->
												// 8 - 14
												// |
												// 16
				prev.setNext(child);
    				min = min.getNext();  // not the real minimum
			}
			else {   // min doesnt have brother
				min = child;
			}	
			updateNumberOfTrees(min_rank-1);
		}
		

		else	 {  // min doesn't have children
			if (  min.hasSibling() ) { 
				prev.setNext(min.getNext());
				min = min.getNext();  // not the real minumum
			}
			else
				min = null ; 
			updateNumberOfTrees(-1);
		}
		buckets = TreesLogArray();
		min = consolidate(); // min is always null  - why???

		tmp.child=null;

    }
		
			
    
    public HeapNode consolidate()  {
    		if (min == null)
    			return null;
    		// doesnt arrive here - min is null ???
    		toBuckets();
    		return fromBuckets();

    		
    }
    
    
    
    public void toBuckets()  {
    	
    		HeapNode x = min ;

		(x.getPrev()).next = null;  // break circularity
		
		HeapNode y;
		
		while (x != null) {
			y = x ;
			x = x.getNext();
			y.setNext(y);
			//System.out.println("key " + y.key+" " +"bucket length "+buckets.length);
			while ( buckets[y.getRank()] != null ) {
				//System.out.println("link "+y.key+" " +buckets[y.getRank()].key);
				y = link(y,buckets[y.getRank()]);
				buckets[y.getRank()-1] = null ;
			}
			buckets[y.getRank()] = y ;
		}
		
		
    }		
    
    
    
	public HeapNode fromBuckets() {
		int cnt = 0;
		HeapNode x = null;
		for (int i = 0; i < buckets.length; i++) {
			if (buckets[i] != null) {
				cnt++;
				if (x == null) {
					x = buckets[i];
					x.setNext(x);
				} 
				else {
					insert_after(x, buckets[i]);
					if (buckets[i].key < x.key)
						x = buckets[i];
				}
    			}
    		}
		this.numOfTrees = cnt;
    		return x;
    
    }
    
    
   public HeapNode link(HeapNode x, HeapNode y) {
	   totalLinks++;
	   numOfTrees -- ;
	   HeapNode temp = null ;
	   if (x.getKey() > y.getKey())   {
		    temp = x;
	   		x = y ;
	   		y = temp ;
	   }
	   if (x.hasChild()) {
			insert_after(x.getChild(),y);
		   }
	   x.child = y;
	   y.parent = x;
	   x.rank ++ ;
	   return x;
	}


/**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    		return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	/* the last tree of heap1 will point
    	 *  to first tree of heap2
    	 */
    		HeapNode min2 = heap2.findMin();
    		HeapNode min2Prev = min2.getPrev();
    		HeapNode minNext = min.getNext();
    		min.setNext(min2);
    		min2Prev.setNext(minNext);
		updateNumberOfNodes(heap2.size()); // add size of heap2 to size of "this"
		updateNumberOfTrees(heap2.numOfTrees); // add number of heap2's trees to number of "this"'s trees

	}

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
	{
		return numOfNodes;
	}
    	
    
    /*
     * return an empty array with size of log of heap's size 
     */
    public int[] intLogArray() {  
		int logOfSize = 2*(int) (Math.log(numOfNodes) / Math.log(2)) + 1 ;  
    		return new int[logOfSize];
    }
    
    public HeapNode[] TreesLogArray() {  
    		int logOfSize = 2*(int) (Math.log(numOfNodes) / Math.log(2)) + 1 ;  
     	return new HeapNode[logOfSize];
     }
    
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    
    public int[] countersRep() {
    		int[] arr = intLogArray();
    		int rank;
    		HeapNode curr = min ;
    		do {
    			rank = curr.getRank();
    			arr[rank]++;
    			curr = curr.next ;
    		}
		while ( curr != min ) ;
		return arr;
    }
	
    
    /*
     * for each node in the way, set it's father as null.
     */
    public void cleanParents(HeapNode node) {
		HeapNode start = node;
		do {
			node.parent=null ;
			node = node.getNext() ;
		}
    		while (node != start) ;
    }

    
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x)  {
    		decreaseKey(x, x.getKey()+1);
    		deleteMin();
    }
    
    


   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) {    
    		x.key -= delta;
	    	HeapNode y = x.getParent();
    		if (y != null  &&  x.getKey() < y.getKey()) { // if x is a root or there isn't a violation - no need to cut
    			//y.marked=false;
    			cascading_cut(x , y );
    		}
    		if (min != x && x.key < min.key)
    			min = x ;
    }

    
   public void cascading_cut(HeapNode x, HeapNode y) {
	   cut(x,y);
	   if (y.parent != null) {
		   if (y.marked == false) {
			   y.marked = true;
			   numOfMarked++;
		   }
		   else
			   cascading_cut(y,y.parent);
	   }
   }


   public void cut(HeapNode x, HeapNode y) {
	   totalCuts+=1;
	   numOfTrees+=1; ;
	   if (x.marked == true) {
		   x.marked = false;
		   numOfMarked--;
	   }
	   y.rank-=1;
	   x.setParent(null);
	   if (x.getNext() == x) 
		   y.child = null;
	   else {
		   y.child = x.getNext();
		   x.getPrev().next = x.getNext();
		   x.getNext().prev = x.getPrev();
	   }
	   x.setNext(x);
	   insert_after(min, x);
   }


/**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    		return numOfTrees + 2 * numOfMarked; 
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinks; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key,rank;
	public HeapNode parent,child,next,prev;
	public boolean marked = false;



  	public HeapNode(int key) {
	    this.key = key;
      }

  	public int getKey() {
	    return this.key;
      }

  	public int getRank() {
	    return this.rank;
      }
  	
 	public HeapNode getParent() {
	    return this.parent;
      }

 	public void setParent(HeapNode node) {
	     this.parent = node;
      }
 	
 	public HeapNode getChild() {
	    return this.child;
      }

 	public HeapNode getNext() {
	    return this.next;
      }

	public HeapNode getPrev() {
	    return this.prev;
      }
		

    public void setNext(HeapNode nextNode) {
    		this.next = nextNode;
    		nextNode.prev = this ;
    }

    public boolean hasChild() {
		return getChild() != null ;
    }
    
    public boolean hasSibling() {
		return (this!=this.next || this!=this.prev);
    }
    
    public boolean hasPrev() {
		return getPrev() != null ;
    }
    
    public boolean hasNext() {
		return getNext() != null ;
    }
    
    
 }
    



}


