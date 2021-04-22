class Node {
    int row;
    int col;
    Node next;
    Node prev;

    Node(int row, int col, Node next, Node prev){
        this.row = row;
        this.col = col;
        this.next = next;
        this.prev = prev;
    }
}
