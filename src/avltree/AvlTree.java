package avltree;
import java.lang.StringBuilder;
import java.lang.Math;

public class AvlTree<T extends Comparable<? super T>> {
	public static class AvlNode<T> {
		private T  value;
		private AvlNode<T>    leftChild;
		private AvlNode<T>    rightChild;
		private int      height;
		public AvlNode (T val){
			this (val, null, null);
		}
		public AvlNode (T val, AvlNode<T> lc, AvlNode<T> rc){
			value = val;
			leftChild = lc;
			rightChild = rc;
		}
	}

	private AvlNode<T> root;
	private int numberOfNodes;

	// to achieve balanced tree
	private int numberOfSingleRotations;
	private int numberOfDoubleRotations;

	public AvlTree (){
		root = null;        
		numberOfNodes = 0;
		numberOfSingleRotations = 0;
		numberOfDoubleRotations = 0;    
	}
	public int getNodeHeight (AvlNode<T> node){
		return node == null ? -1 : node.height;
	}
	public boolean insert (T newNode){
		try {
			root = insert (newNode, root);

			numberOfNodes++;
			return true;
		} catch(Exception e){

			return false;
		}
	}
	private AvlNode<T> insert (T w, AvlNode<T> z) throws Exception{
		// insert algorithm: (reference: geeksforgeeks.org)
		/* 
		    Let the newly inserted node be w
			1) Perform standard BST insert for w.
			2) Starting from w, travel up and find the first unbalanced node. 
					Let z be the first unbalanced node, y be the child of z that comes on 
					the path from w to z and x be the grandchild of z that comes on the path from w to z.
			3) Re-balance the tree by performing appropriate rotations on the 
					subtree rooted with z. There can be 4 possible cases that needs to be
					handled as x, y and z can be arranged in 4 ways. Following are the possible 4 arrangements:
				
				a) y is left child of z and x is left child of y (Left Left Case)
					T1, T2, T3 and T4 are subtrees.
				         z                                      y 
				        / \                                   /   \
				       y   T4      Right Rotate (z)          x      z
				      / \          - - - - - - - - ->      /  \    /  \ 
				     x   T3                               T1  T2  T3  T4
				    / \
				  T1   T2
				
				
				b) y is left child of z and x is right child of y (Left Right Case)
					     z                               z                           x
					    / \                            /   \                        /  \ 
					   y   T4  Left Rotate (y)        x    T4  Right Rotate(z)    y      z
					  / \      - - - - - - - - ->    /  \      - - - - - - - ->  / \    / \
					T1   x                          y    T3                    T1  T2 T3  T4
					    / \                        / \
					  T2   T3                    T1   T2
				
				c) y is right child of z and x is right child of y (Right Right Case)
					  z                                y
					 /  \                            /   \ 
					T1   y     Left Rotate(z)       z      x
					    /  \   - - - - - - - ->    / \    / \
					   T2   x                     T1  T2 T3  T4
					       / \
					     T3  T4
				
				d) y is right child of z and x is left child of y (Right Left Case)
					   z                            z                            x
					  / \                          / \                          /  \ 
					T1   y   Right Rotate (y)    T1   x      Left Rotate(z)   z      x
					    / \  - - - - - - - - ->     /  \   - - - - - - - ->  / \    / \
					   x   T4                      T2   y                  T1  T2  T3  T4
					  / \                              /  \
					T2   T3                           T3   T4
				
		 */
		if (z == null)
			z = new AvlNode<T> (w);
		else if (w.compareTo (z.value) < 0){
			z.leftChild = insert (w, z.leftChild);

			if (getNodeHeight (z.leftChild) - getNodeHeight (z.rightChild) == 2){ // a or b
				if (w.compareTo (z.leftChild.value) < 0){ // a: left left case
					z = rotateWithLeftChild (z);
					numberOfSingleRotations++;
				}
				else { // left right case
					z = doubleWithLeftChild (z);
					numberOfDoubleRotations++;
				}
			}
		}
		else if (w.compareTo (z.value) > 0){ // c or d
			z.rightChild = insert (w, z.rightChild);

			if ( getNodeHeight (z.rightChild) - getNodeHeight (z.leftChild) == 2)
				if (w.compareTo (z.rightChild.value) > 0){ // right right case
					z = rotateWithRightChild (z);
					numberOfSingleRotations++;
				}
				else{ // right left case
					z = doubleWithRightChild (z);
					numberOfDoubleRotations++;
				}
		}
		else {
			throw new Exception("Value already exists in the tree.");
		}

		z.height = Math.max (getNodeHeight (z.leftChild), getNodeHeight (z.rightChild)) + 1;
		return z; // z is the root node obviously
	}
	private AvlNode<T> rotateWithLeftChild (AvlNode<T> node1){
		AvlNode<T> node2 = node1.leftChild;

		node1.leftChild = node2.rightChild;
		node2.rightChild = node1;

		node1.height = Math.max (getNodeHeight (node1.leftChild), getNodeHeight (node1.rightChild)) + 1;
		node2.height = Math.max (getNodeHeight (node2.leftChild), node1.height) + 1;

		return (node2);
	}
	private AvlNode<T> doubleWithLeftChild (AvlNode<T> node){
		node.leftChild = rotateWithRightChild (node.leftChild);
		return rotateWithLeftChild (node);
	}
	private AvlNode<T> rotateWithRightChild (AvlNode<T> node1){
		AvlNode<T> node2 = node1.rightChild;

		node1.rightChild = node2.leftChild;
		node2.leftChild = node1;

		node1.height = Math.max (getNodeHeight (node1.leftChild), getNodeHeight (node1.rightChild)) + 1;
		node2.height = Math.max (getNodeHeight (node2.rightChild), node1.height) + 1;

		return (node2);
	}
	private AvlNode<T> doubleWithRightChild (AvlNode<T> node){
		node.rightChild = rotateWithLeftChild (node.rightChild);
		return rotateWithRightChild (node);
	}
	public String infixNotation(){
		StringBuilder str = new StringBuilder();
		infixNotation (root, str, " ");
		return str.toString();
	}
	private void infixNotation(AvlNode<T> node, StringBuilder str, String seperator){
		if (node != null){
			infixNotation (node.leftChild, str, seperator);
			str.append(node.value.toString());
			str.append(seperator);
			infixNotation (node.rightChild, str, seperator);
		}    
	}
	public String prefixNotation(){
		StringBuilder str = new StringBuilder();
		prefixNotation (root, str, " ");
		return str.toString();
	}
	private void prefixNotation (AvlNode<T> t, StringBuilder str, String sep){
		if (t != null){
			str.append(t.value.toString());
			str.append(sep);
			prefixNotation (t.leftChild, str, sep);
			prefixNotation (t.rightChild, str, sep);
		}
	}
	public void makeEmpty(){
		root = null;
	}
	public boolean isEmpty(){
		return (root == null);
	}
	public T minValue( )
	{
		if( isEmpty( ) ) return null;

		return minValue( root ).value;
	}
	public T maxValue( )
	{
		if( isEmpty( ) ) return null;
		return maxValue( root ).value;
	}
	private AvlNode<T> minValue(AvlNode<T> node)
	{
		if( node == null )
			return node;

		while( node.leftChild != null )
			node = node.leftChild;
		return node;
	}
	private AvlNode<T> maxValue( AvlNode<T> node )
	{
		if( node == null )
			return node;

		while( node.rightChild != null )
			node = node.rightChild;
		return node;
	}
	public void remove( T node ) {
		root = remove(node, root);
	}
	private AvlNode<T> remove(T w, AvlNode<T> z) {
		// deletion algorithm:  (reference: geeksforgeeks.org)
		/*
		    Let w be the node to be deleted
			1) Perform standard BST delete for w.
			2) Starting from w, travel up and find the first unbalanced node. 
					Let z be the first unbalanced node, y be the larger height child of z,
					and x be the larger height child of y. Note that the definitions of x 
					and y are different from insertion here.
			3) Re-balance the tree by performing appropriate rotations on the subtree rooted with z.
					There can be 4 possible cases that needs to be handled as x, y and z
					can be arranged in 4 ways. Following are the possible 4 arrangements:
				
				a) y is left child of z and x is left child of y (Left Left Case)
					T1, T2, T3 and T4 are subtrees.
				         z                                      y 
				        / \                                   /   \
				       y   T4      Right Rotate (z)          x      z
				      / \          - - - - - - - - ->      /  \    /  \ 
				     x   T3                               T1  T2  T3  T4
				    / \
				  T1   T2
				
				b) y is left child of z and x is right child of y (Left Right Case)
					     z                               z                           x
					    / \                            /   \                        /  \ 
					   y   T4  Left Rotate (y)        x    T4  Right Rotate(z)    y      z
					  / \      - - - - - - - - ->    /  \      - - - - - - - ->  / \    / \
					T1   x                          y    T3                    T1  T2 T3  T4
					    / \                        / \
					  T2   T3                    T1   T2
				
				c) y is right child of z and x is right child of y (Right Right Case)
					  z                                y
					 /  \                            /   \ 
					T1   y     Left Rotate(z)       z      x
					    /  \   - - - - - - - ->    / \    / \
					   T2   x                     T1  T2 T3  T4
					       / \
					     T3  T4
				
				d) y is right child of z and x is left child of y (Right Left Case)
					   z                            z                            x
					  / \                          / \                          /  \ 
					T1   y   Right Rotate (y)    T1   x      Left Rotate(z)   z      x
					    / \  - - - - - - - - ->     /  \   - - - - - - - ->  / \    / \
					   x   T4                      T2   y                  T1  T2  T3  T4
					  / \                              /  \
					T2   T3                           T3   T4
				
		 */
		if (z==null)    {
			System.out.println("No such value found in the tree.");
			return null;
		}
		if (w.compareTo(z.value) < 0 ) {
			z.leftChild = remove(w,z.leftChild);
			int l = z.leftChild != null ? z.leftChild.height : 0;

			if((z.rightChild != null) && (z.rightChild.height - l >= 2)) {
				int rightHeight = z.rightChild.rightChild != null ? z.rightChild.rightChild.height : 0;
				int leftHeight = z.rightChild.leftChild != null ? z.rightChild.leftChild.height : 0;

				if(rightHeight >= leftHeight)
					z = rotateWithLeftChild(z); // left left case            
				else
					z = doubleWithRightChild(z); // left right case
			}
		}
		else if (w.compareTo(z.value) > 0) {
			z.rightChild = remove(w,z.rightChild);
			int r = z.rightChild != null ? z.rightChild.height : 0;
			if((z.leftChild != null) && (z.leftChild.height - r >= 2)) {
				int leftHeight = z.leftChild.leftChild != null ? z.leftChild.leftChild.height : 0;
				int rightHeight = z.leftChild.rightChild != null ? z.leftChild.rightChild.height : 0;
				if(leftHeight >= rightHeight)
					z = rotateWithRightChild(z); // right right case                
				else
					z = doubleWithLeftChild(z); // right left case
			}
		}
		else if(z.leftChild != null) { // w.compareTo(z.value) == 0
			z.value = maxValue(z.leftChild).value;
			remove(z.value, z.leftChild);

			if((z.rightChild != null) && (z.rightChild.height - z.leftChild.height >= 2)) {
				int rightHeight = z.rightChild.rightChild != null ? z.rightChild.rightChild.height : 0;
				int leftHeight = z.rightChild.leftChild != null ? z.rightChild.leftChild.height : 0;

				if(rightHeight >= leftHeight)
					z = rotateWithLeftChild(z);            
				else
					z = doubleWithRightChild(z);
			}
		}
		else // w.compareTo(z.value) > 0 && z.leftChild == null
			z = (z.leftChild != null) ? z.leftChild : z.rightChild;

		if(z != null) {
			int leftHeight = z.leftChild != null ? z.leftChild.height : 0;
			int rightHeight = z.rightChild!= null ? z.rightChild.height : 0;
			z.height = Math.max(leftHeight,rightHeight) + 1;
		}
		return z;
	}
	public boolean contains(T x){
		return contains(x, root); 
	}
	private boolean contains(T node, AvlNode<T> from) {
		if (from == null){
			return false; // The node was not found

		} else if (node.compareTo(from.value) < 0){
			return contains(node, from.leftChild);
		} else if (node.compareTo(from.value) > 0){
			return contains(node, from.rightChild); 
		}

		return true;
	}
	public boolean checkBalance(AvlTree.AvlNode<Integer> current) {

		boolean balancedRight = true, balancedLeft = true;
		int leftHeight = 0, rightHeight = 0;

		if (current.rightChild != null) {
			balancedRight = checkBalance(current.rightChild);
			rightHeight = getDepth(current.rightChild);
		}

		if (current.leftChild != null) {
			balancedLeft = checkBalance(current.leftChild);
			leftHeight = getDepth(current.leftChild);
		}

		return balancedLeft && balancedRight && Math.abs(leftHeight - rightHeight) < 2;
	}

	public int getDepth(AvlTree.AvlNode<Integer> n) {
		int leftHeight = 0, rightHeight = 0;

		if (n.rightChild != null)
			rightHeight = getDepth(n.rightChild);
		if (n.leftChild != null)
			leftHeight = getDepth(n.leftChild);

		return Math.max(rightHeight, leftHeight) + 1;
	}

	public boolean checkOrdering(AvlTree.AvlNode<Integer> current) {
		if(current.leftChild != null) {
			if(current.leftChild.value.compareTo(current.value) > 0)
				return false;
			else
				return checkOrdering(current.leftChild);
		} else  if(current.rightChild != null) {
			if(current.rightChild.value.compareTo(current.value) < 0)
				return false;
			else
				return checkOrdering(current.rightChild);
		} else if(current.leftChild == null && current.rightChild == null)
			return true;

		return true;
	}


	public static void main (String[] args) {
		AvlTree<Integer> t = new AvlTree<Integer>();

		t.insert (new Integer(2));
		t.insert (new Integer(1));
		t.insert (new Integer(4));
		t.insert (new Integer(5));
		t.insert (new Integer(9));
		t.insert (new Integer(3));
		t.insert (new Integer(6));
		t.insert (new Integer(7));

		System.out.println ("Infix Traversal:");
		System.out.println(t.infixNotation());

		System.out.println ("Prefix Traversal:");
		System.out.println(t.prefixNotation());

	}
}
