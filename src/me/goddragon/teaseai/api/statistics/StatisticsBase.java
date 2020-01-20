package me.goddragon.teaseai.api.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import java_cup.emit;
import me.goddragon.teaseai.utils.TeaseLogger;

public class StatisticsBase
{
    protected String isA = "StatisticsBase";
    protected Date StartTime;
    protected Date EndTime;
    
    protected ArrayList<String> StringList1;
    protected ArrayList<String> StringList2;
    protected ArrayList<String> StringList3;
    protected ArrayList<String> StringList4;
    protected ArrayList<String> StringList5;
    protected ArrayList<String> StringList6;
    protected ArrayList<String> StringList7;
    protected ArrayList<String> StringList8;
    protected ArrayList<String> StringList9;
    protected ArrayList<String> StringList10;
    
    protected ArrayList<Integer> IntegerList1;
    protected ArrayList<Integer> IntegerList2;
    protected ArrayList<Integer> IntegerList3;
    protected ArrayList<Integer> IntegerList4;
    protected ArrayList<Integer> IntegerList5;
    protected ArrayList<Integer> IntegerList6;
    protected ArrayList<Integer> IntegerList7;
    protected ArrayList<Integer> IntegerList8;
    protected ArrayList<Integer> IntegerList9;
    protected ArrayList<Integer> IntegerList10;
    
    protected ArrayList<Double> DoubleList1;
    protected ArrayList<Double> DoubleList2;
    protected ArrayList<Double> DoubleList3;
    protected ArrayList<Double> DoubleList4;
    protected ArrayList<Double> DoubleList5;
    protected ArrayList<Double> DoubleList6;
    protected ArrayList<Double> DoubleList7;
    protected ArrayList<Double> DoubleList8;
    protected ArrayList<Double> DoubleList9;
    protected ArrayList<Double> DoubleList10;
    
    protected ArrayList<Boolean> BooleanList1;
    protected ArrayList<Boolean> BooleanList2;
    protected ArrayList<Boolean> BooleanList3;
    protected ArrayList<Boolean> BooleanList4;
    protected ArrayList<Boolean> BooleanList5;
    protected ArrayList<Boolean> BooleanList6;
    protected ArrayList<Boolean> BooleanList7;
    protected ArrayList<Boolean> BooleanList8;
    protected ArrayList<Boolean> BooleanList9;
    protected ArrayList<Boolean> BooleanList10;
    
    protected ArrayList<Object> ObjectList1;
    protected ArrayList<Object> ObjectList2;
    protected ArrayList<Object> ObjectList3;
    protected ArrayList<Object> ObjectList4;
    protected ArrayList<Object> ObjectList5;
    protected ArrayList<Object> ObjectList6;
    protected ArrayList<Object> ObjectList7;
    protected ArrayList<Object> ObjectList8;
    protected ArrayList<Object> ObjectList9;
    protected ArrayList<Object> ObjectList10;
    
    public StatisticsBase()
    {
        StartTime = new Date();
    }
    
    public void init()
    {
        
    }
    
    public void setType(String type)
    {
        isA = type;
    }
    
    public void EndCleanly()
    {
        EndTime = new Date();
    }
    
    @SuppressWarnings("rawtypes")
    public ArrayList getData(String dataType, int listNumber)
    {
         ArrayList toReturn = null;
        if (dataType.toUpperCase().equals("STRING") || dataType.toUpperCase().equals("STR") || dataType.toUpperCase().equals("S"))
        {
            switch (listNumber)
            {
                case 1:
                    if (StringList1 == null)
                    {
                        StringList1 = new ArrayList<String>();
                    }
                    toReturn = StringList1;
                    break;
                case 2:
                    if (StringList2 == null)
                    {
                        StringList2 = new ArrayList<String>();
                    }
                    toReturn = StringList2;
                    break;
                case 3:
                    if (StringList3 == null)
                    {
                        StringList3 = new ArrayList<String>();
                    }
                    toReturn = StringList3;
                    break;
                case 4:
                    if (StringList4 == null)
                    {
                        StringList4 = new ArrayList<String>();
                    }
                    toReturn = StringList4;
                    break;
                case 5:
                    if (StringList5 == null)
                    {
                        StringList5 = new ArrayList<String>();
                    }
                    toReturn = StringList5;
                    break;
                case 6:
                    if (StringList6 == null)
                    {
                        StringList6 = new ArrayList<String>();
                    }
                    toReturn = StringList6;
                    break;
                case 7:
                    if (StringList7 == null)
                    {
                        StringList7 = new ArrayList<String>();
                    }
                    toReturn = StringList7;
                    break;
                case 8:
                    if (StringList8 == null)
                    {
                        StringList8 = new ArrayList<String>();
                    }
                    toReturn = StringList8;
                    break;
                case 9:
                    if (StringList9 == null)
                    {
                        StringList9 = new ArrayList<String>();
                    }
                    toReturn = StringList9;
                    break;
                case 10:
                    if (StringList10 == null)
                    {
                        StringList10 = new ArrayList<String>();
                    }
                    toReturn = StringList10;
                    break;
                default:
                    TeaseLogger.getLogger().log(Level.SEVERE, "getData must accept an integer from 1-10!");
                    return null;
            }
        }
        else  if (dataType.toUpperCase().equals("INTEGER") || dataType.toUpperCase().equals("INT") || dataType.toUpperCase().equals("I"))
        {
            switch (listNumber)
            {
                case 1:
                    if (IntegerList1 == null)
                    {
                        IntegerList1 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList1;
                    break;
                case 2:
                    if (IntegerList2 == null)
                    {
                        IntegerList2 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList2;
                    break;
                case 3:
                    if (IntegerList3 == null)
                    {
                        IntegerList3 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList3;
                    break;
                case 4:
                    if (IntegerList4 == null)
                    {
                        IntegerList4 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList4;
                    break;
                case 5:
                    if (IntegerList5 == null)
                    {
                        IntegerList5 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList5;
                    break;
                case 6:
                    if (IntegerList6 == null)
                    {
                        IntegerList6 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList6;
                    break;
                case 7:
                    if (IntegerList7 == null)
                    {
                        IntegerList7 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList7;
                    break;
                case 8:
                    if (IntegerList8 == null)
                    {
                        IntegerList8 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList8;
                    break;
                case 9:
                    if (IntegerList9 == null)
                    {
                        IntegerList9 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList9;
                    break;
                case 10:
                    if (IntegerList10 == null)
                    {
                        IntegerList10 = new ArrayList<Integer>();
                    }
                    toReturn = IntegerList10;
                    break;
                default:
                    TeaseLogger.getLogger().log(Level.SEVERE, "getData must accept an integer from 1-10!");
                    return null;
            }
        }
        else  if (dataType.toUpperCase().equals("DOUBLE") || dataType.toUpperCase().equals("DOUB") || dataType.toUpperCase().equals("D"))
        {
            switch (listNumber)
            {
                case 1:
                    if (DoubleList1 == null)
                    {
                        DoubleList1 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList1;
                    break;
                case 2:
                    if (DoubleList2 == null)
                    {
                        DoubleList2 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList2;
                    break;
                case 3:
                    if (DoubleList3 == null)
                    {
                        DoubleList3 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList3;
                    break;
                case 4:
                    if (DoubleList4 == null)
                    {
                        DoubleList4 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList4;
                    break;
                case 5:
                    if (DoubleList5 == null)
                    {
                        DoubleList5 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList5;
                    break;
                case 6:
                    if (DoubleList6 == null)
                    {
                        DoubleList6 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList6;
                    break;
                case 7:
                    if (DoubleList7 == null)
                    {
                        DoubleList7 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList7;
                    break;
                case 8:
                    if (DoubleList8 == null)
                    {
                        DoubleList8 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList8;
                    break;
                case 9:
                    if (DoubleList9 == null)
                    {
                        DoubleList9 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList9;
                    break;
                case 10:
                    if (DoubleList10 == null)
                    {
                        DoubleList10 = new ArrayList<Double>();
                    }
                    toReturn = DoubleList10;
                    break;
                default:
                    TeaseLogger.getLogger().log(Level.SEVERE, "getData must accept an integer from 1-10!");
                    return null;
            }
        }
        else  if (dataType.toUpperCase().equals("BOOLEAN") || dataType.toUpperCase().equals("BOOL") || dataType.toUpperCase().equals("B"))
        {
            switch (listNumber)
            {
                case 1:
                    if (BooleanList1 == null)
                    {
                        BooleanList1 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList1;
                    break;
                case 2:
                    if (BooleanList2 == null)
                    {
                        BooleanList2 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList2;
                    break;
                case 3:
                    if (BooleanList3 == null)
                    {
                        BooleanList3 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList3;
                    break;
                case 4:
                    if (BooleanList4 == null)
                    {
                        BooleanList4 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList4;
                    break;
                case 5:
                    if (BooleanList5 == null)
                    {
                        BooleanList5 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList5;
                    break;
                case 6:
                    if (BooleanList6 == null)
                    {
                        BooleanList6 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList6;
                    break;
                case 7:
                    if (BooleanList7 == null)
                    {
                        BooleanList7 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList7;
                    break;
                case 8:
                    if (BooleanList8 == null)
                    {
                        BooleanList8 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList8;
                    break;
                case 9:
                    if (BooleanList9 == null)
                    {
                        BooleanList9 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList9;
                    break;
                case 10:
                    if (BooleanList10 == null)
                    {
                        BooleanList10 = new ArrayList<Boolean>();
                    }
                    toReturn = BooleanList10;
                    break;
                default:
                    TeaseLogger.getLogger().log(Level.SEVERE, "getData must accept an integer from 1-10!");
                    return null;
            }
        }
        else  if (dataType.toUpperCase().equals("OBJECT") || dataType.toUpperCase().equals("OBJ") || dataType.toUpperCase().equals("O"))
        {
            switch (listNumber)
            {
                case 1:
                    if (ObjectList1 == null)
                    {
                        ObjectList1 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList1;
                    break;
                case 2:
                    if (ObjectList2 == null)
                    {
                        ObjectList2 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList2;
                    break;
                case 3:
                    if (ObjectList3 == null)
                    {
                        ObjectList3 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList3;
                    break;
                case 4:
                    if (ObjectList4 == null)
                    {
                        ObjectList4 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList4;
                    break;
                case 5:
                    if (ObjectList5 == null)
                    {
                        ObjectList5 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList5;
                    break;
                case 6:
                    if (ObjectList6 == null)
                    {
                        ObjectList6 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList6;
                    break;
                case 7:
                    if (ObjectList7 == null)
                    {
                        ObjectList7 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList7;
                    break;
                case 8:
                    if (ObjectList8 == null)
                    {
                        ObjectList8 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList8;
                    break;
                case 9:
                    if (ObjectList9 == null)
                    {
                        ObjectList9 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList9;
                    break;
                case 10:
                    if (ObjectList10 == null)
                    {
                        ObjectList10 = new ArrayList<Object>();
                    }
                    toReturn = ObjectList10;
                    break;
                default:
                    TeaseLogger.getLogger().log(Level.SEVERE, "getData must accept an integer from 1-10!");
                    return null;
            }
        }
        else {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid data type. Options are boolean, integer, string, double, object");
        }
        return toReturn;
    }
    
    @Override
    public String toString() {
        return "StatisticsBase [StartTime=" + StartTime.toString() + ", EndTime=" + EndTime.toString() + ", isA=" + isA + "]";
    }
}
