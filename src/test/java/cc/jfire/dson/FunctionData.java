package cc.jfire.dson;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class FunctionData
{
    private int                     a;
    private float                   b;
    private double                  c;
    private long                    d;
    private char                    e;
    private boolean                 f;
    private short                   g;
    private              byte                    h;
    private              Integer                 a1;
    private              Float                   b1;
    private              Double                  c1;
    private              Long                    d1;
    private              String                  e1;
    private              Boolean                 f1;
    private              Short                   g1;
    private              Byte                    h1;
    private NestData            nestData;
    private ArrayList<String>   list;
    private ArrayList<NestData> datas;
    private ArrayList<String>       nolist;
    private HashMap<String, String> map;
    private int[][]                 array2;
    private              String[]                strs;
    private              int[]                   array1;
    private              char[]                  chars;
    private              Integer[]               array3;
    private              Integer[][]             array4;
    private              NestData[]              nestDatas;
    private              ArrayList<String>[]     lists;
    private              Object                  data;
    private static final Logger                  logger = LoggerFactory.getLogger(cc.jfire.dson.Data.class);

    public boolean equal(Object target)
    {
        if (target instanceof FunctionData)
        {
            FunctionData entity = (FunctionData) target;
            if (a != entity.getA() || b != entity.getB() || c != entity.getC() || d != entity.getD() || e != entity.getE() || f != entity.isF() || g != entity.getG() || h != entity.getH())
            {
                return false;
            }
            if (a1.equals(entity.getA1()) == false || b1.equals(entity.getB1()) == false || c1.equals(entity.getC1()) == false || d1.equals(entity.getD1()) == false || e1.equals(entity.getE1()) == false || f1.equals(entity.getF1()) == false || g1.equals(entity.getG1()) == false || h1.equals(entity.getH1()) == false)
            {
                return false;
            }
            if (nestData.equals(entity.getNestData()) == false)
            {
                return false;
            }
            if (list.equals(entity.getList()) == false)
            {
                return false;
            }
            if (datas.equals(entity.getDatas()) == false)
            {
                return false;
            }
            if (entity.getNolist().size() != 0)
            {
                return false;
            }
            if (map.equals(entity.getMap()) == false)
            {
                return false;
            }
            for (int i = 0; i < array2.length; i++)
            {
                for (int j = 0; j < array2[i].length; j++)
                {
                    if (array2[i][j] != entity.getArray2()[i][j])
                    {
                        return false;
                    }
                }
            }
            for (int i = 0; i < strs.length; i++)
            {
                if (strs[i].equals(entity.getStrs()[i]) == false)
                {
                    logger.debug("traceId:{} strs不相等");
                    return false;
                }
            }
            for (int i = 0; i < array1.length; i++)
            {
                if (array1[i] != entity.getArray1()[i])
                {
                    logger.debug("traceId:{} array1不相等");
                    return false;
                }
            }
            for (int i = 0; i < chars.length; i++)
            {
                if (chars[i] != entity.getChars()[i])
                {
                    logger.debug("traceId:{} chars不相等");
                    return false;
                }
            }
            for (int i = 0; i < array3.length; i++)
            {
                if (array3[i].equals(entity.getArray3()[i]) == false)
                {
                    logger.debug("traceId:{} array3不相等");
                    return false;
                }
            }
            for (int i = 0; i < array4.length; i++)
            {
                for (int j = 0; j < array4[i].length; j++)
                {
                    if (array4[i][j].equals(entity.getArray4()[i][j]) == false)
                    {
                        logger.debug("traceId:{} array4不相等");
                        return false;
                    }
                }
            }
            // if (map2.equals(entity.getMap2()) == false)
            // {
            // return false;
            // }
            for (int i = 0; i < nestDatas.length; i++)
            {
                if (nestDatas[i].equals(entity.getNestDatas()[i]) == false)
                {
                    logger.debug("traceId:{} nestDatas不相等");
                    return false;
                }
            }
            for (int i = 0; i < lists.length; i++)
            {
                if (lists[i].equals(entity.getLists()[i]) == false)
                {
                    logger.debug("traceId:{} lists不相等");
                    return false;
                }
            }
            if (entity.getData() == null)
            {
                logger.debug("traceId:{} data不相等");
                return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
