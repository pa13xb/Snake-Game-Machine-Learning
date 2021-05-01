/**
 * @author Philip Akkerman
 * studentID 5479613
 * email pa13xb@brocku.ca
 *
 * @author David Hasler
 * studentID 6041321
 * email dh15pd@brocku.ca
 *
 * This is the Node class where each Node is one block of the Snake.
 */
class Node {
    int row; //the row of the snake part (y value)
    int col; //the column of the snake part (x value)
    Node next; //the next snake part in the list
    Node prev; //the previous snake part in the list

    Node(int row, int col, Node next, Node prev){
        this.row = row;
        this.col = col;
        this.next = next;
        this.prev = prev;
    }//constructor
}
