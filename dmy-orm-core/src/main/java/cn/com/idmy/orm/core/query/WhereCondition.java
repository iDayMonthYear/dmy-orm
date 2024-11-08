package cn.com.idmy.orm.core.query;

public class WhereCondition  {
    protected Column column;
    protected String logic;
    protected Object value;
    protected boolean effective = true;

    protected WhereCondition prev;
    protected WhereCondition next;
    protected SqlConnector connector;

    private boolean empty = false;

    protected void connect(WhereCondition nextCondition, SqlConnector connector) {
        if (this.next == null) {
            nextCondition.connector = connector;
            this.next = nextCondition;
            nextCondition.prev = this;
        } else {
            this.next.connect(nextCondition, connector);
        }
    }
}
