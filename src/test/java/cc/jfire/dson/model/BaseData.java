package cc.jfire.dson.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseData
{
    private float        a       = 2.2365f;
    public  double       b       = 15.689;
    private double       percent = 88.8121218;
    private String       testNull;
    private List<String> testNullList;
}
