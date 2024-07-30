package cn.com.idmy.orm.core.constant;

public enum SqlConnector {
    AND(" AND "),
    OR(" OR "),
    ;
    private final String value;

    SqlConnector(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
