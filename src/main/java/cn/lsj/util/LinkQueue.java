package cn.lsj.util;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/7 23:47
 * @Description: 当用户上传文件时，是分块上传的。所以先把上传文件的请求保存在队列中，
 *                 再逐一处理队列中的文件，防止用户并发上传文件时，不同文件的文件块串位。
 *                 此队列是一个FIFO队列，即先进先出
 */
public class LinkQueue<E> {

    // 链栈的节点
    private class Node {
        E e;
        Node next;

        public Node() {
        }

        Node(E e, Node next) {
            this.e = e;
            this.next = next;
        }
    }

    private Node front;// 队列头，允许删除
    private Node rear;// 队列尾，允许插入
    private int size; //队列当前长度

    public LinkQueue() {
        front = null;
        rear = null;
    }

    /**
     * 判空
     * */
    public boolean empty(){
        return size==0;
    }

    /**
     * 入队
     * */
    public synchronized boolean add(E e){
        if(empty()){    //如果队列为空
            front = new Node(e,null);//只有一个节点，front、rear都指向该节点
            rear = front;
        }else{
            Node newNode = new Node(e, null);
            rear.next = newNode; //让尾节点的next指向新增的节点
            rear = newNode; //以新节点作为新的尾节点
        }
        size ++;
        return true;
    }

    /**
     * 返回队首元素，但不删除
     * */
    public Node peek(){
        if(empty()){
            throw new RuntimeException("空队列异常！");
        }else{
            return front;
        }
    }

    /**
     * 出队
     * */
    public synchronized E poll(){
        if(empty()){
            //throw new RuntimeException("空队列异常！");
            return null;
        }else{
            Node value = front; //得到队列头元素
            front = front.next;//让front引用指向原队列头元素的下一个元素
            value.next = null; //释放原队列头元素的next引用
            size --;
            return value.e;
        }
    }

    //队列长度
    public int length(){
        return size;
    }
}
