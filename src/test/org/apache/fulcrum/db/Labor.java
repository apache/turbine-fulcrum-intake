package org.apache.fulcrum.db;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test class to use with the HibernateService
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class Labor
{
    private static Log log = LogFactory.getLog(Labor.class);

    private Date date;
    private String activity;
    private String operator;
    private double hours;
    private double rate;
    private double cost;
    private int id;

    public Labor()
    {
        super();
    }
    /**
     * Returns the activity.
     * @return String
     */
    public String getActivity()
    {
        return activity;
    }

    /**
     * Returns the cost.
     * @return double
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Returns the date.
     * @return Date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Returns the hours.
     * @return double
     */
    public double getHours()
    {
        return hours;
    }

    /**
     * Returns the operator.
     * @return String
     */
    public String getOperator()
    {
        return operator;
    }

    /**
     * Returns the rate.
     * @return double
     */
    public double getRate()
    {
        return rate;
    }

    /**
     * Sets the activity.
     * @param activity The activity to set
     */
    public void setActivity(String activity)
    {
        this.activity = activity;
    }

    /**
     * Sets the cost.
     * @param cost The cost to set
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     * Sets the date.
     * @param date The date to set
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * Sets the hours.
     * @param hours The hours to set
     */
    public void setHours(double hours)
    {
        this.hours = hours;
    }

    /**
     * Sets the operator.
     * @param operator The operator to set
     */
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    /**
     * Sets the rate.
     * @param rate The rate to set
     */
    public void setRate(double rate)
    {
        this.rate = rate;
    }

    private void setId(int id)
    {
        this.id = id;
    }
    public int getId()
    {
        return id;
    }

}
