package avltree;
import java.lang.StringBuilder;
import java.lang.Math;
import java.util.ArrayList;

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

	public AvlTree (){
		root = null;        
		numberOfNodes = 0;
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

			if (getBalance(z) == 2){ // a or b
				if (w.compareTo (z.leftChild.value) < 0){ // a: left left case
					z = rotateWithLeftChild (z);
				}
				else { // left right case
					z = rotateWithRightThenLeft (z);
				}
			}
		}
		else if (w.compareTo (z.value) > 0){ // c or d
			z.rightChild = insert (w, z.rightChild);

			if ( getBalance(z) == -2)
				if (w.compareTo (z.rightChild.value) > 0){ // right right case
					z = rotateWithRight (z);
				}
				else{ // right left case
					z = rotateWithLeftThenRight (z);
				}
		}
		else {
			throw new Exception("Value already exists in the tree.");
		}

		z.height = Math.max (getNodeHeight (z.leftChild), getNodeHeight (z.rightChild)) + 1;
		return z; // z is the root node obviously
	}
	private AvlNode<T> rotateWithLeftChild (AvlNode<T> father){
		AvlNode<T> lChild = father.leftChild;

		father.leftChild = lChild.rightChild;
		lChild.rightChild = father;

		father.height = Math.max (getNodeHeight (father.leftChild), getNodeHeight (father.rightChild)) + 1;
		lChild.height = Math.max (getNodeHeight (lChild.leftChild), getNodeHeight(father)) + 1;

		return (lChild);
	}
	private AvlNode<T> rotateWithRightThenLeft (AvlNode<T> node){
		node.leftChild = rotateWithRight (node.leftChild);
		return rotateWithLeftChild (node);
	}
	private AvlNode<T> rotateWithRight (AvlNode<T> father){
		AvlNode<T> rChild = father.rightChild;

		father.rightChild = rChild.leftChild;
		rChild.leftChild = father;

		father.height = Math.max (getNodeHeight (father.leftChild), getNodeHeight (father.rightChild)) + 1;
		rChild.height = Math.max (getNodeHeight (rChild.rightChild), getNodeHeight(father)) + 1;

		return (rChild);
	}
	private AvlNode<T> rotateWithLeftThenRight (AvlNode<T> node){
		node.rightChild = rotateWithLeftChild (node.rightChild);
		return rotateWithRight (node);
	}
	public String infixNotation(){
		StringBuilder str = new StringBuilder();
		infixNotation (root, str, " ");
		return str.toString();
	}
	private void infixNotation(AvlNode<T> node, StringBuilder str, String seperator){
		if (node != null){
			infixNotation (node.leftChild, str, seperator);
			str.append(node.value.toString() + seperator);
			infixNotation (node.rightChild, str, seperator);
		}    
	}
	public String prefixNotation(){
		StringBuilder str = new StringBuilder();
		prefixNotation (root, str, " ");
		return str.toString();
	}
	private void prefixNotation (AvlNode<T> t, StringBuilder str, String seperator){
		if (t != null){
			str.append(t.value.toString() + seperator);
			prefixNotation (t.leftChild, str, seperator);
			prefixNotation (t.rightChild, str, seperator);
		}
	}
	public T minValue( )
	{
		if( root == null ) return null;

		return minValue( root ).value;
	}
	public T maxValue( )
	{
		if( root == null ) return null;
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
	private int getBalance(AvlNode<T> node)
	{
		if (node == null)
			return 0;
		numberOfNodes--;
		return getNodeHeight(node.leftChild) - getNodeHeight(node.rightChild);
	}
	private AvlNode<T> remove(T w, AvlNode<T> z) {
		// deletion algorithm:  (reference: geeksforgeeks.org)
		/*

		    T1, T2 and T3 are subtrees of the tree rooted with y (on left side)
				or x (on right side)
                y                               x
               / \     Right Rotation          /  \
              x   T3   – - – - – - – >        T1   y
             / \       < - - - - - - -            / \
            T1  T2     Left Rotation            T2  T3
			Keys in both of the above trees follow the following order
			      keys(T1) < key(x) < keys(T2) < key(y) < keys(T3)
			So BST property is not violated anywhere.



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
		if (z == null)
		{
			System.out.println("No such value found.");
			return z;
		}
		else if(w.compareTo(z.value) < 0) // search key on the left subtree
			z.leftChild = remove(w, z.leftChild);
		else if(w.compareTo(z.value) > 0) // search key on the right subtree
			z.rightChild = remove(w, z.rightChild);

		else { // the key is found!

			// delete node
			if (z.rightChild == null || z.leftChild == null) {
				z = z.rightChild == null ? z.leftChild : z.rightChild;
			}
			else
			{
				z.value = minValue(z.rightChild).value;
				z.rightChild = remove(z.value, z.rightChild);
			}

		}

		if (z == null)
			return null;

		z.height = Math.max(getNodeHeight(z.leftChild), getNodeHeight(z.rightChild)) + 1;
		if (getBalance(z) > 1)
		{
			if (getBalance(z.leftChild) >= 0)
				return rotateWithLeftChild(z); // left left case
			else // if (getBalance(z.leftChild) < 0)
				return rotateWithRightThenLeft(z); // left right case
		}

		else if(getBalance(z) < -1)
		{
			if (getBalance(z.rightChild) <= 0)
				return rotateWithRight(z); // right right case
			else // if (getBalance(z.rightChild) > 0)
				return rotateWithLeftThenRight(z); // right left case
		}


		return z;

	}
	public boolean contains(T x){
		return hasNode(x, root); 
	}
	private boolean hasNode(T node, AvlNode<T> from) {
		if (from == null){
			return false; // The node was not found

		} else if (node.compareTo(from.value) < 0){
			return hasNode(node, from.leftChild);
		} else if (node.compareTo(from.value) > 0){
			return hasNode(node, from.rightChild); 
		}

		return true;
	}

	public boolean search(T x)
	{
		if (!contains(x))
			return false;

		ArrayList<T> elements = new ArrayList<T>();
		while (true) { // remove every node
			if (root != null)
			{
				elements.add(root.value);
				remove(root.value);
			}
			else
				break;
		}
		if (root != null)
			remove(root.value);
		if (numberOfNodes == 0)
		{
			insert(x);
			for (T elem : elements)
				insert(elem);
			return true;
		}
		else
			System.out.println("number of nodes before recreation of the tree: " + numberOfNodes);
		return false;

	}

	public static void main (String[] args) {
		AvlTree<Integer> t = new AvlTree<Integer>();

		t.insert (new Integer(2));
		t.insert (new Integer(4));
		t.insert (new Integer(1));
		t.insert (new Integer(5));
		t.insert (new Integer(9));
		t.insert (new Integer(3));
		t.insert (new Integer(6));
		t.insert (new Integer(7));

		System.out.println ("Infix Traversal:");
		System.out.println(t.infixNotation());

		System.out.println ("Prefix Traversal:");
		System.out.println(t.prefixNotation());

		t.search(5);

		System.out.println ("Infix Traversal after search(5):");
		System.out.println(t.infixNotation());

		System.out.println ("Prefix Traversal after search(5):");
		System.out.println(t.prefixNotation());

		t.remove(new Integer(6));
		
		System.out.println ("Infix Traversal after remove(6):");
		System.out.println(t.infixNotation());

		System.out.println ("Prefix Traversal after remove(6):");
		System.out.println(t.prefixNotation());

	}
}
