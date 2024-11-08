package cn.com.idmy.orm.core.query.ast;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Delete {
    List<Object> asts = new ArrayList<>();

    void addAst(Object ast) {
        asts.add(ast);
    }

    public String buildSql() {
        return "";
    }

    public From from(String table) {
        From ast = new From(table);
        addAst(ast);
        return ast;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public class From {
        String table;

        public Where where() {
            Where ast = new Where();
            addAst(ast);
            return ast;
        }

        public String sql() {
            return buildSql();
        }
    }


    public class Where {
        public And and() {
            And ast = new And();
            addAst(ast);
            return ast;
        }
    }

    public class And {
        public And eq(String col, Object val) {
            Eq ast = new Eq(col, val);
            addAst(ast);
            return this;
        }

        public Or or() {
            Or ast = new Or();
            addAst(ast);
            return ast;
        }

        public String sql() {
            return buildSql();
        }
    }

    public class Eq {
        Object left;
        Object right;
        boolean or;

        public Eq(Object left, Object right) {
            this.left = left;
            this.right = right;
        }

        public String sql() {
            return buildSql();
        }
    }

    public class Or {
        public And and() {
            And ast = new And();
            addAst(ast);
            return ast;
        }

        public Or eq(String left, Object right) {
            Eq ast = new Eq(left, right);
            addAst(ast);
            return this;
        }

        public String sql() {
            return buildSql();
        }
    }

    public static void main(String[] args) {
        new Delete().from(Delete.class.getSimpleName()).where().and().eq("a", 1);
    }
}
