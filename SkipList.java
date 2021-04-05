// Name: Aryan Jha
// NID: ar392004
// Class: COP 3503, Fall 2020
// This code is not authorized to be reproduced!!!!

import java.util.*;
import java.lang.*;

// declaring the Node class will be used in the skiplist
// this class is generic
class Node <AnyType extends Comparable<AnyType>>
{
  // declaring class members
  AnyType data;
  int height;
  // creating an arraylist of references to act as the
  // pointers of this node. Arraylists were chosen
  // because of their dynamic resizing
  ArrayList<Node<AnyType>> references;

  // constructor to create a node if only height is provided
  Node(int height)
  {
    this.height = height;
    this.references = new ArrayList<Node<AnyType>>();
    // initializing the head node with null pointers
    for (int i = 0; i < height; i++)
    {
      references.add(null);
    }
  }
  // constructor to create a node if height and data provided
  Node(int height, AnyType data)
  {
    this.data = data;
    this.height = height;
    this.references = new ArrayList<Node<AnyType>>();
    // initializing the node with null pointers
    for (int i = 0; i < height; i++)
    {
      references.add(null);
    }
  }
  // returns the value of the node
  public AnyType value()
  {
    return this.data;
  }
  // returns the height of this node
  public int height()
  {
    return this.height;
  }

  // returns the node that the current node is pointing to at
  // a certain level
  public Node<AnyType> next(int level)
  {
    // if this level is below or above height, it is invalid so return null
    if (level < 0 || level > height() - 1)
      {
        return null;
      }
    else
      {
        if (this.references.get(level) != null)
        {

        }
        // return the node found, can be null
        return this.references.get(level);
      }
  }

  // reduces the height of the current node by removing references from the
  // top of the its list of references until it reaches the new height
  public void trim(int height)
  {
    // remove top references of this node until we reach given height
    while (this.references.size() != height)
    {
      // remove references from the end of the list, one by one
      this.references.remove(references.size() - 1);
    }
    // set the height of this node to the new height
    this.height = height;
  }

}

// declaring the class SkipList which is generic
public class SkipList <AnyType extends Comparable<AnyType>>
{
  // defining class members
  Node <AnyType> head;
  int num_nodes = 0;

  // constructor if no initial height is provided
  SkipList()
  {
    // initialize the height to one
    this.head = new Node<AnyType>(1);
    // we will count the head node as a node, but will account
    // for this when actually dealing with calculations
    this.num_nodes = 1;
  }
  // constructor if initial height is provided
  SkipList(int height)
  {
    // create the head node, calling the constructor which has
    // an initial height
    this.head = new Node<AnyType>(height);
    this.num_nodes = 1;
  }

  // returns the number of nodes in the skiplist
  public int size()
  {
    // return num nodes - 1 because we account for counting the head node as
    // a part of the num nodes count
    return this.num_nodes - 1;
  }

  // returns the height of the skiplist
  public int height()
  {
    // following the structure of a skiplist, the head node must contain the
    // maximum height, thus the size of its references is the height of the
    // skiplist
    return this.head.references.size();
  }

  // returns the head of the skiplist
  public Node<AnyType> head()
  {
    return this.head;
  }

  // creates and inserts a node with data
  public void insert(AnyType data)
  {
    // check if we need to update max height of skiplist after inserting this node
    this.num_nodes += 1;
    // create the new node and generate a random height which respects the limits
    // given by the skiplist's current number of nodes
    Node <AnyType> new_node = new Node<AnyType>
    (generateRandomHeight(getMaxHeight(this.num_nodes)), data);

    // if the height is surpassed by the maxheight of inserting a new node
    // then we must grow the skiplist
    if (height() < getMaxHeight(this.num_nodes))
    {
      growSkipList();
    }

    // inserting the node into the skiplist
    Node<AnyType> traversal_node = this.head;
    // create an arraylist to store the nodes where we drop down from to update
    // their references if necessary
    ArrayList<Node<AnyType>> update_nodes = new ArrayList<Node<AnyType>>();

    // traverse through the height of the node until you reach the last level
    for (int i = traversal_node.references.size() - 1; i >= 0; i--)
    {
      // set traversal node to the next node at this level if the value of next
      // is less than what value we are inserting and continue along this level
      while (traversal_node.references.get(i) != null &&
      traversal_node.references.get(i).data.compareTo(data) < 0)
      {
        traversal_node = traversal_node.references.get(i);
      }
      // if we reach here, then we have to drop down a level in the current node,
      // so we must add this node to be updated
      update_nodes.add(traversal_node);
    }

    // loop through the height of the new node and adjust any update nodes
    // if necessary
    for (int i = 0; i < new_node.height; i++)
    {
      // this checks if the level we dropped down from is valid
      if (update_nodes.size() - i - 1 >= 0)
      {
        // relink the references starting with linking the new node to update nodes
        // reference at the level we dropped down from, then linking the update node to
        // new node at the same level (to avoid losing reference to it)
        new_node.references.set(i ,update_nodes.get(update_nodes.size() - i - 1)
        .references.get(i));
        update_nodes.get(update_nodes.size() - i - 1).references.set(i, new_node);
      }
    }
  }

  // creates and inserts a node with preset height into the skiplist
  public void insert(AnyType data, int height)
  {
    // create the new node and generate a random height which respects the limits
    // given by the skiplist's current number of nodes
    Node<AnyType> new_node = new Node<AnyType>(height, data);
    this.num_nodes += 1;
    // if the height is surpassed by the maxheight of inserting a new node
    // then we must grow the skiplist (property of skiplists)
    if (height() < getMaxHeight(this.num_nodes))
    {
      growSkipList();
    }
    // create an arraylist to store the nodes where we drop down from to update
    // their references if necessary
    ArrayList<Node<AnyType>> update_nodes = new ArrayList<Node<AnyType>>();
    Node<AnyType> traversal_node = this.head;
    // set traversal node to the next node at this level if the value of next
    // is less than what value we are inserting
    for (int i = traversal_node.references.size() - 1; i >= 0; i--)
    {
      // set traversal node to the next node at this level if the value of next
      // is less than what value we are inserting, and continue along this path
      while (traversal_node.references.get(i) != null &&
      traversal_node.references.get(i).data.compareTo(data) < 0)
      {
        traversal_node = traversal_node.references.get(i);
      }
      // if we reach here, then we have to drop down a level in the current node,
      // so we must add this node to be updated
      update_nodes.add(traversal_node);
    }

    // loop through the height of the new node and adjust any update nodes
    // if necessary
    for (int i = 0; i < new_node.height; i++)
    {
      // this checks if the level we dropped down from is valid
      if (update_nodes.size() - i - 1 >= 0)
      {
        // relink the references starting with linking the new node to update nodes
        // reference at the level we dropped down from, then linking the update node to
        // new node at the same level (to avoid losing reference to it)
        new_node.references.set(i, update_nodes.get(update_nodes.size() - i - 1)
        .references.get(i));
        update_nodes.get(update_nodes.size() - i - 1).references.set(i, new_node);
      }
    }
  }

  // delete the first instance of a node containing given data
  public void delete(AnyType data)
  {
    // search through the nodes using same technique in insert
    // if the next node is greater than deletion node value, drop down a level
    // if the next node is less than deletion node value, continue on this level
    Node<AnyType> traversal_node = this.head;
    ArrayList<Node<AnyType>> update_nodes = new ArrayList<Node<AnyType>>();
    boolean deleted = false;
    for (int i = traversal_node.references.size() - 1; i >= 0; i--)
    {
      while (traversal_node.references.get(i) != null &&
      traversal_node.references.get(i).data.compareTo(data) < 0)
      {
        traversal_node = traversal_node.references.get(i);
      }
      update_nodes.add(traversal_node);
    }

    // the traversal node's next node should be equal to the node we want to delete
    // since we break out of the loop when we are the bottomest level of a node
    Node <AnyType> node_to_delete = traversal_node.references.get(0);

    // check if this is the node we are looking to delete
    if (node_to_delete != null && node_to_delete.data.compareTo(data) == 0)
    {
      // loop through the references of the node to be deleted in order
      // to relink and deference as needed
      for (int i = 0; i < node_to_delete.height; i++)
      {
        if (update_nodes.size() - i - 1 >= 0)
        {
          // element level is represents the node we dropped down from
          int element_level = update_nodes.size() - i - 1;
          // grab the current update node from the list of node's to be updated
          Node <AnyType> node_update = update_nodes.get(element_level);
          // set the node to be updated's reference to the deletion node's reference
          // at i
          node_update.references.set(i, node_to_delete.references.get(i));
          // delink the deletion node by setting it's reference to null
          node_to_delete.references.set(i, null);
        }
      }
      // reduce the number of nodes, but make sure that it's not negative
      if (this.num_nodes - 1 <= 0)
      {
        this.num_nodes = 1;
      }
      else
      {
        this.num_nodes -= 1;
      }
      // if log (n) < height of skiplist after deletion, we must
      // trim the size of the skiplist
      double log_2 = Math.log(this.num_nodes-1) / Math.log(2);
      int ceiling = (int)Math.ceil(log_2);
      if (ceiling < height())
      {
        // check for cases when ceiling may become 0 (log 1), and account for
        // by setting it to one
        if (ceiling < 1)
        {
          ceiling = 1;
        }
        // trim the skiplist with this new height
        trimSkipList(ceiling);
      }

    }
    // else do nothing, the node to be deleted was not found
  }

  // returns whether or not the node containing data is in the skiplist
  public boolean contains(AnyType data)
  {
    Node<AnyType> traversal_node = this.head;
    // loop from the highest level of the skiplist down
    for (int i = traversal_node.references.size() - 1; i >= 0; i--)
    {
      // continue along same level if the next node is less than the node we are searching for
      // and while we haven't encountered a null pointer
      while (traversal_node.references.get(i) != null &&
      traversal_node.references.get(i).data.compareTo(data) < 0)
      {
        traversal_node = traversal_node.references.get(i);
      }
      // if we break from the while loop, we encountered a node greater than
      // the node we are searching for or the end of the list, so we drop down
      // a level and continue the search
    }
    // we have reached the lowest level of the node, so the next
    // node must contain our data
    if (traversal_node.references.get(0).data.compareTo(data) == 0)
    {
      return true;
    }

    return false;
  }
  // returns the node which contains the data we are looking for
  public Node<AnyType> get(AnyType data)
  {
    Node<AnyType> traversal_node = this.head;
    // loop from the highest level of the skiplist down
    for (int i = traversal_node.references.size() - 1; i >= 0; i--)
    {
      // continue along same level if the next node is less than the node we are searching for
      // and while we haven't encountered a null pointer
      while (traversal_node.references.get(i) != null &&
      traversal_node.references.get(i).data.compareTo(data) < 0)
      {
        traversal_node = traversal_node.references.get(i);
      }
      // if we break from the while loop, we encountered a node greater than
      // the node we are searching for or the end of the list, so we drop down
      // a level and continue the search
    }
    // if we reached the bottom of a node,
    // the next node should contain our data if it exists
    if (traversal_node.references.get(0).data.compareTo(data) == 0)
    {
      return traversal_node.references.get(0);
    }
    return null;
  }
  // returns the difficultyRating of this project out of a scale of 5.
  public static double difficultyRating()
  {
    return 5.0;
  }

  // returns the total amount of time I spent on this project
  public static double hoursSpent()
  {
    return 6.0;
  }

  // Suggested methods
  // returns the max height possible for a skiplist of height n
  private int getMaxHeight(int n)
  {
    // log_2(n) = ln(n) / ln(2) from change of base formula
    // we use n - 1 because I counted the head node as a node, so we have to
    // remove to account for that
    double log_2 = Math.log(n - 1) / Math.log(2);
    int ceiling = (int) Math.ceil(log_2);
    return Math.max(ceiling, height());
  }


  private static int generateRandomHeight(int maxHeight)
  {
    // general idea is to flip a coin multiple times
    // if tails --> return height
    // if heads --> increase height and flip again
    int random_height = 1;
    while (Math.random() < 0.5 && random_height < maxHeight)
    {
      random_height += 1;
      continue;
    }
    return random_height;
  }

  // grows the skiplist by increasing the height of any maxed out nodes
  // at a 50% probability each
  private void growSkipList()
  {
    int old_height = height();
    int new_height = height() + 1;
    // increase the height of our head node by adding a null reference
    this.head.references.add(null);
    // increase this nodes height
    this.head.height += 1;
    // store the head as previous so we don't lose reference to it
    Node <AnyType> previous = this.head;
    // start traversing at the old maximum height - 1 (to prevent index out of bounds)
    Node <AnyType> traversal_node = this.head.references.get(old_height);
    // loop through all nodes that have the old height and give them
    // a 50% chance to increase their height by one
    while (traversal_node != null)
    {
      if (Math.random() < 0.5)
      {
        // set previous node's reference at the new height to point to this node
        previous.references.set(new_height, traversal_node);
        // increase the height of the traversal node by adding a null reference to it
        traversal_node.references.add(null);
        // update the node's height
        traversal_node.height += 1;
      }
      // save the current node as previous to prevent any loss of reference
      previous = traversal_node;
      // traverse to the next node whose height was the old max height
      traversal_node = traversal_node.references.get(old_height - 1);
    }

  }


  // all nodes that exceed the new maximum height should simply be
  // trimmed down to the new maximum height.
  private void trimSkipList(int ceiling)
  {
    // if this method is called, then we have to reduce the height of skiplist
    int old_height = height();
    int new_height = ceiling;
    Node <AnyType> traversal_node = this.head;
    Node <AnyType> next = null;
    // save next reference so we don't (possibly) lose the link to it when we remove
    // referencees from head node
    next = traversal_node.references.get(0);
    // deal with head node by trimming its references down to new height
    traversal_node.trim(new_height);
    traversal_node = next;
    while (traversal_node != null)
    {
      // grab the next node from the current node's maximum height so we only deal
      // with nodes that exceed the ceiling height
      next = traversal_node.references.get(traversal_node.references.size() - 1);
      // only trim the node if it exceeds the ceiling height
      if (traversal_node.references.size() > new_height)
      {
        traversal_node.trim(new_height);
      }
      // set current to the next to continue traversing
      traversal_node = next;
    }

  }

}
